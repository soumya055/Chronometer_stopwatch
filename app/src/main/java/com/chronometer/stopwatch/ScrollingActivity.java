package com.chronometer.stopwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.florent37.viewanimator.ViewAnimator;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView count_down, count_down_current;
    private Button start, lap, stop;
    private View divider;
    private RecyclerView recylerView;
    private LinearLayout stop_layout;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private RecyclerView_Adapter recyclerView_adapter;
    private ArrayList<ListViewModel> listViewModelArrayList;

    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;

    private long startCurrentTime = 0L;
    private long CurrenttimeInMilliseconds = 0L;
    private long CurrenttimeSwapBuff = 0L;
    private long CurrentupdatedTime = 0L;

    private boolean counter = false;
    private boolean islap_btn_clicked = false;

    long MILLIS_TO_MINUTES = 60000;
    long MILLIS_TO_HOURS = 3600000;

    private Handler customHandler = new Handler();
    private Handler customCurrentHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        // hiding the status bar makes the activity-layout a fullscreen view
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);

        listViewModelArrayList = new ArrayList<>();

        recylerView = findViewById(R.id.recylerView);
        recyclerView_adapter = new RecyclerView_Adapter(this, listViewModelArrayList);

        recylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recylerView.setAdapter(recyclerView_adapter);
        divider = findViewById(R.id.divider);
        appBarLayout = findViewById(R.id.app_bar);

        count_down = findViewById(R.id.count_down);
        count_down_current = findViewById(R.id.count_down_current);

        start = findViewById(R.id.start);
        stop = findViewById(R.id.imageButton2);
        lap = findViewById(R.id.imageButton3);

        stop_layout = findViewById(R.id.stop_layout);

        start.setOnClickListener(this);
        lap.setOnClickListener(this);
        stop.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
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

            startActivity(new Intent(ScrollingActivity.this,AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.start:
                stop_layout.setVisibility(View.VISIBLE);
                start.setVisibility(View.GONE);
                fab.setImageResource(R.drawable.ic_pause_black_24dp);
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);

                if (islap_btn_clicked) {
                    startCurrentTime = SystemClock.uptimeMillis();
                    customCurrentHandler.postDelayed(updateCurrentTimerThread, 0);
                }
                break;

            case R.id.imageButton2:
                if (stop.getText().toString().equalsIgnoreCase("STOP")) {

                    // stop button onClick
                    stop.setText("RESUME");
                    stop.setTextColor(0xFF33b331);
                    lap.setText("RESET");
                    fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);

                    timeSwapBuff += timeInMilliseconds;
                    customHandler.removeCallbacks(updateTimerThread);

                    if (islap_btn_clicked) {
                        System.out.println(" stop btn is pressed");
                        CurrenttimeSwapBuff += CurrenttimeInMilliseconds;
                        customCurrentHandler.removeCallbacks(updateCurrentTimerThread);
                    }
                } else {

                    // resume button onClick
                    stop.setText("STOP");
                    stop.setTextColor(0xFFff2f00);
                    lap.setText("LAP");
                    fab.setImageResource(R.drawable.ic_pause_black_24dp);
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 0);

                    if (islap_btn_clicked) {
                        startCurrentTime = SystemClock.uptimeMillis();
                        customCurrentHandler.postDelayed(updateCurrentTimerThread, 0);
                    }
                }
                break;

            case R.id.imageButton3:

                if (lap.getText() == "RESET") {

                    // reset button onClick
                    stop.setText("STOP");
                    stop.setTextColor(0xFFff2f00);
                    lap.setText("LAP");

                    count_down.setText("00:00:00:000");
                    timeInMilliseconds = 0L;
                    timeSwapBuff = 0L;
                    updatedTime = 0L;

                    CurrenttimeInMilliseconds = 0L;
                    CurrenttimeSwapBuff = 0L;
                    CurrentupdatedTime = 0L;

                    stop_layout.setVisibility(View.GONE);
                    start.setVisibility(View.VISIBLE);
                    islap_btn_clicked = false;
                    appBarLayout.setExpanded(true);
                    divider.setVisibility(View.GONE);
                    recylerView.setVisibility(View.GONE);
                    recyclerView_adapter.notifyDataSetChanged();
                    count_down_current.setVisibility(View.GONE);
                    fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    listViewModelArrayList.clear();
                    counter = false;

                } else {

                    // lap button onClick
                    appBarLayout.setExpanded(false);
                    islap_btn_clicked = true;
                    CurrenttimeInMilliseconds = 0L;
                    CurrenttimeSwapBuff = 0L;
                    CurrentupdatedTime = 0L;
                    fab.setImageResource(R.drawable.ic_pause_black_24dp);

                    if (!counter) {
                        divider.setVisibility(View.VISIBLE);
                        recylerView.setVisibility(View.VISIBLE);
                        count_down_current.setVisibility(View.VISIBLE);

                        counter = true;
                        ViewAnimator
                                .animate(findViewById(R.id.divider))
                                .fadeIn()
                                .duration(1000)
                                .start();

                        ViewAnimator
                                .animate(findViewById(R.id.recylerView))
                                .fadeIn()
                                .duration(1000)
                                .start();

                        ViewAnimator
                                .animate(findViewById(R.id.count_down_current))
                                .fadeIn()
                                .duration(1000)
                                .start();
                    }

                    startCurrentTime = SystemClock.uptimeMillis();
                    customCurrentHandler.postDelayed(updateCurrentTimerThread, 0);

                    ListViewModel listViewModel = new ListViewModel(count_down.getText().toString(), count_down_current.getText().toString(), (listViewModelArrayList.size() + 1));
                    listViewModelArrayList.add(0, listViewModel);
                    recyclerView_adapter.add_item();


                }
                break;

            case R.id.fab:

                if (start.getVisibility() == View.VISIBLE) {
                    stop_layout.setVisibility(View.VISIBLE);
                    start.setVisibility(View.GONE);
                    fab.setImageResource(R.drawable.ic_pause_black_24dp);
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 0);

                    if (islap_btn_clicked) {
                        startCurrentTime = SystemClock.uptimeMillis();
                        customCurrentHandler.postDelayed(updateCurrentTimerThread, 0);
                    }
                } else {
                    if (stop.getText().toString().equalsIgnoreCase("STOP")) {

                        // stop button onClick
                        stop.setText("RESUME");
                        stop.setTextColor(0xFF33b331);
                        lap.setText("RESET");
                        fab.setImageResource(R.drawable.ic_play_arrow_black_24dp);

                        timeSwapBuff += timeInMilliseconds;
                        customHandler.removeCallbacks(updateTimerThread);

                        if (islap_btn_clicked) {
                            System.out.println(" stop btn is pressed");
                            CurrenttimeSwapBuff += CurrenttimeInMilliseconds;
                            customCurrentHandler.removeCallbacks(updateCurrentTimerThread);
                        }
                    } else {

                        // resume button onClick
                        stop.setText("STOP");
                        stop.setTextColor(0xFFff2f00);
                        lap.setText("LAP");
                        fab.setImageResource(R.drawable.ic_pause_black_24dp);
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 0);

                        if (islap_btn_clicked) {
                            startCurrentTime = SystemClock.uptimeMillis();
                            customCurrentHandler.postDelayed(updateCurrentTimerThread, 0);
                        }
                    }
                }
                break;
        }

    }

    private Runnable updateTimerThread = new Runnable() {

        @SuppressLint("DefaultLocale")
        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int seconds = (int) ((updatedTime / 1000) % 60);
            int minutes = (int) ((updatedTime / MILLIS_TO_MINUTES) % 60);
            int hours = (int) ((updatedTime / MILLIS_TO_HOURS) % 24);
            int millis = (int) (updatedTime % 1000);

            String timer_format = String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis);

            int index;

            if (hours == 0) {
                if (minutes == 0) {
                    if (seconds == 0) {
                        index = timer_format.indexOf(millis + "");
                    } else {
                        index = timer_format.indexOf(seconds + "");
                    }
                } else {
                    index = timer_format.indexOf(minutes + "");
                }
            } else {
                index = timer_format.indexOf(hours + "");
            }
            if (index > 0) {
                String sub_string_variable = timer_format.substring(index);
                count_down.setText(timer_format, TextView.BufferType.SPANNABLE);
                Spannable s = (Spannable) count_down.getText();
                Pattern pattern = Pattern.compile(sub_string_variable);
                Matcher matcher = pattern.matcher(timer_format);
                boolean checki = true;
                while (matcher.find() && checki) {
                    s.setSpan(new ForegroundColorSpan(0xFF1c711b), matcher.start(), matcher.end(), 0);
                    checki = false;
                }
            }

            customHandler.postDelayed(this, 0);
        }

    };

    private Runnable updateCurrentTimerThread = new Runnable() {

        @SuppressLint("DefaultLocale")
        public void run() {

            CurrenttimeInMilliseconds = SystemClock.uptimeMillis() - startCurrentTime;

            CurrentupdatedTime = CurrenttimeSwapBuff + CurrenttimeInMilliseconds;
            int seconds = (int) ((CurrentupdatedTime / 1000) % 60);
            int minutes = (int) ((CurrentupdatedTime / MILLIS_TO_MINUTES) % 60);
            int hours = (int) ((CurrentupdatedTime / MILLIS_TO_HOURS) % 24);
            int millis = (int) (CurrentupdatedTime % 1000);

            count_down_current.setText(String.format(
                    "%02d:%02d:%02d:%03d", hours, minutes, seconds, millis));
            customCurrentHandler.postDelayed(this, 0);
        }

    };

    public class RecyclerView_Adapter extends RecyclerView.Adapter<ViewHolder> {

        Context context;
        ArrayList<ListViewModel> list;


        RecyclerView_Adapter(Context context, ArrayList<ListViewModel> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View convertView = null;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (inflater != null) {
                convertView = inflater.inflate(R.layout.list_item, parent, false);
            }
            return new ViewHolder(convertView);

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ListViewModel model = list.get(position);

            if (model.getCounter() > 9) {
                holder.lap_count.setText((model.getCounter()) + "");
            } else {
                holder.lap_count.setText("0" + (model.getCounter()));
            }

            holder.show_real_time_laps.setText(model.getMain_count_down_timer());

            if (model.getCounter() == 1) {
                holder.show_current_time_laps.setText(model.getMain_count_down_timer());
            } else {
                holder.show_current_time_laps.setText(model.getCurrent_count_down_timer());
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        synchronized void add_item() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyItemInserted(0);
                    recylerView.scrollToPosition(0);
                }
            });

        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView lap_count, show_real_time_laps, show_current_time_laps;
        LinearLayout show_laps_layout;

        ViewHolder(View itemView) {
            super(itemView);

            lap_count = itemView.findViewById(R.id.lap_count);
            show_real_time_laps = itemView.findViewById(R.id.show_real_time_laps);
            show_current_time_laps = itemView.findViewById(R.id.show_current_time_laps);
            show_laps_layout = itemView.findViewById(R.id.show_laps_layout);

        }
    }
}