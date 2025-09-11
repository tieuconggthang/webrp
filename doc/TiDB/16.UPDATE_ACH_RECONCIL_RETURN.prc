CREATE OR REPLACE PROCEDURE RPT.UPDATE_ACH_RECONCIL_RETURN
(
    sFrom_Date varchar2,
    sTo_Date varchar2,
    sFileRole varchar2
) 
AS
/******************************************************************************
   NAME:       UPDATE_ACH_RECONCIL_RETURN
   PURPOSE:    

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        09/10/2019   sondt       1. Created this procedure.

   NOTES: xu ly cac du lieu nhan duoc tu ACH, danh dau theo cac yeu cau cua SRS
          Xu ly cac giao dich IBFT return khac phien duoc danh dau TQT cung ngay giua ACH va IBFT

   Automatically available Auto Replace Keywords:
      Object Name:     UPDATE_ACH_RECONCIL_RETURN
      Sysdate:         09/10/2019
      Date and Time:   09/10/2019, 9:14:33 AM, and 09/10/2019 9:14:33 AM
      Username:        sondt (set in TOAD Options, Procedure Editor)
      Table Name:       (set in the "New PL/SQL Object" dialog)

******************************************************************************/    
    
    Cursor cs IS
        select * From
        (
            select to_date(sTo_Date,'dd/mm/yyyy')+1 - rownum as EDIT_DATE,sFileRole As ACH_FILE_ROLE
            FROM DUAL
            CONNECT BY ROWNUM < 40
        )
        where EDIT_DATE >= to_date(sFrom_Date,'dd/mm/yyyy')
        order by EDIT_DATE asc;
        
    dt cs%ROWTYPE;       
    emesg   VARCHAR2(200);
    iRound  Integer:=0;
    v_rownum integer;
    ecode NUMBER;
    ckexc int := 0;
BEGIN   
    /*
        Tr?ng thái PROCESS_CODE: 0: không dua vào File sai l?ch; 1: c?nh báo email; 2: dua vào File sai l?ch; 3: D? li?u sai phiên quy?t toán
        Tr?ng thái RECONCILATION_RESULT: 0: cân kh?p; 1: l?ch sai; 2: l?ch dúng
        Tr?ng thái SETTLEMENT_STATUS: 0: cân kh?p; 1: l?ch th?a; 2: l?ch thi?u
    */ 
    --iRound:=iRound+1;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'0','START' ,'UPDATE_ACH_RECONCIL_RETURN');
    
    OPEN cs;
        LOOP
            FETCH cs INTO dt;
            EXIT WHEN cs%NOTFOUND;   
                IF(dt.ACH_FILE_ROLE ='ISS') THEN --- ACH dong vai tro la Ngan hang Phat lenh (gui giao dich)
                    
                    --- Bat dau xu ly cap nhat cac giao dich MTI = 0310 (Giao dich IBFT Return - IBFT) cac giao dich RC  = 112/113/114/115 trong bang SHCLOG
                    --- Bat dau Xu ly cac case tinh huong: ACH File va SHCLOG co; SHCLOG co va ACH File khong co 
                    ckexc:=1;
                    iRound:=iRound+1;
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - BEGIN Process (ACH Sender). EDIT_DATE: '|| dt.EDIT_DATE ,'UPDATE_ACH_RECONCIL_RETURN');
                    MERGE INTO
                    (SELECT * 
                        FROM ACH_RECONCILIATION
                        WHERE PROCESS_CODE IS NULL 
                        And ACH_FILE_ROLE='ISS'
                        And MTI =0310
                        And EDIT_DATE_ACH_FILE= dt.EDIT_DATE
                     ) A
                    USING 
                        (SELECT TRIM(PAN) AS PAN,
                                AMOUNT,
                                ORIGTRACE,
                                LOCAL_TIME,
                                LOCAL_DATE,
                                SETTLEMENT_DATE,
                                ACQUIRER,ACQUIRER_RP,
                                TERMID,
                                RESPCODE,
                                MSGTYPE,
                                PCODE,
                                ACQ_CURRENCY_CODE,
                                F5,
                                SETTLEMENT_CODE,
                                SETTLEMENT_RATE,
                                F6,
                                MERCHANT_TYPE,
                                POS_ENTRY_CODE,
                                POS_CONDITION_CODE,
                                ISSUER,
                                BB_BIN_ORIG,
                                REFNUM,
                                AUTHNUM,
                                STT,
                                PREAMOUNT,
                                ACCTNUM  
                            FROM SHCLOG                             
                             WHERE edit_date between dt.EDIT_DATE and dt.EDIT_DATE + 1 - 1/86400
                             And BB_BIN = 980471
                             And substr(PCODE,0,2) in ('91','42')
                             And MSGTYPE = 0210
                             And Fee_note is not null
                             And Edit_date is not null
                             And Respcode in (112,113,114,115)
                             And decode(ACH_RECONCIL_STATUS,null,'ABC',ACH_RECONCIL_STATUS) not in ('Update IBFT Payment to IBFT Return succesfull')
                             --And ACH_RECONCIL_STATUS ='IBFT Online Reconciliation Successful'
                         ) B
                    ON  
                        ( B.LOCAL_DATE= A.LOCAL_DATE_YEAR
                        AND B.SETTLEMENT_DATE = A.SETTLEMENT_DATE_YEAR
                        AND B.LOCAL_TIME= A.LOCAL_TIME_F12
                        AND B.ACQUIRER=CONVERT_ACH_INS_RT(A.ACQ_CODE_F32,A.SETTLEMENT_DATE_YEAR)
                        AND B.ORIGTRACE= A.SYSTEM_TRACE_F11  
                        AND B.AMOUNT=A.SETTLEMENT_AMOUNT_F5
                        AND TRIM(B.PAN) = A.PRIMARY_ACCOUNT_NUMBER_F2                        
                        AND DECODE(B.PREAMOUNT,null,B.AMOUNT,0,B.AMOUNT,B.PREAMOUNT)= A.REFUND_AMOUNT              
                        AND TRIM(B.TERMID) = TRIM(A.TERM_ID_F41)           
                        )
                --- Neu tim thay du lieu o bang ACH_RECONCILIATION va SHCLOG co du lieu khop        
                WHEN MATCHED THEN
                    
                    UPDATE SET A.PROCESS_CODE =
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE in (112,113,114,115) THEN 0  -- Case R1.00
                            --WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE not in (112,113,114,115) AND  B.RESPCODE = 0 THEN 1  -- Case R3.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE in (112,113,114,115)  THEN 0  -- Case R1.68
                            --WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (112,113,114,115) AND  B.RESPCODE = 0 THEN 1  -- Case R3.68
                            ELSE NULL
                        END,
                        A.PROCESS_CONTENT=       
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE in (112,113,114,115) THEN 'Không dua vào file Sai l?ch'  -- Case R1.00
                            --WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE not in (112,113,114,115) AND  B.RESPCODE = 0 THEN 'C?nh báo email'  -- Case R3.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE in (112,113,114,115)  THEN 'Không dua vào file Sai l?ch'  -- Case R1.68
                            --WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (112,113,114,115) AND  B.RESPCODE = 0 THEN 'C?nh báo email'  -- Case R3.68
                            ELSE NULL
                        END,
                        A.RECONCILATION_RESULT=
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE in (112,113,114,115) THEN 0  -- Case R1.00
                            --WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE not in (112,113,114,115) AND  B.RESPCODE = 0 THEN 1  -- Case R3.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE in (112,113,114,115)  THEN 0  -- Case R1.68
                            --WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (112,113,114,115) AND  B.RESPCODE = 0 THEN 1  -- Case R3.68
                            ELSE NULL
                        END,
                        A.SETTLEMENT_STATUS=
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE in (112,113,114,115) THEN 0  -- Case R1.00
                            --WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE not in (112,113,114,115) AND  B.RESPCODE = 0  THEN 1  -- Case R3.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE in (112,113,114,115)  THEN 0  -- Case R1.68
                            --WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (112,113,114,115) AND  B.RESPCODE = 0 THEN 1  -- Case R3.68
                            ELSE NULL
                        END,
                        A.RESPONSE_CODE_IBFT=
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE in (112,113,114,115) THEN B.RESPCODE  -- Case R1.00
                            --WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE not in (112,113,114,115) AND  B.RESPCODE = 0 THEN B.RESPCODE  -- Case R3.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE in (112,113,114,115)  THEN B.RESPCODE  -- Case R1.68
                            --WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (112,113,114,115) AND  B.RESPCODE = 0 THEN B.RESPCODE  -- Case R3.68
                            ELSE NULL
                        END,   
                        A.EDIT_DATE=sysdate,
                        A.UPDATE_CASE =         
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE in (112,113,114,115) THEN '(ACH Sender) - ACH: 0000 và IBFT: '||B.RESPCODE||' - Cân kh?p'  -- Case R1.00
                            --WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE not in (112,113,114,115) AND  B.RESPCODE = 0 THEN '(ACH Sender) - ACH (RETURN): 0000 và IBFT (PAYMENT): '||B.RESPCODE||' - L?ch th?a (IBFT du ti?n)'  -- Case R3.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE in (112,113,114,115)  THEN '(ACH Sender) - ACH: 0068 và IBFT: '||B.RESPCODE||' - Cân kh?p'  -- Case R1.68
                            --WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (112,113,114,115) AND  B.RESPCODE = 0 THEN '(ACH Sender) - ACH (RETURN): 0068 và IBFT (PAYMENT): '||B.RESPCODE||' - L?ch th?a (IBFT du ti?n)'  -- Case R3.68
                            ELSE NULL
                        END,
                        A.RECONCIL_CASE =         
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE in (112,113,114,115) THEN 'R1'  -- Case R1.00
                            --WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE not in (112,113,114,115) AND  B.RESPCODE = 0 THEN 'R3'  -- Case R3.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE in (112,113,114,115)  THEN 'R1'  -- Case R1.68
                            --WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (112,113,114,115) AND  B.RESPCODE = 0 THEN 'R3'  -- Case R3.68
                            ELSE NULL
                        END,
                        A.STT=B.STT
                    ---- Neu tim du lieu o bang SHCLOG co Respcode in (112,113,114,115) khong co trong bang ACH_RECONCILIATION    
                    WHEN NOT MATCHED THEN
                        insert(
                          MTI,
                          PRIMARY_ACCOUNT_NUMBER_F2,
                          PROCESSING_CODE_F3,
                          SERVICE_CODE_F62,
                          TRANSACTION_CHANNEL_CODE,
                          TRANSACTION_AMOUNT_F4,
                          REAL_TRANSACTION_AMOUNT,
                          CURRENCY_CODE_F49,
                          SETTLEMENT_AMOUNT_F5,
                          SETTLEMENT_CURRENCY_CODE_F50,
                          SETTLEMENT_EXCHANGE_RATE,
                          BILLING_AMOUNT_F6,
                          REAL_CARDHOLDER_AMOUNT,
                          CARDHOLDER_CURRENCY_CODE_F51,
                          CARDHOLDER_CONVERSION_RATE,
                          SYSTEM_TRACE_F11,
                          LOCAL_TIME_F12,
                          LOCAL_DATE_F13,
                          SETTLEMENT_DATE_F15,
                          MERCHANT_TYPE_F18,
                          POS_ENTRY_MODE_F22,
                          POS_CONDITION_CODE_F25,
                          TERM_ID_F41,
                          ACQ_CODE_F32,
                          ISS_CODE,
                          CARD_ACCEPTOR_CODE_F42,
                          BENICIFIARY_CODE_F100,
                          SOURCE_ACCOUNT_F102,
                          DESTINATION_ACCOUNT_F103,              
                          REF_NUMBER_F37,
                          AUTHORIZATION_CODE_F38,              
                          ACH_FILE_ROLE,
                          RESPONSE_CODE_IBFT,
                          STT,
                          PROCESS_CODE,
                          PROCESS_CONTENT,
                          RECONCILATION_RESULT,
                          SETTLEMENT_STATUS,
                          UPDATE_CASE,
                          SETTLEMENT_DATE_YEAR,
                          LOCAL_DATE_YEAR,
                          RECONCIL_CASE,
                          EDIT_DATE
                        )
                        values(
                            '0310',
                            B.PAN,
                            B.PCODE,
                            null, --- F62,
                            null, --TRANSACTION_CHANNEL_CODE
                            B.AMOUNT,
                            null, ---REAL_TRANSACTION_AMOUNT
                            B.ACQ_CURRENCY_CODE,
                            B.F5,
                            B.SETTLEMENT_CODE,
                            B.SETTLEMENT_RATE,
                            B.F6,
                            null, ---REAL_CARDHOLDER_AMOUNT
                            null, --CARDHOLDER_CURRENCY_CODE_F51
                            null, --CARDHOLDER_CONVERSION_RATE
                            B.ORIGTRACE,
                            B.LOCAL_TIME,
                            TO_CHAR(B.LOCAL_DATE,'mmdd'),
                            TO_CHAR(B.SETTLEMENT_DATE,'mmdd'),
                            B.MERCHANT_TYPE,
                            B.POS_ENTRY_CODE,
                            B.POS_CONDITION_CODE,
                            B.TERMID,
                            B.ACQUIRER,
                            B.ISSUER,
                            null, ---CARD_ACCEPTOR_CODE_F42
                            B.BB_BIN_ORIG,
                            TRIM(SUBSTR(ACCTNUM,1,INSTR(ACCTNUM||'|','|')-1)), ----SOURCE_ACCOUNT_F102 
                            TRIM(SUBSTR(ACCTNUM,INSTR(ACCTNUM||'|','|')+1,LENGTH(ACCTNUM))), --DESTINATION_ACCOUNT_F103
                            B.REFNUM,
                            B.AUTHNUM,
                            'ISS - SHCLOG',
                            B.RESPCODE,
                            B.STT,
                            '4',
                            'C?nh báo email',                
                            1,
                            CASE 
                                WHEN B.RESPCODE in (112,113,114,115) THEN 2  -- Case R4
                                WHEN B.RESPCODE not in (112,113,114,115) THEN 2  -- Case R5
                                ELSE NULL
                            END,
                            CASE 
                                WHEN B.RESPCODE in (112,113,114,115)  THEN '(ACH Sender) - ACH: No record và IBFT: '||B.RESPCODE||' - L?ch thi?u (IBFT thi?u ti?n)'  -- Case R4
                                WHEN B.RESPCODE not in (112,113,114,115)  THEN '(ACH Sender) - ACH: No record và IBFT: '||B.RESPCODE||' - Cân kh?p (IBFT thi?u ti?n)'  -- Case R5
                                ELSE NULL
                            END,
                            B.SETTLEMENT_DATE,
                            B.LOCAL_DATE,
                            CASE 
                                WHEN B.RESPCODE in (112,113,114,115) THEN 'R4'  -- Case R4
                                WHEN B.RESPCODE not in (112,113,114,115) THEN 'R5'  -- Case R5
                                ELSE NULL
                            END,
                            sysdate
                        );  
                    commit;
                    --- Cap nhat cac giao dich ma ACH co IBFT Return va IBFT co IBFT Payment
                    UPDATE_0310_ACH_SENDER(dt.EDIT_DATE);
                    ckexc:=2;
                    --- Ket thuc Xu ly cac case tinh huong: ACH File va SHCLOG co; SHCLOG co va ACH File khong co 
                    --- Cap nhat cac giao dich doi soat thanh cong 
                    UPDATE SHCLOG
                    SET ACH_RECONCIL_STATUS = 'IBFT Return Reconciliation Successful',ACH_EDIT_DATE= sysdate
                    WHERE edit_date between dt.EDIT_DATE and dt.EDIT_DATE + 1 - 1/86400
                         And BB_BIN = 980471
                         And substr(PCODE,0,2) in ('91','42')
                         And MSGTYPE = 0210
                         And EDIT_DATE is not null
                         And Fee_note is not null
                         And Respcode in (112,113,114,115)
                         And STT IN (SELECT distinct STT FROM
                                  ACH_RECONCILIATION
                                  WHERE PROCESS_CODE in (0,1,2,4)
                                  And ACH_FILE_ROLE in ('ISS','ISS - SHCLOG')
                                  And MTI =0310
                                  And EDIT_DATE_ACH_FILE= dt.EDIT_DATE
                                  And STT is not null);
                    v_rownum:=0;      
                    v_rownum:= SQL%ROWCOUNT;
                    ckexc:=3; 
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||'Cap nhat '||v_rownum|| ' giao dich doi soat thanh cong, EDIT_DATE: '|| dt.EDIT_DATE ,'UPDATE_ACH_RECONCIL_RETURN');             
                    COMMIT; 
                    ckexc:=4;
                    --- cap nhat cac giao dich file ACH co du lieu ma shclog khong co du lieu
                    UPDATE ACH_RECONCILIATION 
                    SET PROCESS_CODE=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 1  --- case R2.00
                            WHEN RECONCILIATION_CODE=0068 THEN 1  --- case R2.68
                            ELSE NULL
                        END,
                        PROCESS_CONTENT=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 'C?nh báo email'  --- case R2.00
                            WHEN RECONCILIATION_CODE=0068 THEN 'C?nh báo email'  --- case R2.68
                           
                            ELSE NULL
                        END, 
                        RECONCILATION_RESULT=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 1  --- case R2.00
                            WHEN RECONCILIATION_CODE=0068 THEN 1  --- case R2.68
                            ELSE NULL
                        END, 
                        SETTLEMENT_STATUS=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 2  --- case R2.00
                            WHEN RECONCILIATION_CODE=0068 THEN 2  --- case R2.68
                            ELSE NULL
                        END ,
                        EDIT_DATE=sysdate,
                        UPDATE_CASE=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN '(ACH Sender) - ACH: 0000 và IBFT: No Record - l?ch th?a (IBFT du?c QT th?a)'  --- case R2.00
                            WHEN RECONCILIATION_CODE=0068 THEN '(ACH Sender) - ACH: 0068 và IBFT: No Record - l?ch th?a (IBFT du?c QT th?a)'  --- case R2.68
                            ELSE NULL
                        END,
                        RECONCIL_CASE=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 'R2'  --- case R2.00
                            WHEN RECONCILIATION_CODE=0068 THEN 'R2'  --- case R2.68
                            ELSE NULL
                        END         
                    WHERE PROCESS_CODE IS NULL
                    AND ACH_FILE_ROLE='ISS'
                    And MTI =0310
                    And EDIT_DATE_ACH_FILE = dt.EDIT_DATE;  
                    v_rownum:=0;      
                    v_rownum:= SQL%ROWCOUNT;
                    ckexc:=5;
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||'Cap nhat '||v_rownum|| ' giao dich ACH co va SHCLOG khong co thanh cong, EDIT_DATE: '|| dt.EDIT_DATE ,'UPDATE_ACH_RECONCIL_RETURN'); 
                    commit;
                    ckexc:=6;
                    
                    --- Ket thuc xu ly cap nhat cac giao dich MTI = 0310 (Giao dich IBFT Return - IBFT) cac giao dich RC  = 112/113/114/115 trong bang SHCLOG
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - END Process (ACH Sender). EDIT_DATE: '|| dt.EDIT_DATE ,'UPDATE_ACH_RECONCIL_RETURN');
                    
                ELSIF(dt.ACH_FILE_ROLE ='BNB') THEN
                    --- Bat dau xu ly cap nhat cac giao dich MTI = 0310 (Giao dich IBFT Return - IBFT) cac giao dich RC  = 112/113/114/115 trong bang SHCLOG
                    --- Bat dau Xu ly cac case tinh huong: ACH File va SHCLOG co; SHCLOG co va ACH File khong co 
                    iRound:=iRound+1;
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - BEGIN Process (ACH Receiver). EDIT_DATE: '|| dt.EDIT_DATE ,'UPDATE_ACH_RECONCIL_RETURN');

                    MERGE INTO
                    (SELECT * 
                        FROM ACH_RECONCILIATION
                        WHERE PROCESS_CODE IS NULL
                        And ACH_FILE_ROLE='BNB'
                        And MTI =0310
                        And EDIT_DATE_ACH_FILE= dt.EDIT_DATE
                     ) A
                    USING 
                        (SELECT TRIM(PAN) AS PAN,
                                AMOUNT,
                                ORIGTRACE,
                                LOCAL_TIME,
                                LOCAL_DATE,
                                SETTLEMENT_DATE,
                                ACQUIRER,
                                TERMID,
                                RESPCODE,
                                MSGTYPE,
                                PCODE,
                                ACQ_CURRENCY_CODE,
                                F5,
                                SETTLEMENT_CODE,
                                SETTLEMENT_RATE,
                                F6,
                                MERCHANT_TYPE,
                                POS_ENTRY_CODE,
                                POS_CONDITION_CODE,
                                ISSUER,
                                BB_BIN_ORIG,
                                REFNUM,
                                AUTHNUM,
                                STT,
                                PREAMOUNT,
                                ACCTNUM
                            FROM SHCLOG
                             WHERE edit_date between dt.EDIT_DATE and dt.EDIT_DATE + 1 - 1/86400
                             And Issuer_rp = 980471
                             And substr(PCODE,0,2) in ('91','42')
                             And MSGTYPE = 0210
                             And Fee_note is not null
                             And Edit_date is not null
                             And Respcode in (112,113,114,115)
                         ) B
                    ON  
                        ( B.LOCAL_DATE= A.LOCAL_DATE_YEAR
                        AND B.SETTLEMENT_DATE = A.SETTLEMENT_DATE_YEAR
                        AND B.LOCAL_TIME= A.LOCAL_TIME_F12
                        AND B.ACQUIRER=CONVERT_ACH_INS_RT(A.ACQ_CODE_F32,A.SETTLEMENT_DATE_YEAR)
                        AND B.ORIGTRACE= A.SYSTEM_TRACE_F11  
                        AND B.AMOUNT=A.SETTLEMENT_AMOUNT_F5   
                        AND TRIM(B.PAN) = A.PRIMARY_ACCOUNT_NUMBER_F2                        
                        AND DECODE(B.PREAMOUNT,null,B.AMOUNT,0,B.AMOUNT,B.PREAMOUNT) = A.REFUND_AMOUNT                     
                        AND TRIM(B.TERMID) = TRIM(A.TERM_ID_F41)              
                        )
                    --- Neu tim thay du lieu o bang ACH_RECONCILIATION va SHCLOG co du lieu khop        
                    WHEN MATCHED THEN
                    
                    UPDATE SET A.PROCESS_CODE =
                        CASE 
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE in (112,113,114,115) THEN 0
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE not in (112,113,114,115) THEN 1
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE in (112,113,114,115)  THEN 0
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (112,113,114,115)  THEN 1
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE in (112,113,114,115)  THEN 1
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE not in (112,113,114,115)  THEN 1
                            ELSE NULL
                        END,
                        A.PROCESS_CONTENT=       
                        CASE 
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE in (112,113,114,115) THEN 'Không dua vào file Sai l?ch'
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE not in (112,113,114,115)  THEN 'C?nh báo email'
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE in (112,113,114,115)  THEN 'Không dua vào file Sai l?ch'
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (112,113,114,115)  THEN 'C?nh báo email'
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE in (112,113,114,115)  THEN 'C?nh báo email'
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE not in (112,113,114,115)  THEN 'C?nh báo email'
                            ELSE NULL
                        END,
                        A.RECONCILATION_RESULT=
                        CASE 
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE in (112,113,114,115) THEN 0
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE not in (112,113,114,115)  THEN 1
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE in (112,113,114,115)  THEN 0
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (112,113,114,115)  THEN 1
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE in (112,113,114,115)  THEN 1
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE not in (112,113,114,115)  THEN 1
                            ELSE NULL
                        END,
                        A.SETTLEMENT_STATUS=
                        CASE 
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE in (112,113,114,115) THEN 0
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE not in (112,113,114,115)  THEN 2
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE in (112,113,114,115)  THEN 0
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (112,113,114,115)  THEN 2
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE in (112,113,114,115)  THEN 1
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE not in (112,113,114,115)  THEN 0
                            ELSE NULL
                        END,
                        A.RESPONSE_CODE_IBFT=
                        CASE 
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE in (112,113,114,115) THEN B.RESPCODE
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE not in (112,113,114,115)  THEN B.RESPCODE
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE in (112,113,114,115)  THEN B.RESPCODE
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (112,113,114,115)  THEN B.RESPCODE
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE in (112,113,114,115)  THEN B.RESPCODE
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE not in (112,113,114,115)  THEN B.RESPCODE
                            ELSE NULL
                        END,
                        A.EDIT_DATE=sysdate,
                        A.UPDATE_CASE =         
                        CASE 
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE in (112,113,114,115) THEN '(ACH Receiver) - ACH: 0000 và IBFT: '||B.RESPCODE||' - Cân kh?p'
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE not in (112,113,114,115) THEN '(ACH Receiver) - ACH: 0000 và IBFT: '||B.RESPCODE||' - L?ch thi?u'
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE in (112,113,114,115)  THEN '(ACH Receiver) - ACH: 0068 và IBFT: '||B.RESPCODE||' - Cân kh?p'
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (112,113,114,115) THEN '(ACH Receiver) - ACH: 0068 và IBFT: '||B.RESPCODE||' - L?ch thi?u'
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE in (112,113,114,115)  THEN '(ACH Receiver) - ACH: 00xx và IBFT: '||B.RESPCODE||' - L?ch th?a'
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE not in (112,113,114,115)  THEN '(ACH Receiver) - ACH: 00xx và IBFT: '||B.RESPCODE||' - kh?p'
                            ELSE NULL
                        END,
                        A.RECONCIL_CASE =         
                        CASE 
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE in (112,113,114,115) THEN 'R1'
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE not in (112,113,114,115) THEN 'R2'
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE in (112,113,114,115)  THEN 'R1'
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (112,113,114,115) THEN 'R2'
                            WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE in (112,113,114,115)  THEN 'R3'
                            --WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE not in (112,113,114,115)  THEN 'R4'
                            ELSE NULL
                        END,
                        A.STT=B.STT
                    ---- Neu tim du lieu o bang SHCLOG co Respcode in (112,113,114,115) khong co trong bang ACH_RECONCILIATION    
                    WHEN NOT MATCHED THEN
                        insert(
                          MTI,
                          PRIMARY_ACCOUNT_NUMBER_F2,
                          PROCESSING_CODE_F3,
                          SERVICE_CODE_F62,
                          TRANSACTION_CHANNEL_CODE,
                          TRANSACTION_AMOUNT_F4,
                          REAL_TRANSACTION_AMOUNT,
                          CURRENCY_CODE_F49,
                          SETTLEMENT_AMOUNT_F5,
                          SETTLEMENT_CURRENCY_CODE_F50,
                          SETTLEMENT_EXCHANGE_RATE,
                          BILLING_AMOUNT_F6,
                          REAL_CARDHOLDER_AMOUNT,
                          CARDHOLDER_CURRENCY_CODE_F51,
                          CARDHOLDER_CONVERSION_RATE,
                          SYSTEM_TRACE_F11,
                          LOCAL_TIME_F12,
                          LOCAL_DATE_F13,
                          SETTLEMENT_DATE_F15,
                          MERCHANT_TYPE_F18,
                          POS_ENTRY_MODE_F22,
                          POS_CONDITION_CODE_F25,
                          TERM_ID_F41,
                          ACQ_CODE_F32,
                          ISS_CODE,
                          CARD_ACCEPTOR_CODE_F42,
                          BENICIFIARY_CODE_F100,
                          SOURCE_ACCOUNT_F102,
                          DESTINATION_ACCOUNT_F103,              
                          REF_NUMBER_F37,
                          AUTHORIZATION_CODE_F38,              
                          ACH_FILE_ROLE,
                          RESPONSE_CODE_IBFT,
                          STT,
                          PROCESS_CODE,
                          PROCESS_CONTENT,
                          RECONCILATION_RESULT,
                          SETTLEMENT_STATUS,
                          UPDATE_CASE,
                          SETTLEMENT_DATE_YEAR,
                          LOCAL_DATE_YEAR,
                          RECONCIL_CASE,
                          EDIT_DATE
                        )
                        values(
                            '0310',
                            B.PAN,
                            B.PCODE,
                            null, --- F62,
                            null, --TRANSACTION_CHANNEL_CODE
                            B.AMOUNT,
                            null, ---REAL_TRANSACTION_AMOUNT
                            B.ACQ_CURRENCY_CODE,
                            B.F5,
                            B.SETTLEMENT_CODE,
                            B.SETTLEMENT_RATE,
                            B.F6,
                            null, ---REAL_CARDHOLDER_AMOUNT
                            null, --CARDHOLDER_CURRENCY_CODE_F51
                            null, --CARDHOLDER_CONVERSION_RATE
                            B.ORIGTRACE,
                            B.LOCAL_TIME,
                            TO_CHAR(B.LOCAL_DATE,'mmdd'),
                            TO_CHAR(B.SETTLEMENT_DATE,'mmdd'),
                            B.MERCHANT_TYPE,
                            B.POS_ENTRY_CODE,
                            B.POS_CONDITION_CODE,
                            B.TERMID,
                            B.ACQUIRER,
                            B.ISSUER,
                            null, ---CARD_ACCEPTOR_CODE_F42
                            B.BB_BIN_ORIG,
                            TRIM(SUBSTR(ACCTNUM,1,INSTR(ACCTNUM||'|','|')-1)), ----SOURCE_ACCOUNT_F102 
                            TRIM(SUBSTR(ACCTNUM,INSTR(ACCTNUM||'|','|')+1,LENGTH(ACCTNUM))), --DESTINATION_ACCOUNT_F103
                            B.REFNUM,
                            B.AUTHNUM,
                            'BNB - SHCLOG',
                            B.RESPCODE,
                            B.STT,
                            '4',
                            'C?nh báo email',                
                            1,
                            CASE 
                                WHEN B.RESPCODE in (112,113,114,115) THEN 2
                                ELSE NULL
                            END,
                            CASE 
                                WHEN B.RESPCODE in (112,113,114,115)  THEN '(ACH Receiver) - ACH: No record và IBFT: '||B.RESPCODE||' - L?ch th?a'
                                ELSE NULL
                            END,
                            B.SETTLEMENT_DATE,
                            B.LOCAL_DATE,
                            CASE 
                                WHEN B.RESPCODE in (112,113,114,115)  THEN 'R5'
                                ELSE NULL
                            END,
                            sysdate
                            
                        );  
                    commit;
                    --- Ket thuc Xu ly cac case tinh huong: ACH File va SHCLOG co; SHCLOG co va ACH File khong co 
                    --- Cap nhat cac giao dich doi soat thanh cong 
                    UPDATE SHCLOG
                    SET ACH_RECONCIL_STATUS = 'IBFT Return Reconciliation Successful',ACH_EDIT_DATE= sysdate
                    WHERE edit_date between dt.EDIT_DATE and dt.EDIT_DATE + 1 - 1/86400
                         And Issuer_rp = 980471
                         And substr(PCODE,0,2) in ('91','42')
                         And MSGTYPE = 0210
                         And EDIT_DATE is not null
                         And Fee_note is not null
                         And Respcode in (112,113,114,115)
                         --And ACH_RECONCIL_STATUS ='IBFT Online Reconciliation Successful'
                         And STT IN (SELECT distinct STT FROM
                                  ACH_RECONCILIATION
                                  WHERE PROCESS_CODE in (0,1,2,4)
                                  And ACH_FILE_ROLE in ('BNB','BNB - SHCLOG')
                                  And MTI =0310
                                  And EDIT_DATE_ACH_FILE= dt.EDIT_DATE
                                  And STT is not null);
                    COMMIT;         
                    --- cap nhat cac giao dich file ACH co du lieu ma shclog khong co du lieu
                    UPDATE ACH_RECONCILIATION 
                    SET PROCESS_CODE=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 1  --- case R2.00
                            WHEN RECONCILIATION_CODE=0068 THEN 1  --- case R2.68
                            WHEN RECONCILIATION_CODE not in (0000,0068) THEN 1  --- case R4
                            ELSE NULL
                        END,
                        PROCESS_CONTENT=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 'C?nh báo email'  --- case R2.00
                            WHEN RECONCILIATION_CODE=0068 THEN 'C?nh báo email'  --- case R2.68
                            WHEN RECONCILIATION_CODE not in (0000,0068) THEN 'C?nh báo email'  --- case R4                           
                            ELSE NULL
                        END, 
                        RECONCILATION_RESULT=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 1  --- case R2.00
                            WHEN RECONCILIATION_CODE=0068 THEN 1  --- case R2.68
                            WHEN RECONCILIATION_CODE not in (0000,0068) THEN 1 --- case R4   
                            ELSE NULL
                        END, 
                        SETTLEMENT_STATUS=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 2  --- case R2.00
                            WHEN RECONCILIATION_CODE=0068 THEN 2  --- case R2.68
                            WHEN RECONCILIATION_CODE not in (0000,0068) THEN 2 -- case R4
                            ELSE NULL
                        END ,
                        EDIT_DATE=sysdate,
                        UPDATE_CASE=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN '(ACH Receiver) - ACH: 0000 và IBFT: No Record - l?ch thi?u (ACH trích n? IBFT nhung IBFT không trích n? TCNL)'  --- case R2.00
                            WHEN RECONCILIATION_CODE=0068 THEN '(ACH Receiver) - ACH: 0068 và IBFT: No Record - l?ch thi?u (ACH trích n? IBFT nhung IBFT không trích n? TCNL)'  --- case R2.68
                            WHEN RECONCILIATION_CODE not in (0000,0068) THEN '(ACH Receive) - ACH = 00xx, xx <> 00,68 và IBFT: No Record - l?ch sai (Kh?p)'  --- case R4
                            ELSE NULL
                        END,
                        RECONCIL_CASE=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 'R2'  --- case R2.00
                            WHEN RECONCILIATION_CODE=0068 THEN 'R2'  --- case R2.68
                            WHEN RECONCILIATION_CODE not in (0000,0068) THEN 'R4' --- case R4
                            ELSE NULL
                        END         
                    WHERE PROCESS_CODE IS NULL
                    AND ACH_FILE_ROLE='BNB'
                    And MTI =0310
                    And EDIT_DATE_ACH_FILE = dt.EDIT_DATE;  
                    --- Ket thuc xu ly cap nhat cac giao dich MTI = 0310 (Giao dich IBFT Return - IBFT) cac giao dich RC  = 112/113/114/115 trong bang SHCLOG
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - END Process (ACH Receiver). EDIT_DATE: '|| dt.EDIT_DATE ,'UPDATE_ACH_RECONCIL_RETURN'); 
                END IF;

            END LOOP;
            
    CLOSE cs;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'0','END' ,'UPDATE_ACH_RECONCIL_RETURN');
    
    
    
EXCEPTION
   WHEN OTHERS THEN
        emesg := SQLERRM;
        ecode := SQLCODE;
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'-1',ckexc||' - FILE Round: '||iRound||', Err:'||emesg,'UPDATE_ACH_RECONCIL_RETURN');
        Commit;
END;
/
