package vn.napas.webrp.database.dto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ISOMESSAGETMPTURN {
	 private String mti;
	    private String cardNo;
	    private String procCode;
	    // varchar(18)
	    private String traceNo;
	    private String refNo;
	    private String acqId;
	    private String issId;
	    private String approvalCode;
	    private String termId;

	    // DATETIME
	    private Instant tnxStamp;

	    // bigint(20)
	    private Long amount;

	    private String accountNo;
	    // varchar(18)
	    private String localTime;
	    // varchar(12) MMDD
	    private String localDate;
	    // varchar(12) MMDD
	    private String settleDate;

	    private String mcc;
	    private String destAccount;
	    private String addInfo;
	    private String serviceCode;
	    private String benId;

	    // decimal(28,2)
	    private BigDecimal acqCountry;
	    // decimal(28,2)
	    private BigDecimal posEntryCode;
	    // decimal(28,2)
	    private BigDecimal posConditionCode;

	    private String addResponse;
	    private String mvv;

	    // decimal(26,8)
	    private BigDecimal f4;
	    private BigDecimal f5;
	    private BigDecimal f6;

	    // decimal(18,6)
	    private BigDecimal f49;
	    // decimal(18,6)
	    private BigDecimal settlementCode;
	    // decimal(26,8)
	    private BigDecimal settlementRate;
	    // decimal(26,8)
	    private BigDecimal issConvRate;

	    private String tcc;
	    private String cardAcceptNameLocation;
	    private String cardAcceptIdCode;
	    private String ibftInfo;
}
