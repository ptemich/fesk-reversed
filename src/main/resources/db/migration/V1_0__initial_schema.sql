
    create table local_invoice (
        generated_on date,
        processing_code integer not null,
        buyer varchar(255),
        file_id varchar(255) not null,
        invoice_number varchar(255),
        ksef_number varchar(255),
        processing_description varchar(255),
        seller varchar(255),
        primary key (file_id)
    );
