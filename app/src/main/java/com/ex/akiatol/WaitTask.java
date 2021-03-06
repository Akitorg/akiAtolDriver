/* 
* Copyright (c) AKIP (http://www.akitorg.ru), all rights reserved
* © Все права на распространение и модификацию приложения принадлежат ООО "АКИП" (www.akitorg.ru)
*/

package com.ex.akiatol;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.CountDownTimer;

/**
 * Created by Alex
 * Abstract object for some long background processes
 */

public abstract class WaitTask extends AsyncTask<String, String, String> {
	
	private ProgressDialog pd;
	private int timeout = 1000000;

	private CountDownTimer timer;

	WaitTask(ProgressDialog pd) {
		this.pd = pd;

	}

	WaitTask(ProgressDialog pd, int timeout) {
		this.pd = pd;
		this.timeout = timeout;

	}
	
	@Override
	protected final void onPreExecute() {

		if (pd != null) {
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage("Подождите...");
			pd.show();
		}

		timer = new CountDownTimer(timeout, timeout) {

			public void onTick(long millisUntilFinished) {
				// You can monitor the progress here as well by changing the onTick() time
			}

			public void onFinish() {
				// stop async task if not in progress
				if (getStatus() == AsyncTask.Status.RUNNING) {
					this.cancel();
				}
			}

		}.start();
    } // onPreExecute

	@Override
    protected final void onProgressUpdate(String... args){
		if (pd != null)
        	pd.setMessage( args[0]);
    }
	    
	@Override
	protected String doInBackground(String... args) {
		
		if (args != null && args.length > 0 && args[0].length() > 0)
			publishProgress( args[0]);
		
		try {
			Run();
		} catch (Exception e) {
			return e.getMessage();
		}
		return "";
	}
	
	public abstract void Run() throws Exception;
	
	public abstract void onFinish(String result);
	
	@Override
	protected final void onPostExecute(String result) {
		if (pd != null)
			pd.dismiss();

		timer.cancel();

		onFinish(result);
	}
}
//© Все права на распостранение и модификацию модуля принадлежат ООО "АКИП" (www.akitorg.ru)