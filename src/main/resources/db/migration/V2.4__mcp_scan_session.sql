-- State table for MCP streaming package-vulnerability scans.
-- A session is created when an MCP client submits a (potentially large) list of
-- packages to check; progress is persisted so the stream can be polled or resumed.
CREATE TABLE mcp_scan_session
(
    id               VARCHAR(36)   PRIMARY KEY,
    created_date     TIMESTAMP     NOT NULL,
    updated_date     TIMESTAMP     NULL,
    status           VARCHAR(20)   NOT NULL,
    total_items      INT           NOT NULL DEFAULT 0,
    processed_items  INT           NOT NULL DEFAULT 0,
    vulnerable_count INT           NOT NULL DEFAULT 0,
    client_info      VARCHAR(255)  NULL,
    error            VARCHAR(1000) NULL
);

CREATE INDEX idx_mcp_scan_session_status ON mcp_scan_session (status);
CREATE INDEX idx_mcp_scan_session_created ON mcp_scan_session (created_date);