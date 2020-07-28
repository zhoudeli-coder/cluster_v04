package com.example.product.service.service;

import com.example.common.entity.Commodity;

public interface CommodityService {

    int insert(Commodity commodity);

    int delete(Integer id);

    int update(Commodity commodity);

    Commodity selectOne(Integer id);

    void transactionManage(Commodity commodity);
}
