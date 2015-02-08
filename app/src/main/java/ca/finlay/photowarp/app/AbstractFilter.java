package ca.finlay.photowarp.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptC;

import java.util.Observable;

/**
 * Created by James on 2/8/2015.
 */
public abstract class AbstractFilter extends Observable {

    protected Bitmap _orig;
    protected RenderScript _rs;
    protected ScriptC_transform _script;
    protected Allocation _in, _out;

    public abstract String getProgressMessage();
    protected abstract void invoke();

    public AbstractFilter(Context c, Bitmap original)
    {
        _orig = original;
        _rs = RenderScript.create(c);
        _script = new ScriptC_transform(_rs, c.getResources(), R.raw.transform);

        _in = Allocation.createFromBitmap(_rs, _orig, Allocation.MipmapControl.MIPMAP_NONE,Allocation.USAGE_SCRIPT);
        _out = Allocation.createTyped(_rs, _in.getType());

        _script.set_height(_orig.getHeight());
        _script.set_width(_orig.getWidth());
        _script.bind_input(_in);
        _script.bind_output(_out);
    }

    public Allocation getResult()
    {
        return _out;
    }


}
