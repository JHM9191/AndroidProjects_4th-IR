package com.example.coronaMapProject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class SecondActivity extends AppCompatActivity {
    ListView listView;
    LinearLayout container;
    ArrayList<Item> list;
    ItemAdapter itemAdapter;
    ProgressDialog progressDialog;
    ArrayList<CoronaLocation> maplist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        listView = findViewById(R.id.listView);
        container = findViewById(R.id.container);
        list = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        getData();


    }

    private void getData() {
        String url = "http://13.209.177.31/webview/corona.jsp";
        ItemAsync itemAsync = new ItemAsync(url);
        itemAsync.execute();
    }


    class ItemAsync extends AsyncTask<Void, Void, String> {
        String url;

        public ItemAsync(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setTitle("HTTP Connect ..");
            progressDialog.setMessage("Please Wait..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            result = HttpHandler.getString(url);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("---", s.trim());

            SharedPreferences sp = getSharedPreferences("sp", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("data", s);
            editor.commit();

            progressDialog.dismiss();
            JSONArray ja = null;
            try {
                ja = new JSONArray(s);
                JSONArray ja_location = null;
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    String id = jo.getString("id");
                    String name = jo.getString("name");
                    String sex = jo.getString("sex");
                    int age = jo.getInt("age");
                    String nationality = jo.getString("nationality");
                    int meet = jo.getInt("meet");
                    int risk = jo.getInt("risk");
                    String img = jo.getString("img");
                    ja_location = jo.getJSONArray("location");

                    maplist = new ArrayList<>();
                    for (int j = 0; j < ja_location.length(); j++) {
                        JSONObject jo_location = ja_location.getJSONObject(j);
                        int no = jo_location.getInt("no");
                        String name_location = jo_location.getString("name");
                        Double lat = jo_location.getDouble("lat");
                        Double lon = jo_location.getDouble("lon");
                        CoronaLocation map = new CoronaLocation(no, name_location, lat, lon, name);
//                        Log.v("====", "SecondActivity : maplist.toString() : " + maplist.toString());
                        maplist.add(map);
                    }
                        Item item = new Item(id, name, sex, age, nationality, meet, risk, img, maplist);
//                        Log.v("====", "SecondActivity : item.getArrayList().toString() : " + item.getArrayList().toString());
                        list.add(item);
//                        Log.v("====", "SecondActivity : list.get(i).getArrayList().toString() : " + list.get(i).getArrayList().toString());
                }
                itemAdapter = new ItemAdapter(list);
                listView.setAdapter(itemAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ArrayList<CoronaLocation> alist_location = list.get(position).getArrayList();
//                        Log.v("====", "SecondActivity : onItemClick : position : " + position + "");
//                        Log.v("====", "SecondActivity : onItemClick : position : list.get(position).getName() : " + list.get(position).getName());
//                        Log.v("====", "SecondActivity : onItemClick : position : list.get(position).getArrayList() : " + list.get(position).getArrayList());
//                        Log.v("====", "SecondActivity : onItemClick : alist_location.toString() : " + alist_location.toString() + "");
                        Toast.makeText(SecondActivity.this, alist_location.get(0).getPatientName() + "의 맵으로 이동", Toast.LENGTH_SHORT).show();
                        Intent intent_location = new Intent(getApplicationContext(), CoronaMapActivity.class);
                        intent_location.putExtra("locationListSelected", alist_location);
                        startActivity(intent_location);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ItemAdapter extends BaseAdapter {
        ArrayList<Item> alist;
        String clickno;
        String clickname;

        public ItemAdapter(ArrayList<Item> alist) {
            this.alist = alist;
        }

        @Override
        public int getCount() {
            return alist.size();
        }

        @Override
        public Object getItem(int position) {
            return alist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = null;
            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.item, container, true);
            TextView id = itemView.findViewById(R.id.tv_id);
            TextView name = itemView.findViewById(R.id.tv_name);
            TextView sex = itemView.findViewById(R.id.tv_sex);
            TextView age = itemView.findViewById(R.id.tv_age);
            TextView nat = itemView.findViewById(R.id.tv_nat);
            ProgressBar progressBar = itemView.findViewById(R.id.progressBar);
            TextView meet = itemView.findViewById(R.id.tv_meet);
//            RatingBar risk = itemView.findViewById(R.id.rb_risk);


            final ImageView imageView = itemView.findViewById(R.id.imageView);

            id.setText(alist.get(position).getId() + "번째 확진자");
            name.setText("성함: " + alist.get(position).getName());
            sex.setText("성별: " + alist.get(position).getSex());
            age.setText("나이: " + alist.get(position).getAge() + "세");
            nat.setText("국적: " + alist.get(position).getNationality());

            progressBar.setMax(200);
            progressBar.setProgress(alist.get(position).getMeet(), true);
            meet.setText("접촉자수: " + alist.get(position).getMeet() + " 명");

            clickno = alist.get(position).getId();
            clickname = alist.get(position).getName();

            String img = alist.get(position).getImg();

            // 서버에 이미지 파일 요청하기
            img = "http://13.209.177.31/webview/img/" + img;

            final String finalImg = img;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    URL url = null;
                    InputStream is = null;
                    try {
                        url = new URL(finalImg); // url 접속됨.
                        is = url.openStream(); // 받아온다.
                        final Bitmap bm = BitmapFactory.decodeStream(is);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bm);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();

//            risk.setMax(5);
//            risk.setRating((float) alist.get(position).getRisk());
//            risk.setClickable(false);
//            risk.setFocusable(false);


            return itemView;
        }
    }

    public void click_mapAll(View v) {
        Intent intent = new Intent(getApplicationContext(), CoronaMapAllActivity.class);
        intent.putExtra("itemListSelected", list);
        startActivity(intent);
    }
}