package com.kseirru.utils;

import java.awt.*;

public class JDAColors {
    public static final int DEFAULT = 0x0dbcd2;
    public static final int AQUA = 0x1abc9c;

    public static final int BLACK = 0x000000;

    public static final int BLUE = 0x3498db;

    public static final int BLURPLE = 0x7289da;

    public static final int DARK_BLUE = 0x206694;

    public static final int DARK_GOLD = 0xc27c0e;

    public static final int DARK_GRAY = 0x607d8b;

    public static final int DARK_GREEN = 0x1f8b4c;

    public static final int DARK_GREY = 0x607d8b;

    public static final int DARK_MAGENTA = 0xad1457;

    public static final int DARK_ORANGE = 0xa84300;

    public static final int DARK_PURPLE = 0x71368a;

    public static final int DARK_RED = 0x992d22;

    public static final int DARK_TEAL = 0x11806a;

    public static final int GOLD = 0xf1c40f;

    public static final int GREEN = 0x2ecc71;

    public static final int GREYPLE = 0x99aab5;

    public static final int LIGHT_GRAY = 0x979c9f;

    public static final int LIGHT_GREY = 0x979c9f;

    public static final int MAGENTA = 0xe91e63;

    public static final int ORANGE = 0xe67e22;

    public static final int PINK = 0xe91e63;

    public static final int PURPLE = 0x9b59b6;

    public static final int RED = 0xe74c3c;

    public static final int TEAL = 0x3498db;

    public static int rgb(int red, int green, int blue) {
        return 65536 * red + 256 * green + blue;
    }

    public static int[] hexToRGB(int hex) {
        return new int[] {(hex & 0xFF0000) >> 16, (hex & 0xFF00) >> 8, hex & 0xFF};
    }

    public static int colorToHex(Color color) {
        return rgb(color.getRed(), color.getGreen(), color.getBlue());
    }
}