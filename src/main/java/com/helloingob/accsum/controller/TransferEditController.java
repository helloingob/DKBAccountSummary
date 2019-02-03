package com.helloingob.accsum.controller;

import java.util.ArrayList;
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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import com.helloingob.accsum.data.dao.TagDAO;
import com.helloingob.accsum.data.dao.TransferDAO;
import com.helloingob.accsum.data.to.TagTO;
import com.helloingob.accsum.data.to.TransferTO;

public class TransferEditController extends GenericForwardComposer<Component> {
    private static final long serialVersionUID = -3253307595606220856L;
    private Textbox tbxTitle;
    private Textbox tbxComment;
    private Listbox lbxTags;
    private Combobox cbxTags;

    private TransferTO transfer;
    private List<TagTO> tags;

    @Override
    public void doAfterCompose(Component comp) {
        try {
            super.doAfterCompose(comp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        transfer = (TransferTO) arg.get("transfer");
        tbxTitle.setValue(transfer.getApplicant().getName());
        tbxComment.setValue(transfer.getComment());

        tags = new ArrayList<TagTO>();
        for (TagTO tag : transfer.getTags()) {
            tags.add(tag);
        }
        init();
    }

    private void init() {
        fillListbox();
        fillCombobox();
        if (cbxTags.getItemCount() > 0) {
            cbxTags.setSelectedIndex(0);
        }
    }

    private void fillCombobox() {
        cbxTags.getItems().clear();
        for (Map.Entry<String, TagTO> entry : TagDAO.get(tags).entrySet()) {
            Comboitem comboitem = new Comboitem(entry.getKey());
            comboitem.setValue(entry.getValue());
            cbxTags.appendChild(comboitem);
        }
    }

    private void fillListbox() {
        lbxTags.getItems().clear();
        lbxTags.disableClientUpdate(true);
        for (TagTO tag : tags) {
            Listitem listitem = new Listitem();
            listitem.setValue(tag);
            listitem.appendChild(new Listcell(tag.getTitle()));
            listitem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    event.getTarget().detach();
                    syncTags();
                    init();
                }
            });
            lbxTags.appendChild(listitem);
        }
        lbxTags.disableClientUpdate(false);
        lbxTags.invalidate();
    }

    public void onClick$btnAdd() {
        if (cbxTags.getSelectedItem() != null) {
            TagTO tag = (TagTO) cbxTags.getSelectedItem().getValue();
            tags.add(tag);
            init();
        } else {
            Clients.showNotification("Add tag first!", Clients.NOTIFICATION_TYPE_ERROR, null, "top_center", 3000);
        }
    }

    public void onClick$btnOk() {
        if (tbxComment.getValue().isEmpty()) {
            transfer.setComment(null);
        } else {
            transfer.setComment(tbxComment.getValue());
        }
        transfer.setTags(tags);
        if (TransferDAO.update(transfer)) {
            Clients.showNotification("Transfer successfully updated.", Clients.NOTIFICATION_TYPE_INFO, null, "top_center", 3000);
            Events.postEvent("onReload", this.self, null);
            this.self.detach();
        } else {
            Clients.showNotification("Error occured!", Clients.NOTIFICATION_TYPE_ERROR, null, "top_center", 3000);
        }

    }

    private void syncTags() {
        tags.clear();
        for (Listitem listitem : lbxTags.getItems()) {
            tags.add((TagTO) listitem.getValue());
        }
    }

}
