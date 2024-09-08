# Document Store and Search Engine Project

This project is a semester-long assignment for the Data Structures course (COM 1320) at Yeshiva University. It involves building a simple search engine over multiple stages, progressively implementing data structures, memory management techniques, and software engineering skills.

## Project Structure

The project is divided into six stages, each focusing on a different aspect of the search engine’s functionality:

1. **Stage 1**: Build a simple in-memory document store that supports basic operations like put, get, and delete. Documents are stored in a `HashMap` and can be retrieved using their URI.
2. **Stage 2**: Replace `HashMap` with a custom `HashTable` implementation using separate chaining to handle collisions.
3. **Stage 3**: Add undo functionality using a stack to reverse the last operation on the document store.
4. **Stage 4**: Implement a Trie to support keyword-based search and prefix search across documents.
5. **Stage 5**: Implement memory management with a min-heap to track document usage and enforce memory limits.
6. **Stage 6**: Introduce two-tier storage using a B-tree, allowing documents to be moved between memory and disk as needed.

## Technologies Used

- **Java**: The project is written in Java, and uses advanced language features like generics and lambdas.
- **Maven**: Used for dependency management and project building.
- **JUnit**: Unit testing is used to ensure each stage functions correctly.
- **GSON**: For document serialization and deserialization in JSON format (Stage 6).

## Key Interfaces and Classes

- `DocumentStore`: Interface for storing and managing documents.
- `Document`: Represents a document, including its URI, content, and metadata.
- `HashTable`: Custom hash table implementation (Stage 2).
- `MinHeap`: Custom min-heap to track document usage (Stage 5).
- `BTree`: Custom B-tree to manage document storage across memory and disk (Stage 6).

## Contributors
Benzion Rotblat


