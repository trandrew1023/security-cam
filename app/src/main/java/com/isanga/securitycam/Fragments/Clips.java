package com.isanga.securitycam.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.isanga.securitycam.Adapters.ClipsRecyclerViewAdapter;
import com.isanga.securitycam.Models.ClipsModel;
import com.isanga.securitycam.R;

import java.io.File;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Clips extends Fragment implements ClipsRecyclerViewAdapter.ClipsRecyclerViewListener {

    /**
     * List to preview clips
     */
    private RecyclerView recyclerView;
    /**
     * Holds a list of clips
     */
    private ArrayList<ClipsModel> models;
    /**
     * Manager for recyclerview
     */
    private RecyclerView.LayoutManager manager;
    /**
     * Adapter for recyclerview
     */
    private ClipsRecyclerViewAdapter adapter;
    /**
     * Popup dialog to edit title of clips
     */
    private AlertDialog editTitle;
    /**
     * EditText for AlertDialog
     */
    private EditText titleEditor;
    /**
     * Current id of clip title being edited
     */
    private int modelID;
    /**
     * Media folder
     */
    private File folder;

    public Clips() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         * Inflates the layout for this fragment
         */
        View view = inflater.inflate(R.layout.fragment_clips, container, false);
        /**
         * Get the clips folder
         */
        folder = getContext().getExternalFilesDir("media");
        /**
         * Initializes private variables
         */
        editTitle = new AlertDialog.Builder(getContext()).create();
        titleEditor = new EditText(getContext());
        editTitle.setTitle("Edit title");
        editTitle.setView(titleEditor);

        /**
         * Handles editing titles when SAVE is clicked
         */
        editTitle.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String path = folder.getAbsolutePath();
                File video = new File(path + "/" + models.get(modelID).getTitle());
                String newTitle = titleEditor.getText().toString();
                File newVideo = new File(path + "/" + newTitle);
                video.renameTo(newVideo);
                models.get(modelID).setTitle(newTitle);
                models.get(modelID).setThumbnail(newVideo);
                adapter.notifyDataSetChanged();
            }
        });

        if (savedInstanceState == null) {
            setUpRecyclerView(view);
            loadThumbnails();
        }
        /**
         * Need to register recyclerview for ContextMenu to work
         */
        registerForContextMenu(recyclerView);
        return view;

    }

    /**
     * Sets up the recycler view
     *
     * @param view
     */
    private void setUpRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.clips_recyclerview);
        models = new ArrayList<>();
        manager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(manager);
        adapter = new ClipsRecyclerViewAdapter(getContext(), models, this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Handles click event on items from the list
     *
     * @param position position of the item being clicked
     */
    @Override
    public void onItemClick(int position) {
        //If video does not have a default player, prompt apps that support video playing
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = models.get(position).getThumbnail();
        //FileProvider is need on sdk targets bigger than 23
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
        intent.setDataAndType(uri, "video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    /**
     * Handles menu clicks when holding a clip
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clip_delete:
                deleteClip(item.getGroupId());
                return true;
            case R.id.clip_share:
                shareClip(item.getGroupId());
                return true;
            case R.id.clip_edit:
                editClip(item.getGroupId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    /**
     * Deletes selected clip from the recyclerview and from storage
     *
     * @param id
     */
    private void deleteClip(int id) {
        String path = folder.getAbsolutePath() + "/" + models.get(id).getTitle();
        File video = new File(path);
        video.delete();
        models.remove(id);
        adapter.notifyDataSetChanged();
        Log.d(TAG, "deleteClip: " + path);
    }

    /**
     * Starts an intent to share selected video
     *
     * @param id
     */
    private void shareClip(int id) {
        String path = folder.getAbsolutePath() + "/" + models.get(id).getTitle();
        File video = new File(path);
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", video);
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "send"));
    }

    /**
     * Opens up AlertDialog to allow editing title of selected clip
     *
     * @param id
     */
    private void editClip(int id) {
        modelID = id;
        titleEditor.setText(models.get(id).getTitle());
        editTitle.show();
    }

    /**
     * Loads thumbnails from media folder
     */
    private void loadThumbnails() {
        String path = folder.getAbsolutePath();
        Log.d(TAG, "loadThumbnails: " + path);
        if (folder.exists()) {
            File[] videos = folder.listFiles();
            if (videos != null) {
                for (File video : videos) {
                    Log.d(TAG, "currentThumbnail: " + video.getAbsolutePath());
                    if (video.length() != 0)
                        models.add(new ClipsModel(video.getName(), video));
                }
            }
        } else {
            folder.mkdirs();
        }

    }
}
