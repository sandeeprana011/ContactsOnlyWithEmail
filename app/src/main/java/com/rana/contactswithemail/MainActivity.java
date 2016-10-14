package com.rana.contactswithemail;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rana.contactswithemail.adapters.ContactListAdapter;
import com.rana.contactswithemail.asyncloadingcontact.AsychLoadContacts;
import com.rana.contactswithemail.listeners.ListenerCallBacks;
import com.rana.contactswithemail.structure.Contact;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListenerCallBacks {

    private static final int CONTACTS_PERMISSION_REQUEST = 12;
    RecyclerView recyclerView;
    RelativeLayout loaderLayout;
    ProgressBar progressBar;
    TextView textViewStatusLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.checkForPermissionAndRequestPermission();
        recyclerView = (RecyclerView) findViewById(R.id.rv_contacts);
        loaderLayout = (RelativeLayout) findViewById(R.id.rv_loader_view);
        progressBar = (ProgressBar) findViewById(R.id.pb_progressbar);
        textViewStatusLoading = (TextView) findViewById(R.id.tv_progress_status);

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
    public void onUiUpdate(final String[] values) {
        if (values.length > 0 && values[0] != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewStatusLoading.setText(values[0]);
                }
            });

        }
    }

    @Override
    public void onPostUiCalled(ArrayList<Contact> contactArrayList) {
        if (loaderLayout != null) {
            loaderLayout.setVisibility(View.GONE);
        }

        if (contactArrayList != null) {
            ContactListAdapter listAdapter = new ContactListAdapter(this, contactArrayList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(listAdapter);
        }

    }

    @Override
    public void onPreUiCalled() {

    }


}
