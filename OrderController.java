package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.config.JwtUtil;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private JwtUtil jwtUtil;

    // 查询当前用户的订单列表
    @GetMapping
    public Result<List<Order>> list(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return Result.success(orders);
    }

    // 创建订单
    @PostMapping
    public Result<Void> create(@RequestBody Order order, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        order.setUserId(userId);
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setStatus("pending");
        boolean success = orderService.save(order);
        return success ? Result.success() : Result.error("创建订单失败");
    }

    // 查询单个订单
    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    // 更新订单
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Order order) {
        order.setId(id);
        boolean success = orderService.updateById(order);
        return success ? Result.success() : Result.error("更新失败");
    }

    // 删除订单
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = orderService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        return null;
    }
}
