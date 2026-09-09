# Scripts

**build:**

* Builds the application, including a test-phase, and creates a compressed `.jar` file that runs the application.

**run:**

* Runs the application built by `build` (by default at `localhost:8080`). If the application is not present, runs `build` first.

**dev-backend:**

* Builds the front-end and runs the back-end server (at `localhost:8080`) without building the back-end; This runs all Maven phases until and including `compile`.

**dev-frontend:**

* Runs a development environment server for only the front-end with **Vite** (at `localhost:5173`).

