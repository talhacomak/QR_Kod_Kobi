package com.example.qrkodkob;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class giris extends AppCompatActivity {
    Context c1 = this;
    EditText user_name;
    String ad;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent in = new Intent();
        setContentView(R.layout.giris_lay);

        user_name = findViewById(R.id.ad);
        Spinner spin = (Spinner) findViewById(R.id.spin);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (adapterView.getItemAtPosition(i).toString()){
                    case "Turkce":
                        break;
                    case "English":
                        break;
                    case "русский":
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Button btn = (Button) findViewById(R.id.kayit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad = user_name.getText().toString();
                if (user_name.getText().toString().equals("")){
                    final AlertDialog.Builder alert = new AlertDialog.Builder(c1);
                    alert.setTitle("Uyarı").setMessage("Kullanıcı adı giriniz!").setCancelable(false)
                            .setNeutralButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
                else{
                    Intent in = new Intent(c1, MainActivity.class);
                    in.putExtra("ad", ad);
                    startActivityForResult(in,1);
                    finish();
                }
            }
        });
    }
}
