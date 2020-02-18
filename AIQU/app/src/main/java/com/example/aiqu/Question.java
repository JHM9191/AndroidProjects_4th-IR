package com.example.aiqu;

import java.util.Arrays;

public class Question {
    String number;
    String question;
    String type;
    String[] selections;
    String answer;

    public Question() {
    }

    public Question(String number, String question, String type, String[] selections, String answer) {
        this.number = number;
        this.question = question;
        this.type = type;
        this.selections = selections;
        this.answer = answer;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getSelections() {
        return selections;
    }

    public void setSelections(String[] selections) {
        this.selections = selections;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Question{" +
                "number='" + number + '\'' +
                ", question='" + question + '\'' +
                ", type='" + type + '\'' +
                ", selections=" + Arrays.toString(selections) +
                ", answer='" + answer + '\'' +
                '}';
    }
}
