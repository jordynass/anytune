package jordangreenblatt.anytuneusa;

import java.util.Arrays;

/**
 * Created by jordynass on 8/12/15.
 */
class Song {
    static String simpleTitle(String title) {
        if (title == null || title.length() < 1) return title;

        char[] ca = title.toLowerCase().toCharArray();
        int i = 0;
        if (ca[0] == '(' ) {//may want to switch to title.contains but it's probably unnecessary, at least for now
            while (i < ca.length && ca[i] != ')') i++;
            ca = Arrays.copyOfRange(ca, i+1, ca.length);
        }
        if (ca[ca.length-1] == ')') {
            while (i > 0 && ca[i] != '(') i--;
            ca = Arrays.copyOfRange(ca, 0, i);
        }

        if (ca.length < 1) return title;

        if (ca[0] == '[' ) {
            while (i < ca.length && ca[i] != ']') i++;
            ca = Arrays.copyOfRange(ca, i+1, ca.length);
        }
        if (ca[ca.length-1] == '[') {
            while (i > 0 && ca[i] != ']') i--;
            ca = Arrays.copyOfRange(ca, 0, i);
        }
        Arrays.sort(ca);

        while (!Character.isAlphabetic(ca[i])) i++;
        ca = Arrays.copyOfRange(ca, i, ca.length);

        return new String(ca);
    }

    //public static boolean areClose(String title1, String title2)
}
