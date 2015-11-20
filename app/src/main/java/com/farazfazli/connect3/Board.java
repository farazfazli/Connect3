package com.farazfazli.connect3;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by Faraz on 11/20/2015.
 */
public class Board {
    HashMap<Integer, String> board = new HashMap<>();

    public void initializeBoard() {
        for (int i = 1; i <= 9; i++) {
            Log.i("[INIT]", "Adding empty slot " + i + " to board!");
            board.put(i, "empty");
        }
    }

    public void put(Integer integer, String string) {
        board.put(integer, string);
    }

    public String get(Integer i) {
        return board.get(i);
    }

    public void drawAsciiBoard() {
        if (MainActivity.printAsciiBoardToLog) {
            Log.i("[DRAW BOARD]", "++----------- [BEGIN] -------------++");
            Log.i("[ROW 1]", " | " + board.get(1) + " | " + board.get(2) + " | " + board.get(3) + " | ");
            Log.i("[ROW 2]", " | " + board.get(4) + " | " + board.get(5) + " | " + board.get(6) + " | ");
            Log.i("[ROW 3]", " | " + board.get(7) + " | " + board.get(8) + " | " + board.get(9) + " | ");
            Log.i("[DRAW BOARD]", "++------------ [END] --------------++");
        }
    }
}
