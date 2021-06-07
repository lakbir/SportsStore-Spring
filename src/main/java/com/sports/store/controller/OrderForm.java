package com.sports.store.controller;

import com.sports.store.models.Client;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderForm {
    private Client client=new Client();
    private List<OrderProduct> products=new ArrayList<>();
}

@Data
class OrderProduct{
    private Long id;
    private int quantity;

}
