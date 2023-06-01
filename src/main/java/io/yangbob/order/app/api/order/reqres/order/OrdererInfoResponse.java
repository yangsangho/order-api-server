package io.yangbob.order.app.api.order.reqres.order;

import io.yangbob.order.domain.member.entity.Member;

public record OrdererInfoResponse(
        String name,
        String phoneNumber
) {
    public static OrdererInfoResponse from(Member orderer) {
        return new OrdererInfoResponse(orderer.getName(), orderer.getPhoneNumber());
    }
}
