package com.santteam.apphenhosinhvien;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.FrameLayout;

import com.santteam.apphenhosinhvien.ChatFragment;
import com.santteam.apphenhosinhvien.RequestsFragment;

/**
 * Created by Admin on 11/10/2017.
 */

class MainSectionsPagerAdapter extends FragmentPagerAdapter{

    private String nameTab1;
    private String nameTab2;

    public MainSectionsPagerAdapter(FragmentManager fm,String nameTab1,String nameTab2) {
        super(fm);
        this.nameTab1 = nameTab1;
        this.nameTab2 = nameTab2;
    }

    @Override
    public Fragment getItem(int position) {
        if(this.nameTab1.equals("CHATS")) {
            switch (position) {
                case 0:
                    ChatFragment chatFragment = new ChatFragment();
                    return chatFragment;
                case 1:
                    RequestsFragment requestsFragment = new RequestsFragment();
                    return requestsFragment;
                default:
                    return null;
            }
        } else{
            switch (position) {
                case 0:
                    FriendsFragment friendsFragment = new FriendsFragment();
                    return friendsFragment;

                case 1:
                    RequestsFriendFragment requestsFriendFragmentt = new RequestsFriendFragment();
                    return requestsFriendFragmentt;
                default:
                    return null;
            }
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position){

        switch (position) {
            case 0:
                return this.nameTab1;

            case 1:
                return this.nameTab2;

            default:
                return null;
        }

    }

}
