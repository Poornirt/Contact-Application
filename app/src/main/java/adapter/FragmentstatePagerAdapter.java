package adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import fragment.ContactFragment;

public class FragmentstatePagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<ContactFragment> mContactArrayList;

    public FragmentstatePagerAdapter(FragmentManager fm, ArrayList<ContactFragment> pContactArrayList) {
        super(fm);
        this.mContactArrayList = pContactArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        return mContactArrayList.get(position);
    }

    @Override
    public int getCount() {
        return mContactArrayList.size();
    }
}
