package com.example.p489;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    ImageView imageView;
    ProgressBar progressBar;
    MyTask myTask;
    Button button, button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button2.setEnabled(false);
    }

    public void ckbt(View v) {
        if(v.getId() == R.id.button) {
            myTask = new MyTask();
            myTask.execute(50); // 50 : 시작값을 의미함. doInBackground()에 들어감.
        } else if(v.getId() == R.id.button2){
            myTask.cancel(true);
            myTask.onCancelled();
        }
    }

    class MyTask extends AsyncTask<Integer, Integer, String> { // <초기값, 중간값, 결과값>
        @Override
        protected void onPreExecute() {
            progressBar.setMax(50);
            button.setEnabled(false);
            button2.setEnabled(true);
        }

        @Override
        protected String doInBackground(Integer... integers) {
           int cnt = integers[0].intValue();
           int sum = 0;
               for(int i = 0; i < cnt; i++) {
                   if(isCancelled() == true) {
                       break;
                   }
                   try {
                       Thread.sleep(100);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   sum +=i;
                   publishProgress(i); // onProgressUpdate() 메소드를 호출함.
               }

           return "Result: " + sum + "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) { // progress가 돌아가는 중간에 결과값을 출력할 수 있음.
            int i = values[0].intValue();
            progressBar.setProgress(i);
            textView.setText(i + "");
            if(i%2 == 0) {
                imageView.setImageResource(R.drawable.cat2);
            } else {
                imageView.setImageResource(R.drawable.cat3);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            textView.setText(s);
            button.setEnabled(true);
            button2.setEnabled(false);
//            progressBar.setProgress(0);
        }

        @Override
        protected void onCancelled() {
            progressBar.setProgress(0);
            textView.setText("");
            button.setEnabled(true);
            button2.setEnabled(false);
        }
    }
}
