CREATE OR REPLACE PROCEDURE RPT.NAPAS_CAL_FEE_LOCAL_IBFT
    (
        TuTG        CHAR
    )
AS
/* ----------------------  tinh phi gd ------------------------
    Author          : sondt
    Date created    : 25/04/2024
----------------------------------------------------------------------------------------------*/

    d1 DATE;
    d2 DATE;
    iPos Integer := 0;
    num INTEGER := 0 ;
    fee INTEGER := 0 ;
    iMaxStt INTEGER;
    Ecode NUMBER;
    Emesg VARCHAR2(200);
    dt_start DATE;
    v_begin TIMESTAMP(9);
    v_end TIMESTAMP(9);
    v_interval INTERVAL DAY TO SECOND;
    vDetail VARCHAR2(500) := 'Nothing ! ';
    vPhone VARCHAR2(200);
BEGIN
    
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,'0','START','NAPAS_CAL_FEE_LOCAL_IBFT', 0);
    Commit;

    d1 := TO_DATE(TuTG,'dd/mm/yyyy');
    dt_start := sysdate;
    v_begin := SYSTIMESTAMP;
    
    iPos:=1;
    
    Commit;
    
    Merge Into 
    (
        Select STT, MSGTYPE, PREAMOUNT, SETTLEMENT_AMOUNT, PCODE, FEE_PAY_AT,FEE_REC_AT,FEE_IRF_ISS_NO_VAT,
        FEE_SVF_ISS_NO_VAT,FEE_IRF_ACQ_NO_VAT,FEE_SVF_ACQ_NO_VAT,FEE_IRF_BEN_NO_VAT,FEE_SVF_BEN_NO_VAT,
            FEE_KEY,FEE_NOTE, Issuer_Rp, Acquirer_Rp, BB_BIN, AMOUNT, CARDHOLDER_CONV_RATE
        From SHCLOG_SETT_IBFT
        Where SETTLEMENT_DATE = d1
        And 
        (
            (msgtype = 210 AND respcode = 0 AND isrev IS NULL)
            Or
            (msgtype = 430 And Respcode = 114)
        )
        And FEE_KEY Is Not Null
        And
        (
            FEE_NOTE Is Null
            Or
            FEE_NOTE NOT LIKE 'HACH TOAN DIEU CHINH%'
        )
        And
        (
            (
                (
                    SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) In ('00','01','30','35','40','41','42','43','48','20')
                    Or
                    (SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) = '94' And MERCHANT_TYPE = 6011)
                )  
                And
                Decode(FROM_SYS,null,'IST',FROM_SYS) Like '%IST%'
            )
            Or
            (
                FROM_SYS Is Not Null
                And
                SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) In ('01','42','91')
            )
        )
        And SubStr(FEE_KEY,0,3) <> 'ERR'
    ) A
    Using 
    (
        Select FEE_KEY, FEE_NOTE, FEE_ISS_TYPE, FEE_ACQ_TYPE, FEE_PAY_TYPE, FEE_REC_TYPE, FEE_BEN_TYPE, FEE_IRF_ISS, FEE_SVF_ISS, FEE_IRF_ACQ,
            FEE_SVF_ACQ, FEE_IRF_BEN, FEE_SVF_BEN, FEE_PAY_AT, FEE_REC_AT, FEE_ISS_EXT, FEE_ACQ_EXT, FEE_BEN_EXT
        From Gr_Fee_Config_New
        Where d1 Between Valid_From And Valid_To
    ) B
    On (A.FEE_KEY = B.FEE_KEY)
    WHEN MATCHED THEN
        Update Set  FEE_NOTE = B.FEE_NOTE,
                    FEE_PAY_AT =    Case
                                        When (Issuer_Rp = 605609 Or Acquirer_Rp = 605609) And SUBSTR(PCODE,1,2) <> '43' Then
                                            Case
                                                When B.FEE_PAY_TYPE = 'PHANG' Then
                                                    B.FEE_PAY_AT
                                                When B.FEE_PAY_TYPE = 'PHAN_TRAM' Then
                                                    B.FEE_PAY_AT * A.AMOUNT
                                            End
                                        Else A.FEE_PAY_AT
                                    End,
                    FEE_REC_AT =    Case
                                        When (Issuer_Rp = 605609 Or Acquirer_Rp = 605609) And SUBSTR(PCODE,1,2) <> '43' Then
                                            Case
                                                When B.FEE_REC_TYPE = 'PHANG' Then
                                                    B.FEE_REC_AT
                                                When B.FEE_PAY_TYPE = 'PHAN_TRAM' Then
                                                    B.FEE_REC_AT * A.AMOUNT
                                            End
                                        Else A.FEE_REC_AT
                                    End,
                    FEE_IRF_ISS_NO_VAT =   Case
                                        When MSGTYPE = '430' And  B.FEE_ISS_TYPE = 'PHAN_TRAM' Then 
                                            B.FEE_IRF_ISS * A.PREAMOUNT                                    
                                        When MSGTYPE = '210' And B.FEE_ISS_TYPE = 'PHANG' Then
                                            B.FEE_IRF_ISS
                                        When MSGTYPE = '210' And B.FEE_ISS_TYPE = 'PHAN_TRAM' Then
                                            B.FEE_IRF_ISS * A.AMOUNT
                                        When MSGTYPE = '210' And B.FEE_ISS_TYPE = 'CBFT_V2' Then
                                            B.FEE_IRF_ISS * AMOUNT + B.FEE_ISS_EXT
                                        When MSGTYPE = '210' And B.FEE_ISS_TYPE = 'PHANG_PHAN_TRAM' Then
                                            B.FEE_IRF_ISS * AMOUNT + B.FEE_ISS_EXT    
                                        Else A.FEE_IRF_ISS_NO_VAT                                               
                                    End,
                    FEE_SVF_ISS_NO_VAT =   Case
                                        
                                        When MSGTYPE = '430' Then 0    
                                        When MSGTYPE = '210' And B.FEE_ISS_TYPE = 'PHANG' Then
                                            B.FEE_SVF_ISS
                                        When MSGTYPE = '210' And B.FEE_ISS_TYPE = 'PHAN_TRAM' Then
                                            B.FEE_SVF_ISS * A.AMOUNT
                                        When MSGTYPE = '210' And B.FEE_ISS_TYPE = 'PHANG_PHAN_TRAM' Then
                                            B.FEE_SVF_ISS * AMOUNT + B.FEE_ISS_EXT   
                                        Else A.FEE_SVF_ISS_NO_VAT        
                                    End,
                    FEE_IRF_ACQ_NO_VAT =   Case
                                        When MSGTYPE = '430' And  B.FEE_ACQ_TYPE = 'PHAN_TRAM' Then 
                                            B.FEE_IRF_ACQ * A.PREAMOUNT                                        
                                        When MSGTYPE = '210' And B.FEE_ACQ_TYPE = 'PHANG' Then
                                            B.FEE_IRF_ACQ
                                        When MSGTYPE = '210' And B.FEE_ACQ_TYPE = 'PHAN_TRAM' Then
                                            B.FEE_IRF_ACQ * Decode(ACQUIRER_RP,605608,SETTLEMENT_AMOUNT,Decode(MSGTYPE,430,PREAMOUNT,AMOUNT))
                                        When MSGTYPE = '210' And B.FEE_ACQ_TYPE = 'PHANG_PHAN_TRAM' And Issuer_Rp = 602907 Then
                                            B.FEE_IRF_ACQ
                                        When MSGTYPE = '210' And B.FEE_ACQ_TYPE = 'PHANG_PHAN_TRAM' And Issuer_Rp <> 602907 Then
                                            B.FEE_IRF_ACQ * Decode(ACQUIRER_RP,605608,SETTLEMENT_AMOUNT,Decode(MSGTYPE,430,PREAMOUNT,AMOUNT)) + B.FEE_ACQ_EXT   
                                        When MSGTYPE = '210' And B.FEE_ACQ_TYPE = 'PHANG_PHAN_TRAM_USD' Then
                                            B.FEE_IRF_ACQ * Decode(ACQUIRER_RP,605608,SETTLEMENT_AMOUNT,Decode(MSGTYPE,430,PREAMOUNT,AMOUNT)) + B.FEE_ACQ_EXT/A.CARDHOLDER_CONV_RATE           
                                        Else A.FEE_IRF_ACQ_NO_VAT                                           
                                    End,
                    FEE_SVF_ACQ_NO_VAT =   Case
                                        When MSGTYPE = '430' Then 0                                        
                                        When MSGTYPE = '210'And B.FEE_ACQ_TYPE = 'PHANG' Then
                                            B.FEE_SVF_ACQ
                                        When MSGTYPE = '210'And B.FEE_ACQ_TYPE = 'PHAN_TRAM' Then
                                            B.FEE_SVF_ACQ * Decode(ACQUIRER_RP,605608,SETTLEMENT_AMOUNT,Decode(MSGTYPE,430,PREAMOUNT,AMOUNT))
                                        When MSGTYPE = '210'And B.FEE_ACQ_TYPE = 'PHANG_PHAN_TRAM' Then
                                            B.FEE_SVF_ACQ * Decode(ACQUIRER_RP,605608,SETTLEMENT_AMOUNT,Decode(MSGTYPE,430,PREAMOUNT,AMOUNT)) + B.FEE_ACQ_EXT   
                                        When MSGTYPE = '210'And B.FEE_ACQ_TYPE = 'PHANG_PHAN_TRAM_USD' Then
                                            B.FEE_SVF_ACQ * Decode(ACQUIRER_RP,605608,SETTLEMENT_AMOUNT,Decode(MSGTYPE,430,PREAMOUNT,AMOUNT)) + B.FEE_ACQ_EXT/A.CARDHOLDER_CONV_RATE
                                        Else A.FEE_IRF_ACQ_NO_VAT                                                                                              
                                    End,
                    FEE_IRF_BEN_NO_VAT =   Case
                                        When B.FEE_BEN_TYPE = 'PHANG' Then
                                            B.FEE_IRF_BEN
                                        When B.FEE_BEN_TYPE = 'PHAN_TRAM' Then
                                            B.FEE_IRF_BEN * A.AMOUNT
                                        When B.FEE_BEN_TYPE = 'CBFT_V2' Then
                                            B.FEE_IRF_BEN * AMOUNT + B.FEE_BEN_EXT
                                        Else A.FEE_IRF_BEN_NO_VAT        
                                    End,
                    FEE_SVF_BEN_NO_VAT =   Case
                                        
                                        When B.FEE_BEN_TYPE = 'PHANG' Then
                                            B.FEE_SVF_BEN
                                        When B.FEE_BEN_TYPE = 'PHAN_TRAM' Then
                                            B.FEE_SVF_BEN * A.AMOUNT
                                        Else A.FEE_SVF_BEN_NO_VAT       
                                    End                                     
            ;

    fee := SQL%ROWCOUNT;

    v_end := SYSTIMESTAMP;
    v_interval := v_end - v_begin;

    vDetail := 'Tinh phi thanh cong cho '||fee||' giao dich thanh cong, ngay : '||To_Char(d1,'dd/mm/yyyy')||'.'|| CHR(13) || CHR(10)||'Bat dau TP luc: '||To_Char(dt_start,'hh24:mi:ss')||', ket thuc tinh phi luc: '||To_Char(sysdate,'hh24:mi:ss')||'.'|| CHR(13) || CHR(10)||'Tong thoi gian tinh phi la: '||extract(hour from v_interval)||' gio, '||extract(minute from v_interval)||' phut, '||Round(extract(second from v_interval),0)||' giay.';

    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,'SUCC',vDetail,'NAPAS_CAL_FEE_LOCAL_IBFT', 0);
    Commit;
    iPos:=4;
    
    -- Utl_Mail.send(vSender,vReceiver,vCC,bCC,vSub,vDetail,vMType,NULL); -- bo gui  mail 05/08
    ------ ghi log  qua trinh tinh phi ----------------------------------------

    INSERT INTO RP_LOG_FEE (LOG_FEE_ID, TUTG, DENTG,TGTINHPHI,USERTP,numrow,PROCESSTIME)
    VALUES ( Getkhoacuabang('RP_LOG_FEE','LOG_FEE_ID') ,TuTG ,TuTG ,SYSDATE,'KT3',num,(sysdate-dt_start)*24*60*60);
    commit;
    
EXCEPTION WHEN OTHERS THEN
    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' NAPAS_CAL_FEE_LOCAL_IBFT - POS:'||iPos||', Err detail: ' || emesg;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,ecode,vDetail,'NAPAS_CAL_FEE_LOCAL_IBFT', 2);
    commit;
    SEND_SMS('NAPAS_CAL_FEE_LOCAL_IBFT#0983411005;0988766330;0936535868;0919001563#'||vDetail);


END;
/
