package com.example.qrcodesfornoobs.Models;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single player of the game, using that player's username and deviceID.
 */
public class Player {
    public static String LOCAL_USERNAME = null; // only set if user has signed in before
    //TODO: Call to db for Creatures
    private String username;
    private String device;
    private ArrayList<String> creatures = new ArrayList<>();
    private String contact;

    /**
     * Constructor for a player class.
     * @param username Username's unique username. (Uniqueness is checked outside the constructor.)
     * @param device    ID for the device the user logged in on.
     */
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

    /**
     * Add a creature's hash to player's collection.
     * @param creature Creature to be added.
     * @see Creature
     */
    public void addCreature(Creature creature) {
        creatures.add(creature.getHash());
    }

    /**
     * Removes target creature from Player's collection.
     * Does nothing if the Creature's hash is not in Player's collection.
     * @param creature Creature to be deleted.
     */
    public void removeCreature(Creature creature) {
        creatures.remove(creature.getHash());
    }

    /**
     * Removes target creature from Player's collection.
     * Does nothing if the Creature's hash is not in Player's collection.
     * @param i index of the creature being removed.
     */
    public void removeCreature(int i) {
        creatures.remove(i);
    }

    /**
     * Getter function for player's username.
     * @return Player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter function for the player's associated device.
     * @return DeviceID of device the player used to login.
     */
    public String getDevice(){
        return device;
    }

    /**
     * Get the contact information for the player.
     * @return String with contact information.
     */
    public String getContact() {
        return contact;
    }

    /**
     * Returns the arraylist containing the hashes for all the player's Creatures.
     * @return Arraylist of player's hashes.
     */
    public ArrayList<String> getCreatures() {
        return creatures;
    }

    /**
     * Checks if a given Creature is in a player's collection.
     * @param creature Creature class with a hash.
     * @return True if player has the target Creature. False otherwise.
     */
    public boolean containsCreature(Creature creature) {
        return creatures.contains(creature.getHash());
    }

}


