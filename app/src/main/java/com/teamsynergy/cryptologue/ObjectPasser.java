package com.teamsynergy.cryptologue;

import java.util.HashMap;

/**
 * Created by nadeen on 4/1/17.
 */

public class ObjectPasser {
    private static final HashMap<String, Object> mObjects = new HashMap<>();

    public static void putObject(String name, Object obj) {
        mObjects.put(name, obj);
    }

    public static Object peekObject(String name) {
        return mObjects.get(name);
    }

    public static Object popObject(String name) {
        Object obj = peekObject(name);
        mObjects.remove(name);

        return obj;
    }
}
