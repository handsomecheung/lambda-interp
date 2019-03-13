import java.util.HashMap;

class Env {
    private Env outer = null;
    private HashMap<String, Integer> env = new HashMap<String, Integer>();

    private static int currentLocation = 1;
    private static int makeLocation() {
        currentLocation = currentLocation + 1;
        return currentLocation;
    }

    private static Store store = new Store();

    public Env() {}

    public Env(String key, Expr expr) {
        int location = makeLocation();
        this.env.put(key, location);
        this.outer = null;
        Env.store.set(location, expr);
    }

    public Env(HashMap<String, Integer> env) {
        this.env = env;
    }

    public Env add(String key, Expr expr) {
        Env env = new Env(key, expr);
        env.outer = this;
        return env;
    }

    public Expr lookupGently(String key) {
        HashMap<String, Integer> e = find(key);
        if (e == null) {
            return null;
        } else {
            return Env.store.fetch(e.get(key));
        }
    }

    public Expr lookup(String key) {
        Expr r = lookupGently(key);
        if (r == null) {
            throw new IllInterpException("can not find symbol: " + key);
        } else {
            return r;
        }
    }

    private HashMap<String, Integer> find(String key) {
        if (this.env.containsKey(key)) {
            return this.env;
        } else if (this.outer != null) {
            return this.outer.find(key);
        } else {
            return null;
        }
    }

    public String toString() {
        String s = "";
        for (HashMap.Entry<String, Integer> entry: this.env.entrySet()) {
            Expr val = Env.store.fetch(entry.getValue());
            if (s.equals("")) {
                s = String.format("%s: %s", entry.getKey(), val);
            } else {
                s = String.format("%s, %s: %s", s, entry.getKey(), val);
            }
        }
        s = String.format("{%s}", s);

        if (this.outer != null) {
            s = s + ";" + this.outer.toString();
        }
        return s;
    }
}
