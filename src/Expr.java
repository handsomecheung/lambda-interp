abstract class Expr {
    public String value;

    public Var arg;
    public Expr body;

    public Expr lambda;
    public Expr var;

}

class Lambda extends Expr {
    public Lambda(Expr body) {
        this.body = body;
    }
    public Lambda(Var arg, Expr body) {
        this.arg  = arg;
        this.body = body;
    }
    public Lambda(Expr arg, Expr body) {
        this.arg  = new Var(arg.value);
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

class Apply extends Expr {
    public Apply(Expr lambda, Expr var) {
        this.lambda = lambda;
        this.var = var;
    }

    public String toString() {
        return "(" + this.lambda + " " + this.var + ")";
    }
}

class Var extends Expr {
    public Var(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}

// for reduceExpr
class VarR extends Expr {
    public VarR(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
