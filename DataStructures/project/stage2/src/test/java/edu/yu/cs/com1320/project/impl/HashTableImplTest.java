package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HashTableImplTest {
    @Test
    void getCheckNull() {
        HashTableImpl<Integer, String> hash=new HashTableImpl<>();
        hash.put(1, "A");
        hash.put(2, "B");
        hash.put(3, "C");
        hash.put(4, "D");
        hash.put(5, "E");
        hash.put(6, "F");
        hash.put(7, "G");
        hash.put(8, "H");
        hash.put(9, "i");
        hash.put(122332, "J");
        hash.put(122332, "Replaced");
        hash.put(1, null);
        assertEquals(hash.get(1), null);
    }
    @Test
    void getCheck2() {
        HashTableImpl<Integer, String> hash=new HashTableImpl<>();
        hash.put(1, "A");
        hash.put(2, "B");
        hash.put(3, "C");
        hash.put(4, "D");
        hash.put(5, "E");
        hash.put(6, "F");
        hash.put(7, "G");
        hash.put(8, "H");
        hash.put(9, "i");
        hash.put(122332, "J");
        hash.put(122332, "Replaced");
        hash.put(1, null);
        assertEquals(hash.get(2), "B");
    }


    @Test
    void putReturnsVal() {
        HashTableImpl<Integer, String> hash=new HashTableImpl<>();
        hash.put(1, "A");
        hash.put(2, "B");
        hash.put(3, "C");
        hash.put(4, "D");
        hash.put(5, "E");
        hash.put(6, "F");
        hash.put(7, "G");
        hash.put(8, "H");
        String n=hash.put(9, "i");
        hash.put(122332, "A");
        String a=hash.put(122332, "Replaced");
        String s= hash.put(1, null);
        assertEquals(s, a,"A");
    }
    @Test
    void putReturnsNull() {
        HashTableImpl<Integer, String> hash=new HashTableImpl<>();
        hash.put(1, "A");
        hash.put(2, "B");
        hash.put(3, "C");
        hash.put(4, "D");
        hash.put(5, "E");
        hash.put(6, "F");
        hash.put(7, "G");
        hash.put(8, "H");
        String n=hash.put(9, "i");
        hash.put(122332, "A");
        String a=hash.put(122332, "Replaced");
        String s= hash.put(1, null);
        assertNull(n);
    }


    @Test
    void containsKey() {
        HashTableImpl<Integer, String> hash=new HashTableImpl<>();
        hash.put(1, "A");
        hash.put(2, "B");
        hash.put(3, "C");
        hash.put(4, "D");
        assertTrue(hash.containsKey(3));
    }
    @Test
    void containsKey2() {
        HashTableImpl<Integer, String> hash=new HashTableImpl<>();
        hash.put(1, "A");
        hash.put(2, "B");
        hash.put(3, "C");
        hash.put(4, "D");
        assertFalse(hash.containsKey(9));
    }

    @Test
    void keySet() {
        HashTableImpl<Integer, String> hash=new HashTableImpl<>();
        hash.put(1, "A");
        hash.put(2, "B");
        hash.put(3, "C");
        hash.put(4, "D");
         Set<Integer> set=hash.keySet();
         Set<Integer> se=new HashSet<>();
         se.add(1);
         se.add(2);
         se.add(3);
         se.add(4);
         assertEquals(set, se);
    }

    @Test
    void values() {
            HashTableImpl<Integer, String> hash=new HashTableImpl<>();
            hash.put(1, "A");
            hash.put(2, "B");
            hash.put(3, "C");
            hash.put(4, "D");
            Collection<String> col=hash.values();
            Collection<String> c=new ArrayList<>();
            c.add("A");
            c.add("B");
            c.add("C");
            c.add("D");
            c=Collections.unmodifiableCollection(c);
            assertEquals(col.contains("A"), c.contains("A"));
    }

    @Test
    void size0() {
        HashTableImpl<Integer, String> hash = new HashTableImpl<>();
        assertEquals(0, hash.size());

    }
    @Test
    void size4() {
        HashTableImpl<Integer, String> hash = new HashTableImpl<>();
        hash.put(0, "A");

        hash.put(1, "A");
        hash.put(2, "B");
        hash.put(3, "C");
        hash.put(11, "A");
        hash.put(21, "B");
        hash.put(13, "C");
        hash.put(1422231, "D");

        assertEquals(8, hash.size());

    }
}