package com.tisotry.overimagine.samco_test_2.FCC;

import android.content.Context;

/**
 * Created by repli on 2017-11-29.
 */

public class Communication {
    Context mContext;

    Connect mConnect;

    boolean isConnectedFCC = false;

    public Communication (Context context)    {
        mContext = context;

        mConnect = new Connect(context);
        isConnectedFCC = mConnect.getConnectStatus();

    }

}
