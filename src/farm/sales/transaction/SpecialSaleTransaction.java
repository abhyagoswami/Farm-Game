package farm.sales.transaction;

import farm.sales.transaction.CategorisedTransaction;
import farm.sales.transaction.Transaction;
import farm.customer.Customer;
import farm.inventory.product.data.Barcode;
import farm.inventory.product.Product;
import farm.sales.ReceiptPrinter;

import java.util.*;

/**
 * A transaction type that builds on the functionality of a categorised transaction, allowing
 * store-wide discounts to be applied to all products of a nominated type.
 */
public class SpecialSaleTransaction extends CategorisedTransaction {
    private Map<Barcode, Integer> discounts;

    /**
     * Construct a new special sale transaction for an associated customer, with an empty set of
     * discounts (i.e. no products are to be sold at a discount).
     *
     * @param customer The customer who is starting the transaction (beginning to shop).
     */
    public SpecialSaleTransaction(Customer customer) {
        super(customer); // Use superclass constructor
        this.discounts = new HashMap<>();
    }

    /**
     * Construct a new special sale transaction for an associated customer, with a set of
     * discounts to be applied to nominated product types on purchasing.
     *
     * @param customer The customer who is starting the transaction (beginning to shop).
     * @param discounts A mapping from product barcodes to the associated discount.
     *
     * @requires 0 <= discount amount <= 100
     */
    public SpecialSaleTransaction(Customer customer, Map<Barcode, Integer> discounts) {
        super(customer);
        this.discounts = new HashMap<>(discounts);
    }

    /**
     * Determines the total price for the provided product type within this transaction, with
     * any specified discount applied as an integer percentage taken from the usual subtotal.
     *
     * @param type The product type whose subtotal should be calculated.
     * @return The total (discounted) price for all instances of a product in this transaction.
     */
    @Override
    public int getPurchaseSubtotal(Barcode type) {
        int subtotal = super.getPurchaseSubtotal(type);
        int discount = getDiscountAmount(type);
        double discountAmount = (subtotal * (discount / 100.0));
        return (int) (subtotal - discountAmount);
    }

    /**
     * Retrieves the discount percentage that will be applied for a particular product type
     * as an integer.
     *
     * @param type The product type.
     * @return The amount the product is discounted by. If no discount, returns 0.
     */
    public int getDiscountAmount(Barcode type) {
        if (!discounts.containsKey(type)) {
            return 0;
        } else {
            return discounts.get(type);
        }
    }

    /**
     * Calculates the total price (with discounts) of all the products in the transaction.
     *
     * @return The total (discounted) price calculated.
     */
    @Override
    public int getTotal() {
        int total = 0;
        for (Barcode barcode : getPurchasedTypes()) {
            total += getPurchaseSubtotal(barcode);
        }
        return total;
    }

    /**
     * Calculates how much the customer has saved from discounts.
     *
     * @return The numerical savings from discounts.
     */
    public int getTotalSaved() {
        int discountedTotal = this.getTotal();
        int undiscountedTotal = super.getTotal();
        return undiscountedTotal - discountedTotal;
    }

    /**
     * Returns a string representation of this transaction and its current state.
     * The representation contains information about the customer, the transaction's
     * status, the associated products, and the discounts to be applied.
     *
     * @return The formatted string representation of the product.
     */
    @Override
    public String toString() {
        Customer customer = super.getAssociatedCustomer();
        String status = "Active";
        List<Product> cartContents = customer.getCart().getContents();
        if (super.isFinalised()) {
            status = "Finalised";
            cartContents = super.getFinalisedCart().getContents();
        }

        // We make a discounts string
        String strDiscounts = "{";
        for (Barcode barcode : discounts.keySet()) {
            int discountValue = discounts.get(barcode);
            strDiscounts += barcode.getDisplayName().toUpperCase() + "=" + discountValue + ", ";
        }
        // We remove the ", " after the last value using String.substring()
        if (!discounts.isEmpty()) {
            strDiscounts = strDiscounts.substring(0, strDiscounts.length() - 2);
        }
        strDiscounts += "}";

        String result = "Transaction {Customer: " + customer.getName() + " | Phone Number: "
                + customer.getPhoneNumber() + " | Address: " + customer.getAddress()
                + ", Status: " + status + ", Associated Products: " + cartContents
                + ", Discounts: " + strDiscounts + "}";
        return result;
    }

    /**
     * Converts the transaction into a formatted receipt for display, using the ReceiptPrinter.
     *
     * @return The styled receipt representation of this transaction
     */
    public String getReceipt() {
        ReceiptPrinter receiptPrinter = new ReceiptPrinter();
        if (!isFinalised()) {
            return receiptPrinter.createActiveReceipt();

        } else {
            // If not finalised
            List<List<String>> entries = new ArrayList<>();

            // We will use Barcode.values() to retrieve products in the correct order
            for (Barcode barcode : Barcode.values()) {
                int quantity = getPurchaseQuantity(barcode);
                if (quantity > 0) {
                    int subtotal = getPurchaseSubtotal(barcode);
                    String productName = barcode.getDisplayName();
                    String priceEach = String.format("$%.2f", barcode.getBasePrice() / 100.00);
                    String strSubtotal = String.format("$%.2f", subtotal / 100.00);

                    // Populate our entry data
                    List<String> entryData = new ArrayList<>();
                    entryData.add(productName);
                    entryData.add(String.valueOf(quantity));
                    entryData.add(priceEach);
                    entryData.add(strSubtotal);

                    int discount = getDiscountAmount(barcode);
                    if (discount > 0) {
                        String discountMessage = String.format("Discount applied! %d%% off %s",
                                discount, productName);
                        entryData.add(discountMessage);
                    }

                    entries.add(entryData);
                }
            }
            List<String> headings = List.of("Item", "Qty", "Price (ea.)", "Subtotal");
            String total = String.format("$%.2f", getTotal() / 100.00);

            // If discounts are present we add a savings message
            if (getTotalSaved() > 0) {
                String totalSaved = String.format("$%.2f", getTotalSaved() / 100.00);
                return receiptPrinter.createReceipt(headings, entries, total,
                        getAssociatedCustomer().getName(), totalSaved);

            // Otherwise default to normal
            } else {
                return receiptPrinter.createReceipt(headings, entries, total,
                        getAssociatedCustomer().getName());
            }

        }
    }
}
