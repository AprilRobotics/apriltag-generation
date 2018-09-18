package april.tag;

import static java.lang.Math.*;

public class LayoutUtil {
    public static ImageLayout getLayout(String specifier) {
        if (specifier.startsWith("classic_")) {
            int size = Integer.parseInt(specifier.substring(8));
            return LayoutUtil.getClassicLayout(size);
        } else if (specifier.startsWith("standard_")) {
            int size = Integer.parseInt(specifier.substring(9));
            return LayoutUtil.getStandardLayout(size);
        } else if (specifier.startsWith("circle_")) {
            int size = Integer.parseInt(specifier.substring(7));
            return LayoutUtil.getCircleLayout(size);
        } else if (specifier.startsWith("custom_")) {
            return ImageLayout.Factory.createFromString("Custom", specifier.substring(7));
        } else {
            throw new RuntimeException("Invalid layout specification.");
        }
    }

    private static int l1DistToEdge(int x, int y, int size) {
        return Math.min(Math.min(x, size - 1 - x), Math.min(y, size - 1 - y));
    }

    public static ImageLayout getClassicLayout(int size) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (LayoutUtil.l1DistToEdge(x, y, size) == 0) {
                    sb.append('w');
                } else if (LayoutUtil.l1DistToEdge(x, y, size) == 1) {
                    sb.append('b');
                } else {
                    sb.append('d');
                }
            }
        }
        // Classic layout has no name for backwards compatibility.
        return ImageLayout.Factory.createFromString("", sb.toString());
    }

    public static ImageLayout getStandardLayout(int size) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (LayoutUtil.l1DistToEdge(x, y, size) == 1) {
                    sb.append('b');
                } else if (LayoutUtil.l1DistToEdge(x, y, size) == 2) {
                    sb.append('w');
                } else {
                    sb.append('d');
                }
            }
        }
        return ImageLayout.Factory.createFromString("Standard", sb.toString());
    }

    public static ImageLayout getCircleLayout(int size) {
        StringBuilder sb = new StringBuilder();
        double cutoff = size/2.0 - 0.25;
        int borderDistance = (int) ceil(size/2.0 - cutoff*sqrt(0.5) - 0.5);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (l1DistToEdge(x, y, size) == borderDistance) {
                    sb.append('b');
                } else if (l1DistToEdge(x, y, size) == borderDistance+1) {
                    sb.append('w');
                } else if (LayoutUtil.l2DistToCenter(x, y, size) <= cutoff) {
                    sb.append('d');
                } else {
                    sb.append('x');
                }
            }
        }

        return ImageLayout.Factory.createFromString("Circle", sb.toString());
    }

    private static double l2DistToCenter(int x, int y, int size) {
        double r = size/2.0;
        return sqrt(pow(x + 0.5 - r, 2) + pow(y + 0.5 - r, 2));
    }
}
