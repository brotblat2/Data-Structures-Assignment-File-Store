package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapImplTest {
    MinHeapImpl<Integer> minHeap;
    @BeforeEach
    void setUp() {
        minHeap=new MinHeapImpl<>();
        minHeap.insert(49);
        minHeap.insert(4324);
        minHeap.insert(324);
        minHeap.insert(2344);
        minHeap.insert(443);
        minHeap.insert(24);
        minHeap.insert(4);
        minHeap.insert(32344);
        minHeap.insert(34);
        minHeap.insert(43);
    }
    @Test
    void sort() {
        setUp();
        List<Integer> lis= new ArrayList<>();
        while(minHeap.peek()!=null){
            lis.add(minHeap.remove());
        }
        assertEquals(10, lis.size());
    }

}