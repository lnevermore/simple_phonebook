package ru.nsu.ccfit.malkov.phonebook.model.events;

/**
 * @author malkov
 */
public class CallEvent {

    private String number;

    public CallEvent(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
}
