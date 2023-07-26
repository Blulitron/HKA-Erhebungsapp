package hka.erhebungsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EreignisBeschreibungActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ereignis_beschreibung);

        Intent intent = getIntent();
        int fahrtID;
        String standort, datum_zeit, fotoID;
        fahrtID = intent.getIntExtra("intentFahrtID", 0);
        standort = intent.getStringExtra("intentStandort");
        datum_zeit = intent.getStringExtra("intentDatumZeit");
        fotoID = intent.getStringExtra("intentFotoID");

        EditText etBeschreibung = findViewById(R.id.eTEreignisBeschreibung);
        Button btnSpeichern = findViewById(R.id.btnEreignisSpeichern);
        btnSpeichern.setOnClickListener(view -> {
            String textBeschreibung = etBeschreibung.getText().toString();
            DatabaseHelper dbh = new DatabaseHelper(EreignisBeschreibungActivity.this);
            dbh.insertDataEreignisse(fahrtID, standort, datum_zeit, fotoID, textBeschreibung);
            Intent returnIntent = new Intent(EreignisBeschreibungActivity.this, EreignisKameraActivity.class);
            setResult(RESULT_OK,returnIntent);
            finish();
        });
    }
}