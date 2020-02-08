package com.example.p427;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Item> lists = new ArrayList<>();
    LinearLayout container;
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        container = findViewById(R.id.container);
        getData();
        itemAdapter = new ItemAdapter(lists);
        listView.setAdapter(itemAdapter);

    }

    private void getData() {
        lists.add(new Item(R.drawable.p1,"이말숙","010-0000-0000"));
        lists.add(new Item(R.drawable.p2,"김말숙","010-1111-0000"));
        lists.add(new Item(R.drawable.p3,"정말숙","010-2222-0000"));
        lists.add(new Item(R.drawable.p4,"황말숙","010-3333-0000"));
        lists.add(new Item(R.drawable.p5,"홍말숙","010-4444-0000"));
        lists.add(new Item(R.drawable.p1,"장말숙","010-5555-0000"));
        lists.add(new Item(R.drawable.p2,"허말숙","010-6666-0000"));
        lists.add(new Item(R.drawable.p3,"강말숙","010-7777-0000"));
        lists.add(new Item(R.drawable.p4,"임말숙","010-8888-0000"));
        lists.add(new Item(R.drawable.p5,"강말숙","010-9999-0000"));

    }

    class ItemAdapter extends BaseAdapter {
        ArrayList<Item> lists;

        public ItemAdapter(){

        }
        public ItemAdapter(ArrayList<Item> lists){
            this.lists = lists;
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = null;
            LayoutInflater inflater =
                    (LayoutInflater)getSystemService(
                            Context.LAYOUT_INFLATER_SERVICE
                    );
            itemView = inflater.inflate(
                    R.layout.item_layout,
                    container,
                    true
            );
            ImageView
               img = itemView.findViewById(R.id.imageView);
            TextView
                    name = itemView.findViewById(R.id.textView);
            TextView
                    phone = itemView.findViewById(R.id.textView2);

            Item item = lists.get(position);
            img.setImageResource(item.getImg());
            name.setText(item.getName());
            phone.setText(item.getPhone());

            return itemView;
        }
    }

}






