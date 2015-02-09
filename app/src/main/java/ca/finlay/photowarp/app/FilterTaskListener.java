package ca.finlay.photowarp.app;

import android.renderscript.Allocation;

/**
 * Created by James on 2/8/2015.
 */
public interface FilterTaskListener {
    public void onFilterComplete(Allocation result);
}
