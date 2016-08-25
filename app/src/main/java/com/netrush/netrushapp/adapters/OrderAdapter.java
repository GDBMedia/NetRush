package com.netrush.netrushapp.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.netrush.netrushapp.R;
import com.netrush.netrushapp.models.Order;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Garrett on 8/17/2016.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    public final String TAG = this.getClass().getSimpleName();
    private ArrayList<Order> mOrderArrayList = new ArrayList<>();
    private Context mContext;


    public OrderAdapter(Context context, ArrayList<Order> orderArrayList) {
        mContext = context;
        mOrderArrayList = orderArrayList;
    }

    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        OrderViewHolder viewHolder = new OrderViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OrderAdapter.OrderViewHolder holder, int position) {
        holder.bindOrder(mOrderArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return mOrderArrayList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.card_view) CardView cv;
        @Bind(R.id.titleTextView) TextView mTitle;
        @Bind(R.id.dateTextView) TextView mdate;
        @Bind(R.id.productimg) ImageView mImage;

        private Context mContext;

        public OrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mContext = itemView.getContext();
            cv.setOnClickListener(this);
        }

        public void bindOrder(Order order) {
            String title = order.getTitle();
            if(order.getTitle().length() > 30){
                title = order.getTitle().substring(0, 30) + mContext.getString(R.string.elip);
            }
            Picasso.with(mContext).load(order.getImageUrl()).into(mImage);
            mTitle.setText(title);
            mdate.setText(mContext.getString(R.string.last_ordered) + order.getDate());

        }

        @Override
        public void onClick(View view) {
            if (view == cv) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                final View passwordDialog = inflater.inflate(R.layout.password_dialog_layout, null);

                final EditText userAmazonPassword = (EditText) passwordDialog.findViewById(R.id.userPassword);

                final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("Confirm with Amazon Password");
                alert.setView(passwordDialog);
                alert.setCancelable(true);
                final AlertDialog dialog = alert.create();
                dialog.show();

                Button cancel = (Button) passwordDialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.hide();
                    }
                });

                Button confirm = (Button) passwordDialog.findViewById(R.id.confirm);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = userAmazonPassword.getText().toString().trim();
                        // TODO Webcrawler to purchase item goes here ---------------------------------------------------------
                        Toast.makeText(mContext, "Your password is " + password, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
