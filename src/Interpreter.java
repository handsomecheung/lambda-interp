import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;

public class Interpreter {
    private Env env = new Env();
    private HashSet<String> allVars = new HashSet<String>();
    private HashSet<String> infiniteApplies = new HashSet<String>();

    private boolean debug = false;
    private boolean silence = true;

    public Interpreter(){}

    public void addDefinition(String key, Expr expr) {
        boolean o = this.silence;
        this.silence = true;
        Expr newExpr = interp(expr);
        this.env = this.env.add(key, newExpr);
        this.silence = o;
    }

    public void enableDebug() {
        this.debug = true;
    }

    public void disableSilence() {
        this.silence = false;
    }

    public Expr interp(Expr expr) {
        this.allVars = new HashSet<String>();
        this.infiniteApplies = new HashSet<String>();

        Expr result;
        result = interp(expr, this.env, new HashSet<String>(), false);
        result = interp(result, this.env, new HashSet<String>(), true);
        result = exprSimplify(result, this.env, new HashSet<String>());

        return result;
    }

    private Expr interp(Expr expr, Env env, HashSet<String> allApplies, boolean continual) {
        interpDebugEntry(expr, env, allApplies, continual);

        Expr result;

        if (expr instanceof ExprVar) {
            result = env.lookup(expr.value);
            if (!(result instanceof ExprVar)) {
                result = interp(result, env, allApplies, continual);
            }
        } else if (expr instanceof ExprLambda) {
            result = extendExprLambda(expr, env, allApplies, continual);
        } else if (expr instanceof ExprApply) {
            result = new ExprApply(interp(expr.rator, env, allApplies, continual),
                                   extendExpr(expr.rand, env, allApplies, false));
            if (!isInfinite(result)) {
                if (result.rator instanceof ExprLambda) {
                    HashSet<String> newAllApplies = addApply(allApplies, result);
                    if (newAllApplies.size() == allApplies.size()) {
                        result = extendExprOnce(result, env, allApplies);
                        if (!this.silence) {
                            Expr hint = exprSimplify(result, new Env(), new HashSet<String>());
                            System.out.printf("Warning: infinite loop happens in expression `%s`\n\n",
                                              hint);
                        }
                    } else {
                        Env newEnv = env.add(result.rator.arg.value, result.rand);
                        result = interp(result.rator.body, newEnv, newAllApplies, continual);
                    }
                }
            }
        } else {
            throw invalidExpr(expr);
        }

        interpDebugExit(expr, env, allApplies, continual, result);

        return result;
    }

    private void interpDebugEntry(Expr expr, Env env,
                                  HashSet<String> allApplies, boolean continual) {

        if (this.debug) {
            System.out.println();
            System.out.println("==========================================================");
            System.out.println("-------------------- interp entry ------------------------");
            System.out.printf("interp expr, %s: %s\n\n", expr.getClass(), expr);
            System.out.printf("interp continual: %s\n\n", continual);
            System.out.printf("interp all applies: %s\n\n", allApplies);
            System.out.printf("interp allVars: %s\n\n", this.allVars);
            System.out.printf("interp env: %s\n\n", env);
        }
    }

    private void interpDebugExit(Expr expr, Env env,
                                 HashSet<String> allApplies, boolean continual, Expr result) {
        if (this.debug) {
            System.out.printf("interp expr, %s: %s\n\n", expr.getClass(), expr);
            System.out.printf("interp continual: %s\n\n", continual);
            System.out.printf("interp result, %s: %s\n\n", result.getClass(), result);
            System.out.printf("interp all applies: %s\n\n", allApplies);
            System.out.printf("interp env: %s\n\n", env);
            System.out.println("-------------------- interp exit -------------------------");
            System.out.println("==========================================================");
            System.out.println();
        }
    }

    private Expr extendExprLambda(Expr expr, Env env, HashSet<String> allApplies, boolean continual) {
        Env newEnv = env;
        ExprVar originalArg = expr.arg;
        ExprVar newArg = expr.arg;
        if (newArg != null) {
            newArg = tryRenameVar(newArg, this.allVars);
            newEnv = newEnv.add(originalArg.value, newArg);
            if (!originalArg.value.equals(newArg.value)) {
                newEnv = newEnv.add(newArg.value, newArg);
            }

            this.allVars.add(newArg.value);
        }

        return new ExprLambda(newArg, extendExpr(expr.body, newEnv, allApplies, continual));
    }

    private Expr extendExpr(Expr expr, Env env, HashSet<String> allApplies, boolean continual) {
        extendExprDebugEntry(expr, env, allApplies, continual);

        Expr result;

        if (expr instanceof ExprVar) {
            if (continual) {
                result = interp(expr, env, allApplies, continual);
            } else {
                result = env.lookup(expr.value);
            }
        } else if (expr instanceof ExprLambda) {
            result = interp(expr, env, allApplies, continual);
        } else if (expr instanceof ExprApply) {
            result = new ExprApply(extendExpr(expr.rator, env, allApplies, continual),
                                   extendExpr(expr.rand, env, allApplies, continual));
            if (continual && !isInfinite(expr)) {
                result = interp(result, env, allApplies, continual);
            }
        } else {
            throw invalidExpr(expr);
        }

        extendExprDebugExit(expr, env, allApplies, continual, result);

        return result;
    }

    private void extendExprDebugEntry(Expr expr, Env env,
                                      HashSet<String> allApplies, boolean continual) {

        if (this.debug) {
            System.out.println();
            System.out.println("==========================================================");
            System.out.println("-------------------- lambda entry ------------------------");
            System.out.printf("extendExpr %s, expr: %s\n\n", expr.getClass(), expr);
            System.out.printf("extendExpr continual: %s\n\n", continual);
            System.out.printf("extendExpr all applies: %s\n\n", allApplies);
            System.out.printf("extendExpr env: %s\n\n", env);
        }
    }

    private void extendExprDebugExit(Expr expr, Env env,
                                     HashSet<String> allApplies, boolean continual,
                                     Expr result) {

        if (this.debug) {
            System.out.printf("extendExpr expr: %s, %s\n\n", expr.getClass(), expr);
            System.out.printf("extendExpr result: %s, %s\n\n", result.getClass(), result);
            System.out.printf("extendExpr all applies: %s\n\n", allApplies);
            System.out.printf("extendExpr env: %s\n\n", env);
            System.out.println("-------------------- lambda exit -------------------------");
            System.out.println("==========================================================");
            System.out.println();
        }
    }

    private Expr extendExprOnce(Expr expr, Env env, HashSet<String> allApplies) {
        Expr result;

        if (expr instanceof ExprApply) {
            Expr rator = expr.rator;
            Expr rand = expr.rand;

            if (rand instanceof ExprLambda) {
                rand = extendExprLambda(rand, env, allApplies, false);
            } else {
                throw invalidExpr(expr);
            }

            if (rator instanceof ExprLambda) {
                Env newEnv = env;
                if (rator.arg != null) {
                    newEnv = newEnv.add(rator.arg.value, rator);
                }
                result = extendExpr(rator.body, newEnv, allApplies, false);
            } else {
                throw invalidExpr(expr);
            }
        } else {
            throw invalidExpr(expr);
        }

        return result;
    }

    private ExprVar renameVar(ExprVar expr) {
        return new ExprVar(MyStr.addLastNumber(expr.value));
    }

    private ExprVar tryRenameVar(ExprVar target, HashSet<String> vars) {
        while (vars.contains(target.value)) {
            target = renameVar(target);
        }
        return target;
    }

    private interface VarChanger {
        String method(String str);
    }

    private ExprVar tryChangeExprVar(ExprVar target, HashSet<String> vars, VarChanger changer) {
        target = new ExprVar(changer.method(target.value));
        return tryRenameVar(target, vars);
    }

    private Expr exprVarModify(Expr expr, Env env, HashSet<String> vars, VarChanger changer) {
        Expr result;

        if (expr instanceof ExprVar) {
            result = env.lookupGently(expr.value);
            if (result == null) {
                HashSet<String> newVars = new HashSet<String>(vars);
                result = tryChangeExprVar(new ExprVar(expr.value), vars, changer);
                newVars.add(result.value);
            } else if (!(result instanceof ExprVar)) {
                result = exprVarModify(result, env, vars, changer);
            }
        } else if (expr instanceof ExprLambda) {
            Env newEnv = env;
            ExprVar originalArg = expr.arg;
            ExprVar newArg = expr.arg;
            HashSet<String> newVars = new HashSet<String>(vars);
            if (newArg != null) {
                newArg = tryChangeExprVar(newArg, vars, changer);
                newEnv = newEnv.add(originalArg.value, newArg);
                newVars.add(newArg.value);
            }
            result = new ExprLambda(newArg, exprVarModify(expr.body, newEnv, newVars, changer));
        } else if (expr instanceof ExprApply) {
            result = new ExprApply(exprVarModify(expr.rator, env, vars, changer),
                                   exprVarModify(expr.rand, env, vars, changer));
        } else {
            throw invalidExpr(expr);
        }

        return result;
    }

    private Expr exprSimplify(Expr expr, Env env, HashSet<String> vars) {
        VarChanger changer = str -> MyStr.removeLastNumber(str);
        return exprVarModify(expr, env, vars, changer);
    }

    private Expr exprUnify(Expr expr, Env env, HashSet<String> vars) {
        VarChanger changer = str -> "x";
        return exprVarModify(expr, env, vars, changer);
    }

    private boolean isInfinite(Expr expr) {
        String unifiedExpr = exprUnify(expr, new Env(), new HashSet<String>()).toString();
        return this.infiniteApplies.contains(unifiedExpr);
    }

    private HashSet<String> addApply(HashSet<String> allApplies, Expr expr) {
        HashSet<String> newAllApplies = allApplies;
        String unifiedExpr = exprUnify(expr, new Env(), new HashSet<String>()).toString();

        boolean existed = allApplies.contains(unifiedExpr);
        if (!existed) {
            newAllApplies = new HashSet<String>(newAllApplies);
            newAllApplies.add(unifiedExpr);
        } else {
            this.infiniteApplies.add(unifiedExpr);
        }

        return newAllApplies;
    }

    private RuntimeException invalidExpr(Expr expr) {
        return new RuntimeException(String.format("invalid expr: %s, %s", expr.getClass(), expr));
    }
}
