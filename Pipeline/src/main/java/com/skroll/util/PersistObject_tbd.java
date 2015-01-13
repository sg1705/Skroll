package com.skroll.util;

import java.io.*;

/**
 * Created by saurabhagarwal on 1/11/15.
 */
public class PersistObject_tbd implements Serializable {

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
    public static void persistObject(Object obj, String objName) throws ObjectPersistException {
        // ensure that the object is implemented as serializable interface

        if (!(obj instanceof java.io.Serializable)) {
            throw new ObjectPersistException("Object does not implement java.io.Serializable . To persist the Object, The object need to be serialized.");
        }

        // Write to disk with FileOutputStream
        FileOutputStream f_out = null;
        try {
            f_out = new FileOutputStream(configuration.get(MODEL_PERSIST_FOLDER) +"/" + objName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ObjectPersistException("Folder to persist are not accessible. Check the directory where the file are getting persisted:");

        }

        // Write object with ObjectOutputStream
        ObjectOutputStream obj_out = null;
        try {
            obj_out = new ObjectOutputStream(f_out);
            obj_out.writeObject ( obj );
        } catch (IOException e) {
            e.printStackTrace();
            throw new ObjectPersistException("Unable to write to the file system:");

        }


    }

    public static Object readObject(String objectName)  throws ObjectPersistException {

        // Read from disk using FileInputStream
        FileInputStream f_in = null;
        try {
            f_in = new
                    FileInputStream(configuration.get(MODEL_PERSIST_FOLDER)+ "/"+ objectName);
            System.out.println("Writing the object to the file: "  +configuration.get(MODEL_PERSIST_FOLDER)+ "/" + objectName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ObjectPersistException("Object Persistence directory not found ");
        }

        // Read object using ObjectInputStream
        ObjectInputStream obj_in =
                null;
        try {
            obj_in = new ObjectInputStream(f_in);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ObjectPersistException("Failed to read the object input stream");
        }
        Object obj =null;
        // Read an object
        try {
            obj = obj_in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ObjectPersistException("Failed to read the Object");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new ObjectPersistException("Failed to cast to the Object class");
        }

        return obj;
    }


    public static class ObjectPersistException extends Throwable {
        public ObjectPersistException(String s) {
        }
    }
}
