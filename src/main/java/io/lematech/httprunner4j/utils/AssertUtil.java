package io.lematech.httprunner4j.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.lematech.httprunner4j.common.DefinedException;
import io.lematech.httprunner4j.entity.testcase.Comparator;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Slf4j
public class AssertUtil {
    private static Map<String,String> alisaMap = new HashMap<>();
    private static Matcher buildMatcherObj(String comparatorName,List<String> params,Object expect){
        Object obj = null;
        try {
            Class<?> clzValue = Class.forName("org.hamcrest.Matchers");
            String methodName = alisaMap.containsKey(comparatorName) ? alisaMap.get(comparatorName) : comparatorName;
            Method method = clzValue.getMethod(methodName,Class.forName(params.get(0)));
            obj = method.invoke(null,expect);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return (Matcher)obj;
    }
    public static void assertObject(Map<String, Object> objectMap) {
        Map<String,List> methodAlisaMap = comparatorAlisaMap();
        if (objectMap.containsKey("check") && objectMap.containsKey("expect")){
            Comparator comparator = JSON.parseObject(JSON.toJSONString(objectMap), Comparator.class);
            String comparatorName = comparator.getComparator();
            if(StrUtil.isEmpty(comparatorName)){
                throw new DefinedException("比较器名称不能为空");
            }
            if(!methodAlisaMap.containsKey(comparatorName)){
                throw new DefinedException(String.format("当前不支持 %s 比较器",comparatorName));
            }
            try {
                Class<?> clz = Class.forName("org.junit.Assert");
                Method method = clz.getMethod("assertThat",Object.class, Matcher.class);
                method.invoke(null,comparator.getCheck()
                        ,buildMatcherObj(comparatorName,methodAlisaMap.get(comparatorName),comparator.getExpect()));
                log.info("断言成功");
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException targetException) {
                log.info("断言失败：{}",targetException.getCause().toString());
            }
        }
        return;
    }
    public static void assertList(List<Map<String,Object>> mapList){
        for(Map<String,Object> objectMap:mapList){
            assertObject(objectMap);
        }
    }

    public static Map<String,List> comparatorAlisaMap(){
        Map<String, List> methodMap = new HashMap<>();
        try {
            Class matcherClz = Class.forName("org.hamcrest.Matchers");
            Method [] methods = matcherClz.getDeclaredMethods();
            for(Method method : methods){
                Type[] types = method.getParameterTypes();
                String methodName = method.getName();
                List<String> typeList = new ArrayList<>();
                for(Type type : types){
                    typeList.add(type.getTypeName());
                }
                methodMap.put(methodName,typeList);
                if (isSetAlisa(methodName)) {
                    String methodAlisa = transferMethodAlisa(methodName);
                    alisaMap.put(methodAlisa,methodName);
                    Integer parameterSize = typeList.size();
                    if(parameterSize == 1){
                        methodMap.put(methodAlisa,typeList);
                    }else {
                        String overrideMethodName = String.format("%s_%s",methodAlisa,parameterSize);
                        methodMap.put(overrideMethodName,typeList);
                    }
                }
            }
           // log.info("方法集：{}",JSON.toJSONString(methodMap));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return methodMap;
    }
    private static boolean isSetAlisa(String methodName){
        boolean flag = true;
        if(methodName.length() <= 5){
            flag = false;
        }
        return flag;
    }
    private static String transferMethodAlisa(String methodName){
        StringBuilder methodAlisa = new StringBuilder();
        char[] chars = methodName.toCharArray();
        for(int index=0 ;index<chars.length ; index++){
            char letter = chars[index];
            if(index == 0){
                methodAlisa.append(letter);
            }else{
                if(Character.isUpperCase(letter)){
                    methodAlisa.append(letter);
                }
            }
        }
        String simpleMethodName = methodAlisa.toString().toLowerCase();
        //log.info("方法名：{},简写：{}",methodName,simpleMethodName);
        return simpleMethodName;
    }
}
