package vn.napas.webrp.report.service.ibft;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.constant.TableConstant;
import vn.napas.webrp.database.repo.ErrExRepo;
import vn.napas.webrp.database.repo.TableMaintenanceRepository;
import vn.napas.webrp.database.repo.sql.IsoMessageTmpTurnLoader;
import vn.napas.webrp.database.repo.sql.ShclogSettIbftSqlBatch;
import vn.napas.webrp.database.repo.sql.ZenFeeValueIbftBatch;
import vn.napas.webrp.database.repo.store.GatherStats;
import vn.napas.webrp.database.repo.store.Proc_CHECK_BACKEND_DOUBLE;
import vn.napas.webrp.database.repo.store.Proc_CHECK_SPEC_CHAR_IBFT;
import vn.napas.webrp.database.repo.store.Proc_CHECK_TC_NULL;
import vn.napas.webrp.database.repo.store.Proc_GET_ISOMESSAGE_TMP_TURN;
import vn.napas.webrp.database.repo.store.Proc_MERGE_SHC_SETT_IBFT_200;
import vn.napas.webrp.database.repo.store.Proc_MergeFeeKeyToShclogSettIbft;
import vn.napas.webrp.database.repo.store.Proc_NAPAS_SHC_TMP_DOMESTIC_IBFT;
import vn.napas.webrp.database.repo.store.Proc_NapasCalFeeLocalIbft;
import vn.napas.webrp.database.repo.store.Proc_ZenFeeValuePcodeLocalIbft;
import vn.napas.webrp.database.repo.store.Proc_CHECK_BACKEND_DOUBLE.Summary;
import vn.napas.webrp.report.dto.EcomSearchForDisputeRequest;
import vn.napas.webrp.report.service.DisputeService;

@Service("test ibft")
@Slf4j
public class Test {
	@Autowired
	TableMaintenanceRepository tableMaintenanceRepository;
//	@Autowired 
//	private IsoMessageTmpTurnLoader isoMessageTmpTurnLoader;
//	@Autowired
//	private GatherStats gatherStats; 
//	

	@Autowired
	private TableMaintenanceRepository tablMaintenanceRepos;
	@Autowired
	private ErrExRepo errExRepo;
	@Autowired
	private IsoMessageTmpTurnLoader isoMessageTmpTurnLoader;
	@Autowired
	private GatherStats gatherStats;
	@Autowired
	private Proc_GET_ISOMESSAGE_TMP_TURN proc_GET_ISOMESSAGE_TMP_TURN;
	@Autowired
	private MergeIbft200JdbcService mergeIbft200JdbcService;

	@Autowired
	private Proc_CHECK_BACKEND_DOUBLE proc_CHECK_BACKEND_DOUBLE;
	@Autowired
	private ShclogSettIbftSqlBatch shclogSettIbftSqlBatch;
	@Autowired
	ZenFeeValueIbftBatch zenFeeValueIbftBatch;
	@Autowired
	Proc_ZenFeeValuePcodeLocalIbft proc_ZenFeeValuePcodeLocalIbft;
	@Autowired
	Proc_MergeFeeKeyToShclogSettIbft proc_MergeFeeKeyToShclogSettIbft;
	@Autowired
	Proc_NapasCalFeeLocalIbft proc_NapasCalFeeLocalIbft;
	@Autowired
	Proc_CHECK_SPEC_CHAR_IBFT proc_CHECK_SPEC_CHAR_IBFT;
	@Autowired
	Proc_CHECK_TC_NULL prTc_NULL;
	@Autowired
	Proc_NAPAS_SHC_TMP_DOMESTIC_IBFT proc_NAPAS_SHC_TMP_DOMESTIC_IBFT;
	@Autowired
	NapasMasterViewDomesticIbftService napasMasterViewDomesticIbftService;
	@Autowired
	NapasMasterViewDomesticServiceInline19 napasMasterViewDomesticServiceInline19;
	@Autowired
	Proc_MERGE_SHC_SETT_IBFT_200 proc_MERGE_SHC_SETT_IBFT_200;

	@EventListener(ApplicationReadyEvent.class)
	public void test() {
//		testMainenance();
		LocalDate localDateTest = LocalDate.of(2025, 9, 9);
		// 4
//		insertIsoMsgTurnfromIsoMSg(localDateTest);
//		5
//		insertIsoMsgTurnfromV_APG10_TRANS(localDateTest);
		// 6
//		gatherTable(TableConstant.shemaName, TableConstant.ISOMESSAGE_TMP_TURN);
//		LocalDate localDatenow = LocalDate.now();
//		LocalDate localDateTest = LocalDate.of(2025, 9, 9);
		// 8
//		insertIsoMsgTurnfromISOMESSAGE_TMP_68_TO();

		// 9.
//		getIsoMsgTmpTurnToSHCLOG_SETT_IBFT();
		// 10.
//		gatherTable(TableConstant.shemaName, TableConstant.SHCLOG_SETT_IBFT);
//		// 12.
		MERGE_SHC_SETT_IBFT_200();
//		truncateTable(TableConstant.TBL_PAYMENT);
//		// 20.
//		checkBackEdnDouble();
//		// 21
//		indexFee();
//		// 22
//		calFeeTransaction();
//		// 23
//		updateISSFee();
//		// 24
//		truncateTable(TableConstant.ZEN_FEE_VALUE_IBFT);
//		// 25
//		calAcqFee(localDateTest);
//		// 26
//		calIssFee(localDateTest);
//		// 27
//		calProcFee(localDateTest);
//		// 28
//		calFeeMerchantType(localDateTest);
//		// 29
//		calFeeCurrencyCode(localDateTest);
//		// 30
//		truncateTable(TableConstant.ZEN_CONFIG_FEE_IBFT);
//		// 31
//		summaryFEE_KEY(localDateTest, 1);
//		// 32 type name = new type();
		// 33
		MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT(localDateTest);
		// 33
//		NAPAS_CAL_FEE_LOCAL_IBFT(localDateTest);
//		// 34
//		checkSpecCharInTrans();
//		// 35
//		checkTCNUll();
//		// 36
//		NAPAS_SHC_TMP_DOMESTIC_IBFT(localDateTest, localDateTest, "java", 0);
//		// 37
//
//		NAPAS_MASTER_VIEW_DOMESTIC_IBFT(localDateTest, localDateTest, "java");

	}

	private void testMainenance() {
		log.info("start testMainenance");
		tableMaintenanceRepository.truncateTable(TableConstant.SHCLOG_SETT_IBFT);
		tableMaintenanceRepository.truncateTable(TableConstant.ISOMESSAGE_TMP_TURN);
		tableMaintenanceRepository.truncateTable(TableConstant.ISOMESSAGE_TMP_68_TO);
		log.info("finish testMainenance");
	}

	private void insertIsoMsgTurnfromIsoMSg(LocalDate localDate) {
		try {
			log.info("insertIsoMsgTurnfromIsoMSg");
//			LocalDate localDate = LocalDate.now();
//			LocalDate localDate = LocalDate.of(2025, 9, 9);
			List<String> mtiList = Arrays.asList("0200", "0210");
			List<String> procPrefixes = Arrays.asList("91", "42");
			List<String> issList = Arrays.asList("980472", "980471", "980474", "980475");
			String acqList = "605609";
			int rowcount = isoMessageTmpTurnLoader.insertFromSourceIsomessage(localDate, acqList, mtiList, issList,
					procPrefixes);

			log.info("insertIsoMsgTurnfromIsoMSg" + rowcount);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}

	private void insertIsoMsgTurnfromV_APG10_TRANS(LocalDate localDate) {
		try {
			log.info("insertIsoMsgTurnfromIsoMSg");
//			LocalDate localDate =LocalDate.of(2025, 9, 9);
			int row = isoMessageTmpTurnLoader.insertFromSourceV_APG10_TRANS(localDate);
			log.info("row insert: " + row);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}

	private void insertIsoMsgTurnfromISOMESSAGE_TMP_68_TO() {
		try {
			log.info("insertIsoMsgTurnfromISOMESSAGE_TMP_68_TO");
//			LocalDate localDate = LocalDate.now();
			isoMessageTmpTurnLoader.insertFromSourceISOMESSAGE_TMP_68_TO();
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}

	private void gatherTable(String chemaName, String tableName) {
		try {
			log.info("Starting");
			gatherStats.gatherTableFillDataDaily(chemaName, tableName);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}

	}

	private void getIsoMsgTmpTurnToSHCLOG_SETT_IBFT() {
		try {
			log.info("Starting 5.GET_ISOMESSAGE_TMP_TURN.prc");
			int rowEffect = proc_GET_ISOMESSAGE_TMP_TURN.execute();
			log.info("Row udpated: {}", rowEffect);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}

	private void MERGE_SHC_SETT_IBFT_200() {
		try {
			log.info("Starting MERGE_SHC_SETT_IBFT_200");
//			mergeIbft200JdbcService.runAll();
			proc_MERGE_SHC_SETT_IBFT_200.runAll();
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}

	private void checkBackEdnDouble() {
		try {
			log.info("Starting MERGE_SHC_SETT_IBFT_200");
			Summary sum = proc_CHECK_BACKEND_DOUBLE.runCheckBackendDouble(605608);
//			Gson gson = new Gson();
			log.info("Summary: {}", sum);

		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}

	/**
	 * Danh so giao dich tinh fee theo bac thang
	 */
	private void indexFee() {
		try {
			log.info("Starting indexFee");
			shclogSettIbftSqlBatch.maskFeeTransactionAll();
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}

	/**
	 * Xac dinh phi cho tung giao dich
	 */
	private void calFeeTransaction() {
		try {
			log.info("Starting calFeeTransaction");
			int i = shclogSettIbftSqlBatch.syncTransactionFeePackageFromInstitution(false);
			log.info("row updated: {}", i);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}

	/**
	 * update fee cho iss
	 */
	private void updateISSFee() {
		try {
			log.info("Starting updateISSFee");
			int i = shclogSettIbftSqlBatch.updateIssuerFe();
			log.info("updated row: {}", i);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}

	}

	/**
	 * CAL FEE theo ACQUIRER
	 */
	private void calAcqFee(LocalDate date) {
		try {
			log.info("Starting calAcqFee");
			int i = zenFeeValueIbftBatch.insertAcquirer(date);
			log.info("updated row: {}", i);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}

	}

	/**
	 * CAL FEE theo ISS
	 */
	private void calIssFee(LocalDate date) {
		try {
			log.info("Starting calIssFee");
			int i = zenFeeValueIbftBatch.insertIssuer(date);
			log.info("updated row: {}", i);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}

	}

	/**
	 * CAL FEE theo ISS
	 */
	private void calProcFee(LocalDate date) {
		try {
			log.info("Starting calProcFee");
			String ddMMyyyy = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			System.out.println("dd/MM/yyyy = " + ddMMyyyy);
			int i = proc_ZenFeeValuePcodeLocalIbft.insertZenValues(ddMMyyyy);
			log.info("updated row: {}", i);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}

	}

	/**
	 * CAL FEE theo Currency Code
	 */
	private void calFeeMerchantType(LocalDate date) {
		try {
			log.info("Starting calFeeMerchantType");
//			String ddMMyyyy = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//			System.out.println("dd/MM/yyyy = " + ddMMyyyy);
			zenFeeValueIbftBatch.insertMerchantType(date);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}

	/**
	 * CAL FEE theo Currency Code
	 */
	private void calFeeCurrencyCode(LocalDate date) {
		try {
			log.info("Starting calProcFee");
			String ddMMyyyy = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			System.out.println("dd/MM/yyyy = " + ddMMyyyy);
			zenFeeValueIbftBatch.insertCurrencyCode(date);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}

	/**
	 * Insert Into ZEN_CONFIG_FEE_IBFT From ZEN_FEE_VALUE_IBFT Tổng hợp dữ liệu
	 * FEE_KEY theo bộ key ACQUIRER-ISSUER-PCODE-MERCHANT_TYPE-CURRENCY_CODE từ 5
	 * bước phía trên 27.stt_100100400240.sl
	 */
	private void summaryFEE_KEY(LocalDate date, int order) {
		try {
			log.info("Starting summaryFEE_KEY");
			int i = zenFeeValueIbftBatch.insertConfigFeeIbft(date, 1);
			log.info("row update: {}", i);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}

	}

	/**
	 * Map FEE_KEY vào bảng SHCLOG_SETT_IBFT
	 * 28.MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT.prc
	 * 
	 * @param date
	 */
	private void MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT(LocalDate date) {
		try {
			log.info("Starting");
			String ddMMyyyy = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			System.out.println("dd/MM/yyyy = " + ddMMyyyy);
			proc_MergeFeeKeyToShclogSettIbft.mergeFeeKeyToShclogSettIbft(ddMMyyyy);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}

	}

	/**
	 * Điền phí tương ứng vào từng GD 29.NAPAS_CAL_FEE_LOCAL_IBFT.prc
	 * 
	 * @param date
	 */
	private void NAPAS_CAL_FEE_LOCAL_IBFT(LocalDate date) {
		try {
			log.info("Starting");
			String ddMMyyyy = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			System.out.println("dd/MM/yyyy = " + ddMMyyyy);
			proc_NapasCalFeeLocalIbft.calFeeLocalIbft(date);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}

	/**
	 * Kiểm tra GD có kí tự đặc biệt 30.CHECK_SPEC_CHAR_IBFT.prc
	 */
	private void checkSpecCharInTrans() {
		try {
			log.info("Starting checkSpecCharInTrans");
			proc_CHECK_SPEC_CHAR_IBFT.runCheckSpecCharIbft();
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}

	}

	/**
	 * Kiểm tra GD không xác định chiều CK 31.CHECK_TC_NULL.prc
	 */
	private void checkTCNUll() {
		try {
			log.info("Starting checkTCNUll");
			prTc_NULL.runCheckTcNull();
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}

	}

	/**
	 * Tổng hợp dữ liệu điều chỉnh phiên hiện tại vào SHCLOG_SETT_IBFT_ADJUST
	 */
	private void NAPAS_SHC_TMP_DOMESTIC_IBFT(LocalDate pQRY_FROM_DATE, LocalDate pQRY_TO_DATE, String user,
			int settleCode) {
		try {
			log.info("Starting NAPAS_SHC_TMP_DOMESTIC_IBFT");

			proc_NAPAS_SHC_TMP_DOMESTIC_IBFT.run(pQRY_FROM_DATE, pQRY_TO_DATE, user, settleCode);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}

	}

	private void NAPAS_MASTER_VIEW_DOMESTIC_IBFT(LocalDate pQRY_FROM_DATE, LocalDate pQRY_TO_DATE, String user) {
		try {
			log.info("Starting");
//			napasMasterViewDomesticIbftService.run(pQRY_FROM_DATE, pQRY_TO_DATE, user);

			napasMasterViewDomesticServiceInline19.run(pQRY_FROM_DATE, pQRY_FROM_DATE, user);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}

	}

	private void truncateTable(String tableName) {
		try {
			log.info("Starting truncate " + tableName);
			tablMaintenanceRepos.truncateTable(tableName);

		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}

		tablMaintenanceRepos.truncateTable(tableName);
	}

}
