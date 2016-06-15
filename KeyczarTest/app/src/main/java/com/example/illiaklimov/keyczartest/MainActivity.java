package com.example.illiaklimov.keyczartest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.illiaklimov.keyczartest.KeyUtil.Util;

import org.keyczar.Crypter;
import org.keyczar.DefaultKeyType;
import org.keyczar.GenericKeyczar;
import org.keyczar.enums.KeyPurpose;
import org.keyczar.exceptions.KeyczarException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Crypter crypter;
    private TextView txtSmall;
    private TextView txtBig;
    private TextView txtHuge;
    GenericKeyczar genericKeyczar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSmall = (TextView) findViewById(R.id.txtSmall);
        txtBig = (TextView) findViewById(R.id.txtBig);
        txtHuge = (TextView) findViewById(R.id.txtHuge);
    }

    /**
     * encript/decrypt small text
     *
     * @param view
     */
    public void onSmallEncrypt(View view) {
        Log.i(TAG, "onSmallEncrypt");
        if (crypter == null) {
            Toast.makeText(this, "No Crypto generated", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long l = System.currentTimeMillis();
                    String ciphertext = crypter.encrypt("hello");
                    Log.i(TAG, "ciphertext " + ciphertext);
                    String plaintext = crypter.decrypt(ciphertext);

                    final long l1 = System.currentTimeMillis() - l;
                    Log.i(TAG, l1 + " plaintext " + plaintext);

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtSmall.setText("Duration (ms) " + l1);
                        }
                    });
                } catch (KeyczarException e) {
                    Log.e(TAG, "no encryption", e);
                }
            }
        }).start();


    }


    /**
     * encript/decrypt 12 Mb
     *
     * @param view
     */
    public void onBigEncrypt(View view) {
        Log.i(TAG, "onBigEncrypt");
        if (crypter == null) {
            Toast.makeText(this, "No Crypto generated", Toast.LENGTH_LONG).show();
            return;
        }
        int size = 10 * 1024 * 1024;

        final byte[] data = new byte[size];

        for (int i = 0; i < size; i++) {
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
                            txtBig.setText("Duration (ms) " + l1);
                        }
                    });
                } catch (KeyczarException e) {
                    Log.e(TAG, "no encryption", e);
                }
            }
        }).start();

    }

    /**
     * encript/decrypt 50 Mb
     *
     * @param view
     */
    public void onHugeEncrypt(View view) {
        Log.i(TAG, "onHugeEncrypt");

        if (crypter == null) {
            Toast.makeText(this, "No Crypto generated", Toast.LENGTH_LONG).show();
            return;
        }

        int size = 50 * 1024 * 1024;

        final byte[] data = new byte[size];

        for (int i = 0; i < size; i++) {
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
                    Log.e(TAG, "no encryption", e);
                }
            }
        }).start();
    }

    public void onWriteEncrypt(View view) {
        Log.i(TAG, "onWriteEncrypt");
        if (genericKeyczar == null) {
            Toast.makeText(this, "No Crypto generated", Toast.LENGTH_LONG).show();
        } else
            Util.writeJsonToFile(genericKeyczar, new File(getFilesDir(), "key.txt"));
    }

    public void onReadEncrypt(View view) {
        Log.i(TAG, "onReadEncrypt");

        crypter = Util.crypterFromFile(new File(getFilesDir(), "key.txt"));

        if (crypter == null) {
            Toast.makeText(this, "No Crypto restored", Toast.LENGTH_LONG).show();
        }
    }

    public void onCryptoGenerate(View view) {
        try {
            genericKeyczar = Util.createKey(DefaultKeyType.AES, KeyPurpose.DECRYPT_AND_ENCRYPT);
            crypter = new Crypter(Util.readerFromKeyczar(genericKeyczar));
        } catch (KeyczarException e) {
            Log.e(TAG, "no Crypto generated", e);
            crypter = null;
        }
    }
}
