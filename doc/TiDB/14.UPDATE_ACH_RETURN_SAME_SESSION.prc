CREATE OR REPLACE PROCEDURE RPT.UPDATE_ACH_RETURN_SAME_SESSION
(
    sFileRole varchar2
    --sFileName varchar2
)
AS
/******************************************************************************
   NAME:       UPDATE_ACH_RETURN_SAME_SESSION
   PURPOSE:    

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        09/10/2019   sondt       1. Created this procedure.

   NOTES: Cap nhat cac giao dich IBFT Return cung phien

   Automatically available Auto Replace Keywords:
      Object Name:     UPDATE_ACH_RETURN_SAME_SESSION
      Sysdate:         09/10/2019
      Date and Time:   09/10/2019, 9:14:33 AM, and 09/10/2019 9:14:33 AM
      Username:        sondt (set in TOAD Options, Procedure Editor)
      Table Name:       (set in the "New PL/SQL Object" dialog)

******************************************************************************/    
    
    Cursor cs IS
        Select distinct SETTLEMENT_DATE_YEAR,ACH_FILE_ROLE
        From ACH_RECONCILIATION
        Where PROCESS_CODE IS NULL
        And MTI =310
        And SETTLEMENT_DATE_YEAR < trunc(sysdate)
        And ACH_FILE_ROLE=sFileRole
        --And FILE_NAME= sFileName
        ;
        
    dt cs%ROWTYPE;       
    emesg   VARCHAR2(200);
    iRound  Integer:=0;
    CountUpdate Integer:=0;
BEGIN   
    /*
        Tr?ng thái PROCESS_CODE: 
        0: không dua vào File sai l?ch; 
        1: c?nh báo email; 
        2: dua vào File sai l?ch; 
        3: D? li?u sai phiên quy?t toán
        4: SHCLOG co va ACH khong co
        5: IBFT Return cung phien
        Tr?ng thái RECONCILATION_RESULT: 0: cân kh?p; 1: l?ch sai; 2: l?ch dúng
        Tr?ng thái SETTLEMENT_STATUS: 0: cân kh?p; 1: l?ch th?a; 2: l?ch thi?u
    */ 
    iRound:=iRound+1;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'0','Round '||iRound ||' - START' ,'UPDATE_ACH_RETURN_SAME_SESSION');
    commit;
    OPEN cs;
        LOOP
            FETCH cs INTO dt;
            EXIT WHEN cs%NOTFOUND; 
                CountUpdate:= 0;
                iRound:=iRound+1;
                Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                Values(sysdate,'0','Round '||iRound ||' - BEGIN Process (ACH Role: '|| dt.ACH_FILE_ROLE||' SETT_DATE: '|| dt.SETTLEMENT_DATE_YEAR ,'UPDATE_ACH_RETURN_SAME_SESSION');               
                MERGE INTO
                (SELECT LOCAL_DATE_YEAR,
                        SETTLEMENT_DATE_YEAR,
                        LOCAL_TIME_F12,
                        ACQ_CODE_F32,
                        SYSTEM_TRACE_F11,
                        PRIMARY_ACCOUNT_NUMBER_F2,
                        SETTLEMENT_AMOUNT_F5,
                        PROCESS_CODE,
                        RECONCIL_CASE,
                        EDIT_DATE,
                        STT,
                        UPDATE_CASE,
                        ACH_FILE_ROLE
                    FROM ACH_RECONCILIATION
                    WHERE SETTLEMENT_DATE_YEAR= dt.SETTLEMENT_DATE_YEAR
                    And MTI =0310
                    And PROCESS_CODE IS NULL
                 ) A
                USING 
                    (SELECT LOCAL_DATE_YEAR,
                        SETTLEMENT_DATE_YEAR,
                        LOCAL_TIME_F12,
                        ACQ_CODE_F32,
                        SYSTEM_TRACE_F11,
                        PRIMARY_ACCOUNT_NUMBER_F2,
                        SETTLEMENT_AMOUNT_F5,
                        STT
                        FROM ACH_RECONCILIATION
                        WHERE SETTLEMENT_DATE_YEAR= dt.SETTLEMENT_DATE_YEAR
                        And MTI =0210
                        And PROCESS_CODE IS NULL
                     ) B
                ON  
                    (B.LOCAL_DATE_YEAR= A.LOCAL_DATE_YEAR
                    AND B.SETTLEMENT_DATE_YEAR = A.SETTLEMENT_DATE_YEAR
                    AND B.LOCAL_TIME_F12= A.LOCAL_TIME_F12
                    AND B.ACQ_CODE_F32=A.ACQ_CODE_F32
                    AND B.SYSTEM_TRACE_F11= A.SYSTEM_TRACE_F11  
                    AND B.PRIMARY_ACCOUNT_NUMBER_F2= A.PRIMARY_ACCOUNT_NUMBER_F2
                    AND B.SETTLEMENT_AMOUNT_F5=A.SETTLEMENT_AMOUNT_F5                    
                    )
                --- Neu tim thay du lieu IBFT Payment (0210) va IBFT Return (0310) trong cung Phien       
                WHEN MATCHED THEN 
                                       
                    UPDATE SET A.PROCESS_CODE = 5, --- IBFT Return cung phien   
                        A.RECONCIL_CASE ='PRSS',   --- Payment Return same session                      
                        A.EDIT_DATE=sysdate,
                        A.STT= B.STT, --- Cap nhat gia tri STT da duoc insert tu buoc doi soat IBFT Payment
                        A.UPDATE_CASE =         
                        CASE 
                            WHEN ACH_FILE_ROLE='ISS' THEN '(ACH Sender) - IBFT Return same session'
                            --WHEN ACH_FILE_ROLE='BNB' THEN '(ACH Receiver) - IBFT Return same session'  
                            ELSE NULL
                        END;
                        CountUpdate:= CountUpdate+1;                    
                 commit; 
                 Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                 Values(sysdate,'0','Round '||iRound ||' - Count Update: '||CountUpdate ,'UPDATE_ACH_RETURN_SAME_SESSION');  
                 MERGE INTO
                (SELECT LOCAL_DATE_YEAR,
                        SETTLEMENT_DATE_YEAR,
                        LOCAL_TIME_F12,
                        ACQ_CODE_F32,
                        SYSTEM_TRACE_F11,
                        PRIMARY_ACCOUNT_NUMBER_F2,
                        SETTLEMENT_AMOUNT_F5,
                        PROCESS_CODE,
                        RECONCIL_CASE,
                        EDIT_DATE,
                        STT,
                        UPDATE_CASE,
                        ACH_FILE_ROLE
                        FROM ACH_RECONCILIATION
                        WHERE SETTLEMENT_DATE_YEAR= dt.SETTLEMENT_DATE_YEAR
                        And MTI =0210
                        And PROCESS_CODE IS NULL                
                 ) A
                USING 
                    (SELECT LOCAL_DATE_YEAR,
                        SETTLEMENT_DATE_YEAR,
                        LOCAL_TIME_F12,
                        ACQ_CODE_F32,
                        SYSTEM_TRACE_F11,
                        PRIMARY_ACCOUNT_NUMBER_F2,
                        SETTLEMENT_AMOUNT_F5,
                        STT
                    FROM ACH_RECONCILIATION
                    WHERE SETTLEMENT_DATE_YEAR= dt.SETTLEMENT_DATE_YEAR
                    And MTI =0310
                    And PROCESS_CODE = 5
                     ) B
                ON  
                    ( B.LOCAL_DATE_YEAR= A.LOCAL_DATE_YEAR
                    AND B.SETTLEMENT_DATE_YEAR = A.SETTLEMENT_DATE_YEAR
                    AND B.LOCAL_TIME_F12= A.LOCAL_TIME_F12
                    AND B.ACQ_CODE_F32=A.ACQ_CODE_F32
                    AND B.SYSTEM_TRACE_F11= A.SYSTEM_TRACE_F11  
                    AND B.PRIMARY_ACCOUNT_NUMBER_F2= A.PRIMARY_ACCOUNT_NUMBER_F2
                    AND B.SETTLEMENT_AMOUNT_F5=A.SETTLEMENT_AMOUNT_F5                   
   
                    )
                --- Neu tim thay du lieu IBFT Payment (0210) va IBFT Return (0310) trong cung Phien       
                WHEN MATCHED THEN                    
                    UPDATE SET A.PROCESS_CODE = 5, --- IBFT Return cung phien   
                        A.RECONCIL_CASE ='PRSS',   --- Payment Return same session                    
                        A.EDIT_DATE=sysdate,
                        A.STT= B.STT, --- Cap nhat gia tri STT da duoc insert tu buoc doi soat IBFT Payment
                        A.UPDATE_CASE =         
                        CASE 
                            WHEN ACH_FILE_ROLE='BNB' THEN '(ACH Sender) - IBFT Return same session'
                            --WHEN ACH_FILE_ROLE='BNB' THEN '(ACH Receiver) - IBFT Return same session'  
                            ELSE NULL
                        END;                    
                 commit;    
                 --- Cap nhat cac giao dich trong bang ACH_reconciliation gia tri STT
                 MERGE INTO
                ( SELECT LOCAL_DATE_YEAR,
                         SETTLEMENT_DATE_YEAR,
                         LOCAL_TIME_F12,
                         ACQ_CODE_F32,
                         SYSTEM_TRACE_F11,
                         SETTLEMENT_AMOUNT_F5,
                         PRIMARY_ACCOUNT_NUMBER_F2,
                         TERM_ID_F41,
                         STT
                    FROM ACH_RECONCILIATION
                    WHERE SETTLEMENT_DATE_YEAR= dt.SETTLEMENT_DATE_YEAR     
                    And RECONCIL_CASE ='PRSS' --- Payment Return same session 
                    And MTI =0210
                    And ACH_File_Role ='BNB'
                    And PROCESS_CODE = 5         
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
                                STT  
                             FROM SHCLOG_SETT_IBFT
                             WHERE SETTLEMENT_DATE = dt.SETTLEMENT_DATE_YEAR
                             And BB_BIN = 980471
                             And substr(PCODE,0,2) in ('91','42')
                             And MSGTYPE = 0210
                             And Edit_date is null
                             And ACH_RECONCIL_STATUS is null 
                    
                     ) B
                ON  
                     ( A.LOCAL_DATE_YEAR= B.LOCAL_DATE
                        AND A.SETTLEMENT_DATE_YEAR = B.SETTLEMENT_DATE
                        AND A.LOCAL_TIME_F12=B.LOCAL_TIME
                        AND CONVERT_ACH_INS(A.ACQ_CODE_F32)= B.ACQUIRER
                        AND A.SYSTEM_TRACE_F11= B.ORIGTRACE
                        AND A.SETTLEMENT_AMOUNT_F5 =   B.AMOUNT
                        AND A.PRIMARY_ACCOUNT_NUMBER_F2 = TRIM(B.PAN)                        
                        AND TRIM(A.TERM_ID_F41)=TRIM(B.TERMID)         
                        )
                --- Neu tim thay du lieu IBFT Payment (0210) trong bang SHCLOG      
                WHEN MATCHED THEN                    
                    UPDATE SET A.STT = B.STT; --- IBFT Return cung phien chuyenve khong TQT                          
                commit;
                --- Backup du lieu SHCLOG truoc khi update
                BACKUP_DATA_SHCLOG_PRSS_ACH(dt.SETTLEMENT_DATE_YEAR , 'BNB');
                 --- Cap nhat cac giao dich IBFT Payment cung phien sang trang thai khong quyet toan
                 MERGE INTO
                (SELECT TRIM(PAN) AS PAN,
                                AMOUNT,
                                ORIGTRACE,
                                LOCAL_TIME,
                                LOCAL_DATE,
                                SETTLEMENT_DATE,
                                ACQUIRER,
                                TERMID,
                                RESPCODE,
                                PCODE,
                                ACH_EDIT_DATE,
                                ACH_RECONCIL_STATUS,
                                FEE_KEY,
                                FEE_NOTE,
                                FEE_IRF_ISS,
                                FEE_SVF_ISS,
                                FEE_IRF_BEN,
                                FEE_SVF_BEN,
                                ISSUER_FE,
                                SEQUENCE_IN_MONTH 
                             FROM SHCLOG_SETT_IBFT
                             WHERE SETTLEMENT_DATE = dt.SETTLEMENT_DATE_YEAR
                             And BB_BIN = 980471
                             And substr(PCODE,0,2) in ('91','42')
                             And MSGTYPE = 0210
                             And Edit_date is null
                             And ACH_RECONCIL_STATUS is null                
                 ) A
                USING 
                    (SELECT LOCAL_DATE_YEAR,
                            SETTLEMENT_DATE_YEAR,
                            LOCAL_TIME_F12,
                            ACQ_CODE_F32,
                            SYSTEM_TRACE_F11,
                            SETTLEMENT_AMOUNT_F5,
                            PRIMARY_ACCOUNT_NUMBER_F2,
                            TERM_ID_F41
                    FROM ACH_RECONCILIATION
                    WHERE SETTLEMENT_DATE_YEAR= dt.SETTLEMENT_DATE_YEAR
                    And RECONCIL_CASE ='PRSS' --- Payment Return same session 
                    And MTI =0210
                    And ACH_File_role ='BNB'
                    And PROCESS_CODE = 5
                     ) B
                ON  
                    ( A.LOCAL_DATE= B.LOCAL_DATE_YEAR
                        AND A.SETTLEMENT_DATE = B.SETTLEMENT_DATE_YEAR
                        AND A.LOCAL_TIME= B.LOCAL_TIME_F12
                        AND A.ACQUIRER=CONVERT_ACH_INS(B.ACQ_CODE_F32)
                        AND A.ORIGTRACE= B.SYSTEM_TRACE_F11
                        AND A.AMOUNT=B.SETTLEMENT_AMOUNT_F5  
                        AND TRIM(A.PAN) = B.PRIMARY_ACCOUNT_NUMBER_F2
                        AND TRIM(A.TERMID) = TRIM(B.TERM_ID_F41)        
                        )
                --- Neu tim thay du lieu IBFT Payment (0210) trong bang SHCLOG      
                WHEN MATCHED THEN                    
                    UPDATE SET A.Respcode = 5, --- IBFT Return cung phien chuyenve khong TQT                          
                        A.ACH_EDIT_DATE =sysdate,          
                        A.ACH_RECONCIL_STATUS = 'IBFT Online Reconciliation Successful (Same session)',
                        A.FEE_KEY =null,
                        A.FEE_NOTE=null,
                        A.FEE_IRF_ISS=null,
                        A.FEE_SVF_ISS=null,
                        A.FEE_IRF_BEN=null,
                        A.FEE_SVF_BEN=null,                     
                        A.ISSUER_FE =null,
                        A.SEQUENCE_IN_MONTH = null;       
                 
                 commit;  
                 
                Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                Values(sysdate,'0','Round '||iRound ||' - BEGIN Process (ACH Role: '|| dt.ACH_FILE_ROLE||' SETT_DATE: '|| dt.SETTLEMENT_DATE_YEAR ,'UPDATE_ACH_RETURN_SAME_SESSION');
                commit;
            END LOOP;
            
    CLOSE cs;
    iRound:=iRound+1;
    
    
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'0','Round '||iRound ||' - END' ,'UPDATE_ACH_RETURN_SAME_SESSION');
    commit;
    
    
EXCEPTION
   WHEN OTHERS THEN
        emesg := SQLERRM;
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'-1','FILE Round: '||iRound||', Err:'||emesg,'UPDATE_ACH_RETURN_SAME_SESSION');
        Commit;
END;
/
