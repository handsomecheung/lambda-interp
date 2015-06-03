import java.util.ArrayList;

public class PreDefinition {
    public static String[] all() {
        ArrayList<String> definitions = new ArrayList<String>();

        // boolean
        definitions.add("define true = %x y.x");
        definitions.add("define false = %x y.y");
        definitions.add("define if = %p x y. p x y");

        // pairs
        definitions.add("define pair = %x y f.f x y");
        definitions.add("define fst = %p.p true");
        definitions.add("define snd = %p.p false");

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
        definitions.add("define prefn = %f p.pair (f (fst p)) (fst p)");
        definitions.add("define pre = %n f x.snd (n (prefn f) (pair x x))");
        definitions.add("define sub = %m n.n pre m");

        // table
        definitions.add("define nil = %z.z");
        definitions.add("define cons = %x y.pair false (pair x y)");
        definitions.add("define null = fst");
        definitions.add("define hd = %z.fst (snd z)");
        definitions.add("define tl = %z.snd (snd z)");

        // recursion of CALL BY NAME
        definitions.add("define Y = %f.(%x.f (x x)) (%x.f (x x))");
        definitions.add("define fact = Y (%g n. if (iszero n) 1 (mult n (g (pre n))))");
        definitions.add("define append = Y(%g z w.if (null z) w (cons (hd z) (g (tl z) w)))");
        definitions.add("define inflist = Y(%z.cons MORE z)");

        // recursion of CALL BY VALUE
        definitions.add("define YV = %f.(%x.f (%y.x x y)) (%x.f (%y.x x y))");
        definitions.add("define factV = YV (%g n.(if (iszero n) (%y.1) (%y.mult n (g (pre n)))) y)");

        return definitions.toArray(new String[definitions.size()]);
    }
}
