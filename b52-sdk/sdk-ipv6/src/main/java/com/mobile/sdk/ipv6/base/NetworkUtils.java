package com.mobile.sdk.ipv6.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

public class NetworkUtils {

    public static final int NETWORK_CLASS_UNKNOWN = 0;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_CLASS_2_G = 2;
    public static final int NETWORK_CLASS_3_G = 3;
    public static final int NETWORK_CLASS_4_G = 4;
    public static final int NETWORK_CLASS_5_G = 5;

    private static int getCellularClass(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE
        );
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;

            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;

            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;

            case TelephonyManager.NETWORK_TYPE_NR:
                return NETWORK_CLASS_5_G;

            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    private static String getNetWorkClass(Context context) {
        int networkType = NETWORK_CLASS_UNKNOWN;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw != null) {
                NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
                if (actNw != null) {
                    if(actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                        networkType = NETWORK_WIFI;
                    }else if(actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                        networkType = getCellularClass(context);
                    }
                }
            }
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                int type = networkInfo.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    networkType = NETWORK_WIFI;
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    networkType = getCellularClass(context);
                }
            }
        }

        switch (networkType) {
            case NETWORK_WIFI:
                return "wifi";
            case NETWORK_CLASS_2_G:
                return getOperatorName(context) + "2G";
            case NETWORK_CLASS_3_G:
                return getOperatorName(context) + "3G";
            case NETWORK_CLASS_4_G:
                return getOperatorName(context) + "4G";
            case NETWORK_CLASS_5_G:
                return getOperatorName(context) + "5G";
            default:
                return getOperatorName(context) + "未知网络";
        }
    }

    public static int getNetWorkType(Context context) {
        String netWorkClass = getNetWorkClass(context);
        if (netWorkClass.contains("wifi")) return 1;
        if (netWorkClass.contains("中国移动4G")) return 2;
        if (netWorkClass.contains("中国移动5G")) return 3;
        if (netWorkClass.contains("中国联通4G")) return 4;
        if (netWorkClass.contains("中国联通5G")) return 5;
        if (netWorkClass.contains("中国电信4G")) return 6;
        if (netWorkClass.contains("中国电信5G")) return 7;
        return 8;
    }

    private static String getOperatorName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operatorName = telephonyManager.getSimOperatorName().toLowerCase();
        if (operatorName.contains("unicom")) {
            return "中国联通";
        }
        if (operatorName.contains("mobile")) {
            return "中国移动";
        }
        if (operatorName.contains("telecom")) {
            return "中国电信";
        }
        if (operatorName.contains("netcom")) {
            return "中国网通";
        }
        return operatorName.toUpperCase();
    }
}
