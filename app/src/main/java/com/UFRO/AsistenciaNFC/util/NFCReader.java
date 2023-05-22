package com.UFRO.AsistenciaNFC.util;
import com.UFRO.AsistenciaNFC.view.MainActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Log;



public class NFCReader {

    private String savedResult;
    final static String TAG = "NFCTag";
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private Context context;

    public NFCReader(Context context) {
        this.context = context;

        nfcAdapter = NfcAdapter.getDefaultAdapter(this.context);
        pendingIntent = PendingIntent.getActivity(this.context, 0, new Intent(this.context, this.context.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    public boolean isNFCEnabled() {
        return nfcAdapter != null;
    }

    public void enableForegroundDispatch() {
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch((MainActivity) context, pendingIntent, null, null);
        }
    }

    public void disableForegroundDispatch() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch((MainActivity) context);
        }
    }

    public void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            assert tag != null;
            detectTagData(tag);
        }
    }

    private void detectTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n');

        Log.v(TAG,sb.toString());
        Log.v(TAG, "Type of savedResult: " + this.savedResult.getClass().getSimpleName());
    }

    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        Log.v(TAG, String.valueOf(result));

        savedResult = String.valueOf(result);

        return result;
    }

    public String getSavedResult() {
        return savedResult;
    }


}



