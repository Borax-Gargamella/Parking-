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

public class PaymentActivity extends BaseActivity {

    private Button pagaOnlineButton;
    private ImageView qrCodeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inietta il layout specifico per PaymentActivity nel container della BaseActivity
        setActivityLayout(R.layout.activity_payment);

        pagaOnlineButton = findViewById(R.id.btnPagaOnline);
        qrCodeImage = findViewById(R.id.qrCodeImage);

        // Genera un QR Code (esempio)
        generateQrCode("Targa:AB123CD-PostoAuto:XYZ-Data:2023...");

        pagaOnlineButton.setOnClickListener(v -> {
            // Apri un sito web (esempio di finto pagamento)
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
            startActivity(i);
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
