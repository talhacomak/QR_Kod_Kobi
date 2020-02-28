package com.example.qrkodkob;

import android.os.Bundle;

import com.google.zxing.Result;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanCode extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView scannerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

    }

    @Override
    public void handleResult(Result result) {
        MainActivity.result.setText(result.getText());
        new MainActivity.RequestTask_Post().execute("https://web.bilist.com.tr:1512/dv_test_qr/hs/qr");
        onBackPressed();
    }

    @Override
    protected void onPause(){
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume(){
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}
