package ca.finlay.photowarp.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Activity for changing number of undos.
 */
public class SettingsActivity extends ActionBarActivity {

    public static final String UNDO_SETTINGS = "UNDO_SETTING";
    public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";

    private SeekBar _seekUndo;
    private Button _btnApply;
    private TextView _txtUndo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final SharedPreferences sharedPref = this.getApplicationContext().
                getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        _seekUndo = (SeekBar) findViewById(R.id.seek_undo);
        _btnApply = (Button) findViewById(R.id.btnApply);
        _txtUndo = (TextView) findViewById(R.id.txtUndo);

        // 5 seems to work without running out of memory
        _seekUndo.setMax(5);
        _seekUndo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                _txtUndo.setText("Number of Undos stored: " + progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        _seekUndo.setProgress(sharedPref.getInt(UNDO_SETTINGS, 5));
        _txtUndo.setText("Number of Undos stored: " + _seekUndo.getProgress());


        _btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(UNDO_SETTINGS, _seekUndo.getProgress());
                editor.commit();
                finish();
            }
        });


    }


}
