package vn.napas.webrp.report.util;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public final class SqlLogUtils {
  private static final Pattern NAMED_PARAM = Pattern.compile(":(\\w+)");

  private SqlLogUtils() {}

  public static String renderSql(String sql, Map<String, ?> params) {
    if (params == null || params.isEmpty()) return sql;
    Matcher m = NAMED_PARAM.matcher(sql);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      String name = m.group(1);
      Object val = params.get(name);
      String rep = toSqlLiteral(val);
      m.appendReplacement(sb, Matcher.quoteReplacement(rep));
    }
    m.appendTail(sb);
    return sb.toString();
  }

  private static String toSqlLiteral(Object v) {
    if (v == null) return "NULL";

    if (v instanceof Collection<?> col) {
      if (col.isEmpty()) return "(NULL)";
      // Nếu SQL đã có dấu ngoặc, ta chỉ trả về 'a','b'...; nếu không, cũng an toàn.
      return col.stream().map(SqlLogUtils::toSqlLiteralScalar).collect(Collectors.joining(", "));
    }
    return toSqlLiteralScalar(v);
  }

  private static String toSqlLiteralScalar(Object v) {
    if (v == null) return "NULL";
    if (v instanceof Number) return v.toString();
    if (v instanceof Boolean b) return b ? "TRUE" : "FALSE";
    if (v instanceof java.util.Date d)
      return "'" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
          .format(Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime()) + "'";
    if (v instanceof LocalDate ld)   return "'" + ld + "'";
    if (v instanceof LocalDateTime ldt)
      return "'" + ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'";
    if (v instanceof Instant ins)
      return "'" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
          .format(ins.atZone(ZoneId.systemDefault()).toLocalDateTime()) + "'";

    // String & các loại khác → coi như text, cần escape '
    String s = String.valueOf(v);
    s = s.replace("'", "''");
    return "'" + s + "'";
  }
}
