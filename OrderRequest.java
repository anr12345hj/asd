package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotBlank(message = "收货地址不能为空")
    private String address;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    @NotBlank(message = "收货人不能为空")
    private String receiver;

    private String status;

    @Valid
    @NotEmpty(message = "订单商品不能为空")
    private List<OrderItemRequest> items;
}
