CREATE OR REPLACE PROCEDURE RPT.UPDATE_TRANS_TGTT_20
(
    i INTEGER
) IS
    ecode NUMBER;
    emesg VARCHAR2(200);
    vDetail VARCHAR2(500) := 'Nothing ! ';
    iCount INTEGER := 0;
    pProcessDate date;
    
    v_csv_content    CLOB := '';                  -- Use CLOB for large content
    v_row_content    CLOB := '';
    v_att_filename   VARCHAR2 (255) := 'Danh sach GD TimeOut Lien thong co TCNL = TGTT 2.0 khong tim thay.csv'; -- Filename with .csv extension
    v_mime_type      VARCHAR2 (100) := 'text/csv; charset=UTF-8';
    v_sender         VARCHAR2 (50) := 'baocaotest@napas.com.vn';
    v_recipient      VARCHAR2 (300) := 'dattt@napas.com.vn;hangttn@napas.com.vn,thuyntt@napas.com.vn,hattt@napas.com.vn';
    v_subject        VARCHAR2 (100) := 'C?nh báo d?i chi?u GD timeout 1.0 sang TGTT 2.0';
    v_message        VARCHAR2 (4000) := 'Nothing ! ';
    vCC              VARCHAR2 (120) := '';
    /*
    Project name: 
    Dev Jira Tikcet (If have):
    Author: DatTT
    Dev Date: 27-Jun-2025
    Edit content: Cap nhat DE#18, DE#67, DE#43 tu IBFT 2.0 cho giao dich thanh toan lien thong co TCNL = TGTT 2.0
    */
    
BEGIN
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,'0','BEGIN','UPDATE_TRANS_TGTT_20', 0);
    Commit;
    
	--dong bo them TGTT 20
	MERGE INTO TGTT_20 A 
	USING 
	(
		SELECT * 
		FROM ibftbackend20.V_PARTICIPANT_IBFT20@LINKACHTEST
		WHERE PARTICIPANT_TYPE = 'NONBANK'
	) B
	ON (A.TGTT_ID = B.PARTICIPANT_CODE)
	WHEN NOT MATCHED THEN
		INSERT (TGTT_ID,TGTT_SHORT)
		VALUES (B.PARTICIPANT_CODE, B.PARTICIPANT_CODE)
	;
    
	Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,'0','Merge Nonbank','UPDATE_TRANS_TGTT_20', 0);
    Commit;
    pProcessDate := sysdate - i;
    MERGE INTO (
        Select * 
        From SHCLOG_SETT_IBFT
        Where SETTLEMENT_DATE = trunc(pProcessDate)
            And BB_BIN = 980478
            And BB_BIN_ORIG IN (SELECT TGTT_ID FROM TGTT_20) 
            And RESPCODE = 0
            And ORIGRESPCODE In (68,97) 
            And INS_PCODE Is Null
        ) A
    USING
    (
        Select *
        From ibftbackend20.V_RR_QRPAY_NONBANK_PENDING@LINKACHTEST 
        Where ORIGINAL_SETTLE_DATETIME = trunc(pProcessDate)
    ) B
    ON
    (   
        A.ORIGTRACE = TO_NUMBER(B.F11)
        And A.LOCAL_TIME = TO_NUMBER(B.F12)
        And A.LOCAL_DATE = NP_CONVERT_LOCAL_DATE(B.F13,Trunc(Sysdate))
        And A.ACQUIRER_RP = TO_NUMBER(B.ACQ_ID)
        And Trim(A.TERMID) = Trim(B.F41)
    )
    WHEN MATCHED THEN
    UPDATE 
        Set A.INS_PCODE = TRIM(SUBSTR(B.F3,0,2)),
        A.MERCHANT_TYPE_ORIG = B.F18,
        A.ACCEPTORNAME = B.F43;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,'0','Merge SHCLOG_SETT_IBFT','UPDATE_TRANS_TGTT_20', 0);
    Commit;
    /*
    v_csv_content := 'NGAY GD, GIO GD, TRACE, RESPCODE, SETTLEMENT DATE, AMOUNT, ISS, BEN'
      || CHR (10);
    v_message := v_message;
    FOR rec
         IN (SELECT *
            FROM SHCLOG_SETT_IBFT
            WHERE SETTLEMENT_DATE = trunc(pProcessDate)
                And BB_BIN = 980478
                And BB_BIN_ORIG IN (SELECT TGTT_ID FROM TGTT_20) 
                And RESPCODE = 0
                And ORIGRESPCODE In (68,97)
                And INS_PCODE Is Null)
    LOOP 
        v_row_content :=
               TO_CHAR(rec.LOCAL_DATE,'dd/mm/yyyy')
            || ','
            || rec.LOCAL_TIME
            || ','
            || rec.ORIGTRACE
            || ','
            || rec.RESPCODE
            || ','
            || rec.SETTLEMENT_DATE
            || ','
            || rec.AMOUNT
            || ','
            || rec.ISSUER_RP
            || ','
            || rec.BB_BIN_ORIG;
         v_csv_content := v_csv_content || v_row_content || CHR (10);
         
         iCount := iCount + 1;
      END LOOP;
    
    if iCount = 0 Then
        v_message :=
            'Không phát sinh GD liên thông t? 1.0 sang 2.0 c?a TGTT t?i Backend 2.0  ngày '
         || TO_CHAR (TRUNC (SYSDATE - 1), 'dd/mm/yyyy')        
         || ' b? thi?u khi d?i chi?u.';  
         
        UTL_MAIL.send (sender          => v_sender,
                                  recipients      => v_recipient,
                                  subject         => v_subject,
                                  MESSAGE         => v_message,
                                  mime_type       => 'text/plain; charset=UTF-8',
                                  cc              => vCC);  
    else
        v_message :=
            'Thi?u GD liên thông t? 1.0 sang 2.0 c?a TGTT t?i Backend 2.0 ngày '
         || TO_CHAR (TRUNC (SYSDATE - 1), 'dd/mm/yyyy')        
         || ' (xem danh sách GD và các tru?ng thông tin trong file dính kèm).';
        
        UTL_MAIL.send_attach_varchar2 (sender          => v_sender,
                                  recipients      => v_recipient,
                                  subject         => v_subject,
                                  MESSAGE         => v_message,
                                  mime_type       => 'text/plain; charset=UTF-8',
                                  attachment      => v_csv_content,
                                  att_inline      => FALSE, -- To ensure it's sent as an attachment
                                  att_mime_type   => v_mime_type,
                                  att_filename    => v_att_filename, -- Set the attachment's filename with .csv extension
                                  cc              => vCC);
 
        Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
        Values(sysdate,'0','IBFT TGTT 2.0: Thieu '||iCount|| ' GD lien thong tu 1.0 sang 2.0 cua TGTT tai backend 2.0','UPDATE_TRANS_TGTT_20', 0);
        Commit;
    End If;
    */
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,'0','END','UPDATE_TRANS_TGTT_20', 0);
    Commit;
    
EXCEPTION 
    WHEN OTHERS THEN    
    ecode := SQLCODE;
    emesg := SQLERRM;
    vDetail := ' UPDATE_TRANS_TGTT_20 Err num: ' || TO_CHAR(ecode) || ' - Err detail: ' || emesg;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE, CRITICAL)
    Values(sysdate,ecode,emesg,'UPDATE_TRANS_TGTT_20', 2);    
    Commit;
END UPDATE_TRANS_TGTT_20;
/
