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

import java.util.List;
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


    //  Add to Realtime Database
    public static void addToDatabase(String BASE_TAG, Object object,String ID) {
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


    public static void getDataFromDatabaseOP(String BASE_TAG, final Context context, final List<Object> objectList) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(BASE_TAG);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                objectList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()
                ) {
                    Object object = itemSnapshot.getValue(Object.class);
                    objectList.add(object);

                }
            }

//            TODO: End this method
//            itemAdapter = new ItemAdapter(objectList,MainActivity.this);
//            rv.setAdapter(itemAdapter);
//            itemAdapter.setOnItemClickListener(MainActivity.this);

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


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

    //    OTP
    private void sendVerificationFK(String number, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack, Activity activity) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                activity,
                mCallBack
        );

    }

    private void verifyCodeFK(String code, String verificationId) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
//        signInWithCredentials(credential);
    }

    private void phoneAuthVerifyOTP(){
//      TODO: Finish this method

//       !!!!onCreate {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_main);
//            ButterKnife.bind(this);
//
//
//            mAdView = findViewById(R.id.adView);
//            AdRequest adRequest = new AdRequest.Builder().build();
//            mAdView.loadAd(adRequest);
//            mAuth = FirebaseAuth.getInstance();
//
//            if (mAuth.getCurrentUser()!=null){
//                startActivity(new Intent(getApplicationContext(), VideosActivity.class));
//            }
//
//
//            btnDone.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//
//                    String number = etPhone.getText().toString();
//
//                    if (!number.isEmpty()) {
//
//                        sendVerification(number);
//
//                    } else
//                        Toast.makeText(MainActivity.this, "Empty string", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            btnCode.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    String code = etCode.getText().toString();
//                    if (!code.isEmpty()){
//                        verifyCode(code);
//                    }
//                }
//            });
//        }


//
//        private void sendVerification(String number) {
//            PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                    number,
//                    60,
//                    TimeUnit.SECONDS,
//                    this,
//                    mCallBack
//            );}
//

//        private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            @Override
//            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                super.onCodeSent(s, forceResendingToken);
//                Toast.makeText(MainActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
//                verificationId = s;
//            }
//
//            @Override
//            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                Toast.makeText(MainActivity.this, "Automatically verified", Toast.LENGTH_SHORT).show();
//                if (phoneAuthCredential.getSmsCode() != null)
//                    verifyCode(phoneAuthCredential.getSmsCode());
//            }
//
//            @Override
//            public void onVerificationFailed(@NonNull FirebaseException e) {
//                Toast.makeText(MainActivity.this, "" + e.toString(), Toast.LENGTH_SHORT).show();
//            }
//        };

//
//        private void verifyCode(String code) {
//            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
//            signInWithCredentials(credential);
//        }
//
//        private void signInWithCredentials(PhoneAuthCredential credential) {
//            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//            firebaseAuth.signInWithCredential(credential)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(MainActivity.this, "Successful login", Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(getApplicationContext(), VideosActivity.class));
//                            }else{
//                                Toast.makeText(MainActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        }

    }


}
