package ru.nsu.ccfit.malkov.phonebook.model;

import com.squareup.otto.Bus;

/**
 * @author malkov
 */
public class BusHolder {
    private static Bus ourInstance = new Bus();

    public static Bus getInstance() {
        return ourInstance;
    }

}
