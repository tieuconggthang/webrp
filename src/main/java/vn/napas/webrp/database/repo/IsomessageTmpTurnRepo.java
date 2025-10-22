package vn.napas.webrp.database.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.napas.webrp.database.entities.IsomessageTmpTurn;
@Repository
public interface IsomessageTmpTurnRepo extends JpaRepository<IsomessageTmpTurn, Long>{
	@Query(value = "SELECT * FROM isomessage_tmp_turn V " +
            "WHERE V.CARD_NO IS NOT NULL\r\n"
            + "AND V.MTI = '0200' "
            + "AND NOT EXISTS (" +
            "  SELECT 1 FROM SHCLOG_SETT_IBFT A " +
            "  WHERE TRIM(A.PAN) = V.CARD_NO " +
            "    AND A.ORIGTRACE = V.TRACE_NO_U " +
            "    AND A.TERMID = V.TERM_ID " +
            "    AND DATE(A.LOCAL_DATE) = V.LOCAL_DATE_D " +
            "    AND A.LOCAL_TIME = V.LOCAL_TIME_DEC " +
            "    AND A.ACQUIRER = V.ACQ_INT" +
            ") ORDER BY V.tidb_id", nativeQuery = true)
    Page<IsomessageTmpTurn> find200UnmatchedRecords(Pageable pageable);
}
