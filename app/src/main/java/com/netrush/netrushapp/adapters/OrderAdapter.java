package com.netrush.netrushapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.netrush.netrushapp.R;
import com.netrush.netrushapp.models.Order;
import com.netrush.netrushapp.ui.ProductListActivity;
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
    private int itemNum = 1;
    public Boolean itemInCart = false;
    private static final int TYPE_FULL = 0;
    private static final int TYPE_HALF = 1;
    private int lastPosition = -1;

    public OrderAdapter(Context context, ArrayList<Order> orderArrayList) {
        mContext = context;
        mOrderArrayList = orderArrayList;
    }

    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final int[] cutoff = {20};
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int length = 20;
                final int type = viewType;
                final ViewGroup.LayoutParams lp = view.getLayoutParams();
                if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
                    switch (type) {
                        case TYPE_FULL:
                            sglp.setFullSpan(true);
                            length = 30;
                            break;
                        case TYPE_HALF:
                            sglp.setFullSpan(false);
                            sglp.width = view.getWidth();
                            length = 20;
                            break;
                    }
                    view.setLayoutParams(sglp);
                    final StaggeredGridLayoutManager lm = (StaggeredGridLayoutManager) ((RecyclerView) parent).getLayoutManager();
                    lm.invalidateSpanAssignments();
                }
                cutoff[0] = length;
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        OrderViewHolder viewHolder = new OrderViewHolder(view, viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OrderAdapter.OrderViewHolder holder, int position) {
        holder.bindOrder(mOrderArrayList.get(position));
        setAnimation(holder.cv, position);
    }

    private void setAnimation(CardView cv, int position) {
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.item_animate);
            cv.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Order order = mOrderArrayList.get(position);
        if(Integer.valueOf(order.getQuantity()) > 3){
            return TYPE_FULL;
        }
        return TYPE_HALF;

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
        private int mViewType;

        public OrderViewHolder(View itemView, int viewType) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mViewType = viewType;
            mContext = itemView.getContext();
            cv.setOnClickListener(this);
        }

        public void bindOrder(Order order) {
            final int itemPosition = getLayoutPosition();
            int cutoff;
            switch (mViewType){
                case TYPE_FULL:
                    cutoff = 35;
                    break;
                case TYPE_HALF:
                    cutoff = 20;
                    break;
                default:
                    cutoff = 20;
                    break;
            }
            cv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    final View productDetails = inflater.inflate(R.layout.product_details, null);
                    final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    alert.setView(productDetails);
                    alert.setCancelable(true);
                    final AlertDialog dialog = alert.create();
                    dialog.show();
                    return false;
                }
            });
            if(ProductListActivity.mAsins.contains(mOrderArrayList.get(itemPosition).getAsin())){
                setClicked();
            }else{
                setUnClicked();
            }
            String title = order.getTitle();
            if(order.getTitle().length() > cutoff){
                title = order.getTitle().substring(0, cutoff) + mContext.getString(R.string.elip);
            }
            Picasso.with(mContext).load(order.getImageUrl()).into(mImage);
            mTitle.setText(title);
            mdate.setText(mContext.getString(R.string.last_ordered) + order.getDate());
        }

        private void setUnClicked() {
            cv.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardview_light_background));
            mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            mdate.setTextColor(ContextCompat.getColor(mContext, R.color.divider));
        }

        private void setClicked() {
            cv.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardview_dark_background));
            mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.cardview_light_background));
            mdate.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getLayoutPosition();
            if(ProductListActivity.mAsins.contains(mOrderArrayList.get(itemPosition).getAsin())){
               setUnClicked();
                ProductListActivity.mAsins.remove( mOrderArrayList.get(itemPosition).getAsin());
            }else{
                setClicked();
                ProductListActivity.mAsins.add( mOrderArrayList.get(itemPosition).getAsin());
            }

            ProductListActivity.setButtonVisibility();

        }
    }
}
