package com.example.aiqu;

import java.util.ArrayList;

public class Summary {
    ArrayList<Integer> answered;
    ArrayList<Integer> unanswered;
    ArrayList<Integer> correct;
    ArrayList<Integer> inccorect;

    public Summary() {
    }

    public Summary(ArrayList<Integer> answered, ArrayList<Integer> unanswered, ArrayList<Integer> correct, ArrayList<Integer> inccorect) {
        this.answered = answered;
        this.unanswered = unanswered;
        this.correct = correct;
        this.inccorect = inccorect;
    }

    public ArrayList<Integer> getAnswered() {
        return answered;
    }

    public void setAnswered(ArrayList<Integer> answered) {
        this.answered = answered;
    }

    public ArrayList<Integer> getUnanswered() {
        return unanswered;
    }

    public void setUnanswered(ArrayList<Integer> unanswered) {
        this.unanswered = unanswered;
    }

    public ArrayList<Integer> getCorrect() {
        return correct;
    }

    public void setCorrect(ArrayList<Integer> correct) {
        this.correct = correct;
    }

    public ArrayList<Integer> getInccorect() {
        return inccorect;
    }

    public void setInccorect(ArrayList<Integer> inccorect) {
        this.inccorect = inccorect;
    }

    @Override
    public String toString() {
        return "Summary{" +
                "answered=" + answered +
                ", unanswered=" + unanswered +
                ", correct=" + correct +
                ", inccorect=" + inccorect +
                '}';
    }
}
