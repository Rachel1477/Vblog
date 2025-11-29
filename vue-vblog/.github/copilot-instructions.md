**Project Overview**

- **Stack:** Vue 3 (Composition API, `<script setup>`), Vite, Pinia, Vue Router, Axios.
- **Purpose:** Single-page frontend for a simple blog/notes app. Backend APIs expected at `http://localhost:8080`.
- **Entry points:** app bootstraps in `src/main.js`; routes defined in `src/router/index.js`.

**Dev / Build Commands**

- **Install:** `npm install`
- **Dev server (hot reload):** `npm run dev` (Vite default port, typically `http://localhost:5173`)
- **Build production bundle:** `npm run build`
- **Preview production build:** `npm run preview`
- **Lint / format:** `npm run lint`, `npm run format`

**Important Files & Patterns**

- `vite.config.js` — defines alias `@` -> `src`. Use `@/` when importing source files.
- `src/api/request.js` — centralized axios instance. Important details:
  - `baseURL` is `http://localhost:8080`.
  - Request interceptor reads token from Pinia via `getActivePinia()` and `useUserStore(pinia)` and sets `Authorization: Bearer <token>` header.
  - When adding new API helpers, import this `request` instance (see `src/api/note.js`).
- `src/store/user.js` — Pinia store. Token is persisted in `localStorage` under the key `token`.
  - Use `useUserStore()` to access `token` and `user` state.
- `src/router/index.js` — routes include `meta.requireAuth` (example: `/notes`). Router guard checks `userStore.token` and redirects to `/auth` when missing.
- `src/api/note.js` — canonical examples of API helper functions (getMyNotes, getPublicNotes, createNote, updateNote, deleteNote).
- `src/views/*.vue` — use `<script setup>`; many views call APIs and expect server responses shaped as `{ code: 200, data: ... }`.

**Project-specific conventions and gotchas (useful for an AI coder)**

- Auth token flow:
  - Store: `src/store/user.js` (localStorage key `token`).
  - Interceptor: `src/api/request.js` reads token dynamically using `getActivePinia()` so the axios instance can be reused outside component setup.
  - Avoid importing the store directly into plain helper modules unless you pass an active Pinia instance or rely on `getActivePinia()` like `request.js` does.
- API response shape: views expect `res.data.code === 200` and `res.data.data`. When writing new components or helpers, mirror this check.
- Mixing patterns:
  - Preferred: call `src/api/*` helpers which use the shared `request` instance.
  - Existing components sometimes call `axios` directly (see `src/views/Notes.vue`). When refactoring, prefer the `src/api` helpers for authorization consistency.
- Routing / auth:
  - Protected routes use `meta.requireAuth` and the global `beforeEach` guard (`src/router/index.js`). When adding routes that require login, add the meta flag.
- Imports: use the `@/` alias for modules inside `src` (e.g., `@/api/note`, `@/store/user`).

**Examples to follow (copyable snippets)**

- Add a new API helper (pattern used in `src/api/note.js`):

```js
// src/api/myResource.js
import request from '@/api/request'

export function fetchSomething() {
  return request.get('/api/something')
}
```

- Use the Pinia store in components:

```js
import { useUserStore } from '@/store/user'
const userStore = useUserStore()
// read token: userStore.token
```

**Integration / External Dependencies**

- Backend: expected at `http://localhost:8080`. Many endpoints used under `/api/note/*`.
- Devtools: `vite-plugin-vue-devtools` is included in `devDependencies`. Use Vue Devtools in the browser for debugging component state.

**When editing code, prefer small, local changes**

- Keep changes minimal and consistent with existing patterns (`<script setup>`, Pinia, `@` imports).
- If adding API calls, add them to `src/api/*` and call them from views rather than sprinkling `axios` usage.

**Where to look for further context**

- Authentication and store: `src/store/user.js`
- Axios instance & auth header behavior: `src/api/request.js`
- Main API surface for notes: `src/api/note.js`
- Routing and route-guard rules: `src/router/index.js`
- Example views and patterns: `src/views/Notes.vue`, `src/views/PublicNotes.vue`, `src/views/NoteCreate.vue`

If anything here is unclear or you want additional guidance (for example, unit-test setup, CI scripts, or a different API base URL), tell me which area to expand and I will iterate.
