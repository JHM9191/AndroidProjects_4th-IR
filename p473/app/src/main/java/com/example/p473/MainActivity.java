package com.example.p473;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    TextView textView, textView2;
    ProgressBar progressBar, progressBar2;
    Button button,button2;

    MyHandler myHandler;
    Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        myHandler = new MyHandler();
        handler = new Handler();
    }

    // 1. 객체 형대로 만든 스레드 방식
    Thread t = new Thread() {
        @Override
        public void run() {
            for(int i=0;i<50;i++){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final int temp = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("T1:"+ temp);
                        progressBar.setProgress(temp);
                    }
                });
                Log.d("---","T1:"+i);
            } // end for
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    button.setEnabled(true);
                }
            });

        }
    };


    // 2. 인터페이스 형태로 만든 스레드 방식
    Runnable r = new Runnable() {
        @Override
        public void run() {
            for(int i=0;i<50;i++){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("---","R1:"+i);
                Message message = myHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("cnt",i);
                message.setData(bundle);
                myHandler.sendMessage(message);
            }
        }
    };
    class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();
            int cnt = bundle.getInt("cnt");
            textView2.setText(cnt+"");
            progressBar2.setProgress(cnt);
            if(cnt == 49){
                button2.setEnabled(true);
            }
        }
    }


    public void ckbt(View v){
        if(v.getId() == R.id.button){
            t.start();
            button.setEnabled(false);
        }else if(v.getId() == R.id.button2){
           Thread t = new Thread(r);
           t.start();
            button2.setEnabled(false);
        }else if(v.getId() == R.id.button3){
            delay();
        }
    }

    public void delay(){
        final ProgressDialog progressDialog =
                new ProgressDialog(this);

        AlertDialog.Builder dialog =
                new AlertDialog.Builder(this);
        dialog.setTitle("Delay..");
        dialog.setMessage("5 seconds ....");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setCancelable(false);
                progressDialog.show();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 5000);
            }
        });
        dialog.show();
    }



}
