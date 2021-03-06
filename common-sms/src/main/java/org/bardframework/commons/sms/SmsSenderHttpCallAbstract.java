package org.bardframework.commons.sms;

import org.bardframework.commons.utils.StringUtils;
import org.bardframework.commons.web.http.HttpCallResult;
import org.bardframework.commons.web.http.HttpCallerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class SmsSenderHttpCallAbstract implements SmsSender {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SmsSenderHttpCallAbstract.class);

    @Override
    public SendResult send(String receiverNumber, String message) {
        try {
            return this.getSmsSender(receiverNumber, message, null, null, null);
        } catch (Exception e) {
            LOGGER.error("error occurred in sms sender thread", e);
            return SendResult.ERROR;
        }
    }

    @Override
    public SendResult send(String receiverNumber, String message, String signature) {
        try {
            return this.getSmsSender(receiverNumber, message, signature, null, null);
        } catch (Exception e) {
            LOGGER.error("error occurred in sms sender thread", e);
            return SendResult.ERROR;
        }
    }

    @Override
    public SendResult send(String receiverNumber, String message, String username, String password) {
        try {
            return this.getSmsSender(receiverNumber, message, null, username, password);
        } catch (Exception e) {
            LOGGER.error("error occurred in sms sender thread", e);
            return SendResult.ERROR;
        }
    }

    private SendResult getSmsSender(String receiverNumber, String message, String signature, String username, String password) {
        String receiverNumberForLog = StringUtils.hideString(receiverNumber, 4, '*');
        LOGGER.info("sending sms to:  " + receiverNumberForLog);
        Map<String, String> variables = new HashMap<>();
        variables.put("::to::", receiverNumber);
        variables.put("::message::", message);
        variables.put("::signature::", signature);
        variables.put("::username::", username);
        variables.put("::password::", password);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", this.getContentType());
        try {
            HttpCallResult callResult = HttpCallerUtils.httpCall(this.getUrlTemplate(), this.getHttpMethod(), this.getBodyTemplate(), variables, headers);
            LOGGER.info("Result of sending sms to [{}] is [{}]", receiverNumberForLog, callResult.getResponseCode());
            if (callResult.hasError()) {
                LOGGER.info("error response sending sms to [{}], [{}]", receiverNumberForLog, callResult.getBody());
                return SendResult.ERROR;
            }
            String body = new String(callResult.getBody(), StandardCharsets.UTF_8);
            if (null != this.getInsufficientCreditPattern() && Pattern.matches(this.getInsufficientCreditPattern(), body)) {
                return SendResult.INSUFFICIENT_CREDIT;
            }
            if (null != this.getSuccessPattern()) {
                if (Pattern.matches(this.getSuccessPattern(), body)) {
                    return SendResult.SUCCESS;
                } else {
                    return SendResult.ERROR;
                }
            }
            return SendResult.SUCCESS;
        } catch (Exception e) {
            LOGGER.debug("error sending sms", e);
            return SendResult.ERROR;
        }
    }

    public abstract String getHttpMethod();

    public abstract String getUrlTemplate();

    public abstract String getBodyTemplate();

    public abstract String getContentType();

    public abstract String getSuccessPattern();

    public abstract String getInsufficientCreditPattern();
}
