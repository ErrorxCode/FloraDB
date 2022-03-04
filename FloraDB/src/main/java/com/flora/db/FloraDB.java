package com.flora.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * This is the main class of the database. This class is a utility class that provides static methods to perform
 * database operations. You have to initialize this by {@link #init(File)} method before calling any of the database method.
 * Otherwise, a {@link NullPointerException} will be thrown. You also need to commit ({@code commit()}) to the database to
 * save the changes. Not all methods requires committing. Methods that requires committing are clearly stated in the javadoc of
 * that method.
 */
public class FloraDB {
    protected static File DIR;
    protected static volatile Map<String,Object> map = new HashMap<>();
    protected static volatile Map<String,Object> primitives = new HashMap<>();

    /**
     * Initialize the database with the directory provided. This may took longer time depending on your data size.
     * This method runs synchronously (and it should be) and blocks thread until completed.
     * @param databaseDir The directory where your database is located or should be created. Usually file dir of your application
     * @throws RuntimeException If file is not a directory or is not writable
     */
    public static void init(File databaseDir){
        if (databaseDir.isDirectory() && databaseDir.canWrite()){
            DIR = databaseDir;
            try {
                File file = new File(databaseDir, "Sync.db");
                if (!file.exists() && !file.createNewFile())
                    throw new RuntimeException(new IOException("Cannot create directory for some reason"));
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                map = (Map) in.readObject();
                in.close();
                if (map.containsKey("primitives"))
                    primitives = (Map) map.get("primitives");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else
            throw new RuntimeException(new IOException(databaseDir + " is not a directory or is not writable"));
    }

    /**
     * Inserts a simple datatype into the database. Updates if id already exist. Requires {@link #commit()} call.
     * @param id ID of the data (like primary key)
     * @param data A String,Number or boolean value to insert into database
     */
    public static void put(String id,Object data){
        if (!map.containsKey("primitives")){
            map.put("primitives",primitives);
        }
        if (data == null)
            primitives.remove(id);
        else if (data instanceof String || data instanceof Number || data instanceof Boolean)
            primitives.put(id, data);
        else
            throw new IllegalArgumentException("data must be one of String,Number or boolean");
    }


    /**
     * Gets the simple data from the database. (data inserted using {@link #put(String, Object)} method
     * @param id The unique id of the data.
     * @param defaultValue Value in case it does not exist in the database
     * @return Value or defaultValue if none found.
     */
    public static Object get(String id,Object defaultValue){
        return primitives.getOrDefault(id,defaultValue);
    }


    /**
     * Creates and inserts a new list into the database. Updates if it already exist.
     * You need to call {@link #commit()} for this.
     * @param id The id of the list
     * @param items The default items in the list
     */
    public static <T> void createList(String id,T[] items){
        List<T> list = new ArrayList<>();
        if (items != null)
            list = new ArrayList<>(Arrays.asList(items));
        map.put(id,list);
    }


    /**
     * Updates the list asynchronously from the interface deification. The {@link SyncedDataManager#update(Object)} method
     * contains a list object. That list is the the list which is saved in the database. You can modify it
     * and it will be reflected in the database. You don't need to call {@link #commit()} for this.
     * @param id The id of the list
     * @param manager Interface defining the list
     * @throws IllegalArgumentException if there is no list with the id
     */
    public static <T> void updateList(String id,SyncedDataManager<List<T>> manager){
        try {
            List<T> list = (List) map.get(id);
            if (list == null)
                throw new IllegalArgumentException("There is no list associated with id " + id + " in the database");
            else
                manager.update(list);

            commitAsync(null);
        } catch (ClassCastException e){
            throw new IllegalArgumentException(id + " is not of a list");
        }
    }


    /**
     * Creates a new syncable mutable list or gets if already exist. The returned list will be syncable and mutable,
     * it means that any modification made to it will also be modified in the database. You need to however call {@link #commit()}
     * after calling this if you had modified it, Otherwise not.
     * @param id The id of the list
     * @return A syncable and mutable list
     */
    public static <T> List<T> createOrGetList(String id){
        return (List) map.getOrDefault(id,new ArrayList<T>());
    }

    /**
     * Gets a non-syncable immutable list from the database. null if not exist
     * @param id Id of the list
     * @return {@link List} or null if not exist in the database
     * @throws ClassCastException If the id is not of a list
     */
    public static <T> List<T> getList(String id){
        List list = (List) map.get(id);
        if (list == null)
            return null;
        else
            return (List) Collections.unmodifiableList(list);
    }


    /**
     * Creates and inserts a new object into the database. Updates if already exist
     * Requires {@link #commit()} call.
     * @param id The id of the list
     * @param object Your POJO
     */
    public static <T extends Syncable> void createObject(String id,T object){
        map.put(id,object);
    }


    /**
     * Creates a new syncable mutable object (POJO) or gets if already exist. The returned object will be syncable and mutable,
     * it means that any modification made to it will also be modified in the database. You need to however call {@link #commit()}
     * after calling this if you had modified it, Otherwise not.
     * @param id The id of the object
     * @param objClass The class type of your object.
     * @return A syncable and mutable Object of generic type
     * @throws ClassCastException If id is not of a valid syncable object
     */
    public static <T extends Syncable> T createOrGetObject(String id,Class<T> objClass){
        Object pojo = map.get(id);
        try {
            if (pojo == null){
                return objClass.newInstance();
            } else
                return (T) pojo;
        } catch (ClassCastException e){
            throw new IllegalArgumentException(id + " is not id of a POJO");
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("Modal class must have a empty (no-arg) constructor");
        }
    }


    /**
     * Gets a syncable object (POJO) from the database. Mutable means that the object returned
     * by this method can be further modified. You need to however call {@link #commit()}
     * after calling this if you had modified it, Otherwise not.
     * @param id Id of the object
     * @return a POJO or null if not exist in the database
     * @param <T> Type class of your POJO
     * @throws IllegalArgumentException If the id is not of a 'type' object or if the object cannot be casted to 'type' class.
     */
    public static <T extends Syncable> T getObject(String id,Class<T> type){
        Object obj = map.get(id);
        if (obj == null)
            return null;
        else if (type.isInstance(obj))
            return type.cast(obj);
        else
            throw new IllegalArgumentException(id + " is not id a POJO");
    }


    /**
     * Updates the object asynchronously from the interface deification. The {@link SyncedDataManager#update(Object)} method
     * contains a provided class object. That object is the the POJO which is saved in the database. You can modify it
     * and it will be reflected in the database. You don't need to call {@link #commit()} for this.
     * @param id The id of the object
     * @param manager Interface defining the object
     * @param <T> Type class of your POJO
     * @throws IllegalArgumentException if there is no object with the id
     */
    public static <T> void updateObject(String id,SyncedDataManager<T> manager){
        try {
            Object obj = map.get(id);
            if (obj == null)
                throw new IllegalArgumentException("There is no object associated with id " + id + " in the database");
            else
                manager.update((T) obj);

            commitAsync(null);
        } catch (ClassCastException e){
            throw new IllegalArgumentException(id + " is not of a POJO");
        }
    }


    /**
     * Does as the name suggest. It require {@link #commit()} call
     * @param id Id of the data to delete
     */
    public static void delete(String id){
        map.remove(id);
    }


    public static Query query(){
        return new Query(map);
    }

    /**
     * Saves and commits all the changes to database. This must be the last call on database as calling this will
     * nullify and close all the streams related to database.
     *
     * @throws RuntimeException A checked-runtime exception when an IOException occurs. Runtime because there are extremely low
     * chances of occurring most common types if IOException like FileNotFoundException (already handled while initializing).
     * This may only occur when the underlying filesystem throws it. It is recommended not to handle it as it would indicate serious bug.
     */
    public static void commit(){
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(DIR,"Sync.db")));
            System.out.println(map);
            os.writeObject(map);
            os.close();
            DIR = null;
            System.gc();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Similar as {@link #commit()} but runs asynchronously. This will not block the thread.
     * @param callback A optional callback that indicates success and failure of the commit.
     */
    public static void commitAsync(Callback callback){
        if (callback == null)
            new Thread(FloraDB::commit).start();
        else
            CompletableFuture.runAsync(FloraDB::commit).thenRun(callback::onComplete).whenComplete((d, e) -> callback.onFailed((Exception) e));
    }

    /**
     * An interface that is used to update a list or a object in the database.
     * @param <T>
     */
    public interface SyncedDataManager<T> {
        void update(T object);
    }

    /**
     * Callback for {@code commitAsync()} method.
     */
    public interface Callback {
        void onComplete();
        void onFailed(Exception e);
    }
}
