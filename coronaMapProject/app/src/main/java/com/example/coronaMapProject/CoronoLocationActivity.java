package com.example.coronaMapProject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CoronoLocationActivity extends AppCompatActivity {

    ListView listView;
    LinearLayout container;
    ArrayList<CoronaLocation> list;
    LocationAdapter locationAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        listView = findViewById(R.id.lv_location);
        container = findViewById(R.id.container_location);
        list = new ArrayList<>();
        getData();
    }

    private void getData() {
        Intent intent = getIntent();
        list = (ArrayList<CoronaLocation>) intent.getSerializableExtra("locationListSelected");
        Log.d("---", list.size() + "");
        for(int i = 0; i < list.size(); i++) {
            Log.d("---", list.get(i).getName() + " "
                    + list.get(i).getName() + " "
                    + list.get(i).getLat() + " "
                    + list.get(i).getLon() + " "
            );
        }
        locationAdapter = new LocationAdapter(list);
        listView.setAdapter(locationAdapter);
    }


    class LocationAdapter extends BaseAdapter {
        ArrayList<CoronaLocation> alist;

        public LocationAdapter(ArrayList<CoronaLocation> alist) {
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

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = null;
            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.location, container, true);
            TextView no = itemView.findViewById(R.id.tv_location_no);
            TextView name = itemView.findViewById(R.id.tv_location_name);
            TextView lat = itemView.findViewById(R.id.tv_location_lat);
            TextView lon = itemView.findViewById(R.id.tv_location_lon);

            no.setText(alist.get(position).getNo() + "");
            name.setText(alist.get(position).getName());
            lat.setText(alist.get(position).getLat() + "");
            lon.setText(alist.get(position).getLon() + "");

            return itemView;
        }
    }

//    public void bt_map(View v) {
//        Intent intent = new Intent(getApplicationContext(), CoronaMapActivity.class);
//        intent.putExtra("locationListSelected", list);
//        startActivity(intent);
//    }
}
