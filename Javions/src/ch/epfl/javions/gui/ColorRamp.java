package ch.epfl.javions.gui;

import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

/**
 * A color ramp defines a sequence of colors used for mapping values to colors in a visualization.
 * The `at` method can be used to retrieve the color at a given index within the ramp.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class ColorRamp {
    private final Color[] colors;
    private final int maxIndex;

    /**
     * An instance of the `ColorRamp` class representing the "Plasma" color map.
     */
    public static final ColorRamp PLASMA = new ColorRamp(
            Color.valueOf("0x0d0887ff"), Color.valueOf("0x220690ff"),
            Color.valueOf("0x320597ff"), Color.valueOf("0x40049dff"),
            Color.valueOf("0x4e02a2ff"), Color.valueOf("0x5b01a5ff"),
            Color.valueOf("0x6800a8ff"), Color.valueOf("0x7501a8ff"),
            Color.valueOf("0x8104a7ff"), Color.valueOf("0x8d0ba5ff"),
            Color.valueOf("0x9814a0ff"), Color.valueOf("0xa31d9aff"),
            Color.valueOf("0xad2693ff"), Color.valueOf("0xb6308bff"),
            Color.valueOf("0xbf3984ff"), Color.valueOf("0xc7427cff"),
            Color.valueOf("0xcf4c74ff"), Color.valueOf("0xd6556dff"),
            Color.valueOf("0xdd5e66ff"), Color.valueOf("0xe3685fff"),
            Color.valueOf("0xe97258ff"), Color.valueOf("0xee7c51ff"),
            Color.valueOf("0xf3874aff"), Color.valueOf("0xf79243ff"),
            Color.valueOf("0xfa9d3bff"), Color.valueOf("0xfca935ff"),
            Color.valueOf("0xfdb52eff"), Color.valueOf("0xfdc229ff"),
            Color.valueOf("0xfccf25ff"), Color.valueOf("0xf9dd24ff"),
            Color.valueOf("0xf5eb27ff"), Color.valueOf("0xf0f921ff"));

    /**
     * Constructs a new color ramp with the given array of colors.
     *
     * @param colors The list of colors in the ramp.
     * @throws IllegalArgumentException if the number of colors is less than 2.
     */
    public ColorRamp(Color ...colors){
        if(colors.length < 2)
            throw new IllegalArgumentException();
        this.colors = colors;
        this.maxIndex = colors.length - 1;
    }

    /**
     * Constructs a new color ramp with the given list of colors.
     *
     * @param colors The array of colors in the ramp.
     * @throws IllegalArgumentException if the number of colors is less than 2.
     */
    public ColorRamp(List<Color> colors){
        if(colors.size() < 2)
            throw new IllegalArgumentException();
        this.colors = colors.toArray(new Color[0]);
        this.maxIndex = colors.size() - 1;
    }

    /**
     * Returns the color at the given index within the color ramp.
     *
     * @param index The index of the color to retrieve.
     * @return The color at the given index within the color ramp.
     */
    public Color at(double index){
        if(index <= 0){
            return colors[0];
        }
        if(index >= 1)
            return colors[maxIndex];

        int lowerIndex;
        if (index >= 1) {
            lowerIndex = maxIndex;
        } else {
            lowerIndex = (int) (index * maxIndex);
        }

        Color lowerColor = colors[lowerIndex];
        if (index == 1 || lowerIndex == maxIndex) {
            return lowerColor;
        }

        int upperIndex = lowerIndex + 1;
        Color upperColor = colors[upperIndex];
        return lowerColor.interpolate(upperColor, index * maxIndex - lowerIndex);
    }
}
