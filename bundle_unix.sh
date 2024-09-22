#!/bin/bash

if [ -f submission.zip ]; then
  old=$(date +%s)
  echo "Back up old submission as submission-${old}\n"
  mv submission.zip submission-${old}.zip
fi

zip -r submission.zip src/farm/core/Farm.java
zip -r submission.zip src/farm/core/FarmManager.java
zip -r submission.zip src/farm/core/CustomerNotFoundException.java
zip -r submission.zip src/farm/core/DuplicateCustomerException.java
zip -r submission.zip src/farm/core/FailedTransactionException.java
zip -r submission.zip src/farm/core/InvalidStockRequestException.java

zip -r submission.zip src/farm/customer/AddressBook.java
zip -r submission.zip src/farm/customer/Customer.java

zip -r submission.zip src/farm/inventory/Inventory.java
zip -r submission.zip src/farm/inventory/BasicInventory.java
zip -r submission.zip src/farm/inventory/FancyInventory.java

zip -r submission.zip src/farm/inventory/product/Product.java
zip -r submission.zip src/farm/inventory/product/Egg.java
zip -r submission.zip src/farm/inventory/product/Jam.java
zip -r submission.zip src/farm/inventory/product/Milk.java
zip -r submission.zip src/farm/inventory/product/Wool.java

zip -r submission.zip src/farm/sales/Cart.java
zip -r submission.zip src/farm/sales/TransactionManager.java
zip -r submission.zip src/farm/sales/TransactionHistory.java

zip -r submission.zip src/farm/sales/transaction/Transaction.java
zip -r submission.zip src/farm/sales/transaction/CategorisedTransaction.java
zip -r submission.zip src/farm/sales/transaction/SpecialSaleTransaction.java

zip -r submission.zip ai/README.txt
zip -r submission.zip ai/*