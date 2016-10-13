package com.rana.contactswithemail;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.rana.contactswithemail.AsyncLoadingContact.AsychLoadContacts;
import com.rana.contactswithemail.listeners.ListenerCallBacks;
import com.rana.contactswithemail.structure.Contact;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListenerCallBacks {

    private static final int CONTACTS_PERMISSION_REQUEST = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.checkForPermissionAndRequestPermission();

        AsychLoadContacts asychLoadContacts = new AsychLoadContacts(this, this);
        asychLoadContacts.execute();

    }


    /**
     * This function will check for runtime permission
     */
    private void checkForPermissionAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG},
                        CONTACTS_PERMISSION_REQUEST);
            }
        }
    }

    @Override
    public void onUiUpdate(String[] values) {

    }

    @Override
    public void onPostUiCalled(ArrayList<Contact> contactArrayList) {

    }

    @Override
    public void onPreUiCalled() {

    }



}