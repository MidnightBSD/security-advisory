-- Remove the MCP streaming scan-session state table added in V2.4.
-- The streaming /api/mcp/check/stream + scan-status endpoints were dropped when the
-- service moved to a native MCP (Streamable HTTP) server, so this table is now unused.
-- Dropping the table also drops its indexes (idx_mcp_scan_session_status, _created).
DROP TABLE IF EXISTS mcp_scan_session;