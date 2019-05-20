package com.rk.youlock;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private int OVERLAY_PERMISSION_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!canDrawOverlays(this)) {
            Toast.makeText(this, "Kindly switch on the permission button", Toast.LENGTH_SHORT).show();
            addOverlay();
        } else {
            Common.getInstance(this).show_ui();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.youtube.com"));
            intent.setPackage("com.google.android.youtube");
            startActivity(intent);
            finish();
        }
    }



    public boolean addOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_CODE);

                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_CODE) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    check_overlay();
                }
            }, 1500);

        }
    }

    private void check_overlay() {
        if (!canDrawOverlays(this)) {
            Toast.makeText(this, "Kindly switch on the permission button", Toast.LENGTH_SHORT).show();
            addOverlay();
        }
    }

    static boolean canDrawOverlays(Context context) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Settings.canDrawOverlays(context))
            return true;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            //USING APP OPS MANAGER
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (manager != null) {
                try {
                    int result = manager.checkOp(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Binder.getCallingUid(), context.getPackageName());
                    return result == AppOpsManager.MODE_ALLOWED;
                } catch (Exception ignore) {
                }
            }
        }

        try {
            //IF This Fails, we definitely can't do it
            WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (mgr == null) return false; //getSystemService might return null
            View viewToAdd = new View(context);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(0, 0, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
            viewToAdd.setLayoutParams(params);
            mgr.addView(viewToAdd, params);
            mgr.removeView(viewToAdd);
            return true;
        } catch (Exception ignore) {
        }
        return false;

    }
}
