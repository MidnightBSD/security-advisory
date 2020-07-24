# security-advisory
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FMidnightBSD%2Fsecurity-advisory.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2FMidnightBSD%2Fsecurity-advisory?ref=badge_shield)

![CodeQL](https://github.com/MidnightBSD/security-advisory/workflows/CodeQL/badge.svg)

This project pulls data from NIST NVD feeds and offers it 
for search or by specific vendors for use in REST clients. 

It was developed for the midnightbsd-security-advisory command
line tool which checks installed packages for vulnerabilities.

It stores all advisories from any vendor though so you can find
Windows, Linux, FreeBSD, Node.JS, Apache or any other vendor.

The web frontend is written in Angular.JS and the backend is
Spring Boot 2.

Requires:
* Redis 3.x
* Java 8
* PostgreSQL 9.x or newer
* ElasticSearch 5.5.x



## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FMidnightBSD%2Fsecurity-advisory.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FMidnightBSD%2Fsecurity-advisory?ref=badge_large)
