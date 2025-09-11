Insert Into ZEN_FEE_VALUE_IBFT(ZEN_TYPE,ZEN_VALUE)
Select 'MERCHANT_TYPE' As ZEN_TYPE, 
Case
    When TRAN_CASE = '72|C3' Then 6011 
    When Pcode2 In (890000,880000,720000,730000) Then 0
    When Pcode2 In (840000,968400,978400,988400,998400) Then Merchant_type
    When SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2) in (98,99) And SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2) = '30' And Merchant_type = 6012 Then Merchant_type 
    When Pcode2 In (960000,970000,980000,990000,968500,978500,988500,998500,967700,977700,987700,997700,
                        967500,977500,987500,997500,967600,977600,987600,997600,
                        967800,977800,987800,997800,967900,977900,987900,997900,
                        966100,976100,986100,996100,966200,976200,986200,996200) And SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2) in ('00','20') And  MERCHANT_TYPE_ORIG in (
                        Select MERCHANT_TYPE 
                            From GR_FEE_CONFIG_NEW 
                            Where sysdate between valid_from and valid_to                                
                            and substr(pro_code,3,2) = substr(PCODE2,0,2)
                            ) Then MERCHANT_TYPE_ORIG
    When Substr(decode(PCODE2,null,0,PCODE2),0,2) not in (96,97,98,99) And SUBSTR(trim(TO_CHAR(PCODE,'099999')),1,2) in('00','20') And MERCHANT_TYPE_ORIG in (4111, 4131,5172,9211, 9222, 9223, 9311, 9399,8398, 7523, 7524) Then MERCHANT_TYPE_ORIG   
    Else MERCHANT_TYPE 
End ZEN_VALUE
From SHCLOG_SETT_IBFT
Where SETTLEMENT_DATE = To_Date('07/09/2025','dd/mm/yyyy')
And msgtype = 210
AND respcode = 0 AND isrev IS NULL
And 
(
    (
        (
            (SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) In ('00','01','30','35','40','41','42','43','48','20','03') And DECODE(MSGTYPE_DETAIL,null,'NA',MSGTYPE_DETAIL) not in ('VPREC'))
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
Group By Case
    When TRAN_CASE = '72|C3' Then 6011 
    When Pcode2 In (890000,880000,720000,730000) Then 0
    When Pcode2 In (840000,968400,978400,988400,998400) Then Merchant_type
    When SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2) in (98,99) And SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2) = '30' And Merchant_type = 6012 Then Merchant_type 
    When Pcode2 In (960000,970000,980000,990000,968500,978500,988500,998500,967700,977700,987700,997700,
                        967500,977500,987500,997500,967600,977600,987600,997600,
                        967800,977800,987800,997800,967900,977900,987900,997900,
                        966100,976100,986100,996100,966200,976200,986200,996200) And SUBSTR(Trim(TO_CHAR(PCODE,'099999')),1,2) in ('00','20') And  MERCHANT_TYPE_ORIG in (
                        Select MERCHANT_TYPE 
                            From GR_FEE_CONFIG_NEW 
                            Where sysdate between valid_from and valid_to                                
                            and substr(pro_code,3,2) = substr(PCODE2,0,2)
                            ) Then MERCHANT_TYPE_ORIG
    When Substr(decode(PCODE2,null,0,PCODE2),0,2) not in (96,97,98,99) And SUBSTR(trim(TO_CHAR(PCODE,'099999')),1,2) in('00','20') And MERCHANT_TYPE_ORIG in (4111, 4131,5172,9211, 9222, 9223, 9311, 9399,8398, 7523, 7524) Then MERCHANT_TYPE_ORIG   
    Else MERCHANT_TYPE 
End 