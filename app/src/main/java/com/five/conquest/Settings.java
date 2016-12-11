package com.five.conquest;

import java.io.Serializable;

/**
 * Created by Michael on 12/11/2016.
 */

public class Settings implements Serializable {
    private boolean kilometers;
    private boolean colorBlind;

    public Settings () {
        kilometers = false;
        colorBlind = false;
    }

    public Settings (boolean kilometers, boolean colorBlind) {
        this.kilometers = kilometers;
        this.colorBlind = colorBlind;
    }

    public boolean getKilometers() {return kilometers;}

    public boolean getColorblind() {return colorBlind;}
}
