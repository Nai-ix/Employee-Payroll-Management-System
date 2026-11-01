
import java.util.ArrayList;  // ArrayList = resizable list. Used for payroll history because we don't know how many records there will be
import java.util.Scanner;  // Scanner = reads what the user types in the console


public class Payroll {
    // WHY "static"?
    // All variables and methods below are static because main() is static. A static method can only
    // directly use other static members, so we don't need to create an object (new Payroll()) to use them.
    // Static members belong to the class itself, and there is only ONE shared copy of them.
    // This works well here because the whole program only manages one set of payroll data.

    // ===================== CONSTANTS =====================
    // "final" means the value can never be changed once set (constant).
    static final int MAX_EMPLOYEES = 100; // Maximum number of employees that can be registered (size of our arrays)
    static final double OVERTIME_MULTIPLIER = 1.25;   // Overtime is paid at 125% of the regular hourly rate


    // ===================== EMPLOYEE DATA (PARALLEL ARRAYS) =====================
    // These arrays are "parallel": the same index in each array belongs to the SAME employee.
    // Example: employeeIds[0], employeeNames[0], and employeeRates[0] are all Employee #1.
    static int[] employeeIds = new int[MAX_EMPLOYEES];  // Employee IDs
    static String[] employeeNames = new String[MAX_EMPLOYEES];  // Employee names
    static double[] employeeRates = new double[MAX_EMPLOYEES];  // Hourly rates in PHP
    static int employeeCount = 0; // How many employees are registered so far (also the next free index in the arrays)


    // ===================== PAYROLL HISTORY (PARALLEL ARRAYLISTS) =====================
    // Same idea as the arrays above: index i in every list belongs to the SAME payroll record.
    // ArrayList is used here (instead of arrays) because it grows automatically with every new record.
    // Note: ArrayList can't store primitives (int, double), so we use wrapper classes (Integer, Double).
    static ArrayList<Integer> histRecordNumber = new ArrayList<Integer>();  // Record number (1, 2, 3, ...)
    static ArrayList<Integer> histEmployeeId = new ArrayList<Integer>();    // Which employee the record is for
    static ArrayList<String> histEmployeeName = new ArrayList<String>();    // Employee name at the time of payroll
    static ArrayList<String> histPayrollMonth = new ArrayList<String>();    // Month covered, e.g. "September 2026"
    static ArrayList<Double> histRegularHours = new ArrayList<Double>();    // Regular hours worked
    static ArrayList<Double> histOvertimeHours = new ArrayList<Double>();   // Overtime hours worked
    static ArrayList<Double> histBasicPay = new ArrayList<Double>();        // Regular hours x hourly rate
    static ArrayList<Double> histOvertimePay = new ArrayList<Double>();     // Overtime hours x rate x 1.25
    static ArrayList<Double> histGrossPay = new ArrayList<Double>();        // Basic pay + overtime pay (before deductions)
    static ArrayList<Double> histSss = new ArrayList<Double>();             // SSS deduction
    static ArrayList<Double> histPhilHealth = new ArrayList<Double>();      // PhilHealth deduction
    static ArrayList<Double> histPagIbig = new ArrayList<Double>();         // Pag-IBIG deduction
    static ArrayList<Double> histTax = new ArrayList<Double>();             // Withholding tax
    static ArrayList<Double> histTotalDeductions = new ArrayList<Double>(); // SSS + PhilHealth + Pag-IBIG + Tax
    static ArrayList<Double> histNetSalary = new ArrayList<Double>();       // Gross pay - total deductions (take-home pay)

    
    static Scanner sc = new Scanner(System.in); // One shared Scanner for the whole program (System.in = keyboard input)


    // ===================== MAIN METHOD =====================
    // The program starts here. It shows the menu again and again until the user confirms exit.
    public static void main(String[] args) {

        boolean running = true; // Controls the main loop: when it becomes false, the program ends
        boolean exitChoice = true;  // Controls the "Do you want to exit?" confirmation loop
        
        while (running) {
            displayMainMenu(); // Show the menu
            int choice = readMenuChoice(); // Get a valid number from the user

            
            // switch = runs the code of the case that matches the user's choice
            // "break" stops the switch so it doesn't continue into the next case
            switch (choice) {
                case 1:
                    registerEmployee();  // Add a new employee
                    break;
                case 2:
                    calculatePayroll();  // Compute payroll for one employee
                    break;
                case 3:
                    showPayrollHistory();  // Show all saved payroll records
                    break;
                case 4:
                    showRegisteredEmployees();  // Show the list of employees
                    break;
                case 5:
                    // Exit selected: ask for confirmation first so the user doesn't quit by accident
                    while (exitChoice) {
                        int exitOption = readExitChoice(); // 1 = Yes, 2 = No
                        if (exitOption == 1) {
                            System.out.println("\nThank you for using the Payroll System");
                            running = false;    // Stops the main loop
                            exitChoice = false; // Stops this confirmation loop
                        } else if (exitOption == 2) {
                            exitChoice = false; // User said No, leave this loop and go back to the menu
                            // NOTE: exitChoice is never set back to true. So after answering "No" once,
                            // choosing option 5 again will skip this loop and the program won't exit.
                        }
                    }
                     
                    break;
                default:
                    // default = runs when the choice doesn't match any case above (e.g. 0, 9, -1)
                    System.out.println("\nInvalid Option. Please enter a number from 1 to 5."); 
                    break;
            }
        }

        sc.close(); // Close the Scanner to free up resources (done after the program loop ends)
    }

    // ===================== MENU METHODS =====================
    
    // Prints the main menu options
    static void displayMainMenu() {
        System.out.println("\n==================================================");
        System.out.println("     EMPLOYEE PAYROLL MANAGEMENT SYSTEM");
        System.out.println("==================================================");
        System.out.println("1. Register Employee");
        System.out.println("2. Calculate Payroll");
        System.out.println("3. Payroll History");
        System.out.println("4. Registered Employees");
        System.out.println("5. Exit");
        System.out.println("==================================================");
    }

    // Reads the user's menu choice. It keeps asking until the user types a whole number.
    // (It only checks that the input is a number. Whether it's 1 to 5 is checked in the switch in main.)
    static int readMenuChoice() {
        while (true) {
            System.out.print("Enter your choice: ");
            // nextLine() reads the whole line typed by the user.
            // trim() removes extra spaces at the start and end (so " 3 " becomes "3").
            String input = sc.nextLine().trim();

            // try-catch: "try" runs code that might fail, "catch" handles the error so the program doesn't crash
            try {
                return Integer.parseInt(input); // Convert the text to an int. "3" becomes 3
            } catch (NumberFormatException e) { 
                // NumberFormatException is thrown when the text can't be converted to a number (e.g. "abc", "2.5", "")
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // Asks the user to confirm exit. Returns 1 if Yes, 2 if No.
    static int readExitChoice() {
        while (true) {
            System.out.print("Do you want to exit? (Yes/No): ");
            String input = sc.nextLine().trim(); // Read the answer and remove extra spaces

            // The catch below is just a safety net for unexpected errors
            try {
                // equalsIgnoreCase compares text without caring about uppercase/lowercase ("yes", "YES", "Yes" all work)
                if (input.equalsIgnoreCase("YES")) {
                    return 1; // 1 means the user confirmed exit
                } else if (input.equalsIgnoreCase("NO")) {
                    return 2; // 2 means the user wants to stay
                } else {
                    System.out.println("Invalid input. Please enter 'YES' or 'NO'.");
                }
            } catch (Exception e) { // Exception is the general type that catches any unexpected error
                System.out.println("An error occurred. Please try again.");
            }
            
        }
    }
    
    // ===================== REGISTER EMPLOYEE =====================

    // Registers a new employee: asks for the name and hourly rate, then saves them in the arrays
    static void registerEmployee() {
        System.out.println("\n--- Register Employee ---");

        // Stop if the arrays are already full, because we can't store more than MAX_EMPLOYEES
        if (employeeCount >= MAX_EMPLOYEES) {
            System.out.println("Employee list is full. Cannot register more employees.");
            return; // return here exits the method early (nothing below it runs)
        }

        String name = readEmployeeName(); // Get a valid name
        double hourlyRate = readHourlyRate(); // Get a valid hourly rate

        
        int newId = employeeCount + 1; // IDs start at 1. First employee = 1, second = 2, and so on

        // employeeCount is the next free slot in the arrays, so we save the new employee there
        employeeIds[employeeCount] = newId; 
        employeeNames[employeeCount] = name; 
        employeeRates[employeeCount] = hourlyRate; 
        employeeCount++; // Move to the next free slot (and count the new employee)

        System.out.println("Employee registered successfully! Employee ID: " + newId); 
    }

    
    // Asks for the employee's name until a valid one is entered
    static String readEmployeeName() {
        while (true) {
            System.out.print("Enter Employee Name: ");
            String name = sc.nextLine().trim(); // Read the name and remove extra spaces

            // Validation 1: the name must not be empty
            if (name.isEmpty()) {
                System.out.println("Employee name cannot be empty. Please try again.");
                continue; // continue = skip the rest of this loop round and ask again from the top
            }

            // Validation 2: the name must have at least one letter (so "123" or "!!!" is rejected)
            // containsLetter returns false if no letter is found, and ! flips it to true so the error is shown
            if (!containsLetter(name)) { 
                System.out.println("Employee name must contain at least one letter. Please try again.");
                continue;
            }

            return name; // Valid name: exit the method and send the name back to registerEmployee()
        }
    }

    
    // Checks if a text has at least one letter. Returns true if it has, false if it doesn't.
    // It checks the text one character at a time, from the first to the last.
    static boolean containsLetter(String text) {
        for (int i = 0; i < text.length(); i++) {
            // charAt(i) gets the character at position i (the first character is position 0).
            // Character.isLetter() returns true if that character is a letter (A-Z, a-z).
            if (Character.isLetter(text.charAt(i))) { 
                return true; // Found a letter, so we can stop checking right away
            }
        }
        return false; // The loop finished and no letter was found
    }

    
    // Asks for the hourly rate until a valid number greater than zero is entered
    static double readHourlyRate() {
        while (true) {
            System.out.print("Enter Hourly Rate (PHP): ");
            String input = sc.nextLine().trim();

            try {
                double rate = parseValidDouble(input); // Convert the text to a decimal number (may throw NumberFormatException)

                // The rate must be above zero, because an hourly rate of 0 or less makes no sense
                if (rate > 0) {
                    return rate; 
                } else {
                    System.out.println("Hourly rate must be greater than zero. Please try again.");
                }
            } catch (NumberFormatException e) {
                // The input was not a valid number (e.g. "abc")
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // ===================== CALCULATE PAYROLL =====================

    // Computes the payroll of one employee and saves the result to the payroll history
    static void calculatePayroll() {
        System.out.println("\n--- Calculate Payroll ---");

        // Can't compute payroll if there are no employees yet
        if (employeeCount == 0) {
            System.out.println("No employees registered yet. Please register an employee first.");
            return;
        }

        showRegisteredEmployees(); // Show the employees so the user can see which ID to pick

        int targetId = readPositiveInt("Enter Employee ID: "); // The ID of the employee to process
        int index = findEmployeeIndex(targetId); // Convert the ID into its position in the arrays (-1 if not found)

        // -1 means the ID doesn't exist, so we tell the user and stop
        if (index == -1) { 
            System.out.println("No employee found with ID " + targetId + ".");
            return;
        }

        String payrollMonth = readPayrollMonth(); // Which month this payroll is for

        // Prevent double payroll: one employee can only have one record per month
        if (isDuplicatePayroll(targetId, payrollMonth)) {
            System.out.println("A payroll record for Employee ID " + targetId
                    + " for " + payrollMonth + " already exists. Check Payroll History (option 3).");
            return;
        }

        
        System.out.println();
        System.out.println("Enter your Hours Worked for " + employeeNames[index] + " (Employee ID: " + targetId + ") for " + payrollMonth + ":");
        double regularHours = readNonNegativeAmount("Enter Total Regular Hours: ");
        double overtimeHours = readNonNegativeAmount("Enter Total Overtime Hours: ");

        double hourlyRate = employeeRates[index]; // Get this employee's rate from the array

        
        // ----- Pay computation -----
        double basicPay = regularHours * hourlyRate;                             // Regular hours x rate
        double overtimePay = overtimeHours * hourlyRate * OVERTIME_MULTIPLIER;   // Overtime hours x rate x 1.25
        double grossPay = basicPay + overtimePay;                                // Total pay BEFORE deductions

        
        // ----- Deductions -----
        // readDeductions returns the 4 amounts inside an array:
        // index 0 = SSS, 1 = PhilHealth, 2 = Pag-IBIG, 3 = Withholding Tax
        double[] deductions = readDeductions(grossPay); 
        double sss = deductions[0];
        double philHealth = deductions[1];
        double pagIbig = deductions[2];
        double tax = deductions[3];
        double totalDeductions = sss + philHealth + pagIbig + tax;

        double netSalary = grossPay - totalDeductions; // Take-home pay

        
        // ----- Save to history -----
        // The new record number is the current number of records + 1
        int recordNumber = histRecordNumber.size() + 1;

        // Add every value to its own list. They must all be added in the same round
        // so that the same index in every list belongs to the same record.
        histRecordNumber.add(recordNumber);
        histEmployeeId.add(employeeIds[index]);
        histEmployeeName.add(employeeNames[index]);
        histPayrollMonth.add(payrollMonth);
        histRegularHours.add(regularHours);
        histOvertimeHours.add(overtimeHours);
        histBasicPay.add(basicPay);
        histOvertimePay.add(overtimePay);
        histGrossPay.add(grossPay);
        histSss.add(sss);
        histPhilHealth.add(philHealth);
        histPagIbig.add(pagIbig);
        histTax.add(tax);
        histTotalDeductions.add(totalDeductions);
        histNetSalary.add(netSalary);

        // Show the payroll summary (payslip) of the record we just saved
        printPayrollSummary(recordNumber, employeeIds[index], employeeNames[index], payrollMonth,
                regularHours, overtimeHours,
                basicPay, overtimePay, grossPay,
                sss, philHealth, pagIbig, tax,
                totalDeductions, netSalary);
    }


    // Searches for an employee ID in the array (linear search).
    // Returns the index where it was found, or -1 if the ID doesn't exist.
    static int findEmployeeIndex(int employeeId) {
        int index = 0;
        int foundIndex = -1; // -1 = "not found" (a real index can never be negative)

        // Only check up to employeeCount because the rest of the array is still empty
        while (index < employeeCount) {
            if (employeeIds[index] == employeeId) {
                foundIndex = index;
                break; // Found it, no need to keep searching
            }
            index++; // Move to the next employee
        }

        return foundIndex;
    }


    // Asks for the payroll month (free text, only checks that it's not empty)
    static String readPayrollMonth() {
        while (true) {
            System.out.print("Enter Payroll Month (e.g., September 2026): ");
            String month = sc.nextLine().trim();

            if (month.isEmpty()) {
                System.out.println("Payroll month cannot be empty. Please try again.");
                continue;
            }

            return month;
        }
    }


    // Checks if the employee already has a payroll record for that month.
    // Returns true if there is (duplicate), false if none yet.
    static boolean isDuplicatePayroll(int employeeId, String payrollMonth) {
        // Go through every saved record in the history
        for (int i = 0; i < histEmployeeId.size(); i++) {
            
            // Duplicate = same employee ID AND same month (not case-sensitive, so "march 2026" = "March 2026")
            if (histEmployeeId.get(i) == employeeId
                    && histPayrollMonth.get(i).equalsIgnoreCase(payrollMonth)) {
                return true;
            }
        }
        return false; // No match found in any record
    }



    // Asks the user to enter the 4 deductions. Returns them in an array: {SSS, PhilHealth, Pag-IBIG, Tax}.
    // If the total deductions are higher than the gross pay, it asks the user to enter them again
    // (so the net salary can never become negative).
    static double[] readDeductions(double grossPay) {
        while (true) {
            // String.format("%.2f", value) shows the number with 2 decimal places
            System.out.println("\nEnter deduction amounts (Gross Pay: PHP "
                    + String.format("%.2f", grossPay) + "):");

            double sss = readNonNegativeAmount("  SSS Deduction: ");
            double philHealth = readNonNegativeAmount("  PhilHealth Deduction: ");
            double pagIbig = readNonNegativeAmount("  Pag-IBIG Deduction: ");
            double tax = readNonNegativeAmount("  Withholding Tax: ");

            double total = sss + philHealth + pagIbig + tax;

            // Deductions can't be more than what the employee earned
            if (total > grossPay) {
                System.out.println("Total deductions (PHP " + String.format("%.2f", total)
                        + ") cannot exceed Gross Pay (PHP " + String.format("%.2f", grossPay)
                        + "). Please re-enter the deductions.");
                continue; // Go back to the top and ask for all 4 deductions again
            }

            // Valid: package the 4 values into one array and send them back
            return new double[] { sss, philHealth, pagIbig, tax };
        }
    }

    // ===================== INPUT VALIDATION HELPERS =====================

    // Asks for a number that is zero or higher (used for hours and deductions). Keeps asking until valid.
    // "prompt" is the message shown to the user, so this one method can be reused for many inputs.
    static double readNonNegativeAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                double value = parseValidDouble(input);

                // 0 is allowed (e.g. no overtime), but negative numbers are not
                if (value >= 0) {
                    return value;
                } else {
                    System.out.println("Value cannot be negative. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    
    // Converts text to a double, but rejects NaN ("Not a Number") and Infinity.
    // Double.parseDouble() actually accepts the text "NaN" and "Infinity", which would break the
    // computations, so we throw an error ourselves for those.
    static double parseValidDouble(String text) {
        double value = Double.parseDouble(text); // Throws NumberFormatException if the text isn't a number

        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new NumberFormatException("Value is not a valid finite number.");
        }

        return value;
    }

    // Asks for a whole number greater than zero (used for the Employee ID). Keeps asking until valid.
    static int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                int value = Integer.parseInt(input); // Only whole numbers work here (no decimals)
                if (value > 0) {
                    return value;
                } else {
                    System.out.println("Value must be greater than zero. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    // ===================== DISPLAY METHODS =====================

    // Prints one payroll record (payslip). Used right after computing payroll and also in the history.
    // Formatting guide for printf:
    //   %.2f = decimal number with 2 decimal places
    //   %n   = new line
    static void printPayrollSummary(int recordNumber, int employeeId, String employeeName, String payrollMonth,
                                     double regularHours, double overtimeHours,
                                     double basicPay, double overtimePay, double grossPay,
                                     double sss, double philHealth, double pagIbig, double tax,
                                     double totalDeductions, double netSalary) {
        System.out.println("\n--- Payroll Summary (Record #" + recordNumber + ") ---");
        System.out.println("Employee ID     : " + employeeId);
        System.out.println("Employee Name   : " + employeeName);
        System.out.println("Payroll Month   : " + payrollMonth);
        System.out.printf("Regular Hours   : %.2f%n", regularHours);
        System.out.printf("Overtime Hours  : %.2f%n", overtimeHours);
        System.out.println("--------------------------------------------");
        System.out.printf("Basic Pay           : PHP %.2f%n", basicPay);
        System.out.printf("Overtime Pay        : PHP %.2f%n", overtimePay);
        System.out.printf("GROSS PAY           : PHP %.2f%n", grossPay);
        System.out.println("--------------------------------------------");
        System.out.printf("SSS Deduction       : PHP %.2f%n", sss);
        System.out.printf("PhilHealth Deduction: PHP %.2f%n", philHealth);
        System.out.printf("Pag-IBIG Deduction  : PHP %.2f%n", pagIbig);
        System.out.printf("Withholding Tax     : PHP %.2f%n", tax);
        System.out.printf("TOTAL DEDUCTIONS    : PHP %.2f%n", totalDeductions);
        System.out.println("--------------------------------------------");
        System.out.printf("NET SALARY          : PHP %.2f%n", netSalary);
    }

    
    
    // Shows every saved payroll record, then the total net pay of all records
    static void showPayrollHistory() {
        System.out.println("\n--- Payroll History ---");

        // No records yet, so there is nothing to show
        if (histRecordNumber.size() == 0) {
            System.out.println("No payroll records yet.");
            return;
        }

        double totalNetPayDisbursed = 0; // Running total of all net salaries

        // Go through every record using its index i
        for (int i = 0; i < histRecordNumber.size(); i++) {
            
            // Safety check: skip a record if its net salary is missing (null)
            if (histNetSalary.get(i) == null) {
                continue; // Skip this record and move to the next one
            }

            // get(i) takes the value at position i from each list, so together they form one full record
            printPayrollSummary(
                    histRecordNumber.get(i), histEmployeeId.get(i), histEmployeeName.get(i), histPayrollMonth.get(i),
                    histRegularHours.get(i), histOvertimeHours.get(i),
                    histBasicPay.get(i), histOvertimePay.get(i), histGrossPay.get(i),
                    histSss.get(i), histPhilHealth.get(i), histPagIbig.get(i), histTax.get(i),
                    histTotalDeductions.get(i), histNetSalary.get(i)
            );

            totalNetPayDisbursed += histNetSalary.get(i); // Add this record's net salary to the total
        }

        System.out.println("\n==================================================");
        System.out.printf("TOTAL NET PAY DISBURSED (ALL RECORDS): PHP %.2f%n", totalNetPayDisbursed);
        System.out.println("==================================================");
    }

    
    // Shows the list of registered employees (ID, name, hourly rate)
    static void showRegisteredEmployees() {
        System.out.println("\n--- Registered Employees ---");

        if (employeeCount == 0) {
            System.out.println("No employees registered yet.");
            return;
        }

        // Only loop up to employeeCount (the filled part of the arrays)
        // Formatting guide: %-4d = whole number, left-aligned in 4 spaces
        //                   %-20s = text, left-aligned in 20 spaces (so columns line up)
        for (int i = 0; i < employeeCount; i++) {
            System.out.printf("ID: %-4d Name: %-20s Hourly Rate: PHP %.2f%n",
                    employeeIds[i], employeeNames[i], employeeRates[i]);
        }
    }
}
