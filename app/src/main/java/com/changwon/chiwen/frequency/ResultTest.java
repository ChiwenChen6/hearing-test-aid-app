package com.changwon.chiwen.frequency;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.SpannableStringBuilder;
        import android.view.ViewGroup;
        import android.widget.RelativeLayout;
        import android.widget.TextView;

        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;

public class ResultTest extends AppCompatActivity {


    int[] MenualRealLeftValue = new int[100];
    int[] MenualRealRightValue = new int[100];
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

    public static int listnumber;
    String date_month;
    String date_day;
    String date_hour;
    String date_minute;

    boolean list_left_decibel = false;
    boolean list_right_decibel = false;
    boolean list_left_ratio = false;
    boolean list_right_ratio = false;

    int num_list_leftdecibel;
    int num_list_leftdecibel_text;
    int num_list_rightdecibel;
    int num_list_rightdecibel_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menual_result);

//        Intent intent = getIntent();
//        MenualRealRightValue = intent.getIntArrayExtra("menualrightvalue");
//        MenualRealLeftValue = intent.getIntArrayExtra("menualleftvalue");

        MenualRealLeftValue = MenualTest.menualrealleftvalue;
        MenualRealRightValue = MenualTest.menualrealrightvalue;


        l_sextant = Function.sextantfunction(MenualRealLeftValue[2], MenualRealLeftValue[3], MenualRealLeftValue[3], MenualRealLeftValue[4], MenualRealLeftValue[4],
                MenualRealLeftValue[6]);
        r_sextant = Function.sextantfunction(MenualRealRightValue[2], MenualRealRightValue[3], MenualRealRightValue[3], MenualRealRightValue[4], MenualRealRightValue[4],
                MenualRealRightValue[6]);

        l_sextant_text = Function.Resultdgreefunction(l_sextant);
        r_sextant_text = Function.Resultdgreefunction(r_sextant);


        PTA_R = Function.PTAfunction(MenualRealRightValue[2], MenualRealRightValue[3], MenualRealRightValue[4], MenualRealRightValue[5]);
        PTA_L = Function.PTAfunction(MenualRealLeftValue[2], MenualRealLeftValue[3], MenualRealLeftValue[4], MenualRealLeftValue[5]);
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
        FrequencyVO frequencydate = new FrequencyVO(0,date_month + "月 " + date_day + "日",date_hour + "時 " + date_minute + "分", MenualRealLeftValue[0],
                MenualRealLeftValue[1], MenualRealLeftValue[2], MenualRealLeftValue[3], MenualRealLeftValue[4], MenualRealLeftValue[5], MenualRealLeftValue[6],
                MenualRealLeftValue[7], MenualRealLeftValue[8], MenualRealRightValue[0], MenualRealRightValue[1], MenualRealRightValue[2], MenualRealRightValue[3],
                MenualRealRightValue[4], MenualRealRightValue[5], MenualRealRightValue[6], MenualRealRightValue[7], MenualRealRightValue[8], l_sextant, r_sextant,
                l_sextant_text, r_sextant_text, textLeft, textRight, HH);
        MainActivity.dao.insert(frequencydate);

        ChartView charView = new ChartView(this);
        charView.outerRectMargin = 80;

        charView.innerVerticalTickCount = 9;
        charView.topLabeles = new ArrayList<String>();
        charView.topLabeles.add("125");
        charView.topLabeles.add("250");
        charView.topLabeles.add("500");
        charView.topLabeles.add("1000");
//        charView.topLabeles.add("1000");
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
            charView.dataO.add(new Double(MenualRealLeftValue[a]));
        }
        charView.dataX = new ArrayList<Double>();
        for(int a = 0; 9 > a; a++){
            charView.dataX.add(new Double(MenualRealRightValue[a]));
        }

        setContentView(R.layout.activity_menual_result);

        TextView ResultText = (TextView)findViewById(R.id.menualresulttext);
        ResultText.setText("右耳聽力 "+r_sextant + "dB "+ r_sextant_text +"左耳聽力 " + l_sextant + "dB " + l_sextant_text + ".\n" +
                "您的右耳聽力損失率" + textRight + "%, 左耳聽力損失率" + textLeft + "% 總計損失率 " + HH + "% .");

        SpannableStringBuilder builder = new SpannableStringBuilder(ResultText.getText());

//        if(r_sextant >= 0) {
//            list_right_decibel = false;
//        }
//        else {
//            list_right_decibel = true;
//        }
//
//        if(l_sextant >= 0) {
//            list_left_decibel = false;
//        }
//        else {
//            list_left_decibel = true;
//        }
//
//        if(list_right_decibel) {
//
//
//            if(list_left_decibel) {
//                builder.setSpan(new ForegroundColorSpan(Color.RED),22,28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.RED),30,35, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////                builder.setSpan(new StyleSpan(Typeface.BOLD),38,64, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),44,49, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),51,56, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////                builder.setSpan(new StyleSpan(Typeface.BOLD),57,64, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                builder.setSpan(new ForegroundColorSpan(Color.RED),74,84, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.RED),86,92, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),93,102, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),104,110, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new StyleSpan(Typeface.BOLD),114,123, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new StyleSpan(Typeface.BOLD),125,126, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//
//            else {
//                builder.setSpan(new ForegroundColorSpan(Color.RED),22,28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.RED),30,35, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////                builder.setSpan(new StyleSpan(Typeface.BOLD),38,64, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),44,49, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),51,55, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////                builder.setSpan(new StyleSpan(Typeface.BOLD),57,64, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                builder.setSpan(new ForegroundColorSpan(Color.RED),73,83, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.RED),85,91, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),92,101, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),103,108, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new StyleSpan(Typeface.BOLD),111,120, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new StyleSpan(Typeface.BOLD),122,128, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//
//
//        }
//
//        else {                                    // 如果聽力正常
//            if(list_left_decibel) {             //左耳聽覺不佳
//                builder.setSpan(new ForegroundColorSpan(Color.RED),22,28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.RED),30,34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),44,49, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),51,55, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new StyleSpan(Typeface.BOLD),57,64, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                builder.setSpan(new ForegroundColorSpan(Color.RED),73,83, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.RED),85,90, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),92,100, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),102,107, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new StyleSpan(Typeface.BOLD),111,120, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new StyleSpan(Typeface.BOLD),122,128, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//
//            else {
//                builder.setSpan(new ForegroundColorSpan(Color.RED),22,28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.RED),30,34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),43,48, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),50,54, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new StyleSpan(Typeface.BOLD),57,64, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                builder.setSpan(new ForegroundColorSpan(Color.RED),73,83, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.RED),85,90, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),92,100, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new ForegroundColorSpan(Color.BLUE),102,107, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new StyleSpan(Typeface.BOLD),111,120, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                builder.setSpan(new StyleSpan(Typeface.BOLD),122,128, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//
//        }
//
        ResultText.setText(builder);

        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.menual_wrap_char_view);

        RelativeLayout.LayoutParams chartViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        charView.setLayoutParams(chartViewLayoutParams);
        rootLayout.addView(charView);

    }
}
