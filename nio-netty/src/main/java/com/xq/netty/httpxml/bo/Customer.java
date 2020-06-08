package com.xq.netty.httpxml.bo;

public class Customer {
    private long customerNum;
    private String name;

    public long getCustomerNum() {
        return customerNum;
    }

    public void setCustomerNum(long customerNum) {
        this.customerNum = customerNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerNum=" + customerNum +
                ", name='" + name + '\'' +
                '}';
    }
}
