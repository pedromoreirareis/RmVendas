package com.pedromoreirareisgmail.rmvendas.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Datas {

    public static String getDateTime() {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Date date = new Date();

        return dateFormat.format(date);
    }

    public static String getDate() {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date date = new Date();

        return dateFormat.format(date);
    }

    public static String formatTime(String dataHora) {

        Date novaHora = null;

        SimpleDateFormat horaFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {

            novaHora = horaFormat.parse(dataHora);

        } catch (ParseException e) {

            e.printStackTrace();
        }

        horaFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        return horaFormat.format(novaHora);
    }

    public static String formatDatePesquisa(String dataHora) {

        Date novaHora = null;

        SimpleDateFormat horaFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {

            novaHora = horaFormat.parse(dataHora);

        } catch (ParseException e) {

            e.printStackTrace();
        }

        horaFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return horaFormat.format(novaHora);
    }

    public static String dateSetListenerPesquisa(int year, int month, int day) {

        month = month + 1;

        String yearString = String.valueOf(year);
        String monthString;
        String dayString;

        if (month < 10) {

            monthString = "0" + String.valueOf(month);

        } else {

            monthString = String.valueOf(month);
        }

        if (day < 10) {

            dayString = "0" + String.valueOf(day);

        } else {

            dayString = String.valueOf(day);
        }

        return yearString + "-" + monthString + "-" + dayString;
    }

    public static String dateSetListenerTitle(int year, int month, int day) {

        month = month + 1;

        String yearString = String.valueOf(year);
        String monthString;
        String dayString;

        if (month < 10) {

            monthString = "0" + String.valueOf(month);

        } else {

            monthString = String.valueOf(month);
        }

        if (day < 10) {

            dayString = "0" + String.valueOf(day);

        } else {

            dayString = String.valueOf(day);
        }

        return dayString + "/" + monthString + "/" + yearString;
    }
}
