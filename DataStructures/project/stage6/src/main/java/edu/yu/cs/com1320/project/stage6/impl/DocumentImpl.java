package edu.yu.cs.com1320.project.stage6.impl;



import edu.yu.cs.com1320.project.stage6.Document;

import java.net.URI;
import java.util.*;

public class DocumentImpl implements Document {
    private URI uri;
    private String txt;
    private byte[] binaryData;
    private HashMap<String, String> meta;
    private Map<String, Integer> wordCounts;
    private long lastUseTime;



    public DocumentImpl(URI uri, String txt){
        if (uri==null || uri.toString().isEmpty() || txt==null || txt.isEmpty()){
            throw new IllegalArgumentException("Empty or null URI or value");
        }
        this.uri=uri;
        this.txt=txt;
        this.meta= new HashMap<>();
        this.wordCounts=this.wordCountTable();
        this.lastUseTime=System.nanoTime();
    }

    public DocumentImpl(URI uri, String txt, Map<String, Integer> wordCountMap){
        if (uri==null || uri.toString().isEmpty() || txt==null || txt.isEmpty()){
            throw new IllegalArgumentException("Empty or null URI or value");
        }
        this.uri=uri;
        this.txt=txt;
        this.meta= new HashMap<>();
        this.wordCounts=wordCountMap;
        this.lastUseTime=System.nanoTime();
    }

    public DocumentImpl(URI uri, byte[] binaryData){
        if (uri == null) {
            throw new IllegalArgumentException("Empty or null URI or value");
        } else if (uri.toString().isEmpty()) {
            throw new IllegalArgumentException("Empty or null URI or value");
        } else if (binaryData == null) {
            throw new IllegalArgumentException("Empty or null URI or value");
        } else if (binaryData.length == 0) {
            throw new IllegalArgumentException("Empty or null URI or value");
        }

        this.uri=uri;
        this.binaryData=binaryData;
        this.meta= new HashMap<>();
        this.lastUseTime=System.nanoTime();

    }

    /**
     * @param key   key of document metadata to store a value for
     * @param value value to store
     * @return old value, or null if there was no old value
     * @throws IllegalArgumentException if the key is null or blank
     */

    @Override
    public String setMetadataValue(String key, String value){
        if (key==null || key.isEmpty() ){
            throw new IllegalArgumentException("Empty metadata value");
        }
        String old=this.meta.get(key);
        this.meta.put(key, value);
        return old;
    }

    /**
     * @param key metadata key whose value we want to retrieve
     * @return corresponding value, or null if there is no such key
     * @throws IllegalArgumentException if the key is null or blank
     */
    public String getMetadataValue(String key){
        if (key==null || key.isEmpty() ){
            throw new IllegalArgumentException("Empty metadata key");
        }
        return this.meta.get(key);
    }

    /**
     * @return a COPY of the metadata saved in this document
     */
    public HashMap<String, String> getMetadata(){
        HashMap<String, String> copy = new HashMap<>();
        Set<String> keys= meta.keySet();
        Collection<String> values= meta.values();
        for (String key :keys){
            copy.put(key, meta.get(key));
        }
        return copy;

    }

    @Override
    public void setMetadata(HashMap<String, String> metadata) {
        this.meta= metadata;
    }

    /**
     * @return content of text document
     */
    //Do I throw an error or just return null?
    public String getDocumentTxt(){
        return this.txt;
    }

    /**
     * @return content of binary data document
     */
    public byte[] getDocumentBinaryData(){
        return this.binaryData;
    }

    /**
     * @return URI which uniquely identifies this document
     */
    public URI getKey(){
        return this.uri;

    }
    private HashSet <Integer> numberSet(){
       HashSet<Integer> set= new HashSet<>();
        for (int i=0;i<128;i++){
            if (i<48|| (i>57&&i<65) || (i>90 &&i<97) || i>123){
                set.add(i);
            }
        }
        set.remove(32);//spacebar
        return set;
    }
    //strips out non-alpha chars and splits based on spaces. Iterate thru array and add to hashtable
    private HashMap <String, Integer> wordCountTable(){
        String fixed= txt;

        fixed = fixed.replaceAll("[^a-zA-Z0-9 ]", "");

        String[] words= fixed.split(" ");
        HashMap<String, Integer> hash= new HashMap<>();
        for (String s: words){
            Integer val=hash.get(s);
            //hash.merge(s, 1, Integer::sum);
            if (val==null){
                hash.put(s, 1);
            }
            else{
                hash.put(s, val+1);
            }
        }
        hash.remove("");
        return hash;
    }
    /**
     * how many times does the given word appear in the document?
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    @Override
    public int wordCount(String word) {
        if (this.txt==null)
            return 0;
        Integer i= this.wordCounts.get(word);
        if (i==null) return 0;
        return i;
    }

    /**
     * @return all the words that appear in the document
     */
    @Override
    public Set<String> getWords() {
        if (wordCounts==null) return new HashSet<>();
        return wordCounts.keySet();
    }

    @Override
    public long getLastUseTime() {
        return this.lastUseTime;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        this.lastUseTime=timeInNanoseconds;
    }

    @Override
    public HashMap<String, Integer> getWordMap() {
        return (HashMap<String, Integer>) wordCounts;
    }

    @Override
    public void setWordMap(HashMap<String, Integer> wordMap) {
        wordCounts=wordMap;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (txt != null ? txt.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return Math.abs(result);
    }

    @Override
    public boolean equals(Object o){
        if (this.hashCode()==o.hashCode()){
            return true;
        }
        else{
            return false;
        }
    }
    @Override
    public int compareTo(Document o) {
        long dif = this.lastUseTime - o.getLastUseTime();
        if (dif < 0) return -1;
        else if (dif > 0) return 1;
        else return 0;
    }



}
