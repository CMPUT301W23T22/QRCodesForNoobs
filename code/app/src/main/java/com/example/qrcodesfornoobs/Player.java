package com.example.qrcodesfornoobs;

import android.provider.ContactsContract;

import java.util.ArrayList;

public class Player {

    private String username;
    private String device;
    private ArrayList<Creature> creatures = new ArrayList<>();

    public Player(String username, String device) {
        this.username = username;
        this.device = device;
    }
    public void addCreature(Creature creature) {
        creatures.add(creature);
    }

    public void removeCreature(Creature creature) {
        creatures.remove(creature);
    }

    public void removeCreature(int i) {
        creatures.remove(i);
    }

    public String getUsername() {
        return username;
    }

    public String getDevice(){
        return device;
    }
}


