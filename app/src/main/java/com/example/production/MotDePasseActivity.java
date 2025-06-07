package com.example.production;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.production.livraison.PourcentagesActivity;
import com.example.production.StockActivity;
import com.example.production.comptabilite.ComptabiliteAccueilActivity;

public class MotDePasseActivity extends AppCompatActivity {

    private EditText editTextPassword;
    private Button buttonValider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mot_de_passe);

        // Références aux éléments de l'interface
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonValider = findViewById(R.id.buttonValider);

        // Récupération de la section ciblée (envoyée par MainActivity)
        String section = getIntent().getStringExtra("section");

        buttonValider.setOnClickListener(v -> {
            String mdp = editTextPassword.getText().toString().trim();
            Intent intent = null;

            if (section == null) {
                Toast.makeText(this, "Section non définie.", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (section.toLowerCase()) {
                case "livraison":
                    if (mdp.equals("livraison123")) {
                        intent = new Intent(this, PourcentagesActivity.class);
                    }
                    break;
                case "production":
                    if (mdp.equals("prod456")) {
                        intent = new Intent(this, StockActivity.class);
                    }
                    break;
                case "comptabilite":
                    if (mdp.equals("compta789")) {
                        intent = new Intent(this, ComptabiliteAccueilActivity.class);
                    }
                    break;
            }

            if (intent != null) {
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
