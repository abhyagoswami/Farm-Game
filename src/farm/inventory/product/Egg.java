package farm.inventory.product;

import farm.inventory.product.Product;
import farm.inventory.product.data.Quality;
import farm.inventory.product.data.Barcode;

/**
 * A class representing an instance of an egg.
 */
public class Egg extends Product {

    /**
     * Create an egg instance with no additional details.
     * Item quality is not specified, so will default to be REGULAR.
     */

    public Egg() {
        super(Barcode.EGG, Barcode.EGG.getDisplayName(), Quality.REGULAR,
                Barcode.EGG.getBasePrice());
    }

    /**
     * Create an egg instance with a quality value.
     *
     * @param quality The quality level to assign to this egg.
     */
    public Egg(Quality quality) {
        super(Barcode.EGG, Barcode.EGG.getDisplayName(), quality, Barcode.EGG.getBasePrice());
    }

}
