package com.rana.contactswithemail.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rana.contactswithemail.R;
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

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Contact> contactArrayList;

    public ContactListAdapter(Context context, ArrayList<Contact> contactArrayList) {
        this.context = context;

        this.contactArrayList = contactArrayList;
    }

    @Override
    public ContactListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactListAdapter.ViewHolder holder, int position) {
        Contact contact = this.contactArrayList.get(position);
//        holder.iAvatar.setText(contact.get());

        Log.e("URI", contact.getPhotoUri() + "");

        Glide
                .with(this.context)
                .load(contact.getPhotoUri())
                .centerCrop()
                .override(250, 250)
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(holder.iAvatar);


        holder.tName.setText(contact.getName());
        holder.tEmail.setText(contact.getEmail());
        holder.tNumber.setText(contact.getMobile());
        holder.tLastCall.setText(contact.getLastContactTime());


    }

    @Override
    public int getItemCount() {
        return this.contactArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iAvatar;
        TextView tName, tNumber, tEmail, tLastCall;

        ViewHolder(View itemView) {
            super(itemView);
            iAvatar = (ImageView) itemView.findViewById(R.id.contact_avatar);
            tName = (TextView) itemView.findViewById(R.id.contact_name);
            tEmail = (TextView) itemView.findViewById(R.id.contact_email);
            tNumber = (TextView) itemView.findViewById(R.id.contact_number);
            tLastCall = (TextView) itemView.findViewById(R.id.contact_lastcall);
        }
    }
}
