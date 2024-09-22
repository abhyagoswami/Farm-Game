package farm.sales;

import farm.inventory.product.Product;
import farm.sales.transaction.CategorisedTransaction;
import farm.sales.transaction.Transaction;
import farm.inventory.product.data.Barcode;
import farm.sales.transaction.SpecialSaleTransaction;
import farm.inventory.product.Egg;
import farm.inventory.product.Milk;
import farm.inventory.product.Jam;
import farm.inventory.product.Wool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A record of all past transactions.
 * Handles retrieval of statistics about past transactions, like earnings and popular products.
 */
public class TransactionHistory {

    private List<Transaction> transactions;

    /**
     * Generates a new TransactionHistory instance.
     */
    public TransactionHistory() {
        this.transactions = new ArrayList<>();
    }

    /**
     * Adds the given transaction to the record of all past transactions.
     *
     * @param transaction The transaction to add to the record.
     * @requires The transaction to be recorded has been finalised
     */
    public void recordTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Retrieves the most recent transaction.
     *
     * @return The most recent transaction added to the record.
     */
    public Transaction getLastTransaction() {
        if (transactions.size() > 0) {
            int indexLastTransaction = transactions.size() - 1;
            return transactions.get(indexLastTransaction); // Get last value in list
        } else {
            return null;
        }
    }

    /**
     * Calculates the gross earnings, i.e. total income, from all transactions.
     *
     * @return The gross earnings from all transactions in history, in cents.
     */
    public int getGrossEarnings() {
        int grossEarnings = 0;
        for (Transaction transaction : transactions) {
            grossEarnings += transaction.getTotal();
        }
        return grossEarnings;
    }

    /**
     * Calculates the gross earnings, i.e. total income, from all sales of a particular product type.
     *
     * @param type The Barcode of the item of interest.
     * @return The gross earnings from all sales of the product type, in cents.
     */
    public int getGrossEarnings(Barcode type) {
        int grossEarningsForType = 0;

        for (Transaction transaction : transactions) {

            // In case we have discount, we will down-cast to SpecialSaleTransaction to get price
            if (transaction instanceof SpecialSaleTransaction) {
                grossEarningsForType +=
                        ((SpecialSaleTransaction) transaction).getPurchaseSubtotal(type);
            } else {
                // If no discount we simply add up the correct products
                List<Product> purchases = transaction.getPurchases();
                for (Product product : purchases) {
                    if (product.getBarcode() == type) {
                        grossEarningsForType += product.getBasePrice();
                    }
                }
            }
        }
        return grossEarningsForType;
    }

    /**
     * Calculates the number of transactions made.
     *
     * @return The number of transactions in total.
     */
    public int getTotalTransactionsMade() {
        return transactions.size();
    }

    /**
     * Calculates the number of products sold over all transactions.
     *
     * @return The total number of products sold.
     */
    public int getTotalProductsSold() {
        int productsSold = 0;
        for (Transaction transaction : transactions) {
            productsSold += transaction.getPurchases().size();
        }
        return productsSold;
    }

    /**
     * Calculates the number of sold of a particular product type, over all transactions.
     *
     * @param type The Barcode for the product of interest
     * @return The total number of products sold, for that particular product.
     */
    public int getTotalProductsSold(Barcode type) {
        int productsSoldForType = 0;
        for (Transaction transaction : transactions) {
            List<Product> products = transaction.getPurchases();
            for (Product product : products) {
                if (product.getBarcode() == type) {
                    productsSoldForType++;
                }
            }
        }
        return productsSoldForType;
    }

    /**
     * Retrieves the transaction with the highest gross earnings, i.e. reported total.
     * If there are multiple return the one that first was recorded.
     *
     * @return The transaction with the highest gross earnings.
     */
    public Transaction getHighestGrossingTransaction() {
        int maxEarning = 0;
        Transaction maxEarningTransaction = null;
        for (Transaction transaction : transactions) {
            if (transaction.getTotal() > maxEarning) {
                maxEarning = transaction.getTotal();
                maxEarningTransaction = transaction;
            }
        }
        return maxEarningTransaction;
    }

    /**
     * Calculates which type of product has had the highest quantity sold overall.
     * If two products are sold at same quantity, return the one that appears first
     * in Barcode enum.
     *
     * @return The identifier for the product type of most popular product.
     */
    public Barcode getMostPopularProduct() {

        // We will use a map to keep track of how much each product was sold.
        Map<Barcode, Integer> salesByProduct = new HashMap<>();
        for (Barcode barcode : Barcode.values()) {
            salesByProduct.put(barcode, getTotalProductsSold(barcode));
        }

        // Next we iterate over our map to find the most sold product.
        int maxSales = 0;
        Barcode maxSoldProduct = null;
        for (Map.Entry<Barcode, Integer> entry : salesByProduct.entrySet()) {
            if (entry.getValue() > maxSales) {
                maxSales = entry.getValue();
                maxSoldProduct = entry.getKey();
            }
        }
        return maxSoldProduct;
    }

    /**
     * Calculates the average amount spent by customers across all transactions.
     *
     * @return The average amount spent overall, in cents (with decimals).
     */
    public double getAverageSpendPerVisit() {
        if (transactions.isEmpty()) {
            return 0.000;
        } else {
            return (double) getGrossEarnings() / getTotalTransactionsMade();
        }
    }

    /**
     * Calculates the average amount a product has been discounted by, across all sales of that product.
     *
     * @param type Identifier of the product of interest.
     * @return The average discount for the product, in cents (with decimals).
     */
    public double getAverageProductDiscount(Barcode type) {
        int totalDiscount = 0;
        int totalQuantity = 0;

        // We will down-cast Transaction to SpecialSaleTransaction.
        // If no discounts are present on this product, we will take it as 0.
        for (Transaction transaction : transactions) {
            if (transaction instanceof SpecialSaleTransaction) {
                SpecialSaleTransaction specialTransaction = (SpecialSaleTransaction) transaction;
                int discount = specialTransaction.getDiscountAmount(type);
                int quantitySold = specialTransaction.getPurchaseQuantity(type);
                totalDiscount += discount * quantitySold;
                totalQuantity += quantitySold;
            } else {
                List<Product> purchases = transaction.getPurchases();
                for (Product product : purchases) {
                    if (product.getBarcode() == type) {
                        totalQuantity++;
                    }
                }
            }
        }

        if (totalQuantity == 0) {
            return 0.000;
        } else {
            return (double) totalDiscount / totalQuantity;
        }
    }
}