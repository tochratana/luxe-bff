# Luxe BFF

Luxe BFF follows the course reference architecture: Spring Cloud Gateway is
the single browser-facing entry point for the customer storefront, internal
Admin application, and Luxe API.

## Local routing

| Incoming request | Upstream |
| --- | --- |
| `/luxe-api/**` on either host | `http://localhost:16800`, with `/luxe-api` removed |
| `admin.localhost:16801/**` | `http://localhost:3001` |
| `localhost:16801/**` | `http://localhost:3000` |

The default BFF address is `http://localhost:16801`.

## Run locally

Start Luxe API on port `16800`, Luxe UI on port `3000`, and Luxe Admin on port
`3001`:

```bash
cd ../luxe-ui
npm run dev
```

```bash
cd ../luxe-admin
npm run dev
```

Start the BFF:

```bash
./gradlew bootRun
```

Open the customer storefront at `http://localhost:16801` and Admin at
`http://admin.localhost:16801/dashboard`. Both applications use their own
origin under `/luxe-api/api/v1/**` for browser API requests.

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| `BFF_PORT` | `16801` | Browser-facing BFF port |
| `LUXE_API_URL` | `http://localhost:16800` | Luxe API upstream |
| `LUXE_ADMIN_URL` | `http://localhost:3001` | Luxe Admin upstream |
| `LUXE_ADMIN_HOST_PATTERN` | `admin.localhost:*` | Host pattern routed to Luxe Admin |
| `LUXE_UI_URL` | `http://localhost:3000` | Luxe UI upstream |

## Production

Production keeps Luxe UI and Luxe Admin on Vercel and deploys the BFF as a
small API gateway on VM2. The `prod` profile exposes only `/luxe-api/**` and
forwards it to the existing `luxe-api` container on the shared `luxe-net`
Docker network.

Deployment assets are in [`deploy/`](deploy/README.md):

- A multi-stage, non-root production `Dockerfile`
- A hardened Compose service using the external `luxe-net` network
- A GitHub Actions workflow that publishes to Docker Hub and deploys by SSH
- DNS, Caddy, Vercel environment, and smoke-test instructions
