package com.mlsdev.android.townwizard;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.mlsdev.android.townwizard.R;
import com.mlsdev.android.townwizard.utils.TownWizardConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class UploadPhotoActivity extends Activity {

    private ImageView mImageView;
    private Button mCancelButton;
    private Button mUploadButton;
    private OnClickListener mOnClickListener;
    private String mImagePath;
    private Uri mImageUri;
    private EditText mEditText;
    private String mUrlUpload;
    private Bitmap mImage;
    private String mSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.upload_view);
	mImageView = (ImageView) findViewById(R.id.iv_upload_photo);
	mCancelButton = (Button) findViewById(R.id.bt_cancel_upload);
	mUploadButton = (Button) findViewById(R.id.bt_upload);
	mEditText = (EditText) findViewById(R.id.et_enter_name_description);
	Bundle extras = getIntent().getExtras();
	mSource = extras.getString(TownWizardConstants.SOURCE);
	Uri imageUri = (Uri) extras.get(TownWizardConstants.IMAGE_URI);
	mImageUri = imageUri;

	mUrlUpload = extras.getString(TownWizardConstants.URL);

	mImagePath = imageUri.getPath();
	Log.d("mImagePath", mImagePath);

	try {
	    mImage = Media.getBitmap(this.getContentResolver(), imageUri);

	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	if (mImage != null) {
	    mImageView.setImageBitmap(mImage);
	    mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
		    switch (v.getId()) {
		    case R.id.bt_cancel_upload:
			UploadPhotoActivity.this.finish();
			break;
		    case R.id.bt_upload: {
			uploadPhoto();
			break;
		    }

		    }
		}
	    };
	    mCancelButton.setOnClickListener(mOnClickListener);
	    mUploadButton.setOnClickListener(mOnClickListener);
	}
    }

    private void uploadPhoto() {

	String BOUNDRY = "0xKhTmLbOuNdArY";
	HttpURLConnection conn = null;
	try {
	    StringBuffer requestBody = new StringBuffer();
	    requestBody.append("--");
	    requestBody.append(BOUNDRY);
	    requestBody.append("\r\n");
	    requestBody.append("Content-Disposition: form-data; name=\"caption\"\r\n\r\n");
	    requestBody.append(mEditText.getText());

	    requestBody.append("\r\nContent-Disposition: form-data; name=\"username\"\r\n\r\n");
	    requestBody.append("");
	    requestBody.append("\r\n--" + BOUNDRY + "\r\n");
	    requestBody.append("Content-Disposition: form-data; name=\"userphoto\"; filename=\"mainphoto.jpg\"\r\n"); //$NON-NLS-1$
	    requestBody.append("Content-Type: application/octet-stream\r\n\r\n");
	    StringBuffer requestBody2 = new StringBuffer();
	    requestBody2.append("\r\n--" + BOUNDRY + "\r\n");
	    requestBody2.append("\r\n--" + BOUNDRY + "\r\n");
	    requestBody2.append("Content-Disposition: form-data; name=\"userthumb\"; filename=\"thumb.jpg\"\r\n"); //$NON-NLS-1$
	    requestBody2.append("Content-Type: application/octet-stream\r\n\r\n");

	    StringBuffer requestBody3 = new StringBuffer();
	    requestBody3.append("\r\n--" + BOUNDRY + "\r\n");

	    // Make a connect to the server
	    URL url = new URL(mUrlUpload);
	    conn = (HttpURLConnection) url.openConnection();

	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    conn.setUseCaches(false);
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDRY);
	    conn.setConnectTimeout(3 * 60 * 1000);

	    DataOutputStream dataOS = new DataOutputStream(conn.getOutputStream());
	    dataOS.writeUTF(requestBody.toString());
	    Log.d("request body writing", requestBody.toString());
	    if (mSource.equals("CAMERA")) {
		dataOS.write(getBytesFromFile(new File(mImagePath)));
	    } else {
		dataOS.write(getBytesFromFile(new File(getRealPathFromURI(mImageUri))));
	    }
	    Log.d("IMAGE PATH", mImagePath);
	    Log.d("request body writing11111", requestBody2.toString());
	    dataOS.writeUTF(requestBody2.toString());
	    Log.d("request body writing1122222", requestBody.toString());
	    if (mSource.equals("CAMERA")) {
		dataOS.write(getBytesFromFile(new File(mImagePath)));
	    } else {
		dataOS.write(getBytesFromFile(new File(getRealPathFromURI(mImageUri))));
	    }

	    Log.d("request body writing33333", requestBody.toString());
	    dataOS.writeUTF(requestBody3.toString());
	    Log.d("request body writing44444", requestBody3.toString());
	    dataOS.flush();
	    dataOS.close();

	    Log.d("Response code", Integer.toString(conn.getResponseCode()));

	    InputStream is = conn.getInputStream();
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] bytes = new byte[1024];
	    int bytesRead;
	    while ((bytesRead = is.read(bytes)) != -1) {
		baos.write(bytes, 0, bytesRead);
	    }
	    byte[] bytesReceived = baos.toByteArray();
	    baos.close();

	    is.close();
	    String response = new String(bytesReceived);

	    Log.d("responseUpload", response);

	    if (response.equals("1")) {
		String title = getResources().getString(R.string.upload_successful);
		String message = getResources().getString(R.string.thanks_for_sharing);
		showUploadDialog(title, message);
	    } else {
		String title = getResources().getString(R.string.sorry);
		String message = getResources().getString(R.string.service_currently_unavaible);
		showUploadDialog(title, message);
	    }

	} catch (Exception e) {
	    e.printStackTrace();

	} finally {

	    if (conn != null) {
		conn.disconnect();
	    }
	}

    }

    private void showUploadDialog(String title, String message) {
	AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);

	aBuilder.setTitle(title);
	aBuilder.setMessage(message);
	aBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		finish();
	    }
	});
	aBuilder.show();
    }

    // read the photo file into a byte array...
    public byte[] getBytesFromFile(File file) throws IOException {

	ContentResolver cr = getContentResolver();
	Log.d("uriFromFile", Uri.fromFile(file).toString());

	InputStream is = cr.openInputStream(Uri.fromFile(file));

	// Get the size of the file
	long length = file.length();

	// Create the byte array to hold the data
	byte[] bytes = new byte[(int) length];
	// Read in the bytes
	int offset = 0;
	int numRead = 0;
	while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
	    offset += numRead;
	}

	// Close the input stream and return bytes
	is.close();
	return bytes;
    }

    // And to convert the image URI to the direct file system path of the image
    // file
    public String getRealPathFromURI(Uri contentUri) {

	// can post image
	String[] proj = { MediaColumns.DATA };
	Cursor cursor = managedQuery(contentUri, proj, null, null, null);

	int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	cursor.moveToFirst();

	return cursor.getString(column_index);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
	super.onConfigurationChanged(newConfig);
    }

}
