package com.contest.parking.presentation;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.contest.parking.R;
import com.contest.parking.data.model.Storico;
import com.contest.parking.data.model.Utente;
import com.contest.parking.data.repository.AuthRepository;
import com.contest.parking.data.repository.StoricoRepository;
import com.contest.parking.data.repository.UtenteRepository;
import com.contest.parking.domain.UseCaseAggiornaCredenziali;
import com.contest.parking.domain.UseCaseAggiornaDatiUtente;
import com.contest.parking.domain.UseCaseCaricaDatiUtente;
import com.contest.parking.domain.UseCaseCaricaPrenotazioniNonPagate;
import com.contest.parking.presentation.adapter.StoricoAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class UserAreaActivity extends BaseActivity {

    private LinearLayout llUserData;
    private TextView textNome, textCognome, textEmail, textTarga, textPostoPrenotato;
    private MaterialButton btnLogout, btnModificaDati, btnModificaCredenziali;

    private RecyclerView recyclerPrenotazioni; // la "tabella"
    private StoricoAdapter storicoAdapter;

    private UtenteRepository utenteRepository;
    private AuthRepository authRepository;
    private String currentUid;

    private UseCaseCaricaDatiUtente useCaseCaricaDatiUtente;
    private UseCaseCaricaPrenotazioniNonPagate useCaseCaricaPrenotazioniNonPagate;
    private UseCaseAggiornaDatiUtente useCaseAggiornaDatiUtente;
    private UseCaseAggiornaCredenziali useCaseAggiornaCredenziali;

    // Aggiungi un tuo repository/storico
    private StoricoRepository storicoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityLayout(R.layout.activity_user_area);

        // Binding
        llUserData = findViewById(R.id.llUserData);
        textNome = findViewById(R.id.editNome);
        textCognome = findViewById(R.id.editCognome);
        textEmail = findViewById(R.id.textEmail);
        textTarga = findViewById(R.id.editTarga);
        textPostoPrenotato = findViewById(R.id.textPostoPrenotato);
        btnLogout = findViewById(R.id.btnLogout);
        btnModificaDati = findViewById(R.id.btnModificaDati);
        btnModificaCredenziali = findViewById(R.id.btnModificaCredenziali);

        recyclerPrenotazioni = findViewById(R.id.recyclerPrenotazioni);
        recyclerPrenotazioni.setLayoutManager(new LinearLayoutManager(this));

        authRepository = new AuthRepository();
        useCaseCaricaDatiUtente = new UseCaseCaricaDatiUtente();

        storicoRepository = new StoricoRepository();
        useCaseCaricaPrenotazioniNonPagate = new UseCaseCaricaPrenotazioniNonPagate(storicoRepository);

        utenteRepository = new UtenteRepository();
        useCaseAggiornaDatiUtente = new UseCaseAggiornaDatiUtente(utenteRepository);

        useCaseAggiornaCredenziali = new UseCaseAggiornaCredenziali();

        currentUid = authRepository.getCurrentUserId();
        if (currentUid == null) {
            startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
            finish();
        } else {
            llUserData.setVisibility(View.VISIBLE);
            caricaDatiUtente();
            caricaPrenotazioniNonPagate();
        }

        // Gestione click modifica Dati Utente
        btnModificaDati.setOnClickListener(v -> {
            if (btnModificaDati.getText().toString().equals("Modifica Dati")) {
                // Abilita la modifica dei campi
                textNome.setEnabled(true);
                textCognome.setEnabled(true);
                textTarga.setEnabled(true);
                btnModificaDati.setText("Salva");
            } else {
                // Raccogli i dati aggiornati
                String nuovoNome = textNome.getText().toString().trim();
                String nuovoCognome = textCognome.getText().toString().trim();
                String nuovaTarga = textTarga.getText().toString().trim();
                nuovaTarga = nuovaTarga.toUpperCase();

                // Chiama il Use Case per aggiornare i dati
                useCaseAggiornaDatiUtente.aggiornaDatiUtente(currentUid, nuovoNome, nuovoCognome, nuovaTarga, new UseCaseAggiornaDatiUtente.OnAggiornaDatiUtenteListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(UserAreaActivity.this, "Dati aggiornati con successo", Toast.LENGTH_SHORT).show();
                        // Disabilita i campi e ripristina il testo del bottone
                        textNome.setEnabled(false);
                        textCognome.setEnabled(false);
                        textTarga.setEnabled(false);
                        btnModificaDati.setText("Modifica Dati");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(UserAreaActivity.this, "Errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // Gestione click logout
        btnLogout.setOnClickListener(v -> {
            authRepository.logoutUser();
            startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
            finish();
        });

        // Gestione click modifica credenziali
        btnModificaCredenziali.setOnClickListener(v -> showAggiornaCredenzialiDialog());
    }

    private void caricaDatiUtente() {
        useCaseCaricaDatiUtente.loadUserData(currentUid, new UseCaseCaricaDatiUtente.OnUserDataLoadedListener() {
            @Override
            public void onSuccess(Utente utente) {
                textNome.setText(utente.getNome());
                textCognome.setText(utente.getCognome());
                textEmail.setText(utente.getEmail());
                textTarga.setText(utente.getTarga());
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(UserAreaActivity.this,
                        "Errore caricamento utente: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Carica tutti i record di Storico per l'utente currentUid
     * con "pagato" = false (es: prenotazioni in sospeso).
     */
    private void caricaPrenotazioniNonPagate() {
        useCaseCaricaPrenotazioniNonPagate.execute(currentUid, new UseCaseCaricaPrenotazioniNonPagate.Callback() {
            @Override
            public void onSuccess(List<Storico> storiciNonPagati) {
                // Aggiornamento della UI
                if (storiciNonPagati.size() == 1) {
                    textPostoPrenotato.setText("Posto auto prenotato: " + storiciNonPagati.size());
                } else if (storiciNonPagati.isEmpty()) {
                    textPostoPrenotato.setText("Nessun posto auto prenotato");
                } else {
                    textPostoPrenotato.setText("Posti auto prenotati: " + storiciNonPagati.size());
                }
                setupRecyclerView(storiciNonPagati);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(UserAreaActivity.this,
                        "Errore caricamento prenotazioni non pagate: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAggiornaCredenzialiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_aggiorna_credenziali, null);

        builder.setView(dialogView)
                .setNegativeButton("Annulla", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Salva", null); // Listener verrà settato successivamente

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dlg -> {
            // Precompila il campo email con l'email corrente
            TextInputEditText editEmail = dialogView.findViewById(R.id.editEmail);
            String currentEmail = textEmail.getText().toString().replace("Email: ", "").trim();
            editEmail.setText(currentEmail);

            // Riferimenti Campi Password
            TextInputEditText editCurrentPassword = dialogView.findViewById(R.id.editCurrentPassword);
            TextInputEditText editNewPassword = dialogView.findViewById(R.id.editNewPassword);
            TextInputEditText editConfirmPassword = dialogView.findViewById(R.id.editConfirmPassword);

            // Recupero RadioButton
            RadioGroup radioGroupUpdate = dialogView.findViewById(R.id.radioGroupUpdate);
            RadioButton rbAggiornaEmail = dialogView.findViewById(R.id.rbAggiornaEmail);
            RadioButton rbAggiornaPassword = dialogView.findViewById(R.id.rbAggiornaPassword);
            RadioButton rbAggiornaEntrambi = dialogView.findViewById(R.id.rbAggiornaEntrambi);

            // Gestione cambio RadioButton
            // Email, Password, Entrambi
            // Stato iniziale Email
            radioGroupUpdate.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rbAggiornaEmail) {
                    // Modalità aggiornamento email: abilita email, nascondi password
                    editEmail.setEnabled(true);
                    editNewPassword.setVisibility(View.GONE);
                    editConfirmPassword.setVisibility(View.GONE);
                } else if (checkedId == R.id.rbAggiornaPassword) {
                    // Modalità aggiornamento password: disabilita email, mostra password
                    editEmail.setEnabled(false);
                    editNewPassword.setVisibility(View.VISIBLE);
                    editConfirmPassword.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.rbAggiornaEntrambi) {
                    // Modalità aggiornamento entrambi: abilita tutto
                    editEmail.setEnabled(true);
                    editNewPassword.setVisibility(View.VISIBLE);
                    editConfirmPassword.setVisibility(View.VISIBLE);
                }
            });

            // Imposta lo stato iniziale in base alla selezione di default
            if (rbAggiornaEmail.isChecked()) {
                editEmail.setEnabled(true);
                editNewPassword.setVisibility(View.GONE);
                editConfirmPassword.setVisibility(View.GONE);
            } else if (rbAggiornaPassword.isChecked()) {
                editEmail.setEnabled(false);
                editNewPassword.setVisibility(View.VISIBLE);
                editConfirmPassword.setVisibility(View.VISIBLE);
            } else if (rbAggiornaEntrambi.isChecked()) {
                editEmail.setEnabled(true);
                editNewPassword.setVisibility(View.VISIBLE);
                editConfirmPassword.setVisibility(View.VISIBLE);
            }

            Button btnSalva = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnSalva.setOnClickListener(v -> {
                String newEmail = editEmail.getText().toString().trim();
                String currentPassword = editCurrentPassword.getText().toString().trim();
                String newPassword = editNewPassword.getText().toString().trim();
                String confirmPassword = editConfirmPassword.getText().toString().trim();

                // Determina la modalità in base al radio button selezionato
                int selectedId = radioGroupUpdate.getCheckedRadioButtonId();
                if (selectedId == R.id.rbAggiornaEmail) {
                    // Aggiornamento Email: richiede email e password corrente
                    // (I campi password nuova non sono visibili, quindi li consideriamo vuoti)
                    useCaseAggiornaCredenziali.aggiornaCredenzialiEmail(newEmail, currentPassword, new UseCaseAggiornaCredenziali.OnAggiornaCredenzialiListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(UserAreaActivity.this, "Email aggiornata", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            textEmail.setText(newEmail);
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(UserAreaActivity.this, "Errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (selectedId == R.id.rbAggiornaPassword) {
                    // Aggiornamento Password: usa l'email corrente e richiede la nuova password
                    useCaseAggiornaCredenziali.aggiornaCredenzialiPassword(currentEmail, currentPassword, newPassword, confirmPassword, new UseCaseAggiornaCredenziali.OnAggiornaCredenzialiListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(UserAreaActivity.this, "Password aggiornata", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(UserAreaActivity.this, "Errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (selectedId == R.id.rbAggiornaEntrambi) {
                    // Aggiornamento Entrambi: richiede email, password corrente e nuova password
                    useCaseAggiornaCredenziali.aggiornaCredenzialiEntrambi(newEmail, currentPassword, newPassword, confirmPassword, new UseCaseAggiornaCredenziali.OnAggiornaCredenzialiListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(UserAreaActivity.this, "Credenziali aggiornate", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            textEmail.setText(newEmail);
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(UserAreaActivity.this, "Errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        });
        dialog.show();
    }

    private void setupRecyclerView(List<Storico> list) {
        storicoAdapter = new StoricoAdapter(list, item -> {
            // onPagaClick
            // Avvia PaymentActivity, passandogli l'ID del doc (o i dati che servono)
            Intent intent = new Intent(UserAreaActivity.this, PaymentActivity.class);
            intent.putExtra("Data", item.getDataInizio() + " " + item.getDataFine());
            intent.putExtra("storicoId", item.getId());
            intent.putExtra("importo", item.getPrezzo());
            startActivity(intent);
        });
        recyclerPrenotazioni.setAdapter(storicoAdapter);
    }
}
