package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.stage4.DocumentStore;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class DocumentStoreImplTest {

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
        dstore.undo(uri3);
        assertNull(dstore.get(uri3));
    }






}