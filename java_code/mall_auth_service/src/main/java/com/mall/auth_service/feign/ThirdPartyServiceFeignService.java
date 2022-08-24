package com.mall.auth_service.feign;

import com.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("third-party-service")
public interface ThirdPartyServiceFeignService {

        @PostMapping("/sms/send")
        public R useSMS(@RequestParam("mobile") String mobile,
                        @RequestParam("value") String value);

}
