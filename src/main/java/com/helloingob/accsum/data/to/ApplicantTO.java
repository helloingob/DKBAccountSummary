package com.helloingob.accsum.data.to;

public class ApplicantTO {

    private Integer pk;
    private String name;
    private String iban;
    private String bic;

    public ApplicantTO() {}

    public ApplicantTO(String name, String iban, String bic) {
        this.name = name;
        this.iban = iban;
        this.bic = bic;
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getTooltip() {
        return this.getIban() + "\n" + this.getBic();
    }

}
