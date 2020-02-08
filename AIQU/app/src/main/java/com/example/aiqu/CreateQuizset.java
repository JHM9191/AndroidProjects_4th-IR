package com.example.aiqu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.Inflater;

public class CreateQuizset extends AppCompatActivity {
    //    final static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestLog/logfile.txt";
//    final static String filePath = "/etc/public.libraries.txt";
//    TextView textView;
//    TextView detail;

    EditText et_filepath;
    EditText et_quizset_name;
    EditText et_subject;

    Intent fileIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createquizset);
        // ActionBar 숨기기
        getSupportActionBar().hide();
//        Log.i("-----", filePath);
    }


    public void createQuizset(View v) {
        if (v.getId() == R.id.bt_choose_file) {
            fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            fileIntent.setType("*/*");
            startActivityForResult(fileIntent, 100);
        } else if(v.getId() == R.id.bt_register_quizset) {
            et_quizset_name = findViewById(R.id.et_quizset_name);
            et_subject = findViewById(R.id.et_subject);
            Log.d("---", et_filepath.getText() + " " + et_quizset_name.getText() + " " + et_subject.getText());
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

            intent.putExtra("name", et_quizset_name.getText() + "");
            intent.putExtra("subject", et_subject.getText() + "");
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100) {
            if (resultCode == RESULT_OK) {
                String path = data.getData().getPath();
                Log.d("---", path);
                setContentView(R.layout.activity_registerquizset);
                et_filepath = findViewById(R.id.et_filepath);
                et_filepath.setEnabled(false);
                et_filepath.setText(path);

            }
        }
    }
    //    public void createQuizset(View view) {
//        String read = ReadTextFile(filePath);
//        setContentView(R.layout.activity_registerquizset);
//        detail = findViewById(R.id.detail);
//        detail.setText(read);
//        pdfGo();
//
//
//    }
//
//    void pdfGo() {
//        ///권한확인
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//            /////권한 추가
//            ActivityCompat.requestPermissions(this, new String[]{
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            }, 1);
//        }
//
//        ///SDK 버전 문제
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//
//        String sdCardDir = Environment.getExternalStorageDirectory().getAbsolutePath();
//        System.out.println(sdCardDir);
//        String fileName = "2_03.pdf";
//        String filePath = sdCardDir + File.separator + fileName;
//
//        Uri uri = Uri.fromFile(new File(filePath));
//        //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "application/pdf");
//        startActivity(intent);
//
//    }
//
//    //경로의 텍스트 파일읽기
//    public String ReadTextFile(String path) {
//        StringBuffer strBuffer = new StringBuffer();
//        try {
//            InputStream is = new FileInputStream(path);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                strBuffer.append(line + "\n");
//            }
//            reader.close();
//            is.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        }
//        return strBuffer.toString();
//    }
}
