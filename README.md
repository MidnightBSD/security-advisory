# security-advisory

[![Build Status](https://jenkins.midnightbsd.org/buildStatus/icon?job=MidnightBSD%2Fsecurity-advisory%2Fmaster&build=8)](https://jenkins.midnightbsd.org/job/MidnightBSD/job/security-advisory/job/master/8/)

[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FMidnightBSD%2Fsecurity-advisory.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2FMidnightBSD%2Fsecurity-advisory?ref=badge_shield)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MidnightBSD_security-advisory&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=MidnightBSD_security-advisory)

This project pulls data from NIST NVD feeds and offers it
for search or by specific vendors for use in REST clients.

It was developed for the midnightbsd-security-advisory command
line tool which checks installed packages for vulnerabilities.

It stores all advisories from any vendor though so you can find
Windows, Linux, FreeBSD, Node.JS, Apache or any other vendor.

The web frontend is written in Angular.JS and the backend is
Spring Boot 2.

Requires:
* Java 17 or later
* PostgreSQL 9.x or newer
* ElasticSearch 7.x or newer

The public instance of this app is available at https://sec.midnightbsd.org/

## MCP server

The app exposes a native [Model Context Protocol](https://modelcontextprotocol.io)
server over the **Streamable HTTP** transport at `/api/mcp`, so AI coding agents
(Claude, Codex, etc.) can look up CVEs and check installed packages for known
vulnerabilities. See the in-app docs at `/mcp` for client setup and the tool list.

## Deploying behind Apache

The app listens on port `8210` and is meant to run behind an Apache (httpd)
reverse proxy. Any path under `/api` is proxied to the backend, which already
covers the MCP endpoint at `/api/mcp`. The one thing to get right is the MCP
**Streamable HTTP / SSE** channel: it must not be gzip-compressed or torn down by
a short timeout. Add a dedicated block *before* your general `ProxyPass` (the
longest path match must come first):

```apache
<VirtualHost *:443>
	ServerName sec.midnightbsd.org
	# ... your TLS config ...

	ProxyPreserveHost On
	ProxyRequests Off

	# MCP Streamable HTTP endpoint: no compression, long timeout for the event stream
	<Location "/api/mcp">
		SetEnv no-gzip 1
		ProxyPass        http://127.0.0.1:8210/api/mcp connectiontimeout=5 timeout=1800
		ProxyPassReverse http://127.0.0.1:8210/api/mcp
	</Location>

	# the rest of the app (keep AFTER the block above)
	ProxyPass        / http://127.0.0.1:8210/
	ProxyPassReverse / http://127.0.0.1:8210/

	ProxyTimeout 1800
</VirtualHost>
```

Notes:

* Requires `mod_proxy` and `mod_proxy_http` (already enabled if proxying works).
* `no-gzip` is the important one — if `mod_deflate` compresses `text/event-stream`
the stream buffers and MCP clients hang. Make sure any `AddOutputFilterByType
DEFLATE` rules do not include `text/event-stream`.
* Raise `ProxyTimeout` / the per-route `timeout` to match the app's stream timeout
so long-lived server-to-client channels are not dropped.

Smoke-test the endpoint after deploy (confirm no `Content-Encoding: gzip` header):

```bash
curl -sN https://sec.midnightbsd.org/api/mcp \
-H 'Content-Type: application/json' \
-H 'Accept: application/json, text/event-stream' \
-d '{"jsonrpc":"2.0","id":1,"method":"tools/list","params":{}}' -D -
```

## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FMidnightBSD%2Fsecurity-advisory.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FMidnightBSD%2Fsecurity-advisory?ref=badge_large)
