package com.lanparty.calendify;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class emotionActivity extends AppCompatActivity {

    private final String MY_PREFS_NAME = "gruppopreferenze"; // il gruppo preferenze comprende tutte le SharedPreferences, anche quelle di altri giorni

    private String smileName; //nome della preference di smile da salvare
    private String data_giorno; //giorno cliccato dall'utente
    private String save_smile_value;// valore della preference smile da salvare


    ListView list;
    Integer[] resources = new Integer[]{R.drawable.ic_sentiment_very_satisfied_black_48dp,
            R.drawable.ic_sentiment_satisfied_black_48dp,
            R.drawable.ic_sentiment_neutral_black_48dp,
            R.drawable.ic_sentiment_dissatisfied_black_48dp,
            R.drawable.ic_sentiment_very_dissatisfied_black_48dp};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion);

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
            data_giorno=getIntent().getStringExtra("data_giorno"); //data del giorno selezionato
            titolo.setText(data_giorno);
            smileName="smile_"+data_giorno; //nome del smile da salvare
        }

        list = (ListView) findViewById(R.id.listView);
        list.setAdapter(new myAdapter(this, resources, new String[] {
                getResources().getString(R.string.emotion1),
                getResources().getString(R.string.emotion2),
                getResources().getString(R.string.emotion3),
                getResources().getString(R.string.emotion4),
                getResources().getString(R.string.emotion5)})); //setto l'adapter della lista e gli passo i miei parametri

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), Integer.toString(position),Toast.LENGTH_SHORT).show();
                //Position indica l'elemento della lista cliccato, da 0 a 4
                //I numeri e le immagini sono in accordo con l'array Resources

                save_smile_value=Integer.toString(position); //memorizzo la posizione dello smile come stringa
                save();
            }
        });
    }


    public void save() // metodo per salvare le shared preferences
    {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, 0).edit();
        editor.putString(smileName, save_smile_value );
        editor.commit();

        BackupManager b=new BackupManager(emotionActivity.this);  //indico che i dati sono pronti per il backup
        b.dataChanged();

        Toast.makeText(emotionActivity.this,"Salvataggio eseguito con successo",
                Toast.LENGTH_SHORT).show();
        finish();
    }
}

class myAdapter extends BaseAdapter { //Il mio adapter per gli elementi personalizzati della lista

    Context context;
    String[] data;
    Integer[] res;
    private static LayoutInflater inflater = null;

    public myAdapter(Context context, Integer[] res , String[] data) {
        this.context = context;
        this.data = data;
        this.res = res;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null); //inflate del layout
        TextView text = (TextView) vi.findViewById(R.id.text); //assegno al layout i valori che voglio
        ImageView img = (ImageView) vi.findViewById(R.id.emotionView);
        text.setText(data[position]);
        img.setImageResource(res[position]);
        return vi;
    }
}
