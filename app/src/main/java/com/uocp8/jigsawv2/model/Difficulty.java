package com.uocp8.jigsawv2.model;

public enum Difficulty {
    EASY(2), MEDIUM(4), HARD(6);
    private int value;

    Difficulty(int value) {
        this.value = value;
    }

    public static Difficulty fromValue(int which) {
        switch (which) {
            case 0:
                return EASY;
            case 1:
                return MEDIUM;
            case 2:
                return HARD;
            default:
                throw new IllegalArgumentException("Unknown difficulty level selected.");
        }
    }

    public int getValue() {
        return value;
    }

    public int getScoreInitial()
    {
        if(this.value == 2) return 100;
        if(this.value == 4) return 300;
        else return 600;
    }
}
