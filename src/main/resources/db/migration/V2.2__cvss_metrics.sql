CREATE TABLE cvss_metrics3
(
    id                     SERIAL PRIMARY KEY,
    source                 VARCHAR(100) NULL,
    type                   VARCHAR(200) NULL,
    exploitability_score   VARCHAR(10)  NULL,
    impact_score           VARCHAR(10)  NULL,
    version                VARCHAR(10)  NULL,
    vector_string          VARCHAR(100) NULL,
    attack_vector          VARCHAR(100) NULL,
    attack_complexity      VARCHAR(100) NULL,
    privileges_required    VARCHAR(100) NULL,
    user_interaction       VARCHAR(100) NULL,
    scope                  VARCHAR(100) NULL,
    confidentiality_impact VARCHAR(100) NULL,
    integrity_impact       VARCHAR(100) NULL,
    availability_impact    VARCHAR(100) NULL,
    base_score             VARCHAR(100) NULL,
    base_severity          VARCHAR(100) NULL,
    access_vector          VARCHAR(100) NULL,
    access_complexity      VARCHAR(100) NULL,
    authentication         VARCHAR(100) NULL,
    advisory_id            INT REFERENCES advisory (id)
);