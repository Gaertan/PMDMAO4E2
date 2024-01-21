package com.example.pmdmao4e2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions;
import com.google.mlkit.nl.translate.TranslateLanguage.Language;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private boolean autoStatus;

    private ProgressDialog progressDialog;
    private ArrayList<ModelLanguage> languagesArrayList;




    private Button buttonTraducir;
    private Button buttonReproducir;
    private Switch switchAuto;
    private ImageButton imageButtonGrabar;
    private Spinner spinnerIdiomaReconocido;
    private Spinner spinnerIdiomaDisponible;
    private EditText editTextEscuchado;
    private EditText editTextTraducido;
    private String sourceLanguageCodeDefault = "es";
    private String targetLanguageCodeDefault = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Inicializar los atributos de clase
        buttonTraducir = findViewById(R.id.buttonTraducir);
        buttonReproducir = findViewById(R.id.buttonReproducir);
        switchAuto = findViewById(R.id.switchAuto);
        imageButtonGrabar = findViewById(R.id.imageButtonGrabar);
        spinnerIdiomaReconocido = findViewById(R.id.spinnerIdiomaReconocido);
        spinnerIdiomaDisponible = findViewById(R.id.spinnerIdiomaDisponible);
        editTextEscuchado = findViewById(R.id.editTextEscuchado);
        editTextTraducido = findViewById(R.id.editTextTraducido);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadAvailableLanguages();
        editTextEscuchado.setText("String de prueba");
        imageButtonGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escuchar();
            }
        });

        buttonTraducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                traducir();
            }
        });

        buttonReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reproducir();
            }
        });

        spinnerIdiomaReconocido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerIdiomaDisponible.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayAdapter<ModelLanguage> adapterIdiomaReconocido = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languagesArrayList);
        adapterIdiomaReconocido.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIdiomaReconocido.setAdapter(adapterIdiomaReconocido);

        ArrayAdapter<ModelLanguage> adapterIdiomaDisponible = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languagesArrayList);
        adapterIdiomaDisponible.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIdiomaDisponible.setAdapter(adapterIdiomaDisponible);

      //  seleccionarSpinnerReconocido(sourceLanguageCodeDefault);
       seleccionarSpinnerDisponible(targetLanguageCodeDefault);
        spinnerIdiomaReconocido.setSelection(AdapterView.INVALID_POSITION);
       // spinnerIdiomaDisponible.setSelection(AdapterView.INVALID_POSITION);
    }

    private void loadAvailableLanguages() {
        languagesArrayList = new ArrayList<>();


        List<String> languageCodeList = TranslateLanguage.getAllLanguages();

        for(String languageCode: languageCodeList){
            String languageTitle = new Locale(languageCode).getDisplayLanguage();

        ModelLanguage modelLanguage = new ModelLanguage(languageCode,languageTitle);
        languagesArrayList.add(modelLanguage);
        }

    }

    //FUNCIONALIDADES ESCUCHA --------------------

    private void escuchar() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast
                    .makeText(MainActivity.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                editTextEscuchado.setText(
                        Objects.requireNonNull(result).get(0));

                reconocerLenguage();
                if(autoStatus){traducir();reproducir();}

            }
        }


    }
//--------------------------------------
    private void seleccionarSpinnerReconocido(String codigo){
        for (int i = 0; i < spinnerIdiomaReconocido.getCount(); i++) {
        ModelLanguage modelLanguage = (ModelLanguage) spinnerIdiomaReconocido.getItemAtPosition(i);
        if (modelLanguage.getLanguageCode().equals(codigo)) {
            spinnerIdiomaReconocido.setSelection(i);
            break;
        }
    }}
    private void seleccionarSpinnerDisponible(String codigo){
        for (int i = 0; i < spinnerIdiomaDisponible.getCount(); i++) {
            ModelLanguage modelLanguage = (ModelLanguage) spinnerIdiomaDisponible.getItemAtPosition(i);
            if (modelLanguage.getLanguageCode().equals(codigo)) {
                spinnerIdiomaDisponible.setSelection(i);
                break;
            }
        }}

    private void reconocerLenguage(){
        LanguageIdentifier languageIdentifier = LanguageIdentification.getClient(
                new LanguageIdentificationOptions.Builder()
                        .setConfidenceThreshold(0.11f)
                        .build());
        languageIdentifier.identifyLanguage(editTextEscuchado.getText().toString())
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode.equals("und")) {
                                    Toast.makeText(MainActivity.this, "No se ha reconocido el lenguaje(codigo und)", Toast.LENGTH_SHORT).show();
                                    seleccionarSpinnerReconocido(sourceLanguageCodeDefault);


                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Se ha reconocido el idioma: " + languageCode, Toast.LENGTH_SHORT).show();
                                    seleccionarSpinnerReconocido(languageCode);

                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "hubo un fallo procesando el reconocimiento", Toast.LENGTH_SHORT).show();
                                seleccionarSpinnerReconocido(sourceLanguageCodeDefault);

                            }
                        });
    }
    private boolean validarTextoYSeleccion(){
        String textoEscuchado = editTextEscuchado.getText().toString().trim();

        if (textoEscuchado.isEmpty() || textoEscuchado.equals(" ")) {
            return false;
        }
        else return true;
    }
    private boolean validarSeleccionSpinner() {
        ModelLanguage idiomaReconocido = (ModelLanguage) spinnerIdiomaReconocido.getSelectedItem();
        if ( idiomaReconocido == null ) {
            return false;
        }
        else return true;
    }

    private void traducir(){
        if(!validarSeleccionSpinner()){reconocerLenguage();}
        reconocerLenguage();
        if (!validarTextoYSeleccion()) {
            Toast.makeText(MainActivity.this, "Debe grabar un audio para traducirlo", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            ModelLanguage idiomaReconocido = (ModelLanguage) spinnerIdiomaReconocido.getSelectedItem();
            ModelLanguage idiomaDisponible = (ModelLanguage) spinnerIdiomaDisponible.getSelectedItem();


            String codigoLenguageOrigen = idiomaReconocido.getLanguageCode();
            String codigoLenguageTarget = idiomaDisponible.getLanguageCode();

            progressDialog.setMessage("Procesando modelo de lenguaje");
            progressDialog.show();

            TranslatorOptions translatorOptions = new TranslatorOptions.Builder()
                    .setSourceLanguage(codigoLenguageOrigen)
                    .setTargetLanguage(codigoLenguageTarget)
                    .build();
            Translator translator = Translation.getClient(translatorOptions);
            progressDialog.dismiss();
            DownloadConditions downloadConditions = new DownloadConditions.Builder()
                    .requireWifi()
                    .build();
            translator.downloadModelIfNeeded(downloadConditions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.setMessage("traduciendo...");
                            progressDialog.show();


                            translator.translate(editTextEscuchado.getText().toString())
                                    .addOnSuccessListener(new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String textoTraducido){
                                            editTextTraducido.setText(textoTraducido);
                                            progressDialog.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                        }
                                    }) ;

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });



        }



    }



    private void reproducir(){}





}



