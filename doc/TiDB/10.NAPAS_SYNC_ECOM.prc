CREATE OR REPLACE PROCEDURE RPT.NAPAS_SYNC_ECOM
    (
      i integer
    )
AS

    -- Writen by    :   Hoind
    -- Date create  :   2019-03-13
    -- Description  :   Dong bo du lieu QR ECOM tu phong TTDT

    iFound INTEGER := 0 ;
    
    iUpdate INTEGER := 0 ;
    
    imaxstt INTEGER := 0 ;
    
    iStt INTEGER := 0 ;
    
    iSttShclog INTEGER := 0 ;
    
    iQuyetToan INTEGER:= 0;
    
    iRespcode INTEGER:= 0;
    iLanDieuChinh INTEGER:= 0;
    
    iTC INTEGER:= 0;
    iHT_TP INTEGER:= 0; 
    iHT_MP_LAN_DAU INTEGER:= 0;
    iHT_MP_DC INTEGER:= 0; 
    iTong_HT NUMBER(28,8);
    iAmount_Gd NUMBER(28,8);
    
    ecode NUMBER;
    emesg VARCHAR2(200);
    vMType  VARCHAR2(30) := 'text/plain; charset=us-ascii';
    vSender VARCHAR2(40) := 'db_baocao@napas.com.vn';
    vReceiver VARCHAR2(30) := 'hoind@napas.com.vn';
    vCC VARCHAR2(70) := 'doantien@napas.com.vn';
    bCC VARCHAR2(100) := 'sondt@napas.com.vn';
    ipaddr VARCHAR2(20); -- ip host db
    vSub VARCHAR2(150):= 'Sync QR ECOM' ;
    vDetail VARCHAR2(500) := 'Nothing ! ';
    
    vKeyTime VARCHAR2(50) := To_Char(Sysdate,'yyyyMMddhh24miss');
    
    iPos Integer;
    iCount integer;
    Cursor cs IS
        Select STT, PAN, Origtrace, Local_Time, Local_Date, Acquirer, TermID
        From SHCLOG_SETT_IBFT
        Where Msgtype = 210
        And Settlement_Date = Trunc(Sysdate-1)
        And Pcode2 = 890000
        And BB_BIN = GET_ECOM_ID()
        And Respcode = 68;
    
    dtTimeOut cs%ROWTYPE;
    
    Cursor csFullRefund IS
        Select STT, REFUND_AMOUNT, CARD_NUMBER_DETAIL, F11_TRACE, F12_Local_time, 
            Case 
                When To_Date(To_Char(Sysdate,'yyyy')||F13_Local_date,'yyyymmdd') > SysDate 
                Then add_months(To_Date(To_Char(Sysdate,'yyyy')||F13_Local_date,'yyyymmdd'),-12)
                Else
                    To_Date(To_Char(Sysdate,'yyyy')||F13_Local_date,'yyyymmdd')
            End F13_Local_date, F32_acquirer, F41_card_acceptor_id
        From ECOM_REFUND_ALL
        Where GET_DATE = Trunc(Sysdate)
        ;  
    
    dtFullRefund csFullRefund%ROWTYPE;
    
    Cursor csPartRefund IS
        Select F11_TRACE, F12_LOCAL_TIME, 
            Case 
                When To_Date(To_Char(Sysdate,'yyyy')||F13_Local_date,'yyyymmdd') > SysDate 
                Then add_months(To_Date(To_Char(Sysdate,'yyyy')||F13_Local_date,'yyyymmdd'),-12)
                Else
                    To_Date(To_Char(Sysdate,'yyyy')||F13_Local_date,'yyyymmdd')
            End F13_Local_date, LOCAL_DATE, F15_SETTLE_DATE, SETT_DATE, 
            F32_ACQUIRER, F41_CARD_ACCEPTOR_ID, CARD_NUMBER_DETAIL, TOTAL_REFUND_AMOUNT, STT, TOTAL_TRAN
        From ECOM_REFUND_GROUP
--        Where GET_DATE = Trunc(Sysdate)
        ;  
    
    dtPartRefund csPartRefund%ROWTYPE;
    
BEGIN
    /*
    Select count(*) into iCount
        from NP_EXEC_LOG
            where trunc(exec_date) = Trunc(Sysdate)
            And STT = '100100800300'
            And EX_ERR =0;
            
    If(iCount>0) Then
    */
        --Select Max(Stt) Into imaxstt From Shclog;
        imaxstt := 123456789;

        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'0','Start-MAX STT:'||imaxstt,'NAPAS_SYNC_ECOM');
        
        EXECUTE IMMEDIATE 'truncate table ECOM_REFUND';
        
        EXECUTE IMMEDIATE 'truncate table ECOM_REFUND_ALL';
        
        EXECUTE IMMEDIATE 'truncate table ECOM_REFUND_GROUP';
        
        EXECUTE IMMEDIATE 'truncate table ECOM_SUCCESS';
        
        Commit;
        
        iPos := 1;
        
        ----    Get new data from ECOM     Transaction Success     ----------------
        
        MERGE INTO ECOM_SUCCESS A
        USING
            (
                Select *
                From ecom.VIEW_QRECOM_SALE@QR_ECOM_STAGING_NEW
                Where Transaction_type ='QR_PUSH'
            ) B
        On  
            (
                A.CARD_NUMBER_DETAIL = B.CARD_NUMBER_DETAIL
                And
                A.F11_TRACE = B.F11_TRACE
                And
                A.F12_Local_time = B.F12_Local_time
                And
                A.F13_Local_date = B.F13_Local_date
                And 
                A.F32_acquirer = B.F32_acquirer
                And
                A.F41_card_acceptor_id = B.F41_card_acceptor_id
            )      
        WHEN NOT MATCHED THEN
            Insert ( TRANSACTION_CODE,  AMOUNT, SETTLEMENT_AMOUNT, CURRENCY, ISSUER_BANK_CODE, ISSUER_BANK_NAME, CARD_NUMBER, CARD_NUMBER_DETAIL,
                CARD_HOLDER_NAME, CARD_EXPIRED_DATE, AUTHORISATION_CODE, ORDER_ID, ORDER_DATE, ORDER_INFO, MERCHANT_TRANSACTION_CODE, MERCHANT_CODE, MERCHANT_NAME,
                MERCHANT_ID, TRANSACTION_TYPE, RESPONSE_CODE, MCC_ID, TRANSACTION_STATUS, INTEGRATION_TYPE, FAST_PAY, F11_TRACE, F12_Local_time, F13_Local_date, F15_Settle_DATE,
                F32_acquirer ,F41_card_acceptor_id,F63_TRANS_SWITCH,SWITCH_STATUS, GET_DATE, KEY_TIME)
            Values
                (B.TRANSACTION_CODE, B.AMOUNT, B.SETTLEMENT_AMOUNT, B.CURRENCY, B.ISSUER_BANK_CODE, B.ISSUER_BANK_NAME, B.CARD_NUMBER, B.CARD_NUMBER_DETAIL,
            B.CARD_HOLDER_NAME, B.CARD_EXPIRED_DATE, B.AUTHORISATION_CODE, B.ORDER_ID, B.ORDER_DATE, B.ORDER_INFO, B.MERCHANT_TRANSACTION_CODE, B.MERCHANT_CODE, B.MERCHANT_NAME,
            B.MERCHANT_ID, B.TRANSACTION_TYPE, B.RESPONSE_CODE, B.MCC_ID, B.TRANSACTION_STATUS, B.INTEGRATION_TYPE, B.FAST_PAY, B.F11_TRACE, B.F12_Local_time, B.F13_Local_date, B.F15_Settle_DATE,
            B.F32_acquirer ,B.F41_card_acceptor_id,B.F63_TRANS_SWITCH,B.SWITCH_STATUS, Trunc(Sysdate), vKeyTime);
            
        ----    Get new data from ECOM     Partial Refund     ----------------
        
        iPos := 2;
        
        MERGE INTO ECOM_REFUND A
        USING
            (
                Select *
                From ecom.VIEW_QRECOM_REFUND_HALF@QR_ECOM_STAGING_NEW
                Where Tranx_type ='QR_PUSH'
                -- REFUND_AMOUNT: So tien refund
            ) B
        On  
            (
                A.CARD_NUMBER_DETAIL = B.CARD_NUMBER_DETAIL
                And
                A.F11_TRACE = B.F11_TRACE
                And
                A.F12_Local_time = B.F12_Local_time
                And
                A.F13_Local_date = B.F13_Local_date
                And 
                A.F32_acquirer = B.F32_acquirer
                And
                A.F41_card_acceptor_id = B.F41_card_acceptor_id
                And 
                A.REFUND_DATE = B.REFUND_DATE       -- Refund 1 phan them dieu kien refund_date de phan biet lan hoan tra 1 phan
            )      
        WHEN NOT MATCHED THEN
            Insert (REFUND_AMOUNT, REFUND_DATE, LOCAL_DATE, F15_SETTLE_DATE, SETT_DATE, F32_acquirer, F41_CARD_ACCEPTOR_ID, F63_TRANS_SWITCH, CARD_NUMBER, ISSUER_BANK_CODE, SWITCH_STATUS,
                SWITCH_SETTLE_DATE, ID, AMOUNT, USER_NAME, UPDATE_DATE, TRANSACTION_INFO,
                TRANSACTION_CODE, DESCRIPTION, STATUS_ID, TRANSACTION_TYPE, MERCHANT_RF_ID, F11_TRACE, F12_LOCAL_TIME, F13_LOCAL_DATE, LATE_REFUND , GET_DATE, KEY_TIME,
                REFUND_TRANSACTION_CODE,MERCHANT_TRANSACTION_CODE,STATUS_CODE,CARD_NUMBER_DETAIL,CARD_HOLDER_NAME)
            Values
                (B.REFUND_AMOUNT, B.REFUND_DATE, null, B.F15_SETTLE_DATE, null, B.F32_acquirer, B.F41_CARD_ACCEPTOR_ID, B.F63_TRANS_SWITCH, B.CARD_NUMBER, B.ISSUER_BANK_CODE, B.SWITCH_STATUS,
                Trunc(Sysdate), null, B.AMOUNT,B.USER_NAME, null, B.TRANSACTION_INFO,
                B.TRANSACTION_CODE, B.DESCRIPTION, B.STATUS_CODE, B.TRANSACTION_TYPE, B.MERCHANT_RF_ID, B.F11_TRACE, B.F12_LOCAL_TIME, B.F13_LOCAL_DATE ,0,Trunc(Sysdate), vKeyTime,
                B.REFUND_TRANSACTION_CODE,B.MERCHANT_TRANSACTION_CODE,B.STATUS_CODE,B.CARD_NUMBER_DETAIL,B.CARD_HOLDER_NAME);

        ------  Get late refund                           -----------------
        
        iPos := 3;
        
        Insert Into ECOM_REFUND(REFUND_AMOUNT, REFUND_DATE, LOCAL_DATE, F15_SETTLE_DATE, SETT_DATE, F32_acquirer, F41_CARD_ACCEPTOR_ID, F63_TRANS_SWITCH, CARD_NUMBER, ISSUER_BANK_CODE, SWITCH_STATUS,
                SWITCH_SETTLE_DATE, ID, AMOUNT, USER_NAME, UPDATE_DATE, TRANSACTION_INFO,
                TRANSACTION_CODE, DESCRIPTION, STATUS_ID, TRANSACTION_TYPE, MERCHANT_RF_ID, F11_TRACE, F12_LOCAL_TIME, F13_LOCAL_DATE, LATE_REFUND, GET_DATE, KEY_TIME,
                REFUND_TRANSACTION_CODE,MERCHANT_TRANSACTION_CODE,STATUS_CODE,CARD_NUMBER_DETAIL,CARD_HOLDER_NAME)
        Select REFUND_AMOUNT, REFUND_DATE, LOCAL_DATE, F15_SETTLE_DATE, SETT_DATE, F32_acquirer, F41_CARD_ACCEPTOR_ID, F63_TRANS_SWITCH, CARD_NUMBER, ISSUER_BANK_CODE, SWITCH_STATUS,
                Trunc(Sysdate), ID, AMOUNT, USER_NAME, UPDATE_DATE, TRANSACTION_INFO,
                TRANSACTION_CODE, DESCRIPTION, STATUS_ID, TRANSACTION_TYPE, MERCHANT_RF_ID, F11_TRACE, F12_LOCAL_TIME, F13_LOCAL_DATE, 1, GET_DATE, vKeyTime,
                REFUND_TRANSACTION_CODE,MERCHANT_TRANSACTION_CODE,STATUS_CODE,CARD_NUMBER_DETAIL,CARD_HOLDER_NAME
        From ECOM_REFUND_LATE            
        Where IS_SYNC = 0;
        
        Update ECOM_REFUND_LATE
        Set IS_SYNC = 1, LAST_SYNC = Sysdate
        Where IS_SYNC = 0;
        
        ----    Get new data from ECOM     Full Refund     ----------------
        
        iPos := 4;
        
        MERGE INTO ECOM_REFUND_ALL A
        USING
            (
                Select *
                From ecom.VIEW_QRECOM_REFUND_FULL@QR_ECOM_STAGING_NEW
                Where Tranx_type ='QR_PUSH'
                -- REFUND_AMOUNT: So tien refund
            ) B
        On  
            (
                A.CARD_NUMBER_DETAIL = B.CARD_NUMBER_DETAIL
                And
                A.F11_TRACE = B.F11_TRACE
                And
                A.F12_Local_time = B.F12_Local_time
                And
                A.F13_Local_date = B.F13_Local_date
                And 
                A.F32_acquirer = B.F32_acquirer
                And
                A.F41_card_acceptor_id = B.F41_card_acceptor_id
            )      
        WHEN NOT MATCHED THEN
            Insert (REFUND_AMOUNT, REFUND_DATE, LOCAL_DATE, F15_SETTLE_DATE, SETT_DATE, F32_acquirer, F41_CARD_ACCEPTOR_ID, F63_TRANS_SWITCH, CARD_NUMBER, ISSUER_BANK_CODE, SWITCH_STATUS,
                SWITCH_SETTLE_DATE, AMOUNT, USER_NAME, UPDATE_DATE, TRANSACTION_INFO,
                TRANSACTION_CODE, DESCRIPTION, STATUS_ID, TRANSACTION_TYPE, MERCHANT_RF_ID, F11_TRACE, F12_LOCAL_TIME, F13_LOCAL_DATE, GET_DATE, KEY_TIME,
                REFUND_TRANSACTION_CODE,MERCHANT_TRANSACTION_CODE,STATUS_CODE,CARD_NUMBER_DETAIL,CARD_HOLDER_NAME)
            Values
                (B.REFUND_AMOUNT, B.REFUND_DATE, null, B.F15_SETTLE_DATE, null, B.F32_acquirer, B.F41_CARD_ACCEPTOR_ID, B.F63_TRANS_SWITCH, B.CARD_NUMBER, B.ISSUER_BANK_CODE, B.SWITCH_STATUS,
                Trunc(Sysdate), B.AMOUNT, B.USER_NAME, null, B.TRANSACTION_INFO,
                B.TRANSACTION_CODE, B.DESCRIPTION, B.STATUS_CODE, B.TRANSACTION_TYPE, B.MERCHANT_RF_ID, B.F11_TRACE, B.F12_LOCAL_TIME, B.F13_LOCAL_DATE, Trunc(Sysdate), vKeyTime,
                B.REFUND_TRANSACTION_CODE,B.MERCHANT_TRANSACTION_CODE,B.STATUS_CODE,B.CARD_NUMBER_DETAIL,B.CARD_HOLDER_NAME);
                
        ----    Update timeout transaction follow BEN ECOM     ----------------
        
        iPos := 5;
        
        OPEN cs;

            LOOP

                FETCH cs INTO dtTimeOut;
                EXIT WHEN cs%NOTFOUND;
                
                
                iStt := dtTimeOut.STT;
                
                Select Count(*) Into iFound
                From ECOM_SUCCESS A
                Where Trim(A.CARD_NUMBER_DETAIL) = Trim(dtTimeOut.PAN)
                And
                To_Number(Trim(A.F11_TRACE)) = dtTimeOut.Origtrace
                And
                To_Number(Trim(A.F12_Local_time)) = dtTimeOut.Local_time
                And
                A.F13_Local_date = To_Char(dtTimeOut.Local_date,'mmdd')
                And 
                Trim(A.F32_acquirer) = dtTimeOut.Acquirer
                And
                Trim(A.F41_card_acceptor_id) = Trim(dtTimeOut.TermId);
                
                If (iFound = 1) Then
                
                    Update SHCLOG_SETT_IBFT
                    Set Respcode = 0, Reason_Edit = vKeyTime||'- Update Rc 68 -> 0 follow Ecom'
                    Where Stt = dtTimeOut.Stt;
                    Commit;
                    NAPAS_CALCU_FEE_QRECOM(dtTimeOut.Stt);

                Else
                                    
                    Update SHCLOG_SETT_IBFT
                    Set Respcode = 5, Reason_Edit = vKeyTime||'- Found: '||iFound||' tran from Ecom Success'
                    Where Stt = dtTimeOut.Stt;
                    
                End If;
                
                Commit;
                
            END LOOP;

        CLOSE cs;
        Commit;

        ----    Update Full Refund transaction follow BEN ECOM     ----------------    
        iPos := 6;
        
        OPEN csFullRefund;

            LOOP

                FETCH csFullRefund INTO dtFullRefund;
                EXIT WHEN csFullRefund%NOTFOUND;
                
                iStt := dtFullRefund.STT;            
                -- Giao dich co refund khac phien
                
                Update Shclog
                    Set Respcode = 115, Edit_Date = Sysdate - 1, Edit_User = 'ECOM', Reason_Edit = vKeyTime||' - refund from Ecom'
                Where Msgtype = 210
                And Local_Date= dtFullRefund.F13_Local_date
                And Trim(PAN) = Trim(dtFullRefund.CARD_NUMBER_DETAIL)
                And Origtrace = to_number(trim(dtFullRefund.F11_TRACE))
                And Local_time = to_number(trim(dtFullRefund.F12_Local_time))
                And Acquirer = To_Number(trim(dtFullRefund.F32_acquirer))
                And Trim(TermId) = Trim(dtFullRefund.F41_card_acceptor_id)
                And Amount = dtFullRefund.REFUND_AMOUNT
                And TGGUIQT Is Not Null     -- Giao dich da duoc quyet toan
                And Respcode = 0
                --And Fee_Note Is Not Null
                And BB_BIN = GET_ECOM_ID()
                And Pcode2 = 890000;
                    
                iFound := SQL%ROWCOUNT;
                
                -- dbms_output.put_line('test du lieu'||dtFullRefund.CARD_NUMBER_DETAIL);
                
                If (iFound = 0) Then
                
                    -- Giao dich co refund cung phien voi giao dich goc
                
                    Update SHCLOG_SETT_IBFT
                    Set Respcode = 1, Isrev = 420, FEE_KEY = null, FEE_ISS = null, FEE_ACQ = null, FEE_PAY_AT = null, FEE_REC_AT = null,
                        FEE_IRF_ISS = null, FEE_SVF_ISS = null, FEE_IRF_ACQ = null, FEE_SVF_ACQ = null,                    
                        FEE_IRF_BEN = null, FEE_SVF_BEN = null, FEE_NOTE = null, Reason_Edit = vKeyTime||' - Remove Ecom Succ because have refund same day from Ecom'
                    Where Msgtype = 210
                    And Local_Date= dtFullRefund.F13_Local_date
                    And Trim(PAN) = Trim(dtFullRefund.CARD_NUMBER_DETAIL)
                    And Origtrace = to_number(trim(dtFullRefund.F11_TRACE))
                    And Local_time = to_number(trim(dtFullRefund.F12_Local_time))
                    And Acquirer = To_Number(trim(dtFullRefund.F32_acquirer))
                    And Trim(TermId) = Trim(dtFullRefund.F41_card_acceptor_id)
                    And Amount = dtFullRefund.REFUND_AMOUNT
                    And TGGUIQT Is Null
                    And Respcode = 0
                    --And Fee_Note Is Not Null
                    And BB_BIN = GET_ECOM_ID()
                    And Pcode2 = 890000;
                    
                    iFound := SQL%ROWCOUNT;
                    
                    If (iFound = 1) Then
                    
                        Update ECOM_REFUND_ALL
                        Set SWITCH_STATUS = '01', SYNC_DATE = Sysdate   -- Giao dich co refund cung phien
                        Where Stt = dtFullRefund.Stt;
                        
                    ElsIf (iFound = 0) Then
                    
                        Update ECOM_REFUND_ALL
                        Set SWITCH_STATUS = '03', SYNC_DATE = Sysdate   -- Khong update duoc giao dich hoac giao dich goc khong thanh cong
                        Where Stt = dtFullRefund.Stt;
                        
                    Else
                    
                        Update ECOM_REFUND_ALL
                        Set SWITCH_STATUS = '04', SYNC_DATE = Sysdate   -- Nhieu hon 1 giao dich    -> Warning
                        Where Stt = dtFullRefund.Stt;  
                                               
                    End If;
                    
                ElsIf (iFound = 1) Then
                    
                    Update ECOM_REFUND_ALL
                    Set SWITCH_STATUS = '00', SYNC_DATE = Sysdate   -- Giao dich co refund khac phien
                    Where Stt = dtFullRefund.Stt;
                    
                Else
                
                    Update ECOM_REFUND_ALL
                    Set SWITCH_STATUS = '04', SYNC_DATE = Sysdate   -- Nhieu hon 1 giao dich
                    Where Stt = dtFullRefund.Stt; 
                    
                End If;
                
                Commit;
                
            END LOOP;

        CLOSE csFullRefund;
        Commit;

        ----    Update Refund 1 phan follow BEN ECOM     ----------------    
        iPos := 7;
        
            --------- Group part refund     --------------
        
        Insert Into ECOM_REFUND_GROUP(F11_TRACE, F12_LOCAL_TIME, F13_LOCAL_DATE, F15_SETTLE_DATE, F32_ACQUIRER, 
            F41_CARD_ACCEPTOR_ID, CARD_NUMBER, TOTAL_REFUND_AMOUNT, TOTAL_TRAN, KEY_TIME,CARD_NUMBER_DETAIL)        
        Select F11_TRACE, F12_LOCAL_TIME, F13_LOCAL_DATE, F15_SETTLE_DATE, F32_ACQUIRER, 
            F41_CARD_ACCEPTOR_ID, CARD_NUMBER, Sum(REFUND_AMOUNT), Count(*) As TOTAL_TRAN, vKeyTime,CARD_NUMBER_DETAIL
        From ECOM_REFUND
        Group By F11_TRACE, F12_LOCAL_TIME, F13_LOCAL_DATE, F15_SETTLE_DATE, F32_ACQUIRER, 
            F41_CARD_ACCEPTOR_ID, CARD_NUMBER,CARD_NUMBER_DETAIL
        ;        
        
        iPos := 8;
        
        OPEN csPartRefund;

            LOOP
            
       
                FETCH csPartRefund INTO dtPartRefund;
                EXIT WHEN csPartRefund%NOTFOUND;
                
                dbms_output.put_line('TRACE: '||dtPartRefund.F11_TRACE);
                
                iPos := 9;
                
                Select NVL(Sum(Case When Respcode In (0,110) Then 1 Else 0 End),0) As TC, 
                    NVL(Sum(Case When Respcode In (113,115) And Fee_Note Not Like '%HACH TOAN DIEU CHINH%' Then 1 Else 0 End),0) As HT_TP,
                    NVL(Sum(Case When Respcode In (112,114) And Fee_Note Not Like '%HACH TOAN DIEU CHINH%' Then 1 Else 0 End),0) As HT_MP_LAN_DAU,
                    NVL(Sum(Case When Respcode In (112,114) And Fee_Note Like '%HACH TOAN DIEU CHINH%' Then 1 Else 0 End),0) As HT_MP_DC,
                    NVL(Sum(Case When Respcode In (113,115) Then Amount When Respcode In (112,114) Then PreAmount Else 0 End),0) As Tong_HT_HC,
                    NVL(Sum(Case When TGGUIQT Is Null Then 0 Else 1 End),0),
                    NVL(Sum(Case When Respcode In (0,110,112,113,114,115) And Fee_Note Not Like '%HACH TOAN DIEU CHINH%' Then Amount Else 0 End),0) As AmountSale,
                    NVL(Sum(Case When Fee_Note Like '%HACH TOAN DIEU CHINH%' Then 1 Else 0 End),0) As LanDieuChinh
                    
                    Into iTC, iHT_TP, iHT_MP_LAN_DAU, iHT_MP_DC, iTong_HT, iQuyetToan, iAmount_Gd, iLanDieuChinh
                     
                From 
                (
                    Select Respcode, Fee_Note, Amount, PreAmount, TGGUIQT
                    From Shclog
                    Where Msgtype = 210
                    And Local_Date = dtPartRefund.F13_Local_date
                    And Trim(PAN) = Trim(dtPartRefund.CARD_NUMBER_DETAIL)
                    And Origtrace = to_number(trim(dtPartRefund.F11_TRACE))
                    And Local_time = to_number(trim(dtPartRefund.F12_Local_time))
                    And Acquirer = To_Number(trim(dtPartRefund.F32_acquirer))
                    And Trim(TermId) = Trim(dtPartRefund.F41_card_acceptor_id)
                    And BB_BIN = GET_ECOM_ID()
                    And Fee_Note Is Not Null
                    And Pcode2=890000
                    Union
                    Select Respcode, Fee_Note, Amount, PreAmount, TGGUIQT
                    From SHCLOG_SETT_IBFT
                    Where Msgtype = 210
                    And Local_Date = dtPartRefund.F13_Local_date
                    And Trim(PAN) = Trim(dtPartRefund.CARD_NUMBER_DETAIL)
                    And Origtrace = to_number(trim(dtPartRefund.F11_TRACE))
                    And Local_time = to_number(trim(dtPartRefund.F12_Local_time))
                    And Acquirer = To_Number(trim(dtPartRefund.F32_acquirer))
                    And Trim(TermId) = Trim(dtPartRefund.F41_card_acceptor_id)
                    And BB_BIN = GET_ECOM_ID()
                    --And Fee_Note Is Not Null
                    And Pcode2=890000
                );
                dbms_output.put_line('TRACE: '||dtPartRefund.F11_TRACE);
                dbms_output.put_line('STT: '||iSttShclog);
                
                -------- List lai log sau khi lam xong  ---------------------
                
                If (iTC = 0 And iHT_TP = 0 And iHT_MP_LAN_DAU = 0 And iHT_MP_DC = 0 And iTong_HT = 0) Then   
                
                    iPos := 10;
                    -- Khong ton tai giao dich tai backend

                    Update ECOM_REFUND_GROUP
                    Set IS_ERROR = 1
                    Where STT = dtPartRefund.STT;
                    
                    Update ECOM_REFUND
                    Set SWITCH_STATUS = '03', SYNC_DATE = Sysdate
                    Where CARD_NUMBER_DETAIL = dtPartRefund.CARD_NUMBER_DETAIL
                        And
                        F11_TRACE = dtPartRefund.F11_TRACE
                        And
                        F12_Local_time = dtPartRefund.F12_Local_time
                        And
                        F13_Local_date = To_Char(dtPartRefund.F13_Local_date,'mmdd')
                        And 
                        F32_acquirer = dtPartRefund.F32_acquirer
                        And
                        F41_card_acceptor_id = dtPartRefund.F41_card_acceptor_id;
                    
                ElsIf (iTC = 1 And iQuyetToan = 0) Then -- Giao dich chua quyet toan -> bang SHCLOG_SETT_IBFT
                    
                    iPos := 11;
                    
                    Select Stt Into iSttShclog
                    --From Shclog   -- Chuyen nguon giao dich thanh cong
                    From SHCLOG_SETT_IBFT
                    Where Msgtype = 210
                    And Local_Date = dtPartRefund.F13_Local_date
                    And Trim(PAN) = Trim(dtPartRefund.CARD_NUMBER_DETAIL)
                    And Origtrace = to_number(trim(dtPartRefund.F11_TRACE))
                    And Local_time = to_number(trim(dtPartRefund.F12_Local_time))
                    And Acquirer = To_Number(trim(dtPartRefund.F32_acquirer))
                    And Trim(TermId) = Trim(dtPartRefund.F41_card_acceptor_id)
                    And BB_BIN = GET_ECOM_ID()
                    --And Fee_Note Is Not Null
                    And Pcode2=890000;
                
                    If (iHT_TP = 0 And iHT_MP_LAN_DAU = 0 And iAmount_Gd = dtPartRefund.TOTAL_REFUND_AMOUNT ) Then
                
                        -- Giao dich sale cung phien, tong refund 1 phan nhieu lan cung phien = tong giao dich -> Bo giao dich khong quyet toan
                        
                        iPos := 12;
                        
                        Update SHCLOG_SETT_IBFT
                        Set Respcode = 1, Isrev = 420, Reason_Edit = vKeyTime||' - Remove gd Sale vi co nhieu refund 1 phan = sale',
                            FEE_KEY = null, FEE_ISS = null, FEE_ACQ = null, FEE_PAY_AT = null, FEE_REC_AT = null,
                            FEE_IRF_ISS = null, FEE_SVF_ISS = null, FEE_IRF_ACQ = null, FEE_SVF_ACQ = null,                    
                            FEE_IRF_BEN = null, FEE_SVF_BEN = null, FEE_NOTE = null
                        Where Stt = iSttShclog;  
                        
                        Update ECOM_REFUND_GROUP
                        Set IS_ERROR = 0
                        Where STT = dtPartRefund.STT;
                        
                        Update ECOM_REFUND
                        Set SWITCH_STATUS = '01', SYNC_DATE = Sysdate
                        Where CARD_NUMBER_DETAIL = dtPartRefund.CARD_NUMBER_DETAIL
                        And
                        F11_TRACE = dtPartRefund.F11_TRACE
                        And
                        F12_Local_time = dtPartRefund.F12_Local_time
                        And
                        F13_Local_date = To_Char(dtPartRefund.F13_Local_date,'mmdd')
                        And 
                        F32_acquirer = dtPartRefund.F32_acquirer
                        And
                        F41_card_acceptor_id = dtPartRefund.F41_card_acceptor_id;
                        
                                                                        
                    ElsIf (iHT_TP = 0 And iHT_MP_LAN_DAU = 0 And iAmount_Gd > dtPartRefund.TOTAL_REFUND_AMOUNT) Then
                  
                        -- Giao dich tr?ng thái RC = 0, chua quyet toan -> tong refund 1 lan < sale, Quyet toan giao dich sale, chuyen refund 1 phan sang phien sau
                        
                        iPos := 13;
                        
                        Insert Into ECOM_REFUND_LATE(REFUND_AMOUNT, REFUND_DATE, LOCAL_DATE, F15_SETTLE_DATE, SETT_DATE, F32_acquirer, F41_CARD_ACCEPTOR_ID, F63_TRANS_SWITCH, CARD_NUMBER, ISSUER_BANK_CODE, SWITCH_STATUS,
                                    SWITCH_SETTLE_DATE, AMOUNT, USER_NAME, UPDATE_DATE, TRANSACTION_INFO,
                                    TRANSACTION_CODE, DESCRIPTION, STATUS_ID, TRANSACTION_TYPE, MERCHANT_RF_ID, F11_TRACE, F12_LOCAL_TIME, F13_LOCAL_DATE, GET_DATE, KEY_TIME,SYNC_DATE,IS_SYNC,CARD_NUMBER_DETAIL, REFUND_TRANSACTION_CODE)
                        Select REFUND_AMOUNT, REFUND_DATE, LOCAL_DATE, F15_SETTLE_DATE, SETT_DATE, F32_acquirer, F41_CARD_ACCEPTOR_ID, F63_TRANS_SWITCH, CARD_NUMBER, ISSUER_BANK_CODE, SWITCH_STATUS,
                                    SWITCH_SETTLE_DATE, AMOUNT, USER_NAME, UPDATE_DATE, TRANSACTION_INFO,
                                    TRANSACTION_CODE, DESCRIPTION, STATUS_ID, TRANSACTION_TYPE, MERCHANT_RF_ID, F11_TRACE, F12_LOCAL_TIME, F13_LOCAL_DATE, Sysdate, KEY_TIME, Sysdate, 0,CARD_NUMBER_DETAIL, REFUND_TRANSACTION_CODE
                        From ECOM_REFUND
                        Where CARD_NUMBER_DETAIL = dtPartRefund.CARD_NUMBER_DETAIL
                        And
                        F11_TRACE = dtPartRefund.F11_TRACE
                        And
                        F12_Local_time = dtPartRefund.F12_Local_time
                        And
                        F13_Local_date = To_Char(dtPartRefund.F13_Local_date,'mmdd')
                        And 
                        F32_acquirer = dtPartRefund.F32_acquirer
                        And
                        F41_card_acceptor_id = dtPartRefund.F41_card_acceptor_id;
                        
                        Update ECOM_REFUND_GROUP
                        Set IS_ERROR = 0    
                        Where STT = dtPartRefund.STT;
                        
                        Update ECOM_REFUND
                        Set SWITCH_STATUS = '02', SYNC_DATE = Sysdate
                        Where CARD_NUMBER_DETAIL = dtPartRefund.CARD_NUMBER_DETAIL
                        And
                        F11_TRACE = dtPartRefund.F11_TRACE
                        And
                        F12_Local_time = dtPartRefund.F12_Local_time
                        And
                        F13_Local_date = To_Char(dtPartRefund.F13_Local_date,'mmdd')
                        And 
                        F32_acquirer = dtPartRefund.F32_acquirer
                        And
                        F41_card_acceptor_id = dtPartRefund.F41_card_acceptor_id;
                        
                    Else
                        -- OH MY GOD
                        iUpdate:= SQL%ROWCOUNT;
                        
                        iPos := 16;
                        
                        Update ECOM_REFUND_GROUP
                        Set IS_ERROR = 2    
                        Where STT = dtPartRefund.STT;
                        
                        Update ECOM_REFUND
                        Set SWITCH_STATUS = '99', SYNC_DATE = Sysdate
                        Where CARD_NUMBER_DETAIL = dtPartRefund.CARD_NUMBER_DETAIL
                        And
                        F11_TRACE = dtPartRefund.F11_TRACE
                        And
                        F12_Local_time = dtPartRefund.F12_Local_time
                        And
                        F13_Local_date = To_Char(dtPartRefund.F13_Local_date,'mmdd')
                        And 
                        F32_acquirer = dtPartRefund.F32_acquirer
                        And
                        F41_card_acceptor_id = dtPartRefund.F41_card_acceptor_id;
                    
                    End If; 
                ElsIf (iTC = 1 And iQuyetToan = 1) Then -- Giao dich da quyet toan -> bang SHCLOG
                    iPos := 111;
                    
                    Select Stt Into iSttShclog
                    --From Shclog   -- Chuyen nguon giao dich thanh cong
                    From SHCLOG
                    Where Msgtype = 210
                    And Local_Date = dtPartRefund.F13_Local_date
                    And Trim(PAN) = Trim(dtPartRefund.CARD_NUMBER_DETAIL)
                    And Origtrace = to_number(trim(dtPartRefund.F11_TRACE))
                    And Local_time = to_number(trim(dtPartRefund.F12_Local_time))
                    And Acquirer = To_Number(trim(dtPartRefund.F32_acquirer))
                    And Trim(TermId) = Trim(dtPartRefund.F41_card_acceptor_id)
                    And BB_BIN = GET_ECOM_ID()
                    And Fee_Note Is Not Null
                    And Pcode2=890000;
                    
                    If (iHT_TP = 0 And iHT_MP_LAN_DAU = 0 And iAmount_Gd = dtPartRefund.TOTAL_REFUND_AMOUNT) Then
--                        
                        iPos := 14;
                        
                        -- Giao dich sale da quyet toan, chua co hoan tra 1 phan lan nao    -> tong refund 1 phan = gia tri giao dich -> Lam hoan tra toan phan
                        Update SHCLOG
                        Set Respcode = 115, Edit_Date = Sysdate - 1, Edit_User = 'ECOM', Reason_Edit = vKeyTime||' - Refund gd do tong refund = Sale, Num Refund:'||dtPartRefund.TOTAL_TRAN
                        Where Stt = iSttShclog;  
                        
                        Update ECOM_REFUND_GROUP
                        Set IS_ERROR = 0    
                        Where STT = dtPartRefund.STT;
                        
                        Update ECOM_REFUND
                        Set SWITCH_STATUS = '00', SYNC_DATE = Sysdate
                        Where CARD_NUMBER_DETAIL = dtPartRefund.CARD_NUMBER_DETAIL
                        And
                        F11_TRACE = dtPartRefund.F11_TRACE
                        And
                        F12_Local_time = dtPartRefund.F12_Local_time
                        And
                        F13_Local_date = To_Char(dtPartRefund.F13_Local_date,'mmdd')
                        And 
                        F32_acquirer = dtPartRefund.F32_acquirer
                        And
                        F41_card_acceptor_id = dtPartRefund.F41_card_acceptor_id;
                        
                    ElsIf (iHT_TP = 0 And iHT_MP_LAN_DAU = 0 And iAmount_Gd > dtPartRefund.TOTAL_REFUND_AMOUNT) Then
                  
                        -- Giao dich sale da quyet toan, chua co hoan tra 1 phan lan nao   -> Tong refund 1 phan < Gia tri giao dich -> Lam hoan tra 1 phan nhu binh thuong
                        
                        iPos := 15;
                        
                        Update Shclog
                        Set Respcode = 114, PreAmount = dtPartRefund.TOTAL_REFUND_AMOUNT ,Edit_Date = Sysdate - 1, Edit_User = 'ECOM', Reason_Edit = vKeyTime||' - Refund gd do tong refund = Sale, Num Refund:'||dtPartRefund.TOTAL_TRAN
                        Where Stt = iSttShclog;
                        
                        Update ECOM_REFUND_GROUP
                        Set IS_ERROR = 0    
                        Where STT = dtPartRefund.STT;
                        
                        Update ECOM_REFUND
                        Set SWITCH_STATUS = '00', SYNC_DATE = Sysdate
                        Where CARD_NUMBER_DETAIL = dtPartRefund.CARD_NUMBER_DETAIL
                        And
                        F11_TRACE = dtPartRefund.F11_TRACE
                        And
                        F12_Local_time = dtPartRefund.F12_Local_time
                        And
                        F13_Local_date = To_Char(dtPartRefund.F13_Local_date,'mmdd')
                        And 
                        F32_acquirer = dtPartRefund.F32_acquirer
                        And
                        F41_card_acceptor_id = dtPartRefund.F41_card_acceptor_id;
                        
                    Else
                        -- OH MY GOD
                        iUpdate:= SQL%ROWCOUNT;
                        
                        iPos := 16;
                        
                        Update ECOM_REFUND_GROUP
                        Set IS_ERROR = 2    
                        Where STT = dtPartRefund.STT;
                        
                        Update ECOM_REFUND
                        Set SWITCH_STATUS = '99', SYNC_DATE = Sysdate
                        Where CARD_NUMBER_DETAIL = dtPartRefund.CARD_NUMBER_DETAIL
                        And
                        F11_TRACE = dtPartRefund.F11_TRACE
                        And
                        F12_Local_time = dtPartRefund.F12_Local_time
                        And
                        F13_Local_date = To_Char(dtPartRefund.F13_Local_date,'mmdd')
                        And 
                        F32_acquirer = dtPartRefund.F32_acquirer
                        And
                        F41_card_acceptor_id = dtPartRefund.F41_card_acceptor_id;
                    
                    End If; 
                ElsIf (iTC = 0 And iHT_TP = 0 And iHT_MP_LAN_DAU = 1 And iAmount_Gd > (dtPartRefund.TOTAL_REFUND_AMOUNT + iTong_HT)) Then
                    
                    dbms_output.put_line('HTDC TRACE: '||dtPartRefund.F11_TRACE);
                    dbms_output.put_line('STT: '||iSttShclog);
                    -- Giao dich sale da quyet toan, da co hoan tra 1 phan 1 lan -> SHCLOG, lan nay van chua hoan tra het -> Lam hach toan dieu chinh
                    
                    iPos := 17;
                    
                    Insert Into Shclog (
                        ACCEPTORNAME, ACCTNUM, ACCTNUM1, ACQUIRER, ACQUIRER_FE, ACQUIRER_RP, ACQ_COUNTRY, ACQ_CURRENCY_CODE, ACQ_RQ, ADDRESPONSE, 
                        AUTHNUM, BB_ACCOUNT, BB_BIN, BB_BIN_ORIG, BNB_ACC, BNB_SWC, CAP_DATE, CARDHOLDER_AMOUNT, CARDHOLDER_CONV_RATE,
                        CARDPRODUCT, CARD_SEQNO, CHIP_INDEX, CH_CURRENCY_CODE, CONFIG_FEE_ID, CONTENT_FUND, CONV_RATE, CONV_RATE_ACQ, DES, ENTITYID, 
                        FEE, FEE_ACQ, FEE_IRF_ACQ, FEE_IRF_BEN, FEE_IRF_ISS, FEE_IRF_PAY_AT, FEE_IRF_REC_AT, FEE_ISS,
                        FEE_SVF_ACQ, FEE_SVF_BEN, FEE_SVF_ISS,FEE_SVF_PAY_AT, FEE_SVF_REC_AT,
                        FEE_PAY_AT, FEE_PAY_DF, FEE_REC_AT, FEE_REC_DF, FEE_SERVICE, 
                        FORWARD_INST, FROM_SML, FROM_SYS, INS_PCODE, ISREV, ISSUER, ISSUER_FE, ISSUER_RP, ISS_CONV_RATE,
                        ISS_CURRENCY_CODE, ISS_RQ, LDDNV, LOAIGDREVESO, LOCAL_DATE, LOCAL_TIME, MERCHANT_TYPE, MERCHANT_TYPE_ORIG,
                        MSGTYPE, MVV, NEW_FEE, ONLY_SML, ORIGINATOR, ORIGISS, ORIGRESPCODE, ORIGTRACE, ORIG_ACQ, PAN, PCODE, PCODE2,
                        PCODE_ORIG, POS_CONDITION_CODE, POS_ENTRY_CODE, PRE_CARDHOLDER_AMOUNT, 
                        RC, RC_ACQ, RC_ACQ_72, RC_BEN, RC_ISS, RC_ISS_72, REFNUM, REPAY_USD, RESPCODE_GW, REVCODE, SENDER_ACC,
                        SENDER_SWC, SETTLEMENT_AMOUNT, SETTLEMENT_CODE, SETTLEMENT_DATE, SETTLEMENT_RATE, SETT_CURRENCY_CODE,
                        SHCERROR, SML_VERIFY, SRC, TCC, TERMID, TERMID_ACQ, TERMLOC, TGDDNV, TGGUIGD, TGGUINV, TGGUIQT,
                        TGGUIQTP, TGTP, TGXLNV, THAYDOI, TOKEN, TRACE, TRANDATE, TRANSFEREE, TRANTIME, TRAN_CASE, TXNDEST, TXNSRC,
                        EDIT_DATE, EDIT_DATE_INS, NAPAS_DATE, NAPAS_EDIT_DATE, NAPAS_EDIT_DATE_INS, NAPAS_ND_DATE, EDIT_USER, 
                        FAMOUNT, QAMOUNT, RAMOUNT,REAMOUNT, TRANSACTION_AMOUNT, F15, F49, 
                        RESPCODE, 
                        F4, F5, F6, AMOUNT, 
                        FEE_KEY, FEE_NOTE, REASON_EDIT
                        )
                    Select ACCEPTORNAME, ACCTNUM, ACCTNUM1, ACQUIRER, ACQUIRER_FE, ACQUIRER_RP, ACQ_COUNTRY, ACQ_CURRENCY_CODE, ACQ_RQ, ADDRESPONSE, 
                        AUTHNUM, BB_ACCOUNT, BB_BIN, BB_BIN_ORIG, BNB_ACC, BNB_SWC, CAP_DATE, CARDHOLDER_AMOUNT, CARDHOLDER_CONV_RATE,
                        CARDPRODUCT, CARD_SEQNO, CHIP_INDEX, CH_CURRENCY_CODE, CONFIG_FEE_ID, CONTENT_FUND, CONV_RATE, CONV_RATE_ACQ, DES, ENTITYID, 
    --                    FEE, FEE_ACQ, FEE_IRF_ACQ, FEE_IRF_BEN, FEE_IRF_ISS, FEE_IRF_PAY_AT, FEE_IRF_REC_AT, FEE_ISS,
                        0,     0,       0,           0,           0,           0,              0,               0,
    --                    FEE_SVF_ACQ, FEE_SVF_BEN, FEE_SVF_ISS, FEE_SVF_PAY_AT, FEE_SVF_REC_AT,
                        0,             0,           0,           0,              0,
    --                    FEE_PAY_AT, FEE_PAY_DF, FEE_REC_AT, FEE_REC_DF, FEE_SERVICE, 
                        0,            0,          0,          0,          0,
                        FORWARD_INST, FROM_SML, FROM_SYS, INS_PCODE, ISREV, ISSUER, ISSUER_FE, ISSUER_RP, ISS_CONV_RATE,
                        ISS_CURRENCY_CODE, ISS_RQ, LDDNV, LOAIGDREVESO, LOCAL_DATE, LOCAL_TIME, MERCHANT_TYPE, MERCHANT_TYPE_ORIG,
                        MSGTYPE, MVV, NEW_FEE, ONLY_SML, ORIGINATOR, ORIGISS, ORIGRESPCODE, ORIGTRACE, ORIG_ACQ, PAN, PCODE, PCODE2,
                        PCODE_ORIG, POS_CONDITION_CODE, POS_ENTRY_CODE, PRE_CARDHOLDER_AMOUNT, 
                        RC, RC_ACQ, RC_ACQ_72, RC_BEN, RC_ISS, RC_ISS_72, REFNUM, REPAY_USD, RESPCODE_GW, REVCODE, SENDER_ACC,
                        SENDER_SWC, SETTLEMENT_AMOUNT, SETTLEMENT_CODE, SETTLEMENT_DATE, SETTLEMENT_RATE, SETT_CURRENCY_CODE,
                        SHCERROR, SML_VERIFY, SRC, TCC, TERMID, TERMID_ACQ, TERMLOC, TGDDNV, TGGUIGD, TGGUINV, TGGUIQT,
                        TGGUIQTP, TGTP, TGXLNV, THAYDOI, TOKEN, TRACE, TRANDATE, TRANSFEREE, TRANTIME, TRAN_CASE, TXNDEST, TXNSRC,
    --                    EDIT_DATE, EDIT_DATE_INS, NAPAS_DATE, NAPAS_EDIT_DATE, NAPAS_EDIT_DATE_INS, NAPAS_ND_DATE, EDIT_USER, 
                        Sysdate - 1, EDIT_DATE_INS, null,       null,            NAPAS_EDIT_DATE_INS, null,          'ECOM',
                        FAMOUNT, QAMOUNT, RAMOUNT,REAMOUNT, TRANSACTION_AMOUNT, F15, F49, 
    --                    RESPCODE, 
    --                     F4, F5, F6, AMOUNT, 
    --                    FEE_KEY, FEE_NOTE, REASON_EDIT
                        115,
                        dtPartRefund.TOTAL_REFUND_AMOUNT, dtPartRefund.TOTAL_REFUND_AMOUNT, dtPartRefund.TOTAL_REFUND_AMOUNT, dtPartRefund.TOTAL_REFUND_AMOUNT,
                        null, 'HACH TOAN DIEU CHINH CO GD GOC||ECOM LAN: '||To_Char(iLanDieuChinh + 1), vKeyTime||' - Refund gd do tong refund < Sale, dieu chinh lan dau, Num Refund:'||dtPartRefund.TOTAL_TRAN
                    From Shclog
                    Where Msgtype = 210
                    And Local_Date = dtPartRefund.F13_Local_date
                    And Trim(PAN) = Trim(dtPartRefund.CARD_NUMBER_DETAIL)
                    And Origtrace = to_number(trim(dtPartRefund.F11_TRACE))
                    And Local_time = to_number(trim(dtPartRefund.F12_Local_time))
                    And Acquirer = To_Number(trim(dtPartRefund.F32_acquirer))
                    And Trim(TermId) = Trim(dtPartRefund.F41_card_acceptor_id)
                    And BB_BIN = GET_ECOM_ID()
                    And Fee_Note Not Like '%HACH TOAN DIEU CHINH%'
                    And Pcode2=890000;
                    
                    
                    iFound := SQL%ROWCOUNT;
                    
                    dbms_output.put_line('HTDC: '||iSttShclog);
                    
                    If (iFound = 1) Then
                    
                        Update ECOM_REFUND_GROUP
                        Set IS_ERROR = 0    
                        Where STT = dtPartRefund.STT;
                            
                        Update ECOM_REFUND
                        Set SWITCH_STATUS = '00', SYNC_DATE = Sysdate
                        Where CARD_NUMBER_DETAIL = dtPartRefund.CARD_NUMBER_DETAIL
                        And
                        F11_TRACE = dtPartRefund.F11_TRACE
                        And
                        F12_Local_time = dtPartRefund.F12_Local_time
                        And
                        F13_Local_date = To_Char(dtPartRefund.F13_Local_date,'mmdd')
                        And 
                        F32_acquirer = dtPartRefund.F32_acquirer
                        And
                        F41_card_acceptor_id = dtPartRefund.F41_card_acceptor_id;
                        
                    Else
                        Update ECOM_REFUND_GROUP
                        Set IS_ERROR = 8    
                        Where STT = dtPartRefund.STT;
                            
                        Update ECOM_REFUND
                        Set SWITCH_STATUS = '98', SYNC_DATE = Sysdate
                        Where CARD_NUMBER_DETAIL = dtPartRefund.CARD_NUMBER_DETAIL
                        And
                        F11_TRACE = dtPartRefund.F11_TRACE
                        And
                        F12_Local_time = dtPartRefund.F12_Local_time
                        And
                        F13_Local_date = To_Char(dtPartRefund.F13_Local_date,'mmdd')
                        And 
                        F32_acquirer = dtPartRefund.F32_acquirer
                        And
                        F41_card_acceptor_id = dtPartRefund.F41_card_acceptor_id;
                    End If;
                    
                ElsIf (iTC = 0 And iHT_TP = 0 And iHT_MP_LAN_DAU = 1 And iAmount_Gd = (dtPartRefund.TOTAL_REFUND_AMOUNT + iTong_HT)) Then
                    
                    iPos := 18;
                    
                    --  Giao dich sale da quyet toan, da co hoan tra 1 phan 1 lan + co the co hach toan dieu chinh
                    -- lan nay hoan tra not -> Lam  hach toan dieu chinh, dong thoi hoan tra phi giao dich goc
                    
                    Insert Into Shclog (
                        ACCEPTORNAME, ACCTNUM, ACCTNUM1, ACQUIRER, ACQUIRER_FE, ACQUIRER_RP, ACQ_COUNTRY, ACQ_CURRENCY_CODE, ACQ_RQ, ADDRESPONSE, 
                        AUTHNUM, BB_ACCOUNT, BB_BIN, BB_BIN_ORIG, BNB_ACC, BNB_SWC, CAP_DATE, CARDHOLDER_AMOUNT, CARDHOLDER_CONV_RATE,
                        CARDPRODUCT, CARD_SEQNO, CHIP_INDEX, CH_CURRENCY_CODE, CONFIG_FEE_ID, CONTENT_FUND, CONV_RATE, CONV_RATE_ACQ, DES, ENTITYID, 
                        FEE, FEE_ACQ, FEE_IRF_ACQ, FEE_IRF_BEN, FEE_IRF_ISS, FEE_IRF_PAY_AT, FEE_IRF_REC_AT, FEE_ISS,
                        FEE_SVF_ACQ, FEE_SVF_BEN, FEE_SVF_ISS,FEE_SVF_PAY_AT, FEE_SVF_REC_AT,
                        FEE_PAY_AT, FEE_PAY_DF, FEE_REC_AT, FEE_REC_DF, FEE_SERVICE, 
                        FORWARD_INST, FROM_SML, FROM_SYS, INS_PCODE, ISREV, ISSUER, ISSUER_FE, ISSUER_RP, ISS_CONV_RATE,
                        ISS_CURRENCY_CODE, ISS_RQ, LDDNV, LOAIGDREVESO, LOCAL_DATE, LOCAL_TIME, MERCHANT_TYPE, MERCHANT_TYPE_ORIG,
                        MSGTYPE, MVV, NEW_FEE, ONLY_SML, ORIGINATOR, ORIGISS, ORIGRESPCODE, ORIGTRACE, ORIG_ACQ, PAN, PCODE, PCODE2,
                        PCODE_ORIG, POS_CONDITION_CODE, POS_ENTRY_CODE, PRE_CARDHOLDER_AMOUNT, 
                        RC, RC_ACQ, RC_ACQ_72, RC_BEN, RC_ISS, RC_ISS_72, REFNUM, REPAY_USD, RESPCODE_GW, REVCODE, SENDER_ACC,
                        SENDER_SWC, SETTLEMENT_AMOUNT, SETTLEMENT_CODE, SETTLEMENT_DATE, SETTLEMENT_RATE, SETT_CURRENCY_CODE,
                        SHCERROR, SML_VERIFY, SRC, TCC, TERMID, TERMID_ACQ, TERMLOC, TGDDNV, TGGUIGD, TGGUINV, TGGUIQT,
                        TGGUIQTP, TGTP, TGXLNV, THAYDOI, TOKEN, TRACE, TRANDATE, TRANSFEREE, TRANTIME, TRAN_CASE, TXNDEST, TXNSRC,
                        EDIT_DATE, EDIT_DATE_INS, NAPAS_DATE, NAPAS_EDIT_DATE, NAPAS_EDIT_DATE_INS, NAPAS_ND_DATE, EDIT_USER, 
                        FAMOUNT, QAMOUNT, RAMOUNT,REAMOUNT, TRANSACTION_AMOUNT, F15, F49, 
                        RESPCODE, 
                        F4, F5, F6, AMOUNT, 
                        FEE_KEY, FEE_NOTE, REASON_EDIT
                        )
                    Select ACCEPTORNAME, ACCTNUM, ACCTNUM1, ACQUIRER, ACQUIRER_FE, ACQUIRER_RP, ACQ_COUNTRY, ACQ_CURRENCY_CODE, ACQ_RQ, ADDRESPONSE, 
                        AUTHNUM, BB_ACCOUNT, BB_BIN, BB_BIN_ORIG, BNB_ACC, BNB_SWC, CAP_DATE, CARDHOLDER_AMOUNT, CARDHOLDER_CONV_RATE,
                        CARDPRODUCT, CARD_SEQNO, CHIP_INDEX, CH_CURRENCY_CODE, CONFIG_FEE_ID, CONTENT_FUND, CONV_RATE, CONV_RATE_ACQ, DES, ENTITYID, 
                        FEE, FEE_ACQ, FEE_IRF_ACQ, FEE_IRF_BEN, FEE_IRF_ISS, FEE_IRF_PAY_AT, FEE_IRF_REC_AT, FEE_ISS,
                        FEE_SVF_ACQ, FEE_SVF_BEN, FEE_SVF_ISS, FEE_SVF_PAY_AT, FEE_SVF_REC_AT,
                        FEE_PAY_AT, FEE_PAY_DF, FEE_REC_AT, FEE_REC_DF, FEE_SERVICE, 
                        FORWARD_INST, FROM_SML, FROM_SYS, INS_PCODE, ISREV, ISSUER, ISSUER_FE, ISSUER_RP, ISS_CONV_RATE,
                        ISS_CURRENCY_CODE, ISS_RQ, LDDNV, LOAIGDREVESO, LOCAL_DATE, LOCAL_TIME, MERCHANT_TYPE, MERCHANT_TYPE_ORIG,
                        MSGTYPE, MVV, NEW_FEE, ONLY_SML, ORIGINATOR, ORIGISS, ORIGRESPCODE, ORIGTRACE, ORIG_ACQ, PAN, PCODE, PCODE2,
                        PCODE_ORIG, POS_CONDITION_CODE, POS_ENTRY_CODE, PRE_CARDHOLDER_AMOUNT, 
                        RC, RC_ACQ, RC_ACQ_72, RC_BEN, RC_ISS, RC_ISS_72, REFNUM, REPAY_USD, RESPCODE_GW, REVCODE, SENDER_ACC,
                        SENDER_SWC, SETTLEMENT_AMOUNT, SETTLEMENT_CODE, SETTLEMENT_DATE, SETTLEMENT_RATE, SETT_CURRENCY_CODE,
                        SHCERROR, SML_VERIFY, SRC, TCC, TERMID, TERMID_ACQ, TERMLOC, TGDDNV, TGGUIGD, TGGUINV, TGGUIQT,
                        TGGUIQTP, TGTP, TGXLNV, THAYDOI, TOKEN, TRACE, TRANDATE, TRANSFEREE, TRANTIME, TRAN_CASE, TXNDEST, TXNSRC,
    --                    EDIT_DATE, EDIT_DATE_INS, NAPAS_DATE, NAPAS_EDIT_DATE, NAPAS_EDIT_DATE_INS, NAPAS_ND_DATE, EDIT_USER,
                        Sysdate - 1, EDIT_DATE_INS, null,       null,            NAPAS_EDIT_DATE_INS, null,          'ECOM', 
                        FAMOUNT, QAMOUNT, RAMOUNT,REAMOUNT, TRANSACTION_AMOUNT, F15, F49, 
    --                    RESPCODE, 
    --                     F4, F5, F6, AMOUNT, 
    --                    FEE_KEY, FEE_NOTE, REASON_EDIT
                        115,
                        dtPartRefund.TOTAL_REFUND_AMOUNT, dtPartRefund.TOTAL_REFUND_AMOUNT, dtPartRefund.TOTAL_REFUND_AMOUNT, dtPartRefund.TOTAL_REFUND_AMOUNT,
                        null, 'HACH TOAN DIEU CHINH CO GD GOC||ECOM LAN: '||To_Char(iLanDieuChinh + 1)||', Lan cuoi -> Hoan lai phi giao dich tu dong', vKeyTime||' - Refund gd lan cuoi, Num Refund:'||dtPartRefund.TOTAL_TRAN
                    From Shclog
                    Where Msgtype = 210
                    And Local_Date = dtPartRefund.F13_Local_date
                    And Trim(PAN) = Trim(dtPartRefund.CARD_NUMBER_DETAIL)
                    And Origtrace = to_number(trim(dtPartRefund.F11_TRACE))
                    And Local_time = to_number(trim(dtPartRefund.F12_Local_time))
                    And Acquirer = To_Number(trim(dtPartRefund.F32_acquirer))
                    And Trim(TermId) = Trim(dtPartRefund.F41_card_acceptor_id)
                    And BB_BIN = GET_ECOM_ID()
                    And Fee_Note Not Like '%HACH TOAN DIEU CHINH%'
                    And Pcode2=890000;
                    
                    iFound := SQL%ROWCOUNT;
                    
                    If (iFound = 1) Then
                    
                        Update ECOM_REFUND_GROUP
                        Set IS_ERROR = 0    
                        Where STT = dtPartRefund.STT;
                            
                        Update ECOM_REFUND
                        Set SWITCH_STATUS = '00', SYNC_DATE = Sysdate
                        Where CARD_NUMBER_DETAIL = dtPartRefund.CARD_NUMBER_DETAIL
                            And
                            F11_TRACE = dtPartRefund.F11_TRACE
                            And
                            F12_Local_time = dtPartRefund.F12_Local_time
                            And
                            F13_Local_date = To_Char(dtPartRefund.F13_Local_date,'mmdd')
                            And 
                            F32_acquirer = dtPartRefund.F32_acquirer
                            And
                            F41_card_acceptor_id = dtPartRefund.F41_card_acceptor_id;
                    Else
                        Update ECOM_REFUND_GROUP
                        Set IS_ERROR = 7    
                        Where STT = dtPartRefund.STT;
                            
                        Update ECOM_REFUND
                        Set SWITCH_STATUS = '97', SYNC_DATE = Sysdate
                        Where CARD_NUMBER_DETAIL = dtPartRefund.CARD_NUMBER_DETAIL
                            And
                            F11_TRACE = dtPartRefund.F11_TRACE
                            And
                            F12_Local_time = dtPartRefund.F12_Local_time
                            And
                            F13_Local_date = To_Char(dtPartRefund.F13_Local_date,'mmdd')
                            And 
                            F32_acquirer = dtPartRefund.F32_acquirer
                            And
                            F41_card_acceptor_id = dtPartRefund.F41_card_acceptor_id;                      
                    End If;
                    
                ElsIf (iTC = 0 And iHT_TP = 0 And iHT_MP_LAN_DAU = 1 And iAmount_Gd < (dtPartRefund.TOTAL_REFUND_AMOUNT + iTong_HT)) Then
                    
                    iPos := 18;
                    
                    
                    
                    Update ECOM_REFUND_GROUP
                    Set IS_ERROR = 0    
                    Where STT = dtPartRefund.STT;
                            
                    Update ECOM_REFUND
                    Set SWITCH_STATUS = '03', SYNC_DATE = Sysdate
                    Where CARD_NUMBER_DETAIL = dtPartRefund.CARD_NUMBER_DETAIL
                        And
                        F11_TRACE = dtPartRefund.F11_TRACE
                        And
                        F12_Local_time = dtPartRefund.F12_Local_time
                        And
                        F13_Local_date = To_Char(dtPartRefund.F13_Local_date,'mmdd')
                        And 
                        F32_acquirer = dtPartRefund.F32_acquirer
                        And
                        F41_card_acceptor_id = dtPartRefund.F41_card_acceptor_id;
                                   
                Else
                    
                    iPos := 19;
                    -- Chi co CHUA hoac em MEN moi biet no la case nao
                    
                    Update ECOM_REFUND_GROUP
                    Set IS_ERROR = 3
                    Where STT = dtPartRefund.STT;
                    
                    Update ECOM_REFUND
                    Set SWITCH_STATUS = '96', SYNC_DATE = Sysdate
                    Where CARD_NUMBER_DETAIL = dtPartRefund.CARD_NUMBER_DETAIL
                    And
                    F11_TRACE = dtPartRefund.F11_TRACE
                    And
                    F12_Local_time = dtPartRefund.F12_Local_time
                    And
                    F13_Local_date = To_Char(dtPartRefund.F13_Local_date,'mmdd')
                    And 
                    F32_acquirer = dtPartRefund.F32_acquirer
                    And
                    F41_card_acceptor_id = dtPartRefund.F41_card_acceptor_id;
                                            
                    iUpdate:= SQL%ROWCOUNT;
                    
                End If;
                
                
            END LOOP;

        CLOSE csPartRefund;
        Commit;
        
        iPos := 20;
        
        -- Backup giao dich
        Insert Into ECOM_REFUND_BK(AMOUNT,USER_NAME,UPDATE_DATE,TRANSACTION_INFO,
            TRANSACTION_CODE,DESCRIPTION,STATUS_ID,TRANSACTION_TYPE,MERCHANT_RF_ID,F11_TRACE,F12_LOCAL_TIME,F13_LOCAL_DATE,LOCAL_DATE,
            F15_SETTLE_DATE,SETT_DATE,F32_ACQUIRER,F41_CARD_ACCEPTOR_ID,F63_TRANS_SWITCH,CARD_NUMBER,ISSUER_BANK_CODE,SWITCH_STATUS,
            SWITCH_SETTLE_DATE,GET_DATE,KEY_TIME,REFUND_AMOUNT,REFUND_DATE,STT,SYNC_DATE,LATE_REFUND,BK_TIME,CARD_NUMBER_DETAIL,REFUND_TRANSACTION_CODE)
        Select AMOUNT,USER_NAME,UPDATE_DATE,TRANSACTION_INFO,
            TRANSACTION_CODE,DESCRIPTION,STATUS_ID,TRANSACTION_TYPE,MERCHANT_RF_ID,F11_TRACE,F12_LOCAL_TIME,F13_LOCAL_DATE,LOCAL_DATE,
            F15_SETTLE_DATE,SETT_DATE,F32_ACQUIRER,F41_CARD_ACCEPTOR_ID,F63_TRANS_SWITCH,CARD_NUMBER,ISSUER_BANK_CODE,SWITCH_STATUS,
            SWITCH_SETTLE_DATE,GET_DATE,KEY_TIME,REFUND_AMOUNT,REFUND_DATE,STT,SYNC_DATE,LATE_REFUND,Sysdate,CARD_NUMBER_DETAIL,REFUND_TRANSACTION_CODE
        From ECOM_REFUND;
        
        Insert Into ECOM_REFUND_ALL_BK(F15_SETTLE_DATE,SETT_DATE,F32_ACQUIRER,F41_CARD_ACCEPTOR_ID,F63_TRANS_SWITCH,CARD_NUMBER,ISSUER_BANK_CODE,SWITCH_STATUS,
            SWITCH_SETTLE_DATE,GET_DATE,KEY_TIME,REFUND_DATE,REFUND_AMOUNT,STT,SYNC_DATE,AMOUNT,
            USER_NAME,UPDATE_DATE,TRANSACTION_INFO,TRANSACTION_CODE,DESCRIPTION,STATUS_ID,
            TRANSACTION_TYPE,MERCHANT_RF_ID,F11_TRACE,F12_LOCAL_TIME,F13_LOCAL_DATE,LOCAL_DATE,BK_TIME,CARD_NUMBER_DETAIL,REFUND_TRANSACTION_CODE)
        Select F15_SETTLE_DATE,SETT_DATE,F32_ACQUIRER,F41_CARD_ACCEPTOR_ID,F63_TRANS_SWITCH,CARD_NUMBER,ISSUER_BANK_CODE,SWITCH_STATUS,
            SWITCH_SETTLE_DATE,GET_DATE,KEY_TIME,REFUND_DATE,REFUND_AMOUNT,STT,SYNC_DATE,AMOUNT,
            USER_NAME,UPDATE_DATE,TRANSACTION_INFO,TRANSACTION_CODE,DESCRIPTION,STATUS_ID,
            TRANSACTION_TYPE,MERCHANT_RF_ID,F11_TRACE,F12_LOCAL_TIME,F13_LOCAL_DATE,LOCAL_DATE,Sysdate,CARD_NUMBER_DETAIL,REFUND_TRANSACTION_CODE
        From ECOM_REFUND_ALL;
             
        Insert Into ECOM_REFUND_GROUP_BK(F11_TRACE,F12_LOCAL_TIME,F13_LOCAL_DATE,LOCAL_DATE,F15_SETTLE_DATE,SETT_DATE,F32_ACQUIRER,
            F41_CARD_ACCEPTOR_ID,CARD_NUMBER,TOTAL_REFUND_AMOUNT,TOTAL_TRAN,KEY_TIME,IS_ERROR,STT,BK_TIME,CARD_NUMBER_DETAIL,REFUND_TRANSACTION_CODE)
        Select F11_TRACE,F12_LOCAL_TIME,F13_LOCAL_DATE,LOCAL_DATE,F15_SETTLE_DATE,SETT_DATE,F32_ACQUIRER,
            F41_CARD_ACCEPTOR_ID,CARD_NUMBER,TOTAL_REFUND_AMOUNT,TOTAL_TRAN,KEY_TIME,IS_ERROR,STT,Sysdate,CARD_NUMBER_DETAIL,REFUND_TRANSACTION_CODE
        From ECOM_REFUND_GROUP;
        
        Insert Into ECOM_SUCCESS_BK(TRANSACTION_CODE,AMOUNT,SETTLEMENT_AMOUNT,CURRENCY,ORDER_INFO,MERCHANT_TRANSACTION_CODE,
            URL_RETURN,URL_BACK,URL_BACK_DEFAULT,TRANSACTION_STATUS_ID,MERCHANT_ID,ISSUER_BANK_ID,CARD_ID,CARD_NUMBER,
            CARD_EXPIRED_DATE,AUTHORISATION_CODE,RESPONSE_CODE_ID,BATCH_NUMBER,ORDER_ID,
            MERCHANT_CODE,MERCHANT_NAME,SECRET_KEY,IPN_URL,IPN_URL_RSA,ISSUER_BANK_CODE,ISSUER_BANK_NAME,ISSUER_BANK_URL_FORWARD,TRANSACTION_TYPE,
            IP_ADDRESS,VERSION,RESPONSE_CODE,URL_LOGO,URL_ISSUER_LOGO,MERCHANT_ISSUER_BANK_CODE,MCC_ID,TRANSACTION_STATUS,INTEGRATION_TYPE,FAST_PAY,
            RESPONSE_CODE_MESSAGE,F11_TRACE,F12_LOCAL_TIME,F13_LOCAL_DATE,LOCAL_DATE,F15_SETTLE_DATE,SETT_DATE,F41_CARD_ACCEPTOR_ID,F63_TRANS_SWITCH,
            GET_DATE,KEY_TIME,F32_ACQUIRER,STT,CARD_HOLDER_NAME,ORDER_CODE,ORDER_DATE,LOCALE,ISSUER_TRANSACTION_REF,SWITCH_STATUS,BK_TIME,CARD_NUMBER_DETAIL,REFUND_TRANSACTION_CODE)
        Select TRANSACTION_CODE,AMOUNT,SETTLEMENT_AMOUNT,CURRENCY,ORDER_INFO,MERCHANT_TRANSACTION_CODE,
            URL_RETURN,URL_BACK,URL_BACK_DEFAULT,TRANSACTION_STATUS_ID,MERCHANT_ID,ISSUER_BANK_ID,CARD_ID,CARD_NUMBER,
            CARD_EXPIRED_DATE,AUTHORISATION_CODE,RESPONSE_CODE_ID,BATCH_NUMBER,ORDER_ID,
            MERCHANT_CODE,MERCHANT_NAME,SECRET_KEY,IPN_URL,IPN_URL_RSA,ISSUER_BANK_CODE,ISSUER_BANK_NAME,ISSUER_BANK_URL_FORWARD,TRANSACTION_TYPE,
            IP_ADDRESS,VERSION,RESPONSE_CODE,URL_LOGO,URL_ISSUER_LOGO,MERCHANT_ISSUER_BANK_CODE,MCC_ID,TRANSACTION_STATUS,INTEGRATION_TYPE,FAST_PAY,
            RESPONSE_CODE_MESSAGE,F11_TRACE,F12_LOCAL_TIME,F13_LOCAL_DATE,LOCAL_DATE,F15_SETTLE_DATE,SETT_DATE,F41_CARD_ACCEPTOR_ID,F63_TRANS_SWITCH,
            GET_DATE,KEY_TIME,F32_ACQUIRER,STT,CARD_HOLDER_NAME,ORDER_CODE,ORDER_DATE,LOCALE,ISSUER_TRANSACTION_REF,SWITCH_STATUS,Sysdate,CARD_NUMBER_DETAIL,REFUND_TRANSACTION_CODE
        From ECOM_SUCCESS;
        
        Commit;
        
        --Select Max(Stt) Into imaxstt From Shclog;
        
        NP_SYNC_QRIBFT_ECOM(1);
        
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'0','Success - MAX STT:'||imaxstt,'NAPAS_SYNC_ECOM');                        
        commit;
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'0','Finish NAPAS_SYNC_ECOM','NAPAS_SYNC_ECOM');                        
        commit;
    /*
    Else
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
        Values(sysdate,'0','Wait fill online to Backend Close','NAPAS_SYNC_ECOM');
        commit;   
        DBMS_LOCK.sleep(120);
        NAPAS_SYNC_ECOM(0);
    End If;
    */
EXCEPTION
    WHEN OTHERS THEN

    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' NAPAS_SYNC_ECOM Err num: ' ||iPos||'-'||' - STT'||iStt||'-'||TO_CHAR(ecode) || ' - Err detail: ' || emesg;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE,CRITICAL)
    Values(sysdate,ecode,vDetail,'NAPAS_SYNC_ECOM',2);

    commit;
End;
/
