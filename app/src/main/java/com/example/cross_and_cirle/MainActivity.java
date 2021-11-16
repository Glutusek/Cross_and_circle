package com.example.cross_and_cirle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean X_move;
    boolean gameStarted = false;

    Button [][] tilesArray = new Button[3][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("com.example.cross_and_circle", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        /*
            Pobieranie
            sharedPreferences.getInt("nazwa_komorki", defaultowa wartosc);

            Wpisywanie
            editor.putInt("nazwa_komorki", wartosc);
            editor.commit();
         */

        for(int i = 0; i < 9; i++) {
            String str_tileID = "tile_" + (i+1);
            int tileID = getResources().getIdentifier(str_tileID, "id", getPackageName());
            tilesArray[i/3][i%3] = findViewById(tileID);
            tilesArray[i/3][i%3].setEnabled(false);
        }
    }

    public void newGame(View view) {
        X_move = sharedPreferences.getBoolean("X_won", true);

        for(int i = 0; i < 9; i++){
            tilesArray[i/3][i%3].setText("");
            tilesArray[i/3][i%3].setEnabled(true);
        }

        gameStarted = true;
    }

    public void click(View view) {
        if(gameStarted) {
            Button pressedButton = findViewById(view.getId());

            if(pressedButton.getText().equals("")) {
                if(X_move) {
                    pressedButton.setText("X");

                    if(winCheck(X_move)) {
                        gameStarted = false;
                        Toast.makeText(getApplicationContext(), "Wygrana gracza X!", Toast.LENGTH_SHORT).show();
                    } else if(drawCheck()) {
                        gameStarted = false;
                        Toast.makeText(getApplicationContext(), "Remis!", Toast.LENGTH_SHORT).show();
                    }

                    X_move = false;
                } else {
                    pressedButton.setText("O");

                    if (winCheck(X_move)) {
                        gameStarted = false;
                        Toast.makeText(getApplicationContext(), "Wygrana gracza O!", Toast.LENGTH_SHORT).show();
                    } else if(drawCheck()) {
                        gameStarted = false;
                        Toast.makeText(getApplicationContext(), "Remis!", Toast.LENGTH_SHORT).show();
                    }

                    X_move = true;
                }
            }

        } else {
            Toast.makeText(getApplicationContext(), "Najpierw zacznij grę!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean winCheck(boolean X_move) {
        String [] winningConditions = {
            "123",
            "456",
            "789",
            "147",
            "258",
            "369",
            "159",
            "357"
        };

        boolean win = false;
        
        for(String condition: winningConditions) {
            char [] tiles = condition.toCharArray();
            win = true;
            
            for(char tile: tiles) {
                int ID = getResources().getIdentifier("tile_"+ tile, "id", getPackageName());
                Button tempButton = findViewById(ID);

                if(X_move && (tempButton.getText().equals("O") || tempButton.getText().equals(""))) {
                    win = false;
                    break;
                }

                if(!X_move && (tempButton.getText().equals("X") || tempButton.getText().equals(""))) {
                    win = false;
                    break;
                }
            }
            
            if(win)
                break;
        }
        
        return win;
    }

    private boolean drawCheck() {
        for(int i = 0; i < 9; i++) {
            if(!tilesArray[i/3][i%3].getText().equals("")) {
                return false;
            }
        }
        return true;
    }
}