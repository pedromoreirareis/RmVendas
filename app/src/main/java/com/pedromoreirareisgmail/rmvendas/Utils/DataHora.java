package com.pedromoreirareisgmail.rmvendas.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DataHora {

    /**
     * Apenas pega o horário do sistema, não faz conversão para outro formato de data ou hora
     *
     * @return Data e hora do sistema no formato "yyyy-MM-dd HH:mm:ss"
     */
    public static String getDataHoraSistema() {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Date dataSistema = new Date();

        return dateFormat.format(dataSistema);
    }


    /**
     * Pega escolhida no caléndario ou a data atual e faz a conversão do formato do sistema para o
     * formato usado no Brasil
     *
     * @return Data do Sistema no formato "dd/MM/yyyy". Converte de "yyyy-MM-dd" para "dd/MM/yyyy"
     */
    public static String formatDataBr() {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date dataSistema = new Date();

        return dateFormat.format(dataSistema);
    }


    /**
     * Recebe uma data e hora do sistema no formato "yyyy-MM-dd HH:mm:ss" e  formata para o
     * "HH:mm", que são as horas e minuto em formato do Brasil
     *
     * @param dataHora data e hora recebida do sistema no formato "yyyy-MM-dd HH:mm:ss"
     * @return Hora e minutos no formato "HH:mm"
     */
    public static String formatHoraMinutoBr(String dataHora) {

        Date dataSistema = null;

        SimpleDateFormat horaFormatar =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {

            dataSistema = horaFormatar.parse(dataHora);

        } catch (ParseException e) {

            e.printStackTrace();
        }

        horaFormatar = new SimpleDateFormat("HH:mm", Locale.getDefault());

        return horaFormatar.format(dataSistema);
    }

    /**
     * Recebe uma data do sistema - do dia - ou escolhida no calendario no formato "yyyy-MM-dd HH:mm:ss"
     * e formata para "yyyy-MM-dd", que sera utilizado para fazer uma pesquisa de dados cadastrados
     * no sistema nessa data
     *
     * @param dataHora data e hora do sistema ou escolhida no calendario no formato "yyyy-MM-dd HH:mm:ss"
     * @return data no formato "yyyy-MM-dd" para fazer a pesquisa no banco de dados
     */
    public static String formatDataPesquisarBancoDados(String dataHora) {

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
     * Pega do calendário a data escolhida e formata o ano, mes e dia, para o formato que esta salvo
     * no banco de dados. No banco de dados a data e hora esta no formato "yyyy-MM-dd HH:mm:ss" e
     * aqui retorna o formato "yyyy-MM-dd", que é utilizado para compara no banco de dados
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

            mes = "0" + String.valueOf(month);

        } else {

            mes = String.valueOf(month);
        }

        if (day < 10) {

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
    public static String dateSetListenerDataTitleBr(int year, int month, int day) {

        month = month + 1;

        String ano = String.valueOf(year);
        String mes;
        String dia;

        if (month < 10) {

            mes = "0" + String.valueOf(month);

        } else {

            mes = String.valueOf(month);
        }

        if (day < 10) {

            dia = "0" + String.valueOf(day);

        } else {

            dia = String.valueOf(day);
        }

        return dia + "/" + mes + "/" + ano;
    }
}
