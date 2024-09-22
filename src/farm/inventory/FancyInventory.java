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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fancy inventory which stores products in stacks, enabling quantity information.
 */
public class FancyInventory implements Inventory {

    // An ordered list of products in our inventory
    private List<Product> inventory;
    // List of products being removed by a remove transaction
    private List<Product> currentlyBeingRemoved;

    /**
     * Constructs a new FancyInventory which allows >1 operations at a time.
     */
    public FancyInventory() {
        this.inventory = new ArrayList<>();
        this.currentlyBeingRemoved = new ArrayList<>();
    }

    /**
     * Private method, given a barcode and quality, generate a product.
     *
     * @param barcode The barcode of desired product.
     * @param quality The quality level of desired product.
     * @return Product instance with the barcode and quality desired.
     */
    private Product generateProduct(Barcode barcode, Quality quality) {
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
        return product;
    }

    /**
     * Adds a new product with corresponding barcode to the inventory.
     *
     * @param barcode The barcode of the product to add.
     * @param quality The quality of added product.
     */
    public void addProduct(Barcode barcode, Quality quality) {
        Product product = generateProduct(barcode, quality);
        inventory.add(product);
    }

    /**
     * Adds multiple of the product with corresponding barcode to the inventory.
     *
     * @param barcode The barcode of the product to add.
     * @param quality The quality of added product.
     * @param quantity The amount of the product to add.
     *
     * @throws InvalidStockRequestException Never, because inventory is fancy.
     */
    public void addProduct(Barcode barcode, Quality quality, int quantity)
            throws InvalidStockRequestException {
        int i = quantity;
        Product product = generateProduct(barcode, quality);
        while (i > 0) {
            inventory.add(product);
            i--;
        }
    }

    /**
     * Determines if a product exists in the inventory with the given barcode.
     *
     * @param barcode The barcode of the product to check.
     * @return True iff a product exists, else false.
     */
    public boolean existsProduct(Barcode barcode) {
        for (Product product : inventory) {
            if (product.getBarcode() == barcode) {
                return true;
            }
        }
        return false;
    }

    /**
     * Private method which removes the first instance of a product from the inventory.
     * This ensures that the highest quality items are removed first.
     *
     * @param barcode The barcode of the product to remove.
     */
    private void removeFromInventory(Barcode barcode) {
        List<Quality> qualityPreference = new ArrayList<>();
        qualityPreference.add(Quality.IRIDIUM);
        qualityPreference.add(Quality.GOLD);
        qualityPreference.add(Quality.SILVER);
        qualityPreference.add(Quality.REGULAR);

        for (Quality quality : qualityPreference) {
            for (int i = 0; i < inventory.size(); i++) {
                Product product = inventory.get(i);
                if (product.getBarcode() == barcode && product.getQuality() == quality) {
                    System.out.println("Removing product: " + product);
                    currentlyBeingRemoved.add(inventory.remove(i));
                    return;
                }
            }
        }
    }

    /**
     * Removes the highest quality product with corresponding barcode from the inventory.
     *
     * @param barcode The barcode of the product to be removed.
     * @return A list containing the removed product if it exists, else an empty list.
     */
    public List<Product> removeProduct(Barcode barcode) {
        currentlyBeingRemoved.clear();
        removeFromInventory(barcode);
        return currentlyBeingRemoved;
    }

    /**
     * Removes a given number of products with corresponding barcode from the inventory,
     * choosing the highest quality products possible.
     *
     * @param barcode The barcode of the product to be removed.
     * @param quantity The total amount of the product to remove from the inventory.
     *
     * @return A list containing the removed product if it exists, else an empty list.
     * @throws FailedTransactionException Never, because inventory is fancy.
     */
    public List<Product> removeProduct(Barcode barcode, int quantity)
            throws FailedTransactionException {
        currentlyBeingRemoved.clear();
        int i = quantity;
        if (getStockedQuantity(barcode) < quantity) {
            i = getStockedQuantity(barcode);
        }
        while (i > 0) {
            removeFromInventory(barcode);
            i--;
        }
        return currentlyBeingRemoved;
    }

    /**
     * Retrieves the full stock currently held in the inventory, in correct order.
     *
     * @return An organised list containing all products currently stored in the inventory.
     */
    public List<Product> getAllProducts() {
        List<Product> organizedProducts = new ArrayList<>();
        for (Barcode barcode : Barcode.values()) {
            for (Product product : inventory) {
                if (product.getBarcode() == barcode) {
                    organizedProducts.add(product);
                }
            }
        }
        return organizedProducts;
    }

    /**
     * Get the quantity of a specific product in the inventory.
     *
     * @param barcode The barcode of the product.
     *
     * @return The amount of the corresponding product currently in the inventory.
     */
    public int getStockedQuantity(Barcode barcode) {
        int quantity = 0;
        for (Product product : inventory) {
            if (product.getBarcode() == barcode) {
                quantity++;
            }
        }
        return quantity;
    }
}
