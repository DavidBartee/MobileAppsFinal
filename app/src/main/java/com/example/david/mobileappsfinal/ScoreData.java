package com.example.david.mobileappsfinal;

import java.util.Calendar;
import java.util.Date;

public class ScoreData {

    int score;
    String dateAndTime;

    public ScoreData(int score) {
        this.score = score;
        Calendar cal = Calendar.getInstance();
        dateAndTime = (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE) + "/" + cal.get(Calendar.YEAR);
    }
    public ScoreData(int score, String date) {
        this.score = score;
        dateAndTime = date;
    }
}
