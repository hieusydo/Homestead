package com.homestead.launcher;
import android.content.Context;
class DockMigrationTest {
    public static void main(String[] args) {
        Context existing=new Context();
        existing.prefs.edit().putBoolean("outfit_modular_v1",true)
            .putString("background_id","outfit_daily_mix").putBoolean("simple_mode",true).apply();
        OutfitDefaults.initialize(existing);
        check(existing.prefs.getInt("home_design",-1)==1,"existing installs get dock migration");
        check(!existing.prefs.getBoolean("simple_mode",true),"dock not suppressed by Simple mode");
        check(existing.prefs.getString("pinned","").split(",").length==4,"four favorites seeded");
        check(existing.prefs.getString("pinned","").startsWith("app:com.amazon.firetv.youtube,app:com.netflix.ninja,"),"favorite order");
        check(existing.prefs.getString("background_id","").equals("outfit_daily_mix"),"wallpaper retained");
        check(existing.prefs.getBoolean("greeting",false),"greeting enabled");
        check(existing.prefs.getInt("date_format",-1)==1,"date enabled");
        check(existing.prefs.getInt("dock_size",-1)==1,"four tiles fit");
        existing.prefs.edit().putInt("home_design",0).putString("pinned","user-choice").apply();
        OutfitDefaults.initialize(existing);
        check(existing.prefs.getInt("home_design",-1)==0,"subsequent design choice preserved");
        check(existing.prefs.getString("pinned","").equals("user-choice"),"subsequent pins preserved");
        Context configured=new Context();
        configured.prefs.edit().putInt("home_design",0).putString("pinned","custom")
            .putInt("shortcuts_place",1).putString("background_id","photo").apply();
        OutfitDefaults.initialize(configured);
        check(configured.prefs.getInt("home_design",-1)==0,"existing upstream design preserved");
        check(configured.prefs.getInt("shortcuts_place",-1)==1,"existing shortcuts preserved");
        check(configured.prefs.getString("pinned","").equals("app:com.amazon.firetv.youtube,app:com.netflix.ninja,app:com.peacock.peacockfiretv,app:com.abclocal.kgo.tv.amazon"),"requested four-app profile applied exactly once");
        check(configured.prefs.getString("background_id","").equals("photo"),"photo preserved");
        Context fresh=new Context(); OutfitDefaults.initialize(fresh);
        check(fresh.prefs.getInt("home_design",-1)==1,"fresh dock default");
        System.out.println("PASS: dock migration for existing, configured and fresh installs; repeat migration preserves choices");
    }
    static void check(boolean ok,String reason) { if(!ok) throw new AssertionError(reason); }
}
