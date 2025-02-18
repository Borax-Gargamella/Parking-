package com.contest.parking.domain;

import androidx.core.util.Pair;
import com.contest.parking.data.model.Range;
import com.contest.parking.presentation.utils.CustomDateValidator;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class UseCaseDateRangePicker {
    private final List<Range> dateOccupate;
    private final double prezzo;

    public UseCaseDateRangePicker(List<Range> dateOccupate, double prezzo) {
        this.dateOccupate = dateOccupate;
        this.prezzo = prezzo;
    }

    // Metodo di utilità per troncare una data a mezzanotte
    public long truncateToDay(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    // Valida l'intervallo: controlla ogni giorno se è occupato
    private boolean checkIntervalValid(long startDay, long endDay) {
        long oneDay = 86400000L;
        for (long day = startDay; day <= endDay; day += oneDay) {
            for (Range range : dateOccupate) {
                if (day >= range.getStart() && day <= range.getEnd()) {
                    return false;
                }
            }
        }
        return true;
    }

    // Callback per restituire il risultato al chiamante
    public interface DateRangePickerCallback {
        void onDateRangeSelected(long startDay, long endDay);
        void onDateRangeInvalid();
    }

    // Costruisce e configura il MaterialDatePicker con i relativi constraint e listener
    public MaterialDatePicker<Pair<Long, Long>> buildPicker(DateRangePickerCallback callback) {
        long todayMidnight = truncateToDay(System.currentTimeMillis());

        // 1. Validator per disabilitare le date precedenti a oggi
        CalendarConstraints.DateValidator minDateValidator = DateValidatorPointForward.from(todayMidnight);

        // 2. Combinazione dei validator: disabilita giorni passati e quelli occupati
        CalendarConstraints.DateValidator compositeValidator =
                CompositeDateValidator.allOf(
                        Arrays.asList(minDateValidator, new CustomDateValidator(dateOccupate))
                );

        // 3. Costruzione dei constraint per il calendario
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setStart(todayMidnight)
                .setValidator(compositeValidator);

        // 4. Costruzione del range picker
        MaterialDatePicker<Pair<Long, Long>> rangePicker = MaterialDatePicker.Builder
                .dateRangePicker()
                .setTitleText("Seleziona l'intervallo di giorni liberi")
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        // 5. Impostazione del listener per il risultato
        rangePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null) {
                long startDay = truncateToDay(selection.first);
                long endDay = truncateToDay(selection.second);

                if (!checkIntervalValid(startDay, endDay)) {
                    callback.onDateRangeInvalid();
                    return;
                }

                callback.onDateRangeSelected(startDay, endDay);
            }
        });

        return rangePicker;
    }
}
