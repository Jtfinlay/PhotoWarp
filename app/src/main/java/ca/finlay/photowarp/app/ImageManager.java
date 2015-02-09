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

    public static void SaveBitmap(SaveTaskListener l, Context c, Bitmap image)
    {
        (new SaveTask(c)).execute(image, l);
    }

}
