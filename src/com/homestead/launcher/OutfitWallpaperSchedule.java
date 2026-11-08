package com.homestead.launcher;

/** Pure calendar and migration policy, independent of Android and preset list positions. */
final class OutfitWallpaperSchedule {
    static final int DAILY_NATURE=14, DAILY_ABSTRACT=15, DAILY_MIX=16;
    static final int[] NATURE={0,1,2,3,8,9,10,11,16,17,18,19,20,21,22,23};
    static final int[] ABSTRACT={4,5,6,7,12,13,14,15};
    static final int[] MIXED={0,4,16,1,5,17,2,6,18,3,7,19,8,12,20,9,13,21,10,14,22,11,15,23};
    static final String[] UPSTREAM={"ls_drawn_dunes","ls_drawn_dusk","ls_drawn_forest","ls_drawn_lake",
            "ls_photo_ciucas","ls_photo_crete","ls_photo_sanjuan","ls_photo_scuol"};
    static String fixedId(int index) { return index>=16 ? UPSTREAM[index-16] : "outfit_art_"+index; }
    static String resolveId(String stored, java.util.Calendar date) {
        if (!stored.startsWith("outfit_daily_")) return stored;
        int mode=stored.endsWith("nature")?DAILY_NATURE:stored.endsWith("abstract")?DAILY_ABSTRACT:DAILY_MIX;
        int index=forDate(mode,date);
        return index>=16?fixedId(index):stored;
    }
    static int forDate(int mode, java.util.Calendar date) {
        // Use the local calendar date, not elapsed 24-hour intervals (DST-safe).
        java.util.GregorianCalendar utc = new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"));
        utc.clear();
        utc.set(date.get(java.util.Calendar.YEAR), date.get(java.util.Calendar.MONTH),
                date.get(java.util.Calendar.DAY_OF_MONTH));
        long day = utc.getTimeInMillis() / 86400000L;
        int[] pool=mode==DAILY_MIX?MIXED:mode==DAILY_ABSTRACT?ABSTRACT:NATURE;
        return pool[(int)((day%pool.length+pool.length)%pool.length)];
    }



    static String legacyId(int old, boolean dailyMigrated) {
        String[] ids={"sky","outfit_aurora","charcoal","dusk","black","photo"};
        if(old==1 && !dailyMigrated) return "outfit_daily_mix";
        return old>=0 && old<6 ? ids[old] : old>=6 && old<14 ? "outfit_art_"+(old-6)
                : old==14?"outfit_daily_nature":old==15?"outfit_daily_abstract":"outfit_daily_mix";
    }
}
