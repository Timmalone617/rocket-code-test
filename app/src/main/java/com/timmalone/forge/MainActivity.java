package com.timmalone.forge;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.os.StatFs;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.File;
import java.util.Arrays;

public final class MainActivity extends Activity {
    private static final int BG = Color.rgb(2,8,18), CARD = Color.rgb(7,20,34), BLUE = Color.rgb(27,139,255), TEXT = Color.rgb(245,249,255), MUTED = Color.rgb(145,164,190), GREEN = Color.rgb(42,216,112), WARN = Color.rgb(255,183,45);
    private LinearLayout root;
    private TextView status;
    public void onCreate(Bundle b) { super.onCreate(b); getWindow().setStatusBarColor(BG); getWindow().setNavigationBarColor(Color.BLACK); showHome(); }
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private TextView tv(String s, float z, int c, int style) { TextView t = new TextView(this); t.setText(s); t.setTextSize(z); t.setTextColor(c); t.setTypeface(Typeface.DEFAULT, style); t.setGravity(Gravity.CENTER_VERTICAL); return t; }
    private GradientDrawable bg(int color, int stroke) { GradientDrawable g = new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp(16)); if (stroke != 0) g.setStroke(dp(1), stroke); return g; }
    private void showHome() {
        ScrollView sc = new ScrollView(this); sc.setFillViewport(true); sc.setBackgroundColor(BG);
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(20), dp(18), dp(20), dp(28)); sc.addView(root);
        TextView title = tv("NOVA Forge", 28, TEXT, Typeface.BOLD); title.setGravity(Gravity.CENTER); root.addView(title, new LinearLayout.LayoutParams(-1, dp(56)));
        addCard("Project Intake", "Open .caproj packages, verify archive structure, reject unsafe paths, and preserve SHA-256 lineage.", BLUE);
        addCard("Scan First Toolchain", "Reuse accessible JDK 17, Android SDK 35, ARM64 aapt2, Gradle, or verified archives before download.", GREEN);
        addCard("Fail Closed READY", "Forge must not claim phone build readiness until Java, SDK, aapt2, and Gradle probes all pass.", WARN);
        Button scan = new Button(this); scan.setText("Scan Toolchain"); scan.setAllCaps(false); scan.setTextColor(TEXT); scan.setBackground(bg(Color.rgb(11,83,170), BLUE)); scan.setOnClickListener(v -> scan()); root.addView(scan, new LinearLayout.LayoutParams(-1, dp(58)));
        status = tv("Status: GitHub Actions bootstrap build for Forge 0.9.5. Device testing remains UNKNOWN until run on the Samsung phone.", 13, MUTED, Typeface.NORMAL); status.setPadding(dp(14), dp(14), dp(14), dp(14)); status.setBackground(bg(CARD, BLUE)); root.addView(status); setContentView(sc);
    }
    private void addCard(String h, String b, int accent) { LinearLayout c = new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(dp(16), dp(14), dp(16), dp(14)); c.setBackground(bg(CARD, accent)); c.addView(tv(h,17,TEXT,Typeface.BOLD)); c.addView(tv(b,13,MUTED,Typeface.NORMAL)); LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,dp(8),0,dp(8)); root.addView(c,p); }
    private void scan() {
        File files = getFilesDir();
        File cache = getCacheDir();
        boolean arm64 = Arrays.asList(Build.SUPPORTED_ABIS).contains("arm64-v8a");
        boolean java = find(files, "java", 5) || find(cache, "java", 5);
        boolean sdk = find(files, "android.jar", 7) || find(cache, "android.jar", 7);
        boolean aapt2 = find(files, "aapt2", 7) || find(cache, "aapt2", 7);
        boolean gradle = find(files, "gradle", 7) || find(cache, "gradle", 7);
        long free = new StatFs(files.getAbsolutePath()).getAvailableBytes() / 1048576L;
        status.setText("SCAN COMPLETE\nABI arm64-v8a: "+state(arm64)+"\nJDK 17 candidate: "+state(java)+"\nAndroid SDK 35 candidate: "+state(sdk)+"\naapt2 candidate: "+state(aapt2)+"\nGradle candidate: "+state(gradle)+"\nPrivate free MB: "+free+"\nUNKNOWN means not found in app-accessible roots, not proven absent from the phone.");
    }
    private static String state(boolean ok) { return ok ? "FOUND" : "UNKNOWN"; }
    private static boolean find(File f, String name, int depth) { if (f == null || depth < 0 || !f.exists()) return false; if (f.isFile()) return f.getName().equals(name); File[] kids = f.listFiles(); if (kids == null) return false; for (File k : kids) if (find(k, name, depth - 1)) return true; return false; }
}
