package io.yangbob.order.app.api.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yangbob.order.app.api.order.reqres.completeorder.CompleteOrderRequest;
import io.yangbob.order.app.api.order.reqres.order.OrderResponse;
import io.yangbob.order.app.api.order.reqres.takeorder.ProductWithQuantityRequest;
import io.yangbob.order.app.api.order.reqres.takeorder.ShippingInfoReqRes;
import io.yangbob.order.app.api.order.reqres.takeorder.TakeOrderRequest;
import io.yangbob.order.app.api.order.service.OrderQueryService;
import io.yangbob.order.app.api.order.service.OrderService;
import io.yangbob.order.app.common.reqres.CommonPageResponse;
import io.yangbob.order.domain.order.data.OrderFilter;
import io.yangbob.order.domain.order.data.OrderSort;
import io.yangbob.order.domain.order.dto.OrdersResponseDto;
import io.yangbob.order.domain.order.entity.order.Order;
import io.yangbob.order.domain.order.entity.order.OrderId;
import io.yangbob.order.domain.order.entity.order.OrderStatus;
import io.yangbob.order.domain.payment.entity.PaymentMethod;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;
import util.ConstrainedDescriptor;
import util.EntityFactory;
import util.JsonFileReader;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(OrderController.class)
@AutoConfigureRestDocs
class OrderControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private OrderService orderService;
    @MockBean
    private OrderQueryService orderQueryService;

    private static Stream<Arguments> takeOrderArgumentNotValidTestParams() {
        return Stream.of(
                Arguments.of("takeorderrequest/not_valid_uuid"),
                Arguments.of("takeorderrequest/not_valid_quantity"),
                Arguments.of("takeorderrequest/not_valid_blank"),
                Arguments.of("takeorderrequest/not_valid_phone"),
                Arguments.of("takeorderrequest/not_valid_empty_list")
        );
    }

    @ParameterizedTest
    @MethodSource("takeOrderArgumentNotValidTestParams")
    void takeOrderNotValidTest(String jsonFilePath) throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.post("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonFileReader.read(jsonFilePath))
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        ).andExpect(
                result -> Assertions.assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class)
        );
    }

    @Test
    void takeOrderTest() throws Exception {
        String requestJson = JsonFileReader.read("takeorderrequest/expected");
        OrderId createdId = new OrderId();
        when(orderService.takeOrder(mapper.readValue(requestJson, TakeOrderRequest.class))).thenReturn(createdId);
        ConstrainedDescriptor takeOrderReqDescriptor = new ConstrainedDescriptor(TakeOrderRequest.class);
        ConstrainedDescriptor productWithQuantityReqDescriptor = new ConstrainedDescriptor(ProductWithQuantityRequest.class);
        ConstrainedDescriptor shippingInfoReqDescriptor = new ConstrainedDescriptor(ShippingInfoReqRes.class);

        mvc.perform(
                MockMvcRequestBuilders.post("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpectAll(
                MockMvcResultMatchers.status().isCreated(),
                MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"),
                MockMvcResultMatchers.jsonPath("orderId").value(createdId.toString())
        ).andDo(
                document(
                        "take-order",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                takeOrderReqDescriptor.fieldWithPath("ordererId").description("주문자 회원 ID"),
                                takeOrderReqDescriptor.fieldWithPath("productWithQuantities", "productWithQuantityRequests").description("주문 상품 목록"),
                                productWithQuantityReqDescriptor.fieldWithPath("productWithQuantities[].productId", "productId").description("상품 ID"),
                                productWithQuantityReqDescriptor.fieldWithPath("productWithQuantities[].quantity", "quantity").description("상품 수량"),
                                takeOrderReqDescriptor.fieldWithPath("shippingInfo", "shippingInfoRequest").description("배송 정보"),
                                shippingInfoReqDescriptor.fieldWithPath("shippingInfo.receiverName", "receiverName").description("받는 사람 이름"),
                                shippingInfoReqDescriptor.fieldWithPath("shippingInfo.receiverPhoneNumber", "receiverPhoneNumber").description("받는 사람 전화번호"),
                                shippingInfoReqDescriptor.fieldWithPath("shippingInfo.address", "address").description("배송 주소"),
                                shippingInfoReqDescriptor.fieldWithPath("shippingInfo.message", "message").description("배송 시 요청사항").optional()
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("접수한 주문 ID")
                        )
                )
        );
    }

    private static Stream<Arguments> completeOrderTestParams() {
        return Stream.of(
                Arguments.of("orderId", "completeorderrequest/expected", ConstraintViolationException.class),
                Arguments.of(UUID.randomUUID().toString(), "completeorderrequest/not_valid_method", HttpMessageNotReadableException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("completeOrderTestParams")
    void completeOrderNotValidTest(String orderId, String jsonFilePath, Class<?> clazz) throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.post("/orders/{order-id}", orderId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonFileReader.read(jsonFilePath))
        ).andDo(print()).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        ).andExpect(
                result -> Assertions.assertThat(result.getResolvedException()).isInstanceOf(clazz)
        );
    }

    @Test
    void completeOrderTest() throws Exception {
        String requestJson = JsonFileReader.read("completeorderrequest/expected");
        OrderId orderId = new OrderId("31111111-1111-1111-1111-111111111111");

        doNothing().when(orderService).completeOrder(orderId.toString(), mapper.readValue(requestJson, CompleteOrderRequest.class));

        ConstrainedDescriptor descriptor = new ConstrainedDescriptor(CompleteOrderRequest.class);

        mvc.perform(
                RestDocumentationRequestBuilders.post("/orders/{orderId}", orderId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andDo(
                document(
                        "complete-order",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestDocumentation.pathParameters(
                                parameterWithName("orderId").description("주문 ID")
                        ),
                        requestFields(
                                descriptor.fieldWithPath("paymentMethod").description("결제 수단, ENUM : " + Arrays.toString(PaymentMethod.values()))
                        )
                )
        );
    }

    @Test
    void findOrdersTest() throws Exception {
        Sort sort = Sort.by(OrderSort.ORDER_TIME.name()).ascending();
        Pageable pageable = PageRequest.of(0, 5, sort);
        OrderFilter filter = OrderFilter.STATUS_COMPLETED;

        List<OrdersResponseDto> dtos = List.of(
                new OrdersResponseDto("상품1 외 1건", OrderStatus.COMPLETED, "서울특별시", 10000, LocalDateTime.now()),
                new OrdersResponseDto("상품2 외 4건", OrderStatus.COMPLETED, "부산광역시", 45000, LocalDateTime.now())
        );
        CommonPageResponse<OrdersResponseDto> response = new CommonPageResponse<>(2, 0, 0, 5, dtos);
        when(orderQueryService.findOrders(pageable, filter)).thenReturn(response);

        mvc.perform(
                MockMvcRequestBuilders.get("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("page", String.valueOf(pageable.getPageNumber()))
                        .queryParam("size", String.valueOf(pageable.getPageSize()))
                        .queryParam("sort", OrderSort.ORDER_TIME.name(), "ASC")
                        .queryParam("filter", filter.toString())
        ).andDo(print()).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andDo(
                document(
                        "find-orders",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestDocumentation.queryParameters(
                                parameterWithName("page").description("주문 목록 페이지 번호").attributes(key("defaults").value("5")).optional(),
                                parameterWithName("size").description("각 페이지별 크기").attributes(key("defaults").value("0")).optional(),
                                parameterWithName("sort").description("주문 목록 정렬 정보, ENUM : " + Arrays.toString(OrderSort.values())).attributes(key("defaults").value("ORDER_TIME, ASC")).optional(),
                                parameterWithName("filter").description("주문 목록 필터 정보, ENUM : " + Arrays.toString(OrderFilter.values())).attributes(key("defaults").value("NONE")).optional()
                        ),
                        responseFields(
                                fieldWithPath("totalElementsCount").description("전체 주문 개수"),
                                fieldWithPath("totalPage").description("전체 페이지 개수"),
                                fieldWithPath("page").description("현재 페이지 번호"),
                                fieldWithPath("size").description("현재 페이지 크기"),
                                fieldWithPath("elements").description("주문 목록"),
                                fieldWithPath("elements[].representativeProductName").description("상품 대표 이름"),
                                fieldWithPath("elements[].status").description("주문 상태"),
                                fieldWithPath("elements[].address").description("배송지"),
                                fieldWithPath("elements[].totalAmount").description("최종 결제 금액"),
                                fieldWithPath("elements[].orderTime").description("주문 시간")
                        )
                )
        );
    }

    @Test
    void findOrderTest() throws Exception {
        Order order = EntityFactory.createOorder();
        OrderResponse response = OrderResponse.of(order, null);
        when(orderQueryService.findOrder(order.getId().toString())).thenReturn(response);

        mvc.perform(
                RestDocumentationRequestBuilders.get("/orders/{orderId}", order.getId().toString())
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andDo(
                document(
                        "find-order",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestDocumentation.pathParameters(
                                parameterWithName("orderId").description("주문 ID")
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("status").description("주문 상태, ENUM : " + Arrays.toString(OrderStatus.values())),
                                fieldWithPath("productWithQuantities").description("상품 정보 목록"),
                                fieldWithPath("productWithQuantities[].productId").description("상품 ID"),
                                fieldWithPath("productWithQuantities[].name").description("상품 이름"),
                                fieldWithPath("productWithQuantities[].price").description("상품 가격"),
                                fieldWithPath("productWithQuantities[].quantity").description("상품 주문 수량"),
                                fieldWithPath("amountInfo").description("결제 금액 정보. 결제 예정 금액 or 결제한 금액"),
                                fieldWithPath("amountInfo.shipping").description("배송비"),
                                fieldWithPath("amountInfo.hasDiscount").description("10% 할인 여부"),
                                fieldWithPath("amountInfo.products").description("상품 금액"),
                                fieldWithPath("amountInfo.total").description("최종 결제 금액"),
                                fieldWithPath("orderTime").description("주문 시간"),
                                fieldWithPath("ordererInfo").description("주문자 정보"),
                                fieldWithPath("ordererInfo.name").description("주문자 이름"),
                                fieldWithPath("ordererInfo.phoneNumber").description("주문자 전화번호"),
                                fieldWithPath("shippingInfo").description("배송 정보"),
                                fieldWithPath("shippingInfo.receiverName").description("배송 수령인 이름"),
                                fieldWithPath("shippingInfo.receiverPhoneNumber").description("배송 수령인 전화번호"),
                                fieldWithPath("shippingInfo.address").description("배송지 주소"),
                                fieldWithPath("shippingInfo.message").description("배송 요청 메세지").optional()
                        )
                )
        );
    }
}
