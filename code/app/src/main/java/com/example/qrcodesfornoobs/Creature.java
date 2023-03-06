package com.example.qrcodesfornoobs;

import android.location.Location;
import android.media.Image;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a creature derived from a string
 */
public class Creature {
    private String name;
    private String hash;
    private int score;
    private Image photo;
    private Location location;
    private Image photoLocation;
    private ArrayList<String> comments = new ArrayList<>();

    /**
     *
     * @param code
     */
    public Creature (String code) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // Change this to UTF-16 if needed
        md.update(code.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();

        hash = String.format("%064x", new BigInteger(1, digest));

        // Calculating the score
        int count = 0;
        int prev = -1;
        score = 0;
        for (int i = 0; i < hash.length(); i++){
            int b = Integer.decode("0x"+hash.charAt(i));
            if (b == 0) {b = 20;} // special case of 0 which scales by 20 points.
            if (prev == b){
                count *= b; // multiply when current digit is same and the previous digit
            } else {
                score += count; // Add the current total to the current score
                count = 1;  //reset count value to 1
                prev = b;
            }
        }
        score += count; // Final addition for the end of the loop.

        // Generate a name
        name = "";
        for(int i = 0; i < 4; i++){
            int b = Integer.decode("0x"+hash.charAt(i));
            // Wouldn't it be funny if I just glued together random syllables and hoped it made something coherent?
            switch(b){
                case 0: name = name.concat("Ha"); break;
                case 1: name = name.concat("Mo"); break;
                case 2: name = name.concat("Chi"); break;
                case 3: name = name.concat("Go"); break;
                case 4: name = name.concat("Na"); break;
                case 5: name = name.concat("Li"); break;
                case 6: name = name.concat("Tri"); break;
                case 7: name = name.concat("Ca"); break;
                case 8: name = name.concat("Pen"); break;
                case 9: name = name.concat("Kal"); break;
                case 10: name = name.concat("Vee"); break;
                case 11: name = name.concat("Ri"); break;
                case 12: name = name.concat("Tee"); break;
                case 13: name = name.concat("Wa"); break;
                case 14: name = name.concat("Sa"); break;
                case 15: name = name.concat("Ya"); break;
            }
        }

        // TODO: Image functionality
    }

    public Creature(String name, String hash, int score, Image photo, Location location, ArrayList<String> comments){
        this.name = name;
        this.hash = hash;
        this.score = score;
        this.photo = photo;
        this.location = location;
        this.comments = comments;

    }

    // NO ARGUMENT CONSTRUCTOR -> USED SO THAT SOMETHING DOESNT BREAK I GUESS IDK
    // Was running into this issue:
    // https://stackoverflow.com/questions/60389906/could-not-deserialize-object-does-not-define-a-no-argument-constructor-if-you
    public Creature(){

    }
    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public Image getPhoto() {
        return photo;
    }
    public Image getPhotoLocation() {
        return photoLocation;
    }
    public Location getLocation() {
        return location;
    }

    public void setPhoto(Image photo) {
        this.photo = photo;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    public void setPhotoLocation(Image photoLocation) {
        this.photoLocation = photoLocation;
    }
    public void addComment(String comment){
        comments.add(comment);
    }
    public void removeComment(String comment){
        comments.remove(comment);
    }
    public ArrayList<String> getComments() {return comments;}
}