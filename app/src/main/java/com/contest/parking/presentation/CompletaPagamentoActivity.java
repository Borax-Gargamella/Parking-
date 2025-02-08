package com.contest.parking.presentation;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.contest.parking.R;
import com.contest.parking.data.repository.AuthRepository;
import com.contest.parking.data.repository.PostoAutoRepository;
import com.contest.parking.data.repository.StoricoRepository;
import com.contest.parking.domain.UseCaseCompletaPagamento;
import com.google.android.material.button.MaterialButton;

public class CompletaPagamentoActivity extends BaseActivity {

    private TextView textRiepilogo;
    private MaterialButton btnConfermaPagamento;

    private UseCaseCompletaPagamento useCaseCompletaPagamento;
    private AuthRepository authRepository;

    private String postoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inietta il layout specifico per CompletaPagamentoActivity
        setActivityLayout(R.layout.activity_payment);

        textRiepilogo = findViewById(R.id.textPaymentTitle);
        btnConfermaPagamento = findViewById(R.id.btnPagaOnline);

        // Inizializza repository e use case
        useCaseCompletaPagamento = new UseCaseCompletaPagamento(
                new PostoAutoRepository(),
                new StoricoRepository()
        );
        authRepository = new AuthRepository();

        // Ricevi l'ID del posto tramite Intent
        postoId = getIntent().getStringExtra("postoId");
        // Qui puoi mostrare un riepilogo (es. tempo, costo, ecc.) in textRiepilogo

        btnConfermaPagamento.setOnClickListener(v -> {
            String userId = authRepository.getCurrentUserId();
            useCaseCompletaPagamento.completaPagamento(postoId, userId, aVoid -> {
                Toast.makeText(CompletaPagamentoActivity.this, "Pagamento completato, posto liberato!", Toast.LENGTH_SHORT).show();
                finish(); // Torna indietro
            });
        });
    }
}
