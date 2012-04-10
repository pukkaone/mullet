package org.springframework.petclinic.util;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class BindingResultUtils {

    /**
     * Gets messages for all errors.
     *
     * @param bindingResult
     *            binding result
     * @param messageSource
     *            use to format message
     * @return messages
     */
    public static List<String> getErrorMessages(
            BindingResult bindingResult, MessageSource messageSource)
    {
        ArrayList<String> messages = new ArrayList<String>();
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    String message = messageSource.getMessage(
                            fieldError, LocaleContextHolder.getLocale());
                    messages.add(message);
                }
            }
        }

        return messages;
    }
}
