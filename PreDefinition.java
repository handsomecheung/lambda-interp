import java.util.ArrayList;

public class PreDefinition {
    public static String[] all() {
        ArrayList<String> definitions = new ArrayList<String>();
        definitions.add("define true = %x y.x");
        definitions.add("define false = %x y.y");
        definitions.add("define if = %p x y. p x y");

        return definitions.toArray(new String[definitions.size()]);
    }
}
