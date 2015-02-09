package ca.finlay.photowarp.app;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Observable;
import java.util.UUID;

/**
 * Created by James on 2/8/2015.
 */
public class SaveTask extends AsyncTask<Object, Double, Boolean> {

    private Context _c;
    private SaveTaskListener _parent;

    public SaveTask(Context c) {
        _c = c;
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        Bitmap image = (Bitmap) params[0];
        _parent = (SaveTaskListener) params[1];

        try
        {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, String.valueOf(UUID.randomUUID()));
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "Whatever");
            values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

            Uri url = _c.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            OutputStream os = _c.getContentResolver().openOutputStream(url);
            image.compress(Bitmap.CompressFormat.PNG, 50, os);
            os.close();

            Log.v("SaveTask", "Done!");
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.v("SaveTask", "Problem saving image.");
        return false;

    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        _parent.onSaveComplete(result);
    }
}
