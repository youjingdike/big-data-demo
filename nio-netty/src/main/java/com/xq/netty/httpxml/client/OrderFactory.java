package com.xq.netty.httpxml.client;

import com.xq.netty.httpxml.bo.Order;

public class OrderFactory {
    public static Order create(long id) {
        Order order = new Order();
        order.setOrderNumber(id);
        order.setTotal(4534.34F);
        return order;
    }
}
