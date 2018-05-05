package allaber.com.myapplication;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.Dialog;
import android.widget.Button;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    @Override
    // Создание TimePickerDialog
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), this, 0, 0, true);
        return tpd;
    }

    // Создание события TimePickerDialog при внесении времени и нажатия кнопки "ОК"
    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {

        // Получаем параметры с активити. Тут у нас ID кнопок которые вызвали TimePickerDialog
        Bundle bundle = this.getArguments();
        // Если параметр не пустой то выполнеем следующие действие
        if (bundle != null) {
            // Получение ID кнопки
            int i = bundle.getInt("ID");
            // Находим по этому ID нашу кнопку
            Button time = (Button) getActivity().findViewById(i);
            // Получение выбронного часа в TimePickerDialog и занесение его в переменную стринг
            String hour = String.valueOf(hourOfDay);
            // Получение выбронной минуты в TimePickerDialog и занесение его в переменную стринг
            String minute = String.valueOf(minuteOfDay);
            // Условие при котором всегда выводится число с двумя цифрами
            if (hourOfDay < 10) hour = "0" + hour;
            // Условие при котором всегда выводится число с двумя цифрами
            if (minuteOfDay < 10) minute = "0" + minute;
            // Изменение текста кнопки которая вызвала TimePickerDialog
            time.setText(hour + ":" + minute);
        }
        if (getActivity() != null) {
            Settings ma = (Settings) getActivity();
            ma.writeTimeForFile();
        }
    }
}






















