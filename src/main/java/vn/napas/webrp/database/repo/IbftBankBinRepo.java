package vn.napas.webrp.database.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.napas.webrp.database.entities.ErrEx;
import vn.napas.webrp.database.entities.IbftBankBin;
@Repository
public interface IbftBankBinRepo extends JpaRepository<IbftBankBin, Long>{

}
