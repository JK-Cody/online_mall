package com.mall.sales.third_party_service.constant;

import com.mall.common.utils.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信发送的模板
 */
@Data
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
@Component
public class AliSMSTemplate {

    @Value("${spring.cloud.alicloud.sms.host}")
    private String host;
    @Value("${spring.cloud.alicloud.sms.path}")
    private String path;
    @Value("${spring.cloud.alicloud.sms.templateId}")
    private String templateId;  //短信模板Id
    @Value("${spring.cloud.alicloud.sms.appCode}")
    private String appCode;

    public String sendSmsCode(String mobile, String value){  //接收方手机号,动态验证码

        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + this.appCode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", mobile);  //必须命名为mobile
        querys.put("templateId", this.templateId);
        querys.put("value", value);
        Map<String, String> bodys = new HashMap<String, String>();
        HttpResponse response = null;
        try {
            response = HttpUtils.doPost(this.host, this.path, method, headers, querys, bodys);
            //获取response的body
            if(response.getStatusLine().getStatusCode() == 200){
                //发送成功时，{"data":{"taskId":"JS6420885281662797"},"msg":"成功","success":true,"code":200,"taskNo":"13516403432971667030"}
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //发送失败时
        assert response != null;
        return "fail_" + response.getStatusLine().getStatusCode();
    }
}
