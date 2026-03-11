import mysql.connector
import json


DB_CONFIG = {
    'user': 'dss_user',
    'password': 'dss2526',
    'host': 'localhost',
    'database': 'dss_cadeia_db',
    'raise_on_warnings': True
}


RAW_JSON = """
{
  "Meals": {
    "CBO": {
      "name": "CBO", "price": 5.80, "expectedPrep": 6,
      "recipe": { "Pao_Bacon": 1, "Frango_Panado": 1, "Queijo_Bacon": 1, "Bacon": 2, "Cebola_Frita": 1, "Alface": 1, "Molho_CBO": 1 }
    },
    "BIG_MAC": {
      "name": "Big Mac", "price": 4.95, "expectedPrep": 5,
      "recipe": { "Pao_Triplo": 1, "Carne_Vaca": 2, "Queijo_Cheddar": 1, "Alface": 1, "Picles": 2, "Cebola": 1, "Molho_BigMac": 1 }
    },
    "MC_CHICKEN": {
      "name": "McChicken", "price": 4.20, "expectedPrep": 4,
      "recipe": { "Pao_Sesamo": 1, "Frango_Panado": 1, "Alface": 1, "Maionese": 1 }
    },
    "ROYAL_CHEESE": {
      "name": "Royal Cheese", "price": 4.80, "expectedPrep": 5,
      "recipe": { "Pao_Sesamo": 1, "Carne_Quarter": 1, "Queijo_Cheddar": 2, "Picles": 2, "Cebola": 1, "Ketchup": 1, "Mostarda": 1 }
    },
    "MCNUGGETS_10": {
      "name": "McNuggets 10un", "price": 4.50, "expectedPrep": 3,
      "recipe": { "Nuggets": 10, "Molho_Barbecue": 1 }
    },
    "DOUBLE_CHEESEBURGER": {
      "name": "Double Cheeseburger", "price": 3.80, "expectedPrep": 4,
      "recipe": { "Pao_Sesamo": 1, "Carne_Vaca": 2, "Queijo_Cheddar": 2, "Cebola": 1, "Picles": 1, "Ketchup": 1, "Mostarda": 1 }
    },
    "MROYAL_BACON": {
      "name": "McRoyal Bacon", "price": 5.50, "expectedPrep": 6,
      "recipe": { "Pao_Sesamo": 1, "Carne_Vaca": 1, "Bacon": 2, "Queijo_Cheddar": 1, "Alface": 1, "Molho_McBacon": 1 }
    },
    "BIG_TASTY_SINGLE": {
      "name": "Big Tasty Single", "price": 6.50, "expectedPrep": 7,
      "recipe": { "Pao_Sespecial": 1, "Carne_Vaca": 1, "Queijo_Cheddar": 2, "Bacon": 1, "Alface": 1, "Tomate": 1, "Cebola": 1, "Molho_Tasty": 1 }
    },
    "MCVEGGIE": {
      "name": "McVeggie", "price": 4.90, "expectedPrep": 5,
      "recipe": { "Pao_Sesamo": 1, "Hamburger_Vegetariano": 1, "Alface": 1, "Tomate": 1, "Maionese_Vegetariana": 1 }
    }
  },
  "Sides": {
    "BATATA_M": {
      "name": "Batata Frita Média", "price": 2.00, "expectedPrep": 3,
      "recipe": { "Batatas": 1, "Sal": 1 }
    },
    "BATATA_G": {
      "name": "Batata Frita Grande", "price": 2.50, "expectedPrep": 3,
      "recipe": { "Batatas": 1.5, "Sal": 1 }
    },
    "SOPA_FEIJAO": {
      "name": "Sopa de Feijão Verde", "price": 1.80, "expectedPrep": 2,
      "recipe": { "Feijao_Verde": 1, "Agua": 0.5 }
    },
    "SALADA_MISTA": {
      "name": "Salada Mista", "price": 2.90, "expectedPrep": 1,
      "recipe": { "Alface": 1, "Tomate": 1, "Cenoura_Ralada": 0.5, "Molho_Salada": 1 }
    }
  },
  "Drinks": {
    "COLA_M": {
      "name": "Coca-Cola Média", "price": 1.50, "expectedPrep": 1,
      "recipe": { "Xarope_Cola": 1, "Agua_Gaseificada": 1, "Gelo": 1 }
    },
    "COLA_Z_ZERO_M": {
      "name": "Coca-Cola Zero Média", "price": 1.50, "expectedPrep": 1,
      "recipe": { "Xarope_Cola_Zero": 1, "Agua_Gaseificada": 1, "Gelo": 1 }
    },
    "FANTA_L_ZERO_M": {
      "name": "Fanta Zero Média", "price": 1.50, "expectedPrep": 1,
      "recipe": { "Xarope_Fanta": 1, "Agua_Gaseificada": 1, "Gelo": 1 }
    },
    "AGUA": {
      "name": "Água 50cl", "price": 1.20, "expectedPrep": 0,
      "recipe": { "Garrafa_Agua": 1 }
    },
    "SUMO_LARANJA": {
      "name": "Sumo Laranja Natural", "price": 2.20, "expectedPrep": 2,
      "recipe": { "Laranjas": 3 }
    }
  },
  "Menus": {
    "MENU_CBO_COLA_M": {
      "name": "Menu CBO + Cola Média", "price": 7.21, "expectedPrep": 6,
      "recipe": { "CBO": 1, "COLA_M": 1 }
    },
    "MENU_BIG_MAC_COLA_M": {
      "name": "Menu Big Mac + Cola Média", "price": 6.13, "expectedPrep": 5,
      "recipe": { "BIG_MAC": 1, "COLA_M": 1 }
    },
    "MENU_MC_CHICKEN_COLA_M": {
      "name": "Menu McChicken + Cola Média", "price": 5.41, "expectedPrep": 4,
      "recipe": { "MC_CHICKEN": 1, "COLA_M": 1 }
    },
    "MENU_ROYAL_CHEESE_COLA_M": {
      "name": "Menu Royal Cheese + Cola Média", "price": 6.27, "expectedPrep": 5,
      "recipe": { "ROYAL_CHEESE": 1, "COLA_M": 1 }
    }
  }
}
"""

def limpar_tabelas(cursor):
    print(">> A limpar dados antigos...")
    tabelas = ["menu_composition", "recipe", "catalog_product", "ingredient_stock", 
               "task", "items", "`order`", "message", "funcionario", "restaurant"]
    
    cursor.execute("SET FOREIGN_KEY_CHECKS = 0")
    for t in tabelas:
        cursor.execute(f"TRUNCATE TABLE {t}")
    cursor.execute("SET FOREIGN_KEY_CHECKS = 1")
    print(">> Tabelas limpas.")

def criar_restaurantes_funcionarios(cursor):
    print(">> A criar Restaurantes e Funcionários...")
    
    sql_rest = "INSERT INTO restaurant (idRest, Nome, Localização, nextOrderID, nextIngredientOrderID) VALUES (%s, %s, %s, %s, %s)"
    cursor.execute(sql_rest, ('SEDE', 'Sede Central', 'Lisboa', 0, 0))
    cursor.execute(sql_rest, ('LOJA_01', 'Restaurante Braga', 'Largo do Paço', 1, 1))

    # Funcionários
    sql_func = "INSERT INTO funcionario (number, name, email, phone_number, password, role, restaurant_idRest) VALUES (%s, %s, %s, %s, %s, %s, %s)"
    funcionarios = [
        ('M1', 'COO Joana', 'admin@dss.pt', '999999999', 'admin', 'COO', 'SEDE'),
        ('C1', 'Joao Chefe', 'joao@dss.pt', '911111111', '1234', 'Chefe', 'LOJA_01'),
        ('O1', 'Maria Caixa', 'maria@dss.pt', '922222222', '1234', 'Operador', 'LOJA_01')
    ]
    cursor.executemany(sql_func, funcionarios)
    print(">> Restaurantes e RH criados.")

def povoar_catalogo_e_stock(cursor, data):
    print(">> A povoar Catálogo e Stock...")
    
    key_to_name_map = {}
    
    todos_ingredientes = set()

    categorias_simples = ["Meals", "Sides", "Drinks"]
    
    for cat in categorias_simples:
        tipo = "Meal" if cat in ["Meals", "Sides"] else "Drink"
        
        if cat in data:
            for key, item in data[cat].items():
                nome_real = item["name"]
                
                key_to_name_map[key] = nome_real
                
                sql_prod = "INSERT INTO catalog_product (name, price, exp_prep_minutes, type) VALUES (%s, %s, %s, %s)"
                cursor.execute(sql_prod, (nome_real, item["price"], item["expectedPrep"], tipo))
                
                if "recipe" in item:
                    for ing, qtd in item["recipe"].items():
                        sql_recipe = "INSERT INTO recipe (ing_name, catalog_product_name, quantity) VALUES (%s, %s, %s)"
                        cursor.execute(sql_recipe, (ing, nome_real, qtd))
                        
                        todos_ingredientes.add(ing)

    if "Menus" in data:
        for key, item in data["Menus"].items():
            nome_menu = item["name"]
            
            sql_prod = "INSERT INTO catalog_product (name, price, exp_prep_minutes, type) VALUES (%s, %s, %s, %s)"
            cursor.execute(sql_prod, (nome_menu, item["price"], item["expectedPrep"], "Menu"))
            
            if "recipe" in item:
                for item_key, qtd in item["recipe"].items():

                    nome_item_filho = key_to_name_map.get(item_key, item_key)
                    
                    sql_comp = "INSERT INTO menu_composition (menu_name, item_name, quantity) VALUES (%s, %s, %s)"
                    try:
                        cursor.execute(sql_comp, (nome_menu, nome_item_filho, qtd))
                    except mysql.connector.Error as err:
                        print(f"Erro ao ligar {nome_item_filho} ao menu {nome_menu}: {err}")

    print(f">> A criar stock inicial para {len(todos_ingredientes)} ingredientes únicos...")
    
    sql_stock = "INSERT INTO ingredient_stock (name, quantity, id_rest) VALUES (%s, %s, %s)"
    for ing in todos_ingredientes:
        cursor.execute(sql_stock, (ing, 500, 'LOJA_01'))

    print(">> Catálogo e Stock finalizados.")

def main():
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        data = json.loads(RAW_JSON)
        
        limpar_tabelas(cursor)
        criar_restaurantes_funcionarios(cursor)
        povoar_catalogo_e_stock(cursor, data)
        
        conn.commit()
        print("\n✅ Base de Dados povoada com sucesso!")
        
    except mysql.connector.Error as err:
        print(f"\n❌ Erro de Base de Dados: {err}")
    finally:
        if 'conn' in locals() and conn.is_connected():
            cursor.close()
            conn.close()

if __name__ == "__main__":
    main()