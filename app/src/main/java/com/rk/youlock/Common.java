package com.rk.youlock;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * Created by user1 on 31/7/18.
 */
public class Common {
    private static Context context;
    private static Common common;
    private final ImageView btnTag;
    private final ImageView btnCancel;
    private final int pad = 20;
    private WindowManager wm;
    private RelativeLayout relativeLayout;
    WindowManager.LayoutParams params;
    private boolean lock = false;

    protected void show_ui() {
        //add button to the blocker
        lock = false;
        try {
            wm.addView(relativeLayout, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void clear() {
        if (lock) {
//            wm.removeViewImmediate(relativeLayout);
//            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wm.updateViewLayout(relativeLayout, params);
            btnTag.setImageResource(R.drawable.unlock);
            btnTag.setBackgroundResource(R.drawable.bg_curve);
            lock = false;
//            btnCancel.setVisibility(View.VISIBLE);
        } else {
//            wm.addView(relativeLayout, params);
//            params.gravity = Gravity.TOP;
//            params.height = wm.getDefaultDisplay().getHeight() - 500;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            wm.updateViewLayout(relativeLayout, params);
            btnTag.setImageResource(R.drawable.lock);
            btnTag.setBackgroundResource(R.drawable.bg_curve);
            lock = true;
//            btnCancel.setVisibility(View.GONE);
        }
    }

    public Common() {
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);

//        params.height = wm.getDefaultDisplay().getHeight() - 500;
//        Log.e("height", String.valueOf(params.height));
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;

        params.gravity = Gravity.TOP | Gravity.LEFT;
        LayoutInflater inflater = LayoutInflater.from(context);
        View parent = inflater.inflate(R.layout.blocker, null);
        relativeLayout = (RelativeLayout) parent.findViewById(R.id.relative);
        btnTag = (ImageView) parent.findViewById(R.id.btn_lock);
        btnCancel = (ImageView) parent.findViewById(R.id.btn_cancel);
        btnTag.setImageResource(R.drawable.unlock);
        btnTag.setBackgroundResource(R.drawable.bg_curve);
        btnTag.setPadding(pad, pad, pad, pad);
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

    public static Common getInstance(Context mcontext) {
        context = mcontext;
        if (common == null)
            common = new Common();
        return common;
    }

    public void cancel() {
        wm.removeViewImmediate(relativeLayout);
        lock = false;
    }


    private void openAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

}
