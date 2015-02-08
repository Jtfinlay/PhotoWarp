package ca.finlay.photowarp.app;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by James on 2/8/2015.
 */
public class FilterTask extends AsyncTask<Object, Double, Allocation> implements Observer {

    private FilterListener _parent;

    @Override
    protected Allocation doInBackground(Object... params) {
        AbstractFilter filter = (AbstractFilter) params[0];
        _parent = (FilterListener) params[1];
        filter.addObserver(this);
        filter.invoke();
        return filter.getResult();
    }

    @Override
    protected void onProgressUpdate(Double... values)
    {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Allocation result)
    {
        _parent.onComplete(result);
    }

    @Override
    public void update(Observable observable, Object data)
    {
        publishProgress(Double.valueOf((Double) data));
    }
}
