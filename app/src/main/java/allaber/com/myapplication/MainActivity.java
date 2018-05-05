package allaber.com.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    private Handler mHandler;
    boolean TimerOn;
    ArrayList<Long> masTime;
    final String FILENAME = "file";
    private AdView mAdView;
    ImageView arrow;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        masTime = new ArrayList<>();
        readTimeForFile();
        masTime.add((long) 99999);
        Collections.sort(masTime);

        arrow = (ImageView) findViewById(R.id.arrow);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("D18B9F8FEDE6CADE3AD410C38C141B3B").build();
        mAdView.loadAd(adRequest);



        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                TextView txtViev1 = (TextView) findViewById(R.id.txtView1);
                TextView txtViev2 = (TextView) findViewById(R.id.txtView2);

                SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
                SimpleDateFormat sdf2 = new SimpleDateFormat("mm");
                SimpleDateFormat sdf3 = new SimpleDateFormat("ss");

                long TimeCurrent;
                long TimeCurrentSeconds;
                long TimeLeft;

                long HourTimeCurrent;
                long MinTimeCurrent;
                long SecTimeCurrent;

                long HourTimeLeft;
                long MinTimeLeft;
                long SecTimeLeft;

                String StringHourTimeLeft;
                String StringMinTimeLeft;
                String StringSecTimeLeft;

                int MasTimePosition = 0;

                if (TimerOn) {
                    TimeCurrent = Calendar.getInstance().getTimeInMillis();

                    String TimeString1 = sdf1.format(TimeCurrent);
                    String TimeString2 = sdf2.format(TimeCurrent);
                    String TimeString3 = sdf3.format(TimeCurrent);


                    HourTimeCurrent = Integer.parseInt(TimeString1) * 60 * 60;
                    MinTimeCurrent = Integer.parseInt(TimeString2) * 60;
                    SecTimeCurrent = Integer.parseInt(TimeString3);
                    TimeCurrentSeconds = HourTimeCurrent + MinTimeCurrent + SecTimeCurrent;

                    if (masTime.size() != 1) {
                        for (int i = 0; i < masTime.size(); i++) {
                            if (masTime.get(i) > TimeCurrentSeconds) {
                                if (i != masTime.size() - 1) {
                                    MasTimePosition = i;
                                    break;
                                } else {
                                    MasTimePosition = 999;
                                }
                            }


                        }

                        if (MasTimePosition != 999) {
                            TimeLeft = masTime.get(MasTimePosition) - TimeCurrentSeconds;
                            HourTimeLeft = TimeLeft / 3600;
                            MinTimeLeft = (TimeLeft % 3600) / 60;
                            SecTimeLeft = TimeLeft % 60;


                            StringHourTimeLeft = String.valueOf(HourTimeLeft);
                            StringMinTimeLeft = String.valueOf(MinTimeLeft);
                            StringSecTimeLeft = String.valueOf(SecTimeLeft);

                            if (HourTimeLeft < 10)
                                StringHourTimeLeft = "0" + String.valueOf(HourTimeLeft);
                            if (MinTimeLeft < 10)
                                StringMinTimeLeft = "0" + String.valueOf(MinTimeLeft);
                            if (SecTimeLeft < 10)
                                StringSecTimeLeft = "0" + String.valueOf(SecTimeLeft);

                            if (HourTimeLeft == 0 && MinTimeLeft == 0) {
                                txtViev1.setText(StringSecTimeLeft + "c. ");
                            } else {
                                if (HourTimeLeft == 0) {
                                    txtViev1.setText(StringMinTimeLeft +"м. " + StringSecTimeLeft + "c. ");
                                } else {
                                    txtViev1.setText(StringHourTimeLeft + "ч. " + StringMinTimeLeft + "м. " + StringSecTimeLeft + "c. ");
                                }
                            }


                            mHandler.sendEmptyMessageDelayed(0, 1000);
                        } else {
                            txtViev1.setVisibility(View.GONE);
                            txtViev2.setText(R.string.lessons_are_over);
                        }
                    } else {
                        txtViev1.setVisibility(View.GONE);
                        txtViev2.setText(R.string.add_a_schedule_in_the_settings);
                        arrow.setVisibility(View.VISIBLE);

                    }
                }
            }

        };
        TimerOn = true;
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.about_this) {
            onCreateDialog().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void readTimeForFile() {
        try {
            // Открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(FILENAME)));
            String str;
            String str1 = null;

            // Читаем содержимое

            while ((str = br.readLine()) != null) {
                str1 = str;
            }
            if (str1 != null) {
                if (masTime.size() == 0) {
                    String strArr[] = str1.split(" ");
                    for (String aStrArr : strArr) {
                        masTime.add(Long.valueOf(aStrArr));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_about_this, null))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    @Override
    protected void onResume() {
        mAdView.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mAdView.destroy();
        super.onDestroy();

    }

}

















