package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public class TrieImpl <Value> implements Trie<Value> {
    private static final int alphabetSize = 62; // Figure this out, no nafka mina for now
    private Node<Value> root; // root of trie

    //unclear the access mod
    private class Node<Value>
    {
        private Set<Value> valSet;
        private Node[] links = new Node[alphabetSize];
        private Node(){
            this.valSet=new HashSet<>();
            this.links= new Node[alphabetSize];
        }
        private boolean hasKids() {
            boolean has = false;
            for (Node<Value> n : links) {
                if (n != null) {
                    has = true;
                }
            }
            return has;
        }
    }
    public TrieImpl(){
        this.root=new Node<>();
    }

    /**
     * add the given value at the given key
     * @param key
     * @param val
     */
    public void put(String key, Value val){
        this.root = put(root, key, val, 0);
    }
    private Node put (Node x, String key, Value val, int d){
        if (x == null) {
            x = new Node();
        }
        if (d == key.length()){
            x.valSet.add(val);
            return x;
        }
        char c = key.charAt(d);
        int k=index(c);
        x.links[k] = this.put(x.links[k], key, val, d + 1);
        return x;
    }
    private int index(char ch){
        int c=ch;
        if (c>=48&&c<=57){
            c-=48;
        }
        if (c>=65&&c<=90){
            c-=55;
        }
        if (c>=97&&c<=122){
            c-=61;
        }

       return c;
    }


    /**
     * Get all exact matches for the given key, sorted in descending order, where "descending" is defined by the comparator.
     * NOTE FOR COM1320 PROJECT: FOR PURPOSES OF A *KEYWORD* SEARCH, THE COMPARATOR SHOULD DEFINE ORDER AS HOW MANY TIMES THE KEYWORD APPEARS IN THE DOCUMENT.
     * Search is CASE SENSITIVE.
     * @param key
     * @param comparator used to sort values
     * @return a List of matching Values. Empty List if no matches.
     */
    public List<Value> getSorted(String key, Comparator<Value> comparator){

        List<Value> list = new ArrayList<>(this.get(key));
        Collections.sort(list, comparator);
        return list;
    }

    /**
     * get all exact matches for the given key.
     * Search is CASE SENSITIVE.
     * @param key
     * @return a Set of matching Values. Empty set if no matches.
     */
    public Set<Value> get(String key){
        Node x =  this.get( root, key, 0);
        if (x == null){
            return new HashSet<>();
        }
        return (Set) x.valSet;
    }
    private Node get(Node x, String key, int d){
        if (x == null){
            return null;
        }
        if (d == key.length()) {
            return x;
        }
        char c = key.charAt(d);
        int k=index(c);
        return this.get(x.links[k], key, d + 1);
    }


    /**
     * get all matches which contain a String with the given prefix, sorted in descending order, where "descending" is defined by the comparator.
     * NOTE FOR COM1320 PROJECT: FOR PURPOSES OF A *KEYWORD* SEARCH, THE COMPARATOR SHOULD DEFINE ORDER AS HOW MANY TIMES THE KEYWORD APPEARS IN THE DOCUMENT.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE SENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order. Empty List if no matches.
     */
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator){
        Node x=this.get(root, prefix, 0);
        if (x==null){
            return new ArrayList<>();
        }
        List<Value> a=this.getAllUnder(x);
        Set<Value> s = new HashSet<>(a);
        List<Value> b= new ArrayList<>(s);
        Collections.sort(b, comparator);
        return b;
        }


    private List<Value> getAllUnder(Node n){
        List<Value> vals = new ArrayList<>();
        List<Node<Value>> queue = new ArrayList<>();
        queue.add(n);
            while(!queue.isEmpty()){
                Node<Value> current = queue.remove(0);
                vals.addAll(current.valSet);
                for (Node<Value> child : current.links) {
                    if(child!=null)
                       queue.add(child);
                }
            }
            return vals;
    }


    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE SENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAllWithPrefix(String prefix){//missing the recursive delete!!!
        String prefix1=prefix.substring(0, prefix.length()-1);
        Node x=this.get(root, prefix, 0);
        List<Value> a=this.getAllUnder(x);
        Set<Value> set = new HashSet<>(a);

        Node y=this.get(root, prefix1, 0);
        char c=prefix.charAt(prefix.length()-1);
        y.links[index(c)]=null;

        if ((!(y.valSet.isEmpty()))||y.hasKids()){
            return set;
        }
        del(prefix1);
        return set;
    }


    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     * @param key
     * @return a Set of all Values that were deleted.
     */
   public Set<Value> deleteAll(String key){
       Set<Value> vals= get(key);
       del(key);
       return vals;
   }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    public Value delete(String key, Value val){

        Node<Value> node=get(root, key, 0);
        Value hold=null;
        if (node==null|| node.valSet==null) return null;
        for(Value v:node.valSet){
            if (v.equals(val)){
                hold=v;
            }
        }
        boolean b=node.valSet.remove(val);
        if (!node.valSet.isEmpty()){
            return hold;
        }
        del(key);
        return hold;
    }



    private void del(String key)
    {
        this.root = del(this.root, key, 0);
    }

    private Node del(Node x, String key, int d)
    {
        if (x == null)
        {
            return null;
        }
        //we're at the node to del - set the val to null
        if (d == key.length())
        {
            x.valSet = new HashSet<>();
        }
        //continue down the trie to the target node
        else
        {
            char c = key.charAt(d);
            x.links[index(c)] = this.del(x.links[index(c)], key, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (x.valSet.isEmpty()==false)
        {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int i= 0; i <alphabetSize; i++) {
            if (x.links[i] != null)
            {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

}
