package com.javadbadirkhanly.cleeviotaskproject.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.javadbadirkhanly.cleeviotaskproject.R;
import com.javadbadirkhanly.cleeviotaskproject.fragments.FileManagerFragment;
import com.javadbadirkhanly.cleeviotaskproject.java.Constants;
import com.javadbadirkhanly.cleeviotaskproject.java.SharedPreference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbarMainActivity)
    Toolbar toolbar;

    @BindView(R.id.ivSettingsMainActivity)
    ImageView ivSettings;

    @BindView(R.id.ivRefreshMainActivity)
    ImageView ivRefresh;

    @BindView(R.id.ivBackToolbarMainActivity)
    ImageView ivBackToolbar;

    @BindView(R.id.ivDeleteMainActivity)
    ImageView ivDelete;

    @BindView(R.id.tvToolbarTitleMainActivity)
    TextView tvToolbarTitle;

    @BindView(R.id.clParentViewMainActivity)
    ConstraintLayout clParentView;

    @BindView(R.id.flPlaceHolderMainActivity)
    FrameLayout flPlaceHolder;

    private static final int PERMISSION_REQUEST_CODE = 1;

    private SharedPreference sharedPreference;

    private FragmentManager fragmentManager;
    private Fragment fragment;

    private String defaultPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        sharedPreference = new SharedPreference(this);

        tvToolbarTitle.setText(getResources().getString(R.string.toolbar_title_main_activity));

        setOnClicks();

        fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentByTag(Constants.RETAINED_FRAGMENT_TAG);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                if (sharedPreference.getData(Constants.DEFAULT_FOLDER_PATH).equals("")) {
                    defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                } else
                    defaultPath = sharedPreference.getData(Constants.DEFAULT_FOLDER_PATH);

                if (fragment == null)
                    addFragment(defaultPath);

            } else {
                requestPermission();
            }
        } else {
            if (sharedPreference.getData(Constants.DEFAULT_FOLDER_PATH).equals("")) {
                defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } else
                defaultPath = sharedPreference.getData(Constants.DEFAULT_FOLDER_PATH);

            if (fragment == null)
                addFragment(defaultPath);
        }
    }

    private void setOnClicks() {
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreference.getData(Constants.DEFAULT_FOLDER_PATH).equals("")) {
                    defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                } else {
                    defaultPath = sharedPreference.getData(Constants.DEFAULT_FOLDER_PATH);
                }

                addFragment(defaultPath);
            }
        });

        ivBackToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableCAB();
            }
        });
    }

    public void addFragment(String path) {
        if (fragmentManager.getBackStackEntryCount() > 0)
            fragmentManager.popBackStackImmediate();
        Log.d(TAG, "addFragment: path: " + path);
        flPlaceHolder.setVisibility(View.VISIBLE);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        fragmentTransaction.addToBackStack("fragment");
        fragmentTransaction.add(R.id.flPlaceHolderMainActivity, FileManagerFragment.newInstance(path), "retainedFragment");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //super.onSaveInstanceState(outState, outPersistentState);
    }

    public void enableCAB(String name) {
        ivBackToolbar.setVisibility(View.VISIBLE);
        ivDelete.setVisibility(View.VISIBLE);
        ivRefresh.setVisibility(View.GONE);
        ivSettings.setVisibility(View.GONE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorCAB));

        tvToolbarTitle.setText(getResources().getString(R.string.toolbar_cab_title, name));
    }

    public void disableCAB() {
        ivBackToolbar.setVisibility(View.GONE);
        ivDelete.setVisibility(View.GONE);
        ivSettings.setVisibility(View.VISIBLE);
        ivRefresh.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tvToolbarTitle.setText(getResources().getString(R.string.toolbar_title_main_activity));
    }

    private boolean checkPermission() {
        int resultWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int resultRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.d(TAG, "checkPermission: resultWrite: " + resultWrite + "   resultRead: " + resultRead);
        return resultWrite == PackageManager.PERMISSION_GRANTED && resultRead == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(TAG, "onRequestPermissionsResult: grantResult[0]: " + grantResults[0] + "    grantResult[1]: " + grantResults[1]);
                    Log.d(TAG, "Permission Granted, Now you can use local drive .");
                    if (sharedPreference.getData(Constants.DEFAULT_FOLDER_PATH).equals("")) {
                        defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    } else
                        defaultPath = sharedPreference.getData(Constants.DEFAULT_FOLDER_PATH);

                    //if (fragment == null) {
                        addFragment(defaultPath);
                    //}
                } else {
                    Log.d(TAG, "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
