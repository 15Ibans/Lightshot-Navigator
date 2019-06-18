import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class LinkManager {

    private static String[] charArray = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    public static URL getURL(String directURL) { // only this uses jsoup
        System.out.println("Beginning a new operation");
        URL realSrc = null;
        Document site;
        try {
            //System.out.println("Connecting...");
            site = Jsoup.connect(directURL).userAgent("Mozilla/5.0").get();
            //System.out.println("Got the html. Parsing now");
            Element image = site.select("img.screenshot-image").first();
            String imgSrc = image.absUrl("src");
            realSrc = new URL(imgSrc);
            //System.out.println("Parsed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return realSrc;
    }

    public static int getNumber(String id) {
        int exp = id.length() - 1;
        int psn = 0;
        int total = 0;
        for (int i = 0; i < id.length(); i++) {
            for (int j = 0; j < charArray.length; j++) {
                if (id.substring(i, i + 1).equals(charArray[j])) {
                    psn = j;
                    break;
                }
            }
            total = total + (int) (psn * (Math.pow(charArray.length, exp)));
            exp--;
        }
        return total;
    }

    public static String getString(int number) {
        ArrayList<Double> remainders = new ArrayList<>();
        double mod = 0;
        double aDouble = 0;
        int whileCounter = 0;
        String result = "";
        while (number != 0) {
            aDouble = Double.valueOf(number) / 36;
            mod = (aDouble % 1) * 36;
            remainders.add(whileCounter, mod);
            number = number / 36;
            whileCounter++;
        }

        for (int i = 0; i < remainders.size(); i++) {
            result = charArray[(int) Math.round(remainders.get(i))] + result;
        }

        return result;
    }

    public static String incrementID(String id, int factor) {
        int dec = getNumber(id) + factor;
        return getString(dec);
    }

    public static String decrementID(String id, int factor) {
        int dec = getNumber(id) - factor;
        return getString(dec);
    }

}
