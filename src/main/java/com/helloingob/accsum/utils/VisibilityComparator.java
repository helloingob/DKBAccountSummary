package com.helloingob.accsum.utils;

import java.io.Serializable;
import java.util.Comparator;
import org.zkoss.zul.Listitem;

import com.helloingob.accsum.data.to.TagTO;

public class VisibilityComparator implements Comparator<Object>, Serializable {

    private static final long serialVersionUID = -5912530255855223726L;
    private boolean asc = true;

    public VisibilityComparator(boolean asc) {
        this.asc = asc;
    }

    public int compare(Object object1, Object object2) {
        Listitem listitem1 = (Listitem) object1;
        Listitem listitem2 = (Listitem) object2;

        TagTO tag1 = listitem1.getValue();
        TagTO tag2 = listitem2.getValue();

        return (asc ? 1 : -1) * tag1.isVisible().compareTo(tag2.isVisible());
    }

}
