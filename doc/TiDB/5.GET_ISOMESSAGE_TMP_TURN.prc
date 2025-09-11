CREATE OR REPLACE PROCEDURE RPT.GET_ISOMESSAGE_TMP_TURN
as
    Ecode 	NUMBER;
    Emesg 	VARCHAR2(200);
    vDetail VARCHAR2(500) := 'Nothing ! ';
    iNum 	Integer;
    iNumIST Integer;
    iSTT 	integer:=0; 
    
    /*
	Project name: DVTT b?ng mã QR dành cho ÐVCNTT (v?i DCORP)
	Dev Jira Tikcet (If have):
	Author: NinhNT
	Dev Date: 08-Oct-2024
	Edit content: Gi? nguyên RESPCODE t? online cho GD Master Merchant (BEN_ID = 971133, DEST_ACCOUNT like 'NPDC%')
	
	Project name: DVTT b?ng mã QR dành cho ÐVCNTT (v?i KIOTVIET)
	Dev Jira Tikcet (If have): ITCRB 291-32
	Author: DatTT
	Dev Date: 19-Aug-2025
	Edit content: B? sung nh?n d?ng GD Master Merchant v?i KIOTVIET (DEST_ACCOUNT like 'NQ%')
	*/
	
BEGIN       

    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,0,'Begin Insert From ISOMESSAGE_TMP_TURN to SHCLOG_SETT_IBFT','GET_ISOMESSAGE_TMP_TURN');
    commit;
    Select /*+ index (shclog IDTT) */ Max(STT) Into iNum
    From SHCLOG;
    
    Select /*+ index (shclog IDTT_SETT_IST) */ Max(STT) Into iNumIST
    From SHCLOG_SETT_IST;
    
    if(iNumIST is null) then
        iNumIST :=0;
    End If;
    
    if(iNum > iNumIST) then
        iSTT:=iNum;
    Else
        iSTT:=iNumIST;
    End if;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,0,'STT Start in SHCLOG_SETT_IBFT:'|| iSTT,'GET_ISOMESSAGE_TMP_TURN');
    commit;
    Insert /*+ parallel(6) */  Into SHCLOG_SETT_IBFT(DATA_ID,PPCODE,STT,MSGTYPE,PAN,PCODE,Amount,ACQ_CURRENCY_CODE,TRACE,LOCAL_TIME,LOCAL_DATE,SETTLEMENT_DATE,
		ACQUIRER,ISSUER,RESPCODE,MERCHANT_TYPE,MERCHANT_TYPE_ORIG,AUTHNUM,SETT_CURRENCY_CODE,TERMID,ADD_INFO,ACCTNUM,
		ISS_CURRENCY_CODE,ORIGTRACE,ORIGISS,ORIGRESPCODE,CH_CURRENCY_CODE,ACQUIRER_FE,ACQUIRER_RP,ISSUER_FE,
		ISSUER_RP,PCODE2,FROM_SYS,BB_BIN,BB_BIN_ORIG,CONTENT_FUND,TXNSRC,ACQ_COUNTRY,POS_ENTRY_CODE,POS_CONDITION_CODE,
		ADDRESPONSE,MVV,F4,F5,F6,F49,SETTLEMENT_CODE,SETTLEMENT_RATE,ISS_CONV_RATE,TCC,
		refnum,trandate,trantime,ACCEPTORNAME,TERMLOC, F15, pcode_orig,ACCOUNT_NO,DEST_ACCOUNT,INS_PCODE)
	Select 1,To_Number(PROC_CODE) AS PPCODE,rownum+iSTT,210, CARD_NO As PAN,
        To_Number(PROC_CODE) PCODE,
        To_Number(Decode(SubStr(AMOUNT,0,Length(AMOUNT)-2),'',0,SubStr(AMOUNT,0,Length(AMOUNT)-2))) As Amount,
        704 As ACQ_CURRENCY_CODE,
        To_Number('2'||TRACE_NO) As TRACE,
        To_Number(LOCAL_TIME) As LOCAL_TIME,
        NP_CONVERT_LOCAL_DATE(LOCAL_DATE,Trunc(Sysdate)) As LOCAL_DATE,
        NP_CONVERT_LOCAL_DATE(SETTLE_DATE,Trunc(Sysdate)) As SETTLEMENT_DATE,
        To_Number(ACQ_ID) As ACQUIRER, To_Number(ACQ_ID) As ISSUER,
        Case
            When BEN_ID = 971133 AND (DEST_ACCOUNT LIKE 'NPDC%' OR DEST_ACCOUNT LIKE 'NQ%') Then RESPONSE_CODE
            When SERVICE_CODE = 'QR_PUSH' Then RESPONSE_CODE
            When BEN_ID  = 971100 And TCC ='99' Then RESPONSE_CODE
            When Trim(ISS_ID) = '980471' Or Trim(ISS_ID) = '980472' Then RESPONSE_CODE
            When RESPONSE_CODE = '68' Then '0'
            Else RESPONSE_CODE
        End As RESPCODE,
        6011 As MERCHANT_TYPE, To_Number(MCC) MERCHANT_TYPE_ORIG,
        APPROVAL_CODE As AUTHNUM, 704 As SETT_CURRENCY_CODE,
        TERM_ID As TERMID,
        ADD_INFO,
        Decode(ACCOUNT_NO,null,' ',ACCOUNT_NO)||'|'||Decode(DEST_ACCOUNT,null,'',DEST_ACCOUNT) As ACCTNUM,
        704 As ISS_CURRENCY_CODE,To_Number(TRACE_NO) As ORIGTRACE,
        To_Number(ACQ_ID) As ORIGISS,
        To_Number(Decode(ORIGRESPCODE,null,97,ORIGRESPCODE)) As ORIGRESPCODE,
        704 As CH_CURRENCY_CODE,
        Case
        When To_Number(ACQ_ID) = 191919 Then 970459
        When To_Number(ACQ_ID) = 970415 Then 970489
        Else MAP_IBFT_ACQ_ID(ACQ_ID)
        End As ACQUIRER_FE,
        Case
        When Trim(ISS_ID) = '980471' Then 980471 
        When Trim(ISS_ID) = '980475' Then 980478 --ninhnt them cho du an IBFT2.0
        When To_Number(ACQ_ID) = 191919 Then 970459
        When To_Number(ACQ_ID) = 970415 Then 970489
        Else MAP_IBFT_ACQ_ID(ACQ_ID)
        End As ACQUIRER_RP,
        Case
        When To_Number(ACQ_ID) = 191919 Then 970459
        When To_Number(ACQ_ID) = 970415 Then 970489
        Else MAP_IBFT_ACQ_ID(ACQ_ID)
        End As ISSUER_FE,
        Case
        When Trim(ISS_ID) = '980471' Then 980471 
        When Trim(ISS_ID) = '980475' Then 980478 --ninhnt them cho du an IBFT2.0
        When To_Number(ACQ_ID) = 191919 Then 970459
        When To_Number(ACQ_ID) = 970415 Then 970489
        Else MAP_IBFT_ACQ_ID(ACQ_ID)
        End As ISSUER_RP,
        Case 
        When TCC ='99' Then 930000
        When TCC ='95' Then 950000
        When SERVICE_CODE = 'QR_PUSH' Then 890000
        When TCC ='97' Then 720000 
        When TCC ='98' Then 730000
        Else 910000 
        End As PCODE2,
        'IBT' As FROM_SYS,
        Case
        When Trim(ISS_ID) = '980472' Then 980471
        When Trim(ISS_ID) = '980474' Then 980478 --ninhnt them cho du an IBFT2.0
        When BEN_ID Is Not Null And PROC_CODE IN ('912020','910020')
        Then GET_IBT_BIN(BEN_ID)
        Else GET_IBT_BIN(SUBSTR(DEST_ACCOUNT, 1, 6))
        End As BB_BIN, 
        Case
        When Trim(ISS_ID) In ('980472','980474') --ninhnt them cho du an IBFT2.0
        Then
        Case
        When BEN_ID Is Not Null And PROC_CODE IN ('912020','910020') 
            Then GET_IBT_BIN(BEN_ID)
        Else GET_IBT_BIN(SUBSTR(DEST_ACCOUNT, 1, 6))        
        End 
        Else to_number_bnv(BEN_ID)
        End As BEN_ID , IBFT_INFO, VAS_INFO,
        --TCKTM
        ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV,To_Number(Decode(SubStr(F4,0,Length(F4)-2),'',0,SubStr(F4,0,Length(F4)-2))) F4,
        To_Number(Decode(SubStr(F5,0,Length(F5)-2),'',0,SubStr(F5,0,Length(F5)-2))) F5,To_Number(Decode(SubStr(F6,0,Length(F6)-2),'',0,SubStr(F6,0,Length(F6)-2))) F6, F49, 
        SETTLEMENT_CODE, SETTLEMENT_RATE,ISS_CONV_RATE,TCC,REF_NO, trunc(Tnx_Stamp) trandate,to_char(tnx_stamp,'HH24MISS') trantime
        ,CARD_ACCEPT_NAME_LOCATION As ACCEPTORNAME, CARD_ACCEPT_ID_CODE As TERMLOC,
        NP_CONVERT_LOCAL_DATE(SETTLE_DATE,Trunc(Sysdate)) As SETTLEMENT_DATE, To_Number(PROC_CODE) PCODE,ACCOUNT_NO,DEST_ACCOUNT,SUBSTR(OF_YEAR,0,2)        
	From ISOMESSAGE_TMP_TURN
	Where MTI ='0210'
        and (is_number(Decode(BEN_ID,null,'0',BEN_ID)) <> 0 
        And Is_number(trace_no) <> 0);
	commit;
	Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
	Values(sysdate,0,'End Insert From ISOMESSAGE_TMP_TURN to SHCLOG_SETT_IBFT','GET_ISOMESSAGE_TMP_TURN');
	commit;
        
EXCEPTION WHEN OTHERS THEN
    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' GET_ISOMESSAGE_TMP_TURN, Err detail: ' || emesg;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,ecode,vDetail,'GET_ISOMESSAGE_TMP_TURN', 2);
    commit;
    SEND_SMS('GET_ISOMESSAGE_TMP_TURN#0366155501;0983411005;0988766330#'||vDetail);
END;
/
