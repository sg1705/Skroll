package com.skroll.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.skroll.analyzer.model.applicationModel.TrainingDocumentTOCAnnotatingModel;
import com.skroll.analyzer.model.applicationModel.TrainingTextAnnotatingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;

/**
 * Created by saurabhagarwal on 1/12/15.
 */
public class ObjectPersistUtil {

    public static final Logger logger = LoggerFactory
            .getLogger(Configuration.class);
   // public Configuration configuration = new Configuration();
    public String objectPersistFolder = "/tmp";

    public ObjectPersistUtil(String persistFolder){
        objectPersistFolder = persistFolder;
    }

    // Persist the Object
    public  void persistObject(Type type, Object obj, String objectName) throws ObjectPersistException {
        // Write to disk with FileOutputStream
        FileOutputStream f_out = null;
        File file = new File(objectPersistFolder + "/" + objectName);
        if (file.exists()){
            file.delete();
        }
        try {
            Files.createParentDirs(file);
            f_out = new FileOutputStream(objectPersistFolder + "/" + objectName);
            logger.info("writing object to file: " + objectPersistFolder + "/" + objectName);

        } catch (IOException e) {
            e.printStackTrace();
            throw new ObjectPersistException("Folder to persist are not accessible. Check the directory where the file are getting persisted:");

        }

        Writer writer = null;
        try {
            writer = new OutputStreamWriter(f_out, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new ObjectPersistException("UnsupportedEncodingException while reading the persisted object: "+ objectName);

        }
//        Gson gson = new GsonBuilder().create();
//        gson.toJson(obj, type, writer);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            mapper.writerWithDefaultPrettyPrinter().writeValue(writer, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            writer.close();
            f_out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object readObject(Type type,String objectName) throws ObjectPersistException {

        // Read from disk using FileInputStream
        FileInputStream f_in = null;
        try {
            f_in = new
                    FileInputStream(objectPersistFolder + "/" + objectName);
            logger.info("reading object from file: " + objectPersistFolder + "/" + objectName);
        } catch (FileNotFoundException e) {
            logger.error("Object Persistence file not found: " + objectPersistFolder + "/" + objectName);
            throw new ObjectPersistException("Object Persistence directory not found ");
        }

        Reader reader = null;
        try {
            reader = new InputStreamReader(f_in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new ObjectPersistException("UnsupportedEncodingException while reading the persisted object: "+ objectName);
        }
//        Gson gson = new GsonBuilder().create();
//        Object obj = gson.fromJson(reader, type);
        TrainingTextAnnotatingModel obj = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            obj = mapper.readValue(reader, (Class<TrainingTextAnnotatingModel>) type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            reader.close();
             f_in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public static class ObjectPersistException extends Throwable {
        public ObjectPersistException(String s) {
        }
    }
}
