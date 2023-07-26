package hka.erhebungsapp;

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
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import hka.erhebungsapp.ui.start.StartFragment;

public class EreignisKameraActivity extends AppCompatActivity implements LocationListener{
    int fahrtID;
    String standort = null;
    String datum_zeit = null;
    String fotoID;
    Button buttonFotoAufnehmen;
    PreviewView previewView;
    private ImageCapture imageCapture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    LocationManager locationManager;
    String laengengrad = null;
    String breitengrad = null;
    String ort = null;
    Criteria criteria;
    public String bestProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ereignis_kamera);

        Intent intent = getIntent();
        fahrtID = intent.getIntExtra("FahrtID", 0);

        buttonFotoAufnehmen = findViewById(R.id.buttonEreignisFotoAufnehmen);
        previewView = findViewById(R.id.previewView_EreignisKamera);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, getExecutor());

        buttonFotoAufnehmen.setOnClickListener(view -> {
            capturePhoto();
            try {
                if (ActivityCompat.checkSelfPermission(EreignisKameraActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EreignisKameraActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EreignisKameraActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                criteria = new Criteria();
                bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));
                Location location = locationManager.getLastKnownLocation(bestProvider);
                if (location != null) {
                    double lon = location.getLongitude();
                    double lat = location.getLatitude();
                    laengengrad = String.valueOf(lon);
                    breitengrad = String.valueOf(lat);
                    getNearestPlace(location);
                } else {
                    locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent nextIntent = new Intent(getApplicationContext(), EreignisBeschreibungActivity.class);
            nextIntent.putExtra("intentFahrtID", fahrtID);
            nextIntent.putExtra("intentStandort", standort);
            nextIntent.putExtra("intentDatumZeit", datum_zeit);
            nextIntent.putExtra("intentFotoID", fotoID);
            startActivityForResult(nextIntent, 1);
            Intent returnIntent = new Intent(getApplicationContext(), StartFragment.class);
            setResult(RESULT_OK,returnIntent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            finish();
        }
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
    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private void capturePhoto(){
        File photoDir = new File(Environment.getExternalStorageDirectory() + "/Documents/HKA-Erhebungsapp/Data/Ereignisse");

        if (!photoDir.exists())
            photoDir.mkdir();

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());
        String timeStamp = dateFormat.format(date);
        datum_zeit = timeStamp;
        String timeStampFormatted = timeStamp.replace(":", "-");
        timeStamp = timeStampFormatted;
        timeStampFormatted = timeStamp.replace(" ", "_");
        fotoID = timeStampFormatted + ".jpg";
        String photoFilePath = photoDir.getAbsolutePath() + "/" + fotoID;

        File photoFile = new File(photoFilePath);

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(photoFile).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(EreignisKameraActivity.this, "Foto konnte nicht gespeichert werden: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

    public void getNearestPlace(Location location){
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addressList.size() > 0) {
                ort = addressList.get(0).getLocality();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        standort = ort + ", " + laengengrad + ", " + breitengrad;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        locationManager.removeUpdates(this);
        laengengrad = String.valueOf(location.getLongitude());
        breitengrad = String.valueOf(location.getLatitude());
        getNearestPlace(location);
    }
}