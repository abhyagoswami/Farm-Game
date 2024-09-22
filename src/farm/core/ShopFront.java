package farm.core;

import farm.inventory.product.data.Barcode;
import java.util.*;

/**
 * The user interface for the farm program.
 * <p>
 * <b>Note: </b> There are more methods in the ShopFront then displayed in the JavaDocs.
 * Only the methods you will be required to use are visible though there are JavaDocs
 * in the provided code if you are interested.
 * @provided
 */
public class ShopFront {
    public static final String CAT = """
                  ╱|、
                (˚ˎ 。7
                 |、˜〵
                 じしˍ,)ノ
                """;

    public static final String BARN = """
                      ':.
                         []_____
                        /\\      \\
                    ___/  \\__/\\__\\__
                ---/\\___\\ |''''''|__\\-- ---
                   ||'''| |''||''|''|
                   ``""\"`"`""))""`""`
                """;

    private final Scanner input = new Scanner(System.in);

    /**
     * Centres strings for pretty printing.
     * @param component the string being centred
     * @param lineLength the length of the line to centre the string in.
     * @return the component with indentation
     * @hidden
     */
    public static String centreStringComponent(String component, int lineLength) {
        int max = Arrays.stream(component.split("\n"))
                .map(String::length)
                .max(Integer::compareTo)
                .orElse(component.length());
        int leftMarginSize = (lineLength - max) / 2;
        return component.indent(leftMarginSize);
    }

    // -- Mode Prompts --//

    /**
     * Prompts the user to select which mode they would like to enter.
     * @return a list of commands entered.
     * @hidden
     */
    public List<String> promptModeSelect() {
        Set<String> commands = Set.of("q", "inventory", "address", "sales", "history");
        String helpMsg = """
                    Mode Options:
                     - q: Quit the application.
                     - inventory: Manage the farm's inventory
                     - address: Manage the farm's address book.
                     - sales: Enter the sales mode.
                     - history: View the farm's sales history.
                    """;
        System.out.println(CAT);
        System.out.println(BARN);
        return List.of(modePromptHandler("MENU", commands, helpMsg));
    }

    /**
     * Prompts the user to interact with inventory mode and select options.
     * @return the list of commands entered.
     * @hidden
     */
    public List<String> promptInventoryCmd() {
        Set<String> commands = Set.of("add", "list", "q");
        String helpMsg = """
                        Command Options:
                         - q: Quit the inventory mode.
                         - add <product-name>: Add a product to the inventory, with a given quantity if Fancy.
                         - add -o: List all the product type options available to be stocked.
                         - list: List all the products currently stocked in the inventory.
                        """;
        return List.of(modePromptHandler("INVENTORY", commands, helpMsg));
    }

    /**
     * Prompts the user to interact with address book mode and select options.
     * @return the list of commands entered.
     * @hidden
     */
    public List<String> promptAddressBookCmd() {
        Set<String> commands = Set.of("add", "list", "q");
        String helpMsg = """
                    Command Options:
                     - q: Quit the address book mode.
                     - add: Add a customer to the address book.
                     - list: Display all the customers in the address book.
                    """;
        return List.of(modePromptHandler("ADDRESS BOOK", commands, helpMsg));
    }

    /**
     * Prompts the user to interact with sales mode and select options.
     * @return the list of commands entered.
     * @hidden
     */
    public List<String> promptSalesCmd() {
        Set<String> commands = Set.of("q", "start", "add", "checkout");
        String helpMsg = """
            Command Options:
            - q: Quit the sales mode.
            - start [-specialsale | -categorised]: Begin processing a new transaction.
                                      [Optional transaction type].
            - add <product-name>: Place the specified product in the current customer's cart, with a given quantity if Fancy.
                                   ** Note: There must already be an ongoing transaction. **
            - add -o: List all the product type options available to be sold.
            - checkout: Finalise the sale of the products in the current customer's cart.
            """;
        return List.of(modePromptHandler("SALES", commands, helpMsg));
    }

    /**
     * Prompts the user to interact with sales history mode and select options.
     * @return the list of commands entered.
     * @hidden
     */
    public List<String> promptHistoryCmd() {
        Set<String> commands = Set.of("q", "stats", "last", "grossing", "popular");
        String helpMsg = """
            Command Options:
            - q: Quit the sales history mode.
            - stats [<product-name>]: Get the total stats for the shop. [Optional product stats]
            - last: Prints the receipt of the last transaction made.
            - grossing: Prints the receipt of the highest grossing transaction.
            - popular: Displays the name of the most sold product.
            """;
        return List.of(modePromptHandler("HISTORY", commands, helpMsg));
    }

    /**
     * Handles parsing inputs from the user dependent upon which mode they are working in.
     * @param modeName the mode currently active.
     * @param commands the commands entered by the user.
     * @param helpMsg the help message for the current mode.
     * @return the commands entered by the user, as a list.
     */
    private String[] modePromptHandler(String modeName, Set<String> commands, String helpMsg) {
        String[] args;
        do {
            System.out.print(modeName + ": Please enter command (h to see options): ");
            args = input.nextLine().toLowerCase().trim().split(" ");
            if (args.length > 0 && args[0].equals("h")) {
                System.out.println(helpMsg);
            }
        } while (args.length < 1 || !commands.contains(args[0]));
        return args;
    }

    /**
     * Displays an incorrect arguments error message to the user.
     * @hidden
     */
    public void displayIncorrectArguments() {
        displayMessage("Oops! Incorrect arguments provided.");
    }


    // -- Inventory -- //

    /**
     * Prompts user to enter a product name.
     * @return the product name entered by the user.
     * @hidden
     */
    public String promptForProductName() {
        System.out.print("Please enter item name (h to see options): ");
        String response = input.nextLine().toLowerCase().trim();
        if (response.equals("h")) {
            StringJoiner result = new StringJoiner("\n - ");
            result.add("Options:");
            result.add("q: Exit back to mode selection");
            for (Barcode product : Barcode.values()) {
                result.add(product.name().toLowerCase());
            }
            System.out.println(result);
            return promptForProductName();
        }
        return response;
    }

    /**
     * Displays an error message to the user.
     * @hidden
     */
    public void displayInvalidQuantity() {
        displayMessage("Oops! That's not a valid quantity");
    }

    /**
     * Displays an error message to the user.
     * @hidden
     */
    public void displayQuantitiesNotSupported() {
        displayMessage("Basic Inventory does not support quantities. You need to get Fancy!");
    }


    // -- Address Book -- //

    // -- Sales -- //

    /**
     * Displays a given receipt to the user.
     * @param receipt the receipt to display.
     * @hidden
     */
    public void displayReceipt(String receipt) {
        displayMessage("Here's your receipt!");
        displayMessage(receipt);
    }

    /**
     * Prompts the user for a discount amount.
     * @param prompt the String to display to the user.
     * @return the discount amount entered between 0 and 100 inclusive, or -1 if user quits
     * without entering discount.
     * @ensures return value is -1 or a valid percentage.
     * @hidden
     */
    public int promptForDiscount(String prompt) {
        int discount = -1;
        do {
            System.out.print(prompt);
            String response = input.nextLine().toLowerCase().trim();
            if (response.equals("q") || response.equals("quit")) {
                break;
            }
            try {
                discount = Integer.parseInt(response);
                if (discount < 0) {
                    discount = 0;
                } else if (discount > 100) {
                    discount = 100;
                }
            } catch (NumberFormatException ignored) {
                System.out.println("Please enter a valid integer.");
            }
        } while (discount < 0);
        return discount;
    }

    /**
     * Displays the requested message to the user.
     * @param message message to display.
     * @hidden
     */
    public void displayMessage(String message) {
        System.out.println(message);
    }

    // -- vv -- USE THESE -- vv -- //

    /**
     * Displays an invalid product name error message to the user.
     */
    public void displayInvalidProductName() {
        displayMessage("Oops! That's not a valid name");
    }

    /**
     * Displays a product added success message to the user.
     */
    public void displayProductAddSuccess() {
        displayMessage("Product added successfully");
    }

    /**
     * Displays a product adding failed message to the user with details of the problem.
     */
    public void displayProductAddFailed(String details) {
        displayMessage("Product could not be added:");
        displayMessage(details);
    }

    /**
     * Displays a duplicate customer error message to the user.
     */
    public void displayDuplicateCustomer() {
        displayMessage("That customer already exists");
    }

    /**
     * Displays an invalid phone number error message to the user.
     */
    public void displayInvalidPhoneNumber() {
        displayMessage("Oops! That's not a valid phone number:");
    }

    /**
     * Displays a customer not found error message to the user.
     */
    public void displayCustomerNotFound() {
        displayMessage("No customer was found with those details");
    }

    /**
     * Displays a new transaction started message to the user.
     */
    public void displayTransactionStart() {
        displayMessage("---*--- Beginning new transaction... ---*---");
    }

    /**
     * Displays a transaction failure error message to the user.
     */
    public void displayFailedToCreateTransaction() {
        displayMessage("New transaction could not be initiated!");
    }

    /**
     * Prompts user to enter a customer's name.
     * @return the customer's name entered by the user.
     */
    public String promptForCustomerName() {
        System.out.print("Enter customer name: ");
        return input.nextLine().trim();
    }

    /**
     * Prompts user to enter a customer's phone number.
     * @return the customer's phone entered by the user.
     */
    public int promptForCustomerNumber() throws NumberFormatException {
        System.out.print("Enter customer number: ");
        return Integer.parseInt(input.nextLine().trim());
    }

    /**
     * Prompts user to enter a customer's address.
     * @return the customer's address entered by the user.
     */
    public String promptForCustomerAddress() {
        System.out.print("Enter customer address: ");
        return input.nextLine().trim();
    }


}
