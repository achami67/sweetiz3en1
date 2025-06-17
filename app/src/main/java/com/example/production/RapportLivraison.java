package com.example.production.comptabilite;

public class RapportLivraison {
    private int id;
    private String date;           // Format : yyyy-MM-dd
    private String client;
    private int quantite;
    private boolean estPaye;
    private double montant;

    public RapportLivraison(int id, String date, String client, int quantite, boolean estPaye, double montant) {
        this.id = id;
        this.date = date;
        this.client = client;
        this.quantite = quantite;
        this.estPaye = estPaye;
        this.montant = montant;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getClient() {
        return client;
    }

    public int getQuantite() {
        return quantite;
    }

    public boolean isEstPaye() {
        return estPaye;
    }

    public double getMontant() {
        return montant;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void setEstPaye(boolean estPaye) {
        this.estPaye = estPaye;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }
}
