package com.example.chatclient2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import tcpip2.Msg;


public class MainActivity extends AppCompatActivity {

    EditText editText, editText2, editText3, editText4; //
    Button button, button2;
    TextView textView,textView2;

    Socket socket;
    Sender sender; // socket이 만들어진 다음에 만들어야함.

    ConnectThread ct;

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

    class ConnectThread extends Thread {

        String ip;
        int port;

        public ConnectThread(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                socket = new Socket(ip, port);
                textView.setText(ip + " Connected \n"+textView.getText());

            }catch(Exception e) {
                while(true) {
                    Log.e("---","Retry...");
                    textView.setText(ip + " Retry... \n" + textView.getText());
                    try {
                        Thread.sleep(1000);
                        socket = new Socket(ip, port);
                        break;
                    } catch (Exception e1) {
                        Log.e("---", "Unable to connect. Check ip or port");
                    textView.setText(ip + " Unable to connect. Check ip or port \n" + textView.getText());
                        //e1.printStackTrace();
                    }
                }
            }
            // run()이 끝나면 소켓이 만들어진다.
            // 여기에서 sender를 만들어준다.
            try {
                sender = new Sender(socket);
                new Receiver(socket).execute();
//                if (new ObjectInputStream(socket.getInputStream()).readObject() instanceof HashMap) {
//                    Log.d("---", "entered if()");
//                    HashMap<String, ObjectOutputStream> maps = (HashMap<String, ObjectOutputStream>) new ObjectInputStream(socket.getInputStream()).readObject();
//                    ArrayList ipList = new ArrayList<String>();
//                    Set<String> keys = maps.keySet();
//                    Iterator<String> skeys = keys.iterator();
//
//                    while(skeys.hasNext()) {
//                        String ip = skeys.next();
//                        ipList.add(ip);
//                        Log.d("---", ip + "");
//                        textView2.setText(textView2.getText() + "\n" + ip);
//                    }
//                }

                    textView2.setText("");
//                String ip = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
                    String ip = new DataInputStream(socket.getInputStream()).readUTF();
                    Log.d("---1", ip  + "");
                if (ip instanceof String) {
                    Log.d("---2", ip  + "");
                    textView2.setText(ip + "\n" + textView2.getText());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } // end run()

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

    class Receiver extends AsyncTask<Void, Msg, Void> {
        InputStream is;
        ObjectInputStream ois;
        Msg msg = null;

        public Receiver(Socket socket) {
            try {
                is = socket.getInputStream();
                ois = new ObjectInputStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
                while (ois != null) {
            try {
                if (ois.readObject() instanceof Msg) {
                    msg = (Msg) ois.readObject();
                    Log.d("---", msg.getMsg()+"");
                    publishProgress(msg);
                }
            } catch (IOException e) {
                Log.e("---", "ois.readObject() error");
                msg = new Msg("System", "Server Dead(IOException)", null, null);
                publishProgress(msg);
                e.printStackTrace();
                break;
            } catch (ClassNotFoundException e) {
                Log.e("---", "ois.readObject() error");
                msg = new Msg("System", "Server Dead(ClassNotFoundException)", null, null);
                e.printStackTrace();
                break;
            }
                }
            return null;
        }

        @Override
        protected void onProgressUpdate(Msg... msgs) {
            Msg msg = msgs[0];
            textView.setText(msg.getId() + " : " + msg.getMsg() + "\n" + textView.getText());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        try {
                if(ois != null) {
                    ois.close();
                }
                if(socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                Log.e("---", "ois or socket improperly closed.");
                e.printStackTrace();
            }
        }
    }

    public void ckbt(View v) {
        if(v.getId() == R.id.button) { // CONNECT button
            String ip = editText.getText().toString();
            int port = Integer.parseInt(editText2.getText().toString());
            ct = new ConnectThread(ip,port);
            ct.start();
        } else if(v.getId() == R.id.button2) { // SEND button
            String ip = editText3.getText().toString();
            String txt = editText4.getText().toString();
            Msg msg = null;
            if (ip == null || ip.equals("")) {
                msg = new Msg("jhm", txt, null, null);
            } else {
                msg = new Msg("jhm", txt, ip, null);
            }
            sender.setMsg(msg);
            new Thread(sender).start();
        } else if(v.getId() == R.id.button3) { // DISCONNECT button
            Msg msg = null;
            msg = new Msg("jhm", "q", null, null);
            sender.setMsg(msg);
//            new Thread(sender).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
