package ca.finlay.photowarp.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.renderscript.Allocation;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends ActionBarActivity implements FilterListener {

    private Bitmap _bitMap;
    private Button _btnLoad, _btnCamera, _btnSave, _btnDiscard;
    private ImageView _imageView;
    private ImageManager _imageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _btnLoad = (Button) findViewById(R.id.btnLoad);
        _btnCamera = (Button) findViewById(R.id.btnCamera);
        _btnSave = (Button) findViewById(R.id.btnSave);
        _btnDiscard = (Button) findViewById(R.id.btnDiscard);
        _imageView = (ImageView) findViewById(R.id.image_area);

        _btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageManager.LaunchDirectorySearch(MainActivity.this);
            }
        });
        _btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageManager.LaunchCamera(MainActivity.this);
            }
        });
        _btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  ImageManager.SaveBitmap(MainActivity.this, _bitMap);
            }
        });
        _btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _bitMap = null;
                _imageView.setImageBitmap(_bitMap);
                updateView();
            }
        });

        _imageManager = new ImageManager();
        _imageManager.createCache();

        handleIntent();

    }

    private void handleIntent()
    {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (!Intent.ACTION_VIEW.equals(action) && !Intent.ACTION_EDIT.equals(action)) return;
        if (!type.startsWith("image/")) return;

        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri == null) return;

        loadImage(imageUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode)
        {
            case ImageManager.LOAD_REQUEST_CODE:
                loadImage(data.getData());
                break;
            case ImageManager.CAMERA_REQUEST_CODE:
                setBitmap((Bitmap) data.getExtras().get("data"), true);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_load:
                ImageManager.LaunchDirectorySearch(this);
                break;
            case R.id.action_camera:
                ImageManager.LaunchCamera(this);
                break;
            case R.id.action_save:
                ImageManager.SaveBitmap(this, _bitMap);
                break;
            case R.id.action_swirl:
                applyFilter(new SwirlFilter(this, _bitMap));
                break;
            case R.id.action_bulge:
                applyFilter(new BulgeFilter(this, _bitMap));
                break;
            case R.id.action_fisheye:
                applyFilter(new FisheyeFilter(this, _bitMap));
                break;
            case R.id.action_undo:
                undoFilter();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void applyFilter(AbstractFilter filter)
    {
        (new FilterTask()).execute(filter, this, _bitMap);
    }

    /**
     * Load image from uri and draw to the ImageView
     * @param data - input stream uri
     */
    private void loadImage(Uri data)
    {
        InputStream stream = null;
        try {

            stream = getContentResolver().openInputStream(data);
            setBitmap(BitmapFactory.decodeStream(stream), true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void undoFilter()
    {
        setBitmap(_imageManager.pullBitmapFromCache(), false);
    }

    private void setBitmap(Bitmap bm, boolean resetCache)
    {
        if (resetCache)
        {
            _imageManager.clearCache();
        }
        if (_bitMap != null)_imageManager.pushBitmapToCache(_bitMap);

        _bitMap = bm;
        _imageView.setImageBitmap(_bitMap);

        updateView();
    }

    private void updateView()
    {
        if (_bitMap != null)
        {
            _btnLoad.setVisibility(View.GONE);
            _btnCamera.setVisibility(View.GONE);
            _btnSave.setVisibility(View.VISIBLE);
            _btnDiscard.setVisibility(View.VISIBLE);
        } else
        {
            _btnLoad.setVisibility(View.VISIBLE);
            _btnCamera.setVisibility(View.VISIBLE);
            _btnSave.setVisibility(View.GONE);
            _btnDiscard.setVisibility(View.GONE);
        }
    }

    @Override
    public void onComplete(Allocation result) {
        if (_bitMap != null)_imageManager.pushBitmapToCache(_bitMap);

        result.copyTo(_bitMap);
        _imageView.setImageBitmap(_bitMap);
        updateView();
    }
}
