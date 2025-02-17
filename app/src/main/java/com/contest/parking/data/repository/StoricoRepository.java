package com.contest.parking.data.repository;

import com.contest.parking.data.model.Storico;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StoricoRepository {

    private final FirebaseFirestore db = FirestoreDataSource.getFirestore();
    private final CollectionReference storicoCollection = db.collection("Storico");

    public Task<Void> addStorico(Storico storico) {
        String docId = storicoCollection.document().getId();
        storico.setId(docId);
        return storicoCollection.document(docId).set(storico);
    }

    public CollectionReference getAllStorici() {
        return storicoCollection;
    }

    public Task<QuerySnapshot> getStoricoAperto(String postoId, String utenteId) {
        return storicoCollection.whereEqualTo("postoAutoId", postoId)
                .whereEqualTo("utenteId", utenteId)
                .whereEqualTo("dataFine", 0)
                .get();
    }

    public Task<Void> updateDataFine(String docId, long fine) {
        return storicoCollection.document(docId).update("dataFine", fine);
    }

    public Task<QuerySnapshot> getStoricoApertoByUtente(String utenteId) {
        return storicoCollection.whereEqualTo("utenteId", utenteId)
                .whereEqualTo("dataFine", 0)
                .get();
    }

    public Task<QuerySnapshot> getStoricoForSpot(String spotId) {
        return storicoCollection.whereEqualTo("postoAutoId", spotId).get();
    }

    /**
     * Callback per restituire una lista di Storico
     */
    public interface OnStoricoLoadedListener {
        void onStoricoLoaded(List<Storico> listaStorico);
        void onError(Exception e);
    }

    /**
     * Esempio di metodo che restituisce tutti i record di Storico
     * con "postoAutoId" = spotId, e li converte in List<Storico>
     */
    public void getStoricoBySpotId(String spotId, OnStoricoLoadedListener callback) {
        getStoricoForSpot(spotId)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Storico> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        if (doc.exists()) {
                            Storico storico = doc.toObject(Storico.class);
                            lista.add(storico);
                        }
                    }
                    callback.onStoricoLoaded(lista);
                })
                .addOnFailureListener(e -> callback.onError(e));
    }

    public void getStoricoValidoByUtente(String uid, OnStoricoLoadedListener cb) {
        long currentTime = System.currentTimeMillis();
        storicoCollection
                .whereEqualTo("utenteId", uid)
                //.whereEqualTo("pagato", false)
                .whereGreaterThan("dataFine", currentTime)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Storico> risultato = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        if (doc.exists()) {
                            Storico s = doc.toObject(Storico.class);
                            s.setId(doc.getId());
                            risultato.add(s);
                        }
                    }
                    cb.onStoricoLoaded(risultato);
                })
                .addOnFailureListener(e -> {
                    cb.onError(e);
                });
    }

    public Task<Void> updatePagato(String docId, boolean pagato) {
        return storicoCollection.document(docId).update("pagato", pagato);
    }

    private long getTodayStartInMillis() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private long getTodayEndInMillis() {
        return getTodayStartInMillis() + 86400000L - 1;
    }

    public Task<Boolean> isOggiOccupato(String postoAutoId) {
        long dayStart = getTodayStartInMillis();
        long dayEnd = getTodayEndInMillis();

        // 1) Esegui la query su Firestore
        Task<QuerySnapshot> queryTask = storicoCollection
                .whereEqualTo("postoAutoId", postoAutoId)
                .whereLessThanOrEqualTo("dataInizio", dayEnd)
                .whereGreaterThanOrEqualTo("dataFine", dayStart)
                .get();

        // 2) Usa "continueWith(...)": al termine della query,
        //    restituisci un Task<Boolean> che indica se la snapshot è vuota o no.
        return queryTask.continueWith(task -> {
            // Se la query ha fallito, solleva l'eccezione
            if (!task.isSuccessful()) {
                Exception e = task.getException() != null ? task.getException() :
                        new Exception("Query Storico fallita");
                throw e;
            }
            // Altrimenti, se la query è riuscita
            QuerySnapshot snap = task.getResult();
            // isEmpty() = nessun documento -> false (NON occupato)
            // !isEmpty() = almeno un documento -> true (occupato)
            return !snap.isEmpty();
        });
    }
}

