package com.example.product.service.service.impl;

import com.example.common.entity.Commodity;
import com.example.product.mapper.ProviderMapper;
import com.example.product.service.mapper.CommodityMapper;
import com.example.product.service.service.CommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service("commodityService")
public class CommodityServiceImpl implements CommodityService {
    @Autowired
    private CommodityMapper commodityMapper;

    @Autowired
    private ProviderMapper providerMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int insert(Commodity commodity) {
        return commodityMapper.insert(commodity);
    }

    @Override
    public int delete(Integer id) {
        return commodityMapper.delete(id);
    }

    @Override
    public int update(Commodity commodity) {
        return commodityMapper.update(commodity);
    }

    @Override
    public Commodity selectOne(Integer id) {
        return commodityMapper.selectOne(id);
    }

    public void transactionManage(Commodity commodity) {
        int insert = insert(commodity);

//        int a = 100/0;
        delete(3);
//        providerMapper.delete(3);
    }
}
