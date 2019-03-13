import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

class Keyword {
    final static String LAMBDA_SIGN = "%";
    final static String LAMBDA_DOT = ".";
    final static HashSet<String> SYMBOLS = new HashSet<String>(Arrays.asList(LAMBDA_SIGN, LAMBDA_DOT, "(", ")"));

    public static boolean isId(String str) {
        Pattern r = Pattern.compile("^[a-zA-z0-9]$");
        Matcher m = r.matcher(str);
        return m.find();
    }

    public static boolean isKey(String str) {
        return SYMBOLS.contains(str);
    }

}

public abstract class Token {
    public String value;

    public static Token[] scan(String ss) {
        ArrayList<Token> tokens = scanning(new ArrayList<Token>(), ss);
        return tokens.toArray(new Token[tokens.size()]);
    }

    private static ArrayList<Token> scanning(ArrayList<Token> tokens, String lamStr) {
        String strHead = MyStr.head(lamStr);
        String strTail = MyStr.tail(lamStr);
        if (strHead.isEmpty() && strTail.isEmpty()) {
            return tokens;
        } else if (Keyword.isId(strHead)) {
            String[] l = MyStr.splitByP(lamStr, (c) -> Keyword.isId(c));
            String alphaNum = l[0];
            String rest = l[1];
            tokens.add(new Id(alphaNum));
            return scanning(tokens, rest);
        } else if (Keyword.isKey(strHead)) {
            tokens.add(new Key(strHead));
            return scanning(tokens, strTail);
        } else if (MyStr.isSpace(strHead)) {
            return scanning(tokens, strTail);
        } else {
            throw new IllTokenException("invalid char: " + strHead);
        }
    }
}

class Key extends Token {
    public Key(String value) {
        this.value = value;
    }
}

class Id extends Token {
    public Id(String value) {
        this.value = value;
    }
}
