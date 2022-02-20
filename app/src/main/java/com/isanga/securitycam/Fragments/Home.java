package com.isanga.securitycam.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.isanga.securitycam.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    /**
     * Does nothing.
     */
    private boolean spyMode;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_home, container, false);
        TextView textView = layout.findViewById(R.id.about);
        textView.setText("Welcome to SecurityCam!\n\n" +
                "Use this app to:\n" +
                "   - View your camera feed and record clips\n" +
                "   - View, rename, share, and delete your clips\n" +
                "   - Use your Google Account to save clips to the cloud\n" +
                "   - Stream your camera to a remote server for viewing and processing\n" +
                "   - View live streams from IP Cameras\n\n" +
                "How to use the app:\n" +
                "   - Swipe right from the left side of the screen to view the menu\n" +
                "   - Home: You are here\n" +
                "   - Camera: View your camera's feed and start recording those thieves\n" +
                "   - Viewer: View a stream from a specified IP Camera\n" +
                "   - Streamer: Stream your camera's feed to a remote IP\n" +
                "   - Clips: View your saved clips\n" +
                "   - User: Sign into your Google Account to save your clips to the cloud\n\n" +
                "You can view our project here: https://git.ece.iastate.edu/isanga/securitycam\n\n" +
                "This app was developed by Andrew Tran, Isaac Sanga, and Lucas Jedlicka.");

        ImageView imageView = layout.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!spyMode) {
                    Toast spyToast = Toast.makeText(view.getContext(), "Spy mode activated", Toast.LENGTH_SHORT);
                    spyToast.show();
                    spyMode = true;
                } else {
                    Toast spyToast = Toast.makeText(view.getContext(), "Spy mode deactivated", Toast.LENGTH_SHORT);
                    spyToast.show();
                    spyMode = false;
                }
            }
        });

        //make it so the link is clickable
        Linkify.addLinks(textView, Linkify.PHONE_NUMBERS | Linkify.WEB_URLS);

        return layout;
    }

}
