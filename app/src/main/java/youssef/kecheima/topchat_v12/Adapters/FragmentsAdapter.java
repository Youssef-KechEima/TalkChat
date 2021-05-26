package youssef.kecheima.topchat_v12.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import youssef.kecheima.topchat_v12.Fragments.CallFragment;
import youssef.kecheima.topchat_v12.Fragments.ChatFragment;
import youssef.kecheima.topchat_v12.Fragments.RequestFragment;
import youssef.kecheima.topchat_v12.Fragments.UserFragment;

public class FragmentsAdapter extends FragmentStatePagerAdapter {
    private int tabCount;
    public FragmentsAdapter(FragmentManager fragmentManager, int countTabs){
        super(fragmentManager);
        this.tabCount=countTabs;

    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ChatFragment();
            case 1:
                return new UserFragment();
            case 2:
                return new RequestFragment();
            case 3:
               return new CallFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
