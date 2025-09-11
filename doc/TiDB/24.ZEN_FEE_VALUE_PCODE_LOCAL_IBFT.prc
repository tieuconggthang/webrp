CREATE OR REPLACE PROCEDURE RPT.ZEN_FEE_VALUE_PCODE_LOCAL_IBFT
(
        sSettlement_date        CHAR
)

AS
/* ---------------------- Tinh gia tri PCODE ------------------------
    Author          : sondt
    Date created    : 25/04/2024
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
    Values(sysdate,0,'Begin Zen Pcode value to SHCLOG_SETT_IBFT','ZEN_FEE_VALUE_PCODE_LOCAL_IBFT');
    commit;
    Insert Into ZEN_FEE_VALUE_IBFT(ZEN_TYPE,ZEN_VALUE)
    Select 'PCODE' As ZEN_TYPE, 
    Case    
    When decode(PCODE2,null,0,PCODE2) not in (960000,970000,980000,990000,760000,
            967500,977500,987500,997500,967600,977600,987600,997600,
            967700,977700,987700,997700,
            967800,977800,987800,997800,967900,977900,987900,997900,
            968500,978500,988500,998500) Then
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
    Else SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2)||Decode(to_char(PCODE2),'967500','960000','977500','970000','968500','960000','978500','970000','987500','980000','997500','990000',
        '967700','960000','977700','970000','987700','980000','997700','990000',
        '967800','960000','977800','970000','987800','980000','997800','990000',
        '967900','960000','977900','970000','987900','980000','997900','990000',
        '967600','960000','977600','970000','987600','980000','997600','990000',
        to_char(PCODE2))
    End ZEN_VALUE
    From SHCLOG_SETT_IBFT
    Where SETTLEMENT_DATE = To_Date(sSettlement_date,'dd/mm/yyyy')
    And msgtype = 210
    AND respcode = 0 AND isrev IS NULL
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
    )
    Group By  
    Case
    When decode(PCODE2,null,0,PCODE2) not in (960000,970000,980000,990000,760000,
                    967500,977500,987500,997500,967600,977600,987600,997600,
                    967700,977700,987700,997700,
                    967800,977800,987800,997800,967900,977900,987900,997900,
                    968500,978500,988500,998500) Then
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
    Else SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2)||Decode(to_char(PCODE2),'967500','960000','977500','970000','968500','960000','978500','970000','987500','980000','997500','990000',
        '967700','960000','977700','970000','987700','980000','997700','990000',
        '967800','960000','977800','970000','987800','980000','997800','990000',
        '967900','960000','977900','970000','987900','980000','997900','990000',
        '967600','960000','977600','970000','987600','980000','997600','990000',
        to_char(PCODE2))
    End;      
    Commit;    
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,0,'End Zen Pcode value to SHCLOG_SETT_IBFT','ZEN_FEE_VALUE_PCODE_LOCAL_IBFT');
    commit;
EXCEPTION WHEN OTHERS THEN
    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' ZEN_FEE_VALUE_PCODE_LOCAL_IBFT, Err detail: ' || emesg;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,ecode,vDetail,'ZEN_FEE_VALUE_PCODE_LOCAL_IBFT', 2);
    commit;
    SEND_SMS('ZEN_FEE_VALUE_PCODE_LOCAL_IBFT#0366155501;0983411005;0988766330#'||vDetail);

END;
/
