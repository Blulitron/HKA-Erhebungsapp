package hka.erhebungsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import hka.erhebungsapp.ui.start.StartFragment;

public class NeueRouteActivity extends AppCompatActivity {
    EditText eT_Start;
    EditText eT_Ziel;
    RadioGroup rG_Verkehrsmittel;
    RadioButton rB_Verkehrsmittel;
    Switch switch_Gepaeck;
    Button buttonWeiter;
    EditText eT_Sonstige;
    boolean isChecked;
    boolean eT_Start_Ready, eT_Ziel_Ready, radioButtonChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neueroute);
        Intent intent = getIntent();

        eT_Start = findViewById(R.id.editTextStart);
        eT_Ziel = findViewById(R.id.editTextZiel);
        rG_Verkehrsmittel = findViewById(R.id.radioGroupVerkehrsmittel);
        switch_Gepaeck = findViewById(R.id.switchGepaeck);
        buttonWeiter = findViewById(R.id.buttonWeiter_NR);
        eT_Sonstige = findViewById(R.id.editTextSonstige);

        buttonWeiter.setEnabled(false);
        eT_Sonstige.setVisibility(View.INVISIBLE);

        buttonWeiter.setOnClickListener(view -> {
            String start = eT_Start.getText().toString();
            String ziel = eT_Ziel.getText().toString();
            String verkehrsmittel = rB_Verkehrsmittel.getText().toString();
            if (verkehrsmittel.equals("Sonstige")){
                verkehrsmittel = eT_Sonstige.getText().toString();
            }
            startKameraActivity(start,ziel,verkehrsmittel,isChecked);
        });
        switch_Gepaeck.setOnCheckedChangeListener((compoundButton, b) -> isChecked = b);
        eT_Start.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (eT_Start.getText().toString().equals("")){
                    eT_Start_Ready = false;
                } else {
                    eT_Start_Ready = true;
                }
                checkEnableNextButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        eT_Ziel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (eT_Ziel.getText().toString().equals("")){
                    eT_Ziel_Ready = false;
                } else {
                    eT_Ziel_Ready = true;
                }
                checkEnableNextButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        eT_Sonstige.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (eT_Sonstige.getText().toString().equals("")){
                    radioButtonChecked = false;
                } else {
                    radioButtonChecked = true;
                }
                checkEnableNextButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
    public void onRadioButtonClicked(View view){
        int radioID = rG_Verkehrsmittel.getCheckedRadioButtonId();
        rB_Verkehrsmittel = findViewById(radioID);
        if (rB_Verkehrsmittel.getText().toString().equals("Sonstige")){
            eT_Sonstige.setVisibility(View.VISIBLE);
            if (eT_Sonstige.getText().toString().equals("")){
                radioButtonChecked = false;
            } else {
                radioButtonChecked = true;
            }
        } else {
            eT_Sonstige.setVisibility(View.INVISIBLE);
            radioButtonChecked = true;
        }
        checkEnableNextButton();
    }

    public boolean checkReadyForNext(){
        if (eT_Start_Ready  && eT_Ziel_Ready && radioButtonChecked){
            return true;
        } else {
            return false;
        }
    }

    public void checkEnableNextButton(){
        if (checkReadyForNext()){
            buttonWeiter.setEnabled(true);
        } else {
            buttonWeiter.setEnabled(false);
        }
    }
    public void startKameraActivity(String start, String ziel, String verkehrsmittel, boolean isChecked){
        Intent intent = new Intent(this, KameraActivity.class);
        intent.putExtra("intentStart", start);
        intent.putExtra("intentZiel", ziel);
        intent.putExtra("intentVerkehrsmittel", verkehrsmittel);
        intent.putExtra("intentGepaeck", isChecked);
        startActivityForResult(intent, 1);
        Intent returnIntent = new Intent(getApplicationContext(), StartFragment.class);
        setResult(RESULT_OK,returnIntent);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}