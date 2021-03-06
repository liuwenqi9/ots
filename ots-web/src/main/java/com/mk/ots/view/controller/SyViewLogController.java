package com.mk.ots.view.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mk.ots.view.service.ISyViewLogService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 钱包统一入口
 * <p/>
 * Created by nolan on 15/9/9.
 */
@Controller
@RequestMapping(value = "/sys", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class SyViewLogController {

    final Logger logger = LoggerFactory.getLogger(SyViewLogController.class);

    @Autowired
    private ISyViewLogService syViewLogService;


    @RequestMapping(value = "/viewevent")
    public ResponseEntity<Map<String, Object>> addviewevent(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultrtnMap = Maps.newHashMap();
        boolean result = false;
        try {
            request.setCharacterEncoding("UTF-8");
            int size = request.getContentLength();
            InputStream is = request.getInputStream();
            byte[] reqBodyBytes = readBytes(is, size);
            String res = new String(reqBodyBytes);
            logger.info("【sys/addviewevent】 res  is : {}", res);
            if (StringUtils.isEmpty(res)) {
                logger.error("获取目标data失败.");
                resultrtnMap.put("errcode", HttpStatus.BAD_REQUEST.value());
                resultrtnMap.put("errmsg", "获取目标data失败.");
                return new ResponseEntity<Map<String, Object>>(resultrtnMap, HttpStatus.OK);
            }
            res = res.replace("\\","");

            logger.info("【sys/addviewevent】 res  aftrer  replace  is : {}", res);

            JSONObject  jsObject = JSONObject.parseObject(res);
             String  dateStr =  jsObject.getString("data");
            logger.info("【sys/addviewevent】 dataStr is : {}", dateStr);
            if (StringUtils.isEmpty(dateStr)) {
                logger.error("获取目标data失败.");
                resultrtnMap.put("errcode", HttpStatus.BAD_REQUEST.value());
                resultrtnMap.put("errmsg", "获取目标data失败!");
                return new ResponseEntity<Map<String, Object>>(resultrtnMap, HttpStatus.OK);
            }
            JSONArray ja = JSONArray.parseArray(dateStr);
            //组织数据响应
            syViewLogService.pushSyViewLog(ja);
            result = true;

        } catch (Exception e) {
            result = false;
        } finally {
            resultrtnMap.put("success", result);
            if (result) {
                return new ResponseEntity<Map<String, Object>>(resultrtnMap, HttpStatus.OK);
            } else {
                resultrtnMap.put("errcode", HttpStatus.BAD_REQUEST.value());
                resultrtnMap.put("errmsg", "添加日志失败");
                return new ResponseEntity<Map<String, Object>>(resultrtnMap, HttpStatus.OK);
            }
        }

    }

    public static final byte[] readBytes(InputStream is, int contentLen) {
        if (contentLen > 0) {
            int readLen = 0;
            int readLengthThisTime = 0;
            byte[] message = new byte[contentLen];
            try {
                while (readLen != contentLen) {
                    readLengthThisTime = is.read(message, readLen, contentLen - readLen);
                    if (readLengthThisTime == -1) {// Should not happen.
                        break;
                    }
                    readLen += readLengthThisTime;
                }
                return message;
            } catch (IOException e) {
                // Ignore
                // e.printStackTrace();
            }
        }
        return new byte[] {};
    }

    public static void main(String []args){
            String  str = "\"{\"data\":[{\"os\":\"android\",\"model\":\"HM NOTE 1S\",\"packageName\":\"com.lezhu.mike\",\"timeStamp\":\"1451026487\",\"userId\":\"18018647025\",\"eventType\":\"1\",\"channel\":\"test\",\"deviceId\":\"99000644059292\",\"version\":\"3.3.0_60\",\"dataName\":\"m_hotel_home\"},{\"id\":\"0\",\"os\":\"android\",\"model\":\"HM NOTE 1S\",\"text\":\"一元秒杀\",\"packageName\":\"com.lezhu.mike\",\"timeStamp\":\"1451026501\",\"userId\":\"18018647025\",\"eventType\":\"2\",\"district\":\"上海市\",\"channel\":\"test\",\"deviceId\":\"99000644059292\",\"version\":\"3.3.0_60\",\"dataName\":\"e_hotel_home_tab\"},{\"os\":\"android\",\"model\":\"HM NOTE 1S\",\"packageName\":\"com.lezhu.mike\",\"timeStamp\":\"1451026533\",\"userId\":\"18018647025\",\"eventType\":\"1\",\"district\":\"上海市\",\"channel\":\"test\",\"deviceId\":\"99000644059292\",\"version\":\"3.3.0_60\",\"dataName\":\"m_hotel_home\"}]}\"";
             str = str.replace("\\\"","\"");
            if(str.startsWith("\""));
            str = str.substring(2,str.length()-2);
             System.out.println(str);
         JSONObject  jsObject = JSONObject.parseObject(str);
        String  dateStr =  jsObject.getString("data");
        System.out.println();

    }

}