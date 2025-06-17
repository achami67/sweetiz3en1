package com.example.production.livraison;

import java.util.ArrayList;
import java.util.List;

public class CommandeClient {
    private String nomClient;
    private List<GoutQuantite> gouts;
    private int quantiteTotale;
    private List<String> goutsExclus;
    private boolean inclureAujourdHui = false;

    // Constructeur sans argument nécessaire pour Firebase
    public CommandeClient() {
        this.nomClient = "";
        this.gouts = new ArrayList<>();
        this.goutsExclus = new ArrayList<>();
        this.quantiteTotale = 0;
        this.inclureAujourdHui = false;
    }

    public CommandeClient(String nomClient) {
        this.nomClient = nomClient;
        this.gouts = new ArrayList<>();
        this.goutsExclus = new ArrayList<>();
    }

    // getters & setters (inchangés)
    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public List<GoutQuantite> getGouts() {
        return gouts;
    }

    public void setGouts(List<GoutQuantite> gouts) {
        this.gouts = gouts;
    }

    public int getQuantiteTotale() {
        return quantiteTotale;
    }

    public void setQuantiteTotale(int quantiteTotale) {
        this.quantiteTotale = quantiteTotale;
    }

    public List<String> getGoutsExclus() {
        return goutsExclus;
    }

    public void setGoutsExclus(List<String> goutsExclus) {
        this.goutsExclus = goutsExclus;
    }

    public boolean isInclureAujourdHui() {
        return inclureAujourdHui;
    }

    public void setInclureAujourdHui(boolean inclureAujourdHui) {
        this.inclureAujourdHui = inclureAujourdHui;
    }
}
