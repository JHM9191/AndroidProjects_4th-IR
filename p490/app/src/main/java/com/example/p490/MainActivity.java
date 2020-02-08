package com.example.p490;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // speed
    TextView tv_speed;
    static int speed;
    static int prev_speed;
    ImageView iv_speed;
    Button btn_speed_up;
    Button btn_speed_down;
    Button btn_speed_zero;

    // temp
    TextView tv_temp;
    ImageView iv_temp;
    static int temp;
    static int prev_temp;
    MyHandler myHandler;
    Thread thread;

    // rpm
    TextView tv_rpm;
    RpmTask rpmTask;
    ImageView iv_rpm;
    static int rpm;
    Button btn_rpm_up;
    Button btn_rpm_down;
    Button btn_rpm_zero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_speed = findViewById(R.id.tv_speed);
        iv_speed = findViewById(R.id.iv_speed);
        btn_speed_up = findViewById(R.id.btn_speed_up);
        btn_speed_down = findViewById(R.id.btn_speed_down);
        btn_speed_zero = findViewById(R.id.btn_speed_zero);
        btn_speed_down.setEnabled(false);
        btn_speed_zero.setEnabled(false);

        tv_temp = findViewById(R.id.tv_temp);
        iv_temp = findViewById(R.id.iv_temp);
        iv_temp.setImageResource(R.drawable.sign_yellow);
        myHandler = new MyHandler();

        tv_rpm = findViewById(R.id.tv_rpm);
        iv_rpm = findViewById(R.id.iv_rpm);
        btn_rpm_up = findViewById(R.id.btn_rpm_up);
        btn_rpm_down = findViewById(R.id.btn_rpm_down);
        btn_rpm_zero = findViewById(R.id.btn_rpm_zero);
        btn_rpm_down.setEnabled(false);
        btn_rpm_zero.setEnabled(false);
    }

/**************************************************************************************************/
/** 1. UIThread를 사용한 방식. **/
/*

예상 결과:

    green: speed = [0, 50]
    yellow: speed = [51,80]
    red: speed = [81, inf]

 */
    public void click_speed(View v) {
        btn_speed_up.setEnabled(false);
        btn_speed_down.setEnabled(false);
        btn_speed_zero.setEnabled(false);
        if(v.getId() == R.id.btn_speed_up) {
            btn_speed_up.setEnabled(false);
            speed += 10;
            uiThread.start();
        } else if(v.getId() == R.id.btn_speed_down) {
            speed -= 10;
            if(speed >= 10) {
                uiThread.start();
            } else if(speed < 10){
                speed = 0;
                uiThread.start();
                btn_speed_down.setEnabled(false);
            }
        } else if(v.getId() == R.id.btn_speed_zero) {
            speed = 0;
            prev_speed = 0;
            tv_speed.setText("0 km/h");
            btn_speed_up.setEnabled(true);
            btn_speed_down.setEnabled(false);
            btn_speed_zero.setEnabled(false);
            iv_speed.setImageResource(R.drawable.sign_none);
        }
    }

    Thread uiThread = new Thread() {
        @Override
        public void run() {
            if((speed - prev_speed) > 0) { // clicked speed up
                for(int i = speed - 9; i <= speed;i++) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final int s = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_speed.setText(s + " km/h");
                            setSpeedImage(s);
                        }
                    });
                }
            } else if((speed - prev_speed) < 0) { // clicked speed down
                for(int i = prev_speed - 1; i >= speed;i--) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final int s = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_speed.setText(s + " km/h");
                            setSpeedImage(s);
                        }
                    });
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_speed_up.setEnabled(true);
                    btn_speed_zero.setEnabled(true);
                    if (speed < 10) {
                        btn_speed_down.setEnabled(false);
                        btn_speed_zero.setEnabled(false);
                        iv_speed.setImageResource(R.drawable.sign_none);

                    } else {
                        btn_speed_down.setEnabled(true);
                    }
                }
            });
            prev_speed = speed;
        }
    };

    public void setSpeedImage(int s) {
        if(s < 50) {
            iv_speed.setImageResource(R.drawable.sign_green);
        } else if(s >= 50 && s < 80) {
            iv_speed.setImageResource(R.drawable.sign_yellow);
        } else if(s >= 80) {
            iv_speed.setImageResource(R.drawable.sign_red);
        } else if(s == 0) {
            iv_speed.setImageResource(R.drawable.sign_none);
        }
    }

/**************************************************************************************************/
/** 2. Handler를 사용한 방식 **/
/*

예상 결과:

-15 ----- -10 ----- -5 ----- 0 ----- 5 ----- 10 ----- 15 ----- 20 ----- 25 ----- 30 ----- 35 ---
red -12][-11                   yellow                    17][18   green  26][27  yellow   35][36 red

    green: temp = [18,26]
    yellow: temp = [-11,17] & [27,35]
    red: temp = [-inf, -12] & [36, inf]

 */
    public void click_temp(View v) {
        if(v.getId() == R.id.btn_temp_up) {
            temp += 5;
            thread = new Thread(r);
            thread.start();
        } else if(v.getId() == R.id.btn_temp_down) {
            temp -= 5;
            thread = new Thread(r);
            thread.start();
        } else if(v.getId() == R.id.btn_temp_zero) {
            tv_temp.setText("0°C");
            temp = 0;
            prev_temp = 0;
            iv_temp.setImageResource(R.drawable.sign_yellow);
        }
    }

    Runnable r = new Runnable() {

        @Override
        public void run() {
            if(temp > prev_temp) {
                for(int i = prev_temp + 1; i <= temp; i++){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = myHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putInt("cnt",i);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }
            } else if(temp < prev_temp) {
                for(int i = prev_temp - 1; i >= temp; i--){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = myHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putInt("cnt",i);
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }
            }
            prev_temp = temp;
        }
    };

    class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
            int cnt = bundle.getInt("cnt");
            tv_temp.setText(cnt+"°C");
            if(cnt >= 18 && cnt <= 26) {
                iv_temp.setImageResource(R.drawable.sign_green);
            } else if((cnt > 26 && cnt <= 35) || (cnt < 18 && cnt >= - 11)) {
                iv_temp.setImageResource(R.drawable.sign_yellow);
            } else if(cnt > 35 || cnt < -11) {
                iv_temp.setImageResource(R.drawable.sign_red);
            }
        }
    }

/**************************************************************************************************/
/** 3. AsyncTask를 사용한 방식. **/

/*

예상 결과:

    green: rpm = [0, 100]
    yellow: rpm = [101, 200]
    red: rpm = [200, inf]

 */
    public void click_rpm(View v) {
        if(v.getId() == R.id.btn_rpm_up) {
            rpmTask = new RpmTask();
            rpm += 50;
            rpmTask.execute(rpm,1);
            btn_rpm_zero.setEnabled(true);
        } else if(v.getId() == R.id.btn_rpm_down) {
            if(rpm >= 50) {
                rpmTask = new RpmTask();
                rpm -= 50;
                rpmTask.execute(rpm,2);
            } else {
                btn_rpm_down.setEnabled(false);
            }
        } else if(v.getId() == R.id.btn_rpm_zero) {
            rpmTask.cancel(true);
            rpmTask.isCancelled();
            Log.i("---", "clicked reset");
        }
    }

    class RpmTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... integers) {
            int count = integers[0].intValue();
            int cond = integers[1].intValue();
            if(cond == 1) {
                for (int i = rpm - 50; i <= count; i++) {
                    if (isCancelled() == true) {
                        break;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(i);
                }
            } else if(cond == 2) {
                for (int i = count+50; i >= count; i--) {
                    if (isCancelled() == true) {
                        break;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(i);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int i = values[0].intValue();
            tv_rpm.setText(i+" rpm");

            if(i > 0 && i <= 100){
                iv_rpm.setImageResource(R.drawable.sign_green);
            } else if(i > 100 && i <= 200) {
                iv_rpm.setImageResource(R.drawable.sign_yellow);
            } else if(i > 200) {
                iv_rpm.setImageResource(R.drawable.sign_red);
            } else if(i == 0) {
                iv_rpm.setImageResource(R.drawable.sign_none);
            }

            if(i >= 50) {
                btn_rpm_down.setEnabled(true);
                btn_rpm_zero.setEnabled(true);
            } else if(i < 50) {
                btn_rpm_down.setEnabled(false);
            }
        }

        @Override
        protected void onCancelled() {
            tv_rpm.setText("0 rpm");
            rpm = 0;
        }
    }
}
/**************************************************************************************************/

