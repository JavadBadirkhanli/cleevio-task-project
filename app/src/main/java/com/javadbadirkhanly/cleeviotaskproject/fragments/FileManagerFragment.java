package com.javadbadirkhanly.cleeviotaskproject.fragments;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.javadbadirkhanly.cleeviotaskproject.BuildConfig;
import com.javadbadirkhanly.cleeviotaskproject.R;
import com.javadbadirkhanly.cleeviotaskproject.activities.MainActivity;
import com.javadbadirkhanly.cleeviotaskproject.activities.SettingsActivity;
import com.javadbadirkhanly.cleeviotaskproject.adapters.FileManagerAdapter;
import com.javadbadirkhanly.cleeviotaskproject.java.Constants;
import com.javadbadirkhanly.cleeviotaskproject.java.FileTypeDetector;
import com.javadbadirkhanly.cleeviotaskproject.java.SharedPreference;
import com.javadbadirkhanly.cleeviotaskproject.models.FileModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by javadbadirkhanly on 12/18/17.
 */

public class FileManagerFragment extends Fragment implements FileManagerAdapter.Listener {

    private static final String TAG = FileManagerFragment.class.getSimpleName();

    @BindView(R.id.rvFilesFileManagerFragment)
    RecyclerView rvFiles;

    private SharedPreference sharedPreference;

    private RecyclerView.LayoutManager layoutManager;
    private FileManagerAdapter adapter;

    private List<FileModel> fileModelList;

    private String path;

    private int longClickedItemPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files_manager, container, false);
        ButterKnife.bind(this, view);
        setRetainInstance(true);

        sharedPreference = new SharedPreference(getActivity());

        // get path from bundle
        path = getArguments().getString("path");
        Log.d(TAG, "onCreateView: path: " + path);

        if (path != null) {
            new LoadData().execute(path);
        }

        if (getActivity() instanceof MainActivity) {
            getActivity().findViewById(R.id.ivDeleteMainActivity).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteFileDialog();
                }
            });
        }

        return view;
    }

    private class LoadData extends AsyncTask<String, String, List<FileModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<FileModel> doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: ");

            return loadFiles(new File(path));
        }

        @Override
        protected void onPostExecute(List<FileModel> fileModelList) {
            super.onPostExecute(fileModelList);
            Log.d(TAG, "onPostExecute: ");
            fillRecycleData();
        }
    }

    private boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    private void showDeleteFileDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Attention")
                .setMessage("Delete file?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (deleteFile(fileModelList.get(longClickedItemPosition).getPath())) {
                            fileModelList.remove(longClickedItemPosition);
                            adapter.notifyItemRemoved(longClickedItemPosition);
                            Log.d(TAG, "onClick: file deleted successfully");
                            ((MainActivity) getActivity()).disableCAB();
                        } else {
                            Log.d(TAG, "onClick: file not deleted");
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private List<FileModel> loadFiles(File f) {
        fileModelList = new ArrayList<>();
        File[] files = f.listFiles();

        for (File file : files) {
            FileModel fileModel = new FileModel();
            fileModel.setName(file.getName());
            fileModel.setPath(file.getPath());
            fileModel.setFile(file);

            if (file.isDirectory())
                fileModel.setDirectory(true);

            fileModelList.add(fileModel);
        }

        return fileModelList;
    }

    private void fillRecycleData() {
        if (getData() != null) {
            fileModelList = getData();
        }

        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new FileManagerAdapter(getActivity(), fileModelList, this);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            rvFiles.setLayoutManager(layoutManager);
        } else {
            rvFiles.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        }

        rvFiles.setAdapter(adapter);
    }

    public void setData(List<FileModel> fileModelList) {
        this.fileModelList = fileModelList;
    }

    public List<FileModel> getData() {
        return fileModelList;
    }

    private void openFile(File file) {
        MimeTypeMap myMime = MimeTypeMap.getSingleton();

        Intent newIntent = new Intent(Intent.ACTION_VIEW);

        String mimeType = myMime.getMimeTypeFromExtension(FileTypeDetector.FileExtension(file.getName()).substring(1));
        Uri fileUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", file);
        Log.d(TAG, "openFile: fileUri: " + fileUri);

        newIntent.setDataAndType(fileUri, mimeType);
        newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void ClickListener(int position) {
        Log.d(TAG, "sendItemPosition: id: " + position);
        Log.d(TAG, "sendItemPosition: path: " + fileModelList.get(position).getPath());

        if (getActivity() instanceof MainActivity) {

            Log.d(TAG, "sendItemPosition: instance is MainActivity");
            if (fileModelList.get(position).isDirectory()) {

                // getting clicked folder path and add fragment with this folder items
                ((MainActivity) getActivity()).addFragment(fileModelList.get(position).getPath());
                sharedPreference.saveData(Constants.FILE_PATH, fileModelList.get(position).getPath());

            } else
                // opening clicked file with default application
                openFile(fileModelList.get(position).getFile());

        } else if (getActivity() instanceof SettingsActivity) {

            Log.d(TAG, "sendItemPosition: instance is SettingsActivity");
            if (fileModelList.get(position).isDirectory())

                // getting clicked folder path and add fragment with this folder items
                ((SettingsActivity) getActivity()).addFragment(fileModelList.get(position).getPath());
            else
                // opening clicked file with default application
                openFile(fileModelList.get(position).getFile());
        }
    }

    @Override
    public void LongClickListener(int position) {
        Log.d(TAG, "LongClickListener: long clicked to item position: " + position);
        longClickedItemPosition = position;

        if (getActivity() instanceof MainActivity) {

            // enabling CAB when long clicked to item in MainActivity
            ((MainActivity) getActivity()).enableCAB(fileModelList.get(position).getName());
        } else if (getActivity() instanceof SettingsActivity) {

            if (fileModelList.get(position).isDirectory()) {
                // add folder path as default path when long clicked to item in SettingsActivity
                ((SettingsActivity) getActivity()).removeFragment(fileModelList.get(position));
            } else
                Toast.makeText(getActivity(), "File can't be a default folder", Toast.LENGTH_SHORT).show();
        }
    }

    public static FileManagerFragment newInstance(String path) {
        FileManagerFragment filesListFragment = new FileManagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        filesListFragment.setArguments(bundle);
        return filesListFragment;
    }
}
