package com.example.aiqu.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiqu.HomeActivity;
import com.example.aiqu.Question;
import com.example.aiqu.Quiz;
import com.example.aiqu.QuizsetItem;
import com.example.aiqu.R;
import com.example.aiqu.Summary;
import com.example.aiqu.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        for (int i = 0; i < count; i++) {
            QuizsetItem quizset = new QuizsetItem();
            quizset.setQuizset_name("hello" + i);
            quizset.setSubject("math" + i);
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
        public View getView(int position, View converView, ViewGroup parent) {
            final ViewHolder holder;
            if (converView == null) {
                converView = myInflater.inflate(R.layout.item_quizset, null);
                holder = new ViewHolder();
                holder.name = (TextView) converView.findViewById(R.id.tv_item_quizset_name);
                holder.subject = (TextView) converView.findViewById(R.id.tv_item_quizset_subject);

                converView.setTag(holder);
            } else {
                holder = (ViewHolder) converView.getTag();
            }
            // SharedPreferences에 있는 data로 리스트뷰 값 세팅하기.
            SharedPreferences sp = getActivity().getSharedPreferences("sp", Context.MODE_PRIVATE);
            String data = "";
            if (sp != null && sp.contains("data")) {
                data = sp.getString("data", "");
                Toast.makeText(getActivity(), sp.getString("data", "None"), Toast.LENGTH_SHORT).show();
            }

            try {
                JSONObject jo_userInfo = new JSONObject(data);
                User user = new User();
                ArrayList<Quiz> quizlist = new ArrayList<>();
                // user
                JSONObject jo_user = jo_userInfo.getJSONObject("user");
                user.setId(jo_user.getString("id"));
                user.setPw(jo_user.getString("pw"));
                user.setName(jo_user.getString("name"));
                user.setEmail(jo_user.getString("email"));
                Log.d("---", user.toString());

                // quizlist
                JSONArray ja_quizlist = jo_userInfo.getJSONArray("quizlist");
                Log.d("===", "ja_quizlist.length(): " + ja_quizlist.length());
                for (int j = 0; j < ja_quizlist.length(); j++) {
                    JSONObject jo_quiz = ja_quizlist.getJSONObject(j);

                    //
                    String quiz_name = jo_quiz.getString("quiz_name");

                    //
                    JSONArray ja_quiz_set = jo_quiz.getJSONArray("quiz_set");
                    ArrayList<Question> question_list = new ArrayList<>();
                    for (int k = 0; k < ja_quiz_set.length(); k++) {
                        JSONObject jo_question = ja_quiz_set.getJSONObject(k);

                        String n = jo_question.getString("#");
                        String q = jo_question.getString("question");
                        String qt = jo_question.getString("question_type");

//                        JSONObject jo_selections = (JSONObject) jo_question.get("selections");
//                        String strrr = jo_question.getString("selections");
//                        JSONArray jasdf = new JSONArray(strrr);

//                        Log.d("===", "string : " + strrr);
//                        String str = jo_question.getJSONObject("selections").toString();
//                        Log.d("===", "str : " + str);
                        JSONArray ja_selections = jo_question.getJSONArray("selections");
//                        JSONArray ja_selections =  (JSONArray) jo_question.getJSONArray("selections");

                        String[] selections = new String[ja_selections.length()];
                        for (int p = 0; p < ja_selections.length(); p++) {
                            selections[p] = ja_selections.get(p) + "";
                        }
                        String answer = jo_question.getString("answer");
                        Question question = new Question(n, q, qt, selections, answer);
                        question_list.add(question);
                    }

                    //
                    JSONObject quiz_summary = jo_quiz.getJSONObject("quiz_summary");


                    Quiz quiz = new Quiz(quiz_name, question_list, null);

                    Log.d("===", quiz.toString());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.name.setText(lists.get(position).getQuizset_name() + "");
            holder.subject.setText(lists.get(position).getSubject() + "");

            converView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return converView;
        }
    }

    static class ViewHolder {
        TextView name, subject;
    }


}