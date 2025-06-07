package com.example.production;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class ResumeJourneeActivity extends AppCompatActivity {

    private LinearLayout layoutResultats;
    private EditText editTextSeuil;
    private TextView textViewAlerte;

    // Exemples statiques
    private final Map<String, Integer> stockPrevu = Map.of(
            "Fraise", 130,
            "Chocolat", 100,
            "Vanille", 70
    );

    private final Map<String, Integer> stockReel = Map.of(
            "Fraise", 120,
            "Chocolat", 105,
            "Vanille", 75
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_journee);

        layoutResultats = findViewById(R.id.layoutResultats);
        editTextSeuil = findViewById(R.id.editTextSeuil);
        textViewAlerte = findViewById(R.id.textViewAlerte);

        Button buttonValider = findViewById(R.id.buttonValider);
        Button buttonModifier = findViewById(R.id.buttonModifier);

        afficherResultats(10); // seuil par défaut

        editTextSeuil.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int seuil = 10;
                try {
                    seuil = Integer.parseInt(s.toString());
                } catch (NumberFormatException ignored) {}
                afficherResultats(seuil);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });

        buttonValider.setOnClickListener(v -> Toast.makeText(this, "✅ Données validées", Toast.LENGTH_SHORT).show());
        buttonModifier.setOnClickListener(v -> Toast.makeText(this, "🔁 Modifier les données", Toast.LENGTH_SHORT).show());
    }

    private void afficherResultats(int seuil) {
        layoutResultats.removeAllViews();
        boolean alerte = false;
        StringBuilder ecartsDetectes = new StringBuilder();

        int totalStock = getTotal(stockReel);

        for (String gout : stockPrevu.keySet()) {
            int prevu = stockPrevu.get(gout);
            int reel = stockReel.getOrDefault(gout, 0);
            int ecart = reel - prevu;
            int pourcentage = (int) ((reel / (float) totalStock) * 100);

            LinearLayout ligne = new LinearLayout(this);
            ligne.setOrientation(LinearLayout.HORIZONTAL);
            ligne.setPadding(0, 8, 0, 8);
            ligne.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView label = new TextView(this);
            label.setText(gout + " : " + pourcentage + "%");
            label.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            ligne.addView(label);

            ProgressBar progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            progress.setProgress(pourcentage);
            progress.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
            ligne.addView(progress);

            TextView quantite = new TextView(this);
            quantite.setText(reel + " unités");
            quantite.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            quantite.setGravity(View.TEXT_ALIGNMENT_CENTER);
            ligne.addView(quantite);

            layoutResultats.addView(ligne);

            if (Math.abs(ecart) > seuil) {
                alerte = true;
                ecartsDetectes.append("- ").append(gout).append(" : écart de ").append(ecart).append(" unités\n");
            }
        }

        if (alerte) {
            textViewAlerte.setVisibility(View.VISIBLE);
            textViewAlerte.setText("⚠️ Écart important constaté :\n" + ecartsDetectes.toString().trim());
        } else {
            textViewAlerte.setVisibility(View.VISIBLE);
            textViewAlerte.setText("✅ Aucun écart significatif détecté.");
        }
    }

    private int getTotal(Map<String, Integer> map) {
        int total = 0;
        for (int val : map.values()) total += val;
        return total;
    }
}
