package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl <T>implements Stack<T> {
    private T[] stackArray;
    private int top;
    private int size;

    
    public StackImpl(){
        stackArray= (T[]) new Object[10];
        top=-1;
        size=0;
    }
    @Override
    public void push(T element){
        if (element==null)
            throw new IllegalArgumentException("null input into the stack");

        if(top == stackArray.length - 1){
            T[] copy =(T[])new Object[2*stackArray.length];
            for (int i=0; i<stackArray.length;i++){
                copy[i]=stackArray[i];
                stackArray=copy;
            }
        }
        top++;
        size++;
        stackArray[top] = element;
    }
    /**
     * removes and returns element at the top of the stack
     * @return element at the top of the stack, null if the stack is empty
     */
    @Override
    public T pop() {
        if(top == -1){
            return null;
        }
        T item = stackArray[top];
        stackArray[top] = null;
        top--;
        size--;
        return item;
    }
    /**
     *
     * @return the element at the top of the stack without removing it
     */
    @Override
    public T peek() {
        if(top == -1){
            return null;
        }
        return stackArray[top];
    }
    /**
     *
     * @return how many elements are currently in the stack
     */
    @Override
    public int size() {
     return this.size;
    }

}
