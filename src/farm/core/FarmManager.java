package farm.core;

import farm.core.FailedTransactionException;
import farm.core.Farm;
import farm.core.InvalidStockRequestException;
import farm.core.ShopFront;
import farm.customer.Customer;
import farm.inventory.product.Product;
import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;
import farm.sales.TransactionHistory;
import farm.sales.transaction.CategorisedTransaction;
import farm.sales.transaction.SpecialSaleTransaction;
import farm.sales.transaction.Transaction;

import java.util.*;

/**
 * Controller class, coordinating information between the model and view/UI of the program.
 * <p>
 * <p>
 * Whilst a lot of the Manager has been provided to you there are four methods you
 * need to implement to make it work.
 * <br>
 * Method stubs have been provided to ensure the code compiles and these methods have been
 * made protected.
 * <p>
 * Whilst the majority of FarmManager is implemented in stage2, the concept of quantities isn't
 * introduced till stage3, at which point the FarmManager will need to be extended.
 * <p>
 * Only edit the four methods defined in the JavaDoc that aren't tagged as provided,
 * if you touch something else it may break the control loop.
 * <br> These being: {@link FarmManager#addToInventory(String)},
 * {@link  FarmManager#addToInventory(String, int)},
 * {@link FarmManager#createCustomer()},
 * {@link FarmManager#initiateTransaction(String)}.
 * @stage2
 */
public class FarmManager {
    private final Farm farm;
    private final ShopFront shop;
    private final boolean enableFancy;

    /**
     * Create a new FarmManager instance with a farm and shop provided.
     *
     * @param farm the model for the program.
     * @param shop the UI/view for the program.
     * @provided
     */
    public FarmManager(Farm farm, ShopFront shop) {
        this(farm, shop, false); // disable fancy behaviour until later stage
    }

    /**
     * Create a new FarmManager instance with a farm and shop provided, and the ability to enable
     * the inventory type.
     *
     * @param farm        the model for the program.
     * @param shop        the UI/view for the program.
     * @param enableFancy flag indicating whether to use the FancyInventory inventory type (Stage 2)
     * @provided
     */
    public FarmManager(Farm farm, ShopFront shop, boolean enableFancy) {
        this.farm = farm;
        this.shop = shop;
        this.enableFancy = enableFancy;
    }

    /**
     * Begins the running of the UI and interprets user input to begin the appropriate mode.
     *
     * @provided
     */
    public void run() {
        boolean running = true;
        this.startDisplay();
        while (running) {
            switch (this.getModeSelection()) {
                case "q" -> running = false;
                case "inventory" -> this.launchInventoryMode();
                case "address" -> this.launchAddressBookMode();
                case "sales" -> this.launchSalesMode();
                case "history" -> this.launchHistoryMode();
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // EDIT BELOW THIS LINE - Be careful not to break anything else but maybe have a look around

    /**
     * Private method, converts a valid product name to a bardcode.
     *
     * @param productName The name of the product to add to the farm.
     * @return The barcode corresponding to the product name.
     * @throws IllegalArgumentException If the product name is invalid.
     */
    private Barcode productNameToBarcode(String productName) throws IllegalArgumentException {
        if (productName == Barcode.EGG.getDisplayName()) {
            return Barcode.EGG;
        } else if (productName == Barcode.MILK.getDisplayName()) {
            return Barcode.MILK;
        } else if (productName == Barcode.JAM.getDisplayName()) {
            return Barcode.JAM;
        } else if (productName == Barcode.WOOL.getDisplayName()) {
            return Barcode.WOOL;
        } else {
            throw new IllegalArgumentException("Product name is invalid.");
        }
    }

    /**
     * Adds a single product with corresponding name to the Farm's inventory.
     * All the products added should be of regular quality.
     * If the product is added successfully display the product add success message.
     *
     * @param productName The name of the product to add to the farm.
     */
    protected void addToInventory(String productName) {
        try {
            Barcode barcode = productNameToBarcode(productName);
            farm.stockProduct(barcode, Quality.REGULAR);
            shop.displayProductAddSuccess();
        } catch (Exception e) {
            shop.displayProductAddFailed(e.getMessage());
        }
    }

    /**
     * Adds a certain number of the given product with corresponding name to the Farm's inventory.
     * All the products added should be of regular quality.
     * If the product is added successfully display the product add success message.
     *
     * @param productName the name of the product to add to the farm.
     * @param quantity    the amount of the product to add.
     */
    protected void addToInventory(String productName, int quantity) {
        try {
            Barcode barcode = productNameToBarcode(productName);
            farm.stockProduct(barcode, Quality.REGULAR, quantity);
            shop.displayProductAddSuccess();
        } catch (Exception e) {
            shop.displayProductAddFailed(e.getMessage());
        }
    }

    /**
     * Prompt the user to create a new customer and then save it to the farms
     * address book for later usage.
     */
    protected void createCustomer() {
        String name = shop.promptForCustomerName();
        int phoneNumber;
        try {
            phoneNumber = shop.promptForCustomerNumber();
        } catch (Exception e) {
            shop.displayInvalidPhoneNumber();
            return;
        }
        String address = shop.promptForCustomerAddress();
        Customer customer = new Customer(name, phoneNumber, address);

        try {
            farm.saveCustomer(customer);
        } catch (Exception e) {
            shop.displayDuplicateCustomer();
        }
    }

    /**
     * Start a new transaction for the transaction manager to manage.
     * There are three types of transactions that can be made that correspond to different values
     * being passed in.
     * @param transactionType the type of transaction to make
     */
    protected void initiateTransaction(String transactionType) {
        String name = shop.promptForCustomerName();
        int phoneNumber = 0;
        try {
            phoneNumber = shop.promptForCustomerNumber();
        } catch (Exception e) {
            shop.displayInvalidPhoneNumber();
            return;
        }

        Customer customer = null;
        try {
            customer = farm.getCustomer(name, phoneNumber);
        } catch (Exception e) {
            shop.displayCustomerNotFound();
            return;
        }

        Transaction transaction = null;
        switch (transactionType) {
            case "-s":
            case "-specialsale":
                Map<Barcode, Integer> discounts = getDiscounts();
                transaction = new SpecialSaleTransaction(customer, discounts);
                break;

            case "-c":
            case "-categorised":
                transaction = new CategorisedTransaction(customer);
                break;
            default:
                transaction = new Transaction(customer);
                break;
        }
        try {
            farm.getTransactionManager().setOngoingTransaction(transaction);
            shop.displayTransactionStart();
        } catch (Exception e) {
            shop.displayFailedToCreateTransaction();
        }
    }

    // EDIT ABOVE THIS LINE
    // ---------------------------------------------------------------------------------------------

    /** UI REQUESTS and UPDATES **/
    private void startDisplay() {
        shop.displayMessage("-*- WELCOME TO FARM MVP -*-");
    }

    private String getModeSelection() {
        return shop.promptModeSelect().getFirst().trim().toLowerCase();
    }

    // -- MODE LAUNCHERS and INPUT VALIDATORS -- //

    /**
     * Launches the inventory mode of the CLI.
     */
    private void launchInventoryMode() {
        boolean running = true;
        while (running) {
            List<String> input = shop.promptInventoryCmd();
            switch (input.getFirst()) {
                case "q" -> running = false;
                case "add" -> handleInventoryAddRequest(input);
                case "list" -> {
                    int count = 1;
                    List<Product> stock = farm.getAllStock();
                    if (stock.isEmpty()) {
                        shop.displayMessage("Inventory is empty.");
                    } else {
                        StringBuilder builder = new StringBuilder("{" + stock.getFirst());
                        for (Product product : stock.subList(1, stock.size())) {
                            builder.append(",").append("\t\t");
                            if (count % 4 == 0) {
                                builder.append("\n");
                            }
                            builder.append(product.toString());
                            count++;
                        }
                        shop.displayMessage(builder.append("}").toString());
                    }
                }
            }
        }
    }

    /**
     * Launches the address book mode of the CLI.
     */
    private void launchAddressBookMode() {
        boolean running = true;
        while (running) {
            List<String> input = shop.promptAddressBookCmd();
            switch (input.getFirst()) {
                case "q" -> running = false;
                case "add" -> createCustomer();
                case "list" -> {
                    for (Customer customer : farm.getAllCustomers()) {
                        shop.displayMessage(customer.toString());
                    }
                }
            }
        }
    }

    /**
     * Launches the sales mode of the CLI.
     */
    private void launchSalesMode() {
        boolean running = true;
        while (running) {
            List<String> input = shop.promptSalesCmd();
            switch (input.getFirst()) {
                case "q" -> {
                    if (farm.getTransactionManager().hasOngoingTransaction()) {
                        shop.displayMessage("You have a transaction in progress. Please check out "
                                + "before quitting sales mode or your inventory may be corrupted.");
                    } else {
                        running = false;
                    }
                }
                case "start" -> handleStartTransaction(input);
                case "add" -> handleTransactionAddRequest(input);
                case "checkout" -> handleCheckoutRequest();
            }
        }
    }

    /**
     * Launches the sales history mode of the CLI.
     */
    private void launchHistoryMode() {
        boolean running = true;
        while (running) {
            List<String> input = shop.promptHistoryCmd();
            switch (input.getFirst()) {
                case "q" -> running = false;
                case "stats" -> handleHistoryStats(input);
                case "last" -> {
                    if (farm.getTransactionHistory().getTotalTransactionsMade() == 0) {
                        shop.displayMessage("No transactions made!");
                    } else {
                        shop.displayReceipt(farm.getLastReceipt());
                    }
                }
                case "grossing" -> {
                    if (farm.getTransactionHistory().getTotalTransactionsMade() == 0) {
                        shop.displayMessage("No transactions made!");
                    } else {
                        shop.displayReceipt(farm.getTransactionHistory()
                                .getHighestGrossingTransaction().getReceipt());
                    }
                }
                case "popular" -> shop.displayMessage(
                        farm.getTransactionHistory().getMostPopularProduct().getDisplayName()
                                + " is the most popular!!");
            }
        }
    }


    // -- INVENTORY MODE CONTROLS -- //

    private void handleInventoryAddRequest(List<String> cmdInput) {
        if (cmdInput.size() == 2) {
            String arg = cmdInput.get(1);
            if (arg.equals("-o")) {
                for (Barcode barcode : Barcode.values()) {
                    shop.displayMessage(barcode.toString().toLowerCase());
                }
            } else {
                addToInventory(arg);
            }
        } else if (cmdInput.size() == 3) {
            if (!enableFancy) {
                shop.displayQuantitiesNotSupported();
                return;
            }
            try {
                int quantity = Integer.parseInt(cmdInput.get(2));
                addToInventory(cmdInput.get(1), quantity);
            } catch (NumberFormatException e) {
                shop.displayInvalidQuantity();
            }
        } else {
            shop.displayIncorrectArguments();
        }
    }

    // -- ADDRESS BOOK MODE CONTROLS -- //


    // -- SALES MODE CONTROLS -- //

    private void handleStartTransaction(List<String> input) {
        if (input.size() == 1) {
            initiateTransaction("");
        } else if (input.size() == 2) {
            initiateTransaction(input.get(1));
        } else {
            shop.displayIncorrectArguments();
        }
    }

    private void handleCheckoutRequest() {
        try {
            boolean printReceipt = farm.checkout();
            if (printReceipt) {
                shop.displayReceipt(farm.getLastReceipt());
                return;
            }
            shop.displayMessage("Thanks for stopping by!");

        } catch (FailedTransactionException e) {
            shop.displayMessage("Checkout request failed: " + e.getMessage());
        }
    }

    private void handleTransactionAddRequest(List<String> cmdInput) {
        if (cmdInput.size() == 2 || cmdInput.size() == 3) {

            if (cmdInput.get(1).equals("-o")) {
                for (Barcode barcode : Barcode.values()) {
                    shop.displayMessage(barcode.toString().toLowerCase());
                }
                return;
            }

            // Check to see if attempted to use quantities.
            int quantity = 1;
            if (cmdInput.size() == 3) {
                if (!enableFancy) {
                    shop.displayQuantitiesNotSupported();
                    return;
                }
                try {
                    quantity = Integer.parseInt(cmdInput.get(2));
                } catch (NumberFormatException e) {
                    shop.displayInvalidQuantity();
                }
            }

            // Attempt to add to transaction
            try {

                int actualQuantity;
                if (cmdInput.size() == 3) {
                    if (!enableFancy) {
                        shop.displayQuantitiesNotSupported();
                        return;
                    }
                    actualQuantity =  farm.addToCart(convertProductName(cmdInput.get(1)), quantity);
                } else {
                    actualQuantity = farm.addToCart(convertProductName(cmdInput.get(1)));
                }

                if (enableFancy && actualQuantity < quantity) {
                    shop.displayMessage("We only had " + actualQuantity + " " + cmdInput.get(1)
                            + " to give you :(");
                } else if (actualQuantity > 0) {
                    shop.displayMessage("Item/s added to cart");
                } else {
                    shop.displayMessage("Sorry, that's out of stock!");
                }
            } catch (InvalidStockRequestException e) {
                shop.displayInvalidProductName();
            } catch (FailedTransactionException | IllegalArgumentException e) {
                shop.displayMessage("Product could not be added to transaction: " + e.getMessage());
            }

        } else {
            shop.displayIncorrectArguments();
        }
    }

    private Map<Barcode, Integer> getDiscounts() {
        shop.displayMessage("Entering Discount Setting!");
        Map<Barcode, Integer> discounts = new HashMap<>();
        String productName;
        while (true) {
            try {
                productName = shop.promptForProductName().toLowerCase();
                if (productName.equals("q")) {
                    break;
                }
                Barcode barcode = convertProductName(productName);

                int discount = shop.promptForDiscount("Discount (%): ");
                if (discount < 0) {
                    break;
                }
                discounts.put(barcode, discount); // overwrites old discount if they enter it twice
            } catch (InvalidStockRequestException ignored) {
                shop.displayMessage("Please enter a valid product name.");
            }
        }
        shop.displayMessage("Discounts entered as follows: ");
        shop.displayMessage(discounts.toString());
        return discounts;
    }

    // -- HISTORY MODE CONTROLS -- //

    private void handleHistoryStats(List<String> input) {
        TransactionHistory history = farm.getTransactionHistory();

        if (input.size() == 2) {
            try {
                Barcode barcode = convertProductName(input.get(1));
                shop.displayMessage(String.format("""
                    |--------------------------
                    |     Stats for all
                    | Total Transactions:  %s
                    | Average Sale Price:  $%.2f
                    |--------------------------
                    |     Stats for %s
                    | Total Products Sold: %s
                    | Gross Earning        $%.2f
                    | Average Discount:    %.0f`
                    |--------------------------
                    """, history.getTotalTransactionsMade(),
                        history.getAverageSpendPerVisit() / 100.0f, barcode.getDisplayName(),
                        history.getTotalProductsSold(barcode),
                        history.getGrossEarnings(barcode) / 100.0f,
                        history.getAverageProductDiscount(barcode)
                        ).replace("`", "%")
                );
            } catch (InvalidStockRequestException e) {
                shop.displayInvalidProductName();
            }
            return;
        }

        shop.displayMessage(String.format("""
            |--------------------------
            |     Stats for all
            | Total Transactions:  %s
            | Average Sale Price:  $%.2f
            | Total Products Sold: %s
            | Gross Earning        $%.2f
            |--------------------------
            """, history.getTotalTransactionsMade(), history.getAverageSpendPerVisit() / 100.0f,
            history.getTotalProductsSold(), history.getGrossEarnings() / 100.0f));
    }

    /** Private Helper Methods **/
    private Barcode convertProductName(String productName) throws InvalidStockRequestException {
        return switch (productName) {
            case "egg" -> Barcode.EGG;
            case "milk" -> Barcode.MILK;
            case "jam" -> Barcode.JAM;
            case "wool" -> Barcode.WOOL;
            default -> throw new InvalidStockRequestException("Invalid product name provided: "
                    + productName);
        };
    }
}