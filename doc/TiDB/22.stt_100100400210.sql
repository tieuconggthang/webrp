Insert Into ZEN_FEE_VALUE_IBFT(ZEN_TYPE,ZEN_VALUE)
Select 'ACQUIRER' As ZEN_TYPE, Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE) ZEN_VALUE
From SHCLOG_SETT_IBFT
Where SETTLEMENT_DATE = To_Date('07/09/2025','dd/mm/yyyy')
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
Group By Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE)