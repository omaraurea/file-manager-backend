# Report Manager - Spring Boot Application

The **Report Manager** application is a backend service built using **Spring Boot**. It provides RESTful APIs for managing and processing reports, integrating with AWS services like DynamoDB for data storage and S3 for file management. This application is designed to be part of a larger infrastructure, such as the **File Manager** service, and can be deployed on various environments, including local setups, Docker containers, and AWS EKS.

## Features

- **RESTful APIs**: Exposes endpoints for managing reports, including creation, update, deletion, and retrieval.
- **Integration with AWS DynamoDB**: Stores metadata related to reports in a DynamoDB table.
- **Integration with AWS S3**: Manages file uploads, downloads, and deletions in an S3 bucket.
- **Error Handling and Logging**: Provides comprehensive error handling and logs for monitoring and debugging.

## Prerequisites

Before you begin, ensure you have the following tools and configurations set up:

1. **Java 11 or higher**: Installed on your machine.
    - [Java Installation Guide](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
2. **Maven**: Installed for managing dependencies and building the project.
    - [Maven Installation Guide](https://maven.apache.org/install.html)
3. **Docker**: Installed and running if you want to build and run the application inside a Docker container.
    - [Docker Installation Guide](https://docs.docker.com/get-docker/)
4. **AWS CLI**: Configured with appropriate IAM permissions to interact with DynamoDB and S3.
    - [AWS CLI Installation Guide](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html)
    ```bash
    aws configure
    ```



### Key Files and Folders

- **`ReportManagerApplication.java`**: Main class to bootstrap the Spring Boot application.
- **`controller/`**: Contains REST controllers that handle incoming API requests.
- **`service/`**: Contains business logic and service layer classes.
- **`repository/`**: Contains repository interfaces for interacting with DynamoDB.
- **`application.properties`**: Configures application settings such as database connections, S3 bucket names, and other properties.

## Configuration

### 1. AWS Configuration

The application requires access to DynamoDB and S3. Update the `application.properties` file with the following AWS configurations:

```properties
# AWS S3 Configuration
cloud.aws.credentials.access-key=YOUR_AWS_ACCESS_KEY
cloud.aws.credentials.secret-key=YOUR_AWS_SECRET_KEY
cloud.aws.region.static=us-east-1
cloud.aws.s3.bucket=your-s3-bucket-name

# AWS DynamoDB Configuration
cloud.aws.dynamodb.endpoint=https://dynamodb.us-east-1.amazonaws.com
cloud.aws.dynamodb.table-name=filemanager


2. Database Configuration (DynamoDB)
If you have not yet created the DynamoDB table, create one with the following attributes:

Table Name: filemanager
Partition Key: id (String)
Sort Key: user (String)
3. S3 Bucket Configuration
Create an S3 bucket for storing files related to the reports:

Bucket Name: your-s3-bucket-name
4. Build the Application
Build the Spring Boot application using Maven:

mvn clean install

5. Run the Application Locally
You can run the application locally using Maven or by executing the generated JAR file:

# Run using Maven
mvn spring-boot:run

# OR run the JAR file (if the build is successful)
java -jar target/reportmanager-0.0.1-SNAPSHOT.jar

Access the application at http://localhost:8080

API Endpoints
The following API endpoints are exposed by the application:

GET /reports: Retrieve a list of all reports.
GET /reports/{id}: Retrieve details of a specific report by its ID.
POST /reports: Create a new report.
PUT /reports/{id}: Update an existing report by its ID.
DELETE /reports/{id}: Delete a specific report by its ID.
GET /reports/download/{id}: Download the file associated with a report.





