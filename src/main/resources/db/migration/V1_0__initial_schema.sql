create table local_invoice (
   exported boolean not null,
   buyer varchar(255),
   file_id varchar(255) not null,
   seller varchar(255),
   primary key (file_id)
);