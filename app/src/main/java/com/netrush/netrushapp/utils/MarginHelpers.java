package com.netrush.netrushapp.utils;

import android.widget.LinearLayout;

import com.netrush.netrushapp.R;

/**
 * Created by Garrett on 9/6/2016.
 */


public class MarginHelpers {
    private static final int TYPE_FULL = 0;
    private static final int TYPE_HALF = 1;
    private static int itemMargin;
    private static int itemMarginHalf;
    private static int mArraySize;
    private static LinearLayout.LayoutParams mLayoutParams;
    private static int mFullCardCount;


    public static LinearLayout.LayoutParams setMarginOfStaggeredCards(LinearLayout.LayoutParams layoutParams, int type, int itemPosistion, int fullCardCount, int arraySize, int margin){
        mLayoutParams = layoutParams;
        mArraySize = arraySize;
        itemMargin = margin;
        itemMarginHalf = margin/2;
        mFullCardCount = fullCardCount;
        switch (type){
            case TYPE_FULL:
                return setFullCard(itemPosistion);
            case TYPE_HALF:
                int visualItemPosition = itemPosistion + fullCardCount;
                return setHalfCard(visualItemPosition);
        }
        return null;
    }

    private static LinearLayout.LayoutParams setFullCard(int itemPosition) {
        if (itemPosition == 0){
            mLayoutParams.setMargins(itemMargin, itemMargin, itemMargin, itemMarginHalf);
            return mLayoutParams;
        }else if(itemPosition == mArraySize-1){
            mLayoutParams.setMargins(itemMargin, itemMarginHalf, itemMargin, itemMargin);
            return mLayoutParams;
        }else{
            mLayoutParams.setMargins(itemMargin, itemMarginHalf, itemMargin, itemMarginHalf);
            return mLayoutParams;
        }
    }

    private static LinearLayout.LayoutParams setHalfCard(int visualitemPosition) {
        if(visualitemPosition%2 == 0){
            return setLeftCard(visualitemPosition);
        }else{
            return setRightCard(visualitemPosition);
        }
    }

    private static LinearLayout.LayoutParams setRightCard(int itemPosition) {
        if(itemPosition == 0){
            mLayoutParams.setMargins(itemMarginHalf, itemMargin, itemMargin, itemMarginHalf);
            return mLayoutParams;
        }else if(itemPosition == mArraySize+ mFullCardCount -1){
            mLayoutParams.setMargins(itemMarginHalf, itemMarginHalf, itemMargin, itemMargin);
            return mLayoutParams;
        }else{
            mLayoutParams.setMargins(itemMarginHalf, itemMarginHalf, itemMargin, itemMarginHalf);
            return mLayoutParams;
        }
    }

    private static LinearLayout.LayoutParams setLeftCard(int itemPosition) {
        if(itemPosition == 0){
            mLayoutParams.setMargins(itemMargin, itemMargin, itemMarginHalf, itemMarginHalf);
            return mLayoutParams;
        }else if(itemPosition == mArraySize+ mFullCardCount -1){
            mLayoutParams.setMargins(itemMargin, itemMarginHalf, itemMarginHalf, itemMargin);
            return mLayoutParams;
        }else{
            mLayoutParams.setMargins(itemMargin, itemMarginHalf, itemMarginHalf, itemMarginHalf);
            return mLayoutParams;
        }

    }
}
