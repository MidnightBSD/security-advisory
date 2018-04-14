CREATE TABLE config_node_cpe (
  id   SERIAL PRIMARY KEY,
  vulnerable BOOLEAN NULL,
  cpe22Uri VARCHAR(100) NULL,
  cpe23Uri VARCHAR(100) NULL,
  config_node_id INT REFERENCES config_node(id) NULL
);

create INDEX config_node_cpe_node on config_node_cpe(config_node_id);

alter table config_node drop COLUMN vulnerable;
alter table config_node drop COLUMN cpe22Uri;
alter table config_node drop COLUMN cpe23Uri;