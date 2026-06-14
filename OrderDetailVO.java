package com.example.demo.vo;

import com.example.demo.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private String address;
    private String phone;
    private String receiver;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime updateTime;
    private List<OrderItem> items;
}
