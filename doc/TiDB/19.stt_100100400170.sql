MERGE INTO (SELECT PAN,AMOUNT,LOCAL_DATE,LOCAL_TIME,ORIGTRACE,ISSUER_RP,TERMID,BB_BIN,SEQUENCE_IN_MONTH FROM SHCLOG_SETT_IBFT
                    --- Lay cac giao dich khong thuoc rule bac thang
                    WHERE PCODE2 in (910000,930000,950000)
                    And RESPCODE =0
                    And not (Decode(ISSUER_RP,null,0,ISSUER_RP) in (980471,980478)--ninhnt bo sung 980478 IBFT 2.0 (03/07/2024)
                            Or
                            Decode(BB_BIN,null,0,BB_BIN) = 971100
                            Or
                            Decode(ISSUER_RP,null,0,ISSUER_RP) = 970412 And ACCOUNT_NO ='110014886114' -- xu ly k tinh phi bac thang cho giao dich KIOS VIET
                            )
       ) A
USING (
         SELECT PAN,AMOUNT,LOCAL_DATE,LOCAL_TIME,ORIGTRACE,B1.ISSUER_RP,TERMID,BB_BIN,SEQUENCE_TMP+SEQUENCE_START+TRANSACTION_SUM AS SEQUENCE_TMP
         FROM
            (SELECT PAN,AMOUNT,LOCAL_DATE,LOCAL_TIME,ORIGTRACE,ISSUER_RP,TERMID,BB_BIN,
                    row_number() over (partition by ISSUER_RP order by LOCAL_DATE,LOCAL_TIME,ORIGTRACE) as SEQUENCE_TMP  
            FROM SHCLOG_SETT_IBFT
            --- Lay cac giao dich khong thuoc rule bac thang
            WHERE PCODE2 in (910000,930000,950000)
            And RESPCODE =0
            And not (Decode(ISSUER_RP,null,0,ISSUER_RP) in (980471,980478)--ninhnt bo sung 980478 IBFT 2.0 (03/07/2024)
                    Or
                    Decode(BB_BIN,null,0,BB_BIN) = 971100
                    Or
                    Decode(ISSUER_RP,null,0,ISSUER_RP) = 970412 And ACCOUNT_NO ='110014886114' -- xu ly k tinh phi bac thang cho giao dich KIOS VIET
                    )
            ) B1
                         
            LEFT JOIN (Select SEQUENCE_START,TRANSACTION_SUM,ISSUER_RP 
            From FEE_TIER_IN_MONTH ) B2
            ON B1.ISSUER_RP = B2.ISSUER_RP
       ) B
ON (A.PAN =B.PAN
    AND A.AMOUNT = B.AMOUNT
    AND A.LOCAL_DATE = B.LOCAL_DATE
    AND A.LOCAL_TIME = B.LOCAL_TIME
    AND A.ORIGTRACE = B.ORIGTRACE
    AND A.ISSUER_RP = B.ISSUER_RP
    AND A.TERMID = B.TERMID
    AND DECODE(A.BB_BIN,NULL,0,A.BB_BIN) = DECODE(B.BB_BIN,NULL,0,B.BB_BIN)
)
WHEN MATCHED THEN
    UPDATE SET A.SEQUENCE_IN_MONTH = B.SEQUENCE_TMP