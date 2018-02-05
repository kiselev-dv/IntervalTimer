package me.osm.dkiselev.intervaltimer;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.osm.dkiselev.intervaltimer.model.Timer;
import me.osm.dkiselev.intervaltimer.persistence.AppDatabase;
import me.osm.dkiselev.intervaltimer.persistence.TimerDao;

public class TimersActivity extends AppCompatActivity {

    private List<Timer> timers = new ArrayList<>();
    private TimerDao timerDao;
    private TimerArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timers);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppDatabase db = Room.databaseBuilder(
                    getApplicationContext(),
                    AppDatabase.class,
                    "IntervalTimersDB")
                .fallbackToDestructiveMigration()
                .build();

        timerDao = db.timerDao();
        fetchTimers();

        final ListView timersList = (ListView) findViewById(R.id.listTimers);

        adapter = new TimerArrayAdapter(this, timers);

        timersList.setAdapter(adapter);
        timersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Timer timer = (Timer)timersList.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), WorkoutActivity.class);

                intent.putExtra("label", timer.getLabel());
                intent.putExtra("sets", timer.getSets());
                intent.putExtra("work", timer.getWork());
                intent.putExtra("rest", timer.getRest());

                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertTimer(new Timer(4, 30, 30, "Workout"));
            }
        });

    }

    private void fetchTimers() {
        new AsyncTask<Void, Void, Integer> (){

            @Override
            protected Integer doInBackground(Void... voids) {
                timers.addAll(timerDao.getAll());
                return timers.size();
            }

            @Override
            protected void onPostExecute(Integer count) {
                adapter.notifyDataSetChanged();

                if (count == 0) {
                    insertTimer(new Timer(4, 30, 30, "Workout"));
                }
            }

        }.execute();
    }

    private void insertTimer(final Timer timer) {
        new AsyncTask<Void, Void, Integer> (){

            @Override
            protected Integer doInBackground(Void... voids) {
                long[] ids = timerDao.insertAll(timer);
                timer.setId((int)ids[0]);
                timers.add(timer);
                return timers.size();
            }

            @Override
            protected void onPostExecute(Integer count) {
                adapter.notifyDataSetChanged();
            }

        }.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        timers.clear();
        fetchTimers();
    }

}
