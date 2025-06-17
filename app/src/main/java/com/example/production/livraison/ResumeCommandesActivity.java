package com.example.production.livraison;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.production.R;
import com.example.production.SuiviProductionActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.*;

public class ResumeCommandesActivity extends AppCompatActivity {

    private LinearLayout tableauCommandesHabituelles;
    private LinearLayout tableauCommandesPonctuelles;
    private LinearLayout tableauCommandesSpeciales;
    private LinearLayout tableauTotalParGout;
    private List<PourcentageGout> basePourcentages;

    private List<CommandeClient> habituelles = new ArrayList<>();
    private List<CommandeClient> ponctuelles = new ArrayList<>();
    private List<CommandeSpecialeClient> speciales = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_commandes);

        tableauCommandesHabituelles = findViewById(R.id.tableauCommandesHabituelles);
        tableauCommandesPonctuelles = findViewById(R.id.tableauCommandesPonctuelles);
        tableauCommandesSpeciales = findViewById(R.id.tableauCommandesSpeciales);
        tableauTotalParGout = findViewById(R.id.tableauTotalParGout);
        basePourcentages = chargerBasePourcentages();

        String mode = getIntent().getStringExtra("mode");
        Log.d("MODE", "Mode reçu : " + mode);

        if ("lecture".equalsIgnoreCase(mode)) {
            chargerDonneesDepuisFirebase();
        } else {
            chargerEtEnvoyerDonnees();
        }

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

    private void chargerDonneesDepuisFirebase() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child("commandes_habituelles").get().addOnSuccessListener(snapshotHabituelles -> {
            GenericTypeIndicator<Map<String, Map<String, Long>>> t1 = new GenericTypeIndicator<>() {};
            Map<String, Map<String, Long>> rawHabit = snapshotHabituelles.getValue(t1);
            Map<String, Map<String, Integer>> habit = convertNestedMap(rawHabit);

            rootRef.child("commandes_ponctuelles").get().addOnSuccessListener(snapshotPonctuelles -> {
                GenericTypeIndicator<Map<String, Map<String, Long>>> t2 = new GenericTypeIndicator<>() {};
                Map<String, Map<String, Long>> rawPonc = snapshotPonctuelles.getValue(t2);
                Map<String, Map<String, Integer>> ponc = convertNestedMap(rawPonc);

                rootRef.child("commandes_speciales").get().addOnSuccessListener(snapshotSpeciales -> {
                    GenericTypeIndicator<List<CommandeSpecialeClient>> t3 = new GenericTypeIndicator<>() {};
                    speciales = snapshotSpeciales.getValue(t3);
                    if (speciales == null) speciales = new ArrayList<>();

                    rootRef.child("total_par_gout").get().addOnSuccessListener(snapshotTotal -> {
                        Map<String, Long> totalLong = new HashMap<>();
                        if (snapshotTotal.exists()) {
                            GenericTypeIndicator<Map<String, Long>> t = new GenericTypeIndicator<>() {};
                            totalLong = snapshotTotal.getValue(t);
                        }
                        Map<String, Integer> total = convertLongMapToIntMap(totalLong);

                        afficherCommandesMap(habit, tableauCommandesHabituelles);
                        afficherCommandesMap(ponc, tableauCommandesPonctuelles);
                        afficherCommandesSpeciales(speciales);
                        afficherTotalGlobal(total);
                    });
                });
            });
        });
    }

    private void chargerEtEnvoyerDonnees() {
        habituelles = chargerCommandes("commandes_habituelles");
        ponctuelles = chargerCommandes("commandes_ponctuelles");
        speciales = chargerCommandesSpeciales("commandes_speciales");

        Map<String, Integer> global = new HashMap<>();
        Map<String, Map<String, Integer>> mapHabit = new HashMap<>();
        Map<String, Map<String, Integer>> mapPonc = new HashMap<>();

        for (CommandeClient c : habituelles) {
            if (!c.isInclureAujourdHui()) continue;
            Map<String, Integer> repartition = calculerRepartitionClient(c);
            mapHabit.put(c.getNomClient(), repartition);
            accumulerDansGlobal(global, repartition);
        }

        for (CommandeClient c : ponctuelles) {
            Map<String, Integer> repartition = calculerRepartitionClient(c);
            mapPonc.put(c.getNomClient(), repartition);
            accumulerDansGlobal(global, repartition);
        }

        for (CommandeSpecialeClient c : speciales) {
            for (SousCommande sc : c.getSousCommandes()) {
                Map<String, Integer> goutFixe = new HashMap<>();
                for (GoutQuantite gq : sc.getGouts()) {
                    goutFixe.put(gq.getNom(), gq.getQuantite());
                }
                accumulerDansGlobal(global, goutFixe);
            }
        }

        afficherCommandesMap(mapHabit, tableauCommandesHabituelles);
        afficherCommandesMap(mapPonc, tableauCommandesPonctuelles);
        afficherCommandesSpeciales(speciales);
        afficherTotalGlobal(global);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("commandes_habituelles").setValue(mapHabit);
        rootRef.child("commandes_ponctuelles").setValue(mapPonc);
        rootRef.child("commandes_speciales").setValue(speciales);
        rootRef.child("total_par_gout").setValue(global);
    }

    private void afficherCommandesMap(Map<String, Map<String, Integer>> data, LinearLayout container) {
        container.removeAllViews();
        for (Map.Entry<String, Map<String, Integer>> entry : data.entrySet()) {
            ajouterBlocCommande(container, entry.getKey(), entry.getValue());
        }
    }

    private List<CommandeClient> chargerCommandes(String key) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String json = prefs.getString(key, null);
        if (json == null || json.equals("null")) return new ArrayList<>();
        return new Gson().fromJson(json, new TypeToken<List<CommandeClient>>() {}.getType());
    }

    private List<CommandeSpecialeClient> chargerCommandesSpeciales(String key) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String json = prefs.getString(key, null);
        if (json == null || json.equals("null")) return new ArrayList<>();
        return new Gson().fromJson(json, new TypeToken<List<CommandeSpecialeClient>>() {}.getType());
    }

    private Map<String, Integer> calculerRepartitionClient(CommandeClient client) {
        int total = client.getQuantiteTotale();
        Map<String, Integer> goutFixe = new HashMap<>();
        Set<String> exclusions = new HashSet<>(client.getGoutsExclus());
        int dejaPris = 0;

        for (GoutQuantite gq : client.getGouts()) {
            goutFixe.put(gq.getNom(), gq.getQuantite());
            dejaPris += gq.getQuantite();
        }

        int reste = Math.max(total - dejaPris, 0);
        return repartirAvecPourcentage(goutFixe, exclusions, reste);
    }

    private List<PourcentageGout> chargerBasePourcentages() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String json = prefs.getString("liste_gouts", null);
        if (json != null && !json.equals("null")) {
            return new Gson().fromJson(json, new TypeToken<List<PourcentageGout>>() {}.getType());
        }
        return new ArrayList<>();
    }

    private void afficherCommandesSpeciales(List<CommandeSpecialeClient> clients) {
        tableauCommandesSpeciales.removeAllViews();
        for (CommandeSpecialeClient client : clients) {
            for (SousCommande sc : client.getSousCommandes()) {
                Map<String, Integer> goutFixe = new HashMap<>();
                for (GoutQuantite gq : sc.getGouts()) {
                    goutFixe.put(gq.getNom(), gq.getQuantite());
                }
                ajouterBlocCommande(tableauCommandesSpeciales, client.getNomClient(), goutFixe);
            }
        }
    }

    private void afficherTotalGlobal(Map<String, Integer> global) {
        tableauTotalParGout.removeAllViews();
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

        if (totalPourcent == 0) return res;

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
        valides.sort((a, b) -> Double.compare(
                reelle.get(b.getNomGout()) - repartition.get(b.getNomGout()),
                reelle.get(a.getNomGout()) - repartition.get(a.getNomGout())
        ));

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

    private Map<String, Integer> convertLongMapToIntMap(Map<String, Long> longMap) {
        Map<String, Integer> intMap = new HashMap<>();
        if (longMap != null) {
            for (Map.Entry<String, Long> entry : longMap.entrySet()) {
                intMap.put(entry.getKey(), entry.getValue().intValue());
            }
        }
        return intMap;
    }

    private Map<String, Map<String, Integer>> convertNestedMap(Map<String, Map<String, Long>> longMap) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        if (longMap != null) {
            for (Map.Entry<String, Map<String, Long>> entry : longMap.entrySet()) {
                Map<String, Integer> inner = new HashMap<>();
                for (Map.Entry<String, Long> innerEntry : entry.getValue().entrySet()) {
                    inner.put(innerEntry.getKey(), innerEntry.getValue().intValue());
                }
                result.put(entry.getKey(), inner);
            }
        }
        return result;
    }
}
