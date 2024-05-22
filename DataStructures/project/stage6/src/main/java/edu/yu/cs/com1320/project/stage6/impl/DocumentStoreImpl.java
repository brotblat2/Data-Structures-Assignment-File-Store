
package edu.yu.cs.com1320.project.stage6.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;

import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.stage6.PersistenceManager;
import edu.yu.cs.com1320.project.undo.CommandSet;
import edu.yu.cs.com1320.project.undo.GenericCommand;
import edu.yu.cs.com1320.project.undo.Undoable;

public class DocumentStoreImpl implements DocumentStore {

    private BTreeImpl<URI, Document> store;
    private StackImpl<Undoable> commandStack;
    private TrieImpl<DocSub> documentTrie;
    private MinHeapImpl<DocSub> documentMinHeap;
    private HashMap<String, Set<MetaNode>> metaMap;
    private int docCount;
    private int byteCount;
    private int maxDocumentCount;
    private int maxDocumentBytes;


    public DocumentStoreImpl(){
        this.commandStack= new StackImpl<>();
        this.store= new BTreeImpl<>();
        DocumentPersistenceManager dpm= new DocumentPersistenceManager();
        store.setPersistenceManager(dpm);
        this.documentTrie=new TrieImpl<>();
        this.documentMinHeap= new MinHeapImpl<>();
        this.metaMap=new HashMap<>();
    }

    public DocumentStoreImpl(File baseDir){
        this.commandStack= new StackImpl<>();
        this.store= new BTreeImpl<>();
        DocumentPersistenceManager dpm= new DocumentPersistenceManager(baseDir);
        store.setPersistenceManager(dpm);
        this.documentTrie=new TrieImpl<>();
        this.documentMinHeap= new MinHeapImpl<>();
        this.metaMap=new HashMap<>();
    }

    private class DocSub implements Comparable<DocSub> {
        URI uri;
        private DocSub(Document doc){
            this.uri=doc.getKey();
        }

        private DocSub(URI url){
            this.uri=url;
        }
        private DocumentImpl getDoc(){
            return (DocumentImpl) store.get(this.uri);
        }
        private long getLastUseTime(){
            return store.get(uri).getLastUseTime();
        }
        private URI getKey(){
            return this.uri;
        }

        @Override
        public int hashCode() {
            int result = uri.hashCode();
            result = 31 * result + (store.get(uri).getDocumentTxt() != null ? store.get(uri).getDocumentTxt().hashCode() : 0);
            result = 31 * result + Arrays.hashCode(store.get(uri).getDocumentBinaryData());
            return Math.abs(result);
        }

        @Override
        public boolean equals(Object o){
            if (o instanceof DocSub ){
                if (Objects.equals(this.uri.toString(), ((DocSub) o).getKey().toString())) return true;
            }
            if (o instanceof URI)
                return this.uri.toString().equals(((URI) o).toString());
            return false;
        }

        @Override
        public int compareTo(DocSub o) {
            long dif = this.getLastUseTime() - o.getLastUseTime();
            if (dif < 0) return -1;
            else if (dif > 0) return 1;
            else return 0;
        }
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
        addMetaNode(metaMap, key, new MetaNode(key,value,uri));
        this.store.get(uri).setLastUseTime(System.nanoTime());
        updateHeapAndBtreeStorage(this.store.get(uri));

        Consumer<URI> u = (squash) -> {
            Document doc=this.store.get(uri);
            addMetaNode(metaMap, key, new MetaNode(key,data,uri));
            doc.setMetadataValue(key, data);
            doc.setLastUseTime(System.nanoTime());
            updateHeapAndBtreeStorage(doc);
        };

        GenericCommand<URI> com=new GenericCommand<URI>(uri, u);
        commandStack.push(com);
        return data;
    }

    private void updateHeapAndBtreeStorage(Document doc) {
        try{
            documentMinHeap.reHeapify(new DocSub(doc));
        }
        catch (NoSuchElementException e) {
            //this logic is being done in the btree get method
            // this.store.put(doc.getKey(), doc);
            documentMinHeap.insert(new DocSub(doc));
            byteCount += getDocBytes(doc);
            docCount++;
            if (maxDocumentBytes > 0) {
                while (byteCount > maxDocumentBytes) {
                    try {
                        URI uri=documentMinHeap.remove().getKey();
                        Document removed=store.get(uri);
                        byteCount-=getDocBytes(removed);
                        docCount--;
                        this.store.moveToDisk(uri);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            if (maxDocumentCount > 0) {
                while (docCount > maxDocumentCount) {
                    try {
                        URI uri=documentMinHeap.remove().getKey();
                        Document removed=store.get(uri);
                        byteCount-=getDocBytes(removed);
                        docCount--;
                        this.store.moveToDisk(uri);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
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
        Document doc=this.store.get(uri);
        doc.setLastUseTime(System.nanoTime());
        updateHeapAndBtreeStorage(doc);
        return doc.getMetadataValue(key);
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
        if (this.maxDocumentBytes>0 && input.readAllBytes().length>this.maxDocumentBytes) throw new IllegalArgumentException();

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
            binaryPut(input, uri);
        }
        else {
            byte[] b = input.readAllBytes();
            String s = new String(b);
            DocumentImpl d = new DocumentImpl(uri, s);

            Document oldDoc =this.store.get(uri);

            if (oldDoc!=null) {
                DocSub ods= new DocSub(oldDoc);
                for (String word : oldDoc.getWords()) {
                    documentTrie.delete(word,ods);
                }
                deleteFromHeap(oldDoc);
                byteCount-=getDocBytes(oldDoc);
                docCount--;
            }

            this.store.put(uri, d);
            //put time in to TRIE
            Set<String> wordSet = d.getWords();
            DocSub ds= new DocSub(d);


            for (String word : wordSet)
                documentTrie.put(word, ds);


            for (String key: d.getMetadata().keySet())
                addMetaNode(metaMap, key, new MetaNode(key,d.getMetadataValue(key),uri));


            this.store.get(uri).setLastUseTime(System.nanoTime());
            updateHeapAndBtreeStorage(d);



            Consumer<URI> u = (squash) -> {
                Document newDoc=d;
                //this first if else if is just making sure that we are under the cap
                if ((oldDoc!=null) && oldDoc.getDocumentBinaryData()!=null && oldDoc.getDocumentBinaryData().length>this.maxDocumentBytes && this.maxDocumentBytes>0);
                else if ( (oldDoc!=null) && oldDoc.getDocumentTxt()!=null && oldDoc.getDocumentTxt().getBytes().length>this.maxDocumentBytes && this.maxDocumentBytes>0);

                else {

                    deleteFromHeap(newDoc);

                    for (String word : newDoc.getWords()) {
                        documentTrie.delete(word, ds);
                    }
                    for (String key: d.getMetadata().keySet())
                        addMetaNode(metaMap, key, new MetaNode(key,null,uri));
                    if (oldDoc != null) {
                        for (String key: oldDoc.getMetadata().keySet())
                            addMetaNode(metaMap, key, new MetaNode(key,oldDoc.getMetadataValue(key),uri));
                    }


                    this.byteCount-=b.length;

                    this.docCount--;

                    this.store.put(uri, oldDoc);


                    if (oldDoc != null) {
                        DocSub ods= new DocSub(oldDoc);

                        this.store.get(uri).setLastUseTime(System.nanoTime());
                        updateHeapAndBtreeStorage(oldDoc);

                        if (oldDoc.getWords()!=null) for (String word : ods.getDoc().getWords()) {
                            documentTrie.put(word, ods);
                        }
                    }

                }


            };
            GenericCommand<URI> com = new GenericCommand<>(uri, u);
            commandStack.push(com);
        }
    }

    private void binaryPut(InputStream input, URI uri) throws IOException {
        byte[] b = input.readAllBytes();
        DocumentImpl newDoc= new DocumentImpl(uri, b);

        Document oldDoc=this.store.get(uri);
        //don't think I need to touch the heap honestly, but at least I reheapify
        if (oldDoc!=null)deleteFromHeap(oldDoc);
        this.store.put(uri, newDoc);
        newDoc.setLastUseTime(System.nanoTime());
        updateHeapAndBtreeStorage(newDoc);

        Consumer <URI> u = (squash) -> {
            if (oldDoc!=null && oldDoc.getDocumentBinaryData()!=null && oldDoc.getDocumentBinaryData().length>this.maxDocumentBytes && this.maxDocumentBytes>0);
            else if (oldDoc!=null && oldDoc.getDocumentTxt()!=null && oldDoc.getDocumentTxt().getBytes().length>this.maxDocumentBytes && this.maxDocumentBytes>0);
            else {
                deleteFromHeap(newDoc);

                for (String key: newDoc.getMetadata().keySet())
                    addMetaNode(metaMap, key, new MetaNode(key,null,uri));
                if (oldDoc != null) {
                    for (String key: oldDoc.getMetadata().keySet())
                        addMetaNode(metaMap, key, new MetaNode(key,oldDoc.getMetadataValue(key),uri));
                }

                this.byteCount-=this.getDocBytes(newDoc);
                this.docCount--;
                this.store.put(uri, oldDoc);
                if (oldDoc != null) {
                    DocSub ods= new DocSub(oldDoc);
                    this.store.get(uri).setLastUseTime(System.nanoTime());
                    updateHeapAndBtreeStorage(oldDoc);
                    if (oldDoc.getWords()!=null) for (String word : oldDoc.getWords()) {
                        documentTrie.put(word, ods);
                    }
                }
            }
        };
        GenericCommand<URI> com=new GenericCommand<>(uri, u);
        commandStack.push(com);
    }


    /**
     * @param url the unique identifier of the document to get
     * @return the given document
     */
    public Document get(URI url){
        if (this.store.get(url)!=null) {
            this.store.get(url).setLastUseTime(System.nanoTime());
            updateHeapAndBtreeStorage(this.store.get(url));
        }
        return this.store.get(url);
    }

    /**
     * @param url the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    public boolean delete(URI url) {
        if (this.store.get(url) == null) {
            return false;
        }
        //NEED TO DELETE FROM THE TRIE
        DocumentImpl doc = (DocumentImpl) this.store.get(url);
        doc.setLastUseTime(System.nanoTime());
        if (deleteFromHeap(doc)){
            docCount--;
            byteCount-=this.getDocBytes(doc);
        }
        Set<String> words = doc.getWords();
        for (String word : words) {
            documentTrie.delete(word, new DocSub(doc));
        }
        for (String key: doc.getMetadata().keySet())
            addMetaNode(metaMap, key, new MetaNode(key,null,doc.getKey()));




        this.store.put(url, null);


        GenericCommand<URI> com = undoDeleteCommand(url, doc);
        commandStack.push(com);

        return true;
    }

    private GenericCommand<URI> undoDeleteCommand(URI url, Document doc) {
        Consumer<URI> u = (squash) -> {
            if (doc.getDocumentBinaryData()!=null && doc.getDocumentBinaryData().length>this.maxDocumentBytes && this.maxDocumentBytes>0);
            else if (doc.getDocumentTxt()!=null && doc.getDocumentTxt().getBytes().length>this.maxDocumentBytes && this.maxDocumentBytes>0);
            else {
                this.store.put(url, doc);
                DocSub docSub=new DocSub(doc);
                this.store.get(url).setLastUseTime(System.nanoTime());
                updateHeapAndBtreeStorage(doc);

                for (String key: doc.getMetadata().keySet())
                    addMetaNode(metaMap, key, new MetaNode(key, doc.getMetadataValue(key), doc.getKey()));

                for (String word : doc.getWords()) {
                    this.documentTrie.put(word, docSub);
                }

            }
        };

        GenericCommand<URI> com = new GenericCommand<>(url, u);
        return com;
    }

    private Document privateDeleteFromTable(URI url){
        if (this.store.get(url)==null){
            return null;
        }
        DocumentImpl doc=(DocumentImpl) this.store.put(url, null);
        //NEED TO DELETE FROM THE TRIE
        return doc;
    }

    @Override
    public void undo() throws IllegalStateException {
        //fixed from old assignment
        if (commandStack.size()==0) {throw new IllegalStateException();}
        while (commandStack.peek()==null){
            if (commandStack.size()==0){throw new IllegalStateException();}
            commandStack.pop();
        }
        Undoable c= commandStack.pop();
        c.undo();
    }

    private boolean hasURI(Undoable undoable, URI uri){
        if (undoable instanceof GenericCommand){
            return ((GenericCommand<URI>) undoable).getTarget().toString().equals(uri.toString());
        }
        else{
            return (((CommandSet<URI>)undoable).containsTarget(uri));
        }
    }

    @Override
    public void undo(URI url) throws IllegalStateException {
        StackImpl<Undoable> temp=new StackImpl<>();
        boolean found=false;
        while (commandStack.size()!=0){
            if (commandStack.peek()==null){
                commandStack.pop();
            }
            else if (!(hasURI(commandStack.peek(), url))){
                temp.push(commandStack.pop());
            }
            else{
                Undoable u=commandStack.peek();
                if (u instanceof CommandSet<?>){
                    ((CommandSet)u).undo(url);
                    if(((CommandSet)u).size()==0) commandStack.pop();
                }
                else {
                    u.undo();
                    commandStack.pop();
                }
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
       //make a comparator
        List<DocSub> list=  documentTrie.getSorted(keyword, new DocSubComparator(keyword));
        if (list==null || list.isEmpty()) return new ArrayList<>();

        List<Document> lis= new ArrayList<>();
        for (DocSub ds:list){
            Document doc= ds.getDoc();
            if (doc!=null){
                lis.add(doc);
                doc.setLastUseTime(System.nanoTime());
                updateHeapAndBtreeStorage(doc);
            }
        }


        return lis;
    }

    private List<Document> privateSearch(String keyword) {
        //make a comparator
        List<DocSub> list=  documentTrie.getSorted(keyword, new DocSubComparator(keyword));
        if (list==null || list.isEmpty()) return new ArrayList<>();

        List<Document> lis= new ArrayList<>();
        for (DocSub ds:list){
            if (ds.getDoc()!=null) lis.add(ds.getDoc());        }

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
        List<DocSub> list=  documentTrie.getAllWithPrefixSorted(keywordPrefix, new DocSubComparatorPrefix(keywordPrefix));

        if (list==null || list.isEmpty()) return new ArrayList<>();

        List<Document> lis= new ArrayList<>();

        for (DocSub ds:list) {
            Document doc = ds.getDoc();
            if (doc != null) {
                lis.add(doc);
                doc.setLastUseTime(System.nanoTime());
                updateHeapAndBtreeStorage(doc);
            }
        }

        return lis;

    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * Search is CASE SENSITIVE.
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAll(String keyword) {
        Set<DocSub> oldDocs= new HashSet<>( documentTrie.get(keyword));
        if (oldDocs==null || oldDocs.isEmpty()) return new HashSet<>();
        Set<URI> oldUris= new HashSet<>();
        for(DocSub d:oldDocs) {
            //ask this shaila, assuming no for now regarding the setting last time when deleted
            //d.getDoc().setLastUseTime(System.nanoTime());
            //documentMinHeap.reHeapify(d);
            oldUris.add(d.getKey());
        }


        CommandSet<URI> cset= new CommandSet<>();

        for(URI uri:oldUris){
            cset.addCommand(undoDeleteCommand(uri, this.store.get(uri)));
        }

        for(DocSub d:oldDocs){
            if (d.getDoc()!=null){
                for (String s:d.getDoc().getWords()){
                    documentTrie.delete(s,d);
                }
                for (String key: d.getDoc().getMetadata().keySet())
                    addMetaNode(metaMap, key, new MetaNode(key,null,d.getKey()));
                if (deleteFromHeap(d)) {
                    byteCount -= getDocBytes(d.getDoc());
                    docCount--;
                }
            }
        }


        for(URI u:oldUris){
            this.privateDeleteFromTable(u);
        }

        commandStack.push(cset);
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
        Set<DocSub> oldDocs= new HashSet<>( documentTrie.getAllWithPrefixSorted(keywordPrefix,new DocSubComparatorPrefix(keywordPrefix) ) );
        if (oldDocs==null || oldDocs.isEmpty()) return new HashSet<>();
        Set<URI> oldUris= new HashSet<>();
        for(DocSub d:oldDocs) {
            //ask this shaila, assuming no for now regarding the setting last time when deleted
            //d.getDoc().setLastUseTime(System.nanoTime());
            //documentMinHeap.reHeapify(d);
            oldUris.add(d.getKey());
        }


        CommandSet<URI> cset= new CommandSet<>();

        for(URI uri:oldUris){
            cset.addCommand(undoDeleteCommand(uri, this.store.get(uri)));
        }

        for(DocSub d:oldDocs){
            for (String s:d.getDoc().getWords()){
                documentTrie.delete(s,d);
            }
            for (String key: d.getDoc().getMetadata().keySet())
                addMetaNode(metaMap, key, new MetaNode(key,null,d.getKey()));

            if (deleteFromHeap(d)) {
                byteCount -= getDocBytes(d.getDoc());
                docCount--;
            }
        }

        for(URI u:oldUris){
            this.privateDeleteFromTable(u);
        }

        commandStack.push(cset);
        return oldUris;
    }


    private class MetaNode{
        String key;
        String value;
        URI uri;
        private MetaNode(String key, String value, URI uri){
            this.key=key;
            this.value=value;
            this.uri=uri;
        }

        @Override
        public boolean equals(Object o) {
            // Check for self-comparison
            if (o instanceof MetaNode && ((MetaNode)o).hashCode()==this.hashCode()) return true;
            return false;
        }
        @Override
        public int hashCode() {
            return Objects.hash(uri.toString(),value);
        }

    }
    private void addMetaNode(Map<String, Set<MetaNode>> mm, String key, MetaNode mn) {

        Set<MetaNode> set = mm.get(key);
        if (mn.value==null){
            if (set==null) {
                return;
            }

            set.removeIf(m -> m.uri.toString().equals(mn.uri.toString()));
            return;

        }
        if (set == null) {
            set = new HashSet<>();
            mm.put(key, set);
        }
        set.add(mn);
    }

    /**
     * @param keysValues metadata key-value pairs to search for
     * @return a List of all documents whose metadata contains ALL OF the given values for the given keys. If no documents contain all the given key-value pairs, return an empty list.
     */

    @Override
    public List<Document> searchByMetadata(Map<String, String> keysValues) {
        List<Document> docsList=new ArrayList<>();
        List<URI> uriList= privateSearchByMetadata(keysValues);
        for(URI uri: uriList){
            docsList.add(store.get(uri));
        }

        for (Document d:docsList) {
            d.setLastUseTime(System.nanoTime());
            updateHeapAndBtreeStorage(d);
        }
        return docsList;
    }

//UP TO HERE!!!
    private List<URI> privateSearchByMetadata(Map<String, String> keysValues) {
        if (keysValues.isEmpty()) return new ArrayList<>();

        List<String> list= new ArrayList<>(keysValues.keySet());
        Set<URI> uriSet=new HashSet<>();
        Set<MetaNode> mns=new HashSet<>(metaMap.get(list.get(0)));

        for(MetaNode mn:mns){
            if (mn.value.equals(keysValues.get(list.get(0)))) uriSet.add(mn.uri);
        }

        for (String key:list){
            Set<MetaNode> metaNodeSet=metaMap.get(key);
            Set<URI> tempUriSet=new HashSet<>();
            for (MetaNode metaNode: metaNodeSet){
                if(metaNode.value.equals(keysValues.get(key))) tempUriSet.add(metaNode.uri);
            }
            uriSet.retainAll(tempUriSet);

        }
        return new ArrayList<>(uriSet);
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


        List<URI> uriList= privateSearchByMetadata(keysValues);
        if(uriList.isEmpty()) return new ArrayList<>();

        List<DocSub> list=new ArrayList<>(documentTrie.get(keyword));
        if( list.isEmpty()) return new ArrayList<>();

        Set<URI> uriList2= new HashSet<>();
        for (DocSub ds:list){uriList2.add(ds.getKey()); }

        uriList.retainAll(uriList2);

        List<Document> docsList=new ArrayList<>();
        for(URI uri:uriList){
            docsList.add(store.get(uri));
        }

        for (Document d:docsList) {
            d.setLastUseTime(System.nanoTime());
            updateHeapAndBtreeStorage(d);
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
        List<URI> uriList= privateSearchByMetadata(keysValues);
        if(uriList.isEmpty()) return new ArrayList<>();

        List<DocSub> list = new ArrayList<>(documentTrie.getAllWithPrefixSorted(keywordPrefix, new DocSubComparator(keywordPrefix)));
        if( list.isEmpty()) return new ArrayList<>();

        Set<URI> uriList2= new HashSet<>();
        for (DocSub ds:list){uriList2.add(ds.getKey()); }

        uriList.retainAll(uriList2);

        List<Document> docsList=new ArrayList<>();
        for(URI uri:uriList){
            docsList.add(store.get(uri));
        }

        for (Document d:docsList) {
            d.setLastUseTime(System.nanoTime());
            updateHeapAndBtreeStorage(d);
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
        List<Document> oldDocs= searchByMetadata(keysValues);
        Set<URI> oldUris= new HashSet<>();
        for(Document d:oldDocs) {
            oldUris.add(d.getKey());
        }

        if (oldDocs==null || oldDocs.isEmpty()) return new HashSet<>();

        CommandSet<URI> cset= new CommandSet<>();

        for(URI uri:oldUris){
            cset.addCommand(undoDeleteCommand(uri, this.store.get(uri)));
        }

        for(Document d:oldDocs){
            for (String s:d.getWords()){
                documentTrie.delete(s,new DocSub(d.getKey()));
            }
            for (String key: d.getMetadata().keySet())
                addMetaNode(metaMap, key, new MetaNode(key,null,d.getKey()));

            if (deleteFromHeap(d)) {
                byteCount -= getDocBytes(d);
                docCount--;
            }
        }

        for(URI u:oldUris){
            this.privateDeleteFromTable(u);
        }

        commandStack.push(cset);
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
        List<Document> oldDocs= searchByKeywordAndMetadata(keyword,keysValues);
        Set<URI> oldUris= new HashSet<>();
        for(Document d:oldDocs) {
            oldUris.add(d.getKey());
        }

        if (oldDocs==null || oldDocs.isEmpty()) return new HashSet<>();

        CommandSet<URI> cset= new CommandSet<>();

        for(URI uri:oldUris){
            cset.addCommand(undoDeleteCommand(uri, this.store.get(uri)));
        }

        for(Document d:oldDocs){
            for (String s:d.getWords()){
                documentTrie.delete(s,new DocSub(d.getKey()));
            }
            for (String key: d.getMetadata().keySet())
                addMetaNode(metaMap, key, new MetaNode(key,null,d.getKey()));
            if (deleteFromHeap(d)) {
                byteCount -= getDocBytes(d);
                docCount--;
            }
        }

        for(URI u:oldUris){
            this.privateDeleteFromTable(u);
        }

        commandStack.push(cset);
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
        List<Document> oldDocs= searchByPrefixAndMetadata(keywordPrefix,keysValues);
        Set<URI> oldUris= new HashSet<>();
        for(Document d:oldDocs) {
            oldUris.add(d.getKey());
        }

        if (oldDocs==null || oldDocs.isEmpty()) return new HashSet<>();

        CommandSet<URI> cset= new CommandSet<>();

        for(URI uri:oldUris){
            cset.addCommand(undoDeleteCommand(uri, this.store.get(uri)));
        }

        for(Document d:oldDocs){
            for (String s:d.getWords()){
                documentTrie.delete(s,new DocSub(d.getKey()));
            }
            for (String key: d.getMetadata().keySet())
                addMetaNode(metaMap, key, new MetaNode(key,null,d.getKey()));
            if (deleteFromHeap(d)) {
                byteCount -= getDocBytes(d);
                docCount--;
            }
        }

        for(URI u:oldUris){
            this.privateDeleteFromTable(u);
        }

        commandStack.push(cset);
        return oldUris;
    }

    @Override
    public void setMaxDocumentCount(int limit) {
        if(limit<1) throw new IllegalArgumentException();
        this.maxDocumentCount=limit;

        if (this.docCount>limit){
            int p=this.docCount-limit;
            List<Document> docList= new ArrayList<>();
            for (int i=0; i<p; i++){
                try {
                    Document d= documentMinHeap.remove().getDoc();
                    this.docCount--;
                    this.byteCount-=getDocBytes(d);
                    store.moveToDisk(d.getKey());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }


//    private List<URI> removeFromStackPastLimit(List<Document> docList) {
//        List<URI> uriList=new ArrayList<>();
//
//        for (Document doc : docList){
//            uriList.add(doc.getKey());
//        }
//
//        StackImpl<Undoable> temp= new StackImpl<>();
//
//        while(commandStack.size()!=0){
//
//            if (commandStack.peek() instanceof GenericCommand<?>) {
//
//                GenericCommand<URI> peek = (GenericCommand<URI>) commandStack.pop();
//
//                if (!uriList.contains(peek.getTarget())) {
//                    temp.push(peek);
//                }
//            }
//
//            else{
//                CommandSet<URI> peek = (CommandSet<URI>) commandStack.pop();
//                CommandSet<URI> rep=new CommandSet<>();
//                if (peek!=null) for(GenericCommand<URI> gc:peek){
//                    if (!(uriList.contains(gc.getTarget()))){
//                        rep.addCommand(gc);
//                    }
//                }
//                if (rep.size()!=0) temp.push(rep);
//            }
//
//        }
//        while (temp.size()!=0){
//            this.commandStack.push(temp.pop());
//        }
//        return uriList;
//    }

    @Override
    public void setMaxDocumentBytes(int limit) {
        if(limit<1) throw new IllegalArgumentException();

        this.maxDocumentBytes=limit;

        if (this.byteCount>limit){
            int p=this.byteCount-limit;
            List<Document> docList= new ArrayList<>();
            while(this.byteCount>limit){
                try {
                    Document d= documentMinHeap.remove().getDoc();
                    this.docCount--;
                    this.byteCount-=getDocBytes(d);
                    store.moveToDisk(d.getKey());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }
    private void makeSpace(){
        if (this.maxDocumentCount>0) setMaxDocumentCount(this.maxDocumentCount);
        if (this.maxDocumentBytes>0) setMaxDocumentBytes(this.maxDocumentBytes);
    }

    private boolean deleteFromHeap(Document doc){
        return deleteFromHeap(new DocSub(doc.getKey()));
    }
    private boolean deleteFromHeap(DocSub doc){
        if (doc==null) return false;
        MinHeapImpl<DocSub> temp=new MinHeapImpl<>();
        boolean b=false;
        while (this.documentMinHeap.peek()!=null){
            DocSub d=this.documentMinHeap.remove();
            if (!d.equals(doc)){
                temp.insert(d);
            }
            if (d.equals(doc)) b=true;
        }
        this.documentMinHeap=temp;
        return b;
    }


    private int getDocBytes(Document doc) {
        if (doc==null) return 0;
        if (doc.getWords().isEmpty()){
            return doc.getDocumentBinaryData().length;
        }
        else{
            return doc.getDocumentTxt().getBytes().length;
        }
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
    private class DocSubComparator implements Comparator<DocSub> {
        private String word;
        private DocSubComparator(String word){
            this.word=word;
        }
        @Override
        public int compare(DocSub o1, DocSub o2) {
            //if (o1.getDoc()==null || o1.getDoc()==null)
                //this is a guess- maybe ask on Piazza
            return Integer.compare( o1.getDoc().wordCount(word),o2.getDoc().wordCount(word))*-1;
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
    private class DocSubComparatorPrefix implements Comparator<DocSub> {
        private String pre;
        private DocSubComparatorPrefix(String word){
            this.pre=word;
        }
        @Override
        public int compare(DocSub o1, DocSub o2) {
            int a=0;
            int b=0;
            for (String s: o1.getDoc().getWords()){
                if(s.length()>=pre.length() && s.substring(0, pre.length()).equals(pre)){
                    a+= o1.getDoc().wordCount(s);
                }
            }
            for (String s: o2.getDoc().getWords()){
                if(s.length()>=pre.length() && s.substring(0, pre.length()).equals(pre)){
                    b+= o2.getDoc().wordCount(s);
                }
            }

            return Integer.compare(a,b)*-1;
        }
    }
}
