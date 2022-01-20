package banking;

import java.util.HashMap;
import java.util.Random;

import static banking.Constants.*;

public class Account {
    private final Random rd = new Random();
    private final HashMap<String, String> maps = new HashMap<>();

    private String cardNo;
    private long balance;
    private int pin;

    public String getCardNo() {
        return cardNo;
    }

    public int getPin() {
        return pin;
    }

    public void onCreateAccount() {
        this.cardNo = onCreateCreditCardLuhn();
        this.pin = rd.nextInt(9000) + 1000;
        maps.put(getCardNo(), String.valueOf(getPin()));
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    @Override
    public String toString() {
        return String.format("%s\n%s\n%s\n%s\n%s", HAS_BEEN_CREATED, LABEL_CREATE_CARD, getCardNo(), LABEL_CREATE_PIN, getPin());
    }
    public boolean isValidLuhn(String cardNo) {
        StringBuilder stringBuilder = new StringBuilder(cardNo);
        int nSum = 0;
        for (int i = 0; i < stringBuilder.length(); i += 2) {
            int d = stringBuilder.charAt(i) - '0';
            d = d * 2;
            if (d / 10 != 0) {
                stringBuilder.setCharAt(i, Character.forDigit(d - 9, 10));
            } else {
                stringBuilder.setCharAt(i, Character.forDigit(d, 10));
            }
        }
        for (int i = 0; i < stringBuilder.length(); i++) {
            nSum += stringBuilder.charAt(i) - '0';
        }
        return nSum % 10 == 0;
    }
    private String onCreateCreditCardLuhn() {
        String creditCard = "400000" + (rd.nextInt(90000) + 10000) + (rd.nextInt(9000) + 1000);
        StringBuilder stringBuilder = new StringBuilder(creditCard);

        int nSum = 0;
        for (int i = 0; i < stringBuilder.length(); i += 2) {
            int d = stringBuilder.charAt(i) - '0';
            d = d * 2;
            if (d / 10 != 0) {
                stringBuilder.setCharAt(i, Character.forDigit(d - 9, 10));
            } else {
                stringBuilder.setCharAt(i, Character.forDigit(d, 10));
            }
        }
        for (int i = 0; i < stringBuilder.length(); i++) {
            nSum += stringBuilder.charAt(i) - '0';
        }
        if (nSum % 10 == 0) {
            creditCard = creditCard.concat("0");
        } else {
            int last = 10 - nSum % 10;
            creditCard = creditCard.concat(String.valueOf(last));
        }
        return creditCard;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}
