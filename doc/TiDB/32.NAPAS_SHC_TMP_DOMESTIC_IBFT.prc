CREATE OR REPLACE PROCEDURE RPT.NAPAS_SHC_TMP_DOMESTIC_IBFT
    (
        pQRY_FROM_DATE VARCHAR2,
        pQRY_TO_DATE VARCHAR2,
        pCreated_User VARCHAR2,
        pSett_Code integer
    )
AS
/* ---------------------- Do du lieu dieu chinh IBFT noi dia vao bang SHCLOG_SETT_IBFT_ADJUST ------------------------
    Author          : sondt
    Date created    : 09/08/2024
----------------------------------------------------------------------------------------------*/        
    df date;
    dt date;
    d1 DATE;
    d2 DATE;
    day1 DATE;
    day2 DATE;
    thue NUMBER(5,3);

    vMType  VARCHAR2(30) := 'text/plain; charset=us-ascii';
    vSender VARCHAR2(40) := 'db_baocao@napas.com.vn';
    vReceiver VARCHAR2(60) := 'hoind@napas.com.vn';
    vCC VARCHAR2(30) := 'hoind@napas.com.vn';
    bCC VARCHAR2(30) := 'hoind@napas.com.vn';
    vSub VARCHAR2(150):= 'Do du lieu sang bang tam(New)' ;
    vDetail VARCHAR2(500) := 'Nothing ! ';
    ecode NUMBER;
    emesg VARCHAR2(200);
    dt_start DATE;
    dt_end DATE;
    v_begin TIMESTAMP(9);
    v_end TIMESTAMP(9);
    v_interval INTERVAL DAY TO SECOND;
    rl NVARCHAR2(40);
    iCup integer;
    vNapasDate  Date := trunc(sysdate-4);
    vlistsms varchar2(100) :='0983411005';
    
    O_RATE           NUMBER(28,15);
   dLastSett         Date;
   iCompleted      INTEGER := 0;
BEGIN
    WHILE iCompleted < 10
        LOOP
            Select sum(TotalJOB) into iCompleted-- 10 Job
            From (
                select Count(*) As TotalJOB from NP_EXEC_LOG
                where trunc(exec_date) = Trunc(Sysdate)
                And STT = '100100400200'
                And EX_ERR =0
                and rownum =1 
                Union all
                select  Count(*) aS TotalJOB from NP_EXEC_LOG
                where trunc(exec_date) = Trunc(Sysdate)
                And STT ='100100400255'
                And EX_ERR =0 
                and rownum =1   
                Union all
                Select Count(*) As TotalJOB
                From err_ex
                Where Trunc(Err_Time) = Trunc(Sysdate)
                And ERR_MODULE = 'MATCH_FILE'
                And ERR_CODE = '0'
                And ERR_DETAIL = 'Successfully'
                and rownum =1 
                Union all
                Select Count(*) As TotalJOB From Err_ex
                where err_time > trunc(sysdate)
                and err_module ='GET_DATA_IPSGW_TO_IBFT'
                and err_detail ='END PROCESS DATA FROM IPS GATEWAY'
                and Critical = 0
                and rownum =1 
                Union all
                Select Count(*) As TotalJOB
                from err_ex
                where err_time > trunc(sysdate)
                and err_module ='ACH_READ_FILE'
                and err_detail ='READ FILE DONE'
                and Err_code =0
                and critical =0
                Union all
                Select Count(*) As TotalJOB
                From err_ex
                Where Trunc(Err_Time) = Trunc(Sysdate)
                And ERR_MODULE = 'BACKEND_DOUBLE'
                And ERR_DETAIL = 'OK'
                and rownum =1
                Union all 
                Select Count(*) As TotalJOB
                From NP_EXEC_LOG
                where trunc(exec_date)  = Trunc(Sysdate)
                And STT ='100100300700'
                And EX_ERR =0
                and rownum =1 
                Union all 
                Select Count(*) As TotalJOB
                From err_ex
                Where Trunc(Err_Time) = Trunc(Sysdate)
                And ERR_MODULE = 'TC_NULL'
                And ERR_DETAIL = 'OK'
                and rownum =1 
                Union all 
                Select Count(*) As TotalJOB
                from err_ex
                where Trunc(Err_Time) = Trunc(Sysdate)
                and err_module ='NAPAS_SYNC_ECOM'
                and Err_detail like '%Success - MAX STT:%'
                and rownum =1 
                Union all
                Select Count(*) As TotalJOB
                From err_ex
                Where Trunc(Err_Time) = Trunc(Sysdate)
                and err_module in ('CHECK_SPEC_CHAR','CHECK_SPEC_CHAR_IBFT')
                and err_code = 0
                and rownum =1 
                );            
            if iCompleted < 10 then
                Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                Values(sysdate,'0','Wait check data ready','NAPAS_SHC_TMP_DOMESTIC_IBFT');
                commit;   
                DBMS_LOCK.sleep(60);
            end if;
            
        END LOOP;
    Begin
        Select PARA_VALUE Into vlistsms
        From NAPAS_PARA
        Where PARA_NAME = 'LIST_SMS';
    EXCEPTION
        WHEN OTHERS THEN
            vlistsms := '0983411005';
    End;    
    Begin
        Select To_Date(PARA_VALUE,'dd/mm/yyyy') Into dLastSett
        From NAPAS_PARA
        Where PARA_NAME = 'LAST_SETT_USD';
    EXCEPTION
        WHEN OTHERS THEN
            dLastSett := To_Date('09/09/2019','dd/mm/yyyy');
    End;
    
    Begin
        Select To_Number(PARA_VALUE) Into O_RATE
        From NAPAS_PARA
        Where PARA_NAME = 'LAST_RATE';
    EXCEPTION
        WHEN OTHERS THEN
            O_RATE := null;
    End;
    
    
-- KHOI TAO CAC GIA TRI
    df := sysdate;
    v_begin := SYSTIMESTAMP;
    
    dt_start := sysdate;
    
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'-1','Start:'||pQRY_FROM_DATE||' - '||pQRY_TO_DATE,'NAPAS_SHC_TMP_DOMESTIC_IBFT');
    
    d1 := TO_DATE(pQRY_FROM_DATE,'dd/MM/yyyy');
    d2 := TO_DATE(pQRY_TO_DATE,'dd/MM/yyyy');
    vNapasDate :=trunc(d2+1);
    day1 := TO_DATE(pQRY_FROM_DATE||' 230000','dd/MM/yyyy HH24MISS');
    day2 := TO_DATE(pQRY_TO_DATE||' 230000','dd/MM/yyyy HH24MISS');
    -- LAY DU LIEU
    EXECUTE IMMEDIATE 'truncate table SHCLOG_SETT_IBFT_ADJUST';

    commit;
    
    If (d2 = Trunc(Sysdate-1)) Then
    
        Insert Into NP_SETT_DATE(SETT_DATE, DT_TYPE, F_DATE, T_DATE)
        Values(Sysdate, '000000',d1,d2);
        
    End If;
    
    
    dbms_output.put_line(to_char(d1));
    dbms_output.put_line(to_char(d2));

    SELECT  TO_NUMBER(PARAMVALUE)  INTO thue FROM SYS_TBLPARAMETERS WHERE PARAMNAME = 'THUE' ;
    
    dbms_output.put_line('shclog insert begin');
    
    If (pSett_Code = 900 Or pSett_Code = 901) Then -- Hach toan ngoai VND, USD
        dbms_output.put_line('shclog insert if');
        
        rl:= 'PAUSE';
        
    Else
	--block insert
        dbms_output.put_line('shclog insert else');        
        -- 25-dec-2023: xu ly lai lay cac giao dich hoan tra va QTBS o bang SHCLOG
        INSERT  /*+APPEND*/  INTO SHCLOG_SETT_IBFT_ADJUST( MSGTYPE,PAN,PCODE,AMOUNT,SETTLEMENT_AMOUNT,cardholder_amount,PRE_CARDHOLDER_AMOUNT,ACQ_CURRENCY_CODE
            ,SETT_CURRENCY_CODE,CH_currency_code,FEE_ISS,FEE_ACQ,TRACE,LOCAL_TIME,LOCAL_DATE,CAP_DATE,
            SETTLEMENT_DATE,ACQUIRER,ISSUER ,RESPCODE,MERCHANT_TYPE,TERMID,ACCTNUM,ISS_CURRENCY_CODE,
            TRANDATE,TRANTIME,CARDPRODUCT,REVCODE,ORIGTRACE,ACCEPTORNAME,TERMLOC,TGGUIQT,TGXLNV,TGGUINV,TGGUIQTP,EDIT_DATE,
            EDIT_USER,SML_VERIFY,ORIGISS,ORIGRESPCODE,isrev,preamount,ppcode,AUTHNUM,thu,thu_refun,tt1,tt2,tt3,tt4,
            SETTLEMENT_CONV_RATE,CARDHOLDER_CONV_RATE,BB_BIN,FORWARD_INST,TRANSFEREE,refnum,MERCHANT_TYPE_ORIG,
            ACQUIRER_FE, ACQUIRER_RP, ISSUER_FE, ISSUER_RP, FEE_NOTE,PCODE2, FROM_SYS, TRAN_CASE, BB_BIN_ORIG, SRC, DES,
            PCODE_ORIG, RC_ISS, RC_BEN,napas_DATE,napas_EDIT_DATE, STT,Token, ADDRESPONSE, MVV,
            FEE_KEY, ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, F4,
            F5, F6, F49, SETTLEMENT_CODE, SETTLEMENT_RATE,
            ISS_CONV_RATE,TCC,FEE_IRF_ISS,FEE_SVF_ISS,FEE_IRF_ACQ,FEE_SVF_ACQ,FEE_IRF_BEN,FEE_SVF_BEN,Txnsrc,txndest,Content_Fund,ADD_INFO, DATA_ID,
            F60_UPI,F100_UPI,PREAMOUNT_USD,IS_PART_REV,TRANSIT_CSRR,MSGTYPE_DETAIL
            )
        SELECT MSGTYPE,PAN,SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) AS PCODE,
            Case When ACQ_CURRENCY_CODE In (840,418)
                Then ROUND(Case When SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) In ('00','01','40','41','42','43','48','91','20') Then AMOUNT Else 0 End,2)
                Else ROUND(Case When SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) In ('00','01','40','41','42','43','48','91','20') Then AMOUNT Else 0 End)
            End As AMOUNT,
            SETTLEMENT_AMOUNT,
            Case 
                When acquirer_rp = GET_BCCARD_ID() then CARDHOLDER_AMOUNT
                Else TRANSACTION_AMOUNT
            End,PRE_CARDHOLDER_AMOUNT,
            Case
                    When ACQ_CURRENCY_CODE = 840 Then 840
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704
            End As ACQ_CURRENCY_CODE,
            SETT_CURRENCY_CODE,CH_currency_code,
            Case When FEE_ISS Is null then 0
                    Else
                        Case
                            When ACQ_CURRENCY_CODE In (840,418) Then ROUND(FEE_ISS * thue,2)
                            Else
                                Case
                                    When PCODE2 In (810000,820000,830000,860000,870000,880000) Or Trim(FEE_NOTE) = 'IBT 72 (VAT)' Or FROM_SYS = 'IBT' Then ROUND(FEE_ISS,0)
                                    Else ROUND(FEE_ISS * thue,0)
                                End
                        End
            End As FEE_ISS,
            Case When FEE_ACQ Is null then 0
            When Acquirer = 970416 And Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE) = 456123 And Issuer Not In (602907,605609,220699) And SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) = '00' And Respcode = 115 Then 0
                Else
                    Case
                        When ACQ_CURRENCY_CODE In (840,418) Then ROUND(FEE_ACQ * thue,2)
                        Else
                            Case
                                When PCODE2 In (810000,820000,830000,860000,870000,880000) Or Trim(FEE_NOTE) = 'IBT 72 (VAT)' Or FROM_SYS = 'IBT' Then ROUND(FEE_ACQ,0)
                                Else ROUND(FEE_ACQ * thue,0)
                            End
                    End
            End As FEE_ACQ,

            TRACE,LOCAL_TIME,LOCAL_DATE,CAP_DATE,SETTLEMENT_DATE,trim(ACQUIRER) AS ACQUIRER,trim (ISSUER) AS ISSUER,
            RESPCODE,
            MERCHANT_TYPE,TERMID,
                     
            Case 
                When Decode(BB_BIN,null,0,BB_BIN) = 970403 And (TRIM(SUBSTR(ACCTNUM,1,INSTR(ACCTNUM || '|','|') -1)) Is Null Or TRIM(SUBSTR(ACCTNUM,1,INSTR(ACCTNUM || '|','|') -1)) = '')
                    Then Trim(PAN)||'|'||TRIM(SUBSTR(ACCTNUM,INSTR(ACCTNUM || '|','|') + 1, LENGTH(ACCTNUM)))
                Else ACCTNUM
            End ACCTNUM       
            ,ISS_CURRENCY_CODE,
            TRANDATE,TRANTIME,CARDPRODUCT,REVCODE,ORIGTRACE,ACCEPTORNAME,TERMLOC,TGGUIQT,TGXLNV,TGGUINV, TGGUIQTP,
            EDIT_DATE,
            EDIT_USER,SML_VERIFY,ORIGISS,ORIGRESPCODE,
            
            CASE WHEN respcode = 110 THEN 0 WHEN respcode =115 AND origrespcode =225 THEN 0 WHEN Isrev IS NULL THEN 0 ELSE 1 END isrev,
            
            Case When ACQ_CURRENCY_CODE In (840,418) Then ROUND(preamount,2)
                        Else ROUND(preamount)
            End As preamount,
            
            pcode AS ppcode,AUTHNUM,
            0 AS thu,
            0 AS thu_refun,
            0 AS tt1,
            0 AS tt2,
            0 AS tt3,
            0 AS tt4,
            CONV_RATE,
                                   
            Case 
                When Acquirer_Rp = 970488 And Acq_Currency_Code = 840 And SETTLEMENT_DATE < dLastSett 
                    Then Decode(O_RATE,null,CARDHOLDER_CONV_RATE,O_RATE)
                Else CARDHOLDER_CONV_RATE
            End,
            Decode(SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2),'40',null,BB_BIN),FORWARD_INST,TRANSFEREE,refnum,
            Decode(MERCHANT_TYPE_ORIG,null,MERCHANT_TYPE,MERCHANT_TYPE_ORIG),
            Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE),
            Decode(ACQUIRER_RP,null,ACQUIRER,ACQUIRER_RP),
            Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
            Decode(ISSUER_RP,null,ISSUER,ISSUER_RP),
            FEE_NOTE,PCODE2, FROM_SYS, TRAN_CASE, Decode(SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2),'40',null,BB_BIN_ORIG), SRC, DES, PCODE_ORIG
            , Decode(RC_ISS,null,'0000',RC_ISS), Decode(RC_BEN,null,'0000',RC_BEN)
                                                                                       
            ,case when issuer = 602907 then decode(napas_edit_date,null,napas_nd_date,napas_edit_date) else decode(edit_date,null,settlement_date+1,trunc(edit_date) +1) end napas_date
            ,decode(napas_EDIT_DATE,'29-aug-2016',napas_EDIT_DATE-1,napas_EDIT_DATE) napas_EDIT_DATE, STT,token,
            ADDRESPONSE,MVV,FEE_KEY,ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, nvl(F4,amount), CASE
          WHEN nvl(F5, amount) <= 0
               AND substr(t.pcode, 1, 2) = '42' THEN
           amount
          WHEN substr(t.pcode, 1, 2) IN ('30', '35', '38', '94') THEN
           0
          ELSE
           nvl(F5, amount)
       END , F6,
            F49, SETTLEMENT_CODE, SETTLEMENT_RATE,
            ISS_CONV_RATE,TCC,
            Case When FEE_IRF_ISS Is null then 0
                    Else
                        Case 
                            When ACQ_CURRENCY_CODE In (840,418) Then ROUND(FEE_IRF_ISS * thue,2)
                            Else ROUND(FEE_IRF_ISS * thue,0)
                        End
            End As FEE_IRF_ISS,           
            Case 
                When SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) In ('41','48') And SETTLEMENT_DATE < To_Date('20170401','yyyymmdd')
                    Then -(ROUND(FEE_ISS * thue,0) + ROUND(FEE_IRF_ISS * thue,0))
                Else
                    Case When FEE_SVF_ISS Is null then 0
                            Else
                                Case 
                                    When ACQ_CURRENCY_CODE In (840,418) Then ROUND(FEE_SVF_ISS * thue,2)
                                    Else ROUND(FEE_SVF_ISS * thue,0)
                                End
                    End 
            End As FEE_SVF_ISS,
            Case When FEE_IRF_ACQ Is null then 0
                    Else
                        Case 
                            When ACQ_CURRENCY_CODE In (840,418) Then ROUND(FEE_IRF_ACQ * thue,2)
                            When Acquirer_Rp = Get_BCCard_Id() Then ROUND(FEE_IRF_ACQ * thue,2)
                            Else ROUND(FEE_IRF_ACQ * thue,0)
                        End
            End As FEE_IRF_ACQ,
            Case When FEE_SVF_ACQ Is null then 0
                    Else
                        Case 
                            When ACQ_CURRENCY_CODE In (840,418) Then ROUND(FEE_SVF_ACQ * thue,2)
                            Else ROUND(FEE_SVF_ACQ * thue,0)
                        End
            End As FEE_SVF_ACQ,
            Case When FEE_IRF_BEN Is null then 0
                    Else
                        Case 
                            When ACQ_CURRENCY_CODE In (840,418) Then ROUND(FEE_IRF_BEN * thue,2)
                            Else ROUND(FEE_IRF_BEN * thue,0)
                        End
            End As FEE_IRF_BEN,
            Case When FEE_SVF_BEN Is null then 0
                    Else
                        Case 
                            When ACQ_CURRENCY_CODE In (840,418) Then ROUND(FEE_SVF_BEN * thue,2)
                            Else ROUND(FEE_SVF_BEN * thue,0)
                        End
            End As FEE_SVF_BEN
            ,Txnsrc,txndest,substr(t.content_fund,1,300),ADD_INFO,
            Case
                When Issuer_Rp = 600005 Or Acquirer_rp = 600005 Then 4
                When Issuer_Rp = 600007 Then 3
                When Issuer_Rp = 600006 Then 2
                When Issuer_Rp = 602907 Then 5 --- UPI
                When Acquirer_rp = GET_BCCARD_ID() then 8 -- Bccard
                When Issuer_rp  = 764000 Or BB_BIN  = 764000 Then 9 -- QR_ITMX
                When Issuer_rp = 600008 Then 10  -- Amex -- Len that rao lai
                Else 1
            End,F60_UPI,F100_UPI,PREAMOUNT_USD,IS_PART_REV,TRANSIT_CSRR,MSGTYPE_DETAIL
        FROM shclog t 
        WHERE
        t.edit_date between d1  and d2 + 1 - 1/86400        
        AND length(trim(T.ACQUIRER))= 6 --- Cau hinh khong lay du lieu cua JCB co acquirer > 6
        AND FROM_SYS Is Not Null
        AND
        (
            (
                SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) ='42'
                And
                Decode(FROM_SYS,null,'IST',FROM_SYS) Like '%IST%'
            )
            Or
            (
                FROM_SYS Is Not Null
                And
                SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) In ('42','91')
            )
        )
        AND
        (   -- C?ng Quy?t toán b? sung, xác nh?n KTC, nghi v?n, hoàn tr? toàn ph?n
            (
                                 
                RESPCODE IN (110,111,113,115) 
                AND
                ( 
                    (
                        edit_date BETWEEN d1 AND d2+1
                                                
                        And (ISSUER NOT IN (602907,600005,600006,600007,605609,220699)
                                And ACQUIRER NOT IN (605608,600005,605609,220699)
                                And ISSUER_RP not in (764000,600008,600009,600011) 
                                And decode(BB_BIN,null,'000000',BB_BIN) not in (764000,600009,600011)
                            )
                    )                     
                )                    
                AND PREAMOUNT IS NULL
                And SETTLEMENT_DATE < d1
            )
            OR -- Hoàn tr? m?t ph?n
            ( 
                RESPCODE In (112,114)
                AND
                ( 
                    (
                        edit_date BETWEEN d1 AND d2+1
                        And ( ISSUER NOT IN (602907,600005,600006,600007,605609,220699)
                            And ACQUIRER NOT IN (605608,600005,605609,220699)
                            And ISSUER_RP not in (764000,600008,600009 ,600011) 
                            And decode(BB_BIN,null,'000000',BB_BIN) not in (764000,600009,600011))
                         
                    )
                )  
                AND PREAMOUNT IS Not NULL
                And SETTLEMENT_DATE < d1
            )  
        );
        
    End If;
    
    commit;    
	--block update 1`
    Update SHCLOG_SETT_IBFT_ADJUST
    Set FEE_KEY = NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),
        FEE_ISS = OLD_FINAL_FEE_CAL_VER('ISS',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    Case 
                        When FROM_SYS Is null Then SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2)
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_ACQ = OLD_FINAL_FEE_CAL_VER('ACQ',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    Case 
                        When FROM_SYS Is null Then SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2)
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_IRF_ISS = NAPAS_FEE_OLD_TRAN(MSGTYPE,'IRF_ISS',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_SVF_ISS = NAPAS_FEE_OLD_TRAN(MSGTYPE,'SVF_ISS',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_IRF_ACQ = NAPAS_FEE_OLD_TRAN(MSGTYPE,'IRF_ACQ',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_SVF_ACQ = NAPAS_FEE_OLD_TRAN(MSGTYPE,'SVF_ACQ',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),                    
        FEE_IRF_BEN = NAPAS_FEE_OLD_TRAN(MSGTYPE,'IRF_BEN',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_SVF_BEN = NAPAS_FEE_OLD_TRAN(MSGTYPE,'SVF_BEN',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),                                        
        FEE_NOTE = NAPAS_GET_FEE_NAME(NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE)),
        RE_FEE = 1                                        
    where msgtype = 210
    and respcode = 110
    And Fee_Key Is Null
    And FEE_NOTE Not Like '%HACH TOAN DIEU CHINH%'
    ;
    
    commit;
	--block update 2 respcode In (110,112,113,114,115)
    Update SHCLOG_SETT_IBFT_ADJUST
    Set FEE_IRF_ISS = NAPAS_FEE_OLD_TRAN(MSGTYPE,'IRF_ISS',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_SVF_ISS = NAPAS_FEE_OLD_TRAN(MSGTYPE,'SVF_ISS',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_IRF_ACQ = NAPAS_FEE_OLD_TRAN(MSGTYPE,'IRF_ACQ',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_SVF_ACQ = NAPAS_FEE_OLD_TRAN(MSGTYPE,'SVF_ACQ',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),                    
        FEE_IRF_BEN = NAPAS_FEE_OLD_TRAN(MSGTYPE,'IRF_BEN',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_SVF_BEN = NAPAS_FEE_OLD_TRAN(MSGTYPE,'SVF_BEN',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        RE_FEE = 1                                                        
    where msgtype = 210
    and respcode In (110,112,113,114,115)
    And Fee_Key In
        (
            Select Fee_Key
            From Gr_Fee_Config_New
            Where Trunc(Valid_To) <= To_Date('20171001','yyyymmdd')
        )   
    ;
    --block update 3 respcode In (110,112,113,114,115) SETTLEMENT_DATE Between To_Date('20170701','yyyymmdd') And To_Date('20171001','yyyymmdd')   -- UTM phi moi 1/7/2017->1/10/2017
    Update SHCLOG_SETT_IBFT_ADJUST
    Set FEE_IRF_ISS = NAPAS_FEE_OLD_TRAN(MSGTYPE,'IRF_ISS',Fee_Key,Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_SVF_ISS = NAPAS_FEE_OLD_TRAN(MSGTYPE,'SVF_ISS',Fee_Key,Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_IRF_ACQ = NAPAS_FEE_OLD_TRAN(MSGTYPE,'IRF_ACQ',Fee_Key,Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_SVF_ACQ = NAPAS_FEE_OLD_TRAN(MSGTYPE,'SVF_ACQ',Fee_Key,Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),                    
        FEE_IRF_BEN = NAPAS_FEE_OLD_TRAN(MSGTYPE,'IRF_BEN',Fee_Key,Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_SVF_BEN = NAPAS_FEE_OLD_TRAN(MSGTYPE,'SVF_BEN',Fee_Key,Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        RE_FEE = 1
    where msgtype = 210
    and respcode In (110,112,113,114,115)
    And Fee_Key In
    (
        'C4J94BD3-E9K8-46EC-Q8A7-FE46J0E95P1E',
        'LC01751F-0C65-4FA2-9T34-8H86C13CDTC0'
    )
    And SETTLEMENT_DATE Between To_Date('20170701','yyyymmdd') And To_Date('20171001','yyyymmdd')   -- UTM phi moi 1/7/2017->1/10/2017
    ;
    
    commit;
	-- block update 4 respcode In (110,112,113,114,115)
    Update SHCLOG_SETT_IBFT_ADJUST
    Set FEE_IRF_ISS = NAPAS_FEE_OLD_TRAN(MSGTYPE,'IRF_ISS',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_SVF_ISS = NAPAS_FEE_OLD_TRAN(MSGTYPE,'SVF_ISS',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_IRF_ACQ = NAPAS_FEE_OLD_TRAN(MSGTYPE,'IRF_ACQ',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_SVF_ACQ = NAPAS_FEE_OLD_TRAN(MSGTYPE,'SVF_ACQ',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),                    
        FEE_IRF_BEN = NAPAS_FEE_OLD_TRAN(MSGTYPE,'IRF_BEN',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        FEE_SVF_BEN = NAPAS_FEE_OLD_TRAN(MSGTYPE,'SVF_BEN',NAPAS_GET_FEE_KEY(Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE), 
                    Case 
                        When PCODE2 Is Null Then PCODE
                        When PCODE2 In (810000,820000,830000,860000,870000,880000) Then SUBSTR(Trim(TO_CHAR(PCODE2,'099999')),1,2)
                        When PCODE2 In (910000) And (FROM_SYS = 'IST' Or TRAN_CASE = 'C3|72') Then PCODE
                        Else SUBSTR( trim(TO_CHAR(PCODE2,'099999')),1,2)
                    End,
                    Case When TRAN_CASE = '72|C3' Then 6011 When Pcode2 = 880000 Then 0 Else MERCHANT_TYPE End,ACQ_CURRENCY_CODE,SETTLEMENT_DATE,TRACE),Decode(ACQUIRER_FE,null,ACQUIRER,ACQUIRER_FE), Decode(ISSUER_FE,null,ISSUER,ISSUER_FE),
                    ACQ_CURRENCY_CODE,Decode(ACQ_CURRENCY_CODE,418,SETTLEMENT_AMOUNT,AMOUNT),CARDHOLDER_CONV_RATE,TRACE),
        RE_FEE = 1                                                        
    where msgtype = 210
    and respcode In (110,112,113,114,115)
    And Fee_Key In
    (
        Select Fee_Key
        From Gr_Fee_Config_New
        Where Trunc(Valid_To) <= To_Date('20171001','yyyymmdd')
    )
    And FEE_SVF_ISS = 0
    And FEE_IRF_ISS = 0
    And FEE_IRF_ACQ = 0
    And FEE_SVF_ACQ = 0
    ;
    commit;
    -- block update 5
    update SHCLOG_SETT_IBFT_ADJUST
    Set FEE_IRF_ISS= Case When FEE_IRF_ISS Is null then 0
                    Else
                        Case 
                            When ACQ_CURRENCY_CODE In (840,418) Then ROUND(FEE_IRF_ISS * thue,2)
                            Else ROUND(FEE_IRF_ISS * thue,0)
                        End
            End,           
            FEE_SVF_ISS = Case When FEE_SVF_ISS Is null then 0
                            Else
                                Case 
                                    When ACQ_CURRENCY_CODE In (840,418) Then ROUND(FEE_SVF_ISS * thue,2)
                                    Else ROUND(FEE_SVF_ISS * thue,0)
                                End
                    End,
            FEE_IRF_ACQ = Case When FEE_IRF_ACQ Is null then 0
                    Else
                        Case 
                            When ACQ_CURRENCY_CODE In (840,418) Then ROUND(FEE_IRF_ACQ * thue,2)
                            Else ROUND(FEE_IRF_ACQ * thue,0)
                        End
            End,
            FEE_SVF_ACQ = Case When FEE_SVF_ACQ Is null then 0
                    Else
                        Case 
                            When ACQ_CURRENCY_CODE In (840,418) Then ROUND(FEE_SVF_ACQ * thue,2)
                            Else ROUND(FEE_SVF_ACQ * thue,0)
                        End
            End,
            FEE_IRF_BEN = Case When FEE_IRF_BEN Is null then 0
                    Else
                        Case 
                            When ACQ_CURRENCY_CODE In (840,418) Then ROUND(FEE_IRF_BEN * thue,2)
                            Else ROUND(FEE_IRF_BEN * thue,0)
                        End
            End,
            FEE_SVF_BEN = Case When FEE_SVF_BEN Is null then 0
                    Else
                        Case 
                            When ACQ_CURRENCY_CODE In (840,418) Then ROUND(FEE_SVF_BEN * thue,2)
                            Else ROUND(FEE_SVF_BEN * thue,0)
                        End
            End 
    Where Decode(RE_FEE,null,0,RE_FEE) = 1
    ;
    
    Update SHCLOG_SETT_IBFT_ADJUST 
    Set FEE_SVF_ISS = FEE_ISS + FEE_IRF_ISS
    Where Fee_Key In
    (
    'A300C4EB-E648-4F98-99FC-9C11BFACE6D6', -- 41 cu
    '46206160-7462-46C0-81D0-6DA81C367958', -- 42 cu ATM
    'A775AB95-CF99-411E-83CE-F42E5AB4F61B'  -- 48 cu
    );
    commit;
    UPDATE SHCLOG_SETT_IBFT_ADJUST t
    SET    SVFISSNP = nvl(t.Fee_Svf_Iss, 0),
           IRFISSACQ = CASE
                          WHEN acquirer_rp = 220699
                               OR issuer_rp = 220699 THEN
                           nvl(FEE_IRF_ACQ, 0)
                          ELSE
                           CASE
                              WHEN t.fee_irf_acq > 0 THEN
                               nvl(t.fee_irf_iss, 0)
                              ELSE
                               0
                           END
                       END,
           IRFISSBNB = CASE
                          WHEN t.fee_irf_ben > 0 THEN
                           nvl(t.fee_irf_iss, 0)
                          ELSE
                           0
                       END, SVFACQNP = nvl(t.Fee_Svf_ACQ, 0),
           IRFACQISS = CASE
                          WHEN t.fee_irf_iss > 0 THEN
                           nvl(t.fee_irf_acq, 0)
                          ELSE
                           0
                       END,
           IRFACQBNB = CASE
                          WHEN t.fee_irf_ben > 0 THEN
                           nvl(t.fee_irf_acq, 0)
                          ELSE
                           0
                       END, SVFBNBNP = nvl(t.fee_svf_ben, 0),
           IRFBNBISS = CASE
                          WHEN t.fee_irf_ISS > 0 THEN
                           nvl(t.fee_irf_ben, 0)
                          ELSE
                           0
                       END,
           IRFBNBACQ = CASE
                          WHEN t.fee_irf_ACQ > 0 THEN
                           nvl(t.fee_irf_ben, 0)
                          ELSE
                           0
                       END
    WHERE  t.respcode IN (0, 110, 115, 113);
    COMMIT;
    
    --------------- hoind 17/04/2018 Loai bo ky tu dac biet ---------------------
	-- block update 6 loai bo ky tu dac biet
    Update SHCLOG_SETT_IBFT_ADJUST
    Set CONTENT_FUND = replace(CONTENT_FUND,chr(9),'')
    Where  
        (
            CONTENT_FUND like '%'||chr(9)||'%'
            Or
            CONTENT_FUND like '%'||chr(10)||'%'
            Or
            CONTENT_FUND like '%'||chr(13)||'%'
        )
    ;
	-- block update 7 loai bo ky tu dac biet
    Update SHCLOG_SETT_IBFT_ADJUST
    Set CONTENT_FUND = replace(CONTENT_FUND,chr(10),'')
    Where  
        (
            CONTENT_FUND like '%'||chr(9)||'%'
            Or
            CONTENT_FUND like '%'||chr(10)||'%'
            Or
            CONTENT_FUND like '%'||chr(13)||'%'
        )
    ;
	-- block update 8
    Update SHCLOG_SETT_IBFT_ADJUST
    Set CONTENT_FUND = replace(CONTENT_FUND,chr(13),'')
    Where  
        (
            CONTENT_FUND like '%'||chr(9)||'%'
            Or
            CONTENT_FUND like '%'||chr(10)||'%'
            Or
            CONTENT_FUND like '%'||chr(13)||'%'
        )
    ;
    
    -------------- hoind update Pcode_Orig cho gd C3-72 phuc vu sinh file BEN --------------------------
    -- block update 9
    Update SHCLOG_SETT_IBFT_ADJUST
    Set Pcode_Orig = '91'||CREATE_PCODE(ACQUIRER,PAN)||CREATE_PCODE(BB_BIN,SUBSTR (ACCTNUM,INSTR (ACCTNUM || '|', '|') + 1,LENGTH (ACCTNUM)))
    Where From_Sys = 'IST|IBT' And Tran_Case = 'C3|72';
    
    Update SHCLOG_SETT_IBFT_ADJUST
    Set Pcode_Orig = '91'||CREATE_PCODE(ACQUIRER,PAN)||CREATE_PCODE(BB_BIN,SUBSTR (ACCTNUM,INSTR (ACCTNUM || '|', '|') + 1,LENGTH (ACCTNUM)))
    Where Pcode = '43'
    And BB_BIN In
    (
        Select To_Number(SHCLOG_ID) BANK
        From RP_INSTITUTION
        Where Decode(BEN_72,null,0,BEN_72) = 1
    );
    
    Update SHCLOG_SETT_IBFT_ADJUST
    Set Pcode_Orig = '420000'
    Where Pcode = '43'
    And BB_BIN Not In
    (
        Select To_Number(SHCLOG_ID) BANK
        From RP_INSTITUTION
        Where Decode(BEN_72,null,0,BEN_72) = 1
    );
    Update SHCLOG_SETT_IBFT_ADJUST
    Set FEE_IRF_ISS=0, FEE_SVF_ISS =0, FEE_IRF_BEN =0, FEE_SVF_BEN = 0,SVFISSNP =0 ,IRFISSACQ = 0,
    IRFISSBNB = 0,SVFACQNP= 0, IRFACQISS = 0,IRFACQBNB = 0, SVFBNBNP = 0, IRFBNBISS= 0, IRFBNBACQ = 0
    Where PCODE in (42,91)
    and decode(PCODE2,null,0,PCODE2) <> '890000'
    and RESPCODE in (112,113,114,115);
    ------------------------------------------------------------
    
    INSERT INTO RP_LOG_SHC_TMP(LOG_DDL_ID,TUTG,DENTG,TGDODL,CREATED_USER,SETT_CODE)
    VALUES(Getkhoacuabang('RP_LOG_SHC_TMP','LOG_DDL_ID') ,TO_CHAR(day1,'dd/mm/yyyy hh24:mi:ss') ,TO_CHAR(day2,'dd/mm/yyyy hh24:mi:ss'),SYSDATE,pCreated_User,Decode(pSett_Code,900,7041,901,8401,pSett_Code));
    commit;
    
    DATA_TO_ECOM(pQRY_FROM_DATE, pQRY_TO_DATE);
    
    dbms_output.put_line('shclog RP_LOG_SHC_TMP');
    dt := sysdate;
    v_end := SYSTIMESTAMP;
    dt_end := sysdate;
    v_interval := v_end - v_begin;

    vDetail := 'Do du lieu(New) '||pSett_Code||' tc, user: '||vNapasDate||'.DL tu '||TO_CHAR(d1,'dd/mm/yyyy')||' toi '||TO_CHAR(d2,'dd/mm/yyyy')||' . Bat dau do luc: '||TO_CHAR(df,'hh24:mi:ss dd/mm/yyyy')||' hoan thanh luc: '||TO_CHAR(dt,'hh24:mi:ss dd/mm/yyyy')|| CHR(13) || CHR(10)||'Tong TG do dl la: '||extract(hour from v_interval)||' gio, '||extract(minute from v_interval)||' phut, '||Round(extract(second from v_interval),0)||' giay.';
    --Utl_Mail.send(vSender,vReceiver,vCC,bCC,vSub,vDetail,vMType,NULL);
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'-1',vDetail,'NAPAS_SHC_TMP_DOMESTIC_IBFT');
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,0,'End fill data to SHC_TMP_DOMESTIC_IBFT','NAPAS_SHC_TMP_DOMESTIC_IBFT');
    dbms_output.put_line('shclog ERR_EX');
    commit;
    
EXCEPTION
    WHEN OTHERS THEN
    ecode := SQLCODE;
    emesg := SQLERRM;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE,CRITICAL)
    Values(sysdate,ecode,emesg,'NAPAS_SHC_TMP_DOMESTIC_IBFT',2);
    --SELECT  TEN INTO  pResult FROM SYS_THONGBAO WHERE MA = 'ERROR - TP' ;
    vDetail := 'user: '||pCreated_User||'.Do du lieu  '||pSett_Code||' tu '||TO_CHAR(day1,'hh24:mi:ss dd/mm/yyyy')||' toi '||TO_CHAR(day2,'hh24:mi:ss dd/mm/yyyy')||' khong thanh cong ! Err num: ' || TO_CHAR(ecode) || ' - Err detail: ' || emesg;
    vDetail :='Co loi do du lieu ' || substr(emesg,1,120);
    --Utl_Mail.send(vSender,vReceiver,vCC,bCC,vSub,vDetail,vMType,NULL);
    SEND_SMS('NAPAS_SHC_TMP_DOMESTIC_IBFT#' || vlistsms || '#' || vDetail);
END; /* GOLDENGATE_DDL_REPLICATION */
/
