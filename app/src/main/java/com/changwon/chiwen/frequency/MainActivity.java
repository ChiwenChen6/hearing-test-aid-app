package com.changwon.chiwen.frequency;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity {

    static FrequencyDAO dao;
    private BackPressCloseHandler backPressCloseHandler;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = new FrequencyDAO(getApplicationContext(), 1);

//        FrequencyVO frequencydate = new FrequencyVO(0,"8 21","3 10",10,10, 20,30, 40, 50, 60, 70, 80, 30,20, 50,-10, 40, 15, 60, 35, 80, 55,33,"","",44,11,22);
//        dao.insert(frequencydate);

//        frequencydate = new FrequencyVO(0,"3 8","1 30",1000, 200,300, 400, 500, 60, 70, 80, 10, 20,30, 40, 50, 60, 70, 80, 55,33,4,11,22);
//        dao.insert(frequencydate);

//        dao.delete(9);

// list
//        List<FrequencyVO> list = dao.list();
//        for(int i=0;i<list.size();i++) {
//            Log.i("編號 ",list.get(i).empno+"");
//            Log.i("日期 ",list.get(i).date+"");
//            Log.i("時間 ",list.get(i).time+"");
//            Log.i("l_250Hz ",list.get(i).l_a250+"");
//            Log.i("l_500Hz ",list.get(i).l_a500+"");
//            Log.i("l_1000Hz ",list.get(i).l_a1000+"");
//            Log.i("l_2000Hz ",list.get(i).l_a2000+"");
//            Log.i("l_3000Hz ",list.get(i).l_a3000+"");
//            Log.i("l_4000Hz ",list.get(i).l_a4000+"");
//            Log.i("l_6000Hz ",list.get(i).l_a6000+"");
//            Log.i("l_8000Hz ",list.get(i).l_a8000+"");
//            Log.i("r_250Hz ",list.get(i).r_a250+"");
//            Log.i("r_500Hz ",list.get(i).r_a500+"");
//            Log.i("r_1000Hz ",list.get(i).r_a1000+"");
//            Log.i("r_2000Hz ",list.get(i).r_a2000+"");
//            Log.i("r_3000Hz ",list.get(i).r_a3000+"");
//            Log.i("r_4000Hz ",list.get(i).r_a4000+"");
//            Log.i("r_6000Hz ",list.get(i).r_a6000+"");
//            Log.i("r_8000Hz ",list.get(i).r_a8000+"");
//            Log.i("Lcon ",list.get(i).l_sextant+"");
//            Log.i("Rcon ",list.get(i).r_sextant+"");
//            Log.i("Lloss_ratio ",list.get(i).l_loss_ratio+"");
//            Log.i("Rloss_ratio ",list.get(i).r_loss_ratio+"");
//            Log.i("Tloss_ratio ",list.get(i).loss_ratio+"");
//        }




        backPressCloseHandler = new BackPressCloseHandler(this);

        Button btn_main = (Button) findViewById(R.id.btn_main);
        Button btn_sub = (Button) findViewById(R.id.btn_sub);
        TextView textView = (TextView)findViewById(R.id.test1);
//        Button testing = (Button) findViewById(R.id.testing);
        Button menual = (Button) findViewById(R.id.menual);
//        Button btn_test = (Button) findViewById(R.id.btn_test);
        Button dB_btn = (Button)findViewById(R.id.dbtest);
//        Button  listbutton = (Button)findViewById(R.id.listtest);
        Button hearing_btn = (Button)findViewById(R.id.btn_hearing);


        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this,VolumeCheck.class);
                startActivity(intent);

            }
        });
        btn_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this,ResultList.class);
                startActivity(intent);

            }
        });
//        testing.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, ResultTest.class);
//                startActivity(intent);
//            }
//        });

        menual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenualTest.class);
                startActivity(intent);
            }
        });

        dB_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, dBTest.class);
                startActivity(intent);
            }
        });

//        listbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MainResult.class);
//                startActivity(intent);
//            }
//        });

//        btn_test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent=new Intent(MainActivity.this,FrequencyTest.class);
//                startActivity(intent);
//
//            }
//        });
        hearing_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this,SpeechActivity.class);
                startActivity(intent);

            }
        });





        textView.setText("\n\nHEARING TEST\n" +
                "使用稱為分貝的單位來指示聲音的靈敏度。\n" +
                "聲音強度不是聲音本身的大小.");

    }

    //Back Intent Active

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

                Intent t = new Intent(activity, MainActivity.class);
                activity.startActivity(t);

                activity.moveTaskToBack(true);
                activity.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }

        public void showGuide() {
            toast = Toast.makeText(activity, "再按一次退出.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
