import java.util.ArrayList;

public class PreDefinition {
    public static String[] all() {
        ArrayList<String> definitions = new ArrayList<String>();

        // boolean
        definitions.add("define true = %x y.x");
        definitions.add("define false = %x y.y");
        definitions.add("define if = %p x y. p x y");

        // cons
        definitions.add("define cons = %x y f.f x y");
        definitions.add("define car = %p.p true");
        definitions.add("define cdr = %p.p false");

        // number
        definitions.add("define suc = %n f x.n f (f x)");
        definitions.add("define iszero = %n.n (%x.false) true");
        definitions.add("define 0 = %f x. x");
        definitions.add("define 1 = suc 0");
        definitions.add("define 2 = suc 1");
        definitions.add("define 3 = suc 2");
        definitions.add("define 4 = suc 3");
        definitions.add("define 5 = suc 4");
        definitions.add("define 6 = suc 5");
        definitions.add("define 7 = suc 6");
        definitions.add("define 8 = suc 7");
        definitions.add("define 9 = suc 8");
        definitions.add("define add = %m n f x.m f (n f x)");
        definitions.add("define mult = %m n f.m f (n f)");
        definitions.add("define expt = %m n f x.n m f x");
        definitions.add("define prefn = %f p.cons (f (car p)) (car p)");
        definitions.add("define pre = %n f x.cdr (n (prefn f) (cons x x))");
        definitions.add("define sub = %m n.n pre m");

        // table
        definitions.add("define nil = %z.z");
        definitions.add("define list = %x y.cons false (cons x y)");
        definitions.add("define null = car");
        definitions.add("define head = %z.car (cdr z)");
        definitions.add("define tail = %z.cdr (cdr z)");

        // recursion
        definitions.add("define Y = %f.(%x.f (x x)) (%x.f (x x))");
        definitions.add("define fact = Y (%g n. if (iszero n) 1 (mult n (g (pre n))))");
        definitions.add("define append = Y(%g z w.if (null z) w (list (head z) (g (tail z) w)))");
        // definitions.add("define inflist = Y(%z.list MORE z)");

        return definitions.toArray(new String[definitions.size()]);
    }
}
