CREATE OR REPLACE PROCEDURE RPT.CHECK_BACKEND_DOUBLE
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
    vSender VARCHAR2(40) := 'baocaotest@napas.com.vn';
    vReceiver VARCHAR2(30) := 'hoind@napas.com.vn';
    vCC VARCHAR2(120) := 'cmt_doisoat@napas.com.vn';
    bCC VARCHAR2(120) := '';
    vSub VARCHAR2(150):= 'Check backend double trans ATM/POS' ;
    vDetail VARCHAR2(500) := 'Nothing ! ';
    iDdl INTEGER := 0;
    iCompleted      INTEGER := 0;  
    Cursor cs IS
        Select Local_Date, Trace, Local_Time, TERMID, ACQUIRER, AMOUNT
        From Ibft_Double
        Where  RUNTIME > Trunc(Sysdate)
        And MODULE = 'BACKEND_DOUBLE';
        
    dt cs%ROWTYPE;        
    
Begin

      
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'0','BEGIN','BACKEND_DOUBLE');
    commit;
	
    MERGE INTO 
    (
        Select *
        From ibft_double
        Where MODULE = 'BACKEND_DOUBLE'
        And Trunc(RUNTIME) >= Trunc(Sysdate-3)
    ) A 
    USING
    (
        Select ACQUIRER, ORIGTRACE AS TRACE, LOCAL_TIME, LOCAL_DATE, trim(TERMID) TERMID, AMOUNT
        From
        (
            Select ACQUIRER, ORIGTRACE, LOCAL_TIME, LOCAL_DATE, trim(TERMID) TERMID,SETTLEMENT_DATE,EDIT_DATE,PCODE,RESPCODE, AMOUNT
                    From Shclog
                    Where (Settlement_Date = Trunc(Sysdate-2) OR EDIT_DATE >= trunc(sysdate-2))
                    and msgtype = 210
                    and pcode <> 390000
                    and respcode = 0
                    and pcode in (910020,910000,912000,912020)
            Union all
            Select ACQUIRER, ORIGTRACE, LOCAL_TIME, LOCAL_DATE, trim(TERMID) TERMID,SETTLEMENT_DATE,EDIT_DATE,PCODE,RESPCODE, AMOUNT
                    From Shclog_sett_ibft
                    Where Settlement_Date = Trunc(Sysdate-1) 
                    and msgtype = 210
                    and pcode <> 390000
                    and respcode = 0
        )
        Group by ACQUIRER, ORIGTRACE, LOCAL_TIME, LOCAL_DATE, trim(TERMID), AMOUNT
        Having Count(*) > 1
    ) B
    On 
    (
        A.TRACE = B.TRACE
        And A.LOCAL_TIME = B.LOCAL_TIME
        And A.LOCAL_DATE = B.LOCAL_DATE
        And A.TERMID = B.TERMID
        And A.Amount = B.Amount
        And Decode(A.ACQUIRER,null,0,A.ACQUIRER) = B.ACQUIRER
    )
    WHEN NOT MATCHED THEN 
        INSERT(TRACE, LOCAL_TIME, LOCAL_DATE, TERMID, RUNTIME, MODULE,AMOUNT,ACQUIRER)
        Values(B.TRACE, B.LOCAL_TIME, B.LOCAL_DATE, trim(B.TERMID), Sysdate, 'BACKEND_DOUBLE',B.AMOUNT,B.ACQUIRER)
        ;
        
    num := SQL%ROWCOUNT;
        
    commit;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'0','MERGE INTO ibft_double Success. Count: '||num|| ' rows','BACKEND_DOUBLE');
    commit;
    IF num <> 0 THEN
        
        OPEN cs;

        LOOP

            FETCH cs INTO dt;
            EXIT WHEN cs%NOTFOUND;

            Select sum(SLGD) Into iNumEnd 
            From
            (
                Select /*+ index (shclog SHCLOG_OTRACE2) */  Count(*) as SLGD    -- 2 gd trung la giao dich IBFT
                From Shclog
                Where Local_Date = dt.Local_Date
                And Origtrace = dt.Trace
                And Local_Time = dt.Local_Time
                And trim(TERMID) = dt.TERMID
                And ACQUIRER = dt.ACQUIRER
                And Amount = dt.Amount
                And SUBSTR(TRIM(TO_CHAR(PCODE, '099999')), 1, 2) In ('42','91')
                Union all
                Select /*+ index (Shclog_sett_ibft SHCLOG_OTRACE2_SETT_ibft) */  Count(*) as SLGD    -- 2 gd trung la giao dich IBFT
                From Shclog_sett_ibft
                Where Local_Date = dt.Local_Date
                And Origtrace = dt.Trace
                And Local_Time = dt.Local_Time
                And trim(TERMID) = dt.TERMID
                And ACQUIRER = dt.ACQUIRER
                And Amount = dt.Amount
                And SUBSTR(TRIM(TO_CHAR(PCODE, '099999')), 1, 2) In ('42','91')
            );
            

            If (iNumEnd >= 2) Then
                
                Select sum(SLGD) Into iNumEnd
                From
                (
                    Select count(*) as SLGD
                    From
                    (
                        Select PAN,BB_BIN,TOACC
                        From  
                        (
                            Select /*+ index (shclog SHCLOG_OTRACE2) */  Trim(PAN) as PAN, BB_BIN, TRIM(SUBSTR(ACCTNUM,INSTR(ACCTNUM || '|','|') + 1, LENGTH(ACCTNUM))) TOACC
                            From Shclog
                            Where Local_Date = dt.Local_Date
                            And Origtrace = dt.Trace
                            And Local_Time = dt.Local_Time
                            And trim(TERMID) = dt.TERMID
                            And ACQUIRER = dt.ACQUIRER
                            And Amount = dt.Amount
                            And SUBSTR(TRIM(TO_CHAR(PCODE, '099999')), 1, 2) In ('42','91')
                            Group By Trim(PAN), BB_BIN, TRIM(SUBSTR(ACCTNUM,INSTR(ACCTNUM || '|','|') + 1, LENGTH(ACCTNUM)))
                            Union all
                            Select /*+ index (Shclog_sett_ibft SHCLOG_OTRACE2_SETT_ibft) */  Trim(PAN) As PAN, BB_BIN, TRIM(SUBSTR(ACCTNUM,INSTR(ACCTNUM || '|','|') + 1, LENGTH(ACCTNUM))) TOACC
                            From Shclog_sett_ibft
                            Where Local_Date = dt.Local_Date
                            And Origtrace = dt.Trace
                            And Local_Time = dt.Local_Time
                            And trim(TERMID) = dt.TERMID
                            And ACQUIRER = dt.ACQUIRER
                            And Amount = dt.Amount
                            And SUBSTR(TRIM(TO_CHAR(PCODE, '099999')), 1, 2) In ('42','91')
                            Group By Trim(PAN), BB_BIN, TRIM(SUBSTR(ACCTNUM,INSTR(ACCTNUM || '|','|') + 1, LENGTH(ACCTNUM)))
                        )
                        Group by PAN,BB_BIN,TOACC    
                        )                   
                );

                If (iNumEnd = 1) Then   -- Giao dich trung nhau nguyen nhan do khac F15 giua 200 vA  210 -> Loai giao dich moi nhat
                        
                    Select Max(STT) Into istt
                    From
                    (
                        Select /*+ index (Shclog_sett_ibft SHCLOG_OTRACE2_SETT_ibft) */  STT                
                        From Shclog_sett_ibft
                        Where Local_Date = dt.Local_Date
                        And Origtrace = dt.Trace
                        And Local_Time = dt.Local_Time
                        And trim(TERMID) = dt.TERMID
                        And ACQUIRER = dt.ACQUIRER
                        And Amount = dt.Amount
                        And SUBSTR(TRIM(TO_CHAR(PCODE, '099999')), 1, 2) In ('42','91')
                    );
                    
                    Insert Into SHCLOG_BLE_AUTO(SETTLEMENT_RATE, ISS_CONV_RATE, F49, F5, F4, F6, F15, TCC, FEE_IRF_ISS, FEE_SVF_ISS, FEE_IRF_ACQ, 
                        FEE_SVF_ACQ, FEE_IRF_PAY_AT, FEE_SVF_PAY_AT, FEE_IRF_REC_AT, FEE_SVF_REC_AT, FEE_IRF_BEN, FEE_SVF_BEN, 
                        TOKEN, RC, SETTLEMENT_CODE, ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV, 
                        TERMID1, MSGTYPE, PAN, PCODE, AMOUNT, ACQ_CURRENCY_CODE, FEE, NEW_FEE, SETTLEMENT_AMOUNT, TRACE, 
                        LOCAL_TIME,  LOCAL_DATE, SETTLEMENT_DATE, ACQUIRER, ISSUER, RESPCODE, MERCHANT_TYPE, AUTHNUM, 
                        CH_CURRENCY_CODE, TERMID, REFNUM, ACCTNUM1, CARD_SEQNO, ISS_CURRENCY_CODE, CHIP_INDEX, TRANDATE, TRANTIME, 
                        CARDPRODUCT,  REVCODE, ORIGTRACE, ACCEPTORNAME, TERMLOC, LOAIGDREVESO, THAYDOI, CONFIG_FEE_ID, TGTP, 
                        TGGUIGD,  TGGUIQT, TGDDNV, TGXLNV, REAMOUNT, RAMOUNT, QAMOUNT, LDDNV, FAMOUNT, TGGUINV, TGGUIQTP, EDIT_DATE, 
                        EDIT_USER,  SML_VERIFY, ORIGISS, ORIGRESPCODE, STT, ISREV, PREAMOUNT, CAP_DATE, FEE_ISS, FEE_ACQ, INS_PCODE, 
                        CONV_RATE,  FEE_REC_AT, FEE_PAY_AT, FEE_REC_DF, FEE_PAY_DF, EDIT_DATE_INS, ENTITYID, TRANSACTION_AMOUNT, 
                        CARDHOLDER_AMOUNT, CARDHOLDER_CONV_RATE, BB_BIN, FORWARD_INST, TRANSFEREE, SETT_CURRENCY_CODE, PRE_CARDHOLDER_AMOUNT, 
                        REPAY_USD, CONV_RATE_ACQ, TERMID_ACQ, SHCERROR, MERCHANT_TYPE_ORIG, BB_ACCOUNT, FEE_SERVICE, SENDER_ACC, 
                        BNB_ACC, SENDER_SWC, BNB_SWC, CONTENT_FUND, RESPCODE_GW, ACCTNUM, FROM_SML, ORIGINATOR, ORIG_ACQ, FEE_NOTE, 
                        ONLY_SML, ACQUIRER_FE, ACQUIRER_RP, ISSUER_FE, ISSUER_RP, FEE_KEY, ACQ_RQ, ISS_RQ, FROM_SYS, PCODE2, 
                        BB_BIN_ORIG, TXNSRC, TXNDEST, SRC, DES, TRAN_CASE, PCODE_ORIG, RC_ISS_72, RC_ACQ_72, RC_ISS, RC_BEN, 
                        RC_ACQ, NAPAS_DATE, NAPAS_EDIT_DATE, NAPAS_EDIT_DATE_INS, NAPAS_ND_DATE, REASON_EDIT)
                    Select SETTLEMENT_RATE, ISS_CONV_RATE, F49, F5, F4, F6, F15, TCC, FEE_IRF_ISS, FEE_SVF_ISS, FEE_IRF_ACQ, 
                        FEE_SVF_ACQ, FEE_IRF_PAY_AT, FEE_SVF_PAY_AT, FEE_IRF_REC_AT, FEE_SVF_REC_AT, FEE_IRF_BEN, FEE_SVF_BEN, 
                        TOKEN, RC, SETTLEMENT_CODE, ACQ_COUNTRY, POS_ENTRY_CODE, POS_CONDITION_CODE, ADDRESPONSE, MVV, 
                        TERMID1, MSGTYPE, PAN, PCODE, AMOUNT, ACQ_CURRENCY_CODE, FEE, NEW_FEE, SETTLEMENT_AMOUNT, TRACE, 
                        LOCAL_TIME,  LOCAL_DATE, SETTLEMENT_DATE, ACQUIRER, ISSUER, RESPCODE, MERCHANT_TYPE, AUTHNUM, 
                        CH_CURRENCY_CODE, TERMID, REFNUM, ACCTNUM1, CARD_SEQNO, ISS_CURRENCY_CODE, CHIP_INDEX, TRANDATE, TRANTIME, 
                        CARDPRODUCT,  REVCODE, ORIGTRACE, ACCEPTORNAME, TERMLOC, LOAIGDREVESO, THAYDOI, CONFIG_FEE_ID, TGTP, 
                        TGGUIGD,  TGGUIQT, TGDDNV, TGXLNV, REAMOUNT, RAMOUNT, QAMOUNT, LDDNV, FAMOUNT, TGGUINV, TGGUIQTP, EDIT_DATE, 
                        EDIT_USER,  SML_VERIFY, ORIGISS, ORIGRESPCODE, STT, ISREV, PREAMOUNT, CAP_DATE, FEE_ISS, FEE_ACQ, INS_PCODE, 
                        CONV_RATE,  FEE_REC_AT, FEE_PAY_AT, FEE_REC_DF, FEE_PAY_DF, EDIT_DATE_INS, ENTITYID, TRANSACTION_AMOUNT, 
                        CARDHOLDER_AMOUNT, CARDHOLDER_CONV_RATE, BB_BIN, FORWARD_INST, TRANSFEREE, SETT_CURRENCY_CODE, PRE_CARDHOLDER_AMOUNT, 
                        REPAY_USD, CONV_RATE_ACQ, TERMID_ACQ, SHCERROR, MERCHANT_TYPE_ORIG, BB_ACCOUNT, FEE_SERVICE, SENDER_ACC, 
                        BNB_ACC, SENDER_SWC, BNB_SWC, CONTENT_FUND, RESPCODE_GW, ACCTNUM, FROM_SML, ORIGINATOR, ORIG_ACQ, FEE_NOTE, 
                        ONLY_SML, ACQUIRER_FE, ACQUIRER_RP, ISSUER_FE, ISSUER_RP, FEE_KEY, ACQ_RQ, ISS_RQ, FROM_SYS, PCODE2, 
                        BB_BIN_ORIG, TXNSRC, TXNDEST, SRC, DES, TRAN_CASE, PCODE_ORIG, RC_ISS_72, RC_ACQ_72, RC_ISS, RC_BEN, 
                        RC_ACQ, NAPAS_DATE, NAPAS_EDIT_DATE, NAPAS_EDIT_DATE_INS, NAPAS_ND_DATE, To_Char(Sysdate,'yyyymmddhh24miss')        
                    From Shclog_sett_ibft
                    Where Stt = istt;                      
                    iUpdate := SQL%ROWCOUNT;
                    commit;
                    If (iUpdate = 1) Then                            
                        Delete
                        From Shclog_sett_ibft
                        Where Stt = istt;

                        iUpdate := SQL%ROWCOUNT;
                        commit;
                        If (iUpdate = 1) Then
                        
                            Update ibft_double
                            Set MODULE = 'BACKEND_DOUBLE-D'
                            Where  RUNTIME > Trunc(Sysdate)
                            And MODULE = 'BACKEND_DOUBLE'
                            And ACQUIRER = dt.ACQUIRER
                            And TRACE = dt.TRACE
                            And LOCAL_TIME = LOCAL_TIME
                            And LOCAL_DATE = LOCAL_DATE
                            And trim(TERMID) = dt.TERMID
                            And AMOUNT = dt.AMOUNT;
                            Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
                            Values(sysdate,'0','STEP 5 - ORIGTRACE ='||dt.Trace||'; Local_time='||dt.Local_Time,'BACKEND_DOUBLE');
                            commit;
                        End If;
                    End If;

                ElsIf (iNumEnd >= 2) Then    -- Khong phai giao dich trung, loai bo tu dong
                    Update ibft_double
                    Set MODULE = 'BACKEND_DOUBLE-I'
                    Where  RUNTIME > Trunc(Sysdate)
                    And MODULE = 'BACKEND_DOUBLE'
                    And ACQUIRER = dt.ACQUIRER
                    And TRACE = dt.TRACE
                    And LOCAL_TIME = LOCAL_TIME
                    And LOCAL_DATE = LOCAL_DATE
                    And trim(TERMID) = dt.TERMID
                    And AMOUNT = dt.AMOUNT;    

                End If;
                    
            End If;
                
            Commit;

        END LOOP;

        CLOSE cs;
        Commit;
              
        Select Count(*) Into iNumEnd
        From Ibft_Double
        Where  RUNTIME > Trunc(Sysdate)
        And MODULE = 'BACKEND_DOUBLE';
            
        vDetail := ' Co '||num||' gd trung TT, da loai bo tu dong: '||(num-iNumEnd)||' giao dich, vui long kiem tra';
            
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'0',vDetail,'BACKEND_DOUBLE-C');
        commit;
        If (iNumEnd = 0) Then
            Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
            Values(sysdate,'0','OK','BACKEND_DOUBLE');
            commit;
        Else
            Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE,CRITICAL)
            Values(sysdate,'-1','Co GD chua duoc loai tu dong. De nghi check','BACKEND_DOUBLE_ALERT',2);
            commit;
        End If;
            
        SEND_SMS('BACKEND_DOUBLE_IBFT#0366155501;0988766330#'||vDetail);
            
    Else
        vDetail := ' Khong co giao dich nao trung thong tin tren backend';
            
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'0','OK','BACKEND_DOUBLE');
        commit;
    END IF;
    CHECK_BACKEND_DOUBLE_KTC();

    Utl_Mail.send(vSender,vReceiver,vCC,bCC,vSub,vDetail,vMType,NULL); --  bo gui  mail 05/08
EXCEPTION 
    WHEN OTHERS THEN
    
    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' CHECK_BACKEND_DOUBLE Err num: ' || TO_CHAR(ecode) || ' - Err detail: ' || emesg;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,ecode,emesg,'BACKEND');
    
    commit;
    
End;
/
