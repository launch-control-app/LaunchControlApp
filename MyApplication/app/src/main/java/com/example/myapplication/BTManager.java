package com.example.myapplication;

public class BTManager {
    private BluetoothChatService mChatService;
    private static BTManager btManager;

    public static BTManager getBTConn(dataListener dataListener)
    {
        if (btManager == null)
        {
            btManager = new BTManager(dataListener);
        }
        else
        {
            btManager.mChatService.addDataListener(dataListener);
        }

        return btManager;
    }

    private BTManager(dataListener dataListener)
    {
        mChatService = new BluetoothChatService(dataListener);
        mChatService.tryConnection();
    }
}
