package com.camadeusa.utility;

public class MathUtil {
    private static final int sin_precision = 5;
    private static final int sin_modulo = 360 * sin_precision;
    private static final float[] sin_values = new float[sin_modulo];
    private static final int atan2_dim;
    private static final float[] atan2_values;

    private static float lookup(int theta) {
        return theta >= 0 ? sin_values[theta % sin_modulo] : - sin_values[(- theta) % sin_modulo];
    }

    public static float sin(float theta) {
        return MathUtil.lookup((int)(theta * (float)sin_precision + 0.5f));
    }

    public static float cos(float theta) {
        return MathUtil.lookup((int)((theta + 90.0f) * (float)sin_precision + 0.5f));
    }

    public static float tan(float theta) {
        return MathUtil.sin(theta) / MathUtil.cos(theta);
    }

    public static float csc(float theta) {
        return 1.0f / MathUtil.sin(theta);
    }

    public static float sec(float theta) {
        return 1.0f / MathUtil.cos(theta);
    }

    public static float cot(float theta) {
        return MathUtil.cos(theta) / MathUtil.sin(theta);
    }

    public static final float arctan(float y, float x) {
        float mul;
        float add;
        if (x < 0.0f) {
            if (y < 0.0f) {
                x = - x;
                y = - y;
                mul = 1.0f;
            } else {
                x = - x;
                mul = -1.0f;
            }
            add = Math.copySign(3.1415927f, -1.0f);
        } else {
            if (y < 0.0f) {
                y = - y;
                mul = -1.0f;
            } else {
                mul = 1.0f;
            }
            add = 0.0f;
        }
        float invDiv = (float)(atan2_dim - 1) / (x < y ? y : x);
        int xi = (int)(x * invDiv);
        int yi = (int)(y * invDiv);
        return (float)Math.toDegrees((atan2_values[yi * atan2_dim + xi] + add) * mul);
    }

    public static int fastFloor(double d) {
        return d >= 0.0 ? (int)d : (int)d - 1;
    }

    public static int fastCeil(double d) {
        return d >= 0.0 ? (int)d + 1 : (int)d;
    }

    public static int fastAbs(int val) {
        return val >= 0 ? val : val * -1;
    }

    public static double getHarmonic(int n) {
        double result = 0.0;
        for (double i = 1.0; i <= (double)n; i += 1.0) {
            result += 1.0 / i;
        }
        return result;
    }

    public static int getMax(int ... numbers) {
        int max = Integer.MIN_VALUE;
        for (int number : numbers) {
            if (number <= max) continue;
            max = number;
        }
        return max;
    }

    public static int getMin(int ... numbers) {
        int min = Integer.MAX_VALUE;
        for (int number : numbers) {
            if (number >= min) continue;
            min = number;
        }
        return min;
    }

    public static int[] groupAbs(int ... numbers) {
        int[] abs = new int[numbers.length];
        for (int i = 0; i < numbers.length; ++i) {
            int number = numbers[i];
            abs[i] = number < 0 ? number * -1 : number;
        }
        return abs;
    }

    public static double clamp(double min, double max, double value) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(float min, float max, float value) {
        return Math.max(min, Math.min(max, value));
    }

    public static long clamp(long min, long max, long value) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int min, int max, int value) {
        return Math.max(min, Math.min(max, value));
    }

    public static int getNthDigit(int number, int base, int n) {
        return (int)((double)number / Math.pow(base, n - 1) % (double)base);
    }
    
    public static double distance(float x1, float x2, float z1, float z2) {
    		return Math.hypot(x1 - x2, z1 - z2);
    }

    public static double distance(double x1, double x2, double z1, double z2) {
   		return Math.hypot(x1 - x2, z1 - z2);

    }

    static {
        int radian_factor = sin_precision * 180;
        for (int i = 0; i < sin_values.length; ++i) {
            MathUtil.sin_values[i] = (float)Math.sin((double)i * 3.141592653589793 / (double)radian_factor);
        }
        int atan2_bits = 7;
        int atan2_bits2 = 14;
        int atan2_mask = 16383;
        int atan2_count = 16384;
        atan2_dim = (int)Math.sqrt(16384.0);
        atan2_values = new float[16384];
        for (int i = 0; i < atan2_dim; ++i) {
            for (int j = 0; j < atan2_dim; ++j) {
                float x = (float)i / (float)atan2_dim;
                float y = (float)j / (float)atan2_dim;
                MathUtil.atan2_values[j * MathUtil.atan2_dim + i] = (float)Math.atan2(y, x);
            }
        }
    }

}

