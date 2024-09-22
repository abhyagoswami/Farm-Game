package farm.customer;

import java.util.ArrayList;
import java.util.List;
import farm.customer.Customer;
import farm.core.DuplicateCustomerException;
import farm.core.CustomerNotFoundException;

/**
 * The address book where the farmer stores their customers' details.
 * Keeps track of all the customers that come and visit the Farm.
 */
public class AddressBook extends Object {

    private List<Customer> addressBook;

    /**
     * Constructs new instance of AddressBook.
     */
    public AddressBook() {
        this.addressBook = new ArrayList<>();
    }

    /**
     * Add a new customer to the address book.
     *
     * @param customer The customer to be added.
     * @throws DuplicateCustomerException If customer already exists in address book.
     *
     * @ensures The address book contains no duplicate customers.
     */
    public void addCustomer(Customer customer) throws DuplicateCustomerException {
        for (Customer existingCustomer : addressBook) {
            if (existingCustomer.equals(customer)) {
                throw new DuplicateCustomerException(existingCustomer.toString());
            }
        }
        addressBook.add(customer); // If no duplicate
    }

    /**
     * Check to see if a customer is already in the address book.
     *
     * @param customer The customer to check.
     * @return True iff the customer already exists, else false.
     */
    public boolean containsCustomer(Customer customer) {
        for (Customer existingCustomer : addressBook) {
            if (existingCustomer.equals(customer)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieve all customer records stored in the address book.
     * Returns:
     *
     * @return A list of all customers in the address book.
     * @ensures The returned list is a shallow copy and cannot modify the original address book.
     */
    public List<Customer> getAllRecords() {
        return new ArrayList<>(addressBook); // return new to prevent modification
    }

    /**
     * Lookup a customer in address book, if they exist using their details.
     *
     * @param name The name of the customer to lookup.
     * @param phoneNumber The phone number of the customer.
     * @return The Customer iff they exist in the address book.
     *
     * @throws CustomerNotFoundException If there is no customer matching the information in
     *          the address book.
     * @requires That name is non-empty and with no trailing whitespace, and phone number is
     *          a positive number.
     */
    public Customer getCustomer(String name, int phoneNumber) throws CustomerNotFoundException {
        for (Customer existingCustomer : addressBook) {
            if (existingCustomer.getName() == name && existingCustomer.getPhoneNumber()
                    == phoneNumber) {
                return existingCustomer;
            }
        }
        throw new CustomerNotFoundException();
    }
}
