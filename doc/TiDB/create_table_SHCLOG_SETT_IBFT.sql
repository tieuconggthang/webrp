DROP TABLE RPT.SHCLOG_SETT_IBFT CASCADE CONSTRAINTS;

CREATE TABLE RPT.SHCLOG_SETT_IBFT
(
  MSGTYPE                INTEGER,
  PAN                    CHAR(19 BYTE),
  PCODE                  INTEGER,
  AMOUNT                 NUMBER(26,8),
  ACQ_CURRENCY_CODE      INTEGER,
  FEE                    NUMBER(26,8),
  NEW_FEE                NUMBER(26,8),
  SETTLEMENT_AMOUNT      NUMBER(26,8),
  TRACE                  INTEGER,
  LOCAL_TIME             INTEGER,
  LOCAL_DATE             DATE,
  SETTLEMENT_DATE        DATE,
  ACQUIRER               INTEGER,
  ISSUER                 INTEGER,
  RESPCODE               INTEGER,
  MERCHANT_TYPE          INTEGER,
  AUTHNUM                CHAR(6 BYTE),
  CH_CURRENCY_CODE       INTEGER,
  TERMID                 CHAR(8 BYTE),
  REFNUM                 CHAR(12 BYTE),
  ACCTNUM1               CHAR(60 BYTE),
  CARD_SEQNO             INTEGER,
  ISS_CURRENCY_CODE      INTEGER,
  CHIP_INDEX             CHAR(20 BYTE),
  TRANDATE               DATE,
  TRANTIME               INTEGER,
  CARDPRODUCT            CHAR(10 BYTE),
  REVCODE                INTEGER,
  ORIGTRACE              INTEGER,
  ACCEPTORNAME           CHAR(40 BYTE),
  TERMLOC                CHAR(25 BYTE),
  LOAIGDREVESO           VARCHAR2(4 BYTE),
  THAYDOI                CHAR(1 BYTE),
  CONFIG_FEE_ID          NUMBER(28),
  TGTP                   DATE,
  TGGUIGD                DATE,
  TGGUIQT                DATE,
  TGDDNV                 DATE,
  TGXLNV                 DATE,
  REAMOUNT               NUMBER(26,8),
  RAMOUNT                NUMBER(26,8),
  QAMOUNT                NUMBER(26,8),
  LDDNV                  CHAR(1 BYTE),
  FAMOUNT                NUMBER(26,8),
  TGGUINV                DATE,
  TGGUIQTP               DATE,
  EDIT_DATE              DATE,
  EDIT_USER              VARCHAR2(60 BYTE),
  SML_VERIFY             CHAR(1 BYTE),
  ORIGISS                CHAR(10 BYTE),
  ORIGRESPCODE           INTEGER,
  STT                    INTEGER,
  ISREV                  INTEGER,
  PREAMOUNT              NUMBER(26,8),
  CAP_DATE               DATE,
  FEE_ISS                NUMBER(26,8),
  FEE_ACQ                NUMBER(26,8),
  INS_PCODE              INTEGER,
  CONV_RATE              NUMBER(26,8),
  FEE_REC_AT             NUMBER(26,8),
  FEE_PAY_AT             NUMBER(26,8),
  FEE_REC_DF             NUMBER(26,8),
  FEE_PAY_DF             NUMBER(26,8),
  EDIT_DATE_INS          DATE,
  ENTITYID               INTEGER,
  TRANSACTION_AMOUNT     NUMBER(26,8),
  CARDHOLDER_AMOUNT      NUMBER(26,8),
  CARDHOLDER_CONV_RATE   INTEGER,
  BB_BIN                 INTEGER,
  FORWARD_INST           INTEGER,
  TRANSFEREE             NUMBER(26,8),
  SETT_CURRENCY_CODE     INTEGER,
  PRE_CARDHOLDER_AMOUNT  NUMBER(26,8),
  REPAY_USD              NUMBER(26,8),
  CONV_RATE_ACQ          NUMBER(26,8),
  TERMID_ACQ             CHAR(16 BYTE),
  SHCERROR               INTEGER,
  MERCHANT_TYPE_ORIG     INTEGER,
  BB_ACCOUNT             VARCHAR2(60 BYTE),
  FEE_SERVICE            NUMBER(26,8),
  SENDER_ACC             VARCHAR2(28 BYTE),
  BNB_ACC                VARCHAR2(28 BYTE),
  SENDER_SWC             VARCHAR2(12 BYTE),
  BNB_SWC                VARCHAR2(12 BYTE),
  CONTENT_FUND           VARCHAR2(999 BYTE),
  RESPCODE_GW            VARCHAR2(2 BYTE),
  ACCTNUM                CHAR(70 BYTE),
  FROM_SML               VARCHAR2(1 BYTE),
  ORIGINATOR             CHAR(10 BYTE),
  ORIG_ACQ               INTEGER,
  FEE_NOTE               NVARCHAR2(500),
  ONLY_SML               NVARCHAR2(20),
  ACQUIRER_FE            INTEGER,
  ACQUIRER_RP            INTEGER,
  ISSUER_FE              INTEGER,
  ISSUER_RP              INTEGER,
  FEE_KEY                NVARCHAR2(50),
  ACQ_RQ                 INTEGER,
  ISS_RQ                 INTEGER,
  FROM_SYS               NVARCHAR2(10),
  PCODE2                 INTEGER,
  BB_BIN_ORIG            INTEGER,
  TXNSRC                 NVARCHAR2(30),
  TXNDEST                NVARCHAR2(30),
  SRC                    VARCHAR2(3 BYTE),
  DES                    VARCHAR2(3 BYTE),
  TRAN_CASE              NVARCHAR2(10),
  PCODE_ORIG             INTEGER,
  RC_ISS_72              INTEGER,
  RC_ACQ_72              INTEGER,
  RC_ISS                 VARCHAR2(4 BYTE),
  RC_BEN                 VARCHAR2(4 BYTE),
  RC_ACQ                 VARCHAR2(4 BYTE),
  NAPAS_DATE             DATE,
  NAPAS_EDIT_DATE        DATE,
  NAPAS_EDIT_DATE_INS    DATE,
  NAPAS_ND_DATE          DATE,
  REASON_EDIT            NVARCHAR2(200),
  ACQ_COUNTRY            NUMBER(28,2),
  POS_ENTRY_CODE         NUMBER(28,2),
  POS_CONDITION_CODE     NUMBER(28,2),
  ADDRESPONSE            NVARCHAR2(25),
  MVV                    NVARCHAR2(20),
  TERMID1                CHAR(30 BYTE),
  FEE_IRF_ISS            NUMBER                 DEFAULT null,
  FEE_SVF_ISS            NUMBER                 DEFAULT null,
  FEE_IRF_ACQ            NUMBER                 DEFAULT null,
  FEE_SVF_ACQ            NUMBER                 DEFAULT null,
  FEE_IRF_PAY_AT         NUMBER                 DEFAULT null,
  FEE_SVF_PAY_AT         NUMBER                 DEFAULT null,
  FEE_IRF_REC_AT         NUMBER                 DEFAULT null,
  FEE_SVF_REC_AT         NUMBER                 DEFAULT null,
  FEE_IRF_BEN            NUMBER                 DEFAULT null,
  FEE_SVF_BEN            NUMBER                 DEFAULT null,
  TOKEN                  NVARCHAR2(50),
  RC                     INTEGER,
  SETTLEMENT_CODE        INTEGER,
  SETTLEMENT_RATE        NUMBER(26,8),
  ISS_CONV_RATE          NUMBER(26,8),
  F49                    INTEGER,
  F5                     NUMBER(26,8),
  F4                     NUMBER(26,8),
  F6                     NUMBER(26,8),
  F15                    DATE,
  TCC                    VARCHAR2(64 BYTE),
  PREAMOUNT_USD          NUMBER(26,8),
  ACQUIRER_REF           VARCHAR2(30 BYTE),
  TXN_START_TIME         INTEGER,
  TXN_END_TIME           INTEGER,
  ACH_RECONCIL_STATUS    VARCHAR2(70 BYTE),
  ISSUER_DATA            VARCHAR2(128 BYTE),
  PREAMOUNT_ACQ          NUMBER,
  CODE_REF               INTEGER,
  ADD_INFO               VARCHAR2(450 BYTE),
  ACH_EDIT_DATE          DATE,
  DEVICE_FEE             NUMBER(26,8),
  STT_ORIG               INTEGER,
  PREAMOUNT_TRANX        NUMBER,
  F60_UPI                VARCHAR2(60 BYTE),
  F100_UPI               VARCHAR2(33 BYTE),
  IS_PARTIAL_SYNC        INTEGER,
  IS_PART_REV            INTEGER,
  ENDTOENDID             VARCHAR2(35 BYTE),
  MSGID                  VARCHAR2(35 BYTE),
  INSTRID                VARCHAR2(35 BYTE),
  MA_TLTO                VARCHAR2(3 BYTE),
  DATE_XL_TLTO           DATE,
  FEE_IRF_QT             NUMBER                 DEFAULT null,
  FEE_SVF_QT             NUMBER                 DEFAULT null,
  TRACK2_SERVICE_CODE    VARCHAR2(50 BYTE),
  NSPK_F5                NUMBER(26,8),
  TRANSIT_CSRR           VARCHAR2(1 BYTE),
  SEQUENCE_IN_MONTH      INTEGER,
  ACCOUNT_NO             VARCHAR2(69 BYTE),
  DEST_ACCOUNT           VARCHAR2(69 BYTE),
  MERCHANT_CODE          VARCHAR2(96 BYTE),
  MSGTYPE_DETAIL         VARCHAR2(10 BYTE),
  F90                    VARCHAR2(43 BYTE),
  TOKEN_REQUESTOR        VARCHAR2(10 BYTE),
  FEE_PACKAGE_TYPE       VARCHAR2(15 BYTE),
  INS_TYPE_FEE           VARCHAR2(4 BYTE),
  FEE_IRF_ISS_NO_VAT     NUMBER                 DEFAULT 0,
  FEE_SVF_ISS_NO_VAT     NUMBER                 DEFAULT 0,
  FEE_IRF_ACQ_NO_VAT     NUMBER                 DEFAULT 0,
  FEE_SVF_ACQ_NO_VAT     NUMBER                 DEFAULT 0,
  FEE_IRF_BEN_NO_VAT     NUMBER(28,8),
  FEE_SVF_BEN_NO_VAT     NUMBER(28,8),
  SVFISSNP               NUMBER(26,8),
  IRFISSACQ              NUMBER(26,8),
  IRFISSBNB              NUMBER(26,8),
  SVFACQNP               NUMBER(26,8),
  IRFACQISS              NUMBER(26,8),
  IRFACQBNB              NUMBER(26,8),
  SVFBNBNP               NUMBER(26,8),
  IRFBNBISS              NUMBER(26,8),
  IRFBNBACQ              NUMBER(26,8),
  PPCODE                 NUMBER,
  DATA_ID                INTEGER
)
TABLESPACE BNVNREPD
PCTUSED    0
PCTFREE    10
INITRANS   1
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            NEXT             1M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
LOGGING 
NOCOMPRESS 
NOCACHE
MONITORING;


CREATE INDEX RPT.IDTT_SETT_IBFT ON RPT.SHCLOG_SETT_IBFT
(STT)
LOGGING
TABLESPACE BNVNREPD
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            NEXT             1M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           );

CREATE INDEX RPT.SHCLOG_ACQUIRER_RP_SETT_IBFT ON RPT.SHCLOG_SETT_IBFT
(ACQUIRER_RP)
LOGGING
TABLESPACE BNVNREPD
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            NEXT             1M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           );

CREATE INDEX RPT.SHCLOG_BB_BIN_SETT_IBFT ON RPT.SHCLOG_SETT_IBFT
(BB_BIN)
LOGGING
TABLESPACE BNVNREPD
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            NEXT             1M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           );

CREATE INDEX RPT.SHCLOG_ISSUER_RP_SETT_IBFT ON RPT.SHCLOG_SETT_IBFT
(ISSUER_RP)
LOGGING
TABLESPACE BNVNREPD
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            NEXT             1M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           );

CREATE INDEX RPT.SHCLOG_OTRACE2_SETT_IBFT ON RPT.SHCLOG_SETT_IBFT
(ORIGTRACE)
LOGGING
TABLESPACE BNVNREPD
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            NEXT             1M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           );

CREATE OR REPLACE SYNONYM BNVNREPD.SHCLOG_SETT_IBFT FOR RPT.SHCLOG_SETT_IBFT;


GRANT SELECT ON RPT.SHCLOG_SETT_IBFT TO BIGDATA;

GRANT SELECT ON RPT.SHCLOG_SETT_IBFT TO ODPDB1;
