package com.zuijiao.android.util.functional;

/**
 * Created by xiaqibo on 2015/6/30.
 */
public class ImageUrlUtil {

    public static void main(String[] args) {
        String origin = "http://debug.com/Avatar/1.png";
        String result = imageUrl(origin);
        System.out.println(result);

        String origin1 = "debug:/Avatar/1.png";
        String result1 = imageUrl(origin1);
        System.out.println(result1);

        String origin2 = "/debug/Avatar/1.png";
        String result2 = imageUrl(origin2);
        System.out.println(result2);
    }

    public static String imageUrl(String url) {
        if (url == null || url.startsWith("http")) return url;

        if (url.indexOf(':') > 0) {
            Tuple<String> t = splitToStringTuple(url, ":");
            return String.format("http://%s.b0.upaiyun.com%s", t.getFst(), t.getSnd());
        }
        return "http://pic.zuijiao.net" + url;
    }

    public static Tuple<String> splitToStringTuple(String toSplit, String sep) {
        String[] s = toSplit.split(sep);
        assert s.length == 2;
        return new Tuple<String>(s[0], s[1]);
    }

}

class Tuple<T> {
    private T fst;
    private T snd;

    public Tuple(T fst, T snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public T getFst() {
        return fst;
    }

    public T getSnd() {
        return snd;
    }
}
