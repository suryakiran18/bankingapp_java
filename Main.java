import java.util.*;
import java.text.*;
import java.util.TimeZone;

interface Account {
    void deposit(double amount, Date date);
    void withdraw(double amount, Date date);
    void addMonthlyInterest();
}

class SavingsAccount implements Account {
    private static final double INTEREST_RATE = 0.05;
    private static final double OVERDRAFT_LIMIT = 200;
    private static int accountCounter = 1;

    private final int accountNumber;
    private final String accountHolderName;
    private final String accountType = "Savings";
    private double balance;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private Date lastInterestDate = new Date(0);

    public SavingsAccount(String accountHolderName, double initialDeposit, Date date) {
        this.accountNumber = accountCounter++;
        this.accountHolderName = accountHolderName;
        this.balance = initialDeposit;
        addTransaction("Initial deposit", initialDeposit, date);
    }

    public void deposit(double amount, Date date) {
        balance += amount;
        addTransaction("Deposit", amount, date);
    }

    public void withdraw(double amount, Date date) {
        if (amount > (balance - OVERDRAFT_LIMIT)) {
            System.out.println("Insufficient balance.");
        } else {
            balance -= amount;
            addTransaction("Withdrawal", amount, date);
        }
    }

    public void addMonthlyInterest() {
        Date currentDate = new Date();
        Calendar lastInterestCalendar = Calendar.getInstance();
        lastInterestCalendar.setTime(lastInterestDate);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentDate);

        if (currentCalendar.get(Calendar.MONTH) == lastInterestCalendar.get(Calendar.MONTH) &&
                currentCalendar.get(Calendar.YEAR) == lastInterestCalendar.get(Calendar.YEAR)) {
            System.out.println("Interest has already been added for this month.");
        } else {
            double monthlyInterest = (INTEREST_RATE / 12) * balance;
            balance += monthlyInterest;
            addTransaction("Interest", monthlyInterest, currentDate);
            lastInterestDate = currentDate;
            System.out.println("Monthly interest has been added.");
        }
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getLastTransactions(int n) {
        return transactions.subList(0, Math.min(transactions.size(), n));
    }

    private void addTransaction(String type, double amount, Date date) {
        transactions.add(0, new Transaction(type, amount, date));
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getAccountType() {
        return accountType;
    }


    public static class Transaction {
        private static int transactionCounter = 1;
        private final int transactionId;
        private final String type;
        private final double amount;
        private final Date date;

        public Transaction(String type, double amount, Date date) {
            this.transactionId = transactionCounter++;
            this.type = type;
            this.amount = amount;
            this.date = date;
        }

        @Override
        public String toString() {
            return "ID: " + transactionId + ", Type: " + type + ", Amount: " + formatCurrency(amount) +
                    ", Date: " + formatDate(date);
        }

        private String formatCurrency(double amount) {
            return NumberFormat.getCurrencyInstance().format(amount);
        }

        private String formatDate(Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            return sdf.format(date);
        }
    }
}

class Customer {
    String username, password, name, address, phone;
    SavingsAccount account;

    Customer(String username, String password, String name, String address, String phone, double initialDeposit, Date date) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.account = new SavingsAccount(name, initialDeposit, date);
    }
}

public class Main {
    private Map<String, Customer> customers = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Main().start();
    }

    public void start() {
        while (true) {
            System.out.println("\nBANK OF CHUBB");
            System.out.println("1. Register account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    registerCustomer();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    exit();
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    private void registerCustomer() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter contact number: ");
        String phone = scanner.nextLine();

        System.out.print("Set username: ");
        String username = scanner.nextLine();
        while (customers.containsKey(username)) {
            System.out.println("Username already exists. Choose another: ");
            username = scanner.nextLine();
        }

        System.out.print("Set password (8 chars, 1 digit, 1 lowercase, 1 uppercase, 1 special character): ");
        String password = scanner.nextLine();
        while (!isValidPassword(password)) {
            System.out.println("Invalid password. Try again: ");
            password = scanner.nextLine();
        }

        System.out.print("Enter initial deposit: ");
        double initialDeposit = scanner.nextDouble();
        scanner.nextLine();

        customers.put(username, new Customer(username, password, name, address, phone, initialDeposit, new Date()));
        System.out.println("Account registered successfully!");
    }

    private void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Customer customer = customers.get(username);
        if (customer != null && customer.password.equals(password)) {
            customerMenu(customer);
        } else {
            System.out.println("Invalid username/password.");
        }
    }

    private void customerMenu(Customer customer) {
        while (true) {
            System.out.println("\nAccount Number: " + customer.account.getAccountNumber());
            System.out.println("Account Holder: " + customer.account.getAccountHolderName());
            System.out.println("Account Type: " + customer.account.getAccountType());
            System.out.println("Balance: " + formatCurrency(customer.account.getBalance()));
            System.out.println("\n1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. View last 5 transactions");
            System.out.println("4. Add monthly interest");
            System.out.println("5. Check balance");
            System.out.println("6. Log out");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    depositAmount(customer);
                    break;
                case 2:
                    withdrawAmount(customer);
                    break;
                case 3:
                    showLastTransactions(customer);
                    break;
                case 4:
                    customer.account.addMonthlyInterest();
                    break;
                case 5:
                    checkBalance(customer);
                    break;
                case 6:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    private void depositAmount(Customer customer) {
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        customer.account.deposit(amount, new Date());
        System.out.println("Deposit successful.");
    }

    private void withdrawAmount(Customer customer) {
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        customer.account.withdraw(amount, new Date());
    }

    private void showLastTransactions(Customer customer) {
        for (SavingsAccount.Transaction transaction : customer.account.getLastTransactions(5)) {
            System.out.println(transaction);
        }
    }

    private void checkBalance(Customer customer) {
        System.out.println("Current balance: " + formatCurrency(customer.account.getBalance()));
    }

    private boolean isValidPassword(String password) {
        return password.matches("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*_]).{8,}");
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance().format(amount);
    }

    private void exit() {
        System.out.println("Thank you for using Bank of CHUBB.");
        System.exit(0);
    }
}
