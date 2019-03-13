abstract class Expr {
    // ExprLambda
    public ExprVar arg;
    public Expr body;

    // ExprApply
    public Expr rator;
    public Expr rand;

    // ExprVar
    public String value;
}

class ExprLambda extends Expr {
    public ExprLambda(Expr body) {
        this.body = body;
    }

    public ExprLambda(Expr arg, Expr body) {
        this.arg  = new ExprVar(arg.value);
        this.body = body;
    }

    public ExprLambda(ExprVar arg, Expr body) {
        this.arg  = arg;
        this.body = body;
    }

    public String toString() {
        String argString;
        if (this.arg == null) {
            argString = "";
        } else {
            argString = this.arg.toString();
        }
        return "(" + Keyword.LAMBDA_SIGN + argString + Keyword.LAMBDA_DOT + this.body + ")";
    }
}

class ExprApply extends Expr {
    public ExprApply(Expr rator, Expr rand) {
        this.rator = rator;
        this.rand = rand;
    }

    public String toString() {
        return "(" + this.rator + " " + this.rand + ")";
    }
}

class ExprVar extends Expr {
    public ExprVar(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
