package banking;


import java.sql.SQLException;
import java.util.Scanner;

import static banking.Constants.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static Account account = new Account();
    private static DBHelper dbHelper;

    public static void main(String[] args) throws SQLException {
        String url = "jdbc:sqlite:" + args[1];
        dbHelper = new DBHelper(url);
        dbHelper.createDatabase();
        showMenu(MENU_BANKING);
    }


    private static void createCard() {
        account.onCreateAccount();
        dbHelper.insertAccount(account);
        System.out.println(account);
    }

    private static void logInfoCard() {
        System.out.println(LABEL_CREATE_CARD);
        String number = scanner.next();
        System.out.println(LABEL_CREATE_PIN);
        String pin = scanner.next();
        if (dbHelper.isAccountExistAndLogged(number, pin, "isLogged")) {
            account = dbHelper.getAccount(number, pin);
            System.out.println(MESSAGE_LOGIN_SUCCESSFUL);
            showMenu(MENU_PERSONAL);
        } else {
            System.out.println(MESSAGE_LOGIN_FAILED);
        }
    }

    private static void showMenu(String menu) {
        int selected = 100;
        while (selected != 0) {
            System.out.println(menu);
            selected = scanner.nextInt();
            scanner.nextLine();
            if (menu.equals(MENU_BANKING)) {
                menuBanking(selected);
            } else {
                menuPersonal(selected);
            }
        }
    }

    private static void menuBanking(int selected) {
        account = new Account();
        switch (selected) {
            case 1:
                createCard();
                break;
            case 2:
                logInfoCard();
                break;
            case 0:
                exit();
                break;
            default:
                menuBanking(selected);
        }
    }

    private static void menuPersonal(int selected) {
        switch (selected) {
            case 1:
                System.out.println("Balance: " + dbHelper.getBalance(account.getCardNo()));
                break;
            case 2: {
                System.out.println("Enter income : ");
                int balance = scanner.nextInt();
                scanner.nextLine();
                dbHelper.depositMoneyToAccount(balance, account.getCardNo());
                account.setBalance(balance);
                System.out.println("Income was added");
            }
            break;
            case 3:
                doTransfer(account, valueIO(LABEL_CREATE_CARD));
                break;
            case 4: {
                dbHelper.closeAccount(account.getCardNo(), String.valueOf(account.getPin()));
                System.out.println("The account has been closed!");
                showMenu(MENU_BANKING);
            }
            break;
            case 5:
                System.out.println(MESSAGE_LOGOUT);
                showMenu(MENU_BANKING);
                break;
            case 0:
                exit();
                break;
            default:
                menuPersonal(selected);
        }
    }

    private static String valueIO(String label) {
        System.out.println(label);
        return scanner.next();
    }

    private static void exit() {
        System.out.println(MESSAGE_EXIT);
        System.exit(0);
    }

    public static void doTransfer(Account account, String numberCard) {
        if (account.getCardNo().equals(numberCard)) {
            System.out.println("You can't transfer money to the same account!");
        } else if (!account.isValidLuhn(numberCard)) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        } else if (!dbHelper.isAccountExistAndLogged(numberCard, "", "Exist")) {
            System.out.println("Such a card does not exist.");
        } else {
            long moneyTransfer = Long.parseLong(valueIO("Enter how much money you want to transfer: "));
            if (moneyTransfer > account.getBalance()) {
                System.out.println("Not enough money!");
            } else {
                dbHelper.transferMoneyToAccount(moneyTransfer, numberCard, account.getCardNo());
                System.out.println("Success!");
            }
        }
    }
}