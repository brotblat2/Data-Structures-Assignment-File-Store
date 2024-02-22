package edu.yu.cs.com1320.project.stage2.impl;



import edu.yu.cs.com1320.project.stage2.Document;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class DocumentImpl implements Document {
    private URI uri;
    private String txt;
    private byte[] binaryData;
    private HashTableImpl <String, String> meta;



    public DocumentImpl(URI uri, String txt){
        if (uri==null || uri.toString().isEmpty() || txt==null || txt.isEmpty()){
            throw new IllegalArgumentException("Empty or null URI or value");
        }
        this.uri=uri;
        this.txt=txt;
        this.meta= new HashTableImpl<>();
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
        this.meta= new HashTableImpl<>();


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
    public HashTableImpl<String, String> getMetadata(){
        HashTableImpl<String, String> copy = new HashTableImpl<>();
        Set<String> keys= meta.keySet();
        Collection<String> values= meta.values();
        for (String key :keys){
            copy.put(key, meta.get(key));
        }
        return copy;

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

}
