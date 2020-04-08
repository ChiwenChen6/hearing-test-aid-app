package com.changwon.chiwen.frequency;


public class Function {

    public static int sextantfunction(int Value1, int Value2, int Value3, int Value4, int Value5, int Value6) {
        int sextant = (Value1 + Value2 + Value3 + Value4 + Value5 + Value6) / 6;

        return sextant;
    }

    public static float PTAfunction(int Value1, int Value2, int Value3, int Value4) {
//
        float PTA = (Value1 + Value2 + Value3 + Value4) / 4;

        return PTA;
    }

    public static float MIfunction(float PTA) {

        float MI = (PTA-25) * 1.5f;
        if( MI < 0 ) {
            MI = 0;
        }
        return MI;
    }

    public static float HHfunction(float MIb, float MIw) {
        float HH = (5*MIb + MIw) / 6;

        return HH;
    }

    public static String Resultdgreefunction(float sextant) {

        String sextant_text;

        if(sextant <= 25) {           //   聽力狀況
            sextant_text = "正常" ;
        }
        else if(sextant <= 40) {
            sextant_text = "輕度聽力損失\n";
        }
        else if(sextant <= 55) {
            sextant_text = "輕中度聽力損失";
        }
        else if(sextant <= 70) {
            sextant_text = "中度聽力損失";
        }
        else if(sextant <= 90) {
            sextant_text = "高聽力損失";
        }
        else {
            sextant_text = "無";
        }

        return sextant_text;
    }
    public static int SpinnerConverter(int decibel){

        decibel = decibel / 5;
        decibel += 2;

        return decibel;
    }
}
