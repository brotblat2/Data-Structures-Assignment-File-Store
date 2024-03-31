
package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;

import java.util.*;

public class HashTableImpl <Key, Value> implements HashTable<Key, Value>{
    private class Linker <K, V> {
        private K key;
        private V value;

        private Linker<K, V> next;


        private Linker (K key, V value){
            this.key=key;
            this.value=value;
            this.next= null;
        }

        private V add(K key, V val) {

            Linker<K, V> current = this;
            if (current.key.equals(key)) {
                V v =  current.value;
                current.value = val;
                return v;
            }
            while (current.next != null) {
                current = current.next;
                if (current.key.equals(key)) {
                    V v =  current.value;
                    current.value = val;
                    return v;
                }
            }
            current.next=new Linker<>(key, val);
            return null;
        }




        private K getKey(){
            return  this.key;
        }
        private V getValue(){
            return  this.value;
        }


        private Linker<K, V> getNext(){
            return this.next;
        }


        private V get(K k){
            Linker<K, V> current=this;
            if (current.key.equals(k)){
                return  current.value;
            }
            while (current.next!=null){
                current=current.next;
                if (current.getKey().equals(k)){
                    return  current.value;
                }
                if (current.next==null){
                    return null;
                }
            }
            return null;
        }

        private Linker<K, V> remove (K k) {
            if (k == null) {
                throw new IllegalArgumentException();
            }
            Linker<K,V> current = this;


            if (this.key.equals(k)) {
                return this.next;
            }

            while (current.next != null && !current.next.key.equals(k)) {
                current = current.next;
            }

            if (current.next != null) {
                current.next = current.next.next;
            }
            return this;

        }
        private int length(){
            int c=1;
            Linker<K,V> current=this;
            if (current.next==null){
                return c;
            }

            while (current.next!=null){
                c++;
                current=current.next;
            }
            return c;
        }

    }

    Linker<Key, Value>[] baseArray;
    int size;

    public HashTableImpl(){
        this.baseArray= new Linker[5];
        size=0;
    }

    /**
     * @param k the key whose value should be returned
     * @return the value that is stored in the HashTable for k, or null if there is no such key in the table
     */
    @Override
    public Value get(Key k) {
        int index = Math.abs(k.hashCode() % baseArray.length);
        if (this.baseArray[index] == null) {
            return null;
        }
        return this.baseArray[index].get(k);
    }




    /**
     * @param k the key at which to store the value
     * @param v the value to store
     *          To delete an entry, put a null value.
     * @return if the key was already present in the HashTable, return the previous value stored for the key. If the key was not already present, return null.
     */
    @Override
    public Value put(Key k, Value v){
        int index = Math.abs(k.hashCode() % baseArray.length);

        if (v==null){
            Value val=baseArray[index].get(k);
            if (val!=null){
                this.size-=1;
            }
            baseArray[index] = baseArray[index].remove(k);
            return val;
        }
        if (baseArray[index]==null){
            baseArray[index]=new Linker<>(k,v);
            this.size+=1;
            return null;
        }
        Value val = baseArray[index].add(k, v);
        if (val==null){
            size+=1;
        }
        // RESIZE TIME
        this.resize();

        return val;
    }

    private boolean resize(){
        boolean b=false;
        if(this.size>=4*this.baseArray.length){
            b=true;

            Linker<Key, Value>[] copy= new Linker[baseArray.length*2];

            for (Linker<Key, Value> current:baseArray){
                if (current!=null){
                    int i=current.key.hashCode()%copy.length;
                    if (copy[i]==null){
                        copy[i]=new Linker<>(current.key, current.value);
                    }
                    else{
                        copy[i].add(current.key, current.value);
                    }
                    while (current.next!=null){
                        current=current.next;
                        i=current.key.hashCode()%copy.length;
                        if (copy[i]==null){
                            copy[i]=new Linker<>(current.key, current.value);
                        }
                        else{
                            copy[i].add(current.key, current.value);
                        }
                    }
                }
            }
            baseArray=copy;
        }
        return b;
    }

    /**
     * @param key the key whose presence in the hashtable we are inquiring about
     * @return true if the given key is present in the hashtable as a key, false if not
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public boolean containsKey( Key key){
        if(key==null){
            throw new NullPointerException();
        }

        int index = Math.abs(key.hashCode() % baseArray.length);
        if(baseArray[index]==null){
            return false;
        }
        if (baseArray[index].get( key)==null){
            return false;
        }
        return true;
    }

    /**
     * @return an unmodifiable set of all the keys in this HashTable
     * @see java.util.Collections#unmodifiableSet(Set)
     */
    @Override
    public Set<Key> keySet(){
        Set<Key> col = new HashSet<>();
        for (Linker<Key,Value> l:baseArray) {
            if (l != null) {
                col.add(l.getKey());
                while(l.next!=null){
                    l=l.getNext();
                    col.add(l.getKey());
                }
            }
        }
        return Collections.unmodifiableSet((col));
    }

    /**
     * @return an unmodifiable collection of all the values in this HashTable
     * @see java.util.Collections#unmodifiableCollection(Collection)
     */
    @Override
    public Collection<Value> values(){
        Collection<Value> col = new ArrayList<>();
        for (Linker<Key, Value> l:baseArray) {
            if (l != null) {
                col.add(l.getValue());
                while(l.next!=null){
                    l=l.getNext();
                    col.add(l.getValue());
                }
            }
        }
        return Collections.unmodifiableCollection(col);
    }

    /**
     * @return how entries there currently are in the HashTable
     */
    @Override
    public int size(){
        int c=0;
        for (Linker<Key, Value> l:baseArray){
            if (l!=null){
                c+=l.length();
            }
        }
        return c;
    }


}
