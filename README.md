# Spring Boot Blog Application with JWT Authentication

## Overview

This repository contains the source code for a Blog application built with Spring Boot. The application provides a RESTful API for managing blog posts, comments, users, tags, and categories. JWT (JSON Web Token) authentication is implemented to secure the endpoints.

## Technologies Used

- **Spring Boot**: A powerful and convention-over-configuration-based framework for Java development.
- **Spring HATEOAS**: HATEOAS (Hypermedia as the Engine of Application State) support in Spring Boot for creating RESTful services that follow the principles of REST, including hypermedia-driven navigation.
- **Spring Security**: A powerful and customizable authentication and access control framework for Java applications.

- **JUnit 5**: JUnit 5 is used for writing and running tests.

- **MySQL**: A popular relational database for storing and managing data.
- **JWT Authentication**: JSON Web Token for secure authentication and authorization.

## Getting Started

### Prerequisites

- Java Development Kit (JDK17)
- Apache Maven
- Your favorite IDE (e.g., IntelliJ, Eclipse)

### Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/your-blog-repo.git
   cd your-blog-repo

2. Configure MySQL:
Create a MySQL database and update the application.properties file with the database connection details.

3. Run the app using maven

```bash
mvn spring-boot:run
```
The app will start running at <http://localhost:8103>

## Rest Endpoints

### Auth

| Method | Url | Decription | Valid Request Body | 
| ------ | --- | ---------- | --------------------------- |
| POST   | /api/v1/auth/register | Sign up | [JSON](#register) |
| POST   | /api/v1/auth/login | Log in | [JSON](#login) |

### Category

| Method | Url | Description | Valid Request Body |
| ------ | --- | ----------- | ------------------------- |
| GET    | /api/v1/categories | Get all categories | |
| GET    | /api/v1/categories/{id} | Get category by id | |
| POST   | /api/v1/categories | Add category (Only for admins) | [JSON](#categorycreate) |
| PUT    | /api/v1/categories/{id} | Update category (Only for admins) | [JSON](#categoryupdate) |
| DELETE | /api/v1/categories/{id} | Delete category (Only for admins) | |

### Comment

| Method | Url | Description | Valid Request Body |
| ------ | --- | ----------- | ------------------------- |
| GET    | /api/v1/posts/{postId}/comments | Get all comments for post with given id | |
| GET    | /api/v1/posts/{postId}/comments/{commentId} | Get comment by id | |
| POST   | /api/v1/posts/{postId}/comments | Add comment | [JSON](#commentcreate) |
| PUT    | /api/v1/posts/{postId}/comments/{commentId} | Update comment (Only for admins or user that owns this comment) | [JSON](#commentupdate) |
| DELETE | /api/v1/posts/{postId}/comments/{commentId} | Delete comment (Only for admins or user that owns this comment) | |

### Post 

| Method | Url | Description | Valid Request Body |
| ------ | --- | ----------- | ------------------------- |
| GET    | /api/v1/posts | Get all posts | |
| GET    | /api/v1/posts/category/{categoryId} | Get all posts by category id | |
| GET   | /api/v1/posts/tag/{tagId} | Get all posts by tag id | |
| GET   | /api/v1/posts/user/{userId} | Get all posts by user id | |
| GET   | /api/v1/posts/{postId} | Get post by id | |
| POST   | /api/v1/posts | Add post | [JSON](#postcreate) |
| PUT    | /api/v1/posts/{postId} | Update post (Only for admins or user that owns this post) | [JSON](#postupdate) |
| DELETE | /api/v1/posts/{postId} | Delete post (Only for admins or user that owns this post) | |

### Tag 

| Method | Url | Description | Valid Request Body |
| ------ | --- | ----------- | ------------------------- |
| GET    | /api/v1/tags | Get all tags | |
| GET    | /api/v1/tags/post/{postId} | Get all tags by post id | |
| GET   | /api/v1/tags/{tagId} | Get tag by id | |
| POST   | /api/v1/tags | Add tag (Only for admin)| [JSON](#tagcreate) |
| PUT    | /api/v1/tags/{tagId} | Update tag (Only for admin) | [JSON](#tagupdate) |
| DELETE | /api/v1/posts/{postId} | Delete post (Only for admin) | |

### User 

| Method | Url | Description |  Valid Request Body |
| ------ | --- | ----------- | ------------------------- |
| GET    | /api/v1/users/{userId} | Get user by id | |
| GET    | /api/v1/users/me | Get current logged user | |
| GET   | /api/v1/users/me/comments | Get comments for logged user | |
| POST   | /api/v1/users | Add user (Only for admin)| [JSON](#usercreate) |
| PUT    | /api/v1/users/{userId}/promote-to-admin | Promote user to admin role (Only for admin) | |
| PUT    | /api/v1/users/{userId}/remove-admin-role | Remove admin role (Only for admin) | |
| GET   | /api/v1/users/identities/email/{email} | Get user by email (if response is 404 email is unique) | |
| GET   | /api/v1/users/identities/username/{username} | Get user by username (if response is 404 username is unique) | |

## Valid JSON Requests

##### <a id="register">Register -> /api/v1/auth/register</a>
```json
{
	"email": "adam18@gmail.com",
	"password": "Password1!",
	"username": "adam",
	"firstName": "adam",
	"lastName": "smith",
	"phone": "123456789"
}
```

##### <a id="login">Login -> /api/v1/auth/login</a>
```json
{
	"username": "adam",
	"password": "Password1!"
}
```

##### <a id="categorycreate">Create Category -> /api/v1/categories</a>
```json
{
	"name": "category name"
}
```

##### <a id="categoryupdate">Update Category -> /api/v1/categories/{categoryId}</a>
```json
{
	"name": "category name"
}
```

##### <a id="commentcreate">Create Comment -> /api/v1/posts/{postId}/comments</a>
```json
{
	"body": "comment body"
}
```

##### <a id="commentupdate">Update Comment -> /api/v1/posts/{postId}/comments/{commentId}</a>
```json
{
	"body": "comment body"
}
```

##### <a id="postcreate">Create Post -> /api/v1/posts</a>
```json
{
	"title": "Post title",
	"body": "Body of the post",
	"categoryId": 1,
	"tags": [
		"tag1",
		"tag2",
		"tag3",
		"tag4"
	]
}
```

##### <a id="postupdate">Update Post -> /api/v1/posts/{postId}</a>
```json
{
	"title": "Post title",
	"body": "Body of the post",
	"categoryId": 1,
	"tags": [
		"tag1",
		"tag2",
		"tag3",
		"tag4"
	]
}
```

##### <a id="tagcreate">Create Tag -> /api/v1/tags</a>
```json
{
	"name": "tag name"
}
```

##### <a id="tagupdate">Update Tag -> /api/v1/tags/{tagId}</a>
```json
{
	"name": "tag name"
}
```

##### <a id="usercreate">Create User -> /api/users</a>
```json
{
	"email": "adam18@gmail.com",
	"password": "Password1!",
	"username": "adam",
	"firstName": "adam",
	"lastName": "smith",
	"phone": "123456789"
}
```

##### <a id="userupdate">Update User -> /api/users/{username}</a>
```json
{
	"firstName": "Ervin",
	"lastName": "Howell",
	"username": "ervin",
	"password": "updatedpassword",
	"email": "ervin.howell@gmail.com",
	"address": {
		"street": "Victor Plains",
		"suite": "Suite 879",
		"city": "Wisokyburgh",
		"zipcode": "90566-7771",
		"geo": {
			"lat": "-43.9509",
			"lng": "-34.4618"
		}
	},
	"phone": "010-692-6593 x09125",
	"website": "http://erwinhowell.com",
	"company": {
		"name": "Deckow-Crist",
		"catchPhrase": "Proactive didactic contingency",
		"bs": "synergize scalable supply-chains"
	}
}
```

## JWT Authentication

JSON Web Token (JWT) is used for secure authentication in this application. Here's how it works:

1. **Login (Authentication)**:
   - When a user successfully logs in (using the login endpoint), the server generates a JWT containing information about the user (e.g., user email, roles).
   - This JWT is signed with a secret key known only to the server, ensuring its integrity.

2. **Token Response**:
   - The server sends the generated JWT as part of the response to the login request.
   - The client should store this token securely.

3. **Subsequent Requests (Authorization)**:
   - For each subsequent request that requires authorization, the client includes the JWT in the request headers.
   - The JWT is included in the `Authorization` header using the "Bearer" authentication scheme:

     ```
     Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
     ```

4. **Server Verification**:
   - On the server side, the application verifies the integrity and authenticity of the received JWT using the secret key.
   - If the token is valid, the server processes the request.
