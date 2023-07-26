package hka.erhebungsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class EvaluationActivity extends AppCompatActivity {
    int fahrtID;
    String standort, datum_zeit, fotoID;
    RadioGroup rG_Frage1, rG_Frage2, rG_Frage3, rG_Frage4, rG_Frage5, rG_Frage6;
    RadioButton rB_Frage1, rB_Frage2, rB_Frage3, rB_Frage4, rB_Frage5, rB_Frage6;
    Button buttonSpeichern;
    boolean checkedFrage1, checkedFrage2, checkedFrage3, checkedFrage4, checkedFrage5, checkedFrage6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        Intent intent = getIntent();
        fahrtID = intent.getIntExtra("intentFahrtID", 0);
        standort = intent.getStringExtra("intentStandort");
        datum_zeit = intent.getStringExtra("intentDatumZeit");
        fotoID = intent.getStringExtra("intentFotoID");
        buttonSpeichern = findViewById(R.id.button_saveEvaluation);
        rG_Frage1 = findViewById(R.id.rG_Frage1);
        rG_Frage2 = findViewById(R.id.rG_Frage2);
        rG_Frage3 = findViewById(R.id.rG_Frage3);
        rG_Frage4 = findViewById(R.id.rG_Frage4);
        rG_Frage5 = findViewById(R.id.rG_Frage5);
        rG_Frage6 = findViewById(R.id.rG_Frage6);

        buttonSpeichern.setEnabled(false);
        buttonSpeichern.setOnClickListener(view -> {
            DatabaseHelper dbh = new DatabaseHelper(EvaluationActivity.this);
            String frage1, frage2, frage3, frage4, frage5, frage6;
            frage1 = rB_Frage1.getText().toString();
            frage2 = rB_Frage2.getText().toString();
            frage3 = rB_Frage3.getText().toString();
            frage4 = rB_Frage4.getText().toString();
            frage5 = rB_Frage5.getText().toString();
            frage6 = rB_Frage6.getText().toString();
            dbh.insertDataEvaluation(fahrtID, standort, datum_zeit, fotoID, frage1, frage2, frage3, frage4, frage5, frage6);
            dbh.updateFahrtAbgeschlossen(fahrtID, true);
            Intent returnIntent = new Intent(EvaluationActivity.this, EvaluationKameraActivity.class);
            setResult(RESULT_OK,returnIntent);
            finish();
        });
    }
    public void onRadioButtonClickedF1(View view){
        int radioID = rG_Frage1.getCheckedRadioButtonId();
        rB_Frage1 = findViewById(radioID);
        checkedFrage1 = true;
        checkEnableSaveButton();
    }
    public void onRadioButtonClickedF2(View view) {
        int radioID = rG_Frage2.getCheckedRadioButtonId();
        rB_Frage2 = findViewById(radioID);
        checkedFrage2 = true;
        checkEnableSaveButton();
    }

    public void onRadioButtonClickedF3(View view) {
        int radioID = rG_Frage3.getCheckedRadioButtonId();
        rB_Frage3 = findViewById(radioID);
        checkedFrage3 = true;
        checkEnableSaveButton();
    }

    public void onRadioButtonClickedF4(View view) {
        int radioID = rG_Frage4.getCheckedRadioButtonId();
        rB_Frage4 = findViewById(radioID);
        checkedFrage4 = true;
        checkEnableSaveButton();
    }

    public void onRadioButtonClickedF5(View view) {
        int radioID = rG_Frage5.getCheckedRadioButtonId();
        rB_Frage5 = findViewById(radioID);
        checkedFrage5 = true;
        checkEnableSaveButton();
    }

    public void onRadioButtonClickedF6(View view) {
        int radioID = rG_Frage6.getCheckedRadioButtonId();
        rB_Frage6 = findViewById(radioID);
        checkedFrage6 = true;
        checkEnableSaveButton();
    }

    public boolean checkReadyForNext(){
        if (checkedFrage1 && checkedFrage2 && checkedFrage3 && checkedFrage4 && checkedFrage5 && checkedFrage6){
            return true;
        } else {
            return false;
        }
    }

    public void checkEnableSaveButton(){
        if (checkReadyForNext()){
            buttonSpeichern.setEnabled(true);
        } else {
            buttonSpeichern.setEnabled(false);
        }
    }
}