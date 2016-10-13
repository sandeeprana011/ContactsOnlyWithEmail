package com.rana.contactswithemail.listeners;

import com.rana.contactswithemail.structure.Contact;

import java.util.ArrayList;

/**
 * Created by sandeeprana on 13/10/16.
 * License is only applicable to individuals and non-profits
 * and that any for-profit company must
 * purchase a different license, and create
 * a second commercial license of your
 * choosing for companies
 */
public interface ListenerCallBacks {
    public void onUiUpdate(String[] values);

    public void onPostUiCalled(ArrayList<Contact> contactArrayList);

    public void onPreUiCalled();
}
