CREATE TABLE config_node (
  id   SERIAL PRIMARY KEY,
  parent_id INT NULL,
  operator VARCHAR(10) NULL,
  vulnerable BOOLEAN NULL,
  cpe22Uri VARCHAR(100) NULL,
  cpe23Uri VARCHAR(100) NULL,
  advisory_id INT REFERENCES advisory (id)
);

create INDEX config_node_advisory on config_node(advisory_id);