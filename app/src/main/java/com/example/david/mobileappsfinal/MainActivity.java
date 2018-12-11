package com.example.david.mobileappsfinal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView scoresText;
    TextView datesText;
    Button startGameButton;

    public static ArrayList<ScoreData> scores = new ArrayList<ScoreData>();
    public static final String scoresFilename = "scores.data";
    FileOutputStream outputStream;

    public final static String PREVIOUS_BEST = "previousBestKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getBaseContext().getFileStreamPath(scoresFilename).exists()) {
            try {
                FileInputStream inputStream = openFileInput(scoresFilename);
                ObjectInputStream oos = new ObjectInputStream(inputStream);
                scores = (ArrayList<ScoreData>) oos.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Intent previousGameIntent = getIntent();
        if (previousGameIntent != null) {
            scores.add(new ScoreData(previousGameIntent.getIntExtra(GameActivity.NEW_BEST, 0)));
        }
        sortScores();
        if (scores.size() > 10) {
            for (int i = scores.size() - 1; i > 9; i--) {
                scores.remove(i);
            }
        }
        for (int i = scores.size() - 1; i >= 0; i--) {
            if (scores.get(i).score == 0) {
                scores.remove(i);
            }
        }

        try {
            outputStream = openFileOutput(scoresFilename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(scores);
            oos.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        scoresText = findViewById(R.id.scoresText);
        datesText = findViewById(R.id.datesText);
        if (scores.size() > 0) {
            String allScores = "";
            String allDates = "";
            for (int i = 0; i < scores.size(); i++) {
                allScores += scores.get(i).score;
                allDates += scores.get(i).dateAndTime;
                if (i < 9) {
                    allScores += "\n";
                    allDates += "\n";
                }
            }
            scoresText.setText(allScores);
            datesText.setText(allDates);
        } else {
            scoresText.setText("There are no high scores yet!");
            datesText.setText("");
        }

        startGameButton = findViewById(R.id.startGameButton);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent (MainActivity.this, GameActivity.class);
                gameIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (scores.size() > 0)
                    gameIntent.putExtra(PREVIOUS_BEST, scores.get(0).score);
                else
                    gameIntent.putExtra(PREVIOUS_BEST, 0);
                startActivity(gameIntent);
                finish();
            }
        });
    }

    protected void sortScores() {
        for (int i = 0; i < scores.size() - 1; i++) {
            for (int j = 0; j < scores.size() - i - 1; j++) {
                if (scores.get(j) == null || scores.get(j + 1) == null)
                    continue;
                if (scores.get(j).score < scores.get(j + 1).score) {
                    ScoreData temp = scores.get(j);
                    scores.set(j, scores.get(j + 1));
                    scores.set(j + 1, temp);
                }
            }
        }
    }
}
