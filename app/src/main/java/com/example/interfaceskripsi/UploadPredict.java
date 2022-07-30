package com.example.interfaceskripsi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.interfaceskripsi.ml.ModelFreshRottenFruitPart3;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Locale;

public class UploadPredict extends AppCompatActivity {

    TextToSpeech TTS;
    private ImageView imageView;
    private TextView result,confidence;
    private Bitmap img;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_predict);

        ImageView back_bt = findViewById(R.id.back_button);
        imageView = findViewById(R.id.action_image);
        ImageButton vc_predict = findViewById(R.id.voice_predict);
        ImageButton vc_result = findViewById(R.id.voice_result);
        Button select_but = findViewById(R.id.select_picture);
        Button predict_but = findViewById(R.id.predict_but);
        result = findViewById(R.id.result_text);
        confidence = findViewById(R.id.confidences_text);

        back_bt.setOnClickListener(view -> {
            Intent iPindah = new Intent(getApplicationContext(), MenuLayout.class);
            startActivity(iPindah);
        });

        imageView.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,100);
        });

        select_but.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,100);
        });

        predict_but.setOnClickListener(view -> {
           img = Bitmap.createScaledBitmap(img, 150, 150, true);

            try {
                ModelFreshRottenFruitPart3 model = ModelFreshRottenFruitPart3.newInstance(getApplicationContext());
                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 150, 150, 3}, DataType.FLOAT32);

                TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                tensorImage.load(img);
                ByteBuffer byteBuffer = tensorImage.getBuffer();

                inputFeature0.loadBuffer(byteBuffer);

                // Runs model inference and gets result.
                ModelFreshRottenFruitPart3.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                float[] confidences = outputFeature0.getFloatArray();
                // find the index of the class with the biggest confidence.
                int maxPos = 0;
                float maxConfidence = 0;
                for(int i = 0; i < confidences.length; i++){
                    if(confidences[i] > maxConfidence){
                        maxConfidence = confidences[i];
                        maxPos = i;
                    }
                }
                String[] classes = {"Apel Busuk","Apel Segar",
                        "Jeruk Mandarin Busuk","Jeruk Mandarin Segar",
                        "Pisang Busuk","Pisang Segar"};
                //result.setText(classes[maxPos] + maxConfidence);
                result.setText(classes[maxPos]);

                StringBuilder s = new StringBuilder();
                for(int i = 0; i < classes.length; i++){
                    s.append(String.format("My Prediction is: " + "%s: %.1f%% \n", classes[i], confidences[i] * 10.0f));
                }
                confidence.setText(s.toString());

                // Releases model resources if no longer used.
                //model.close();
            } catch (IOException e) {
                // TODO Handle the exception
            }

        });

        TTS = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                TTS.setLanguage(Locale.ENGLISH);

            } else {
                Toast.makeText(UploadPredict.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });

        vc_result.setOnClickListener(view -> {

            String toSpeak = result.getText().toString().trim();
            if (toSpeak.equals("")) {
                Toast.makeText(UploadPredict.this, "PESAN KOSONG", Toast.LENGTH_SHORT).show();

            } else {
                TTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

            }

        });

        vc_predict.setOnClickListener(view -> {

            String toSpeak = confidence.getText().toString().trim();
            if (toSpeak.equals("")) {
                Toast.makeText(UploadPredict.this, "PESAN KOSONG", Toast.LENGTH_SHORT).show();

            } else {
                TTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

            }

        });

    }//end of main program

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100){
            assert data != null;
            imageView.setImageURI(data.getData());
            Uri uri = data.getData();
            try {
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}