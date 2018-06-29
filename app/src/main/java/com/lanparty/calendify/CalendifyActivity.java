package com.lanparty.calendify;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.lang.Math;

public class CalendifyActivity extends AppCompatActivity {

    private final String MY_PREFS_NAME = "gruppopreferenze"; // il gruppo preferenze comprende tutte le SharedPreferences anche quelle di altri giorni
    private String smileName; //nome della preference di smile da salvare

    Integer[] resources = new Integer[]{R.drawable.ic_sentiment_very_satisfied_black_48dp,
            R.drawable.ic_sentiment_satisfied_black_48dp,
            R.drawable.ic_sentiment_neutral_black_48dp,
            R.drawable.ic_sentiment_dissatisfied_black_48dp,
            R.drawable.ic_sentiment_very_dissatisfied_black_48dp};

    Calendar cal = Calendar.getInstance();
    int cYear = cal.get(Calendar.YEAR);
    int cMonth = cal.get(Calendar.MONTH);
    final int oMonth = cMonth, oYear = cYear;
    final int oDay = cal.get(Calendar.DAY_OF_MONTH);
    int cDay = cal.get(Calendar.DAY_OF_MONTH);
    final Integer[] mesi = {R.string.month1,R.string.month2,R.string.month3
            ,R.string.month4,R.string.month5,R.string.month6
            ,R.string.month7,R.string.month8,R.string.month9
            ,R.string.month10,R.string.month11,R.string.month12};
    public final static String MONTH = "M", YEAR="Y", DAY="D";
    static final String SELECTED_DAY="SDAY",ACT_ANIM_STATE="ACTANIM", CUR_YEAR="CY", CUR_MONTH="CM";
    HashMap<String,LinearLayout> calDays;
    HashMap<String,TextView> calLabels;
    TextView mese,anno;
    LinearLayout actions;
    int lastClicked=-1; //valore standard quando non è stato premuto niente
    Animation fadeIn;
    boolean actionVisible=false;
    final Activity act = this;

    private ImageView smile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendify);
        GridLayout grid = (GridLayout) findViewById(R.id.gridCal);
        TextView[] days = new TextView[35];
        mese = (TextView) findViewById(R.id.textMese);
        anno = (TextView) findViewById(R.id.textAnno);

        ImageView next = (ImageView) findViewById(R.id.nextButton);
        ImageView prev = (ImageView) findViewById(R.id.prevButton);
        //final ImageView smile = (ImageView) findViewById(R.id.smileIcon);
        smile = (ImageView) findViewById(R.id.smileIcon);
        final ImageView txt = (ImageView) findViewById(R.id.textIcon);

        //Inizio dichiarazione custom ActionBar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actbar_layout);
        //Fine dichiarazione custom ActionBar

        calDays = new HashMap<String,LinearLayout>(); //mappa per memorizzare i LinearLayout
        for(int i=0;i<42;i++) {
            calDays.put(Integer.toString(i),(LinearLayout) findViewById(getResources().getIdentifier("day"+i, "id", "com.lanparty.calendify")));
        }


        calLabels = new HashMap<String,TextView>(); //mappa per memorizzare i Label dei LinearLayout
        calLabels.put("0",(TextView) findViewById(R.id.textView));
        for(int i=1;i<42;i++) {
            calLabels.put(Integer.toString(i),(TextView) findViewById(getResources().getIdentifier("textView"+(i+1), "id", "com.lanparty.calendify")));
        }

        //Caricamento animazioni
        actions = (LinearLayout) findViewById(R.id.actions);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);

        mese.setText(getResources().getString(mesi[cMonth]));
        anno.setText(Integer.toString(cYear));
        refreshCalendar(cMonth,cYear);

        smileName=null;// non conosco ancora il nome non è stato selezionato nulla

        next.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                loadNextMonth();
            }
        });
        prev.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                loadPrevMonth();
            }
        });
        anno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText yText = new EditText(act); //Creo dinamicamente un Alert DIalog per scorrere gli anni
                yText.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(act)
                        .setTitle("Seleziona anno")       //titolo
                        .setMessage("Seleziona l'anno che vuoi visualizzare") //Richiesta
                        .setView(yText)
                        .setPositiveButton("Vai!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton)
                            { //In caso di esecuzione
                                cYear = Math.abs(Integer.parseInt(yText.getText().toString()));
                                anno.setText(Integer.toString(cYear));
                                refreshCalendar(cMonth,cYear);
                            }
                        })
                        .setNegativeButton("Annulla", new DialogInterface.OnClickListener() { //In caso di annullamento
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
            }
        });

        if(savedInstanceState != null)
        {
            lastClicked = savedInstanceState.getInt(SELECTED_DAY); //Recupero l'ultimo giorno selezionato
            if(lastClicked!=-1) {
                calDays.get(((Integer) savedInstanceState.getInt(SELECTED_DAY)).toString()).setBackgroundColor(getResources().getColor(R.color.colorPrimaryTransparent));
            }
            if(savedInstanceState.getBoolean(ACT_ANIM_STATE)==true)
                actions.setAlpha(1);
            cMonth = savedInstanceState.getInt(CUR_MONTH);
            cYear = savedInstanceState.getInt(CUR_YEAR);
            mese.setText(getResources().getString(mesi[cMonth]));
            anno.setText(Integer.toString(cYear));
            refreshCalendar(cMonth,cYear);
            if(lastClicked!=-1)
            {
                String day = calLabels.get(Integer.toString(lastClicked)).getText().toString();
                smileName = "smile_" + day + "/" + (cMonth + 1) + "/" + cYear; //aggiorno il nome dell'ultimo smile
            }
            actionVisible = savedInstanceState.getBoolean(ACT_ANIM_STATE);
        }
    }


    public String readSmile(String smile) // metodo per leggere le shared preferences smile
    {
        String stringa;
        SharedPreferences gruppoPref=getSharedPreferences(MY_PREFS_NAME,0);
        stringa=gruppoPref.getString(smile,null);
        return stringa; //ritorna una stringa che contiene la posizione nell'array resource dello smile, null nel caso non ci sia nulla salvato
    }

    public String readText(String text) // metodo per leggere le shared preferences smile
    {
        String stringa;
        SharedPreferences gruppoPref=getSharedPreferences(MY_PREFS_NAME,0);
        stringa=gruppoPref.getString(text,null);
        return stringa; //ritorna una stringa che contiene la posizione nell'array resource dello smile, null nel caso non ci sia nulla salvato
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(SELECTED_DAY, lastClicked);
        savedInstanceState.putBoolean(ACT_ANIM_STATE, actionVisible);
        savedInstanceState.putInt(CUR_YEAR,cYear);
        savedInstanceState.putInt(CUR_MONTH,cMonth);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() //in onResume Aggiorno lo stato dello smile. In questo modo anche quando si ritorna indietro da emoticonActivity si ottiene lo smile aggiornato
    {
        super.onResume();
        if(smileName!=null) //vuol dire che non è passato per on create oppure si è salvato lo stato
        {
            String stringa=readSmile(smileName);
            if(stringa!=null)// è presente una preferenza
            {
                smile.setImageResource(resources[Integer.parseInt(stringa)]);
            }
            else //non è presente una preferenza
            {
                smile.setImageResource(resources[2]);
            }
        }
        if(lastClicked!=-1) //Per aggiornare il colore del TextView del giorno quando torno da TextActivity
            if(readText("testo_" + (calLabels.get(Integer.toString(lastClicked)).getText().toString()) + "/" + (cMonth+1) + "/"+ cYear)!=null)
                calLabels.get(Integer.toString(lastClicked)).setTextColor(getResources().getColor(R.color.colorAccent));
            else
                calLabels.get(Integer.toString(lastClicked)).setTextColor(getResources().getColor(R.color.black));
    }

    public void editText(View view) //metodo necessario per chiamare l'activity TextActivity
    {
        if(actionVisible && lastClicked!=-1)
        {
            TextView tx;
            Intent intent = new Intent(this, TextActivity.class); //dichiaro l'intent..
            intent.putExtra(YEAR, cYear); //.. e aggiungo gli extra
            intent.putExtra(MONTH, cMonth+1);
            tx = (TextView) (calDays.get(Integer.toString(lastClicked)).getChildAt(0)); //Ottengo l'oggetto del TextView all'interno del LinearLayout del giorno
            intent.putExtra(DAY, Integer.parseInt(tx.getText().toString()));
            //Toast.makeText(getApplicationContext(),((TextView) calDays.get(Integer.toString(lastClicked)).getChildAt(0)).getText() + " " + cMonth + " " + cYear,Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
    }

    public void editSmile(View view) //metodo necessario per chiamare l'activity emotionActivity
    {
        if(actionVisible && lastClicked!=-1)
        {
            TextView tx;
            Intent intent = new Intent(this, emotionActivity.class);
            tx = (TextView) (calDays.get(Integer.toString(lastClicked)).getChildAt(0));
            intent.putExtra("data_giorno",Integer.parseInt(tx.getText().toString())+"/"+(cMonth+1)+"/"+cYear);
            startActivity(intent);
        }
    }

    private void refreshCalendar(int onMonth, int onYear) //si occupa di aggiornare tutti i giorni del calendario
    {
        cal.set(onYear, onMonth, 1); //setto il calendario al primo giorno del mese per sapere da dove iniziare
        int numDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH); //ottengo quanti giorni ci sono in un mese
        int dow = cal.get(Calendar.DAY_OF_WEEK); //Ottengo il primo giorno del mese
        if(dow==1) //perchè 1 è domenica, voglio fare in modo che 0 sia lunedì e 6 sia domenica
            dow=6;
        else
            dow-=2;
        int prevNumDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)+2;
        //Toast.makeText(getApplicationContext()," "+ cal.getActualMaximum(Calendar.DAY_OF_MONTH),Toast.LENGTH_LONG).show();
        for(int i=0; i<dow; i++) //giorni del mese precedente
        {
            calLabels.get(Integer.toString(i)).setText(Integer.toString(prevNumDays-dow+i-1));
            calLabels.get(Integer.toString(i)).setTypeface(Typeface.DEFAULT);
            calLabels.get(Integer.toString(i)).setTextColor(getResources().getColor(R.color.blackTransparent));
            calLabels.get(Integer.toString(i)).setAlpha((float)0.5);
            calDays.get(Integer.toString(i)).setOnClickListener(null); //Questi giorni NON possono essere cliccati
            calLabels.get(Integer.toString(i)).setOnClickListener(null);
        }
        for(int i=dow; i<dow+numDays; i++) //giorni del mese corrente
        {
            calLabels.get(Integer.toString(i)).setText(Integer.toString(i-dow+1));
            calLabels.get(Integer.toString(i)).setTypeface(Typeface.DEFAULT_BOLD);
            calLabels.get(Integer.toString(i)).setTextColor(getResources().getColor(R.color.black));
            calLabels.get(Integer.toString(i)).setAlpha(1);
            if(readText("testo_" + (i-dow+1) + "/" + (onMonth+1) + "/"+ onYear)!=null)
                calLabels.get(Integer.toString(i)).setTextColor(getResources().getColor(R.color.colorAccent));
            final int finalI = i;
            calLabels.get(Integer.toString(i)).setOnClickListener(new View.OnClickListener() { //Setto la chiamata alla funzione daySelection per ogni Label
                @Override
                public void onClick(View v) {
                    daySelection(finalI);
                }
            });
            calDays.get(Integer.toString(i)).setOnClickListener(new View.OnClickListener() { //Setto la chiamata alla funzione daySelection per ogni LinearLayout
                @Override
                public void onClick(View v) {
                    daySelection(finalI);
                }
            });
        }
        for(int i=numDays+dow; i<42; i++) //giorni del mese successivo
        {
            calLabels.get(Integer.toString(i)).setText(Integer.toString(i-numDays+1));
            calLabels.get(Integer.toString(i)).setTypeface(Typeface.DEFAULT);
            calLabels.get(Integer.toString(i)).setTextColor(getResources().getColor(R.color.blackTransparent));
            calLabels.get(Integer.toString(i)).setAlpha((float)0.5);
            calDays.get(Integer.toString(i)).setOnClickListener(null);
            calLabels.get(Integer.toString(i)).setOnClickListener(null);
        }
        cal.set(oMonth,oYear,oDay);
    }

    private void daySelection(int l)
    {
        if(lastClicked!=-1)
            calDays.get(Integer.toString(lastClicked)).setBackgroundColor(getResources().getColor(R.color.backgroundStd)); //ripristino il colore di sfondo normale a quelli cliccati in precedenza
        calDays.get(Integer.toString(l)).setBackgroundColor(getResources().getColor(R.color.colorPrimaryTransparent));

        String day=calLabels.get(Integer.toString(l)).getText().toString(); //prendo il giorno attuale

        smileName="smile_"+day+"/"+(cMonth+1)+"/"+cYear; //nome della preferenza "smile_giornoSelezionato"
        String stringa=readSmile(smileName);
        if(stringa!=null) //è presente una preferenza
        {
            smile.setImageResource(resources[Integer.parseInt(stringa)]); //metto lo smile scelto
        }
        else //non ci sono preferenze
        {
            smile.setImageResource(resources[2]); //metto lo smile neutro
        }
        if(!actionVisible) //Controllo se devo eseguire l'animazione
        {
            actionVisible=true;
            actions.setAlpha(1);
            actions.startAnimation(fadeIn);
        }

        lastClicked=l;
    }

    private void loadNextMonth()
    {
        if(cMonth == 11) //se sono arrivato a dicembre
        {
            cYear++;
            cMonth = 0;
        }
        else
        {
            cMonth++;
        }
        mese.setText(getResources().getString(mesi[cMonth]));
        anno.setText(Integer.toString(cYear));
        if(lastClicked!=-1) //resetto l'ultimo giorno premuto
        {
            calDays.get(Integer.toString(lastClicked)).setBackgroundColor(getResources().getColor(R.color.backgroundStd));
            lastClicked = -1;
        }
        smileName=null;   //Rimetto lo smile originale in quanto nulla è più selezionato
        smile.setImageResource(resources[2]);
        refreshCalendar(cMonth,cYear);
    }

    private void loadPrevMonth()
    {
        if(cMonth == 0) //se sono arrivato a gennaio
        {
            cYear--;
            cMonth = 11;
        }
        else
        {
            cMonth--;
        }
        mese.setText(getResources().getString(mesi[cMonth]));
        anno.setText(Integer.toString(cYear));
        if(lastClicked!=-1)
        {
            calDays.get(Integer.toString(lastClicked)).setBackgroundColor(getResources().getColor(R.color.backgroundStd));
            lastClicked = -1;
        }
        smileName=null;   //Rimetto lo smile originale in quanto nulla è più selezionato
        smile.setImageResource(resources[2]);
        refreshCalendar(cMonth,cYear);
    }
}
