package com.contest.parking.presentation.utils;

import android.os.Parcel;
import android.os.Parcelable;
import com.contest.parking.data.model.Range;
import com.google.android.material.datepicker.CalendarConstraints;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomDateValidator implements CalendarConstraints.DateValidator, Parcelable {

    private final List<Range> dateOccupate;

    public CustomDateValidator(List<Range> dateOccupate) {
        this.dateOccupate = dateOccupate;
    }

    @Override
    public boolean isValid(long date) {
        // "date" rappresenta la mezzanotte UTC del giorno in esame
        // Tronchiamo a mezzanotte locale se vuoi essere coerente col fuso orario
        long dayStart = truncateToDay(date);
        long dayEnd = dayStart + 86400000L - 1; // Fine della giornata (23:59:59)

        // Se [dayStart, dayEnd] si sovrappone a uno qualunque dei Range esistenti, invalidiamo
        for (Range r : dateOccupate) {
            // Sovrapposizione se dayStart <= r.end e r.start <= dayEnd
            if (dayStart <= r.end && r.start <= dayEnd) {
                return false;
            }
        }
        return true;
    }

    private long truncateToDay(long timeInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    // Metodi necessari per Parcelable (boilerplate)
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // In un progetto semplice, possiamo lasciare minimal
        dest.writeInt(dateOccupate.size());
    }

    public static final Creator<CustomDateValidator> CREATOR = new Creator<CustomDateValidator>() {
        @Override
        public CustomDateValidator createFromParcel(Parcel source) {
            // In un caso base, non ricarichiamo i dati
            return new CustomDateValidator(new ArrayList<>());
        }
        @Override
        public CustomDateValidator[] newArray(int size) {
            return new CustomDateValidator[size];
        }
    };
}

