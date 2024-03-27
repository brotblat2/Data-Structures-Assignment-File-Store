package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DocumentStoreImplTest2 {
    DocumentStoreImpl dstore;
    URI uri;
    URI uri1;
    URI uri2;

    @BeforeEach
    void setUp() throws IOException {
        dstore = new DocumentStoreImpl();

        uri = URI.create("http://GA.com");
        String GA = "Four score and seven years ago our fathers brought forth on this continent a new nation, conceived in liberty, and dedicated to the proposition that all men are created equal. “Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battlefield of that war. We have come to dedicate a portion of that field as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this. “But in a larger sense we cannot dedicate, we cannot consecrate, we cannot hallow this ground. The brave men, living and dead, who struggled here have consecrated it, far above our poor power to add or detract. The world will little note, nor long remember, what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us,that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion, that we here highly resolve that these dead shall not have died in vain, that this nation, under God, shall have a new birth of freedom, and that government of the people, by the people, for the people, shall not perish from the earth.";
         uri1 = URI.create("http://DoI.com");
        String DoI = "The unanimous Declaration of the thirteen united States of America, When in the Course of human events, it becomes necessary for one people to dissolve the political bands which have connected them with another, and to assume among the powers of the earth, the separate and equal station to which the Laws of Nature and of Nature's God entitle them, a decent respect to the opinions of mankind requires that they should declare the causes which impel them to the separation.";
         uri2 = URI.create("http://FA.com");
        String FA ="Congress shall make no law respecting an establishment of religion, or prohibiting the free exercise thereof; or abridging the freedom of speech, or of the press; or the right of the people peaceably to assemble, and to petition the Government for a redress of grievances.";
        byte[] b = GA.getBytes();
        byte[] b1 = DoI.getBytes();
        byte[] b2 = FA.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);


        dstore.put(stream, uri, DocumentStore.DocumentFormat.TXT);
        dstore.setMetadata(uri, "year", "1863");
        dstore.setMetadata(uri, "author", "Lincoln");
        dstore.setMetadata(uri, "color", "none");
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.TXT);
        dstore.setMetadata(uri1, "year", "1776");
        dstore.setMetadata(uri1, "author", "Washington");
        dstore.setMetadata(uri1, "color", "white");
        dstore.put(stream2, uri2, DocumentStore.DocumentFormat.TXT);
        dstore.setMetadata(uri2, "year", "1789");
        dstore.setMetadata(uri2, "color", "white");



    }

    @Test
    void putCheckTable() throws IOException {
        setUp();

        URI uriT = URI.create("http://example.com");
        String s= "hello";
        byte[] bytes = s.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        dstore.put(stream,uriT, DocumentStore.DocumentFormat.TXT );

        assertEquals(dstore.get(uriT).getDocumentTxt(), s);
    }

    @Test
    void putCheckTrie() throws IOException {
        setUp();
        List<DocumentImpl> set= new ArrayList<>();

        String GA1 = "Four score and seven years ago our fathers brought forth on this continent a new nation, conceived in liberty, and dedicated to the proposition that all men are created equal. “Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battlefield of that war. We have come to dedicate a portion of that field as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this. “But in a larger sense we cannot dedicate, we cannot consecrate, we cannot hallow this ground. The brave men, living and dead, who struggled here have consecrated it, far above our poor power to add or detract. The world will little note, nor long remember, what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us,that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion, that we here highly resolve that these dead shall not have died in vain, that this nation, under God, shall have a new birth of freedom, and that government of the people, by the people, for the people, shall not perish from the earth.";
        DocumentImpl doc= new DocumentImpl(uri,GA1 );
        set.add(doc);
        List<Document> s=dstore.search("score");
        assertTrue(s.containsAll(set));
    }


    @Test
    void delete() throws IOException {
        setUp();
        assertEquals(dstore.search("score").size(), 1);
        assertEquals(dstore.get(uri).getDocumentTxt(),"Four score and seven years ago our fathers brought forth on this continent a new nation, conceived in liberty, and dedicated to the proposition that all men are created equal. “Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battlefield of that war. We have come to dedicate a portion of that field as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this. “But in a larger sense we cannot dedicate, we cannot consecrate, we cannot hallow this ground. The brave men, living and dead, who struggled here have consecrated it, far above our poor power to add or detract. The world will little note, nor long remember, what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us,that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion, that we here highly resolve that these dead shall not have died in vain, that this nation, under God, shall have a new birth of freedom, and that government of the people, by the people, for the people, shall not perish from the earth.");
        dstore.delete(uri);
        assertEquals(dstore.search("score").size(), 0);
        assertNull(dstore.get(uri));
    }

    @Test
    void searchIsSorted() throws IOException {
        dstore = new DocumentStoreImpl();

        uri = URI.create("http://GA.com");
        String GA = "kugel kugel Four score and seven years ago our fathers brought forth on this continent a new nation, conceived in liberty, and dedicated to the proposition that all men are created equal. “Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battlefield of that war. We have come to dedicate a portion of that field as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this. “But in a larger sense we cannot dedicate, we cannot consecrate, we cannot hallow this ground. The brave men, living and dead, who struggled here have consecrated it, far above our poor power to add or detract. The world will little note, nor long remember, what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us,that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion, that we here highly resolve that these dead shall not have died in vain, that this nation, under God, shall have a new birth of freedom, and that government of the people, by the people, for the people, shall not perish from the earth.";
        uri1 = URI.create("http://DoI.com");
        String DoI = "kugel The unanimous Declaration of the thirteen united States of America, When in the Course of human events, it becomes necessary for one people to dissolve the political bands which have connected them with another, and to assume among the powers of the earth, the separate and equal station to which the Laws of Nature and of Nature's God entitle them, a decent respect to the opinions of mankind requires that they should declare the causes which impel them to the separation.";
        uri2 = URI.create("http://FA.com");
        String FA ="kugel kugel kugel Congress shall make no law respecting an establishment of religion, or prohibiting the free exercise thereof; or abridging the freedom of speech, or of the press; or the right of the people peaceably to assemble, and to petition the Government for a redress of grievances.";
        byte[] b = GA.getBytes();
        byte[] b1 = DoI.getBytes();
        byte[] b2 = FA.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);


        dstore.put(stream, uri, DocumentStore.DocumentFormat.TXT);
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.TXT);
        dstore.put(stream2, uri2, DocumentStore.DocumentFormat.TXT);

        List<DocumentImpl> lis= new ArrayList<>();
        lis.add(new DocumentImpl(uri2,FA));
        lis.add(new DocumentImpl(uri,GA));
        lis.add(new DocumentImpl(uri1,DoI));

        List<Document> list=dstore.search("kugel");
        assertEquals(lis.get(0).getDocumentTxt(), list.get(0).getDocumentTxt());
        assertEquals(lis.get(1).getDocumentTxt(), list.get(1).getDocumentTxt());
        assertEquals(lis.get(2).getDocumentTxt(), list.get(2).getDocumentTxt());


    }

    @Test

    void searchByPrefix() throws IOException {

        dstore = new DocumentStoreImpl();

        uri = URI.create("http://GA.com");
        String GA = "98Four 98score and seven years ago our fathers brought forth on this continent a new nation, conceived in liberty, and dedicated to the proposition that all men are created equal. “Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battlefield of that war. We have come to dedicate a portion of that field as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this. “But in a larger sense we cannot dedicate, we cannot consecrate, we cannot hallow this ground. The brave men, living and dead, who struggled here have consecrated it, far above our poor power to add or detract. The world will little note, nor long remember, what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us,that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion, that we here highly resolve that these dead shall not have died in vain, that this nation, under God, shall have a new birth of freedom, and that government of the people, by the people, for the people, shall not perish from the earth.";
        uri1 = URI.create("http://DoI.com");
        String DoI = "98The unanimous Declaration of the thirteen united States of America, When in the Course of human events, it becomes necessary for one people to dissolve the political bands which have connected them with another, and to assume among the powers of the earth, the separate and equal station to which the Laws of Nature and of Nature's God entitle them, a decent respect to the opinions of mankind requires that they should declare the causes which impel them to the separation.";
        uri2 = URI.create("http://FA.com");
        String FA ="98Congress 98shall 98make 98no 98law 98respecting 98an 98establishment of religion, or prohibiting the free exercise thereof; or abridging the freedom of speech, or of the press; or the right of the people peaceably to assemble, and to petition the Government for a redress of grievances.";
        byte[] b = GA.getBytes();
        byte[] b1 = DoI.getBytes();
        byte[] b2 = FA.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);


        dstore.put(stream, uri, DocumentStore.DocumentFormat.TXT);
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.TXT);
        dstore.put(stream2, uri2, DocumentStore.DocumentFormat.TXT);

        List<DocumentImpl> lis= new ArrayList<>();
        lis.add(new DocumentImpl(uri2,FA));
        lis.add(new DocumentImpl(uri,GA));
        lis.add(new DocumentImpl(uri1,DoI));

        List<Document> list=dstore.searchByPrefix("98");
        assertEquals(lis.get(0).getDocumentTxt(), list.get(0).getDocumentTxt());
        assertEquals(lis.get(1).getDocumentTxt(), list.get(1).getDocumentTxt());
        assertEquals(lis.get(2).getDocumentTxt(), list.get(2).getDocumentTxt());

    }



    @Test
    void deleteAll() throws IOException {
        setUp();

        Set<URI> set= new HashSet<>();

       // String GA1 = "Four score and seven years ago our fathers brought forth on this continent a new nation, conceived in liberty, and dedicated to the proposition that all men are created equal. “Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battlefield of that war. We have come to dedicate a portion of that field as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this. “But in a larger sense we cannot dedicate, we cannot consecrate, we cannot hallow this ground. The brave men, living and dead, who struggled here have consecrated it, far above our poor power to add or detract. The world will little note, nor long remember, what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us,that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion, that we here highly resolve that these dead shall not have died in vain, that this nation, under God, shall have a new birth of freedom, and that government of the people, by the people, for the people, shall not perish from the earth.";
        //  DocumentImpl doc= new DocumentImpl(uri,GA1 );
        set.add(uri);
        Set<URI> set2 = dstore.deleteAll("score");
        //Got URI's
        assertTrue(set2.containsAll(set));
        //No longer in Trie
        assertEquals(0, dstore.search("score").size());
        //Other word no longer in trie
        List<Document> docs=dstore.search("the");
        assertEquals(2,docs.size() );
        //No longer in HashTable
        assertNull(dstore.get(uri));

    }

    @Test
    void deleteAllWithPrefix() throws IOException {
        setUp();

        Set<URI> set= new HashSet<>();
        List<Document> docList=dstore.search("the");
        assertEquals(3,docList.size() );
        // String GA1 = "Four score and seven years ago our fathers brought forth on this continent a new nation, conceived in liberty, and dedicated to the proposition that all men are created equal. “Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battlefield of that war. We have come to dedicate a portion of that field as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this. “But in a larger sense we cannot dedicate, we cannot consecrate, we cannot hallow this ground. The brave men, living and dead, who struggled here have consecrated it, far above our poor power to add or detract. The world will little note, nor long remember, what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us,that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion, that we here highly resolve that these dead shall not have died in vain, that this nation, under God, shall have a new birth of freedom, and that government of the people, by the people, for the people, shall not perish from the earth.";
        //  DocumentImpl doc= new DocumentImpl(uri,GA1 );
        set.add(uri);
        // UP TO HERE LETS FIGURE OUT THIS NULL POINTER EXCEPTION!!!!
        Set<URI> set2 = dstore.deleteAllWithPrefix("scor");
        //Got URI's
        assertTrue(set2.containsAll(set));
        //No longer in Trie
        assertEquals(0, dstore.searchByPrefix("scor").size());
        //Other word no longer in trie
        List<Document> docs=dstore.search("the");
        assertEquals(2,docs.size() );
        //No longer in HashTable
        assertNull(dstore.get(uri));
    }

    @Test
    void searchByMetadata() throws IOException {
        setUp();
        HashMap<String, String> map= new HashMap<>();
        map.put ("year", "1863");
        map.put ("author", "Lincoln");
        assertEquals(dstore.searchByMetadata(map).get(0).getKey(),uri);
        assertEquals(dstore.searchByMetadata(map).size(), 1);

        map.clear();
        map.put("color", "white");
        assertEquals(dstore.searchByMetadata(map).size(), 2);
        map.put("horse", "yellow");
        assertEquals(dstore.searchByMetadata(map).size(), 0);


    }

    @Test
    void searchByKeywordAndMetadata() throws IOException {
        setUp();
        HashMap<String, String> map= new HashMap<>();
        map.put ("year", "1863");
        map.put ("author", "Lincoln");
        assertEquals(dstore.searchByKeywordAndMetadata("score",map).get(0).getKey(),uri);
        assertEquals(dstore.searchByKeywordAndMetadata("score",map).size(), 1);
        assertEquals(dstore.searchByKeywordAndMetadata("zebra",map).size(), 0);

        map.clear();

        map.put("color", "white");
        assertEquals(dstore.searchByKeywordAndMetadata("and",map).size(), 2);

        map.put("horse", "yellow");
        assertEquals(dstore.searchByKeywordAndMetadata("and",map).size(), 0);
    }

    @Test
    void searchByPrefixAndMetadata() throws IOException {
        setUp();
        HashMap<String, String> map= new HashMap<>();
        map.put ("year", "1863");
        map.put ("author", "Lincoln");
        assertEquals(dstore.searchByPrefixAndMetadata("sc",map).get(0).getKey(),uri);
        assertEquals(dstore.searchByPrefixAndMetadata("sc",map).size(), 1);
        assertEquals(dstore.searchByPrefixAndMetadata("zeb",map).size(), 0);

        map.clear();

        map.put("color", "white");
        assertEquals(dstore.searchByPrefixAndMetadata("peo",map).size(), 2);

        map.put("horse", "yellow");
        assertEquals(dstore.searchByPrefixAndMetadata("and",map).size(), 0);
    }


    @Test
    void deleteAllWithMetadata() throws IOException {
        setUp();
        HashMap<String, String> map= new HashMap<>();
        map.put ("year", "1863");
        map.put ("author", "Lincoln");

        Set<URI> set= new HashSet<>();

        set.add(uri);

        // UP TO HERE LETS FIGURE OUT
        Set<URI> set2 = dstore.deleteAllWithMetadata(map);
        //Got URI's
        assertTrue(set2.containsAll(set));
        //No longer in Trie
        assertEquals(0, dstore.searchByMetadata(map).size());
        //Other word no longer in trie
        List<Document> docs=dstore.search("the");
        assertEquals(2,docs.size() );
        //No longer in HashTable
        assertNull(dstore.get(uri));
    }

    @Test
    void deleteAllWithKeywordAndMetadata() throws IOException {
        setUp();
        HashMap<String, String> map= new HashMap<>();
        map.put ("year", "1863");
        map.put ("author", "Lincoln");

        Set<URI> set= new HashSet<>();
        set.add(uri);

        Set<URI> set9 = dstore.deleteAllWithKeywordAndMetadata("sandwich",map);
        assertEquals(0,set9.size());
        // UP TO HERE LETS FIGURE OUT
        Set<URI> set2 = dstore.deleteAllWithKeywordAndMetadata("the",map);
        //Got URI's
        assertTrue(set2.containsAll(set));
        //No longer in Trie
        assertEquals(0, dstore.searchByMetadata(map).size());
        //Other word no longer in trie
        List<Document> docs=dstore.search("the");
        assertEquals(2,docs.size() );
        //No longer in HashTable
        assertNull(dstore.get(uri));
    }

    @Test
    void deleteAllWithPrefixAndMetadata() throws IOException {
        setUp();
        HashMap<String, String> map= new HashMap<>();
        map.put ("year", "1863");
        map.put ("author", "Lincoln");
        Set<URI> set= new HashSet<>();
        set.add(uri);

        Set<URI> set9 = dstore.deleteAllWithPrefixAndMetadata("9292",map);
        assertEquals(0,set9.size());
        // UP TO HERE LETS FIGURE OUT
        Set<URI> set2 = dstore.deleteAllWithPrefixAndMetadata("t",map);
        //Got URI's
        assertTrue(set2.containsAll(set));
        //No longer in Trie
        assertEquals(0, dstore.searchByMetadata(map).size());
        //Other word no longer in trie
        List<Document> docs=dstore.searchByPrefix("t");
        assertEquals(2,docs.size());
        //No longer in HashTable
        assertNull(dstore.get(uri));
        map.clear();
        map.put("color", "white");
        Set<URI> uriSet=dstore.deleteAllWithPrefixAndMetadata("t", map);
        assertTrue(uriSet.contains(uri1));
        assertTrue(uriSet.contains(uri2));
        assertFalse(uriSet.contains(uri));
    }
}