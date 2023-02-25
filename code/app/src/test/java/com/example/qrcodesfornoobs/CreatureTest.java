package com.example.qrcodesfornoobs;

import org.junit.Test;
import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;

public class CreatureTest {

    private Creature testHash() throws NoSuchAlgorithmException {
        return new Creature("test");
    }

    @Test
    public void testCreate() throws NoSuchAlgorithmException {
        Creature test = testHash();
        assertEquals("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08",
                        test.getHash());

        assertEquals(99, test.getScore());
        assertEquals("KalYaPenTri", test.getName());
    }

}
