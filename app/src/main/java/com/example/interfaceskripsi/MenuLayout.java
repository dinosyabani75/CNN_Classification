package com.example.interfaceskripsi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MenuLayout extends AppCompatActivity {
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_layout);

        Button bt_realtime = findViewById(R.id.bt_realtime);
        Button bt_storage = findViewById(R.id.bt_storage);
        Button bt_about = findViewById(R.id.bt_about);
        ImageView bt_exit = findViewById(R.id.bt_exit);

        bt_realtime.setOnClickListener(v -> {
            //Pindah Activity
            Intent iPindah = new Intent(getApplicationContext(), CameraPredict.class);
            startActivity(iPindah);
        });

        bt_storage.setOnClickListener(v -> {
            Intent iPindah = new Intent(getApplicationContext(), UploadPredict.class);
            startActivity(iPindah);
        });

        bt_about.setOnClickListener(v ->{
            Intent iPindah = new Intent(getApplicationContext(), AboutUs.class);
            startActivity(iPindah);
        });

        bt_exit.setOnClickListener(v -> {
            //Menampilkan alert dialog logout
            showExitDialog();
        });

    }

    private void showExitDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title dialog
        alertDialogBuilder.setTitle("Keluar");
        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Anda yakin akan keluar?")
                .setIcon(R.mipmap.icon_app)
                .setCancelable(false)
                .setPositiveButton("Ya", (dialog, id) -> {
                    // jika tombol diklik, maka akan menutup activity ini
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                })
                .setNegativeButton("Tidak", (dialog, id) -> {
                    // jika tombol ini diklik, akan menutup dialog
                    // dan tidak terjadi apa2
                    dialog.cancel();
                });
        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();
        // menampilkan alert dialog
        alertDialog.show();
    }

    //Saat menekan tombol kembali, akan dikonfirmasi lagi apakah akan keluar aplikasi.
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 3000) {
            Toast.makeText(this, "TEKAN LAGI UNTUK KELUAR", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {

            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }
}