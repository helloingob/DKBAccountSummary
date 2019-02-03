package com.helloingob.accsum.data.to;

public class PostingTO {

    private Integer pk;
    private String text;

    public PostingTO() {}

    public PostingTO(String text) {
        this.text = text;
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
