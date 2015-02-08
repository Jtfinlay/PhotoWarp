package ca.finlay.photowarp.app;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by James on 2/8/2015.
 */
public class BulgeFilter extends AbstractFilter {

    public BulgeFilter(Context c, Bitmap original) {
        super(c, original);
    }

    @Override
    public String getProgressMessage() {
        return "Applying Bulge Filter...";
    }

    @Override
    protected void invoke() {
        _script.forEach_bulge(_in, _out);
    }
}
