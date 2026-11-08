package com.homestead.launcher;
import android.content.Context;
import android.view.View;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

/** Keep greeting/date below the upstream clock instead of competing with its pills. */
final class OutfitHeader {
    static int scrollTop(Context c,int upstream) {
        return Prefs.greeting(c) || Prefs.dateFormat(c)!=Prefs.DATE_OFF ? 114 : upstream;
    }
    static void update(Context c,LinearLayout head,TextView greet,TextView date) {
        boolean greeting=Prefs.greeting(c), showDate=Prefs.dateFormat(c)!=Prefs.DATE_OFF;
        int hour=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        greet.setText(hour>=5 && hour<12?R.string.home_greet_morning:hour<18 && hour>=12?R.string.home_greet_afternoon:R.string.home_greet_evening);
        greet.setTextSize(15); greet.setVisibility(greeting?View.VISIBLE:View.GONE);
        date.setText(Prefs.dateFormat(c)==Prefs.DATE_SHORT
                ?android.text.format.DateFormat.getDateFormat(c).format(new Date())
                :new SimpleDateFormat("EEE, MMM d, yyyy",Locale.getDefault()).format(new Date()));
        date.setTextSize(13); date.setVisibility(showDate?View.VISIBLE:View.GONE);
        head.setVisibility(greeting || showDate?View.VISIBLE:View.GONE);
        FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams)head.getLayoutParams();
        int top=AppModel.dp(c,61); if(lp.topMargin!=top) { lp.topMargin=top; head.setLayoutParams(lp); }
    }
}
