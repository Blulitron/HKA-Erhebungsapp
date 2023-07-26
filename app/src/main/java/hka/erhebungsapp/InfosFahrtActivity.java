package hka.erhebungsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import hka.erhebungsapp.ui.start.StartFragment;

public class InfosFahrtActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_fahrt);
        Context context = InfosFahrtActivity.this;
        Intent intent = getIntent();
        int fahrtID = intent.getIntExtra("FahrtID", 0);

        Button btnEreignis = findViewById(R.id.button_ereignis);
        btnEreignis.setOnClickListener(view -> {
            Intent nextIntent = new Intent (context, EreignisKameraActivity.class);
            nextIntent.putExtra("FahrtID", fahrtID);
            startActivityForResult(nextIntent, 1);
            Intent returnIntent = new Intent(getApplicationContext(), StartFragment.class);
            setResult(RESULT_OK,returnIntent);
            finish();
        });

        Button btnEvaluation = findViewById(R.id.button_evaluation);
        btnEvaluation.setOnClickListener(view -> {
            Intent nextIntent = new Intent (context, EvaluationKameraActivity.class);
            nextIntent.putExtra("FahrtID", fahrtID);
            startActivityForResult(nextIntent, 1);
            Intent returnIntent = new Intent(getApplicationContext(), StartFragment.class);
            setResult(RESULT_OK,returnIntent);
            finish();
        });

        Button btnLoeschen = findViewById(R.id.button_loeschen);
        btnLoeschen.setOnClickListener(view -> {
            confirmDeleteDialog(fahrtID);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            finish();
        }
    }

    public void confirmDeleteDialog(int fahrtID){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fahrt löschen");
        builder.setMessage("Möchtest du diese Fahrt wirklich löschen? Diese Aktion ist nicht wiederrufbar!");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            DatabaseHelper dbh = new DatabaseHelper(InfosFahrtActivity.this);
            dbh.deleteFahrt(fahrtID);
            Toast.makeText(this, "Die Fahrt wurde gelöscht.", Toast.LENGTH_SHORT).show();
            finish();
        });
        builder.setNegativeButton("Nein", (dialogInterface, i) -> {});
        builder.create().show();
    }
}