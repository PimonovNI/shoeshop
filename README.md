# Shoeshop (Web-store of shoes)

Web application, with which the user can choose shoes according to his taste and add them to the cart.

User capabilities:

* Creating a personal account
* Sorting and filtering of goods according to different criterias
* Creating and filling a personal cart with goods

Administrator capabilities:

* Adding, editing and deleting shoes items

# Implementation Details

The project was created using the **Spring Framework**. The following technologies were used: *Spring Boot, Spring Web, Spring Security, Spring Data JPA (Hibernate),
Java Mail Sender*. *Thymeleaf* was chosen as the template engine. Additionally, *Spring Validator, Hibernate Validator and Lombok* were used. *JUnit 5, Mockito* were used for testing.

*PostgreSQL* was taken as RDBMS. The database has the following form:

![image](https://drive.google.com/uc?export=view&id=1b47UyuT0EbwTyzN4ncfQXS2zB0yaFN6b)

Authorization is not required to view goods, but to add products to the cart, you must either log in to the created account or register. During registration, you need to
enter your personal email, to which you will receive a letter, where you will have to click on the link to activate the account. Information about the mail domain for distribution
can be configured in the `application.properties` file.

![image](https://drive.google.com/uc?export=view&id=1aZ1NSn-XDtb0-A-A9URZ_uqt5M6N05k5)

The home page displays all products that can be filtered by price, shoe size, brands available, accessories (men's, women's, children's) and can also be sorted by descending and ascending price.

![image](https://drive.google.com/uc?export=view&id=1skzPK3TL_bVgRFRiZcZigO--cFRWuzuJ)

In the user's cart, user can view the selected products, their amount, and make the purchase itself (payment is not feasible, and the purchase button clears the basket and reduces the number
of certain shoes, of a certain size, that were purchased).

![image](https://drive.google.com/uc?export=view&id=1wT899w6rE3UkytiQt3Lvp5Y2Sdxrwmbf)

# How to Build

* Download the project
* Fill in empty fields in the `application.properties` file; These are data about the database and the mailing domain
* Build maven using `mvnw.cmd` or `mvnw`
* Run the project

# Pre Requisites

* Local server for the *PostgreSQL* DBMS
* Local domain for sending e-mails
