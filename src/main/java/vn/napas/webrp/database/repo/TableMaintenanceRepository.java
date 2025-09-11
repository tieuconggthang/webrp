package vn.napas.webrp.database.repo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class TableMaintenanceRepository {

    @PersistenceContext
    private EntityManager em;

    public void truncateTable(String tableName) {
        em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
    }
}
