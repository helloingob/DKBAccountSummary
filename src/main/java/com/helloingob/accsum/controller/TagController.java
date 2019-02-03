package com.helloingob.accsum.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;

import com.helloingob.accsum.data.dao.TagDAO;
import com.helloingob.accsum.data.to.TagTO;

public class TagController extends GenericForwardComposer<Component> {
    private static final long serialVersionUID = -8320778906220990022L;
    private Textbox tbxTitle;
    private Checkbox chbxVisible;

    private TagTO tag;

    @Override
    public void doAfterCompose(Component comp) {
        try {
            super.doAfterCompose(comp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        init();
    }

    private void init() {
        if (arg.containsKey("tag")) {
            tag = (TagTO) arg.get("tag");
            tbxTitle.setValue(tag.getTitle());
            chbxVisible.setChecked(tag.isVisible());
        }
    }

    public void onClick$btnOk() {
        if (tbxTitle.getValue().isEmpty()) {
            Clients.showNotification("Please add a title!", Clients.NOTIFICATION_TYPE_WARNING, tbxTitle, "end_center", 3000);
        } else {
            if (tag == null) {
                if (TagDAO.insert(new TagTO(tbxTitle.getValue(), chbxVisible.isChecked()))) {
                    Clients.showNotification("Tag successfully added.", Clients.NOTIFICATION_TYPE_INFO, null, "top_center", 3000);
                    Events.postEvent("onReload", this.self, null);
                    this.self.detach();
                } else {
                    Clients.showNotification("Error occured!", Clients.NOTIFICATION_TYPE_ERROR, null, "top_center", 3000);
                }
            } else {
                tag.setTitle(tbxTitle.getValue());
                tag.setVisible(chbxVisible.isChecked());
                if (TagDAO.update(tag)) {
                    Clients.showNotification("Tag successfully updated.", Clients.NOTIFICATION_TYPE_INFO, null, "top_center", 3000);
                    Events.postEvent("onReload", this.self, null);
                    this.self.detach();
                } else {
                    Clients.showNotification("Error occured!", Clients.NOTIFICATION_TYPE_ERROR, null, "top_center", 3000);
                }
            }
        }
    }

}
