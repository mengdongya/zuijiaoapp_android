package android.zuijiao.net.zuijiaoapp;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {

        super(Application.class);
        String url= "222.baidu.com/2222" ;
        Integer in = Integer.valueOf(url.substring(url.lastIndexOf("/"), url.length())) ;
        System.out.println("in==" + in);

    }
}