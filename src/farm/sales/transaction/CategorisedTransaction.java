package farm.sales.transaction;

import farm.sales.transaction.Transaction;
import farm.customer.Customer;
import farm.inventory.product.data.Barcode;
import farm.inventory.product.Product;
import farm.sales.ReceiptPrinter;

import java.util.*;

/**
 * A transaction type that allows products to be categorised by their types, where the receipt
 * displays types with associated quantity purchased and subtotal, rather than a single
 * line for each product.
 */
public class CategorisedTransaction extends Transaction {

    /**
     * Construct a new active categorised transaction for an associated customer.
     *
     * @param customer The customer who is starting the transaction (beginning to shop).
     */
    public CategorisedTransaction(Customer customer) {
        super(customer); // Use superclass constructor
    }

    /**
     * Retrieves all unique product types of the purchases associated with the transaction.
     *
     * @return A set of all product types in the transaction.
     */
    public Set<Barcode> getPurchasedTypes() {
        Set<Barcode> purchaseTypes = new HashSet<>();
        for (Product product : getPurchases()) {
            purchaseTypes.add(product.getBarcode());
        }
        return purchaseTypes;
    }

    /**
     * Retrieves all products associated with the transaction, grouped by their type.
     * If the transaction has been finalised, this is all products that were 'locked in' as
     * final purchases, otherwise it is all products currently in the associated customer's cart.
     *
     * @return The products in the transaction, grouped by their type.
     */
    public Map<Barcode, List<Product>> getPurchasesByType() {
        Map<Barcode, List<Product>> purchasesByType = new HashMap<>();

        // getPurchases already handles isFinalised
        for (Product product : getPurchases()) {
            Barcode barcode = product.getBarcode();

            // Check if barcode not already in map
            if (!purchasesByType.containsKey(barcode)) {
                List<Product> products = new ArrayList<>();
                products.add(product);
                purchasesByType.put(barcode, products);
            } else {
                // If barcode already in map, we just add product to that barcode
                purchasesByType.get(barcode).add(product);
            }
        }
        return purchasesByType;
    }

    /**
     * Retrieves the number of products of a particular type associated with the transaction.
     *
     * @param type The product type.
     *
     * @return The number of products of the specified type associated with the transaction.
     */
    public int getPurchaseQuantity(Barcode type) {
        int quantity = 0;
        for (Product product : getPurchases()) {
            if (product.getBarcode() == type) {
                quantity += 1;
            }
        }
        return quantity;
    }

    /**
     * Determines the total price for the provided product type within this transaction.
     *
     * @param type The product type.
     * @return The total price for all instances of that product type within the transaction.
     */
    public int getPurchaseSubtotal(Barcode type) {
        int quantity = getPurchaseQuantity(type);
        int price = type.getBasePrice();
        return quantity * price;
    }

    /**
     * Converts the transaction into a formatted receipt for display, using the ReceiptPrinter.
     *
     * @return The styled receipt representation of this transaction
     */
    @Override
    public String getReceipt() {
        ReceiptPrinter receiptPrinter = new ReceiptPrinter();
        if (!isFinalised()) {
            return receiptPrinter.createActiveReceipt();
        } else {
            // If not finalised
            List<List<String>> entries = new ArrayList<>();

            // We will use Barcode.values() to retrieve products in order

            for (Barcode barcode : Barcode.values()) {
                int quantity = getPurchaseQuantity(barcode);
                if (quantity > 0) {
                    int subtotal = getPurchaseSubtotal(barcode);
                    String productName = barcode.getDisplayName();
                    String priceEach = String.format("$%.2f", barcode.getBasePrice() / 100.00);
                    String strSubtotal = String.format("$%.2f", subtotal / 100.00);
                    List<String> entryData = List.of(productName, String.valueOf(quantity),
                            priceEach, strSubtotal);
                    entries.add(entryData);
                }
            }

            List<String> headings = List.of("Item", "Qty", "Price (ea.)", "Subtotal");
            String total = String.format("$%.2f", getTotal() / 100.00);

            return receiptPrinter.createReceipt(headings, entries, total,
                    getAssociatedCustomer().getName());
        }
    }

}
