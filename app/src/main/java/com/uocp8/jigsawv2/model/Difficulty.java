package com.uocp8.jigsawv2.model;

public enum Difficulty {
    EASY(2), MEDIUM(4), HARD(8);
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
}
