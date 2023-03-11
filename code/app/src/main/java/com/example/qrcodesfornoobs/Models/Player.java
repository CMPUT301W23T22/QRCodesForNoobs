package com.example.qrcodesfornoobs.Models;

import java.util.ArrayList;
import java.util.Objects;

public class Player {
    public static String LOCAL_USERNAME = null; // only set if user has signed in before
    //TODO: Call to db for Creatures
    private String username;
    private String device;
    private ArrayList<String> creatures = new ArrayList<>();
    private String contact;

    public Player(String username, String device) {
        this.username = username;
        this.device = device;
    }

    // addressing https://stackoverflow.com/questions/60389906/could-not-deserialize-object-does-not-define-a-no-argument-constructor-if-you
    public Player() {

    }

    /**
     * 2 Player are considered equal if they have the same username & device
     * @param o player to be compared with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return username.equals(player.username) && device.equals(player.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, device);
    }

    public void addCreature(Creature creature) {
        creatures.add(creature.getHash());
    }

    public void removeCreature(Creature creature) {
        creatures.remove(creature.getHash());
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

    public String getContact() {
        return contact;
    }
    public ArrayList<String> getCreatures() {
        return creatures;
    }
    public boolean containsCreature(Creature creature) {
        return creatures.contains(creature.getHash());
    }

}


