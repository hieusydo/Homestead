package com.homestead.launcher;
import java.util.*;
public class WallpaperTest {
    public static void main(String[] args) {
        for(String zone:new String[]{"America/Los_Angeles","Asia/Tokyo","UTC"}) {
            Calendar c=new GregorianCalendar(TimeZone.getTimeZone(zone)); c.clear(); c.set(2024,0,1,12,0);
            int previous=-1;
            for(int day=0;day<1100;day++) {
                int mix=OutfitWallpaperSchedule.forDate(16,c);
                int nature=OutfitWallpaperSchedule.forDate(14,c), abstractArt=OutfitWallpaperSchedule.forDate(15,c);
                check(mix>=0 && mix<24 && mix!=previous && contains(OutfitWallpaperSchedule.NATURE,nature) && contains(OutfitWallpaperSchedule.ABSTRACT,abstractArt),"collection bounds/next day");
                Calendar later=(Calendar)c.clone(); later.set(Calendar.HOUR_OF_DAY,23);
                check(mix==OutfitWallpaperSchedule.forDate(16,later),"stable within local date");
                previous=mix; c.add(Calendar.DAY_OF_MONTH,1);
            }
        }
        Calendar cycle=new GregorianCalendar(2026,10,1);
        Set<Integer> seen=new HashSet<>();
        for(int i=0;i<24;i++) { seen.add(OutfitWallpaperSchedule.forDate(16,cycle)); cycle.add(Calendar.DAY_OF_MONTH,1); }
        check(seen.size()==24,"every wallpaper appears once per mixed cycle");
        for(int i=0;i<8;i++) check(OutfitWallpaperSchedule.fixedId(16+i).equals(OutfitWallpaperSchedule.UPSTREAM[i]),"upstream asset mapping");
        check(OutfitWallpaperSchedule.resolveId("photo",cycle).equals("photo"),"custom photo not redirected");
        check(OutfitWallpaperSchedule.resolveId("ls_photo_crete",cycle).equals("ls_photo_crete"),"fixed landscape not redirected");
        String[] ids={"sky","outfit_aurora","charcoal","dusk","black","photo",
            "outfit_art_0","outfit_art_1","outfit_art_2","outfit_art_3","outfit_art_4","outfit_art_5","outfit_art_6","outfit_art_7",
            "outfit_daily_nature","outfit_daily_abstract","outfit_daily_mix"};
        for(int i=0;i<ids.length;i++) check(ids[i].equals(OutfitWallpaperSchedule.legacyId(i,true)),"legacy setting "+i);
        check("outfit_daily_mix".equals(OutfitWallpaperSchedule.legacyId(1,false)),"pre-daily Aurora upgrade");
        check("photo".equals(OutfitWallpaperSchedule.legacyId(5,false)),"custom photo preserved");
        System.out.println("PASS: 3,300 dates including DST/leap-year boundaries; all 17 legacy wallpaper IDs");
    }
    static boolean contains(int[] values,int value) { for(int v:values) if(v==value) return true; return false; }
    static void check(boolean ok,String reason) { if(!ok) throw new AssertionError(reason); }
}
