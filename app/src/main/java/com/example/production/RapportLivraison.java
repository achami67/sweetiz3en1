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

    // Getters & Setters...
}
