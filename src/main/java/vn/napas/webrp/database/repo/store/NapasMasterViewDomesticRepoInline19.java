
package vn.napas.webrp.database.repo.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import vn.napas.webrp.constant.TableConstant;
import vn.napas.webrp.database.repo.TableMaintenanceRepository;
import vn.napas.webrp.report.util.SqlLogUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * NAPAS_MASTER_VIEW_DOMESTIC_IBFT
 */
@Repository
@Slf4j
public class NapasMasterViewDomesticRepoInline19 {
    private final NamedParameterJdbcTemplate jdbc;
    @Autowired Proc_INSERT_TCKT_SESSION_DOMESTIC_IBFT proc_INSERT_TCKT_SESSION_DOMESTIC_IBFT;
    @Autowired TableMaintenanceRepository tableMaintenanceRepository;
    private static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public NapasMasterViewDomesticRepoInline19(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public void executeAll(LocalDate fromDate, LocalDate toDate, String user) {
    	String from,  to;
    	from = fromDate.format(DMY);
    	to = toDate.format(DMY);
        MapSqlParameterSource p = new MapSqlParameterSource()
            .addValue("pQRY_FROM_DATE", from)
            .addValue("pQRY_TO_DATE", to)
            .addValue("pUser", user)
            .addValue("LIST_SMS", user);

        execStep("01", STEP_01_SQL, p);
//        execStep("02", STEP_02_SQL, p);
        proc_INSERT_TCKT_SESSION_DOMESTIC_IBFT.run(from, to, user);
//        execStep("03", STEP_03_SQL, p);
//        execStep("04", STEP_04_SQL, p);
        tableMaintenanceRepository.truncateTable(TableConstant.TCKT_NAPAS_IBFT);
        execStep("05", STEP_05_SQL, p);
        execStep("06", STEP_06_SQL, p);
//        execStep("07", STEP_07_SQL, p);
        execStep("08", STEP_08_SQL, p);
        execStep("08A", STEP_08A_SQL, p);
        execStep("09", STEP_09_SQL, p);
        execStep("10", STEP_10_SQL, p);
        execStep("11", STEP_11_SQL, p);
        execStep("12", STEP_12_SQL, p);
        execStep("13", STEP_13_SQL, p);
        execStep("14", STEP_14_SQL, p);
        execStep("15", STEP_15_SQL, p);
        execStep("16", STEP_16_SQL, p);
        execStep("17", STEP_17_SQL, p);
        execStep("18", STEP_18_SQL, p);
        execStep("19", STEP_19_SQL, p);
    }

    private void execStep(String tag, String sql, MapSqlParameterSource p) {
        if (sql == null || sql.isBlank()) return;
        // Skip legacy procedure call in step content; call from service instead if needed
//        if (sql.matches("(?s).*INSERT_TCKT_SESSION_DOMESTIC_IBFT\s*\(.*")) return;

        if (sql != null && sql.matches("(?s).*INSERT_TCKT_SESSION_DOMESTIC_IBFT\\s*\\(.*")) {
            return;
        }
        // Split on semicolon + newline, or blank lines
        String[] parts = sql.split(";\n|\n\n");
        for (String stmt : parts) {
            String s = stmt.trim();
            if (s.isEmpty()) continue;
            try {
            	log.info("sql tag: {}", SqlLogUtils.renderSql(s, p.getValues()));
                int rows = jdbc.update(s, p);
                log.info("{}: {} row(s)", tag, rows);
            } catch (DataAccessException ex) {
            	log.error("Execption: {}", ex.getMessage(), ex);
                logErr("-1", "Step " + tag + " failed: " + ex.getMessage(), "NAPAS_MASTER_VIEW_DOMESTIC_IBFT");
            }
        }
    }

    private void logErr(String code, String detail, String module) {
        String sql = "INSERT INTO ERR_EX(ERR_TIME, ERR_CODE, ERR_DETAIL, ERR_MODULE) VALUES (NOW(), :c, :d, :m)";
        jdbc.update(sql, Map.of("c", code, "d", detail, "m", module));
    }

    // lines 23-25
    private static final String STEP_01_SQL = """
    -- step 1 insert log action
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(NOW(),'0','Start','NAPAS_MASTER_VIEW_DOMESTIC_IBFT');
""";

    // lines 26-28
    private static final String STEP_02_SQL = """
    --step 2 insert bang TCKT_SESSION_DOMESTIC
    --tong hop du lieu IBFT phien quyet toan vao bang tam TCKT_SESSION_DOMESTIC
    INSERT_TCKT_SESSION_DOMESTIC_IBFT(pQRY_FROM_DATE, pQRY_TO_DATE, :pUser);    
""";

    // lines 29-31
    private static final String STEP_04_SQL = """
	/*step 4 xoa du lieu bang TCKT_NAPAS_IBFT*/
    EXECUTE IMMEDIATE 'Truncate Table TCKT_NAPAS_IBFT';

""";

    // lines 32-438
    private static final String STEP_05_SQL = """
	/*step 5
     Begin: Xu ly tong hop du lieu GD thanh cong tu bang SHCLOG_SETT_IBFT
     ISS-ACQ*/
    Insert Into    TCKT_NAPAS_IBFT(MSGTYPE_DETAIL,SUB_BANK,SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, 
            NAPAS_FEE,ADJ_FEE,NP_ADJ_FEE, MERCHANT_TYPE, BC_NP_SUM, BC_CL_ADJ, STEP,FEE_TYPE,PART_FE,LIQUIDITY)
    Select MSGTYPE_DETAIL,Case
                When ISSUER_RP = 970426 And SUBSTRING(Trim(PAN),0,8) ='97046416' Then 970464
                Else null
           End,
           Case
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End  SETT_DATE,
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','42','48','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End As GROUP_TRAN, 
        Case
            When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
                967700,977700,987700,997700,967800,977800,987800,997800,967900,977900,987900,997900,
                966100,976100,986100,996100,966200,976200,986200,996200) Then SUBSTRING(Trim(DATE_FORMAT(PCODE,'09')),1,2)||PCODE2
            Else SUBSTRING(Trim(DATE_FORMAT(PCODE,'09')),1,2)
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
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB'      
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'ISS-ACQ' As GROUP_ROLE,
        ISSUER_RP As BANK_ID,
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
                        When PCODE = '20' And Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE -PREAMOUNT END
                        When PCODE = '20' Then -AMOUNT
                        When Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE PREAMOUNT END
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
                        When Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE PREAMOUNT END
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
                When FEE_IRF_ISS > 0 And CASE Pcode2 WHEN null THEN 0 ELSE Pcode2 END not in (890000,720000) 
                    Then FEE_IRF_ISS 
                Else 0 
            End
        ) As CD_IR_FEE,
        SUM(Case When FEE_SVF_ISS < 0 Then 0 Else FEE_SVF_ISS End)  As CD_SV_FEE,
        SUM(
            Case 
                When Pcode In ('40') Then
                    Case
                        When Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE PREAMOUNT END
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End As FEE_TYPE,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 End As PART_FE,
        Case When Max(B.COLUMN_VALUE) Is Null And Max(C.COLUMN_VALUE) Is Null And Max(D.COLUMN_VALUE) Is Null Then 'Y' Else 'N' End
    From SHCLOG_SETT_IBFT A
    Left Join Table(GET_LIQUIDITY_BANK) B
        On A.ISSUER_RP = B.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) C
        On A.ACQUIRER_RP = C.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) D
        On A.BB_BIN = D.COLUMN_VALUE
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
                When ISSUER_RP = 970426 And SUBSTRING(Trim(PAN),0,8) ='97046416' Then 970464
                Else null
           End, 
            Case
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','42','48','91')  Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End, 
        Case
            When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
                967700,977700,987700,997700,967800,977800,987800,997800,967900,977900,987900,997900,
                966100,976100,986100,996100,966200,976200,986200,996200) Then SUBSTRING(Trim(DATE_FORMAT(PCODE,'09')),1,2)||PCODE2
            Else SUBSTRING(Trim(DATE_FORMAT(PCODE,'09')),1,2)
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
                                CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                              ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'                    
 
            Else 'POS'
        End,
        ISSUER_RP,
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 End,
        Case When B.COLUMN_VALUE Is Null And C.COLUMN_VALUE Is Null And D.COLUMN_VALUE Is Null Then 'Y' Else 'N' End
    ;
""";

    // lines 439-813
    private static final String STEP_06_SQL = """
	--step 6
    -- ACQ-ISS
    Insert   Into TCKT_NAPAS_IBFT(MSGTYPE_DETAIL,SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, 
            NAPAS_FEE,ADJ_FEE,NP_ADJ_FEE, MERCHANT_TYPE, BC_NP_ADJ, BC_NP_SUM, STEP,FEE_TYPE,PART_FE,LIQUIDITY)
    Select MSGTYPE_DETAIL,Case
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End SETT_DATE, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End As GROUP_TRAN, 
        Case
            When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
                967700,977700,987700,997700,967800,977800,987800,997800,967900,977900,987900,997900,
                966100,976100,986100,996100,966200,976200,986200,996200) Then SUBSTRING(Trim(DATE_FORMAT(PCODE,'09')),1,2)||PCODE2
            Else SUBSTRING(Trim(DATE_FORMAT(PCODE,'09')),1,2)
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
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'
                       
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'ACQ-ISS' As GROUP_ROLE,
        ACQUIRER_RP As BANK_ID,         
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
                        When PCODE = '20' And Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE -PREAMOUNT END
                        When PCODE = '20' Then -AMOUNT
                        When Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE PREAMOUNT END
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End as FEE_TYPE,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 
        End,
        Case When Max(B.COLUMN_VALUE) Is Null And Max(C.COLUMN_VALUE) Is Null And Max(D.COLUMN_VALUE) Is Null Then 'Y' Else 'N' End
    From SHCLOG_SETT_IBFT A
    Left Join Table(GET_LIQUIDITY_BANK) B
        On A.ISSUER_RP = B.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) C
        On A.ACQUIRER_RP = C.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) D
        On A.BB_BIN = D.COLUMN_VALUE
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
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End, 
        Case
            When PCODE2 in (960000,970000,980000,990000,967500,977500,967600,977600,968400,978400,968500,978500,987500,997500,987600,997600,988400,998400,988500,998500,
                967700,977700,987700,997700,967800,977800,987800,997800,967900,977900,987900,997900,
                966100,976100,986100,996100,966200,976200,986200,996200) Then SUBSTRING(Trim(DATE_FORMAT(PCODE,'09')),1,2)||PCODE2
            Else SUBSTRING(Trim(DATE_FORMAT(PCODE,'09')),1,2)
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
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB' 
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'
                              
            Else 'POS'
        End,
        ACQUIRER_RP,
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 
        End,
        Case When B.COLUMN_VALUE Is Null And C.COLUMN_VALUE Is Null And D.COLUMN_VALUE Is Null Then 'Y' Else 'N' End
    ;
""";

    // lines 814-1118
    private static final String STEP_08_SQL = """
	--step 8
    -- ISS-BEN
    Insert   Into TCKT_NAPAS_IBFT(SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, 
            NAPAS_FEE,ADJ_FEE,NP_ADJ_FEE, MERCHANT_TYPE,STEP,FEE_TYPE,LIQUIDITY)
    Select Case
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End SETT_DATE, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
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
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB'      
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'
             
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'ISS-BEN' As GROUP_ROLE,
        ISSUER_RP As BANK_ID,     
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
                When Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE PREAMOUNT END
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End as FEE_TYPE,
        Case When Max(B.COLUMN_VALUE) Is Null And Max(C.COLUMN_VALUE) Is Null And Max(D.COLUMN_VALUE) Is Null Then 'Y' Else 'N' End 
    From SHCLOG_SETT_IBFT A
    Left Join Table(GET_LIQUIDITY_BANK) B
        On A.ISSUER_RP = B.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) C
        On A.ACQUIRER_RP = C.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) D
        On A.BB_BIN = D.COLUMN_VALUE
    Where 
    (
        (Respcode = 0 And Isrev is null)
        Or
        Respcode In (110,112,113,114,115)
    )
    And Fee_Note Is not null
    And Msgtype = 210
    And Pcode In ('41','42','43','48','91')
    Group By Case
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
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
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB'  
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'
            Else 'POS'
        End,
        ISSUER_RP,
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End,
        Case When B.COLUMN_VALUE Is Null And C.COLUMN_VALUE Is Null And D.COLUMN_VALUE Is Null Then 'Y' Else 'N' End
    ;
""";

    // lines 1119-1409
    private static final String STEP_08A_SQL = """
	--step 8
    -- BEN-ISS
    Insert   Into TCKT_NAPAS_IBFT(SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY,
            NAPAS_FEE,ADJ_FEE,NP_ADJ_FEE,MERCHANT_TYPE,STEP,FEE_TYPE,LIQUIDITY)
    Select Case
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End SETT_DATE, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
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
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB' 
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'
 
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'BEN-ISS' As GROUP_ROLE,
        BB_BIN As BANK_ID, 
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
                When Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE PREAMOUNT END
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End as FEE_TYPE,
        Case When Max(B.COLUMN_VALUE) Is Null And Max(C.COLUMN_VALUE) Is Null And Max(D.COLUMN_VALUE) Is Null Then 'Y' Else 'N' End
    From SHCLOG_SETT_IBFT A
    Left Join Table(GET_LIQUIDITY_BANK) B
        On A.ISSUER_RP = B.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) C
        On A.ACQUIRER_RP = C.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) D
        On A.BB_BIN = D.COLUMN_VALUE
    Where 
    (
        (Respcode = 0 And Isrev is null)
        Or
        Respcode In (110,112,113,114,115)
    )
    And Fee_Note Is not null
    And Msgtype = 210
    And Pcode In ('41','42','43','48','91')
    Group By Case
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
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
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB' 
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'
 
            Else 'POS'
        End,
        BB_BIN,
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End,
        Case When B.COLUMN_VALUE Is Null And C.COLUMN_VALUE Is Null And D.COLUMN_VALUE Is Null Then 'Y' Else 'N' End
    ;    
    -- End: Ket thuc phan do du lieu giao dich thanh cong tu SHCLOG_SET_IBFT vao TCKT_NAPAS_IBFT
    --- Begin: Bat dau do du lieu giao dich dieu chinh tu SHCLOG_SETT_IBFT_ADJUST vao TCKT_NAPAS_IBFT
""";

    // lines 1410-1817
    private static final String STEP_09_SQL = """
	--step 9
    -- ISS-ACQ
    Insert   Into TCKT_NAPAS_IBFT(MSGTYPE_DETAIL,SUB_BANK,SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, 
            NAPAS_FEE,ADJ_FEE,NP_ADJ_FEE, MERCHANT_TYPE, BC_NP_SUM, BC_CL_ADJ, STEP,FEE_TYPE,PART_FE,LIQUIDITY)
    Select MSGTYPE_DETAIL,Case
                When ISSUER_RP = 970426 And SUBSTRING(Trim(PAN),0,8) ='97046416' Then 970464
                Else null
           End,
           Case
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End  SETT_DATE,
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','42','48','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End As GROUP_TRAN, 
        Case
            When Issuer_Rp = 602907 Then CASE PCODE_ORIG WHEN null THEN SUBSTRING(Trim(DATE_FORMAT(PCODE,'09')),1,2) ELSE Pcode_Orig END
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
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB'      
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'ISS-ACQ' As GROUP_ROLE,
        ISSUER_RP As BANK_ID,
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
                        When PCODE = '20' And Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE -PREAMOUNT END
                        When PCODE = '20' Then -AMOUNT
                        When Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE PREAMOUNT END
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
                        When Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE PREAMOUNT END
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
                When FEE_IRF_ISS > 0 And CASE Pcode2 WHEN null THEN 0 ELSE Pcode2 END not in (890000,720000) 
                    Then FEE_IRF_ISS 
                Else 0 
            End
        ) As CD_IR_FEE,
        SUM(Case When FEE_SVF_ISS < 0 Then 0 Else FEE_SVF_ISS End)  As CD_SV_FEE,
        SUM(
            Case 
                When Pcode In ('40') Then
                    Case
                        When Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE PREAMOUNT END
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End As FEE_TYPE,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 End As PART_FE,
        Case When Max(B.COLUMN_VALUE) Is Null And Max(C.COLUMN_VALUE) Is Null And Max(D.COLUMN_VALUE) Is Null Then 'Y' Else 'N' End
    From SHCLOG_SETT_IBFT_ADJUST A
    Left Join Table(GET_LIQUIDITY_BANK) B
        On A.ISSUER_RP = B.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) C
        On A.ACQUIRER_RP = C.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) D
        On A.BB_BIN = D.COLUMN_VALUE
    Where 
    (
        (Respcode = 0 And Isrev = 0)
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
                When ISSUER_RP = 970426 And SUBSTRING(Trim(PAN),0,8) ='97046416' Then 970464
                Else null
           End, 
            Case
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','42','48','91')  Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End, 
        Case
            When Issuer_Rp = 602907 Then CASE PCODE_ORIG WHEN null THEN SUBSTRING(Trim(DATE_FORMAT(PCODE,'09')),1,2) ELSE Pcode_Orig END
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
                                CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                              ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'                    
 
            Else 'POS'
        End,
        ISSUER_RP,
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 End,
        Case When B.COLUMN_VALUE Is Null And C.COLUMN_VALUE Is Null And D.COLUMN_VALUE Is Null Then 'Y' Else 'N' End
    ;
""";

    // lines 1818-2195
    private static final String STEP_10_SQL = """
	--step 10
    -- ACQ-ISS
    Insert   Into TCKT_NAPAS_IBFT(MSGTYPE_DETAIL,SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, 
            NAPAS_FEE,ADJ_FEE,NP_ADJ_FEE, MERCHANT_TYPE, BC_NP_ADJ, BC_NP_SUM, STEP,FEE_TYPE,PART_FE,LIQUIDITY)
    Select MSGTYPE_DETAIL,Case
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End SETT_DATE, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End As GROUP_TRAN, 
        Case
            When Issuer_Rp = 602907 Then CASE PCODE_ORIG WHEN null THEN SUBSTRING(Trim(DATE_FORMAT(PCODE,'09')),1,2) ELSE Pcode_Orig END
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
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'
                       
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'ACQ-ISS' As GROUP_ROLE,
        ACQUIRER_RP As BANK_ID,         
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
                        When PCODE = '20' And Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE -PREAMOUNT END
                        When PCODE = '20' Then -AMOUNT
                        When Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE PREAMOUNT END
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End as FEE_TYPE,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 
        End,
        Case When Max(B.COLUMN_VALUE) Is Null And Max(C.COLUMN_VALUE) Is Null And Max(D.COLUMN_VALUE) Is Null Then 'Y' Else 'N' End
    From SHCLOG_SETT_IBFT_ADJUST A
    Left Join Table(GET_LIQUIDITY_BANK) B
        On A.ISSUER_RP = B.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) C
        On A.ACQUIRER_RP = C.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) D
        On A.BB_BIN = D.COLUMN_VALUE
    Where 
    (
        (Respcode = 0 And Isrev = 0)
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
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
            When Pcode2 In (810000,820000,830000,860000,870000)  Then 'UTMQT'
            Else 'Non IBFT'
        End, 
        Case
            When Issuer_Rp = 602907 Then CASE PCODE_ORIG WHEN null THEN SUBSTRING(Trim(DATE_FORMAT(PCODE,'09')),1,2) ELSE Pcode_Orig END
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
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB' 
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'
                              
            Else 'POS'
        End,
        ACQUIRER_RP,
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End,
        Case 
        When Msgtype = 430 And Respcode = 114 Then 1 
        When Msgtype = 210 And Respcode in (112,114)  And Is_part_rev = 430 then 1
        Else 0 
        End,
        Case When B.COLUMN_VALUE Is Null And C.COLUMN_VALUE Is Null And D.COLUMN_VALUE Is Null Then 'Y' Else 'N' End
    ;

""";

    // lines 2196-2500
    private static final String STEP_11_SQL = """
	--step 11
    -- ISS-BEN
    Insert   Into TCKT_NAPAS_IBFT(SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, 
            NAPAS_FEE,ADJ_FEE,NP_ADJ_FEE, MERCHANT_TYPE,STEP,FEE_TYPE,LIQUIDITY)
    Select Case
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End SETT_DATE, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
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
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB'      
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'
             
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'ISS-BEN' As GROUP_ROLE,
        ISSUER_RP As BANK_ID,     
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
                When Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE PREAMOUNT END
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End as FEE_TYPE,
        Case When Max(B.COLUMN_VALUE) Is Null And Max(C.COLUMN_VALUE) Is Null And Max(D.COLUMN_VALUE) Is Null Then 'Y' Else 'N' End 
    From SHCLOG_SETT_IBFT_ADJUST A
    Left Join Table(GET_LIQUIDITY_BANK) B
        On A.ISSUER_RP = B.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) C
        On A.ACQUIRER_RP = C.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) D
        On A.BB_BIN = D.COLUMN_VALUE
    Where 
    (
        (Respcode = 0 And Isrev = 0)
        Or
        Respcode In (110,112,113,114,115)
    )
    And Fee_Note Is not null
    And Msgtype = 210
    And Pcode In ('41','42','43','48','91')
    Group By Case
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
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
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB'  
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'
            Else 'POS'
        End,
        ISSUER_RP,
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End,
        Case When B.COLUMN_VALUE Is Null And C.COLUMN_VALUE Is Null And D.COLUMN_VALUE Is Null Then 'Y' Else 'N' End
    ;
""";

    // lines 2501-2790
    private static final String STEP_12_SQL = """
``	--step 12
    -- BEN-ISS
    Insert   Into TCKT_NAPAS_IBFT(SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE,
            SERVICE_CODE, GROUP_ROLE, BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE,
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY,
            NAPAS_FEE,ADJ_FEE,NP_ADJ_FEE,MERCHANT_TYPE,STEP,FEE_TYPE,LIQUIDITY)
    Select Case
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End SETT_DATE, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
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
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB' 
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'
 
            Else 'POS'
        End As TRAN_TYPE,
        'SWITCH' As SERVICE_CODE,
        'BEN-ISS' As GROUP_ROLE,
        BB_BIN As BANK_ID, 
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
                When Respcode In (112,114) Then CASE PREAMOUNT WHEN null THEN 0 ELSE PREAMOUNT END
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU'
            End as FEE_TYPE,
        Case When Max(B.COLUMN_VALUE) Is Null And Max(C.COLUMN_VALUE) Is Null And Max(D.COLUMN_VALUE) Is Null Then 'Y' Else 'N' End
    From SHCLOG_SETT_IBFT_ADJUST A
    Left Join Table(GET_LIQUIDITY_BANK) B
        On A.ISSUER_RP = B.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) C
        On A.ACQUIRER_RP = C.COLUMN_VALUE
    Left Join Table(GET_LIQUIDITY_BANK) D
        On A.BB_BIN = D.COLUMN_VALUE
    Where 
    (
        (Respcode = 0 And Isrev = 0)
        Or
        Respcode In (110,112,113,114,115)
    )
    And Msgtype = 210
    And Fee_Note Is not null
    And Pcode In ('41','42','43','48','91')
    Group By Case
                When Respcode = 0 And SETTLEMENT_DATE < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE > STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y')
                When Respcode = 0 And SETTLEMENT_DATE Between STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') And STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y') Then SETTLEMENT_DATE
                Else null
            End, 
        Case
            When Respcode = 0 Then null
            Else
                Case    
                    When DATE(Edit_Date) < STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y') Then STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y')
                    Else DATE(Edit_Date)
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
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT'
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
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'ATM'
            When merchant_type = 6013 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'SMS'
            When merchant_type = 6014 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 
                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'INT'
            When merchant_type = 6015 And Pcode not In ('41','42','48','91') And  (
                                            Pcode2 Is Null 

                                            Or 
                                            CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST'
                                          ) then 'MOB' 
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END = 'IST|IBT' And (TRAN_CASE = 'C3|72' or  TRAN_CASE = '72|C3') Then 'IBFT khc'
            When CASE FROM_SYS WHEN null THEN 'IST' ELSE FROM_SYS END in('IST','IBT') And Pcode In ('41','48','42','91') Then 'IBFT khc'
 
            Else 'POS'
        End,
        BB_BIN,
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
            When PCODE2 = 910000 And ISSUER_FE  = 980478 Then 'IBFT20_FEE' --ninhnt them 980478 cho du an IBFT2.0
            Else 'GDDC_CU' 
            End,
        Case When B.COLUMN_VALUE Is Null And C.COLUMN_VALUE Is Null And D.COLUMN_VALUE Is Null Then 'Y' Else 'N' End
    ;
    --- End : Ket thuc do du lieu giao dich dieu chinh tu SHCLOG_SETT_IBFT_ADJUST vao TCKT_NAPAS_IBFT
""";

    // lines 2791-2821
    private static final String STEP_13_SQL = """
	--step 13
    Update TCKT_NAPAS_IBFT
    Set DB_IR_FEE = 0, DB_SV_FEE = 0, DB_TOTAL_FEE = 0, CD_IR_FEE = 0, CD_SV_FEE = 0, ADJ_FEE = 0, BC_NP_ADJ = 0, BC_NP_SUM = 0
    Where RESPCODE In (112,114)
    And BANK_ID Not In (600005,GET_BCCARD_ID()) And WITH_BANK Not In (600005,GET_BCCARD_ID()) -- hoind 3-may-2020: Hoan phi gd hoan tra 1 phan cua NSPK, BC Card
    ;

    Update TCKT_NAPAS_IBFT
    Set DB_TOTAL_FEE = DB_IR_FEE + DB_SV_FEE + CD_SV_FEE
    ;

    Update TCKT_NAPAS_IBFT
    Set DB_TOTAL_MONEY = DB_TOTAL_FEE + DB_AMOUNT
    ;

    Update TCKT_NAPAS_IBFT
    Set CD_TOTAL_MONEY = CD_AMOUNT + CD_IR_FEE
    ;

    Update TCKT_NAPAS_IBFT
    Set NAPAS_FEE = Case 
                        When ADJ_FEE <> 0 Then DB_TOTAL_FEE - CD_IR_FEE - ADJ_FEE
                        Else DB_SV_FEE + CD_SV_FEE - NP_ADJ_FEE
                    End;

    Update TCKT_NAPAS_IBFT
    Set DB_TOTAL_TRAN = - DB_TOTAL_TRAN, DB_AMOUNT = -DB_AMOUNT, DB_IR_FEE = -DB_IR_FEE, DB_SV_FEE = -DB_SV_FEE, 
        DB_TOTAL_FEE = -DB_TOTAL_FEE, DB_TOTAL_MONEY = -DB_TOTAL_MONEY, CD_TOTAL_TRAN = -CD_TOTAL_TRAN, BC_CL_ADJ = - BC_CL_ADJ,
        CD_AMOUNT = -CD_AMOUNT, CD_IR_FEE = -CD_IR_FEE, CD_SV_FEE = -CD_SV_FEE, CD_TOTAL_MONEY = -CD_TOTAL_MONEY, NAPAS_FEE = -NAPAS_FEE, BC_NP_ADJ = - BC_NP_ADJ, BC_NP_SUM = - BC_NP_SUM
    Where RESPCODE In (112,113,114,115)
    ;
""";

    // lines 2822-2858
    private static final String STEP_14_SQL = """
	--step 14
    Update TCKT_NAPAS_IBFT
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
    ;
""";

    // lines 2859-2933
    private static final String STEP_15_SQL = """
    --step 15
    Insert Into TCKT_NAPAS_IBFT(MSGTYPE_DETAIL,OD_BY, SETT_DATE,EDIT_DATE,SETTLEMENT_CURRENCY,RESPCODE,GROUP_TRAN, 
        PCODE, TRAN_TYPE,SERVICE_CODE, GROUP_ROLE,BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE, 
        DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, NAPAS_FEE, DEBIT,
        CREDIT, ADJ_FEE, NP_ADJ_FEE, BC_NP_ADJ, BC_NP_SUM, BC_CL_ADJ, STEP, SUB_BANK, LIQUIDITY)
    Select MSGTYPE_DETAIL,null As OD_BY,SETT_DATE, EDIT_DATE,SETTLEMENT_CURRENCY,null RESPCODE,GROUP_TRAN,null PCODE,GROUP_TRAN As TRAN_TYPE, 
        SERVICE_CODE, null As GROUP_ROLE,BANK_ID,WITH_BANK,SUM(DB_TOTAL_TRAN) DB_TOTAL_TRAN,SUM(DB_AMOUNT) DB_AMOUNT,SUM(DB_IR_FEE) DB_IR_FEE, 
        SUM(DB_SV_FEE) DB_SV_FEE, SUM(DB_TOTAL_FEE) DB_TOTAL_FEE,SUM(DB_TOTAL_MONEY) DB_TOTAL_MONEY,SUM(CD_TOTAL_TRAN) CD_TOTAL_TRAN, 
        SUM(CD_AMOUNT) CD_AMOUNT, SUM(CD_IR_FEE) CD_IR_FEE,SUM(CD_SV_FEE) CD_SV_FEE,
        SUM(CD_TOTAL_MONEY) CD_TOTAL_MONEY,SUM(NAPAS_FEE) NAPAS_FEE,
        Case 
            When SUM(Case 
                            When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then DB_AMOUNT  --ninhnt them 980478 cho du an IBFT2.0
                            When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then DB_AMOUNT
                            Else DB_TOTAL_MONEY End
                        ) 
                > 
                SUM(
                    Case When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then CD_AMOUNT  --ninhnt them 980478 cho du an IBFT2.0
                    When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then CD_AMOUNT
                    Else CD_TOTAL_MONEY End
                ) 
                Then SUM(Case 
                            When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then DB_AMOUNT --ninhnt them 980478 cho du an IBFT2.0
                            When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then DB_AMOUNT
                            Else DB_TOTAL_MONEY End
                        ) 
                - 
                SUM(
                    Case When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then CD_AMOUNT --ninhnt them 980478 cho du an IBFT2.0
                    When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then CD_AMOUNT
                    Else CD_TOTAL_MONEY End
                ) 
            Else 0 
        End As DEBIT,
        Case 
            When SUM(
                Case 
                    When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then CD_AMOUNT --ninhnt them 980478 cho du an IBFT2.0
                    When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then CD_AMOUNT
                    Else CD_TOTAL_MONEY 
                End
                ) 
                > 
                SUM(
                    Case 
                        When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then DB_AMOUNT --ninhnt them 980478 cho du an IBFT2.0
                        When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then DB_AMOUNT 
                        Else DB_TOTAL_MONEY 
                    End
                ) 
                Then 
                    SUM(
                    Case 
                        When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then CD_AMOUNT --ninhnt them 980478 cho du an IBFT2.0
                        When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then CD_AMOUNT
                        Else CD_TOTAL_MONEY 
                    End
                    ) 
                    - 
                    SUM(
                        Case 
                            When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then DB_AMOUNT --ninhnt them 980478 cho du an IBFT2.0
                            When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then DB_AMOUNT 
                            Else DB_TOTAL_MONEY 
                        End
                    )
            Else 0 
        End As CREDIT, SUM(ADJ_FEE), SUM(NP_ADJ_FEE) NP_ADJ_FEE, Sum(BC_NP_ADJ) BC_NP_ADJ, Sum(BC_NP_SUM) BC_NP_SUM, Sum(BC_CL_ADJ) BC_CL_ADJ, 'B-GROUP_BY_SV', SUB_BANK, LIQUIDITY
    From TCKT_NAPAS_IBFT
    Where STEP = 'A-BY_ROLE'
        Group By MSGTYPE_DETAIL,SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY,GROUP_TRAN, SERVICE_CODE,BANK_ID, WITH_BANK, SUB_BANK, LIQUIDITY
    ;
    
    -- Cong bank
""";

    // lines 2934-3008
    private static final String STEP_16_SQL = """
	-- step 16
    Insert Into TCKT_NAPAS_IBFT(MSGTYPE_DETAIL,OD_BY, SETT_DATE,EDIT_DATE,SETTLEMENT_CURRENCY,RESPCODE,GROUP_TRAN, 
        PCODE, TRAN_TYPE,SERVICE_CODE, GROUP_ROLE,BANK_ID, WITH_BANK, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE, 
        DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, NAPAS_FEE, DEBIT, 
        CREDIT, ADJ_FEE, NP_ADJ_FEE, BC_NP_ADJ, BC_NP_SUM, BC_CL_ADJ, STEP, SUB_BANK, LIQUIDITY)
    Select MSGTYPE_DETAIL,null As OD_BY,SETT_DATE,EDIT_DATE,SETTLEMENT_CURRENCY,null RESPCODE,'AAA' GROUP_TRAN,null PCODE, 
        'C?ng' As TRAN_TYPE, SERVICE_CODE,null As GROUP_ROLE,BANK_ID,WITH_BANK, SUM(DB_TOTAL_TRAN) DB_TOTAL_TRAN,SUM(DB_AMOUNT) DB_AMOUNT, 
        SUM(DB_IR_FEE) DB_IR_FEE, SUM(DB_SV_FEE) DB_SV_FEE,SUM(DB_TOTAL_FEE) DB_TOTAL_FEE,SUM(DB_TOTAL_MONEY) DB_TOTAL_MONEY, 
        SUM(CD_TOTAL_TRAN) CD_TOTAL_TRAN, SUM(CD_AMOUNT) CD_AMOUNT,SUM(CD_IR_FEE) CD_IR_FEE,SUM(CD_SV_FEE) CD_SV_FEE, 
        SUM(CD_TOTAL_MONEY) CD_TOTAL_MONEY, SUM(NAPAS_FEE) NAPAS_FEE, 
        Case 
            When SUM(Case 
                            When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then DB_AMOUNT --ninhnt them 980478 cho du an IBFT2.0
                            When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then DB_AMOUNT
                            Else DB_TOTAL_MONEY End
                        ) 
                > 
                SUM(
                    Case When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then CD_AMOUNT --ninhnt them 980478 cho du an IBFT2.0
                    When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then CD_AMOUNT
                    Else CD_TOTAL_MONEY End
                ) 
                Then SUM(Case 
                            When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then DB_AMOUNT --ninhnt them 980478 cho du an IBFT2.0
                            When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then DB_AMOUNT
                            Else DB_TOTAL_MONEY End
                        ) 
                - 
                SUM(
                    Case When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then CD_AMOUNT --ninhnt them 980478 cho du an IBFT2.0
                    When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then CD_AMOUNT
                    Else CD_TOTAL_MONEY End
                ) 
            Else 0 
        End As DEBIT,
        Case 
            When SUM(
                Case 
                    When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then CD_AMOUNT --ninhnt them 980478 cho du an IBFT2.0
                    When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then CD_AMOUNT
                    Else CD_TOTAL_MONEY 
                End
                ) 
                > 
                SUM(
                    Case 
                        When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then DB_AMOUNT --ninhnt them 980478 cho du an IBFT2.0
                        When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then DB_AMOUNT 
                        Else DB_TOTAL_MONEY 
                    End
                ) 
                Then 
                    SUM(
                    Case 
                        When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then CD_AMOUNT --ninhnt them 980478 cho du an IBFT2.0
                        When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then CD_AMOUNT
                        Else CD_TOTAL_MONEY 
                    End
                    ) 
                    - 
                    SUM(
                        Case 
                            When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then DB_AMOUNT --ninhnt them 980478 cho du an IBFT2.0
                            When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then DB_AMOUNT 
                            Else DB_TOTAL_MONEY 
                        End
                    )
            Else 0 
        End As CREDIT,SUM(ADJ_FEE),SUM(NP_ADJ_FEE), Sum(BC_NP_ADJ) BC_NP_ADJ, Sum(BC_NP_SUM) BC_NP_SUM, Sum(BC_CL_ADJ) BC_CL_ADJ, 'D-TOTAL_BANK', SUB_BANK, LIQUIDITY
    From TCKT_NAPAS_IBFT
    Where STEP = 'A-BY_ROLE'
        Group By MSGTYPE_DETAIL,SETT_DATE,EDIT_DATE, SETTLEMENT_CURRENCY,SERVICE_CODE,BANK_ID, WITH_BANK,SUB_BANK, LIQUIDITY
    ;

    -- Nhom tong cong      
""";

    // lines 3009-3097
    private static final String STEP_17_SQL = """
    --step 17
    Insert Into TCKT_NAPAS_IBFT(MSGTYPE_DETAIL,OD_BY, SETT_DATE,EDIT_DATE,SETTLEMENT_CURRENCY,RESPCODE,GROUP_TRAN, 
        PCODE, TRAN_TYPE,SERVICE_CODE, GROUP_ROLE,BANK_ID, WITH_BANK, BANK_NAME, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE, 
        DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, NAPAS_FEE, DEBIT, 
        CREDIT, ADJ_FEE, NP_ADJ_FEE, BC_NP_ADJ, BC_NP_SUM, BC_CL_ADJ, STEP, SUB_BANK, LIQUIDITY)
    Select MSGTYPE_DETAIL,null As OD_BY,SETT_DATE,EDIT_DATE,SETTLEMENT_CURRENCY,null RESPCODE,GROUP_TRAN,null PCODE,TRAN_TYPE,SERVICE_CODE, 
        null As GROUP_ROLE, null BANK_ID, WITH_BANK, 'T?ng C?ng' BANK_NAME,SUM(DB_TOTAL_TRAN) DB_TOTAL_TRAN,SUM(DB_AMOUNT) DB_AMOUNT, 
        SUM(
            Case 
                When WITH_BANK = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 840 Then 0
                When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then 0 
                When BANK_ID In (605609,602907,600005,600006, 600007,980471,971100,971111,980478) Then 0 --ninhnt them 980478 cho du an IBFT2.0
                Else DB_IR_FEE 
            End
            ) DB_IR_FEE, 
        SUM(
            Case 
                When WITH_BANK = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 840 Then 0
                When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then 0 
                When BANK_ID In (605609,602907,600005,600006, 600007,980471,971100,971111,980478) Then 0 --ninhnt them 980478 cho du an IBFT2.0
                Else DB_SV_FEE 
            End
            ) DB_SV_FEE,
        SUM(
            Case 
                When WITH_BANK = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 840 Then 0
                When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then 0 
                When BANK_ID In (605609,602907,600005,600006, 600007,980471,971100,971111,980478) Then 0 --ninhnt them 980478 cho du an IBFT2.0
                Else DB_TOTAL_FEE 
            End
            ) DB_TOTAL_FEE, 
        SUM(DB_TOTAL_MONEY) - 
            SUM(
                Case 
                    When WITH_BANK = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 840 Then DB_IR_FEE
                    When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then DB_IR_FEE
                    When BANK_ID In (605609,602907,600005,600006, 600007,980471,971100,971111,980478) Then DB_TOTAL_FEE --ninhnt them 980478 cho du an IBFT2.0
                    Else 0 
                End
            ) DB_TOTAL_MONEY, 
        SUM(CD_TOTAL_TRAN) CD_TOTAL_TRAN, SUM(CD_AMOUNT) CD_AMOUNT,
        SUM(
            Case 
                When WITH_BANK = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 840 Then 0
                When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then 0 
                When BANK_ID In (605609,602907,600005,600006, 600007,980471,971100,971111,980478) Then 0 --ninhnt them 980478 cho du an IBFT2.0
                Else CD_IR_FEE 
            End
            ) CD_IR_FEE,
        SUM(
            Case 
                When WITH_BANK = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 840 Then 0
                When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then 0
                When BANK_ID In (605609,602907,600005,600006, 600007,980471,971100,971111,980478) Then 0 --ninhnt them 980478 cho du an IBFT2.0
                Else CD_SV_FEE 
            End
        ) CD_SV_FEE, 
        SUM(CD_TOTAL_MONEY) - 
            SUM(
                Case 
                    When WITH_BANK = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 840 Then CD_IR_FEE
                    When BANK_ID = GET_BCCARD_ID() And SETTLEMENT_CURRENCY = 704 Then CD_IR_FEE                   
                    When BANK_ID In (605609,602907,600005,600006, 600007,980471,971100,971111,980478) Then CD_IR_FEE --ninhnt them 980478 cho du an IBFT2.0
                    Else 0 
                End
                ) CD_TOTAL_MONEY, 
        SUM(
            Case 
                When BANK_ID In (602907,605609,600005,600005,600006, 600007) Then CD_IR_FEE - DB_TOTAL_FEE 
                WHEN BANK_ID in( 980471,971100,971111,980478) Then NAPAS_FEE+BC_NP_ADJ+CD_IR_FEE - DB_TOTAL_FEE  -- Tong NAPAS + Phi Chia se Ghi co - Tong Phi ghi no --ninhnt them 980478 cho du an IBFT2.0
                Else NAPAS_FEE + BC_NP_ADJ
            End    
        ) 
        NAPAS_FEE, 
        Case 
            When SUM(Case When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then DB_AMOUNT Else DB_TOTAL_MONEY End + BC_NP_SUM) > SUM(Case When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then CD_AMOUNT Else CD_TOTAL_MONEY End)  --ninhnt them 980478 cho du an IBFT2.0
                Then SUM(Case When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then DB_AMOUNT Else DB_TOTAL_MONEY End  + BC_NP_SUM) - SUM(Case When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then CD_AMOUNT Else CD_TOTAL_MONEY End)  --ninhnt them 980478 cho du an IBFT2.0
            Else 0 
        End As DEBIT,
        Case 
            When SUM(Case When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then CD_AMOUNT Else CD_TOTAL_MONEY End) > SUM(Case When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then DB_AMOUNT Else DB_TOTAL_MONEY End + BC_NP_SUM) --ninhnt them 980478 cho du an IBFT2.0
                Then SUM(Case When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then CD_AMOUNT Else CD_TOTAL_MONEY End) - SUM(Case When BANK_ID In (602907,605609,600005,600006, 600007,980471,971100,971111,980478) Then DB_AMOUNT Else DB_TOTAL_MONEY End + BC_NP_SUM) --ninhnt them 980478 cho du an IBFT2.0
            Else 0 
        End As CREDIT, SUM(ADJ_FEE),SUM(NP_ADJ_FEE),
        Sum(BC_NP_ADJ) BC_NP_ADJ, Sum(BC_NP_SUM) BC_NP_SUM, Sum(BC_CL_ADJ) BC_CL_ADJ, 'E-TOTAL_REPORT', SUB_BANK, LIQUIDITY
    From TCKT_NAPAS_IBFT
    --Where STEP In ('B-GROUP_BY_SV','C-GROUP_IBFT_OTHER','D-TOTAL_BANK','A-BY_ROLE')
        Group By MSGTYPE_DETAIL,GROUP_TRAN, TRAN_TYPE, WITH_BANK, SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, SERVICE_CODE, SUB_BANK, LIQUIDITY
    ;    
""";

    // lines 3098-3136
    private static final String STEP_18_SQL = """
    --step 18
    Update TCKT_NAPAS_IBFT
    Set OD_BY = Case
                    When GROUP_TRAN = 'Non IBFT' And TRAN_TYPE = 'Non IBFT' Then 'AA'
                    When GROUP_TRAN = 'Non IBFT' And TRAN_TYPE = 'ATM' Then 'AB'
                    When GROUP_TRAN = 'Non IBFT' And TRAN_TYPE = 'POS' Then 'AC'
                    When GROUP_TRAN = 'Non IBFT' And TRAN_TYPE = 'SSP_ON' Then 'AD'
                    When GROUP_TRAN = 'Non IBFT' And TRAN_TYPE = 'SSP_OFF' Then 'AE'
                    When GROUP_TRAN = 'Non IBFT' And TRAN_TYPE = 'TRANSIT' Then 'AF'
                    When GROUP_TRAN = 'Non IBFT' And TRAN_TYPE = 'BP_ON' Then 'AG'
                    When GROUP_TRAN = 'Non IBFT' And TRAN_TYPE = 'BP_OFF' Then 'AH'
                    When GROUP_TRAN = 'Non IBFT' And TRAN_TYPE = 'APPLEPAY_ON' Then 'AI'
                    When GROUP_TRAN = 'Non IBFT' And TRAN_TYPE = 'APPLEPAY_OFF' Then 'AJ'

                    When GROUP_TRAN = 'IBFT' And TRAN_TYPE = 'IBFT' Then 'BA'
                    When GROUP_TRAN = 'IBFT' And TRAN_TYPE = 'QR_IBFT' Then 'BB'
                    When GROUP_TRAN = 'IBFT' And TRAN_TYPE = 'IBFT khc' Then 'BC'
                    When GROUP_TRAN = 'IBFT' And TRAN_TYPE = 'Mobile IBFT' Then 'BD'
                    When GROUP_TRAN = 'CBFT' And TRAN_TYPE = 'CBFT' Then 'CA'
                    When GROUP_TRAN = 'CBFT' And TRAN_TYPE = 'INT' Then 'CB'
                    When GROUP_TRAN = 'CBFT' And TRAN_TYPE <> 'INT' Then 'CD'
                    When GROUP_TRAN = 'NON-BANK' And TRAN_TYPE = 'NON-BANK' Then 'EA'
                    When GROUP_TRAN = 'NON-BANK' And TRAN_TYPE = 'E-Wallet' Then 'EB'
                    When GROUP_TRAN = 'QRPAY' And TRAN_TYPE = 'QRPAY' Then 'FA'
                    When GROUP_TRAN = 'QRPAY' And TRAN_TYPE = 'QRC' Then 'FB'
                    When GROUP_TRAN = 'UTMQT' And TRAN_TYPE = 'UTMQT' Then 'HA'
                    When GROUP_TRAN = 'UTMQT' And TRAN_TYPE = 'CA1' Then 'HB'
                    When GROUP_TRAN = 'UTMQT' And TRAN_TYPE = 'CA2' Then 'HC'
                    When GROUP_TRAN = 'UTMQT' And TRAN_TYPE = 'CA3' Then 'HD'
                    When GROUP_TRAN = 'UTMQT' And TRAN_TYPE = 'CA4' Then 'HE'
                    When GROUP_TRAN = 'UTMQT' And TRAN_TYPE = 'CA5' Then 'HF'
                    When GROUP_TRAN ='E-Wallet' And TRAN_TYPE= 'E-Wallet' Then 'IA'
                    When GROUP_TRAN = 'E-Wallet' And TRAN_TYPE ='CAOT' Then 'IB'
                    When GROUP_TRAN = 'EFT' And TRAN_TYPE ='EFT' Then 'JA'
                    When GROUP_TRAN = 'EFT' And TRAN_TYPE ='EFTC' Then 'JB'
                    When GROUP_TRAN = 'QR' And TRAN_TYPE = 'QR' Then 'KA'
                    When GROUP_TRAN = 'QR' And TRAN_TYPE = 'QR_ITMX' Then 'KB'
                    When GROUP_TRAN = 'AAA' And TRAN_TYPE = 'C?ng' Then 'Z'
                End;
""";

    // lines 3137-3191
    private static final String STEP_19_SQL = """
    --step 19
    Update TCKT_NAPAS_IBFT
    Set BANK_NAME = Replace(GET_FULL_BANK_NAME(BANK_ID),'Ngn hng','NH')
    Where Bank_ID Is not null
    ;
    Update TCKT_NAPAS_IBFT SET SERVICE_TYPE ='IBFT';
   
    If (:pUser = 'hoind' Or :pUser = 'sondt') Then
    
        Delete
        From NAPAS_FEE_MONTH
        Where NAPAS_TIME between  DATE(NOW())  and  DATE(NOW()) + 1 - 1/86400
        And DATA_TYPE ='DOMESTIC'
        And SERVICE_TYPE ='IBFT';      
    
        Insert Into NAPAS_FEE_MONTH(MSGTYPE_DETAIL, NAPAS_TIME, USER_SETT, EDIT_NOTE, SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, 
            GROUP_TRAN, PCODE, TRAN_TYPE, SERVICE_CODE, GROUP_ROLE, BANK_ID, WITH_BANK, BANK_NAME, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE, 
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, NAPAS_FEE, DEBIT, CREDIT, OD_BY,ADJ_FEE,
            NP_ADJ_FEE,MERCHANT_TYPE,STEP,SETTLEMENT_DATE_FROM,SETTLEMENT_DATE_TO,BC_NP_ADJ, BC_NP_SUM, BC_CL_ADJ,FEE_TYPE,SUB_BANK,DATA_TYPE,LIQUIDITY,SERVICE_TYPE)
        Select  MSGTYPE_DETAIL,NOW(), :pUser, 'TCKT NAPAS' EDIT_NOTE, SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY, RESPCODE, 
            GROUP_TRAN, PCODE, TRAN_TYPE, SERVICE_CODE, GROUP_ROLE, a.BANK_ID, WITH_BANK, BANK_NAME, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, DB_SV_FEE, 
            DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, NAPAS_FEE, DEBIT, CREDIT, OD_BY,ADJ_FEE,
            NP_ADJ_FEE,MERCHANT_TYPE,STEP,STR_TO_DATE(:pQRY_FROM_DATE, '%d/%m/%Y'), STR_TO_DATE(:pQRY_TO_DATE, '%d/%m/%Y'),BC_NP_ADJ, BC_NP_SUM, BC_CL_ADJ,FEE_TYPE,SUB_BANK,'DOMESTIC', LIQUIDITY,SERVICE_TYPE
        From TCKT_NAPAS_IBFT A
        ;
        
        Insert Into NAPAS_FEE_MONTH_AUTO_BACKUP(MSGTYPE_DETAIL,NAPAS_SETT_DATE, NAPAS_TIME, USER_SETT, EDIT_NOTE, SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY,
            RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE, SERVICE_CODE, GROUP_ROLE, BANK_ID, WITH_BANK, BANK_NAME, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, 
            DB_SV_FEE, DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, NAPAS_FEE, DEBIT,
            CREDIT, OD_BY, ADJ_FEE, BKDATE,NP_ADJ_FEE,MERCHANT_TYPE,STEP,SETTLEMENT_DATE_FROM,SETTLEMENT_DATE_TO,BC_NP_ADJ, BC_NP_SUM, BC_CL_ADJ,FEE_TYPE,SUB_BANK,DATA_TYPE,LIQUIDITY,SERVICE_TYPE)
        Select MSGTYPE_DETAIL,NAPAS_SETT_DATE, NAPAS_TIME, USER_SETT, EDIT_NOTE, SETT_DATE, EDIT_DATE, SETTLEMENT_CURRENCY,
            RESPCODE, GROUP_TRAN, PCODE, TRAN_TYPE, SERVICE_CODE, GROUP_ROLE, BANK_ID, WITH_BANK, BANK_NAME, DB_TOTAL_TRAN, DB_AMOUNT, DB_IR_FEE, 
            DB_SV_FEE, DB_TOTAL_FEE, DB_TOTAL_MONEY, CD_TOTAL_TRAN, CD_AMOUNT, CD_IR_FEE, CD_SV_FEE, CD_TOTAL_MONEY, NAPAS_FEE, DEBIT,
            CREDIT, OD_BY, ADJ_FEE, NOW(),NP_ADJ_FEE,MERCHANT_TYPE,STEP,SETTLEMENT_DATE_FROM,SETTLEMENT_DATE_TO,BC_NP_ADJ, BC_NP_SUM, BC_CL_ADJ,FEE_TYPE,SUB_BANK,DATA_TYPE,LIQUIDITY,SERVICE_TYPE
        From NAPAS_FEE_MONTH
        Where NAPAS_TIME between  DATE(NOW())  and  DATE(NOW()) + 1 - 1/86400
        And DATA_TYPE = 'DOMESTIC'
        And SERVICE_TYPE ='IBFT'; 
                  
    End If;
    
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE)
    Values(NOW(),'0','End','NAPAS_MASTER_VIEW_DOMESTIC_IBFT');

EXCEPTION WHEN OTHERS THEN 
    ecode := SQLCODE;
    emesg := '-'||SQLERRM;
    Insert Into ERR_EX(ERR_TIME,ERR_CODE,ERR_DETAIL,ERR_MODULE,CRITICAL)
    Values(NOW(),ecode,emesg,'NAPAS_MASTER_VIEW_DOMESTIC_IBFT',2);
    vDetail := 'Co loi tong hop bao cao ' || ecode || '-' || SUBSTRING(emesg, 1, 120);
    SEND_SMS('ALERT_ERR#' || :LIST_SMS || '#' || vDetail);
END;
/
""";

    // placeholder for missing step 3
    private static final String STEP_03_SQL = "";

    // placeholder for missing step 7
    private static final String STEP_07_SQL = "";

}
