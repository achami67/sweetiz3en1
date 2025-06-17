package com.example.production.comptabilite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.production.R;

public class ComptabiliteAccueilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comptabilite_accueil);

        Button buttonSaisirRapport = findViewById(R.id.buttonSaisirRapport);
        Button buttonResumeJour = findViewById(R.id.buttonResumeJour);

        buttonSaisirRapport.setOnClickListener(v ->
                startActivity(new Intent(this, SaisieRapportActivity.class))
        );

        buttonResumeJour.setOnClickListener(v ->
                startActivity(new Intent(this, ResumeComptableActivity.class))
        );
    }
}
