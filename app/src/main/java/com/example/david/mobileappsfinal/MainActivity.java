package com.example.david.mobileappsfinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView scoreboardText;
    Button startGameButton;

    public static ArrayList<ScoreData> scores = new ArrayList<ScoreData>();
    public static final String scoresFilename = "scores.data";
    File directory = getBaseContext().getFilesDir();
    File saveFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getBaseContext().getFileStreamPath(scoresFilename).exists()) {

        }

        scoreboardText = findViewById(R.id.scoreboardText);
        scoreboardText.setText("There are no high scores yet!");

        startGameButton = findViewById(R.id.startGameButton);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent (MainActivity.this, GameActivity.class);
                startActivity(gameIntent);
            }
        });
    }
}
