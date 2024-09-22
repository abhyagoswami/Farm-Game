package farm.sales;

import java.util.ArrayList;
import java.util.List;
import farm.inventory.product.Product;
import farm.inventory.product.Egg;
import farm.inventory.product.Milk;
import farm.inventory.product.Jam;
import farm.inventory.product.Wool;

/**
 * A shopping cart that stores the customer products until they check out.
 */
public class Cart extends Object {
    private List<Product> cart;

    /**
     * Construct an instance of empty cart.
     */
    public Cart() {
        this.cart = new ArrayList<>();
    }

    /**
     * Adds a given product to the shopping cart.
     *
     * @param product The product to add.
     */
    public void addProduct(Product product) {
        cart.add(product);
    }

    /**
     * Retrieves all the products in the Cart in the order they were added.
     *
     * @return A list of all products in the cart
     * @ensures The returned list is a shallow copy and cannot modify the original cart
     */
    public List<Product> getContents() {
        return new ArrayList<>(cart); // return new to prevent modification
    }

    /**
     * Empty out the shopping cart.
     */
    public void setEmpty() {
        cart.clear();
    }

    /**
     * Returns if the cart is empty
     *
     * @return True iff there is nothing in the cart, else false.
     */
    public boolean isEmpty() {
        return cart.isEmpty();
    }
}
