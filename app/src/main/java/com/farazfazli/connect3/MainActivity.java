package com.farazfazli.connect3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

public class MainActivity extends Activity {

    private Board mBoard;

    // [CONFIG] //
    public static final boolean printAsciiBoardToLog = true;
    private boolean sounds = true;
    private static final String PLAYER_ONE = "red";
    private static final String PLAYER_TWO = "yellow";
    // [/CONFIG] //

    // [RESOURCES] //
    private ImageView slot;
    private MediaPlayer move;
    private MediaPlayer win;
    // [/RESOURCES] //

    // [PLAYERS&STATS] //
    String startingPlayer;
    String activePlayer;
    private int turns = 0;
    private int redWins;
    private int yellowWins;
    private int draws;
    private String winner = "none";
    // [/PLAYERS&STATS] //

    // [UI] //
    private RelativeLayout startLayout;
    private GridLayout gridLayout;
    private TextView statsWithColorOfCurrentPlayer;
    private ToggleButton soundsToggle;
    // [/UI] //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////// LAYOUTS /////////
        startLayout = (RelativeLayout) findViewById(R.id.startLayout);
        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        statsWithColorOfCurrentPlayer = (TextView) findViewById(R.id.stats);
        startLayout.setVisibility(View.VISIBLE);
        Button redButton = (Button) findViewById(R.id.red);
        Button yellowButton = (Button) findViewById(R.id.yellow);
        Button randomButton = (Button) findViewById(R.id.random);
        soundsToggle = (ToggleButton) findViewById(R.id.sounds);

        ////// SOUNDS //////////
        move = MediaPlayer.create(this, R.raw.move);
        win = MediaPlayer.create(this, R.raw.win);
        mBoard = new Board();

        /////// START GAME BUTTONS ///////
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame("random");
            }
        });

        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame("red");
            }
        });

        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame("yellow");
            }
        });

        /////// SOUNDS TOGGLE ////////////
        soundsToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundsToggle.isChecked()) {
                    sounds = true;
                    Log.i("[TOGGLE]", "Sounds ON!");
                    checkSoundsToggleAndPlayMove();
                }
                if (!soundsToggle.isChecked()) {
                    sounds = false;
                    Log.i("[TOGGLE]", "Sounds OFF!");
                }
            }
        });
    }

    public void startGame(String startingPlayer) {
        checkSoundsToggleAndPlayMove();
        this.startingPlayer = startingPlayer;
        Log.i("[" + startingPlayer.toUpperCase() + "]", "Starting!");
        startLayout.setVisibility(View.INVISIBLE);
        gridLayout.setVisibility(View.VISIBLE);
        initializeBoard();
    }

    public void initializeBoard() {
        Log.i("[INIT]", "Resetting turns!");
        turns = 0;
        Log.i("[INIT]", "Resetting winner!");
        winner = "none";

        Log.i("[INIT]", "Initializing new board!");
        statsWithColorOfCurrentPlayer.setVisibility(View.INVISIBLE);
        mBoard.initializeBoard();

        Log.i("[INIT]", "Clearing grid!");
        clearImageGrid();

        if (startingPlayer.equals("random")) {
            generateRandomStartingPlayer();
        } else {
            Log.i("[NEW]", "The first player is " + startingPlayer + "!");
            activePlayer = startingPlayer;
        }
        setStatsAndTurnColor();
        statsWithColorOfCurrentPlayer.setVisibility(View.VISIBLE);
    }

    public void clearImageGrid() {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            Log.i("[INIT]", "Resetting image for slot " + i + " on board!");
            if (gridLayout.getChildAt(i).isShown()) {
                ((ImageView) gridLayout.getChildAt(i)).setImageResource(0);
            }
        }
    }

    public void generateRandomStartingPlayer() {
        Random random = new Random();
        int randomFirstPlayer = random.nextInt(2);
        activePlayer = (randomFirstPlayer == 1 ? PLAYER_ONE : PLAYER_TWO);
        Log.i("[NEW]", " ^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^");
        Log.i("[NEW]", "  The first player is " + activePlayer + " because the random number generated was: " + randomFirstPlayer);
        Log.i("[NEW]", " ^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^v^");
    }

    public void setStatsAndTurnColor() {
        statsWithColorOfCurrentPlayer.setText(Html.fromHtml("<b>" + "R: " + redWins + "! Y: " + yellowWins + "! D: " + draws + "!</b>"));
        if (turns == 0) {
            Log.i("[STATS]", "Setting initial stats color! -> " + activePlayer);
        }
        if (turns > 0) {
            Log.i("[STATS]", "Changing stats color!");
        }
        switch (activePlayer) {
            case "red":
                statsWithColorOfCurrentPlayer.setTextColor(Color.RED);
                break;
            case "yellow":
                statsWithColorOfCurrentPlayer.setTextColor(Color.YELLOW);
                break;
        }
    }

    public void gamePieceDropIn(View view) {
        if (winner.equals("none")) {
            slot = (ImageView) view;
            int tappedBoxInGrid = Integer.parseInt(getResources().getResourceEntryName(slot.getId()).substring(9));
            if (isItValid(tappedBoxInGrid)) {
                checkSoundsToggleAndPlayMove();
                turns++;
                setColor();
                makeMove(tappedBoxInGrid);
                if (turns > 2) {
                    checkForWins();
                }
            }
        }
    }

    public boolean isItValid(int tapped) {
        if (!mBoard.get(tapped).equals("empty")) {
            Log.i("[INVALID MOVE]", "[" + tapped + "] -> " + activePlayer);
            displayToastError();
            return false;
        }
        return true;
    }

    public void checkSoundsToggleAndPlayMove() {
        if (sounds) {
            move.start();
        }
    }

    public void setColor() {
        switch (activePlayer) {
            case "yellow":
                slot.setImageResource(R.drawable.yellow);
                break;
            case "red":
                slot.setImageResource(R.drawable.red);
                break;
        }
    }

    public void displayToastError() {
        final Toast toast = Toast.makeText(this, "Invalid move!", Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 300);
    }

    public void makeMove(int tapped) {
        Log.i("[TURN #]", String.valueOf(turns));
        Log.i("[ASSIGN]", "[" + tapped + "] -> " + activePlayer);
        mBoard.put(tapped, activePlayer);
        mBoard.drawAsciiBoard();
        animateMove();
        activePlayer = (activePlayer.equals(PLAYER_TWO) ? PLAYER_ONE : PLAYER_TWO); // Switching colors!
        setStatsAndTurnColor();
    }

    public void animateMove() {
        slot.setTranslationY(-1000f);
        slot.animate().translationYBy(1000f).setDuration(300);
        slot.animate().rotationBy(360).setDuration(100);
    }


    public void checkForWins() {
            /*
             HORIZONTAL WINS
             1, 2, 3
             4, 5, 6
             7, 8, 9
             VERTICAL WINS
             1, 4, 7
             2, 5, 8
             3, 6, 9
             DIAGONAL WINS
             1, 5, 9
             3, 5, 7
             */
        for (int i = 1; i < 9; i = i + 3) {
            if (mBoard.get(i).equals(mBoard.get(i + 1)) && mBoard.get(i + 1).equals(mBoard.get(i + 2)) && !(mBoard.get(i).equals("empty"))) {
                Log.i("[WIN]", "Horizontal!");
                winner = mBoard.get(i);
                endGame("Winner!", winner, " horizontally");
                return;
            }
        }
        for (int i = 1; i <= 3; i++) {
            if (mBoard.get(i).equals(mBoard.get(i + 3)) && mBoard.get(i + 3).equals(mBoard.get(i + 6)) && !(mBoard.get(i).equals("empty"))) {
                Log.i("[WIN]", "Vertical!");
                winner = mBoard.get(i);
                endGame("Winner!", winner, " vertically");
                return;
            }
        }
        if (!mBoard.get(5).equals("empty")) {
            if (mBoard.get(1).equals(mBoard.get(5)) && mBoard.get(5).equals(mBoard.get(9)) || mBoard.get(3).equals(mBoard.get(5)) && mBoard.get(5).equals(mBoard.get(7))) {
                Log.i("[WIN]", "Diagonal!");
                winner = mBoard.get(5);
                endGame("Winner!", winner, " diagonally");
                return;
            }
        }
        if (turns == 9 && winner.equals("none")) {
            statsWithColorOfCurrentPlayer.setTextColor(Color.BLUE);
            endGame("Draw", "No one", ", it was a draw");
        }
        if (turns > 9) {
            statsWithColorOfCurrentPlayer.setTextColor(Color.BLUE);
            Toast.makeText(this, "Somehow you made more than 9 turns!", Toast.LENGTH_LONG).show();
        }
    }

    public void endGame(String title, String winner, String style) {
        checkSoundsToggleAndPlayWin();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        statsWithColorOfCurrentPlayer.setTextColor(Color.BLUE);
        builder1.setTitle(title);
        builder1.setMessage(winner.substring(0, 1).toUpperCase() + winner.substring(1) + " has won" + style + "! Play again?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        incrementWin();
                        initializeBoard();
                        checkSoundsToggleAndPlayMove();
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetStats();
                        checkSoundsToggleAndPlayMove();
                        gridLayout.setVisibility(View.INVISIBLE);
                        startLayout.setVisibility(View.VISIBLE);
                        dialog.cancel();
                    }
                });

        AlertDialog endGameDialog = builder1.create();
//        endGameDialog.setCanceledOnTouchOutside(false);
        endGameDialog.setCancelable(false);
        endGameDialog.show();
    }

    public void incrementWin() {
        if (winner.equals("red")) {
            redWins++;
        } else if (winner.equals("yellow")) {
            yellowWins++;
        } else {
            draws++;
        }
    }

    public void checkSoundsToggleAndPlayWin() {
        if (sounds) {
            win.start();
        }
    }

    public void resetStats() {
        redWins = 0;
        yellowWins = 0;
        draws = 0;
        statsWithColorOfCurrentPlayer.setText(Html.fromHtml("<b>" + "R: " + redWins + "! Y: " + yellowWins + "! D: " + draws + "!</b>"));
    }

    @Override
    public void onBackPressed() {
        if (!startLayout.isShown()) {
            checkSoundsToggleAndPlayMove();
            Log.d("[BACK PRESSED]", "Going to menu!");
            Intent setIntent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(setIntent);
        }
    }
}
