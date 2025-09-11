CREATE OR REPLACE PROCEDURE RPT.GATHER_TABLE_FILL_DATA_DAILY (
 p_ownname IN VARCHAR2,
 p_tabname IN VARCHAR2
) IS
    ecode NUMBER;
    emesg VARCHAR2(200);
    vDetail VARCHAR2(500) := 'Nothing ! ';
    v_start_time TIMESTAMP;
    v_end_time TIMESTAMP;
    v_elapsed_seconds NUMBER;
    v_elapsed_text VARCHAR2(100);
BEGIN
    
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'0','START GATHER TABLE: '|| p_tabname,'GATHER_TABLE_FILL_DATA_DAILY');
    commit;
    v_start_time := SYSTIMESTAMP;
    DBMS_STATS.GATHER_TABLE_STATS(ownname => UPPER(p_ownname),tabname => UPPER(p_tabname),degree => 4);
    v_end_time := SYSTIMESTAMP;
    v_elapsed_seconds := EXTRACT(SECOND FROM (v_end_time - v_start_time)) 
     + EXTRACT(MINUTE FROM (v_end_time - v_start_time)) * 60 
     + EXTRACT(HOUR FROM (v_end_time - v_start_time)) * 3600;

    v_elapsed_text := 'END GATHER TABLE: ' || p_tabname || ' - Duration: ' ||
     TRUNC(v_elapsed_seconds / 60) || ' min ' ||
     MOD(v_elapsed_seconds, 60) || ' sec';

    INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
    VALUES (SYSDATE, '0', v_elapsed_text, 'GATHER_TABLE_FILL_DATA_DAILY');
    COMMIT;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'0','FINISH GATHER TABLE: '|| p_tabname,'GATHER_TABLE_FILL_DATA_DAILY');
    commit;
EXCEPTION
    WHEN OTHERS THEN    
    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' GATHER_TABLE_FILL_DATA_DAILY Err num: ' || TO_CHAR(ecode) || ' - Err detail: ' || emesg;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE,CRITICAL)
    Values(sysdate,ecode,emesg,'GATHER_TABLE_FILL_DATA_DAILY',2);
    
    commit;
END;
/
