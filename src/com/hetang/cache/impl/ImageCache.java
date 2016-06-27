package com.hetang.cache.impl;

import com.hetang.cache.CachePolicy;
import com.hetang.cache.factories.ImageCacheFactory;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * I assuming that, This class will invoke from background thread.
 * Hence, {@code getBitmap} will have blocking IO.
 * Created by hetashah on 6/25/16.
 */
public class ImageCache {
    CachePolicy<String, byte[]> cache;

    /**
     * Implementing singleton class. Hence, cache can be shared among different caller.
     */
    private static class SingletonHolder {
        static ImageCache INSTANCE;
    }

    public static ImageCache getInstance() {
        if(SingletonHolder.INSTANCE == null) {
            SingletonHolder.INSTANCE = new ImageCache();
        }

        return SingletonHolder.INSTANCE;
    }

    protected ImageCache() {
        cache = ImageCacheFactory.create(ImageCacheFactory.CacheTypes.LRU);
    }

    /**
     * Setting cache max size at runtime. Recommended way to call this function only once.
     * Setting multiple time limit will have side effect on cache values.
     * If you do not know whats optimal value then pass {@code cacheSize} as 0 or -1.
     * @param cacheSize
     */
    public void setCacheLimit(int cacheSize) {
        if(cache != null) {
            cache.setCacheLimit(cacheSize);
        }
    }

    public int getCacheSize() {
        return cache.size();
    }

    public int getCacheLimit() {
        return cache.getCacheLimit();
    }

    public synchronized byte[] load(String url) throws OutOfMemoryError {
        byte[] bitmap = cache.get(url);
        if (bitmap == null) {
            bitmap = getBitmap(url);
            cache.put(url, bitmap);
        }

        return bitmap;
    }

    private byte[] getBitmap(String url) {
        /**
         * I assume that this method will be implemented by each platform specific code.
         * In Android, you may want to use AsyncTask to load Bitmap from the backend server.
         * Hence, I am assuming that {@code load} method will be invoke from background thread.
         * Where in Java, you can directly download through HttpURLConnection.
         * I have referred below code from
         * @see <a href="http://www.java2s.com/Code/Android/2D-Graphics/GetImageBitmapFromUrl.htm"> http://www.java2s.com/Code/Android/2D-Graphics/GetImageBitmapFromUrl.htm</a>
         */
        byte[]  bitmap = null;
        try {
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            if (conn.getResponseCode() != 200) {
                return bitmap;
            }
            conn.connect();
            InputStream is = conn.getInputStream();

            BufferedInputStream bis = new BufferedInputStream(is);
            try {
                bitmap = IOUtils.toByteArray(is);
            } catch (OutOfMemoryError ex) {
                bitmap = null;
            }
            bis.close();
            is.close();
        } catch (Exception e) {
            Logger.getLogger(ImageCache.class.getName()).log(Level.SEVERE, "Error while downloading image", e);
        }

        return bitmap;
    }
}
