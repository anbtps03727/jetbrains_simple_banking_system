package banking;


import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class DBHelper {
    private final String url;

    public DBHelper(String url) {
        this.url = url;
    }

    private Connection connect() {
        Connection conn = null;
        try {
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl(url);
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private String createDatabaseStatement() {
        return "CREATE TABLE IF NOT EXISTS card(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "number TEXT NOT NULL," +
                "pin TEXT NOT NULL," +
                "balance INTEGER DEFAULT 0)";
    }

    public void createDatabase() {
        try (Connection conn = connect();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate(createDatabaseStatement());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertAccount(Account account) {
        String query = "INSERT INTO card (number,pin) VALUES (?,?)";
        try (Connection conn = connect(); PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, account.getCardNo());
            preparedStatement.setString(2, String.valueOf(account.getPin()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void depositMoneyToAccount(int income, String numberCard) {
        String query = "UPDATE card SET balance = balance + ? WHERE number = ?";
        try (Connection conn = connect(); PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, income);
            preparedStatement.setString(2, numberCard);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeAccount(String number, String pin) {
        String query = "DELETE FROM card WHERE number = ? AND pin = ?";
        try (Connection conn = connect(); PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, number);
            preparedStatement.setString(2, pin);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isAccountExistAndLogged(String number, String pin, String condition) {
        String query = "";
        if (condition.equals("Exist")) {
            query = "SELECT EXISTS (SELECT 1 FROM card WHERE number = ?)";
        } else {
            query = "SELECT EXISTS (SELECT 1 FROM card WHERE number = ? AND pin = ?)";
        }
        try (Connection conn = connect(); PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            if (condition.equals("Exist")) {
                preparedStatement.setString(1, number);
            } else {
                preparedStatement.setString(1, number);
                preparedStatement.setString(2, pin);
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.getInt(1) == 1) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Long getBalance(String number) {
        String query = "SELECT balance FROM card WHERE number = ?";
        long balance = 0;
        try (Connection conn = this.connect(); PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, number);
            ResultSet resultSet = preparedStatement.executeQuery();
            balance = Long.parseLong(resultSet.getString("balance"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }


    public void transferMoneyToAccount(long balance, String numberAccountReceive, String numberAccountSource) {
        String updateAccountReceive = "UPDATE card SET balance = balance + ? WHERE number = ?";
        String updateAccountSource = "UPDATE card SET balance = balance - ? WHERE number = ?";
        try (Connection conn = this.connect()) {
            conn.setAutoCommit(false);
            PreparedStatement preparedStatement1 = conn.prepareStatement(updateAccountSource);
            preparedStatement1.setLong(1, balance);
            preparedStatement1.setString(2, numberAccountSource);
            preparedStatement1.executeUpdate();

            PreparedStatement preparedStatement2 = conn.prepareStatement(updateAccountReceive);
            preparedStatement2.setLong(1, balance);
            preparedStatement2.setString(2, numberAccountReceive);
            preparedStatement2.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Account getAccount(String number, String pin) {
        String query = "SELECT * FROM card WHERE number = ? AND pin = ?";
        Account account = new Account();
        try (Connection conn = this.connect(); PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, number);
            preparedStatement.setString(2, pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            account.setCardNo(resultSet.getString("number"));
            account.setPin(Integer.parseInt(resultSet.getString("pin")));
            account.setBalance(Long.parseLong(resultSet.getString("balance")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }
}

