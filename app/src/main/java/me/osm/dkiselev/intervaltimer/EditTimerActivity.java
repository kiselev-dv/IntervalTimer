package me.osm.dkiselev.intervaltimer;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toolbar;

import java.util.List;

import me.osm.dkiselev.intervaltimer.model.Timer;
import me.osm.dkiselev.intervaltimer.persistence.AppDatabase;
import me.osm.dkiselev.intervaltimer.persistence.TimerDao;

public class EditTimerActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener, TextWatcher {

    private AppDatabase db;
    private TimerDao dao;
    private Timer timer;

    private EditText labelEdit;
    private NumberPicker setsEdit;

    private NumberPicker workM2Edit;
    private NumberPicker workS1Edit;
    private NumberPicker workS2Edit;

    private NumberPicker restM2Edit;
    private NumberPicker restS1Edit;
    private NumberPicker restS2Edit;

    private boolean ignoreChangeEvents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timer);

        invalidateOptionsMenu();
        supportInvalidateOptionsMenu();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Edit Timer");

        labelEdit = findViewById(R.id.editLabel);

        workM2Edit = findViewById(R.id.wM2);
        workS1Edit = findViewById(R.id.wS1);
        workS2Edit = findViewById(R.id.wS2);

        restM2Edit = findViewById(R.id.rM2);
        restS1Edit = findViewById(R.id.rS1);
        restS2Edit = findViewById(R.id.rS2);

        setsEdit = findViewById(R.id.editSetsNumber);
        setsEdit.setOnValueChangedListener(this);
        setsEdit.setMinValue(1);
        setsEdit.setMaxValue(25);
        setsEdit.setWrapSelectorWheel(false);

        workM2Edit.setMaxValue(30);
        workS1Edit.setMaxValue(9);
        workS2Edit.setMaxValue(9);

        restM2Edit.setMaxValue(30);
        restS1Edit.setMaxValue(9);
        restS2Edit.setMaxValue(9);

        labelEdit.addTextChangedListener(this);

        workM2Edit.setOnValueChangedListener(this);
        workS1Edit.setOnValueChangedListener(this);
        workS2Edit.setOnValueChangedListener(this);

        restM2Edit.setOnValueChangedListener(this);
        restS1Edit.setOnValueChangedListener(this);
        restS2Edit.setOnValueChangedListener(this);

        int timerId = getIntent().getIntExtra("id", -1);

        if (timerId >= 0) {
            db = Room.databaseBuilder(
                    getApplicationContext(),
                    AppDatabase.class,
                    "IntervalTimersDB").build();

            dao = db.timerDao();
            getTimer(timerId);
        }

    }

    private void timerLoaded() {
        ignoreChangeEvents = true;
        labelEdit.setText(timer.getLabel());

        setsEdit.setValue(timer.getSets());

        setWorkTime(timer.getWork());
        setRestTime(timer.getRest());
        ignoreChangeEvents = false;
    }

    private void setWorkTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;

        workM2Edit.setValue(minutes);
        workS1Edit.setValue(seconds / 10);
        workS2Edit.setValue(seconds % 10);
    }

    private void setRestTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;

        restM2Edit.setValue(minutes);
        restS1Edit.setValue(seconds / 10);
        restS2Edit.setValue(seconds % 10);
    }

    private int getWorkTime() {
        int minutes = workM2Edit.getValue();
        int seconds = workS1Edit.getValue() * 10 + workS2Edit.getValue();

        return minutes * 60 + seconds;
    }

    private int getRestTime() {
        int minutes = restM2Edit.getValue();
        int seconds = restS1Edit.getValue() * 10 + restS2Edit.getValue();

        return minutes * 60 + seconds;
    }

    private void getTimer(int timerId) {
        new AsyncTask<Integer, Void, Timer>(){

            @Override
            protected Timer doInBackground(Integer... ids) {
                List<Timer> r = dao.loadAllByIds(new int[]{ids[0]});
                if(r.isEmpty()) {
                   return null;
                }
                else {
                    return r.get(0);
                }
            }

            @Override
            protected void onPostExecute(Timer t) {
                timer = t;
                timerLoaded();
            }

        }.execute(timerId);
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
        if (!ignoreChangeEvents) {
            updateTimer();
        }
    }

    private void updateTimer() {
        timer.setLabel(labelEdit.getText().toString());
        timer.setSets(setsEdit.getValue());
        timer.setWork(getWorkTime());
        timer.setRest(getRestTime());
    }

    private void saveTimerToDB() {
        new AsyncTask<Timer, Void, Void>() {

            @Override
            protected Void doInBackground(Timer... timer) {
                dao.update(timer[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void t) {
                // Do nothing
            }

        }.execute(timer);
    }

    private void dropTimer() {
        new AsyncTask<Timer, Void, Void>() {

            @Override
            protected Void doInBackground(Timer... timer) {
                dao.delete(timer[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void t) {
                // Do nothing
            }

        }.execute(timer);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("id", -1);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        saveTimerToDB();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("id", timer.getId());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                dropTimer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(!ignoreChangeEvents) {
            updateTimer();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
