package edu.yu.cs.com1320.project.stage6.impl;

import com.google.gson.*;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.PersistenceManager;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;


public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {
    Gson gson;
    File baseDir;


     class DocumentSerializer implements JsonSerializer<Document> {
        @Override
        public JsonElement serialize(Document src, Type typeOfSrc, JsonSerializationContext context) {
            DocumentImpl doc= (DocumentImpl) src;


            JsonObject jsonObject= new JsonObject();
            if (doc.getDocumentTxt()!=null)
                jsonObject.addProperty("Text", doc.getDocumentTxt());

            if (doc.getDocumentBinaryData() != null) {
                byte[] binaryData = doc.getDocumentBinaryData();
                String base = DatatypeConverter.printBase64Binary(binaryData);
                jsonObject.addProperty("ByteArray", base);
            }

            JsonObject wordMapObject = new JsonObject();
            if (doc.getWordMap()!=null) {
                for (Map.Entry<String, Integer> entry : doc.getWordMap().entrySet()) {
                    wordMapObject.addProperty(entry.getKey(), entry.getValue());
                }
                jsonObject.add("WordMap", wordMapObject);
            }
            JsonObject metadataObject = new JsonObject();
            for (Map.Entry<String, String> entry : doc.getMetadata().entrySet()) {
                metadataObject.addProperty(entry.getKey(), entry.getValue());
            }
            jsonObject.add("Metadata", metadataObject);

            jsonObject.addProperty("URI", doc.getKey().toString());

            return jsonObject;
        }
    }
    class DocumentDeserializer implements JsonDeserializer<Document> {

        @Override
        public Document deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();


            String text = jsonObject.has("Text") ? jsonObject.get("Text").getAsString() : null;
            byte[] binaryData = jsonObject.has("ByteArray") ? DatatypeConverter.parseBase64Binary(jsonObject.get("ByteArray").getAsString()) : null;

            URI key = URI.create(jsonObject.get("URI").getAsString());


            Map<String, Integer> wordMap = new HashMap<>();
            JsonObject wordMapObject = jsonObject.getAsJsonObject("WordMap");
            if (wordMapObject != null) {
                for (Map.Entry<String, JsonElement> entry : wordMapObject.entrySet()) {
                    wordMap.put(entry.getKey(), entry.getValue().getAsInt());
                }
            }

            HashMap<String, String> metadata = new HashMap<>();
            JsonObject metadataObject = jsonObject.getAsJsonObject("Metadata");
            if (metadataObject != null) {
                for (Map.Entry<String, JsonElement> entry : metadataObject.entrySet()) {
                    metadata.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
            DocumentImpl doc;
            if (text!=null){
                doc= new DocumentImpl( key, text, wordMap);
            }
            else{
                doc= new DocumentImpl(key, binaryData);
            }
            doc.setMetadata(metadata);
            return  doc;
        }
    }

        public DocumentPersistenceManager(){
        GsonBuilder gsonBuilder= new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DocumentImpl.class, new DocumentSerializer());
        gsonBuilder.registerTypeAdapter(DocumentImpl.class, new DocumentDeserializer());
        gson = gsonBuilder.create();
    }

    public DocumentPersistenceManager(File baseDir){
        GsonBuilder gsonBuilder= new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DocumentImpl.class, new DocumentSerializer());
        gsonBuilder.registerTypeAdapter(DocumentImpl.class, new DocumentDeserializer());
        gson = gsonBuilder.create();
        this.baseDir=baseDir;
    }

    @Override
    public void serialize(URI uri, Document doc) throws IOException {
        String json= gson.toJson(doc);
        String path= uri.toString().substring(7);
        String[] dirs= path.split("/");
        path+=".json";


        if (dirs.length==1) {
            path=path.replace("/", File.separator);
            File file;
            if (baseDir != null) file = new File(baseDir, path);
            else file = new File(path);
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        }

        else {
            String dirpath = path.substring(0, path.lastIndexOf("/"));
            dirpath = dirpath.replace("/", File.separator);
            File file;
            if (baseDir != null) file = new File(baseDir, dirpath);
            else file = new File(dirpath);
            file.mkdirs();
            File jsonFile = new File(file, path.substring(path.lastIndexOf("/")+1));
            FileWriter writer = new FileWriter(jsonFile);
            writer.write(json);
            writer.close();
        }
    }


    @Override//UP TO HERE: THINGS TO DO: A) Fix this up, look into reading files B) Make the base directory constructor to the document class. C) Get rid of the old document constructor D) Fix up the BTREE and figure out how to make it useful for this assignment!
    public Document deserialize(URI uri) throws IOException {
        String pathString= uri.toString().substring(7);
        pathString+=".json";
        pathString=pathString.replace("/", File.separator);

        File file;
        if (baseDir!=null) file = new File(baseDir, pathString) ;
        else file = new File(pathString);
        String jsonString = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        return gson.fromJson(jsonString, DocumentImpl.class);
    }


    /**
     * delete the file stored on disk that corresponds to the given key
     * @param uri
     * @return true or false to indicate if deletion occured or not
     * @throws IOException
     */
    @Override
    public boolean delete(URI uri) throws IOException {
        String pathString= uri.toString().substring(7);
        pathString+=".json";
        String[] dirs= pathString.split("/");
        pathString=pathString.replace("/", File.separator);

        boolean returnValue;
        if (baseDir!=null){
            Path baseDirPath = Paths.get(baseDir.getAbsolutePath(), pathString);
            returnValue= Files.deleteIfExists(baseDirPath);
        }
        else returnValue= Files.deleteIfExists(Paths.get(pathString));

       File file;
        if (returnValue){
            while(pathString.contains(File.separator)){
                pathString=pathString.substring(0, pathString.lastIndexOf(File.separator));

                if (baseDir != null) file = new File(baseDir, pathString);
                else file = new File(pathString);
                if (file!=baseDir && file.isDirectory() &&  file.listFiles().length==0)
                    file.delete();
                else break;
            }
        }
        return returnValue;
    }

}
