package com.example.david.mobileappsfinal;

import java.util.Calendar;
import java.util.Date;

public class ScoreData {

    int score;
    String dateAndTime;

    public ScoreData(int score) {
        this.score = score;
        Date currentTime = Calendar.getInstance().getTime();
        dateAndTime = Calendar.MONTH + "/" + Calendar.DATE + "/" + Calendar.YEAR + " " + currentTime.toString();
    }
    public ScoreData(int score, String date) {
        this.score = score;
        dateAndTime = date;
    }
}
