package imageProcessing.latency;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageProcessing {

    public static final String SOURCE_FILE = "/home/vyom/IdeaProjects/multithreading/src"
            + "/resources/many-flowers.jpg";
    public static final String DESTINATION_FILE = "/home/vyom/IdeaProjects/multithreading/src/out"
            + "/many-flowers.jpg";

    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage result = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        long startTime = System.currentTimeMillis();
        recolorMultiThreaded(originalImage, result, 4);
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
        File outputFile = new File(DESTINATION_FILE);
        ImageIO.write(result, "jpg", outputFile);
    }

    public static void recolorMultiThreaded(BufferedImage image, BufferedImage result,
            int numberOfThreads) {
        List<Thread> threads = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight() / numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadMultiplier = i;
            Thread thread = new Thread(() -> {
                int leftCorner = 0;
                int topCorner = height * threadMultiplier;

                recolorImage(image, result, leftCorner, topCorner, width, height);
            });
            threads.add(thread);
        }

        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void recolorSingleThreaded(BufferedImage image, BufferedImage resultImage) {
        recolorImage(image, resultImage, 0, 0, image.getWidth(), image.getHeight());
    }

    public static void recolorImage(BufferedImage originalImage, BufferedImage resultImage,
            int leftCorner, int topCorner, int width, int height) {
        for (int x = leftCorner; x < leftCorner + width
                && x < originalImage.getWidth(); x++) {
            for (int y = topCorner; y < topCorner + height
                    && y < originalImage.getHeight(); y++) {
                recolorPixel(originalImage, resultImage, x, y);
            }
        }
    }

    public static void recolorPixel(BufferedImage image, BufferedImage resultImage, int x, int y) {
        int rgb = image.getRGB(x, y);
        int red = getRed(rgb);
        int blue = getBlue(rgb);
        int green = getGreen(rgb);

        int newRed = red;
        int newBlue = blue;
        int newGreen = green;

        if (isShadeOfGrey(red, green, blue)) {
            newGreen = Math.max(0, green - 80);
            newRed = Math.min(255, red + 10);
            newBlue = Math.max(0, blue - 20);
        }

        int newRgb = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRgb);
    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }


    public static boolean isShadeOfGrey(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30
                && Math.abs(green - blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;
        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }

}
