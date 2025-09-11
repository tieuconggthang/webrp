package vn.napas.webrp.report.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ParamUtils {
    private ParamUtils(){}

    public static String nz(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    public static Timestamp toTs(String d, String t) {
        String dd = nz(d), tt = nz(t);
        if (dd == null) return null;
        if (tt == null) tt = "000000";
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/uuuu HHmmss");
        LocalDateTime ldt = LocalDateTime.parse(dd + " " + tt, f);
        return Timestamp.valueOf(ldt);
    }

    public static Timestamp dayStart(String d) { return toTs(d, "000000"); }
    public static Timestamp dayEnd(String d)   { return toTs(d, "235959"); }

    public static String maskPan(String pan) {
        if (pan == null || pan.length() < 10) return pan;
        return pan.substring(0,6) + "******" + pan.substring(pan.length()-4);
    }
}
