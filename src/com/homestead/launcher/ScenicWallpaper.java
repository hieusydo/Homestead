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
        {0xFF211D29,0xFF68524D,0xFFAB8270,0xFF3E3443},
        {0xFF281931,0xFFCA7956,0xFFAA5144,0xFF582B43},
        {0xFF071E30,0xFF267873,0xFF20524F,0xFF0B3034},
        {0xFF121D3B,0xFF5379AD,0xFF829EB9,0xFF263F6A},
        {0xFF092B41,0xFF269A9A,0xFF256E6D,0xFF133E4B},
        {0xFF180E30,0xFF632E65,0xFFD99572,0xFF26172F},
        {0xFF151D3E,0xFF385495,0xFF8D70A7,0xFF246B79},
        {0xFF11132E,0xFF3C367E,0xFF7279BF,0xFF21234E},
        {0xFF2E162E,0xFFA05B78,0xFFC18D86,0xFF582D57}
    };
    ScenicWallpaper(int scene) { this.scene = Math.max(0, Math.min(PALETTES.length-1, scene)); }
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
    private void polygon(Canvas c,int ink,float... points) {
        color(ink); path.reset(); path.moveTo(points[0],points[1]);
        for(int i=2;i<points.length;i+=2) path.lineTo(points[i],points[i+1]);
        path.close(); c.drawPath(path,paint);
    }
    private void pine(Canvas c,float x,float y,float h,int ink) {
        polygon(c,ink,x,y-h,x-h*.35f,y,x+h*.35f,y);
        color(ink); c.drawRect(x-3,y,x+3,y+15,paint);
    }
    private void drawExtra(Canvas c,int[] p) {
        switch(scene) {
        case 8: // Broad terracotta mesas with a glowing sunset in the open middle band.
            glow(c,760,190,200,0xAADD9A58); color(0xFFD4A071); c.drawCircle(760,190,45,paint);
            polygon(c,p[2],360,410,440,190,540,190,580,285,680,285,725,410);
            polygon(c,p[3],770,440,850,260,875,175,1040,175,1080,340,1200,350,1200,600,0,600);
            color(0x448E5260); c.drawRect(875,220,1040,234,paint); break;
        case 9: // Pines frame a turquoise moon and its lake reflection.
            glow(c,820,190,190,0x8058CABB); color(0xFFB5D7CB); c.drawCircle(820,180,34,paint);
            color(0xFF173E4D); c.drawRect(0,315,1200,600,paint);
            for(int i=0;i<11;i++) { color(0x556BADB0); c.drawRect(805-i*7,325+i*10,835+i*7,327+i*10,paint); }
            for(int i=0;i<9;i++) pine(c,380+i*100,330+(i%3)*30,80+(i*47%110),i%2==0?p[3]:p[2]);
            break;
        case 10: // Angular snowcaps, rather than the rounded hills used by other scenes.
            polygon(c,p[2],300,440,580,140,785,440);
            polygon(c,0xFFA7BDCC,492,235,580,140,651,244,590,214,558,243,540,207);
            polygon(c,p[3],600,460,905,110,1200,460);
            polygon(c,0xFFC4D2DC,800,231,905,110,1015,240,939,204,912,229,878,180);
            glow(c,1040,210,180,0x405F9FD1); break;
        case 11: // Bright lagoon with flat island silhouettes and a distant sun.
            glow(c,780,210,260,0x805ED8BE); color(0xFFD7C998); c.drawCircle(910,180,27,paint);
            for(int i=0;i<9;i++) { color(0x304CD3CB); c.drawRect(400,270+i*19,1200,272+i*19,paint); }
            color(p[3]); c.drawOval(500,275,830,335,paint); c.drawOval(900,220,1150,265,paint);
            polygon(c,p[2],550,298,620,225,660,270,710,250,790,308);
            polygon(c,0xFFB8B68A,510,309,805,312,780,320,540,320); break;
        case 12: // A luminous eclipse, offset away from clock and app title.
            glow(c,810,215,185,0xFFE9A575); color(0xFFD6A47F); c.drawCircle(810,215,100,paint);
            color(0xFF17162D); c.drawCircle(796,202,97,paint);
            glow(c,940,360,170,0x506B4289); break;
        case 13: // Large translucent geometric planes, each with a distinct hue.
            polygon(c,0xAA657AC7,430,290,650,105,850,300);
            polygon(c,0xAA36A59F,650,105,1030,145,850,300);
            polygon(c,0xAAAD6EAC,850,300,1030,145,1200,370);
            polygon(c,0xAA3C577E,430,290,850,300,610,540);
            polygon(c,0xAA99799F,850,300,1200,370,930,540); break;
        case 14: // A sparse star field and a recognizable constellation.
            glow(c,850,230,300,0x70566DBB);
            float[] stars={520,220,660,160,760,245,900,175,1040,255};
            color(0x708799D2); paint.setStrokeWidth(2);
            for(int i=0;i<stars.length-2;i+=2) c.drawLine(stars[i],stars[i+1],stars[i+2],stars[i+3],paint);
            for(int i=0;i<stars.length;i+=2) { glow(c,stars[i],stars[i+1],18,0x90A8CDE5); color(0xFFE2EAF3); c.drawCircle(stars[i],stars[i+1],4,paint); }
            for(int i=0;i<35;i++) { color(0x808EA4D3); c.drawCircle(430+(i*173%750),120+(i*47%210),1.5f,paint); }
            break;
        case 15: // Layered rose/coral wave crests sweep across the open center.
            for(int i=0;i<5;i++) {
                color(new int[]{0xFFA88091,0xFF905978,0xFF71385F,0xFF502547,0xFF301D35}[i]);
                path.reset(); path.moveTo(300,600); path.lineTo(300,360+i*27);
                path.cubicTo(640,390+i*20,730,80+i*30,1200,185+i*28);
                path.lineTo(1200,600); path.close(); c.drawPath(path,paint);
            } break;
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
        if (scene>=8) {
            drawExtra(canvas,p);
        } else if (scene<4) {
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
            for(int i=0;i<(scene==6?2:4);i++) {
                path.reset(); path.moveTo(1200,110+i*50);
                path.cubicTo(760,15+i*65,scene==5?380:910,450+i*26,180,620);
                path.lineTo(1200,620); path.close();
                color((p[3]&0xFFFFFF)|((45+i*15)<<24)); canvas.drawPath(path,paint);
            }
        }
        if(scene==7) {
            paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(3);
            color(0x809FBAAE);
            canvas.drawOval(550,145,1040,310,paint); canvas.drawOval(630,110,960,345,paint);
            paint.setStyle(Paint.Style.FILL);
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
