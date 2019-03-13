import org.junit.Test;

import org.junit.Assert;
import org.junit.Before;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;

public class TestLambdaInterp{
    private static final Pattern DEFINITION_SYNTAX = Pattern.compile("define (.+) = (.+)$");
    private final static Interpreter interp = new Interpreter();

    public void define(String defStr) {
        Matcher m = DEFINITION_SYNTAX.matcher(defStr);
        m.matches();
        String var = m.group(1);
        Expr def = Desugar.desugar(Parser.parser(Token.scan(m.group(2))));
        interp.addDefinition(var, def);
    }

    public Expr doEvaluate(String s) {
        Token[] tokens = Token.scan(s);
        ExprS exprS = Parser.parser(tokens);
        Expr expr = Desugar.desugar(exprS);
        return interp.interp(expr);
    }

    public String evaluate(String s) {
        return doEvaluate(s).toString();
    }

    private void test(String test, String expect, Integer timeout) {
        System.out.println("test: " + test);

        long t1 = new Date().getTime();
        String result = evaluate(test);
        long t2 = new Date().getTime();
        Assert.assertEquals(expect, result);

        if (timeout > 0) {
            Long real = t2 - t1;
            if (real > timeout) {
                Assert.fail(String.format("timeout. expect %d: real: %s", timeout, real));
            }
        }
    }

    @Before public void addDefinition() {
        for(String defStr: PreDefinition.all()) {
            define(defStr);
        }
    }

    @Test public void testEvaluation(){
        test("%x.x", "(%x.x)", 0);
        test("(%x.x) %y.y", "(%y.y)", 0);
        test("%x.%.x", "(%x.(%.x))", 0);
        test("%x y.x", "(%x.(%y.x))", 0);
        test("%x.x %y.x y x", "(%x.(x (%y.((x y) x))))", 0);
        test("(%f x.((%z.f z) x))", "(%f.(%x.(f x)))", 0);
        test("(%f x.((%y.y) f x))", "(%f.(%x.(f x)))", 0);
        test("((%n f x.(%x.x) (n f x)) (%f x.x))", "(%f.(%x.x))", 0);
        test("(%f x.((%x.(x (%x y.x))) (%f.f x x)))", "(%f.(%x.x))", 0);
        test("((%m n f x.m f (n f x)) (%f x.x) (%f x.x))", "(%f.(%x.x))", 0);
        test("((%f x.f (f x)) (%p.(%f.(p f))))", "(%x.(%f.(x f)))", 0);
        test("(%_1._1) (%x.x)", "(%x.x)", 0);
        test("(%f.(%x f.x f) f)", "(%f.(%f1.(f f1)))", 0);
        test("(%n.n (%x.%x y.y) %x y.x) ((%m n f x.m f (n f x)) (%f x.x) (%f x.x))", "(%x.(%y.x))", 0);
        test("(%n.n (%x.%x y.y) %x y.x) ((%m n f x.m f (n f x)) (%f x.x) (%f x.f x))", "(%x.(%y.y))", 0);
        test("(%n.n (%x.%x y.y) %x y.x) ((%m n f x.m f (n f x)) (%f x.f x) (%f x.f x))", "(%x.(%y.y))", 0);

        test("%x.x %y.y", "(%x.(x (%y.y)))", 0);
        test("%x y z.x y z", "(%x.(%y.(%z.((x y) z))))", 0);
        test("(%s.s s) %x.x %y.y", "(%y.y)", 0);

        test("((%f x.f (f (f x))) (%p.(%f. p (p (%x.x)))))", "(%x.(%f.(x (x (%x1.x1)))))", 0);
    }

    @Test public void testDefinition(){
        test("if", "(%p.(%x.(%y.((p x) y))))", 0);
        test("true", "(%x.(%y.x))", 0);
        test("false", "(%x.(%y.y))", 0);
        test("if true (%a.a) (%b.b)", "(%a.a)", 0);
        test("if false (%a.a) (%b.b)", "(%b.b)", 0);
        test("if true (%a.a) ((%x.x x) %x.x x)", "(%a.a)", 0);

        test("suc 0", "(%f.(%x.(f x)))", 0);
        test("1", "(%f.(%x.(f x)))", 0);

        test("iszero (add 0 0)", "(%x.(%y.x))", 0);
        test("iszero (add 0 1)", "(%x.(%y.y))", 0);
        test("iszero (add 1 1)", "(%x.(%y.y))", 0);
        test("add 9", "(%n.(%f.(%x.(f (f (f (f (f (f (f (f (f ((n f) x)))))))))))))", 0);
        test("iszero (add 9 9)", "(%x.(%y.y))", 0);

        test("0", "(%f.(%x.x))", 0);
        test("1", "(%f.(%x.(f x)))", 0);
        test("9", "(%f.(%x.(f (f (f (f (f (f (f (f (f x)))))))))))", 0);

        test("add 0 0", "(%f.(%x.x))", 0);
        test("add 1 2", "(%f.(%x.(f (f (f x)))))", 0);
        test("add 9 9", "(%f.(%x.(f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f x))))))))))))))))))))", 0);

        test("mult 0 0", "(%f.(%x.x))", 0);
        test("mult 1 0", "(%f.(%x.x))", 0);
        test("mult 0 1", "(%f.(%x.x))", 0);
        test("mult 9 9", "(%f.(%x.(f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f x)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))", 0);

        test("iszero (pre 1)", "(%x.(%y.x))", 0);
        test("iszero (pre 2)", "(%x.(%y.y))", 0);

        test("iszero (sub 2 (mult 1 2))", "(%x.(%y.x))", 0);
        test("iszero (sub 3 (mult 1 2))", "(%x.(%y.y))", 0);

        test("iszero (sub 2 (mult 2 1))", "(%x.(%y.x))", 0);
        test("iszero (sub 3 (mult 2 1))", "(%x.(%y.y))", 0);

        test("iszero (sub 1 (car (cons 1 2)))", "(%x.(%y.x))", 0);
        test("iszero (sub 2 (car (cons 1 2)))", "(%x.(%y.y))", 0);
        test("iszero (sub 2 (cdr (cons 1 2)))", "(%x.(%y.x))", 0);
        test("iszero (sub 3 (cdr (cons 1 2)))", "(%x.(%y.y))", 0);

        define("define a = %a.a");
        define("define b = %b.b");
        test("a", "(%a.a)", 0);
        test("b", "(%b.b)", 0);
        test("if true a b", "(%a.a)", 0);
        test("if false a b", "(%b.b)", 0);

        test("sub 1 1", "(%f.(%x.x))", 0);
        test("sub 2 2", "(%f.(%x.x))", 0);
        test("sub 2 1", "(%f.(%x.(f x)))", 0);
        test("sub 3 1", "(%f.(%x.(f (f x))))", 0);
        test("sub 9 1", "(%f.(%x.(f (f (f (f (f (f (f (f x))))))))))", 0);

        test("pre 6", "(%f.(%x.(f (f (f (f (f x)))))))", 0);
        test("pre (pre 6)", "(%f.(%x.(f (f (f (f x))))))", 0);

        test("add 2 1", "(%f.(%x.(f (f (f x)))))", 0);
        test("add 8 9", "(%f.(%x.(f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f x)))))))))))))))))))", 0);

        test("mult 2 1", "(%f.(%x.(f (f x))))", 0);
        test("mult 2 3", "(%f.(%x.(f (f (f (f (f (f x))))))))", 0);
        test("mult 4 3", "(%f.(%x.(f (f (f (f (f (f (f (f (f (f (f (f x))))))))))))))", 0);

        test("car (cons 3 2)", "(%f.(%x.(f (f (f x)))))", 0);
        test("cdr (cons 3 2)", "(%f.(%x.(f (f x))))", 0);
    }

    @Test public void testInfiniteExpr(){
        test("Y", "(%f.(f (f ((%x.(f (x x))) (%x.(f (x x)))))))", 0);
        test("(%x.((%b.(b b)) (%b.(b b))))", "(%x.((%b.(b b)) (%b.(b b))))", 0);
        test("fact 1", "(%f.(%x.(f x)))", 0);

        test("iszero (fact 1)", "(%x.(%y.y))", 0);
        test("iszero (sub 1 (fact 1))", "(%x.(%y.x))", 0);

        test("iszero (sub 2 (fact 2))", "(%x.(%y.x))", 0);
        test("iszero (sub 3 (fact 2))", "(%x.(%y.y))", 0);
    }

    @Test public void testPerformance(){
        test("(%f x.f (f x)) (%n f x.(%p.p (%x y.y)) (n ((%f p.(%x y f.f x y) (f ((%p.p (%x y.x)) p)) ((%p.p (%x y.x)) p)) f) ((%x y f.f x y) x x))) (%f x.f (f (f (f x))))",
             "(%f.(%x.(f (f x))))", 1000);

        test("sub 7 3", "(%f.(%x.(f (f (f (f x))))))", 0);
        test("sub 9 9", "(%f.(%x.x))", 0);

        // test("iszero (sub 9 6)", "(%x.(%y.y))", 0);
        // test("iszero (sub 9 9)", "(%x.(%y.x))", 0);

        // test("iszero (sub 6 (fact 3))", "(%x.(%y.x))", 0);
        // test("iszero (sub 7 (fact 3))", "(%x.(%y.y))", 0);
        // test("iszero (sub 8 (fact 3))", "(%x.(%y.y))", 0);
        // test("iszero (sub 9 (fact 3))", "(%x.(%y.y))", 0);
    }

    @Test public void testException(){
        try {
            test("(%x.(f x))", "", 0);
        }
        catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "can not find symbol: f");
        }
    }

}
