package com.example.production.livraison;

public class GoutQuantite {
    private String nom;
    private int quantite;
    private boolean goutFixe;

    // Constructeur sans argument pour Firebase
    public GoutQuantite() {
        this.nom = "";
        this.quantite = 0;
        this.goutFixe = false;
    }

    public GoutQuantite(String nom, int quantite) {
        this.nom = nom;
        this.quantite = quantite;
        this.goutFixe = quantite > 0;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public boolean isGoutFixe() {
        return goutFixe;
    }

    public void setGoutFixe(boolean goutFixe) {
        this.goutFixe = goutFixe;
    }
}
