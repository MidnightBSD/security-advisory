CREATE INDEX idx_vendor_lower_name_prefix
    ON vendor (lower(name) text_pattern_ops);
