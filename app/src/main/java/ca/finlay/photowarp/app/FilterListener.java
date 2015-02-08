package ca.finlay.photowarp.app;

import android.renderscript.Allocation;

/**
 * Created by James on 2/8/2015.
 */
public interface FilterListener {
    public void progressUpdate(double value);
    public void onComplete(Allocation result);
}
