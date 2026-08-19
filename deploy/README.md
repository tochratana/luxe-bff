# Luxe BFF production deployment

The production BFF runs on VM2 alongside the existing Luxe API, Redis,
Keycloak, MinIO, and Caddy services. It joins the existing external
`luxe-net` Docker network and reaches the API by its container name:
`http://luxe-api:16800`.

The storefront and Admin remain hosted on Vercel. Both applications proxy
their same-origin `/luxe-api/**` requests to
`https://bff.luxe-kh.online/luxe-api/**`. The BFF then removes the
`/luxe-api` prefix and forwards the request to Luxe API.

## One-time VM2 preparation

Create the shared network if it does not already exist:

```bash
docker network inspect luxe-net >/dev/null 2>&1 || \
  docker network create --subnet 172.30.0.0/24 luxe-net
```

Add this site to the Caddyfile used by `luxe-caddy`:

```caddy
bff.luxe-kh.online {
    encode zstd gzip
    reverse_proxy luxe-bff:16801
}
```

Point the DNS `A` record for `bff.luxe-kh.online` to VM2's public IP, then
reload Caddy after the BFF container is healthy.

## GitHub production configuration

Configure these repository variables:

- `DOCKERHUB_USERNAME`
- `VM_HOST`
- `VM_USER`
- `VM_SSH_PORT`

Configure these repository or production-environment secrets:

- `DOCKERHUB_TOKEN`
- `VM_SSH_PRIVATE_KEY`
- `VM_KNOWN_HOSTS`

Pushing the `deploy` branch, or running the workflow manually, builds the
image, pushes immutable and `latest` tags, copies the Compose file to VM2,
and recreates only the BFF container.

## Vercel configuration

Set this environment variable in both the Luxe UI and Luxe Admin Vercel
projects for Production, Preview, and Development as appropriate:

```dotenv
LUXE_BFF_URL=https://bff.luxe-kh.online
```

Redeploy both projects after changing the environment variable because
Next.js evaluates rewrites from `next.config.ts` during deployment.

Allow both BFF callback URLs on the Keycloak client used by `luxe-bff`:

```text
https://www.luxe-kh.online/bff/login/oauth2/code/keycloak
https://admin.luxe-kh.online/bff/login/oauth2/code/keycloak-admin
```

Also allow `https://www.luxe-kh.online/` and
`https://admin.luxe-kh.online/` as valid post-logout redirect URIs.

## Smoke tests

```bash
curl --fail https://bff.luxe-kh.online/actuator/health
curl --fail https://bff.luxe-kh.online/luxe-api/actuator/health
curl --fail https://www.luxe-kh.online/luxe-api/api/v1/products
curl --fail https://admin.luxe-kh.online/luxe-api/api/v1/products
```

The BFF does not connect directly to PostgreSQL, Redis, Keycloak, or MinIO.
Those services remain owned by the existing Luxe API production deployment.
