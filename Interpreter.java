import java.util.HashMap;

class IllInterpException extends LambdaInterpException{
    public IllInterpException(){
        super();
    }
    public IllInterpException(String msg){
        super(msg);
    }
}

class EnvValue {
    private boolean evaluation = false;
    private Expr value;

    public EnvValue(Expr value) {
        this.value = value;
    }

    public EnvValue(Expr value, boolean evaluation) {
        this.value = value;
        this.evaluation = evaluation;
    }

    public boolean isEvaluation() {
        return this.evaluation;
    }

    public Expr getValue() {
        return this.value;
    }
}

class Env {
    private Env outer = null;
    private HashMap<String, EnvValue> env = new HashMap<String, EnvValue>();

    public Env() {}

    public Env(String key, EnvValue envValue, Env outer) {
        this.env.put(key, envValue);
        this.outer = outer;
    }

    public Env(String key, Expr expr, Env outer) {
        this.env.put(key, new EnvValue(expr));
        this.outer = outer;
    }

    public Env(String key, Expr expr, Env outer, boolean bool) {
        this.env.put(key, new EnvValue(expr, bool));
        this.outer = outer;
    }

    public void add(String key, Expr expr) {
        this.env.put(key, new EnvValue(expr));
    }

    public HashMap<String, EnvValue> getEnv() {
        return this.env;
    }

    public Env getOuter() {
        return this.outer;
    }

    public void evaluateEnv(String key, Expr expr) {
        this.env.put(key, new EnvValue(expr, true));
    }

    public EnvValue lookup(String key) {
        HashMap<String, EnvValue> e = find(key);
        if (e == null) {
            throw new IllInterpException("can not find symbol: " + key);
        } else {
            return e.get(key);
        }
    }

    private HashMap<String, EnvValue> find(String key) {
        if (this.env.containsKey(key)) {
            return this.env;
        } else if (outer != null) {
            return this.outer.find(key);
        } else {
            return null;
        }
    }
}

class Closure {
    public Expr expr;
    public Env env;

    public Closure(Expr expr, Env env) {
        this.expr = expr;
        this.env  = env;
    }
}

public class Interpreter {
    private Env theEnv = new Env();

    public Interpreter(){}

    public void addDefinition(String key, Expr expr) {
        this.theEnv.add(key, expr);
    }

    public Expr interp(Expr expr) {
        Closure c = interp(expr, this.theEnv);
        return c.expr;
    }

    private Closure interp(Expr expr, Env env) {
        // Display.print(expr);
        if (expr instanceof Apply) {
            Closure lambdaC = interp(expr.lambda, env);
            Expr lambda = lambdaC.expr;
            env = lambdaC.env;
            if (lambda.arg != null) {
                if (expr.var instanceof Apply) {
                    env = new Env(lambda.arg.value, expr.var, env, false);
                } else if (expr.var instanceof Var) {
                    env = new Env(lambda.arg.value, env.lookup(expr.var.value), env);
                } else {
                    env = new Env(lambda.arg.value, expr.var, env, true);
                }
            }
            return interp(lambda.body, env);
        } else if (expr instanceof Var) {
            return getFromEnvWithEval(expr.value, env);
        } else {
            return new Closure(expr, env);
        }
    }

    private Closure getFromEnvWithEval(String key, Env env) {
        EnvValue envValue = env.lookup(key);
        Expr expr = envValue.getValue();
        Closure closure = new Closure(expr, env);
        if (!envValue.isEvaluation()) {
            closure = interp(expr, env);
            env.evaluateEnv(key, closure.expr);
        }
        return closure;
    }
}
