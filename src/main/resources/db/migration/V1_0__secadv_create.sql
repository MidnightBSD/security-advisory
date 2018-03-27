CREATE TABLE advisory (
  id                 SERIAL PRIMARY KEY,
  cve_id             VARCHAR(30) NOT NULL,
  description        TEXT        NULL,
  published_date     TIMESTAMP,
  last_modified_date TIMESTAMP,
  severity           VARCHAR(30)
);

CREATE TABLE vendor (
  id   SERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL
);

CREATE TABLE product (
  id        SERIAL PRIMARY KEY,
  name      VARCHAR(200) NOT NULL,
  vendor_id INT REFERENCES vendor (id),
  version   VARCHAR(100)
);

CREATE TABLE advisory_product_map (
  advisory_id INT REFERENCES advisory (id),
  product_id  INT REFERENCES product (id)
);

CREATE TABLE package_fixed (
  id      SERIAL PRIMARY KEY,
  name    VARCHAR(255) NOT NULL,
  version VARCHAR(255) NOT NULL
);

CREATE TABLE advisory_package_fixed_map (
  advisory_id      INT REFERENCES advisory (id),
  package_fixed_id INT REFERENCES package_fixed (id)
);