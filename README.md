# z-youtube

English | [简体中文](/docs/zh_CN/README.md) | [More](/docs/README.md)

A Spring Boot backend practice project for a YouTube-ish content platform. The current scope is intentionally focused on backend fundamentals for content systems: authentication, accounts, videos, comments, validation, persistence, and access control.

## Status

The core learning path is already in place:

- `account`: register, self profile, public profile, update profile, change password, withdraw
- `auth`: login, logout, current user
- `video`: create, update, publish, unpublish, delete, public detail, public listing
- `comment`: create, paginated listing, delete own comment
- infrastructure: `ApiResponse`, global exception handling, validation, session-based auth

This repository is no longer just a scaffold. It is a usable base for continuing backend practice around content-platform workflows.

## Stack

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- MySQL
- H2 for tests

## Run

Start MySQL:

```bash
docker compose up -d
```

Start the backend:

```bash
cd back
./mvnw spring-boot:run
```

Default local setup:

- app port: `6969`
- MySQL: `localhost:3306`
- database: `zyoutube`
- username: `root`
- password: `root`

Health check:

```text
GET /ping
```

## Repository Layout

```text
.
├── back/
│   ├── pom.xml
│   └── src/main/java/com/zyoutube/
├── docker-compose.yml
└── README.md
```

## Current Focus

This repo is best used to keep improving content-platform backend skills:

- service layering and domain boundaries
- JPA entity modeling and query design
- Spring Security and ownership checks
- transaction handling for non-commerce flows
- Redis and MQ practice for read-heavy platform scenarios

## Recommended Next Work

### Engineering cleanup

- add authentication-specific exception handling instead of falling back to `500`
- fix the login response field mapping issue
- close the video deletion vs comment foreign key gap
- define and implement author access rules for draft/private videos
- define and implement `UNLISTED` visibility behavior
- add core integration tests for `auth`, `account`, `video`, and `comment`

### Minimal MVP features

- creator-side listing for my videos
- draft/private video detail for the owner
- like/dislike interaction
- simple `viewCount`
- latest feed based on the existing public video listing
- media fields such as `videoUrl` and `coverUrl`

### Good middleware practice in this repo

- Redis leaderboard
- hot video cache
- pull or push feed patterns
- unread counters
- comment list caching
- async notification flows
- MQ-based decoupling

## Out of Scope For Now

This repository is centered on content-platform workflows. Payment, order, inventory, flash-sale, and similar transaction-heavy scenarios are intentionally out of scope here.
