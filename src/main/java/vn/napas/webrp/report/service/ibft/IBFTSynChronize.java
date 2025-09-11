package vn.napas.webrp.report.service.ibft;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.constant.TableConstant;
import vn.napas.webrp.database.entities.ErrEx;
import vn.napas.webrp.database.repo.ErrExRepo;
import vn.napas.webrp.database.repo.TableMaintenanceRepository;
import vn.napas.webrp.database.repo.sql.IsoMessageTmpTurnLoader;
import vn.napas.webrp.database.repo.store.GatherStats;

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

	/**
	 * start synchonize ibft transaction to tidb 
	 * 1.Truncate Table SHCLOG_SETT_IBFT
	 * 2. Kiểm tra và chờ có log này mới chạy tiếp (100100100101) 
	 * 3. Truncate Table ISOMESSAGE_TMP_TURN 
	 * 4. INSERT INTO ISOMESSAGE_TMP_TURN from ISOMESSAGE
	 * 5.INSERT INTO ISOMESSAGE_TMP_TURN From V_APG10_TRANS 
	 * 6. BEGIN GATHER_TABLE_FILL_DATA_DAILY('RPT','ISOMESSAGE_TMP_TURN'); END; 
	 * 7. Truncate Table ISOMESSAGE_TMP_68_TO 
	 * 8. Đẩy dữ liệu RESPONSE_CODE = 68 tu ISOMESSAGE_TMP_TURN sang ISOMESSAGE_TMP_68_TO
	 * 9. begin GET_ISOMESSAGE_TMP_TURN();end; GET_ISOMESSAGE_TMP_TURN.prc 
	 * 10. BEGIN GATHER_TABLE_FILL_DATA_DAILY('RPT','SHCLOG_SETT_IBFT'); END; 
	 * 11. BEGIN MERGE_SHC_SETT_IBFT_200(); END; 
	 * 12. BEGIN UPDATE_TRANS_TGTT_20(1); END;
	 */
	public void process() {
		try {
			log.info("Starting syn IBFT");
			truncateTable(TableConstant.SHCLOG_SETT_IBFT);
			if (searchLog(null) == false) {
				log.info("Khong co du lieu trong bang np_error_log!\n Dung dong bo");
			}
			truncateTable(TableConstant.ISOMESSAGE_TMP_TURN);
			insertIsoMsgTurnfromIsoMSg();
			insertIsoMsgTurnfromV_APG10_TRANS();
			gatherTable(TableConstant.shemaName, TableConstant.ISOMESSAGE_TMP_TURN);
			truncateTable(TableConstant.ISOMESSAGE_TMP_68_TO);
			insertIsoMsgTurnfromISOMESSAGE_TMP_68_TO();
			//Wait stroe from Phuong
			gatherStats.gatherTableFillDataDaily(TableConstant.shemaName, TableConstant.SHCLOG_SETT_IBFT);
			
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

	private void insertIsoMsgTurnfromIsoMSg() {
		try {
			log.info("insertIsoMsgTurnfromIsoMSg");
			LocalDate localDate = LocalDate.now();
			isoMessageTmpTurnLoader.insertFromSourceIsomessage(localDate, null, null, null, null);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}
	
	private void insertIsoMsgTurnfromV_APG10_TRANS() {
		try {
			log.info("insertIsoMsgTurnfromIsoMSg");
			LocalDate localDate = LocalDate.now();
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
}
