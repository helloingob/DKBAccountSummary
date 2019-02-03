package com.helloingob.accsum.controller;

import java.util.HashMap;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Span;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.helloingob.accsum.data.dao.TagDAO;
import com.helloingob.accsum.data.to.TagTO;
import com.helloingob.accsum.handler.WindowHandler;
import com.helloingob.accsum.utils.VisibilityComparator;

public class TagsController extends GenericForwardComposer<Component> {
    private static final long serialVersionUID = -2349434469580866428L;
    private Listbox lbxTags;
    private Listheader lhdVisible;
    private Textbox tbxSearch;

    @Override
    public void doAfterCompose(Component comp) {
        try {
            super.doAfterCompose(comp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        lhdVisible.setSortAscending(new VisibilityComparator(true));
        lhdVisible.setSortDescending(new VisibilityComparator(false));

        init();
    }

    private void init() {
        fillListbox();
    }

    private void fillListbox() {
        lbxTags.getItems().clear();
        for (Map.Entry<String, TagTO> entry : TagDAO.get().entrySet()) {
            Listitem listitem = new Listitem();
            TagTO tag = entry.getValue();
            listitem.setValue(tag);
            listitem.appendChild(new Listcell(entry.getKey()));
            if (tag.isVisible()) {
                listitem.appendChild(new Listcell());
            } else {
                Listcell listcell = new Listcell();
                Span span = new Span();
                span.setSclass("z-icon-times");
                listcell.appendChild(span);
                listitem.appendChild(listcell);
            }
            lbxTags.appendChild(listitem);
        }
    }

    public void onClick$btnAdd() {
        Window window = WindowHandler.createWindow("Add tag", "/tag.zul", null, false);
        window.addEventListener("onReload", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                fillListbox();
            }
        });
    }

    public void onClick$btnEdit() {
        Listitem listitem = lbxTags.getSelectedItem();
        if (listitem == null) {
            Clients.showNotification("Please select a tag", Clients.NOTIFICATION_TYPE_WARNING, lbxTags, "end_center", 3000);
        } else {
            TagTO tag = (TagTO) listitem.getValue();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tag", tag);
            Window window = WindowHandler.createWindow("Edit tag", "/tag.zul", map, false);
            window.addEventListener("onReload", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    fillListbox();
                }
            });
        }
    }

    public void onClick$btnDelete() {
        Listitem listitem = lbxTags.getSelectedItem();
        if (listitem == null) {
            Clients.showNotification("Please select a tag!", Clients.NOTIFICATION_TYPE_WARNING, lbxTags, "end_center", 3000);
        } else {
            final TagTO tag = (TagTO) listitem.getValue();
            if (TagDAO.isUsed(tag)) {
                Clients.showNotification("Tag is in use!", Clients.NOTIFICATION_TYPE_WARNING, listitem, "end_center", 3000);
            } else {
                Messagebox.show("Do you really want to delete this tag?", "Confirmation", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new EventListener<Event>() {
                    public void onEvent(Event evt) throws InterruptedException {
                        if (Events.ON_OK.equals(evt.getName())) {
                            if (TagDAO.delete(tag)) {
                                Clients.showNotification("Tag successfully deleted.", Clients.NOTIFICATION_TYPE_INFO, null, "top_center", 3000);
                                fillListbox();
                            } else {
                                Clients.showNotification("Error occured!", Clients.NOTIFICATION_TYPE_ERROR, null, "top_center", 3000);
                            }
                        }
                    }
                });
            }
        }
    }

    public void onClick$btnOk() {
        Events.postEvent("onReload", this.self, null);
        this.self.detach();
    }

    public void onOK$tbxSearch() {
        for (Listitem listitem : lbxTags.getItems()) {
            listitem.setVisible(false);
            for (Component component : listitem.getChildren()) {
                Listcell listcell = (Listcell) component;
                if (listcell.getLabel().toLowerCase().contains(tbxSearch.getText().toLowerCase()) || tbxSearch.getText().isEmpty()) {
                    listitem.setVisible(true);
                    break;
                }
            }
        }
    }

}
