package com.rana.contactswithemail.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
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
    public void onBindViewHolder(final ContactListAdapter.ViewHolder holder, int position) {
        Contact contact = this.contactArrayList.get(position);
//        holder.iAvatar.setText(contact.get());

        Log.e("URI", contact.getPhotoUri() + "");

        Glide
                .with(this.context)
                .load(contact.getPhotoUri())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.avatar)
                .into(new BitmapImageViewTarget(holder.iAvatar) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.iAvatar.setImageDrawable(circularBitmapDrawable);
                    }
                });

        holder.tName.setText(contact.getName());
        holder.tEmail.setText(contact.getEmail());
        holder.tNumber.setText(contact.getMobile());
        holder.tLastCall.setText(contact.getLastContactTime());
        holder.tDOB.setText(contact.getDob());
        holder.tTotalTime.setText(contact.getTotalTimeFormatted());


    }

    @Override
    public int getItemCount() {
        return this.contactArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iAvatar;
        TextView tName, tNumber, tEmail, tLastCall, tDOB, tTotalTime;

        ViewHolder(View itemView) {
            super(itemView);
            iAvatar = (ImageView) itemView.findViewById(R.id.contact_avatar);
            tName = (TextView) itemView.findViewById(R.id.contact_name);
            tEmail = (TextView) itemView.findViewById(R.id.contact_email);
            tNumber = (TextView) itemView.findViewById(R.id.contact_number);
            tLastCall = (TextView) itemView.findViewById(R.id.contact_lastcall);
            tDOB = (TextView) itemView.findViewById(R.id.contact_dob);
            tTotalTime = (TextView) itemView.findViewById(R.id.contact_total_talktime);

        }
    }


}
