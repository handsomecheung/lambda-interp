import java.util.HashMap;

class Store {
    private HashMap<Integer, Expr> storage = new HashMap<Integer, Expr>();

    public Store() {}

    public Expr fetch(int location) {
        if (this.storage.containsKey(location)) {
            return this.storage.get(location);
        } else {
            throw new RuntimeException("can not find location: " + location);
        }
    }

    public void set(int location, Expr expr) {
        this.storage.put(location, expr);
    }
}
