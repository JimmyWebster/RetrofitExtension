package com.jimmy.rext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

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


    private void phoneAuthVerifyOTP() {

    }


}
