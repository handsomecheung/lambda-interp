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
}

class Apply extends Expr {
    public Apply(Expr lambda, Expr var) {
        this.lambda = lambda;
        this.var = var;
    }
}

class Var extends Expr {
    public Var(String value) {
        this.value = value;
    }
}
