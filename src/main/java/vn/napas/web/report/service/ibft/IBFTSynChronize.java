package vn.napas.web.report.service.ibft;

import org.springframework.stereotype.Service;

@Service
public class IBFTSynChronize {
	/**
	 * start synchonize ibft transaction to tidb 1.Truncate Table SHCLOG_SETT_IBFT
	 * 2. Kiểm tra và chờ có log này mới chạy tiếp (100100100101) 3. Truncate Table
	 * ISOMESSAGE_TMP_TURN 4. INSERT INTO ISOMESSAGE_TMP_TURN from ISOMESSAGE
	 * 5.INSERT INTO ISOMESSAGE_TMP_TURN From V_APG10_TRANS 6. BEGIN
	 * GATHER_TABLE_FILL_DATA_DAILY('RPT','ISOMESSAGE_TMP_TURN'); END; 7. Truncate
	 * Table ISOMESSAGE_TMP_68_TO 8. Đẩy dữ liệu RESPONSE_CODE = 68 từ
	 * ISOMESSAGE_TMP_TURN sang ISOMESSAGE_TMP_68_TO 9. begin
	 * GET_ISOMESSAGE_TMP_TURN();end; GET_ISOMESSAGE_TMP_TURN.prc 10. BEGIN
	 * GATHER_TABLE_FILL_DATA_DAILY('RPT','SHCLOG_SETT_IBFT'); END; 11. BEGIN
	 * MERGE_SHC_SETT_IBFT_200(); END; 12. BEGIN UPDATE_TRANS_TGTT_20(1); END;
	 */
	public void process() {

	}
}
