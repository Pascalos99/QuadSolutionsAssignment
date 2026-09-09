# Quad Assignment

## How to build

This project consists of a Java Spring Boot back-end with a Vue.js front-end. The back-end **requires a Java JDK (v21 or higher)** to be accessible from the `quad-assignment-backend` directory. Building the back-end automatically builds the front-end as well and installs the required Node.js version locally inside the `quad-assignment-frontend` directory; Therefore, **Node.js (v24.20.0) is only required when running the front-end on its own**.



To build the entire project, run the following from the `quad-assignment-backend` directory:

**on Windows:**

```cmd
mvnw clean package
```

**on Linux/MacOS:**

``````bash
./mvnw clean package
``````

This will build the entire project into an executable `.jar` file located at `quad-assignment-backend/target/QuadAssignment.jar`.



To run the resulting project, run the following from the `quad-assignment-backend` directory:

**on Windows:** (and on **Linux/MacOS** if the JDK is installed globally)

``````cmd
java -jar target/QuadAssignment.jar
``````

**on Linux/MacOS:** (if the JDK is installed locally)

``````bash
./java -jar target/QuadAssignment.jar
``````

This will run the server at `localhost:8080` by default.
