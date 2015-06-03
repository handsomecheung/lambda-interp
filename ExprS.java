abstract class ExprS {
    public String value;

    public ArgsS args;
    public ExprS body;

    public VarS arg;
    public ArgsS others;

    public ExprS lambda;
    public ExprS var;
}

class ArgsS extends ExprS {
    public ArgsS() {}

    public ArgsS(VarS arg) {
        this.arg = arg;
    }

    public ArgsS(VarS arg, ArgsS others) {
        this.arg = arg;
        this.others = others;
    }

    public ArgsS(ExprS arg) {
        this.arg = new VarS(arg.value);
    }

    public ArgsS(ExprS arg, ExprS others) {
        this.arg = new VarS(arg.value);
        this.others = new ArgsS(others.arg, others.others);
    }
}

class LambdaS extends ExprS {
    public LambdaS(ExprS body) {
        this.body = body;
    }

    public LambdaS(ArgsS args, ExprS body) {
        this.args = args;
        this.body = body;
    }

    public LambdaS(ExprS args, ExprS body) {
        ArgsS _args;
        if (args.others != null) {
            _args = new ArgsS(args.arg, args.others);
        } else {
            _args = new ArgsS(args.arg);
        }
        this.args = _args;
        this.body = body;
    }
}

class ApplyS extends ExprS {
    public ApplyS(ExprS lambda, ExprS var) {
        this.lambda = lambda;
        this.var = var;
    }
}

class VarS extends ExprS {
    public VarS(String value) {
        this.value = value;
    }
}

class SymbolS extends ExprS {
    public SymbolS(String value) {
        this.value = value;
    }
}

class EmptyS extends ExprS {}
