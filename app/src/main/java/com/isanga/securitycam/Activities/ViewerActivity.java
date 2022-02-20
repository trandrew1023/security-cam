package com.isanga.securitycam.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.isanga.securitycam.R;


public class ViewerActivity extends AppCompatActivity {

    public final static String TAG = "ViewerActivity";

    private VideoView mVideoView;
    private EditText mIpText;
    private Uri mUri;
    private ImageButton mButtonHome;


    /**
     * initialize the state of the app by creating correct text box interactions and initializing the video
     * The video will take some time to open
     * VLC stream capture-card format H264 + AAC with container TS
     * Add RTSP destination
     * Set the path to /test.ts and port to 8554
     * Configure profile to be MPEG-TS, H-264, and MPEG 4 Audio AAC
     * Click stream
     *
     * @param savedInstanceState bundle containing init info
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_viewer);

        mButtonHome = findViewById(R.id.btnHome);
        mVideoView = findViewById(R.id.videoView);
        mUri = Uri.parse(getString(R.string.enter_ip));
        updateVideo(mUri);

        mIpText = findViewById(R.id.editTextIP);

        mIpText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    Uri fresh = Uri.parse(textView.getText().toString());
                    if (fresh != null) {
                        updateVideo(fresh);
                        Log.d(TAG, "onEditorAction: " + fresh.toString());
                    } else {
                        Toast.makeText(ViewerActivity.this, "Invalid URI", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
        mButtonHome.setOnClickListener(new View.OnClickListener() {
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
     * cleanup the video stream
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.suspend();
    }

    /**
     * queue up a new mUri to stream
     *
     * @param uri
     */
    private void updateVideo(Uri uri) {
        this.mUri = uri;
        mVideoView.stopPlayback();
        mVideoView.suspend();
        mVideoView.setVideoURI(uri);
        mVideoView.start();
        resizeVideo();
    }

    /**
     * make the drawn surface aspect look correct
     */
    private void resizeVideo() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        mVideoView.setLayoutParams(params);
    }


}
