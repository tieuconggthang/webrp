package vn.napas.webrp.database.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for table SHCLOG_SETT_IBFT.
 * Note: Primary Key = (settlementDate, msgtype, acquirer, stt, dataId)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShclogSettIbftDto {
    private BigDecimal msgtype;                 // DECIMAL(18,6) NOT NULL
    private String pan;                         // CHAR(19)
    private BigDecimal pcode;                   // DECIMAL(18,6)
    private BigDecimal amount;                  // DECIMAL(26,8)
    private BigDecimal acqCurrencyCode;         // DECIMAL(18,6)
    private BigDecimal fee;                     // DECIMAL(26,8)
    private BigDecimal newFee;                  // DECIMAL(26,8)
    private BigDecimal settlementAmount;        // DECIMAL(26,8)
    private BigDecimal trace;                   // DECIMAL(18,6)
    private BigDecimal localTime;               // DECIMAL(18,6)
    private LocalDateTime localDate;            // DATETIME
    private LocalDateTime settlementDate;       // DATETIME NOT NULL
    private BigDecimal acquirer;                // DECIMAL(18,6) NOT NULL
    private BigDecimal issuer;                  // DECIMAL(18,6)
    private BigDecimal respcode;                // DECIMAL(18,6)
    private BigDecimal merchantType;            // DECIMAL(18,6)
    private String authnum;                     // CHAR(6)
    private BigDecimal chCurrencyCode;          // DECIMAL(18,6)
    private String termid;                      // CHAR(8)
    private String refnum;                      // CHAR(12)
    private String acctnum1;                    // CHAR(60)
    private BigDecimal cardSeqno;               // DECIMAL(18,6)
    private BigDecimal issCurrencyCode;         // DECIMAL(18,6)
    private String chipIndex;                   // CHAR(20)
    private LocalDateTime trandate;             // DATETIME
    private BigDecimal trantime;                // DECIMAL(18,6)
    private String cardproduct;                 // CHAR(10)
    private BigDecimal revcode;                 // DECIMAL(18,6)
    private BigDecimal origtrace;               // DECIMAL(18,6)
    private String acceptorname;                // CHAR(40)
    private String termloc;                     // CHAR(25)
    private String loaigdreveso;                // VARCHAR(4)
    private String thaydoi;                     // CHAR(1)
    private BigDecimal configFeeId;             // DECIMAL(28,0)
    private LocalDateTime tgtp;                 // DATETIME
    private LocalDateTime tgguigd;              // DATETIME
    private LocalDateTime tgguiqt;              // DATETIME
    private LocalDateTime tgddnv;               // DATETIME
    private LocalDateTime tgxlnv;               // DATETIME
    private BigDecimal reamount;                // DECIMAL(26,8)
    private BigDecimal ramount;                 // DECIMAL(26,8)
    private BigDecimal qamount;                 // DECIMAL(26,8)
    private String lddnv;                       // CHAR(1)
    private BigDecimal famount;                 // DECIMAL(26,8)
    private LocalDateTime tgguinv;              // DATETIME
    private LocalDateTime tgguiqtp;             // DATETIME
    private LocalDateTime editDate;             // DATETIME
    private String editUser;                    // VARCHAR(60)
    private String smlVerify;                   // CHAR(1)
    private String origiss;                     // CHAR(10)
    private BigDecimal origrespcode;            // DECIMAL(18,6)
    private BigDecimal stt;                     // DECIMAL(18,6) NOT NULL
    private BigDecimal isrev;                   // DECIMAL(18,6)
    private BigDecimal preamount;               // DECIMAL(26,8)
    private LocalDateTime capDate;              // DATETIME
    private BigDecimal feeIss;                  // DECIMAL(26,8)
    private BigDecimal feeAcq;                  // DECIMAL(26,8)
    private BigDecimal insPcode;                // DECIMAL(18,6)
    private BigDecimal convRate;                // DECIMAL(26,8)
    private BigDecimal feeRecAt;                // DECIMAL(26,8)
    private BigDecimal feePayAt;                // DECIMAL(26,8)
    private BigDecimal feeRecDf;                // DECIMAL(26,8)
    private BigDecimal feePayDf;                // DECIMAL(26,8)
    private LocalDateTime editDateIns;          // DATETIME
    private BigDecimal entityid;                // DECIMAL(18,6)
    private BigDecimal transactionAmount;       // DECIMAL(26,8)
    private BigDecimal cardholderAmount;        // DECIMAL(26,8)
    private BigDecimal cardholderConvRate;      // DECIMAL(18,6)
    private BigDecimal bbBin;                   // DECIMAL(18,6)
    private BigDecimal forwardInst;             // DECIMAL(18,6)
    private BigDecimal transferee;              // DECIMAL(26,8)
    private BigDecimal settCurrencyCode;        // DECIMAL(18,6)
    private BigDecimal preCardholderAmount;     // DECIMAL(26,8)
    private BigDecimal repayUsd;                // DECIMAL(26,8)
    private BigDecimal convRateAcq;             // DECIMAL(26,8)
    private String termidAcq;                   // CHAR(16)
    private BigDecimal shcerror;                // DECIMAL(18,6)
    private BigDecimal merchantTypeOrig;        // DECIMAL(18,6)
    private String bbAccount;                   // VARCHAR(60)
    private BigDecimal feeService;              // DECIMAL(26,8)
    private String senderAcc;                   // VARCHAR(28)
    private String bnbAcc;                      // VARCHAR(28)
    private String senderSwc;                   // VARCHAR(12)
    private String bnbSwc;                      // VARCHAR(12)
    private String contentFund;                 // VARCHAR(999)
    private String respcodeGw;                  // VARCHAR(2)
    private String acctnum;                     // CHAR(70)
    private String fromSml;                     // VARCHAR(1)
    private String originator;                  // CHAR(10)
    private BigDecimal origAcq;                 // DECIMAL(18,6)
    private String feeNote;                     // VARCHAR(500)
    private String onlySml;                     // VARCHAR(20)
    private BigDecimal acquirerFe;              // DECIMAL(18,6)
    private BigDecimal acquirerRp;              // DECIMAL(18,6)
    private BigDecimal issuerFe;                // DECIMAL(18,6)
    private BigDecimal issuerRp;                // DECIMAL(18,6)
    private String feeKey;                      // VARCHAR(50)
    private BigDecimal acqRq;                   // DECIMAL(18,6)
    private BigDecimal issRq;                   // DECIMAL(18,6)
    private String fromSys;                     // VARCHAR(10)
    private BigDecimal pcode2;                  // DECIMAL(18,6)
    private BigDecimal bbBinOrig;               // DECIMAL(18,6)
    private String txnsrc;                      // VARCHAR(30)
    private String txndest;                     // VARCHAR(30)
    private String src;                         // VARCHAR(3)
    private String des;                         // VARCHAR(3)
    private String tranCase;                    // VARCHAR(10)
    private BigDecimal pcodeOrig;               // DECIMAL(18,6)
    private BigDecimal rcIss72;                 // DECIMAL(18,6)
    private BigDecimal rcAcq72;                 // DECIMAL(18,6)
    private String rcIss;                       // VARCHAR(4)
    private String rcBen;                       // VARCHAR(4)
    private String rcAcq;                       // VARCHAR(4)
    private LocalDateTime napasDate;            // DATETIME
    private LocalDateTime napasEditDate;        // DATETIME
    private LocalDateTime napasEditDateIns;     // DATETIME
    private LocalDateTime napasNdDate;          // DATETIME
    private String reasonEdit;                  // VARCHAR(200)
    private BigDecimal acqCountry;              // DECIMAL(28,2)
    private BigDecimal posEntryCode;            // DECIMAL(28,2)
    private BigDecimal posConditionCode;        // DECIMAL(28,2)
    private String addresponse;                 // VARCHAR(25)
    private String mvv;                         // VARCHAR(20)
    private String termid1;                     // CHAR(30)
    private BigDecimal feeIrfIss;               // DECIMAL(18,6)
    private BigDecimal feeSvfIss;               // DECIMAL(18,6)
    private BigDecimal feeIrfAcq;               // DECIMAL(18,6)
    private BigDecimal feeSvfAcq;               // DECIMAL(18,6)
    private BigDecimal feeIrfPayAt;             // DECIMAL(18,6)
    private BigDecimal feeSvfPayAt;             // DECIMAL(18,6)
    private BigDecimal feeIrfRecAt;             // DECIMAL(18,6)
    private BigDecimal feeSvfRecAt;             // DECIMAL(18,6)
    private BigDecimal feeIrfBen;               // DECIMAL(18,6)
    private BigDecimal feeSvfBen;               // DECIMAL(18,6)
    private String token;                       // VARCHAR(50)
    private BigDecimal rc;                      // DECIMAL(18,6)
    private BigDecimal settlementCode;          // DECIMAL(18,6)
    private BigDecimal settlementRate;          // DECIMAL(26,8)
    private BigDecimal issConvRate;             // DECIMAL(26,8)
    private BigDecimal f49;                     // DECIMAL(18,6)
    private BigDecimal f5;                      // DECIMAL(26,8)
    private BigDecimal f4;                      // DECIMAL(26,8)
    private BigDecimal f6;                      // DECIMAL(26,8)
    private LocalDateTime f15;                  // DATETIME
    private String tcc;                         // VARCHAR(64)
    private BigDecimal preamountUsd;            // DECIMAL(26,8)
    private String acquirerRef;                 // VARCHAR(30)
    private BigDecimal txnStartTime;            // DECIMAL(18,6)
    private BigDecimal txnEndTime;              // DECIMAL(18,6)
    private String achReconcileStatus;          // VARCHAR(70)
    private String issuerData;                  // VARCHAR(128)
    private BigDecimal preamountAcq;            // DECIMAL(18,6)
    private BigDecimal codeRef;                 // DECIMAL(18,6)
    private String addInfo;                     // VARCHAR(450)
    private LocalDateTime achEditDate;          // DATETIME
    private BigDecimal deviceFee;               // DECIMAL(26,8)
    private BigDecimal sttOrig;                 // DECIMAL(18,6)
    private BigDecimal preamountTranx;          // DECIMAL(18,6)
    private String f60Upi;                      // VARCHAR(60)
    private String f100Upi;                     // VARCHAR(33)
    private BigDecimal isPartialSync;           // DECIMAL(18,6)
    private BigDecimal isPartRev;               // DECIMAL(18,6)
    private String endtoendId;                  // VARCHAR(35)
    private String msgid;                       // VARCHAR(35)
    private String instrid;                     // VARCHAR(35)
    private String maTlto;                      // VARCHAR(3)
    private LocalDateTime dateXlTlto;           // DATETIME
    private BigDecimal feeIrfQt;                // DECIMAL(18,6)
    private BigDecimal feeSvfQt;                // DECIMAL(18,6)
    private String track2ServiceCode;           // VARCHAR(50)
    private BigDecimal nspkF5;                  // DECIMAL(26,8)
    private String transitCsrr;                 // VARCHAR(1)
    private BigDecimal sequenceInMonth;         // DECIMAL(18,6)
    private String accountNo;                   // VARCHAR(69)
    private String destAccount;                 // VARCHAR(69)
    private String merchantCode;                // VARCHAR(96)
    private String msgtypeDetail;               // VARCHAR(10)
    private String f90;                         // VARCHAR(43)
    private String tokenRequestor;              // VARCHAR(10)
    private String feePackageType;              // VARCHAR(15)
    private String insTypeFee;                  // VARCHAR(4)
    private BigDecimal feeIrfIssNoVat;          // DECIMAL(18,6)
    private BigDecimal feeSvfIssNoVat;          // DECIMAL(18,6)
    private BigDecimal feeIrfAcqNoVat;          // DECIMAL(18,6)
    private BigDecimal feeSvfAcqNoVat;          // DECIMAL(18,6)
    private BigDecimal feeIrfBenNoVat;          // DECIMAL(28,8)
    private BigDecimal feeSvfBenNoVat;          // DECIMAL(28,8)
    private BigDecimal svfissnp;                // DECIMAL(26,8)
    private BigDecimal irfissacq;               // DECIMAL(26,8)
    private BigDecimal irfissbnb;               // DECIMAL(26,8)
    private BigDecimal svfacqnp;                // DECIMAL(26,8)
    private BigDecimal irfacqiss;               // DECIMAL(26,8)
    private BigDecimal irfacqbnb;               // DECIMAL(26,8)
    private BigDecimal svfbnbnp;                // DECIMAL(26,8)
    private BigDecimal irfbnbiss;               // DECIMAL(26,8)
    private BigDecimal irfbnbacq;               // DECIMAL(26,8)
    private BigDecimal ppcode;                  // DECIMAL(18,6)
    private BigDecimal dataId;                  // DECIMAL(18,6) NOT NULL
}
