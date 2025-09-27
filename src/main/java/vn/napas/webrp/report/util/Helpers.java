package vn.napas.webrp.report.util;

import java.math.BigDecimal;

public class Helpers {
	public static String nv(String s) {
		return s == null ? "" : s;
	}

	// PCODE đã paddings -> "&.substring(0,2)"
	public static String left2Padded(Integer pcode) {
		if (pcode == null)
			return "";
		String s = String.format("%06d", pcode);
		return s.substring(0, 2);
	}

	// ====== helpers ======
	public static boolean in(Integer v, int... arr) {
		if (v == null)
			return false;
		for (int a : arr)
			if (v == a)
				return true;
		return false;
	}

	// ====== helpers ======
	public static boolean in(String v, String... arr) {
		if (v == null)
			return false;
		for (String a : arr)
			if (v.equalsIgnoreCase(a))
				return true;
		return false;
	}

//	    public static String nv(String s) { return s == null ? "" : s; }
	public static int n0(Integer i) {
		return i == null ? 0 : i;
	}

	public static BigDecimal nz(BigDecimal b) {
		return b == null ? BigDecimal.ZERO : b;
	}

	public static int str2Int(String val) {
		try {
			return Integer.parseInt(val);
		} catch (Exception e) {
			return 0;
		}
	}
}
