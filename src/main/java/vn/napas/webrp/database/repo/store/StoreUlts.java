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
			while (Lcd.isAfter(sysNow)) {
				Lcd = CONVERT_LOCAL_DATE(sLocalDate, sSysDate.minusMonths(12));
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

	public static Integer MAP_IBFT_ACQ_ID(String iF32) {
		try {

			log.info("Starting");
			int iValue = Integer.parseInt(iF32);
			int iRValue = switch (iValue) {
			case 970429 -> 157979;
			case 970400 -> 161087;
			case 970427 -> 166888;
			case 970441 -> 180906;
			case 970425 -> 970459; // Note: 191919 is commented out in original, using 970459
			case 970431 -> 452999;
			case 970436 -> 686868;
			case 970419 -> 818188;
			case 970407 -> 888899;
			case 970434 -> 888999;
			case 970440 -> 970468;
			case 970418 -> 970488;
			case 970432 -> 981957;
			case 970405 -> 970499;
			default -> iValue;
			};

			return iRValue;
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
			return null;
		} finally {

		}

	}

	public static Integer MAP_IBFT_ACQ_ID(String iF32, String ISS_ID, String ACQ_ID) {
		try {

			log.info("Starting");
			if (ISS_ID != null && ISS_ID.trim().equalsIgnoreCase("980471"))
				return 980471;
			if (ISS_ID != null && ISS_ID.trim().equalsIgnoreCase("980475"))
				return 980478;
//			int iValue = Integer.parseInt(iF32);
			int iACQ_ID = Integer.parseInt(ACQ_ID);
			Integer iRValue = switch (iACQ_ID) {
			case 191919 -> 970459;
			case 970400 -> 970415;
			default -> null;
			};
			if (iRValue == null)
				iRValue = MAP_IBFT_ACQ_ID(iF32);
			return iRValue;
		} catch (Exception e) {
			log.error("Exception " + e.getMessage(), e);
			return null;
		} finally {

		}

	}

}
