import java.util.HashMap;

public class Debugger {
    public static void printEnv(Env env) {
        System.out.println();
        System.out.println("--------- env print begin -------------------------");
        printEnv(env, 1);
        System.out.println("--------- env print end   -------------------------");
        System.out.println();
    }

    public static void printEnv(Env env, int level) {
        System.out.println("---------- env level " + level + " ------------------");

        HashMap<String, EnvValue> theEnv = env.getEnv();
        Env theOuter = env.getOuter();
        for (HashMap.Entry<String, EnvValue> entry: theEnv.entrySet()) {
            System.out.println(entry.getKey() + ": " + Display.expr2String(entry.getValue().getValue()));
        }
        if (theOuter != null) {
            printEnv(theOuter, level + 1);
        }
    }
}
