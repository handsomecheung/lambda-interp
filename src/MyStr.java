import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.Predicate;

class MyStr {
    public static String head(String str) {
        int len = str.length();
        if (len == 0) {
            return "";
        } else {
            return str.substring(0, 1);
        }
    }

    public static String tail(String str) {
        int len = str.length();
        if (len == 0) {
            return "";
        } else {
            return str.substring(1, len);
        }
    }

    public static Boolean isSpace(String str) {
        Pattern pattern = Pattern.compile("^\\s+$");
        Matcher m = pattern.matcher(str);
        return m.find();
    }

    public static String[] splitByP(String str, Predicate<String> p, String matched) {
        String h = head(str);
        String t = tail(str);
        if (p.test(h)) {
            if (t.isEmpty()) {
                return new String[] {matched + h, t};
            } else {
                return splitByP(t, p, matched + h);
            }
        } else {
            return new String[] {matched, str};
        }
    }

    public static String[] splitByP(String str, Predicate<String> p) {
        return splitByP(str, p, "");
    }

    public static String addLastNumber(String str) {
        Pattern pattern = Pattern.compile("^(.*[^0-9]+)([0-9]*)$");
        Matcher matcher = pattern.matcher(str);
        int baseInt;

        matcher.find();
        if (matcher.group(2).isEmpty()) {
            baseInt = 0;
        } else {
            baseInt = Integer.parseInt(matcher.group(2));
        }
        return matcher.group(1) + Integer.toString(baseInt + 1);
    }
}
