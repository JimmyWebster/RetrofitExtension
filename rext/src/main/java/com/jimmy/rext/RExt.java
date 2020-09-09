
//      Copyright 2020 Jimmy Webster
//
//      Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the
//      Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
//      and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
//
//      The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
//
//      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
//      MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
//      ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
//      THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


package com.jimmy.rext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.cs.googlemaproute.DrawRoute;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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


    public static String fkGetHttpWebSite(String URL) {
        try {
            java.net.URL mySite = new URL(URL);
            URLConnection yc = mySite.openConnection();
            Scanner in = new Scanner(new InputStreamReader(yc.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            while (in.hasNext()) {
                stringBuilder.append(in.next());
            }
            in.close();

            return stringBuilder.toString();
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }


    // return sum of ints in list
    // list may not be null
    private static int fkSum(int[] list) {
        assert list != null : "Failed precondition, sum: parameter list" +
                " may not be null.";
        int total = 0;
        for (int x : list) {
            total += x;
        }
        return total;
    }


    // pre: url != null
    // Connect to the URL specified by the String url.
    // Map characters to index in array.
    // All non ASCII character dumped into one element of array
    // If IOException occurs message printed and array of
    // length 0 returned.
    public static int[] fkCreateFreqTableURL(String url) {
        final int NUM_ASCII_CHAR = 128;
        if (url == null)
            throw new IllegalArgumentException("Violation of precondition. parameter url must not be null.");

        int[] freqs = new int[NUM_ASCII_CHAR];
        try {
            URL inputURL = new URL(url);
            InputStreamReader in
                    = new InputStreamReader(inputURL.openStream());

            while (in.ready()) {
                int c = in.read();
                if (0 <= c && c < freqs.length)
                    freqs[c]++;
                else
                    System.out.println("Non ASCII char: " + c + " " + (char) c);
            }
            in.close();
        } catch (MalformedURLException e) {
            System.out.println("Bad URL.");
            freqs = new int[0];
        } catch (IOException e) {
            System.out.println("Unable to read from resource." + e);
            freqs = new int[0];
        }
        return freqs;
    }


    // Connect to the file specified by the String fileName.
    // Assumes it is in same directory as compiled code.
    // Map characters to index in array.
    public static int[] fkCreateTable(String fileName) throws FileNotFoundException, IOException {
        final int NUM_ASCII_CHAR = 128;
        int[] freqs = new int[NUM_ASCII_CHAR];
        File f = new File(fileName);
        FileReader r = new FileReader(f);
        while (r.ready()) {
            int ch = r.read();
//            if( 0 <= ch && ch <= NUM_ASCII_CHAR)
//                freqs[ch]++;
//            else
//                freqs[INDEX_NON_ASCII]++;
            if (0 <= ch && ch < freqs.length)
                freqs[ch]++;
            else
                System.out.println((char) ch);

        }
        r.close();
        return freqs;
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


    public <T> List<T> getDataFromDatabase(Class<T> klazz, DataSnapshot dataSnapshot) {
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

    public static <T> void saveDataToPreferences(Context context, List<T> list, String prefTag) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(prefTag, json);
        editor.apply();
    }

    public static <T> List<T> loadDataFromPreferences(Context context, List<T> list, String prefTag) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(prefTag, null);
        Type type = new TypeToken<ArrayList<T>>() {
        }.getType();
        list = gson.fromJson(json, type);

        if (list == null) {
            list = new ArrayList<>();
        }

        return list;

    }

    public static void requestPermissionStorage(Activity activity, int REQUEST_CODE) {
        activity.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_CODE);


    }

    public static <T> void filterListViewByParameterOP(List<T> list, Class<T> klazz) {
        List<T> videoListNew = new ArrayList(list);
        List<T> delete = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
//            if (!list.get(i).isDownloaded())
            delete.add(list.get(i));
        }

        videoListNew.removeAll(delete);


    }

    public static void setScreenFlags(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }


    public static Integer moveVideo(int millis, VideoView videoView) {
        float duration = (float) videoView.getDuration();
        float ration = millis / duration;
        int percent = (int) (ration * 100);
        videoView.seekTo(millis);
        return videoView.getCurrentPosition() / 1000;
//        videoView.start();

    }


    private void initVideoOP(final VideoView mVideoView, String m_video_url, boolean isDownloaded, Uri downloadedUri, final int mSecondsMove) {
        Uri uri;
        if (!isDownloaded) {
            uri = Uri.parse(m_video_url);
            mVideoView.setVideoURI(uri);
        } else {
            Uri fileUri = downloadedUri;
            mVideoView.setVideoURI(fileUri);

        }

        mVideoView.requestFocus();

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                System.out.println();
                return false;
            }
        });
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mVideoView.start();

//                loadData(video.getName()); Loads and moves
                moveVideo(mSecondsMove, mVideoView);
//
                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        if (mVideoView.getDuration() != -1 && !isPaused) {
//                            secondsCur++;
//                            seekBar.setProgress((int) ((((float) (secondsCur * 1000) / mVideoView.getDuration()) * 100)));
//
//                        }
//                        if (secondsCur * 1000 < mVideoView.getDuration()) {
//                            handler.postDelayed(this, 1000);
//                        }
                    }
                });

            }
        });


    }


    public static <T> void saveOnLeaveOP(Context context, VideoView videoView, T editedVideo, List<T> videoList, String TAG) {
        if (videoView.getCurrentPosition() != 0) {
//            Video editedVideo = new Video(videoList.size(), video.getName(), videoView.getCurrentPosition(), videoView.getDuration(), video.getUrl(), false,null);

            for (int i = 0; i < videoList.size(); i++) {
//                if (videoList.get(i).getUrl().equals(editedVideo.getUrl())) {

                videoList.set(i, editedVideo);

            }

            saveDataToPreferences(context, videoList, TAG);
        }
    }

    public static List<String> openActivityFromUrl(Activity activity) {

        //   GET URI. If  link
        Uri uri = activity.getIntent().getData();
        if (uri != null) {
            List<String> params = uri.getPathSegments();
//            Get last parameter = id
//            String id = params.get(params.size() - 1);
            return params;
        }

        return null;

    }

    public static String randomHash(Integer seed) {
//        TODO: Finish this method
        List<String> hashes = new ArrayList<>();
        hashes.add("5TlAr02Qo5mOxtCepl8OCDSIEghcUCbdTUu");
        hashes.add("ZHFYu3lCNsWRv8K1Es3WU379zItWoqCcpTy");
        hashes.add("SyADqUkZxGuyY63OwmvtopZg1C4qzHQCxHU");
        hashes.add("RA8gBwB8eHO3Yz22m8KnfOYwhqeorjgTCBI");
        hashes.add("PWdWkOhLAGUyV21HQF2b86O7XjULMRqCzqB");
        hashes.add("IJS9WKw777rpPKZZ2eXIF6TVE3ah68ptov9");

        int randomPick = (int) ((Math.random() * (hashes.size())));
        return hashes.get(randomPick);
    }


    public static void nfCreateChannelInApp(Context context) {
        final String CHANNEL_ID = "exampleServiceChannel";

        NotificationChannel serviceCHannel = new NotificationChannel(CHANNEL_ID,
                "EXAMPLE SERVICE CHANNEL"
                , NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceCHannel);
    }

    public static void nfCreateExampleService2OP(Context context, Object activity) {
        final String CHANNEL_ID = "exampleServiceChannel";

//        String extra = intent.getStringExtra("inputExtra");
//        toaster();

        Intent notificationIntent = new Intent(context, activity.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(context,
                CHANNEL_ID)
                .setContentText("This app is running")
                .setContentTitle("Nice app!")
//                .setSmallIcon()
                .setContentIntent(pendingIntent)
                .build();

//        startForeground(1,notification);
//        return START_STICKY;
    }

    public static void nfCreateFBMessagingService3(Object remoteMessage, Context context) {
//        ov: on message received
//        if (remoteMessage.getData() != null) {
        sendFictionNew3OP(remoteMessage, context, Activity.class);


    }

    public static <T> void sendFictionNew3OP(Object remoteMessage, Context context, Class<T> clasz) {
//        Map<String, String> data = remoteMessage.getData();
        Map<String, String> data = (Map<String, String>) remoteMessage;
        String title = data.get("title");
        String content = data.get("content");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "TEST";
        @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                "MyNotification", NotificationManager.IMPORTANCE_MAX);

        notificationChannel.setDescription("Text");
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.MAGENTA);
//        notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000,500,100,100,100,1000});
        notificationChannel.enableVibration(false);
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

//        new intent
        Intent notificationIntent = new Intent(context, clasz);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setTicker("Hearty365")
                .setContentTitle(title)
                .setContentText(content)
                .setColor(Color.MAGENTA)
                .setContentIntent(contentIntent)
                .setContentInfo("info");

        notificationManager.notify(1, notificationBuilder.build());

    }

    public static void createFbInstanceIdService5() {
//        Todo: finish this method with refresh actions
//        ov: tkn refresh
    }

    public static void startServiceOnMainThread(Context context) {
//        Intent serviceIntent = new Intent(this, ExampleService.class);
        context.startService(new Intent());
    }

    public static void stopServiceOnMainThread(Context context) {
        context.startService(new Intent());

    }

    public static void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("all");

    }

    public static void openImage(Activity activity, int SELECT_PICTURE) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public static void uploadImage(Uri uri, Context context) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child("images/" + System.currentTimeMillis() + (int) (Math.random() * 10000) + "." + getFileExtension(uri, context));
        uri.getPath();

        riversRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
//                        URL:
                        Uri uri1 = downloadUrl.getResult();
                        System.out.println();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    // Get Extension
    public static String getFileExtension(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Return file Extension
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public static Uri getUriFromData(Intent data) {
        return data.getData();
    }

    public static void drive(Context context, String GKEY, GoogleMap mMap, String color, LatLng from, LatLng to, float zoom) {
        DrawRoute drawRoute = new DrawRoute(new DrawRoute.onDrawRoute() {
            @Override
            public void afterDraw(String result) {
            }
        }, context);

        drawRoute.setGmapAndKey(GKEY, mMap)
                .setColorHash(color)
                .setFromLatLong(from.latitude, from.longitude).setToLatLong(to.latitude, to.longitude)

                .setZoomLevel(zoom).run();

    }

    public static void addMyLoc(Context context, Activity activity, final GoogleMap mMap, final LatLng myPos, final int bitmapResourseId) {

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @SuppressLint("MissingPermission")
                @Override
                public void onMyLocationChange(Location location) {
                    initOnLocChange(mMap, myPos, bitmapResourseId, location);
                }
            });


        } else {
            ActivityCompat.requestPermissions(activity, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1000);
        }
    }

    @SuppressLint("MissingPermission")
    public static void initOnLocChange(GoogleMap mMap, LatLng myPos, int bitmapResourseId, Location location) {

        if (myPos == null) {
            if (location != null) {
                myPos = new LatLng(location.getLatitude(), location.getLongitude());
            }

            mMap.addMarker(new MarkerOptions().position(myPos).icon(BitmapDescriptorFactory.fromResource(bitmapResourseId)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 12f));
            mMap.setMyLocationEnabled(false);
        }
    }


    public static void initBarChart(BarChart barChart, ArrayList<BarEntry> list, String label, String desc) {


        BarDataSet barDataSet = new BarDataSet(list, label);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(15f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText(desc);

    }


    public static void initPieChart(PieChart pieChart, ArrayList<PieEntry> list, String label, String desc) {


        PieDataSet pieDataSet = new PieDataSet(list, label);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(15f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(true);
        pieChart.setCenterText(label);
        pieChart.setTransparentCircleRadius(0);
        pieChart.setHoleRadius(0);
        pieChart.getDescription().setText(desc);
        pieChart.animate();
    }


    public static void initLineChart(LineChart lineChart, ArrayList<Entry> list, String label, String desc) {

        LineDataSet lineDataSet = new LineDataSet(list, label);
        lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(15f);

        LineData lineData = new LineData(lineDataSet);

        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(true);
        lineChart.getDescription().setText(desc);
        lineChart.animate();

    }


}
