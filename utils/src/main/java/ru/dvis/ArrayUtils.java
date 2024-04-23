package ru.dvis;

public class ArrayUtils {

    public static int findMaximumSlopePoint(int[] arr) {
        double maxSlope = Double.NEGATIVE_INFINITY;
        int maxIndex = 0;

        for (int i = 1; i < arr.length - 1; i++) {
            double slope = Math.atan2(arr[i + 1] - arr[i - 1], 2.0);
            if (slope > maxSlope) {
                maxSlope = slope;
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    public static double calculateSlope(int[] arr, int index) {
        double slope = Math.atan2(arr[index + 1] - arr[index - 1], 2.0);
        return Math.toDegrees(slope);
    }
}
