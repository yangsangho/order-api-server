package io.yangbob.order;

import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.order.entity.Order;
import io.yangbob.order.domain.order.entity.Receiver;
import io.yangbob.order.domain.order.entity.ShippingInfo;
import io.yangbob.order.domain.product.entity.Product;

public class EntityFactory {
    public static Member createMember() {
        return new Member("yangbob", "01012341234");
    }

    public static Order createOrder(Member orderer) {
        Receiver receiver = new Receiver(orderer.getName(), orderer.getPhoneNumber());
        ShippingInfo shippingInfo = new ShippingInfo(receiver, "서울특별시", "문 앞에 두세요");
        return new Order(orderer, shippingInfo);
    }

    public static Product createProduct() {
        return new Product("선풍기", 125000);
    }

    public static Product createProduct(String name, int price) {
        return new Product(name, price);
    }
}
