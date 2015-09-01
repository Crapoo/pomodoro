/*
    Pomodoro is a simple Pomodoro Technique app for Android
    Copyright (C) 2015 Matteo Taroli <contact@matteotaroli.be>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package be.matteotaroli.pomodoro;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Runs the timer and visually shows its progression.
 */

public class TimerActivity extends AppCompatActivity {
    private static final String TAG = "TimerActivity";

    private TextView mMinutesTv;
    private TextView mSecondsTv;

    /* Timer elements */
    private Handler mHandler;
    private Runnable mRunnable;
    private boolean mTimerStarted = false;
    private Vibrator mVibrator;

    private int mTimerCurrentTime;

    /* Timer constants */
    private final int mTimerStartTime = 5 * 60;
    private final long[] mVibratorPattern = {0, 1500, 500, 1500, 500, 1500};

    @Override
    @TargetApi(21)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        /* Set status bar color on KitKat+ */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.BackgroundColorDarker));
        }


        LinearLayout mTimerLayout = (LinearLayout) findViewById(R.id.timer_layout);
        mTimerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer();
            }
        });
        mMinutesTv = (TextView) findViewById(R.id.minutes_textview);
        mSecondsTv = (TextView) findViewById(R.id.seconds_textview);
        resetUI();

        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }


    /**
     * Resets the timer to the start time.
     */

    public void resetUI() {
        updateUI(mTimerStartTime);
    }

    /**
     * Sets the timer to the actual time.
     *
     * @param timeInSeconds Actual time in seconds
     */

    public void updateUI(long timeInSeconds) {
        int minutes = (int) timeInSeconds / 60;
        int seconds = (int) timeInSeconds - minutes * 60;
        mMinutesTv.setText(String.format("%02d", minutes));
        mSecondsTv.setText(String.format("%02d", seconds));
    }

    /**
     * Starts or stops the timer according to its current state.
     */

    public void timer() {
        if (!mTimerStarted) {
            Log.d(TAG, "Start timer");
            mTimerStarted = true;
            startTimer();
        } else {
            Log.d(TAG, "Stop timer");
            mTimerStarted = false;
            stopTimer();
        }
    }

    /**
     * Starts the timer.
     */

    public void startTimer() {
        mTimerCurrentTime = mTimerStartTime;
        resetUI();
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (--mTimerCurrentTime <= 0) {
                    updateUI(0);
                    mVibrator.vibrate(mVibratorPattern, -1);
                    stopTimer();
                } else {
                    updateUI(mTimerCurrentTime);
                    mHandler.postDelayed(mRunnable, 1000);
                }
            }
        };
        mHandler.postDelayed(mRunnable, 1000);
    }

    /**
     * Stops the timer.
     */

    public void stopTimer() {
        mHandler.removeCallbacks(mRunnable);
        mHandler = null;
        mRunnable = null;
    }
}