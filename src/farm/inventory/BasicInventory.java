package farm.inventory;

import farm.inventory.Inventory;
import farm.inventory.product.Egg;
import farm.inventory.product.Jam;
import farm.inventory.product.Milk;
import farm.inventory.product.Wool;
import farm.inventory.product.Product;
import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;
import farm.core.InvalidStockRequestException;
import farm.inventory.product.Product;
import farm.core.FailedTransactionException;



import java.util.ArrayList;
import java.util.List;

/**
 * A very basic inventory that both stores and handles products individually, and only supports
 * operations on a single product at a time.
 */
public class BasicInventory implements Inventory {

    private List<Product> inventory; // Products currently in inventory

    /**
     * Constructs an empty BasicInventory.
     */
    public BasicInventory() {
        this.inventory = new ArrayList<>();
    }

    /**
     * Adds a new product with corresponding barcode to the inventory.
     *
     * @param barcode The barcode of the product to add.
     * @param quality The quality of added product.
     */
    public void addProduct(Barcode barcode, Quality quality) {
        Product product = null;
        switch (barcode) {
            case Barcode.EGG:
                product = new Egg(quality);
                break;
            case Barcode.MILK:
                product = new Milk(quality);
                break;
            case Barcode.JAM:
                product = new Jam(quality);
                break;
            case Barcode.WOOL:
                product = new Wool(quality);
                break;
        }

        inventory.add(product);
    }

    /**
     * Adds the specified quantity of the product with corresponding barcode to the inventory,
     * provided that the implementing inventory supports adding multiple products at once.
     *
     * @param barcode  The barcode of the product to add.
     * @param quality  The quality of added product.
     * @param quantity The amount of the product to add.
     * @throws InvalidStockRequestException Always, since Basic inventories don't support >1
     *                                      operations.
     */
    public void addProduct(Barcode barcode, Quality quality, int quantity)
            throws InvalidStockRequestException {
        throw new InvalidStockRequestException("Current inventory is not fancy enough. "
                + "Please supply products one at a time.");
    }

    /**
     * Determines if a product exists in the inventory with the given barcode.
     *
     * @param barcode The barcode of the product to check.
     * @return True iff a product exists, else false.
     */
    public boolean existsProduct(Barcode barcode) {
        // First we determine which product we are referring to
        for (Product product : inventory) {
            if (product.getBarcode() == barcode) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the first product with corresponding barcode from the inventory.
     *
     * @param barcode The barcode of the product to be removed.
     * @return A list containing the removed product if it exists, else an empty list.
     */
    public List<Product> removeProduct(Barcode barcode) {
        List<Product> removedProduct = new ArrayList<>();
        for (Product product : inventory) {
            if (product.getBarcode() == barcode) {
                removedProduct.add(product);
                inventory.remove(product);
                break; // We break since we only remove first instance
            }
        }
        return removedProduct;
    }

    /**
     * Removes the given number of products with corresponding barcode from the inventory,
     * provided that the implementing inventory supports removing multiple products at once.
     *
     * @param barcode The barcode of the product to be removed.
     * @param quantity The total amount of the product to remove from the inventory.
     *
     * @return A list containing the removed product if it exists, else an empty list.
     * @throws FailedTransactionException Always, since Basic inventories don't support >1
     *          operations.
     */
    public List<Product> removeProduct(Barcode barcode, int quantity)
            throws FailedTransactionException {
        throw new FailedTransactionException("Current inventory is not fancy enough. "
                + "Please purchase products one at a time.");
    }

    /**
     * Retrieves the full stock currently held in the inventory.
     *
     * @return A list containing all products currently stored in the inventory.
     */
    public List<Product> getAllProducts() {
        List<Product> shallowCopy = new ArrayList<>(inventory);
        return inventory;
    }

}
