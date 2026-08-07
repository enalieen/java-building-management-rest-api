# NothingBurger presents: Digital Building Data Management

## Project Description
This project is a digital building management system developed as part of the IT project course.
The application allows the management of customers and meter readings through a REST API and a graphical frontend. The software was implemented using Java, Javalin, JDBC, MariaDB, Docker, and Maven.

**Main features:**
* Customer and Meter reading management (CRUD operations)
* Database persistence with MariaDB
* Graphical Frontend with HTML/JS/CSS and Bootstrap
* Data export functionality (JSON, CSV, XML)
* Containerized database environment using Docker
* Automated testing (JUnit, JaCoCo) and CI pipeline (GitHub Actions)

---
## Software Architecture & Project Structure

The project follows a clean architecture separating the routing (Javalin Server), the data access layer (DAO pattern), and the data models.

```text
src
├── main
│   ├── java
│   │   ├── database
│   │   │   └── CreateDatabase.java
│   │   ├── de.gc.jdbc
│   │   │   └── Main.java                 <-- Application Entry Point
│   │   ├── server
│   │   │   ├── Server.java
│   │   │   ├── CustomerEndpoints.java
│   │   │   └── ReadingEndpoints.java
│   │   └── vorlage
│   │       ├── databaseConnection
│   │       │   ├── IDatabaseConnection.java
│   │       │   └── JDBCDBConn.java
│   │       └── model
│   │           ├── Customer.java
│   │           ├── CustomerDAO.java
│   │           ├── Reading.java
│   │           └── ReadingDAO.java
│   └── resources
│       ├── frontend
│       │   ├── index.html
│       │   ├── app.js
│       │   └── style.css
│       └── db.properties
```
---

### Technologies Used
| Technology | Purpose |
|---|---|
| **Java 17** | Backend development |
| **Javalin 6** | REST API routing |
| **JDBC & MariaDB** | Database communication |
| **Maven** | Build management & dependencies |
| **Docker** | Shared database environment |
| **Bootstrap** | Frontend styling |
| **JUnit & JaCoCo** | Testing and Coverage |

---

## Database Schema

The database consists of two main tables: `customer` and `reading`.
* One customer can have multiple meter readings.
* A reading can optionally reference a customer.

![Overview DB](/doc/90-Images/overview_db.png?raw=true "Overview of the Database structure")
*(Ensure the image is located at `/doc/90-Images/overview_db.png`)*

---

## UML Sequence Diagram

The following sequence diagram shows a typical CRUD operation (e.g., Creating a new customer):

![Sequence Diagram](/doc/90-Images/Customer Json Flow-2026-05-20-112926.png?raw=true "Sequence Diagram of a CRUD operation")

---

## User Interface (Frontend)

The frontend communicates with the backend through the REST API using the JavaScript Fetch API. It allows users to view, create, and export data.

![Frontend Screenshot](/doc/90-Images/frontend.png?raw=true "Screenshot of the Frontend GUI")

---

## How to Use:

### Running Tests and Using Docker Compose
This project uses JUnit, Maven, and Docker Compose.
A shared database instance is created through Docker, ensuring that all team members and the CI pipeline work with the exact same configuration.

### Requirements
- Docker and Docker Compose: For setting up the shared database environment.
- Java (JDK 11+): For running the application and tests.
- Maven: For building the project and running tests.
- Port 3306 available (or adjust it in docker-compose.yml)

### Installing Maven:
Maven is a build and dependency management tool for Java projects.  
1. Download `apache-maven-3.9.9-bin.zip` from the official website.
2. Unzip the archive to any folder, for example: `C:\Program Files\Apache\Maven`.
3. Add the `bin` directory (for example: `C:\Program Files\Apache\Maven\bin`) to your system PATH environment variable.
4. Restart the terminal and run `mvn -v` to check that Maven is installed.

### Installing JDK 17 (Adoptium Temurin):
1. Download **Temurin** from: [https://adoptium.net](https://adoptium.net)
2. Install it, make sure these options are enabled:
* Add to PATH
* Set JAVA_HOME variable
3. Verifying: after installation, restart the terminal and run:
```
java -version
javac -version
```



---
## Setup Database with Docker Compose

1. **Start Database**:
Run the following command in the project directory:
     ```bash
     docker-compose up -d
     ```
   * This will start the MariaDB container on port 3306.
   * Ensure MariaDB is running on the configured port (default: 3306).

2. **Check if Database is Running**:
Run:
     ```bash
     docker ps
     ```
   * You should see the MariaDB container running.

3. **Run the application**
     ```bash
     mvn "clean" "compile" "exec:java" "-Dexec.mainClass=server.Main"
     ```

4. **Access the API:**
    Open your browser or use Postman/curl to test endpoints, for example:  
    ```bash
    GET http://localhost:8080/customers
    POST http://localhost:8080/customers
    ``` 
   (etc)
5. **Stop the server:**
   Close the terminal


## To run the Tests with Maven

1. **Run Tests**:
In the project directory, run:
     ```bash
     mvn clean test
     ```
   * This will run all the JUnit tests.
  
Generate Coverage Report:
     ```bash
     mvn jacoco:report
     ```
   * This generates the HTML report in target/site/jacoco/.

Open HTML Report in Browser from IntelliJ IDEA:

Go to the target/site/jacoco/ folder in the Project tool window.

Right-click index.html → Open in Browser → choose your preferred browser.

Alternatively, you can drag the index.html file into any browser window.

Note: Opening directly by double-click may not render styles correctly in some browsers. Using Open in Browser from IDEA ensures CSS/JS load properly.

## CI Pipeline with GitHub Actions

* The CI pipeline runs automatically on every commit to test the code.
* It sets up the database with Docker, runs the tests, and ensures everything works.


---

This setup ensures all team members have a consistent environment, and the CI pipeline runs tests automatically.

---






## Authors: 
@enalieen
