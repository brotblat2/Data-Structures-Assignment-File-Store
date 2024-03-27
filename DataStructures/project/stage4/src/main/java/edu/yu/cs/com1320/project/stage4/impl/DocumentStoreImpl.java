package edu.yu.cs.com1320.project.stage4.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;

import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
import edu.yu.cs.com1320.project.undo.Command;

import javax.print.Doc;

public class DocumentStoreImpl implements DocumentStore {

    private HashTableImpl<URI, DocumentImpl> store;
    private StackImpl<Command> commandStack;
    private TrieImpl<Document> documentTrie;

    public DocumentStoreImpl(){
        this.commandStack= new StackImpl<>();
        this.store= new HashTableImpl<>();
        documentTrie=new TrieImpl<>();
    }

    /**
     * the two document formats supported by this document store.
     * Note that TXT means plain text, i.e. a String.
     */

    /**
     * set the given key-value metadata pair for the document at the given uri
     *
     * @param uri
     * @param key
     * @param value
     * @return the old value, or null if there was no previous value
     * @throws IllegalArgumentException if the uri is null or blank, if there is no document stored at that uri, or if the key is null or blank
     */
    public String setMetadata(URI uri, String key, String value){
        if (uri==null || uri.toString().isEmpty()){
            throw new IllegalArgumentException("Empty uri");
        }
        if (this.store.get(uri)==null){
            throw new IllegalArgumentException("No Doc");
        }
        if (key==null || key.isEmpty()){
            throw new IllegalArgumentException("Inputted key is empty");
        }
        String data= this.store.get(uri).setMetadataValue(key, value);

        Consumer<URI> u = (squash) -> {
            this.store.get(uri).setMetadataValue(key, data);
        };

        Command com=new Command(uri, u);
        commandStack.push(com);
        return data;
    }

    /**
     * get the value corresponding to the given metadata key for the document at the given uri
     *
     * @param uri
     * @param key
     * @return the value, or null if there was no value
     * @throws IllegalArgumentException if the uri is null or blank, if there is no document stored at that uri, or if the key is null or blank
     */
    public String getMetadata(URI uri, String key){
        if (uri==null || uri.toString().isEmpty()){
            throw new IllegalArgumentException("Empty uri");
        }
        if (this.store.get(uri)==null){
            throw new IllegalArgumentException("No Doc");
        }

        if (key==null || key.isEmpty()){
            throw new IllegalArgumentException("Inputted key is empty");
        }
        return this.store.get(uri).getMetadataValue(key);

    }

    /**
     * @param input  the document being put
     * @param uri    unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null
     * ,this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws IOException              if there is an issue reading input
     * @throws IllegalArgumentException if uri is null or empty, or format is null
     */
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException{
        if (uri==null || uri.toString().isEmpty()){
            throw new IllegalArgumentException("Empty uri");
        }
        if (format==null){
            throw new IllegalArgumentException("Null Format");
        }
        //Return Value and acting as delete
        int x;
        if (input==null){
            x=store.get(uri).hashCode();
            if (this.delete(uri)) return x;
            else return 0;
        }
        // return value
        if (store.get(uri)==null){
            x=0;
        }
        else{
            x= store.get(uri).hashCode();
        }
        putImple(input, uri, format);
        return x;
    }
    private void putImple(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if (format==DocumentFormat.BINARY){
            byte[] b = input.readAllBytes();
            DocumentImpl d= new DocumentImpl(uri, b);
            DocumentImpl doc=this.store.put(uri, d);
            Consumer <URI> u = (squash) -> {
                this.store.put(uri, doc);
            };
            Command com=new Command(uri, u);
            commandStack.push(com);
        }
        else{
            byte[] b=input.readAllBytes();
            String s =new String(b);
            DocumentImpl d= new DocumentImpl(uri, s);

            //put time in to TRIE
            Set<String> wordSet=d.getWords();
            for (String word:wordSet) {
                documentTrie.put(word, d);
            }

            DocumentImpl doc=this.store.put(uri, d);

            Consumer <URI> u = (squash) -> {
                this.store.put(uri, doc);
            };
            Command com=new Command(uri, u);
            commandStack.push(com);
        }
    }


    /**
     * @param url the unique identifier of the document to get
     * @return the given document
     */
    public Document get(URI url){
        return this.store.get(url);
    }

    /**
     * @param url the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    public boolean delete(URI url){
        if (this.store.get(url)==null){
            return false;
        }

        //NEED TO DELETE FROM THE TRIE
        Set<String> words=this.store.get(url).getWords();
        for(String word:words){
            documentTrie.delete(word, this.store.get(url));
        }

        DocumentImpl doc= this.store.put(url, null);
        Consumer <URI> u = (squash) -> {
               this.store.put(url, doc);
            };
            Command com=new Command(url, u);
            commandStack.push(com);
            return true;
    }

    private boolean privateDeleteFromTable(URI url){
        if (this.store.get(url)==null){
            return false;
        }
        DocumentImpl doc= this.store.put(url, null);
        //NEED TO DELETE FROM THE TRIE
        return true;
    }

    @Override
    public void undo() throws IllegalStateException {
        //fixed from old assignment
        if (commandStack.size()==0){throw new IllegalStateException();}
        Command c= commandStack.pop();
        c.undo();
    }

    @Override
    public void undo(URI url) throws IllegalStateException {
        StackImpl<Command> temp=new StackImpl<>();
        boolean found=false;
        while (commandStack.peek()!=null){
            if (!(commandStack.peek().getUri().toString().equals(url.toString()))){
                temp.push(commandStack.pop());
            }
            else{
               commandStack.pop().undo();
                found=true;
                break;
            }
        }
        if (!found){
            throw new IllegalStateException();
        }

        while (temp.peek()!=null){
            commandStack.push(temp.pop());
        }

        }

    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */


    @Override
    public List<Document> search(String keyword) {
       List lis=  documentTrie.getSorted(keyword, new DocumentComparator(keyword));
       return lis;
    }
    /**
     * Retrieve all documents that contain text which starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE SENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        return documentTrie.getAllWithPrefixSorted(keywordPrefix, new DocumentComparatorPrefix(keywordPrefix));
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * Search is CASE SENSITIVE.
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAll(String keyword) {
        Set<Document> oldDocs= documentTrie.get(keyword);
        Set<URI> oldUris= new HashSet<>();
        for(Document d:oldDocs){
            oldUris.add(d.getKey());
            for (String s:d.getWords()){
                documentTrie.delete(s,d);
            }
        }
        for(URI u:oldUris){
            this.privateDeleteFromTable(u);
        }

        return oldUris;

    }
    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE SENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    //UP TO HERE!!!!!!!!!
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Set<Document> oldDocs=documentTrie.deleteAllWithPrefix(keywordPrefix);
        Set<URI> oldUris= new HashSet<>();
        for(Document d:oldDocs){
            oldUris.add(d.getKey());
            for (String s:d.getWords()){
                documentTrie.delete(s,d);
            }
            this.privateDeleteFromTable(d.getKey());
        }

        return oldUris;
    }

    /**
     * @param keysValues metadata key-value pairs to search for
     * @return a List of all documents whose metadata contains ALL OF the given values for the given keys. If no documents contain all the given key-value pairs, return an empty list.
     */
    @Override
    public List<Document> searchByMetadata(Map<String, String> keysValues) {
        List<Document> docsList=documentTrie.getAllWithPrefixSorted("", new DocumentComparatorPrefix(""));
        Set<Document> docSet=new HashSet<>();
        for(Document d:docsList){
            boolean has=true;
            for (String s:keysValues.keySet()){
                if (!(d.getMetadata().containsKey(s) && keysValues.get(s).equals(d.getMetadataValue(s)))){
                    has=false;
                    break;
                }
            }
            if (has) docSet.add(d);
        }
        ;
        List<Document> returnList=new ArrayList<>(docSet);
        return returnList;
    }
    /**
     * Retrieve all documents whose text contains the given keyword AND which has the given key-value pairs in its metadata
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     * @param keyword
     * @param keysValues
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByKeywordAndMetadata(String keyword, Map<String, String> keysValues) {
        List<Document> docsList=searchByMetadata(keysValues);
        if(docsList==null || docsList.isEmpty()) return new ArrayList<>();
        List<Document> list=search(keyword);
        if(list==null || list.isEmpty()) return new ArrayList<>();

        for (Document doc : new ArrayList<>(docsList)) {
            if (!list.contains(doc)) {
                docsList.remove(doc);
            }
        }
        return docsList;
    }
    /**
     * Retrieve all documents that contain text which starts with the given prefix AND which has the given key-value pairs in its metadata
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE SENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) {
        List<Document> docsList=searchByMetadata(keysValues);
        List<Document> list=searchByPrefix(keywordPrefix);

        for (Document doc : new ArrayList<>(docsList)) {
            if (!list.contains(doc)) {
                docsList.remove(doc);
            }
        }
        return docsList;
    }
    /**
     * Completely remove any trace of any document which has the given key-value pairs in its metadata
     * Search is CASE SENSITIVE.
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithMetadata(Map<String, String> keysValues) {
        List<Document> list=searchByMetadata(keysValues);
        Set<URI> oldUris= new HashSet<>();
        //Delete from TRIE
        for(Document doc:list){
            for (String s:doc.getWords()){
                documentTrie.delete(s, doc);
            }
            oldUris.add(doc.getKey());
            this.privateDeleteFromTable(doc.getKey());
        }
        return oldUris;
    }
    /**
     * Completely remove any trace of any document which contains the given keyword AND which has the given key-value pairs in its metadata
     * Search is CASE SENSITIVE.
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithKeywordAndMetadata(String keyword, Map<String, String> keysValues) {
        List<Document> list=searchByKeywordAndMetadata(keyword,keysValues);
        Set<URI> oldUris= new HashSet<>();
        //Delete from TRIE
        for(Document doc:list){
            for (String s:doc.getWords()){
                documentTrie.delete(s, doc);
            }
            oldUris.add(doc.getKey());
            this.privateDeleteFromTable(doc.getKey());
        }
        return oldUris;
    }
    /**
     * Completely remove any trace of any document which contains a word that has the given prefix AND which has the given key-value pairs in its metadata
     * Search is CASE SENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) {
        List<Document> list=searchByPrefixAndMetadata(keywordPrefix, keysValues);
        Set<URI> oldUris= new HashSet<>();
        //Delete from TRIE
        for(Document doc:list){
            for (String s:doc.getWords()){
                documentTrie.delete(s, doc);
            }
            oldUris.add(doc.getKey());
            this.privateDeleteFromTable(doc.getKey());
        }
        return oldUris;
    }

    private class DocumentComparator implements Comparator<Document> {
        private String word;
        private DocumentComparator(String word){
            this.word=word;
        }
        @Override
        public int compare(Document o1, Document o2) {
            return Integer.compare( o1.wordCount(word),o2.wordCount(word))*-1;
        }
    }
    private class DocumentComparatorPrefix implements Comparator<Document> {
        private String pre;
        private DocumentComparatorPrefix(String word){
            this.pre=word;
        }
        @Override
        public int compare(Document o1, Document o2) {
            int a=0;
            int b=0;
            for (String s: o1.getWords()){
                if(s.length()>=pre.length() && s.substring(0, pre.length()).equals(pre)){
                    a+= o1.wordCount(s);
                }
            }
            for (String s: o2.getWords()){
                if(s.length()>=pre.length() && s.substring(0, pre.length()).equals(pre)){
                    b+= o2.wordCount(s);
                }
            }

            return Integer.compare(a,b)*-1;
        }
    }
}