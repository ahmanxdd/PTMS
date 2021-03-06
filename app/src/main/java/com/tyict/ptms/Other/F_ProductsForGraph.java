package com.tyict.ptms.Other;


import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tyict.ptms.R;
import com.tyict.ptms.dataInfo.DatabaseView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by RAYMOND on 7/5/2015.
 */
public class F_ProductsForGraph extends Fragment {
    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    private View _this;
    private static TreeMap<String, String> averTime;
    private int[] avgTime = {95, 25, 45, 70, 215, 240, 140, 149};
    private int[] color = {0xffff0000, 0xffFF9800, 0xffffff00, 0xff8BC34A, 0xff64FFDA, 0xff2196F3, 0xff3F51B5, 0xff673AB7};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _this = inflater.inflate(R.layout.f_product_for_graph, container, false);

        averTime = getAverTime();
        getTotalMinutes(averTime.get(averTime.keySet().toArray(new String[averTime.size()])[1]));

        int totalMin = 0;
        for (String str : averTime.keySet().toArray(new String[averTime.size()]))
            totalMin += getTotalMinutes(averTime.get(str));


        String[] productID = averTime.keySet().toArray(new String[averTime.size()]);


        Graphic g = new Graphic().setTitle("Average Service Time for each Product");
        for (int i = 0; i < averTime.size(); i++) {
            g.addRow(productID[i], getTotalMinutes(averTime.get(productID[i])));
        }

        ScrollView sv = new ScrollView(getActivity());

        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(g.getGraphicAsLinear(getActivity()));
        ll.addView(new CircleGraphic((getActivity())).getView());
        sv.addView(ll);

        return sv;
    }

    private TreeMap<String, String> getAverTime() {
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("CN1008", "1:35");
        treeMap.put("CN2186", "0:45");
        treeMap.put("HP1002", "1:10");
        treeMap.put("HP2055", "2:20");
        treeMap.put("HP1008", "3:35");
        treeMap.put("CN1099", "0:25");
        treeMap.put("HP2002", "4:00");
        treeMap.put("HP3377", "2:29");
        return treeMap;
    }

    private int getTotalMinutes(String time) {
        try {
            String hours = "";
            for (char c : time.toCharArray()) {
                if (c != ':')
                    hours += c;
                else break;
            }
            String minutes = time.substring(time.indexOf(":") + 1, time.length());

            int h = Integer.parseInt(hours);
            int m = h * 60;
            m += Integer.parseInt(minutes);
            return m;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private class Graphic {
        private String _title = null;
        private int _maxLength = 0;
        private List<Pair> pair = new ArrayList<>();

        public Graphic() {

        }

        public Graphic setTitle(String label) {
            _title = label;
            return this;
        }

        public Graphic addRow(String label, int length) {
            pair.add(new Pair(label, length));
            if (_maxLength < length)
                _maxLength = length;
            return this;
        }

        public LinearLayout getGraphicAsLinear(Context context) {

            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);

            if (_title != null) {
                TextView title = new TextView(context);
                title.setText(_title);

                title.setTextSize(15);
                title.setSingleLine(true);
                title.setBackgroundColor(Color.GRAY);
                title.setTextColor(Color.WHITE);
                title.setGravity(Gravity.CENTER_HORIZONTAL);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                ll.addView(title);
            }

            for (int i = 0; i < pair.size(); i++) {
                ll.addView(genRow((String) pair.get(i).first, (int) (pair.get(i).second)));
            }
            return ll;
        }

        private int dpToInt(int dp) {
            Resources r = getResources();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        }

        private ViewGroup genRow(String label, double length) {
            Display d = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int width = d.getWidth();
            width = width - dpToInt(30) * 5;
            double unit = (double) width / _maxLength;

            LinearLayout ll = new LinearLayout(getActivity());
            ll.setOrientation(LinearLayout.HORIZONTAL);
            TextView tv = new TextView(getActivity());
            TextView tv_length = new TextView(getActivity());
            tv_length.setText(Double.toString(length) + "mins");
            tv_length.setTextSize(10);
            tv_length.setTextColor(Color.GRAY);
            tv.setText(label);

            tv.setLayoutParams(new LayoutParams(dpToInt((int) (tv.getTextSize()) * 2), LayoutParams.WRAP_CONTENT));
            DrawView rect = new DrawView(getActivity(), (int) Math.floor(length * unit), tv.getLineHeight(),color[i++]).setPaddingTopBottom(5).getView();

            ll.addView(tv);
            ll.addView(rect);
            ll.addView(tv_length);
            return ll;
        }

    }
    int i  = 0;
    private class DrawView extends View {
        private int _length;
        private Paint p = new Paint();
        private int _height = 50;
        private int _padding = 5;

        public DrawView(Context context, int length, int height,int color) {
            super(context);
            p.setColor(color);
            if (height > 15)
                _height = height;
            setMeasuredDimension(20 + length, _height);
            _length = length;
        }

        public DrawView setPaddingTopBottom(int padding) {
            if (padding < _height - 5)
                _padding = padding;
            return this;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            p.setStrokeWidth(1);
            canvas.drawRect(0, _padding, _length, _height - _padding, p);
        }

        public DrawView getView() {
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(_length + 5, _height);
            p.gravity = Gravity.CENTER_VERTICAL;
            this.setLayoutParams(p);
            return this;
        }

    }

    private class CircleGraphic extends View {
        int size;

        public View getView()
        {
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(size , size);

            p.gravity = Gravity.CENTER_HORIZONTAL;
            this.setLayoutParams(p);
            return this;
        }


        public CircleGraphic(Context context) {
            super(context);
            Display d = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            size = d.getWidth();
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);

            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);

            paint.setTextSize(30);

            XChartCalc xcalc = new XChartCalc();

            float cDegree = 0;
            int left = 100;
            int top =100;
            int right = size - 150;
            int bottom = size - 150;
            for (int i = 0; i < avgTime.length; i++) {
                float factor = (float) avgTime[i] * 360 / total();
                paint.setColor(color[i]);
                canvas.drawArc(new RectF(left, top, right, bottom), cDegree, factor, true, paint);
                paint.setColor(Color.BLACK);
                xcalc.CalcArcEndPointXY((right - left) / 2 + left - 30, (bottom - top) / 2 + top, (right - left) / 2 + (right - left) / 15, (cDegree + factor / 2));
                canvas.drawText(((int)(avgTime[i]/total()*1000)/(float)10)+"%", xcalc.getPosX(),xcalc.getPosY(), paint);
                cDegree += factor;
            }
        }

    }

    public float total() {
        int total = 0;
        for (int i = 0; i < avgTime.length; i++) {
            total += avgTime[i];
        }
        return total;
    }

    public class XChartCalc {
        private float posX = 0.0f;
        private float posY = 0.0f;

        public XChartCalc() {
        }

        public void CalcArcEndPointXY(float cirX, float cirY, float radius, float cirAngle) {
            float arcAngle = (float) (Math.PI * cirAngle / 180.0);
            if (cirAngle < 90) {
                posX = cirX + (float) (Math.cos(arcAngle)) * radius;
                posY = cirY + (float) (Math.sin(arcAngle)) * radius;
            } else if (cirAngle == 90) {
                posX = cirX;
                posY = cirY + radius;
            } else if (cirAngle > 90 && cirAngle < 180) {
                arcAngle = (float) (Math.PI * (180 - cirAngle) / 180.0);
                posX = cirX - (float) (Math.cos(arcAngle)) * radius;
                posY = cirY + (float) (Math.sin(arcAngle)) * radius;
            } else if (cirAngle == 180) {
                posX = cirX - radius;
                posY = cirY;
            } else if (cirAngle > 180 && cirAngle < 270) {
                arcAngle = (float) (Math.PI * (cirAngle - 180) / 180.0);
                posX = cirX - (float) (Math.cos(arcAngle)) * radius;
                posY = cirY - (float) (Math.sin(arcAngle)) * radius;
            } else if (cirAngle == 270) {
                posX = cirX;
                posY = cirY - radius;
            } else {
                arcAngle = (float) (Math.PI * (360 - cirAngle) / 180.0);
                posX = cirX + (float) (Math.cos(arcAngle)) * radius;
                posY = cirY - (float) (Math.sin(arcAngle)) * radius;
            }
        }

        public float getPosX() {
            return posX;
        }

        public float getPosY() {
            return posY;

        }
    }


}
