package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {


    @Override
    public void reHeapify(E element) {
        int i=getArrayIndex(element);
        super.upHeap(i);
        super.downHeap(i);
    }

    @Override
    protected int getArrayIndex(E element) {

        for (int i = 0; i < this.elements.length; i++) {
            if (this.elements[i].equals(element)) return i;
        }
        return -1;

    }


    @Override
    protected void doubleArraySize() {
        if (elements[this.elements.length - 1] != null) {
            E[] temp = new E[elements.length * 2];
            for (int i = 0; i < this.elements.length; i++) {
                temp[i] = elements[i];
            }
            elements=temp;
        }

    }
}
