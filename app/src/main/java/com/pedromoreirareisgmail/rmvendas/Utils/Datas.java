package com.pedromoreirareisgmail.rmvendas.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Datas {

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd/MM/yyyy", Locale.getDefault());

        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm:ss", Locale.getDefault());

        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String formatDateTimeEmTime(String dataHora){
        Date novaHora = null;
        SimpleDateFormat horaFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());

        try {
            novaHora = horaFormat.parse(dataHora);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        horaFormat = new SimpleDateFormat("HH:mm",Locale.getDefault());


        return horaFormat.format(novaHora);
    }

    public static String formatDatePesquisa(String dataHora) {
        Date novaHora = null;
        SimpleDateFormat horaFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            novaHora = horaFormat.parse(dataHora);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        horaFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


        return horaFormat.format(novaHora);
    }

}
