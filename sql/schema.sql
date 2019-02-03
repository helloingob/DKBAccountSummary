CREATE
    TABLE
        applicant(
            pk BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
            name varchar(100) NOT NULL,
            iban varchar(100) NOT NULL UNIQUE,
            bic varchar(100) NOT NULL
        );

CREATE
    TABLE
        posting(
            pk BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
            text varchar(100) NOT NULL UNIQUE
        );

CREATE
    TABLE
        transfer(
            pk BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
            date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            postingtext_fk INT4 NOT NULL REFERENCES posting,
            applicant_fk INT4 NOT NULL REFERENCES applicant,
            reasonforpayment varchar(1000) NULL,
            amount FLOAT8 NOT NULL,
            comment varchar(500) NULL
        );

CREATE
    TABLE
        tag(
            pk BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
            title varchar(100) NOT NULL UNIQUE,
            visible bool NOT NULL DEFAULT true,
            description varchar(100) NULL
        );

CREATE
    TABLE
        transfer_tag(
            transfer_fk INT4 NOT NULL REFERENCES transfer,
            tag_fk INT4 NOT NULL REFERENCES tag,
            UNIQUE(
                transfer_fk,
                tag_fk
            )
        );