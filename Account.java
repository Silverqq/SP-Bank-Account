import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private double balance;
    private Lock lock;

    public Account(double initialBalance) {
        this.balance = initialBalance;
        this.lock = new ReentrantLock();
    }

    public void deposit(double amount) {
        lock.lock();
        try {
            balance += amount;
            System.out.println("Пополнение на " + amount + " рублей. Баланс: " + balance + " рублей");
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(double amount) {
        lock.lock();
        try {
            while (balance < amount) {
                System.out.println("Ожидание пополнения баланса...");
                Thread.sleep(1000); // Поток ожидает 1 секунду
            }

            balance -= amount;
            System.out.println("Снятие " + amount + " рублей. Баланс: " + balance + " рублей");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public double getBalance() {
        return balance;
    }

    public static void main(String[] args) {
        Account account = new Account(0);

        // Поток для пополнения счета
        Thread depositThread = new Thread(() -> {
            Random random = new Random();
            for (int i = 0; i < 5; i++) {
                double amount = random.nextDouble() * 100; // Случайная сумма от 0 до 100
                account.deposit(amount);
            }
        });
        depositThread.start();

        // Вызов метода для снятия денег после достижения требуемой суммы
        double targetAmount = 200; // Требуемая сумма для снятия денег
        while (account.getBalance() < targetAmount) {
            try {
                Thread.sleep(1000); // Поток проверяет каждую секунду
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        account.withdraw(targetAmount);

        // Вывод остатка на балансе
        System.out.println("Остаток на балансе: " + account.getBalance() + " рублей");
    }
}