package com.homestead.launcher;

import android.content.Context;
import android.content.SharedPreferences;

/** One-time Outfit defaults/migrations; upstream Prefs remains unchanged. */
final class OutfitDefaults {
    static void initialize(Context c) {
        SharedPreferences p=c.getSharedPreferences("settings",Context.MODE_PRIVATE);
        migrateDock(c, p);
        if (p.getBoolean("outfit_modular_v1",false)) return;
        SharedPreferences.Editor e=p.edit();
        if(!p.contains("columns")) e.putInt("columns",5);
        if(!p.contains("sec_nontv")) e.putBoolean("sec_nontv",false);
        if(!p.contains("adb_reapply_on_boot")) e.putBoolean("adb_reapply_on_boot",false);
        if(!p.contains("onboarded")) e.putBoolean("onboarded",true);
        if(!p.contains("update_auto_check")) e.putBoolean("update_auto_check",false);
        if(!p.contains("background_id")) {
            int old=p.getInt("background",16);
            String id=OutfitWallpaperSchedule.legacyId(old,p.getBoolean("daily_wallpaper_v1",false));
            e.putString("background_id",id);
        }
        e.putBoolean("outfit_modular_v1",true).apply();
        if(!p.getBoolean("outfit_utility_visibility_v1",false)) {
            Prefs.setHidden(c,"app:com.ghisler.android.TotalCommander",true);
            Prefs.setHidden(c,"app:com.esaba.downloader",true);
            Prefs.setHidden(c,"app:com.amazon.firetv.youtube",false);
            p.edit().putBoolean("outfit_utility_visibility_v1",true).apply();
        }
        if(!p.getBoolean("outfit_hide_airplay_v1",false)) {
            Prefs.setHidden(c,"app:com.apple.airplay.amazon.airplay",true);
            p.edit().putBoolean("outfit_hide_airplay_v1",true).apply();
        }
    }
    /** Adopt upstream settings once; subsequent user choices always win. */
    private static void migrateDock(Context c, SharedPreferences p) {
        if (p.getBoolean("outfit_upstream_dock_v1", false)) return;
        if (!p.contains("home_design")) {
            Prefs.setDesign(c, Prefs.DESIGN_DOCK);
            Prefs.setSimpleMode(c, false); // Upstream Simple mode intentionally uses Classic.
        }
        if (!p.contains("shortcuts_place")) Prefs.setShortcutsPlace(c, Prefs.SHORTCUTS_BAR);
        if (!p.contains("pinned")) Prefs.setPinned(c, java.util.Arrays.asList(
                "app:com.amazon.firetv.youtube", "app:com.netflix.ninja", "app:com.hbo.hbonow",
                "app:com.abclocal.kgo.tv.amazon", "app:com.nbcuni.nbcots.nbcbayarea.firetv"));
        p.edit().putBoolean("outfit_upstream_dock_v1", true).apply();
    }
}
