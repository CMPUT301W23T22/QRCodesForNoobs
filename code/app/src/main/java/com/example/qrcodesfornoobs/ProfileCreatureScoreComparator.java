package com.example.qrcodesfornoobs;

import java.util.Comparator;

public class ProfileCreatureScoreComparator implements Comparator<Creature> {

    @Override
    public int compare(Creature c1, Creature c2) {
        return c1.getScore() - c2.getScore();
    }
}
