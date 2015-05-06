package test;

import com.zuijiao.android.util.MD5;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.Gourmets;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.android.zuijiao.network.RouterGourmet;
import com.zuijiao.android.zuijiao.network.RouterOAuth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.zip.CRC32;

import javax.xml.bind.DatatypeConverter;

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
                            errorMessage -> System.out.println("failure")
                    );
                },
                errorMessage -> System.out.println("failure"));
    }

    static void testVisitor() {
        RouterOAuth.INSTANCE.loginEmailRoutine("2",
                "c81e728d9d4c2f636f067f89cc14862c",
                Optional.empty(),
                Optional.empty(),
                () -> System.out.println("success"),
                errorMessage -> System.out.println("failure")
        );
    }

    static void md5Test() throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("md5");
        byte[] result = messageDigest.digest("2".getBytes());
        System.out.println("MD5(2): " + DatatypeConverter.printHexBinary(result));
    }

    static LambdaExpression fetchRecommendation() {
        return () -> {
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
        };
    }

    static void testOp(LambdaExpression expression) {
        RouterOAuth.INSTANCE.loginEmailRoutine("2",
                "c81e728d9d4c2f636f067f89cc14862c",
                Optional.empty(),
                Optional.empty(),
                () -> expression.action(),
                errorMessage -> System.out.println("failure")
        );
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
//        Router.setup("http://api.zuijiaodev.com", "zuijiaoapp", null, null);
//        testOp(fetchRecommendation());

//        CRC32 crc32 = new CRC32();
//        Long time = new Long(0x14d0873c72b);
//        crc32.update(time.byteValue());
//        System.out.println(String.format("before: %s\tafter: %s", Long.toHexString(time), Long.toHexString(crc32.getValue())));

        String s = "before: 14d0873c72b\tafter: 7ebe16cd";
        System.out.println("MD5: " + MD5.crypt(s));

    }

}
