# Luxe BFF

Luxe BFF is the browser-facing gateway for the Luxe customer storefront, Admin
dashboard, and backend API. It implements the Backend for Frontend pattern with
Spring Cloud Gateway and Spring Security OAuth2 Client.

The BFF owns the Keycloak login session, keeps access and refresh tokens on the
server, sends only the session cookie to the browser, and relays the access
token when forwarding API requests.

## Features

- Single browser entry point on port `16801`
- Customer storefront routing to Luxe UI
- Host-based Admin routing through `admin.localhost`
- `/luxe-api/**` path rewriting and forwarding to Luxe API
- OAuth2 and OpenID Connect login with Keycloak
- Separate customer and Admin Keycloak client registrations
- Server-side session and automatic access-token relay
- Customer and Admin logout with separate return destinations
- Authenticated-user status endpoint for both frontend applications
- Actuator health probes
- Graceful shutdown and production Docker image

## Technology

| Technology | Purpose |
| --- | --- |
| Java 25 | Application language and runtime |
| Spring Boot 4 | Application framework and Actuator |
| Spring Cloud Gateway | Reactive routing, filters, and proxy behavior |
| Spring Security OAuth2 Client | Keycloak login, session, logout, and token relay |
| Gradle | Build and dependency management |
| Docker | Production packaging and health checks |

## Local routing

| Browser request | Upstream |
| --- | --- |
| `/luxe-api/**` on either host | Luxe API at `http://localhost:16800`, with `/luxe-api` removed |
| `admin.localhost:16801/**` | Luxe Admin at `http://localhost:3001` |
| `localhost:16801/**` | Luxe UI at `http://localhost:3000` |

Generated OAuth endpoints are exposed under `/bff/**` by the Next.js rewrites.
The browser uses `/bff/api/v1/auth/is-authenticated` to check the BFF session.

## Run locally

### Prerequisites

- JDK 25
- Luxe API on `http://localhost:16800`
- Luxe UI on `http://localhost:3000`
- Luxe Admin on `http://localhost:3001`
- Keycloak on `http://localhost:9090` with the `luxe` realm

Copy the environment template and set the local `luxe-ui` client secret:

```bash
cp .env.example .env
```

Load the environment and start the gateway:

```bash
set -a
source .env
set +a
./gradlew bootRun
```

Open:

- Customer storefront: [http://localhost:16801](http://localhost:16801)
- Admin dashboard: [http://admin.localhost:16801/dashboard](http://admin.localhost:16801/dashboard)
- Health check: [http://localhost:16801/actuator/health](http://localhost:16801/actuator/health)

The local Keycloak clients must allow these callback URLs:

```text
http://localhost:16801/bff/login/oauth2/code/keycloak
http://admin.localhost:16801/bff/login/oauth2/code/keycloak-admin
```

## Environment variables

| Variable | Local default | Purpose |
| --- | --- | --- |
| `BFF_PORT` | `16801` | Browser-facing gateway port |
| `LUXE_API_URL` | `http://localhost:16800` | Luxe API upstream |
| `LUXE_UI_URL` | `http://localhost:3000` | Luxe UI upstream |
| `LUXE_ADMIN_URL` | `http://localhost:3001` | Luxe Admin upstream |
| `LUXE_ADMIN_HOST_PATTERN` | `admin.localhost:*` | Host pattern routed to Luxe Admin |
| `LUXE_UI_PUBLIC_URL` | `http://localhost:16801/` | Customer post-logout destination |
| `LUXE_ADMIN_PUBLIC_URL` | `http://admin.localhost:16801/` | Admin post-logout destination |
| `KEYCLOAK_ISSUER_URI` | `http://localhost:9090/realms/luxe` | Keycloak realm issuer |
| `KEYCLOAK_CLIENT_ID` | `luxe-ui` | Customer OAuth client |
| `KEYCLOAK_CLIENT_SECRET` | Required | Customer OAuth client secret |
| `KEYCLOAK_ADMIN_CLIENT_ID` | `luxe-admin` | Public Admin PKCE client |
| `OAUTH2_REDIRECT_URI` | Customer callback above | Customer OAuth callback |
| `OAUTH2_ADMIN_REDIRECT_URI` | Admin callback above | Admin OAuth callback |

## Commands

| Command | Purpose |
| --- | --- |
| `./gradlew bootRun` | Start the local BFF |
| `./gradlew test` | Run the minimal context test |
| `./gradlew clean build` | Run verification and build the executable JAR |
| `docker build -t luxe-bff:local .` | Build the production container image |

## Project structure

```text
src/main/java/kh/edu/istad/luxe/bff/
├── config/          # Gateway security, login, logout, and session rules
├── controller/      # Browser authentication status endpoint
├── dto/             # BFF response contracts
└── BffApplication.java

src/main/resources/application.yaml  # Local defaults and environment overrides
deploy/                               # Production Compose and deployment guide
Dockerfile                            # Multi-stage non-root image
```

## Production

Production values are supplied through environment variables. Deployment
assets in `deploy/` run the BFF on the shared `luxe-net` Docker network and
route `/luxe-api/**` to the Luxe API container.
