package com.example.coronaMapProject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText editText, editText2;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        progressDialog = new ProgressDialog(this);
    }

    public void login(View v) {
        String id = editText.getText().toString();
        String pwd = editText2.getText().toString();
        HttpTask task = new HttpTask(id, pwd);
        task.execute();
    }


    // Thread 방법 중 AsyncTask 방법을 사용하기로 함.
    class HttpTask extends AsyncTask<Void, Void, String> {

        String url;

        public HttpTask(String id, String pwd) {
            url = "http://13.209.177.31/webview/login.jsp?";
            url += "id=" + id + "&pwd=" + pwd;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setTitle("HTTP Connect ..");
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return HttpHandler.getString(url);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("---", s.trim());
            if (s.trim().equals("1")) {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                startActivity(intent);
            }
            progressDialog.dismiss();
        }
    }
}
