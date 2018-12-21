package com.example.maurocaredda.copydata;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class FindFiles extends ContextWrapper implements Runnable {

    public static final String LOG = FindFiles.class.getSimpleName();
    private StorageReference mStorage;
    public FindFiles(Context base) {
        super(base);
    }

    @Override
    public void run() {

        }
    public String getPathUri(Context context, Uri uri) {
        Cursor cursor = null;
        String result = null;
        try {
            String[] projector = {MediaStore.Images.Media.DISPLAY_NAME};
            cursor = context.getContentResolver().query(uri, projector, null, null, null);
            int colums = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

            cursor.moveToFirst();
            result = cursor.getString(colums);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

}


