package vn.napas.webrp.database.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.napas.webrp.database.entities.IsomessageTmpTurn;
@Repository
public interface IsomessageTmpTurnRepo extends JpaRepository<IsomessageTmpTurn, Long>{
	@Query(value = """
		    SELECT B.*
		    FROM ISOMESSAGE_TMP_TURN B
		    LEFT JOIN SHCLOG_SETT_IBFT A
		      ON TRIM(A.PAN) = B.CARD_NO
		     AND B.TRACE_NO   REGEXP '^[0-9]+$'
		     AND B.LOCAL_TIME REGEXP '^[0-9]+$'
		     AND B.ACQ_ID     REGEXP '^[0-9]+$'
		     AND A.ORIGTRACE  = CAST(B.TRACE_NO   AS UNSIGNED)
		     AND A.TERMID     = B.TERM_ID
		     AND A.LOCAL_TIME = CAST(B.LOCAL_TIME AS UNSIGNED)
		     AND A.ACQUIRER   = CAST(B.ACQ_ID     AS UNSIGNED)
		     AND DATE_FORMAT(A.LOCAL_DATE,'%m%d') = B.LOCAL_DATE
		    WHERE B.MTI = '0200'
		      AND B.CARD_NO IS NOT NULL
		      AND A.DATA_ID IS NULL
		      AND B.tidb_id > :lastId                 -- keyset bằng PK
		    ORDER BY B.tidb_id
		    LIMIT :pageSize
		    """,
		    nativeQuery = true)
	List<IsomessageTmpTurn> fetchNotMatchedAfterId(@Param("lastId") long lastId,
		                                               @Param("pageSize") int pageSize);
}
