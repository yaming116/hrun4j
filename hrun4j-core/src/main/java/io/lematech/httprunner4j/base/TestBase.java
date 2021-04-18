package io.lematech.httprunner4j.base;

import io.lematech.httprunner4j.common.Constant;
import io.lematech.httprunner4j.core.provider.NGDataProvider;
import io.lematech.httprunner4j.common.DefinedException;
import io.lematech.httprunner4j.widget.log.MyLog;
import org.testng.annotations.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author lematech@foxmail.com
 * @version 1.0.0
 * @className TestBase
 * @description TODO
 * @created 2021/1/20 4:41 下午
 * @publicWechat lematech
 */

public class TestBase {
    private String testCaseName;
    @BeforeSuite
    public void beforeSuite(){
        MyLog.info("[========================================]@beforeSuite()");
    }
    @BeforeMethod
    public void setUp() {
        MyLog.info("[====================" + this.testCaseName + "====================]@START");
    }
    @AfterMethod
    public void tearDown() {
        MyLog.info("[====================" + this.testCaseName + "====================]@END");
    }
    @AfterSuite
    public void afterSuite(){
        MyLog.info("[========================================]@afterSuite()");
    }
    @DataProvider
    public Object[][] dataProvider(Method method) {
        Object[][] objects = null;
        this.testCaseName = method.getName();
        try {
            objects = new NGDataProvider().dataProvider(fromClassExtractPkg(method.getDeclaringClass().getName()), testCaseName);
        } catch (Exception e) {
            e.printStackTrace();
            String exceptionMsg = String.format("testcase %s ,data provider occur exception: %s", testCaseName, e.getMessage());
            throw new DefinedException(exceptionMsg);
        }
        return objects;
    }
    /**
     * method.getDeclaringClass().getPackage().getName()
     * avoid dyn load class ,getpakcage nullpointerexception
     * @param className
     * @return
     */
    public String fromClassExtractPkg(String className) {
        if (className == null) {
            return null;
        } else {
            int index = className.lastIndexOf(Constant.DOT_PATH);
            if (index == -1) {
                return "";
            } else {
                String ext = className.substring(0,index);
                return ext;
            }
        }
    }
}
