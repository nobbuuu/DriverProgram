package com.haylion.android.user.wallet;

import android.content.Context;

import com.haylion.android.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class WalletTabAdapter extends FragmentPagerAdapter {

    private final WalletPage[] mPages;

    WalletTabAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm);
        mPages = buildWalletPage(context);
    }

    private WalletPage[] buildWalletPage(Context context) {
        return new WalletPage[]{
                new WalletPage(TransactionListFragment.TYPE_ALL,
                        context.getString(R.string.tab_wallet_all)),
                new WalletPage(TransactionListFragment.TYPE_INCOME,
                        context.getString(R.string.tab_wallet_income)),
                new WalletPage(TransactionListFragment.TYPE_EXPENSE,
                        context.getString(R.string.tab_wallet_expense))
        };
    }

    private static class WalletPage {

        private int type;
        private String title;

        private WalletPage(int type, String title) {
            this.type = type;
            this.title = title;
        }
    }

    @Override
    public int getCount() {
        if (mPages != null) {
            return mPages.length;
        }
        return 0;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return TransactionListFragment.newInstance(mPages[position].type);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mPages[position].title;
    }


}
