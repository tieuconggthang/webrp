package vn.napas.webrp.report.service.ibft;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.database.repo.store.Proc_NAPAS_MASTER_VIEW_DOMESTIC_IBFT;

@Slf4j
@Service
@RequiredArgsConstructor
public class NapasMasterViewDomesticIbftService {
	private final Proc_NAPAS_MASTER_VIEW_DOMESTIC_IBFT repo;

	/**
	 * Thực thi toàn bộ luồng tương đương procedure NAPAS_MASTER_VIEW_DOMESTIC_IBFT.
	 * Sử dụng một transaction duy nhất (ngoại trừ khi bạn cố ý dùng TRUNCATE).
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void run(LocalDate fromDate, LocalDate toDate, String user) {
		final String MODULE = "NAPAS_MASTER_VIEW_DOMESTIC_IBFT";
		String smsList = repo.getSmsListOrDefault("0983411005");
		repo.logErrEx("BEGIN", "Start", MODULE, 0);
		try {
// 1) Tiền xử lý phiên
			repo.insertSessionDomesticIbft(fromDate, toDate, user);

// 2) Làm sạch bảng đích
			repo.clearTargetTable();

// 3) Khối insert chính (skeleton – cần mở rộng thêm các block theo store gốc)
			int n1 = repo.insertBlock_SuccessByRole(fromDate, toDate);
			log.info("insertBlock_SuccessByRole row insert: {}", n1)		;												// toDate);
			int n2 = repo.insertBlock_ErrorAdjust(fromDate, toDate);
			log.info("Inserted rows: success={}, errorAdjust={}", n1, n2);

// 4) Snapshot phí tháng và backup
			repo.upsertNapasFeeMonth(fromDate, toDate);
			repo.backupNapasFeeMonth();

			repo.logErrEx("END", "End", MODULE, 0);
		} catch (Exception ex) {
			String detail = "" + ex.getClass().getSimpleName() + ": " + ex.getMessage();
			log.error("{} failed: {}", MODULE, detail, ex);
			repo.logErrEx("ERR", detail, MODULE, 2);
// Nếu cần, tích hợp SMS service tại đây. DB gốc dùng SEND_SMS('ALERT_ERR#' || <LIST_SMS> || '#' || <DETAIL>)
// smsService.send("ALERT_ERR#" + smsList + "#" + detail);
			throw ex; // giữ transactional rollback
		}
	}
}
