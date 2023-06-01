package io.yangbob.order.app.api.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yangbob.order.JsonFileReader;
import io.yangbob.order.app.api.order.reqres.TakeOrderRequest;
import io.yangbob.order.app.api.order.service.OrderService;
import io.yangbob.order.domain.order.entity.order.OrderId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@WebMvcTest(OrderController.class)
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
                Arguments.of("takeorderrequest/not_valid_phone")
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
        when(orderService.takeOrder(mapper.readValue(requestJson, TakeOrderRequest.class))).thenReturn(new OrderId());

        mvc.perform(
                MockMvcRequestBuilders.post("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
