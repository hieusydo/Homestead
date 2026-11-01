package com.homestead.launcher;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/** Original vector artwork: static, resolution independent, and entirely offline. */
final class ScenicWallpaper extends Drawable {
    private final int scene;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private static final int[][] PALETTES = {
        {0xFF101E30,0xFF526B80,0xFF293F52,0xFF172D3F},
        {0xFF0A232B,0xFF54786E,0xFF274C4C,0xFF143337},
        {0xFF241C31,0xFF916E70,0xFF69505B,0xFF3E334B},
        {0xFF0B2138,0xFF527F92,0xFF28576C,0xFF16394F},
        {0xFF11162E,0xFF303B70,0xFF455B92,0xFF202946},
        {0xFF0A252D,0xFF246263,0xFF4C8880,0xFF173E4A},
        {0xFF21162F,0xFF614868,0xFF97758D,0xFF342741},
        {0xFF211D29,0xFF68524D,0xFFAB8270,0xFF3E3443}
    };
    ScenicWallpaper(int scene) { this.scene = Math.max(0, Math.min(7, scene)); }
    private void color(int value) { paint.setShader(null); paint.setColor(value); }
    private void glow(Canvas c, float x, float y, float r, int color) {
        paint.setColor(Color.WHITE);
        paint.setShader(new RadialGradient(x,y,r,new int[]{color,color & 0x00FFFFFF},
                null,Shader.TileMode.CLAMP));
        c.drawCircle(x,y,r,paint); paint.setShader(null);
    }
    private void ridge(Canvas c, int color, float y, float height, int layer) {
        color(color); path.reset(); path.moveTo(0,600); path.lineTo(0,y);
        if (scene == 0) {
            path.lineTo(130,y-height*.4f); path.lineTo(280,y+30);
            path.lineTo(470,y-height); path.lineTo(560,y-height*.45f);
            path.lineTo(640,y-height*.7f); path.lineTo(810,y+25);
            path.lineTo(950,y-height*.6f); path.lineTo(1200,y);
        } else {
            path.cubicTo(220,y-height,330,y+height,540,y);
            path.cubicTo(780,y-height,930,y-height,1200,y+30);
        }
        path.lineTo(1200,600); path.close(); c.drawPath(path,paint);
        if (scene == 1) {
            for (int i=0;i<22;i++) {
                float x=i*59+layer*17, top=y-18-(i*37%65);
                path.reset(); path.moveTo(x,top); path.lineTo(x-19,y+45);
                path.lineTo(x+19,y+45); path.close(); c.drawPath(path,paint);
            }
        }
    }
    @Override public void draw(Canvas canvas) {
        Rect b=getBounds(); if (b.isEmpty()) return;
        int save=canvas.save(); canvas.translate(b.left,b.top);
        canvas.scale(b.width()/1200f,b.height()/600f);
        int[] p=PALETTES[scene];
        paint.setColor(Color.WHITE);
        paint.setShader(new LinearGradient(0,0,1000,600,p[0],p[1],Shader.TileMode.CLAMP));
        canvas.drawRect(0,0,1200,600,paint);
        if (scene<4) {
            glow(canvas,800,230,270,0x405FABB4);
            color(0x55D6D5C8); canvas.drawCircle(820,220,scene==2?28:17,paint);
            ridge(canvas,p[2],365,110,0); ridge(canvas,p[3],440,65,1);
            if (scene==3) {
                color(0x224D99A8); paint.setStrokeWidth(2);
                for(int i=0;i<7;i++) canvas.drawLine(660-i*23,390+i*13,930+i*18,390+i*13,paint);
            }
        } else {
            glow(canvas,800,285,370,(p[2]&0xFFFFFF)|0x99000000);
            glow(canvas,340,430,320,(p[1]&0xFFFFFF)|0xAA000000);
            for(int i=0;i<4;i++) {
                path.reset(); path.moveTo(1200,110+i*50);
                path.cubicTo(760,15+i*65,910,450+i*26,180,620);
                path.lineTo(1200,620); path.close();
                color((p[3]&0xFFFFFF)|((45+i*15)<<24)); canvas.drawPath(path,paint);
            }
        }
        // Keep the clock/toolbar and app dock quiet, with the artwork between them.
        paint.setColor(Color.WHITE);
        paint.setShader(new LinearGradient(0,0,0,600,
                new int[]{0xC209101C,0x3009101C,0x5509101C,0xED09101C},
                new float[]{0,.35f,.58f,1},Shader.TileMode.CLAMP));
        canvas.drawRect(0,0,1200,600,paint); paint.setShader(null);
        canvas.restoreToCount(save);
    }
    @Override public void setAlpha(int alpha) { }
    @Override public void setColorFilter(ColorFilter filter) { paint.setColorFilter(filter); }
    @Override public int getOpacity() { return PixelFormat.OPAQUE; }
}
