package edu.yu.cs.com1320.project.stage3.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Consumer;

import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;
import edu.yu.cs.com1320.project.undo.Command;

public class DocumentStoreImpl implements DocumentStore {

    private HashTableImpl<URI, DocumentImpl> store;
    private StackImpl<Command> commandStack;

    public DocumentStoreImpl(){
        this.commandStack= new StackImpl<>();
        this.store= new HashTableImpl<>();
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
            if (this.delete(uri)){
                return x;
            }
            else{
                return 0;
            }
        }
        // return value
        if (store.get(uri)==null){
            x=0 ;
        }
        else{
            x= store.get(uri).hashCode();
        }

        //Actual implementation
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
            DocumentImpl doc=this.store.put(uri, d);
            Consumer <URI> u = (squash) -> {
                this.store.put(uri, doc);
            };
            Command com=new Command(uri, u);
            commandStack.push(com);
        }
        return x;

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
        DocumentImpl doc= this.store.put(url, null);
            Consumer <URI> u = (squash) -> {
               this.store.put(url, doc);
            };
            Command com=new Command(url, u);
            commandStack.push(com);
            return true;
    }

    @Override
    public void undo() throws IllegalStateException {
        Command c= commandStack.pop();
        c.undo();
    }

    @Override
    public void undo(URI url) throws IllegalStateException {
        StackImpl<Command> temp=new StackImpl<>();
        while (commandStack.peek()!=null){
            if (!(commandStack.peek().getUri().toString().equals(url.toString()))){
                temp.push(commandStack.pop());
            }
            else{
               Command undone=commandStack.pop();
            }
        }
        while (temp.peek()!=null){
            commandStack.push(temp.pop());
        }
        }
}