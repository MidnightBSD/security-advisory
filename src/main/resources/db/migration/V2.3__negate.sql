alter table config_node add COLUMN negate BOOLEAN NULL;

alter table config_node_cpe add COLUMN version_end_including varchar(200) NULL;
alter table config_node_cpe add COLUMN version_start_including varchar(200) NULL;
alter table config_node_cpe add COLUMN version_start_excluding varchar(200) NULL;