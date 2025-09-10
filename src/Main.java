import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain();
        ExecutorService executor = Executors.newFixedThreadPool(12);

        for (int i = 1; i <= 10; i++) {
            executor.submit(new Miner(i, blockchain));
        }

        executor.submit(() -> {
            Random random = new Random();
            List<String> users = List.of("miner1", "miner2", "miner3", "Nick", "Alice", "Bob", "CarShop");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String sender = users.get(random.nextInt(users.size()));
                    String receiver = users.get(random.nextInt(users.size()));
                    if (!sender.equals(receiver)) {
                        int amount = random.nextInt(30) + 1;
                        blockchain.addTransaction(new Transaction(sender, receiver, amount));
                    }
                    Thread.sleep(random.nextInt(200) + 50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        while (blockchain.getChainSize() < 15) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }
        }

        executor.shutdownNow();
    }
}