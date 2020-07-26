package com.example.product.service.mapper;

import com.example.common.constant.DataSourceType;
import com.example.common.entity.Commodity;
import com.example.product.annotations.DataSource;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommodityMapper {

    @DataSource(DataSourceType.MASTER)
    int insert(Commodity commodity);

    @DataSource(DataSourceType.MASTER)
    int delete(Integer id);

    @DataSource(DataSourceType.MASTER)
    int update(Commodity commodity);

    @DataSource(DataSourceType.SLAVE)
    Commodity selectOne(Integer id);
}
