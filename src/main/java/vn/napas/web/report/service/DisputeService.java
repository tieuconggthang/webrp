package vn.napas.web.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.napas.web.report.dto.EcomSearchForDisputeRequest;
import vn.napas.web.report.repo.DisputeRepo;
import vn.napas.web.report.util.ParamUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DisputeService {
    private final DisputeRepo repo;

    @Transactional(readOnly = true)
    public List<Map<String,Object>> ecomSearchForDispute(EcomSearchForDisputeRequest r) {
        if (!"ECOM".equalsIgnoreCase(ParamUtils.nz(r.getVSystem()))) {
            return List.of();
        }

        String type = ParamUtils.nz(r.getVDisputeType());
        String biz  = ParamUtils.nz(r.getVBusiness());

        if ("TS".equalsIgnoreCase(type)) {
            if ("CRT".equalsIgnoreCase(biz)) return repo.tsCrt(r);
            if ("VRF".equalsIgnoreCase(biz)) return repo.tsVrf(r);
            if ("EDT".equalsIgnoreCase(biz)) return repo.tsEdt(r);
            if ("CCL".equalsIgnoreCase(biz)) return repo.tsCcl(r);
            if ("RCV".equalsIgnoreCase(biz)) return repo.tsRcv(r);
            return List.of();
        }

        if ("SRCH".equalsIgnoreCase(type) && "ALL".equalsIgnoreCase(biz)) {
            return repo.srchAll(r);
        }

        return List.of();
    }
}
