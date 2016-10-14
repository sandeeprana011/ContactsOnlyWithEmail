package com.rana.contactswithemail;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CONTACTS_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    this.loadContacts();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    final AlertDialog.Builder alertDailogBuilder = new AlertDialog.Builder(this)
                            .setTitle("Permission Denied!")
                            .setMessage("App wouldn't work without Contact and Log read permissions!")
                            .setPositiveButton("Show Permissons", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkForPermissionAndRequestPermission();
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    System.exit(0);

                                }
                            });
                    alertDailogBuilder.show();

                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (textViewStatusLoading != null) {
                        textViewStatusLoading.setText("Access Denied!");
                    }
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
                    Manifest.permission.READ_CONTACTS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALL_LOG)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG},
                        CONTACTS_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG},
                        CONTACTS_PERMISSION_REQUEST);
            }
        } else {
            this.loadContacts();
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
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        if (textViewStatusLoading != null)
            textViewStatusLoading.setVisibility(View.VISIBLE);

    }

    private void loadContacts() {
        AsychLoadContacts asychLoadContacts = new AsychLoadContacts(this, this);
        asychLoadContacts.execute();
    }

}
