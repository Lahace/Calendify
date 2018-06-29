package com.lanparty.calendify;

import android.Manifest;
import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TextActivity extends AppCompatActivity {

    private final String MY_PREFS_NAME = "gruppopreferenze"; // il gruppo preferenze comprende tutte le SharedPreferences anche quelle di altri giorni

    private String testoName; //nome della preference di testo da salvare
    private String data_giorno; //giorno cliccato dall'utente

    Button btn,btndel;
    EditText txt;
    final Activity act = this;
    final String TEXT="TX";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        btn = (Button) findViewById(R.id.save);
        btndel = (Button) findViewById(R.id.delete);
        txt = (EditText) findViewById(R.id.editText);

        //Inizio dichiarazione custom ActionBar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actbar_layout);
        View v = getSupportActionBar().getCustomView();
        TextView titolo = (TextView) v.findViewById(R.id.actbar);
        //Fine dichiarazione custom ActionBar

        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            //Toast.makeText(getApplicationContext(), CalendifyActivity.DAY + " " + CalendifyActivity.MONTH + " " + CalendifyActivity.YEAR, Toast.LENGTH_LONG).show();
            data_giorno=extras.getInt(CalendifyActivity.DAY) + "/" + extras.getInt(CalendifyActivity.MONTH) + "/" + extras.getInt(CalendifyActivity.YEAR); //compongo la data da usare per il backup
           //data del giorno selezionato
            titolo.setText(data_giorno);

            testoName="testo_"+data_giorno; //nome del testo da salvare nelle shared preferences
        }
        if(savedInstanceState != null)
        {
            txt.setText(savedInstanceState.getString(TEXT));
        }
        else
        {
            read(); //metto il valore salvato se presente
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        btndel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
    }


    public void save() // metodo per salvare le shared preferences
    {
        if(!(txt.getText().toString().equals("")))
        {
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, 0).edit();
            editor.putString(testoName, txt.getText().toString());
            editor.commit();

            BackupManager b = new BackupManager(TextActivity.this); //indico che i dati sono pronti per il backup
            b.dataChanged();

            Toast.makeText(TextActivity.this, "Salvataggio eseguito con successo",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    public void read() // metodo per leggere le shared preferences
    {
        String stringa;
        SharedPreferences gruppoPref=getSharedPreferences(MY_PREFS_NAME,0);
        stringa=gruppoPref.getString(testoName,"");
        txt.setText(stringa); //aggiorno l'Edit text
        if(!stringa.equals(""))
            btndel.setEnabled(true);
    }

    public void delete() //metodo per eliminare una shared preference
    {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, 0).edit();
        editor.remove(testoName);
        editor.commit();

        BackupManager b=new BackupManager(TextActivity.this); //indico che i dati sono pronti per il backup
        b.dataChanged();

        Toast.makeText(TextActivity.this,"Rimozione eseguita con successo",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(TEXT, txt.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

}
