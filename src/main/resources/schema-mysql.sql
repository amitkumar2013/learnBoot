DROP TABLE IF EXISTS products;
 
CREATE TABLE products (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  name VARCHAR(25) NOT NULL,
  price DOUBLE DEFAULT NULL
);
