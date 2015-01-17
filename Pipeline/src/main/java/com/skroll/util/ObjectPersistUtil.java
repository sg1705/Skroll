package com.skroll.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;

/**
 * Created by saurabhagarwal on 1/12/15.
 */
public class ObjectPersistUtil<T> {

    public static final Logger logger = LoggerFactory
            .getLogger(Configuration.class);
    public static Configuration configuration = null;
    public static String MODEL_PERSIST_FOLDER = "model.persist.folder";

    static {
        try {
            configuration = new Configuration();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Persist the Object
    public void persistObject(Object obj, String objectName) throws ObjectPersistException {
        // Write to disk with FileOutputStream
        FileOutputStream f_out = null;
        Type type = new TypeToken<T>() {}.getType();
        File file = new File(configuration.get(MODEL_PERSIST_FOLDER) + "/" + objectName);
        //TODO: delete if the file exist. will implement the versioning in the next iteration.
        if (file.exists()){
            file.delete();
        }
        try {
            f_out = new FileOutputStream(configuration.get(MODEL_PERSIST_FOLDER) + "/" + objectName);
            logger.info("writing object to file: " + configuration.get(MODEL_PERSIST_FOLDER) + "/" + objectName);

        } catch (FileNotFoundException e) {
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
        Gson gson = new GsonBuilder().create();
        gson.toJson(obj, type, writer);

        try {
            writer.close();
            f_out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object readObject(String objectName) throws ObjectPersistException {
        Type type = new TypeToken<T>() {}.getType();
        // Read from disk using FileInputStream
        FileInputStream f_in = null;
        try {
            f_in = new
                    FileInputStream(configuration.get(MODEL_PERSIST_FOLDER) + "/" + objectName);
            logger.info("reading object from file: " + configuration.get(MODEL_PERSIST_FOLDER) + "/" + objectName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ObjectPersistException("Object Persistence directory not found ");
        }

        Reader reader = null;
        try {
            reader = new InputStreamReader(f_in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new ObjectPersistException("UnsupportedEncodingException while reading the persisted object: "+ objectName);
        }
        Gson gson = new GsonBuilder().create();
        Object obj = gson.fromJson(reader, type);

        logger.info("Object:" + obj);

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
