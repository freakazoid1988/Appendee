package com.cfg.appendee.objects;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by davem on 27/09/2015.
 */
public class Participant {
    private int number;
    private long entrata, uscita;

    public Participant(int number, long entrata, long uscita) {
        this.number = number;
        this.entrata = entrata;
        this.uscita = uscita;
    }

    public Participant() {

    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getEntrata() {
        return entrata;
    }

    public void setEntrata(long entrata) {
        this.entrata = entrata;
    }

    public long getUscita() {
        return uscita;
    }

    public void setUscita(long uscita) {
        this.uscita = uscita;
    }

    @Override
    public String toString(){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(getEntrata() * 1000); //Because we stored it in seconds
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String entrata = sdf.format(c.getTime()).toString();
        c.setTimeInMillis(getUscita() * 1000); //Because we stored it in seconds
        String uscita = sdf.format(c.getTime()).toString();

        return Integer.toString(getNumber())+" Entrata: "+entrata+" Uscita: "+uscita;
    }
}
