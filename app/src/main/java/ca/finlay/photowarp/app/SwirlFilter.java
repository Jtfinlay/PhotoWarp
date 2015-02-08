package ca.finlay.photowarp.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.RenderScript;

/**
 * Created by James on 2/8/2015.
 */
public class SwirlFilter extends AbstractFilter {

    public SwirlFilter(Context c, Bitmap original) {
        super(c, original);
    }

    @Override
    public String getProgressMessage() {
        return "Applying Swirl Filter...";
    }

    @Override
    protected void invoke() {
        _script.forEach_swirl(_in, _out);
    }

}
