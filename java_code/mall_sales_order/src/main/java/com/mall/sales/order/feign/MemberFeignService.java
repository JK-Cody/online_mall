package com.mall.sales.order.feign;

import com.mall.sales.order.vo.MemberAddressVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("mall-sales-member")
public interface MemberFeignService {

    @GetMapping("/member/memberreceiveaddress/{memberId}")
    List<MemberAddressVO> getMemberAddressList(@PathVariable("memberId") Long memberId);
}
