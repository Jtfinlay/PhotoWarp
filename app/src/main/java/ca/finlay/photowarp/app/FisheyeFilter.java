package ca.finlay.photowarp.app;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by James on 2/8/2015.
 */
public class FisheyeFilter extends AbstractFilter {

    public FisheyeFilter(Context c, Bitmap original) {
        super(c, original);
    }

    @Override
    public String getProgressMessage() {
        return "Applying Fisheye Filter...";
    }

    @Override
    protected void invoke() {
        _script.forEach_fisheye(_in, _out);
    }
}
