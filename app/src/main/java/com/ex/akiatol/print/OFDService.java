package com.ex.akiatol.print;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.atol.drivers.fptr.Fptr;
import com.atol.drivers.fptr.settings.SettingsActivity;

import static com.ex.akiatol.Const.FPTR_PREFERENCES;

/**
 * Сервис для проверки неотправленных документов в ОФД
 * Created by Leo on 03.10.17.
 */

public class OFDService extends Service {

    private static final long NOTIFY_INTERVAL = 60 * 60 * 1000; //Интервал выгрузки (каждые 60 минут)

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private Timer mTimer = null;

    @Override
    public void onCreate() {
        super.onCreate();

        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new OFDService.TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

    }

    private class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {

            executor.execute(new Runnable() {
                @Override
                public void run() {

                    int count = 0;
                    String unsendDate = "";

                    Fptr fptr = new Fptr();

                    try {
                        fptr.create(getApplication());

                        //publishProgress("Загрузка настроек...");
                        if (fptr.put_DeviceSettings( getSharedPreferences( FPTR_PREFERENCES, Context.MODE_PRIVATE).getString( SettingsActivity.DEVICE_SETTINGS, null)) < 0) {
                            //checkError();
                            fptr.destroy();
                            return;
                        }
                        //publishProgress("Установка соединения...");
                        if (fptr.put_DeviceEnabled(true) < 0) {
                            //checkError();
                            fptr.destroy();
                            return;
                        }
                        //publishProgress("Проверка связи...");
                        if (fptr.GetStatus() < 0) {
                            //checkError();
                            fptr.destroy();
                            return;
                        }

                        if (fptr.put_RegisterNumber(44) < 0) {
                            //checkError();
                            fptr.destroy();
                            return;
                        }
                        if (fptr.GetRegister() < 0) {
                            //checkError();
                            fptr.destroy();
                            return;
                        }

                        count = fptr.get_Count();

                        if (count > 0) {

                            if (fptr.put_RegisterNumber(45) < 0) {
                                //checkError();
                                fptr.destroy();
                                return;
                            }
                            if (fptr.GetRegister() < 0) {
                                //checkError();
                                fptr.destroy();
                                return;
                            }

                            unsendDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss",
                                    Locale.getDefault()).format(fptr.get_Date());
                        }

                    } catch (Exception e) {

                        String err = e.getMessage();
                        if (err == null)
                            err = e.toString();

                        Log.i("OFDService", err);

                    } finally {
                        fptr.destroy();
                    }

                    Log.i("OFDService", "IT'S ALIVE!!!");
                    if (count > 0) {

                        Log.e("OFDService", "Количество неотправленных документов " + count);

                        Intent i = new Intent("ofdUnsend");
                        i.putExtra("unsendCount", count);
                        i.putExtra("unsendDate", unsendDate);

                        sendBroadcast(i);

                    } else
                        Log.i("OFDService", "Exchange success!");
                }

            });
        }

    }

    @Nullable @Override
    public IBinder onBind(Intent intent) {return null;}

}
