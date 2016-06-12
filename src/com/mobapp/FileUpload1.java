package com.mobapp;


import com.mobapp.AndroidMultiPartEntity.ProgressListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FileUpload1 extends Activity{

	Bitmap bmp;
	TextView messageText, dnldfile;
    Button uploadButton, startBtn, showbtn;
    ImageView serverimg;
    ProgressDialog dialog = null;
    
    int serverResponseCode = 0;
    int outfile = 0 ;
    
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;

    // File Path
    //MainMob fpath = new MainMob();
    //String uploadFilePath = fpath.selpath;
    //final String uploadFileName = "katewinslet.jpg";
    //final String uploadFileName = "text.txt";
    //final String uploadFileName = "javatophp.java";

    
    //String uploadFilePath ;   //path from the previous Activity and do some change in code
    
    
    // for c programming
    final String uploadFilePath = "/mnt/sdcard/expt/cprog/"; //for amit pc
    //final String uploadFilePath = "/mnt/sdcard/expt_evaluation/"; //for windows pc-farhan
    final String uploadFileName = "search.c";   //Not needed when file path is selected from previous Activity
    //final String uploadFileName = "InsertionSort.c";
    //final String uploadFileName = "matmul.c";
    //final String uploadFileName = "mergesort.c";
    
    // for java programming
    //final String uploadFilePath = "/mnt/sdcard/expt/javaprog/"; 
    //final String uploadFileName = "search.java";
    //final String uploadFileName = "insertsort.java";
    //final String uploadFileName = "matmul.java";
    //final String uploadFileName = "mergesort.java";
    
    // for mobile id of device
    String m_androidId;
    
    String upLoadServerUri = null;
    String downloadfilename; // = m_androidId + "_" + uploadFileName + ".out" ;
    String sdcardpath = "/sdcard/" + downloadfilename; 
    //String serverip = "http://192.168.12.97:80/android_connect/";
    String serverip = "http://54.69.110.134:80/android_connect/";  // for AWS cloud server
    String dwnldFilePath = serverip + "uploads/" + downloadfilename;
    String delUri = serverip + "delfile.php";
    
    // Timing calculation
    double dstart, endd, tt, tstart, endu, upt, dnt,d ;
    
    String t1, t2, t3 ,d1, u1;
    int ping ;
    
    MainMob1 brm = new MainMob1();
    
    // PHP script path
    String postReceiverUrl = serverip + "test1.php";
    
    private ProgressBar progressBar;
    private TextView txtPercentage;
    long totalSize = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_file);
		
		// android mobile id of device 
		m_androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		
		//uploadButton = (Button)findViewById(R.id.uploadButton);
        messageText  = (TextView)findViewById(R.id.messageText);
       
        //txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        
        // Get the bundle
//        Bundle bundle = getIntent().getExtras();
        // Extract the data
//        uploadFilePath = bundle.getString("key");
        
        //tstart = SystemClock.uptimeMillis();
        // File uploading to server
        new Uploadserver().execute();
        /*
        endu = SystemClock.uptimeMillis();
    	upt = endu - tstart;
    	t1 = String.valueOf(upt);*/
    	//Toast.makeText(getApplicationContext(), t, Toast.LENGTH_SHORT).show(); 
    	
        
        // For splitting the path to get the filename. Change from here for use in device.
        //Toast.makeText(getApplicationContext(), uploadFilePath, Toast.LENGTH_SHORT).show();
        String f1 = uploadFilePath ;
        String[] tokens = f1.split("/") ;
 //       String f2 = tokens[7] ;  //change here
        String f2 = tokens[3] ;
        String uploadFileName1 = f2 ;
        System.out.println(f2);

//        downloadfilename = m_androidId + "_" + uploadFileName1 + ".out" ;
        
        downloadfilename = m_androidId + "_" + uploadFileName + ".out" ;
        
        String dwnldFilePath = serverip + "uploads/" + downloadfilename;
        
        // check if output file exists
        int cnt = 1 ; 
        new CheckFileExist().execute(dwnldFilePath) ;
        while(outfile != 1) {
            try {
            	cnt++ ;
            	ping = cnt ;
                Thread.sleep(100);                
                new CheckFileExist().execute(dwnldFilePath) ;
                Log.i("Check file", "Waiting for the output file..." + cnt);
              } catch (Exception e) {
            	System.out.println("Exception: " + e) ;
                e.getLocalizedMessage();
              }
        }     
        
        
        //dstart = SystemClock.uptimeMillis();
        // if exists: download the file and display
        new DownloadFileAsync().execute(dwnldFilePath);
        
        // Display the output of file like java, c, text, image etc. 
        dnldfile = (TextView) findViewById(R.id.textView);
        
       /* endd = SystemClock.uptimeMillis();
    	//dnt = endd - dstart;
    	tt = endd - tstart;
    	t2 = String.valueOf(d);
    	t3 = String.valueOf(tt);*/
    	//Toast.makeText(getApplicationContext(), "Upload time: " + t1 + "\nDownload time: " + t2 + "Total time: " +t3,
    		//	Toast.LENGTH_LONG).show(); 
        
        //serverimg = (ImageView) findViewById(R.id.imageView1);
        
        // Delete File from server 
		new ExecutePHPFile().execute(delUri);    
		
		BroadcastReceiver batteryReceiver2 = new BroadcastReceiver() {
            double scale = -1;
            double level = -1;
            double batteryPct2;
            @SuppressLint("ShowToast")
			@Override
            public void onReceive(Context context, Intent intent) {
               /* level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                batteryPct2 = level / scale;
                Log.e("BatteryManager", "level is "+level+"/"+scale);
                */
                //Toast.makeText(getApplicationContext(), level, Toast.LENGTH_SHORT);
                
                Toast.makeText(getApplicationContext(), "Upload time: " + u1 + "\nDownload time: " + d1 + 
                		"\nServer ping: " + ping, Toast.LENGTH_LONG).show(); 
                
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver2, filter);  
       
	}
	
	public class Uploadserver extends AsyncTask<Void, Integer, String> {

	/*	@Override
		protected void onPreExecute() {
			// setting progress bar to zero
			progressBar.setProgress(0);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			// Making progress bar visible
			progressBar.setVisibility(View.VISIBLE);

			// updating progress bar value
			progressBar.setProgress(progress[0]);

			// updating percentage value
			txtPercentage.setText(String.valueOf(progress[0]) + "%");
		}
		*/
		
		@Override
		protected String doInBackground(Void... params) {
			return uploadcode();
		}

		@SuppressWarnings("deprecation")
		private String uploadcode() {		
			String responseString = null;
			
			tstart = SystemClock.uptimeMillis();
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(postReceiverUrl);
			
			System.out.println(postReceiverUrl);
			
			try {
				AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
						new ProgressListener() {

							@Override
							public void transferred(long num) {
								publishProgress((int) ((num / (float) totalSize) * 100));
								System.out.println("far");
							}
						});
				 
//				File sourceFile = new File(uploadFilePath);  // change here
				File sourceFile = new File(uploadFilePath + uploadFileName);
				System.out.println("File path : " + sourceFile.getAbsolutePath());

				// Adding file data to http body
				entity.addPart("uploaded_file", new FileBody(sourceFile));

				// Extra parameters if you want to pass to server
				entity.addPart("mid",new StringBody(m_androidId));
				
				totalSize = entity.getContentLength();
				httppost.setEntity(entity);

				// Making server call
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();

				int statusCode = response.getStatusLine().getStatusCode();
				System.out.println(statusCode);
				if (statusCode == 200) {
					// Server response
					responseString = EntityUtils.toString(r_entity);
				} else {
					responseString = "Error occurred! Http Status Code: "+ statusCode;
				}

			} catch (ClientProtocolException e) {
				responseString = e.toString();
			} catch (IOException e) {
				responseString = e.toString();
			}

			endu = SystemClock.uptimeMillis();
	    	upt = endu - tstart;
			u1 = String.valueOf(upt);
			System.out.println(responseString);
			return responseString;
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.i("Response from server: " , result);

			//Toast.makeText(getApplicationContext(), "\nUpload Time: " + u1, Toast.LENGTH_SHORT).show();
			
			// showing the server response in an alert dialog
			showAlert(result);

			super.onPostExecute(result);
		}
		
	}
	
	private void showAlert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setTitle("Response from Servers2:")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// do nothing
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	class CheckFileExist extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				  HttpURLConnection.setFollowRedirects(false);
				  // note : you may also need
				  // HttpURLConnection.setInstanceFollowRedirects(false)
				  HttpURLConnection con = (HttpURLConnection) new URL(params[0]).openConnection();
				  con.setRequestMethod("HEAD");
				  
				  if (con.getResponseCode() == HttpURLConnection.HTTP_OK)
					 outfile = 1 ;
				     return "1" ;
				}
				catch (Exception e) {
				  e.printStackTrace();
		          System.out.println("checkfilestatus Exception: " + e) ;
				  outfile = 0 ;
				  return "0";
				}	
			//return null;
		}
		
	}

	// Download first method by using Asynctask
	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Downloading file..");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
        }
    }

	class DownloadFileAsync extends AsyncTask<String, String, String> {
   
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DIALOG_DOWNLOAD_PROGRESS);
		}
	
		
		
		@Override
		protected String doInBackground(String... aurl) {
			dstart = SystemClock.uptimeMillis();
			//dstart = SystemClock.elapsedRealtimeNanos();
			int count;
			try {
				URL url = new URL(aurl[0]);
			
				Log.i("Download Start", "File Downloading ....");
				Log.i("FILE_URLLINK", "File URL is "+url);
			
				URLConnection conexion = url.openConnection();
				conexion.connect();
		
				int lenghtOfFile = conexion.getContentLength();
				//Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
		
				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(sdcardpath);
		
				byte data[] = new byte[1024];
		
				long total = 0;
		
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress(""+(int)((total*100)/lenghtOfFile));
					output.write(data, 0, count);
				}
		
				output.flush();
				output.close();
				input.close();	
			
				Log.i("Download stop", "File Downloading Done.");
			
			} catch (Exception e) {
				e.printStackTrace();
	            Log.i("ERROR ON DOWNLOADING FILES", "ERROR IS" +e);
			}
		
			endd = SystemClock.uptimeMillis();
			//endd = SystemClock.elapsedRealtimeNanos();
	    	d = (endd - dstart);
	    	d1= String.valueOf(d);
			
			return d1;
		}
	
		protected void onProgressUpdate(String... progress) {
			 //Log.d("ANDRO_ASYNC",progress[0]);
			 mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}
	
		@Override
		protected void onPostExecute(String unused) {
			dismissDialog(DIALOG_DOWNLOAD_PROGRESS);	
			//Toast.makeText(getApplicationContext(), "Download Time: " + d1, Toast.LENGTH_SHORT).show();
			textshow() ;
			//deloutfilefromserver() ;
		}
	}

	void textshow () {
		
		try {
			File myFile = new File(sdcardpath);
			//System.out.println("Path : " + myFile.getAbsolutePath());
			FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
			String aDataRow = "";
			String aBuffer = "";
			while ((aDataRow = myReader.readLine()) != null) {
				aBuffer += aDataRow + "\n";
			}
			
			Log.i("Textview", "Showing output of app.");
			dnldfile.setText(aBuffer);
			myReader.close();
			//Toast.makeText(getBaseContext(), "Done reading SD.", Toast.LENGTH_SHORT).show();
			deleteFile();
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
/*	public void imgshow() {
        bmp = BitmapFactory.decodeFile("/sdcard/duplicatekate.jpg");
		serverimg.setImageBitmap(bmp);
	}*/
	
	private void deleteFile() {
		File folder = Environment.getExternalStorageDirectory();
		String fileName = folder.getPath() + "/" + downloadfilename;
		//System.out.println(fileName);
		File myFile = new File(fileName);
		Log.i("SDCard Delete", "Delete from SDCard");
		if(myFile.exists())
			myFile.delete();
	}
	
	class ExecutePHPFile extends AsyncTask<String, String, String> {
		
		@Override
		protected String doInBackground(String... params) {   
			Looper.prepare();
			try {
				URL url = new URL(params[0]);
	            URLConnection conn = url.openConnection();
	            conn.connect();
	            conn.getContent();
	            //Toast.makeText(getBaseContext(), "File deleted...", Toast.LENGTH_SHORT).show();
	            Log.i("DELETE", "File deleted...");
	        } catch(Exception e) {
	            Log.i("File Delete", "File cannot be deleted..." + e); 
		    } 
			Looper.loop();
	        return null ;
		}	
	} 

}