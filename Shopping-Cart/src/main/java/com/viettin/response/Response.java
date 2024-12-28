package com.viettin.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.viettin.dto.*;
import com.viettin.entity.Product;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private int status;
    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();

    private String token;
    private String role;
    private String expirationTime;

    private int totalPage;
    private Long totalElement;

    private AddressDto addressDto;

    private UserDto userDto;
    private List<UserDto> userDtoList;

    private CategoryDto categoryDto;
    private List<CategoryDto> categoryDtoList;

    private Product product;

    private ProductDto productDto;
    private List<ProductDto> productDtoList;

    private OrderDto orderDto;
    private List<OrderDto> orderDtoList;

    private OrderItemDto orderItemDto;
    private List<OrderItemDto> orderItemDtoList;
}
