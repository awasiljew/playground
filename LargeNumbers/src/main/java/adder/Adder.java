package adder;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Adam Wasiljew
 */
public class Adder {

    public String add(String a, String b) {
        String c;
        if (a.length() < b.length()) {
            c = StringUtils.reverse(b);
            b = StringUtils.reverse(a);
            a = c;
        } else {
            a = StringUtils.reverse(a);
            b = StringUtils.reverse(b);
        }

        StringBuilder sb = new StringBuilder();
        int r = 0;
        for (int i = 0; i < b.length(); i++) {
            String v = addSingle(b.charAt(i), a.charAt(i), r);
            v = StringUtils.reverse(v);
            r = getOverflow(v);
            sb.append(v.substring(0, 1));
        }
        if (b.length() < a.length()) {
            for (int i = b.length(); i < a.length(); i++) {
                String v = addSingle('0', a.charAt(i), r);
                v = StringUtils.reverse(v);
                r = getOverflow(v);
                sb.append(v.substring(0, 1));
                if (r == 0) {
                    if (i < a.length()) {
                        sb.append(a.substring(i + 1));
                    }
                    break;
                }
            }
        } else {
            if (r > 0) {
                sb.append(r);
            }
        }
        return sb.reverse().toString();
    }


    int getOverflow(String v) {
        if (v.length() > 1) {
            return Integer.parseInt(v.substring(1));
        }
        return 0;
    }

    private String addSingle(char a, char b, int r) {
        int ai = Integer.parseInt(a + "");
        int bi = Integer.parseInt(b + "");
        return (ai + bi + r) + "";
    }


}
