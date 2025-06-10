# Document Store: Data Structures Project

A Java-based document store built for a university-level data structures course. This project applies custom data structures to support efficient storage, search, and metadata-based queries.

## Key Features

* Store, retrieve, and delete documents by URI
* Search by keyword, prefix, and metadata
* Assign metadata to documents
* Undo operations globally or per-document
* Eviction based on document count and byte size limits

## Core Data Structures

* **B-Tree** for disk-backed document storage
* **Trie** for keyword and prefix indexing
* **Min-Heap** for LRU tracking
* **Command Stack** for undo/redo
* **HashMap** for metadata indexing

## GUI (Optional)

Includes a JavaFX GUI for adding, viewing, searching, and deleting documents. GUI is not required to use the backend.

## Run Instructions

Requires Java 17+ and JavaFX SDK 21+.

```bash
javac --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls -d out src/**/*.java
java --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls -cp out gui.DocumentStoreGUI
```

Course Context

This project was developed as part of COM 1320: Data Structures at Yeshiva University. Specifications were provided by the course instructor and focused on building performant, real-world data structures.

Author

Benzion Rotblat (@brotblat2)
