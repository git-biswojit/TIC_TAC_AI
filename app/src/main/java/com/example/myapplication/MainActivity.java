package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    boolean isAutoTurn = false;
    int clickCount = 0;
    boolean winnerFound = false;
    Button[] buttonArray = new Button[9];
    int[] buttonIds = {
            R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
    };
    String[] buttonContentArray = new String[9];
    AutoPlayer auto = new AutoPlayer(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        for (int i = 0; i < 9; i++) {
            buttonArray[i] = findViewById(buttonIds[i]);
        }
        //TODO: Get Username And Show If He/She Wins
        //TODO: Add Counter For Total Wins,Ties
        //TODO: During 2player Turn Off AI
        reset(null);
    }

    public void reset(View v) {
        isAutoTurn = false;
        clickCount = 0;
        winnerFound = false;
        for (int i = 0; i < 9; i++) {
            buttonArray[i].setText("");
            buttonContentArray[i] = "";
        }
    }

    public void showWinner() {
        String winner = checkAndGetWinner();
        if (!winner.equals("NoWinner") && !winner.isEmpty()) {
            Toast.makeText(this, "Winner Is " + winner, Toast.LENGTH_LONG).show();
            winnerFound = true;
        }
    }

    public void check(View v) {
        Button currentButton = (Button) v;

        if (currentButton.getText().toString().isEmpty() && !winnerFound) {
            currentButton.setText("X");
            updateButtonContentArray();
            clickCount++;
            showWinner();

            if (!winnerFound && clickCount < 9) {
                int btnIdx = auto.chooseButtonToMove();
                if (btnIdx != -1) {
                    buttonArray[btnIdx].setText("O");
                    updateButtonContentArray();
                    clickCount++;
                    showWinner();
                }
            }else if(clickCount>=9){
                Toast.makeText(this, "Well Played. It's A Tie. Please Reset", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateButtonContentArray() {
        for (int i = 0; i < 9; i++) {
            buttonContentArray[i] = buttonArray[i].getText().toString();
        }
    }

    public String checkAndGetWinner() {
        // Rows
        for (int i = 0; i < 9; i += 3) {
            if (!buttonContentArray[i].isEmpty() && buttonContentArray[i].equals(buttonContentArray[i + 1]) && buttonContentArray[i].equals(buttonContentArray[i + 2])) {
                return buttonContentArray[i];
            }
        }
        // Columns
        for (int i = 0; i < 3; i++) {
            if (!buttonContentArray[i].isEmpty() && buttonContentArray[i].equals(buttonContentArray[i + 3]) && buttonContentArray[i].equals(buttonContentArray[i + 6])) {
                return buttonContentArray[i];
            }
        }
        // Diagonals
        if (!buttonContentArray[0].isEmpty() && buttonContentArray[0].equals(buttonContentArray[4]) && buttonContentArray[0].equals(buttonContentArray[8])) {
            return buttonContentArray[0];
        }
        if (!buttonContentArray[2].isEmpty() && buttonContentArray[2].equals(buttonContentArray[4]) && buttonContentArray[2].equals(buttonContentArray[6])) {
            return buttonContentArray[2];
        }
        return "NoWinner";
    }
}
class AutoPlayer {
    MainActivity activity;

    AutoPlayer(MainActivity activity) {
        this.activity = activity;
    }



    public int chooseButtonToMove() {
        return minimaxBase();
    }

    private int winnerStringToNumber() {
        String winner = activity.checkAndGetWinner();
        if (winner.equals("NoWinner") || winner.isEmpty()) {
            return 0;
        } else if (winner.equals("X")) {
            return -1;
        } else {
            return 1;
        }
    }

    private int minimaxBase() {
        int bestMoveIdx = -1;
        int bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < 9; i++) {
            if (activity.buttonContentArray==null || activity.buttonContentArray[i].isEmpty()) {
                activity.buttonContentArray[i] = "O";
                int score = minimax(false);
                activity.buttonContentArray[i] = "";
                if (score > bestScore) {
                    bestScore = score;
                    bestMoveIdx = i;
                }
            }
        }
        return bestMoveIdx;
    }

    private int minimax(boolean aiTurn) {
        int score = winnerStringToNumber();
        System.out.println(score);
        if (score != 0) {
            return score;
        }

        if (isBoardFull()) {
            return 0;
        }

        if (aiTurn) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (activity.buttonContentArray==null || activity.buttonContentArray[i].isEmpty()) {
                    activity.buttonContentArray[i] = "O";
                    score = minimax(false);
                    activity.buttonContentArray[i] = "";
                    bestScore = Math.max(bestScore, score);
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (activity.buttonContentArray==null || activity.buttonContentArray[i].isEmpty()) {
                    activity.buttonContentArray[i] = "X";
                    score = minimax(true);
                    activity.buttonContentArray[i] = "";
                    bestScore = Math.min(bestScore, score);
                }
            }
            return bestScore;
        }
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 9; i++) {
            if (activity.buttonContentArray==null || activity.buttonContentArray[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }
}