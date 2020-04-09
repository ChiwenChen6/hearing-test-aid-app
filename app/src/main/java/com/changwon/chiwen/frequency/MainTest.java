package com.changwon.chiwen.frequency;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;


public class MainTest extends AppCompatActivity {

    private EditText editTextFreq;
    // private EditText editTextDuration;
    private TextView textViewvol;
    private TextView textViewFreq;
    private TextView sideValue;
    // private EditText editTextVol;
    Boolean trigger = true;
    Boolean Ready = false;
    Boolean Stop = false;
    Boolean TextReady = false;
    Boolean ClickEvent = false;
    Boolean ButtonEvent = true;
    Boolean isPlaying = false;

    private BackPressCloseHandler backPressCloseHandler;

    AudioManager audioManager;

    int number[] = {1000, 2000, 3000, 4000, 6000, 8000, 1000, 500, 250, 125}; // 頻率陣列
    float sound_decibel[] = {0.0001f,0.0002f, 0.0004f, 0.0008f, 0.0012f, 0.0025f, 0.0050f, 0.0080f, 0.0140f, 0.0220f, 0.0400f, 0.0600f, 0.0900f,
    0.1600f, 0.2400f, 0.32768f, 0.65536f};
    int dec = 10; // 當前分貝
    int duration = 2; // 延遲時間
    int num = 0; // 按鈕點擊
    int count = 0; //時間
    int[] LeftValue = new int[100];     // 測試左分貝值
    int[] RightValue = new int[100];    // 測試右分貝值
    public static int[] RealLeftValue = new int[100];     // 實際左分貝值（用於結果
    public static int[] RealRightValue = new int[100];    // 實際右分貝值（用於結果
    int MindB = -20;      //目前為止，用戶聽到的最小分貝
    int iscycle = 0;
    boolean matchdB = false;
    int Testtime = 3;       // 一個變量，確定分貝應增加多少秒（例如果測試時間= 3，則dB每3秒增加一次）。
    int LeftCount = 0;
    int RightCount = 0;
    int FqCount = 0; // 頻率帶
    int start = 0; //開始位置
    float realdB = 0; //真正的分貝
    boolean scenedelay = false;
    int scenedelaycount = 0;    // 延遲時間

    TextView LText;
    TextView RText;
    TextView FqValue;
    TextView Test;
    TextView StartText;
    TextView TestTitle;

    String Left;
    String Right;

    Button btn_left;

//    public static boolean Sound = true;
    Thread thread;

    private FloatingActionButton myFab;

    Intent gotoMain4;
    Intent gotoSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);

        gotoMain4 = new Intent(this, MainResult.class);
        gotoSpeech= new Intent(this, SpeechActivity.class);


        Bundle leftBun =new Bundle();
        leftBun.putIntArray("LeftBun", RealLeftValue);

        Bundle RightBun =new Bundle();
        RightBun.putIntArray("RightBun", RealRightValue);

        gotoMain4.putExtra("RealLeftValue", leftBun);
        gotoMain4.putExtra("RealRightValue", RightBun);
        gotoSpeech.putExtra("RealLeftValue", leftBun);
        gotoSpeech.putExtra("RealRightValue", RightBun);

        backPressCloseHandler = new BackPressCloseHandler(this);

        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        btn_left = (Button) findViewById(R.id.btn_left);
        // editTextFreq = (EditText) findViewById(R.id.editTextFreq);
        textViewvol = (TextView) findViewById(R.id.textView5);
//        LText = (TextView) findViewById(R.id.LeftValue);
//        Left = LText.getText().toString();
//        RText = (TextView) findViewById(R.id.RightValue);
//        Right = RText.getText().toString();
        FqValue = (TextView) findViewById(R.id.FrequencyValue);
        StartText = (TextView)findViewById(R.id.starttext);
        TestTitle = (TextView)findViewById(R.id.testtitle);
        sideValue = (TextView)findViewById(R.id.sideValue);


//        Test = (TextView)findViewById(R.id.woogitest);




        SecondThread runnable = new SecondThread();
        thread = new Thread(runnable);
        thread.setDaemon(true);

//        btn_left.setEnabled(true);

        textViewvol.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                realdB = getdB(dec);
//                ZenTone.getInstance().stop();
//                Toast.makeText(MainTest.this, "" + realdB, Toast.LENGTH_SHORT).show();
                handleTonePlay();
                ButtonEvent = true;

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }



    public void onClick(View view) {

        boolean left = false;
        boolean right = false;

        switch (view.getId()) {
            case R.id.btn_left:
//                if (trigger && !Ready) {
//                    btn_left.setEnabled(false);
//                    thread.start();
//                    trigger = false;
//                }
//
//                if( Ready ) {
//                    mHandler.sendEmptyMessage(1);
//                }

                if (ButtonEvent) {

                    if(!Ready) {
                        if(trigger) {
//                            btn_left.setEnabled(false);
                            StartText.setText("如果聽到聲音，請按下面的按鈕.");
                            TestTitle.setText("自動聽力測試中\n");

                            textViewvol.setText("30");
                            textViewvol.setTextColor(Color.parseColor("#FF00FF"));



                            thread.start();
                            trigger = false;
                        }
                    }
                    else {

                        if(MindB == dec) {
                            matchdB = true;
                        }

                        MindB = dec;
//                        Toast.makeText(this, "MindB is " + MindB, Toast.LENGTH_SHORT).show();
                        isPlaying = false;
                        PlayTone.getInstance().stop();
//                        ZenTone.getInstance().stop();
                        mHandler.sendEmptyMessage(1);
                    }
                }

                ButtonEvent = false;
                if (!Ready) {
                    Ready = true;
                }
                break;
        }
    }
    //Thread MARK
    class SecondThread implements Runnable {
        public void run() {
            while (true) {

                if(isPlaying == false) {        // 單擊該按鈕時，它將停止播放並運行下一個分貝頻率
                    count = 3;
                }

                if ( ClickEvent ) {         // 按下按鈕時執行-10dB的頻率
                    if(count % 3 == 0 ) {
                        TextReady = true;
                        count = 0;
                        mHandler.sendEmptyMessage(0);
//                        ClickEvent = false;
                    }
                }
                else {
                    if(dec <= 110){      // 最大dB限制為110 dB

                        if(PlayTone.Sound) {

                            if(!matchdB) {

                                if(iscycle == 0 && count % Testtime == 0) {
                                    if(dec < 110) {
                                        dec += 20;
                                    }
                                    count = 0;
                                    TextReady = true;
                                    mHandler.sendEmptyMessage(0);
                                }
                                else if(iscycle == 1 && count % Testtime == 0) {
                                    if(dec < 110) {
                                        dec += 5;
                                    }
                                    count = 0;
                                    TextReady = true;
                                    mHandler.sendEmptyMessage(0);
                                }
                                else if (iscycle == 2 && count % Testtime == 0) {
                                    if(dec < 110) {
                                        dec += 5;
                                    }
                                    count = 0;
                                    TextReady = true;
                                    mHandler.sendEmptyMessage(0);
                                }

                            }
                            else if(matchdB) {      //
                                count = 0;
                                mHandler.sendEmptyMessage(2);
                                matchdB = false;
                            }
                        }

                        else {      // Frequency running to right ear


                            if(!matchdB) {
                                if(iscycle == 0 && count % Testtime == 0) {
                                    if(dec < 110) {
                                        dec += 20;
                                    }
                                    count = 0;
                                    TextReady = true;
                                    mHandler.sendEmptyMessage(0);
                                }
                                else if(iscycle == 1 && count % Testtime == 0) {
                                    if(dec < 110) {
                                        dec += 5;
                                    }
                                    count = 0;
                                    TextReady = true;
                                    mHandler.sendEmptyMessage(0);
                                }
                                else if (iscycle == 2 && count % Testtime == 0) {
                                    if(dec < 110) {
                                        dec += 5;
                                    }
                                    count = 0;
                                    TextReady = true;
                                    mHandler.sendEmptyMessage(0);
                                }
                            }
                            else if(matchdB) {
                                count = 0;
                                mHandler.sendEmptyMessage(3);
                                matchdB = false;
                            }
                        }

                    }
                }
                if(scenedelay) {                // scenedelaycount Move view after time
                    try {           // 每秒運行一次
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        ;
                    }
                    scenedelaycount++;

                    if(scenedelaycount == 2) {
                        mHandler.sendEmptyMessage(4);
                    }
                }

                try {           // 每秒運行一次
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    ;
                }

                count++;
//                mHandler.sendEmptyMessage(10);
                if(count % 3 == 0) {
                    count = 0;
                }

            }
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {        //handle frequency execution
//                // handleTonePlay();
                if (TextReady) {
                    textViewvol.setText("" + dec + "dB .");
                    textViewvol.setTextColor(Color.parseColor("#FF00FF"));
                    FqValue.setText("頻率 :" + number[FqCount]);
                    FqValue.setTextColor(Color.parseColor("#FF05FF"));

                    count = 0;
                    TextReady = false;
                    ClickEvent = false;
                }
            }

            if (msg.what == 1) {        //  handle button click
                if(iscycle < 2) {
                    iscycle += 1;
                }
                ClickEvent = true;
                if(!matchdB && dec > -5) {
                    dec -= 10;
                }
                else if(!matchdB && dec > -10) {
                    dec -= 5;
                }
                if(matchdB == true && PlayTone.Sound == true) {
                    mHandler.sendEmptyMessage(2);
                }
                else if(matchdB == true && PlayTone.Sound == false) {
                    mHandler.sendEmptyMessage(3);
                }
                else {
                    mHandler.sendEmptyMessage(0);
                }
            }

            if (msg.what == 2) {
                sideValue.setText("右耳");
                sideValue.setTextColor(Color.parseColor("primary_text"));
                LeftValue[LeftCount] = MindB;
//                LText.setText(Left + " " + LeftValue[LeftCount] + "dB");
                LeftCount++;
                PlayTone.Sound = false;
                matchdB = false;
                iscycle = 0;
//                count = 0;
                TextReady = true;
                dec = 30;
                MindB = -20;
//                Left = LText.getText().toString();
                mHandler.sendEmptyMessage(0);

            }

            if (msg.what == 3) {
                sideValue.setText("左耳");
                sideValue.setTextColor(Color.parseColor("primary_text"));
                RightValue[RightCount] = MindB;
//                RText.setText(Right + " " + RightValue[RightCount] + "dB");
                RightCount++;
                FqCount += 1;
//                count = 0;
                matchdB = false;
                PlayTone.Sound = true;
                iscycle = 0;
                TextReady = true;

                if (FqCount < 10) {
                    FqValue.setText("頻率 :" + number[FqCount]);
                } else {
                    // 將左耳分貝存儲回陣列以顯示結果
                    RealLeftValue[0] = LeftValue[9];
                    RealLeftValue[1] = LeftValue[8];
                    RealLeftValue[2] = LeftValue[7];

                    if(LeftValue[0] >= LeftValue[6]) {
                        RealLeftValue[3] = LeftValue[6];
                    }
                    else {
                        RealLeftValue[3] = LeftValue[0];
                    }

                    RealLeftValue[4] = LeftValue[1];
                    RealLeftValue[5] = LeftValue[2];
                    RealLeftValue[6] = LeftValue[3];
                    RealLeftValue[7] = LeftValue[4];
                    RealLeftValue[8] = LeftValue[5];

                    // 將右耳分貝存儲回陣列以顯示結果
                    RealRightValue[0] = RightValue[9];
                    RealRightValue[1] = RightValue[8];
                    RealRightValue[2] = RightValue[7];

                    if(RightValue[0] >= RightValue[6]) {
                        RealRightValue[3] = RightValue[6];
                    }
                    else {
                        RealRightValue[3] = RightValue[0];
                    }

                    RealRightValue[4] = RightValue[1];
                    RealRightValue[5] = RightValue[2];
                    RealRightValue[6] = RightValue[3];
                    RealRightValue[7] = RightValue[4];
                    RealRightValue[8] = RightValue[5];

                    scenedelay = true;
                    Toast.makeText(MainTest.this, "自動聽力測試已結束。\n" +
                            "移至測試結果.", Toast.LENGTH_SHORT).show();

                }
                dec = 30;
                MindB = -20;
//                Right = RText.getText().toString();
                mHandler.sendEmptyMessage(0);
//                RightValue[RightCount] = dec;
//                RText.setText(Right + " " + RightValue[RightCount] + "dB");
//                RightCount++;
//                Sound = true;
//                num = 0;
////                count = 0;
//                TextReady = true;
//
//                if (FqCount < 7) {
//                    FqValue.setText("頻率帶 :" + number[FqCount]);
//
//                } else {
//                    startActivityForResult(gotoMain4, 0);
//                }
//                FqCount += 1;
//                dec = 30;
//                Right = RText.getText().toString();
//                mHandler.sendEmptyMessage(0);
            }

            if (msg.what == 4) {
                startActivityForResult(gotoMain4, 0);
            }

        }
    };

    private void handleTonePlay() {

        if (FqCount >= 10) return;
        btn_left.setEnabled(true);

        isPlaying = true;
        PlayTone.getInstance().makeTone(number[FqCount], duration, realdB);
        PlayTone.getInstance().play();

//        ZenTone.getInstance().generate(number[FqCount], duration, realdB, new ToneStoppedListener() {
//
//            @Override
//            public void onToneStopped() {
//
//                // btn_left.setEnabled(false);
//
//                // myFab.setImageResource(R.drawable.ic_play_arrow_white_24dp);
//            }
//        });
    }


    private float getdB(int decibel) {        // 實際的分貝轉換
//        float dB = decibel - 10;
        int real_dB = decibel/5 - 2;
        if(decibel < 10) return 0.000005f;
        else if(decibel > 90) return 1.0f;
        else return sound_decibel[real_dB];
//        dB /= 1000f;

//        return dB;
    }


//    //Back Intent Active
//
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }


    public class BackPressCloseHandler {
        private long backKeyPressedTime = 0;
        private Toast toast;

        private Activity activity;

        public BackPressCloseHandler(Activity context) {
            this.activity = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                toast.cancel();


//                activity.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                MainTest.this.finish();
                Intent t = new Intent(activity, MainActivity.class);
                activity.startActivity(t);
//                activity.moveTaskToBack(true);

            }
        }

        public void showGuide() {
            toast = Toast.makeText(activity, "Press again to return to the first screen.\n.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}