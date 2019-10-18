package activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.example.contact.R;

import java.util.ArrayList;

import adapter.FragmentPagerAdpater;
import jdo.Contact;

public class ContactInfoActivity extends AppCompatActivity {

    private ViewPager mViewpager;
    private ArrayList<Contact> mContactArrayList;
    private FragmentPagerAdpater mAdapter;
    private Contact mSelectedContact;
    private boolean isleftSwipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        mViewpager = findViewById(R.id.view_pager);
        mContactArrayList = new ArrayList<>();
        Bundle intent = getIntent().getExtras();

        mContactArrayList = (ArrayList<Contact>) intent.getSerializable("contactArrayList");
        // mPosition=intent.getInt("position");

        mAdapter = new FragmentPagerAdpater(ContactInfoActivity.this, mContactArrayList);
        mViewpager.setAdapter(mAdapter);

        Contact contact = (Contact) intent.getSerializable("ContactObject");
        mContactArrayList.indexOf(contact);
        mViewpager.setCurrentItem(mContactArrayList.indexOf(contact));

        LocalBroadcastManager.getInstance(ContactInfoActivity.this).registerReceiver(lBroadcastResponse,
                new IntentFilter("UpdatedPositionIntent"));

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Intent localBroadcastIntent = new Intent("pagerPositionIntent");
                if (position == mContactArrayList.size() - 2) {
                    localBroadcastIntent.putExtra("contactObject", mContactArrayList.get(mContactArrayList.size() - 1));
                    localBroadcastIntent.putExtra("Swipe", false);
                    LocalBroadcastManager.getInstance(ContactInfoActivity.this).sendBroadcast(localBroadcastIntent);
                    isleftSwipe = false;
                } else if (position < 2) {
                    localBroadcastIntent.putExtra("contactObject", mContactArrayList.get(0));
                    localBroadcastIntent.putExtra("Swipe", true);
                    LocalBroadcastManager.getInstance(ContactInfoActivity.this).sendBroadcast(localBroadcastIntent);
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
            if (isleftSwipe) {
                mContactArrayList.addAll(0, (ArrayList<Contact>) bundle.getSerializable("UpdatedList"));
                mViewpager.setCurrentItem(mContactArrayList.indexOf(mSelectedContact), false);
            } else {
                mContactArrayList.addAll((ArrayList<Contact>) bundle.getSerializable("UpdatedList"));
            }
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(ContactInfoActivity.this).unregisterReceiver(lBroadcastResponse);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("contactObject", mSelectedContact);
        setResult(RESULT_OK, intent);
        finishActivity(1);
    }
}