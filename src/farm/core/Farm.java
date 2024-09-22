package farm.core;

import farm.customer.AddressBook;
import farm.inventory.Inventory;
import farm.inventory.FancyInventory;
import farm.customer.Customer;
import farm.inventory.product.Product;
import farm.sales.TransactionManager;
import farm.sales.TransactionHistory;
import farm.core.DuplicateCustomerException;
import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;
import farm.core.InvalidStockRequestException;
import farm.core.FailedTransactionException;
import farm.sales.transaction.Transaction;
import farm.core.CustomerNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Top-level model class responsible for storing and making updates to the data and smaller
 * model entities that make up the internal state of a farm.
 */
public class Farm {

    private Inventory inventory;
    private AddressBook addressBook;
    private TransactionManager transactionManager;
    private TransactionHistory transactionHistory;

    /**
     * Creates a new farm instance with an inventory and address book supplied.
     *
     * @param inventory The inventory through which access to the farm's stock is provisioned.
     * @param addressBook  The address book storing the farm's customer records.
     */
    public Farm(Inventory inventory, AddressBook addressBook) {
        this.inventory = inventory;
        this.addressBook = addressBook;
        this.transactionManager = new TransactionManager();
        this.transactionHistory = new TransactionHistory();
    }

    /**
     * Retrieves all customer records currently stored in the farm's address book.
     *
     * @return A list of all customers in the address book
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = addressBook.getAllRecords();
        List<Customer> copy = new ArrayList<>(customers);
        return copy;
    }

    /**
     * Retrieves all products currently stored in the farm's inventory.
     *
     * @return A list of all products in the inventory
     */
    public List<Product> getAllStock() {
        List<Product> stock = inventory.getAllProducts();
        List<Product> copy = new ArrayList<>(stock);
        return copy;
    }

    /**
     * Retrieves the farm's transaction manager.
     *
     * @return The farm's transaction manager
     */
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    /**
     * Retrieves the farm's transaction history.
     *
     * @return The farm's transaction history
     */
    public TransactionHistory getTransactionHistory() {
        return transactionHistory;
    }

    /**
     * Saves the supplied customer in the farm's address book.
     *
     * @param customer The customer to add to the address book.
     * @throws DuplicateCustomerException If the address book already contains this customer.
     */
    public void saveCustomer(Customer customer) throws DuplicateCustomerException {
        addressBook.addCustomer(customer);
    }

    /**
     * Adds a single product of the specified type and quality to the farm's inventory.
     *
     * @param barcode The product type to add to the inventory.
     * @param quality The quality of the product to add to the inventory.
     */
    public void stockProduct(Barcode barcode, Quality quality) {
        try {
            inventory.addProduct(barcode, quality);
        } catch (Exception e) {
            // Do nothing
        }
    }

    /**
     * Adds some quantity of products of the specified type and quality to the farm's inventory.
     *
     * @param barcode The product type to add to the inventory.
     * @param quality The quality of the product to add to the inventory.
     * @param quantity The number of products to add to the inventory.
     *
     * @throws InvalidStockRequestException If the quantity is greater than 1 when a
     *          FancyInventory is not in use.
     */
    public void stockProduct(Barcode barcode, Quality quality, int quantity)
            throws InvalidStockRequestException {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        } else if (!(inventory instanceof FancyInventory) && quantity > 1) {
            throw new InvalidStockRequestException("Current inventory is not fancy enough."
                    + " Please supply products one at a time.");
        } else {
            inventory.addProduct(barcode, quality, quantity);
        }
    }

    /**
     * Sets the provided transaction as the current ongoing transaction.
     *
     * @param transaction The transaction to set as ongoing.
     * @throws FailedTransactionException If the farm's transaction manager rejects the request
     *          to begin managing this transaction.
     *
     * @requires The customer associated with transaction exists in the farm's addressbook.
     */
    public void startTransaction(Transaction transaction) throws FailedTransactionException {
        transactionManager.setOngoingTransaction(transaction);
    }

    /**
     * Attempts to add a single product of the given type to the customer's shopping cart.
     *
     * @param barcode The product type to add.
     *
     * @return The number of products successfully added to the cart. If no products of this
     *          type exist in the inventory, this method will return 0.
     * @throws FailedTransactionException If no transaction is ongoing.
     */
    public int addToCart(Barcode barcode) throws FailedTransactionException {
        if (!transactionManager.hasOngoingTransaction()) {
            throw new FailedTransactionException("Cannot add to cart when no customer"
                    + " has started shopping.");
        }
        if (inventory.existsProduct(barcode)) {
            List<Product> removed = inventory.removeProduct(barcode);
            transactionManager.registerPendingPurchase(removed.get(0));
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Attempts to add the specified number of products of the given type to the customer's shopping cart.
     *
     * @param barcode The product type to add.
     * @param quantity The number of products to add.
     *
     * @return The number of products successfully added to the cart.
     * @throws FailedTransactionException  If no transaction is ongoing, or if the quantity is
     * greater than 1 when a FancyInventory is not in use.
     */
    public int addToCart(Barcode barcode, int quantity)
            throws FailedTransactionException {
        if (!(inventory instanceof FancyInventory) && quantity > 1) {
            throw new FailedTransactionException("Current inventory is not fancy enough."
                    + " Please purchase products one at a time.");
        } else if (!transactionManager.hasOngoingTransaction()) {
            throw new FailedTransactionException("Cannot add to cart when no customer"
                    + " has started shopping.");
        } else if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }

        FancyInventory fancyInventory = (FancyInventory) inventory;
        int amounToAdd = quantity;
        if (fancyInventory.getStockedQuantity(barcode) < quantity) {
            int amountToAdd = fancyInventory.getStockedQuantity(barcode);
        }

        // Then remove it from inventory
        List<Product> removed = fancyInventory.removeProduct(barcode, amounToAdd);

        // We register a product the correct amount of times
        for (int i = 0; i < amounToAdd; i++) {
            transactionManager.registerPendingPurchase(removed.get(i));
        }
        return amounToAdd;
    }

    /**
     * Closes the ongoing transaction. If items have been purchased in this transaction,
     * records the transaction in the farm's history.
     *
     * @return True iff the finalised transaction contained products.
     * @throws FailedTransactionException If transaction cannot be closed.
     */
    public boolean checkout() throws FailedTransactionException {
        Transaction closed = transactionManager.closeCurrentTransaction();
        if (closed.getPurchases().size() > 0) {
            transactionHistory.recordTransaction(closed);
            return true;
        }
        return false;
    }

    /**
     * Retrieves the receipt associated with the most recent transaction.
     *
     * @return The receipt associated with the most recent transaction.
     */
    public String getLastReceipt() {
        return transactionHistory.getLastTransaction().getReceipt();
    }

    /**
     * Retrieves a customer from the address book.
     *
     * @param name The name of the customer.
     * @param phoneNumber The phone number of the customer.
     *
     * @return The customer instance matching the name and phone number.
     * @throws CustomerNotFoundException If the customer does not exist in the address book.
     */
    public Customer getCustomer(String name, int phoneNumber)
            throws CustomerNotFoundException {
        return addressBook.getCustomer(name, phoneNumber);
    }


}

