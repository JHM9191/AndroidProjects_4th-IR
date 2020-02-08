package com.example.coronaMapProject;

import java.io.Serializable;
import java.util.ArrayList;

public class Item implements Serializable {
    String id;
    String name;
    String sex;
    int age;
    String nationality;
    int meet;
    double risk;
    String img;
    ArrayList<CoronaLocation> arrayList;

    public Item() {
    }

    public Item(String id, String name, String sex, int age, String nationality, int meet, double risk, String img, ArrayList<CoronaLocation> arrayList) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.nationality = nationality;
        this.meet = meet;
        this.risk = risk;
        this.img = img;
        this.arrayList = arrayList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public int getMeet() {
        return meet;
    }

    public void setMeet(int meet) {
        this.meet = meet;
    }

    public double getRisk() {
        return risk;
    }

    public void setRisk(int risk) {
        this.risk = risk;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setRisk(double risk) {
        this.risk = risk;
    }

    public ArrayList<CoronaLocation> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<CoronaLocation> arrayList) {
        this.arrayList = arrayList;
    }
}
