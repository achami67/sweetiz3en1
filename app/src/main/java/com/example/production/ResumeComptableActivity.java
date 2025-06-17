package com.example.production.comptabilite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.production.R;

import java.util.List;

public class ResumeComptableActivity extends AppCompatActivity {

    private ListView listViewRapports;
    private TextView textViewTotaux;
    private ComptabiliteDBHelper dbHelper;
    private List<RapportLivraison> rapports;
    private RapportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_comptable);

        listViewRapports = findViewById(R.id.listViewRapports);
        textViewTotaux = findViewById(R.id.textViewTotaux);
        dbHelper = new ComptabiliteDBHelper(this);

        // Clic sur un rapport pour modification
        listViewRapports.setOnItemClickListener((parent, view, position, id) -> {
            RapportLivraison rapport = rapports.get(position);
            Intent intent = new Intent(ResumeComptableActivity.this, ModifierRapportActivity.class);
            intent.putExtra("rapport_id", rapport.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerResume(); // Recharge à chaque retour sur l'activité
    }

    private void chargerResume() {
        rapports = dbHelper.getRapportsDuJour(); // Ou getTousLesRapports()
        adapter = new RapportAdapter(this, rapports);
        listViewRapports.setAdapter(adapter);

        int totalLivraisons = rapports.size();
        int totalQuantite = 0;
        double totalMontant = 0.0;

        for (RapportLivraison r : rapports) {
            totalQuantite += r.getQuantite();
            totalMontant += r.getMontant();
        }

        textViewTotaux.setText("Livraisons : " + totalLivraisons +
                " | Quantité totale : " + totalQuantite +
                " | Montant total : " + totalMontant + " €");
    }
}
