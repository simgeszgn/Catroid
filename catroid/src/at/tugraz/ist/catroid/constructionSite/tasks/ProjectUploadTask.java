package at.tugraz.ist.catroid.constructionSite.tasks;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.UtilZip;

public class ProjectUploadTask extends AsyncTask<Void, Void, Boolean> {
	private Context mContext;
	private String mProjectPath;
	private String mZipFile;
	private ProgressDialog mProgressdialog;
	
	public ProjectUploadTask(Context context, String projectPath, String zipFile) {
		mContext = context;
		mProjectPath = projectPath;
		mZipFile = zipFile;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		String title = mContext.getString(R.string.please_wait);
		String message = mContext.getString(R.string.loading);
		mProgressdialog = ProgressDialog.show(mContext, title,
				message);
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			File dirPath = new File(mProjectPath);
			String[] pathes = dirPath.list(new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					if(filename.endsWith(ConstructionSiteActivity.DEFAULT_FILE_ENDING) || filename.equalsIgnoreCase("images")
							|| filename.equalsIgnoreCase("sounds"))
						return true;
					return false;
				}
			});
			if(pathes == null)
				return false;
			
			
			for(int i=0;i<pathes.length;++i) {
				pathes[i] = dirPath +"/"+ pathes[i];
			}	
			
			File file = new File(mZipFile);
	    	if(!file.exists()) {
	    		file.getParentFile().mkdirs();
	    		file.createNewFile();
	    	}
	    	
			if (!UtilZip.writeToZipFile(pathes, mZipFile)) {
				file.delete();
				return false;
			}
			InputStream is = ConnectionWrapper.doHttpPostFileUpload("", null, "filetag", mZipFile);
			
			file.delete();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	
		if(mProgressdialog != null && mProgressdialog.isShowing())
			mProgressdialog.dismiss();
		
		if(!result) {
			Toast.makeText(mContext, R.string.error_project_upload, Toast.LENGTH_SHORT).show();
			return;
		}
		
		Toast.makeText(mContext, R.string.success_project_upload, Toast.LENGTH_SHORT).show();
		
	}

}