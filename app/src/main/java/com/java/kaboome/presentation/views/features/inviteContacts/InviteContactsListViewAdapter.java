package com.java.kaboome.presentation.views.features.inviteContacts;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.java.kaboome.R;
import com.java.kaboome.presentation.entities.ContactModel;
import com.java.kaboome.presentation.images.glide.GlideApp;
import com.java.kaboome.presentation.views.features.inviteContacts.adapter.InvitationContactListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class InviteContactsListViewAdapter extends RecyclerView.Adapter<InviteContactsListViewAdapter.ContactViewHolder> implements Filterable {

    private static final String TAG = "KMInviteContactsLVAdap";
    private final InvitationContactListener invitationContactListener;
    private List<ContactModel> contacts;
    private List<ContactModel> contactsOriginal;
    private Context mContext;
    private Fragment callingFragment;


    class ContactViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        ProgressBar progressBar;
        TextView displayName;
        CheckBox checkBox;



        ContactViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.contact_invite_user_image);
            progressBar = itemView.findViewById(R.id.contact_invite_user_image_progress);
            displayName = itemView.findViewById(R.id.contact_invite_user_name);
            checkBox = itemView.findViewById(R.id.contact_invite_user_checkbox);
        }
    }

    public InviteContactsListViewAdapter(List<ContactModel> contactsList, Context mContext, InvitationContactListener invitationContactListener) {
        this.contacts = contactsList;
        this.mContext = mContext;
        this.invitationContactListener = invitationContactListener;
        contactsOriginal = new ArrayList<>(contactsList);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_contact_invite_user,
                parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final @NonNull ContactViewHolder holder, int position) {
        final ContactModel contact = contacts.get(position);

        if(contact.getPhotoURI() != null) {
            holder.progressBar.setVisibility(View.VISIBLE);
            GlideApp.with(mContext).load(contact.getPhotoURI()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.d(TAG, "Error loading groupImage from server " + e.getMessage());
                    if (holder.progressBar != null) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Log.d(TAG, "Loaded groupImage successfully from server");
                    if (holder.progressBar != null) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                    return false;
                }
            }).into(holder.profileImage);
        }

        holder.displayName.setText(contact.getName());

        if(invitationContactListener.isContactAlreadySelected(contact)){
            holder.checkBox.setChecked(true);
        }
        else{
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean checked = ((CheckBox) v).isChecked();

                if(checked){ //if it is checked, add it to the list of contacts
                    invitationContactListener.contactChecked(contact);
                }
                else{ //if unchecked, remove it from the list of contacts
                    invitationContactListener.contactCheckedRemoved(contact);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public Filter getFilter() {
        return contactsFilter;
    }

    private Filter contactsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ContactModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(contactsOriginal);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ContactModel item : contactsOriginal) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contacts.clear();
            contacts.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public void updateData(List<ContactModel> contacts_data) {

        this.contacts.clear();
        this.contacts.addAll(contacts_data);
        notifyDataSetChanged();
    }

}
