Declare
    iCount integer;    
Begin
    iCount:=0;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,'BEGIN','Start','PROCESS_68_TIMEOUT', 0);
    commit;
    Insert /*+ parallel(6) */ Into ISOMESSAGE_TMP_68_TO 
    Select * From ISOMESSAGE_TMP_TURN
    Where RESPONSE_CODE = 68
    And mti||card_no||trace_no||local_time||local_date||acq_id||term_id in 
    (Select mti||card_no||trace_no||local_time||local_date||acq_id||term_id 
    From ISOMESSAGE_TMP_TURN 
    Where RESPONSE_CODE not in (68,0)
    );  
    iCount:=SQL%rowcount; 
    commit;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,'0','Move to ISOMESSAGE_TMP_68_TO '||iCount||' rows','PROCESS_68_TIMEOUT', 0);
    commit;
    If(iCount >0) Then
        Delete /*+ parallel(6) */ From ISOMESSAGE_TMP_TURN
        Where RESPONSE_CODE = 68
        And mti||card_no||trace_no||local_time||local_date||acq_id||term_id in 
        (Select mti||card_no||trace_no||local_time||local_date||acq_id||term_id 
        From ISOMESSAGE_TMP_TURN 
        Where RESPONSE_CODE not in (68,0)
        );
        iCount:=SQL%rowcount; 
        commit;
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
        Values(sysdate,'0','Delete From ISOMESSAGE_TMP_TURN '||iCount||' rows','PROCESS_68_TIMEOUT', 0);
        commit;
        Insert Into ISOMESSAGE_TMP_68_TO_FULL 
               Select t.*,NP_CONVERT_LOCAL_DATE(SETTLE_DATE,sysdate) as F15_YEAR,
               NP_CONVERT_LOCAL_DATE(LOCAL_DATE,sysdate) as LOCAL_DATE_YEAR,
               SYSDATE as INSERT_DATE                
               From ISOMESSAGE_TMP_68_TO t;
        Commit;
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
        Values(sysdate,'ALERT','Co '||iCount||' Giao dich RC =68 co ma tuong minh duoc xu ly ||SETTLEMENT_DATE ='|| trunc(sysdate-1),'PROCESS_68_TIMEOUT', 0);
        commit;
    Else
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
        Values(sysdate,'ALERT','Khong Giao dich RC =68 co ma tuong minh||SETTLEMENT_DATE ='|| trunc(sysdate-1),'PROCESS_68_TIMEOUT', 0);
        commit;
    End If;    
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,'END','Finish','PROCESS_68_TIMEOUT', 0);
    commit;
End;