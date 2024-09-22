package farm.sales;

import farm.core.FailedTransactionException;
import farm.sales.transaction.Transaction;
import farm.inventory.product.Product;

/**
 * The controlling class for all transactions, which opens and closes transactions, as well as
 * ensuring only one transaction is active at any given time.
 */
public class TransactionManager {

    private Transaction transaction;
    private boolean ongoingTransaction; // Tracks whether a transaction is ongoing

    /**
     * Constructs a TransactionManager with no ongoing transactions.
     */
    public TransactionManager() {
        this.ongoingTransaction = false;
        this.transaction = null; // Use null as placeholder when we have no transaction
    }

    /**
     * Determine whether a transaction is currently in progress.
     *
     * @return True iff a transaction is in progress, else false.
     */
    public boolean hasOngoingTransaction() {
        return ongoingTransaction;
    }

    /**
     * Begins managing the specified transaction, provided one is not already ongoing.
     *
     * @param transaction The transaction to set as the manager's ongoing transaction.
     * @throws FailedTransactionException Iff a transaction is already in progress.
     */
    public void setOngoingTransaction(Transaction transaction)
            throws FailedTransactionException {
        if (ongoingTransaction) {
            throw new FailedTransactionException("A transaction is already in progress.");
        } else {
            this.transaction = transaction;
            ongoingTransaction = true;
        }
    }

    /**
     * Adds the given product to the cart of the customer associated with the current transaction
     * if there is currently an ongoing transaction that has not been finalised.
     *
     * @param product The product to add to customer's cart.
     * @throws FailedTransactionException Iff there is no ongoing transaction or the transaction
     *          has already been finalised.
     * @requires The provided product is known to be valid for purchase, i.e. has been
     *          successfully retrieved from the farm's inventory
     */
    public void registerPendingPurchase(Product product)
            throws FailedTransactionException {

        if (ongoingTransaction && !transaction.isFinalised()) {
            transaction.getAssociatedCustomer().getCart().addProduct(product);
        } else {
            throw new FailedTransactionException();
        }
    }

    /**
     * Finalises the currently ongoing transaction and readies the TransactionManager to accept
     * a new ongoing transaction.
     *
     * @return The finalised transaction.
     * @throws FailedTransactionException Iff no transactions are currently ongoing.
     */
    public Transaction closeCurrentTransaction()
            throws FailedTransactionException {
        if (!ongoingTransaction) {
            throw new FailedTransactionException("No ongoing transaction.");
        } else {
            transaction.finalise(); // Cart emptying handled in Transaction class already
            ongoingTransaction = false;
            Transaction finalisedTransaction = transaction;
            transaction = null;
            return finalisedTransaction;
        }
    }
}
