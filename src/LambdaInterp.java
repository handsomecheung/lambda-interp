import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LambdaInterp {
    private static final Pattern DEFINITION_SYNTAX = Pattern.compile("define (.+) = (.+)$");
    private final static Scanner sc = new Scanner(System.in);
    private final static Interpreter interp = new Interpreter();

    private static void repl() {
        do {
            System.out.print("> ");
        } while (readEvaluatePrint());
    }

    private static boolean readEvaluatePrint() {
        try {
            String input = sc.nextLine();
            return evaluatePrint(input);
        } catch (LambdaInterpException e) {
            System.out.println("ERROR: " + e);
            return true;
        }
    }

    private static boolean evaluatePrint(String input) {
        if (input.isEmpty()) {
            return false;
        } else if (isDefinition(input)) {
            addDefinition(input);
            return true;
        } else {
            Expr expr = createExpr(input);
            expr = interpExpr(expr);
            expr = reduceExpr(expr);
            System.out.println("Result: " + expr);
            return true;
        }
    }

    private static Expr createExpr(String lamStr) {
        Token[] tokens = Token.scan(lamStr);
        ExprS exprS = Parser.parser(tokens);
        Expr expr = Desugar.desugar(exprS);
        return expr;
    }

    private static Expr interpExpr(Expr expr) {
        return interp.interp(expr);
    }

    private static Expr reduceExpr(Expr expr) {
        return interp.reduceExpr(expr, new Env());
    }

    private static boolean isDefinition(String str) {
        return DEFINITION_SYNTAX.matcher(str).matches();
    }

    private static void addDefinition(String defStr) {
        Matcher m = DEFINITION_SYNTAX.matcher(defStr);
        m.matches();
        interp.addDefinition(m.group(1), createExpr(m.group(2)));
    }

    private static void addPreDefinition() {
        for(String definition: PreDefinition.all()){
            addDefinition(definition);
        }
    }

    public static void main(String [] args) {
        addPreDefinition();
        repl();
    }
}
