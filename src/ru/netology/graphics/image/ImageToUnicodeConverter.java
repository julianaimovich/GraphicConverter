package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class ImageToUnicodeConverter implements TextGraphicsConverter {

    protected int width;
    protected int height;
    protected double maxRatio;
    protected TextColorSchema schema;

    public ImageToUnicodeConverter(TextColorSchema textColorSchema) {
        this.schema = textColorSchema;
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));

        int currentWidth = img.getWidth();
        int currentHeight = img.getHeight();
        int newWidth;
        int newHeight;

        if (currentWidth > this.width) {
            float diff = currentWidth / (float) this.width;
            if (diff > this.maxRatio) {
                throw new BadImageSizeException(diff, this.maxRatio);
            }
            newWidth = Math.round(currentWidth / diff);
            newHeight = Math.round(currentHeight / diff);
        } else if (currentHeight > this.height) {
            float diff = currentHeight / (float) this.height;
            if (diff > this.maxRatio) {
                throw new BadImageSizeException(diff, this.maxRatio);
            }
            newWidth = Math.round(currentWidth / diff);
            newHeight = Math.round(currentHeight / diff);
        } else {
            newWidth = currentWidth;
            newHeight = currentHeight;
        }

        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        WritableRaster bwRaster = bwImg.getRaster();

        char[][] arr = new char[newHeight][newWidth];
        for (int w = 0; w < newWidth; w++) {
            for (int h = 0; h < newHeight; h++) {
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                arr[h][w] = c;
            }
        }

        String lineSeparator = System.lineSeparator();
        StringBuilder sb = new StringBuilder();

        char[] result = new char[newWidth*2];
        for (char[] row : arr) {
            int count = 0;
            for (char x : row) {
                for (char c : new char[]{x, x}) {
                    result[count++] = c;
                }
            }
            result = Arrays.copyOfRange(result, 0, count);

            String str = new String(result);
            sb.append(str).append(lineSeparator);
        }

        return sb.toString();
    }

    @Override
    public void setMaxWidth(int width) {
        this.width = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.height = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}