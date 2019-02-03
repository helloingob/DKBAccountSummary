package com.helloingob.accsum.utils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Comparator;
import org.zkoss.zul.Listitem;

import com.helloingob.accsum.data.to.TransferTO;

public class TimestampComparator implements Comparator<Object>, Serializable {

    private static final long serialVersionUID = -5912530255855223726L;
    private boolean asc = true;

    public TimestampComparator(boolean asc) {
        this.asc = asc;
    }

    public int compare(Object object1, Object object2) {
        Listitem listitem1 = (Listitem) object1;
        Listitem listitem2 = (Listitem) object2;

        TransferTO transfer1 = listitem1.getValue();
        TransferTO transfer2 = listitem2.getValue();

        LocalDate date1 = transfer1.getDate();
        LocalDate date2 = transfer2.getDate();

        int cmp = (date1.getYear() - date2.getYear());
        if (cmp == 0) {
            cmp = (date1.getMonthValue() - date2.getMonthValue());
            if (cmp == 0) {
                cmp = (date1.getDayOfMonth() - date2.getDayOfMonth());
            }
        }
        return (asc ? 1 : -1) * cmp;
    }

}
