CREATE OR REPLACE PROCEDURE RPT.MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT
(
        sSettlement_date        CHAR
)

AS
/* ---------------------- Tinh gia tri FEE_KEY cho cac giao dich ------------------------
    Author          : sondt
    Date created    : 24-08-2023
----------------------------------------------------------------------------------------------*/

    iPos Integer := 0;
    num INTEGER := 0 ;
    fee INTEGER := 0 ;
    Ecode NUMBER;
    Emesg VARCHAR2(200);
    dt_start DATE;
    v_begin TIMESTAMP(9);
    v_end TIMESTAMP(9);
    v_interval INTERVAL DAY TO SECOND;
    vDetail VARCHAR2(500) := 'Nothing ! ';
    vPhone VARCHAR2(200);
BEGIN
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,0,'Begin Merge Fee key to SHCLOG_SETT_IBFT ','MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT');
    commit;
    MERGE INTO 
    (
    Select FEE_KEY, Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE)||Decode(ISSUER_FE,null,ISSUER,ISSUER_FE)||
    Decode(ACQ_CURRENCY_CODE,null,704,840,840,704)||Case
        When TRAN_CASE = '72|C3' Then 6011 
        When Pcode2 In (890000,880000,720000,730000) Then 0
        When Pcode2 In (840000,968400,978400,988400,998400) Then Merchant_type
        When SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2) in (98,99) And SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2) = '30' And Merchant_type = 6012 Then Merchant_type 
        When Pcode2 In (960000,970000,980000,990000,968500,978500,988500,998500,967500,977500,987500,997500,
                    967600,977600,987600,997600,967700,977700,987700,997700,
                    967800,977800,987800,997800,967900,977900,987900,997900,
                    966100,976100,986100,996100,966200,976200,986200,996200) And SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2) in ('00','20') And  MERCHANT_TYPE_ORIG in (
                            Select MERCHANT_TYPE 
                                From GR_FEE_CONFIG_NEW 
                                Where To_Date(sSettlement_date,'dd/mm/yyyy') between valid_from and valid_to                                
                                and substr(pro_code,3,2) = substr(PCODE2,0,2)
                                ) Then MERCHANT_TYPE_ORIG
        When Substr(decode(PCODE2,null,0,PCODE2),0,2) not in (96,97,98,99) And SUBSTR(trim(TO_CHAR(PCODE,'099999')),1,2) in('00','20') And MERCHANT_TYPE_ORIG in (4111, 4131,5172,9211, 9222, 9223, 9311, 9399,8398, 7523, 7524,5541,5542) Then MERCHANT_TYPE_ORIG   
        Else MERCHANT_TYPE 
    End||Case
    When PCODE2 in (760000,967600,977600,987600,997600) Then '76'
    When decode(PCODE2,null,0,PCODE2) not in (960000,970000,980000,990000,968500,978500,988500,998500,967700,977700,987700,997700,967500,977500,987500,997500,
            967800,977800,987800,997800,967900,977900,987900,997900,
            966100,976100,986100,996100,966200,976200,986200,996200) Then
     Case 
        When Issuer_Rp = 602907 Then Decode(Pcode_Orig,null,SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2),Pcode_Orig)
        When PCODE2 Is Null or PCODE2 = 950000 or PCODE2 = 850000 or PCODE2 = 750000 or PCODE2 = 760000 or PCODE2 = 780000 or PCODE2 = 790000 Then SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2)
        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
        When PCODE2 = 910000 And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2)
        When PCODE2 = 930000 Then SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2)
        When decode(PCODE2,null,0,PCODE2) in (968400,978400,988400,998400) Then '84'    
        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
        End 
    When ACQUIRER_RP in (605609,220699,605608,600005) And SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2) in (96,97,98,99) Then SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2)
    When SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2) in (98,99) And 
         (
         (SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2) = '30' And Merchant_type = 6012) 
         or
         (SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2) in ('01','30','35','40','94') And Merchant_type = 6011) 
         or 
         (SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2) in ('00','20') And Merchant_type_orig not in (4111, 4131,5172,9211, 9222, 9223, 9311, 9399,8398,7523,7524,5541,5542))
         )
         Then SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2)    
    Else SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2)||Decode(to_char(PCODE2),'968500','960000','978500','970000',
        '967500','960000','977500','970000','987500','980000','997500','990000',
        '967700','960000','977700','970000','987700','980000','997700','990000',
        '967800','960000','977800','970000','987800','980000','997800','990000',
        '967900','960000','977900','970000','987900','980000','997900','990000',
        '966100','960000','976100','970000','986100','980000','996100','990000',
        '966200','960000','976200','970000','986200','980000','996200','990000',
        to_char(PCODE2))
    End FEE_VALUE
    From SHCLOG_SETT_IBFT
    Where SETTLEMENT_DATE = To_Date(sSettlement_date,'dd/mm/yyyy')
    And 
    (
    (msgtype = 210 AND respcode = 0 AND isrev IS NULL)
    Or
    (msgtype = 430 And Respcode = 114)
    )
    And Issuer_rp not in ('602907')
    And
    (
        FEE_NOTE Is Null
        Or
        FEE_NOTE NOT LIKE 'HACH TOAN DIEU CHINH%'
    )
    And 
    (
    (
    (
        (SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) In ('00','01','30','35','40','41','42','43','48','20') And DECODE(MSGTYPE_DETAIL,null,'NA',MSGTYPE_DETAIL) not in ('VPREC')
        )
        Or
        (SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) = '94' And MERCHANT_TYPE = 6011)
    )                
    And
    Decode(FROM_SYS,null,'IST',FROM_SYS) Like '%IST%'
    )
    Or
    (
        FROM_SYS Is Not Null
        And
        SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) In ('01','42','91')
    )
    )) A
    Using ZEN_CONFIG_FEE_IBFT B
    On (A.FEE_VALUE = B.FEE_VALUE)
    WHEN MATCHED THEN
        Update Set A.FEE_KEY = B.FEE_KEY;      
    Commit;    
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,0,'End Merge Fee key to SHCLOG_SETT_IBFT','MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT');
    commit;
EXCEPTION WHEN OTHERS THEN
    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT, Err detail: ' || emesg;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,ecode,vDetail,'MERGE_FEE_KEY_TO_SHCLOG_IBFT', 2);
    commit;
    SEND_SMS('MERGE_FEE_KEY_TO_SHCLOG_SETT_IBFT#0366155501;0983411005;0988766330#'||vDetail);

END;
/
