CREATE OR REPLACE PROCEDURE WEBBC.DATA_TO_ECOM
(
   pQRY_FROM_DATE VARCHAR2,
   pQRY_TO_DATE   VARCHAR2
) AS
   -- Nguoi viet: Doantien
   -- Ngay: 25/12/2008
   -- Muc dich: Do du lieu tu shclog vao shclog_tmp de lap bao cao

   vMType    VARCHAR2(30) := 'text/plain; charset=us-ascii';
   vSender   VARCHAR2(40) := 'DBA@DB1';
   vReceiver VARCHAR2(60) := 'hoind@napas.com.vn;trungnv@napas.com.vn';
   vCC       VARCHAR2(30) := 'hoind@napas.com.vn';
   bCC       VARCHAR2(30) := 'hoind@napas.com.vn';
   vSub      VARCHAR2(150) := 'Do du lieu sang bang tam(New)';
   vDetail   VARCHAR2(500) := 'Nothing ! ';
   ecode     NUMBER;
   emesg     VARCHAR2(200);
   vlistsms  VARCHAR2(100) := '0983411005;0988766330';

   d1 DATE;
   d2 DATE;
BEGIN
   d1 := TO_DATE(pQRY_FROM_DATE, 'dd/MM/yyyy');
   d2 := TO_DATE(pQRY_TO_DATE, 'dd/MM/yyyy');
   DELETE FROM QR_ECOM_SUCC WHERE SWITCH_SETTLE_DATE = d2 + 1;

   INSERT INTO QR_ECOM_SUCC
      (AMOUNT, CARD_NUMBER_DETAIL, AUTHORISATION_CODE, MERCHANT_ID, F11_TRACE,
       F12_LOCAL_TIME, F13_LOCAL_DATE, F15_SETTLE_DATE, F32_ACQUIER, F41_CARD_ACCEPTOR_ID,
       F63_TRANS_SWITCH, F100_BEN, SWITCH_SETTLE_DATE, SWITCH_STATUS)
      SELECT AMOUNT, TRIM(PAN) AS CARD_NUMBER_DETAIL, AUTHNUM AS AUTHORISATION_CODE,
             TRIM(SUBSTR(ACCTNUM, INSTR(ACCTNUM || '|', '|') + 1, LENGTH(ACCTNUM))) AS MERCHANT_ID,
             Origtrace AS F11_TRACE, LOCAL_TIME AS F12_LOCAL_TIME,
             TO_CHAR(LOCAL_DATE, 'MMDD') AS F13_LOCAL_DATE,
             NVL(TO_CHAR(SETTLEMENT_DATE, 'MMDD'), '0101') AS F15_SETTLE_DATE,
             Acquirer AS F32_ACQUIER, TRIM(TERMID) AS F41_CARD_ACCEPTOR_ID,
             TRIM(ADDRESPONSE) AS F63_TRANS_SWITCH,
             -- Check F63 IPS de o dau
             BB_BIN AS F100_BEN, Trunc(SYSDATE) AS SWITCH_SETTLE_DATE,
             CASE
                WHEN RESPCODE IN (0, 1) THEN
                 TRIM(TO_CHAR(RESPCODE, '09'))
                ELSE
                 TRIM(TO_CHAR(RESPCODE, '099'))
             END
      FROM   Shclog_SETT_IBFT
      WHERE  Msgtype = 210
             AND SETTLEMENT_DATE BETWEEN d1 AND d2
             AND
             ((Respcode = 0 AND Fee_note IS NOT NULL) OR
             (Respcode = 1 AND OrigRespcode = 68) OR (Respcode = 1 AND OrigRespcode = 0) OR (Respcode = 1 AND reason_edit like '%(QRIBFTECOM)%') )
             AND BB_BIN in (971100,971111);


EXCEPTION
   WHEN OTHERS THEN
      ecode   := SQLCODE;
      emesg   := SQLERRM;
      vDetail := 'Tao du lieu cho View ECOM Err num: ' || TO_CHAR(ecode) ||
                 ' - Err detail: ' || emesg;
   
      INSERT INTO ERR_EX
         (ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE)
      VALUES
         (SYSDATE, ecode, vDetail, 'SHC_TMP');
   
      SEND_SMS('ALERT_ERR#' || vlistsms || '#' || vDetail);
   
END;