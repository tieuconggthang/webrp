CREATE OR REPLACE PROCEDURE RPT.CHECK_SPEC_CHAR_IBFT
AS

    -- Writen by    :   Hoind  
    -- Date create  :   2016-06-29
    -- Description  :   Kiem tra giao dich co hai thong diep chuyen tien kenh Internet Banking
    
    num INTEGER := 0 ;  

    ecode NUMBER;
    emesg VARCHAR2(200);
    vMType  VARCHAR2(30) := 'text/plain; charset=us-ascii';
    vSender VARCHAR2(40) := 'baocaotest@napas.com.vn';
    vReceiver VARCHAR2(30) := 'hoind@napas.com.vn';
    vCC VARCHAR2(120) := 'cmt_doisoat@napas.com.vn';
    bCC VARCHAR2(120) := '';
    vSub VARCHAR2(150):= 'Check backend special character' ;
    vDetail VARCHAR2(500) := 'Nothing ! ';
	iDdl INTEGER := 0;
    iCompleted      INTEGER := 0;  
Begin
    
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'1','BEGIN','CHECK_SPEC_CHAR_IBFT');
    commit;
    
    Insert Into ibft_double(TRACE, LOCAL_TIME, LOCAL_DATE, TERMID, RUNTIME, MODULE,STT)
    Select ORIGTRACE AS TRACE, LOCAL_TIME, LOCAL_DATE, TERMID, Sysdate, 'CHECK_SPEC_CHAR_IBFT',STT
    From Shclog_sett_ibft
    Where msgtype = 210
    And 
    (
        acctnum like '%'||chr(9)||'%'
        Or
        acctnum like '%'||chr(10)||'%'
        Or
        acctnum like '%'||chr(13)||'%'
        Or
        pan like '%'||chr(9)||'%'
        Or
        pan like '%'||chr(10)||'%'
        Or
        pan like '%'||chr(13)||'%'
        Or
        Content_fund like '%'||chr(39)||'%'
        Or
        termloc like '%'||chr(9)||'%'
        Or
        content_fund like '%%'
        Or
        content_fund like '%?%'
        Or
        (Content_fund like '%'||chr(91)||'%' And BB_BIN = 970430)
        Or
        (Content_fund like '%'||chr(93)||'%' And BB_BIN = 970430)
    );
		
    num := SQL%ROWCOUNT;		
		
    IF num <> 0 THEN
        vDetail := ' Co '||num||' giao dich co ky tu dac biet, vui long kiem tra';
			
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'1',vDetail,'CHECK_SPEC_CHAR_IBFT');
        commit;
			
        Update Shclog_sett_ibft
        set acctnum = replace(acctnum,chr(9),' ')
        Where msgtype = 210
        And 
        (
            acctnum like '%'||chr(9)||'%'
            Or
            pan like '%'||chr(9)||'%'

        )
        And STT in (Select STT From IBFT_DOUBLE
                        where runtime > trunc(sysdate)
                        and module ='CHECK_SPEC_CHAR_IBFT'
                        );
        commit;
        Update Shclog_sett_ibft
        set acctnum = replace(acctnum,chr(10),' ')
        Where msgtype = 210
        And 
        (
            acctnum like '%'||chr(10)||'%'
            Or
            pan like '%'||chr(10)||'%'
        )
        And STT in (Select STT From IBFT_DOUBLE
                        where runtime > trunc(sysdate)
                        and module ='CHECK_SPEC_CHAR_IBFT'
                        );
        commit;
        Update Shclog_sett_ibft
        set acctnum = replace(acctnum,chr(13),' ')
        Where msgtype = 210
        And 
        (
            acctnum like '%'||chr(13)||'%'
            Or
            pan like '%'||chr(13)||'%'
        )
        And STT in (Select STT From IBFT_DOUBLE
                        where runtime > trunc(sysdate)
                        and module ='CHECK_SPEC_CHAR_IBFT'
                        );
        commit;
        Update Shclog_sett_ibft
        set Content_fund = replace(Content_fund,chr(39),' ')
        Where msgtype = 210
        And Content_fund like '%'||chr(39)||'%'
        And STT in (Select STT From IBFT_DOUBLE
                        where runtime > trunc(sysdate)
                        and module ='CHECK_SPEC_CHAR_IBFT'
                        );
        commit;
        Update Shclog_sett_ibft
        set termloc = replace(termloc,chr(9),' ')
        Where msgtype = 210
        And termloc like '%'||chr(9)||'%'
        And STT in (Select STT From IBFT_DOUBLE
                        where runtime > trunc(sysdate)
                        and module ='CHECK_SPEC_CHAR_IBFT'
                        );
        commit;
        Update Shclog_sett_ibft
        set Content_fund = replace(Content_fund,'?',' ')
        Where msgtype = 210
        And content_fund like '%?%'
        And STT in (Select STT From IBFT_DOUBLE
                        where runtime > trunc(sysdate)
                        and module ='CHECK_SPEC_CHAR_IBFT'
                        );
        commit;
        Update Shclog_sett_ibft
        set Content_fund = replace(Content_fund,'',' ')
        Where msgtype = 210
        And content_fund like '%%'
        And STT in (Select STT From IBFT_DOUBLE
                        where runtime > trunc(sysdate)
                        and module ='CHECK_SPEC_CHAR_IBFT'
                        );
        commit;
        Update Shclog_sett_ibft
        set Content_fund = replace(Content_fund,chr(91),' ')
        Where msgtype = 210
        And Content_fund like '%'||chr(91)||'%'
        And BB_BIN = 970430
        And STT in (Select STT From IBFT_DOUBLE
                        where runtime > trunc(sysdate)
                        and module ='CHECK_SPEC_CHAR_IBFT'
                        );
        commit;
        Update Shclog_sett_ibft
        set Content_fund = replace(Content_fund,chr(93),' ')
        Where msgtype = 210
        And Content_fund like '%'||chr(93)||'%'
        And BB_BIN = 970430
        And STT in (Select STT From IBFT_DOUBLE
                        where runtime > trunc(sysdate)
                        and module ='CHECK_SPEC_CHAR_IBFT'
                        );
        commit;
        Select count(*) into iCompleted
        From err_ex
        where err_time > trunc(sysdate)
        and err_module ='CHECK_SPEC_CHAR_IST'
        and ERR_CODE =0;
        If(iCompleted >=0) Then
            Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
            Values(sysdate,'0',vDetail,'CHECK_SPEC_CHAR');
            commit;
        Else
            Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
            Values(sysdate,'0',vDetail,'CHECK_SPEC_CHAR_IBFT');
            commit;
        End If;   
    Else
        vDetail := ' Khong co giao dich IBFT co ky tu dac biet tren backend';
        Select count(*) into iCompleted
        From err_ex
        where err_time > trunc(sysdate)
        and err_module ='CHECK_SPEC_CHAR_IST'
        and ERR_CODE =0;
        If(iCompleted>0) Then
            Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
            Values(sysdate,'0',vDetail,'CHECK_SPEC_CHAR');
            commit;
        Else
            Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
            Values(sysdate,'0',vDetail,'CHECK_SPEC_CHAR_IBFT');
            commit;
        End If;
    END IF;
	  
    --Utl_Mail.send(vSender,vReceiver,vCC,bCC,vSub,vDetail,vMType,NULL); --  bo gui  mail 05/08

EXCEPTION 
    WHEN OTHERS THEN
    
    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' CHECK_SPEC_CHAR Err num: ' || TO_CHAR(ecode) || ' - Err detail: ' || emesg;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,ecode,emesg,'CHECK_SPEC_CHAR');
    
    commit;
    
End;
/
