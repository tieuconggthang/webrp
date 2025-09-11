UPDATE SHCLOG_SETT_IBFT
    SET ISSUER_FE = Case 
        When Decode(ISSUER_RP,null,0,ISSUER_RP) = 970412 And ACCOUNT_NO ='110014886114' then 128050 -- Danh dau ISSUER_FE cho gd KIOS VIET
        When ACQUIRER_RP <> 605609 And Decode(PCODE2,null,0,PCODE2) Not In (890000,720000,730000)  -- bo 810000 code co khong hieu 
        And decode(BB_BIN,null,0,BB_BIN) not in (971101,971100) And SUBSTR( trim(TO_CHAR(PCODE,'099999')),1,2) In ('42','91')
        And ISSUER_RP not in ('980471','980478') Then --ninhnt cap nhat IBFT 2.0 04/07/2024
            Case 
                When Amount between 1 and 499999999 And SEQUENCE_IN_MONTH is not null then
                    Case
                        --- G1 phi bac thang
                        When SEQUENCE_IN_MONTH between 0 and 10 and FEE_PACKAGE_TYPE is null then 130004
                        When SEQUENCE_IN_MONTH between 11 and 20 and FEE_PACKAGE_TYPE is null then 130005
                        When SEQUENCE_IN_MONTH between 21 and 40 and FEE_PACKAGE_TYPE is null then 130006
                        When SEQUENCE_IN_MONTH between 41 and 60 and FEE_PACKAGE_TYPE is null then 130007
                        When SEQUENCE_IN_MONTH between 61 and 80 and FEE_PACKAGE_TYPE is null then 130013
                        When SEQUENCE_IN_MONTH between 81 and 100 and FEE_PACKAGE_TYPE is null then 130015
                        When SEQUENCE_IN_MONTH between 101 and 120 and FEE_PACKAGE_TYPE is null then 130016
                        When SEQUENCE_IN_MONTH > 120 and FEE_PACKAGE_TYPE is null then 130017 
                        --- G2 phi bac thang
                        When SEQUENCE_IN_MONTH between 0 and 10 and FEE_PACKAGE_TYPE='G2_2022' then 130008
                        When SEQUENCE_IN_MONTH between 11 and 20 and FEE_PACKAGE_TYPE='G2_2022' then 130009
                        When SEQUENCE_IN_MONTH between 21 and 40 and FEE_PACKAGE_TYPE='G2_2022' then 130010
                        When SEQUENCE_IN_MONTH between 41 and 60 and FEE_PACKAGE_TYPE='G2_2022' then 130011
                        When SEQUENCE_IN_MONTH between 61 and 80 and FEE_PACKAGE_TYPE ='G2_2022' then 130014
                        When SEQUENCE_IN_MONTH between 81 and 100 and FEE_PACKAGE_TYPE ='G2_2022' then 130018
                        When SEQUENCE_IN_MONTH between 101 and 120 and FEE_PACKAGE_TYPE ='G2_2022'then 130019
                        When SEQUENCE_IN_MONTH > 120 and FEE_PACKAGE_TYPE='G2_2022' then 130020 
                     End                    
            End 
        When ISSUER_RP = 980471 then 980471
        When ISSUER_RP = 980478 then 980478 --ninhnt cap nhat IBFT 2.0 04/07/2024
        When BB_BIN = 971100 Then 971100                    
        When BB_BIN = 971101 Then 971101
        When Decode(PCODE2,null,0,PCODE2) = 720000 Then 128013   
        When Decode(PCODE2,null,0,PCODE2) = 730000 Then
            Case 
                When INS_TYPE_FEE ='TGTT' Then
                    Case
                       When Amount <= 300000000 Then 128015
                       When Amount between 300000001 and 499999999 Then 128016 
                    End
                Else
                    Case
                       When Amount <= 300000000 Then 128017
                       When Amount between 300000001 and 499999999 Then 128018 
                    End
            End 
        Else ISSUER_FE    
    End