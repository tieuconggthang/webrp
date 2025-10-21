package vn.napas.webrp.report.service.ibft;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.database.entities.IbftBankBin;
import vn.napas.webrp.database.repo.IbftBankBinRepo;
import vn.napas.webrp.database.repo.store.StoreUlts;

@Service
@Slf4j
public class IbftBankBinService {
	@Autowired
	IbftBankBinRepo ibftBankBinRepo;
	HashMap<String, IbftBankBin> listIbftBankBin;

	/** Làm tươi cache định kỳ */
	public void refresh() {
		List<IbftBankBin> all = ibftBankBinRepo.findAll();
		HashMap<String, IbftBankBin> tmplistIbftBankBin = new HashMap<String, IbftBankBin>();
//        ibftBankBinRepo.fi
		for (IbftBankBin element : all) {
			tmplistIbftBankBin.put(element.getBin(), element);
		}
		if (tmplistIbftBankBin.size() > 0) {
			listIbftBankBin.clear();
			listIbftBankBin = tmplistIbftBankBin;
		}
	}

	public Integer getIbtBin(String destAccount) {
		if (destAccount == null || destAccount.isBlank())
			return null;
//		        Map<String, List<Integer>> map = cacheRef.get();
//		        List<Integer> members = map.get(destAccount.trim());
//		        Integer val = (members != null && members.size() == 1) ? members.get(0) : null;
		Integer val = null;
		if (listIbftBankBin.containsKey(destAccount))
			val = StoreUlts.to_number_bnv(listIbftBankBin.get(destAccount).getMemberId());
		return val;
	}
//        cacheRef.set(map);

}
