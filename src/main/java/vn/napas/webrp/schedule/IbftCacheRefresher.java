package vn.napas.webrp.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import vn.napas.webrp.report.service.ibft.IbftBankBinService;
import vn.napas.webrp.report.service.ibft.Tgtt20Service;

@Component
public class IbftCacheRefresher {
	@Autowired
	IbftBankBinService ibftBankBinService;
	@Autowired Tgtt20Service tgtt20Service;

	@Scheduled(initialDelay = 0, fixedDelay = 60000)
	public void refreshIbftCache() {
		ibftBankBinService.refresh();
		tgtt20Service.refresh();
	}
}
