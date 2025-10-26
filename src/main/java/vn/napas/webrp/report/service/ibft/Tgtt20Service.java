package vn.napas.webrp.report.service.ibft;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.database.entities.*;
import vn.napas.webrp.database.repo.*;
import vn.napas.webrp.database.repo.store.StoreUlts;

@Service
@Slf4j
public class Tgtt20Service {
	@Autowired
	Tgtt20Repo tgtt20Repo;
	HashMap<String, Tgtt20> listTgTT20;
	String strlistTgTT20 = "";

	/** Làm tươi cache định kỳ */
	public void refresh() {
		List<Tgtt20> all = tgtt20Repo.findAll();
		HashMap<String, Tgtt20> tmplistTgtt20 = new HashMap<String, Tgtt20>();
		String strTmpListTgTT20 = "";
//        ibftBankBinRepo.fi
		for (Tgtt20 element : all) {
			tmplistTgtt20.put(element.getTgttId(), element);
			strTmpListTgTT20 = strTmpListTgTT20 + "," + element.getTgttId();
		}
		if (tmplistTgtt20.size() > 0) {
			if (listTgTT20 != null)
				listTgTT20.clear();
			listTgTT20 = tmplistTgtt20;
			strlistTgTT20 = strTmpListTgTT20;

		}
	}

	public Tgtt20 getTgTT20(String tgttid) {
		if (tgttid == null || tgttid.isBlank())
			return null;
//		        Map<String, List<Integer>> map = cacheRef.get();
//		        List<Integer> members = map.get(destAccount.trim());
//		        Integer val = (members != null && members.size() == 1) ? members.get(0) : null;
		Tgtt20 val = null;
		if (listTgTT20.containsKey(tgttid))
			val = listTgTT20.get(tgttid);
		return val;
	}

	public Boolean checkTGTTExist(String tgttid) {
		return listTgTT20.containsKey(tgttid);
	}
//        cacheRef.set(map);

}
