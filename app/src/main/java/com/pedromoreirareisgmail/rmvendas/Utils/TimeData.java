package com.pedromoreirareisgmail.rmvendas.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class TimeData {

    /**
     * Obtem data e hora do sistema
     *
     * @return data e hora no formato "yyyy-MM-dd HH:mm:ss"
     */
    public static String getDateTime() {

        SimpleDateFormat formatDate =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Date systemDate = new Date();

        return formatDate.format(systemDate);
    }

    /**
     * Obtem data do sistema e formata para padrao utilizado no Brasil
     * Formata de "yyyy-MM-dd" para "dd/MM/yyyy"
     *
     * @return data no formato "dd/MM/yyyy"
     */
    public static String getDateTitleBr() {

        SimpleDateFormat formatDate =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date systemDate = new Date();

        return formatDate.format(systemDate);
    }

    /**
     * Obtem no calendário year(ano),month(mês), day(dia)
     * formata os dados para o formato "yyyy-MM-dd"
     * Essa data sera usada para fazer pesquisa no BD.
     * No BD a data e hora esta no formato "yyyy-MM-dd HH:mm:ss"
     *
     * @param year  ano
     * @param month mes
     * @param day   dia
     * @return data no formato "yyyy-MM-dd"
     */
    public static String getDateSearchDB(int year, int month, int day) {

        month = month + 1;

        String yearString;
        String monthString;
        String dayString;

        yearString = String.valueOf(year);

        // month deve ter dois digitos
        if (month < 10) {

            // Se month tiver 1 digito acrescenta um zero a esquerda
            monthString = "0" + String.valueOf(month);

        } else {

            // Se month tiver 2 digitos apenas converte int para string
            monthString = String.valueOf(month);
        }

        // Day deve ter dois digitos
        if (day < 10) {

            // Se day tiver 1 digito acrescenta um zero a esquerda
            dayString = "0" + String.valueOf(day);

        } else {

            // Se day tiver 2 digitos apenas converte int para string
            dayString = String.valueOf(day);
        }

        return yearString + "-" + monthString + "-" + dayString;
    }

    /**
     * Obtem no calendário year(ano),month(mês), day(dia)
     * formata os dados para o formato "dd-MM-yyyy"
     * Essa data sera usada no titulo das Activitys
     *
     * @param year  ano
     * @param month mes
     * @param day   dia
     * @return data no formato "dd-MM-yyyy", usada nos titulos das Activitys
     */
    public static String getDateTitleBr(int year, int month, int day) {

        month = month + 1;

        String yearString;
        String monthString;
        String dayString;

        yearString = String.valueOf(year);

        // month deve ter dois digitos
        if (month < 10) {

            // Se month tiver 1 digito acrescenta um zero a esquerda
            monthString = "0" + String.valueOf(month);

        } else {

            // Se month tiver 2 digitos apenas converte int para string
            monthString = String.valueOf(month);
        }

        // Day deve ter dois digitos
        if (day < 10) {

            // Se day tiver 1 digito acrescenta um zero a esquerda
            dayString = "0" + String.valueOf(day);

        } else {

            // Se day tiver 2 digitos apenas converte int para string
            dayString = String.valueOf(day);
        }

        return dayString + "/" + monthString + "/" + yearString;
    }

    /**
     * Recebe data no formato "yyyy-MM-dd HH:mm:ss", formata para aproveitar
     * apenas as horas e minutos
     *
     * @param date data no formato "yyyy-MM-dd HH:mm:ss"
     * @return horas e minutos no formato "HH:mm"
     */
    public static String formatDateToHourAndMinute(String date) {

        Date hourParse = null;

        // Recebe a data no formato "yyyy-MM-dd HH:mm:ss"
        SimpleDateFormat formatHour =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {

            // Analisa a data e retorna sua representação em milisegundos
            hourParse = formatHour.parse(date);

        } catch (ParseException e) {

            e.printStackTrace();
        }

        // Formata a data em milisegundos e retorna apenas as horas e minutos
        formatHour = new SimpleDateFormat("HH:mm", Locale.getDefault());

        return formatHour.format(hourParse);
    }

    /**
     * Recebe data no formato "yyyy-MM-dd HH:mm:ss", e  formata
     * a data no formatdo "dd-MM-yyyy"
     *
     * @param date data no formato "yyyy-MM-dd HH:mm:ss"
     * @return data no formato "dd-MM-yyyy"
     */
    public static String formatDateBr(String date) {

        Date dateParse = null;

        // Recebe data no formato "yyyy-MM-dd HH:mm:ss"
        SimpleDateFormat formatDate =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {

            // Analise a data e retorna sua reapresentacao em milisegundos
            dateParse = formatDate.parse(date);

        } catch (ParseException e) {

            e.printStackTrace();
        }

        // Recebe a data em milisegundos e formata para "dd/MM/yyyy" usado no Brasil
        formatDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        return formatDate.format(dateParse);
    }

    /**
     * Recebe data no formato "yyyy-MM-dd HH:mm:ss", e formata
     * para "yyyy-MM-dd", para pesquisas no banco de dados
     *
     * @param date data no formato "yyyy-MM-dd HH:mm:ss"
     * @return data no formato "yyyy-MM-dd"
     */
    public static String formatDateSearch(String date) {

        Date dateSearchDB = null;

        // Recebe data no formato "yyyy-MM-dd HH:mm:ss"
        SimpleDateFormat formatDate =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {

            // Analise data e converte para milisegundos
            dateSearchDB = formatDate.parse(date);

        } catch (ParseException e) {

            e.printStackTrace();
        }

        // Formata data em milisegundo para o formato "yyyy-MM-dd"
        formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return formatDate.format(dateSearchDB);
    }
}
