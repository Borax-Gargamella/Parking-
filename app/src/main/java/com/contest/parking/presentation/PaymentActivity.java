package com.contest.parking.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.contest.parking.R;
import com.contest.parking.data.repository.StoricoRepository;
import com.contest.parking.domain.UseCaseProcessPagamento;
import com.contest.parking.presentation.utils.PagamentoQrCodeGenerator;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class PaymentActivity extends BaseActivity {

    private Button pagaOnlineButton, btnSimulaPagamento;
    private ImageView qrCodeImage;
    private TextView textImporto;

    private StoricoRepository storicoRepository;
    private UseCaseProcessPagamento useCaseProcessPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inietta il layout specifico per PaymentActivity nel container della BaseActivity
        setActivityLayout(R.layout.activity_payment);

        storicoRepository = new StoricoRepository();
        useCaseProcessPayment = new UseCaseProcessPagamento(storicoRepository);

        pagaOnlineButton = findViewById(R.id.btnPagaOnline);
        btnSimulaPagamento = findViewById(R.id.btnSimulaPagamento);
        qrCodeImage = findViewById(R.id.qrCodeImage);
        textImporto = findViewById(R.id.textImporto);

        // Genera un QR Code
        Intent intent = getIntent();
        String data = intent.getStringExtra("Data");
        String idStorico = intent.getStringExtra("storicoId");
        Double importo = intent.getDoubleExtra("importo", 0.0);

        try {
            Bitmap qrBitmap = PagamentoQrCodeGenerator.generateQrCode(data, 400, 400);
            qrCodeImage.setImageBitmap(qrBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        textImporto.setText(String.format("Importo: %.2f €", importo));

        // Apri un sito web (simula un pagamento)
        pagaOnlineButton.setOnClickListener(v -> {
            // Apri un sito web (esempio di finto pagamento)
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(i);
        });

        // Simula un pagamento
        btnSimulaPagamento.setOnClickListener(v -> {
            useCaseProcessPayment.processPayment(idStorico, new UseCaseProcessPagamento.PaymentCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(PaymentActivity.this, "Pagamento effettuato", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(PaymentActivity.this, MainActivity.class);
                    startActivity(intent1);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(PaymentActivity.this, "Errore nel Pagamento: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void generateQrCode(String data) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
            qrCodeImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
