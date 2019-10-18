package fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.contact.R;

import java.util.ArrayList;

import helper.IntentHelper;
import jdo.Contact;



public class ContactFragment extends Fragment {

    private Context mContext;
    private ImageView mProfile_pic;
    private TextView mType, mContent, mContent_type, mContactName;
    private ImageButton mCallButton, mTextButton, mEmailButton;
    private ArrayList<String> mNumber, mNumber_type, mEmail, mEmail_type;
    private String mImg_url, mName;
    private LinearLayout mLinearLayout;
    private Contact mContact;
    private final String TAG = "ContactFragment";
    
    public ContactFragment(Contact pContact,Context pContext) {
        mContext = pContext;
        mContact = pContact;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "onCreate: ");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup item_view = (ViewGroup) inflater.inflate(R.layout.activity_contact_information, container, false);
        mProfile_pic = item_view.findViewById(R.id.profile_pic);
        mType = item_view.findViewById(R.id.type);
        mContactName = item_view.findViewById(R.id.contactname);
        mLinearLayout = item_view.findViewById(R.id.linear_layout_one);
        mNumber = new ArrayList<>();
        mNumber_type = new ArrayList<>();
        mEmail = new ArrayList<>();
        mEmail_type = new ArrayList<>();
        mCallButton = item_view.findViewById(R.id.callIcon);
        showContents();
        Log.d(TAG, "onCreateView: "+mContact.getName());
        return item_view;
    }

    
    public void showContents(){
        Log.d(TAG, "showContents: ");
        mImg_url = mContact.getImg_url();
        mName = mContact.getName();
        mNumber = mContact.getNumber();
        mNumber_type = mContact.getNumber_type();
        mEmail = mContact.getEmail();
        mEmail_type = mContact.getEmail_type();
        mContactName.setText(mName);

        Glide.with(getActivity()).load(mImg_url).placeholder(R.drawable.profileplaceholder).into(mProfile_pic);

        mType.setText("Phone");

        for (int i = 0; i < mNumber.size(); i++) {
            LayoutInflater inflater1 = LayoutInflater.from(mContext);
            View view = inflater1.inflate(R.layout.number_list, null);
            mLinearLayout.addView(view, 2);
            mContent = view.findViewById(R.id.content);
            mContent_type = view.findViewById(R.id.content_type);
            mCallButton = view.findViewById(R.id.callIcon);
            mTextButton = view.findViewById(R.id.textIcon);
            mCallButton.setVisibility(View.VISIBLE);
            mTextButton.setVisibility(View.VISIBLE);
            mContent.setText(mNumber.get(i));
            mContent_type.setText(mNumber_type.get(i));
            final int finalI = i;
            mCallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IntentHelper lCallIntent = new IntentHelper();
                    lCallIntent.callIntent(mNumber.get(finalI), mContext);
                }
            });
            mTextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IntentHelper lTextIntent = new IntentHelper();
                    lTextIntent.textIntent(mNumber.get(finalI), mContext);
                }
            });
        }


        for (int i = 0; i < mEmail.size(); i++) {
            LayoutInflater inflater2 = LayoutInflater.from(mContext);
            View view = inflater2.inflate(R.layout.number_list, null);
            mLinearLayout.addView(view);
            mContent = view.findViewById(R.id.content);
            mContent_type = view.findViewById(R.id.content_type);
            mEmailButton = view.findViewById(R.id.emailIcon);
            mEmailButton.setVisibility(View.VISIBLE);
            mContent.setText(mEmail.get(i));
            mContent_type.setText(mEmail_type.get(i));
            final int finalI = i;
            mEmailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IntentHelper lEmailIntent = new IntentHelper();
                    lEmailIntent.emailIntent(mEmail.get(finalI), mContext);
                }
            });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: "+mContact.getName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
