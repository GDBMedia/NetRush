package com.netrush.netrushapp;

/**
 * Created by Garrett on 8/17/2016.
 */
public class Constants {

    public static final String GETORDERS = "https://amazonorders.herokuapp.com/orders";
    public static final String TYPE = "type";
    public static final String AWS_ACCESS_KEY_ID = BuildConfig.AWS_ACCESS_KEY_ID;
    public static final String AWS_SECRET_KEY = BuildConfig.AWS_SECRET_KEY;
    public static final String ASSOCIATE_TAG = BuildConfig.ASSOCIATE_TAG;
    public static final String ENDPOINT = "webservices.amazon.com";
    public static final String ITEM = "Item.";
    public static final String ASIN = ".ASIN";
    public static final String QUANT = ".Quantity";
    public static final String AMOUNT = "1";
    public static final String KEY = BuildConfig.KEY;
    public static final String KEYQ = "key";
    public static final String ID = "id";
    public static final String DATE_FORMAT_SOURCE = "yyyy-MM-dd";
    public static final String ORDER_NUMQ = "orderNum";
    public static final String DATE_FORMAT_OUTPUT = "MMMM d";
    public static final String DATE_FORMAT_OUTPUT_YEAR = "MMMM d, yyyy";
    public static final String PUSH_DATA_CHILD = "pushData";
    public static final String TIME_STAMP_CHILD = "timeStamp";
    public static final String ORDER_DATA_REF = "/pushData/data";
    public static final String USERS_REF = "users/";
    public static final String USER_ID_REF = "userId";
    public static final String ORDER_NUM_CHILD = "orderNum";
    public static final String DASH = "-";
    public static final String BLANK_SPACE = "";

    public static final int VIBRATOR_LENGTH = 60;
    public static final int TARGET_WIDTH = 900;
    public static final int TARGET_HEIGHT = 875;
    public static final int TYPE_FULL = 0;
    public static final int TYPE_HALF = 1;
    public static final int LONG_CUTOFF = 37;
    public static final int SHORT_CUTOFF = 15;
    public static final int PRODUCT_DETAIL_TITLE_CUTOFF = 36;
    public static final int FADE_OUT_TYPE = 0;
    public static final int FADE_IN_TYPE = 1;
    public static final int ZERO = 0;
}
