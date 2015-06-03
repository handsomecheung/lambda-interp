public class Display {
    public static void print(Expr expr) {
        System.out.println("Result: " + expr2String(expr));
    }

    public static void print(ExprS exprS) {
        System.out.println("Result: " + exprS2String(exprS));
    }

    public static String expr2String(Expr expr) {
        if (expr instanceof Lambda) {
            return Keyword.LAMBDA_SIGN + expr2String(expr.arg) + Keyword.LAMBDA_DOT + expr2String(expr.body);
        } else if (expr instanceof Apply) {
            return "(" + expr2String(expr.lambda) + " " + expr2String(expr.var) + ")";
        } else if (expr instanceof Var) {
            return expr.value;
        } else if (expr == null) {
            return "";
        } else {
            throw new RuntimeException("invalid expr: " + expr);
        }
    }

    public static String exprS2String(ExprS exprS) {
        if (exprS instanceof LambdaS) {
            return Keyword.LAMBDA_SIGN + exprS2String(exprS.args) + Keyword.LAMBDA_DOT + exprS2String(exprS.body);
        } else if (exprS instanceof ArgsS) {
            String s = exprS.arg.value;
            if (exprS.others != null) {
                s = s + " " + exprS2String(exprS.others);
            }
            return s;
        } else if (exprS instanceof ApplyS) {
            return "(" + exprS2String(exprS.lambda) + " " + exprS2String(exprS.var) + ")";
        } else if (exprS instanceof VarS) {
            return exprS.value;
        } else if (exprS == null) {
            return "";
        } else {
            throw new RuntimeException("invalid exprS: " + exprS);
        }
    }
}
