CREATE OR REPLACE PROCEDURE RPT.PROCESS_ACH_DUPLICATE_TRAN
AS

    -- Writen by    :   Hoind  
    -- Date create  :   2016-06-29
    -- Description  :   Kiem tra giao dich co hai thong diep chuyen tien kenh Internet Banking
    
    num INTEGER := 0 ;  
    iNumEnd INTEGER := 0 ; 
    iUpdate Integer;
    istt Integer;
    
    ecode NUMBER;
    emesg VARCHAR2(200);
    vMType  VARCHAR2(30) := 'text/plain; charset=us-ascii';
    vSender VARCHAR2(40) := 'db_baocao@napas.com.vn';
    vReceiver VARCHAR2(30) := 'hoind@napas.com.vn';
    vCC VARCHAR2(120) := 'doantien@napas.com.vn;nhungdt@napas.com.vn;trangdt@napas.com.vn;trangnvh@napas.com.vn;trungnv@napas.com.vn';
    bCC VARCHAR2(120) := 'khaihq@napas.com.vn;minhnn@napas.com.vn;thuyntt@napas.com.vn;hatt@napas.com.vn';
    vSub VARCHAR2(150):= 'Check backend double trans' ;
    vDetail VARCHAR2(500) := 'Nothing ! ';
    
     
    
Begin
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'0','START' ,'PROCESS_ACH_DUPLICATE_TRAN');

    --- Delete du lieu IBFT Payment trung thong tin
    Delete From ACH_RECONCILIATION
    Where MTI = 0210 And rowid not in 
        (
            Select max(rowid) from ACH_RECONCILIATION
                Where MTI = 0210
                Group by PRIMARY_ACCOUNT_NUMBER_F2,SETTLEMENT_AMOUNT_F5,
                         SYSTEM_TRACE_F11,LOCAL_TIME_F12,LOCAL_DATE_F13,
                         TERM_ID_F41,ACQ_CODE_F32,RECONCILIATION_CODE
    --            Having Count(*) > 1
        );
    num := SQL%ROWCOUNT;
    commit;
    if(num > 0) then
        vDetail := 'Tong so giao dich IBFT Payment xoa: ' || num;        
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'0',vDetail,'PROCESS_ACH_DUPLICATE_TRAN');
    Else
        vDetail := 'Khong co giao dich IBFT Payment duplicate';        
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'0',vDetail,'PROCESS_ACH_DUPLICATE_TRAN');
    End if;
    commit;
    --- Insert Duplicate IBFT Return
     
     --- Delete du lieu IBFT Return trung thong tin
    Delete From ACH_RECONCILIATION
    Where MTI = 0310 And rowid not in 
    (
        select max(rowid) from ACH_RECONCILIATION        
        Where MTI = 0310
        Group by PRIMARY_ACCOUNT_NUMBER_F2,SETTLEMENT_AMOUNT_F5,
                 SYSTEM_TRACE_F11,LOCAL_TIME_F12,LOCAL_DATE_F13,
                 TERM_ID_F41,ACQ_CODE_F32,RECONCILIATION_CODE,
                 RESERVE_INFO_3,REFUND_AMOUNT
--        Having Count(*) > 1
    );
     num := SQL%ROWCOUNT;
     commit;
    if(num > 0) then
        vDetail := 'Tong so giao dich IBFT Return duplicate xoa: ' || num;           
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'0',vDetail,'PROCESS_ACH_DUPLICATE_TRAN');
    Else
        vDetail := 'Khong co giao dich IBFT Return duplicate';         
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'0',vDetail,'PROCESS_ACH_DUPLICATE_TRAN');
    End if;
    commit;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'0','END' ,'PROCESS_ACH_DUPLICATE_TRAN');
    commit;
EXCEPTION 
    WHEN OTHERS THEN
    
    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' CHECK_ACH_DOUBLE Err num: ' || TO_CHAR(ecode) || ' - Err detail: ' || emesg;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,ecode,emesg,'PROCESS_ACH_DUPLICATE_TRAN');
    
    commit;
    
End;
/
