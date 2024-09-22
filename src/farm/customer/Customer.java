package farm.customer;

import farm.sales.Cart;

/**
 * Keeps a record of a customer who interacts with the farmer's business.
 */

public class Customer {
    private String name;
    private int phoneNumber;
    private String address;
    private Cart cart;

    /**
     * Create a new customer instance with their details.
     *
     * @param name - The name of the customer.
     * @param phoneNumber - The customer's phone number.
     * @param address - The address of the customer.
     *
     * @requires
     * That the name and address is non-empty.
     * That the phone number is a positive number.
     * That the name and address are stripped of trailing whitespaces.
     */
    public Customer(String name, int phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.cart = new Cart();
    }

    /**
     * Retrieve the name of the customer.
     *
     * @return The customers name.
     */
    public String getName() {
        return name;
    }

    /**
     * Update the current name of the customer with a new one.
     *
     * @param newName The new name to override the current name.
     *
     * @requires
     * That the name is non-empty and that it is stripped of trailing whitespaces.
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Retrieve the phone number of the customer.
     *
     * @return - The customer's phone number.
     */
    public int getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Set the current phone number of the customer to be newPhone.
     *
     * @param newPhone The phone number to override the current phone number.
     *
     * @requires
     * The phone number is a positive number.
     */
    public void setPhoneNumber(int newPhone) {
        this.phoneNumber = newPhone;
    }

    /** Retrieve the address of the customer.
     *
     * @return The customer address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the current address of the customer to a new one.
     *
     * @param newAddress The address to override the current address.
     *
     * @requires
     * That the address is non-empty and that it is stripped of trailing whitespaces.
     */
    public void setAddress(String newAddress) {
        this.address = newAddress;
    }

    /**
     * Retrieves the customers cart.
     *
     * @return Their shopping cart.
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * Returns a string representation of this customer class.
     *
     * @return The formatted string representation of the customer.
     */
    @Override
    public String toString() {
        String result = "Name: " + name + " | Phone Number: " + phoneNumber + " | Address: "
                + address;
        return result;
    }

    /**
     * A hashcode method that respects the equals(Object) method.
     *
     * @return An appropriate hashcode value for this instance.
     */
    @Override
    public int hashCode() {
        return name.hashCode() * phoneNumber * 31;
    }

    /**
     * Determines whether the provided object is equal to this customer instance.
     * Equality is defined by having the same phone number and name (case-sensitive).
     *
     * @param obj The object with which to compare
     *
     * @return True if the other object is a customer with the same phone number and name as
     *          the current customer.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }

        Customer otherCustomer = (Customer) obj;

        if (!name.equals(otherCustomer.getName())) {
            return false;
        } else if (phoneNumber != otherCustomer.getPhoneNumber()) {
            return false;
        } else {
            return true;
        }

    }


}
