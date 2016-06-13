package com.example.illiaklimov.keyczartest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.keyczar.Crypter;
import org.keyczar.DefaultKeyType;
import org.keyczar.GenericKeyczar;
import org.keyczar.enums.KeyPurpose;
import org.keyczar.exceptions.KeyczarException;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private Crypter crypter;
    private TextView txtSmall;
    private TextView txtBig;
    private TextView txtHuge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSmall = (TextView) findViewById(R.id.txtSmall);
        txtBig = (TextView) findViewById(R.id.txtBig);
        txtHuge = (TextView) findViewById(R.id.txtHuge);

        GenericKeyczar key = null;
        try {
            key = Util.createKey(DefaultKeyType.AES, KeyPurpose.DECRYPT_AND_ENCRYPT);
            crypter = new Crypter(Util.readerFromKeyczar(key));
        } catch (KeyczarException e) {
            Log.e("MainActivity", "no key generated", e);
            crypter = null;
        }
    }

    /**
     * encript/decrypt small text
     * @param view
     */
    public void onSmallEncrypt(View view) {
        System.out.println("onSmallEncrypt");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long l = System.currentTimeMillis();
                    String ciphertext = crypter.encrypt("hello");
                    Log.i("MainActivity", "ciphertext " + ciphertext);
                    String plaintext = crypter.decrypt(ciphertext);

                    final long l1 = System.currentTimeMillis() - l;
                    Log.i("MainActivity", l1 + " plaintext " + plaintext);

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtSmall.setText("Duration (ms) " + l1);
                        }
                    });
                } catch (KeyczarException e) {
                    Log.e("MainActivity", "no encryption", e);
                }
            }
        }).start();


    }


    /**
     * encript/decrypt 12 Mb
     * @param view
     */
    public void onBigEncrypt(View view) {
        System.out.println("onBigEncrypt");

        InputStream inStream = getResources().openRawResource(R.raw.image);
        try {
           final byte[] music = new byte[inStream.available()];

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        long l = System.currentTimeMillis();
                        byte[] encrypt = crypter.encrypt(music);
                        crypter.decrypt(encrypt);
                        final long l1 = System.currentTimeMillis() - l;
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtBig.setText("Duration (ms) " + l1);
                            }
                        });
                    } catch (KeyczarException e) {
                        Log.e("MainActivity", "no encryption", e);
                    }
                }
            }).start();
        } catch (IOException e) {
            Log.e("MainActivity", "no encryption", e);
        }

    }

    /**
     * encript/decrypt 50 Mb
     * @param view
     */
    public void onHugeEncrypt(View view) {
        System.out.println("onHugeEncrypt");

        int size = 50 * 1024 * 1024;

        final byte[] data = new byte[size];

        for (int i = 0; i<size; i++){
            data[i] = (byte) i;
        }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        long l = System.currentTimeMillis();
                        byte[] encrypt = crypter.encrypt(data);
                        crypter.decrypt(encrypt);
                        final long l1 = System.currentTimeMillis() - l;
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtHuge.setText("Duration (ms) " + l1);
                            }
                        });
                    } catch (KeyczarException e) {
                        Log.e("MainActivity", "no encryption", e);
                    }
                }
            }).start();

    }
}
