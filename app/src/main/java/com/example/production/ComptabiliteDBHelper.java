public class ComptabiliteDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "comptabilite.db";
    private static final int DB_VERSION = 1;

    public ComptabiliteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE rapports (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date TEXT," +
                "client TEXT," +
                "quantite INTEGER," +
                "est_paye INTEGER," +
                "montant REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS rapports");
        onCreate(db);
    }

    // Méthode pour insérer un rapport
    public void ajouterRapport(RapportLivraison rapport) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", rapport.getDate());
        values.put("client", rapport.getClient());
        values.put("quantite", rapport.getQuantite());
        values.put("est_paye", rapport.isEstPaye() ? 1 : 0);
        values.put("montant", rapport.getMontant());
        db.insert("rapports", null, values);
        db.close();
    }
}
