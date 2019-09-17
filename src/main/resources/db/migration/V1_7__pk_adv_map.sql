alter table advisory_product_map add constraint pk_advisory  PRIMARY KEY  (advisory_id, product_id);
drop index adv_product;