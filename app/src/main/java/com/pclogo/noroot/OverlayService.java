package com.pclogo.noroot;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OverlayService extends Service {
    private WindowManager wm;
    private View bubble;
    private View panel;
    private WindowManager.LayoutParams bubbleParams, panelParams;
    private float downX, downY;
    private int startX, startY;

    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public void onCreate() {
        super.onCreate();
        wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        int type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        bubbleParams = new WindowManager.LayoutParams(dp(58), dp(58), type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        bubbleParams.gravity = Gravity.TOP | Gravity.START;
        bubbleParams.x = dp(18); bubbleParams.y = dp(180);

        TextView b = new TextView(this);
        b.setText("🖥");
        b.setTextSize(27);
        b.setGravity(Gravity.CENTER);
        b.setTextColor(Color.WHITE);
        b.setBackground(round(0xFF1769C2, dp(29)));
        bubble = b;
        bubble.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                downX = e.getRawX(); downY = e.getRawY();
                startX = bubbleParams.x; startY = bubbleParams.y;
                return true;
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                float dx = e.getRawX() - downX, dy = e.getRawY() - downY;
                if (Math.abs(dx) < dp(8) && Math.abs(dy) < dp(8)) togglePanel();
                return true;
            } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                bubbleParams.x = startX + (int)(e.getRawX() - downX);
                bubbleParams.y = startY + (int)(e.getRawY() - downY);
                try { wm.updateViewLayout(bubble, bubbleParams); } catch (Exception ignored) {}
                return true;
            }
            return true;
        });
        wm.addView(bubble, bubbleParams);
    }

    private android.graphics.drawable.GradientDrawable round(int color, int radius) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(color); d.setCornerRadius(radius); return d;
    }

    private TextView label(String text, int size, int color) {
        TextView t = new TextView(this);
        t.setText(text); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER); t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(dp(8), dp(8), dp(8), dp(8));
        return t;
    }

    private void togglePanel() {
        if (panel != null) {
            try { wm.removeView(panel); } catch (Exception ignored) {}
            panel = null; return;
        }
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(10), dp(8), dp(10), dp(8));
        box.setBackground(round(0xEE151B2B, dp(16)));

        TextView head = label("PC MODE  •  VISUAL HUD", 15, Color.WHITE);
        box.addView(head);
        box.addView(label("W   A   S   D       MOUSE", 17, 0xFF7EC8FF));
        box.addView(label("PC PLAYER STYLE", 14, 0xFFFFFFFF));
        TextView close = label("CLOSE", 13, 0xFFFFD36A);
        box.addView(close);
        close.setOnClickListener(v -> togglePanel());

        panel = box;
        panelParams = new WindowManager.LayoutParams(dp(245), WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        panelParams.gravity = Gravity.TOP | Gravity.START;
        panelParams.x = bubbleParams.x + dp(64);
        panelParams.y = bubbleParams.y;
        wm.addView(panel, panelParams);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }

    @Override public void onDestroy() {
        if (panel != null) { try { wm.removeView(panel); } catch (Exception ignored) {} panel = null; }
        if (bubble != null) { try { wm.removeView(bubble); } catch (Exception ignored) {} bubble = null; }
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
