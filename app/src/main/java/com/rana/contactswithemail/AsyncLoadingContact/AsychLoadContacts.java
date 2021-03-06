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
import com.rana.contactswithemail.structure.CallLogDetail;
import com.rana.contactswithemail.structure.Contact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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


                String birthdayColumns[] = {
                        ContactsContract.CommonDataKinds.Event.START_DATE,
                        ContactsContract.CommonDataKinds.Event.TYPE,
                        ContactsContract.CommonDataKinds.Event.MIMETYPE,
                };


                String birthdayProj = ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY +
                        " and " + ContactsContract.CommonDataKinds.Event.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' and " + ContactsContract.Data.CONTACT_ID + " = " + contactId;

//                String[] selectionArgs = null;
                String sortOrder = ContactsContract.Contacts.DISPLAY_NAME;


                /**
                 * Evaluating Birhtday
                 */
                String birthday = null;

                Cursor birthdayCur = cr.query(ContactsContract.Data.CONTENT_URI, birthdayColumns, birthdayProj, null, sortOrder);
                if (birthdayCur.getCount() > 0) {
                    while (birthdayCur.moveToNext()) {
                        birthday = birthdayCur.getString(birthdayCur.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
                    }
                }

                String phoneNumber = getContactNumber(context, String.valueOf(contactId));
                // keep unique only
                CallLogDetail callLogDetail = getAllCallTime(context, phoneNumber);
//                long totalCallTime = callLogDetail.getTotalTime();


                if (emailAddress != null && !emailAddress.equals("") && callLogDetail.getTotalTime() > 0) {
                    if (emlRecsHS.add(emailAddress.toLowerCase())) {
                        emlRecs.add(emailAddress);
                    }


                    long timeLastCalled = getLastCallLog(context, phoneNumber);

                    String timeFormated = "";
                    if (timeLastCalled != 0) {
                        timeFormated = new SimpleDateFormat("E dd MMM yyyy hh:mm:ss a", Locale.getDefault()).format(new Date(timeLastCalled));
                    } else {
                        timeFormated = "No call log!";
                    }

                    Contact contact = new Contact(phoneNumber, emailAddress, name, photoUri, timeFormated);

/**
 * Evaluating total time
 */
//                    long totalCallTime = getAllCallTime(context, phoneNumber);

//                    String totalTimeFormatted = "";
//                    if (timeLastCalled != 0) {
//                        totalTimeFormatted = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date(totalCallTime));
//                    } else {
//                        totalTimeFormatted = " Total Call Time : n/a";
//                    }

                    contact.setTotalNoOfTimesCalled(callLogDetail.getHowManyTimesCalled());
                    contact.setTotalTime(callLogDetail.getTotalTime());

                    contact.setTotalTimeFormatted("Total Call Time : " + String.valueOf(callLogDetail.getTotalTime()) + " sec");
                    contact.setLastContactTime(timeFormated);

                    if (birthday != null) {
                        contact.setHasBirthday(true);
                        contact.setDob(birthday);
                    } else {
                        contact.setDob("DOB : n/a");
                    }
                    contactArrayList.add(contact);


                } else {
                    //don't have any email address
                }

            } while (cur.moveToNext());
            cur.close();
        }


        return sortDataList(contactArrayList);
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

    private CallLogDetail getAllCallTime(Context context, String phoneNumber) {

        CallLogDetail callLogDetail = new CallLogDetail(0, 0);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            return callLogDetail;
        }

        String[] projectionLogs = {
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };


        String filterLog = CallLog.Calls.NUMBER + " LIKE '" + phoneNumber + "'";

        Cursor cur = this.context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projectionLogs, filterLog, null, android.provider.CallLog.Calls.DATE + " DESC limit 1000;");
        long totalTime = 0;
        int noOfTimesCalled = 0;

        if (cur != null && cur.moveToFirst()) {

            do {
                totalTime = totalTime + cur.getLong(1);
                noOfTimesCalled++;
            } while (cur.moveToNext());
            callLogDetail.setHowManyTimesCalled(noOfTimesCalled);
            callLogDetail.setTotalTime(totalTime);
            return callLogDetail;
        }
        cur.close();
        return callLogDetail;
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

    public ArrayList<Contact> sortDataList(ArrayList<Contact> list) {
        this.onProgressUpdate("Sorting Data...");
//        List<Contact> list = new ArrayList<Contact>();
        Comparator<Contact> comparator = new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {

                long cLeft = c1.getComparatorParam();
                long cRight = c2.getComparatorParam();

                return (int) (cRight - cLeft); // use your logic
//                return (int) (cLeft - cRight); // use your logic
            }
        };

        Collections.sort(list, comparator); // use the comparator as much as u want
//        System.out.println(list);

        return list;

    }

}

