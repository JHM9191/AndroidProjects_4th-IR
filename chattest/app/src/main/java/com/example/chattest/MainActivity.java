package com.example.chattest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import tcpip2.Msg;

public class MainActivity extends AppCompatActivity {

    EditText editText,editText2,editText3,editText4;
    TextView textView,textView2;

    Socket socket;
    Sender sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeUi();

    }

    private void makeUi() {
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
    }

    class ConnectThread extends Thread{

        String ip;
        int port;

        public ConnectThread(){

        }
        public ConnectThread(String ip, int port){
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                socket = new Socket(ip, port);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(ip+"Connected \n"+textView.getText());
                    }
                });

            }catch(Exception e) {
                while(true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(ip+"Retry \n"+textView.getText());
                        }
                    });
                    try {
                        Thread.sleep(1000);
                        socket = new Socket(ip, port);
                        break;
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
            try {
                sender = new Sender(socket);
                Receiver receiver = new Receiver(socket);
                receiver.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    class Sender implements Runnable{

        OutputStream os;
        ObjectOutputStream oos;
        Msg msg;

        public Sender(Socket socket) throws IOException {
            os = socket.getOutputStream();
            oos = new ObjectOutputStream(os);
        }
        public void setMsg(Msg msg) {
            this.msg = msg;
        }
        @Override
        public void run() {

            if(oos != null) {
                try {
                    oos.writeObject(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    class Receiver extends AsyncTask<Void,Msg,Void>{
        InputStream is;
        ObjectInputStream ois;

        public Receiver(Socket socket) throws IOException {
            is = socket.getInputStream();
            ois = new ObjectInputStream(is);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (ois != null) {
                Msg msg = null;
                try {
                    msg = (Msg) ois.readObject();
                    publishProgress(msg);
                } catch (Exception e) {
                    msg = new Msg("System","Server disconnected",null);
                    publishProgress(msg);
                    break;
                }
            }
            return  null;
        }

        @Override
        protected void onProgressUpdate(Msg... values) {
            if(values[0].getIps() == null || values[0].getIps().size() == 0){
                String txt = values[0].getId()+":"+values[0].getMsg();
                textView.setText(txt+"\n"+textView.getText());
            }else{
                ArrayList<String> list = values[0].getIps();
                String str = "";
                for(String ip:list){
                    str += ip+"\n";
                }
                textView2.setText(str);
            }






        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                if (ois != null)
                    ois.close();

                if (socket != null)
                    socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void ckbt(View v) throws IOException {
        if(v.getId() == R.id.button_connect){
            String ip =null;
            int port = 0;
            try {
                ip = editText.getText().toString();
                port = Integer.parseInt(editText2.getText().toString());
                new ConnectThread(ip,port).start();
            }catch(Exception e){
                Toast.makeText(this, "올바른 ip 주소와 포트 번호를 입력하세요", Toast.LENGTH_SHORT).show();
            }

        }else if(v.getId() == R.id.button_send){
            String ip = editText3.getText().toString();
            String txt = editText4.getText().toString();
            Msg msg = null;
            if(ip == null || ip.equals("")){
                msg = new Msg("ydh",txt,null);
            }else{
                msg = new Msg("ydh",txt,ip);
            }
            sender.setMsg(msg);
            new Thread(sender).start();
            editText4.setText("");


        }else if(v.getId()==R.id.button_disconnect){
            Msg msg =null;
            msg = new Msg("ydh","q",null);
            sender.setMsg(msg);
            socket.close();
        }else if(v.getId()==R.id.button_reset){
            editText.setText("");
            editText2.setText("");
            textView.setText("");
            textView2.setText("");

            Msg msg =null;
            msg = new Msg("ydh","q",null);
            sender.setMsg(msg);
            socket.close();
        }
    }
}



