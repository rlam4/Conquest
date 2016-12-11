package com.five.conquest.Chat;

/**
 * Created by Michael on 12/6/2016.
 */

public enum Team {
    RED, GREEN, BLUE, NEUTRAL;

    public String toString() {
        String result = "";
        switch(this) {
            case RED:
                result = "Red";
                break;
            case GREEN:
                result = "Green";
                break;
            case BLUE:
                result = "Blue";
                break;
            case NEUTRAL:
                result = "Neutral";
                break;
        }
        return result;
    }
}
