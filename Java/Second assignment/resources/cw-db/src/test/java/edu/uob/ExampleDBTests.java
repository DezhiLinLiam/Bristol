package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

public class ExampleDBTests {

    private DBServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    // Random name generator - useful for testing "bare earth" queries (i.e. where tables don't previously exist)
    private String generateRandomName()
    {
        String randomName = "";
        for(int i=0; i<10 ;i++) randomName += (char)( 97 + (Math.random() * 25.0));
        return randomName;
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
        "Server took too long to respond (probably stuck in an infinite loop)");
    }

    // A basic test that creates a database, creates a table, inserts some test data, then queries it.
    // It then checks the response to see that a couple of the entries in the table are returned as expected
    @Test
    public void testBasicCreateAndQuery() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Steve"), "An attempt was made to add Steve to the table, but they were not returned by SELECT *");
        assertTrue(response.contains("Clive"), "An attempt was made to add Clive to the table, but they were not returned by SELECT *");
    }

    // A test to make sure that querying returns a valid ID (this test also implicitly checks the "==" condition)
    // (these IDs are used to create relations between tables, so it is essential that they work !)
    @Test
    public void testQueryID() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        String response = sendCommandToServer("SELECT id FROM marks WHERE name == 'Steve';");
        // Convert multi-lined responses into just a single line
        String singleLine = response.replace("\n"," ").trim();
        // Split the line on the space character
        String[] tokens = singleLine.split(" ");
        // Check that the very last token is a number (which should be the ID of the entry)
        String lastToken = tokens[tokens.length-1];
        try {
            Integer.parseInt(lastToken);
        } catch (NumberFormatException nfe) {
            fail("The last token returned by `SELECT id FROM marks WHERE name == 'Steve';` should have been an integer ID, but was " + lastToken);
        }
    }

    // A test to make sure that databases can be reopened after server restart
    @Test
    public void testTablePersistsAfterRestart() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        // Create a new server object
        server = new DBServer();
        sendCommandToServer("USE " + randomName + ";");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("Steve"), "Steve was added to a table and the server restarted - but Steve was not returned by SELECT *");
    }

    // Test to make sure that the [ERROR] tag is returned in the case of an error (and NOT the [OK] tag)
    @Test
    public void testForErrorTag() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        String response = sendCommandToServer("SELECT * FROM libraryfines;");
        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
    }
    @Test
    public void testAlterTableAndQuery() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE students (name, age);");
        sendCommandToServer("ALTER TABLE students ADD gpa;");
        sendCommandToServer("INSERT INTO students VALUES ('Alice', 22, 3.8);");
        sendCommandToServer("INSERT INTO students VALUES ('Bob', 20, 3.5);");
        String response = sendCommandToServer("SELECT * FROM students;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Alice"), "An attempt was made to add Alice to the table, but they were not returned by SELECT *");
        assertTrue(response.contains("3.8"), "An attempt was made to add a GPA column to the table, but it was not returned by SELECT *");
    }
    @Test
    public void testDeleteAndUpdate() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE employees (name, salary);");
        sendCommandToServer("INSERT INTO employees VALUES ('John', 5000);");
        sendCommandToServer("INSERT INTO employees VALUES ('Jane', 6000);");
        sendCommandToServer("DELETE FROM employees WHERE id == 1;");
        sendCommandToServer("UPDATE employees SET salary = 7000 WHERE id == 2;");
        String response = sendCommandToServer("SELECT * FROM employees;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertFalse(response.contains("John"), "An attempt was made to delete John from the table, but they were returned by SELECT *");
        assertTrue(response.contains("7000"), "An attempt was made to update Jane's salary, but the updated value was not returned by SELECT *");
    }
    @Test
    public void testDropDatabaseAndTable() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE products (id, name, price);");
        sendCommandToServer("DROP TABLE products;");
        String response = sendCommandToServer("SELECT * FROM products;");
        assertTrue(response.contains("[ERROR]"), "A query was made on a dropped table, however an [ERROR] tag was not returned");
        sendCommandToServer("DROP DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        response = sendCommandToServer("CREATE TABLE categories (id, name);");
        assertTrue(response.contains("[ERROR]"), "An attempt was made to create a table in a dropped database, however an [ERROR] tag was not returned");
    }
    @Test
    public void testSelectWithMultipleConditions() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE products (name, price, in_stock);");
        sendCommandToServer("INSERT INTO products VALUES ('Product A', 100, TRUE);");
        sendCommandToServer("INSERT INTO products VALUES ('Product B', 200, TRUE);");
        sendCommandToServer("INSERT INTO products VALUES ('Product C', 300, FALSE);");
        sendCommandToServer("INSERT INTO products VALUES ('Product D', 400, FALSE);");
        String response = sendCommandToServer("SELECT * FROM products WHERE price >= 200 AND in_stock == TRUE;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Product B"), "Expected Product B to be returned, but it was not found in the response");
        assertFalse(response.contains("Product A"), "Product A should not be in the result, but it was found in the response");
    }
    @Test
    public void testInsertAndQueryFloatValues() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE scores (name, score);");
        sendCommandToServer("INSERT INTO scores VALUES ('Alice', 95.5);");
        sendCommandToServer("INSERT INTO scores VALUES ('Bob', 89.0);");
        sendCommandToServer("INSERT INTO scores VALUES ('Charlie', 76.5);");
        String response = sendCommandToServer("SELECT * FROM scores WHERE score > 80;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Alice") && response.contains("95.5"), "Expected Alice with a score of 95.5 to be in the response, but not found");
        assertTrue(response.contains("Bob") && response.contains("89.0"), "Expected Bob with a score of 89.0 to be in the response, but not found");
        assertFalse(response.contains("Charlie"), "Charlie should not be in the result, but it was found in the response");
    }
    @Test
    public void testCombinedDeleteAndUpdate() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE products (name, price);");
        sendCommandToServer("INSERT INTO products VALUES ('ProductA', 10.0);");
        sendCommandToServer("INSERT INTO products VALUES ('ProductB', 20.0);");
        sendCommandToServer("INSERT INTO products VALUES ('ProductC', 30.0);");
        sendCommandToServer("DELETE FROM products WHERE id == 2;");
        sendCommandToServer("UPDATE products SET price = 15.0 WHERE id == 1;");
        String response = sendCommandToServer("SELECT * FROM products;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertFalse(response.contains("ProductB"), "An attempt was made to delete ProductB from the table, but it was returned by SELECT *");
        assertTrue(response.contains("15.0"), "An attempt was made to update ProductA's price, but the updated value was not returned by SELECT *");
    }
    @Test
    public void testSelectWithTwoConditions() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE students (name, age, grade);");
        sendCommandToServer("INSERT INTO students VALUES ('Alice', 18, 'A');");
        sendCommandToServer("INSERT INTO students VALUES ('Bob', 19, 'B');");
        sendCommandToServer("INSERT INTO students VALUES ('Charlie', 20, 'A');");
        sendCommandToServer("INSERT INTO students VALUES ('David', 21, 'C');");
        String response = sendCommandToServer("SELECT * FROM students WHERE age >= 19 AND grade == 'A';");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Charlie"), "Charlie should be returned as they meet both conditions");
        assertFalse(response.contains("Alice"), "Alice should not be returned as they do not meet the age condition");
        assertFalse(response.contains("Bob"), "Bob should not be returned as they do not meet the grade condition");
        assertFalse(response.contains("David"), "David should not be returned as they do not meet the grade condition");
    }
    @Test
    public void testCaseInsensitiveQuery() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE countries (id, name, population);");
        sendCommandToServer("INSERT INTO countries VALUES (1, 'United States', 331000000);");
        sendCommandToServer("INSERT INTO countries VALUES (2, 'India', 1380000000);");
        String response = sendCommandToServer("select * from COUNTRIES;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("United States"), "An attempt was made to query the table, but 'United States' was not returned by SELECT *");
        assertTrue(response.contains("India"), "An attempt was made to query the table, but 'India' was not returned by SELECT *");
    }
    @Test
    public void testJoinOperation() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE authors (name);");
        sendCommandToServer("INSERT INTO authors VALUES ('AuthorA');");
        sendCommandToServer("INSERT INTO authors VALUES ('AuthorB');");
        sendCommandToServer("CREATE TABLE books (title, author_id);");
        sendCommandToServer("INSERT INTO books VALUES ('Book1', 1);");
        sendCommandToServer("INSERT INTO books VALUES ('Book2', 2);");
        String response = sendCommandToServer("JOIN books AND authors ON id AND author_id;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("AuthorA"), "An attempt was made to query the table, but 'AuthorA' was not returned by the JOIN operation");
        assertTrue(response.contains("Book1"), "An attempt was made to query the table, but 'Book1' was not returned by the JOIN operation");
        assertTrue(response.contains("AuthorB"), "An attempt was made to query the table, but 'AuthorB' was not returned by the JOIN operation");
        assertTrue(response.contains("Book2"), "An attempt was made to query the table, but 'Book2' was not returned by the JOIN operation");
    }
    @Test
    public void testInsertAndQueryMultipleConditions() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE products (name, price, stock);");
        sendCommandToServer("INSERT INTO products VALUES ('Product A', 100.0, 10);");
        sendCommandToServer("INSERT INTO products VALUES ('Product B', 200.0, 20);");
        sendCommandToServer("INSERT INTO products VALUES ('Product C', 150.0, 15);");
        String response = sendCommandToServer("SELECT * FROM products WHERE price > 100 AND stock < 20;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Product C"), "An attempt was made to query products with price > 100 and stock < 20, but Product C was not returned");
    }
    @Test
    public void testUpdateAndQuery() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE users (name, age);");
        sendCommandToServer("INSERT INTO users VALUES ('Alice', 25);");
        sendCommandToServer("INSERT INTO users VALUES ('Bob', 30);");
        sendCommandToServer("UPDATE users SET age = 35 WHERE name == 'Alice';");
        String response = sendCommandToServer("SELECT * FROM users WHERE name == 'Alice';");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("35"), "An attempt was made to update Alice's age to 35, but the updated value was not returned by SELECT");
    }
    @Test
    public void testDatabaseNameCaseInsensitive() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");

        String upperCaseName = randomName.toUpperCase();
        String lowerCaseName = randomName.toLowerCase();
        String mixedCaseName = randomName.substring(0, 1).toUpperCase() + randomName.substring(1).toLowerCase();

        sendCommandToServer("USE " + upperCaseName + ";");
        sendCommandToServer("CREATE TABLE upperCaseTable (name, mark, pass);");

        sendCommandToServer("USE " + lowerCaseName + ";");
        sendCommandToServer("CREATE TABLE lowerCaseTable (name, mark, pass);");

        sendCommandToServer("USE " + mixedCaseName + ";");
        sendCommandToServer("CREATE TABLE mixedCaseTable (name, mark, pass);");

        String upperCaseResponse = sendCommandToServer("SELECT * FROM uppercasetable;");
        String lowerCaseResponse = sendCommandToServer("SELECT * FROM lowercasetable;");
        String mixedCaseResponse = sendCommandToServer("SELECT * FROM mixedcasetable;");

        assertTrue(upperCaseResponse.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned for the upper case database name");
        assertTrue(lowerCaseResponse.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned for the lower case database name");
        assertTrue(mixedCaseResponse.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned for the mixed case database name");
    }
    @Test
    public void testColumnNameCaseInsensitiveForQuery() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE students (Name, Age, GPA);");
        sendCommandToServer("INSERT INTO students (Name, Age, GPA) VALUES ('Alice', 20, 3.5);");

        String upperCaseResponse = sendCommandToServer("SELECT NAME, AGE, GPA FROM students;");
        String lowerCaseResponse = sendCommandToServer("SELECT name, age, gpa FROM students;");
        String mixedCaseResponse = sendCommandToServer("SELECT NaMe, aGe, GpA FROM students;");

        assertTrue(upperCaseResponse.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned for the upper case column names");
        assertTrue(lowerCaseResponse.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned for the lower case column names");
        assertTrue(mixedCaseResponse.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned for the mixed case column names");

        assertTrue(upperCaseResponse.contains("Name"), "The original case of the column name 'Name' was not preserved in the response");
        assertTrue(upperCaseResponse.contains("Age"), "The original case of the column name 'Age' was not preserved in the response");
        assertTrue(upperCaseResponse.contains("GPA"), "The original case of the column name 'GPA' was not preserved in the response");
    }
    @Test
    public void testCannotModifyIdValue() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE students ( name, age);");
        sendCommandToServer("INSERT INTO students VALUES ( 'Alice', 20);");
        sendCommandToServer("INSERT INTO students VALUES ('Bob', 22);");

        String response = sendCommandToServer("UPDATE students SET id = 3 WHERE name == 'Alice';");
        assertTrue(response.contains("[ERROR]"), "An attempt was made to modify the ID value, but no error was returned");

        response = sendCommandToServer("SELECT * FROM students WHERE name == 'Alice';");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("1"), "The ID value for Alice should remain 1, indicating it was not modified");
    }

    @Test
    public void testIdContinuesAfterDeleteLastRowExample() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE test_table ( name);");
        sendCommandToServer("INSERT INTO test_table VALUES ( 'Alice');");
        sendCommandToServer("INSERT INTO test_table VALUES ( 'Bob');");
        sendCommandToServer("INSERT INTO test_table VALUES ( 'Charlie');");
        sendCommandToServer("DELETE FROM test_table WHERE id == 3;");
        sendCommandToServer("INSERT INTO test_table VALUES ('David');");

        String response = sendCommandToServer("SELECT * FROM test_table WHERE id == 4;");

        // Assert that the response contains the expected data
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("4"), "The ID of the last row should be 4 after deleting the row with ID 3");
        assertTrue(response.contains("David"), "An attempt was made to add David to the table, but they were not returned by SELECT *");
    }
    @Test
    public void testReservedWordsNotAllowedAsNames() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        String response = sendCommandToServer("CREATE DATABASE SELECT;");
        assertTrue(response.contains("[ERROR]"), "A reserved word was used as a database name, but an [ERROR] tag was not returned");

        response = sendCommandToServer("CREATE TABLE FROM  name);");
        assertTrue(response.contains("[ERROR]"), "A reserved word was used as a table name, but an [ERROR] tag was not returned");

        sendCommandToServer("CREATE TABLE test_table name);");


        response = sendCommandToServer("ALTER TABLE test_table ADD COLUMN WHERE TRUE;");
        assertTrue(response.contains("[ERROR]"), "A reserved word was used as a column name, but an [ERROR] tag was not returned");
    }
    @Test
    public void testSelectWithMultipleWhereConditions1() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE users (name, age, city);");
        sendCommandToServer("INSERT INTO users VALUES ('Alice', 25, 'New York');");
        sendCommandToServer("INSERT INTO users VALUES ('Bob', 30, 'San Francisco');");
        sendCommandToServer("INSERT INTO users VALUES ('Charlie', 22, 'Los Angeles');");
        sendCommandToServer("INSERT INTO users VALUES ('David', 30, 'New York');");

        String response = sendCommandToServer("SELECT * FROM users WHERE age == 30 AND city == 'New York';");

        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        assertFalse(response.contains("Alice"));
        assertFalse(response.contains("Bob"));
        assertFalse(response.contains("Charlie"));
        assertTrue(response.contains("David"));
    }

    @Test
    public void testSelectWithMultipleWhereConditions2() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE products (name, price, category);");
        sendCommandToServer("INSERT INTO products VALUES ('Apple', 1.5, 'Fruit');");
        sendCommandToServer("INSERT INTO products VALUES ('Banana', 0.5, 'Fruit');");
        sendCommandToServer("INSERT INTO products VALUES ('Carrot', 1, 'Vegetable');");
        sendCommandToServer("INSERT INTO products VALUES ('Potato', 0.8, 'Vegetable');");

        String response = sendCommandToServer("SELECT * FROM products WHERE price >= 1 AND category == 'Fruit';");

        assertTrue(response.contains("[OK]"));
        assertFalse(response.contains("[ERROR]"));
        assertTrue(response.contains("Apple"));
        assertFalse(response.contains("Banana"));
        assertFalse(response.contains("Carrot"));
        assertFalse(response.contains("Potato"));
    }
    @Test
    public void testSelectWithThreeWhereConditions() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE students (name, age, grade, passed);");
        sendCommandToServer("INSERT INTO students VALUES ('Alice', 25, 85, TRUE);");
        sendCommandToServer("INSERT INTO students VALUES ('Bob', 24, 75, TRUE);");
        sendCommandToServer("INSERT INTO students VALUES ('Cathy', 23, 90, TRUE);");
        sendCommandToServer("INSERT INTO students VALUES ('David', 22, 60, FALSE);");
        sendCommandToServer("INSERT INTO students VALUES ('Eva', 21, 95, TRUE);");

        String response = sendCommandToServer("SELECT * FROM students WHERE age >= 23 AND grade > 75 AND passed == TRUE;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Alice"), "Alice should be in the result set");
        assertTrue(response.contains("Cathy"), "Cathy should be in the result set");
        assertFalse(response.contains("Bob"), "Bob should not be in the result set");
        assertFalse(response.contains("David"), "David should not be in the result set");
        assertFalse(response.contains("Eva"), "Eva should not be in the result set");
    }
    @Test
    public void testSelectWithThreeWhereConditionsUsingOr() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE products (name, price, category, inStock);");
        sendCommandToServer("INSERT INTO products VALUES ('ProductA', 100, 'Electronics', TRUE);");
        sendCommandToServer("INSERT INTO products VALUES ('ProductB', 200, 'Electronics', FALSE);");
        sendCommandToServer("INSERT INTO products VALUES ('ProductC', 150, 'Clothing', TRUE);");
        sendCommandToServer("INSERT INTO products VALUES ('ProductD', 250, 'Clothing', FALSE);");
        sendCommandToServer("INSERT INTO products VALUES ('ProductE', 50, 'Accessories', TRUE);");

        String response = sendCommandToServer("SELECT * FROM products WHERE price < 200 OR category == 'Clothing' OR inStock == FALSE;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("ProductA"), "ProductA should be in the result set");
        assertTrue(response.contains("ProductB"), "ProductB should be in the result set");
        assertTrue(response.contains("ProductC"), "ProductC should be in the result set");
        assertTrue(response.contains("ProductD"), "ProductD should be in the result set");
        assertTrue(response.contains("ProductE"), "ProductE should be in the result set");

    }
    @Test
    public void testDeleteWithParenthesizedConditions() {
        // 创建测试环境
        String randomName = generateRandomName();  // 生成随机名称
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE employees ( name, father);");
        sendCommandToServer("INSERT INTO employees VALUES ( 'John', 'David');");
        sendCommandToServer("INSERT INTO employees VALUES ( 'Jane', 'John');");
        sendCommandToServer("INSERT INTO employees VALUES ( 'Michael', 'Robert');");
        sendCommandToServer("INSERT INTO employees VALUES ( 'David', 'Frank');");
        sendCommandToServer("INSERT INTO employees VALUES ( 'Lucy', 'Robert');");

        // 进行删除操作
        String response = sendCommandToServer("DELETE FROM employees WHERE id == 2 AND ((name == 'John' OR name == 'Jane') or (father == 'John' OR father == 'Robert'));");

        // 验证删除结果是否正确
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");

        // 验证删除的记录是否正确
        response = sendCommandToServer("SELECT * FROM employees;");
        assertTrue(response.contains("John"), "John should be deleted");
        assertFalse(response.contains("Jane"), "Jane should not be deleted");
        assertTrue(response.contains("Michael"), "Michael should not be deleted");
        assertTrue(response.contains("David"), "David should not be deleted");
        assertTrue(response.contains("Lucy"), "Lucy should not be deleted");
    }
@Test
    public void testSelectWithParenthesizedConditions() {
        // 创建测试环境
        String randomName = generateRandomName();  // 生成随机名称
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE employees (name, father);");
        sendCommandToServer("INSERT INTO employees VALUES ('John', 'David');");
        sendCommandToServer("INSERT INTO employees VALUES ('Jane', 'Liam');");
        sendCommandToServer("INSERT INTO employees VALUES ('Michael', 'Robert');");
        sendCommandToServer("INSERT INTO employees VALUES ('David', 'Frank');");
        sendCommandToServer("INSERT INTO employees VALUES ('Lucy', 'Robert');");

        // 执行 SELECT 操作
        String response = sendCommandToServer("SELECT * FROM employees WHERE (id > 1 AND id < 5) AND ((name == 'John' OR name == 'Jane') OR (father == 'Liam' OR father == 'Robert'));");

        // 验证查询结果是否正确
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertFalse(response.contains("John"), "John should not be in the result");
        assertTrue(response.contains("Jane"), "Jane should be in the result");
        assertTrue(response.contains("Michael"), "Michael should be in the result");
        assertFalse(response.contains("David"), "David should not be in the result");
        assertFalse(response.contains("Lucy"), "Lucy should not be in the result");
    }
    @Test
    public void testUpdateWithParenthesizedConditions() {
        // 创建测试环境
        String randomName = generateRandomName();  // 生成随机名称
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE employees (name, father);");
        sendCommandToServer("INSERT INTO employees VALUES ('John', 'David');");
        sendCommandToServer("INSERT INTO employees VALUES ('Jane', 'John');");
        sendCommandToServer("INSERT INTO employees VALUES ('Michael', 'Robert');");
        sendCommandToServer("INSERT INTO employees VALUES ( 'David', 'Frank');");
        sendCommandToServer("INSERT INTO employees VALUES ( 'Lucy', 'Robert');");

        // 执行 UPDATE 操作
        String response = sendCommandToServer("UPDATE employees SET name = 'Updated' WHERE (id > 1 AND id < 5) AND ((name == 'John' OR name == 'Jane') OR (father == 'John' OR father == 'Robert'));");

        // 验证更新结果是否正确
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
    }
    @Test
    public void testSelectWithComplexParenthesizedConditions() {
        // 创建测试环境
        String randomName = generateRandomName();  // 生成随机名称
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE employees ( name, father, age);");
        sendCommandToServer("INSERT INTO employees VALUES ( 'John', 'David', 25);");
        sendCommandToServer("INSERT INTO employees VALUES ( 'Jane', 'Liam', 30);");
        sendCommandToServer("INSERT INTO employees VALUES ( 'Michael', 'Robert', 35);");
        sendCommandToServer("INSERT INTO employees VALUES ( 'David', 'Frank', 40);");
        sendCommandToServer("INSERT INTO employees VALUES ( 'Lucy', 'Bob', 45);");

        // 执行 SELECT 操作
        String response = sendCommandToServer("SELECT * FROM employees WHERE (age >= 30 AND age <= 40) AND (((name == 'John' OR name == 'Jane') AND father == 'Liam') OR ((father == 'Robert' OR father == 'Frank') AND id < 5));");

        // 验证查询结果是否正确
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Liam"), "John should not be in the result");
        assertTrue(response.contains("Jane"), "Jane should be in the result");
        assertTrue(response.contains("Michael"), "Michael should be in the result");
        assertTrue(response.contains("David"), "David should be in the result");
        assertFalse(response.contains("Lucy"), "Lucy should not be in the result");
    }
    @Test
    public void testSelectWithComplexConditionsAndLike() {
// Create test environment
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE employees ( name, father, age);");
        sendCommandToServer("INSERT INTO employees VALUES ( 'John', 'David', 25);");
        sendCommandToServer("INSERT INTO employees VALUES ( 'Jane', 'Liam', 30);");
        sendCommandToServer("INSERT INTO employees VALUES ( 'Michael', 'Robert', 35);");
        sendCommandToServer("INSERT INTO employees VALUES ( 'David', 'Frank', 40);");
        sendCommandToServer("INSERT INTO employees VALUES ( 'Lucy', 'Bob', 45);");
// Execute SELECT operation
        String response = sendCommandToServer("SELECT * FROM employees WHERE ((name LIKE 'J' OR name LIKE 'D') AND age >= 25) OR (father LIKE 'm' AND (age > 30 OR name != 'David'));");

// Verify that the query result is correct
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("John"), "John should be in the result");
        assertTrue(response.contains("Jane"), "Jane should be in the result");
        assertTrue(response.contains("David"), "David should be in the result");
        assertFalse(response.contains("Lucy"), "Lucy should not be in the result");
    }
}
