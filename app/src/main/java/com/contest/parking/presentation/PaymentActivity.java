package com.contest.parking.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.contest.parking.R;
import com.contest.parking.data.repository.StoricoRepository;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class PaymentActivity extends BaseActivity {

    private Button pagaOnlineButton, btnSimulaPagamento;
    private ImageView qrCodeImage;
    private StoricoRepository storicoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inietta il layout specifico per PaymentActivity nel container della BaseActivity
        setActivityLayout(R.layout.activity_payment);

        pagaOnlineButton = findViewById(R.id.btnPagaOnline);
        btnSimulaPagamento = findViewById(R.id.btnSimulaPagamento);
        qrCodeImage = findViewById(R.id.qrCodeImage);

        // Genera un QR Code
        Intent intent = getIntent();
        String data = intent.getStringExtra("Data");
        String idStorico = intent.getStringExtra("storicoId");
        Toast.makeText(this, idStorico, Toast.LENGTH_SHORT).show();
        Boolean pagato = true;
        generateQrCode(data);

        pagaOnlineButton.setOnClickListener(v -> {
            // Apri un sito web (esempio di finto pagamento)
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(i);
        });

        // Simula un pagamento
        btnSimulaPagamento.setOnClickListener(v -> {
            storicoRepository.updatePagato(idStorico, pagato)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Pagamento effettuato", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Errore nel Pagamento", Toast.LENGTH_SHORT).show());
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
