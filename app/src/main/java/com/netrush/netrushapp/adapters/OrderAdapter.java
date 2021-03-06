package com.netrush.netrushapp.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.RequestId;
import com.netrush.netrushapp.Constants;
import com.netrush.netrushapp.R;
import com.netrush.netrushapp.models.Order;
import com.netrush.netrushapp.ui.ProductListActivity;
import com.netrush.netrushapp.utils.DateHelper;
import com.netrush.netrushapp.utils.MarginHelpers;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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

    private int mLastPosition = -1;
    private int mFullCardCount = Constants.ZERO;

    public OrderAdapter(Context context, ArrayList<Order> orderArrayList) {
        mContext = context;
        mOrderArrayList = orderArrayList;
    }

    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                final int type = viewType;
                final ViewGroup.LayoutParams lp = view.getLayoutParams();
                if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
                    switch (type) {
                        case Constants.TYPE_FULL:
                            sglp.setFullSpan(true);
                            break;
                        case Constants.TYPE_HALF:
                            sglp.setFullSpan(false);
                            sglp.width = view.getWidth();
                            break;
                    }
                    view.setLayoutParams(sglp);
                    final StaggeredGridLayoutManager lm = (StaggeredGridLayoutManager) ((RecyclerView) parent).getLayoutManager();
                    lm.invalidateSpanAssignments();
                }
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
        if (position > mLastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.item_animate);
            cv.startAnimation(animation);
            mLastPosition = position;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Order order = mOrderArrayList.get(position);
        if(Integer.valueOf(order.getQuantity()) > 3){
            return Constants.TYPE_FULL;
        }
        return Constants.TYPE_HALF;
    }

    @Override
    public int getItemCount() {
        return mOrderArrayList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.card_view) CardView cv;
        @Bind(R.id.titleTextView) TextView mTitle;
        @Bind(R.id.productimg) ImageView mImage;
        private ImageView mProductDetailImage;
        private TextView mLastPurchaseDate;
        private TextView mCurrentPriceDisplay;
        private TextView mProductDetailTitle;
        private final int itemMargin;
        private LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        private Context mContext;
        private int mViewType;

        public OrderViewHolder(View itemView, int viewType) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mViewType = viewType;
            mContext = itemView.getContext();
            cv.setOnClickListener(this);
            itemMargin = (int) mContext.getResources().getDimension(R.dimen.itemMargin);
        }

        public void bindOrder(final Order order) {
            int itemPosition = getLayoutPosition();
            int cutoff;

            switch (mViewType){
                case Constants.TYPE_FULL:
                    mFullCardCount++;
                    Picasso.with(mContext).load(order.getImageUrl()).resize(Constants.TARGET_WIDTH, Constants.TARGET_HEIGHT).centerInside().into(mImage);
                    cutoff = Constants.LONG_CUTOFF;
                    break;
                case Constants.TYPE_HALF:
                    Picasso.with(mContext).load(order.getImageUrl()).into(mImage);
                    cutoff = Constants.SHORT_CUTOFF;
                    break;
                default:
                    cutoff = Constants.SHORT_CUTOFF;
                    break;
            }
            cv.setLayoutParams(MarginHelpers.setMarginOfStaggeredCards(layoutParams,mViewType,itemPosition,mFullCardCount,mOrderArrayList.size(),itemMargin));
            cv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(Constants.VIBRATOR_LENGTH);

                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    final View productDetails = inflater.inflate(R.layout.product_details, null);
                    final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    String unitPrice = Double.toString(order.getUnitprice());

                    mProductDetailImage = (ImageView) productDetails.findViewById(R.id.productDetailImage);
                    mLastPurchaseDate = (TextView) productDetails.findViewById(R.id.lastPurchasedDateDisplay);
                    mCurrentPriceDisplay = (TextView) productDetails.findViewById(R.id.currentPriceDisplay);
                    mProductDetailTitle = (TextView) productDetails.findViewById(R.id.productDetailTitle);
                    Picasso.with(mContext).load(order.getImageUrl()).resize(Constants.TARGET_WIDTH, Constants.TARGET_HEIGHT).centerInside().into(mProductDetailImage);
                    mProductDetailImage.setBackgroundColor(ContextCompat.getColor(mContext, R.color.cardview_light_background));
                    mLastPurchaseDate.setText(DateHelper.formatDate(order.getDate(), Constants.DATE_FORMAT_SOURCE, mContext));
                    mCurrentPriceDisplay.setText(mContext.getString(R.string.dollar) + unitPrice);

                    String shortenedProductTitle = order.getTitle().substring(Constants.ZERO, Constants.PRODUCT_DETAIL_TITLE_CUTOFF) + mContext.getString(R.string.elip);
                    if (order.getTitle().length() > Constants.PRODUCT_DETAIL_TITLE_CUTOFF) {
                        mProductDetailTitle.setText(shortenedProductTitle);
                    } else {
                        mProductDetailTitle.setText(order.getTitle());
                    }


                    alert.setView(productDetails);
                    alert.setCancelable(true);
                    
                    final AlertDialog dialog = alert.create();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    dialog.show();
                    return false;
                }
            });
            if(ProductListActivity.mAsins.contains(mOrderArrayList.get(itemPosition).getAsin())){
                setClicked();
            }
            String title = order.getTitle();
            if(order.getTitle().length() > cutoff){
                title = order.getTitle().substring(Constants.ZERO, cutoff) + mContext.getString(R.string.elip);
            }
            mTitle.setText(title);
        }

        private void setUnClicked() {
            ProductListActivity.setButtonVisibility(Constants.FADE_OUT_TYPE);
            cv.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardview_light_background));
            mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        }

        private void setClicked() {
            ProductListActivity.setButtonVisibility(Constants.FADE_IN_TYPE);
            mImage.setBackgroundColor(ContextCompat.getColor(mContext, R.color.cardview_light_background));
            cv.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cardview_dark_background));
            mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.cardview_light_background));
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getLayoutPosition();
            if(ProductListActivity.mAsins.contains(mOrderArrayList.get(itemPosition).getAsin())){
                ProductListActivity.mAsins.remove( mOrderArrayList.get(itemPosition).getAsin());
                setUnClicked();
            }else{
                ProductListActivity.mAsins.add( mOrderArrayList.get(itemPosition).getAsin());
                setClicked();
            }



        }
    }
}
