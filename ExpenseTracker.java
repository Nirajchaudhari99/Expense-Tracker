import java.io.*;
import java.util.*;

class User {
    private String username;
    private String password;
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public boolean validatePassword(String inputPassword) {
        return password.equals(inputPassword);
    }
    
    public static void saveUser(User user) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(user.username + "," + user.password);
            writer.newLine();
        }
    }
    
    @SuppressWarnings("ConvertToTryWithResources")
    public static User login(String username, String password) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("users.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] userData = line.split(",");
            if (userData[0].equals(username) && userData[1].equals(password)) {
                reader.close();
                return new User(username, password);
            }
        }
        reader.close();
        return null;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

class Expense {
    private String date;
    private String category;
    private double amount;
    
    public Expense(String date, String category, double amount) {
        this.date = date;
        this.category = category;
        this.amount = amount;
    }
    
    public String getDate() {
        return date;
    }
    
    public String getCategory() {
        return category;
    }
    
    public double getAmount() {
        return amount;
    }
    
    @Override
    public String toString() {
        return date + " | " + category + " | $" + amount;
    }
    
    @SuppressWarnings("ConvertToTryWithResources")
    public static void saveExpense(Expense expense) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("expenses.txt", true));
        writer.write(expense.date + "," + expense.category + "," + expense.amount);
        writer.newLine();
        writer.close();
    }
    
    public static List<Expense> loadExpenses() throws IOException {
        List<Expense> expenses = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("expenses.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            expenses.add(new Expense(data[0], data[1], Double.parseDouble(data[2])));
        }
        reader.close();
        return expenses;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}

public class ExpenseTracker {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;
    
    public static void main(String[] args) throws IOException {
        while (true) {
            System.out.println("\nExpense Tracker Menu:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    if (currentUser != null) {
                        manageExpenses();
                    }
                    break;
                case 3:
                    System.out.println("Exiting... Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
    
    private static void registerUser() throws IOException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        User newUser = new User(username, password);
        User.saveUser(newUser);
        
        System.out.println("Registration successful!");
    }
    
    private static void loginUser() throws IOException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        currentUser = User.login(username, password);
        if (currentUser != null) {
            System.out.println("Login successful! Welcome, " + currentUser.getUsername());
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }
    
    private static void manageExpenses() throws IOException {
        while (true) {
            System.out.println("\nExpense Management:");
            System.out.println("1. Add Expense");
            System.out.println("2. View Expenses");
            System.out.println("3. View Category-wise Total");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    addExpense();
                    break;
                case 2:
                    viewExpenses();
                    break;
                case 3:
                    viewCategoryWiseTotal();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    currentUser = null;
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
    
    private static void addExpense() throws IOException {
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter category (Food, Transport, Bills, etc.): ");
        String category = scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        
        Expense expense = new Expense(date, category, amount);
        Expense.saveExpense(expense);
        
        System.out.println("Expense added successfully!");
    }
    
    private static void viewExpenses() throws IOException {
        List<Expense> expenses = Expense.loadExpenses();
        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
        } else {
            System.out.println("\nDate | Category | Amount");
            for (Expense e : expenses) {
                System.out.println(e);
            }
        }
    }
    
    private static void viewCategoryWiseTotal() throws IOException {
        List<Expense> expenses = Expense.loadExpenses();
        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }
        
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense e : expenses) {
            categoryTotals.put(e.getCategory(), categoryTotals.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
        }
        
        System.out.println("\nCategory-wise Expense Summary:");
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            System.out.println(entry.getKey() + ": $" + entry.getValue());
        }
    }
}
