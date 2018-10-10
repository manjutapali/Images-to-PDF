package swati4star.createpdf.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;
import static swati4star.createpdf.util.DialogUtils.createOverwriteDialog;
import com.itextpdf.text.Paragraph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.OnItemClickListner;
import swati4star.createpdf.model.ExcelToPDFOptions;
import swati4star.createpdf.util.CreatePdf;
import swati4star.createpdf.util.ExcelUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.StringUtils;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.StringUtils.getDefaultStorageLocation;
import static swati4star.createpdf.util.StringUtils.showSnackbar;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;

public class ExcelToPdfFragment extends Fragment implements OnItemClickListner {

    private Activity mActivity;
    private FileUtils mFileUtils;

    private final int mFileSelectCode = 0;
    private Uri mFileSelectUri = null;
    private int mButtonClicked = 0;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private boolean mPermissionGranted = false;

    private MorphButtonUtility mMorphButtonUtility;

    @BindView(R.id.pdfCreate)
    MorphingButton createPDF;
    private SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstance) {

        View view = layoutInflater.inflate(R.layout.fragment_excel_to_pdf, container, false);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        ButterKnife.bind(this, view);
        mMorphButtonUtility.morphToGrey(createPDF, mMorphButtonUtility.integer());
        createPDF.setEnabled(false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);

        return view;
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mFileUtils = new FileUtils(mActivity);
    }

    @OnClick(R.id.selectFile)
    public void selectFile() {
        if (mButtonClicked == 0) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/vnd.ms-excel");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(
                        intent, String.valueOf(R.string.select_file)),
                        mFileSelectCode);
            } catch (android.content.ActivityNotFoundException e) {
                showSnackbar(
                        mActivity,
                        "No file manager found, please install file manager");
            }

            mButtonClicked = 1;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        mButtonClicked = 0;
        switch (requestCode) {

            case mFileSelectCode:

                if (resultCode == RESULT_OK) {
                    mFileSelectUri = data.getData();
                    Log.d("ExcelToPdf:", "Select file URI : " + mFileSelectUri);
                    showSnackbar(mActivity, "File selected");

                    String fileName = mFileUtils.getFileName(mFileSelectUri);
                    // TODO: add text view to the layout and show the file name

                    createPDF.setEnabled(true);
                    mMorphButtonUtility.morphToSquare(createPDF, mMorphButtonUtility.integer());
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.pdfCreate)
    public void createPDF() {


        if (!mPermissionGranted) {
            getRuntimePermission();
            //return;
        }
        Log.d("ExcelToPDF:", "Create PDF function is called");
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, (dialog, input) -> {

                    if (StringUtils.isEmpty(input)) {
                        showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                    } else {
                        final String inputName = input.toString();

                        if (!mFileUtils.isFileExist(inputName + R.string.pdf_ext)) {
                            createExcelToPDF(inputName);
                        } else {

                            MaterialDialog.Builder builder = createOverwriteDialog(mActivity);
                            builder.onPositive((dialog1, which) -> createExcelToPDF(inputName))
                                    .onNegative((dialog1, which) -> createPDF())
                                    .show();
                        }
                    }
                })
                .show();
    }

    private void createExcelToPDF(String inputName) {
        InputStream inputStream;

        String mPath = mSharedPreferences.getString(STORAGE_LOCATION,
                getDefaultStorageLocation());

        mPath += inputName + ".pdf";
        Log.e("ExcelToPDF:", "Path" + mPath);
        ExcelUtils excelUtils = new ExcelUtils();

        excelUtils.createPDF(new ExcelToPDFOptions(mFileSelectUri, mActivity, mPath));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length < 1)
            return;
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionGranted = true;
                    createPDF();
                    showSnackbar(mActivity, R.string.snackbar_permissions_given);
                } else
                    showSnackbar(mActivity, R.string.snackbar_insufficient_permissions);
            }
        }
    }

    private void getRuntimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)) {

                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);

                return;
            }

            mPermissionGranted = true;
        }


    }

}
