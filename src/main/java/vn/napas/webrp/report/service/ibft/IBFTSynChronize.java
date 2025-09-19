package vn.napas.webrp.report.service.ibft;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.constant.TableConstant;
import vn.napas.webrp.database.entities.ErrEx;
import vn.napas.webrp.database.repo.ErrExRepo;
import vn.napas.webrp.database.repo.TableMaintenanceRepository;
import vn.napas.webrp.database.repo.sql.IsoMessageTmpTurnLoader;
import vn.napas.webrp.database.repo.sql.ShclogSettIbftSqlBatch;
import vn.napas.webrp.database.repo.sql.ZenFeeValueIbftBatch;
import vn.napas.webrp.database.repo.store.*;
import vn.napas.webrp.database.repo.store.Proc_CHECK_BACKEND_DOUBLE.Summary;

@Service
@Slf4j
public class IBFTSynChronize {
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

	/**
	 * start synchonize ibft transaction to tidb 1.Truncate Table SHCLOG_SETT_IBFT
	 * 2. Kiểm tra và chờ có log này mới chạy tiếp (100100100101) 3. Truncate Table
	 * ISOMESSAGE_TMP_TURN 4. INSERT INTO ISOMESSAGE_TMP_TURN from ISOMESSAGE
	 * 5.INSERT INTO ISOMESSAGE_TMP_TURN From V_APG10_TRANS 6. BEGIN
	 * GATHER_TABLE_FILL_DATA_DAILY('RPT','ISOMESSAGE_TMP_TURN'); END; 7. Truncate
	 * Table ISOMESSAGE_TMP_68_TO 8. Đẩy dữ liệu RESPONSE_CODE = 68 tu
	 * ISOMESSAGE_TMP_TURN sang ISOMESSAGE_TMP_68_TO 9. begin
	 * GET_ISOMESSAGE_TMP_TURN();end; GET_ISOMESSAGE_TMP_TURN.prc 10. BEGIN
	 * GATHER_TABLE_FILL_DATA_DAILY('RPT','SHCLOG_SETT_IBFT'); END; 11. BEGIN
	 * MERGE_SHC_SETT_IBFT_200(); END; 12. BEGIN UPDATE_TRANS_TGTT_20(1); END;
	 */
	public void process() {
		try {
			LocalDate localDate = LocalDate.now();
			log.info("Starting syn IBFT");
			// 2.
			truncateTable(TableConstant.SHCLOG_SETT_IBFT);
			// 3.
			if (searchLog(null) == false) {
				log.info("Khong co du lieu trong bang np_error_log!\n Dung dong bo");
			}
			// 4.
			truncateTable(TableConstant.ISOMESSAGE_TMP_TURN);
			// 5.
			insertIsoMsgTurnfromIsoMSg(localDate);
			// 6.
			insertIsoMsgTurnfromV_APG10_TRANS(localDate);
			// 7.
			gatherTable(TableConstant.shemaName, TableConstant.ISOMESSAGE_TMP_TURN);
			// 8.
			truncateTable(TableConstant.ISOMESSAGE_TMP_68_TO);
			// 9.
			insertIsoMsgTurnfromISOMESSAGE_TMP_68_TO();
			// 10.
			getIsoMsgTmpTurnToSHCLOG_SETT_IBFT();
			// 11.
			gatherTable(TableConstant.shemaName, TableConstant.SHCLOG_SETT_IBFT);
			// 12.
			MERGE_SHC_SETT_IBFT_200();
//			truncateTable(TableConstant.TBL_PAYMENT);
			// 20.
			checkBackEdnDouble();
			// 21
			indexFee();
			// 22
			calFeeTransaction();
			// 23
			updateISSFee();
			// 24
			truncateTable(TableConstant.ZEN_FEE_VALUE_IBFT);
			// 25
			calAcqFee(localDate);
			// 26
			calIssFee(localDate);
			// 27
			calProcFee(localDate);
			// 28
			calFeeMerchantType(localDate);
			// 29
			calFeeCurrencyCode(localDate);
			// 30
			truncateTable(TableConstant.ZEN_CONFIG_FEE_IBFT);
			// 31
			summaryFEE_KEY(localDate, 1);
			// 32 type name = new type();
			NAPAS_CAL_FEE_LOCAL_IBFT(localDate);
			// 33
			checkSpecCharInTrans();
			// 34
			checkTCNUll();
			// 35
			NAPAS_SHC_TMP_DOMESTIC_IBFT(localDate, localDate, "java", 0);
			// 36

			NAPAS_MASTER_VIEW_DOMESTIC_IBFT(localDate, localDate, "java");
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

	private boolean searchLog(ErrEx errEx) {
		return true;
	}

	private void insertIsoMsgTurnfromIsoMSg(LocalDate localDate) {
		try {
			log.info("insertIsoMsgTurnfromIsoMSg");
//			LocalDate localDate = LocalDate.now();
			isoMessageTmpTurnLoader.insertFromSourceIsomessage(localDate, null, null, null, null);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}

	private void insertIsoMsgTurnfromV_APG10_TRANS(LocalDate localDate) {
		try {
			log.info("insertIsoMsgTurnfromIsoMSg");
//			LocalDate localDate = LocalDate.now();
			isoMessageTmpTurnLoader.insertFromSourceV_APG10_TRANS(localDate);
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
			log.info("Starting getIsoMsgTmpTurn");
			proc_GET_ISOMESSAGE_TMP_TURN.execute();
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}

	private void MERGE_SHC_SETT_IBFT_200() {
		try {
			log.info("Starting MERGE_SHC_SETT_IBFT_200");
			mergeIbft200JdbcService.runAll();
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
			shclogSettIbftSqlBatch.syncTransactionFeePackageFromInstitution(false);
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
			shclogSettIbftSqlBatch.updateIssuerFe();
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
			zenFeeValueIbftBatch.insertAcquirer(date);
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
			zenFeeValueIbftBatch.insertIssuer(date);
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
			proc_ZenFeeValuePcodeLocalIbft.insertZenValues(ddMMyyyy);
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
			zenFeeValueIbftBatch.insertConfigFeeIbft(null, 0);
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
			log.info("Starting");
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
			log.info("Starting");
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
			log.info("Starting");

			proc_NAPAS_SHC_TMP_DOMESTIC_IBFT.run(pQRY_FROM_DATE, pQRY_TO_DATE, user, settleCode);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}

	}

	private void NAPAS_MASTER_VIEW_DOMESTIC_IBFT(LocalDate pQRY_FROM_DATE, LocalDate pQRY_TO_DATE, String user) {
		try {
			log.info("Starting");
			napasMasterViewDomesticIbftService.run(pQRY_FROM_DATE, pQRY_TO_DATE, user);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}

	}
}
