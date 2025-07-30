
# Franchise Management API

## Introduction 

This is a reactive RESTful API for managing a network of franchises, their branches, and product inventory. The project is built using Spring WebFlux and follows the principles of Hexagonal Architecture (Ports and Adapters) for a clean separation of concerns.

The entire infrastructure is defined as code using Terraform and is designed for a cost-effective, automated deployment to AWS using ECS Fargate and a managed RDS PostgreSQL database.

## 🏛️ Architecture
This project is built using Hexagonal Architecture (Ports and Adapters). This architectural style isolates the core application and domain logic from external concerns like frameworks, databases, and UI.

Domain: Contains the core business logic, models (Franchise, Branch, Product), and port interfaces. It has zero dependency on any framework.

Application: Contains the use cases that orchestrate the business logic. It depends only on the Domain ports.

Infrastructure: Contains the adapters that interact with the outside world.

Input Adapters: REST endpoints (implemented with Spring WebFlux Functional Routes) that handle incoming HTTP requests.

Output Adapters: Implementations of the domain ports that communicate with external services, such as the PostgreSQL database via R2DBC.

## 🛠️ Tech Stack
Language/Framework: Java 17, Spring Boot 3, Spring WebFlux (Reactive)

Database: PostgreSQL with R2DBC (Reactive Driver)

Architecture: Hexagonal (Ports & Adapters)

Containerization: Docker / Podman

Infrastructure as Code (IaC): Terraform

CI/CD: GitHub Actions

Cloud Provider: Amazon Web Services (AWS)

Compute: ECS Fargate

Database: RDS for PostgreSQL

Container Registry: ECR

Testing: JUnit 5, Mockito, StepVerifier

Code Quality: JaCoCo for test coverage

## ⚙️ Prerequisites
To run or deploy this project, you will need the following tools installed:

JDK 17

Gradle 8.x

Podman or Docker

Terraform (v1.5.x or higher)

AWS CLI (configured with your credentials)

## Getting Started
 So easy how to pull the repository in the branch main.

## 🚀 Running the Application Locally

### Start a PostgreSQL Database:
Ensure you have a local PostgreSQL instance running. You can easily start one using Podman/Docker:

podman run --name local-postgres -e POSTGRES_PASSWORD=1234 -e POSTGRES_DB=reactive -p 5432:5432 -d postgres

### Configure the Application:
The src/main/resources/application.yml file is pre-configured with default values to connect to the local database instance described above. No changes are needed if you used that command.

### Run the Application:
Use the Gradle wrapper to start the API:

./gradlew bootRun

The API will be available at http://localhost:8080.


## Access API Documentation:
Once running, the interactive Swagger UI documentation is available at:
http://localhost:8080/swagger-ui.html

## ☁️ Deploying to the AWS Cloud
The deployment process is fully automated using Terraform for infrastructure and GitHub Actions for continuous deployment.

## One-Time Setup
  ### 1. AWS Account: 
Make sure you have an AWS account and have configured your AWS CLI with credentials for an IAM user with Administrator access.
you can use the command:
"aws configure"

### 2. GitHub Secrets: In your GitHub repository, go to Settings > Secrets and variables > Actions and create the following repository secrets:

* AWS_ACCESS_KEY_ID: Your IAM user's access key.

* AWS_SECRET_ACCESS_KEY: Your IAM user's secret key.

* AWS_REGION: The AWS region you want to deploy to (e.g., us-east-2).

* ECR_REPOSITORY: The name of the ECR repository (e.g., nequi-challenge).

## Deployment Flow

### 1. Trigger the CI/CD Pipeline:
Simply push your code to the main branch of your GitHub repository.

* git push origin main


This will automatically trigger the GitHub Actions workflow defined in .github/workflows/deploy.yml. The workflow will:
* a. Build the application.
* b. Create a Docker image.
* c. Push the image to the Amazon ECR repository created by Terraform.
* d. The ECS Fargate service, configured by Terraform for auto-deployment, will detect the new image and deploy it.

### 2. Deploy Infrastructure with Terraform:
Navigate to the terraform directory and run the following commands. This will create all the necessary AWS resources (RDS, ECS Cluster, etc.).

* cd terraform
* terraform init
* terraform apply.

Terraform will prompt you for a secure database password.


### 3. Accessing the Deployed API:
After the Terraform apply command completes, it will output instructions on how to find the Public IP of your running Fargate task. Follow these instructions in the AWS console. You can then access your API at:
* http://<FARGATE-TASK-PUBLIC-IP>:8080/swagger-ui.html

## 🧪 Running Tests
To run the full suite of unit tests and generate a code coverage report, use the following command:

* ./gradlew test
The coverage report will be available at build/reports/jacoco/test/html/index.html.

## 🧹 Cleaning Up
To avoid incurring any costs, you can destroy all the created AWS infrastructure with a single command from the terraform directory:

terraform destroy














