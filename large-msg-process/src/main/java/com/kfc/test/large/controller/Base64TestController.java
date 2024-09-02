package com.kfc.test.large.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * @author: Chenkf
 * @create: 2024/9/2
 **/
@Slf4j
@RestController
@RequestMapping("/base64")
public class Base64TestController {


    @RequestMapping(value = "/test",method = {RequestMethod.POST, RequestMethod.GET})
    public void test(String str) throws Exception {
        log.info("[往MQ发送报文]{}", str);
    }


}
