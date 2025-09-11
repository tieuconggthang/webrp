CREATE OR REPLACE PROCEDURE RPT.CHECK_TC_NULL
AS

    -- Writen by    :   Hoind  
    -- Date create  :   2016-06-29
    -- Description  :   Kiem tra giao dich co hai thong diep chuyen tien kenh Internet Banking
    
    num INTEGER := 0 ;  

    ecode NUMBER;
    emesg VARCHAR2(200);
    vMType  VARCHAR2(30) := 'text/plain; charset=us-ascii';
     vSender VARCHAR2(40) := 'baocaotest@napas.com.vn';
    vReceiver VARCHAR2(30) := 'cmt_doisoat@napas.com.vn';
    vCC VARCHAR2(120) := 'cmt_doisoat@napas.com.vn';
    bCC VARCHAR2(120) := '';
    vSub VARCHAR2(150):= 'Check khong xac dinh thu huong' ;
    vDetail VARCHAR2(500) := 'Nothing ! ';
    iDdl INTEGER := 0;
    iCompleted      INTEGER := 0;  
Begin    
           
    Insert Into ibft_double(PAN, TRACE, LOCAL_TIME, LOCAL_DATE, TERMID, RUNTIME, MODULE)
    Select PAN, ORIGTRACE AS TRACE, LOCAL_TIME, LOCAL_DATE, TERMID, Sysdate, 'TC_NULL'
    From Shclog_sett_ibft
    Where msgtype = 210
    And BB_BIN Is Null    
    and respcode = 0   
    ;
        
    num := SQL%ROWCOUNT;       
        
    IF num <> 0 THEN
        vDetail := ' Co '||num||' giao dich IBFT khong xac dinh chieu CK, vui long kt';
            
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'0',vDetail,'TC_NULL');
        commit;
            
        SEND_SMS('CHECK_TC_NULL#0983411005;0936535868;0988766330#'||vDetail);
            
    Else
        vDetail := ' Khong co giao dich IBFT khong xac dinh chieu CK';
            
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'0','OK','TC_NULL');
        commit;
    END IF;
      
    --Utl_Mail.send(vSender,vReceiver,vCC,bCC,vSub,vDetail,vMType,NULL); --  bo gui  mail 05/08

EXCEPTION 
    WHEN OTHERS THEN
    
    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' CHECK_TC_NULL Err num: ' || TO_CHAR(ecode) || ' - Err detail: ' || emesg;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,ecode,emesg,'TC_NULL');
    
    commit;
    
End;
/
