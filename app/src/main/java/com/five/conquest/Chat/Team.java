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
                result = "Pirates";
                break;
            case GREEN:
                result = "Ninjas";
                break;
            case BLUE:
                result = "Knights";
                break;
            case NEUTRAL:
                result = "Unconquered";
                break;
        }
        return result;
    }
}
