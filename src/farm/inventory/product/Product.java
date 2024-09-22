package farm.inventory.product;

import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

/**
 * An abstract class representing an instance of a product.
 * Each product is a single instance of a specific item.
 */
public abstract class Product extends Object {
    private final Barcode barcode;
    private final String displayName;
    private final Quality quality;
    private final int basePrice;

    /**
     * Constructs abstract product. Protected method, available to subclasses.
     *
     * @param barcode Barcode of product.
     * @param displayName Display name of product.
     * @param quality Quality of product.
     * @param basePrice Base price of product in cents.
     */
    protected Product(Barcode barcode, String displayName, Quality quality, int basePrice) {
        this.barcode = barcode;
        this.displayName = displayName;
        this.quality = quality;
        this.basePrice = basePrice;
    }

    /**
     * Accessor method for the product's identifier.
     *
     * @return The identifying Barcode for this product.
     */
    public Barcode getBarcode() {
        return barcode;
    }

    /**
     * Retrieve the product's display name, for visual/textual representation.
     *
     * @return The product's display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Retrieve the product's quality.
     *
     * @return The quality level for this product.
     */
    public Quality getQuality() {
        return quality;
    }

    /**
     * Retrieve the product's base sale price.
     *
     * @return The price of the product. In cents.
     */
    public int getBasePrice() {
        return basePrice;
    }

    /**
     * Returns a string representation of this product class. The representation contains the
     * display name of the product, followed by its base price and quality.
     *
     * @return The formatted string representation of the product.
     */
    @Override
    public String toString() {
        String result;
        result = displayName + ": " + basePrice + "c *" + quality.toString() + "*";
        return result;
    }

    /**
     * If two instances of product are equal to each other.
     * Equality is defined by having the same barcode, and quality.
     *
     * @param obj The object with which to compare.
     * @return True iff the other object is a product with the same barcode and quality.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }

        Product product = (Product) obj;

        if (this.barcode != product.barcode) {
            return false;
        } else if (quality != product.quality) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * A hashcode method that respects the equals(Object) method.
     *
     * @return An appropriate hashcode value for this instance.
     */
    @Override
    public int hashCode() {
        return displayName.hashCode() * quality.hashCode() * barcode.hashCode() * 31;
    }
}
