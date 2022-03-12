package interThreadCommunication;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

public class MatricesGenerator {

    private static final String OUTPUT_FILE = "./src/resources/matrices";
    private static final int N = 10;
    private static final int MEMBER_OF_MATRIX_PAIRS = 100000;

    public static void main(String[] args) throws IOException {
        generateMatrices();
    }

    static void generateMatrices() throws IOException {
        Random random = new Random();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUTPUT_FILE));
        for (int i = 0; i < MEMBER_OF_MATRIX_PAIRS; i++) {
            for (int i1 = 0; i1 < N; i1++) {
                for (int i2 = 0; i2 < N; i2++) {
                    double number = random.nextDouble(50.00);
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    if (i2 != 9)
                        bufferedWriter.write(decimalFormat.format(number) + ", ");
                    else
                        bufferedWriter.write(decimalFormat.format(number));
                }
                bufferedWriter.write("\n");
                if (i1 == 9) {
                    bufferedWriter.write("\n");
                }
            }
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }


}
