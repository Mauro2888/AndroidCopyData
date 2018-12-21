package com.example.maurocaredda.copydata;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //TODO top
    private Button mUplaodData,mNotify,mCheck;
    private static final int ID_UPLAOD = 102;
    private static final int ID_PERMISSION = 1024;
    private static final String LOG = MainActivity.class.getSimpleName();
    private ProgressBar mProgressUpload;
    private ArrayList<ImagesModel> mListUri = new ArrayList();
    private FirebaseJobDispatcher jobDispatcher;
    private StorageReference mStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpdEnvironment();
    }

    public void scheduleJob(){
        Job job = jobDispatcher.newJobBuilder()
                .setService(ServiceBg.class)
                .setTag("JOB JOB JOB")
                .build();

        jobDispatcher.mustSchedule(job);
    }
    //TODO Environment
    public void setUpdEnvironment(){
        mUplaodData = findViewById(R.id.btn_main_upload);
        mProgressUpload = findViewById(R.id.progressBar);
        mNotify = findViewById(R.id.btn_main_notify);
        mCheck = findViewById(R.id.btn_checkdata);
        //listener
        mUplaodData.setOnClickListener(this);
        mNotify.setOnClickListener(this);
        mCheck.setOnClickListener(this);
        //initialize dispatcher
        jobDispatcher  = new FirebaseJobDispatcher(new GooglePlayDriver(this));


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onClick(View view) {
        if (view == mUplaodData){
            mProgressUpload.setProgress(0);
            Intent selectData = new Intent(Intent.ACTION_GET_CONTENT);
            selectData.setType("image/*");
            selectData.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            startActivityForResult(Intent.createChooser(selectData,"select data"),ID_UPLAOD);
        }
        if (view == mNotify){
            scheduleJob();
            requestPermission();
            new AsyncT().execute();
        }
        if (view == mCheck){
            getUrlDownload();
        }
    }

    private void getUrlDownload() {
        mStorage = FirebaseStorage.getInstance().getReference().child("/images");
        

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ID_UPLAOD & resultCode == RESULT_OK) {
            //TODO Multiple
            //Multiple files need getClipData() method
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri uriData = data.getClipData().getItemAt(i).getUri();
                    String fileName = getPathUri(this, uriData);
                    mListUri.add(new ImagesModel(fileName));
                    mStorage = FirebaseStorage.getInstance().getReference().child("images/").child(fileName);
                    UploadTask uploadTask = mStorage.putFile(uriData);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MainActivity.this, "Complete", Toast.LENGTH_SHORT).show();
                            double countProgress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressUpload.setProgress((int) countProgress);

                        }
                    });
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.d(LOG, fileName);

                }
                //TODO Single file
                //single file need obiviously getData() method
            }else if (data.getData() != null){
                Uri singleFile = data.getData();
                String singleFileName = getPathUri(this,singleFile);
                mListUri.clear();
                mListUri.add(new ImagesModel(singleFileName));
                Log.d(LOG, singleFileName);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case ID_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    scheduleJob();
                    requestPermission();
                    new AsyncT().execute();
                }
        }
    }

    public String getPathUri(Context context, Uri uri){
        Cursor cursor = null;
        String result = null;
        try {
            String[]projector = {MediaStore.Images.Media.DISPLAY_NAME};
            cursor = getContentResolver().query(uri,projector,null,null,null);
            int colums = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

            cursor.moveToFirst();
            result = cursor.getString(colums);
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }

        return result;
    }

    @SuppressLint("InlinedApi")
    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE + Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    102);
        } else {
            scheduleJob();
            requestPermission();
            new AsyncT().execute();

        }
    }

    public void getAll(){
        File getFolderFiles = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File[] filesList = getFolderFiles.listFiles();
        for (File i : filesList) {
            if (i.isDirectory()) {
               File[] listing = i.listFiles();
                for (File j : listing) {
                    final String fileName = j.getName();
                    mStorage = FirebaseStorage.getInstance().getReference().child("images/").child(fileName);
                    UploadTask uploadTask = mStorage.putFile(Uri.fromFile(j));
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(LOG, " " + "done " + fileName);
                        }
                    });
                }

            }

        }
    }

    public class AsyncT extends AsyncTask<String,Object,String>{

        @Override
        protected void onPreExecute() {
            Log.d(LOG,"Start Process");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            File getFolderFiles = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File[] filesList = getFolderFiles.listFiles();
            for (File i : filesList) {
                if (i.isDirectory()) {
                    File[] listing = i.listFiles();
                    for (File j : listing) {
                        final String fileName = j.getName();
                        mStorage = FirebaseStorage.getInstance().getReference().child("images/").child(fileName);
                        UploadTask uploadTask = mStorage.putFile(Uri.fromFile(j));
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d(LOG, " " + "during " + fileName);
                            }
                        });
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(LOG,"Completed");
        }
    }

}
