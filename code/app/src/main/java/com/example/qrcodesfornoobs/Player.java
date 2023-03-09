package com.example.qrcodesfornoobs;

import android.provider.ContactsContract;

import java.util.ArrayList;

public class Player {
    public static String LOCAL_USERNAME = null; // only set if user has signed in before
    //TODO: Call to db for Creatures
    private String username;
    private final String device;
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


