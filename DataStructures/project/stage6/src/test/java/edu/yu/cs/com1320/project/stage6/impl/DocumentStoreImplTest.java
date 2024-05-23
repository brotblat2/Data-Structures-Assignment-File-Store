package edu.yu.cs.com1320.project.stage6.impl;

import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class DocumentStoreImplTest {
    DocumentStoreImpl dstore;
    URI uri;
    URI uri1;
    URI uri2;

    @BeforeEach
    void setUp() throws IOException {


        File testing=new File("testing");
        testing.mkdirs();
        for (File file : testing.listFiles()) {
            file.delete();
        }

        dstore = new DocumentStoreImpl(testing);

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
    void checkHeapOrder() throws IOException {
        setUp();
        dstore.setMaxDocumentCount(1);
        dstore.setMetadata(uri, "year", "1863");
        dstore.get(uri1);
        dstore.getMetadata(uri2, "year");
        dstore.delete(uri);

        dstore.undo();

        URI url = URI.create("http://foxuu.com");
        assertEquals(3, dstore.search("the").size());

        byte[] b = {1, 2, 3, 4};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        dstore.put(stream, url, DocumentStore.DocumentFormat.BINARY);
        dstore.setMetadata(url, "color","blue");


        dstore.setMaxDocumentBytes(1);
        assertEquals(3, dstore.search("the").size());
        String s=url.toString().substring(7);
        s+=".json";
        s="testing/" + s;
        assertTrue(Files.deleteIfExists(Path.of(s)));
    }

    @Test
    void checkHeapOrder2() throws IOException {
        setUp();
        URI url = URI.create("http://foxuu.com");
        byte[] b = {1, 2, 3, 4};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        dstore.put(stream, url, DocumentStore.DocumentFormat.BINARY);
        dstore.setMetadata(url, "color","blue");


        dstore.setMetadata(uri, "year", "1863");
        dstore.get(uri1);
        dstore.getMetadata(uri2, "year");
        dstore.delete(uri);
        dstore.undo();

        dstore.deleteAll("freedom");

        assertEquals(1, dstore.search("the").size());





        assertEquals(1, dstore.search("the").size());

        assertFalse(Files.deleteIfExists(Path.of((dstore.search("the").get(0).getKey().toString().substring(7)) + ".json")));
    }
    @Test
    void checkHeapOrder3() throws IOException {
        setUp();
        URI url = URI.create("http://foxuu.com");
        byte[] b = {1, 2, 3, 4};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        dstore.put(stream, url, DocumentStore.DocumentFormat.BINARY);

        dstore.setMaxDocumentBytes(4);


        assertEquals(3, dstore.search("the").size());

        assertNotNull(dstore.get(url));
    }
    @Test
    void checkHeapOrder4() throws IOException {
        setUp();
        dstore.setMaxDocumentCount(1);

        String s="testing"+ (File.separator) + uri.toString().substring(7);
        s+=".json";

        assertTrue(Files.deleteIfExists(Path.of(s)));

        assertNull(dstore.get(uri));
        assertNotNull(dstore.get(uri1));
        assertNotNull(dstore.get(uri2));

    }
    @Test
    void setMetadataError() throws IOException {
        URI uri = URI.create("http://fox.com");
        String s = "hello";
        URI uri1 = URI.create("http://cnn.com");
        String s1 = "hey";
        byte[] b = {1, 2, 3, 4};
        byte[] b1 = {41, 42, 43, 44, 45};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        URI uri2 = null;
        assertThrows(IllegalArgumentException.class, () -> {
            dstore.setMetadata(uri2, "key", "value");
        });

    }

    @Test
    void setMetadataErrorifnosuchuri() throws IOException {
        URI uri = URI.create("http://fox.com");
        String s = "hello";
        URI uri1 = URI.create("http://cnn.com");
        String s1 = "hey";
        byte[] b = {1, 2, 3, 4};
        byte[] b1 = {41, 42, 43, 44, 45};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        assertThrows(IllegalArgumentException.class, () -> {
            dstore.setMetadata(uri1, "key", "value");
        });
    }

    @Test
    void setMetadataCheck() throws IOException {
        URI uri = URI.create("http://fox.com");
        String s = "hello";
        URI uri1 = URI.create("http://cnn.com");
        String s1 = "hey";
        byte[] b = {1, 2, 3, 4};
        byte[] b1 = {41, 42, 43, 44, 45};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.setMetadata(uri1, "key", "value");
        assertEquals((dstore.getMetadata(uri1, "key")), "value");
    }
    @Test
    public void undoAfterOnePut() throws Exception {
        URI url1 = URI.create("http://cnn.com");
        String txt1 = "Hello";
        byte[] b = txt1.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        DocumentStoreImpl dsi = new DocumentStoreImpl();
        //undo after putting only one doc
        DocumentImpl doc1 = new DocumentImpl(url1, txt1, null);
        dsi.put(stream,url1,DocumentStore.DocumentFormat.TXT);
        DocumentImpl returned1 = (DocumentImpl) dsi.get(url1);
        assertNotNull(returned1, "Did not get a document back after putting it in");
        assertEquals(doc1.getKey(), returned1.getKey(), "Did not get doc1 back");
        dsi.undo();
        returned1 = (DocumentImpl) dsi.get(url1);
        assertNull(returned1, "Put was undone should have been null");
        try {
            dsi.undo();
            fail("no documents should've thrown IllegalStateException");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    void StamUndo() throws IOException {
        URI uri = URI.create("http://fox.com");

        URI uri1 = URI.create("http://cnn.com");
        URI uri11 = URI.create("http://cnn.com");

        URI uri2 = URI.create("http://google.com");
        URI uri3 = URI.create("http://googledocs.com");

        byte[] b8 = {11, 28, 23, 40};
        ByteArrayInputStream stream8 = new ByteArrayInputStream(b8);

        byte[] b = {1, 2, 3, 4};

        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b2 = {91, 92, 93, 94, 95};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);
        ByteArrayInputStream stream3 = new ByteArrayInputStream(b2);

        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream8, uri2, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream3, uri3, DocumentStore.DocumentFormat.BINARY);
        dstore.setMetadata(uri1, "key", "value1");
        dstore.setMetadata(uri1, "key", "value2");
        dstore.put(stream2, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.delete(uri2);
        dstore.undo();
        dstore.undo();
        dstore.undo();
        dstore.undo();
        dstore.undo();
        dstore.undo();
        dstore.undo();
        dstore.undo();
        assertNull((dstore.get(uri3)));
    }

    @Test
    void undo0() throws IOException {
        URI uri = URI.create("http://fox.com");

        URI uri1 = URI.create("http://cnn.com");
        URI uri11 = URI.create("http://cnn.com");

        URI uri2 = URI.create("http://google.com");
        URI uri3 = URI.create("http://googledocs.com");

        byte[] b8 = {11, 28, 23, 40};
        ByteArrayInputStream stream8 = new ByteArrayInputStream(b8);

        byte[] b = {1, 2, 3, 4};

        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b2 = {91, 92, 93, 94, 95};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);
        ByteArrayInputStream stream3 = new ByteArrayInputStream(b2);

        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream2, uri2, DocumentStore.DocumentFormat.BINARY);
        dstore.undo(uri1);
        assertNull((dstore.get(uri1)));
    }

    @Test
    void undo1() throws IOException {
        URI uri = URI.create("http://fox.com");
        URI uri1 = URI.create("http://cnn.com");
        URI uri3 = URI.create("http://googledocs.com");


        byte[] b = {1, 2, 3, 4};
        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b2 = {91, 92, 93, 94, 95};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);

        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.setMetadata(uri1, "key", "value1");
        dstore.setMetadata(uri1, "key", "value2");
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream2, uri3, DocumentStore.DocumentFormat.BINARY);

        dstore.undo(uri1);
        assertEquals("value1", dstore.getMetadata(uri1, "key"));
    }

    @Test
    void undo2() throws IOException {
        URI uri = URI.create("http://fox.com");

        URI uri1 = URI.create("http://cnn.com");
        URI uri11 = URI.create("http://cnn.com");

        URI uri2 = URI.create("http://google.com");
        URI uri3 = URI.create("http://googledocs.com");

        byte[] b8 = {11, 28, 23, 40};
        ByteArrayInputStream stream8 = new ByteArrayInputStream(b8);

        byte[] b = {1, 2, 3, 4};

        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b2 = {91, 92, 93, 94, 95};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);
        ByteArrayInputStream stream3 = new ByteArrayInputStream(b2);

        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream8, uri2, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream3, uri3, DocumentStore.DocumentFormat.BINARY);
        dstore.setMetadata(uri3, "key", "value1");
        dstore.setMetadata(uri1, "key", "value2");
        dstore.put(stream2, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.delete(uri3);
        assertNull(dstore.get(uri3));
    }

    @Test
    void undo2test() throws IOException {
        URI uri = URI.create("http://fox.com");

        URI uri1 = URI.create("http://cnn.com");
        URI uri11 = URI.create("http://cnn.com");

        URI uri2 = URI.create("http://google.com");
        URI uri3 = URI.create("http://googledocs.com");

        byte[] b8 = {11, 28, 23, 40};
        ByteArrayInputStream stream8 = new ByteArrayInputStream(b8);

        byte[] b = {1, 2, 3, 4};

        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b2 = {91, 92, 93, 94, 95};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);
        ByteArrayInputStream stream3 = new ByteArrayInputStream(b2);

        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream8, uri2, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream3, uri3, DocumentStore.DocumentFormat.BINARY);
        dstore.delete(uri3);
        dstore.setMetadata(uri1, "key", "value1");
        dstore.setMetadata(uri1, "key", "value2");
        dstore.put(stream2, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.undo(uri3);
        assertNotNull((dstore.get(uri3)));
    }


    @Test
    void undo3() throws IOException {
        URI uri = URI.create("http://fox.com");
        URI uri1 = URI.create("http://cnn.com");
        URI uri11 = URI.create("http://cnn.com");
        URI uri2 = URI.create("http://google.com");
        URI uri3 = URI.create("http://googledocs.com");
        byte[] b8 = {11, 28, 23, 40};
        ByteArrayInputStream stream8 = new ByteArrayInputStream(b8);

        byte[] b = {1, 2, 3, 4};

        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b2 = {91, 92, 93, 94, 95};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);
        ByteArrayInputStream stream3 = new ByteArrayInputStream(b2);

        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream8, uri2, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream3, uri3, DocumentStore.DocumentFormat.BINARY);
        dstore.setMetadata(uri1, "key", "value1");
        dstore.setMetadata(uri1, "key", "value2");
        dstore.put(stream2, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.undo(uri3);
        assertNull((dstore.get(uri3)));
    }

    @Test
    void setMetadataCheck1() throws IOException {
        URI uri = URI.create("http://fox.com");
        URI uri1 = URI.create("http://cnn.com");
        byte[] b = {1, 2, 3, 4};
        byte[] b1 = {41, 42, 43, 44, 45};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        assertThrows(IllegalArgumentException.class, () -> {
            dstore.setMetadata(uri1, "key", "value");
        });
    }

    @Test
    void setMetadataCheck0() throws IOException {
        URI uri = URI.create("http://fox.com");
        String s = "hello";
        URI uri1 = URI.create("http://cnn.com");
        String s1 = "hey";
        byte[] b = {1, 2, 3, 4};
        byte[] b1 = {41, 42, 43, 44, 45};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.setMetadata(uri1, "key", "value");
        String s0 = dstore.setMetadata(uri1, "key", "value1");
        assertEquals(s0, "value");
    }


    @Test
    void getMetadataReturnsNull() throws IOException {
        URI uri = URI.create("http://fox.com");
        String s = "hello";
        URI uri1 = URI.create("http://cnn.com");
        String s1 = "hey";
        byte[] b = {1, 2, 3, 4};
        byte[] b1 = {41, 42, 43, 44, 45};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.setMetadata(uri1, "key", "value");
        assertEquals(dstore.getMetadata(uri1, "key"), "value");

    }

    @Test
    void DocFormatTXT() throws IOException {
        URI uri = URI.create("http://fox.com");
        String s = "hello";
        URI uri1 = URI.create("http://cnn.com");
        String s1 = "hey";
        byte[] b = s.getBytes();
        byte[] b1 = s1.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream, uri, DocumentStore.DocumentFormat.TXT);
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.TXT);
        assertEquals("hello", dstore.get(uri).getDocumentTxt());
    }

    @Test
    void DocFormatTXT2() throws IOException {
        URI uri = URI.create("http://fox.com");
        String s = "hello";
        URI uri1 = URI.create("http://cnn.com");
        String s1 = "hey";
        byte[] b = s.getBytes();
        byte[] b1 = s1.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.TXT);
        assertEquals(null, dstore.get(uri).getDocumentTxt());
    }

    @Test
    void putNullequalsdelete() throws IOException {
        URI uri = URI.create("http://fox.com");
        String s = "hello";
        URI uri1 = URI.create("http://cnn.com");
        String s1 = "hey";
        byte[] b = {1, 2, 3, 4};
        byte[] b1 = {41, 42, 43, 44, 45};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream nullOne = null;
        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.put(nullOne, uri1, DocumentStore.DocumentFormat.BINARY);
        assertEquals(dstore.get(uri1), null);
    }

    /*
    @Test//FOR SOME REASON, THE INPUTSTREAM IS BUGGING OUT WHEN I TRY TO USE IT AGAIN
    void putReturnValueEquals0() throws IOException{
        URI uri = URI.create("http://fox.com");
        String s= "hello";
        URI uri1 = URI.create("http://cnn.com");
        String s1= "hey";
        byte[] b = {1, 2, 3, 4};
        byte[] b1 = {41, 42, 43, 44, 45};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream nullOne = null;
        DocumentStoreImpl dstore= new DocumentStoreImpl();
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY );
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY );
        dstore.put(nullOne, uri1, DocumentStore.DocumentFormat.BINARY );
        int x= dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY );
        assertEquals(0, x);
    }

     */
    @Test
    void putReturnValueEquals0Attempt2() throws IOException {
        URI uri = URI.create("http://fox.com");
        String s = "hello";
        URI uri1 = URI.create("http://cnn.com");
        String s1 = "hey";
        byte[] b = {1, 2, 3, 4};
        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b6 = {4, 2, 3, 4, 5};

        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream6 = new ByteArrayInputStream(b6);

        ByteArrayInputStream nullOne = null;
        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.put(nullOne, uri1, DocumentStore.DocumentFormat.BINARY);
        int x = dstore.put(stream6, uri1, DocumentStore.DocumentFormat.BINARY);
        assertEquals(0, x);
    }

    @Test
    void putReturnValueEqualsOldHashCode() throws IOException {
        URI uri = URI.create("http://fox.com");
        String s = "hello";
        URI uri1 = URI.create("http://cnn.com");
        String s1 = "hey";
        byte[] b = {1, 2, 3, 4};
        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b6 = {4, 2, 3, 4, 5};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream6 = new ByteArrayInputStream(b6);
        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        int y = dstore.get(uri1).hashCode();
        int x = dstore.put(stream6, uri1, DocumentStore.DocumentFormat.BINARY);
        assertEquals(y, x);
    }

    void DeleteWhenDoesntExist() throws IOException {
        URI uri = URI.create("http://fox.com");
        String s = "hello";
        URI uri1 = URI.create("http://cnn.com");
        URI uri8 = URI.create("http://espn.com");
        String s1 = "hey";
        byte[] b = {1, 2, 3, 4};
        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b6 = {4, 2, 3, 4, 5};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream6 = new ByteArrayInputStream(b6);
        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        int y = dstore.get(uri1).hashCode();
        int x = dstore.put(stream6, uri1, DocumentStore.DocumentFormat.BINARY);
        assertFalse(dstore.delete(uri8));
    }


    //MY FAILED TESTS
    @Test
    void undoAfterOne() throws IOException {
        DocumentStoreImpl dstore = new DocumentStoreImpl();
        URI uri3 = URI.create("http://googledocs.com");
        byte[] b8 = {11, 28, 23, 40};
        ByteArrayInputStream stream8 = new ByteArrayInputStream(b8);
        dstore.put(stream8, uri3, DocumentStore.DocumentFormat.BINARY);
        dstore.undo();
        assertEquals(null, dstore.get(uri3));
    }

    @Test
    void undoDeletept1() throws IOException {
        URI uri = URI.create("http://fox.com");

        URI uri1 = URI.create("http://cnn.com");

        URI uri2 = URI.create("http://google.com");
        URI uri3 = URI.create("http://googledocs.com");

        byte[] b8 = {11, 28, 23, 40};
        ByteArrayInputStream stream8 = new ByteArrayInputStream(b8);

        byte[] b = {1, 2, 3, 4};

        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b2 = {91, 92, 93, 94, 95};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);
        ByteArrayInputStream stream3 = new ByteArrayInputStream(b2);

        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream8, uri2, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream3, uri3, DocumentStore.DocumentFormat.BINARY);
        dstore.setMetadata(uri3, "key", "value1");
        dstore.setMetadata(uri1, "key", "value2");
        dstore.put(stream2, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.delete(uri3);
        assertNull(dstore.get(uri3));
    }

    @Test
    void undoDeletept2() throws IOException {
        URI uri = URI.create("http://fox.com");

        URI uri1 = URI.create("http://cnn.com");
        URI uri11 = URI.create("http://cnn.com");

        URI uri2 = URI.create("http://google.com");
        URI uri3 = URI.create("http://googledocs.com");

        byte[] b8 = {11, 28, 23, 40};
        ByteArrayInputStream stream8 = new ByteArrayInputStream(b8);

        byte[] b = {1, 2, 3, 4};

        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b2 = {91, 92, 93, 94, 95};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);
        ByteArrayInputStream stream3 = new ByteArrayInputStream(b2);

        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream8, uri2, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream3, uri3, DocumentStore.DocumentFormat.BINARY);
        dstore.delete(uri3);
        dstore.setMetadata(uri1, "key", "value1");
        dstore.setMetadata(uri1, "key", "value2");
        dstore.put(stream2, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.undo(uri3);
        assertNotNull((dstore.get(uri3)));
    }

    @Test
    void undoNthPut() throws IOException {
        URI uri = URI.create("http://fox.com");

        URI uri1 = URI.create("http://cnn.com");
        URI uri11 = URI.create("http://cnn.com");

        URI uri2 = URI.create("http://google.com");
        URI uri3 = URI.create("http://googledocs.com");

        byte[] b8 = {11, 28, 23, 40};
        ByteArrayInputStream stream8 = new ByteArrayInputStream(b8);

        byte[] b = {1, 2, 3, 4};

        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b2 = {91, 92, 93, 94, 95};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);
        ByteArrayInputStream stream3 = new ByteArrayInputStream(b2);

        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream8, uri2, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream3, uri3, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream2, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.undo(uri1);
        assertNull((dstore.get(uri1)));
    }
    @Test
    void StamUndoDelete() throws IOException {
        URI uri = URI.create("http://fox.com");

        URI uri1 = URI.create("http://cnn.com");
        URI uri11 = URI.create("http://cnn.com");

        URI uri2 = URI.create("http://google.com");
        URI uri3 = URI.create("http://googledocs.com");

        byte[] b8 = {11, 28, 23, 40};
        ByteArrayInputStream stream8 = new ByteArrayInputStream(b8);

        byte[] b = {1, 2, 3, 4};

        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b2 = {91, 92, 93, 94, 95};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);
        ByteArrayInputStream stream3 = new ByteArrayInputStream(b2);

        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream8, uri2, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream3, uri3, DocumentStore.DocumentFormat.BINARY);
        dstore.setMetadata(uri1, "key", "value1");
        dstore.setMetadata(uri1, "key", "value2");
        dstore.put(stream2, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.delete(uri2);
        dstore.undo();
        assertEquals(b8.length,dstore.get(uri2).getDocumentBinaryData().length);
    }
    @Test
    void undoaftermultipleputs() throws IOException {
        URI uri = URI.create("http://fox.com");

        URI uri1 = URI.create("http://cnn.com");
        URI uri11 = URI.create("http://cnn.com");

        URI uri2 = URI.create("http://google.com");
        URI uri3 = URI.create("http://googledocs.com");

        byte[] b8 = {11, 28, 23, 40};
        ByteArrayInputStream stream8 = new ByteArrayInputStream(b8);

        byte[] b = {1, 2, 3, 4};

        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b2 = {91, 92, 93, 94, 95};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);
        ByteArrayInputStream stream3 = new ByteArrayInputStream(b2);

        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream8, uri2, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream3, uri3, DocumentStore.DocumentFormat.BINARY);
        dstore.undo();
        assertNull(dstore.get(uri3));
    }

    @Test
    void undoURI() throws IOException { //OK THIS ONE I MISSED, DIDN"T THROW THE ERROR!!!
        URI uri = URI.create("http://fox.com");

        URI uri1 = URI.create("http://cnn.com");
        URI uri11 = URI.create("http://cnpsdn.com");

        URI uri2 = URI.create("http://google.com");
        URI uri3 = URI.create("http://googledocs.com");

        byte[] b8 = {11, 28, 23, 40};
        ByteArrayInputStream stream8 = new ByteArrayInputStream(b8);

        byte[] b = {1, 2, 3, 4};

        byte[] b1 = {41, 42, 43, 44, 45};
        byte[] b2 = {91, 92, 93, 94, 95};
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        ByteArrayInputStream stream1 = new ByteArrayInputStream(b1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(b2);
        ByteArrayInputStream stream3 = new ByteArrayInputStream(b2);

        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.put(stream1, uri1, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream, uri, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream8, uri2, DocumentStore.DocumentFormat.BINARY);
        dstore.put(stream3, uri3, DocumentStore.DocumentFormat.BINARY);
        assertThrows(IllegalStateException.class, () -> {dstore.undo(uri11);});
    }




    //STAGE 4!!!

    @Test
    void put() throws IOException {
        setUp();
        URI uriT = URI.create("http://example/flyingfish.com");
        String s= "the hello";
        byte[] bytes = s.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        dstore.put(stream,uriT, DocumentStore.DocumentFormat.TXT );
        assertEquals(dstore.get(uriT).getDocumentTxt(), s);
        assertEquals(1,dstore.search("hello").size());
        dstore.get(uri);
        dstore.setMaxDocumentCount(1);

        dstore.delete(uriT);
        assertEquals(0,dstore.search("hello").size());

        dstore.undo();
        dstore.get(uriT);
        dstore.get(uri);


        assertEquals(4,dstore.searchByPrefix("").size());
        assertTrue(dstore.delete(uriT));
        assertNull(dstore.get(uriT));
    }

    @Test
    void put2() throws IOException {
        setUp();
        URI uriT = URI.create("http://example.com");
        String s= "the hello";
        byte[] bytes = s.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        dstore.setMaxDocumentCount(1);
        dstore.put(stream,uriT, DocumentStore.DocumentFormat.TXT );
        assertEquals(dstore.get(uriT).getDocumentTxt(), s);
        assertEquals(1,dstore.search("hello").size());
        assertNotNull(dstore.get(uri));

        dstore.undo();
        assertEquals(3,dstore.searchByPrefix("").size());
        assertFalse(dstore.delete(uriT));
        assertEquals(0,dstore.search("hello").size());
    }
    @Test
    void put3() throws IOException {
        setUp();
        dstore.setMaxDocumentCount(1);
        dstore.get(uri);
        dstore.get(uri1);
        String s= "the hello";
        byte[] bytes = s.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        dstore.put(stream,uri2, DocumentStore.DocumentFormat.TXT );
        dstore.delete(uri);


        assertEquals(2,dstore.searchByPrefix("").size());
        assertEquals(1,dstore.search("hello").size());
    }

    @Test
    void put7() throws IOException {
        setUp();
        dstore.setMaxDocumentCount(1);
        dstore.get(uri);
        dstore.get(uri1);
        String s= "the hello";
        byte[] bytes = s.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        dstore.put(stream,uri2, DocumentStore.DocumentFormat.BINARY );
        dstore.delete(uri);


        assertEquals(1,dstore.searchByPrefix("").size());
        assertEquals(0,dstore.search("hello").size());
    }

    @Test
    void delete() throws IOException {
        setUp();
        assertEquals(dstore.search("score").size(), 1);
        assertEquals(dstore.get(uri).getDocumentTxt(),"Four score and seven years ago our fathers brought forth on this continent a new nation, conceived in liberty, and dedicated to the proposition that all men are created equal. “Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battlefield of that war. We have come to dedicate a portion of that field as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this. “But in a larger sense we cannot dedicate, we cannot consecrate, we cannot hallow this ground. The brave men, living and dead, who struggled here have consecrated it, far above our poor power to add or detract. The world will little note, nor long remember, what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us,that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion, that we here highly resolve that these dead shall not have died in vain, that this nation, under God, shall have a new birth of freedom, and that government of the people, by the people, for the people, shall not perish from the earth.");
        dstore.delete(uri);
        assertEquals(dstore.search("score").size(), 0);
        assertNull(dstore.get(uri));
        dstore.undo();
        assertEquals(dstore.get(uri).getDocumentTxt(),"Four score and seven years ago our fathers brought forth on this continent a new nation, conceived in liberty, and dedicated to the proposition that all men are created equal. “Now we are engaged in a great civil war, testing whether that nation, or any nation so conceived and so dedicated, can long endure. We are met on a great battlefield of that war. We have come to dedicate a portion of that field as a final resting place for those who here gave their lives that that nation might live. It is altogether fitting and proper that we should do this. “But in a larger sense we cannot dedicate, we cannot consecrate, we cannot hallow this ground. The brave men, living and dead, who struggled here have consecrated it, far above our poor power to add or detract. The world will little note, nor long remember, what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us,that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion, that we here highly resolve that these dead shall not have died in vain, that this nation, under God, shall have a new birth of freedom, and that government of the people, by the people, for the people, shall not perish from the earth.");
        assertEquals(dstore.search("score").size(), 1);

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


        dstore.undo();

        List<Document> docslist=dstore.search("the");
        assertNotNull(dstore.get(uri));
        assertEquals(1, dstore.search("score").size());
        assertEquals(3,docslist.size() );




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
        dstore.undo();
        List<Document> docs2=dstore.search("the");
        assertEquals(3,docs2.size() );
        assertEquals(1, dstore.searchByPrefix("scor").size());
        assertNotNull(dstore.get(uri));
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
        dstore.undo();
        List<Document> docs2=dstore.search("the");
        assertEquals(3,docs2.size() );
        assertEquals(1, dstore.searchByPrefix("scor").size());
        assertNotNull(dstore.get(uri));
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
        dstore.undo();
        List<Document> docs2=dstore.search("the");
        assertEquals(3,docs2.size() );
        assertEquals(1, dstore.searchByPrefix("scor").size());
        assertNotNull(dstore.get(uri));

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
        dstore.undo();
        List<Document> docs2=dstore.search("the");
        assertEquals(3,docs2.size() );
        assertEquals(1, dstore.searchByPrefix("scor").size());
        assertNotNull(dstore.get(uri));
        map.clear();
        map.put("color", "white");
        Set<URI> uriSet=dstore.deleteAllWithPrefixAndMetadata("t", map);
        assertTrue(uriSet.contains(uri1));
        assertTrue(uriSet.contains(uri2));
        assertFalse(uriSet.contains(uri));
        assertEquals(1, dstore.searchByPrefix("t").size());
        dstore.undo(uri1);
        assertEquals(2, dstore.searchByPrefix("t").size());
        dstore.undo();
        assertEquals(3, dstore.searchByPrefix("t").size());

    }
//STAGE 4 PRE UNDO TESTS!!!!
    /*
    package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
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
     */

}