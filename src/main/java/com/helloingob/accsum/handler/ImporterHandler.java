package com.helloingob.accsum.handler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

import com.helloingob.accsum.data.dao.ApplicantDAO;
import com.helloingob.accsum.data.dao.PostingDAO;
import com.helloingob.accsum.data.dao.TagDAO;
import com.helloingob.accsum.data.dao.TransferDAO;
import com.helloingob.accsum.data.to.ApplicantTO;
import com.helloingob.accsum.data.to.PostingTO;
import com.helloingob.accsum.data.to.TagTO;
import com.helloingob.accsum.data.to.TransferTO;

public class ImporterHandler {

    public static void importSummary(String content, boolean ignoreDuplicates) {
        Map<String, PostingTO> postings = PostingDAO.get();
        Map<String, ApplicantTO> applicants = ApplicantDAO.get();
        List<String> lines = new ArrayList<String>(Arrays.asList(content.split("\n")));

        int untaggedCount = 0;
        int successfullyAddedCount = 0;
        int duplicateCount = 0;
        for (int i = 7; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] lineParts = line.split(";");
            TransferTO transfer = new TransferTO();

            //Date
            transfer.setDate(convertDate(clean(lineParts[0])));

            //Posting Text
            if (!postings.containsKey(clean(lineParts[2]))) {
                PostingTO posting = new PostingTO(clean(lineParts[2]));
                if (PostingDAO.insert(posting)) {
                    postings.put(posting.getText(), posting);
                }
            }
            transfer.setPosting(postings.get(clean(lineParts[2])));

            //Applicant
            if (!applicants.containsKey(clean(lineParts[5]))) {
                ApplicantTO applicant = new ApplicantTO(clean(lineParts[3]), clean(lineParts[5]), clean(lineParts[6]));
                if (ApplicantDAO.insert(applicant)) {
                    applicants.put(applicant.getIban(), applicant);
                }
            }
            transfer.setApplicant(applicants.get(clean(lineParts[5])));

            //Reason
            transfer.setReasonForPayment(clean(lineParts[4]));
            //Amount
            transfer.setAmount(convertAmount(clean(lineParts[7])));
            //Tag
            for (TagTO tag : TagDAO.get(transfer.getApplicant().getIban())) {
                transfer.addTag(tag);
            }

            if (transfer.getTags().isEmpty()) {
                untaggedCount++;
            }

            if (!ignoreDuplicates) {
                if (TransferDAO.insert(transfer)) {
                    successfullyAddedCount++;
                } else {
                    Clients.showNotification("Couldn't add " + transfer.toShortString(), Clients.NOTIFICATION_TYPE_ERROR, null, "end_center", 3000);
                }
            } else {
                if (!TransferDAO.isDublicate(transfer)) {
                    if (TransferDAO.insert(transfer)) {
                        successfullyAddedCount++;
                    } else {
                        Clients.showNotification("Couldn't add " + transfer.toShortString(), Clients.NOTIFICATION_TYPE_ERROR, null, "end_center", 3000);
                    }
                } else {
                    duplicateCount++;
                }
            }
        }

        // @formatter:off
        String message = "Got " + (lines.size() - 7) + "x transfer(s).\n\n" 
                + successfullyAddedCount + "x transfer(s) added.\n"
                + untaggedCount + "x transfer(s) are untagged.";
        if (!ignoreDuplicates) {
            message += "\n\n" +duplicateCount +"x duplicate transfer(s) skipped.";
        }
        
        Messagebox.show(message, "Information", Messagebox.OK, Messagebox.INFORMATION);
        // @formatter:on

    }

    private static String clean(String line) {
        return line.substring(1, line.length() - 1);
    }

    private static LocalDate convertDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(date, formatter);
    }

    private static Double convertAmount(String amount) {
        return Double.parseDouble(amount.replaceAll("\\.", "").replaceAll(",", "."));
    }

}
