package com.homestead.launcher;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

/** Outfit-only presentation. Package management and Home handling remain upstream. */
final class OutfitLayout {
    interface Callbacks { View tile(AppModel.Item item); void pick(AppModel.Item item); }
    private final Context ctx;
    private final SettingsPanel.Host host;
    private final Callbacks callbacks;
    private final LinearLayout column;
    private final TextView clock;
    private final Map<String,View> cellsByKey;
    private final List<View> topButtons=new ArrayList<View>();
    private TextView selectedAppName, greeting, date;
    OutfitLayout(Context c, SettingsPanel.Host host, LinearLayout column, TextView clock,
            Map<String,View> cells, Callbacks callbacks) {
        this.ctx=c; this.host=host; this.column=column; this.clock=clock;
        this.cellsByKey=cells; this.callbacks=callbacks;
    }
    private int dp(int v) { return AppModel.dp(ctx,v); }
    void build() {
        topButtons.clear();
        column.removeAllViews(); cellsByKey.clear();
        List<AppModel.Item> all=AppModel.visibleItems(ctx);
        List<AppModel.Item> favorites=new ArrayList<AppModel.Item>();
        List<AppModel.Item> controls=new ArrayList<AppModel.Item>();
        List<AppModel.Item> apps=new ArrayList<AppModel.Item>();
        String[] order={"com.amazon.firetv.youtube","com.netflix.ninja","com.hbo.hbonow","com.abclocal.kgo.tv.amazon","com.nbcuni.nbcots.nbcbayarea.firetv"};
        for(String pkg:order) for(AppModel.Item it:all) if(pkg.equals(it.pkg)) {favorites.add(it);break;}
        for(AppModel.Item it:all) {
            if(it.type==AppModel.TYPE_INPUT || it.type==AppModel.TYPE_LIVETV || it.type==AppModel.TYPE_AMAZON_HOME) controls.add(it);
            else if(!favorites.contains(it) && (it.tvApp || Prefs.showNonTv(ctx))
              && !"com.homestead.launcher".equals(it.pkg)) apps.add(it);
        }
        java.util.Collections.sort(controls,new java.util.Comparator<AppModel.Item>() {
            public int compare(AppModel.Item a,AppModel.Item b) {
                int x=a.type==AppModel.TYPE_SETTINGS?-2:a.type==AppModel.TYPE_HEALTH?99:0;
                int y=b.type==AppModel.TYPE_SETTINGS?-2:b.type==AppModel.TYPE_HEALTH?99:0;
                return Integer.compare(x,y);
            }
        });
        FrameLayout header = new FrameLayout(ctx);
        header.setClipChildren(false);
        greeting = new OutfitTextView(ctx);
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        greeting.setText(hour < 12 ? "GOOD MORNING" : hour < 18 ? "GOOD AFTERNOON" : "GOOD EVENING");
        greeting.setTextSize(11); greeting.setLetterSpacing(.20f); greeting.setTextColor(0xFFB9D6E1);
        header.addView(greeting, new FrameLayout.LayoutParams(-2,-2));
        if (clock.getParent() != null) ((ViewGroup)clock.getParent()).removeView(clock);
        clock.setTextSize(40);
        if(android.os.Build.VERSION.SDK_INT>=26) clock.setTypeface(ctx.getResources().getFont(R.font.outfit));
        FrameLayout.LayoutParams timeLp = new FrameLayout.LayoutParams(dp(205),-2);
        timeLp.topMargin=dp(18); header.addView(clock,timeLp);
        date = new OutfitTextView(ctx);
        date.setText(new SimpleDateFormat("EEE, MMM d",Locale.getDefault()).format(new Date()).toUpperCase(Locale.getDefault()));
        date.setTextSize(12); date.setLetterSpacing(.10f); date.setTextColor(0xFFB9D6E1);
        FrameLayout.LayoutParams dateLp=new FrameLayout.LayoutParams(-2,-2);
        dateLp.topMargin=dp(69); header.addView(date,dateLp);
        LinearLayout toolbar=new LinearLayout(ctx);
        toolbar.setOrientation(LinearLayout.HORIZONTAL); toolbar.setClipChildren(false);
        toolbar.addView(topButton("Inputs",new Runnable() { public void run() { showInputs(controls); } }));
        toolbar.addView(topButton("Wi-Fi",new Runnable() { public void run() {
            try { ctx.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); }
            catch(Exception e) { Toast.makeText(ctx,"Open network settings from the TV settings menu",Toast.LENGTH_SHORT).show(); }
        } }));
        toolbar.addView(topButton("Settings",new Runnable() { public void run() { host.showPanel(SettingsPanel.build(ctx,host)); } }));
        FrameLayout.LayoutParams toolLp=new FrameLayout.LayoutParams(-2,-2,Gravity.TOP|Gravity.RIGHT);
        toolLp.topMargin=dp(20); header.addView(toolbar,toolLp);
        column.addView(header,new LinearLayout.LayoutParams(-1,dp(155)));
        selectedAppName=new OutfitTextView(ctx);
        selectedAppName.setText("YouTube"); selectedAppName.setTextSize(34); selectedAppName.setTextColor(Color.WHITE);
        selectedAppName.setSingleLine(true); selectedAppName.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams nameLp=new LinearLayout.LayoutParams(-1,dp(55));
        nameLp.leftMargin=dp(8); nameLp.bottomMargin=dp(12); column.addView(selectedAppName,nameLp);
        if(!favorites.isEmpty()) addTileGrid(favorites);
        if(!apps.isEmpty()) {
            View gap=new View(ctx); column.addView(gap,new LinearLayout.LayoutParams(1,dp(26)));
            addTileGrid(apps);
        }
        if (!topButtons.isEmpty() && !favorites.isEmpty()) {
            View first=cellsByKey.get(favorites.get(0).key());
            for(AppModel.Item it:favorites) {
                View tile=cellsByKey.get(it.key());
                if(tile!=null) tile.setNextFocusUpId(topButtons.get(0).getId());
            }
            if(first!=null) for(View button:topButtons) button.setNextFocusDownId(first.getId());
        }
    }

    private View topButton(final String title, final Runnable action) {
        final TextView button=new OutfitTextView(ctx);
        button.setId(View.generateViewId()); topButtons.add(button);
        button.setText(title); button.setTextSize(16); button.setGravity(Gravity.CENTER);
        button.setTextColor(Color.WHITE); button.setFocusable(true); button.setFocusableInTouchMode(true);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(104),dp(40)); lp.leftMargin=dp(10); button.setLayoutParams(lp);
        button.setBackground(pill(false));
        button.setOnFocusChangeListener(new View.OnFocusChangeListener() { public void onFocusChange(View v,boolean has) {
            button.setBackground(pill(has)); button.setTextColor(has?0xFF092332:Color.WHITE);
            button.animate().scaleX(has?1.06f:1f).scaleY(has?1.06f:1f).setDuration(160).start();
        } });
        button.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { action.run(); } });
        return button;
    }

    private Drawable pill(boolean focused) {
        GradientDrawable bg=new GradientDrawable(); bg.setCornerRadius(dp(24));
        bg.setColor(focused?0xFFF0FCFF:0x3359A9B9); bg.setStroke(dp(1),focused?0xFFFFFFFF:0x557CD6EB); return bg;
    }

    private void showInputs(List<AppModel.Item> controls) {
        final Panel panel=new Panel(ctx,"Inputs","Choose a source");
        for(final AppModel.Item it:controls) {
            if(it.type==AppModel.TYPE_SETTINGS || it.type==AppModel.TYPE_HEALTH) continue;
            panel.addRow(it.label,"",new Runnable() { public void run() {
                host.closePanel(); callbacks.pick(it);
            } });
        }
        host.showPanel(panel.root);
    }


    void tick() {
        if(greeting==null) return;
        int hour=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        greeting.setText(hour<12?"GOOD MORNING":hour<18?"GOOD AFTERNOON":"GOOD EVENING");
        date.setText(new SimpleDateFormat("EEE, MMM d",Locale.getDefault()).format(new Date()).toUpperCase(Locale.getDefault()));
    }
    private void addTileGrid(List<AppModel.Item> items) {
        int cols=AppModel.columns(ctx), w=AppModel.cellW(ctx)-dp(24)/cols;
        LinearLayout row=null;
        for(int i=0;i<items.size();i++) {
            if(i%cols==0) {
                row=new LinearLayout(ctx); row.setClipChildren(false); row.setClipToPadding(false);
                row.setPadding(dp(12),dp(12),dp(12),dp(12));
                GradientDrawable dock=new GradientDrawable(); dock.setCornerRadius(dp(22));
                dock.setColor(0xC910141C); dock.setStroke(dp(1),0x334A6B80); row.setBackground(dock);
                LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);
                if(i>0) rp.topMargin=dp(16); column.addView(row,rp);
            }
            final AppModel.Item item=items.get(i);
            View cell=callbacks.tile(item); cell.setId(View.generateViewId());
            final View.OnFocusChangeListener original=cell.getOnFocusChangeListener();
            cell.setOnFocusChangeListener((v,has)-> {
                if(original!=null) original.onFocusChange(v,has);
                if(has) selectedAppName.setText(item.label);
            });
            cell.setOnHoverListener((v,event)-> {
                if(event.getAction()==MotionEvent.ACTION_HOVER_ENTER) v.requestFocus(); return false;
            });
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(w,AppModel.cellH(ctx));
            if(i%cols!=0) lp.leftMargin=dp(16); row.addView(cell,lp);
        }
    }
    static void style(Context c, FrameLayout cell, boolean focused) {
        View label=cell.findViewWithTag("name"); if(label!=null) label.setVisibility(View.GONE);
        if(focused) {
            GradientDrawable ring=new GradientDrawable(); ring.setCornerRadius(AppModel.dp(c,8));
            ring.setStroke(AppModel.dp(c,4),0xFF6DE7FF); ring.setColor(Color.TRANSPARENT);
            cell.setForeground(ring);
        }
    }
}
