package com.example.product.controller;

import com.example.common.entity.Commodity;
import com.example.product.service.mapper.CommodityMapper;
import com.example.product.service.service.CommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("product/commodities")
public class CommodityController {

    @Autowired
    private CommodityMapper commodityMapper;

    @Autowired
    private CommodityService commodityService;

    @PostMapping
    public String add(Commodity commodity){
        commodityMapper.insert(commodity);
        return "添加成功" + commodity.getId();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Integer id) {
        commodityMapper.delete(id);
        return "删除成功";
    }

    @PutMapping
    public String update(Commodity commodity) {
        commodityMapper.update(commodity);
        return "修改成功";
    }

    @GetMapping("/{id}")
    public Commodity get(@PathVariable("id") Integer id) {
        return commodityMapper.selectOne(id);
    }

    // 事务管理测试
    @PostMapping("/t_test")
    @Transactional("JtaTransactionManager")
    public String transactionManage(Commodity commodity){
        try {
            commodityService.transactionManage(commodity);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return e.getMessage();
        }
        return "添加成功" + commodity.getId();
    }
}
