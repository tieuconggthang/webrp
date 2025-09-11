package vn.napas.webrp.report.service.test;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.report.dto.EcomSearchForDisputeRequest;
import vn.napas.webrp.report.service.DisputeService;

@Service
@Slf4j
public class Test {
	@Autowired DisputeService disputeService;
	@EventListener(ApplicationReadyEvent.class)
	public void test() {
		ecomSearchForDispute();
	}
	
	
	private void ecomSearchForDispute() {
		EcomSearchForDisputeRequest req = new EcomSearchForDisputeRequest();
//		Long vfromDate = Long.getLong("1753063748600");
		req.setVSystem("ECOM");
		req.setVDisputeType("TS");
		req.setVBusiness("VRF");
		req.setVfrom_date("20/06/2025");
		req.setVfrom_time("083948");

		req.setVto_date("20/07/2025");
		req.setVto_time("093414");

		req.setVtranxID("");
		req.setVtranxREF("");
		req.setVPan("");
		req.setVBankID("970488");
		req.setViss("VCB");
		req.setVacq("");
		req.setVma_ts("000");
		req.setVma_tl("000");
		req.setVTSV("vcb_tuna_ksv");
//		req.setRequestid("1111111111111");
//		SearchDisputeRequest request = req;
		log.info("ecomSearchForDispute: {}", req);
		 
		List<Map<String,Object>> response = disputeService.ecomSearchForDispute(req);
		log.info("DisputeService response: {}", response);
//		System.out.println("Status Code: " + response.getStatusCode());
//		System.out.println("Body: " + response.getBody());
//		System.out.println("Headers: " + response.getHeaders());
	}

}
