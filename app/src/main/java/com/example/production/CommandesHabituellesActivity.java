package com.example.production.livraison;

import android.os.Bundle;

public class CommandesHabituellesActivity extends CommandesNormalesActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Titre spécifique
        setTitle("Commandes habituelles");

        // Ne rien masquer : on conserve les commandes ponctuelles visibles
        // Car tu veux les VOIR sur cette page aussi
    }
}
