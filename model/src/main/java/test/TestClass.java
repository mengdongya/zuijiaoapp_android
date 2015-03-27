package test;

import com.zuijiao.android.util.Optional;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.Gourmets;
import com.zuijiao.android.zuijiao.network.RouterGourmet;
import com.zuijiao.android.zuijiao.network.RouterOAuth;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Chen Hao on 3/16/15.
 */
public class TestClass {

    static void testRegister() {
        RouterOAuth.INSTANCE.registerEmailRoutine("an",
                null,
                "an",
                "an",
                Optional.empty(),
                Optional.empty(),
                () -> {
                    System.out.println("reg success");

                    RouterOAuth.INSTANCE.loginEmailRoutine("an",
                            "an",
                            Optional.empty(),
                            Optional.empty(),
                            () -> System.out.println("success"),
                            () -> System.out.println("failure")
                    );
                },
                () -> System.out.println("failure"));
    }

    static void testVisitor() {
        RouterOAuth.INSTANCE.loginEmailRoutine("2",
                "c81e728d9d4c2f636f067f89cc14862c",
                Optional.empty(),
                Optional.empty(),
                () -> System.out.println("success"),
                () -> System.out.println("failure")
        );
    }

    static void md5Test() throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("md5");
        byte[] result = messageDigest.digest("2".getBytes());
        System.out.println("MD5(2): " + DatatypeConverter.printHexBinary(result));
    }

    static void fetchRecommendation() {
        RouterOAuth.INSTANCE.loginEmailRoutine("2",
                "c81e728d9d4c2f636f067f89cc14862c",
                Optional.empty(),
                Optional.empty(),
                () -> {
                    RouterGourmet.INSTANCE.fetchOurChoice(null
                            , null
                            , 20
                            , (Gourmets gourmets) -> {
                        for (Gourmet gourmet : gourmets.getGourmets()) {
                            System.out.println(gourmet.getName());
                        }
                    }
                            , (String errorString) -> {
                        System.out.println(errorString);
                    });
                },
                () -> System.out.println("failure")
        );
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Date d = new Date();
        d.setDate(12);
        d.setYear(10);
        d.setMonth(0);

        System.out.println("date: " + (new SimpleDateFormat("yyyy-MM-dd").format(d)));
    }

}
