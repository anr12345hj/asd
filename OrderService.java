package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.dto.OrderRequest;
import com.example.demo.entity.Order;
import com.example.demo.vo.OrderDetailVO;

import java.util.List;

public interface OrderService extends IService<Order> {
    List<OrderDetailVO> getOrdersByUserId(Long userId);

    OrderDetailVO getOrderDetail(Long orderId, Long userId);

    OrderDetailVO createOrder(OrderRequest request, Long userId);

    OrderDetailVO updateOrder(Long orderId, OrderRequest request, Long userId);

    void deleteOrder(Long orderId, Long userId);
}
