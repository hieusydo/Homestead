package com.homestead.launcher;

/** Pure calendar and migration policy, independent of Android and preset list positions. */
final class OutfitWallpaperSchedule {
    static final int DAILY_NATURE=14, DAILY_ABSTRACT=15, DAILY_MIX=16;
    static final int[] NATURE={0,1,2,3,8,9,10,11};
    static final int[] ABSTRACT={4,5,6,7,12,13,14,15};
    static int forDate(int mode, java.util.Calendar date) {
        // Use the local calendar date, not elapsed 24-hour intervals (DST-safe).
        java.util.GregorianCalendar utc = new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"));
        utc.clear();
        utc.set(date.get(java.util.Calendar.YEAR), date.get(java.util.Calendar.MONTH),
                date.get(java.util.Calendar.DAY_OF_MONTH));
        long day = utc.getTimeInMillis() / 86400000L;
        int count = mode == DAILY_MIX ? NATURE.length + ABSTRACT.length : NATURE.length;
        int n = (int) ((day % count + count) % count);
        // Mixed alternates between nature and abstract each day.
        return mode == DAILY_MIX ? (n % 2 == 0 ? NATURE[n / 2] : ABSTRACT[n / 2])
                : (mode == DAILY_ABSTRACT ? ABSTRACT[n] : NATURE[n]);
    }



    static String legacyId(int old, boolean dailyMigrated) {
        String[] ids={"sky","outfit_aurora","charcoal","dusk","black","photo"};
        if(old==1 && !dailyMigrated) return "outfit_daily_mix";
        return old>=0 && old<6 ? ids[old] : old>=6 && old<14 ? "outfit_art_"+(old-6)
                : old==14?"outfit_daily_nature":old==15?"outfit_daily_abstract":"outfit_daily_mix";
    }
}
