package com.atguigu.gmall.gmalllogger.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.atguigu.gmall.common.constant.GmallConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoggerController {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("test")
    public String getTest() {
        System.out.println("11111111");
        return "success";
    }

    @PostMapping("log")
    public String doLog(@RequestParam("logString") String logString) {

        // 1 加时间戳
        JSONObject jsonObject = JSON.parseObject(logString);
        jsonObject.put("ts", System.currentTimeMillis());
        String jsonString = jsonObject.toString();
        // 2 log4j
        log.info(jsonString);

        // 3.整合kafka
        if ("startup".equals(jsonObject.getString("type"))) {
            kafkaTemplate.send(GmallConstant.KAFKA_TOPIC_STARTUP, jsonString);
        } else {
            kafkaTemplate.send(GmallConstant.KAFKA_TOPIC_EVENT, jsonString);
        }

        return "success";
    }

}
