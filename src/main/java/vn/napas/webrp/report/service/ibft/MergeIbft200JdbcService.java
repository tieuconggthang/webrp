package vn.napas.webrp.report.service.ibft;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.database.dto.ISOMESSAGETMPTURN;
import vn.napas.webrp.database.repo.IsoMessageTmpTurnReaderJdbc;
import vn.napas.webrp.database.repo.ShclogBatchUpsertJdbc;
import vn.napas.webrp.database.repo.sql.DbLoggerRepository;
import vn.napas.webrp.noti.EmailNotifier;
import vn.napas.webrp.noti.SmsNotifier;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class MergeIbft200JdbcService {
	private static final String MODULE = "MERGE_SHC_SETT_IBFT_200";

	private final IsoMessageTmpTurnReaderJdbc reader;
	private final ShclogBatchUpsertJdbc writer;
	private final DbLoggerRepository errEx;
	private final SmsNotifier smsNotifier;
	private final EmailNotifier emailNotifier; // nếu không dùng email, có thể bỏ dependency
	private final JdbcTemplate jdbc; // để đếm số dòng cần update STT (nếu muốn log giống hệt store)

	public static final int DEFAULT_PAGE_SIZE = 5_000;
	public static final int DEFAULT_BATCH_SIZE = 1_000;

	public void run(Instant startTsInclusive, Instant endTsExclusive, int pageSize, int batchSize) throws Exception {

		final int pz = pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
		final int bz = batchSize > 0 ? batchSize : DEFAULT_BATCH_SIZE;

		Instant lastTs = (startTsInclusive == null) ? Instant.EPOCH : startTsInclusive;
		long lastTrace = 0L;

		final AtomicLong totalRead = new AtomicLong(0);
		final AtomicLong totalUpsert = new AtomicLong(0);
		final Instant t0 = Instant.now();

		// --- BEGIN LOG ---
		errEx.info(MODULE, "Begin Merge SHCLOG_SETT_IBFT and ISOMESSAGE_TMP_TURN");

		try {
			while (true) {
				List<ISOMESSAGETMPTURN> page = reader.fetchAfter(lastTs, lastTrace, pz);
				if (page.isEmpty())
					break;

				if (endTsExclusive != null) {
					page = page.stream()
							.filter(r -> r.getTnxStamp() == null || r.getTnxStamp().isBefore(endTsExclusive)).toList();
					if (page.isEmpty())
						break;
				}

				totalRead.addAndGet(page.size());
				int n = writer.batchUpsert(page, bz);
				totalUpsert.addAndGet(n);

				ISOMESSAGETMPTURN tail = page.get(page.size() - 1);
				lastTs = tail.getTnxStamp();
				lastTrace = Long.parseLong(tail.getTraceNo());

				if (totalRead.get() % (long) (pz * 5) == 0) {
					log.info("…progress: read={}, upserted={}, lastTs={}, lastTrace={}", totalRead.get(),
							totalUpsert.get(), lastTs, lastTrace);
				}
			}

			// --- FINISH MERGE LOG ---
			errEx.info(MODULE, "Finish Merge SHCLOG_SETT_IBFT and ISOMESSAGE_TMP_TURN");

			// (Tùy chọn) đếm & cập nhật STT nếu còn bản ghi STT NULL + ORIGRESPCODE=97 (với
			// giải pháp cấp STT khi insert thì sẽ = 0)
			int needStt = jdbc.queryForObject(
					"SELECT COUNT(*) FROM SHCLOG_SETT_IBFT WHERE STT IS NULL AND ORIGRESPCODE = 97", Integer.class);
			int rowupdate = 0;
			if (needStt > 0) {
				// Nếu bạn vẫn muốn chạy đoạn UPDATE STT cũ, có thể cài đặt thêm một Updater và
				// gọi ở đây.
				// Với giải pháp hiện tại, STT đã được cấp khi insert nên không cần.
				// rowupdate = sttUpdater.updateSttForOrigResp97();
			}

			errEx.info(MODULE,
					"Finish Update STT for " + rowupdate + " transactions ORIGRESPCODE = 97 in SHCLOG_SETT_IBFT");

			// --- END LOG ---
			errEx.info(MODULE, "End Merge SHCLOG_SETT_IBFT and ISOMESSAGE_TMP_TURN");

			Duration dt = Duration.between(t0, Instant.now());
			log.info("IBFT200 done: read={}, upserted={}, elapsed={}s", totalRead.get(), totalUpsert.get(),
					dt.toSeconds());

		} catch (Exception ex) {
			// --- ERROR LOG + ALERT ---
			int ecode = -1;
			String emesg = ex.getMessage();
			if (ex instanceof java.sql.BatchUpdateException) {
				ecode = ((java.sql.BatchUpdateException) ex).getErrorCode();
//				emesg = 
				
			} else if (ex instanceof java.sql.SQLException) {
				ecode = ((java.sql.SQLException) ex).getErrorCode();
			}
			
			String vDetail = MODULE + ", Err detail: " + ex.getMessage();
			errEx.error(ecode + "", MODULE, vDetail,  2);

			// Gửi cảnh báo: giữ nguyên format chuỗi như store
			smsNotifier.notifyError(MODULE, vDetail);
			emailNotifier.notifyError("[ALERT] " + MODULE, vDetail + "\nSee logs for stacktrace.");

			log.error("{} failed", MODULE, ex);
			throw ex; // tuỳ bạn: rethrow để job framework bắt; hoặc swallow nếu muốn “best effort”
		}
	}

	public void runAll() throws Exception {
		run(null, null, DEFAULT_PAGE_SIZE, DEFAULT_BATCH_SIZE);
	}

	public void runWindow(Instant startTsInclusive, Instant endTsExclusive) throws Exception {
		run(startTsInclusive, endTsExclusive, DEFAULT_PAGE_SIZE, DEFAULT_BATCH_SIZE);
	}
}
