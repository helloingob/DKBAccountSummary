package com.helloingob.accsum.data.to;

public class TagTO {

    private Integer pk;
    private String title;
    private Boolean visible;

    public TagTO() {}

    public TagTO(String title, boolean visible) {
        this.title = title;
        this.visible = visible;
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

}
