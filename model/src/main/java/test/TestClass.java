package test;

import com.google.gson.JsonParser;
import com.squareup.okhttp.Route;
import com.zuijiao.android.util.DateUtil;
import com.zuijiao.android.util.HanyuPinyinHelper;
import com.zuijiao.android.util.MD5;
import com.zuijiao.android.util.Optional;
import com.zuijiao.android.util.functional.LambdaExpression;
import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.Banquent.OrderStatus;
import com.zuijiao.android.zuijiao.model.Banquent.Review;
import com.zuijiao.android.zuijiao.model.Banquent.Reviews;
import com.zuijiao.android.zuijiao.model.Gourmet;
import com.zuijiao.android.zuijiao.model.Gourmets;
import com.zuijiao.android.zuijiao.model.common.ConfigurationType;
import com.zuijiao.android.zuijiao.model.message.News;
import com.zuijiao.android.zuijiao.model.user.User;
import com.zuijiao.android.zuijiao.network.Cache;
import com.zuijiao.android.zuijiao.network.Router;
import com.zuijiao.android.zuijiao.network.RouterGourmet;
import com.zuijiao.android.zuijiao.network.RouterOAuth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
        RouterOAuth.INSTANCE.loginEmailRoutine("2@2.2",
                "c81e728d9d4c2f636f067f89cc14862c",
                Optional.empty(),
                Optional.empty(),
                () -> expression.action(),
                errorMessage -> System.out.println("failure")
        );
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Router.setup("http://xielingyu3.zuijiaodev.com", null, null, null, "yyyy-MM-dd'T'HH:mm:ssXXX");
        testOp(() -> {

//            Router.getGourmetModule().addGourmet("pri", "add", "20", "desc", null, null, 1, 1, true, null, null);

//            Router.getBanquentModule().orders(OrderStatus.Waiting, null, 135, s -> {
//                System.out.println(s);
//            }, e -> System.err.println("err: " + e));

//            Router.getMessageModule().markAsRead(() -> System.out.println("succ"), () -> System.out.println("failed"));

//            Router.getBanquentModule().commentsofBanquent(29, null, 20, new OneParameterExpression<Reviews>() {
//                @Override
//                public void action(Reviews reviews) {
//                    List<Review> reviewList = reviews.getReviewList();
//                    System.out.println("test success");
//
//                }
//            }, new OneParameterExpression<String>() {
//                @Override
//                public void action(String s) {
//                    System.out.println("test error");
//                }
//            });
        });
    }

}
