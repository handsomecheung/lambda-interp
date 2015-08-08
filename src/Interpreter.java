import java.util.HashMap;
import java.util.HashSet;

class IllInterpException extends LambdaInterpException{
    private static final long serialVersionUID = -8674501232897615142L;
    public IllInterpException(){
        super();
    }
    public IllInterpException(String msg){
        super(msg);
    }
}

class UnlimitedReduceException  extends RuntimeException {
	private static final long serialVersionUID = 8958282081060521465L;
	public UnlimitedReduceException(){
        super();
    }
    public UnlimitedReduceException(String msg){
        super(msg);
    }
}

class Store {
    private HashMap<Integer, Closure> storage = new HashMap<Integer, Closure>();

    public Store() {}

    public Closure fetch(Integer location) {
        if (this.storage.containsKey(location)) {
            return this.storage.get(location);
        } else {
            throw new RuntimeException("can not find location: " + location);
        }
    }

    public void set(Integer location, Closure c) {
        this.storage.put(location, c);
    }
}

class Env {
    private Env outer = null;
    private HashMap<String, Integer> env = new HashMap<String, Integer>();

    private static Integer currentLocation = 1;
    private static Integer makeLocation() {
        currentLocation = currentLocation + 1;
        return currentLocation;
    }

    private static Store store = new Store();

    public Env() {}

    public Env(String key, Expr expr, Env outer) {
        Integer location = makeLocation();
        this.env.put(key, location);
        this.outer = outer;
        Env.store.set(location, new Closure(expr, outer));
    }

    public Env(String key, Closure c, Env outer) {
        Integer location = makeLocation();
        this.env.put(key, location);
        this.outer = outer;
        Env.store.set(location, c);
    }

    public Env(HashMap<String, Integer> env) {
        this.env = env;
    }

    public void add(String key, Expr expr) {
        Integer location = makeLocation();
        this.env.put(key, location);
        Env.store.set(location, new Closure(expr, this));
    }

    public HashMap<String, Integer> getEnv() {
        return this.env;
    }

    public Env getOuter() {
        return this.outer;
    }


    public Closure lookupGently(String key) {
        HashMap<String, Integer> e = find(key);
        if (e == null) {
            return null;
        } else {
            return Env.store.fetch(e.get(key));
        }
    }

    public Closure lookup(String key) {
        Closure r = lookupGently(key);
        if (r == null) {
            throw new IllInterpException("can not find symbol: " + key);
        } else {
            return r;
        }
    }

    private HashMap<String, Integer> find(String key) {
        if (this.env.containsKey(key)) {
            return this.env;
        } else if (outer != null) {
            return this.outer.find(key);
        } else {
            return null;
        }
    }

    public Env detach(Env otherEnv) {
        Env newEnv = new Env(this.env);
        if (this == otherEnv) {
            return new Env();
        } else if (this.outer != otherEnv) {
            if (this.outer == null) {
                throw new RuntimeException("can not detach");
            } else {
                return newEnv.attach(this.outer.detach(otherEnv));
            }
        } else {
            return newEnv;
        }
    }

    public Env attach(Env otherEnv) {
        if (this.outer == null) {
            this.outer = otherEnv;
            return this;
        } else {
            this.outer.attach(otherEnv);
            return this;
        }
    }

    public String toString() {
        String s = "";
        for (HashMap.Entry<String, Integer> entry: this.env.entrySet()) {
            Closure c = Env.store.fetch(entry.getValue());
            s = s + entry.getKey() + ": " + c.expr + ",";
        }

        if (this.outer != null) {
            s = s + "\n" + this.outer;
        }
        return s;
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
        this.theEnv = new Env(key, new Closure(expr, this.theEnv), this.theEnv);
    }

    public Expr interp(Expr expr) {
        Closure c = interp(expr, this.theEnv);
        return c.expr;
    }

    private Closure interp(Expr expr, Env env) {
        if (expr instanceof Apply) {
            Closure lambdaC = interp(expr.lambda, env);
            Expr lambda = lambdaC.expr;
            if (lambda.arg != null) {
                if (expr.var instanceof Apply) {
                    env = new Env(lambda.arg.value, expr.var, env);
                } else if (expr.var instanceof Var) {
                    Closure c = env.lookup(expr.var.value);
                    env = new Env(lambda.arg.value, c, env);
                } else {
                    env = new Env(lambda.arg.value, expr.var, env);
                }
            }
            return interp(lambda.body, env);
        } else if (expr instanceof Var) {
            Closure c = env.lookup(expr.value);
            Closure cc = interp(c.expr, c.env);
            Env newEnv = cc.env.detach(c.env).attach(env);
            return new Closure(cc.expr, newEnv);
        } else {
            Expr newExpr = extendExpr(expr, env, new HashSet<String>());
            return new Closure(newExpr, env);
        }
    }

    private Expr extendExpr(Expr expr, Env env, HashSet<String> boundVars) {
        if (expr instanceof Lambda) {
            if (expr.arg != null) {
                HashSet<String> newBoundVars = new HashSet<String>();
                for (String v: boundVars) {newBoundVars.add(v);}
                newBoundVars.add(expr.arg.value);
                return new Lambda(expr.arg, extendExpr(expr.body, env, newBoundVars));
            }
            return new Lambda(extendExpr(expr.body, env, boundVars));
        } else if (expr instanceof Var) {
            if (boundVars.contains(expr.value)) {
                return expr;
            } else {
                Closure c = env.lookup(expr.value);
                return extendExpr(c.expr, c.env, new HashSet<String>());
            }
        } else if (expr instanceof Apply) {
            return new Apply(extendExpr(expr.lambda, env, boundVars), extendExpr(expr.var, env, boundVars));
        } else {
            throw new RuntimeException("invalid expr: " + expr);
        }
    }

    private String getAvailableName(String name, Env env) {
        Closure isExist = env.lookupGently(name);
        if (isExist == null) {
            return name;
        } else {
            return getAvailableName(MyStr.addLastNumber(name), env);
        }
    }

    public Expr reduceExpr(Expr expr, Env env) {
        try {
            Expr rExpr = reduceExpr(expr, env, 1);
            return rExpr;
        } catch (UnlimitedReduceException e) {
            System.out.println(e.getMessage());
            return expr;
        }
    }

    public Expr reduceExpr(Expr expr, Env env, int count) {
        if (count == 1000) {
            throw new UnlimitedReduceException("Maybe the expr can not be reduced");
        }
        if (expr instanceof Lambda) {
            String shadowName = getAvailableName(expr.arg.value, env);
            env = new Env(expr.arg.value, new VarR(shadowName), env);
            if (! shadowName.equals(expr.arg.value)) {
                env = new Env(shadowName, new VarR("placeHolder"), env);
            }
            expr = new Lambda(new Var(shadowName), reduceExpr(expr.body, env, count + 1));
            return replaceVarR(expr, new Env());
        } else if (expr instanceof Apply) {
            Expr lam = reduceExpr(expr.lambda, env, count + 1);
            if (lam instanceof Lambda) {
                env = new Env(lam.arg.value, reduceExpr(expr.var, env, count + 1), env);
                return reduceExpr(lam.body, env, count + 1);
            } else {
                return new Apply(lam, reduceExpr(expr.var, env, count + 1));
            }
        } else if (expr instanceof Var) {
            Closure c = env.lookup(expr.value);
            return c.expr;
        } else if (expr instanceof VarR) {
            return expr;
        } else {
            throw new RuntimeException("invalid expr: " + expr);
        }
    }

    public Expr replaceVarR(Expr expr, Env env) {
        if (expr instanceof Lambda) {
            env = new Env(expr.arg.value, new Var(expr.arg.value), env);
            return new Lambda(expr.arg, replaceVarR(expr.body, env));
        } else if (expr instanceof Apply) {
            return new Apply(replaceVarR(expr.lambda, env), replaceVarR(expr.var, env));
        } else if (expr instanceof Var) {
            return expr;
        } else if (expr instanceof VarR) {
            Closure c = env.lookupGently(expr.value);
            if (c == null) {
                return expr;
            } else {
                return new Var(c.expr.value);
            }
        } else {
            throw new RuntimeException("invalid expr: " + expr);
        }
    }
}
