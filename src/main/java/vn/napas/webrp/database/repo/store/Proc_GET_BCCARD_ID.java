package vn.napas.webrp.database.repo.store;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class Proc_GET_BCCARD_ID {
	@Transactional
	public String getBankCardID() {
		return "605608";
	}
}
