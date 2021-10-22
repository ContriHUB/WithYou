package com.example.withyou;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.withyou.apis.RetrofitAccessObject;
import com.example.withyou.models.ImgBbResponse;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.common.util.concurrent.ListenableFuture;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.CALL_PHONE;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    LottieAnimationView hospital, police, police_call, contact, defence, knife, camera;
    TextView set_c, set_t;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CALL = "call";
    public static final String TEXT = "text";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_CODE_CAMERA = 2;
    private String currentPhotoPath;
    private PreviewView previewView;
    private String currentUrl = "";
    public static final String API_KEY = "2cdbe1a7a05c81d37a0c1117e7285468";
    private ImageCapture imageCapture;
    private String locationStatus = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        previewView = findViewById(R.id.viewFinder);
        set_c = findViewById(R.id.set_c);
        set_t = findViewById(R.id.set_t);
// fetching Details provided in settingsActivity by user
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        if (!sharedPreferences.getString(CALL, "").equals(""))
            set_c.setText(getString(R.string.call_no) + sharedPreferences.getString(CALL, ""));
        else
            set_c.setText(R.string.call_no_not_set);

        if (!sharedPreferences.getString(TEXT, "").equals(""))
            set_t.setText(getString(R.string.text_no) + sharedPreferences.getString(TEXT, ""));
        else
            set_t.setText(R.string.text_no_not_set);

        //Shows battery percentage as a Toast as you enter this activity
        getBattery_percentage();
        hospital = findViewById(R.id.hospital);
        police = findViewById(R.id.police);
        police_call = findViewById(R.id.police_call);
        contact = findViewById(R.id.contact);
        defence = findViewById(R.id.defence);
        knife = findViewById(R.id.knife);
        camera = findViewById(R.id.camera);
        startCamera();
        //Here we start a activity to go in our calender set event
        knife.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", cal.getTimeInMillis());
                intent.putExtra("allDay", true);
                intent.putExtra("rrule", "FREQ=DAILY");
                intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
                intent.putExtra("title", "Take Weapon Stash");
                startActivity(intent);
            }
        });

        // this listener starts activity to a specific youtube video
        defence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "https://www.youtube.com/watch?v=T7aNSRoDCmg";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

        // Getting location of nearby hospitals through google navigation
        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "google.navigation:" + "q=hospitals+near+me";
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
            }
        });

        //Getting location of nearby Police stations through google navigation
        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "google.navigation:" + "q=police+station+near+me";
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
            }
        });

        police_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                String callNumber = sharedPreferences.getString(CALL, "");
                String textNumber = sharedPreferences.getString(TEXT, "");
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callNumber));

                // Checking and then Requesting calling permission from user if app doesn't have it
                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{CALL_PHONE}, 1);
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                        startActivity(intent);
                    else
                        Toast.makeText(getBaseContext(), R.string.please_give_call_permission, Toast.LENGTH_SHORT).show();
                }

            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Camera permission check
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CODE_CAMERA
                    );
                    // if already have permission then getting location by calling a method
                } else {
                    captureImage();
                }
                //Location permission check , requesting if app doesn't have it
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                    // if already have permission then getting location by calling a method
                } else {
                    getCurrentLocation();
                }
                // Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:1234456;234567"));
                // smsIntent.putExtra("sms_body", etmessage.getText().toString());
                // startActivity(smsIntent);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking permissions to use camera and write storage , requesting if app doesn't have those
                if (ContextCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            101);
                }

                if (ContextCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    //calling method to take a picture if have permissions
                    dispatchTakePictureIntent();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
    }

    private void captureImage() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp;
            File photo = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    getFilesDir()
            );
            currentPhotoPath = photo.getAbsolutePath();
            Log.i("WithYou", currentPhotoPath);
            imageCapture.takePicture(new ImageCapture.OutputFileOptions.Builder(photo).build(),
                    ContextCompat.getMainExecutor(this),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            uploadImage();
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Log.i("WithYou", exception.getImageCaptureError() + " " + exception.getMessage());
                            exception.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            Log.i("WithYou", e.getMessage());
            e.printStackTrace();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                ProcessCameraProvider cameraProvider = null;
                try {
                    cameraProvider = cameraProviderFuture.get();
                    startCameraX(cameraProvider);
                } catch (Exception e) {
                    Log.i("WithYou", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = storageDir.getAbsolutePath() + imageFileName;
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                galleryAddPic();
            }
        }
    }

    // adding clicked pictures in gallery
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void uploadImage() {
        File f = new File(currentPhotoPath);
        Log.i("WithYou", currentPhotoPath);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", f.getName(), RequestBody.create(MediaType.parse("image/*"), f));
        RetrofitAccessObject.getRetrofitAccessObject()
                .uploadImage(filePart, API_KEY, 604800)
                .enqueue(new Callback<ImgBbResponse>() {
                    @Override
                    public void onResponse(Call<ImgBbResponse> call, Response<ImgBbResponse> response) {
                        if (response.code() == 200 && response.body() != null) {
                            currentUrl = currentUrl + response.body().getData().getUrl() + "\n";
                            Log.i("WithYou", currentUrl);
                            sendPanicSMS();
                        } else {
                            Log.i("WithYou", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ImgBbResponse> call, Throwable t) {
                        Log.i("WithYou", t.getMessage());
                    }
                });
    }

    // checking after requesting location permission if user has granted or denied it
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // if granted then only, getting location
                getCurrentLocation();
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_CAMERA && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // if granted then only, getting location
                captureImage();
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // gets user's location
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            locationStatus = latitude + "---" + longitude;
                            Toast.makeText(getApplicationContext(), "Your Lat---long : " + locationStatus, Toast.LENGTH_SHORT).show();
                            sendPanicSMS();
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void sendPanicSMS() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String callNumber = sharedPreferences.getString(CALL, "");
        String textNumber = sharedPreferences.getString(TEXT, "");

        String message = locationStatus + "\n" + currentUrl;
        Log.i("WithYou", message);
        SmsManager mySmsManager = SmsManager.getDefault();
// If user has provided a number then sending sms else giving toast to ask for number
        if (!textNumber.equals(""))
            mySmsManager.sendTextMessage(textNumber, null, message, null, null);
        else
            Toast.makeText(getBaseContext(), R.string.please_set, Toast.LENGTH_SHORT).show();


//        Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:1234456;234567;9453998530"));
//        smsIntent.putExtra("sms_body", locationStatus);
//        startActivity(smsIntent);
    }

    // Sending sms
    public void sendSMS(String locationStatus) {

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String callNumber = sharedPreferences.getString(CALL, "");
        String textNumber = sharedPreferences.getString(TEXT, "");
        SmsManager mySmsManager = SmsManager.getDefault();
        // If user has provided a number then sending sms else giving toast to ask for number
        if (!textNumber.equals(""))
            mySmsManager.sendTextMessage(textNumber, null, locationStatus, null, null);
        else
            Toast.makeText(getBaseContext(), R.string.please_set, Toast.LENGTH_SHORT).show();


//        Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:1234456;234567;9453998530"));
//        smsIntent.putExtra("sms_body", locationStatus);
//        startActivity(smsIntent);

    }

    // getting user battery_percentage details using The BatteryManager class
    void getBattery_percentage() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        float p = batteryPct * 100;

        Toast.makeText(getApplicationContext(), String.valueOf(p), Toast.LENGTH_SHORT).show();
    }
}
