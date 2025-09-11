CREATE OR REPLACE PROCEDURE RPT.GET_DATA_ACH_TO_IBFT
(
    pSettlementDate varchar2
) IS
tmpVar NUMBER;
iPos Integer;
ecode NUMBER;
emesg VARCHAR2(200);
vDetail VARCHAR2(500) := 'Nothing ! ';
/******************************************************************************
   NAME:       GET_DATA_ACH_TO_IBFT
   PURPOSE:    

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        06/03/2020   sondt       1. Created this procedure.

   NOTES:

   Automatically available Auto Replace Keywords:
      Object Name:     GET_DATA_ACH_TO_IBFT
      Sysdate:         09/15/2022
      Date and Time:   09/15/2022, 3:23:00 PM, and 06/03/2020 3:23:00 PM
      Username:        sondt (set in TOAD Options, Procedure Editor)
      Table Name:       (set in the "New PL/SQL Object" dialog)

******************************************************************************/
BEGIN
   tmpVar := 0;
   iPos:=1;
   Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
   Values(sysdate,'0','BEGIN GET DATA FROM ACH','GET_DATA_ACH_TO_IBFT', 0);
   commit;
   Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
   Values(sysdate,'0','START GET DATA PAYMENT FROM ACH','GET_DATA_ACH_TO_IBFT', 0);
   commit;
   Insert into ACH_RECONCILIATION
    (MTI,
      PRIMARY_ACCOUNT_NUMBER_F2,PROCESSING_CODE_F3,SERVICE_CODE_F62,TRANSACTION_CHANNEL_CODE,TRANSACTION_AMOUNT_F4,REAL_TRANSACTION_AMOUNT,CURRENCY_CODE_F49,
      SETTLEMENT_AMOUNT_F5,SETTLEMENT_CURRENCY_CODE_F50,SETTLEMENT_EXCHANGE_RATE,BILLING_AMOUNT_F6,REAL_CARDHOLDER_AMOUNT,CARDHOLDER_CURRENCY_CODE_F51,
      CARDHOLDER_CONVERSION_RATE,SYSTEM_TRACE_F11,LOCAL_TIME_F12,LOCAL_DATE_F13,SETTLEMENT_DATE_F15,MERCHANT_TYPE_F18,POS_ENTRY_MODE_F22,POS_CONDITION_CODE_F25,
      TERM_ID_F41,ACQ_CODE_F32,ISS_CODE,CARD_ACCEPTOR_CODE_F42,BENICIFIARY_CODE_F100,SOURCE_ACCOUNT_F102,DESTINATION_ACCOUNT_F103,SERVICE_FEE_ISS_NAPAS,
      INTERCHANGE_FEE_ISS_ACQ,INTERCHANGE_FEE_ISS_BNB,SERVICE_FEE_ACQ_NAPAS,INTERCHANGE_FEE_ACQ_ISS,INTERCHANGE_FEE_ACQ_BNB,SERVICE_FEE_BNB_NAPAS,
      INTERCHANGE_FEE_BNB_ISS,INTERCHANGE_FEE_BNB_ACQ,REF_NUMBER_F37,AUTHORIZATION_CODE_F38,TRANS_REF_NUMBER_F63,RECONCILIATION_CODE,
      RESERVE_INFO_1,RESERVE_INFO_2,RESERVE_INFO_3,
            READ_FILE_DATE,
              FILE_NAME,
              ACH_FILE_ROLE,
              SETTLEMENT_DATE_YEAR,
              LOCAL_DATE_YEAR,
              REFUND_AMOUNT,
              EDIT_DATE_ACH_FILE
    )
    SELECT MTI,F2,F3,SVC,TCC,F4,RTA,F49,to_number(F5)/100,F50,F9,F6,RCA,F51,F10,to_number(F11),
              TO_NUMBER(F12),F13,F15,F18,F22,F25,F41,ACQ,ISS,MID,BNB,F102,F103,SVFISSNP,IRFISSACQ,
        IRFISSBNB,SVFACQNP,IRFACQISS,IRFACQBNB,SVFBNBNP,IRFBNBISS,IRFBNBACQ,F37,F38,TRN,RRC,RSV1,RSV2,RSV3,
        sysdate,null,
              Case
                When system_direction = 'ACH_IBFT' Then 'ISS'
                When system_direction = 'IBFT_ACH' Then 'BNB'
              End,
              NP_CONVERT_LOCAL_DATE(F15,sysdate),
              NP_CONVERT_LOCAL_DATE(F13,sysdate),
              Case              
                When MTI='0310' then decode(INSTR(RSV1,'<Amt Ccy="VND">'),0,null,replace(substr(RSV1,INSTR(RSV1,'<Amt Ccy="VND">')+15,INSTR(RSV1,'</Amt>')-INSTR(RSV1,'<Amt Ccy="VND">')-15) ,',','') )
                Else null
              End,
              Case              
                When MTI='0310' then decode(INSTR(RSV2,'<IntrBkSttlmDt>'),0,null,to_date(replace(substr(RSV2,INSTR(RSV2,'<IntrBkSttlmDt>')+15,10) ,',',''),'yyyy-mm-dd') )
                Else null
              End 
    FROM ACHOFFLINE.v_rr_ibft_payment@LINKACHTEST
    Where settled_date = To_Char(To_Date(pSettlementDate,'dd/mm/yyyy'),'mmdd') and SETTLED_YEAR = To_Char(To_Date(pSettlementDate,'dd/mm/yyyy'),'yyyy');
--    iCount:= SQL%ROWCOUNT;
    commit;
   Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
   Values(sysdate,'0','FINISH GET DATA PAYMENT FROM ACH' ,'GET_DATA_ACH_TO_IBFT', 0);
   commit;
   iPos:=2;
   Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
   Values(sysdate,'0','START GET DATA REUTURN FROM ACH','GET_DATA_ACH_TO_IBFT', 0);
   commit;
   iPos:=3;
   Insert into ACH_RECONCILIATION
    (MTI,
      PRIMARY_ACCOUNT_NUMBER_F2,PROCESSING_CODE_F3,SERVICE_CODE_F62,TRANSACTION_CHANNEL_CODE,TRANSACTION_AMOUNT_F4,REAL_TRANSACTION_AMOUNT,CURRENCY_CODE_F49,
      SETTLEMENT_AMOUNT_F5,SETTLEMENT_CURRENCY_CODE_F50,SETTLEMENT_EXCHANGE_RATE,BILLING_AMOUNT_F6,REAL_CARDHOLDER_AMOUNT,CARDHOLDER_CURRENCY_CODE_F51,
      CARDHOLDER_CONVERSION_RATE,SYSTEM_TRACE_F11,LOCAL_TIME_F12,LOCAL_DATE_F13,SETTLEMENT_DATE_F15,MERCHANT_TYPE_F18,POS_ENTRY_MODE_F22,POS_CONDITION_CODE_F25,
      TERM_ID_F41,ACQ_CODE_F32,ISS_CODE,CARD_ACCEPTOR_CODE_F42,BENICIFIARY_CODE_F100,SOURCE_ACCOUNT_F102,DESTINATION_ACCOUNT_F103,SERVICE_FEE_ISS_NAPAS,
      INTERCHANGE_FEE_ISS_ACQ,INTERCHANGE_FEE_ISS_BNB,SERVICE_FEE_ACQ_NAPAS,INTERCHANGE_FEE_ACQ_ISS,INTERCHANGE_FEE_ACQ_BNB,SERVICE_FEE_BNB_NAPAS,
      INTERCHANGE_FEE_BNB_ISS,INTERCHANGE_FEE_BNB_ACQ,REF_NUMBER_F37,AUTHORIZATION_CODE_F38,TRANS_REF_NUMBER_F63,RECONCILIATION_CODE,
      RESERVE_INFO_1,RESERVE_INFO_2,RESERVE_INFO_3,
      READ_FILE_DATE,
              FILE_NAME,
              ACH_FILE_ROLE,
              SETTLEMENT_DATE_YEAR,
              LOCAL_DATE_YEAR,
              REFUND_AMOUNT,
              EDIT_DATE_ACH_FILE
    )
    SELECT MTI,F2,F3,SVC,TCC,F4,RTA,F49,to_number(F5)/100,F50,F9,F6,RCA,F51,F10,to_number(F11),
              TO_NUMBER(F12),F13,F15,F18,F22,F25,F41,ACQ,ISS,MID,BNB,F102,F103,SVFISSNP,IRFISSACQ,
        IRFISSBNB,SVFACQNP,IRFACQISS,IRFACQBNB,SVFBNBNP,IRFBNBISS,IRFBNBACQ,F37,F38,TRN,RRC,RSV1,RSV2,RSV3,
        sysdate,null,
              Case
                When system_direction = 'ACH_IBFT' Then 'ISS'
                When system_direction = 'IBFT_ACH' Then 'BNB'
              End,
              NP_CONVERT_LOCAL_DATE(F15,sysdate),
              NP_CONVERT_LOCAL_DATE(F13,sysdate),
              Case              
                When MTI='0310' then decode(INSTR(RSV1,'<Amt Ccy="VND">'),0,null,replace(substr(RSV1,INSTR(RSV1,'<Amt Ccy="VND">')+15,INSTR(RSV1,'</Amt>')-INSTR(RSV1,'<Amt Ccy="VND">')-15) ,',','') )
                Else null
              End,
              Case              
                When MTI='0310' then decode(INSTR(RSV2,'<IntrBkSttlmDt>'),0,null,to_date(replace(substr(RSV2,INSTR(RSV2,'<IntrBkSttlmDt>')+15,10) ,',',''),'yyyy-mm-dd') )
                Else null
              End 
    FROM ACHOFFLINE.v_rr_ibft_return@LINKACHTEST
    Where a_process_date  between trunc(To_Date(pSettlementDate,'dd/mm/yyyy'))  and   trunc(To_Date(pSettlementDate,'dd/mm/yyyy')) + 1 - 1/86400 ;
    commit;
    iPos:=4;
   Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
   Values(sysdate,'0','FINISH GET DATA REUTURN FROM ACH' ,'GET_DATA_ACH_TO_IBFT', 0);
   commit;
   Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
   Values(sysdate,'0','END GET DATA FROM ACH','GET_DATA_ACH_TO_IBFT', 0);
   commit;
EXCEPTION 
    WHEN OTHERS THEN
    
    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' GET_DATA_ACH_TO_IBFT Err num: ' || TO_CHAR(ecode) || ' - Err detail: ' || emesg||'iPos:'+ iPos;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,ecode,emesg,'GET_DATA_ACH_TO_IBFT', 2);    
    commit;
END GET_DATA_ACH_TO_IBFT;
/
