package com.example.qrkodkob;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static java.util.UUID.randomUUID;

public class MainActivity extends AppCompatActivity {
    static final int Contact_Request = 1;
    public static TextView result;

    String message;
    Button scanner;
    static String GUID;
    static TextView resultText;
    Context c1 = this;
    int k = 1;
    int m = 1;
    shared_pref share = new shared_pref();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (share.isExist(c1, "m")) m = share.getValueInt(c1, "m");
        Intent inn = getIntent();
        if(inn.getExtras() != null && m==1){
            String returnedResult = inn.getExtras().getString("ad");
            GUID = randomUUID().toString();
            share.save(c1, "guid", GUID);
            final AlertDialog.Builder alert = new AlertDialog.Builder(c1);

            message = returnedResult;
            alert.setTitle("Hoşgeldiniz " + message).setMessage("id: " + GUID).setCancelable(false)
                    .setNeutralButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
            m = 0;
            share.save(c1, "m", m);
        }

        if (share.isExist(c1, "guid")) GUID = share.getValue(c1, "guid");

        InputStream is = null;

        if(share.isExist(c1, "k")) k = share.getValueInt(c1, "k");
        if(k==1) {
            k = 0;
            share.save(c1, "k", k);
            Intent in = new Intent(c1, giris.class);
            startActivityForResult(in, Contact_Request);
            finish();
        }

        if (share.isExist(c1, "m")) m = share.getValueInt(c1, "m");


        scanner = (Button) findViewById(R.id.btn1);
        result = (TextView) findViewById(R.id.text2);
        resultText = (TextView) findViewById(R.id.text1);
        Button reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert2 = new AlertDialog.Builder(c1);
                alert2.setTitle("Uyarı!").setMessage("Kullanıcı adı ve GUID sıfırlanacak!\n Emin misiniz?").setCancelable(false)
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                share.clear(c1);
                                Toast.makeText(getApplication(), "Ayarlar sıfırlandı.", Toast.LENGTH_LONG).show();
                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c1, ScanCode.class);
                startActivityForResult(intent, Contact_Request);
            }
        });

    }

    //********************************* ON_CREATE SON **********************************
    //**********************************************************************************
    /******************************** GET REQUEST CODE ********************************/
    class RequestTask_Get extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {  // ANDROİD MANİFESTE EKLE
            String dosya="";
            HttpURLConnection connection = null;
            BufferedReader br = null;
            Authenticator.setDefault(new Authenticator(){
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("admin","admin".toCharArray());
            }});

            try {
                java.net.URL url = new URL (uri[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setUseCaches(false);
                connection.connect();;
                InputStream is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                String satir;
                while ((satir = br.readLine()) != null){
                    Log.d("satir:", satir);
                    dosya += satir;
                }
                return dosya;
            } catch (Exception e){
                e.printStackTrace();
            }
            return "hata";
        }

        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(result);
            Log.d("result",result);
            resultText.setText(result);
        }
    }


    /************************ POST REQUEST CODE ********************************/

    static class RequestTask_Post extends AsyncTask<String, String, String> {

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... uri) { // ANDROİD MANİFESTE EKLE!!!

            //QR'dan gelen parametleri ayırıyoruz**********************
            String str = result.getText().toString();
            String strAr[] = str.split(",");
            String post[] = new String[strAr.length];
            String post_name[] = new String[strAr.length];
            String temp[];
            for (int i=0; i<strAr.length; i++){
                temp = strAr[i].split("\": \"");
                post_name[i] = temp[0].substring(3, temp[0].length());
                post[i] = temp[1].substring(0, temp[1].length()-1);
            }
            post_name[0] = post_name[0].substring(1, post_name[0].length());
            post[post.length-1] = post[post.length-1].substring(0, post[post.length-1].length()-3);

            String dosya="";
            JSONObject postDataParams = new JSONObject();
            HttpURLConnection connection = null;
            BufferedWriter br = null;
            Authenticator.setDefault(new Authenticator(){
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("admin","admin".toCharArray());
                }});

            try {
                for(int i=0; i<post.length; i++){
                    if(!post_name[i].equals("")) postDataParams.put(post_name[i], post[i]);
                }
                postDataParams.put("DeviceID", GUID);
                java.net.URL url = new URL (uri[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.connect();;
                OutputStream os = connection.getOutputStream();
                br = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                br.write(postDataParams.toString());
                br.flush();
                br.close();
                os.close();

                int responseCode=connection.getResponseCode();
                if(responseCode == HttpsURLConnection.HTTP_OK){
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";
                    while((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                }
                return dosya;
            } catch (Exception e){
                e.printStackTrace();
            }
            return "hata";
        }

        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(result);
            Log.d("result",result);
            resultText.setText(result);
        }
    }
}
