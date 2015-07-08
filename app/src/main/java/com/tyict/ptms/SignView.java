package com.tyict.ptms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignView extends View {
    Paint paint = new Paint();
    List<Path> paths = new ArrayList<>();
    Path currentPath;

    private void initPaint() {
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth((float) 3.0);
        paint.setStyle(Paint.Style.STROKE);
    }

    public SignView(Context context) {
        super(context);
        initPaint();
    }

    public SignView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public SignView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawPath(path, paint);
        for (Path p : paths) {
            canvas.drawPath(p, paint);
        }
        if (currentPath != null)
            canvas.drawPath(currentPath, paint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            currentPath = new Path();
            currentPath.moveTo(event.getX(), event.getY());

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            currentPath.lineTo(event.getX(), event.getY());

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            paths.add(currentPath);
            currentPath = null;
        }

        getParent().requestDisallowInterceptTouchEvent(true);
        invalidate();
        return true;
    }

    public Bitmap getBitmap() {
        Bitmap bm = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        draw(canvas);
        return bm;
    }

    public boolean saveToStorage(String filename) {
        Bitmap bm = getBitmap();
        FileOutputStream out = null;
        try {
            out = getContext().openFileOutput(filename, 0);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public static Bitmap loadFromStorage(Context context, String filename) {
        FileInputStream in = null;
        Bitmap bm = null;
        try {
            in = context.openFileInput(filename);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bm = BitmapFactory.decodeStream(in, null, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;
    }

    public static void showOnImageView(Context context, String filename, ImageView imgView) {
        Bitmap bm = loadFromStorage(context, filename);
        if (bm == null)
            return;
        imgView.setImageBitmap(bm);
    }

    public boolean isSigned() {
        return !paths.isEmpty();
    }
}
