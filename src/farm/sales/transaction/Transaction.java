package farm.sales.transaction;

import farm.customer.Customer;
import farm.sales.Cart;
import farm.inventory.product.Product;
import farm.sales.ReceiptPrinter;

import java.util.ArrayList;
import java.util.List;


/**
 * Transactions keeps track of what items are to be (or have been) purchased and by whom.
 */
public class Transaction extends Object {

    private Customer customer;
    private boolean isFinalised; // Tracks whether active or not
    private Cart finalisedCart; // A finalised cart

    /**
     * Construct a new transaction for an associated customer.
     * Transactions are always active at the time of creation.
     *
     * @param customer The customer who is starting the transaction (beginning to shop).
     */
    public Transaction(Customer customer) {
        this.isFinalised = false; // Initially active
        this.customer = customer;
        this.finalisedCart = new Cart();
    }

    /**
     * Retrieves the customer associated with this transaction.
     *
     * @return The customer of the transaction.
     */
    public Customer getAssociatedCustomer() {
        return customer;
    }

    /**
     * Retrieves all products associated with the transaction.
     * If the transaction has been finalised, return all products that were 'locked in' as
     * final purchases. If the transaction is still active, return all products currently
     * in the associated customer's cart.
     *
     * @return The list of purchases comprising the transaction.
     * @ensures The returned list is a shallow copy and cannot modify the original transaction.
     */
    public List<Product> getPurchases() {
        List<Product> copy;
        if (!isFinalised) {
            copy = new ArrayList<>(customer.getCart().getContents());
        } else {
            copy = new ArrayList<>(finalisedCart.getContents());
        }
        return copy;
    }

    /**
     * Calculates the total price of all the current products in the transaction.
     *
     * @return The total price calculated.
     */
    public int getTotal() {
        int total = 0; // In cents
        if (!isFinalised) {
            for (Product product : customer.getCart().getContents()) {
                total += product.getBasePrice();
            }
        } else {
            for (Product product : finalisedCart.getContents()) {
                total += product.getBasePrice();
            }
        }
        return total;
    }

    /**
     * Determines if the transaction is finalised (i.e. sale completed) or not.
     *
     * @return True iff the transaction is over, else false.
     */
    public boolean isFinalised() {
        return isFinalised;
    }

    /**
     * Mark a transaction as finalised and update the transaction's internal state accordingly.
     * This locks in all pending purchases previously added, and no additional modification can
     * be made, and empties the customer's cart.
     */
    public void finalise() {
        for (Product product : customer.getCart().getContents()) {
            finalisedCart.addProduct(product);
        }
        customer.getCart().setEmpty(); // Empty customer's cart
        isFinalised = true; // Mark as finalised
    }

    /**
     * Returns a string representation of this transaction and its current state.
     *
     * @return The formatted string representation of the product.
     */
    @Override
    public String toString() {
        String status = "Active";
        List<Product> cartContents = customer.getCart().getContents();
        if (isFinalised) {
            status = "Finalised";
            cartContents = finalisedCart.getContents();
        }
        String result = "Transaction {Customer: " + customer.getName() + " | Phone Number: "
                + customer.getPhoneNumber() + " | Address: " + customer.getAddress()
                + ", Status: " + status + ", Associated Products: " + cartContents + "}";
        return result;
    }

    /**
     * Converts the transaction into a formatted receipt for display, using the ReceiptPrinter.
     *
     * @return The styled receipt representation of this transaction.
     */
    public String getReceipt() {
        ReceiptPrinter receipt;
        receipt = new ReceiptPrinter();
        if (!isFinalised) {
            return receipt.createActiveReceipt();
        } else {
            List<List<String>> entries = new ArrayList<>();
            for (Product product : finalisedCart.getContents()) {
                String productName = product.getDisplayName();
                String productPrice = String.format("$%.2f", product.getBasePrice() / 100.0);
                entries.add(List.of(productName, productPrice));
            }
            String total = String.format("$%.2f", getTotal() / 100.0);
            List<String> headings = new ArrayList<>();
            headings.add("Item");
            headings.add("Price");
            return receipt.createReceipt(headings, entries, total, customer.getName());
        }
    }

    /**
     * A protected method that returns the finalised cart.
     *
     * @return The finalised cart. If transaction has not been finalised, empty cart is returned.
     */
    protected Cart getFinalisedCart() {
        return finalisedCart;
    }
}
