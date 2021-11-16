package com.example.cross_and_cirle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Gra w kółko i krzyżyk.
 * @author Kamil Wieczorek
 * @version %I%,%G%
 */
public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean X_started;
    boolean X_move;
    boolean gameStarted = false;

    // Dwuwymiarowa tablica na przyciski
    Button [][] tilesArray = new Button[3][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("com.example.cross_and_circle", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        for(int i = 0; i < 9; i++) {
            String str_tileID = "tile_" + (i+1);
            int tileID = getResources().getIdentifier(str_tileID, "id", getPackageName());
            tilesArray[i/3][i%3] = findViewById(tileID);
            tilesArray[i/3][i%3].setEnabled(false);
        }
    }

    /**
     * Przygotowanie nowej gry
     * @param view Wciśnięty przycisk
     */
    public void newGame(View view) {
        // Pobranie wartości o ostatnim wygranym graczu z bazy danych
        X_started = sharedPreferences.getBoolean("X_won", true);
        X_move = X_started;

        // Wyczyszczenie i aktywacja przycisków
        for(int i = 0; i < 9; i++){
            tilesArray[i/3][i%3].setText("");
            tilesArray[i/3][i%3].setEnabled(true);
        }

        gameStarted = true;

        // Dezaktywacja przycisku nowej gry
        findViewById(R.id.newGameButton).setEnabled(false);
    }

    /**
     * Wykonanie czynności po wciśnięciu pola
     * @param view Wciśnięty przycisk
     */
    public void click(View view) {
        // Jeżeli gra jest rozpoczęta
        if(gameStarted) {
            // Pobranie odniesienia do wciśniętego przycisku
            Button pressedButton = findViewById(view.getId());

            // Jeżeli pole nie jest zajęte
            if(pressedButton.getText().equals("")) {
                // Jeżeli ruch gracza X
                if(X_move) {
                    // Wpisanie w pole znaku X
                    pressedButton.setText("X");

                    if(winCheck(X_move)) {
                        // Jeżeli gracz X wygrał

                        // Zakończenie gry z komunikatem
                        gameStarted = false;
                        Toast.makeText(getApplicationContext(), "Wygrana gracza X!", Toast.LENGTH_SHORT).show();

                        // Wprowadzanie wyranego gracza do bazy danych
                        editor.putBoolean("X_won", true);
                        editor.commit();

                        // Aktywacja przycisku nowej gry
                        findViewById(R.id.newGameButton).setEnabled(true);

                    } else if(drawCheck()) {
                        // Jeżeli remis

                        gameStarted = false;
                        Toast.makeText(getApplicationContext(), "Remis!", Toast.LENGTH_SHORT).show();

                        editor.putBoolean("X_won", !X_started);
                        editor.commit();

                        findViewById(R.id.newGameButton).setEnabled(true);
                    }

                    X_move = false;
                } else { // Jeżeli ruch gracza O
                    // Wpisanie w pole znaku O
                    pressedButton.setText("O");

                    if (winCheck(X_move)) {
                        // Jeżeli gracz O wygrał

                        gameStarted = false;
                        Toast.makeText(getApplicationContext(), "Wygrana gracza O!", Toast.LENGTH_SHORT).show();

                        editor.putBoolean("X_won", false);
                        editor.commit();

                        findViewById(R.id.newGameButton).setEnabled(true);
                    } else if(drawCheck()) {
                        // Jeżeli remis

                        gameStarted = false;
                        Toast.makeText(getApplicationContext(), "Remis!", Toast.LENGTH_SHORT).show();

                        editor.putBoolean("X_won", !X_started);
                        editor.commit();

                        findViewById(R.id.newGameButton).setEnabled(true);
                    }

                    X_move = true;
                }
            }

        } else {
            Toast.makeText(getApplicationContext(), "Najpierw zacznij grę!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sprawdzenie czy gracz wygrał
     * @param X_move True - ruch gracza X, False - ruch gracza O
     * @return True - wygrana, False - przegrana
     */
    private boolean winCheck(boolean X_move) {
        // Możliwe układy wygranej
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

        // Dla każdego układu wygranej
        for(String condition: winningConditions) {
            // Rozbicie układu na pojedyncze elementu
            char [] tiles = condition.toCharArray();

            // Założenie wygranej
            win = true;

            // Dla każdego elementu układu
            for(char tile: tiles) {
                // Odniesienie się do konkretnego przycisku
                int ID = getResources().getIdentifier("tile_"+ tile, "id", getPackageName());
                Button tempButton = findViewById(ID);

                // Jeżeli ciąg gracza X i zostaje przerwany przez inny znak
                if(X_move && (tempButton.getText().equals("O") || tempButton.getText().equals(""))) {
                    // Brak wygranej
                    win = false;
                    break;
                }

                // Jeżeli ciąg gracza O i zostaje przerwany przez inny znak
                if(!X_move && (tempButton.getText().equals("X") || tempButton.getText().equals(""))) {
                    // Brak wygranej
                    win = false;
                    break;
                }
            }
            
            if(win)
                break;
        }

        // Zwrócenie czy wygrana
        return win;
    }

    /**
     * Sprawdzenie czy wystąpił remis
     * @return True - prawda, False - fałsz
     */
    private boolean drawCheck() {
        // Dla każdego pola
        for(int i = 0; i < 9; i++) {
            // Jeżeli pole jest puste
            if(tilesArray[i/3][i%3].getText().equals("")) {
                // Brak remisu
                return false;
            }
        }
        // Wystąpił remis
        return true;
    }
}