# DKB Account Summary

Imports csv-exported sales figures from DKB (Deutsche Kreditbank) account. The GUI displays a graphical and tabular view of monthy expenses, income and spend money.

*This has **NO AUTOMATIC import**, you have to manually export and import the data!*

## Features:
- List all transactions between two dates
- Tag individual transactions
- Automatic tagging of known reoccuring transactions (income, rent, ...)
- Tag based bar chart display of spend money

## Requirements
- [Tomcat 9](https://tomcat.apache.org/download-90.cgi)
- [Java 1.8](https://java.com/de/download/)
- [MariaDB 5.5](https://mariadb.com/downloads/)
- [Maven](https://maven.apache.org/download.cgi)


## Build
  - Run "**mvn clean package**"
  
## Database
Add user "**accsum**" with "**helloingob**" password & create **database**.

  ```
  CREATE USER 'accsum'@'localhost' IDENTIFIED BY 'helloingob';
  GRANT ALL PRIVILEGES ON accsum.* TO 'accsum'@'localhost';
  FLUSH PRIVILEGES;

  CREATE DATABASE accsum;  
  ```
Execute [schema.sql](/sql/schema.sql)

## Setup GUI:
1) Copy accsum.war file to tomcat webapps directory
2) Start Tomcat server
3) Access http://localhost:8080/accsum
4) Export "Umsätze" from dkb page
5) Import
  
