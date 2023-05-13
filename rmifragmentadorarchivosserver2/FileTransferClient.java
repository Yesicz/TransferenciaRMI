import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileTransferClient {
    private static final String FILENAME = "FirstAppUnityRA.mp4";
    private static final int NUM_FRAGMENTS = 10;
    private static final int NUM_CLIENTS = 10;

    public static void main(String[] args) throws RemoteException, NotBoundException, IOException {
        File file = new File(FILENAME);
        long fileSize = file.length();
        long chunkSize = fileSize / NUM_FRAGMENTS;

        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        FileTransfer stub = (FileTransfer) registry.lookup("FileTransfer");

        List<Integer> fragmentOrder = generateRandomFragmentOrder();

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[(int) chunkSize];
            int bytesRead;

            for (int fragmentNumber = 0; fragmentNumber < NUM_FRAGMENTS; fragmentNumber++) {
                bytesRead = bis.read(buffer);

                stub.transferFile(buffer, file.getName());
                System.out.println("Soy el cliente " + (fragmentNumber + 1) +
                        " y he enviado el fragmento " + fragmentOrder.get(fragmentNumber) +
                        " del archivo " + FILENAME + ", resta enviar un " +
                        calculatePercentageRemaining(fragmentNumber, NUM_FRAGMENTS) + "% del archivo");
            }
        }
    }

    private static List<Integer> generateRandomFragmentOrder() {
        List<Integer> fragmentOrder = new ArrayList<>();
        for (int i = 1; i <= NUM_FRAGMENTS; i++) {
            fragmentOrder.add(i);
        }
        Collections.shuffle(fragmentOrder);
        return fragmentOrder;
    }

    private static double calculatePercentageRemaining(int fragmentIndex, int totalFragments) {
        double percentage = ((double) (totalFragments - fragmentIndex - 1) / totalFragments) * 100;
        return Math.max(0, percentage);
    }
}
