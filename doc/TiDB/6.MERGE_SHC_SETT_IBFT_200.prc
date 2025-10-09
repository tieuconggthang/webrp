CREATE OR REPLACE PROCEDURE RPT.MERGE_SHC_SETT_IBFT_200

AS
/* ----------------------  tinh phi gd ------------------------
    Author          : hoind
    Date created    : 13/08/2015
----------------------------------------------------------------------------------------------*/

    iPos Integer := 0;
    num INTEGER := 0 ;
    fee INTEGER := 0 ;
    Ecode NUMBER;
    Emesg VARCHAR2(200);
    dt_start DATE;
    v_begin TIMESTAMP(9);
    v_end TIMESTAMP(9);
    v_interval INTERVAL DAY TO SECOND;
    vDetail VARCHAR2(500) := 'Nothing ! ';
    vPhone VARCHAR2(200);
    iSTT integer;
    rowupdate integer;
BEGIN
	--step 1
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,0,'Begin Merge SHCLOG_SETT_IBFT and ISOMESSAGE_TMP_TURN','MERGE_SHC_SETT_IBFT_200');
    commit;
	-- step 2 tach thanh hai buoc 2.1 va 2.2
    Select /*+ index (shclog IDTT_SETT_IBFT) */ Max(STT) Into iSTT
    From SHCLOG_SETT_IBFT;
    -- step 3
    MERGE INTO (Select MSGTYPE,PAN,PCODE,Amount,ACQ_CURRENCY_CODE,TRACE,LOCAL_TIME,LOCAL_DATE,SETTLEMENT_DATE,
            ACQUIRER,ISSUER,RESPCODE,MERCHANT_TYPE,MERCHANT_TYPE_ORIG,AUTHNUM,SETT_CURRENCY_CODE,TERMID,ADD_INFO,ACCTNUM,
            ISS_CURRENCY_CODE,ORIGTRACE,ORIGISS,ORIGRESPCODE,CH_CURRENCY_CODE,ACQUIRER_FE,ACQUIRER_RP,ISSUER_FE,
            ISSUER_RP,PCODE2,FROM_SYS,BB_BIN,BB_BIN_ORIG,CONTENT_FUND,TXNSRC,ACQ_COUNTRY,POS_ENTRY_CODE,POS_CONDITION_CODE,
            ADDRESPONSE,MVV,F4,F5,F6,F49,SETTLEMENT_CODE,SETTLEMENT_RATE,ISS_CONV_RATE,TCC,
            refnum,trandate,trantime,ACCEPTORNAME,TERMLOC, F15, pcode_orig,ACCOUNT_NO,DEST_ACCOUNT,STT,DATA_ID,PPCODE
            From SHCLOG_SETT_IBFT)   A
    USING
    (
         Select *
         From ISOMESSAGE_TMP_TURN
         Where CARD_NO is not null
         And MTI = '0200'
         And (is_number(Decode(BEN_ID,null,'0',BEN_ID)) <> 0 And Is_number(trace_no) <> 0)
    ) B
    On
    (
        TRIM(A.PAN) = B.CARD_NO
        And A.ORIGTRACE = TO_NUMBER(B.TRACE_NO)
        And A.TERMID = B.TERM_ID
        And TO_CHAR(A.LOCAL_DATE,'MMDD') = B.LOCAL_DATE
        And A.LOCAL_TIME = TO_NUMBER(B.LOCAL_TIME)
        And A.ACQUIRER = B.ACQ_ID
    )
    WHEN MATCHED THEN
    Update
        Set 
        A.RESPCODE = Case When  A.AMOUNT <> round(B.AMOUNT/100,0) And  A.RESPCODE=0 Then 116 Else A.RESPCODE End, 
        A.TXNSRC = Case When  A.AMOUNT <> round(B.AMOUNT/100,0) Then 'RC=99' Else to_char(A.TXNSRC) End,
        A.CONTENT_FUND = B.IBFT_INFO       
    WHEN NOT MATCHED THEN   
    Insert (DATA_ID,PPCODE,MSGTYPE,PAN,PCODE,Amount,ACQ_CURRENCY_CODE,TRACE,LOCAL_TIME,LOCAL_DATE,SETTLEMENT_DATE,
            ACQUIRER,ISSUER,RESPCODE,MERCHANT_TYPE,MERCHANT_TYPE_ORIG,AUTHNUM,SETT_CURRENCY_CODE,TERMID,ADD_INFO,ACCTNUM,
            ISS_CURRENCY_CODE,ORIGTRACE,ORIGISS,ORIGRESPCODE,CH_CURRENCY_CODE,ACQUIRER_FE,ACQUIRER_RP,ISSUER_FE,
            ISSUER_RP,PCODE2,FROM_SYS,BB_BIN,BB_BIN_ORIG,CONTENT_FUND,TXNSRC,ACQ_COUNTRY,POS_ENTRY_CODE,POS_CONDITION_CODE,
            ADDRESPONSE,MVV,F4,F5,F6,F49,SETTLEMENT_CODE,SETTLEMENT_RATE,ISS_CONV_RATE,TCC,
            refnum,trandate,trantime,ACCEPTORNAME,TERMLOC, F15, pcode_orig,ACCOUNT_NO,DEST_ACCOUNT)
    Values(1,To_Number(B.PROC_CODE),'210', B.CARD_NO,
            To_Number(B.PROC_CODE),To_Number(Decode(SubStr(B.AMOUNT,0,Length(B.AMOUNT)-2),'',0,SubStr(B.AMOUNT,0,Length(B.AMOUNT)-2))),
            704,
            To_Number('2'||B.TRACE_NO),
            To_Number(B.LOCAL_TIME),
            NP_CONVERT_LOCAL_DATE(B.LOCAL_DATE,Trunc(Sysdate)),
            NP_CONVERT_LOCAL_DATE(B.SETTLE_DATE,Trunc(Sysdate)),
            To_Number(B.ACQ_ID), To_Number(B.ACQ_ID),
            Case
                When BEN_ID = 971133 AND DEST_ACCOUNT LIKE 'NPDC%' Then 68
                When B.SERVICE_CODE = 'QR_PUSH' Then 68
                When B.BEN_ID  = 971100 And TCC ='99' Then 68
                When Trim(B.ISS_ID) = '980471' Or Trim(B.ISS_ID) = '980472' Then 68
                --When Trim(B.ISS_ID) = '980474' Or Trim(B.ISS_ID) = '980475' Then 68 --ninhnt sua IBFT2.0 08/07/2024
                Else 0
            End,
            6011, To_Number(B.MCC),
            B.APPROVAL_CODE, 704,
            B.TERM_ID,
            B.ADD_INFO,
            Decode(B.ACCOUNT_NO,null,' ',B.ACCOUNT_NO)||'|'||Decode(B.DEST_ACCOUNT,null,'',B.DEST_ACCOUNT),
            704,To_Number(B.TRACE_NO),
            To_Number(B.ACQ_ID),
            '97',
            704,
            Case
                When To_Number(B.ACQ_ID) = 191919 Then 970459
                When To_Number(B.ACQ_ID) = 970415 Then 970489
                Else MAP_IBFT_ACQ_ID(B.ACQ_ID)
            End,
            Case
                When Trim(B.ISS_ID) = '980471' Then 980471 
                When Trim(B.ISS_ID) = '980475' Then 980478 --ninhnt sua IBFT2.0 08/07/2024
                When To_Number(B.ACQ_ID) = 191919 Then 970459
                When To_Number(B.ACQ_ID) = 970415 Then 970489
                Else MAP_IBFT_ACQ_ID(B.ACQ_ID)
            End,
            Case
                When To_Number(B.ACQ_ID) = 191919 Then 970459
                When To_Number(B.ACQ_ID) = 970415 Then 970489
                Else MAP_IBFT_ACQ_ID(B.ACQ_ID)
            End,
            Case
                When Trim(B.ISS_ID) = '980471' Then 980471
                When Trim(B.ISS_ID) = '980475' Then 980478 --ninhnt sua IBFT2.0 08/07/2024
                When To_Number(B.ACQ_ID) = 191919 Then 970459
                When To_Number(B.ACQ_ID) = 970415 Then 970489
                Else MAP_IBFT_ACQ_ID(B.ACQ_ID)
            End,
            Case 
                When B.TCC ='99' Then 930000
                When B.TCC ='95' Then 950000
                When B.SERVICE_CODE = 'QR_PUSH' Then 890000
                When B.TCC ='97' Then 720000 
                When B.TCC ='98' Then 730000
                Else 910000 
            End,
            'IBT',
            Case
                When Trim(B.ISS_ID) = '980472' Then 980471
                When Trim(ISS_ID) = '980474' Then 980478  --08/07/2024 ninhnt bo sung IBFT 2.0
                When B.BEN_ID Is Not Null And B.PROC_CODE IN ('912020', '910020')
                Then GET_IBT_BIN(B.BEN_ID)
                Else GET_IBT_BIN(SUBSTR(B.DEST_ACCOUNT, 1, 6))
            End, 
            Case
                When B.BEN_ID In (SELECT TGTT_ID FROM TGTT_20) Then to_number_bnv(B.BEN_ID) --GD thanh toan TGTT 2.0
                When Trim(B.ISS_ID) In ('980472','980474','980475') -- hoind (2-oct-2019) Sua ACH - 08/07/2024 ninhnt bo sung IBFT 2.0
                Then
                    Case When B.BEN_ID Is Not Null And B.PROC_CODE IN ('912020', '910020') Then GET_IBT_BIN(B.BEN_ID)
                    Else GET_IBT_BIN(SUBSTR(B.DEST_ACCOUNT, 1, 6))        
                    End 
                Else to_number_bnv(B.BEN_ID)
            End, B.IBFT_INFO, 'MTI=200',
            B.ACQ_COUNTRY, B.POS_ENTRY_CODE, B.POS_CONDITION_CODE, B.ADDRESPONSE,B.MVV,To_Number(Decode(SubStr(B.F4,0,Length(B.F4)-2),'',0,SubStr(B.F4,0,Length(B.F4)-2))),
            To_Number(Decode(SubStr(B.F5,0,Length(B.F5)-2),'',0,SubStr(B.F5,0,Length(B.F5)-2))),To_Number(Decode(SubStr(B.F6,0,Length(B.F6)-2),'',0,SubStr(B.F6,0,Length(B.F6)-2))), B.F49, 
            B.SETTLEMENT_CODE, B.SETTLEMENT_RATE,B.ISS_CONV_RATE,B.TCC,B.REF_NO, trunc(B.Tnx_Stamp),to_char(B.tnx_stamp,'HH24MISS')
            ,B.CARD_ACCEPT_NAME_LOCATION, B.CARD_ACCEPT_ID_CODE,
            NP_CONVERT_LOCAL_DATE(B.SETTLE_DATE,Trunc(Sysdate)), To_Number(B.PROC_CODE),B.ACCOUNT_NO,B.DEST_ACCOUNT);   
    Commit;  
		-- step 4
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,0,'Finish Merge SHCLOG_SETT_IBFT and ISOMESSAGE_TMP_TURN','MERGE_SHC_SETT_IBFT_200');
    commit; 
	--step 5
    Update SHCLOG_SETT_IBFT
    Set STT = rownum + iSTT
    Where STT is null and Origrespcode =97;
    rowupdate :=sql%rowcount;
    commit; 
		-- step 6
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,0,'Finish Update STT for '||rowupdate||' transactions ORIGRESPCODE = 97 in SHCLOG_SETT_IBFT','MERGE_SHC_SETT_IBFT_200');
    commit; 
	-- step 7
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,0,'End Merge SHCLOG_SETT_IBFT and ISOMESSAGE_TMP_TURN','MERGE_SHC_SETT_IBFT_200');
    commit;
EXCEPTION WHEN OTHERS THEN
    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' MERGE_SHC_SETT_IBFT_200, Err detail: ' || emesg;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,ecode,vDetail,'MERGE_SHC_SETT_IBFT_200', 2);
    commit;
    SEND_SMS('MERGE_SHC_SETT_IBFT_200#0366155501;0983411005;0988766330#'||vDetail);

END; /* GOLDENGATE_DDL_REPLICATION */
/
