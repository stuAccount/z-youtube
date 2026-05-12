# Z-YouTube Frontend

React + Vite + TypeScript frontend for the existing Z-YouTube backend.

## Setup

```bash
npm install
cp .env.example .env
```

Set the backend origin in `.env`:

```bash
VITE_API_BASE_URL=http://your-server-host:6969
```

If `VITE_API_BASE_URL` is empty, requests use the same origin as the frontend.

## Scripts

```bash
npm run dev
npm run typecheck
npm run build
npm run preview
```

The client uses `credentials: "include"` for all requests because the backend authenticates with a session.
