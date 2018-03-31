create INDEX product_name on product(name, version);

create UNIQUE INDEX vendor_name on vendor(name);

create UNIQUE INDEX advisory_cve on advisory(cve_id);