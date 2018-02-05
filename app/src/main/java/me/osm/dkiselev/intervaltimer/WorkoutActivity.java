package me.osm.dkiselev.intervaltimer;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutActivity extends AppCompatActivity {

    private static enum MODE {GET_READY, WORK, REST}

    private static final class CountdownTask {
        public CountdownTask(MODE mode, int set, int time) {
            this.mode = mode;
            this.set = set;
            this.time = time;
        }

        MODE mode;
        int set;
        int time;
    }

    private Runnable callback;
    private CountDownTimer timer;
    private final ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String label = getIntent().getStringExtra("label");
        setTitle(label);

        final List<CountdownTask> countdownTasks = new ArrayList<>();

        int sets = getIntent().getIntExtra("set", 4);
        int work = getIntent().getIntExtra("work", 30);
        int rest = getIntent().getIntExtra("rest", 30);

        countdownTasks.add(new CountdownTask(MODE.GET_READY, sets, 5));

        for (int set = sets; set > 0; set--) {
            countdownTasks.add(new CountdownTask(MODE.WORK, set, work));
            if (set > 1) {
                countdownTasks.add(new CountdownTask(MODE.REST, set, rest));
            }
        }

        callback = new Runnable() {
            @Override
            public void run() {
                if (!countdownTasks.isEmpty()) {
                    CountdownTask task = countdownTasks.remove(0);
                    countdown(task, callback);
                }
                else {
                    allDone();
                }
            }
        };
        callback.run();

    }

    private void allDone(){
        findViewById(R.id.wAction).setVisibility(View.INVISIBLE);
        getWindow().getDecorView().setBackgroundColor(Color.GREEN);
        ((TextView)findViewById(R.id.wSet)).setText("Tap back");
        ((TextView)findViewById(R.id.wTimer)).setText("Done!");

    }

    private boolean matchWithTolerance(long value, int match, int tolerance) {
        return value > match - tolerance && value < match + tolerance;
    }

    private boolean makeBeepEndsAt(long millsToEnd, int ends, int length) {
        int target = ends + length;
        if (matchWithTolerance(millsToEnd, target, 50)) {
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, length);
            return true;
        }
        return false;
    }

    private void countdown(final CountdownTask task, final Runnable callback) {

        final TextView activityV = findViewById(R.id.wAction);
        switch (task.mode) {
            case GET_READY:
                getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                activityV.setText("Get Ready");
                break;
            case WORK:
                getWindow().getDecorView().setBackgroundColor(Color.RED);
                activityV.setText("Work");
                break;
            case REST:
                getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                activityV.setText("Rest");
                break;
        }

        final TextView timerV = findViewById(R.id.wTimer);
        final TextView setV = findViewById(R.id.wSet);
        setV.setText("" + task.set);



        new CountDownTimer(task.time * 1000, 100) {

            public void onTick(long millisUntilFinished) {
                if (task.mode == MODE.GET_READY || task.mode == MODE.REST) {
                    makeBeepEndsAt(millisUntilFinished, 3 * 1000, 200);
                    makeBeepEndsAt(millisUntilFinished, 2 * 1000, 200);
                    makeBeepEndsAt(millisUntilFinished, 1 * 1000, 200);
                    makeBeepEndsAt(millisUntilFinished, 0, 500);
                }

                if (task.mode == MODE.WORK) {
                    makeBeepEndsAt(millisUntilFinished, 0, 200);
                }

                int time = (int)millisUntilFinished / 1000;
                int s = time % 60;
                int m = time / 60;

                timerV.setText(String.format("%02d:%02d", m, s));
            }

            @Override
            public void onFinish() {
                timerV.setText("00:00");
                callback.run();
            }

        }.start();
    }

    @Override
    public void onBackPressed() {
        timer.cancel();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
