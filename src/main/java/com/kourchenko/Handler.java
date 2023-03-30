package com.kourchenko;

import software.amazon.awssdk.http.HttpStatusCode;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kourchenko.models.Event;
import com.kourchenko.service.ResponseService;
import com.kourchenko.utils.FileUtils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kourchenko.exception.BadRequestException;
import com.kourchenko.handlers.FileResizeHandler;

// Handler value: example.Handler
public class Handler implements RequestHandler<Map<String, Object>, String> {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    private AmazonS3 s3Client;

    public Handler() {}

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        long startTime = System.currentTimeMillis();

        String response = "";
        try {
            logger.info("Received file resize request.");

            // Parse input to Event.
            Event eventInput = praseJSONToEventInput(event);

            s3Client = DependencyFactory.getAmazonS3Client(eventInput.getS3AccessKey(),
                    eventInput.getS3SecretKey(), eventInput.getS3Region());

            // Only resize accepted file types.
            String fileName = FileUtils.getFileName(eventInput.getURL());
            if (!FileUtils.fileIsImage(fileName)) {
                throw new Exception("File is not a supported file type.");
            }

            // Process Event, return resized file URL.
            String result = FileResizeHandler.processFileEvent(eventInput, s3Client, logger);

            // Log execution time.
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("Successful file resize request. " + elapsedTime + "(ms).");

            // Return JSON response.
            response = ResponseService.parseResponse(result, null, HttpStatusCode.OK);
        } catch (BadRequestException e) {
            logger.error("Bad request.", e);
            response = ResponseService.parseResponse("", e, HttpStatusCode.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Exception processing request.", e);
            response = ResponseService.parseResponse("", e, HttpStatusCode.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    /**
     * Convert incoming HTTP request body into {@code EventInput}.
     *
     * @param body Map<String, Object> HTTP request body
     * @return EventInput
     */
    private static Event praseJSONToEventInput(Map<String, Object> body) {
        Event eventInput = new ObjectMapper().convertValue(body, Event.class);
        return eventInput;
    }
}
