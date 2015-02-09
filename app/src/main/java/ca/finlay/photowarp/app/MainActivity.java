package ca.finlay.photowarp.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.renderscript.Allocation;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

public class MainActivity extends ActionBarActivity implements FilterTaskListener, SaveTaskListener, SwiperListener {


    private static final int SETTINGS_ID = 4;

    private LinkedList<Bitmap> _bitMap;
    private Button _btnLoad, _btnCamera, _btnSave, _btnDiscard;
    private MenuItem _menuSettings, _menuLoad, _menuCamera,
                     _menuSwirl, _menuBulge, _menuFisheye, _menuUndo;
    private ImageView _imageView;

    /**
     * Setup views
     * @param savedInstanceState
     */
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
                Toast.makeText(MainActivity.this, "Saving... This could take a minute.", Toast.LENGTH_SHORT).show();
                ImageManager.SaveBitmap(MainActivity.this, MainActivity.this, _bitMap.peek());
            }
        });
        _btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discardImage();
            }
        });
        _imageView.setOnTouchListener(new MyTouchListener(MainActivity.this));

        _bitMap = new LinkedList<Bitmap>();
        handleIntent();

    }

    /**
     * Handle intent from previous app
     */
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

    /**
     * Result from launched Activities.
     * @param requestCode
     * @param resultCode
     * @param data
     */
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

    /**
     * Get menu item views
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        _menuSettings = menu.findItem(R.id.settings);
        _menuLoad = menu.findItem(R.id.action_load);
        _menuCamera = menu.findItem(R.id.action_camera);
        _menuSwirl = menu.findItem(R.id.action_swirl);
        _menuBulge = menu.findItem(R.id.action_bulge);
        _menuFisheye = menu.findItem(R.id.action_fisheye);
        _menuUndo = menu.findItem(R.id.action_undo);
        return true;
    }

    /**
     * Handle menu item selection.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_load:
                ImageManager.LaunchDirectorySearch(this);
                break;
            case R.id.action_camera:
                ImageManager.LaunchCamera(this);
                break;
            case R.id.action_swirl:
                applyFilter(new SwirlFilter(this, _bitMap.peek()));
                break;
            case R.id.action_bulge:
                applyFilter(new BulgeFilter(this, _bitMap.peek()));
                break;
            case R.id.action_fisheye:
                applyFilter(new FisheyeFilter(this, _bitMap.peek()));
                break;
            case R.id.action_undo:
                undoFilter();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Apply filter asynchronously
     * @param filter
     */
    private void applyFilter(AbstractFilter filter)
    {
        (new FilterTask(this, filter)).execute(this);
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

    /**
     * Pop top image and apply previous.
     */
    private void undoFilter()
    {
        _bitMap.pop();
        _imageView.setImageBitmap(_bitMap.peek());
        updateView();
    }

    /**
     * Enable / disable buttons and menu items depending on loaded bitmaps.
     */
    private void updateView()
    {
        if (_bitMap.peek() != null)
        {
            _btnLoad.setVisibility(View.GONE);
            _btnCamera.setVisibility(View.GONE);
            _btnSave.setVisibility(View.VISIBLE);
            _btnDiscard.setVisibility(View.VISIBLE);
            _menuLoad.setEnabled(false);
            _menuCamera.setEnabled(false);
            _menuSwirl.setEnabled(true);
            _menuFisheye.setEnabled(true);
            _menuBulge.setEnabled(true);
        } else
        {
            _btnLoad.setVisibility(View.VISIBLE);
            _btnCamera.setVisibility(View.VISIBLE);
            _btnSave.setVisibility(View.GONE);
            _btnDiscard.setVisibility(View.GONE);
            _menuLoad.setEnabled(true);
            _menuCamera.setEnabled(true);
            _menuSwirl.setEnabled(false);
            _menuFisheye.setEnabled(false);
            _menuBulge.setEnabled(false);
        }
        if (_bitMap.size() > 1)
        {
            _menuUndo.setEnabled(true);
        } else
        {
            _menuUndo.setEnabled(false);
        }
    }

    /**
     * Set bitmap
     * @param bm bitmap to add
     * @param resetCache whether to reset the undo queue.
     */
    private void setBitmap(Bitmap bm, boolean resetCache)
    {
        pushBitmap(bm, resetCache);
        _imageView.setImageBitmap(_bitMap.peek());

        updateView();
    }

    /**
     * Add bitmap to undo queue. Checks whether hit max undos.
     * @param bm bitmap to add
     * @param resetCache whether to reset the undo queue.
     */
    public void pushBitmap(Bitmap bm, boolean resetCache)
    {
        if (resetCache)
        {
            _bitMap = new LinkedList<Bitmap>();
        }
        _bitMap.addFirst(bm);

        SharedPreferences sharedPref = this.getSharedPreferences(SettingsActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        while (sharedPref.getInt(SettingsActivity.UNDO_SETTINGS, 5)+1 < _bitMap.size())
        {
            _bitMap.removeLast();
        }
    }

    /**
     * Discard undo queue and the active bitmap. Verifies with alert first.
     */
    private void discardImage()
    {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Discard image")
                .setMessage("Are you sure you want to discard?")
                .setPositiveButton("Kill it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _bitMap = new LinkedList<Bitmap>();
                        _imageView.setImageBitmap(_bitMap.peek());
                        updateView();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Apply filter task complete.
     * From FilterTaskListener
     * @param result
     */
    @Override
    public void onFilterComplete(Allocation result) {
        Bitmap bm = _bitMap.peek().copy(Bitmap.Config.ARGB_8888, true);
        result.copyTo(bm);
        pushBitmap(bm, false);
        _imageView.setImageBitmap(_bitMap.peek());
        updateView();
    }

    /**
     * Save bitmap task complete.
     * From SaveTaskListener
     * @param success:
     */
    @Override
    public void onSaveComplete(boolean success) {
        if (success)
        {
            Toast.makeText(MainActivity.this, "Save successful", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Swirl action from Touch events.
     * From SwiperListener
     */
    @Override
    public void onSwirl() {
        if (!_menuSwirl.isEnabled()) return;
        applyFilter(new SwirlFilter(this, _bitMap.peek()));
    }

    /**
     * Bulge action from Touch events.
     * From SwiperListener
     */
    @Override
    public void onBulge() {
        if (!_menuBulge.isEnabled()) return;
        applyFilter(new BulgeFilter(this, _bitMap.peek()));

    }

    /**
     * Fisheye action from Touch events
     * From SwiperListener
     */
    @Override
    public void onFisheye() {
        if (!_menuFisheye.isEnabled()) return;
        applyFilter(new FisheyeFilter(this, _bitMap.peek()));

    }
}
