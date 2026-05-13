# Z-YouTube Frontend

React + Vite + TypeScript frontend for the existing Z-YouTube backend.

## Setup

```bash
npm install
cp .env.example .env
```

For local development, leave `VITE_API_BASE_URL` empty so Vite proxies API requests to the backend running on the same remote machine:

```bash
VITE_API_BASE_URL=
```

The dev server proxies `/api` and `/ping` to `http://localhost:6969`.

Only set `VITE_API_BASE_URL` when you intentionally want the browser to call a different backend origin directly.

For containerized deployment, the production frontend is served by Nginx and proxies `/api` and `/ping` to the backend container.

## Scripts

```bash
npm run dev
npm run typecheck
npm run build
npm run preview
```

The client uses `credentials: "include"` for all requests because the backend authenticates with a session.
