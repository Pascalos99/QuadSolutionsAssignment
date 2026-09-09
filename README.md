# Quad Assignment

## How to build

This project consists of a Java Spring Boot back-end with a Vue.js front-end. The back-end **requires a Java JDK (v21 or higher)** to be accessible from the `quad-assignment-backend` directory. Building the back-end automatically builds the front-end as well and installs the required Node.js version locally inside the `quad-assignment-frontend` directory; Therefore, **Node.js (v24.20.0) is only required when running the front-end on its own**.

### Building with Command-Line-Interface

To build the entire project, run the following from the [`quad-assignment-backend`](quad-assignment-backend/) directory:

**on Windows:**

```cmd
mvnw clean package
```

**on Linux/MacOS:**

```bash
./mvnw clean package
```

This will build the entire project into an executable `.jar` file located at `quad-assignment-backend/target/QuadAssignment.jar`.



To run the resulting project, run the following from the [`quad-assignment-backend`](quad-assignment-backend/) directory:

**on Windows/Linux/MacOS:** (with a global installation of JDK 21 or higher)

```cmd
java -jar target/QuadAssignment.jar
```

This will run the server at `localhost:8080` by default.

### Building with scripts

Alternatively, the scripts in the [`scripts`](scripts/) directory can be used to build and run the application.

To **build and run** the entire project, execute:
* [`run.cmd`](scripts/run.cmd) for **Windows**
* [`run.sh`](scripts/run.sh) script for **Linux/MacOS**.

To **only build** the project and not run it, execute:
* [`build.cmd`](scripts/build.cmd) for **Windows**
* [`build.sh`](scripts/build.sh) script for **Linux/MacOS**.

Note that the `run` script **will only build the application if it has not been built before**, for subsequent builds, run `build` first and then `run`.
