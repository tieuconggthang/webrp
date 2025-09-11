CREATE OR REPLACE PROCEDURE RPT.PROCESS_DATA_IBFT_20
(
    pProcessDate date
) IS
tmpVar NUMBER;
iPos Integer;
ecode NUMBER;
emesg VARCHAR2(200);
vDetail VARCHAR2(500) := 'Nothing ! ';
iCount integer;
iCompleted      INTEGER := 0;
iUpdate INTEGER:=0;
icheck integer:=0;
/******************************************************************************
   NAME:       PROCESS_DATA_IBFT_20
   PURPOSE:    

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        07/16/2024   sondt       1. Created this procedure.

   NOTES:

   Automatically available Auto Replace Keywords:
      Object Name:     PROCESS_DATA_IBFT_20
      Sysdate:         07/16/2024
      Date and Time:   07/16/2024, 3:23:00 PM, and 06/03/2020 3:23:00 PM
      Username:        sondt (set in TOAD Options, Procedure Editor)
      Table Name:       (set in the "New PL/SQL Object" dialog)

******************************************************************************/
BEGIN
   --- Xu ly them 1 bien icheck phuc vu cho viec chay 1 phan du lieu
   tmpVar := 0;
   iPos:=1;
   Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
   Values(sysdate,'0','BEGIN PROCESS RECONCILE WITH IBFT20 DATA','PROCESS_DATA_IBFT_20', 0);
   commit;
   iPos:=2;  
   Select count(*) into iCheck 
   From err_ex
   where Err_time > trunc(sysdate)
   and Err_module ='PROCESS_DATA_IBFT_20'
   and Err_detail ='Backup data old day to IBFT20_RECONCILIATION_BK table';   
   If(iCheck=0) then
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','Backup data old day to IBFT20_RECONCILIATION_BK table','PROCESS_DATA_IBFT_20', 0);
       commit;
       --- Move du lieu da doi chieu cua ngay hom trc vao bang backup
       Insert into IBFT20_RECONCILIATION_BK(
       PROCESS_DATE,
                F2,
                F3,
                F5,
                F11,
                F12,
                F13,
                F13_YEAR,
                F15,
                F15_YEAR,
                F18,
                F32,
                F41,
                F100,
                F102,
                F103,
                STT,
                REFUND_SAME_SESSION,
                REFUND_AMOUNT,
                RECONCIL_CASE,
                UPDATE_CASE,
                PROCESS_CODE,
                PROCESS_CONTENT,
                RESPONSE_CODE_IBFT,
                RECONCIL_CODE_IBFT20,
                IBFT20_ROLE,
                RECEIVE_DATA_DATE,
                PROCESS_STATE_IBFT,
                EDIT_USER_IBFT,
                EDIT_DATE_IBFT,
                MOVE_DATE,
                SUM_TRANSACTION,
                RECONCIL_EDIT_DATE,
                ORIG_RESPCODE_IBFT,
                F63,
                RECONCIL_CASE_ORIG,
                UPDATE_CASE_ORIG)
       Select PROCESS_DATE,
                F2,
                F3,
                F5,
                F11,
                F12,
                F13,
                F13_YEAR,
                F15,
                F15_YEAR,
                F18,
                F32,
                F41,
                F100,
                F102,
                F103,
                STT,
                REFUND_SAME_SESSION,
                REFUND_AMOUNT,
                RECONCIL_CASE,
                UPDATE_CASE,
                PROCESS_CODE,
                PROCESS_CONTENT,
                RESPONSE_CODE_IBFT,
                RECONCIL_CODE_IBFT20,
                IBFT20_ROLE,
                RECEIVE_DATA_DATE,
                PROCESS_STATE_IBFT,
                EDIT_USER_IBFT,
                EDIT_DATE_IBFT,
                SYSDATE as MOVE_DATE,
                SUM_TRANSACTION,
                RECONCIL_EDIT_DATE,
                ORIG_RESPCODE_IBFT,
                F63,
                RECONCIL_CASE_ORIG,
                UPDATE_CASE_ORIG
               From IBFT20_RECONCILIATION t
               where Process_code is not null
               And RECONCIL_CASE not in('R3','R4');
        commit;
   End If;   
   iPos:=3;
   Select count(*) into iCheck 
   From err_ex
   where Err_time > trunc(sysdate)
   and Err_module ='PROCESS_DATA_IBFT_20'
   and Err_detail ='delete data from IBFT20_RECONCILIATION which reconcil data successful';   
   If(iCheck=0) then
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','delete data from IBFT20_RECONCILIATION which reconcil data successful','PROCESS_DATA_IBFT_20', 0);
       commit;
       --- delete bang du lieu doi chieu phien sau khi move xong de lai case R3 va R4
       Delete from IBFT20_RECONCILIATION
       where Process_code is not null
       And RECONCIL_CASE not in('R3','R4');
       iUpdate:= SQL%ROWCOUNT;
       commit;   
       iPos:=4;
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','Delete '||iUpdate||' rows from IBFT20_RECONCILIATION which reconcil data successful','PROCESS_DATA_IBFT_20', 0);
       commit;
   End if;
   Select count(*) into iCheck 
   From err_ex
   where Err_time > trunc(sysdate)
   and Err_module ='PROCESS_DATA_IBFT_20'
   and Err_detail ='Update data RECONCIL_CASE in (R3,R4)';   
   If(iCheck=0) then
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','Update data RECONCIL_CASE in (R3,R4)','PROCESS_DATA_IBFT_20', 0);
       commit;
       --- Cap nhat cac giao dich case R3 va R4 ve clear de doi chieu lai phien tiep theo
       Update IBFT20_RECONCILIATION
            Set PROCESS_CODE=null,
                PROCESS_CONTENT=null,
                RESPONSE_CODE_IBFT=null,
                RECONCIL_EDIT_DATE=null,
                UPDATE_CASE=null,
                RECONCIL_CASE=null,
                STT =null,
                REFUND_SAME_SESSION=0
       Where RECONCIL_CASE in('R3','R4');
       iUpdate:= SQL%ROWCOUNT;
       commit; 
       iPos:=41;
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','Update '||iUpdate||' rows from IBFT20_RECONCILIATION which RECONCIL_CASE in (R3,R4)','PROCESS_DATA_IBFT_20', 0);
       commit;
   End if;
   Select count(*) into iCheck 
   From err_ex
   where Err_time > trunc(sysdate)
   and Err_module ='PROCESS_DATA_IBFT_20'
   and Err_detail ='Get data from IBFT_20 System';   
   If(iCheck=0) then
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','Get data from IBFT_20 System','PROCESS_DATA_IBFT_20', 0);
       commit;
       --- lay du lieu tu he thong IBFT 2.0 ve bang IBFT20_RECONCILIATION   
       Insert into IBFT20_RECONCILIATION(PROCESS_DATE,F2,F3,F5,F11,F12,F13,F13_YEAR,F15,
                        F15_YEAR,F18,F32,F41,F63,F100,F102,F103,REFUND_SAME_SESSION,REFUND_AMOUNT,
                        IBFT20_ROLE,
                        RECEIVE_DATA_DATE,RECONCIL_CODE_IBFT20)
        Select REFUND_ACCEPT_DATETIME,F2,F3,F5,to_number(F11) as F11,TO_NUMBER(F12) as F12,F13,NP_CONVERT_LOCAL_DATE(F13,sysdate) as F13_YEAR,F15,
        NP_CONVERT_LOCAL_DATE(F15,sysdate) as F15_YEAR,F18,ACQ_ID as F32,F41,F63,BEN_ID as F100,F102,F103,REFUND_SAME_SESSION,REFUND_AMOUNT,
                  Case
                        When system_direction = 'IBFT20_IBFT' Then 'ISS'
                        When system_direction = 'IBFT_IBFT20' Then 'BNB'
                   End as IBFT20_ROLE,
                  sysdate as RECEIVE_DATA_DATE,REFUND_RC             
                from ibftbackend20.V_RR_IBFT_REFUND@LINKACHTEST
                
                    Where REFUND_ACCEPT_DATETIME between pProcessDate and pProcessDate + 1 - 1/86400
                  --  Where trunc(REFUND_ACCEPT_DATETIME) = trunc(pProcessDate)
                    and REFUND_SETTLE_DATETIME = pProcessDate
                    and is_number(F11) > 0;   
    
        commit;
    End if;   
    Select count(*) into iCheck 
    From err_ex
    where Err_time > trunc(sysdate)
    and Err_module ='PROCESS_DATA_IBFT_20'
    and Err_detail ='Truncate table IBFT20_RECONCILIATION_TEMP';   
    If(iCheck=0) then
        iPos:=5;
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
        Values(sysdate,'0','Truncate table IBFT20_RECONCILIATION_TEMP','PROCESS_DATA_IBFT_20', 0);
        commit; 
        EXECUTE IMMEDIATE 'Truncate Table IBFT20_RECONCILIATION_TEMP';
        commit;
    End if;
    Select count(*) into iCheck 
    From err_ex
    where Err_time > trunc(sysdate)
    and Err_module ='PROCESS_DATA_IBFT_20'
    and Err_detail ='Insert same session return data to temp data';   
    If(iCheck=0) then
        iPos:=51;
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
        Values(sysdate,'0','Insert same session return data to temp data','PROCESS_DATA_IBFT_20', 0);
        commit;           
        --- Do du lieu return cung phien sang bang tam de danh dau case du lieu
        --- FULL: Hoan tra toan phan
        --- PARTIAL: Hoan tra 1 phan So tien < So tien GD Goc
        --- PARTIAL_FULL: Hoan tra 1 phan nhieu lan co so tien = so tien GD Goc
        Insert into IBFT20_RECONCILIATION_TEMP(F2,F3,F5,F11,F12,F13,F13_YEAR,F15,F15_YEAR,
                F18,F32,F41,F100,F102,F103,IBFT20_ROLE,REFUND_AMOUNT,SUM_TRANSACTION)
        Select F2,F3,F5,F11,F12,F13,F13_YEAR,F15,F15_YEAR,
                F18,F32,F41,F100,F102,F103,IBFT20_ROLE,sum(REFUND_AMOUNT) As REFUND_AMOUNT,count(*) from IBFT20_RECONCILIATION
        Where REFUND_SAME_SESSION = 1
            And IBFT20_ROLE ='ISS'
        Group by F2,F3,F5,F11,F12,F13,F13_YEAR,F15,F15_YEAR,
                F18,F32,F41,F100,F102,F103,IBFT20_ROLE;
        commit;
        iPos:=6;
        -- Cap nhat phan tach case
        update IBFT20_RECONCILIATION_TEMP
        Set REFUND_TYPE = Case
                    When REFUND_AMOUNT = F5 And SUM_TRANSACTION = 1 then 'FULL'
                    When REFUND_AMOUNT < F5 then 'PARTIAL'
                    When REFUND_AMOUNT = F5 And SUM_TRANSACTION > 1 Then 'PARITAL_FULL'
                    End;
        commit;
    End if;
    Select count(*) into iCheck 
    From err_ex
    where Err_time > trunc(sysdate)
    and Err_module ='PROCESS_DATA_IBFT_20'
    and Err_detail ='Compare return data IBFT20_RECONCILIATION and SHCLOG';   
    If(iCheck=0) then
       iPos:=7;   
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','Compare return data IBFT20_RECONCILIATION and SHCLOG','PROCESS_DATA_IBFT_20', 0);
       commit; 
       
       MERGE INTO IBFT20_RECONCILIATION A
       USING IBFT20_RECONCILIATION_TEMP B
       On ( 
            TRIM(A.F2) = TRIM(B.F2) -- PAN
            And A.F5 = B.F5        -- SETTLEMENT_AMOUNT
            And A.F11 = B.F11 -- TRACE
            And A.F13_YEAR  = B.F13_YEAR -- LOCAL_DATE contains year value
            And A.F12 = B.F12 -- Local_time
            And A.F15_YEAR = B.F15_YEAR -- Settlement date contains year value 
            And A.F32 = B.F32 -- Acquirer/ Issuer
            And TRIM(A.F41) = TRIM(B.F41) -- Term_id
            )      
        WHEN MATCHED THEN
            Update Set A.REFUND_TYPE =B.REFUND_TYPE,
            A.SUM_TRANSACTION = B.SUM_TRANSACTION;
        commit;  
        iPos:=8;
    End if;  
    -- Xu danh dau case hoan tra cung phien 
    -- IBFT20 hoan tra cho IBFT10 (Du lieu goc IBFT1.0: TCPL, IBFT2.0: TCNL)
    -- Cap nhat lai loai hoan tra vao bang du lieu goc case Refund cung phien      
    Select count(*) into iCheck 
    From err_ex
    where Err_time > trunc(sysdate)
    and Err_module ='PROCESS_DATA_IBFT_20'
    and Err_detail ='Compare return data same session IBFT20_RECONCILIATION and SHCLOG';   
    If(iCheck=0) then
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','Compare return data same session IBFT20_RECONCILIATION and SHCLOG','PROCESS_DATA_IBFT_20', 0);
       commit;     
       MERGE INTO (Select * From IBFT20_RECONCILIATION 
                    Where REFUND_SAME_SESSION = 1
                    And IBFT20_ROLE ='ISS'
                  ) A
       USING
            (Select PAN,AMOUNT,ACQUIRER,LOCAL_DATE,SETTLEMENT_DATE,LOCAL_TIME,
                    TERMID,ORIGTRACE,BB_BIN,RESPCODE,STT
             From SHCLOG_SETT_IBFT
             Where BB_BIN = 980478 --- MA ID cua IBFT2.0
             --from shclog
             --where local_date = '2-nov-2024' and BB_BIN = 980478
             And Respcode = 0
            ) B
       On ( 
            TRIM(A.F2) = TRIM(B.PAN) -- PAN
            And A.F5 = B.AMOUNT        -- SETTLEMENT_AMOUNT
            And A.F11 = B.ORIGTRACE -- TRACE
            And A.F13_YEAR  = B.LOCAL_DATE -- LOCAL_DATE contains year value
            And A.F12 = B.LOCAL_TIME -- Local_time
            And A.F15_YEAR = B.SETTLEMENT_DATE -- Settlement date contains year value 
            And A.F32 = B.ACQUIRER -- Acquirer/ Issuer
            And TRIM(A.F41) = TRIM(B.TERMID) -- Term_id
            )      
        WHEN MATCHED THEN
            Update Set
                ---0: Khong canh bao email; 1: canh bao email
                A.PROCESS_CODE = 1,
                A.PROCESS_CONTENT= 'Canh bao email',
                A.RESPONSE_CODE_IBFT = B.RESPCODE,
                A.RECONCIL_EDIT_DATE=sysdate,
                A.UPDATE_CASE =         
                CASE 
                    WHEN REFUND_TYPE = 'FULL' AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 0 THEN '(IBFT2.0 Sender Same session) - IBFT2.0: FULL REFUND and IBFT1.0: RC=0 - Lech dung' --- Case R1
                    WHEN REFUND_TYPE='PARTIAL_FULL' AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 0 THEN '(IBFT2.0 Sender Same session) - IBFT2.0: PARITAL_FULL REFUND and IBFT1.0: RC=0 - Lech dung' --- Case R2
                    WHEN REFUND_TYPE='PARTIAL' AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 0 AND SUM_TRANSACTION = 1 THEN '(IBFT2.0 Sender Same session) - IBFT2.0: PARITAL REFUND ONE TIME and IBFT1.0: RC=0 - Lech dung' --- Case R3
                    WHEN REFUND_TYPE='PARTIAL' AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 0 AND SUM_TRANSACTION > 1 THEN '(IBFT2.0 Sender Same session) - IBFT2.0: PARITAL REFUND MULTI TIME and IBFT1.0: RC=0 - Lech dung' --- Case R4
                    ELSE NULL
                END,
                A.RECONCIL_CASE =         
                CASE 
                    WHEN REFUND_TYPE = 'FULL' AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 0 THEN 'R1' --- Case R1
                    WHEN REFUND_TYPE='PARTIAL_FULL' AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 0 THEN 'R2' --- Case R2
                    WHEN REFUND_TYPE='PARTIAL' AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 0 AND SUM_TRANSACTION = 1 THEN 'R3' --- Case R3
                    WHEN REFUND_TYPE='PARTIAL' AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 0 AND SUM_TRANSACTION > 1 THEN 'R4' --- Case R4
                    ELSE NULL
                END,
                A.UPDATE_CASE_ORIG =         
                CASE 
                    WHEN REFUND_TYPE='PARTIAL' AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 0 AND SUM_TRANSACTION = 1 THEN '(IBFT2.0 Sender Same session) - IBFT2.0: PARITAL REFUND ONE TIME and IBFT1.0: RC=0 - Lech dung' --- Case R3
                    WHEN REFUND_TYPE='PARTIAL' AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 0 AND SUM_TRANSACTION > 1 THEN '(IBFT2.0 Sender Same session) - IBFT2.0: PARITAL REFUND MULTI TIME and IBFT1.0: RC=0 - Lech dung' --- Case R4
                    ELSE NULL
                END,
                A.RECONCIL_CASE_ORIG =         
                CASE 
                    WHEN REFUND_TYPE='PARTIAL' AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 0 AND SUM_TRANSACTION = 1 THEN 'R3' --- Case R3
                    WHEN REFUND_TYPE='PARTIAL' AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 0 AND SUM_TRANSACTION > 1 THEN 'R4' --- Case R4
                    ELSE NULL
                END,
                A.STT=B.STT;           
       commit;
    End if;
    Select count(*) into iCheck 
    From err_ex
    where Err_time > trunc(sysdate)
    and Err_module ='PROCESS_DATA_IBFT_20'
    and Err_detail ='Update Case R6 same session return data';   
    If(iCheck=0) then
       iPos:=9;
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','Update Case R6 same session return data','PROCESS_DATA_IBFT_20', 0);
       commit;
       --- Update cac case con lai ma chua matching duoc voi du lieu SHCLOG_SETT_IBFT sang case R6
        Update IBFT20_RECONCILIATION 
        Set PROCESS_CODE = 1,
            PROCESS_CONTENT= 'Canh bao email',
            RECONCIL_EDIT_DATE=sysdate,
            UPDATE_CASE = 
            Case
                WHEN REFUND_TYPE = 'FULL' AND RECONCIL_CODE_IBFT20 = '00' THEN '(IBFT2.0 Sender Same session) - IBFT2.0: FULL REFUND and IBFT1.0: No Record - Lech sai' 
                WHEN REFUND_TYPE='PARTIAL_FULL' AND RECONCIL_CODE_IBFT20 = '00' THEN '(IBFT2.0 Sender Same session) - IBFT2.0: PARITAL_FULL REFUND and IBFT1.0: No Record - Lech sai' 
                WHEN REFUND_TYPE='PARTIAL' AND RECONCIL_CODE_IBFT20 = '00' AND SUM_TRANSACTION = 1 THEN '(IBFT2.0 Sender Same session) - IBFT2.0: PARITAL REFUND ONE TIME and IBFT1.0: No Record - Lech sai' 
                WHEN REFUND_TYPE='PARTIAL' AND RECONCIL_CODE_IBFT20 = '00' AND SUM_TRANSACTION > 1 THEN '(IBFT2.0 Sender Same session) - IBFT2.0: PARITAL REFUND MULTI TIME and IBFT1.0: No Record - Lech sai' 
            End,
            RECONCIL_CASE =  'R6'
        Where REFUND_SAME_SESSION = 1
        And IBFT20_ROLE ='ISS'
        And PROCESS_CODE is null;
        commit;
    End if;
    Select count(*) into iCheck 
    From err_ex
    where Err_time > trunc(sysdate)
    and Err_module ='PROCESS_DATA_IBFT_20'
    and Err_detail ='Update Case R1 and R2 to SHCLOG_SETT_IBFT (Not settlement)';   
    If(iCheck=0) then
        iPos:=10;
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
        Values(sysdate,'0','Update Case R1 and R2 to SHCLOG_SETT_IBFT (Not settlement)','PROCESS_DATA_IBFT_20', 0);
        commit;
        Update SHCLOG_SETT_IBFT A
        Set A.Respcode = 5
        Where stt in (Select STT From IBFT20_RECONCILIATION
                      Where Reconcil_case in ('R1','R2')
                      )
             And BB_BIN = 980478 --- MA ID cua IBFT2.0
             And Respcode = 0;
       iPos:=11;
       iUpdate:= SQL%ROWCOUNT;
       commit;
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','Update '||iUpdate||' rows Case R1 and R2 to SHCLOG_SETT_IBFT (Not settlement)','PROCESS_DATA_IBFT_20', 0);
       commit;
    End if;    
   --- ket thuc xu ly doi soat giao dich return cung phien

   --- Xu ly doi soat giao d?ch return khac phien chieu IBFT2.0 lam TCPL, IBFT1.0 lam TCNL
   --- Giao dich goc (IBFT1.0 lam TCPL, IBFT2.0 lam TCNL)
    Select count(*) into iCheck 
    From err_ex
    where Err_time > trunc(sysdate)
    and Err_module ='PROCESS_DATA_IBFT_20'
    and Err_detail ='(IBFT2.0 sender) - After session - Compare return data IBFT20_RECONCILIATION and SHCLOG';   
    If(iCheck=0) then
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','(IBFT2.0 sender) - After session - Compare return data IBFT20_RECONCILIATION and SHCLOG','PROCESS_DATA_IBFT_20', 0);
       commit;    
       MERGE INTO (Select * From IBFT20_RECONCILIATION 
                    Where REFUND_SAME_SESSION = 0
                    And IBFT20_ROLE ='ISS'
                  ) A
       USING
            (Select PAN,AMOUNT,ACQUIRER,LOCAL_DATE,SETTLEMENT_DATE,LOCAL_TIME,
                    TERMID,ORIGTRACE,BB_BIN,RESPCODE,STT ,PREAMOUNT,ORIGRESPCODE,ACCOUNT_NO,DEST_ACCOUNT,
                    MERCHANT_TYPE,PCODE,EDIT_DATE,EDIT_USER
             From SHCLOG
             Where trunc(edit_date) between pProcessDate and pProcessDate + 1 - 1/86400
             And BB_BIN = 980478 --- MA ID cua IBFT2.0
             And substr(PCODE,0,2) = '91'
             And MSGTYPE = 0210
             --And Fee_note is not null
             And Edit_date is not null
             And Respcode in (112,113,114,115)
            ) B
       On ( 
            TRIM(A.F2) = TRIM(B.PAN) -- PAN
            And A.F5 = B.AMOUNT        -- SETTLEMENT_AMOUNT
            And A.F11 = B.ORIGTRACE -- TRACE
            And A.F13_YEAR  = B.LOCAL_DATE -- LOCAL_DATE contains year value
            And A.F12 = B.LOCAL_TIME -- Local_time
            And A.F15_YEAR = B.SETTLEMENT_DATE -- Settlement date contains year value 
            And A.F32 = B.ACQUIRER -- Acquirer/ Issuer
            And TRIM(A.F41) = TRIM(B.TERMID) -- Term_id
            And A.REFUND_AMOUNT = DECODE(B.RESPCODE,112,B.PREAMOUNT,114,B.PREAMOUNT,113,B.AMOUNT,115,B.AMOUNT)
            )
        -- Xu ly case du lieu 2 ben deu co va can khop      
        WHEN MATCHED THEN
            Update Set
                --- 0: Khong canh bao email; 1: canh bao email
                A.PROCESS_CODE = 0,                
                A.PROCESS_CONTENT= 'Khong canh bao email',
                A.RESPONSE_CODE_IBFT = B.RESPCODE,
                A.RECONCIL_EDIT_DATE=sysdate,
                A.UPDATE_CASE =         
                CASE 
                    WHEN A.REFUND_AMOUNT = B.PREAMOUNT AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 112 THEN '(IBFT2.0 Sender) - IBFT2.0: PARITAL REFUND and IBFT1.0: RC = 112 - Can khop'
                    WHEN A.REFUND_AMOUNT = B.PREAMOUNT AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 114 THEN '(IBFT2.0 Sender) - IBFT2.0: PARITAL REFUND and IBFT1.0: RC = 114 - Can khop'
                    WHEN A.REFUND_AMOUNT = B.AMOUNT AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 113 THEN '(IBFT2.0 Sender) - IBFT2.0: FULL REFUND and IBFT1.0: RC = 113 - Can khop'
                    WHEN A.REFUND_AMOUNT = B.AMOUNT AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 115 THEN '(IBFT2.0 Sender) - IBFT2.0: FULL REFUND and IBFT1.0: RC = 115 - Can khop'
                    ELSE NULL
                END,
                A.RECONCIL_CASE = 'R7',
                A.STT=B.STT,
                PROCESS_STATE_IBFT='IBFT2.0 Hoan tra',
                EDIT_USER_IBFT=B.EDIT_USER,
                EDIT_DATE_IBFT = B.EDIT_DATE,
                ORIG_RESPCODE_IBFT = B.ORIGRESPCODE
        --- Xu ly case IBFT2.0 khong co, IBFT1.0 co du lieu
        WHEN NOT MATCHED THEN
                insert (F2,F3,F5,F11,F12,F13,F13_YEAR,
                        F15,F15_YEAR,F18,F32,F41,F100,
                        F102,F103,STT,REFUND_SAME_SESSION,REFUND_AMOUNT,
                        RECONCIL_CASE,
                        UPDATE_CASE,
                        PROCESS_CODE,PROCESS_CONTENT,RESPONSE_CODE_IBFT,IBFT20_ROLE,RECEIVE_DATA_DATE,
                        RECONCIL_EDIT_DATE,ORIG_RESPCODE_IBFT,PROCESS_STATE_IBFT,EDIT_USER_IBFT,EDIT_DATE_IBFT)         
                
                values(TRIM(B.PAN),B.PCODE,B.AMOUNT,B.ORIGTRACE,B.LOCAL_TIME,to_char(B.LOCAL_DATE,'mmdd'),B.LOCAL_DATE,
                      to_char(B.SETTLEMENT_DATE,'mmdd'),B.SETTLEMENT_DATE,B.MERCHANT_TYPE,B.ACQUIRER,B.TERMID,B.BB_BIN,
                      B.ACCOUNT_NO,B.DEST_ACCOUNT,B.STT,0,DECODE(B.RESPCODE,112,B.PREAMOUNT,114,B.PREAMOUNT,113,B.AMOUNT,115,B.AMOUNT),
                      'R13',
                      CASE 
                            WHEN B.RESPCODE = 112 THEN '(IBFT2.0 Sender) - IBFT2.0: No Record and IBFT1.0: RC = 112 - Lech sai'
                            WHEN B.RESPCODE = 114 THEN '(IBFT2.0 Sender) - IBFT2.0: No Record and IBFT1.0: RC = 114 - Lech sai'
                            WHEN B.RESPCODE = 113 THEN '(IBFT2.0 Sender) - IBFT2.0: No Record and IBFT1.0: RC = 113 - Lech sai'
                            WHEN B.RESPCODE = 115 THEN '(IBFT2.0 Sender) - IBFT2.0: No Record and IBFT1.0: RC = 115 - Lech sai'
                            ELSE NULL
                      END,
                      1,'Canh bao email',B.RESPCODE,'ISS',sysdate,
                      sysdate,B.ORIGRESPCODE,
                      'IBFT1.0 Hoan tra',
                      B.EDIT_USER,
                      B.EDIT_DATE
                );           
       commit;
    End if;
    Select count(*) into iCheck 
    From err_ex
    where Err_time > trunc(sysdate)
    and Err_module ='PROCESS_DATA_IBFT_20'
    and Err_detail ='(IBFT2.0 sender) - After session - Update value case R11 to IBFT20_RECONCILATION';   
    If(iCheck=0) then
       iPos:=12;
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','(IBFT2.0 sender) - After session - Update value case R11 to IBFT20_RECONCILATION','PROCESS_DATA_IBFT_20', 0);
       commit;
       --- Xu ly cap nhat case IBFT2.0 co và IBFT1.0 khong co
       Update IBFT20_RECONCILIATION
       Set Process_code =1,
           Process_content ='Canh bao email',
           RECONCIL_CASE ='R11' ,
           UPDATE_CASE =
                    CASE 
                            WHEN REFUND_AMOUNT < F5 THEN '(IBFT2.0 Sender) - IBFT2.0: PARTIAL and IBFT1.0: No Record - Lech dung'
                            WHEN REFUND_AMOUNT = F5 THEN '(IBFT2.0 Sender) - IBFT2.0: FULL and IBFT1.0: No Record - Lech dung'
                            ELSE NULL
                      END
       where REFUND_SAME_SESSION =0 
            And Process_code is null
            And IBFT20_ROLE ='ISS';
       iUpdate:= SQL%ROWCOUNT;
       commit;
       iPos:=13;
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','Update '||iUpdate||' rows Case R11','PROCESS_DATA_IBFT_20', 0);
       commit;
    End if;   
   --- Ket thuc xu ly doi soat chieu IBFT2.0 lam TCPL, IBFT1.0 lam TCNL khac phien
   
   --- Xu ly doi soat giao d?ch return khac phien chieu IBFT1.0 lam TCPL, IBFT2.0 lam TCNL
   --- Giao dich goc (IBFT2.0 lam TCPL, IBFT1.0 lam TCNL)
    Select count(*) into iCheck 
    From err_ex
    where Err_time > trunc(sysdate)
    and Err_module ='PROCESS_DATA_IBFT_20'
    and Err_detail ='(IBFT2.0 Receiver) - After session - Compare return data IBFT20_RECONCILIATION and SHCLOG';   
    If(iCheck=0) then
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','(IBFT2.0 Receiver) - After session - Compare return data IBFT20_RECONCILIATION and SHCLOG','PROCESS_DATA_IBFT_20', 0);
       commit;    
       MERGE INTO (Select * From IBFT20_RECONCILIATION 
                    Where REFUND_SAME_SESSION = 0
                    And IBFT20_ROLE ='BNB'
                  ) A
       USING
            (Select PAN,AMOUNT,ACQUIRER,LOCAL_DATE,SETTLEMENT_DATE,LOCAL_TIME,
                    TERMID,ORIGTRACE,BB_BIN,RESPCODE,STT ,PREAMOUNT,ORIGRESPCODE,ACCOUNT_NO,DEST_ACCOUNT,
                    MERCHANT_TYPE,PCODE,EDIT_DATE,EDIT_USER
             From SHCLOG
             Where trunc(edit_date) between pProcessDate and pProcessDate + 1 - 1/86400
             And ISSUER_RP = 980478 --- MA ID cua IBFT2.0
             And substr(PCODE,0,2) = '91'
             And MSGTYPE = 0210
             And Fee_note is not null
             And Edit_date is not null
             And Respcode in (112,113,114,115)
            ) B
       On ( 
            TRIM(A.F2) = TRIM(B.PAN) -- PAN
            And A.F5 = B.AMOUNT        -- SETTLEMENT_AMOUNT
            And A.F11 = B.ORIGTRACE -- TRACE
            And A.F13_YEAR  = B.LOCAL_DATE -- LOCAL_DATE contains year value
            And A.F12 = B.LOCAL_TIME -- Local_time
            And A.F15_YEAR = B.SETTLEMENT_DATE -- Settlement date contains year value 
            And A.F32 = B.ACQUIRER -- Acquirer/ Issuer
            And TRIM(A.F41) = TRIM(B.TERMID) -- Term_id
            And A.REFUND_AMOUNT = DECODE(B.RESPCODE,112,B.PREAMOUNT,114,B.PREAMOUNT,113,B.AMOUNT,115,B.AMOUNT)
            )
        -- Xu ly case du lieu 2 ben deu co va can khop      
        WHEN MATCHED THEN
            Update Set
                --- 0: Khong canh bao email; 1: canh bao email
                A.PROCESS_CODE = 0,                
                A.PROCESS_CONTENT= 'Khong canh bao email',
                A.RESPONSE_CODE_IBFT = B.RESPCODE,
                A.RECONCIL_EDIT_DATE=sysdate,
                A.UPDATE_CASE =         
                CASE 
                    WHEN A.REFUND_AMOUNT = B.PREAMOUNT AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 112 THEN '(IBFT2.0 Receiver) - IBFT2.0: PARITAL REFUND and IBFT1.0: RC = 112 - Can khop'
                    WHEN A.REFUND_AMOUNT = B.PREAMOUNT AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 114 THEN '(IBFT2.0 Receiver) - IBFT2.0: PARITAL REFUND and IBFT1.0: RC = 114 - Can khop'
                    WHEN A.REFUND_AMOUNT = B.AMOUNT AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 113 THEN '(IBFT2.0 Receiver) - IBFT2.0: FULL REFUND and IBFT1.0: RC = 113 - Can khop'
                    WHEN A.REFUND_AMOUNT = B.AMOUNT AND A.RECONCIL_CODE_IBFT20 = '00' AND B.RESPCODE = 115 THEN '(IBFT2.0 Receiver) - IBFT2.0: FULL REFUND and IBFT1.0: RC = 115 - Can khop'
                    ELSE NULL
                END,
                A.RECONCIL_CASE = 'R15',
                A.STT=B.STT,
                PROCESS_STATE_IBFT='IBFT2.0 Hoan tra',
                EDIT_USER_IBFT=B.EDIT_USER,
                EDIT_DATE_IBFT = B.EDIT_DATE,
                ORIG_RESPCODE_IBFT = B.ORIGRESPCODE
        --- Xu ly case IBFT2.0 khong co, IBFT1.0 co du lieu
        WHEN NOT MATCHED THEN
                insert (F2,F3,F5,F11,F12,F13,F13_YEAR,
                        F15,F15_YEAR,F18,F32,F41,F100,
                        F102,F103,STT,REFUND_SAME_SESSION,REFUND_AMOUNT,
                        RECONCIL_CASE,
                        UPDATE_CASE,
                        PROCESS_CODE,PROCESS_CONTENT,RESPONSE_CODE_IBFT,IBFT20_ROLE,RECEIVE_DATA_DATE,
                        RECONCIL_EDIT_DATE,ORIG_RESPCODE_IBFT,PROCESS_STATE_IBFT,EDIT_USER_IBFT,EDIT_DATE_IBFT)         
                
                values(TRIM(B.PAN),B.PCODE,B.AMOUNT,B.ORIGTRACE,B.LOCAL_TIME,to_char(B.LOCAL_DATE,'mmdd'),B.LOCAL_DATE,
                      to_char(B.SETTLEMENT_DATE,'mmdd'),B.SETTLEMENT_DATE,B.MERCHANT_TYPE,B.ACQUIRER,B.TERMID,B.BB_BIN,
                      B.ACCOUNT_NO,B.DEST_ACCOUNT,B.STT,0,DECODE(B.RESPCODE,112,B.PREAMOUNT,114,B.PREAMOUNT,113,B.AMOUNT,115,B.AMOUNT),
                      'R19',
                      CASE 
                            WHEN B.RESPCODE = 112 THEN '(IBFT2.0 Receiver) - IBFT2.0: No Record and IBFT1.0: RC = 112 - Lech dung'
                            WHEN B.RESPCODE = 114 THEN '(IBFT2.0 Receiver) - IBFT2.0: No Record and IBFT1.0: RC = 114 - Lech dung'
                            WHEN B.RESPCODE = 113 THEN '(IBFT2.0 Receiver) - IBFT2.0: No Record and IBFT1.0: RC = 113 - Lech dung'
                            WHEN B.RESPCODE = 115 THEN '(IBFT2.0 Receiver) - IBFT2.0: No Record and IBFT1.0: RC = 115 - Lech dung'
                            ELSE NULL
                      END,
                      1,'Canh bao email',B.RESPCODE,'BNB',sysdate,
                      sysdate,B.ORIGRESPCODE,
                      'IBFT1.0 Hoan tra',
                      B.EDIT_USER,
                      B.EDIT_DATE
                );           
       commit;
    End if;
    Select count(*) into iCheck 
    From err_ex
    where Err_time > trunc(sysdate)
    and Err_module ='PROCESS_DATA_IBFT_20'
    and Err_detail ='(IBFT2.0 Receiver) - After session - Update value case R16 to IBFT20_RECONCILATION';   
    If(iCheck=0) then
       iPos:=14;
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','(IBFT2.0 Receiver) - After session - Update value case R16 to IBFT20_RECONCILATION','PROCESS_DATA_IBFT_20', 0);
       commit;
       --- Xu ly cap nhat case IBFT2.0 co và IBFT1.0 khong co
       Update IBFT20_RECONCILIATION
       Set Process_code =1,
           Process_content ='Canh bao email',
           RECONCIL_CASE ='R16' ,
           UPDATE_CASE =
                    CASE 
                            WHEN REFUND_AMOUNT < F5 THEN '(IBFT2.0 Receiver) - IBFT2.0: PARTIAL and IBFT1.0: No Record - Lech sai'
                            WHEN REFUND_AMOUNT = F5 THEN '(IBFT2.0 Receiver) - IBFT2.0: FULL and IBFT1.0: No Record - Lech sai'
                            ELSE NULL
                      END
       where REFUND_SAME_SESSION =0 
            And Process_code is null
            And IBFT20_ROLE ='BNB';
       iUpdate:= SQL%ROWCOUNT;
       commit;
       iPos:=15;
       Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
       Values(sysdate,'0','Update '||iUpdate||' rows Case R16','PROCESS_DATA_IBFT_20', 0);
       commit;
    End if;   
   --- Ket thuc xu ly doi soat IBFT1.0 hoan tra sang IBFT 2.0 khac phien
   Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
   Values(sysdate,'0','END PROCESS RECONCILE WITH IBFT20 DATA','PROCESS_DATA_IBFT_20', 0);
   commit;
   iPos:=16;
   Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
   Values(sysdate,'0','RECONCIL IBFT20 DONE','PROCESS_DATA_IBFT_20', 0);
   commit;  

EXCEPTION 
    WHEN OTHERS THEN    
    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' PROCESS_DATA_IBFT_20 Err num: ' || TO_CHAR(ecode) || ' - Err detail: ' || emesg||'iPos:'+ iPos;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,ecode,emesg,'PROCESS_DATA_IBFT_20', 2);    
    commit;
END PROCESS_DATA_IBFT_20;
/
