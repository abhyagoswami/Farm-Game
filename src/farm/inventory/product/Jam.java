package farm.inventory.product;

import farm.inventory.product.Product;
import farm.inventory.product.data.Quality;
import farm.inventory.product.data.Barcode;

/**
 * A class representing an instance of jam.
 */
public class Jam extends Product {

    /**
     * Create a jam instance with no additional details.
     * Item quality is not specified, so will default to be REGULAR.
     */
    public Jam() {
        super(Barcode.JAM, Barcode.JAM.getDisplayName(), Quality.REGULAR,
                Barcode.JAM.getBasePrice());
    }

    /**
     * Create a jam instance with a quality value.
     *
     * @param quality The quality level to assign to this jam.
     */
    public Jam(Quality quality) {
        super(Barcode.JAM, Barcode.JAM.getDisplayName(), quality, Barcode.JAM.getBasePrice());
    }
}
