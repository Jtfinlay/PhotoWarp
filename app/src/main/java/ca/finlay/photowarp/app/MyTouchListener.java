package ca.finlay.photowarp.app;

import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Custom Touch Listener to support applying filters.
 * Can apply
 */
public class MyTouchListener implements View.OnTouchListener {

    private float xi, yi, xf, yf;
    private SwiperListener _parent;

    public MyTouchListener(SwiperListener listener)
    {
        _parent = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN)
        {
            xi = event.getX();
            yi = event.getY();
        } else if (action == MotionEvent.ACTION_UP)
        {
            xf = event.getX();
            yf = event.getY();
            performAction(v.getWidth(), v.getHeight());
        }

        return true;
    }

    private void performAction(int width, int height)
    {
        float dx = Math.abs(xf - xi);
        float dy = Math.abs(yf - yi);
        if (dx < width/3 && dy < width /3) return;

        if (xi > width/4 && xi < 3*width/4 && yi > height/4 && yi < 3*height/4)
            _parent.onBulge();
        else if (xf > width/4 && xf < 3*width/4 && yf > height/4 && yf < 3*height/4)
            _parent.onFisheye();
        else
            _parent.onSwirl();
    }
}
