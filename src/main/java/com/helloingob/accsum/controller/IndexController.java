package com.helloingob.accsum.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

import com.helloingob.accsum.data.dao.TagDAO;
import com.helloingob.accsum.data.dao.TransferDAO;
import com.helloingob.accsum.data.to.TagTO;
import com.helloingob.accsum.data.to.TransferTO;
import com.helloingob.accsum.handler.ChartHandler;
import com.helloingob.accsum.handler.WindowHandler;
import com.helloingob.accsum.utils.AmountComparator;
import com.helloingob.accsum.utils.DateConverter;
import com.helloingob.accsum.utils.TimestampComparator;

public class IndexController extends GenericForwardComposer<Component> {
    private static final long serialVersionUID = -1920534633258616395L;
    private Datebox dbxFrom;
    private Datebox dbxTo;
    private Listbox lbxActivities;
    private Combobox cbxSearch;
    private Listheader lhdDate;
    private Listheader lhdAmount;

    private Hbox hbxChart;

    @Override
    public void doAfterCompose(Component comp) {
        try {
            super.doAfterCompose(comp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        lhdDate.setSortAscending(new TimestampComparator(true));
        lhdDate.setSortDescending(new TimestampComparator(false));
        lhdAmount.setSortAscending(new AmountComparator(true));
        lhdAmount.setSortDescending(new AmountComparator(false));

        initDateboxes();
        init();
    }

    private void init() {
        fillTransferListbox();
        onOK$cbxSearch();
        fillTagCombobox();
    }

    public void onClick$btnTags() {
        WindowHandler.createWindow("Tag Manager", "/tags.zul", null, false).addEventListener("onReload", new EventListener<Event>() {
            public void onEvent(Event arg0) throws Exception {
                init();
            }
        });
    }

    public void onClick$btnImport() {
        WindowHandler.createWindow("Import", "/import.zul", null, true).addEventListener("onReload", new EventListener<Event>() {
            public void onEvent(Event arg0) throws Exception {
                init();
            }
        });
    }

    public void onClick$btnEdit() {
        Listitem listitem = lbxActivities.getSelectedItem();
        if (listitem == null) {
            Clients.showNotification("Please select an transfer!", Clients.NOTIFICATION_TYPE_WARNING, lbxActivities, "end_center", 3000);
        } else {
            TransferTO transfer = (TransferTO) listitem.getValue();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("transfer", transfer);
            WindowHandler.createWindow("Edit transfer", "/transferedit.zul", map, false).addEventListener("onReload", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    init();
                }
            });
        }
    }

    private void fillTagCombobox() {
        cbxSearch.getItems().clear();
        for (Map.Entry<String, TagTO> entry : TagDAO.get(DateConverter.convert(dbxFrom.getValue()), DateConverter.convert(dbxTo.getValue())).entrySet()) {
            Comboitem comboitem = new Comboitem(entry.getKey());
            comboitem.setValue(entry.getValue());
            cbxSearch.appendChild(comboitem);
        }
    }

    private void fillTransferListbox() {
        lbxActivities.getItems().clear();
        List<TransferTO> transfers = TransferDAO.get(DateConverter.convert(dbxFrom.getValue()), DateConverter.convert(dbxTo.getValue()));

        for (TransferTO transfer : transfers) {
            Listitem listitem = new Listitem();

            listitem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    onClick$btnEdit();
                }
            });

            listitem.setValue(transfer);
            listitem.appendChild(new Listcell(transfer.getFormattedDate()));
            Listcell listcellAmount = new Listcell(transfer.getFormattedAmount());
            listcellAmount.setStyle("color:red;");
            if (transfer.getAmount() > 0) {
                listcellAmount.setStyle("color:green;");
            }
            listitem.appendChild(listcellAmount);
            Listcell listcell = new Listcell(transfer.getApplicant().getName());
            listcell.setTooltiptext(transfer.getApplicant().getTooltip() + "\n\n" + transfer.getReasonForPayment());
            listitem.appendChild(listcell);
            listitem.appendChild(new Listcell(transfer.getFormattedTags()));

            if (transfer.getComment() != null && transfer.getComment().length() > 10) {
                Listcell listcellComment = new Listcell(transfer.getComment().substring(0, 10) + "...");
                listcellComment.setTooltiptext(transfer.getComment());
                listitem.appendChild(listcellComment);
            } else {
                listitem.appendChild(new Listcell(transfer.getComment()));
            }
            lbxActivities.appendChild(listitem);
        }

        ChartHandler.drawChart(hbxChart, transfers, false);
    }

    private void initDateboxes() {
        LocalDate initial = LocalDate.now();
        dbxFrom.setValue(Date.from(initial.minusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dbxTo.setValue(Date.from(initial.minusMonths(1).withDayOfMonth(initial.minusMonths(1).lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public void onOK$cbxSearch() {
        List<TransferTO> transfers = new ArrayList<TransferTO>();
        for (Listitem listitem : lbxActivities.getItems()) {
            listitem.setVisible(false);
            for (Component component : listitem.getChildren()) {
                Listcell listcell = (Listcell) component;
                if (listcell.getLabel().toLowerCase().contains(cbxSearch.getText().toLowerCase()) || cbxSearch.getText().isEmpty()) {
                    listitem.setVisible(true);
                    transfers.add(listitem.getValue());
                    break;
                }
            }
        }
        if (cbxSearch.getText().toLowerCase().isEmpty()) {
            ChartHandler.drawChart(hbxChart, transfers, false);
        } else {
            ChartHandler.drawChart(hbxChart, transfers, true);
        }
    }

    public void onOK$dbxFrom() {
        init();
    }

    public void onOK$dbxTo() {
        init();
    }

    public void onClick$btnGo() {
        init();
    }

    public void onClick$btnForward() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dbxFrom.getValue());
        calendar.add(Calendar.MONTH, 1);
        dbxFrom.setValue(calendar.getTime());

        calendar.setTime(dbxTo.getValue());
        calendar.add(Calendar.MONTH, 1);
        dbxTo.setValue(calendar.getTime());

        init();
    }

    public void onClick$btnBackward() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dbxFrom.getValue());
        calendar.add(Calendar.MONTH, -1);
        dbxFrom.setValue(calendar.getTime());

        calendar.setTime(dbxTo.getValue());
        calendar.add(Calendar.MONTH, -1);
        dbxTo.setValue(calendar.getTime());

        init();
    }

}
