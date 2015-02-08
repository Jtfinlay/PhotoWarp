package ca.finlay.photowarp.app;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by James on 2/8/2015.
 */
public class FilterTask extends AsyncTask<Object, Double, Bitmap> implements Observer {

    @Override
    protected Bitmap doInBackground(Object... params) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Double... values)
    {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Bitmap result)
    {

    }

    @Override
    public void update(Observable observable, Object data)
    {
        publishProgress(Double.valueOf((Double) data));
    }
}
