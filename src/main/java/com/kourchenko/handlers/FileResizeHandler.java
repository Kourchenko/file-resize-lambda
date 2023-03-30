package com.kourchenko.handlers;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;

import com.kourchenko.DependencyFactory;
import com.kourchenko.enums.ResizeMode;
import com.kourchenko.exception.BadRequestException;
import com.kourchenko.models.Event;
import com.kourchenko.service.AmazonS3Service;
import com.kourchenko.service.ImageOptimizeService;
import com.kourchenko.utils.FileUtils;
import com.amazonaws.services.s3.AmazonS3;

public class FileResizeHandler {

    private static final int DEFAULT_WIDTH = 250;
    private static final int DEFAULT_HEIGHT = 250;

    /**
     * Process a file resize {@code Event} and return the resized file URL.
     *
     * @param event {@code Event}
     * @param s3Client {@code S3Client}
     * @param logger {@code Logger}
     * @return String URL
     * @throws BadRequestException Request format errors.
     */
    public static String processFileEvent(Event event, AmazonS3 s3Client, Logger logger)
            throws BadRequestException {
        try {

            String url = event.getURL();
            String bucket = event.getBucket();
            Integer height = event.getHeight() != null && event.getHeight() > 0 ? event.getHeight()
                    : DEFAULT_HEIGHT;
            Integer width = event.getWidth() != null && event.getWidth() > 0 ? event.getWidth()
                    : DEFAULT_WIDTH;
            String resizeModeStr = StringUtils.defaultIfBlank(event.getResizeMode(), "FIXED_WIDTH");
            ResizeMode resizeMode = ResizeMode.resizeMode(resizeModeStr);

            // Check ResizeMode and equalize height and width.
            height = resizeMode.equals(ResizeMode.FIXED_WIDTH) ? width : height;
            width = resizeMode.equals(ResizeMode.FIXED_HEIGHT) ? height : width;

            // Override S3 Key. Default to key generated from ResizeMode, width, and height.
            String fileName = FileUtils.getFileName(url);
            String key = StringUtils.defaultIfBlank(event.getKey(),
                    FileUtils.createResizedFileName(fileName, resizeMode, width, height));

            // AWS Region
            String region = event.getS3Region();

            logger.info(String.format("URL: %s, S3 Bucket: %s, S3 Key: %s", url, bucket, key));

            // Check if resized file exists
            if (AmazonS3Service.doesObjectExist(s3Client, bucket, key)) {
                String result = formatS3ObjectUrl(region, bucket, key);
                logger.info("Resized file exists: " + result);

                return result;
            }

            // Resize file from URL
            Dimension dimension = new Dimension(width, height);
            BufferedImage newImage = ImageOptimizeService.resizeFromUrl(url, dimension);

            // Write to resized file
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            String imageType = FilenameUtils.getExtension(fileName);
            ImageIO.write(newImage, imageType, outputStream);

            // Upload resized file to S3
            AmazonS3Service.putObject(s3Client, outputStream.toByteArray(), bucket, key, imageType);

            return formatS3ObjectUrl(region, bucket, key);
        } catch (IOException e) {
            throw new BadRequestException(
                    "[FileResizeHandler.processFileEvent] " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Return a full URL path to the S3 Object, for an region, S3 Bucket, S3 Key.
     *
     * @param region String region for S3 Object.
     * @param bucket S3 Bucket
     * @param key S3 Key (aka "file/path/to/image.png")
     * @return Full URL.
     */
    private static String formatS3ObjectUrl(String region, String bucket, String key) {
        return new StringBuilder().append("https://").append(bucket).append(".s3.").append(region)
                .append(".amazonaws.com/").append(key).toString();
    }
}
