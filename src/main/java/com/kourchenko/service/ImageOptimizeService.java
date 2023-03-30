package com.kourchenko.service;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import com.kourchenko.exception.BadRequestException;
import com.kourchenko.utils.FileUtils;

public class ImageOptimizeService {

    /**
     * Given a urlString and dimension, provide a resized BufferedImage.
     *
     * @param urlString The URL String with an image.
     * @param dimension The {@code Dimension}.
     * @return {@code BufferedImage}
     * @throws BadRequestException Request format errors.
     */
    public static BufferedImage resizeFromUrl(String urlString, Dimension dimension)
            throws BadRequestException {
        BufferedImage bufferedImage = ImageRequestService.getBufferedFileFromURL(urlString);

        String fileName = FileUtils.getFileName(urlString);

        return resizeToDimensions(fileName, bufferedImage, dimension);
    }

    /**
     * Given a URL and dimension, provide a resized BufferedImage.
     *
     * @param url The URL String with an image.
     * @param dimension The {@code Dimension}.
     * @return {@code BufferedImage}
     * @throws BadRequestException Request format errors.
     */
    public static BufferedImage resizeFromUrl(URL url, Dimension dimension)
            throws BadRequestException {
        BufferedImage bufferedImage = ImageRequestService.getBufferedFileFromURL(url);

        String fileName = FileUtils.getFileName(url.getPath());

        return resizeToDimensions(fileName, bufferedImage, dimension);
    }

    /**
     * Get resized BufferedImage.
     *
     * @param fileName The file name.
     * @param bufferedImage The BufferedImage to resize.
     * @param dimension The {@code Dimension}.
     * @return Resized {@code BufferedImage}.
     */
    private static BufferedImage resizeToDimensions(String fileName, BufferedImage bufferedImage,
            Dimension dimension) {
        try {
            int width = (int) dimension.getWidth();
            int height = (int) dimension.getHeight();

            Image image = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage scaledBI = convertImageToBufferedImage(image);
            scaledBI.getGraphics().drawImage(scaledBI, 0, 0, null);

            return dropAlphaIfNeeded(scaledBI, fileName);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "[ImageOptimizeService.resizeToDimensions] " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Removes the Alpha (transparency) value from an if it is a JPG or JPEG. If this is not done,
     * drawing an image in this format with an alpha value will result in a heavily distorted image.
     *
     * @param bufferedImage Image to redraw without alpha
     * @param fileName Name of file (to determine extension
     *
     * @return Buffered Image without an alpha value
     *
     */
    private static BufferedImage dropAlphaIfNeeded(BufferedImage bufferedImage, String fileName) {

        if (StringUtils.endsWithIgnoreCase(fileName, "jpg")
                || StringUtils.endsWithIgnoreCase(fileName, "jpeg")) {
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            BufferedImage convertedImg =
                    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            convertedImg.getGraphics().drawImage(bufferedImage, 0, 0, null);

            return convertedImg;
        }

        return bufferedImage;
    }

    /**
     * Convert Image to BufferedImage.
     *
     * @param image {@code Iimage}
     * @return {@code BufferedImage}.
     */
    private static BufferedImage convertImageToBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        int width = image.getWidth(null);
        int height = image.getHeight(null);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D graphics2d = bufferedImage.createGraphics();
        graphics2d.drawImage(image, 0, 0, null);
        graphics2d.dispose();

        return bufferedImage;
    }
}
