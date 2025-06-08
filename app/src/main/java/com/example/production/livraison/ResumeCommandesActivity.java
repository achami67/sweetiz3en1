package com.example.production.livraison;

import com.example.production.R;
import com.example.production.SuiviProductionActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.*;

public class ResumeCommandesActivity extends AppCompatActivity {

    private LinearLayout tableauCommandesHabituelles;
    private LinearLayout tableauCommandesPonctuelles;
    private LinearLayout tableauCommandesSpeciales;
    private LinearLayout tableauTotalParGout;
    private List<PourcentageGout> basePourcentages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_commandes);

        tableauCommandesHabituelles = findViewById(R.id.tableauCommandesHabituelles);
        tableauCommandesPonctuelles = findViewById(R.id.tableauCommandesPonctuelles);
        tableauCommandesSpeciales = findViewById(R.id.tableauCommandesSpeciales);
        tableauTotalParGout = findViewById(R.id.tableauTotalParGout);
        basePourcentages = chargerBasePourcentages();

        Map<String, Integer> global = new HashMap<>();
        afficherCommandesHabituelles(global);
        afficherCommandesPonctuelles(global);
        afficherCommandesSpeciales(global);
        afficherTotalGlobal(global);

        // 🔐 Affichage conditionnel du bouton selon la section
        String section = getIntent().getStringExtra("section");
        Button btnSuivi = findViewById(R.id.buttonSuiviProduction);

        if (!"PRODUCTION".equalsIgnoreCase(section)) {
            btnSuivi.setVisibility(Button.GONE);
        } else {
            btnSuivi.setOnClickListener(v -> {
                Intent intent = new Intent(this, SuiviProductionActivity.class);
                startActivity(intent);
            });
        }
    }

    private List<PourcentageGout> chargerBasePourcentages() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String json = prefs.getString("liste_gouts", null);
        if (json != null && !json.equals("null")) {
            return new Gson().fromJson(json, new TypeToken<List<PourcentageGout>>() {}.getType());
        }
        return new ArrayList<>();
    }

    private void afficherCommandesHabituelles(Map<String, Integer> global) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String json = prefs.getString("commandes_habituelles", null);
        if (json == null || json.equals("null")) return;

        List<CommandeClient> commandes = new Gson().fromJson(json, new TypeToken<List<CommandeClient>>() {}.getType());
        if (commandes == null) return;

        for (CommandeClient client : commandes) {
            if (!client.isInclureAujourdHui()) continue;

            int total = client.getQuantiteTotale();
            Map<String, Integer> goutFixe = new HashMap<>();
            Set<String> exclusions = new HashSet<>(client.getGoutsExclus());
            int dejaPris = 0;

            for (GoutQuantite gq : client.getGouts()) {
                goutFixe.put(gq.getNom(), gq.getQuantite());
                dejaPris += gq.getQuantite();
            }

            int reste = Math.max(total - dejaPris, 0);
            Map<String, Integer> resultat = repartirAvecPourcentage(goutFixe, exclusions, reste);
            ajouterBlocCommande(tableauCommandesHabituelles, client.getNomClient(), resultat);
            accumulerDansGlobal(global, resultat);
        }
    }

    private void afficherCommandesPonctuelles(Map<String, Integer> global) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String json = prefs.getString("commandes_ponctuelles", null);
        if (json == null || json.equals("null")) return;

        List<CommandeClient> commandes = new Gson().fromJson(json, new TypeToken<List<CommandeClient>>() {}.getType());
        if (commandes == null) return;

        for (CommandeClient client : commandes) {
            int total = client.getQuantiteTotale();
            Map<String, Integer> goutFixe = new HashMap<>();
            Set<String> exclusions = new HashSet<>(client.getGoutsExclus());
            int dejaPris = 0;

            for (GoutQuantite gq : client.getGouts()) {
                goutFixe.put(gq.getNom(), gq.getQuantite());
                dejaPris += gq.getQuantite();
            }

            int reste = Math.max(total - dejaPris, 0);
            Map<String, Integer> resultat = repartirAvecPourcentage(goutFixe, exclusions, reste);
            ajouterBlocCommande(tableauCommandesPonctuelles, client.getNomClient(), resultat);
            accumulerDansGlobal(global, resultat);
        }
    }

    private void afficherCommandesSpeciales(Map<String, Integer> global) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String json = prefs.getString("commandes_speciales", null);
        if (json == null || json.equals("null")) return;

        List<CommandeSpecialeClient> clients = new Gson().fromJson(json, new TypeToken<List<CommandeSpecialeClient>>() {}.getType());
        if (clients == null) return;

        for (CommandeSpecialeClient client : clients) {
            for (SousCommande sc : client.getSousCommandes()) {
                Map<String, Integer> goutFixe = new HashMap<>();
                for (GoutQuantite gq : sc.getGouts()) {
                    goutFixe.put(gq.getNom(), gq.getQuantite());
                }
                ajouterBlocCommande(tableauCommandesSpeciales, client.getNomClient(), goutFixe);
                accumulerDansGlobal(global, goutFixe);
            }
        }
    }

    private void afficherTotalGlobal(Map<String, Integer> global) {
        TextView titre = new TextView(this);
        titre.setText("Total par goût :");
        titre.setTextSize(18);
        tableauTotalParGout.addView(titre);

        for (Map.Entry<String, Integer> entry : global.entrySet()) {
            TextView ligne = new TextView(this);
            ligne.setText("- " + entry.getKey() + " : " + entry.getValue());
            ligne.setTextSize(16);
            tableauTotalParGout.addView(ligne);
        }
    }

    private void accumulerDansGlobal(Map<String, Integer> global, Map<String, Integer> ajout) {
        for (Map.Entry<String, Integer> entry : ajout.entrySet()) {
            global.put(entry.getKey(), global.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
    }

    private Map<String, Integer> repartirAvecPourcentage(Map<String, Integer> fixes, Set<String> exclus, int reste) {
        Map<String, Integer> res = new LinkedHashMap<>(fixes);
        if (reste <= 0) return res;

        List<PourcentageGout> valides = new ArrayList<>();
        int totalPourcent = 0;
        for (PourcentageGout pg : basePourcentages) {
            String nom = pg.getNomGout();
            if (!fixes.containsKey(nom) && !exclus.contains(nom)) {
                valides.add(pg);
                totalPourcent += pg.getPourcentage();
            }
        }

        Map<String, Integer> repartition = new LinkedHashMap<>();
        Map<String, Double> reelle = new HashMap<>();
        int cumul = 0;

        for (PourcentageGout pg : valides) {
            String nom = pg.getNomGout();
            double fraction = ((double) pg.getPourcentage() / totalPourcent) * reste;
            int entier = (int) Math.floor(fraction);
            repartition.put(nom, entier);
            reelle.put(nom, fraction);
            cumul += entier;
        }

        int resteARepartir = reste - cumul;
        valides.sort((a, b) -> {
            double diffB = reelle.get(b.getNomGout()) - repartition.get(b.getNomGout());
            double diffA = reelle.get(a.getNomGout()) - repartition.get(a.getNomGout());
            return Double.compare(diffB, diffA);
        });

        for (int i = 0; i < resteARepartir; i++) {
            String nom = valides.get(i % valides.size()).getNomGout();
            repartition.put(nom, repartition.get(nom) + 1);
        }

        res.putAll(repartition);
        return res;
    }

    private void ajouterBlocCommande(LinearLayout parent, String client, Map<String, Integer> gouts) {
        TextView titre = new TextView(this);
        titre.setText("Client : " + client);
        titre.setTextSize(18);
        titre.setPadding(0, 12, 0, 4);
        parent.addView(titre);

        for (Map.Entry<String, Integer> entry : gouts.entrySet()) {
            TextView ligne = new TextView(this);
            ligne.setText("- " + entry.getKey() + " : " + entry.getValue());
            ligne.setTextSize(16);
            parent.addView(ligne);
        }
    }
}
