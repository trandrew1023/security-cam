package com.isanga.securitycam.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.video.VideoQuality;

import android.content.pm.ActivityInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.isanga.securitycam.R;

public class StreamerActivity extends AppCompatActivity
        implements Session.Callback, SurfaceHolder.Callback {

    private String TAG = "StreamerActivity";

    private Session mSession;
    private Button mBtnStartStop, mButtonSwap;
    private ImageButton mButtonHome;
    private EditText mDestText;
    private SurfaceView mSurfaceView;

    /**
     * handle creation of activity, session, and surface
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streamer);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mBtnStartStop = findViewById(R.id.btnStartStop);
        mButtonHome = findViewById(R.id.homeButtonS);
        mButtonSwap = findViewById(R.id.btnSwap);
        mDestText = findViewById(R.id.destText);
        mSurfaceView = findViewById(R.id.surface);

        mSession = SessionBuilder.getInstance()
                .setCallback(this)
                .setSurfaceView(mSurfaceView)
                .setPreviewOrientation(90)
                .setContext(getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_AAC)
                .setAudioQuality(new AudioQuality(16000, 32000))
                .setVideoEncoder(SessionBuilder.VIDEO_H264)
                .setVideoQuality(new VideoQuality(320, 240, 20, 512000))
                .build();

        mSurfaceView.getHolder().addCallback(this);

        mBtnStartStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mSession.setDestination(mDestText.getText().toString());
                if (!mSession.isStreaming()) {
                    mSession.configure();
                } else {
                    mSession.stop();
                }
                mBtnStartStop.setEnabled(false);
            }
        });
        mButtonSwap.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mSession.switchCamera();
            }
        });
        mButtonHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * Ensure the text matches the state of the session
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mSession.isStreaming()) {
            stopStyle();
        } else {
            startStyle();
        }
    }

    public void onPreviewStarted() {
        Log.d(TAG, "onPreviewStarted: ");
    }

    /**
     * Cleanup the activity by destroying session
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSession.release();
    }

    /**
     * Dumps a string for a file to be played on the running computer.
     * INSTRUCTIONS <br>
     * <ol>
     * <li> 1) Find the logcat line from this method </li>
     * <li>2) Copy paste the text to a file.sdp</li>
     * <li>3) Open file.sdp to view the stream!</li></ol>
     * Flipping the camera fixes any green distortion
     */
    @Override
    public void onSessionConfigured() {
        // Once the stream is configured, you can get a SDP formated session description
        // that you can send to the receiver of the stream.
        // For example, to receive the stream in VLC, store the session description in a .sdp file
        // and open it with VLC while streming.
        Log.d(TAG, "Create a file.sdp with the following: \n" + mSession.getSessionDescription());
        mSession.start();
    }

    /**
     * libstreaming session triggers on start, we update the on screen button text to reflect current state
     */
    @Override
    public void onSessionStarted() {
        Log.d(TAG, "Streaming session started.");
        stopStyle();
        mBtnStartStop.setEnabled(true);
    }

    /**
     * libstreaming session triggers on stop, we update the on screen button text to reflect current state
     */
    @Override
    public void onSessionStopped() {
        Log.d(TAG, "onSessionStopped: ");
        startStyle();
        mBtnStartStop.setEnabled(true);
    }

    /**
     * Triggered by in progress streams from libstreaming session
     *
     * @param bitrate the rate of video streaming
     */
    @Override
    public void onBitrateUpdate(long bitrate) {
        //Logging bitrate is overkill
        //Log.d(TAG, "onBitrateUpdate: " + bitrate);
    }

    /**
     * Triggered by errors in libstreaming session
     *
     * @param message    error code
     * @param streamType audio or video error
     * @param e          the exception raised
     */
    @Override
    public void onSessionError(int message, int streamType, Exception e) {
        Log.e(TAG, "onSessionError", e);
    }

    /**
     * resize surface to match screen size
     *
     * @param holder surface changed
     * @param format unused
     * @param width  unused
     * @param height unused
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        holder.setFixedSize(metrics.heightPixels, metrics.widthPixels);
    }

    /**
     * Triggered by surface creation, preview session since
     *
     * @param holder the surface that was created
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSession.startPreview();
    }

    /**
     * Triggered by surface destruction, end session since it wont be displayed
     *
     * @param holder the surface that was destroyed
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSession.stop();
    }

    /**
     * stylize start button
     */
    private void startStyle() {
        mBtnStartStop.setBackgroundColor(0xFFB9F6CA);
        mBtnStartStop.setText(R.string.start);
    }

    /**
     * stylize stop button
     */
    private void stopStyle() {
        mBtnStartStop.setBackgroundColor(0xFFFF8A80);
        mBtnStartStop.setText(R.string.stop);
    }
}
