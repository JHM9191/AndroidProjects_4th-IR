package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import tcpip2.Msg;


public class SecondActivity extends AppCompatActivity {


    TextView textView;
    EditText editText, editText2;
    ListView listView;
    ArrayList<String> ids;
    ArrayAdapter<String> adapter;
    String id,ip;
    int port;
    Socket socket;
    Sender sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        makeUi();
        Intent intent = getIntent();
        ip = intent.getStringExtra("ip");
        port = Integer.parseInt(intent.getStringExtra("port"));
        id = intent.getStringExtra("id");

    }

    private void makeUi() {
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tid = ids.get(position);
                editText.setText(tid);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("---", "onStart");
        new ConnectThread(ip, port, id).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("---", "onResume");
        restoreState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("---", "onPause");
        saveState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("---", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("---", "onDestroy");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void restoreState() {
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
//        if((pref != null) && (pref.contains("ip")) && (pref.contains("port"))){
//            String ip = pref.getString("ip","");
//            String port = pref.getString("port","");
//            editText.setText(ip);
//            editText2.setText(port);
//        }
    }

    protected void saveState() {
//        SharedPreferences pref = getSharedPreferences("pref",Activity.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("ip",editText.getText().toString());
//        editor.putString("port",editText2.getText().toString());
//        editor.commit();
    }

    protected void clearState() {
//        SharedPreferences pref = getSharedPreferences("pref",Activity.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.clear();
//        editor.commit();
    }

    public void ckbt(View v) {
        String tid = editText.getText().toString();
        String txt = editText2.getText().toString();
        tid.trim();
        Msg msg = null;
        if (tid == null || tid.equals("")) {
            if (txt != null || txt.equals("")) {
                msg = new Msg(id, txt, null);
            } else {
                return;
            }
        } else {
            if (txt != null || txt.equals("")) {
                msg = new Msg(id, txt, tid);
            } else {
                return;
            }
        }
        sender.setMsg(msg);
        new Thread(sender).start();
    }


    class ConnectThread extends Thread {

        String ip;
        int port;
        String id;

        public ConnectThread() {

        }

        public ConnectThread(String ip, int port, String id) {
            this.ip = ip;
            this.port = port;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                socket = new Socket(ip, port);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(ip + "Connected \n" + textView.getText());
                    }
                });

            } catch (Exception e) {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(ip + "Retry \n" + textView.getText());
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

                Msg msg = new Msg(id, null, null);
                sender.setMsg(msg);
                new Thread(sender).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Sender implements Runnable {

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

            if (oos != null) {
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
                    msg = new Msg("System", "Server disconnected", null);
                    publishProgress(msg);
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Msg... values) {
            if (values[0].getIps() == null || values[0].getIps().size() == 0) {
                String txt = values[0].getId() + ":" + values[0].getMsg();
                textView.setText(txt + "\n" + textView.getText());
            } else {
                ids = values[0].getIps();
                adapter = new ArrayAdapter<String>(SecondActivity.this, android.R.layout.simple_list_item_1, ids);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);


            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                if (ois != null)
                    ois.close();

                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
