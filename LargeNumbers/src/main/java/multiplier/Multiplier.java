package multiplier;

import adder.Adder;
import org.apache.commons.lang3.StringUtils;
import utils.StringToNumberValidator;

/**
 * @author Adam Wasiljew
 */
public class Multiplier {

    private Adder adder;

    public Multiplier(Adder adder) {
        this.adder = adder;
    }

    public String multiply(String a, String b) {
        if (!new StringToNumberValidator(a).validate()) {
            return "Not valid number: " + a;
        }
        if (!new StringToNumberValidator(b).validate()) {
            return "Not valid number: " + b;
        }
        String c;
        if (a.length() > b.length()) {
            c = StringUtils.reverse(b);
            b = StringUtils.reverse(a);
            a = c;
        } else {
            a = StringUtils.reverse(a);
            b = StringUtils.reverse(b);
        }

        StringBuilder tmp = new StringBuilder();
        StringBuilder level = new StringBuilder();
        tmp.append("0");
        for (int i = 0; i < a.length(); i++) {
            String v = multiplyLevel(b, a.charAt(i), level.toString());
            level.append("0");
            v = adder.add(tmp.toString(), v);
            tmp = new StringBuilder(v);
        }
        return tmp.toString();
    }

    private String multiplyLevel(String a, char b, String level) {
        StringBuilder sb = new StringBuilder();
        int r = 0;
        for (int i = 0; i < a.length(); i++) {
            String v = multiplySingle(a.charAt(i), b, r);
            if (v.length() > 1) {
                sb.append(v.substring(1,2));
                r = Integer.parseInt(v.substring(0,1));
            } else {
                sb.append(v);
                r = 0;
            }
        }
        if(r > 0) {
            sb.append(r);
        }
        return sb.reverse().append(level).toString();
    }

    private String multiplySingle(char a, char b, int r) {
        int ai = Integer.parseInt(a + "");
        int bi = Integer.parseInt(b + "");
        return ((ai * bi) + r) + "";
    }

}
