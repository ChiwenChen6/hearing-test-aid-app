package com.changwon.chiwen.frequency;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainResult extends AppCompatActivity {


    public static int[] RealLeftValue = new int[100];
    public static int[] RealRightValue = new int[100];
    float PTA_R;
    float PTA_L;
    float MIb;
    float MIw;
    float HH;
    float textRight;
    float textLeft;
    int l_sextant;
    int r_sextant;
    String l_sextant_text;
    String r_sextant_text;

    public static int listnumber;   // 當前清單編號
    String date_month;    // 當前月份
    String date_day;    // 當前日期
    String date_hour;   // 當前時間
    String date_minute;     // 當前分鐘

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_result);

        RealLeftValue = MainTest.RealLeftValue;
        RealRightValue = MainTest.RealRightValue;


        l_sextant = Function.sextantfunction(RealLeftValue[2], RealLeftValue[3], RealLeftValue[3], RealLeftValue[4], RealLeftValue[4], RealLeftValue[6]);
        r_sextant = Function.sextantfunction(RealRightValue[2], RealRightValue[3], RealRightValue[3], RealRightValue[4], RealRightValue[4], RealRightValue[6]);

        l_sextant_text = Function.Resultdgreefunction(l_sextant);
        r_sextant_text = Function.Resultdgreefunction(r_sextant);

        PTA_R = Function.PTAfunction(RealRightValue[2], RealRightValue[3], RealRightValue[4],RealRightValue[5]);
        PTA_L = Function.PTAfunction(RealLeftValue[2],RealLeftValue[3], RealLeftValue[4], RealLeftValue[5]);
        if( Function.MIfunction(PTA_R) <= Function.MIfunction(PTA_L) ) {
            MIb = Function.MIfunction(PTA_R);
            MIw = Function.MIfunction(PTA_L);
            textRight = MIb;
            textLeft = MIw;

        }
        else {
            MIb = Function.MIfunction(PTA_L);
            MIw = Function.MIfunction(PTA_R);
            textRight = MIw;
            textLeft = MIb;
        }
        HH = Function.HHfunction(MIb, MIw);

        List<FrequencyVO> list = MainActivity.dao.list();
        listnumber = list.size();
        date_month = new SimpleDateFormat("MM").format(new Date());
        date_day = new SimpleDateFormat("dd").format(new Date());
        date_hour = new SimpleDateFormat("HH").format(new Date());
        date_minute = new SimpleDateFormat("mm").format(new Date());
        FrequencyVO frequencydate = new FrequencyVO(0,date_month + "月 " + date_day + "日",date_hour + "時 " + date_minute + "分", RealLeftValue[0], RealLeftValue[1],
                 RealLeftValue[2], RealLeftValue[3], RealLeftValue[4], RealLeftValue[5], RealLeftValue[6], RealLeftValue[7], RealLeftValue[8], RealRightValue[0], RealRightValue[1],
                RealRightValue[2], RealRightValue[3], RealRightValue[4], RealRightValue[5], RealRightValue[6], RealRightValue[7], RealRightValue[8],
                l_sextant, r_sextant, l_sextant_text, r_sextant_text, textLeft, textRight, HH);
        MainActivity.dao.insert(frequencydate);

        ChartView charView = new ChartView(this);
        charView.outerRectMargin = 80;

        charView.innerVerticalTickCount = 9;
        charView.topLabeles = new ArrayList<String>();
        charView.topLabeles.add("125");
        charView.topLabeles.add("250");
        charView.topLabeles.add("500");
        charView.topLabeles.add("1000");
        charView.topLabeles.add("1000");
        charView.topLabeles.add("2000");
        charView.topLabeles.add("3000");
        charView.topLabeles.add("4000");
        charView.topLabeles.add("6000");
        charView.topLabeles.add("8000");
        charView.strTopUnit = "Hz";

        charView.innerHorizontalTickCount = 13;
        charView.leftLabeles = new ArrayList<String>();
        charView.leftLabeles.add("-10");
        charView.leftLabeles.add("0");
        charView.leftLabeles.add("10");
        charView.leftLabeles.add("20");
        charView.leftLabeles.add("30");
        charView.leftLabeles.add("40");
        charView.leftLabeles.add("50");
        charView.leftLabeles.add("60");
        charView.leftLabeles.add("70");
        charView.leftLabeles.add("80");
        charView.leftLabeles.add("90");
        charView.leftLabeles.add("100");
        charView.leftLabeles.add("110");
        charView.strLeftUnit = "dB";

        charView.maxLevel = 110;
        charView.minLevel = -10;

        charView.dataO = new ArrayList<Double>();
        for(int a = 0; 9 > a; a++){
            charView.dataO.add(new Double(RealLeftValue[a]));
        }
        charView.dataX = new ArrayList<Double>();
        for(int a = 0; 9 > a; a++){
            charView.dataX.add(new Double(RealRightValue[a]));
        }

        setContentView(R.layout.activity_main_result);

        TextView ResultText = (TextView)findViewById(R.id.resulttext);
        ResultText.setText("右耳聽力 "+r_sextant + "dB "+ r_sextant_text +"左耳聽力 " + l_sextant + "dB " + l_sextant_text + ".\n" +
                "您的右耳聽力損失率" + textRight + "%, 左耳聽力損失率" + textLeft + "% 總計損失率 " + HH + "% .");

        SpannableStringBuilder builder = new SpannableStringBuilder(ResultText.getText());
        builder.setSpan(new ForegroundColorSpan(Color.RED),22,28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.RED),30,34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.BLUE),44,49, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.BLUE),51,55, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD),57,64, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setSpan(new ForegroundColorSpan(Color.RED),73,83, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.RED),85,90, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.BLUE),92,100, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.BLUE),102,107, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD),111,120, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD),122,128, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ResultText.setText(builder);

        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.wrap_char_view1);

        RelativeLayout.LayoutParams chartViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        charView.setLayoutParams(chartViewLayoutParams);
        rootLayout.addView(charView);

    }
}
