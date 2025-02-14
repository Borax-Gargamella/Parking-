package com.contest.parking.presentation.utils;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class PagamentoQrCodeGenerator {

    public static Bitmap generateQrCode(String data, int width, int height) throws Exception {
        BarcodeEncoder encoder = new BarcodeEncoder();
        return encoder.encodeBitmap(data, BarcodeFormat.QR_CODE, width, height);
    }

}
