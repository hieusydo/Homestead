package com.cleantv.outfit;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;
public final class OutfitTextView extends TextView {
 private static Typeface outfit;
 public OutfitTextView(Context c) { super(c); setTypeface(null); setFontVariationSettings("'wght' 400"); }
 private Typeface face() {
  if (outfit == null) try { outfit=getResources().getFont(R.font.outfit); } catch(Exception e) { outfit=Typeface.create("sans-serif",0); }
  return outfit;
 }
 @Override public void setTypeface(Typeface ignored) { super.setTypeface(face()); super.setFontVariationSettings("'wght' 400"); }
 @Override public void setTypeface(Typeface ignored,int style) { super.setTypeface(Typeface.create(face(),style)); super.setFontVariationSettings(style==Typeface.BOLD?"'wght' 600":"'wght' 400"); }
}
