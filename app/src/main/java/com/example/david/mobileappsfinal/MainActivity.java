package com.example.david.mobileappsfinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView scoresText;
    TextView datesText;
    Button startGameButton;

    public static ArrayList<ScoreData> scores = new ArrayList<ScoreData>();
    public static final String scoresFilename = "scores.data";
    /*File directory = getBaseContext().getFilesDir();
    File saveFile;*/

    public final static String PREVIOUS_BEST = "previousBestKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getBaseContext().getFileStreamPath(scoresFilename).exists()) {

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
