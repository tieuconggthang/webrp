package vn.napas.webrp.database.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.napas.webrp.database.entities.ErrEx;

public interface ErrExRepo extends JpaRepository<ErrEx, Long> {
	@Query(value = """
			SELECT COUNT(*)
			FROM NP_EXEC_LOG
			WHERE EXEC_DATE > CURRENT_DATE()
			  AND STT = :stt
			  AND EX_ERR = :err
			""", nativeQuery = true)
	long countToday(@Param("stt") String stt, @Param("err") String err);
}
