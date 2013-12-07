package utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Adam Wasiljew
 */
public class StringToNumberValidator {

    private String number;

    public StringToNumberValidator(String number) {
        this.number = number;
    }

    public boolean validate() {
        if(StringUtils.isNotEmpty(number)) {
            return StringUtils.containsOnly(number, "0123456789.");
        }
        return false;
    }

}
