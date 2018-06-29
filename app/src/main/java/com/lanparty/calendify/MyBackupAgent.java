package com.lanparty.calendify;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nikolas on 29/06/2016.
 */
public class MyBackupAgent extends BackupAgentHelper {
    // Il nome del gruppo di SharedPreferences
    static final String PREFS = "gruppopreferenze"; //salvo l'intero gruppo che comprende sia il testo che gli smile(in realtà si salva solo una stringa che identifica lo smile)

    // Una chiave per identificare univocamente le preferenze nel backup
    static final String PREFS_BACKUP_KEY = "prefs";

    // Alloco un helper e lo aggiungo al backup agent
    @Override
    public void onCreate() {

        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, PREFS);
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}