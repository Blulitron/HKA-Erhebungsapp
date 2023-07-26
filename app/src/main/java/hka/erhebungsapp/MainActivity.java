package hka.erhebungsapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import hka.erhebungsapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    LocationManager locationManager;
    Criteria criteria;
    public String bestProvider;
    Location location = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_start, R.id.nav_export, R.id.nav_about)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if (Build.VERSION.SDK_INT >= 30) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent((Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION));
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                Toast.makeText(this, "Bitte erlaube der App 'Zugriff zum Verwalten aller Dateien zulassen'.", Toast.LENGTH_LONG).show();
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        createRootDir();
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            location = locationManager.getLastKnownLocation(bestProvider);
            if (location == null) {
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            Toast.makeText(getApplicationContext(), "Einstellungen sind in dieser Version noch nicht verfügbar...", Toast.LENGTH_LONG).show();
        } else if (id == R.id.action_wipe) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alle Daten löschen");
            builder.setMessage("Möchtest du wirklich alle Daten löschen? Diese Aktion ist nicht wiederrufbar!");
            builder.setPositiveButton("Ja", (dialogInterface, i) -> {
                wipeData();
                Toast.makeText(this, "Alle Daten wurden gelöscht.", Toast.LENGTH_SHORT).show();
                finish();
            });
            builder.setNegativeButton("Nein", (dialogInterface, i) -> {});
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void wipeData(){
        Context context = getApplicationContext();
        context.deleteDatabase("Datenbank.db");
        deleteRecursive(new File(Environment.getExternalStorageDirectory() + "/Documents/HKA-Erhebungsapp/Data"));
        createRootDir();
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public void createRootDir () {
        File rootDir = new File(Environment.getExternalStorageDirectory() + "/Documents/HKA-Erhebungsapp");
        if (!rootDir.exists())
            rootDir.mkdirs();

        File dataDir = new File(Environment.getExternalStorageDirectory() + "/Documents/HKA-Erhebungsapp/Data");
        if (!dataDir.exists())
            dataDir.mkdir();

        File routeDir = new File(Environment.getExternalStorageDirectory() + "/Documents/HKA-Erhebungsapp/Data/Fahrtbeginn");
        if (!routeDir.exists())
            routeDir.mkdir();

        File photoDir = new File(Environment.getExternalStorageDirectory() + "/Documents/HKA-Erhebungsapp/Data/Ereignisse");
        if (!photoDir.exists())
            photoDir.mkdir();

        File evalDir = new File(Environment.getExternalStorageDirectory() + "/Documents/HKA-Erhebungsapp/Data/Fahrtende");
        if (!evalDir.exists())
            evalDir.mkdir();

        File tableDir = new File(Environment.getExternalStorageDirectory() + "/Documents/HKA-Erhebungsapp/Data/Tabellen");
        if (!tableDir.exists()){
            tableDir.mkdir();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location = location;
    }
}