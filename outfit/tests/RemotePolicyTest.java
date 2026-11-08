package com.homestead.launcher;
import java.io.IOException;
class RemotePolicyTest {
    interface Attempt { void run() throws IOException; }
    static void reject(Attempt action) throws IOException {
        try { action.run(); } catch(IOException expected) { return; }
        throw new AssertionError("Unsafe or unavailable image accepted");
    }
    public static void main(String[] args) throws Exception {
        OutfitRemotePolicy.allowed("https://www.bing.com/th?id=image", "bing");
        OutfitRemotePolicy.allowed("https://apod.nasa.gov/apod/image/photo.jpg", "nasa");
        reject(()->OutfitRemotePolicy.allowed("http://www.bing.com/image", "bing"));
        reject(()->OutfitRemotePolicy.allowed("https://bing.com.example.com/image", "bing"));
        reject(()->OutfitRemotePolicy.allowed("https://user@bing.com/image", "bing"));
        reject(()->OutfitRemotePolicy.allowed("https://bing.com:8443/image", "bing"));
        reject(()->OutfitRemotePolicy.nasaImage("video", "", "https://apod.nasa.gov/video"));
        reject(()->OutfitRemotePolicy.nasaImage("image", "https://science.nasa.gov/nasa-logo@2x.png", ""));
        String photo="https://apod.nasa.gov/apod/image/photo.jpg";
        if(!OutfitRemotePolicy.nasaImage("image", "", photo).equals(photo)) throw new AssertionError("image fallback");
        if(!OutfitRemotePolicy.nasaImage("image", photo, "https://science.nasa.gov/article").equals(photo)) throw new AssertionError("HD image preferred");
        System.out.println("PASS: online image host, HTTPS, video, placeholder and fallback policy");
    }
}
