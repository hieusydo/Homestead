package com.homestead.launcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/** Opt-in daily feeds. One private, validated image per source; no background wakeups. */
final class OutfitRemoteWallpaper {
    static final String BING="outfit_remote_bing", NASA="outfit_remote_nasa";
    private static final Handler MAIN=new Handler(Looper.getMainLooper());
    private static boolean busy;
    private static SharedPreferences prefs(Context c) { return c.getSharedPreferences("outfit_remote",0); }
    static String source(Context c) {
        String id=Prefs.backgroundId(c);
        return BING.equals(id)?"bing":NASA.equals(id)?"nasa":null;
    }
    static boolean selected(Context c) { return source(c)!=null; }
    static File file(Context c) {
        String s=source(c); if(s==null) return null;
        File f=new File(c.getFilesDir(),"wallpaper-"+s+".jpg");
        return f.isFile()?f:null;
    }
    private static String day() { return new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date()); }
    static void refresh(Context context,boolean force) {
        final Context c=context.getApplicationContext(); final String source=source(c);
        if(source==null || busy) return;
        final SharedPreferences p=prefs(c); final String today=day();
        long now=System.currentTimeMillis(), age=now-p.getLong(source+"_attempt",0);
        if(!force && today.equals(p.getString(source+"_day","")) && file(c)!=null) return;
        if(age>=0 && age<(force?60000L:3600000L)) return;
        busy=true; p.edit().putLong(source+"_attempt",now).putString(source+"_status","Fetching today's image…").apply();
        new Thread(()-> {
            File temp=new File(c.getFilesDir(),"wallpaper-"+source+".tmp");
            Bitmap bitmap=null,scaled=null;
            try {
                String endpoint=source.equals("bing")
                    ?"https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US"
                    :"https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY&date="+today;
                JSONObject data=new JSONObject(new String(fetch(endpoint,source,OutfitRemotePolicy.MAX_JSON,false),"UTF-8"));
                String url,title,credit,date;
                if(source.equals("bing")) {
                    JSONObject im=data.getJSONArray("images").getJSONObject(0);
                    if(!im.optBoolean("wp",false)) throw new IOException("Bing does not offer today's image as wallpaper");
                    String relative=im.getString("url");
                    url=new URL(new URL("https://www.bing.com"),relative).toString();
                    title=im.optString("title","Bing image of the day"); credit=im.optString("copyright",""); date=im.optString("startdate",today);
                } else {
                    url=OutfitRemotePolicy.nasaImage(data.optString("media_type"),data.optString("hdurl"),data.optString("url"));
                    title=data.optString("title","NASA APOD"); credit=data.optString("copyright","NASA APOD — see source for image credit"); date=data.optString("date",today);
                }
                byte[] bytes=fetch(url,source,OutfitRemotePolicy.MAX_IMAGE,true);
                BitmapFactory.Options bounds=new BitmapFactory.Options(); bounds.inJustDecodeBounds=true;
                BitmapFactory.decodeByteArray(bytes,0,bytes.length,bounds);
                if(bounds.outWidth<320 || bounds.outHeight<180 || bounds.outWidth>30000 || bounds.outHeight>30000)
                    throw new IOException("Source did not return a usable wallpaper");
                BitmapFactory.Options options=new BitmapFactory.Options(); options.inSampleSize=1;
                while(Math.max(bounds.outWidth,bounds.outHeight)/options.inSampleSize>2400) options.inSampleSize*=2;
                bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
                if(bitmap==null) throw new IOException("Image could not be decoded");
                float ratio=Math.min(1f,1920f/Math.max(bitmap.getWidth(),bitmap.getHeight()));
                scaled=Bitmap.createScaledBitmap(bitmap,Math.max(1,Math.round(bitmap.getWidth()*ratio)),Math.max(1,Math.round(bitmap.getHeight()*ratio)),true);
                try(FileOutputStream out=new FileOutputStream(temp)) {
                    if(!scaled.compress(Bitmap.CompressFormat.JPEG,90,out)) throw new IOException("Image could not be saved");
                    out.getFD().sync();
                }
                File dest=new File(c.getFilesDir(),"wallpaper-"+source+".jpg");
                android.system.Os.rename(temp.getAbsolutePath(),dest.getAbsolutePath());
                p.edit().putString(source+"_day",today).putString(source+"_title",title)
                    .putString(source+"_credit",credit).putString(source+"_date",date)
                    .putString(source+"_status","Ready · "+date).apply();
            } catch(Exception e) {
                // Never replace a good cached photo with an error page, video or logo.
                String message=e instanceof IOException?e.getMessage():"Source response could not be used";
                p.edit().putString(source+"_status",message+" · keeping cached/offline wallpaper").apply();
            } finally {
                temp.delete(); if(scaled!=null && scaled!=bitmap) scaled.recycle(); if(bitmap!=null) bitmap.recycle();
                MAIN.post(()->busy=false);
            }
        },"outfit-wallpaper-feed").start();
    }
    private static byte[] fetch(String address,String source,int limit,boolean image) throws IOException {
        URL url=OutfitRemotePolicy.allowed(address,source);
        for(int redirects=0;redirects<5;redirects++) {
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(15000); conn.setReadTimeout(20000); conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("User-Agent","Homestead-Outfit/2.1 wallpaper");
            try {
                int code=conn.getResponseCode();
                if(code>=300 && code<400) {
                    String location=conn.getHeaderField("Location");
                    if(location==null) throw new IOException("Source returned an invalid redirect");
                    url=OutfitRemotePolicy.allowed(new URL(url,location).toString(),source); continue;
                }
                if(code!=200) throw new IOException(code==429?"Source rate limit reached":"Source unavailable (HTTP "+code+")");
                if(image && (conn.getContentType()==null || !conn.getContentType().toLowerCase(Locale.US).startsWith("image/")))
                    throw new IOException("Source returned a page instead of an image");
                if(conn.getContentLengthLong()>limit) throw new IOException("Source image is too large");
                try(InputStream in=conn.getInputStream(); ByteArrayOutputStream out=new ByteArrayOutputStream()) {
                    byte[] buffer=new byte[8192]; int n,total=0;
                    while((n=in.read(buffer))!=-1) { total+=n; if(total>limit) throw new IOException("Source response is too large"); out.write(buffer,0,n); }
                    return out.toByteArray();
                }
            } finally { conn.disconnect(); }
        }
        throw new IOException("Source redirected too many times");
    }
    static void addControls(final Context c,final Panel p,final SettingsPanel.Host host,final Runnable changed) {
        p.addRow("Bing image of the day","Daily online",()->choose(c,BING,host,changed)).opens();
        p.addRow("NASA APOD","Daily online",()->choose(c,NASA,host,changed)).opens()
            .setHelp("Uses NASA's shared demo key. Rate limits, video days or a source outage keep the last good image. Offline artwork is used until the first photo arrives.");
        p.addRow("Online wallpaper status","",()->host.showPanel(statusPanel(c,host))).opens();
    }
    private static void choose(Context c,String id,SettingsPanel.Host host,Runnable changed) {
        Backgrounds.choose(c,id); refresh(c,false); host.rebuildGrid(); changed.run();
        host.showPanel(statusPanel(c,host));
    }
    private static View statusPanel(final Context c,final SettingsPanel.Host host) {
        Panel p=new Panel(c,"Online wallpaper", "Photos update daily while the launcher is open. Cached photos work offline.");
        final Panel.Row source=p.addRow("Source","",null), status=p.addRow("Status","",null), title=p.addRow("Image","",null);
        p.addRow("Refresh now","",()->refresh(c,true));
        p.addRow("Back","",()->host.closePanel());
        final String[] last={""};
        Runnable tick=new Runnable() { public void run() {
            if(!p.root.isAttachedToWindow()) return;
            String s=OutfitRemoteWallpaper.source(c);
            source.setValue(s==null?"Offline collection":s.equals("bing")?"Bing":"NASA APOD");
            status.setValue(s==null?"Choose an online source in Background":prefs(c).getString(s+"_status","Waiting"));
            title.setValue(s==null?"":prefs(c).getString(s+"_title",""));
            title.setHelp(s==null?"":prefs(c).getString(s+"_credit","")+"\nImage date: "+prefs(c).getString(s+"_date",""));
            String key=String.valueOf(s)+"|"+Backgrounds.keyOf(file(c));
            if(!key.equals(last[0])) { last[0]=key; host.rebuildGrid(); }
            p.root.postDelayed(this,2000);
        }};
        p.root.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View v) { v.post(tick); }
            public void onViewDetachedFromWindow(View v) { v.removeCallbacks(tick); }
        });
        return p.root;
    }
}
