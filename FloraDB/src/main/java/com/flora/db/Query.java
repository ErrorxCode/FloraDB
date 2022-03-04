package com.flora.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class which helps in querying data from the database.
 */
public class Query {
    protected final Stream<Map.Entry<String, Object>> stream;
    protected final Map<String,Object> map;

    /**
     * A map version of the database which contain every data of the database
     * @param map The database map
     */
    public Query(Map<String,Object> map){
        this.stream = map.entrySet().stream();
        this.map = map;
    }

    /**
     * Collects all the list stored in the database.
     * @return Array of list containing every list in the database.
     */
    public List[] allList(){
        Object[] list = stream.filter(item -> item.getValue().getClass() == ArrayList.class).map(entry -> ((List) entry.getValue())).toArray();
        return Arrays.copyOf(list, list.length, List[].class);
    }

    /**
     * Collects all the object (POJO) stored in the database.
     * @return Array of syncables.
     */
    public <T extends Syncable> Syncable[] allObjects(){
        Object[] list = stream.filter(item -> item.getValue() instanceof Syncable).map(entry -> ((Syncable) entry.getValue())).toArray();
        return Arrays.copyOf(list, list.length, Syncable[].class);
    }


    /**
     * Collects the list that contain a particular object.
     * @param contains The object that the list contain
     * @param <T> Type of object
     * @return A list[] containing matching results.
     */
    public <T> List<T>[] whereListContains(T contains){
        Object[] list = stream.filter(item -> item.getValue() instanceof ArrayList l && l.contains(contains))
                .map(entry -> ((List) entry.getValue()))
                .toArray();
        return Arrays.copyOf(list,list.length,List[].class);
    }

    /**
     * Collects the objects from the database which meet the condition.
     * @param clazz The type of your object
     * @param condition A boolean function returning a condition to filter.
     * @return A list of objects of type clazz.
     */
    public <T> List<T> whereObjectMeets(Class<T> clazz, Predicate<T> condition){
        return stream.filter(entry -> clazz.isInstance(entry.getValue()))
                .map(item -> clazz.cast(item.getValue()))
                .filter(condition)
                .collect(Collectors.toList());
    }
}
