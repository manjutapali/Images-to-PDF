package swati4star.createpdf.model;

import android.content.Context;
import android.net.Uri;

public class ExcelToPDFOptions {

    private final Uri mInFileUri;
    private final Context mContext;
    private final String mFinalPath;

    public ExcelToPDFOptions(Uri mInFileUri, Context mContext, String mFinalPath) {
        this.mInFileUri = mInFileUri;
        this.mContext = mContext;
        this.mFinalPath = mFinalPath;
    }

    public Uri getmInFileUri() {
        return mInFileUri;
    }

    public Context getmContext() {
        return mContext;
    }

    public String getmFinalPath() {
        return mFinalPath;
    }
}
