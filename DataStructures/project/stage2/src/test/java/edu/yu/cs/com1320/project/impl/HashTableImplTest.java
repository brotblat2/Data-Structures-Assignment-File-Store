package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableImplTest {
    @Test
    void getCheck() {
        HashTableImpl<Integer, String> hash=new HashTableImpl<>();
        hash.put(1, "Hi");
        assertEquals(hash.get(1), "Hi");

    }

    @Test
    void put() {
    }

    @Test
    void containsKey() {
    }

    @Test
    void keySet() {
    }

    @Test
    void values() {
    }

    @Test
    void size() {
    }
}