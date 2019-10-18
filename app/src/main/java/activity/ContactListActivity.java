package activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contact.R;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;

import adapter.RecyclerViewAdapter;
import database.ContactTable;
import helper.ContactsFromContentProvider;
import helper.IntentHelper;
import jdo.Contact;
import listener.PublishProgressListener;
import listener.RecyclerClickListener;

import static constants.Constants.IS_DATA_FETCHED;
import static constants.Constants.PREFERENCE_NAME;

public class ContactListActivity extends AppCompatActivity implements PublishProgressListener, SearchView.OnQueryTextListener {


    private boolean mIsFetched = false;
    private LinearLayoutManager mLinearlayoutManager;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private TextView mProgressvalue, mFetchcontacts;
    private ProgressBar mProgressBar;
    private AlertDialog mAlertDialog;
    private AlertDialog.Builder mAlertDialogBuilder;
    private ArrayList<Contact> mContactArrayList;
    private ArrayList<Contact> mDuplicateArrayList;
    private ArrayList<Contact> lSubList;
    private int mStartOfList, mIndexOfList;
    private final int REQUEST_CODE_FOR_PERMISSION = 1001;
    private final int REQUEST_CODE_FOR_lIST = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        Stetho.initializeWithDefaults(this);
        mRecyclerView = findViewById(R.id.recycler_view);
        mContactArrayList = new ArrayList<>();
        mDuplicateArrayList = new ArrayList<>();
        mLinearlayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mLinearlayoutManager.setOrientation(RecyclerView.VERTICAL);
        mAlertDialogBuilder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View viewRoot = inflater.inflate(R.layout.progress_layout, null);
        mProgressvalue = viewRoot.findViewById(R.id.progress_value);
        mFetchcontacts = viewRoot.findViewById(R.id.fetch_contacts);
        mProgressBar = viewRoot.findViewById(R.id.progress_horizontal);
        mAlertDialogBuilder.setView(viewRoot);
        SharedPreferences lSharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mIsFetched = lSharedPreferences.getBoolean(IS_DATA_FETCHED, false);


        if (mIsFetched) {
            approveToAccessContacts();
        } else if (requestPermissionToReadContacts()) {
            approveToAccessContacts();
        }


        LocalBroadcastManager.getInstance(ContactListActivity.this).registerReceiver(mBroadcastreceiver, new IntentFilter("pagerPositionIntent"));

        mRecyclerView.addOnItemTouchListener(new RecyclerClickListener(ContactListActivity.this, mRecyclerView,
                new RecyclerClickListener.onItemClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(View pView, final int mPosition) {
                        int lPosition = mPosition;
                        mStartOfList = lPosition - 5;
                        if (mStartOfList < 0) {
                            mStartOfList = 0;
                        }
                        mIndexOfList = lPosition + 5;
                        if (mIndexOfList >= mDuplicateArrayList.size()) {
                            mIndexOfList = mDuplicateArrayList.size();
                        }
                        Intent intent = new Intent(ContactListActivity.this, SwipeableContactActivity.class);
                        lSubList = new ArrayList<>(mDuplicateArrayList.subList(mStartOfList, mIndexOfList));
                        intent.putExtra("contactArrayList", lSubList);
                        intent.putExtra("ContactObject", mDuplicateArrayList.get(mPosition));
                        ImageView imageView = pView.findViewById(R.id.image);
                        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ContactListActivity.this,
                                imageView, "profilePic");
                        //startActivity(intent,activityOptionsCompat.toBundle());
                        ActivityCompat.startActivityForResult(ContactListActivity.this, intent, REQUEST_CODE_FOR_lIST, activityOptionsCompat.toBundle());
                    }

                    @Override
                    public void onLongPress(final View pView, final int pPosition) {
                        AlertDialog.Builder alert_builder = new AlertDialog.Builder(ContactListActivity.this);
                        alert_builder.setMessage("Delete Contact?");
                        alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ContactTable contactTable = new ContactTable(ContactListActivity.this);
                                contactTable.deleteContact(mDuplicateArrayList.get(pPosition));
                                mDuplicateArrayList.remove(pPosition);
                                mRecyclerView.removeView(pView);
                                mRecyclerView.getAdapter().notifyItemRemoved(pPosition);
                                mRecyclerView.getAdapter().notifyDataSetChanged();
                            }
                        })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create().show();
                    }
                }));

        ItemTouchHelper.Callback lCallback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView pRecyclerView, @NonNull RecyclerView.ViewHolder pViewHolder) {
                int lFalg = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(0, lFalg);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView pRecyclerView, @NonNull RecyclerView.ViewHolder pViewHolder, @NonNull RecyclerView.ViewHolder pTarget) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder pViewHolder, int pDirection) {
                if (pDirection == ItemTouchHelper.LEFT) {
                    mRecyclerView.getAdapter().notifyItemChanged(pViewHolder.getAdapterPosition());
                    if (mDuplicateArrayList.get(pViewHolder.getAdapterPosition()).getNumber().size() > 1) {
                        AlertDialog.Builder lNumber_builder = new AlertDialog.Builder(ContactListActivity.this);
                        lNumber_builder.setTitle("choose one");
                        int lSize_of_number_list = mDuplicateArrayList.get(pViewHolder.getAdapterPosition()).getNumber().size();
                        final String[] lNumber_list = new String[lSize_of_number_list];
                        for (int i = 0; i < lSize_of_number_list; i++) {
                            lNumber_list[i] = mDuplicateArrayList.get(pViewHolder.getAdapterPosition()).getNumber().iterator().next();
                        }
                        lNumber_builder.setSingleChoiceItems(lNumber_list, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                IntentHelper lCallIntent = new IntentHelper();
                                lCallIntent.callIntent(lNumber_list[i], ContactListActivity.this);
                            }
                        }).create().show();
                    } else {
                        IntentHelper lCallIntent = new IntentHelper();
                        lCallIntent.callIntent(String.valueOf(mDuplicateArrayList.get(pViewHolder.getAdapterPosition()).getNumber()), ContactListActivity.this);
                    }
                } else if (pDirection == ItemTouchHelper.RIGHT) {
                    mRecyclerView.getAdapter().notifyItemChanged(pViewHolder.getAdapterPosition());
                    if (mDuplicateArrayList.get(pViewHolder.getAdapterPosition()).getNumber().size() > 1) {
                        AlertDialog.Builder lNumber_builder = new AlertDialog.Builder(ContactListActivity.this);
                        lNumber_builder.setTitle("choose one");
                        int lSize_of_number_list = mDuplicateArrayList.get(pViewHolder.getAdapterPosition()).getNumber().size();
                        final String[] lNumber_list = new String[lSize_of_number_list];
                        for (int i = 0; i < lSize_of_number_list; i++) {
                            lNumber_list[i] = mDuplicateArrayList.get(pViewHolder.getAdapterPosition()).getNumber().iterator().next();
                        }
                        lNumber_builder.setSingleChoiceItems(lNumber_list, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                IntentHelper lSmsIntent = new IntentHelper();
                                lSmsIntent.textIntent(lNumber_list[i], ContactListActivity.this);
                            }
                        }).create().show();
                    } else {
                        IntentHelper lSmsIntent = new IntentHelper();
                        lSmsIntent.textIntent(String.valueOf(mDuplicateArrayList.get(pViewHolder.getAdapterPosition()).getNumber()), ContactListActivity.this);
                    }
                }
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(lCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_information, menu);
        SearchView lSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        lSearchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String pNewtext) {
        ArrayList<Contact> lSuggestion_list = new ArrayList<>();
        lSuggestion_list = getAutoCompleteText(pNewtext);
        mDuplicateArrayList.clear();
        mDuplicateArrayList.addAll(lSuggestion_list);
        mAdapter.notifyDataSetChanged();
        return true;
    }

    private ArrayList<Contact> getAutoCompleteText(String pNewtext) {
        ArrayList<Contact> lSuggestion_list = new ArrayList<>();
        for (Contact lContact : mContactArrayList) {
            if (lContact.getName().toLowerCase().contains(pNewtext.toLowerCase())) {
                lSuggestion_list.add(lContact);
            }
        }
        return lSuggestion_list;
    }


    private boolean requestPermissionToReadContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_FOR_PERMISSION);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_FOR_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                approveToAccessContacts();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(ContactListActivity.this, "denied", Toast.LENGTH_SHORT).show();
                    showPopup();
                } else {
                    alertDialogBuilder("To show Contacts allow contact access" +
                                    "Tap Settings > Permissions and turn Contact on.", "Settings",
                            "Not Now");
                }
            }
        }
    }

    private void alertDialogBuilder(String pTitleMessage, String pPositiveMessage, String pNegativeMessage) {

        new AlertDialog.Builder(this).setMessage(pTitleMessage)
                .setNegativeButton(pNegativeMessage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        showPopup();
                    }
                })
                .setPositiveButton(pPositiveMessage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        settingsIntent();
                    }
                }).create().show();
    }


    private void showPopup() {
        new AlertDialog.Builder(ContactListActivity.this).setMessage("To access contacts please grant " +
                "permission").setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                settingsIntent();
            }
        }).setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showPopup();
            }
        }).create().show();
    }


    private void settingsIntent() {
        Intent lIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package",
                        getPackageName(), null));
        lIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        lIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(lIntent, REQUEST_CODE_FOR_PERMISSION);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_FOR_PERMISSION) {
                if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    approveToAccessContacts();
                } else {
                    showPopup();
                }
            } else if (requestCode == REQUEST_CODE_FOR_lIST) {
                final Contact contact = (Contact) data.getSerializableExtra("contactObject");
                mRecyclerView.scrollToPosition(mDuplicateArrayList.indexOf(contact));
            }
        }
    }


    private void approveToAccessContacts() {
        if (mIsFetched) {
            new FetchContactFromDatabaseTask().execute();
        } else {
            new FetchDataFromContentProviderTask().execute();
        }
    }


    @Override
    public void publishProgressUpdate(int pCursor_position, int pProgress, int pTotal_Count) {
        FetchDataFromContentProviderTask lFetchDataFromContentProviderTask = new FetchDataFromContentProviderTask();
        lFetchDataFromContentProviderTask.publishProgressUpdate(pCursor_position, pProgress, pTotal_Count);
    }


    /**
     *
     */
    private class FetchDataFromContentProviderTask extends AsyncTask<Integer, Integer, ArrayList<Contact>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressvalue.setVisibility(View.VISIBLE);
            mFetchcontacts.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mAlertDialog = mAlertDialogBuilder.create();
            mAlertDialog.show();
        }


        public void publishProgressUpdate(int pCursor_position, int pProgress, int pTotal_count) {
            publishProgress(pCursor_position, pProgress, pTotal_count);
        }

        @Override
        protected void onProgressUpdate(Integer... pValues) {
            super.onProgressUpdate(pValues);
            mProgressBar.setProgress(pValues[1]);
            mProgressvalue.setText("" + pValues[0] + "/" + "" + pValues[2]);
        }


        @Override
        protected ArrayList<Contact> doInBackground(Integer... pStrings) {
            return new ContactsFromContentProvider(ContactListActivity.this).fetchContactsFromContentProvider();
        }


        @Override
        protected void onPostExecute(ArrayList<Contact> pContactArrayList) {
            super.onPostExecute(pContactArrayList);
            mFetchcontacts.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mProgressvalue.setVisibility(View.GONE);
            mAlertDialog.dismiss();
            mContactArrayList.addAll(pContactArrayList);
            mDuplicateArrayList.addAll(mContactArrayList);
            mAdapter = new RecyclerViewAdapter(ContactListActivity.this, mDuplicateArrayList);
            mRecyclerView.setLayoutManager(mLinearlayoutManager);
            mRecyclerView.setAdapter(mAdapter);
        }

    }

    private class FetchContactFromDatabaseTask extends AsyncTask<String, String, ArrayList<Contact>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ViewGroup layout = (ViewGroup) findViewById(android.R.id.content).getRootView();
            mProgressBar = new ProgressBar(ContactListActivity.this, null, android.R.attr.progressBarStyleLarge);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            RelativeLayout lRelativeLayout = new RelativeLayout(ContactListActivity.this);
            lRelativeLayout.setGravity(Gravity.CENTER);
            lRelativeLayout.addView(mProgressBar);
            layout.addView(lRelativeLayout, params);
        }

        @Override
        protected ArrayList<Contact> doInBackground(String... pStrings) {
            try {
                return new ContactTable(ContactListActivity.this).fetchContactsFromDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Contact> pContactArrayList) {
            super.onPostExecute(pContactArrayList);
            mProgressBar.setVisibility(View.GONE);
            mContactArrayList.addAll(pContactArrayList);
            mDuplicateArrayList.addAll(mContactArrayList);
            mAdapter = new RecyclerViewAdapter(ContactListActivity.this, mDuplicateArrayList);
            mRecyclerView.setLayoutManager(mLinearlayoutManager);
            mRecyclerView.setAdapter(mAdapter);
        }
    }


    BroadcastReceiver mBroadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                boolean swipe = intent.getBooleanExtra("Swipe", false);
                Bundle bundle = intent.getExtras();
                Contact contact = (Contact) bundle.getSerializable("contactObject");
                //Log.d("indexof", String.valueOf(mDuplicateArrayList.lastIndexOf(contact)));
                mIndexOfList = mDuplicateArrayList.indexOf(contact);
                if (swipe) {
                    int lPreviousContacts = mIndexOfList - 10;
                    if (lPreviousContacts < 0) {
                        lPreviousContacts = 0;
                    }
                    lSubList = new ArrayList<>(mDuplicateArrayList.subList(lPreviousContacts, mIndexOfList));
                } else {
                    int lFollowingContacts = mIndexOfList + 10;
                    if (lFollowingContacts > mDuplicateArrayList.size() - 1) {
                        lFollowingContacts = mDuplicateArrayList.size() - 1;
                    }
                    lSubList = new ArrayList<>(mDuplicateArrayList.subList(mIndexOfList, lFollowingContacts));
                }

                Intent broadcastResponse = new Intent("UpdatedPositionIntent");
                broadcastResponse.putExtra("UpdatedList", lSubList);
                LocalBroadcastManager.getInstance(ContactListActivity.this).sendBroadcast(broadcastResponse);
            }

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(ContactListActivity.this).unregisterReceiver(mBroadcastreceiver);
    }


}

