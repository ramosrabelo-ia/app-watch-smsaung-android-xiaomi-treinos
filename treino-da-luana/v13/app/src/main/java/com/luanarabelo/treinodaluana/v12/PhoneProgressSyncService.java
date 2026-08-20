package com.luanarabelo.treinodaluana.v12;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.WearableListenerService;

public class PhoneProgressSyncService extends WearableListenerService {
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            PhoneProgressSync.applyEvent(this, event);
        }
    }
}

