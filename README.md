📘 Task Management System API
🌟 Introduction
Task Management System API is a modern REST backend for managing projects, tasks, comments, file attachments, and labels.
The system is built with Spring Boot and designed for scalability, security, and seamless integration with external services such as Dropbox.
The API supports:
- user registration and authentication
- project management
- task management
- comments
- file attachments (Dropbox integration)
- labels
- user roles
- notifications

⚙️ Technologies & Tools
| Category | Tools / Frameworks | 
| Core Framework | Spring Boot 3 | 
| Security & Auth | Spring Security, JWT | 
| Data Access | Spring Data JPA, Hibernate | 
| Database | MySQL | 
| File Storage | Dropbox API | 
| Validation | Jakarta Validation | 
| Documentation | Swagger / OpenAPI | 
| Containerization | Docker, Docker Compose | 
| Build Tool | Maven | 
| Language | Java 17 | 
| Other | Lombok, MapStruct , Testcontainers | 



🔐 Authentication & Authorization
The system uses JWT tokens for authentication.
Main Endpoints:
|Endpoint | Method | Description | 
| /auth/register | POST | Register a new user | 
| /auth/login | POST | Log in and receive a JWT token | 


Token Usage:
Authorization: Bearer <jwt_token>


Roles:
- USER — can manage projects, tasks, comments, attachments
- ADMIN — can manage users and system-level resources

🧩 Main Features by Controller

👤 AuthController
| Endpoint | Method | Description | 
| /auth/register | POST | Register a new user | 
| /auth/login | POST | Authenticate and retrieve JWT | 



👥 UsersController
| Endpoint | Method | Access | Description | 
| /users/{id}/role | PUT | ADMIN | Update user role | 
| /users | GET | USER | Get current user profile | 
| /users | PUT | USER | Update profile information | 
| /users/{id} | DELETE | ADMIN | Delete user |



📁 ProjectController
| Endpoint | Method | Access | Description | 
| /projects | POST | ADMIN | Create a new project | 
| /projects | GET | USER | Retrieve user projects | 
| /projects/{id} | GET | USER | Get project details | 
| /projects/{id} | PUT | ADMIN | Update project | 
| /projects/{id} | DELETE | ADMIN | Delete project | 



📝 TaskController
| Endpoint | Method | Access | Description |
| /tasks | POST | ADMIN | Create a new task |
| /tasks | GET | USER | Retrieve tasks for a project |
| /tasks/{id} | GET | USER | Get task details |
| /tasks/{id} | PUT | ADMIN | Update task |
| /tasks/{id} | DELETE | ADMIN | Delete task |



💬 CommentController
| Endpoint | Method | Access | Description |
| /comments | POST | USER | Add a comment to a task |
| /comments?taskId={taskId} | GET | USER | Retrieve comments for a task |
| /comments/{id} | DELETE | USER | Delete comment |



📎 AttachmentController (Dropbox Integration)
| Endpoint | Method | Access | Description |
| /attachments | POST | ADMIN | Upload a file to Dropbox and save metadata |
| /attachments?taskId={taskId} | GET | USER | Retrieve attachments for a task |
| /attachments/{id}/download | GET | USER | Download file from Dropbox |
| /attachments/{id} | DELETE | ADMIN | Delete attachment (DB + Dropbox) |



🏷 LabelController
| Endpoint | Method | Access | Description |
| /labels | POST | ADMIN | Create a new label |
| /labels | GET | USER | Retrieve labels |
| /labels/{id} | PUT | ADMIN | Update label |
| /labels/{id} | DELETE | ADMIN | Delete label |

☁️ Dropbox Integration
Files are not stored locally — only in Dropbox.
🔄 Flow:
- User uploads a file
- Backend uploads it to Dropbox
- Dropbox returns a fileId
- The backend stores fileId + metadata in the database
- When downloading, the backend fetches the file from Dropbox using fileId
This approach keeps the system lightweight and scalable.

🧾 .env Example
dropbox.app-key=
dropbox.app-secret=
dropbox.refresh-token=
sendgrid.api-key=

MYSQLDB_USER=
MYSQLDB_PASSWORD=
MYSQLDB_DATABASE=
MYSQLDB_LOCAL_PORT=
MYSQLDB_DOCKER_PORT=

SPRING_LOCAL_PORT=
SPRING_DOCKER_PORT=
DEBUG_PORT=

🧩 MapStruct Integration
Mapping between Entities and DTOs is handled by MapStruct, ensuring:

Clean separation between layers Minimal boilerplate High performance conversions

Main mappers include: AttachmentMapper, UserMapper, CommentMapper, LabelMapper, TaskMapper, ProjectMapper.

🔔 Notifications
- reminder of deadlines via email

⚠️ Error Handling
A global exception handler:
- processes validation errors
- returns structured JSON responses
- handles DropBoxException, RegistrationException

🚧 Challenges & Solutions
| Challenge | Solution | 
| Secure authentication | JWT + role-based access | 
| File storage | Dropbox API | 
| Validation | Jakarta Validation | 
| Clean architecture | DTO + MapStruct | 
| Database migrations | Liquibase | 
| Testing | Testcontainers + MockMvc | 

Swagger UI:
👉 http://localhost:8080/swagger-ui/index.html

🎯 Conclusion
Task Management System API is a modern, flexible, and scalable backend that includes all essential features for managing projects and tasks, Dropbox integration, JWT-based security, and production-ready infrastructure.

👤 Author Petro Ipatii 📧 petroipatiy@gmail.com
