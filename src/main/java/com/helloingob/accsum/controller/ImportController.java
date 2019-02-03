package com.helloingob.accsum.controller;

import java.util.HashMap;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

import com.helloingob.accsum.data.dao.TransferDAO;
import com.helloingob.accsum.data.to.TransferTO;
import com.helloingob.accsum.handler.ImporterHandler;
import com.helloingob.accsum.handler.WindowHandler;
import com.helloingob.accsum.utils.AmountComparator;
import com.helloingob.accsum.utils.TimestampComparator;

public class ImportController extends GenericForwardComposer<Component> {
    private static final long serialVersionUID = -7824377904568650070L;
    private Listheader lhdDate;
    private Listheader lhdAmount;
    private Listbox lbxActivities;
    private Checkbox chbxDuplicate;

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
        init();
    }

    private void init() {
        fillTransferListbox();
    }

    private void fillTransferListbox() {
        lbxActivities.getItems().clear();

        for (TransferTO transfer : TransferDAO.getWithNoTagSet()) {
            Listitem listitem = new Listitem();
            listitem.setValue(transfer);
            
            listitem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    onClick$btnEdit();
                }
            });
            
            listitem.appendChild(new Listcell(transfer.getFormattedDate()));
            listitem.appendChild(new Listcell(transfer.getFormattedAmount()));
            listitem.appendChild(new Listcell(transfer.getApplicant().getName()));
            listitem.appendChild(new Listcell(transfer.getFormattedTags()));
            listitem.appendChild(new Listcell(transfer.getComment()));
            lbxActivities.appendChild(listitem);
        }
    }

    public void onClick$btnEdit() {
        Listitem listitem = lbxActivities.getSelectedItem();
        if (listitem == null) {
            Clients.showNotification("Please select transfer!", Clients.NOTIFICATION_TYPE_WARNING, lbxActivities, "end_center", 3000);
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

    public void onUpload$btnImport(UploadEvent event) {
        ImporterHandler.importSummary(event.getMedia().getStringData(), chbxDuplicate.isChecked());
        init();
    }

    public void onClick$btnOk() {
        Events.postEvent("onReload", this.self, null);
        this.self.detach();
    }

}
