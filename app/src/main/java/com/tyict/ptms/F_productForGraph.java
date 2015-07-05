package com.tyict.ptms;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by RAYMOND on 7/5/2015.
 */
public class F_productForGraph extends Fragment {
    @Nullable
    private View _this;
    private static TreeMap<String, String> averTime;

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
        for (int i = 1; i < averTime.size(); i++) {
            g.addRow(productID[i], getTotalMinutes(averTime.get(productID[i])));
        }

        ScrollView sv = new ScrollView(getActivity());
        sv.addView(g.getGraphicAsLinear(getActivity()));
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

        public View getGraphicAsLinear(Context context) {

            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);

            if (_title != null) {
                TextView title = new TextView(context);
                title.setText(_title);
                title.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                title.setTextSize(dpToInt(8));
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
            tv_length.setTextSize(dpToInt(5));
            tv_length.setTextColor(Color.GRAY);
            tv.setText(label);

            tv.setLayoutParams(new LayoutParams(dpToInt((int) (tv.getTextSize()) * 3), LayoutParams.WRAP_CONTENT));
            DrawView rect = new DrawView(getActivity(), (int) Math.floor(length * unit), tv.getLineHeight()).setPaddingTopBottom(5).getView();

            ll.addView(tv);
            ll.addView(rect);
            ll.addView(tv_length);
            return ll;
        }

    }
    private class DrawView extends View {
        private int _length;
        private Paint p = new Paint();
        private int _height = 50;
        private int _padding = 5;

        public DrawView(Context context, int length, int height) {
            super(context);
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
            p.setColor(Color.RED);
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


}
