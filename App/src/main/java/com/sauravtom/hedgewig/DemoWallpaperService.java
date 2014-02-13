package com.sauravtom.hedgewig;

/**
 * Created by saurav on 21/12/13.
 */

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;


public class DemoWallpaperService extends WallpaperService {

    public int TIME = 50;
    public int i = 0;

    @Override
    public Engine onCreateEngine() {
        return new DemoWallpaperEngine();
    }
    private class DemoWallpaperEngine extends Engine {

        private boolean mVisible = false;
        public final Paint mPaint = new Paint();
        public int speed = 100;

        private final Handler mHandler = new Handler();
        private final Runnable mUpdateDisplay = new Runnable() {
            @Override
            public void run() {
                draw();
            }};

        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                int color = Color.parseColor(prefs.getString("list_preference_color", "GREEN"));
                mPaint.setColor(color);
                speed = Integer.parseInt(prefs.getString("list_preference_speed","100"));

            }
        };

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            try {
                c = holder.lockCanvas();

                if (c != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPurgeable = true;
                    options.inInputShareable = true;
                    options.inSampleSize = 8;

                    //Bitmap s000 = BitmapFactory.decodeResource(getResources(), R.drawable.owl);
                    Bitmap s000 = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("zoo" + i, "drawable", getPackageName()));
                    int X = c.getWidth()/2 - s000.getWidth()/2 ;
                    int Y = c.getHeight()/2 - s000.getHeight()/2 ;
                    //Bitmap.createScaledBitmap(s000, s000.getWidth(), s000.getHeight(), true);

                    c.drawBitmap(s000, X, Y, null);
                    s000.recycle();
                    s000 = null;
                    if(++i > 11) i = 0;
                }
            } finally {
                if (c != null)
                    holder.unlockCanvasAndPost(c);
            }
            mHandler.removeCallbacks(mUpdateDisplay);
            if (mVisible) {
                mHandler.postDelayed(mUpdateDisplay, TIME);
            }
        }


        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                draw();
            } else {
                mHandler.removeCallbacks(mUpdateDisplay);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            draw();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mUpdateDisplay);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mVisible = false;
            mHandler.removeCallbacks(mUpdateDisplay);
        }
    }
}