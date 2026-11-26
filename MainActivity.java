package com.pclogo.noroot;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER);
        page.setPadding(dp(24), dp(24), dp(24), dp(24));
        page.setBackgroundColor(0xFFF4F5FA);

        TextView logo = new TextView(this);
        logo.setText("🖥");
        logo.setTextSize(58);
        logo.setGravity(Gravity.CENTER);
        page.addView(logo, new LinearLayout.LayoutParams(-1, dp(90)));

        TextView title = new TextView(this);
        title.setText("PC LOGO — NO ROOT");
        title.setTextSize(24);
        title.setTextColor(0xFF202033);
        title.setGravity(Gravity.CENTER);
        page.addView(title);

        TextView info = new TextView(this);
        info.setText("Floating PC-style logo and visual gaming panel.\nNo root access required.\n\nNote: This is an on-screen visual overlay only. It does not change Free Fire player identity or inject game controls.");
        info.setTextSize(15);
        info.setTextColor(0xFF555566);
        info.setGravity(Gravity.CENTER);
        info.setPadding(0, dp(18), 0, dp(22));
        page.addView(info);

        Button start = new Button(this);
        start.setText("ENABLE FLOATING LOGO");
        page.addView(start, new LinearLayout.LayoutParams(-1, dp(54)));

        Button stop = new Button(this);
        stop.setText("STOP OVERLAY");
        page.addView(stop, new LinearLayout.LayoutParams(-1, dp(54)));

        TextView footer = new TextView(this);
        footer.setText("Grant “Display over other apps” permission when Android asks.");
        footer.setTextSize(12);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(18), 0, 0);
        page.addView(footer);

        setContentView(page);

        start.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(i);
                Toast.makeText(this, "Allow display over other apps, then return and tap ENABLE again.", Toast.LENGTH_LONG).show();
                return;
            }
            startService(new Intent(this, OverlayService.class));
            Toast.makeText(this, "Floating PC logo enabled.", Toast.LENGTH_SHORT).show();
        });
        stop.setOnClickListener(v -> {
            stopService(new Intent(this, OverlayService.class));
            Toast.makeText(this, "Overlay stopped.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override protected void onResume() {
        super.onResume();
    }
}
