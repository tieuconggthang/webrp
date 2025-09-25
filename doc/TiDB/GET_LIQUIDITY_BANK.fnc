CREATE OR REPLACE FUNCTION RPT.GET_LIQUIDITY_BANK      
    RETURN bank_t     AS      
        l_bank bank_t;
BEGIN    
    select bank_id bulk collect into l_bank
    from
    ( 
        select bank_id from citad_liquidity_risk
        where status='Hi?u l?c' and sysdate > app_date
        union all
        (
        select bank_id from citad_merge
        where merge_to in
            (
            select bank_id from citad_liquidity_risk
            where status='Hi?u l?c' and sysdate > app_date
            )
            and is_active = 'Y'
        )
        union all
        (
        select to_number(tgtt_id) as bank_id from tgtt_config 
        where report_bank in
            (
            select bank_id from citad_liquidity_risk
            where status='Hi?u l?c' and sysdate > app_date
            )
        )
    );
    
    return l_bank;
    
EXCEPTION WHEN OTHERS THEN
    return null;
END;
/
