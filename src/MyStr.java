import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.Predicate;
import java.util.Map;

class VarName {
    private final String letter;
    private final int number;

    public VarName(String letter, int number) {
        this.letter = letter;
        this.number = number;
    }

    public String getLetter() {
        return letter;
    }

    public int getNumber() {
        return number;
    }
}

class MyStr {
    private static final Pattern varNamePattern = Pattern.compile("^(.*[^0-9]+)([0-9]*)$");
    private static final Pattern spacePattern = Pattern.compile("^\\s+$");

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

    public static boolean isSpace(String str) {
        Matcher m = spacePattern.matcher(str);
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

    public static VarName parseVarName(String str) {
        Matcher matcher = varNamePattern.matcher(str);

        String letter = "_";
        int number = 0;

        matcher.find();
        if (!matcher.group(1).isEmpty()) {
            letter = matcher.group(1);
        }

        if (!matcher.group(2).isEmpty()) {
            number = Integer.parseInt(matcher.group(2));
        }

        return new VarName(letter, number);
    }

    public static String findVarNameLetter(String str) {
        VarName name = parseVarName(str);
        return name.getLetter();
    }

    public static String addLastNumber(String str) {
        VarName name = parseVarName(str);
        String letter = name.getLetter();
        int number = name.getNumber();

        return letter + Integer.toString(number + 1);
    }

    public static String removeLastNumber(String str) {
        Matcher matcher = varNamePattern.matcher(str);

        matcher.find();
        if (matcher.group(2).isEmpty()) {
            return str;
        } else {
            return matcher.group(1);
        }
    }
}
