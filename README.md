# Online-Book-Store

The Online Book Store application is a platform designed to facilitate the buying and selling of books. 

This is back-end part of application developed on Java.

### **GENERAL INFO**
**In this app we will have the following domain models (entities):**
- **User:** Contains information about the registered user including their authentication details and personal information.
- **Role:** Represents the role of a user in the system, for example, admin or user.
- **Book:** Represents a book available in the store.
- **Category:** Represents a category that a book can belong to.
- **ShoppingCart:** Represents a user's shopping cart.
- **CartItem:** Represents an item in a user's shopping cart.
- **Order:** Represents an order placed by a user.
- **OrderItem:** Represents an item in a user's order.

**People involved:**
- **Shopper (User):** Someone who looks at books, puts them in a basket (shopping cart), and buys them.
- **Manager (Admin):** Someone who arranges the books on the shelf and watches what gets bought.

**Things Shoppers Can Do:**
1. Join and sign in:
- Join the store.
- Sign in to look at books and buy them.
2. Look at and search for books:
- Look at all the books.
- Look closely at one book.
- Find a book by typing its name.
3. Look at bookshelf sections:
- See all bookshelf sections.
- See all books in one section.
4. Use the basket:
- Put a book in the basket.
- Look inside the basket.
- Take a book out of the basket.
5. Buying books:
- Buy all the books in the basket.
- Look at past receipts.
6. Look at receipts:
- See all books on one receipt.
- Look closely at one book on a receipt.

**Things Managers Can Do:**
1. Arrange books:
- Add a new book to the store.
- Change details of a book.
- Remove a book from the store.
2. Organize bookshelf sections:
- Make a new bookshelf section.
- Change details of a section.
- Remove a section.
3. Look at and change receipts:
- Change the status of a receipt, like "Shipped" or "Delivered".

### **LIST OF AVAILABLE ENDPOINTS:**
**All endpoints were documented using Swagger**
1. Available for non authenticated users:
- POST: /api/auth/register
- POST: /api/auth/login

2. Available for users with role USER
- GET: /api/books
- GET: /api/books/{id}
- GET: /api/categories
- GET: /api/categories/{id}
- GET: /api/categories/{id}/books
- GET: /api/cart
- POST: /api/cart
- PUT: /api/cart/cart-items/{cartItemId}
- DELETE: /api/cart/cart-items/{cartItemId}
- GET: /api/orders
- POST: /api/orders
- GET: /api/orders/{orderId}/items
- GET: /api/orders/{orderId}/items/{itemId}

3. Available for users with role ADMIN:
- POST: /api/books/
- PUT: /api/books/{id}
- DELETE: /api/books/{id}
- POST: /api/categories
- PUT: /api/categories/{id}
- DELETE: /api/categories/{id}
- PATCH: /api/orders/{id}

### KEY TECHNOLOGIES
1. Language: Java. Build System: Maven (with pom.xml file).
2. The app was created using SOLID principles and follows the Controller - Service - Repository architecture with REST software architectural style for APIs.
3. Security was implemented using Spring Boot Security with Bearer authorization using JWT tokens.
4. The Repository layer was implemented using Spring Data JPA (JpaRepository) and Custom Queries.
5. All sensitive information is protected using Data Transfer Objects (DTOs).
6. Validation was applied for queries, and custom validation annotations were created for email and password fields in UserRegistrationRequestDto.
7. Entities fetched from the repository level were automatically transformed into DTOs using Mappers (with MapStruct plugin using Lombok and MapStruct libraries) at the service level.
8. CustomGlobalExceptionHandler was added to provide more informative exception handling.
9. Dynamic filtering of books by author and title was implemented using Criteria Query.
10. Pagination and Swagger were integrated for specific requests.
11. All endpoints were documented using Swagger.
12. Liquibase was used as a database schema change management solution.
- The default user is bob.admin@gmail.com with the password bob.admin and the role ADMIN.
- All users registered through the common available endpoint POST: /api/auth/register will have the default role USER.
13. Tests were written using Testcontainers for repository-level, Mockito for service-level, and MockMvc for controller-level.
14. Finally, Docker was integrated for easy application deployment.