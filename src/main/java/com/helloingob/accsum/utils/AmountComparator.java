package com.helloingob.accsum.utils;

import java.io.Serializable;
import java.util.Comparator;
import org.zkoss.zul.Listitem;

import com.helloingob.accsum.data.to.TransferTO;

public class AmountComparator implements Comparator<Object>, Serializable {

    private static final long serialVersionUID = -5912530255855223726L;
    private boolean asc = true;

    public AmountComparator(boolean asc) {
        this.asc = asc;
    }

    public int compare(Object object1, Object object2) {
        Listitem listitem1 = (Listitem) object1;
        Listitem listitem2 = (Listitem) object2;

        TransferTO transfer1 = listitem1.getValue();
        TransferTO transfer2 = listitem2.getValue();

        return (asc ? 1 : -1) * transfer1.getAmount().compareTo(transfer2.getAmount());
    }

}
