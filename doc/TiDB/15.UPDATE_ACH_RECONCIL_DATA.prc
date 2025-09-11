CREATE OR REPLACE PROCEDURE RPT.UPDATE_ACH_RECONCIL_DATA
(
    sFrom_Date varchar2,
    sTo_Date varchar2,
    sFileRole varchar2
) 
AS
/******************************************************************************
   NAME:       UPDATE_ACH_RECONCIL_DATA
   PURPOSE:    

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        09/10/2019   sondt       1. Created this procedure.

   NOTES: xu ly cac du lieu nhan duoc tu ACH, danh dau theo cac yeu cau cua SRS

   Automatically available Auto Replace Keywords:
      Object Name:     UPDATE_ACH_RECONCIL_DATA
      Sysdate:         09/10/2019
      Date and Time:   09/10/2019, 9:14:33 AM, and 09/10/2019 9:14:33 AM
      Username:        sondt (set in TOAD Options, Procedure Editor)
      Table Name:       (set in the "New PL/SQL Object" dialog)

******************************************************************************/    
    
    emesg   VARCHAR2(200);
    iRound  Integer:=0;
    Cursor cs IS
        select * From
        (
            select to_date(sTo_Date,'dd/mm/yyyy')+1 - rownum as SETTLEMENT_DATE_YEAR,sFileRole As ACH_FILE_ROLE
            FROM DUAL
            CONNECT BY ROWNUM < 40
        )
        where SETTLEMENT_DATE_YEAR >= to_date(sFrom_Date,'dd/mm/yyyy')
        order by SETTLEMENT_DATE_YEAR asc;

    dt cs%ROWTYPE; 
    
BEGIN   
    /*
        Tr?ng thái PROCESS_CODE: 0: không dua vào File sai l?ch; 1: c?nh báo email; 2: dua vào File sai l?ch; 3: D? li?u sai phiên quy?t toán; 4: SHCLOG co va ACH khong co
        Tr?ng thái RECONCILATION_RESULT: 0: cân kh?p; 1: l?ch sai; 2: l?ch dúng
        Tr?ng thái SETTLEMENT_STATUS: 0: cân kh?p; 1: l?ch th?a; 2: l?ch thi?u
    */ 
    --iRound:=iRound+1;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'0','START' ,'UPDATE_ACH_RECONCIL_DATA');
    OPEN cs;
        LOOP
            FETCH cs INTO dt;
            EXIT WHEN cs%NOTFOUND;   
                --- cap nhat cho cac giao dich ACH = sender(ISS), IBFT = Receiver
                IF(dt.ACH_FILE_ROLE ='ISS') THEN
                    iRound:=iRound+1;
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - BEGIN Process (ACH Sender). SETTLEMENT_DATE: '|| dt.SETTLEMENT_DATE_YEAR ,'UPDATE_ACH_RECONCIL_DATA');
                    commit;
                    --- Bat dau xu ly cap nhat cac giao dich MTI = 0210 (Giao dich Payment - IBFT)
                    --- Bat dau Xu ly cac case tinh huong: ACH File va SHCLOG co; SHCLOG co va ACH File khong co                    
                    MERGE INTO
                    (SELECT   * 
                        FROM ACH_RECONCILIATION
                        WHERE SETTLEMENT_DATE_YEAR = dt.SETTLEMENT_DATE_YEAR
                        And ACH_FILE_ROLE='ISS'
                        And MTI =0210
                        And PROCESS_CODE IS NULL
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
								ACCTNUM
                             FROM SHCLOG_SETT_IBFT
                             WHERE Issuer_rp = 980471
                             And substr(PCODE,0,2) in ('91','42')
                             And MSGTYPE = 0210
                             And Edit_date is null
                             And ACH_RECONCIL_STATUS is null
                         ) B
                    ON  
                        ( B.LOCAL_DATE= A.LOCAL_DATE_YEAR
                        AND B.SETTLEMENT_DATE = A.SETTLEMENT_DATE_YEAR
                        AND B.LOCAL_TIME = A.LOCAL_TIME_F12
                        AND B.ACQUIRER = A.ACQ_CODE_F32
                        AND B.ORIGTRACE = A.SYSTEM_TRACE_F11  
                        AND B.AMOUNT = A.SETTLEMENT_AMOUNT_F5  
                        AND TRIM(B.PAN) = A.PRIMARY_ACCOUNT_NUMBER_F2                                      
                        AND TRIM(B.TERMID) = TRIM(A.TERM_ID_F41)           
                        )
                    --- Neu tim thay du lieu o bang ACH_RECONCILIATION va SHCLOG co du lieu khop        
                    WHEN MATCHED THEN
                    
                    UPDATE SET A.PROCESS_CODE =
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = 0 THEN 0 --- Case P1
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = 68 THEN 0  --- Case P2
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE  not in (0,68) THEN 1  --- Case P3
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = 0 THEN 0  --- Case P5.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = 68 THEN 0  --- Case P5.68
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN 2  --- Case P6
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = 0 THEN 1  --- Case P8.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = 68 THEN 1  --- Case P8.68
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND SUBSTR(A.RECONCILIATION_CODE,3,2) = B.RESPCODE THEN 0  --- Case P9
                            ELSE NULL
                        END,
                        A.PROCESS_CONTENT=       
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = 0 THEN 'Khong dua vao file Sai lech' --- Case P1
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = 68 THEN 'Khong dua vao file Sai lech'  --- Case P2
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE  not in (0,68) THEN 'Canh bao email'  --- Case P3
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE =0 THEN 'Khong dua vao file Sai lech'  --- Case P5.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE =68 THEN 'Khong dua vao file Sai lech'  --- Case P5.68
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN 'Dua vao File sai lech'  --- Case P6
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = 0 THEN 'Canh bao email'  --- Case P8.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = 68 THEN 'Canh bao email'  --- Case P8.68
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND SUBSTR(A.RECONCILIATION_CODE,3,2) = B.RESPCODE THEN 'Khong dua vao file Sai lech'  --- Case P9
                            ELSE NULL
                        END,
                        A.RECONCILATION_RESULT=
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = 0 THEN 0 --- Case P1
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = 68 THEN 0  --- Case P2
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE  not in (0,68) THEN 1  --- Case P3
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = 0 THEN 0  --- Case P5.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = 68 THEN 0  --- Case P5.68
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN 2  --- Case P6
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = 0 THEN 1  --- Case P8.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = 68 THEN 1  --- Case P8.68
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND SUBSTR(A.RECONCILIATION_CODE,3,2) = B.RESPCODE THEN 0  --- Case P9
                            ELSE NULL
                        END,
                        A.SETTLEMENT_STATUS=
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = 0 THEN 0 --- Case P1
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = 68 THEN 0  --- Case P2
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE  not in (0,68) THEN 1  --- Case P3
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = 0 THEN 0  --- Case P5.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = 68 THEN 0  --- Case P5.68
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN 1  --- Case P6
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = 0 THEN 2  --- Case P8.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = 68 THEN 2  --- Case P8.68
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND SUBSTR(A.RECONCILIATION_CODE,3,2) = B.RESPCODE THEN 0  --- Case P9
                            ELSE NULL
                        END,
                        A.RESPONSE_CODE_IBFT=
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = 0 THEN 0 --- Case P1
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = 68 THEN 68  --- Case P2
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE  not in (0,68) THEN B.RESPCODE  --- Case P3
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = 0 THEN 0  --- Case P5.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = 68 THEN 68  --- Case P5.68
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN B.RESPCODE  --- Case P6
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = 0 THEN B.RESPCODE  --- Case P8.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = 68 THEN B.RESPCODE  --- Case P8.68
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND SUBSTR(A.RECONCILIATION_CODE,3,2) = B.RESPCODE THEN B.RESPCODE  --- Case P9
                            ELSE NULL
                        END,
                        
                        A.TRANSACTION_STATUS = 
                        CASE
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN '00'||B.RESPCODE  --- Case P6
                            ELSE NULL
                        END,
                        A.EDIT_DATE=sysdate,
                        A.UPDATE_CASE =         
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = 0 THEN '(ACH Sender) - ACH: 0000 va IBFT: 00 - Can khop' --- Case P1
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = 68 THEN '(ACH Sender) - ACH: 0000 va IBFT: 68 - Can khop'  --- Case P2
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE  not in (0,68) THEN '(ACH Sender) - ACH: 0000 va IBFT: <> 00 and 68 - Lech thua (IBFT duoc QT thua)'  --- Case P3
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE =0 THEN '(ACH Sender) - ACH: 0068 va IBFT: 00 - Can khop'  --- Case P5.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = 68 THEN '(ACH Sender) - ACH: 0068 va IBFT: 68 - Can khop'  --- Case P5.68
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN '(ACH Sender) - ACH: 0068 va IBFT: <>00 and 68 - lech thua (IBFT duoc QT thua)'  --- Case P6
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = 0 THEN '(ACH Sender) - ACH: 00xx va IBFT: 00 - lech thieu'  --- Case P8.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = 68 THEN '(ACH Sender) - ACH: 00xx va IBFT: 68 - lech thieu'  --- Case P8.68
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND SUBSTR(A.RECONCILIATION_CODE,3,2) = B.RESPCODE THEN '(ACH Sender) - ACH: 00xx va IBFT: xx - Can khop'  --- Case P9
                            ELSE NULL
                        END,
                        A.RECONCIL_CASE =         
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = 0 THEN 'P1' --- Case P1
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = 68 THEN 'P2'  --- Case P2
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE  not in (0,68) THEN 'P3'  --- Case P3
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE =0 THEN 'P5'  --- Case P5.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = 68 THEN 'P5'  --- Case P5.68
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN 'P6'  --- Case P6
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = 0 THEN 'P8'  --- Case P8.00
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = 68 THEN 'P8'  --- Case P8.68
                            WHEN ACH_FILE_ROLE='ISS' AND A.RECONCILIATION_CODE not in ('0000','0068') AND SUBSTR(A.RECONCILIATION_CODE,3,2) = B.RESPCODE THEN 'P9'  --- Case P9
                            ELSE NULL
                        END,
                        A.STT=B.STT
                    ---- Neu tim du lieu co bang SHCLOG va khong co trong bang ACH_RECONCILIATION    
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
                            LPAD(B.MSGTYPE,4,'0'),
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
                            --null, --SOURCE_ACCOUNT_F102
                            --null, --DESTINATION_ACCOUNT_F103
                            B.REFNUM,
                            B.AUTHNUM,
                            'ISS - SHCLOG',
                            B.RESPCODE,
                            B.STT,
                            '4',
                            'Canh bao email',                
                            1,
                            CASE 
                                WHEN B.RESPCODE = 0 THEN 2  --- case P11.00
                                WHEN B.RESPCODE = 68 THEN 2  --- case P11.68
                                WHEN B.RESPCODE not in (0,68) THEN 0  --- case P12
                                ELSE NULL
                            END,
                            CASE 
                                WHEN B.RESPCODE =0  THEN '(ACH Sender) - ACH: No record va IBFT: 00 - Lech thieu'  --- case P11.00
                                WHEN B.RESPCODE =68  THEN '(ACH Sender) - ACH: No record va IBFT: 68 - Lech thieu'  --- case P11.68
                                WHEN B.RESPCODE not in (0,68) THEN '(ACH Sender) - ACH: No record va IBFT: <> 00 and 68 - can khop'  --- case P12
                                ELSE NULL
                            END,
                            B.SETTLEMENT_DATE,
                            B.LOCAL_DATE,
                            CASE 
                                WHEN B.RESPCODE =0  THEN 'P11'  --- case P11.00
                                WHEN B.RESPCODE =68  THEN 'P11'  --- case P11.68
                                WHEN B.RESPCODE not in (0,68) THEN 'P12'  --- case P12
                                ELSE NULL
                            END,
                            sysdate
                            
                        );  
                    commit;
                    --- Ket thuc Xu ly cac case tinh huong: ACH File va SHCLOG co; SHCLOG co va ACH File khong co 
                    --- Cap nhat cac giao dich doi soat thanh cong 
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - Finish Merge ACH File Data And SHCLOG (ACH Sender). SETT_DATE: '|| dt.SETTLEMENT_DATE_YEAR ,'UPDATE_ACH_RECONCIL_DATA');
					commit;
                    UPDATE SHCLOG_SETT_IBFT
                    SET ACH_RECONCIL_STATUS = 'IBFT Online Reconciliation Successful',ACH_EDIT_DATE= sysdate
                    WHERE substr(PCODE,0,2) in ('91','42')
                         And MSGTYPE = 0210
                         And ACH_RECONCIL_STATUS is null
                         And STT IN (SELECT STT FROM
                                  ACH_RECONCILIATION
                                  WHERE PROCESS_CODE in (0,1,2,4)
                                  And ACH_FILE_ROLE in ('ISS','ISS - SHCLOG')
                                  And MTI =0210
                                  And STT is not null);
                    COMMIT; 
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - Finish Update Reconciliation Successful - SHCLOG (ACH Sender). SETT_DATE: '|| dt.SETTLEMENT_DATE_YEAR ,'UPDATE_ACH_RECONCIL_DATA');
                    --- cap nhat cac giao dich file ACH co du lieu ma shclog khong co du lieu
                    commit;
					UPDATE ACH_RECONCILIATION 
                    SET PROCESS_CODE=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 1  --- case P4
                            WHEN RECONCILIATION_CODE=0068 THEN 2  --- case P7
                            WHEN RECONCILIATION_CODE  not in (0068,0000) THEN 1  --- case P10
                            ELSE NULL
                        END,
                        PROCESS_CONTENT=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 'Canh bao email'  --- case P4
                            WHEN RECONCILIATION_CODE=0068 THEN 'Dua vao File sai lech'  --- case P7
                            WHEN RECONCILIATION_CODE  not in (0068,0000) THEN 'Canh bao email'  --- case P10
                            
                            ELSE NULL
                        END, 
                        RECONCILATION_RESULT=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 1  --- case P4
                            WHEN RECONCILIATION_CODE=0068 THEN 1  --- case P7
                            WHEN RECONCILIATION_CODE  not in (0068,0000) THEN 1  --- case P10
                            ELSE NULL
                        END, 
                        SETTLEMENT_STATUS=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 1  --- case P4
                            WHEN RECONCILIATION_CODE=0068 THEN 1  --- case P7
                            WHEN RECONCILIATION_CODE  not in (0068,0000) THEN 0  --- case P10
                            ELSE NULL
                        END ,
                        TRANSACTION_STATUS=
                        CASE
                            WHEN RECONCILIATION_CODE=0068 THEN '0091'  --- case P7
                            ELSE NULL
                        END ,
                        EDIT_DATE=sysdate,
                        UPDATE_CASE=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN '(ACH Sender) - ACH: 0000 va IBFT: No Record - lech thua (IBFT duoc QT thua)'  --- case P4
                            WHEN RECONCILIATION_CODE=0068 THEN '(ACH Sender) - ACH: 0068 va IBFT: No Record - lech thua (IBFT duoc QT thua)'  --- case P7
                            WHEN RECONCILIATION_CODE  not in (0068,0000) THEN '(ACH Sender) - ACH: 00xx va IBFT: No Record - Can khop'  --- case P10
                            ELSE NULL
                        END,
                        RECONCIL_CASE=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 'P4'  --- case P4
                            WHEN RECONCILIATION_CODE=0068 THEN 'P7'  --- case P7
                            WHEN RECONCILIATION_CODE  not in (0068,0000) THEN 'P10'  --- case P10
                            ELSE NULL
                        END         
                    WHERE SETTLEMENT_DATE_YEAR = dt.SETTLEMENT_DATE_YEAR
                    AND ACH_FILE_ROLE ='ISS'
                    And MTI =0210
                    And PROCESS_CODE is null;        
                    commit; 
                    --- Ket thuc xu ly cap nhat cac giao dich MTI = 0210 (Giao dich Payment - IBFT)
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - END Process (ACH Sender). SETTLEMENT_DATE: '|| dt.SETTLEMENT_DATE_YEAR ,'UPDATE_ACH_RECONCIL_DATA');
					commit;
                ELSIF(dt.ACH_FILE_ROLE ='BNB') THEN
                    iRound:=iRound+1;
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - BEGIN Process (ACH Receiver). SETTLEMENT_DATE: '|| dt.SETTLEMENT_DATE_YEAR ,'UPDATE_ACH_RECONCIL_DATA');
					commit;
                    --- Cap nhat cac giao dich ACH = Receiver (BNB) va IBFT = Sender
                    MERGE INTO
                    (SELECT * 
                        FROM ACH_RECONCILIATION
                        WHERE SETTLEMENT_DATE_YEAR = dt.SETTLEMENT_DATE_YEAR
                        And ACH_FILE_ROLE='BNB'
                        And MTI =0210
                        And PROCESS_CODE IS NULL
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
								ACCTNUM,
                                ORIGRESPCODE											
                             FROM SHCLOG_SETT_IBFT
                             WHERE BB_BIN = 980471
                             And substr(PCODE,0,2) in ('91','42')
                             And MSGTYPE = 0210
                             --And Fee_note is not null
                             And Edit_date is null
                             And ACH_RECONCIL_STATUS is null
                         ) B
                    ON  
                        ( B.LOCAL_DATE= A.LOCAL_DATE_YEAR
                        AND B.SETTLEMENT_DATE = A.SETTLEMENT_DATE_YEAR
                        AND B.LOCAL_TIME= A.LOCAL_TIME_F12
                        AND B.ACQUIRER=CONVERT_ACH_INS(A.ACQ_CODE_F32)
                        AND B.ORIGTRACE= A.SYSTEM_TRACE_F11  
                        AND B.AMOUNT=A.SETTLEMENT_AMOUNT_F5  
                        AND TRIM(B.PAN) = A.PRIMARY_ACCOUNT_NUMBER_F2                     
                        AND TRIM(B.TERMID) = TRIM(A.TERM_ID_F41)        
                        )
                    WHEN MATCHED THEN
                        
                        UPDATE SET A.PROCESS_CODE =
                            CASE 
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = '00' THEN 0  -- Case P1.00
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = '68' THEN 0  -- Case P1.68
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE  not in (0,68) THEN 1  -- Case P2
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = '00' THEN 1  -- Case P4
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = '68' THEN 0  -- Case P5
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN 1  -- Case P6
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = '00' THEN 1  -- Case P8
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = '68' THEN 1  -- Case P9
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND SUBSTR(A.RECONCILIATION_CODE,3,2) = B.RESPCODE THEN 0  -- Case P10
                                ELSE NULL
                            END,
                            A.PROCESS_CONTENT=       
                            CASE 
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = '00' THEN 'Khong dua vao file Sai lech'  -- Case P1.00
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = '68' THEN 'Khong dua vao file Sai lech'  -- Case P1.68
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE  not in (0,68) THEN 'Canh bao email'  -- Case P2
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = '00' THEN 'Canh bao email'  -- Case P4
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = '68' THEN 'Khong dua vao file Sai lech'  -- Case P5
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN 'Canh bao email'  -- Case P6
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = '00' THEN 'Canh bao email'  -- Case P8
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = '68' THEN 'Canh bao email'  -- Case P9
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND SUBSTR(A.RECONCILIATION_CODE,3,2) = B.RESPCODE THEN 'Khong dua vao file Sai lech'  -- Case P10
                                ELSE NULL
                            END,
                            A.RECONCILATION_RESULT=
                            CASE 
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = '00' THEN 0  -- Case P1.00
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = '68' THEN 0  -- Case P1.68
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE  not in (0,68) THEN 1  -- Case P2
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = '00' THEN 0  -- Case P4
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = '68' THEN 0  -- Case P5
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN 1  -- Case P6
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = '00' THEN 1  -- Case P8
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = '68' THEN 2  -- Case P9
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND SUBSTR(A.RECONCILIATION_CODE,3,2) = B.RESPCODE THEN 0  -- Case P10
                                ELSE NULL
                            END,
                            A.SETTLEMENT_STATUS=
                            CASE

                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = '00' THEN 0  -- Case P1.00
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = '68' THEN 0  -- Case P1.68
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE  not in (0,68) THEN 2  -- Case P2
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = '00' THEN 0  -- Case P4
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = '68' THEN 0  -- Case P5
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN 2  -- Case P6
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = '00' THEN 1  -- Case P8
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = '68' THEN 1  -- Case P9
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND SUBSTR(A.RECONCILIATION_CODE,3,2) = B.RESPCODE THEN 0  -- Case P10
                                ELSE NULL
                            END,
                            A.RESPONSE_CODE_IBFT=
                            CASE 
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = '00' THEN 0  -- Case P1.00
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = '68' THEN 68  -- Case P1.68
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE  not in (0,68) THEN B.RESPCODE  -- Case P2
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = '00' THEN 0  -- Case P4
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = '68' THEN 68  -- Case P5
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN B.RESPCODE  -- Case P6
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = '00' THEN B.RESPCODE  -- Case P8
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = '68' THEN B.RESPCODE  -- Case P9
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND SUBSTR(A.RECONCILIATION_CODE,3,2) = B.RESPCODE THEN B.RESPCODE  -- Case P10
                                
                                ELSE NULL
                            END,
                            
                            A.TRANSACTION_STATUS = 
                            CASE
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN '00'||B.RESPCODE  -- Case P6
                                ELSE NULL
                            END,
                            A.EDIT_DATE=sysdate,
                            A.UPDATE_CASE =         
                            CASE 
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = '00' THEN '(ACH Receiver) - ACH: 0000 va IBFT: 00 - Can khop'  -- Case P1.00
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = '68' THEN '(ACH Receiver) - ACH: 0000 va IBFT: 68 - Can khop'  -- Case P1.68
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE  not in (0,68) THEN '(ACH Receiver) - ACH: 0000 va IBFT: <> 00 and 68 - Lech thieu'  -- Case P2
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = '00' THEN '(ACH Receiver) - ACH: 0068 va IBFT: 00 - Can khop'  -- Case P4
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = '68' THEN '(ACH Receiver) - ACH: 0068 va IBFT: 68 - Can khop'  -- Case P5
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN '(ACH Receiver) - ACH: 0068 va IBFT: <> 00 and 68 - lech sai'  -- Case P6
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = '00' THEN '(ACH Receiver) - ACH: 00xx va IBFT: 00 - lech thua (IBFT du tien)'  -- Case P8
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = '68' THEN '(ACH Receiver) - ACH: 00xx va IBFT: 68 - lech thua (IBFT du tien)'  -- Case P9
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND SUBSTR(A.RECONCILIATION_CODE,3,2) = B.RESPCODE THEN '(ACH Receiver) - ACH: 00xx va IBFT: xx - Can khop'  -- Case P10
                                ELSE NULL
                            END,
                            A.RECONCIL_CASE =         
                            CASE 
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = '00' THEN 'P1'  -- Case P1.00
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE = '68' THEN 'P1'  -- Case P1.68
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0000' AND B.RESPCODE  not in (0,68) THEN 'P2'  -- Case P2
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = '00' THEN 'P4'  -- Case P4
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE = '68' THEN 'P5'  -- Case P5
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE = '0068' AND B.RESPCODE not in (0,68) THEN 'P6'  -- Case P6
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = '00' THEN 'P8'  -- Case P8
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND B.RESPCODE = '68' THEN 'P9'  -- Case P9
                                WHEN ACH_FILE_ROLE='BNB' AND A.RECONCILIATION_CODE not in ('0000','0068') AND SUBSTR(A.RECONCILIATION_CODE,3,2) = B.RESPCODE THEN 'P10'  -- Case P10
                                ELSE NULL
                            END,
                            A.STT=B.STT
                    WHEN NOT MATCHED THEN
                        insert(
                          MTI,
                          PRIMARY_ACCOUNT_NUMBER_F2,
                          PROCESSING_CODE_F3,
                          TRANSACTION_AMOUNT_F4,
                          CURRENCY_CODE_F49,
                          SETTLEMENT_AMOUNT_F5,
                          SETTLEMENT_CURRENCY_CODE_F50,
                          SETTLEMENT_EXCHANGE_RATE,
                          BILLING_AMOUNT_F6,
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
                            LPAD(B.MSGTYPE,4,'0'),
                            B.PAN,
                            B.PCODE,
                            B.AMOUNT,
                            B.ACQ_CURRENCY_CODE,
                            B.F5,
                            B.SETTLEMENT_CODE,
                            B.SETTLEMENT_RATE,
                            B.F6,
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
                            B.BB_BIN_ORIG,
							TRIM(SUBSTR(ACCTNUM,1,INSTR(ACCTNUM||'|','|')-1)), ----SOURCE_ACCOUNT_F102 
                            TRIM(SUBSTR(ACCTNUM,INSTR(ACCTNUM||'|','|')+1,LENGTH(ACCTNUM))), --DESTINATION_ACCOUNT_F103
                            --null, --SOURCE_ACCOUNT_F102
                            --null, --DESTINATION_ACCOUNT_F103
                            B.REFNUM,
                            B.AUTHNUM,
                            'BNB - SHCLOG',
                            B.ORIGRESPCODE,
                            B.STT,
                            '4',
                            'Canh bao email',                
                            CASE 
                                WHEN B.RESPCODE = 0 AND B.ORIGRESPCODE = 0 THEN 1  -- Case P12
                                WHEN B.RESPCODE in( 68,0) AND B.ORIGRESPCODE IN (68,97) THEN 2  -- Case P13
                                WHEN B.RESPCODE not in (0,68) THEN 1  -- Case P14
                                ELSE NULL
                            END,
                            CASE 
                                WHEN B.RESPCODE = 0 AND B.ORIGRESPCODE = 0  THEN 1  -- Case P12
                                WHEN B.RESPCODE in( 68,0) AND B.ORIGRESPCODE IN (68,97) THEN 1  -- Case P13
                                WHEN B.RESPCODE not in (0,68) THEN 0  -- Case P14
                                ELSE NULL
                            END,
                            CASE 
                                WHEN B.RESPCODE =0 AND B.ORIGRESPCODE = 0 THEN '(ACH Receiver) - ACH: No record va IBFT: 00 - Lech thua (IBFT du tien)'  -- Case P12
                                WHEN B.RESPCODE in(68,0) AND B.ORIGRESPCODE IN (68,97) THEN '(ACH Receiver) - ACH: No record va IBFT: 68 - Lech thua (IBFT du tien)'  -- Case P13
                                WHEN B.RESPCODE not in (0,68) THEN '(ACH Receiver) - ACH: No record va IBFT: <> 00 and 68 - can khop'  -- Case P14
                                ELSE NULL
                            END,
                            B.SETTLEMENT_DATE,
                            B.LOCAL_DATE,
                            CASE 
                                WHEN B.RESPCODE =0 AND B.ORIGRESPCODE = 0 THEN 'P12'  -- Case P12
                                WHEN B.RESPCODE in( 68,0) AND B.ORIGRESPCODE IN (68,97) THEN 'P13'  -- Case P13
                                WHEN B.RESPCODE not in (0,68) THEN 'P14'  -- Case P14
                                ELSE NULL
                            END,
                            sysdate
                            
                        );  
                    commit;
                    --- Cap nhat cac giao dich doi soat thanh cong
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - Finish Merge ACH File Data And SHCLOG (ACH Receiver). SETT_DATE: '|| dt.SETTLEMENT_DATE_YEAR ,'UPDATE_ACH_RECONCIL_DATA');
                    commit;
					UPDATE SHCLOG_SETT_IBFT
                    SET ACH_RECONCIL_STATUS = 'IBFT Online Reconciliation Successful',ACH_EDIT_DATE= sysdate
                    WHERE substr(PCODE,0,2) in ('91','42')
                         And MSGTYPE = 0210
                         And ACH_RECONCIL_STATUS is null 
                         And STT IN (SELECT STT FROM
                                  ACH_RECONCILIATION
                                  WHERE SETTLEMENT_DATE_YEAR= dt.SETTLEMENT_DATE_YEAR
                                  And ACH_FILE_ROLE in ('BNB','BNB - SHCLOG')
                                  And MTI =0210
                                  And STT is not null
                                  And PROCESS_CODE in (0,1,2,4));
                    COMMIT; 
                    --- cap nhat cac giao dich file ACH co du lieu ma shclog khong co du lieu
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - Finish Update Reconciliation Successful - SHCLOG (ACH Receiver). SETT_DATE: '|| dt.SETTLEMENT_DATE_YEAR ,'UPDATE_ACH_RECONCIL_DATA');
                    commit;
					UPDATE ACH_RECONCILIATION 
                    SET PROCESS_CODE=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 1  -- Case P3
                            WHEN RECONCILIATION_CODE=0068 THEN 1  -- Case P7
                            WHEN RECONCILIATION_CODE  not in (0068,0000) THEN 1  -- Case P11
                            ELSE NULL
                        END,
                        PROCESS_CONTENT=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 'Canh bao email'  -- Case P3
                            WHEN RECONCILIATION_CODE=0068 THEN 'Canh bao email'  -- Case P7
                            WHEN RECONCILIATION_CODE  not in (0068,0000) THEN 'Canh bao email'  -- Case P11
                            
                            ELSE NULL
                        END, 
                        RECONCILATION_RESULT=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 1  -- Case P3
                            WHEN RECONCILIATION_CODE=0068 THEN 1  -- Case P7
                            WHEN RECONCILIATION_CODE  not in (0068,0000) THEN 1  -- Case P11
                            ELSE NULL
                        END, 
                        SETTLEMENT_STATUS=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 2  -- Case P3
                            WHEN RECONCILIATION_CODE=0068 THEN 2  -- Case P7
                            WHEN RECONCILIATION_CODE  not in (0068,0000) THEN 0  -- Case P11
                            ELSE NULL
                        END ,
                        EDIT_DATE=sysdate,
                        UPDATE_CASE=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN '(ACH Receiver) - ACH: 0000 va IBFT: No Record - lech thieu'  -- Case P3
                            WHEN RECONCILIATION_CODE=0068 THEN '(ACH Receiver) - ACH: 0068 va IBFT: No Record - lech thieu'  -- Case P7
                            WHEN RECONCILIATION_CODE  not in (0068,0000) THEN '(ACH Receiver) - ACH: 00xx va IBFT: No Record - Can khop'  -- Case P11
                            ELSE NULL
                        END,
                        RECONCIL_CASE=
                        CASE
                            WHEN RECONCILIATION_CODE=0000 THEN 'P3'  -- Case P3
                            WHEN RECONCILIATION_CODE=0068 THEN 'P7'  -- Case P7
                            WHEN RECONCILIATION_CODE  not in (0068,0000) THEN 'P11'  -- Case P11
                            ELSE NULL
                        END         
                    WHERE SETTLEMENT_DATE_YEAR = dt.SETTLEMENT_DATE_YEAR
                    AND ACH_FILE_ROLE='BNB'
                    And MTI =0210
                    And PROCESS_CODE is null;        
                    commit;
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - END Process (ACH Receiver). SETTLEMENT_DATE: '|| dt.SETTLEMENT_DATE_YEAR ,'UPDATE_ACH_RECONCIL_DATA');
					commit;
                END IF; 
            
    END LOOP;
            
    CLOSE cs;
    iRound:=1;
    --- Xu ly cac giao d?ch doi soat Case 68,97    
    
    OPEN cs;
        LOOP
            FETCH cs INTO dt;
            EXIT WHEN cs%NOTFOUND;
                IF(dt.ACH_FILE_ROLE ='ISS') THEN
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - BEGIN Process (UPDATE_0210_ACH_SENDER). SETTLEMENT_DATE: '|| dt.SETTLEMENT_DATE_YEAR ,'UPDATE_ACH_RECONCIL_DATA');
                    commit;
                    UPDATE_0210_ACH_SENDER(dt.SETTLEMENT_DATE_YEAR);
                    commit;
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - END Process (UPDATE_0210_ACH_SENDER). SETTLEMENT_DATE: '|| dt.SETTLEMENT_DATE_YEAR ,'UPDATE_ACH_RECONCIL_DATA');
                    commit;
                    iRound:= iRound+1;
                ELSIF(dt.ACH_FILE_ROLE ='BNB') THEN
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - BEGIN Process (UPDATE_0210_ACH_RECEIVER). SETTLEMENT_DATE: '|| dt.SETTLEMENT_DATE_YEAR ,'UPDATE_ACH_RECONCIL_DATA');
                    commit;
                    UPDATE_0210_ACH_RECEIVER(dt.SETTLEMENT_DATE_YEAR);
                    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                    Values(sysdate,'0','Round '||iRound ||' - END Process (UPDATE_0210_ACH_RECEIVER). SETTLEMENT_DATE: '|| dt.SETTLEMENT_DATE_YEAR ,'UPDATE_ACH_RECONCIL_DATA');
                    commit;
                    iRound:= iRound+1;
                END IF;
        END LOOP;
                
        CLOSE cs;    
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'0','END' ,'UPDATE_ACH_RECONCIL_DATA');
    commit;
    
EXCEPTION
   WHEN OTHERS THEN
        emesg := SQLERRM;
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'-1','FILE Round: '||iRound||', Err:'||emesg,'UPDATE_ACH_RECONCIL_DATA');
        Commit;
END;
/
