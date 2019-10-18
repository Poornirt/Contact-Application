package activity;

import android.app.SharedElementCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.example.contact.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adapter.FragmentstatePagerAdapter;
import fragment.ContactFragment;
import jdo.Contact;

public class SwipeableContactActivity extends FragmentActivity {

    private ArrayList<Contact> mContactArrayList;
    private FragmentstatePagerAdapter fragmentstatePagerAdapter;
    private ViewPager mViewpager;
    private Contact mSelectedContact;
    private boolean isleftSwipe;
    private ArrayList<ContactFragment> lFragmentlist;
    public static int currentPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        mViewpager = findViewById(R.id.view_pager);
        mViewpager.setOffscreenPageLimit(5);
        mContactArrayList = new ArrayList<>();
        Bundle intent = getIntent().getExtras();
        mContactArrayList = (ArrayList<Contact>) intent.getSerializable("contactArrayList");
        Contact contact = (Contact) intent.getSerializable("ContactObject");


        lFragmentlist = new ArrayList<>();
        // Get it from List
        for (Contact lContact : mContactArrayList) {
            ContactFragment lContactFragment = new ContactFragment(lContact, SwipeableContactActivity.this);
            // lContactFragment.setArguments(new Bundle());
            lFragmentlist.add(lContactFragment);
        }
        fragmentstatePagerAdapter = new FragmentstatePagerAdapter(getSupportFragmentManager(), lFragmentlist);
        mViewpager.setAdapter(fragmentstatePagerAdapter);

        mContactArrayList.indexOf(contact);
        mViewpager.setCurrentItem(mContactArrayList.indexOf(contact));

        LocalBroadcastManager.getInstance(SwipeableContactActivity.this).registerReceiver(lBroadcastResponse, new
                IntentFilter("UpdatedPositionIntent"));

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition=position;
                Intent localBroadcastIntent = new Intent("pagerPositionIntent");
                if (position == mContactArrayList.size() - 2) {
                    localBroadcastIntent.putExtra("contactObject", mContactArrayList.get(mContactArrayList.size() - 1));
                    localBroadcastIntent.putExtra("Swipe", false);
                    LocalBroadcastManager.getInstance(SwipeableContactActivity.this).sendBroadcast(localBroadcastIntent);
                    isleftSwipe = false;
                } else if (position < 2) {
                    localBroadcastIntent.putExtra("contactObject", mContactArrayList.get(0));
                    localBroadcastIntent.putExtra("Swipe", true);
                    LocalBroadcastManager.getInstance(SwipeableContactActivity.this).sendBroadcast(localBroadcastIntent);
                    isleftSwipe = true;
                }
                mSelectedContact = mContactArrayList.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }

    BroadcastReceiver lBroadcastResponse = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            ArrayList<Contact> updatedList = (ArrayList<Contact>) bundle.getSerializable("UpdatedList");
            ArrayList<ContactFragment> lfragmentlist=new ArrayList<>();
            if (isleftSwipe) {
                mContactArrayList.addAll(0,updatedList);
                for (Contact lContact : mContactArrayList) {
                    ContactFragment lContactFragment = new ContactFragment(lContact, SwipeableContactActivity.this);
                    lfragmentlist.add(lContactFragment);
                }
                lFragmentlist.clear();
                lFragmentlist.addAll(0,lfragmentlist);
                fragmentstatePagerAdapter.notifyDataSetChanged();
                mViewpager.setCurrentItem(mContactArrayList.indexOf(mSelectedContact),false);
            } else {
                for (Contact lContact : updatedList) {
                    ContactFragment lContactFragment = new ContactFragment(lContact, SwipeableContactActivity.this);
                    lFragmentlist.add(lContactFragment);
                    fragmentstatePagerAdapter.notifyDataSetChanged();
                }
                mContactArrayList.addAll(updatedList);
                mViewpager.setCurrentItem(mContactArrayList.indexOf(mSelectedContact),false);
            }


        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(SwipeableContactActivity.this).unregisterReceiver(lBroadcastResponse);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
            Intent intent = new Intent();
            intent.putExtra("contactObject", mSelectedContact);
            setResult(RESULT_OK, intent);
            finishActivity(1);

    }

}
