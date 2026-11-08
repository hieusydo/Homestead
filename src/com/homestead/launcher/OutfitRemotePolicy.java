package com.homestead.launcher;
import java.net.URL;
import java.io.IOException;

/** Network limits and metadata validation, kept independent of Android for tests. */
final class OutfitRemotePolicy {
    static final int MAX_IMAGE=16*1024*1024, MAX_JSON=256*1024;
    static URL allowed(String value,String source) throws IOException {
        URL u=new URL(value);
        String h=u.getHost().toLowerCase(java.util.Locale.US);
        boolean host=source.equals("bing") ? h.equals("bing.com") || h.endsWith(".bing.com")
                : h.equals("nasa.gov") || h.endsWith(".nasa.gov");
        if(!u.getProtocol().equals("https") || !host || u.getUserInfo()!=null || (u.getPort()!=-1 && u.getPort()!=443))
            throw new IOException("Unsupported image address");
        return u;
    }
    static String nasaImage(String type,String hd,String url) throws IOException {
        if(!"image".equals(type)) throw new IOException("APOD is not a still image today");
        String image=hd.isEmpty()?url:hd;
        if(image.toLowerCase(java.util.Locale.US).contains("nasa-logo"))
            throw new IOException("NASA returned a placeholder image");
        allowed(image,"nasa"); return image;
    }
}
