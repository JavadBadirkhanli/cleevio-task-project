package com.javadbadirkhanly.cleeviotaskproject.activities;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.javadbadirkhanly.cleeviotaskproject.R;
import com.javadbadirkhanly.cleeviotaskproject.fragments.FileManagerFragment;
import com.javadbadirkhanly.cleeviotaskproject.java.Constants;
import com.javadbadirkhanly.cleeviotaskproject.java.SharedPreference;
import com.javadbadirkhanly.cleeviotaskproject.models.FileModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by javadbadirkhanly on 12/18/17.
 */

public class SettingsActivity extends AppCompatActivity{

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @BindView(R.id.toolbarSettingsActivity)
    Toolbar toolbar;

    @BindView(R.id.tvFolderNameSettingsActivity)
    TextView tvFolderName;

    @BindView(R.id.clSetDefaultFolderSettingsActivity)
    ConstraintLayout clSetDefaultFolder;

    @BindView(R.id.flPlaceHolderSettingsActivity)
    FrameLayout flPlaceHolder;

    private SharedPreference sharedPreference;

    private static final int PERMISSION_REQUEST_CODE = 1;

    private FragmentManager fragmentManager;
    private Fragment fragment;

    private String parentPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.toolbar_title_settings_activity);
        }

        sharedPreference = new SharedPreference(this);

        if (!sharedPreference.getData(Constants.DEFAULT_FOLDER_PATH).equals("")) {
            tvFolderName.setText(sharedPreference.getData(Constants.FOLDER_NAME));
        }

        fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentByTag(Constants.RETAINED_FRAGMENT_TAG);

        if (Build.VERSION.SDK_INT >= 23){
            if (checkPermission()){
                parentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } else {
                requestPermission();
            }
        } else {
            parentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        fragment = fragmentManager.findFragmentByTag(Constants.RETAINED_FRAGMENT_TAG);

        if (fragment == null) {
            Log.d(TAG, "onCreate: fragment is null");
            addFragment(parentPath);
        } else {
            Log.d(TAG, "onCreate: fragment is NOT null");
        }

        clSetDefaultFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragment(parentPath);
            }
        });

        clSetDefaultFolder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showClearDefaultFolder();
                return true;
            }
        });
    }

    private void showClearDefaultFolder(){
        new AlertDialog.Builder(this)
                .setTitle("Attention")
                .setMessage("Do you want to clear default folder?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sharedPreference.saveData(Constants.DEFAULT_FOLDER_PATH, parentPath);
                        addFragment(parentPath);
                        sharedPreference.saveData(Constants.FOLDER_NAME, getResources().getString(R.string.parent_directory_name));
                        tvFolderName.setText(getResources().getString(R.string.parent_directory_name));
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    public void addFragment(String path) {
        if (fragmentManager.getBackStackEntryCount() > 0)
            fragmentManager.popBackStackImmediate();
        Log.d(TAG, "addFragment: path: " + path);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        fragmentTransaction.addToBackStack("fragment");
        fragmentTransaction.add(R.id.flPlaceHolderSettingsActivity, FileManagerFragment.newInstance(path), "retainedFragment");
        fragmentTransaction.commit();
    }

    public void removeFragment(FileModel fileModel) {
        tvFolderName.setText(fileModel.getName());
        sharedPreference.saveData(Constants.FOLDER_NAME, fileModel.getName());
        sharedPreference.saveData(Constants.DEFAULT_FOLDER_PATH, fileModel.getPath());
        Toast.makeText(this, "For clearing default folder long press to settings item", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission Granted, Now you can use local drive .");
                    parentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                } else {
                    Log.d(TAG, "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
