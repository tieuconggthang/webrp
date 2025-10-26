package vn.napas.webrp.database.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Base implementation cho batch insert — dùng chung cho nhiều entity.
 */
@Repository
@Transactional
public class BatchRepo<T> {
	@PersistenceContext
	protected EntityManager em;

//    @Override
	public void insertBatch(List<T> entities) {
		if (entities == null || entities.isEmpty())
			return;

		int batchSize = Math.min(entities.size(), 1000);

		Session session = em.unwrap(Session.class);
		session.setJdbcBatchSize(batchSize);

		for (T entity : entities) {
			em.persist(entity);
		}

		em.flush();
		em.clear();
	}
}
