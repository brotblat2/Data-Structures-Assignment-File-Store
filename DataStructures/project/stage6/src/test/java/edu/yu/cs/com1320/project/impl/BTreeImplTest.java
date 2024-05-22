package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BTreeImplTest {

    @Test
    void get() {
        BTreeImpl<String, String> bTree = new BTreeImpl<>();

        // Insert key-value pairs for each letter of the alphabet
        bTree.put("a", "aardvark");
        bTree.put("b", "bat");
        bTree.put("c", "chimp");
        bTree.put("d", "dawg");
        bTree.put("e", "eel");
        bTree.put("f", "fox");
        bTree.put("g", "gorilla");
        bTree.put("h", "hyena");
        bTree.put("i", "igloo");
        bTree.put("j", "j");
        bTree.put("k", "kanngaroo");
        bTree.put("l", "lynx");
        bTree.put("m", "man");
        bTree.put("n", "narwhal");
        bTree.put("o", "orangutan");
        bTree.put("p", "puffin");
        bTree.put("q", "quill");
        bTree.put("r", "rhino");
        // Print the contents of the BTree to verify
        for (char c = 'a'; c <= 'r'; c++) {
            String key = String.valueOf(c);
            String value = bTree.get(key);
            System.out.println("Key: " + key + ", Value: " + value);
        }
        assertEquals(bTree.get("b"), "bat");
    }

    @Test
    void put() {
    }

    @Test
    void moveToDisk() {
    }

    @Test
    void setPersistenceManager() {
    }
}