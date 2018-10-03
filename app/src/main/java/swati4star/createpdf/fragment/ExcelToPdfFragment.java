package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dd.morphingbutton.MorphingButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.interfaces.OnItemClickListner;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstance) {

        View view = layoutInflater.inflate(R.layout.fragment_excel_to_pdf, container, false);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        ButterKnife.bind(this, view);
        mMorphButtonUtility.morphToGrey(createPDF, mMorphButtonUtility.integer());
        createPDF.setEnabled(false);

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

    public void createPDF() {


    }
}
