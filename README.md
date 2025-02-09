# Aloha-Andes

This is a CRUD application for a hotel-like management system. It is the final project for the course _Transactional Systems_ at Universidad de los Andes. The project is developed in Java. It explores fundamentals of transactional systems, such as ACID properties, isolation levels, and concurrency control, through the use of JDBC and Oracle SQL.

## Getting Started

This project requires a database connection to run. You can run a local Oracle database using Docker. Execute the following command to run the database:

```bash
docker run -d --name oracle-db \
  -p 1521:1521 \
  -p 5500:5500 \
  -e ORACLE_PWD=adminpassword \
  container-registry.oracle.com/database/express:latest
```

Change the database config to suit your needs and update the file `Alohandes/src/main/resources/META-INF/persistence.xml` with the new configuration.

Execute the following commands to run the application:

```bash
cd Alohandes
javac -cp "lib/*" -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp "bin:lib/*:src/main/resources" uniandes.isis2304.alohandes.interfazApp.InterfazAlohandesApp
```


<img width="600" alt="aloha-andes" src="https://github.com/user-attachments/assets/60f51d4e-ece5-476f-add2-cdbbda15e638" />
