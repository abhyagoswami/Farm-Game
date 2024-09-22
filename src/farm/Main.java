package farm;

import farm.core.DuplicateCustomerException;
import farm.core.Farm;
import farm.core.FarmManager;
import farm.core.ShopFront;
import farm.customer.AddressBook;
import farm.customer.Customer;
import farm.inventory.product.Egg;
import farm.inventory.product.Milk;
import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;
import farm.sales.TransactionHistory;
import farm.sales.transaction.SpecialSaleTransaction;

import java.util.*;

/**
 * Execute the Farm MVP program.
 */
public class Main {

    /**
     * Start the farm program.
     * @param args Parameters to the program, currently not supported.
     */
    public static void main(String[] args)
         throws DuplicateCustomerException {
//
//        // -- Stage 0: Completion of AddressBook and Customer at stage
        AddressBook addressBook = new AddressBook();
        Customer customer = new Customer("Ali", 33651111, "UQ");
        addressBook.addCustomer(customer);
        for (String name : List.of("James", "Alex", "Lauren")) {
            addressBook.addCustomer(new Customer(name, 1234, "1st Street"));
        }
        System.out.println(addressBook.getAllRecords());


         Map<Barcode, Integer> discounts = new HashMap<>();
         discounts.put(Barcode.MILK, 50);
         discounts.put(Barcode.EGG, 0);


         SpecialSaleTransaction transaction = new SpecialSaleTransaction(customer, discounts);
         for (int i = 0; i < 3; i++) {
             transaction.getAssociatedCustomer().getCart().addProduct(new Milk());
         }
         transaction.getAssociatedCustomer().getCart().addProduct(new Egg());
         transaction.getAssociatedCustomer().getCart().addProduct(new Milk());
         transaction.finalise();
         System.out.println("\n");
         System.out.println(transaction.getReceipt());


         TransactionHistory transactionHistory = new TransactionHistory();
         transactionHistory.recordTransaction(transaction);
         System.out.println(transactionHistory.getAverageProductDiscount(Barcode.MILK));





         // -- Stage 2 + 3: Combining them together

         farm.inventory.Inventory inventory = new farm.inventory.BasicInventory();
         boolean fancy = false;

         // Keep removed for Stage 2 but add when Stage 3 is done
 //        inventory = new FancyInventory();
 //        fancy = true;

         for (Barcode barcode : List.of(Barcode.MILK, Barcode.EGG, Barcode.WOOL, Barcode.EGG)) {
             for (Quality quality : List.of(Quality.REGULAR, Quality.SILVER, Quality.REGULAR,
                     Quality.GOLD, Quality.REGULAR, Quality.REGULAR, Quality.IRIDIUM)) {
                 try{
                     inventory.addProduct(barcode, quality);
                 } catch (Exception e) {
                     // Do nothing
                 }
             }
         }

         Farm farm = new Farm(inventory, addressBook);
         FarmManager manager = new FarmManager(new Farm(inventory, addressBook),
                 new ShopFront(), fancy);
         manager.run();

    }
}