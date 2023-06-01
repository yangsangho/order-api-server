package io.yangbob.order.app.api.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yangbob.order.app.api.order.reqres.ProductWithQuantityRequest;
import io.yangbob.order.app.api.order.reqres.ShippingInfoRequest;
import io.yangbob.order.app.api.order.reqres.TakeOrderRequest;
import io.yangbob.order.app.api.order.service.OrderService;
import io.yangbob.order.domain.order.entity.order.OrderId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;
import util.ConstrainedDescriptor;
import util.JsonFileReader;

import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@WebMvcTest(OrderController.class)
@AutoConfigureRestDocs
class OrderControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private OrderService orderService;

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
    void takeOrderArgumentNotValidTest(String jsonFilePath) throws Exception {
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
        ConstrainedDescriptor shippingInfoReqDescriptor = new ConstrainedDescriptor(ShippingInfoRequest.class);

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
}
