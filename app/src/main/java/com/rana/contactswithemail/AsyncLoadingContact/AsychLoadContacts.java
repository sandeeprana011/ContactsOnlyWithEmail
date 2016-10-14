package com.rana.contactswithemail.asyncloadingcontact;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.rana.contactswithemail.listeners.ListenerCallBacks;
import com.rana.contactswithemail.structure.Contact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class AsychLoadContacts extends AsyncTask<Void, String, ArrayList<Contact>> {

    private final Context context;
    private ListenerCallBacks callBacks;

    public AsychLoadContacts(Context context, ListenerCallBacks callBacks) {
        this.context = context;
        this.callBacks = callBacks;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (this.callBacks != null) {
            this.callBacks.onPreUiCalled();
        }
    }

    @Override
    protected ArrayList<Contact> doInBackground(Void... voids) {
        return getContactDetails();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        if (this.callBacks != null) {
            this.callBacks.onUiUpdate(values);
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Contact> contacts) {
        super.onPostExecute(contacts);
        if (this.callBacks != null) {
            this.callBacks.onPostUiCalled(contacts);
        }
    }

    private ArrayList<Contact> getContactDetails() {
        ArrayList<Contact> contactArrayList = new ArrayList<>();
        ArrayList<String> emlRecs = new ArrayList<String>();
        HashSet<String> emlRecsHS = new HashSet<String>();

        this.onProgressUpdate("Preparing...");

        Context context = this.context;
        ContentResolver cr = context.getContentResolver();
        String[] projectionArray = new String[]{
                ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID
        };
        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";

        this.onProgressUpdate("Reading Contacts...");
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projectionArray, filter, null, order);
        if (cur != null && cur.moveToFirst()) {
            do {
                // names comes in hand sometimes


                String photoId = cur.getString(2);

                String name = cur.getString(1);
                String emailAddress = cur.getString(3);
                long contactId = cur.getLong(4);

//                    String phoneNo = cur.getString(5);
                String photoUri = null;
                this.onProgressUpdate("Loading\n\n" + name);
                if (photoId != null) {
                    photoUri = this.displayPhotoUri(this.context, contactId).toString();
                }


                Log.e("NAME ", name + "");
                Log.e("PHOTO ", photoUri + "");
                Log.e("EMAIL ", emailAddress + "");
                Log.e("PHONE ", getContactNumber(context, String.valueOf(contactId)) + "");


                // keep unique only
                if (emailAddress != null && !emailAddress.equals("")) {
                    if (emlRecsHS.add(emailAddress.toLowerCase())) {
                        emlRecs.add(emailAddress);
                    }

                    String phoneNumber = getContactNumber(context, String.valueOf(contactId));
                    long timeLastCalled = getLastCallLog(context, phoneNumber);
                    String timeFormated = "";
                    if (timeLastCalled != 0) {
                        timeFormated = new SimpleDateFormat("E dd MMM yyyy hh:mm:ss a", Locale.getDefault()).format(new Date());
                    } else {
                        timeFormated = "No call log!";
                    }

                    Contact contact = new Contact(phoneNumber, emailAddress, name, photoUri, timeFormated);
                    contactArrayList.add(contact);

                } else {
                    //don't have any email address
                }

            } while (cur.moveToNext());
            cur.close();
        }

        return contactArrayList;
    }

    private long getLastCallLog(Context context, String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            return 0;
        }

        String[] projectionLogs = {CallLog.Calls.DATE};


        String filterLog = CallLog.Calls.NUMBER + " LIKE '" + phoneNumber + "'";

        Cursor cur = this.context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projectionLogs, filterLog, null, android.provider.CallLog.Calls.DATE + " DESC limit 1;");
        if (cur != null && cur.moveToLast()) {
            long time = cur.getLong(0);
            Log.e("log", String.valueOf(time) + "");
            return time;
        }
        cur.close();
        return 0;
    }

    private String getContactNumber(Context context, String contactId) {

        String numberPhone = "phone not available!";
        Cursor pCur = this.context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);

        if (pCur != null) {
            while (pCur.moveToNext()) {
                String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String type = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                numberPhone = (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(), Integer.parseInt(type), "");

                Log.e("TAG", numberPhone + " phone: " + phone);
                return phone;
            }
            pCur.close();
        }
        return numberPhone;
    }


    public Uri displayPhotoUri(Context context, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        return Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
    }
}

