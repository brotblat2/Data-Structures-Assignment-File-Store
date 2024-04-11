package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {

    public MinHeapImpl(){
        super.elements= (E[]) new Comparable[10];;
    }


    @Override
    public void reHeapify(E element) {
        int i=getArrayIndex(element);
        super.upHeap(i);
        super.downHeap(i);
    }

    @Override
    protected int getArrayIndex(E element) {

        for (int i = 1; i < this.elements.length; i++) {
            if(elements[i]!=null) if (this.elements[i].equals(element)) return i;
        }

        throw new NoSuchElementException();

    }


    @Override
    protected void doubleArraySize() {
        if (elements[this.elements.length - 1] != null) {
            E[] temp = (E[]) new Comparable[this.elements.length * 2];
            for (int i = 0; i < this.elements.length; i++) {
                temp[i] = elements[i];
            }
            elements=temp;
        }

    }
}
