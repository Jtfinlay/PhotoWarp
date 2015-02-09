package ca.finlay.photowarp.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Filter;

/**
 * Created by James on 2/8/2015.
 */
public class FilterTask extends AsyncTask<Object, Double, Allocation> implements Observer {

    private FilterTaskListener _parent;
    private Context _c;
    private ProgressDialog _pd;
    private AbstractFilter _filter;

    public FilterTask(Context c, AbstractFilter filter)
    {
        _c = c;
        _filter = filter;
        _filter.addObserver(this);
    }

    @Override
    protected Allocation doInBackground(Object... params) {
        _parent = (FilterTaskListener) params[0];
        _filter.invoke();
        return _filter.getResult();
    }

    @Override
    protected void onProgressUpdate(Double... values)
    {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPreExecute()
    {
        _pd = new ProgressDialog(_c);
        _pd.setMessage(_filter.getProgressMessage() + " -- Could be slow if also saving.");
        _pd.setCancelable(false);
        _pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _pd.show();
    }

    @Override
    protected void onPostExecute(Allocation result)
    {
        _pd.dismiss();
        _parent.onFilterComplete(result);
    }

    @Override
    public void update(Observable observable, Object data)
    {
        publishProgress(Double.valueOf((Double) data));
    }
}
