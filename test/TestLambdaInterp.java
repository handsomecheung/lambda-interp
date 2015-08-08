import org.junit.Test;

import static org.junit.Assert.assertEquals;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String evalAndReduce(String s) {
        return interp.reduceExpr(doEvaluate(s), new Env()).toString();
    }

    @Test public void testEvaluation(){
        assertEquals("(%x.x)", evaluate("%x.x"));
        assertEquals("(%y.y)", evaluate("(%x.x) %y.y"));
        assertEquals("(%x.(%.x))", evaluate("%x.%.x"));
        assertEquals("(%x.(%y.x))", evaluate("%x y.x"));
        assertEquals("(%x.(x (%y.((x y) x))))", evaluate("%x.x %y.x y x"));
        assertEquals("(%x.(%y.x))", evaluate("(%n.n (%x.%x y.y) %x y.x) ((%m n f x.m f (n f x)) (%f x.x) (%f x.x))"));
        assertEquals("(%x.(%y.y))", evaluate("(%n.n (%x.%x y.y) %x y.x) ((%m n f x.m f (n f x)) (%f x.x) (%f x.f x))"));
        assertEquals("(%x.(%y.y))", evaluate("(%n.n (%x.%x y.y) %x y.x) ((%m n f x.m f (n f x)) (%f x.f x) (%f x.f x))"));

        assertEquals("(%x.(x (%y.y)))", evaluate("%x.x %y.y"));
        assertEquals("(%x.(%y.(%z.((x y) z))))", evaluate("%x y z.x y z"));
        assertEquals("(%y.y)", evaluate("(%s.s s) %x.x %y.y"));
    }

    @Test public void testDefinition(){
        for(String defStr: PreDefinition.all()){define(defStr);}

        assertEquals("(%p.(%x.(%y.((p x) y))))", evaluate("if"));
        assertEquals("(%x.(%y.x))", evaluate("true"));
        assertEquals("(%x.(%y.y))", evaluate("false"));
        assertEquals("(%a.a)", evaluate("if true (%a.a) (%b.b)"));
        assertEquals("(%b.b)", evaluate("if false (%a.a) (%b.b)"));
        assertEquals("(%a.a)", evaluate("if true (%a.a) ((%x.x x) %x.x x)"));

        assertEquals("(%x.(%y.x))", evaluate("iszero (add 0 0)"));
        assertEquals("(%x.(%y.y))", evaluate("iszero (add 0 1)"));
        assertEquals("(%x.(%y.y))", evaluate("iszero (add 1 1)"));
        assertEquals("(%x.(%y.y))", evaluate("iszero (add 9 9)"));

        assertEquals("(%x.(%y.x))", evaluate("iszero (pre 1)"));
        assertEquals("(%x.(%y.y))", evaluate("iszero (pre 2)"));

        assertEquals("(%x.(%y.x))", evaluate("iszero (sub 9 9)"));
        assertEquals("(%x.(%y.y))", evaluate("iszero (sub 9 6)"));

        assertEquals("(%x.(%y.x))", evaluate("iszero (sub 2 (mult 1 2))"));
        assertEquals("(%x.(%y.y))", evaluate("iszero (sub 3 (mult 1 2))"));

        assertEquals("(%x.(%y.x))", evaluate("iszero (sub 2 (mult 2 1))"));
        assertEquals("(%x.(%y.y))", evaluate("iszero (sub 3 (mult 2 1))"));

        assertEquals("(%x.(%y.y))", evaluate("iszero (sub 9 (fact 3))"));
        assertEquals("(%x.(%y.y))", evaluate("iszero (sub 8 (fact 3))"));
        assertEquals("(%x.(%y.y))", evaluate("iszero (sub 7 (fact 3))"));
        assertEquals("(%x.(%y.x))", evaluate("iszero (sub 6 (fact 3))"));

        assertEquals("(%x.(%y.x))", evaluate("iszero (sub 1 (car (cons 1 2)))"));
        assertEquals("(%x.(%y.y))", evaluate("iszero (sub 2 (car (cons 1 2)))"));
        assertEquals("(%x.(%y.x))", evaluate("iszero (sub 2 (cdr (cons 1 2)))"));
        assertEquals("(%x.(%y.y))", evaluate("iszero (sub 3 (cdr (cons 1 2)))"));

        define("define a = %a.a");
        define("define b = %b.b");
        assertEquals("(%a.a)", evaluate("a"));
        assertEquals("(%b.b)", evaluate("b"));
        assertEquals("(%a.a)", evaluate("if true a b"));
        assertEquals("(%b.b)", evaluate("if false a b"));
    }

    @Test public void testReduce(){
        for(String defStr: PreDefinition.all()){define(defStr);}
        assertEquals("(%f.(%x.x))", evalAndReduce("sub 1 1"));
        assertEquals("(%f.(%x.x))", evalAndReduce("sub 2 2"));
        assertEquals("(%f.(%x.x))", evalAndReduce("sub 9 9"));
        assertEquals("(%f.(%x.(f x)))", evalAndReduce("sub 2 1"));
        assertEquals("(%f.(%x.(f (f x))))", evalAndReduce("sub 3 1"));
        assertEquals("(%f.(%x.(f (f (f (f (f (f (f (f x))))))))))", evalAndReduce("sub 9 1"));
        assertEquals("(%f.(%x.(f (f (f (f x))))))", evalAndReduce("sub 7 3"));
        assertEquals("(%f.(%x.(f (f (f (f (f x)))))))", evalAndReduce("pre 6"));
        assertEquals("(%f.(%x.(f (f (f (f x))))))", evalAndReduce("pre (pre 6)"));

        assertEquals("(%f.(%x.(f (f (f x)))))", evalAndReduce("add 2 1"));
        assertEquals("(%f.(%x.(f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f (f x)))))))))))))))))))", evalAndReduce("add 8 9"));

        assertEquals("(%f.(%x.(f (f x))))", evalAndReduce("mult 2 1"));
        assertEquals("(%f.(%x.(f (f (f (f (f (f x))))))))", evalAndReduce("mult 2 3"));
        assertEquals("(%f.(%x.(f (f (f (f (f (f (f (f (f (f (f (f x))))))))))))))", evalAndReduce("mult 4 3"));

        assertEquals("(%f.(%x.(f (f (f x)))))", evalAndReduce("car (cons 3 2)"));
        assertEquals("(%f.(%x.(f (f x))))", evalAndReduce("cdr (cons 3 2)"));
        assertEquals("(%x.((%b.(b b)) (%b.(b b))))", evalAndReduce("(%x.((%b.(b b)) (%b.(b b))))"));       
    }
}
