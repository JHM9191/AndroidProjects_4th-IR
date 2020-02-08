package com.example.aiqu;

import java.util.Date;

public class QuizsetItem {
    String filename;
    String quizset_name;
    String subject;
    String img;
    Date data;

    public QuizsetItem() {

    }

    public QuizsetItem(String filename, String quizset_name, String subject, String img, Date data) {
        this.filename = filename;
        this.quizset_name = quizset_name;
        this.subject = subject;
        this.img = img;
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getQuizset_name() {
        return quizset_name;
    }

    public void setQuizset_name(String quizset_name) {
        this.quizset_name = quizset_name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "QuizsetItem{" +
                "filename='" + filename + '\'' +
                ", quizset_name='" + quizset_name + '\'' +
                ", subject='" + subject + '\'' +
                ", img='" + img + '\'' +
                ", data=" + data +
                '}';
    }
}
