# Farm-Game
 A farm game model and controller implemented using Java.

# Description
- This farm game is a model, view, controller implementation, that allows unique customers and their interaction with the farm, including purchases and discounts. The farm has a specified inventory stock, from which purchased items are taken, and can be added to as products go in demand.
- The code skeleton and the user interface is credited to The University of Queensland - CSSE2002 2024 S2 staff.
- All implementations are credited to author - Abhya Goswami.


# Features
Under the src/farm folder:
- The src/farm/core folder stores the interface, model and controller classes, as well as some exception classes that get thrown during the game.
- The src/farm/customer folder stores the Customer and AddressBook classes, which handle basics related to customers and their interaction in the game.
- The src/farm/inventory folder stores the inventories (Inventory, BasicInventory and FancyInventory). It also stores the folder product (src/farm/inventory/product), which stores product classes for the products supported at the farm: Product, Egg, Jam, Milk, Wool. In the product folder there is also src/farm/inventory/product/data which stores characteristics of the products (Barcode, Quality).
- The src/farm/sales folder holds everything that is needed to handle a sale at the farm. This includes Cart, ReceiptPrinter, TransactionHistory, TransactionManager, and a folder transaction which holds Transaction, CategorisedTransaction and SpecialSaleTransaction.
- src/farm holds a Main class.

# Instructions
1. Download folder.
2. In a Java application (IntelliJ was used by default) run the Main.java found in src/farm.
3. Enter "h" into the terminal when the farm game appears to view options and commands that interact with the game.
