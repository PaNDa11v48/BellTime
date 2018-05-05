package allaber.com.myapplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import java.util.ArrayList;
import java.util.List;

public class Settings extends AppCompatActivity {

    // Создание ArryList в котром будет хранится строка со всеми View
    private List<View> allEds;
    // Создание ArryList в котром будет хранится наши кнопки
    private List<View> allButtons;
    // Счетчик который показывает количество динамических строк
    private int counter = 0;
    // Создание ArrayList в котором будет хранится массив времени
    ArrayList<Long> masTime;
    // Создание имя файла
    final String FILENAME = "file";
    AlertDialog.Builder alertDialogDelete;
    Context context;
    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("D18B9F8FEDE6CADE3AD410C38C141B3B").build();
        mAdView.loadAd(adRequest);

        // Создание массива элменотов строки
        allEds = new ArrayList<>();
        // Создание массива элменотов кнопок
        allButtons = new ArrayList<>();
        // Создание массива времени
        masTime = new ArrayList<>();
        // Находим наш кастомный LinearLayout по которому будут создаваться строки
        context = Settings.this;
        alertDialogDelete = new AlertDialog.Builder(context);
        alertDialogDelete.setTitle(R.string.delete_line);

        readTimeForFile();// При ошибке отключить
        makeDinamButtons();// При ошибке отключить
        writeTimeForFile();
    }


    public void makeLines() {///////////
        final LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        counter++;
        // В кастомном LinearLayout находим наши Button и TextView
        final View view = getLayoutInflater().inflate(R.layout.activity_custom, null);
        // Создание кнопки удаления
        final ImageButton deleteField = (ImageButton) view.findViewById(R.id.imageButton);
        // Создание текста показывающий номер занятия
        final TextView text1 = (TextView) view.findViewById(R.id.txtView1);
        // Создание кнопки начала занятия
        final Button time1 = view.findViewById(R.id.button1);
        // Создание кнопки конца занятия
        final Button time2 = view.findViewById(R.id.button2);
        // Добаления уникального ID для кнопки начала занятия
        time1.setId(View.generateViewId());
        // Добаления уникального ID для кнопки конца занятия
        time2.setId(View.generateViewId());

        // Условие для вывода номера занятия
        if (counter < 10) {
            text1.setText("Урок  " + counter);
        } else {
            text1.setText("Урок " + counter);
        }

        // Событие на нажатие кнопки начала занятия
        time1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создание TimePickerFragment
                DialogFragment dFragment = new TimePickerFragment();
                dFragment.show(getFragmentManager(), "Time Picker");
                // Отпарвка параметра в TimePickerFragment с нашим ID кнопки
                Bundle bundle = new Bundle();
                bundle.putInt("ID", v.getId());
                dFragment.setArguments(bundle);
            }
        });

        // Событие на нажатие кнопки конца занятия
        time2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создание TimePickerFragment
                DialogFragment dFragment = new TimePickerFragment();
                dFragment.show(getFragmentManager(), "Time Picker");
                // Отпарвка параметра в TimePickerFragment с нашим ID кнопки
                Bundle bundle = new Bundle();
                bundle.putInt("ID", v.getId());
                dFragment.setArguments(bundle);
            }
        });

        // Добавлем все компоненты в наш массив
        allEds.add(view);
        // Добавлем кнопку в наш массив
        allButtons.add(time1);
        // Добавлем кнопку в наш массив
        allButtons.add(time2);
        // Добавляем елементы в LinearLayout
        linear.addView(view);

        // Кнопка удаления  которая удаляет строку и их элементы в массивах
        deleteField.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                alertDialogDelete.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        try {
                            // Получаем родительский view и удаляем его
                            ((LinearLayout) view.getParent()).removeView(view);
                            // Удаляем элементы в массивах
                            allButtons.remove(time1);
                            allButtons.remove(time2);
                            writeTimeForFile();
                        } catch (IndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                alertDialogDelete.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                    }
                });
                alertDialogDelete.show();
            }
        });
        writeTimeForFile();

    }


    private void DelFile() {

        try {
            // Открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(FILENAME)));
            // Читаем содержимое
            while ((br.readLine()) != null) {
                deleteFile(FILENAME);
                masTime.clear();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeTimeForFile() {
        try {
            // Отрываем поток для записи
            DelFile();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput(FILENAME, MODE_APPEND)));
            for (int i = 0; allButtons.size() > i; i++) {
                Button btn = (Button) findViewById(allButtons.get(i).getId());
                String ButtonTime = String.valueOf(btn.getText());
                String hour = String.valueOf(ButtonTime.charAt(0)) + String.valueOf(ButtonTime.charAt(1));
                String minute = String.valueOf(ButtonTime.charAt(3)) + String.valueOf(ButtonTime.charAt(4));
                int intHour = Integer.parseInt(hour);
                int intMinute = Integer.parseInt(minute);
                long timeBtn = (intHour * 60 * 60) + (intMinute * 60);
                bw.write(timeBtn + " ");
            }
            // Закрываем поток
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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


    void makeDinamButtons() {
        final LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        int icount = -2;

        Long hours1;
        Long minute1;
        Long hours2;
        Long minute2;
        String sHours1;
        String sMinute1;
        String sHours2;
        String sMinute2;


        for (int i = 0; i < (masTime.size() / 2); i++) {
            counter++;
            icount++;
            icount++;
            final View view = getLayoutInflater().inflate(R.layout.activity_custom, null);
            final ImageButton deleteField = view.findViewById(R.id.imageButton);
            final TextView text1 = view.findViewById(R.id.txtView1);
            final Button time1 = view.findViewById(R.id.button1);
            final Button time2 = view.findViewById(R.id.button2);
            time1.setId(View.generateViewId());
            time2.setId(View.generateViewId());

            if (counter < 10) {
                text1.setText("Урок  " + counter);
            } else {
                text1.setText("Урок " + counter);
            }

            time1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment dFragment = new TimePickerFragment();
                    dFragment.show(getFragmentManager(), "Time Picker");
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID", v.getId());
                    dFragment.setArguments(bundle);
                }
            });

            time2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment dFragment = new TimePickerFragment();
                    dFragment.show(getFragmentManager(), "Time Picker");
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID", v.getId());
                    dFragment.setArguments(bundle);
                }
            });

            hours1 = masTime.get(icount) / 3600;
            minute1 = (masTime.get(icount) % 3600) / 60;
            hours2 = masTime.get(icount + 1) / 3600;
            minute2 = (masTime.get(icount + 1) % 3600) / 60;
            sHours1 = String.valueOf(hours1);
            sMinute1 = String.valueOf(minute1);
            sHours2 = String.valueOf(hours2);
            sMinute2 = String.valueOf(minute2);

            if (hours1 < 10) sHours1 = "0" + sHours1;
            if (minute1 < 10) sMinute1 = "0" + sMinute1;

            if (hours2 < 10) sHours2 = "0" + sHours2;
            if (minute2 < 10) sMinute2 = "0" + sMinute2;

            time1.setText(sHours1 + ":" + sMinute1);
            time2.setText(sHours2 + ":" + sMinute2);


            allEds.add(view);
            allButtons.add(time1);
            allButtons.add(time2);
            linear.addView(view);
            deleteField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogDelete.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            try {
                                // Получаем родительский view и удаляем его
                                ((LinearLayout) view.getParent()).removeView(view);
                                // Удаляем элементы в массивах
                                allEds.remove(view);
                                allButtons.remove(time1);
                                allButtons.remove(time2);
                                writeTimeForFile();
                            } catch (IndexOutOfBoundsException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    alertDialogDelete.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                        }
                    });
                    alertDialogDelete.show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        menu.findItem(R.id.action_add).setVisible(true);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            makeLines();
            return true;
        }
        if (id == R.id.about_this) {
            onCreateDialog().show();
            return true;
        }
        if (id == android.R.id.home) {
            Intent intent = new Intent(Settings.this, MainActivity.class);
            startActivity(intent);

            writeTimeForFile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.activity_about_this, null))
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                });
        return builder.create();
    }
}



















