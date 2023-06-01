package util;

import io.yangbob.order.domain.member.entity.Member;
import io.yangbob.order.domain.order.dto.ProductWithQuantityDto;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.Receiver;
import io.yangbob.order.domain.order.entity.order.ShippingInfo;
import io.yangbob.order.domain.product.entity.Product;

import java.util.List;

public class EntityFactory {
    public static Member createMember() {
        return new Member("yangbob", "01012341234");
    }

    public static Receiver createReceiver(Member member) {
        return new Receiver(member.getName(), member.getPhoneNumber());
    }

    public static ShippingInfo createShippingInfo(Member member) {
        return new ShippingInfo(createReceiver(member), "서울특별시", "문 앞에 두세요");
    }

    public static List<ProductWithQuantityDto> createProductWithQuantities() {
        Product p1 = new Product("선풍기", 125000);
        Product p2 = new Product("충전기", 7000);
        return List.of(new ProductWithQuantityDto(p1, 1), new ProductWithQuantityDto(p2, 4));
    }

    public static Order createOorder() {
        return createOorder(createMember());
    }

    public static Order createOorder(Member member) {
        return createOorder(member, createProductWithQuantities());
    }

    public static Order createOorder(Member member, List<ProductWithQuantityDto> productWithQuantities) {
        return new Order(member, createShippingInfo(member), productWithQuantities);
    }
}
