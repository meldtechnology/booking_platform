Inspect the project, create a package `repository` under the domain package and create a Reactive Data`
CatalogRepository` with CRUD operations.

-------------------------------------------
Inspect the project, create a package `dto`. create DTO as Java record for the entities and place it in the `dto` 
package. Add appropriate validation constraints on the DTO object. 
Create a package `mapper` place the entity mapper in the `mapper` package. The mapper should be able to map the 
entity to the DTO and vice versa.

-------------------------------------------
Inspect the project and remove the verbose Javadoc comments from the service and controller classes. Also remove the 
Javadoc comments from the model classes, `CatalogRepository`, `CustomCatalogRepositoryImpl`, DTO and Mapper.

-------------------------------------------
Inspect the controller amd creates an OpenAPI Specification documentation called `openapi.yaml` from the controller 
operations. Place it in the `resources/static` folder. Verify the controller tests are passing.

-------------------------------------------
Create a Dockerfile for this Java app. The main class is UserServiceApplication.java. Use Java 21. The app should 
run on port 9000.

-------------------------------------------
Create a Terraform file `ec2.tf` to deploy the app to AWS EC2. The file should provision an AWS EC2 instance of type 
t2.micro in an African region, installs the Docker daemon, and returns the instance's hostname. The app should run on 
port 9000. 

-------------------------------------------
Create a Kubernetes deployment file `booking.yaml` for the booking platform. The image name is josleke/booking_platform:latest. The app 
should run on port 9000.

--------------------------------------------
Create a GitHub Actions workflow that builds the Booking application on every merge to the main branch and deploys it to 
EC2.