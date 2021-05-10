package com.haylion.android.user.wallet;

import android.content.Context;
import android.media.Image;
import android.widget.ImageView;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseItemView;
import com.haylion.android.data.model.Transaction;

import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class TransactionListItemView extends BaseItemView<Transaction> {

    @BindView(R.id.type)
    ImageView typeIcon;
    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.date)
    TextView dateView;
    @BindView(R.id.amount)
    TextView amountView;

    public TransactionListItemView(Context context) {
        super(context);
    }

    @Override
    protected int getOrientation() {
        return RecyclerView.VERTICAL;
    }

    @Override
    public int getLayoutId() {
        return R.layout.transaction_list_item;
    }

    @Override
    public void bind(Transaction transaction) {
        if (transaction.getType() == Transaction.TYPE_INCOME) {
            typeIcon.setImageResource(R.mipmap.ic_wallet_income);
            titleView.setText("收入");
            amountView.setText(String.format(Locale.getDefault(), "+%.1f", transaction.getAmount()));
        } else if (transaction.getType() == Transaction.TYPE_WITHDRAW) {
            typeIcon.setImageResource(R.mipmap.ic_wallet_withdraw);
            titleView.setText("提现");
            amountView.setText(String.format(Locale.getDefault(), "-%.1f", transaction.getAmount()));
        } else {
            typeIcon.setImageResource(0);
            titleView.setText("未知");
            amountView.setText("-");
        }
        dateView.setText(transaction.getDate());
    }

}
