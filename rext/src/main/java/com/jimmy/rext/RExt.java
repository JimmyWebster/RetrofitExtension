package com.jimmy.rext;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RExt {


    //  Returns HTTPClient
    public static OkHttpClient.Builder getHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(loggingInterceptor);

        return httpClient;
    }


    //  Initialize mobs
    public static void initializeMob(AdView mAdView, Context context) {
//
//        MobileAds.initialize(context, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//
//            }
//        });
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
    }

    public static DatabaseReference getReference(String BASE_TAG) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(BASE_TAG);
        return databaseReference;
    }


    //  Add to Realtime Database
    public static void addToDatabase(String BASE_TAG, Object object, String ID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(BASE_TAG);

        databaseReference.child(ID).setValue(object);
    }


    //  Get Unique ID for Database
    public static String getUniqueIdDatabase(String BASE_TAG) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(BASE_TAG);
//      Get unique ID
        String id = databaseReference.push().getKey();
        return id;
    }


    public <T> List<T> getDataFromDatabaseOP(Class<T> klazz, DataSnapshot dataSnapshot) {
        List<T> list = new ArrayList<>();
        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()
        ) {
            T item = itemSnapshot.getValue(klazz);
            list.add(item);
        }
        return list;
    }

    public static void updateDataDatabase(String BASE_TAG, String id, Object object) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(BASE_TAG).child(id);

        databaseReference.setValue(object);
    }

    public static void deleteTwoLevelDataFromDatabase(String itemId, String insideId, String BASE_TAG) {
        DatabaseReference drItem = FirebaseDatabase.getInstance().getReference(BASE_TAG).child(itemId);
        DatabaseReference drInside = FirebaseDatabase.getInstance().getReference(BASE_TAG).child(insideId);

        drItem.removeValue();
        drInside.removeValue();
    }

    public static void deleteDataFromDatabase(String itemId, String BASE_TAG) {
        DatabaseReference drItem = FirebaseDatabase.getInstance().getReference(BASE_TAG).child(itemId);

        drItem.removeValue();
    }


    public static Bitmap getPreview(String videoPath)
            throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());

            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    public static <T> Uri downloadFile(String url, int position, Context context, List<T> list) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, "video" + position + ".mp4");

        long reference = downloadManager.enqueue(request);
        Uri fileUri;
        fileUri = downloadManager.getUriForDownloadedFile(reference);
        if (fileUri != null) {
            return uri;
        } else {
            return downloadManager.getUriForDownloadedFile(reference);
        }


    }

    public static <T> void saveDataToPreferences(Context context, List<T> list) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("videos", json);
        editor.apply();
    }

    public static <T> void loadDataToPreferences(Context context, List<T> list) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("videos", null);
        Type type = new TypeToken<ArrayList<T>>() {
        }.getType();
        list = gson.fromJson(json, type);

        if (list == null) {
            list = new ArrayList<>();
        }

    }

    public static void requestPermissionStorage(Activity activity,int REQUEST_CODE) {
        activity.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_CODE);


    }

    public static <T> void filterListViewByParameterOP(List<T> list,Class<T> klazz){
        List<T> videoListNew = new ArrayList(list);
        List<T> delete = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
//            if (!list.get(i).isDownloaded())
                delete.add(list.get(i));
        }

        videoListNew.removeAll(delete);


    }


}
