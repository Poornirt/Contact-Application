package helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 *
 */
public class IntentHelper {

    /**
     *
     */
    public void emailIntent(String pEmail_address, Context pContext) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + pEmail_address));
        pContext.startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
    }

    public void textIntent(String pMobileNumber,Context pContext) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("smsto:" + pMobileNumber));
        pContext.startActivity(intent);

    }

    public void callIntent(String pMobileNumber,Context pContext) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(("tel:" + pMobileNumber)));
        pContext.startActivity(intent);
    }

}
