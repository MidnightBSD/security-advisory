CREATE INDEX idx_advisory_last_modified_date
    ON advisory (last_modified_date)
    WHERE last_modified_date IS NOT NULL;

CREATE INDEX idx_advisory_published_date
    ON advisory (published_date)
    WHERE published_date IS NOT NULL;

CREATE INDEX idx_advisory_product_map_product_advisory
    ON advisory_product_map (product_id, advisory_id);

CREATE INDEX idx_advisory_package_fixed_map_advisory_package
    ON advisory_package_fixed_map (advisory_id, package_fixed_id);

CREATE INDEX idx_cvss_metrics3_advisory
    ON cvss_metrics3 (advisory_id);

CREATE INDEX idx_product_name_vendor_version
    ON product (name, vendor_id, version);

CREATE INDEX idx_product_version
    ON product (version);

CREATE INDEX idx_package_fixed_name_version
    ON package_fixed (name, version);
