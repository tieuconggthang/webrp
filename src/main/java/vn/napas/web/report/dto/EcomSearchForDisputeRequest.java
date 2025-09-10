package vn.napas.web.report.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EcomSearchForDisputeRequest {
    @NotBlank
    private String vSystem;       // expect "ECOM"
    @NotBlank
    private String vDisputeType;  // "TS" | "SRCH"
    @NotBlank
    private String vBusiness;     // "CRT"|"VRF"|"EDT"|"CCL"|"RCV" | "ALL" (when SRCH)

    private String vfrom_date;    // "dd/MM/yyyy"
    private String vto_date;      // "dd/MM/yyyy"
    private String vfrom_time;    // "HHmmss"
    private String vto_time;      // "HHmmss"

    private String vtranxID;
    private String vtranxREF;
    private String vPan;
    private String vBankID;
    private String viss;
    private String vacq;
    private String vTSV;
    private String vma_ts;
    private String vma_tl;
}
