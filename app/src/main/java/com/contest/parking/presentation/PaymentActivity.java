package com.contest.parking.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.contest.parking.R;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class PaymentActivity extends AppCompatActivity {

    private Button pagaOnlineButton;
    private ImageView qrCodeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        pagaOnlineButton = findViewById(R.id.btnPagaOnline);
        qrCodeImage = findViewById(R.id.qrCodeImage);

        // Se vuoi generare un QR Code
        generateQrCode("Targa:AB123CD-PostoAuto:XYZ-Data:2023...");

        pagaOnlineButton.setOnClickListener(v -> {
            // Apri Google.com come finto pagamento
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(i);
        });
    }

    private void generateQrCode(String data) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
            qrCodeImage.setImageBitmap(bitmap);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
