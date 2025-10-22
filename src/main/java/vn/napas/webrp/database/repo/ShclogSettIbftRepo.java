package vn.napas.webrp.database.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.napas.webrp.database.entities.IbftBankBin;
import vn.napas.webrp.database.entities.ShclogSettIbft;
@Repository
public interface ShclogSettIbftRepo extends JpaRepository<ShclogSettIbft, Long>{

}
