SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';


DROP SCHEMA IF EXISTS `dss_cadeia_db`;
CREATE SCHEMA IF NOT EXISTS `dss_cadeia_db` DEFAULT CHARACTER SET utf8mb4;
USE `dss_cadeia_db`;


CREATE USER IF NOT EXISTS 'dss_user'@'localhost' IDENTIFIED BY 'dss2526';
GRANT ALL PRIVILEGES ON dss_cadeia_db.* TO 'dss_user'@'localhost';
FLUSH PRIVILEGES;



CREATE TABLE IF NOT EXISTS `restaurant` (
  `idRest` VARCHAR(10) NOT NULL,
  `Nome` VARCHAR(45) NOT NULL,
  `Localização` VARCHAR(45) NOT NULL,
  `nextOrderID` INT NOT NULL,
  `nextIngredientOrderID` INT NOT NULL,
  PRIMARY KEY (`idRest`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `funcionario` (
  `number` VARCHAR(10) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `phone_number` VARCHAR(15) NOT NULL,
  `password` VARCHAR(50) NOT NULL,
  `role` VARCHAR(15) NOT NULL,
  `restaurant_idRest` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`number`),
  INDEX `fk_funcionario_restaurant1_idx` (`restaurant_idRest` ASC) VISIBLE,
  CONSTRAINT `fk_funcionario_restaurant1`
    FOREIGN KEY (`restaurant_idRest`)
    REFERENCES `restaurant` (`idRest`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `message` (
  `id_message` INT NOT NULL AUTO_INCREMENT,
  `text` TEXT(500) NOT NULL,
  `date` DATETIME NOT NULL,
  `num_sender` VARCHAR(10) NOT NULL,
  `num_receiver` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`id_message`),
  INDEX `fk_message_funcionario1_idx` (`num_sender` ASC) VISIBLE,
  INDEX `fk_message_funcionario2_idx` (`num_receiver` ASC) VISIBLE,
  CONSTRAINT `fk_message_funcionario1`
    FOREIGN KEY (`num_sender`)
    REFERENCES `funcionario` (`number`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_message_funcionario2`
    FOREIGN KEY (`num_receiver`)
    REFERENCES `funcionario` (`number`)
    ON DELETE CASCADE)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ingredient_stock` (
  `name` VARCHAR(100) NOT NULL, 
  `quantity` INT NOT NULL,
  `id_rest` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`name`, `id_rest`),
  INDEX `fk_ingredient_restaurant1_idx` (`id_rest` ASC) VISIBLE,
  CONSTRAINT `fk_ingredient_restaurant1`
    FOREIGN KEY (`id_rest`)
    REFERENCES `restaurant` (`idRest`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ingredientOrder` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `state` VARCHAR(20) NOT NULL,
  `product` VARCHAR(100) NOT NULL, 
  `quantity` INT NOT NULL,
  `order_date` DATETIME NULL,
  `expected_arrival` DATETIME NULL,
  `idRest` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_ingredientOrder_restaurant1_idx` (`idRest` ASC) VISIBLE,
  CONSTRAINT `fk_ingredientOrder_restaurant1`
    FOREIGN KEY (`idRest`)
    REFERENCES `restaurant` (`idRest`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `dailyReport` (
  `id_Rest` VARCHAR(10) NOT NULL,
  `date` DATE NOT NULL,
  `profit` FLOAT NOT NULL,
  `quantity` INT NOT NULL,
  `avg_attendance_time` INT NOT NULL,
  `totalOrders` INT NOT NULL,
  PRIMARY KEY (`id_Rest`, `date`),
  INDEX `fk_dailyReport_restaurant1_idx` (`id_Rest` ASC) VISIBLE,
  CONSTRAINT `fk_dailyReport_restaurant1`
    FOREIGN KEY (`id_Rest`)
    REFERENCES `restaurant` (`idRest`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `stock_report` (
  `report_id_rest` VARCHAR(10) NOT NULL,
  `report_date` DATE NOT NULL,
  `ingredient_name` VARCHAR(100) NOT NULL, 
  `quantity` INT NOT NULL,
  PRIMARY KEY (`report_id_rest`, `report_date`, `ingredient_name`),
  INDEX `fk_stock_report_dailyReport1_idx` (`report_id_rest` ASC, `report_date` ASC) VISIBLE,
  CONSTRAINT `fk_stock_report_dailyReport1`
    FOREIGN KEY (`report_id_rest` , `report_date`)
    REFERENCES `dailyReport` (`id_Rest` , `date`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `top_sales` (
  `dailyReport_id_Rest` VARCHAR(10) NOT NULL,
  `dailyReport_date` DATE NOT NULL,
  `item_name` VARCHAR(100) NOT NULL,
  `quant_sold` INT NOT NULL,
  PRIMARY KEY (`dailyReport_id_Rest`, `dailyReport_date`, `item_name`),
  INDEX `fk_top_sales_dailyReport1_idx` (`dailyReport_id_Rest` ASC, `dailyReport_date` ASC) VISIBLE,
  CONSTRAINT `fk_top_sales_dailyReport1`
    FOREIGN KEY (`dailyReport_id_Rest` , `dailyReport_date`)
    REFERENCES `dailyReport` (`id_Rest` , `date`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `order` (
  `order_id` VARCHAR(15) NOT NULL,
  `restaurant_idRest` VARCHAR(10) NOT NULL,
  `price` FLOAT NOT NULL,
  `state` VARCHAR(15) NOT NULL,
  `notes` TEXT(100) NULL,
  `expc_prep_time` DATETIME NULL,
  `order_time` DATETIME NULL,
  `finish_time` DATETIME NULL,
  `type` VARCHAR(15) NULL,
  PRIMARY KEY (`order_id`, `restaurant_idRest`),
  INDEX `fk_order_restaurant1_idx` (`restaurant_idRest` ASC) VISIBLE,
  CONSTRAINT `fk_order_restaurant1`
    FOREIGN KEY (`restaurant_idRest`)
    REFERENCES `restaurant` (`idRest`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `items` (
  `order_order_id` VARCHAR(15) NOT NULL,
  `order_restaurant_idRest` VARCHAR(10) NOT NULL,
  `name` VARCHAR(100) NOT NULL, 
  `quantity` INT NOT NULL,
  PRIMARY KEY (`order_order_id`, `order_restaurant_idRest`, `name`),
  INDEX `fk_items_order1_idx` (`order_order_id` ASC, `order_restaurant_idRest` ASC) VISIBLE,
  CONSTRAINT `fk_items_order1`
    FOREIGN KEY (`order_order_id` , `order_restaurant_idRest`)
    REFERENCES `order` (`order_id` , `restaurant_idRest`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `task` (
  `idtask` VARCHAR(20) NOT NULL,
  `is_completed` TINYINT NOT NULL,
  `description` TEXT(100) NOT NULL,
  `order_order_id` VARCHAR(15) NOT NULL,
  `order_restaurant_idRest` VARCHAR(10) NOT NULL,
  `role` VARCHAR(15) NOT NULL,
  `expc_time` DATETIME NOT NULL,
  PRIMARY KEY (`idtask`),
  INDEX `fk_task_order1_idx` (`order_order_id` ASC, `order_restaurant_idRest` ASC) VISIBLE,
  CONSTRAINT `fk_task_order1`
    FOREIGN KEY (`order_order_id` , `order_restaurant_idRest`)
    REFERENCES `order` (`order_id` , `restaurant_idRest`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `catalog_product` (
  `name` VARCHAR(100) NOT NULL, 
  `price` FLOAT NOT NULL,
  `exp_prep_minutes` INT NOT NULL,
  `type` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`name`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `recipe` (
  `ing_name` VARCHAR(100) NOT NULL,
  `catalog_product_name` VARCHAR(100) NOT NULL,
  `quantity` INT NOT NULL,
  PRIMARY KEY (`ing_name`, `catalog_product_name`),
  INDEX `fk_recipe_catalog_product1_idx` (`catalog_product_name` ASC) VISIBLE,
  CONSTRAINT `fk_recipe_catalog_product1`
    FOREIGN KEY (`catalog_product_name`)
    REFERENCES `catalog_product` (`name`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `menu_composition` (
  `menu_name` VARCHAR(100) NOT NULL, 
  `item_name` VARCHAR(100) NOT NULL, 
  `quantity` INT NOT NULL,
  PRIMARY KEY (`menu_name`, `item_name`),
  INDEX `fk_menu_composition_catalog_product2_idx` (`item_name` ASC) VISIBLE,
  CONSTRAINT `fk_menu_composition_catalog_product1`
    FOREIGN KEY (`menu_name`)
    REFERENCES `catalog_product` (`name`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_menu_composition_catalog_product2`
    FOREIGN KEY (`item_name`)
    REFERENCES `catalog_product` (`name`)
    ON DELETE CASCADE)
ENGINE = InnoDB;


INSERT INTO restaurant (idRest, Nome, Localização, nextOrderID, nextIngredientOrderID) VALUES 
('SEDE', 'Sede Central', 'Lisboa', 0, 0),
('LOJA_01', 'Restaurante Braga', 'Largo do Paço', 0, 0);

INSERT INTO funcionario (number, name, email, phone_number, password, role, restaurant_idRest) VALUES 
('M1', 'COO Joana', 'admin@dss.pt', '999999999', 'admin', 'COO', 'SEDE'),
('C1', 'Joao Chefe', 'joao@dss.pt', '911111111', '1234', 'Chefe', 'LOJA_01'),
('O1', 'Maria Caixa', 'maria@dss.pt', '922222222', '1234', 'Operador', 'LOJA_01');

INSERT INTO catalog_product (name, price, exp_prep_minutes, type) VALUES 
('Coca-Cola', 1.50, 1, 'Drink'),
('Bitoque', 8.50, 15, 'Meal'),
('Menu Estudante', 9.00, 15, 'Menu');

INSERT INTO recipe (catalog_product_name, ing_name, quantity) VALUES 
('Bitoque', 'Bife', 1),
('Bitoque', 'Arroz', 100);

INSERT INTO menu_composition (menu_name, item_name, quantity) VALUES 
('Menu Estudante', 'Bitoque', 1),
('Menu Estudante', 'Coca-Cola', 1);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

SELECT 'Base de dados criada e povoada com sucesso!' AS Status;