package com.helloingob.accsum.data.to;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransferTO {

    private Integer pk;
    private LocalDate date;
    private PostingTO posting;
    private ApplicantTO applicant;
    private String reasonForPayment;
    private Double amount;
    private String comment;
    private List<TagTO> tags = new ArrayList<TagTO>();

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return getDate().format(formatter);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public PostingTO getPosting() {
        return posting;
    }

    public void setPosting(PostingTO posting) {
        this.posting = posting;
    }

    public ApplicantTO getApplicant() {
        return applicant;
    }

    public void setApplicant(ApplicantTO applicant) {
        this.applicant = applicant;
    }

    public String getReasonForPayment() {
        return reasonForPayment;
    }

    public void setReasonForPayment(String reasonForPayment) {
        this.reasonForPayment = reasonForPayment;
    }

    public Double getAmount() {
        return amount;
    }

    public String getFormattedAmount() {
        return getAmount().toString() + "€";
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<TagTO> getTags() {
        return tags;
    }

    public void setTags(List<TagTO> tags) {
        this.tags = tags;
    }

    public void addTag(TagTO tag) {
        tags.add(tag);
    }

    public String getFormattedTags() {
        String tags = "";
        for (TagTO tag : getTags()) {
            tags += tag.getTitle() + ", ";
        }
        if (tags.isEmpty()) {
            return "-";
        } else {
            return tags.substring(0, tags.length() - 2);
        }
    }

    @Override
    public String toString() {
        return "TransferTO [pk=" + pk + ", date=" + date + ", posting=" + posting.getText() + ", applicant=" + applicant.getName() + ", reasonForPayment=" + reasonForPayment + ", amount=" + amount + ", comment=" + comment + ", tags=" + tags + "]";
    }

    public String toShortString() {
        return "[" + date + "] " + posting.getText() + " - '" + applicant.getName() + "' (" + amount + "€)";
    }

}
