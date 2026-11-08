package com.homestead.launcher;
import android.content.Context;
import java.util.List;
class Prefs {
    static final int DESIGN_DOCK=1, SHORTCUTS_BAR=3;
    static void setDockSize(Context c,int v) { c.prefs.edit().putInt("dock_size",v).apply(); }
    static void setDesign(Context c,int v) { c.prefs.edit().putInt("home_design",v).apply(); }
    static void setSimpleMode(Context c,boolean v) { c.prefs.edit().putBoolean("simple_mode",v).apply(); }
    static void setShortcutsPlace(Context c,int v) { c.prefs.edit().putInt("shortcuts_place",v).apply(); }
    static void setPinned(Context c,List<String> v) { c.prefs.edit().putString("pinned",String.join(",",v)).apply(); }
    static void setHidden(Context c,String k,boolean v) { c.prefs.edit().putBoolean(k,v).apply(); }
}
