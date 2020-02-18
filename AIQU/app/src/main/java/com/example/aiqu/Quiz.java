package com.example.aiqu;

import java.util.ArrayList;

public class Quiz {
    String name;
    ArrayList<Question> questionList;
    Summary summary;

    public Quiz() {
    }

    public Quiz(String name, ArrayList<Question> questionList, Summary summary) {
        this.name = name;
        this.questionList = questionList;
        this.summary = summary;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(ArrayList<Question> questionList) {
        this.questionList = questionList;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "name='" + name + '\'' +
                ", questionList=" + questionList +
                ", summary=" + summary +
                '}';
    }
}
