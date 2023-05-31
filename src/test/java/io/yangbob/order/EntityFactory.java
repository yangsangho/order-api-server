package io.yangbob.order;

import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.order.dto.ProductWithQuantityDto;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.Receiver;
import io.yangbob.order.domain.order.entity.order.ShippingInfo;
import io.yangbob.order.domain.product.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class EntityFactory {
    public static Member createMember() {
        return new Member("yangbob", "01012341234");
    }

    public static Order createOrder() {
        return createOrder(createMember());
    }

    public static Order createOrder(Member orderer) {
        ArrayList<ProductWithQuantityDto> productWithQuantityList = new ArrayList<>();
        Product p1 = EntityFactory.createProduct();
        Product p2 = EntityFactory.createProduct("충전기", 6700);
        productWithQuantityList.add(new ProductWithQuantityDto(p1, 1));
        productWithQuantityList.add(new ProductWithQuantityDto(p2, 4));

        return createOrder(orderer, productWithQuantityList);
    }

    public static Order createOrder(Member orderer, List<ProductWithQuantityDto> productWithQuantityList) {
        Receiver receiver = new Receiver(orderer.getName(), orderer.getPhoneNumber());
        ShippingInfo shippingInfo = new ShippingInfo(receiver, "서울특별시", "문 앞에 두세요");
        return new Order(orderer, shippingInfo, productWithQuantityList);
    }

    public static Product createProduct() {
        return new Product("선풍기", 125000);
    }

    public static Product createProduct(String name, int price) {
        return new Product(name, price);
    }
}
