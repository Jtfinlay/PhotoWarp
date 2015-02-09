package ca.finlay.photowarp.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

/**
 * Created by James on 2/8/2015.
 */
public class ImageManager {

    public static final int LOAD_REQUEST_CODE = 1;
    public static final int CAMERA_REQUEST_CODE= 2;

    public static final String APP_PATH = "/PHOTOWARP";

    private LruCache<String, Bitmap> _cache;
    private LinkedList<String> _cacheQueue;

    public static void LaunchDirectorySearch(Activity a)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        a.startActivityForResult(intent, LOAD_REQUEST_CODE);
    }

    public static void LaunchCamera(Activity a)
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        a.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    public static void SaveBitmap(Context c, Bitmap image)
    {
        (new SaveTask(c)).execute(image);
    }

    public void pushBitmapToCache(Bitmap bitmap)
    {
        // TODO - Maximum undos
        String key = String.valueOf(UUID.randomUUID());
        _cacheQueue.addFirst(key);
        _cache.put(key, bitmap);
    }

    public Bitmap pullBitmapFromCache()
    {
        String key = _cacheQueue.pollFirst();
        Bitmap result = _cache.get(key);
        _cache.remove(key);

        return result;
    }

    public void clearCache()
    {
        _cache.evictAll();
        _cacheQueue = new LinkedList<String>();
    }

    public void createCache()
    {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        _cacheQueue = new LinkedList<String>();

       _cache = new LruCache<String, Bitmap> (cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap)
            {
                return bitmap.getByteCount() / 1024;
            }
        };
    }
}
