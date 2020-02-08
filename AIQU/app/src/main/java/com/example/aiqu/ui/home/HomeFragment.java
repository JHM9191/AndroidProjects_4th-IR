package com.example.aiqu.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiqu.QuizsetItem;
import com.example.aiqu.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ArrayList<QuizsetItem> lists = new ArrayList<>();
    QuizsetItemAdapter quizsetItemAdapter;
    LinearLayout container;

    static int count;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        homeViewModel =
//                ViewModelProviders.of(this).get(HomeViewModel.class);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        ListView listView = root.findViewById(R.id.lv_quizset);
        Log.d("---", "1");
        getData();
        Log.d("---", "1");
        quizsetItemAdapter = new QuizsetItemAdapter(inflater, lists);
        Log.d("---", "1");
        listView.setAdapter(quizsetItemAdapter);
        Log.d("---", "1");
        return root;
    }


    private void getData() {
        count += 1;

        for(int i = 0; i < count; i++) {
            QuizsetItem quizset = new QuizsetItem();
            quizset.setQuizset_name("hello"+i);
            quizset.setSubject("math"+i);
            lists.add(quizset);
            Log.d("---", lists.get(i).toString() + "");
        }
    }

    class QuizsetItemAdapter extends BaseAdapter {

        ArrayList<QuizsetItem> quizsetItems;

        LayoutInflater myInflater;

        public QuizsetItemAdapter() {

        }

        public QuizsetItemAdapter(LayoutInflater inflater, ArrayList<QuizsetItem> quizsetItems) {
            this.myInflater = inflater;
            this.quizsetItems = quizsetItems;
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return quizsetItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = myInflater.inflate(R.layout.item_quizset, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.tv_item_quizset_name);
                holder.subject = (TextView) convertView.findViewById(R.id.tv_item_quizset_subject);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(lists.get(position).getQuizset_name() + "");
            holder.subject.setText(lists.get(position).getSubject() + "");
            return convertView;
        }
    }

    static class ViewHolder {
        TextView name, subject;
    }
}