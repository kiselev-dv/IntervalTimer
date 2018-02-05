package me.osm.dkiselev.intervaltimer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.osm.dkiselev.intervaltimer.model.Timer;

/**
 * Created by dkiselev on 2/2/18.
 */

public class TimerArrayAdapter extends ArrayAdapter<Timer> {

    private Context context;

    public TimerArrayAdapter(Context context, List<Timer> timers) {
        super(context, 0, timers);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Timer timer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.timer_list_item, parent, false);
        }

        TextView tSWR = (TextView) convertView.findViewById(R.id.tSWR);
        TextView tLabel = (TextView) convertView.findViewById(R.id.tLabel);
        tSWR.setText(timer.getSets() + " " + formatTime(timer.getWork()) + "/" + formatTime(timer.getRest()));
        tLabel.setText(timer.getLabel());

        ImageButton editButton = convertView.findViewById(R.id.tEditButton);
        editButton.setTag(timer);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timer timer = (Timer) view.getTag();
                Intent intent = new Intent(view.getContext(), EditTimerActivity.class);

                intent.putExtra("id", timer.getId());

                ((Activity)context).startActivityForResult(intent, 1);
            }
        });

        return convertView;
    }

    private String formatTime(int time) {
        int min = time / 60;
        int sec = time % 60;

        String minS = min > 0 ? min + ":" : "";

        return minS + sec;
    }

}
