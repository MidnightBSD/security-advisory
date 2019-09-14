drop index product_name;
create INDEX product_name on product(name);
create INDEX product_vendor on product(vendor_id);