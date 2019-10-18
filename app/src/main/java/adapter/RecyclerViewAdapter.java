package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.contact.R;

import java.util.ArrayList;

import jdo.Contact;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context mContext;
    ArrayList<Contact> mContactArrayList;

    public RecyclerViewAdapter(Context pContext, ArrayList<Contact> pContactArrayList) {
        this.mContext = pContext;
        this.mContactArrayList = pContactArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup pParent, int pViewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_items, pParent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder pHolder, int pPosition) {
        pHolder.lName_of_contact.setText(mContactArrayList.get(pPosition).getName());

        if (mContactArrayList.get(pPosition).getImg_url() == null) {
            Glide.with(mContext)
                    .load(R.drawable.contact_icon)
                    .transform(new CircleCrop())
                    .override(600, 200)
                    .into(pHolder.lImageView);
            pHolder.lFirst_character_of_contact.setVisibility(View.VISIBLE);
            pHolder.lFirst_character_of_contact.setText(String.valueOf(mContactArrayList.get(pPosition).getName().toUpperCase().charAt(0)));

        } else {
            pHolder.lImageView.setColorFilter(0);
            pHolder.lFirst_character_of_contact.setVisibility(View.INVISIBLE);
            Glide.with(mContext)
                    .load(mContactArrayList.get(pPosition)
                            .getImg_url()).transform(new CircleCrop())
                    .override(600, 200)
                    .into(pHolder.lImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mContactArrayList.size();
    }


     class ViewHolder extends RecyclerView.ViewHolder {

        TextView lName_of_contact, lFirst_character_of_contact;
        ImageView lImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lName_of_contact = itemView.findViewById(R.id.name);
            lImageView = itemView.findViewById(R.id.image);
            lFirst_character_of_contact = itemView.findViewById(R.id.first_character);
        }
    }


}
