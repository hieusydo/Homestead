package com.homestead.launcher;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

/** Outfit wallpaper IDs and controls, independent of upstream preset ordering. */
final class OutfitBackgrounds {
    static final int DAILY_NATURE=14, DAILY_ABSTRACT=15, DAILY_MIX=16;
    static final String[] NAMES={"Alpine mist","Forest dusk","Desert moon","Ocean quiet",
            "Indigo silk","Jade flow","Plum haze","Amber orbit",
            "Sunset canyon","Moonlit pines","Glacier peaks","Tropical islands",
            "Solar eclipse","Prismatic glass","Cosmic constellation","Coral waves"};
    static final String[] MODES={"outfit_daily_mix","outfit_daily_nature","outfit_daily_abstract"};
    static final String[] COLLECTIONS={"Nature + abstract","Nature","Abstract"};
    static int artIndex(Context c) {
        String id=Backgrounds.currentId(c);
        if(id.startsWith("outfit_daily_")) {
            int mode=id.equals(MODES[1])?14:id.equals(MODES[2])?15:16;
            return OutfitWallpaperSchedule.forDate(mode,java.util.Calendar.getInstance());
        }
        if(id.startsWith("outfit_art_")) try {
            int n=Integer.parseInt(id.substring(11)); return n>=0 && n<NAMES.length?n:-1;
        } catch(NumberFormatException ignored) { }
        return -1;
    }
    static Backgrounds.Preset current(Context c) {
        String id=Backgrounds.currentId(c); int n=artIndex(c);
        if(n<0 && !id.equals("outfit_aurora")) return null;
        return new Backgrounds.Preset(id,n<0?"Aurora":(daily(c)?"Daily · ":"")+NAMES[n],
                0xFF09131F,0xFF172535,0xFFFFFFFF,0xFF6DE7FF,0x3DFFFFFF);
    }
    static boolean daily(Context c) { return Backgrounds.currentId(c).startsWith("outfit_daily_"); }
    static Drawable drawable(Context c, Backgrounds.Preset p) {
        int art=artIndex(c);
        if(art>=0) return new ScenicWallpaper(art);
        if(p.id.equals("outfit_aurora")) return c.getResources().getDrawable(R.drawable.aurora_wallpaper);
        return Backgrounds.drawable(p);
    }
    static int collection(Context c) {
        return Math.max(0,Math.min(2,c.getSharedPreferences("settings",0).getInt("wallpaper_collection",0)));
    }
    static View panel(final Context c, final SettingsPanel.Host host, final Runnable changed,
            final Runnable standard) {
        final Panel p=new Panel(c,"Background","Offline nature and abstract artwork",Panel.SHEET);
        final Panel.Row daily=p.addRow("Change daily",daily(c)?"On":"Off",null);
        final Panel.Row collection=p.addRow("Daily collection",COLLECTIONS[collection(c)],null);
        final Panel.Row fixed=p.addRow("Fixed artwork",Backgrounds.label(c),null);
        final Runnable refresh=()-> {
            daily.setValue(daily(c)?"On":"Off"); collection.setValue(COLLECTIONS[collection(c)]);
            fixed.setValue(Backgrounds.label(c)); host.rebuildGrid(); changed.run();
        };
        daily.cell.setOnClickListener(v->{
            Backgrounds.choose(c,daily(c)?"outfit_art_"+Math.max(0,artIndex(c)):MODES[collection(c)]);
            refresh.run();
        });
        daily.setHelp("Changes once per local calendar day, while Home is open or when you return. Turn off to keep today's artwork.");
        collection.cell.setOnClickListener(v->{
            int n=(collection(c)+1)%3;
            c.getSharedPreferences("settings",0).edit().putInt("wallpaper_collection",n).apply();
            if(daily(c)) Backgrounds.choose(c,MODES[n]); refresh.run();
        });
        fixed.cell.setOnClickListener(v->{
            int n=artIndex(c); Backgrounds.choose(c,n==NAMES.length-1?"outfit_aurora":"outfit_art_"+(n+1)); refresh.run();
        });
        p.addRow("Other colors and custom photo","",standard).opens();
        p.addRow("Back","",()->host.closePanel());
        return p.root;
    }
}
