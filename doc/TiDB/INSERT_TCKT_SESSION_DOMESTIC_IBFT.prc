CREATE OR REPLACE PROCEDURE RPT.INSERT_TCKT_SESSION_DOMESTIC_IBFT
    ( 
        pQRY_FROM_DATE VARCHAR2,
        pQRY_TO_DATE VARCHAR2,
        pUser VARCHAR2
    )
AS
    ecode NUMBER;
    emesg VARCHAR2(200);
    Sett_From DATE := TO_DATE(pQRY_FROM_DATE,'dd/MM/yyyy');
    Sett_To DATE := TO_DATE(pQRY_TO_DATE,'dd/MM/yyyy');
    sLastSettDate    Date;
    vlistsms     VARCHAR2(100) :='0983411005';
    vDetail        VARCHAR2(500) := 'Nothing ! ';    
BEGIN
    Begin
        Select PARA_VALUE Into vlistsms
        From NAPAS_PARA
        Where PARA_NAME = 'LIST_SMS';
    EXCEPTION
        WHEN OTHERS THEN
            vlistsms := '0983411005';
    End;
    
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'0','Start','INSERT_TCKT_SESSION_DOMESTIC_IBFT');    

    Select To_Date(PARA_VALUE,'dd/mm/yyyy') Into  sLastSettDate
    From NAPAS_PARA
    Where PARA_NAME ='CITAD_SETT_DATE';
    
    --xoa du lieu backup > 15 ngay
    DELETE TCKT_SESSION_DOMESTIC_AUTO_BKUP WHERE INSERT_DATE < TRUNC(SYSDATE - 15);
    
    --backup du lieu truoc khi xoa
    INSERT INTO TCKT_SESSION_DOMESTIC_AUTO_BKUP (INSERT_DATE, ADJ_FEE, BANK_ID, BANK_NAME, BC_CL_ADJ, BC_NP_ADJ, BC_NP_SUM, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, CD_TOTAL_TRAN, CORR_BANK_ID, 
        CREDIT, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE, DB_TOTAL_FEE, DB_TOTAL_MONEY, DB_TOTAL_TRAN, DEBIT, EDIT_DATE, FEE_TYPE, GROUP_ROLE, GROUP_TRAN, IS_FEE, LIQUIDITY, MERCHANT_TYPE, 
        MSGTYPE_DETAIL, NAPAS_FEE, NP_ADJ_FEE, OD_BY, PART_FE, PCODE, RESPCODE, SERVICE_CODE, SERVICE_TYPE, SETTLEMENT_CURRENCY, SETT_DATE, STEP, SUB_BANK, TRAN_TYPE, WITH_BANK)
    SELECT sysdate, ADJ_FEE, BANK_ID, BANK_NAME, BC_CL_ADJ, BC_NP_ADJ, BC_NP_SUM, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, CD_TOTAL_TRAN, CORR_BANK_ID, 
        CREDIT, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE, DB_TOTAL_FEE, DB_TOTAL_MONEY, DB_TOTAL_TRAN, DEBIT, EDIT_DATE, FEE_TYPE, GROUP_ROLE, GROUP_TRAN, IS_FEE, LIQUIDITY, MERCHANT_TYPE, 
        MSGTYPE_DETAIL, NAPAS_FEE, NP_ADJ_FEE, OD_BY, PART_FE, PCODE, RESPCODE, SERVICE_CODE, SERVICE_TYPE, SETTLEMENT_CURRENCY, SETT_DATE, STEP, SUB_BANK, TRAN_TYPE, WITH_BANK
    FROM TCKT_SESSION_DOMESTIC
    Where (Trunc(INSERT_DATE) <= Trunc(sLastSettDate) Or INSERT_DATE > Trunc(Sysdate))
        and SERVICE_TYPE = 'IBFT';
    
    --xoa du lieu TCKT_SESSION_DOMESTIC cua phien cu 
    Delete TCKT_SESSION_DOMESTIC
    Where (Trunc(INSERT_DATE) <= Trunc(sLastSettDate) Or INSERT_DATE > Trunc(Sysdate))
        and SERVICE_TYPE = 'IBFT';
    Commit;
    
    -- Begin: Xu ly tong hop du lieu GD thanh cong tu bang SHCLOG_SETT_IBFT
    -- ISS-ACQ
    Insert Into TCKT_SESSION_DOMESTIC(INSERT_DATE, MSGTYPE_DETAIL,SUB_BANK,SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, CORR_BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, 
            NAPAS_FEE,ADJ_FEE,NP_ADJ_FEE, MERCHANT_TYPE, BC_NP_SUM, BC_CL_ADJ, STEP,FEE_TYPE,PART_FE,SERVICE_TYPE)
    Select sysdate,MSGTYPE_DETAIL,Case
                When ISSUER_RP = 970426 And Substr(Trim(PAN),0,8) ='97046416' Then 970464
                Else null
           End,
           Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End  SETT_DATE,
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End As EDIT_DATE, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End As SETTLEMENT_CURRENCY, RESPCODE,
        Case
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
            When PCODE2 = 930000 Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','42','48','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End As GROUP_TRAN, 
        Case
            When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
                967700,977700,987700,997700,967800,977800,987800,997800,967900,977900,987900,997900,
                966100,976100,986100,996100,966200,976200,986200,996200) Then SUBSTR(Trim(TO_CHAR(PCODE,'09')),1,2)||PCODE2
            Else SUBSTR(Trim(TO_CHAR(PCODE,'09')),1,2)
        End,
        Case 
            When PCODE2 In (750000,967500,977500,987500,997500,760000,967600,977600,987600,997600,770000,967700,977700,987700,997700) Then 'TRANSIT'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC'
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 in (840000,968400,978400,988400,998400) Then 'SSP_ON'
            When PCode2 in (850000,968500,978500,988500,998500) Then 'SSP_OFF'
            When PCode2 in (780000,967800,977800,987800,997800) Then 'BP_ON'
            When PCode2 in (790000,967900,979500,988500,997900) Then 'BP_OFF'
            When PCode2 in (610000,966100,976100,986100,996100) Then 'APPLEPAY_ON'
            When PCode2 in (620000,966200,976200,986200,996200) Then 'APPLEPAY_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When PCODE2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB'      
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'ISS-ACQ' As GROUP_ROLE,
        ISSUER_RP As BANK_ID,
        Case
            When ISSUER_RP <> ACQUIRER_RP Then ACQUIRER_RP
            Else BB_BIN
        End As CORR_BANK_ID,
        Case
            When acquirer_rp In (220699, 602907, 605609, GET_BCCARD_ID()) Then Acquirer_Rp
            When PCode2 In (810000,820000,830000,860000,870000) Then 999999
            Else 0
        End As WITH_BANK,                                       
        Sum(Case When Pcode In ('42','91') Then 0 Else 1 End) As DB_TOTAL_TRAN,
        SUM(
            Case 
                When Pcode In ('00','01','40','20') Then
                    Case
                        When PCODE = '20' And Respcode In (112,114) Then Decode(PREAMOUNT,null,0,-PREAMOUNT)
                        When PCODE = '20' Then -AMOUNT
                        When Respcode In (112,114) Then Decode(PREAMOUNT,null,0,PREAMOUNT)
                        Else        

                            Case 
                                When ACQ_CURRENCY_CODE = 418 Then SETTLEMENT_AMOUNT
                                Else 
                                    AMOUNT
                            End
                        
                    End 
                 Else 0
            End
        ) AS DB_AMOUNT,
        SUM(
            Case 
                When FEE_IRF_ISS < 0 Then -FEE_IRF_ISS Else 0 
            End
        ) As DB_IR_FEE,
        -SUM(
            Case 
                When Pcode In ('42','91') Then 0 
                Else FEE_SVF_ISS 
            End
        ) As DB_SV_FEE,
        0 As DB_TOTAL_FEE,
        0 As DB_TOTAL_MONEY,
        0 As CD_TOTAL_TRAN,
        SUM(
            Case 
                When Pcode In ('40') Then
                    Case
                        When Respcode In (112,114) Then Decode(PREAMOUNT,null,0,PREAMOUNT)
                        Else        
                            Case 
                                When ACQ_CURRENCY_CODE = 418 Then SETTLEMENT_AMOUNT
                                Else 
                                    AMOUNT
                            End
                    End
                 Else 0
            End
        ) As CD_AMOUNT,
        SUM(
            Case 
                When FEE_IRF_ISS > 0 And Decode(Pcode2,null,0,Pcode2) not in (890000,720000) 
                    Then FEE_IRF_ISS 
                Else 0 
            End
        ) As CD_IR_FEE,
        SUM(Case When FEE_SVF_ISS < 0 Then 0 Else FEE_SVF_ISS End)  As CD_SV_FEE,
        SUM(
            Case 
                When Pcode In ('40') Then
                    Case
                        When Respcode In (112,114) Then Decode(PREAMOUNT,null,0,PREAMOUNT)
                        Else        
                            Case 
                                When ACQ_CURRENCY_CODE = 418 Then SETTLEMENT_AMOUNT
                                Else 
                                    AMOUNT
                            End
                    End
                 Else 0
            End                                                            
        ) As CD_TOTAL_MONEY,
        0 As NAPAS_FEE,
        Sum(
            Case 
                When Acquirer = 220699 And Merchant_Type = 6011 And Pcode In ('01','30') Then FEE_IRF_ACQ
                When ISSUER = 220699 And Merchant_Type = 6011 And Pcode In ('01','30') Then FEE_IRF_ACQ
                Else 0
            End
        ) ADJ_FEE,
        0 As NP_ADJ_FEE,
        Case 
        When Pcode In ('41','42','48','91') Then MERCHANT_TYPE
        When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
            967800,977800,987800,997800,967900,977900,987900,997900,
            966100,976100,986100,996100,966200,976200,986200,996200) Then MERCHANT_TYPE_ORIG
        When Pcode = 0 And Merchant_type_orig in (4111, 4131,5172,9211, 9222, 9223, 9311, 9399,8398,7523,7524,5541,5542) Then MERCHANT_TYPE_ORIG
        Else Null 
        End MERCHANT_TYPE,
        SUM(
            Case 
                When Issuer_Rp = 600005 And Merchant_Type = 6011 Then FEE_IRF_ISS
                When Issuer_Rp = 600005 And Merchant_Type = 6012 Then FEE_IRF_ISS
                When Issuer_Rp = 600006 And Merchant_Type = 6012 Then FEE_IRF_ISS
                When Issuer_Rp = 600007 Then FEE_IRF_ISS
                Else 0
            End
        ) BC_NP_SUM, 
         Sum(Case When Issuer_Rp In (600005,600007) Then FEE_IRF_ISS Else 0 End) BC_CL_ADJ
        ,
        'A-BY_ROLE',
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End As FEE_TYPE,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 End As PART_FE, 'IBFT'
    From SHCLOG_SETT_IBFT 
    Where 
        (
            (Respcode = 0 And Isrev is null)
            Or
            Respcode In (110,112,113,114,115)
        )
        And Fee_Note Is not null
        And 
            (
                (
                    PCODE2 Is Null And Pcode In ('00','01','30','35','40','41','42','43','48','94','03','20')  -- Sua lay theo pcode
                )
                Or
                (
                    PCODE2 Is Not Null And Pcode In ('00','01','30','35','40','41','42','43','48','94','91','03','20')
                )
            )   
    Group By MSGTYPE_DETAIL,Case
                When ISSUER_RP = 970426 And Substr(Trim(PAN),0,8) ='97046416' Then 970464
                Else null
           End, 
            Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End , RESPCODE,
        Case
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
            When PCODE2 = 930000 Then 'IBFT' 
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','42','48','91')  Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End, 
        Case
            When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
                967700,977700,987700,997700,967800,977800,987800,997800,967900,977900,987900,997900,
                966100,976100,986100,996100,966200,976200,986200,996200) Then SUBSTR(Trim(TO_CHAR(PCODE,'09')),1,2)||PCODE2
            Else SUBSTR(Trim(TO_CHAR(PCODE,'09')),1,2)
        End,
        Case 
            When PCODE2 In (750000,967500,977500,987500,997500,760000,967600,977600,987600,997600,770000,967700,977700,987700,997700) Then 'TRANSIT'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC'
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 in (840000,968400,978400,988400,998400) Then 'SSP_ON'
            When PCode2 in (850000,968500,978500,988500,998500) Then 'SSP_OFF'
            When PCode2 in (780000,967800,977800,987800,997800) Then 'BP_ON'
            When PCode2 in (790000,967900,979500,988500,997900) Then 'BP_OFF'
            When PCode2 in (610000,966100,976100,986100,996100) Then 'APPLEPAY_ON'
            When PCode2 in (620000,966200,976200,986200,996200) Then 'APPLEPAY_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When PCODE2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                Pcode2 Is Null 
                                Or 
                                Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                              ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'                    
 
            Else 'POS'
        End,
        ISSUER_RP,
        Case
            When ISSUER_RP <> ACQUIRER_RP Then ACQUIRER_RP
            Else BB_BIN
        End,
        Case
            When acquirer_rp In (220699, 602907, 605609, GET_BCCARD_ID()) Then Acquirer_Rp
            When PCode2 In (810000,820000,830000,860000,870000) Then 999999
            Else 0
        End,
        Case 
        When Pcode In ('41','42','48','91') Then MERCHANT_TYPE
        When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
            967800,977800,987800,997800,967900,977900,987900,997900,
            966100,976100,986100,996100,966200,976200,986200,996200) Then MERCHANT_TYPE_ORIG        
        When Pcode = 0 And Merchant_type_orig in (4111, 4131,5172,9211, 9222, 9223, 9311, 9399,8398,7523,7524,5541,5542) Then MERCHANT_TYPE_ORIG
        Else Null 
        End,
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 End
    ;

    -- ACQ-ISS
    Insert Into TCKT_SESSION_DOMESTIC(INSERT_DATE, MSGTYPE_DETAIL,SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, CORR_BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, 
            NAPAS_FEE,ADJ_FEE,NP_ADJ_FEE, MERCHANT_TYPE, BC_NP_ADJ, BC_NP_SUM, STEP,FEE_TYPE, PART_FE, SERVICE_TYPE)
    Select sysdate, MSGTYPE_DETAIL,Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End SETT_DATE, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End As EDIT_DATE, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End  As SETTLEMENT_CURRENCY, RESPCODE,
        Case
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
            When PCODE2 = 930000 Then 'IBFT' 
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End As GROUP_TRAN, 
        Case
            When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
                967700,977700,987700,997700,967800,977800,987800,997800,967900,977900,987900,997900,
                966100,976100,986100,996100,966200,976200,986200,996200) Then SUBSTR(Trim(TO_CHAR(PCODE,'09')),1,2)||PCODE2
            Else SUBSTR(Trim(TO_CHAR(PCODE,'09')),1,2)
        End PCODE,
        Case 
            When PCODE2 In (750000,967500,977500,987500,997500,760000,967600,977600,987600,997600,770000,967700,977700,987700,997700) Then 'TRANSIT'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC'
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 in (840000,968400,978400,988400,998400) Then 'SSP_ON'
            When PCode2 in (850000,968500,978500,988500,998500) Then 'SSP_OFF'
            When PCode2 in (780000,967800,977800,987800,997800) Then 'BP_ON'
            When PCode2 in (790000,967900,979500,988500,997900) Then 'BP_OFF'
            When PCode2 in (610000,966100,976100,986100,996100) Then 'APPLEPAY_ON'
            When PCode2 in (620000,966200,976200,986200,996200) Then 'APPLEPAY_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When PCODE2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'
                       
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'ACQ-ISS' As GROUP_ROLE,
        ACQUIRER_RP As BANK_ID,  
        Case
            When ISSUER_RP <> ACQUIRER_RP Then ISSUER_RP
            Else BB_BIN
        End As CORR_BANK_ID,    
        Case
            When ISSUER_RP In (220699, 602907, 605609,GET_BCCARD_ID(), 600005, 600006, 600007) Then ISSUER_RP
            When PCode2 In (810000, 820000, 830000, 860000, 870000) Then 999999
            Else 0
        End As WITH_BANK,                            
        0 As DB_TOTAL_TRAN,
        0 AS DB_AMOUNT,
        SUM(
            Case 
                When ACQUIRER_RP = GET_BCCARD_ID() And FEE_IRF_ISS > 0 Then FEE_IRF_ISS
                When FEE_IRF_ACQ < 0 Then -FEE_IRF_ACQ Else 0 
            End
        )  As DB_IR_FEE,
        0 As DB_SV_FEE,
        0 As DB_TOTAL_FEE,
        0 As DB_TOTAL_MONEY,
        SUM(
            Case 
                When Pcode In ('41','42','43','48','91') Then 0
                Else 1
            End            
        ) As CD_TOTAL_TRAN,
        SUM(
            Case 
                When Pcode In ('00','01','20') Then
                    Case
                        When PCODE = '20' And Respcode In (112,114) Then Decode(PREAMOUNT,null,0,-PREAMOUNT)
                        When PCODE = '20' Then -AMOUNT
                        When Respcode In (112,114) Then Decode(PREAMOUNT,null,0,PREAMOUNT)
                        Else        
                            Case 
                                When ACQ_CURRENCY_CODE = 418 Then SETTLEMENT_AMOUNT
                                Else 
                                    AMOUNT
                            End
                    End
                Else 0    
                End                                         
        ) As CD_AMOUNT,
        SUM(
            Case 
                When Acquirer_Rp = GET_BCCARD_ID() And Merchant_Type = 6011 Then -FEE_IRF_ISS
                When FEE_IRF_ACQ > 0 And ACQUIRER_RP = GET_BCCARD_ID() And MERCHANT_TYPE <> 6011 Then FEE_IRF_ACQ
                When FEE_IRF_ACQ > 0 And ACQUIRER_RP = GET_BCCARD_ID() And MERCHANT_TYPE = 6011 Then 0
                When FEE_IRF_ACQ > 0 Then FEE_IRF_ACQ 
                Else 0 
            End
        ) As CD_IR_FEE,
        -SUM(FEE_SVF_ACQ) As CD_SV_FEE,
        0 As CD_TOTAL_MONEY,
        0 As NAPAS_FEE,
        0 As ADJ_FEE,
        Sum(
            Case 
                When FEE_KEY = 'CFC85B5F-787B-437C-823F-79534D3B72BD' Then FEE_IRF_ACQ 
                Else 0
            End                 
        ) NP_ADJ_FEE,
        Case 
        When Pcode In ('41','42','48','91') Then MERCHANT_TYPE
        When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
            967800,977800,987800,997800,967900,977900,987900,997900,
            966100,976100,986100,996100,966200,976200,986200,996200) Then MERCHANT_TYPE_ORIG
        When Pcode = 0 And Merchant_type_orig in (4111, 4131,5172,9211, 9222, 9223, 9311, 9399,8398,7523,7524,5541,5542) Then MERCHANT_TYPE_ORIG
        Else Null End, 
        -SUM(
            Case 
                When Acquirer_Rp = GET_BCCARD_ID() And Merchant_Type = 6011 Then FEE_IRF_ISS            
                When ACQUIRER_RP = GET_BCCARD_ID() And FEE_IRF_ISS > 0 And Merchant_Type <> 6011 Then FEE_IRF_ISS
                Else 0
            End
        )  As BC_NP_ADJ,
        SUM(
            Case 
                When Acquirer_Rp = GET_BCCARD_ID() And Merchant_Type = 6011 Then -FEE_IRF_ISS
                When Acquirer_Rp = GET_BCCARD_ID() And Merchant_Type <> 6011 Then -FEE_IRF_ISS
                Else 0
            End
        ) BC_NP_SUM, 
        'A-BY_ROLE',
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End as FEE_TYPE,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 
        End, 'IBFT'
    From SHCLOG_SETT_IBFT 
    Where 
        (
            (Respcode = 0 And Isrev is null)
            Or
            Respcode In (110,112,113,114,115)
        )
        And Fee_Note Is not null
        And 
            (
                (
                    PCODE2 Is Null And Pcode In ('00','01','30','35','40','41','42','43','48','94','03','20')  -- Sua lay theo pcode
                )
                Or
                (
                    PCODE2 Is Not Null And Pcode In ('00','01','30','35','40','41','42','43','48','94','91','03','20')
                )
            )
    Group By MSGTYPE_DETAIL,Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End , RESPCODE,
        Case
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
  
            When PCODE2 = 930000 Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End, 
        Case
            When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
                967700,977700,987700,997700,967800,977800,987800,997800,967900,977900,987900,997900,
                966100,976100,986100,996100,966200,976200,986200,996200) Then SUBSTR(Trim(TO_CHAR(PCODE,'09')),1,2)||PCODE2
            Else SUBSTR(Trim(TO_CHAR(PCODE,'09')),1,2)
        End,
        Case
            When PCODE2 In (750000,967500,977500,987500,997500,760000,967600,977600,987600,997600,770000,967700,977700,987700,997700) Then 'TRANSIT'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC'
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 in (840000,968400,978400,988400,998400) Then 'SSP_ON'
            When PCode2 in (850000,968500,978500,988500,998500) Then 'SSP_OFF'
            When PCode2 in (780000,967800,977800,987800,997800) Then 'BP_ON'
            When PCode2 in (790000,967900,979500,988500,997900) Then 'BP_OFF'
            When PCode2 in (610000,966100,976100,986100,996100) Then 'APPLEPAY_ON'
            When PCode2 in (620000,966200,976200,986200,996200) Then 'APPLEPAY_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When PCODE2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB' 
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'
                              
            Else 'POS'
        End,
        ACQUIRER_RP,
        Case
            When ISSUER_RP <> ACQUIRER_RP Then ISSUER_RP
            Else BB_BIN
        End,
        Case
            When ISSUER_RP In (220699, 602907, 605609,GET_BCCARD_ID(), 600005, 600006, 600007) Then ISSUER_RP
            When PCode2 In (810000, 820000, 830000, 860000, 870000) Then 999999
            Else 0
        End,
        Case 
        When Pcode In ('41','42','48','91') Then MERCHANT_TYPE
        When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
            967800,977800,987800,997800,967900,977900,987900,997900,
            966100,976100,986100,996100,966200,976200,986200,996200) Then MERCHANT_TYPE_ORIG
        When Pcode = 0 And Merchant_type_orig in (4111, 4131,5172,9211, 9222, 9223, 9311, 9399,8398,7523,7524,5541,5542) Then MERCHANT_TYPE_ORIG
        Else Null 
        End,
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 
        End
    ;
    
    -- ISS-BEN
    Insert Into TCKT_SESSION_DOMESTIC(INSERT_DATE, SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, CORR_BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, 
            NAPAS_FEE,ADJ_FEE,NP_ADJ_FEE, MERCHANT_TYPE,STEP, FEE_TYPE, SERVICE_TYPE)
    Select sysdate, Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End SETT_DATE, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End As EDIT_DATE, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End  As SETTLEMENT_CURRENCY, RESPCODE,
        Case
            When Pcode2 in (920000) Then 'QR'
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
            When PCODE2 = 930000 Then 'IBFT'  
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End As GROUP_TRAN, PCODE,
        Case 
            When Pcode2 = 920000 Then 'QR_ITMX'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC'
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 = 840000 Then 'SSP_ON'
            When PCode2 = 850000 Then 'SSP_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When Pcode2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB'      
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'
             
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'ISS-BEN' As GROUP_ROLE,
        ISSUER_RP As BANK_ID,
        BB_BIN As CORR_BANK_ID,
        Case
            When PCode2 = 920000 Then 764000
            When Pcode2 In (720000,730000,890000) Then GET_QRC_WITH(BB_BIN)
            Else 0
        End As WITH_BANK,                                   
        SUM(
           Case 
                When Pcode In ('41','43','48') Then 0
                Else 1
            End             
        ) As DB_TOTAL_TRAN,
        SUM(
            Case
                When Respcode In (112,114) Then Decode(PREAMOUNT,null,0,PREAMOUNT)
                Else        
                    Case 
                        When ACQ_CURRENCY_CODE = 418 Then SETTLEMENT_AMOUNT
                        When ACQ_CURRENCY_CODE = 764 Then SETTLEMENT_AMOUNT --- QR_ITMX
                        Else 
                            AMOUNT
                    End
            End                  
        ) AS DB_AMOUNT,
        SUM(Case 
            When 
                Case 
                    When Pcode In ('41','42','43','48','91') Then 0 
                    Else FEE_IRF_ISS 
                End < 0 
                Then -Case 
                            When Pcode In ('41','42','43','48','91') Then 0 
                            Else FEE_IRF_ISS
                      End
             Else 0
        End) As DB_IR_FEE,
        -SUM(Case When Pcode In ('41','43','48') Then 0 Else FEE_SVF_ISS End) As DB_SV_FEE,
        0 As DB_TOTAL_FEE,
        0 As DB_TOTAL_MONEY,
        0 As CD_TOTAL_TRAN,
        0 As CD_AMOUNT,
        SUM(Case When FEE_IRF_ISS > 0 Then FEE_IRF_ISS Else 0 End) As CD_IR_FEE,
        SUM(Case When FEE_SVF_ISS < 0 Then 0 Else FEE_SVF_ISS End)  As CD_SV_FEE,
        0 As CD_TOTAL_MONEY,
        0 As NAPAS_FEE,
        0 As ADJ_FEE,
        0 As NP_ADJ_FEE,
        Case 
        When Pcode In ('41','42','48','91') Then MERCHANT_TYPE 
        When Pcode2 In (960000,970000,968400,978400,968500,978500,967500,977500,967600,977600) Then MERCHANT_TYPE_ORIG
        Else Null 
        End MERCHANT_TYPE,'A-BY_ROLE',
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End as FEE_TYPE, 'IBFT'
    From SHCLOG_SETT_IBFT
    Where 
        (
            (Respcode = 0 And Isrev is null)
            Or
            Respcode In (110,112,113,114,115)
        )
        And Msgtype = 210
        And Fee_note is not null
        And Pcode In ('41','42','43','48','91')
    Group By Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End , RESPCODE,
        Case
            When Pcode2 in(920000) Then 'QR'
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
            When PCODE2 = 930000 Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End, PCODE,
        Case 
            When Pcode2 = 920000 Then 'QR_ITMX'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC'
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 = 840000 Then 'SSP_ON'
            When PCode2 = 850000 Then 'SSP_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When Pcode2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB'  
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'
            Else 'POS'
        End,
        ISSUER_RP,
        BB_BIN,
        Case
            When PCode2 = 920000 Then 764000
            When Pcode2 In (720000,730000,890000) Then GET_QRC_WITH(BB_BIN)
            Else 0
        End,
        Case 
        When Pcode In ('41','42','48','91') Then MERCHANT_TYPE 
        When Pcode2 In (960000,970000,968400,978400,968500,978500,967500,977500,967600,977600) Then MERCHANT_TYPE_ORIG
        Else Null 
        End,
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End
    ;

    -- BEN-ISS
    Insert Into TCKT_SESSION_DOMESTIC(INSERT_DATE, SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, CORR_BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY,
            NAPAS_FEE, ADJ_FEE,NP_ADJ_FEE, MERCHANT_TYPE,STEP, FEE_TYPE, SERVICE_TYPE)
    Select sysdate, Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End SETT_DATE, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End As EDIT_DATE, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End  As SETTLEMENT_CURRENCY, RESPCODE,
        Case
            When Pcode2 in (920000) Then 'QR'
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
            When PCODE2 = 930000 Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End As GROUP_TRAN, PCODE, 
        Case 
            When Pcode2 = 920000 Then 'QR_ITMX'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC' 
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 = 840000 Then 'SSP_ON'
            When PCode2 = 850000 Then 'SSP_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When Pcode2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB' 
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'
 
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'BEN-ISS' As GROUP_ROLE,
        BB_BIN As BANK_ID, 
        ISSUER_RP As CORR_BANK_ID,
        Case
            When PCode2 = 920000 Then 764000
            When ISSUER_RP In (605609) Then Issuer_Rp
            When Pcode2 In (720000,730000,890000) Then GET_QRC_WITH(Issuer_Rp)
            Else 0
        End As WITH_BANK,                                    
        0 As DB_TOTAL_TRAN,
        0 AS DB_AMOUNT,
        SUM(Case When FEE_IRF_BEN < 0 Then -FEE_IRF_BEN Else 0 End)  As DB_IR_FEE,
        0 As DB_SV_FEE,
        0 As DB_TOTAL_FEE,
        0 As DB_TOTAL_MONEY,
        Count(*) As CD_TOTAL_TRAN,
        SUM(
            Case
                When Respcode In (112,114) Then Decode(PREAMOUNT,null,0,PREAMOUNT)
                Else AMOUNT
            End                                            
        ) As CD_AMOUNT,
        SUM(Case 
            When Case When Pcode = '48' Then 0 Else Case When FEE_IRF_BEN > 0 Then FEE_IRF_BEN Else 0 End End > 0 
            Then Case When Pcode = '48' Then 0 Else Case When FEE_IRF_BEN > 0 Then FEE_IRF_BEN Else 0 End End
            Else 0 
        End) As CD_IR_FEE,
        -SUM(FEE_SVF_BEN) As CD_SV_FEE,
        0 As CD_TOTAL_MONEY,
        0 As NAPAS_FEE,
        0 As ADJ_FEE,
        0 As NP_ADJ_FEE,
        Case 
        When Pcode In ('41','42','48','91') Then MERCHANT_TYPE 
        When Pcode2 In (960000,970000,968400,978400,968500,978500,967500,977500,967600,977600) Then MERCHANT_TYPE_ORIG
        Else Null 
        End MERCHANT_TYPE,'A-BY_ROLE',
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End as FEE_TYPE, 'IBFT'
    From SHCLOG_SETT_IBFT 
    Where 
        (
            (Respcode = 0 And Isrev is null)
            Or
            Respcode In (110,112,113,114,115)
        )
        And Msgtype = 210
        And Fee_note is not null
        And Pcode In ('41','42','43','48','91')
    Group By Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End , RESPCODE,
        Case
            When Pcode2 in (920000) Then 'QR'
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
            When PCODE2 = 930000 Then 'IBFT'  
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End, PCODE, 
        Case 
            When Pcode2 = 920000 Then 'QR_ITMX'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC'
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 = 840000 Then 'SSP_ON'
            When PCode2 = 850000 Then 'SSP_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When PCODE2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB' 
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'
 
            Else 'POS'
        End,
        BB_BIN,
        ISSUER_RP,
        Case
            When PCode2 = 920000 Then 764000
            When ISSUER_RP In (605609) Then Issuer_Rp
            When Pcode2 In (720000,730000,890000) Then GET_QRC_WITH(Issuer_Rp)
            Else 0
        End,
        Case When Pcode In ('41','42','48','91') Then MERCHANT_TYPE
        When Pcode2 In (960000,970000,968400,978400,968500,978500,967500,977500,967600,977600) Then MERCHANT_TYPE_ORIG
        Else Null 
        End,
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End
    ;    
    -- End: Ket thuc phan do du lieu giao dich thanh cong tu SHCLOG_SET_IBFT vao TCKT_SESSION_DOMESTIC
    
    -- Begin: Bat dau do du lieu giao dich dieu chinh tu SHCLOG_SETT_IBFT_ADJUST vao TCKT_SESSION_DOMESTIC
    -- ISS-ACQ
    Insert Into TCKT_SESSION_DOMESTIC(INSERT_DATE, MSGTYPE_DETAIL,SUB_BANK,SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, CORR_BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, 
            NAPAS_FEE,ADJ_FEE,NP_ADJ_FEE, MERCHANT_TYPE, BC_NP_SUM, BC_CL_ADJ, STEP,FEE_TYPE, PART_FE, SERVICE_TYPE)
    Select sysdate, MSGTYPE_DETAIL,Case
                When ISSUER_RP = 970426 And Substr(Trim(PAN),0,8) ='97046416' Then 970464
                Else null
           End,
           Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End  SETT_DATE,
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End As EDIT_DATE, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End As SETTLEMENT_CURRENCY, RESPCODE,
        Case
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
            When PCODE2 = 930000 Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','42','48','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End As GROUP_TRAN, 
        Case
            When Issuer_Rp = 602907 Then Decode(PCODE_ORIG,null,SUBSTR(Trim(TO_CHAR(PCODE,'09')),1,2),Pcode_Orig)
            When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
                967700,977700,987700,997700,967800,977800,987800,997800,967900,977900,987900,997900,
                966100,976100,986100,996100,966200,976200,986200,996200) Then PCODE||PCODE2
            Else PCODE
        End,
        Case 
            When PCODE2 In (750000,967500,977500,987500,997500,760000,967600,977600,987600,997600,770000,967700,977700,987700,997700) Then 'TRANSIT'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC'
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 in (840000,968400,978400,988400,998400) Then 'SSP_ON'
            When PCode2 in (850000,968500,978500,988500,998500) Then 'SSP_OFF'
            When PCode2 in (780000,967800,977800,987800,997800) Then 'BP_ON'
            When PCode2 in (790000,967900,979500,988500,997900) Then 'BP_OFF'
            When PCode2 in (610000,966100,976100,986100,996100) Then 'APPLEPAY_ON'
            When PCode2 in (620000,966200,976200,986200,996200) Then 'APPLEPAY_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When PCODE2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB'      
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'ISS-ACQ' As GROUP_ROLE,
        ISSUER_RP As BANK_ID,
        Case
            When ISSUER_RP <> ACQUIRER_RP Then ACQUIRER_RP
            Else BB_BIN
        End As CORR_BANK_ID,
        Case
            When acquirer_rp In (220699, 602907, 605609, GET_BCCARD_ID()) Then Acquirer_Rp
            When PCode2 In (810000,820000,830000,860000,870000) Then 999999
            Else 0
        End As WITH_BANK,                                       
        Sum(Case When Pcode In ('42','91') Then 0 Else 1 End) As DB_TOTAL_TRAN,
        SUM(
            Case 
                When Pcode In ('00','01','40','20') Then
                    Case
                        When PCODE = '20' And Respcode In (112,114) Then Decode(PREAMOUNT,null,0,-PREAMOUNT)
                        When PCODE = '20' Then -AMOUNT
                        When Respcode In (112,114) Then Decode(PREAMOUNT,null,0,PREAMOUNT)
                        Else        

                            Case 
                                When ACQ_CURRENCY_CODE = 418 Then SETTLEMENT_AMOUNT
                                Else 
                                    AMOUNT
                            End
                        
                    End 
                 Else 0
            End
        ) AS DB_AMOUNT,
        SUM(
            Case 
                When FEE_IRF_ISS < 0 Then -FEE_IRF_ISS Else 0 
            End
        ) As DB_IR_FEE,
        -SUM(
            Case 
                When Pcode In ('42','91') Then 0 
                Else FEE_SVF_ISS 
            End
        ) As DB_SV_FEE,
        0 As DB_TOTAL_FEE,
        0 As DB_TOTAL_MONEY,
        0 As CD_TOTAL_TRAN,
        SUM(
            Case 
                When Pcode In ('40') Then
                    Case
                        When Respcode In (112,114) Then Decode(PREAMOUNT,null,0,PREAMOUNT)
                        Else        
                            Case 
                                When ACQ_CURRENCY_CODE = 418 Then SETTLEMENT_AMOUNT
                                Else 
                                    AMOUNT
                            End
                    End
                 Else 0
            End
        ) As CD_AMOUNT,
        SUM(
            Case 
                When FEE_IRF_ISS > 0 And Decode(Pcode2,null,0,Pcode2) not in (890000,720000) 
                    Then FEE_IRF_ISS 
                Else 0 
            End
        ) As CD_IR_FEE,
        SUM(Case When FEE_SVF_ISS < 0 Then 0 Else FEE_SVF_ISS End)  As CD_SV_FEE,
        SUM(
            Case 
                When Pcode In ('40') Then
                    Case
                        When Respcode In (112,114) Then Decode(PREAMOUNT,null,0,PREAMOUNT)
                        Else        
                            Case 
                                When ACQ_CURRENCY_CODE = 418 Then SETTLEMENT_AMOUNT
                                Else 
                                    AMOUNT
                            End
                    End
                 Else 0
            End                                                            
        ) As CD_TOTAL_MONEY,
        0 As NAPAS_FEE,
        Sum(
            Case 
                When Acquirer = 220699 And Merchant_Type = 6011 And Pcode In ('01','30') Then FEE_IRF_ACQ
                When ISSUER = 220699 And Merchant_Type = 6011 And Pcode In ('01','30') Then FEE_IRF_ACQ
                Else 0
            End
        ) ADJ_FEE,
        0 As NP_ADJ_FEE,
        Case 
        When Pcode In ('41','42','48','91') Then MERCHANT_TYPE
        When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
            967800,977800,987800,997800,967900,977900,987900,997900,
            966100,976100,986100,996100,966200,976200,986200,996200) Then MERCHANT_TYPE_ORIG
        When Pcode = 0 And Merchant_type_orig in (4111, 4131,5172,9211, 9222, 9223, 9311, 9399,8398,7523,7524,5541,5542) Then MERCHANT_TYPE_ORIG
        Else Null 
        End MERCHANT_TYPE,
        SUM(
            Case 
                When Issuer_Rp = 600005 And Merchant_Type = 6011 Then FEE_IRF_ISS
                When Issuer_Rp = 600005 And Merchant_Type = 6012 Then FEE_IRF_ISS
                When Issuer_Rp = 600006 And Merchant_Type = 6012 Then FEE_IRF_ISS
                When Issuer_Rp = 600007 Then FEE_IRF_ISS
                Else 0
            End
        ) BC_NP_SUM, 
         Sum(Case When Issuer_Rp In (600005,600007) Then FEE_IRF_ISS Else 0 End) BC_CL_ADJ
        ,
        'A-BY_ROLE',
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End As FEE_TYPE,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 End As PART_FE, 'IBFT'
    From SHCLOG_SETT_IBFT_ADJUST
    Where 
        (
            (Respcode = 0 And Isrev = 0)
            Or
            Respcode In (110,112,113,114,115)
        )
        And 
            (
                (
                    PCODE2 Is Null And Pcode In ('00','01','30','35','40','41','42','43','48','94','03','20')  -- Sua lay theo pcode
                )
                Or
                (
                    PCODE2 Is Not Null And Pcode In ('00','01','30','35','40','41','42','43','48','94','91','03','20')
                )
            )
    Group By MSGTYPE_DETAIL,Case
                When ISSUER_RP = 970426 And Substr(Trim(PAN),0,8) ='97046416' Then 970464
                Else null
           End, 
            Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End , RESPCODE,
        Case
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
            When PCODE2 = 930000 Then 'IBFT' 
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','42','48','91')  Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End, 
        Case
            When Issuer_Rp = 602907 Then Decode(PCODE_ORIG,null,SUBSTR(Trim(TO_CHAR(PCODE,'09')),1,2),Pcode_Orig)
            When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
                967700,977700,987700,997700,967800,977800,987800,997800,967900,977900,987900,997900,
                966100,976100,986100,996100,966200,976200,986200,996200) Then PCODE||PCODE2
            Else PCODE
        End,
        Case 
            When PCODE2 In (750000,967500,977500,987500,997500,760000,967600,977600,987600,997600,770000,967700,977700,987700,997700) Then 'TRANSIT'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC'
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 in (840000,968400,978400,988400,998400) Then 'SSP_ON'
            When PCode2 in (850000,968500,978500,988500,998500) Then 'SSP_OFF'
            When PCode2 in (780000,967800,977800,987800,997800) Then 'BP_ON'
            When PCode2 in (790000,967900,979500,988500,997900) Then 'BP_OFF'
            When PCode2 in (610000,966100,976100,986100,996100) Then 'APPLEPAY_ON'
            When PCode2 in (620000,966200,976200,986200,996200) Then 'APPLEPAY_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When PCODE2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                Pcode2 Is Null 
                                Or 
                                Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                              ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'                    
 
            Else 'POS'
        End,
        ISSUER_RP,
        Case
            When ISSUER_RP <> ACQUIRER_RP Then ACQUIRER_RP
            Else BB_BIN
        End,
        Case
            When acquirer_rp In (220699, 602907, 605609, GET_BCCARD_ID()) Then Acquirer_Rp
            When PCode2 In (810000,820000,830000,860000,870000) Then 999999
            Else 0
        End,
        Case 
        When Pcode In ('41','42','48','91') Then MERCHANT_TYPE
        When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
            967800,977800,987800,997800,967900,977900,987900,997900,
            966100,976100,986100,996100,966200,976200,986200,996200) Then MERCHANT_TYPE_ORIG        
        When Pcode = 0 And Merchant_type_orig in (4111, 4131,5172,9211, 9222, 9223, 9311, 9399,8398,7523,7524,5541,5542) Then MERCHANT_TYPE_ORIG
        Else Null 
        End,
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 End
    ;

    -- ACQ-ISS
    Insert Into TCKT_SESSION_DOMESTIC(INSERT_DATE, MSGTYPE_DETAIL,SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, CORR_BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, 
            NAPAS_FEE,ADJ_FEE,NP_ADJ_FEE, MERCHANT_TYPE, BC_NP_ADJ, BC_NP_SUM, STEP,FEE_TYPE, PART_FE, SERVICE_TYPE)
    Select sysdate, MSGTYPE_DETAIL,Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End SETT_DATE, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End As EDIT_DATE, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End  As SETTLEMENT_CURRENCY, RESPCODE,
        Case
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
            When PCODE2 = 930000 Then 'IBFT' 
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End As GROUP_TRAN, 
        Case
            When Issuer_Rp = 602907 Then Decode(PCODE_ORIG,null,SUBSTR(Trim(TO_CHAR(PCODE,'09')),1,2),Pcode_Orig)
            When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
                967700,977700,987700,997700,967800,977800,987800,997800,967900,977900,987900,997900,
                966100,976100,986100,996100,966200,976200,986200,996200) Then PCODE||PCODE2
            Else PCODE
        End PCODE,
        Case 
            When PCODE2 In (750000,967500,977500,987500,997500,760000,967600,977600,987600,997600,770000,967700,977700,987700,997700) Then 'TRANSIT'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC'
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 in (840000,968400,978400,988400,998400) Then 'SSP_ON'
            When PCode2 in (850000,968500,978500,988500,998500) Then 'SSP_OFF'
            When PCode2 in (780000,967800,977800,987800,997800) Then 'BP_ON'
            When PCode2 in (790000,967900,979500,988500,997900) Then 'BP_OFF'
            When PCode2 in (610000,966100,976100,986100,996100) Then 'APPLEPAY_ON'
            When PCode2 in (620000,966200,976200,986200,996200) Then 'APPLEPAY_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When PCODE2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'
                       
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'ACQ-ISS' As GROUP_ROLE,
        ACQUIRER_RP As BANK_ID, 
        Case
            When ISSUER_RP <> ACQUIRER_RP Then ISSUER_RP
            Else BB_BIN
        End As CORR_BANK_ID,        
        Case
            When ISSUER_RP In (220699, 602907, 605609,GET_BCCARD_ID(), 600005, 600006, 600007) Then ISSUER_RP
            When PCode2 In (810000, 820000, 830000, 860000, 870000) Then 999999
            Else 0
        End As WITH_BANK,                            
        0 As DB_TOTAL_TRAN,
        0 AS DB_AMOUNT,
        SUM(
            Case 
                When ACQUIRER_RP = GET_BCCARD_ID() And FEE_IRF_ISS > 0 Then FEE_IRF_ISS
                When FEE_IRF_ACQ < 0 Then -FEE_IRF_ACQ Else 0 
            End
        )  As DB_IR_FEE,
        0 As DB_SV_FEE,
        0 As DB_TOTAL_FEE,
        0 As DB_TOTAL_MONEY,
        SUM(
            Case 
                When Pcode In ('41','42','43','48','91') Then 0
                Else 1
            End            
        ) As CD_TOTAL_TRAN,
        SUM(
            Case 
                When Pcode In ('00','01','20') Then
                    Case
                        When PCODE = '20' And Respcode In (112,114) Then Decode(PREAMOUNT,null,0,-PREAMOUNT)
                        When PCODE = '20' Then -AMOUNT
                        When Respcode In (112,114) Then Decode(PREAMOUNT,null,0,PREAMOUNT)
                        Else        
                            Case 
                                When ACQ_CURRENCY_CODE = 418 Then SETTLEMENT_AMOUNT
                                Else 
                                    AMOUNT
                            End
                    End
                Else 0    
                End                                         
        ) As CD_AMOUNT,
        SUM(
            Case 
                When Acquirer_Rp = GET_BCCARD_ID() And Merchant_Type = 6011 Then -FEE_IRF_ISS
                When FEE_IRF_ACQ > 0 And ACQUIRER_RP = GET_BCCARD_ID() And MERCHANT_TYPE <> 6011 Then FEE_IRF_ACQ
                When FEE_IRF_ACQ > 0 And ACQUIRER_RP = GET_BCCARD_ID() And MERCHANT_TYPE = 6011 Then 0
                When FEE_IRF_ACQ > 0 Then FEE_IRF_ACQ 
                Else 0 
            End
        ) As CD_IR_FEE,
        -SUM(FEE_SVF_ACQ) As CD_SV_FEE,
        0 As CD_TOTAL_MONEY,
        0 As NAPAS_FEE,
        0 As ADJ_FEE,
        Sum(
            Case 
                When FEE_KEY = 'CFC85B5F-787B-437C-823F-79534D3B72BD' Then FEE_IRF_ACQ 
                Else 0
            End                 
        ) NP_ADJ_FEE,
        Case 
        When Pcode In ('41','42','48','91') Then MERCHANT_TYPE
        When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
            967800,977800,987800,997800,967900,977900,987900,997900,
            966100,976100,986100,996100,966200,976200,986200,996200) Then MERCHANT_TYPE_ORIG
        When Pcode = 0 And Merchant_type_orig in (4111, 4131,5172,9211, 9222, 9223, 9311, 9399,8398,7523,7524,5541,5542) Then MERCHANT_TYPE_ORIG
        Else Null End, 
        -SUM(
            Case 
                When Acquirer_Rp = GET_BCCARD_ID() And Merchant_Type = 6011 Then FEE_IRF_ISS            
                When ACQUIRER_RP = GET_BCCARD_ID() And FEE_IRF_ISS > 0 And Merchant_Type <> 6011 Then FEE_IRF_ISS
                Else 0
            End
        )  As BC_NP_ADJ,
        SUM(
            Case 
                When Acquirer_Rp = GET_BCCARD_ID() And Merchant_Type = 6011 Then -FEE_IRF_ISS
                When Acquirer_Rp = GET_BCCARD_ID() And Merchant_Type <> 6011 Then -FEE_IRF_ISS
                Else 0
            End
        ) BC_NP_SUM, 
        'A-BY_ROLE',
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End as FEE_TYPE,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 
        End, 'IBFT'
    From SHCLOG_SETT_IBFT_ADJUST
    Where 
        (
            (Respcode = 0 And Isrev = 0)
            Or
            Respcode In (110,112,113,114,115)
        )
        And 
            (
                (
                    PCODE2 Is Null And Pcode In ('00','01','30','35','40','41','42','43','48','94','03','20')  -- Sua lay theo pcode
                )
                Or
                (
                    PCODE2 Is Not Null And Pcode In ('00','01','30','35','40','41','42','43','48','94','91','03','20')
                )
            )
    Group By MSGTYPE_DETAIL,Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End , RESPCODE,
        Case
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
  
            When PCODE2 = 930000 Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End, 
        Case
            When Issuer_Rp = 602907 Then Decode(PCODE_ORIG,null,SUBSTR(Trim(TO_CHAR(PCODE,'09')),1,2),Pcode_Orig)
            When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
                967700,977700,987700,997700,967800,977800,987800,997800,967900,977900,987900,997900,
                966100,976100,986100,996100,966200,976200,986200,996200) Then PCODE||PCODE2
            Else PCODE
        End,
        Case
            When PCODE2 In (750000,967500,977500,987500,997500,760000,967600,977600,987600,997600,770000,967700,977700,987700,997700) Then 'TRANSIT'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC'
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 in (840000,968400,978400,988400,998400) Then 'SSP_ON'
            When PCode2 in (850000,968500,978500,988500,998500) Then 'SSP_OFF'
            When PCode2 in (780000,967800,977800,987800,997800) Then 'BP_ON'
            When PCode2 in (790000,967900,979500,988500,997900) Then 'BP_OFF'
            When PCode2 in (610000,966100,976100,986100,996100) Then 'APPLEPAY_ON'
            When PCode2 in (620000,966200,976200,986200,996200) Then 'APPLEPAY_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When PCODE2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB' 
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'
                              
            Else 'POS'
        End,
        ACQUIRER_RP,
        Case
            When ISSUER_RP <> ACQUIRER_RP Then ISSUER_RP
            Else BB_BIN
        End,
        Case
            When ISSUER_RP In (220699, 602907, 605609,GET_BCCARD_ID(), 600005, 600006, 600007) Then ISSUER_RP
            When PCode2 In (810000, 820000, 830000, 860000, 870000) Then 999999
            Else 0
        End,
        Case 
        When Pcode In ('41','42','48','91') Then MERCHANT_TYPE
        When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
            967800,977800,987800,997800,967900,977900,987900,997900,
            966100,976100,986100,996100,966200,976200,986200,996200) Then MERCHANT_TYPE_ORIG
        When Pcode = 0 And Merchant_type_orig in (4111, 4131,5172,9211, 9222, 9223, 9311, 9399,8398,7523,7524,5541,5542) Then MERCHANT_TYPE_ORIG
        Else Null 
        End,
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 
        End
    ;

    -- ISS-BEN
    Insert Into TCKT_SESSION_DOMESTIC(INSERT_DATE, SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, CORR_BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, 
            NAPAS_FEE,ADJ_FEE,NP_ADJ_FEE, MERCHANT_TYPE, STEP, FEE_TYPE, SERVICE_TYPE)
    Select sysdate, Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End SETT_DATE, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End As EDIT_DATE, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End  As SETTLEMENT_CURRENCY, RESPCODE,
        Case
            When Pcode2 in (920000) Then 'QR'
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
            When PCODE2 = 930000 Then 'IBFT'  
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End As GROUP_TRAN, PCODE,
        Case 
            When Pcode2 = 920000 Then 'QR_ITMX'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC'
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 = 840000 Then 'SSP_ON'
            When PCode2 = 850000 Then 'SSP_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When Pcode2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB'      
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'
             
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'ISS-BEN' As GROUP_ROLE,
        ISSUER_RP As BANK_ID, 
        BB_BIN As CORR_BANK_ID,        
        Case
            When PCode2 = 920000 Then 764000
            When Pcode2 In (720000,730000,890000) Then GET_QRC_WITH(BB_BIN)
            Else 0
        End As WITH_BANK,                                   
        SUM(
           Case 
                When Pcode In ('41','43','48') Then 0
                Else 1
            End             
        ) As DB_TOTAL_TRAN,
        SUM(
            Case
                When Respcode In (112,114) Then Decode(PREAMOUNT,null,0,PREAMOUNT)
                Else        
                    Case 
                        When ACQ_CURRENCY_CODE = 418 Then SETTLEMENT_AMOUNT
                        When ACQ_CURRENCY_CODE = 764 Then SETTLEMENT_AMOUNT --- QR_ITMX
                        Else 
                            AMOUNT
                    End
            End                  
        ) AS DB_AMOUNT,
        SUM(Case 
            When 
                Case 
                    When Pcode In ('41','42','43','48','91') Then 0 
                    Else FEE_IRF_ISS 
                End < 0 
                Then -Case 
                            When Pcode In ('41','42','43','48','91') Then 0 
                            Else FEE_IRF_ISS
                      End
             Else 0
        End) As DB_IR_FEE,
        -SUM(Case When Pcode In ('41','43','48') Then 0 Else FEE_SVF_ISS End) As DB_SV_FEE,
        0 As DB_TOTAL_FEE,
        0 As DB_TOTAL_MONEY,
        0 As CD_TOTAL_TRAN,
        0 As CD_AMOUNT,
        SUM(Case When FEE_IRF_ISS > 0 Then FEE_IRF_ISS Else 0 End) As CD_IR_FEE,
        SUM(Case When FEE_SVF_ISS < 0 Then 0 Else FEE_SVF_ISS End)  As CD_SV_FEE,
        0 As CD_TOTAL_MONEY,
        0 As NAPAS_FEE,
        0 As ADJ_FEE,
        0 As NP_ADJ_FEE,
        Case 
        When Pcode In ('41','42','48','91') Then MERCHANT_TYPE 
        When Pcode2 In (960000,970000,968400,978400,968500,978500,967500,977500,967600,977600) Then MERCHANT_TYPE_ORIG
        Else Null 
        End MERCHANT_TYPE,'A-BY_ROLE',
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End as FEE_TYPE, 'IBFT'
    From SHCLOG_SETT_IBFT_ADJUST 
    Where 
        (
            (Respcode = 0 And Isrev = 0)
            Or
            Respcode In (110,112,113,114,115)
        )
        And Msgtype = 210
        And Pcode In ('41','42','43','48','91')
    Group By Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End , RESPCODE,
        Case
            When Pcode2 in(920000) Then 'QR'
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
            When PCODE2 = 930000 Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End, PCODE,
        Case 
            When Pcode2 = 920000 Then 'QR_ITMX'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC'
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 = 840000 Then 'SSP_ON'
            When PCode2 = 850000 Then 'SSP_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When Pcode2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB'  
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'
            Else 'POS'
        End,
        ISSUER_RP,
        BB_BIN,
        Case
            When PCode2 = 920000 Then 764000
            When Pcode2 In (720000,730000,890000) Then GET_QRC_WITH(BB_BIN)
            Else 0
        End,
        Case 
        When Pcode In ('41','42','48','91') Then MERCHANT_TYPE 
        When Pcode2 In (960000,970000,968400,978400,968500,978500,967500,977500,967600,977600) Then MERCHANT_TYPE_ORIG
        Else Null 
        End,
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End
    ;

    -- BEN-ISS
    Insert Into TCKT_SESSION_DOMESTIC(INSERT_DATE, SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, CORR_BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY,
            NAPAS_FEE, ADJ_FEE, NP_ADJ_FEE, MERCHANT_TYPE, STEP, FEE_TYPE, SERVICE_TYPE)
    Select sysdate, Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End SETT_DATE, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End As EDIT_DATE, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End  As SETTLEMENT_CURRENCY, RESPCODE,
        Case
            When Pcode2 in (920000) Then 'QR'
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
            When PCODE2 = 930000 Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End As GROUP_TRAN, PCODE, 
        Case 
            When Pcode2 = 920000 Then 'QR_ITMX'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC' 
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 = 840000 Then 'SSP_ON'
            When PCode2 = 850000 Then 'SSP_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When Pcode2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB' 
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'
 
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'BEN-ISS' As GROUP_ROLE,
        BB_BIN As BANK_ID, 
        ISSUER_RP As CORR_BANK_ID,
        Case
            When PCode2 = 920000 Then 764000
            When ISSUER_RP In (605609) Then Issuer_Rp
            When Pcode2 In (720000,730000,890000) Then GET_QRC_WITH(Issuer_Rp)
            Else 0
        End As WITH_BANK,                                    
        0 As DB_TOTAL_TRAN,
        0 AS DB_AMOUNT,
        SUM(Case When FEE_IRF_BEN < 0 Then -FEE_IRF_BEN Else 0 End)  As DB_IR_FEE,
        0 As DB_SV_FEE,
        0 As DB_TOTAL_FEE,
        0 As DB_TOTAL_MONEY,
        Count(*) As CD_TOTAL_TRAN,
        SUM(
            Case
                When Respcode In (112,114) Then Decode(PREAMOUNT,null,0,PREAMOUNT)
                Else AMOUNT
            End                                            
        ) As CD_AMOUNT,
        SUM(Case 
            When Case When Pcode = '48' Then 0 Else Case When FEE_IRF_BEN > 0 Then FEE_IRF_BEN Else 0 End End > 0 
            Then Case When Pcode = '48' Then 0 Else Case When FEE_IRF_BEN > 0 Then FEE_IRF_BEN Else 0 End End
            Else 0 
        End) As CD_IR_FEE,
        -SUM(FEE_SVF_BEN) As CD_SV_FEE,
        0 As CD_TOTAL_MONEY,
        0 As NAPAS_FEE,
        0 As ADJ_FEE,
        0 As NP_ADJ_FEE,
        Case 
        When Pcode In ('41','42','48','91') Then MERCHANT_TYPE 
        When Pcode2 In (960000,970000,968400,978400,968500,978500,967500,977500,967600,977600) Then MERCHANT_TYPE_ORIG
        Else Null 
        End MERCHANT_TYPE,'A-BY_ROLE',
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End as FEE_TYPE, 'IBFT'
    From SHCLOG_SETT_IBFT_ADJUST
    Where 
        (
            (Respcode = 0 And Isrev = 0)
            Or
            Respcode In (110,112,113,114,115)
        )
        And Msgtype = 210
        And Pcode In ('41','42','43','48','91')
    Group By Case
                When Respcode = 0 And SETTLEMENT_DATE < Sett_From Then Sett_From
                When Respcode = 0 And SETTLEMENT_DATE > Sett_To Then Sett_To
                When Respcode = 0 And SETTLEMENT_DATE Between Sett_From And Sett_To Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When Trunc(Edit_Date) < Sett_From Then Sett_From
                    Else Trunc(Edit_Date)
                End                    
        End, 
        Case 
                    When ACQ_CURRENCY_CODE = 840 Then 840 
                    When ACQ_CURRENCY_CODE = 418 Then 418
                    Else 704 
        End , RESPCODE,
        Case
            When Pcode2 in (920000) Then 'QR'
            When Pcode2 = 890000  Then 'QRPAY'
            When Pcode2 = 720000  Then 'E-Wallet'
            When Pcode2 = 730000  Then 'EFT'
            When Pcode In ('43') And Pcode2 Is Null Then 'CBFT'
            When PCODE2 = 930000 Then 'IBFT'  
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End, PCODE, 
        Case 
            When Pcode2 = 920000 Then 'QR_ITMX'
            When PCode2 = 890000 Then 'QRC'
            When PCode2 = 720000 Then 'CAOT'
            When PCode2 = 730000 Then 'EFTC'
            When PCode2 = 810000 Then 'CA5'
            When PCode2 = 820000 Then 'CA4'
            When PCode2 = 830000 Then 'CA2'
            When PCode2 = 840000 Then 'SSP_ON'
            When PCode2 = 850000 Then 'SSP_OFF'
            When PCode2 = 860000 Then 'CA1'
            When PCode2 = 870000 Then 'CA3'
            When PCODE2 = 930000 Then 'QR_IBFT'
            When PCODE2 = 950000 Then 'Mobile IBFT'
            When merchant_type = 6011 And Pcode not In ('41','42','48','91') And (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST'
                                          ) then 'MOB' 
            When Decode(FROM_SYS,null,'IST',FROM_SYS) = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khác'
            When Decode(FROM_SYS,null,'IST',FROM_SYS) in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khác'
 
            Else 'POS'
        End,
        BB_BIN,
        ISSUER_RP,
        Case
            When PCode2 = 920000 Then 764000
            When ISSUER_RP In (605609) Then Issuer_Rp
            When Pcode2 In (720000,730000,890000) Then GET_QRC_WITH(Issuer_Rp)
            Else 0
        End,
        Case When Pcode In ('41','42','48','91') Then MERCHANT_TYPE
        When Pcode2 In (960000,970000,968400,978400,968500,978500,967500,977500,967600,977600) Then MERCHANT_TYPE_ORIG
        Else Null 
        End,
        Case
            When PCODE2 = 930000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 130012 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 930000 And ISSUER_FE = 971100 Then 'QR_IBFT_FEE'
            When PCODE2 = 950000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 950000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 950000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 950000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 950000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 950000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 950000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 950000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 950000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 950000 And ISSUER_FE  = 980471 Then 'ACH_FEE'
            When PCODE2 = 910000 And ISSUER_FE = 130001 Then 'COVID_FEE'
            When PCODE2 = 910000 And ISSUER_FE in (130002,130003) Then 'AMOUNT_1000K'
            When PCODE2 = 910000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 910000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 910000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 910000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 910000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 910000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 910000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 910000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 = 930000 And ISSUER_FE in (130004,130008) Then 'TIER_LEVEL_1'
            When PCODE2 = 930000 And ISSUER_FE in (130005,130009) Then 'TIER_LEVEL_2'
            When PCODE2 = 930000 And ISSUER_FE in (130006,130010) Then 'TIER_LEVEL_3'
            When PCODE2 = 930000 And ISSUER_FE in (130007,130011) Then 'TIER_LEVEL_4'
            When PCODE2 = 930000 And ISSUER_FE in (130013,130014) Then 'TIER_LEVEL_5'
            When PCODE2 = 930000 And ISSUER_FE in (130015,130018) Then 'TIER_LEVEL_6'
            When PCODE2 = 930000 And ISSUER_FE in (130016,130019) Then 'TIER_LEVEL_7'
            When PCODE2 = 930000 And ISSUER_FE in (130017,130020) Then 'TIER_LEVEL_8'
            When PCODE2 in (910000,930000,950000) And ISSUER_FE  = 980471 Then 'ACH_FEE'
            Else 'GDDC_CU'
            End
    ;
    --- End : Ket thuc do du lieu giao dich dieu chinh tu SHCLOG_SETT_IBFT_ADJUST vao TCKT_SESSION_DOMESTIC
    
    Update TCKT_SESSION_DOMESTIC
    Set DB_IR_FEE = 0, DB_SV_FEE = 0, DB_TOTAL_FEE = 0, CD_IR_FEE = 0, CD_SV_FEE = 0, ADJ_FEE = 0, BC_NP_ADJ = 0, BC_NP_SUM = 0
    Where RESPCODE In (112,114) And SERVICE_TYPE = 'IBFT'
        And BANK_ID Not In (600005,GET_BCCARD_ID()) And WITH_BANK Not In (600005,GET_BCCARD_ID()) -- hoind 3-may-2020: Hoan phi gd hoan tra 1 phan cua NSPK, BC Card
        And INSERT_DATE > Trunc(Sysdate)
    ;

    Update TCKT_SESSION_DOMESTIC
    Set DB_TOTAL_FEE = DB_IR_FEE + DB_SV_FEE + CD_SV_FEE
    Where SERVICE_TYPE = 'IBFT'
        And INSERT_DATE > Trunc(Sysdate)
    ;

    Update TCKT_SESSION_DOMESTIC
    Set DB_TOTAL_MONEY = DB_TOTAL_FEE + DB_AMOUNT
    Where SERVICE_TYPE = 'IBFT'
        And INSERT_DATE > Trunc(Sysdate)
    ;

    Update TCKT_SESSION_DOMESTIC
    Set CD_TOTAL_MONEY = CD_AMOUNT + CD_IR_FEE
    Where SERVICE_TYPE = 'IBFT'
        And INSERT_DATE > Trunc(Sysdate)
    ;

    Update TCKT_SESSION_DOMESTIC
    Set NAPAS_FEE = Case 
                        When ADJ_FEE <> 0 Then DB_TOTAL_FEE - CD_IR_FEE - ADJ_FEE
                        Else DB_SV_FEE + CD_SV_FEE - NP_ADJ_FEE
                    End
    Where SERVICE_TYPE = 'IBFT'
        And INSERT_DATE > Trunc(Sysdate)
    ;

    Update TCKT_SESSION_DOMESTIC
    Set DB_TOTAL_TRAN = - DB_TOTAL_TRAN, DB_AMOUNT = -DB_AMOUNT, DB_IR_FEE = -DB_IR_FEE, DB_SV_FEE = -DB_SV_FEE, 
        DB_TOTAL_FEE = -DB_TOTAL_FEE, DB_TOTAL_MONEY = -DB_TOTAL_MONEY, CD_TOTAL_TRAN = -CD_TOTAL_TRAN, BC_CL_ADJ = - BC_CL_ADJ,
        CD_AMOUNT = -CD_AMOUNT, CD_IR_FEE = -CD_IR_FEE, CD_SV_FEE = -CD_SV_FEE, CD_TOTAL_MONEY = -CD_TOTAL_MONEY, NAPAS_FEE = -NAPAS_FEE, BC_NP_ADJ = - BC_NP_ADJ, BC_NP_SUM = - BC_NP_SUM
    Where RESPCODE In (112,113,114,115) And SERVICE_TYPE = 'IBFT'
        And INSERT_DATE > Trunc(Sysdate)
    ;

    Update TCKT_SESSION_DOMESTIC
    Set DEBIT = Case 
                    When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then 
                        Case 
                            When DB_AMOUNT > CD_AMOUNT Then DB_AMOUNT - CD_AMOUNT
                            Else 0
                        End
                    When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111) Then 
                        Case 
                            When DB_AMOUNT > CD_AMOUNT Then DB_AMOUNT - CD_AMOUNT
                            Else 0
                        End
                    Else 
                        Case 
                            When DB_TOTAL_MONEY > CD_TOTAL_MONEY Then DB_TOTAL_MONEY - CD_TOTAL_MONEY
                            Else 0 
                        End
                End
        ,CREDIT = Case
                        When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 And TRAN_TYPE = 'POS' Then 
                            Case 
                                When CD_AMOUNT > DB_AMOUNT Then CD_AMOUNT - DB_AMOUNT
                                Else 0 
                            End
                        When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111) Then 
                            Case 
                                When CD_AMOUNT > DB_AMOUNT Then CD_AMOUNT - DB_AMOUNT
                                Else 0 
                            End
                        Else 
                            Case 
                                When CD_TOTAL_MONEY > DB_TOTAL_MONEY Then CD_TOTAL_MONEY - DB_TOTAL_MONEY 
                                Else 0 
                            End
                   End
    Where SERVICE_TYPE = 'IBFT'
        And INSERT_DATE > Trunc(Sysdate)
    ;
    
    /***du lieu tam khong can:
        - tong hop theo B-GROUP_BY_SV, D-TOTAL_BANK, E-TOTAL_REPORT
        - set lai truong OD_BY theo loai giao dich
        - set lai BANK_NAME
    ***/  
                  
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(sysdate,'0','End','INSERT_TCKT_SESSION_DOMESTIC_IBFT');

    commit;

EXCEPTION WHEN OTHERS THEN 
    ecode := SQLCODE;
    emesg := '-'||SQLERRM;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE,CRITICAL)
    Values(sysdate,ecode,emesg,'INSERT_TCKT_SESSION_DOMESTIC_IBFT',2);
    vDetail := 'Co loi tong hop bao cao ' || ecode || '-' || substr(emesg, 1, 120);
    SEND_SMS('ALERT_ERR#' || vlistsms || '#' || vDetail);
END;
/
