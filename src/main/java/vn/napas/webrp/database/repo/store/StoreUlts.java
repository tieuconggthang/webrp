package vn.napas.webrp.database.repo.store;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StoreUlts {
	public static Integer to_number_bnv(String p) {
		try {
			return Integer.parseInt(p);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public static LocalDate NP_CONVERT_LOCAL_DATE(String sLocalDate, LocalDate sSysDate) {
		try {
			log.info("Starting NP_CONVERT_LOCAL_DATE");
			LocalDate Lcd = CONVERT_LOCAL_DATE(sLocalDate, sSysDate);
			LocalDate sysNow = LocalDate.now();
			while (Lcd.isAfter(sysNow)){
				Lcd = CONVERT_LOCAL_DATE(sLocalDate, sysNow.minusMonths(12));
			}
			return Lcd;
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
			return null;
		} finally {

		}

	}

	 static LocalDate CONVERT_LOCAL_DATE(String sLocalDate, LocalDate sSysDate) {
		try {
			log.info("CONVERT_LOCAL_DATE");
			if (sLocalDate == null || sSysDate == null)
				return null;
			String localDate = sLocalDate.trim();
			if (localDate.length() != 4 || !localDate.chars().allMatch(Character::isDigit)) {
				return null;
			}
			if (localDate.equals("0229")) {
				int currentYear = LocalDate.now().getYear();
				int y = (currentYear / 4) * 4;
				return LocalDate.of(y, 2, 29);
			} else {
				int mm = Integer.parseInt(sLocalDate.substring(0, 2));
				int dd = Integer.parseInt(sLocalDate.substring(2, 4));
				return LocalDate.of(sSysDate.getYear(), mm, dd);
			}
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
			LocalDate prev = sSysDate.minusDays(365);
			int mm = Integer.parseInt(sLocalDate.substring(0, 2));
			int dd = Integer.parseInt(sLocalDate.substring(2, 4));
			return LocalDate.of(prev.getYear(), mm, dd);

		} finally {
		}

	}

}
