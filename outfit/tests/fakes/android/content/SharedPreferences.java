package android.content;
import java.util.*;
/** In-memory preference fixture; only the operations used by OutfitDefaults. */
public class SharedPreferences {
    private final Map<String,Object> values=new HashMap<>();
    public boolean contains(String k) { return values.containsKey(k); }
    public boolean getBoolean(String k,boolean d) { return (Boolean)values.getOrDefault(k,d); }
    public int getInt(String k,int d) { return (Integer)values.getOrDefault(k,d); }
    public String getString(String k,String d) { return (String)values.getOrDefault(k,d); }
    public Editor edit() { return new Editor(); }
    public class Editor {
        private final Map<String,Object> changes=new HashMap<>();
        public Editor putBoolean(String k,boolean v) { changes.put(k,v);return this; }
        public Editor putInt(String k,int v) { changes.put(k,v);return this; }
        public Editor putString(String k,String v) { changes.put(k,v);return this; }
        public void apply() { values.putAll(changes); }
    }
}
