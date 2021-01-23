package io.lematech.httprunner4j.handler;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lematech.httprunner4j.model.testcase.TestCase;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import java.io.InputStream;


/**
 * @author lematech@foxmail.com
 * @version 1.0.0
 * @className Handler
 * @description TODO
 * @created 2021/1/20 6:27 下午
 * @publicWechat lematech
 */
@Slf4j
public class Handler {
    private ObjectMapper objectMapper = new ObjectMapper();
    private String testCaseName;

    public Handler() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private TestCase load() {
        Yaml yaml = new Yaml(new Constructor(JSONObject.class));
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("testcases/" + this.testCaseName);
        return yaml.load(inputStream);
    }

    public void load(String fileName) {
        this.testCaseName = fileName;
        Yaml yaml = new Yaml(new Constructor(JSONObject.class));
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("testcases/" + this.testCaseName);
        JSONObject testCaseMetas = yaml.load(inputStream);
        TestCase testCase = testCaseMetas.toJavaObject(TestCase.class);

    }

    /*private void generateTestCase(JSONArray testCaseMetas) {
        TestCase testCase = new TestCase();
        List<TestStep> testSteps = new ArrayList<>();
        for (int index = 0; index < testCaseMetas.size(); index++) {
            HashMap<String, Object> testCaseMeta = testCaseMetas.getObject(index, HashMap.class);
            for (Map.Entry<String, Object> entry : testCaseMeta.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if ("config".equals(key)) {
                    Config config = objectMapper.convertValue(value, Config.class);
                    testCase.setConfig(config);
                    log.info("config：{},{},{}", config.getBaseUrl(), config.getName(), config.getVerify());
                } else if ("test".equals(key)) {
                    TestStep testStep = objectMapper.convertValue(value, TestStep.class);
                    log.info("testStep：{},{},{}", testStep.getApi(), testStep.getName(), testStep.getRequest());
                    testSteps.add(testStep);
                } else {
                    log.info("位置信息：" + key);
                }

            }
        }
    }*/
}