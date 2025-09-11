CREATE OR REPLACE PROCEDURE RPT.PROCESS_RECONCILE_DATA_ACH_IBFT
(
    iPreviousDate integer
) IS
tmpVar NUMBER;
iPos Integer;
ecode NUMBER;
emesg VARCHAR2(200);
vDetail VARCHAR2(500) := 'Nothing ! ';
iCount integer;
iCompleted      INTEGER := 0;
icheck integer:=0;
/******************************************************************************
   NAME:       PROCESS_DATA_IPSGW_TO_IBFT
   PURPOSE:    

   REVISIONS:
   Ver        Date        Author           Description
   ---------  ----------  ---------------  ------------------------------------
   1.0        06/03/2020   sondt       1. Created this procedure.

   NOTES:

   Automatically available Auto Replace Keywords:
      Object Name:     PROCESS_DATA_IPSGW_TO_IBFT
      Sysdate:         09/15/2022
      Date and Time:   09/15/2022, 3:23:00 PM, and 06/03/2020 3:23:00 PM
      Username:        sondt (set in TOAD Options, Procedure Editor)
      Table Name:       (set in the "New PL/SQL Object" dialog)

******************************************************************************/
BEGIN
   tmpVar := 0;
   iPos:=1;
   Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
   Values(sysdate,'0','BEGIN PROCESS RECONCILE DATA ACH IBFT','PROCESS_RECONCILE_DATA_ACH_IBFT', 0);
   commit;
   WHILE iCompleted < 3
        LOOP
            Select sum(TotalJOB) into iCompleted
            From (
                    select count(*) as TotalJOB
                    from  ACHOFFLINE.his_reconcilation_report_ibft@LINKACHTEST  
                    where sett_date = trunc(sysdate-iPreviousDate)
                    and STATUS ='OK'
                    and rownum =1
                    Union all
                    Select count(*) as TotalJOB
                    from err_ex
                    where Trunc(Err_Time) = Trunc(Sysdate)
                    and err_module ='GET_DATA_IPSGW_TO_IBFT'
                    and Err_detail like 'END PROCESS DATA FROM IPS GATEWAY'
                    and rownum =1
                    union all
                    Select count(*) as TotalJOB
                    from err_ex
                    where Trunc(Err_Time) = Trunc(Sysdate)
                    and err_module ='NP_SYNC_QRIBFT_ECOM'
                    and Err_detail like '%End reconcile QRIBFT ECOM%'
                    and rownum =1
                    );            
            If iCompleted < 3 then
                Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                Values(sysdate,'0','Wait data reconcile ACH IBFT ready','PROCESS_RECONCILE_DATA_ACH_IBFT');
                commit;   
                DBMS_LOCK.sleep(60);
            End if;
            
        END LOOP;   
    iPos:=2;	
    
	Select count(*) into iCheck 
	From err_ex
	where Err_time > trunc(sysdate)
	and Err_module ='PROCESS_RECONCILE_DATA_ACH_IBFT'
	and Err_detail ='Move old reconcile data ACH - IBFT to Backup table';
	If(iCheck=0) then
		Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
		Values(sysdate,'0','Move old reconcile data ACH - IBFT to Backup table','PROCESS_RECONCILE_DATA_ACH_IBFT');
		commit;
		MOVE_DATA_ACH_RECONCIL_BK();
	End if;
	
    Select count(*) into iCheck 
	From err_ex
	where Err_time > trunc(sysdate)
	and Err_module ='PROCESS_RECONCILE_DATA_ACH_IBFT'
	and Err_detail ='Sync data From ACH Database to IBFT Database';
	If(iCheck=0) then
		Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
		Values(sysdate,'0','Sync data From ACH Database to IBFT Database','PROCESS_RECONCILE_DATA_ACH_IBFT');
		commit;
		GET_DATA_ACH_TO_IBFT(to_char(trunc(sysdate-iPreviousDate),'dd/mm/yyyy'));
	End if;
    
	Select count(*) into iCheck 
	From err_ex
	where Err_time > trunc(sysdate)
	and Err_module ='PROCESS_RECONCILE_DATA_ACH_IBFT'
	and Err_detail ='Process remove duplicate transaction';
	If(iCheck=0) then
		Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
		Values(sysdate,'0','Process remove duplicate transaction','PROCESS_RECONCILE_DATA_ACH_IBFT');
		commit;
		PROCESS_ACH_DUPLICATE_TRAN();
	End if;
	
    Select count(*) into iCheck 
	From err_ex
	where Err_time > trunc(sysdate)
	and Err_module ='PROCESS_RECONCILE_DATA_ACH_IBFT'
	and Err_detail ='Process transaction return same session';
	If(iCheck=0) then
		Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
		Values(sysdate,'0','Process transaction return same session','PROCESS_RECONCILE_DATA_ACH_IBFT');
		commit;
		UPDATE_ACH_RETURN_SAME_SESSION('ISS');
	End if;
	
    Select count(*) into iCheck 
	From err_ex
	where Err_time > trunc(sysdate)
	and Err_module ='PROCESS_RECONCILE_DATA_ACH_IBFT'
	and Err_detail ='Process reconcil payment data with ACH roles ISS';
	If(iCheck=0) then
		Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
		Values(sysdate,'0','Process reconcil payment data with ACH roles ISS','PROCESS_RECONCILE_DATA_ACH_IBFT');
		commit;
		UPDATE_ACH_RECONCIL_DATA(to_char(trunc(sysdate-iPreviousDate),'dd/mm/yyyy'), to_char(trunc(sysdate-iPreviousDate),'dd/mm/yyyy'), 'ISS');
	End if;
	
    Select count(*) into iCheck 
	From err_ex
	where Err_time > trunc(sysdate)
	and Err_module ='PROCESS_RECONCILE_DATA_ACH_IBFT'
	and Err_detail ='Process reconcil return data with ACH roles ISS';
	If(iCheck=0) then
		Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
		Values(sysdate,'0','Process reconcil return data with ACH roles ISS','PROCESS_RECONCILE_DATA_ACH_IBFT');
		commit;
		UPDATE_ACH_RECONCIL_RETURN(to_char(trunc(sysdate-iPreviousDate),'dd/mm/yyyy'), to_char(trunc(sysdate-iPreviousDate),'dd/mm/yyyy'), 'ISS');
	End if;
	
    Select count(*) into iCheck 
	From err_ex
	where Err_time > trunc(sysdate)
	and Err_module ='PROCESS_RECONCILE_DATA_ACH_IBFT'
	and Err_detail ='Process reconcil payment data with ACH roles BNB';
	If(iCheck=0) then
		Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
		Values(sysdate,'0','Process reconcil payment data with ACH roles BNB','PROCESS_RECONCILE_DATA_ACH_IBFT');
		commit;
		UPDATE_ACH_RECONCIL_DATA(to_char(trunc(sysdate-iPreviousDate),'dd/mm/yyyy'), to_char(trunc(sysdate-iPreviousDate),'dd/mm/yyyy'), 'BNB');
	End if;
	
    Select count(*) into iCheck 
	From err_ex
	where Err_time > trunc(sysdate)
	and Err_module ='PROCESS_RECONCILE_DATA_ACH_IBFT'
	and Err_detail ='Process reconcil return data with ACH roles BNB';
	If(iCheck=0) then
		Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
		Values(sysdate,'0','Process reconcil return data with ACH roles BNB','PROCESS_RECONCILE_DATA_ACH_IBFT');
		commit;
		UPDATE_ACH_RECONCIL_RETURN(to_char(trunc(sysdate-iPreviousDate),'dd/mm/yyyy'), to_char(trunc(sysdate-iPreviousDate),'dd/mm/yyyy'), 'BNB');
	End if;    
    
	Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
	Values(sysdate,'0','END PROCESS RECONCILE DATA ACH IBFT','PROCESS_RECONCILE_DATA_ACH_IBFT', 0);
	commit;
      
EXCEPTION 
    WHEN OTHERS THEN
    
    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' PROCESS_RECONCILE_DATA_ACH_IBFT Err num: ' || TO_CHAR(ecode) || ' - Err detail: ' || emesg||'iPos:'+ iPos;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,ecode,emesg,'PROCESS_RECONCILE_DATA_ACH_IBFT', 2);    
    commit;
END PROCESS_RECONCILE_DATA_ACH_IBFT;
/
