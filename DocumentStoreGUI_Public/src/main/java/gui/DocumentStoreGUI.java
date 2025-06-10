package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URI;
import java.util.*;

import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.impl.DocumentStoreImpl;

public class DocumentStoreGUI extends Application {

    private final DocumentStoreImpl store = new DocumentStoreImpl();
    private final Label docCountLabel = new Label("Documents in store: 0");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Document Store GUI");

        TextField uriInput = new TextField();
        uriInput.setPromptText("Document URI");

        TextArea documentText = new TextArea();
        documentText.setPromptText("Document Content");

        CheckBox isBinaryCheckbox = new CheckBox("Binary Document");

        Button addButton = new Button("Add Document");
        Button getButton = new Button("Get Document");
        Button deleteButton = new Button("Delete Document");

        // Search section
        TextField keywordField = new TextField();
        keywordField.setPromptText("Keyword");

        TextField prefixField = new TextField();
        prefixField.setPromptText("Prefix");

        TextField metadataKeyField = new TextField();
        metadataKeyField.setPromptText("Metadata Key");

        TextField metadataValueField = new TextField();
        metadataValueField.setPromptText("Metadata Value");

        Button keywordSearchButton = new Button("Search by Keyword");
        Button prefixSearchButton = new Button("Search by Prefix");
        Button metadataSearchButton = new Button("Search by Metadata");

        // Group delete buttons
        Button deleteByKeywordButton = new Button("Delete All by Keyword");
        Button deleteByPrefixButton = new Button("Delete All by Prefix");
        Button deleteByMetadataButton = new Button("Delete All by Metadata");
        Button deleteByKeywordAndMetadataButton = new Button("Delete All by Keyword + Metadata");
        Button deleteByPrefixAndMetadataButton = new Button("Delete All by Prefix + Metadata");

        TextArea output = new TextArea();
        output.setEditable(false);
        output.setWrapText(true);

        // -- Actions --

        addButton.setOnAction(e -> {
            try {
                URI uri = new URI(uriInput.getText());
                String content = documentText.getText();
                boolean isBinary = isBinaryCheckbox.isSelected();

                if (isBinary) {
                    store.put(new java.io.ByteArrayInputStream(content.getBytes()), uri, DocumentStoreImpl.DocumentFormat.BINARY);
                } else {
                    store.put(new java.io.ByteArrayInputStream(content.getBytes()), uri, DocumentStoreImpl.DocumentFormat.TXT);
                }
                updateDocCount();
                output.setText("Document added.");
            } catch (Exception ex) {
                output.setText("Error: " + ex.getMessage());
            }
        });
        // --- Set Metadata ---
        Button setMetadataButton = new Button("Set Metadata on Document");
        setMetadataButton.setOnAction(e -> {
            try {
                URI uri = new URI(uriInput.getText());
                String key = metadataKeyField.getText();
                String value = metadataValueField.getText();
                if (key.isEmpty() || value.isEmpty()) {
                    output.setText("Please enter both key and value.");
                    return;
                }
                String oldValue = store.setMetadata(uri, key, value);
                updateDocCount();
                output.setText("Metadata set. Old value: " + (oldValue == null ? "null" : oldValue));
            } catch (Exception ex) {
                output.setText("Error: " + ex.getMessage());
            }
        });

        getButton.setOnAction(e -> {
            try {
                URI uri = new URI(uriInput.getText());
                Document doc = store.get(uri);
                if (doc == null) {
                    output.setText("Document not found.");
                } else {
                    output.setText("Found: " + uri + "\n" +
                            (doc.getDocumentTxt() != null
                                    ? doc.getDocumentTxt()
                                    : "[Binary Document: " + doc.getDocumentBinaryData().length + " bytes]"));
                }
            } catch (Exception ex) {
                output.setText("Error: " + ex.getMessage());
            }
        });

        deleteButton.setOnAction(e -> {
            try {
                URI uri = new URI(uriInput.getText());
                boolean deleted = store.delete(uri);
                updateDocCount();
                output.setText(deleted ? "Document deleted." : "No document found to delete.");
            } catch (Exception ex) {
                output.setText("Error: " + ex.getMessage());
            }
        });

        keywordSearchButton.setOnAction(e -> {
            String keyword = keywordField.getText();
            List<Document> results = store.search(keyword);
            output.setText("Matching URIs:\n" + joinURIs(results));
        });

        prefixSearchButton.setOnAction(e -> {
            String prefix = prefixField.getText();
            List<Document> results = store.searchByPrefix(prefix);
            output.setText("Matching URIs:\n" + joinURIs(results));
        });

        metadataSearchButton.setOnAction(e -> {
            String key = metadataKeyField.getText();
            String value = metadataValueField.getText();
            if (!key.isEmpty() && !value.isEmpty()) {
                Map<String, String> metadata = Map.of(key, value);
                List<Document> results = store.searchByMetadata(metadata);
                output.setText("Matching URIs:\n" + joinURIs(results));
            } else {
                output.setText("Please enter both key and value.");
            }
        });

        // Group Deletes
        deleteByKeywordButton.setOnAction(e -> {
            String keyword = keywordField.getText();
            Set<URI> deleted = store.deleteAll(keyword);
            updateDocCount();
            output.setText("Deleted:\n" + joinURIs(deleted));
        });

        deleteByPrefixButton.setOnAction(e -> {
            String prefix = prefixField.getText();
            Set<URI> deleted = store.deleteAllWithPrefix(prefix);
            updateDocCount();
            output.setText("Deleted:\n" + joinURIs(deleted));
        });

        deleteByMetadataButton.setOnAction(e -> {
            String key = metadataKeyField.getText();
            String value = metadataValueField.getText();
            if (!key.isEmpty() && !value.isEmpty()) {
                Map<String, String> metadata = Map.of(key, value);
                Set<URI> deleted = store.deleteAllWithMetadata(metadata);
                updateDocCount();
                output.setText("Deleted:\n" + joinURIs(deleted));
            } else {
                output.setText("Please enter both key and value.");
            }
        });

        deleteByKeywordAndMetadataButton.setOnAction(e -> {
            String keyword = keywordField.getText();
            String key = metadataKeyField.getText();
            String value = metadataValueField.getText();
            if (!key.isEmpty() && !value.isEmpty()) {
                Map<String, String> metadata = Map.of(key, value);
                Set<URI> deleted = store.deleteAllWithKeywordAndMetadata(keyword, metadata);
                updateDocCount();
                output.setText("Deleted:\n" + joinURIs(deleted));
            } else {
                output.setText("Please enter both key and value.");
            }
        });

        deleteByPrefixAndMetadataButton.setOnAction(e -> {
            String prefix = prefixField.getText();
            String key = metadataKeyField.getText();
            String value = metadataValueField.getText();
            if (!key.isEmpty() && !value.isEmpty()) {
                Map<String, String> metadata = Map.of(key, value);
                Set<URI> deleted = store.deleteAllWithPrefixAndMetadata(prefix, metadata);
                updateDocCount();
                output.setText("Deleted:\n" + joinURIs(deleted));
            } else {
                output.setText("Please enter both key and value.");
            }
        });

        // Layout
        VBox leftColumn = new VBox(10,
                new Label("Document I/O"),
                uriInput, documentText, isBinaryCheckbox,
                addButton, getButton, deleteButton,
                docCountLabel
        );

        VBox searchColumn = new VBox(10,
                new Label("Search & Delete"),
                keywordField, prefixField,
                metadataKeyField, metadataValueField,
                setMetadataButton,
                keywordSearchButton, prefixSearchButton, metadataSearchButton,
                deleteByKeywordButton, deleteByPrefixButton,
                deleteByMetadataButton, deleteByKeywordAndMetadataButton,
                deleteByPrefixAndMetadataButton
        );


        VBox rightColumn = new VBox(10, new Label("Output"), output);

        HBox root = new HBox(20, leftColumn, searchColumn, rightColumn);
        root.setPadding(new javafx.geometry.Insets(15));

        primaryStage.setScene(new Scene(root, 1000, 550));
        primaryStage.show();
    }

    private void updateDocCount() {
        try {
            // Count all URIs with any metadata key (assumes all docs go through put or setMetadata)
            Set<URI> seen = new HashSet<>();
            for (var entry : store.searchByMetadata(Map.of()).stream().map(Document::getKey).toList()) {
                seen.add(entry);
            }
            docCountLabel.setText("Documents in store: " + seen.size());
        } catch (Exception e) {
            docCountLabel.setText("Documents in store: ?");
        }
    }


    private String joinURIs(Collection<?> docsOrUris) {
        if (docsOrUris.isEmpty()) return "(None)";
        StringBuilder sb = new StringBuilder();
        for (Object obj : docsOrUris) {
            if (obj instanceof Document doc) {
                sb.append("- ").append(doc.getKey()).append("\n");
            } else if (obj instanceof URI uri) {
                sb.append("- ").append(uri).append("\n");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
