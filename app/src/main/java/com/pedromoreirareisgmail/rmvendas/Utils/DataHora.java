package com.pedromoreirareisgmail.rmvendas.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DataHora {

    /**
     * Pega a data e hora do sistema no formato - USA - "yyyy-MM-dd HH:mm:ss"
     * Não é feita nenhuma formatação
     *
     * @return Data e hora do sistema no formato "yyyy-MM-dd HH:mm:ss"
     */
    public static String obterDataHoraSistema() {

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Date dataSistema = new Date();

        return dateFormat.format(dataSistema);
    }


    /**
     * Pega data e hora do sistema no formato - USA - "yyyy-MM-dd HH:mm:ss"
     * e formata a data para - BR - "dd/MM/yyyy" formato de data usado no Brasil
     *
     * @return Retorna data do sistema no formato "dd/MM/yyyy". Formata de "yyyy-MM-dd" para "dd/MM/yyyy"
     */
    public static String obterFormatarDataBrTitulo() {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date dataSistema = new Date();

        return dateFormat.format(dataSistema);
    }


    /**
     * Recebe uma data e hora no formato "yyyy-MM-dd HH:mm:ss" e  formata a hora para
     * "HH:mm", que é o formtato de hora usado no Brasil
     *
     * @param dataHora data e hora recebida no formato "yyyy-MM-dd HH:mm:ss"
     * @return Hora e minutos no formato "HH:mm"
     */
    public static String formatarHoraMinutoBr(String dataHora) {

        Date dataHoraParse = null;

        SimpleDateFormat horaFormatar =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {

            dataHoraParse = horaFormatar.parse(dataHora);

        } catch (ParseException e) {

            e.printStackTrace();
        }

        horaFormatar = new SimpleDateFormat("HH:mm", Locale.getDefault());

        return horaFormatar.format(dataHoraParse);
    }


    /**
     * Recebe uma data e hora no formato "yyyy-MM-dd HH:mm:ss" e  formata para a data
     * "dd-MM-yyyy", que é o formato de data usado no Brasil
     *
     * @param dataHora data e hora recebida no formato "yyyy-MM-dd HH:mm:ss"
     * @return data no formato "dd-MM-yyyy"
     */
    public static String formatarDataBr(String dataHora) {

        Date dataHoraParse = null;

        SimpleDateFormat dataFormatar =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {

            dataHoraParse = dataFormatar.parse(dataHora);

        } catch (ParseException e) {

            e.printStackTrace();
        }

        dataFormatar = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        return dataFormatar.format(dataHoraParse);
    }

    /**
     * Recebe uma data e hora(do sistema ou escolhida no calendario) no formato "yyyy-MM-dd HH:mm:ss"
     * e formata a data para "yyyy-MM-dd", que sera utilizado para fazer uma pesquisa no BD
     *
     * @param dataHora data e hora no formato "yyyy-MM-dd HH:mm:ss"
     * @return data no formato "yyyy-MM-dd" para fazer a pesquisa no BD
     */
    public static String formatarDataPesquisarBancoDados(String dataHora) {

        Date dataPesquisaBancoDados = null;

        SimpleDateFormat dataFormatar =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {

            dataPesquisaBancoDados = dataFormatar.parse(dataHora);

        } catch (ParseException e) {

            e.printStackTrace();
        }

        dataFormatar = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return dataFormatar.format(dataPesquisaBancoDados);
    }


    /**
     * Recebe do calendário year(ano),month(mês), day(dia), e formata uma data no formato "yyyy-MM-dd"
     * Essa data sera usada para fazer pesquisa no BD. No BD a data e hora esta no formato "yyyy-MM-dd HH:mm:ss"
     *
     * @param year  ano
     * @param month mes
     * @param day   dia
     * @return uma data no formato "yyyy-MM-dd" que é utilizado para pesquisa no banco de dados
     */
    public static String dateSetListenerPesquisarBancoDados(int year, int month, int day) {

        month = month + 1;

        String ano = String.valueOf(year);
        String mes;
        String dia;

        if (month < 10) {

            // se month for "7" então mes fica "07"
            mes = "0" + String.valueOf(month);

        } else {

            mes = String.valueOf(month);
        }

        if (day < 10) {

            // se day for "9" então dia fica "09"
            dia = "0" + String.valueOf(day);

        } else {

            dia = String.valueOf(day);
        }

        return ano + "-" + mes + "-" + dia;
    }

    /**
     * Pega do calendário a data escolhida e formata o ano, mes e dia, para o formato "dd-MM-yyyy"
     * que é mais conhecido no Brasil
     *
     * @param year  ano
     * @param month mes
     * @param day   dia
     * @return data no formato "dd-MM-yyyy", que é formato conhecido no Brasil
     */
    public static String dateSetListenerDataBrTitulo(int year, int month, int day) {

        month = month + 1;

        String ano = String.valueOf(year);
        String mes;
        String dia;

        if (month < 10) {

            // se month for "7" então mes fica "07"
            mes = "0" + String.valueOf(month);

        } else {

            mes = String.valueOf(month);
        }

        if (day < 10) {

            // se day for "9" então dia fica "09"
            dia = "0" + String.valueOf(day);

        } else {

            dia = String.valueOf(day);
        }

        return dia + "/" + mes + "/" + ano;
    }
}
