package com.example.coronaMapProject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHandler {

    public static String getString(String urlstr) {
        String result = null;
        URL url = null;
        HttpURLConnection hcon = null;
        InputStream is = null;
        try {
            url = new URL(urlstr);
            hcon = (HttpURLConnection) url.openConnection(); // 해당하는 url에 접속한다.
            hcon.setConnectTimeout(2000); // 2초 동안 응답이 없으면 접속을 끊어 버려라 라는 의미임.
            hcon.setRequestMethod("GET");
            is = new BufferedInputStream(hcon.getInputStream()); // 입력 데이터를 받기 위한 Reader 객체 생성하기
            result = convertStr(is); // is 로 서버로부터 응답을 받을검.
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String convertStr(InputStream is) {
        BufferedReader bi = null;
        StringBuilder sb = new StringBuilder();
        try {                                                   // InputStreamReader를 Buffer에 한번 filtering 한다.
            bi = new BufferedReader(new InputStreamReader(is)); // Buffer를 이용해서 빨리 읽어오겠다는 것.
            String temp = "";
            while ((temp = bi.readLine()) != null) {
                sb.append(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}