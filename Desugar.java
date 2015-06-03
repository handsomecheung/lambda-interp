public class Desugar {
    public static Expr desugar(ExprS exprS) {
        if (exprS instanceof LambdaS) {
            if (exprS.args == null) {
                return new Lambda(desugar(exprS.body));
            } else if (exprS.args.others == null) {
                return new Lambda(desugar(exprS.args.arg), desugar(exprS.body));
            } else {
                return new Lambda(desugar(exprS.args.arg), desugar(new LambdaS(exprS.args.others, exprS.body)));
            }
        } else if (exprS instanceof ApplyS) {
            return new Apply(desugar(exprS.lambda), desugar(exprS.var));
        } else if (exprS instanceof VarS) {
            return new Var(exprS.value);
        } else {
            throw new RuntimeException("invalid exprS: " + exprS);
        }
    }
}
