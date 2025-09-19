package vn.napas.webrp.report.service.ibft;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.constant.TableConstant;
import vn.napas.webrp.database.repo.TableMaintenanceRepository;
import vn.napas.webrp.database.repo.sql.IsoMessageTmpTurnLoader;
import vn.napas.webrp.database.repo.store.GatherStats;
import vn.napas.webrp.report.dto.EcomSearchForDisputeRequest;
import vn.napas.webrp.report.service.DisputeService;

@Service ("test ibft")
@Slf4j
public class Test {
	@Autowired
	TableMaintenanceRepository tableMaintenanceRepository;
	@Autowired 
	private IsoMessageTmpTurnLoader isoMessageTmpTurnLoader;
	@Autowired
	private GatherStats gatherStats; 
	@EventListener(ApplicationReadyEvent.class)
	public void test() {
//		testMainenance();
//		insertIsoMsgTurnfromIsoMSg();	
//		insertIsoMsgTurnfromV_APG10_TRANS();
//		gatherTable(TableConstant.shemaName, TableConstant.ISOMESSAGE_TMP_TURN);
		insertIsoMsgTurnfromISOMESSAGE_TMP_68_TO();
	}
	
	private void testMainenance() {
		log.info("start testMainenance");
		tableMaintenanceRepository.truncateTable(TableConstant.ISOMESSAGE_TMP_TURN);
		log.info("finish testMainenance");
	}
	
	private void insertIsoMsgTurnfromIsoMSg() {
		try {
			log.info("insertIsoMsgTurnfromIsoMSg");
//			LocalDate localDate = LocalDate.now();
			LocalDate localDate = LocalDate.of(2025, 9, 9);
		int rowcount= 	isoMessageTmpTurnLoader.insertFromSourceIsomessage(localDate, null, null, null, null);
		log.info("insertIsoMsgTurnfromIsoMSg" + rowcount);
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
		} finally {

		}
	}
	
	
	private void insertIsoMsgTurnfromV_APG10_TRANS() {
		try {
			log.info("insertIsoMsgTurnfromIsoMSg");
			LocalDate localDate =LocalDate.of(2025, 9, 9);
			isoMessageTmpTurnLoader.insertFromSourceV_APG10_TRANS(localDate);
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


}
