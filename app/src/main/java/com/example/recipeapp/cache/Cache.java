package com.example.recipeapp.cache;


import android.util.Log;

import com.example.recipeapp.models.RecipeDetails;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cache {

    private static Cache cache;
    private long timeToLive;
    private int maxItems;
    private Map<Integer, CacheObject> cacheMap;

    protected class CacheObject {
        public long lastAccessed = System.currentTimeMillis();
        public RecipeDetails recipeDetails;

        protected CacheObject(RecipeDetails recipeDetails) {
            this.recipeDetails = recipeDetails;
        }
    }

    public Cache(long timeToLive, final long timerInterval, int maxItems) {
        this.timeToLive = timeToLive * 1000;
        this.maxItems = maxItems;

        cacheMap = new LinkedHashMap<>(maxItems);

        if (this.timeToLive > 0 && timerInterval > 0) {

            Thread t = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(timerInterval * 1000);
                        } catch (InterruptedException ex) {
                        }
                        cleanup();
                    }
                }
            });

            t.setDaemon(true);
            t.start();
        }
    }

    public static Cache getCache() {
        if (cache == null) {
            cache = new Cache(86400, 1, 20);
        }
        return cache;
    }

    public void put(Integer id, RecipeDetails recipeDetails) {
        synchronized (cacheMap) {
            if (cacheMap.size() == maxItems) {
                int firstKey = cacheMap.entrySet().iterator().next().getKey();
                cacheMap.remove(firstKey);
            }

            cacheMap.put(id, new CacheObject(recipeDetails));
            Log.i("Cache", cacheMap.toString());
        }
    }

    @SuppressWarnings("unchecked")
    public RecipeDetails get(Integer id) {
        synchronized (cacheMap) {
            CacheObject object = (CacheObject) cacheMap.get(id);

            if (object == null)
                return null;
            else {
                object.lastAccessed = System.currentTimeMillis();
                cacheMap.remove(id);
                cacheMap.put(id, object);
                return object.recipeDetails;
            }
        }
    }

    public void remove(Integer id) {
        synchronized (cacheMap) {
            cacheMap.remove(id);
        }
    }

    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }

    @SuppressWarnings("unchecked")
    public void cleanup() {

        long now = System.currentTimeMillis();
        ArrayList<Integer> deleteKey = null;

        synchronized (cacheMap) {
            Iterator<Map.Entry<Integer, CacheObject>> itr = cacheMap.entrySet().iterator();

            deleteKey = new ArrayList<Integer>((cacheMap.size() / 2) + 1);
            Integer id = null;
            CacheObject object = null;

            while (itr.hasNext()) {
                Map.Entry<Integer, CacheObject> entry = itr.next();
                id = entry.getKey();
                object = entry.getValue();

                if (object != null && (now > (timeToLive + object.lastAccessed))) {
                    deleteKey.add(id);
                }
            }
        }

        for (Integer id : deleteKey) {
            synchronized (cacheMap) {
                cacheMap.remove(id);
            }

            Thread.yield();
        }
    }
}
