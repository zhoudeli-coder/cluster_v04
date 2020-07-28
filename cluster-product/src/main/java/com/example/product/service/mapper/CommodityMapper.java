package com.example.product.service.mapper;

import com.example.common.constant.DataSourceType;
import com.example.common.entity.Commodity;
import com.example.product.annotations.DataSourceAnnotation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommodityMapper {

    @DataSourceAnnotation(DataSourceType.MASTER)
    int insert(Commodity commodity);

    @DataSourceAnnotation(DataSourceType.SLAVE)
    int delete(Integer id);

    @DataSourceAnnotation(DataSourceType.MASTER)
    int update(Commodity commodity);

    @DataSourceAnnotation(DataSourceType.SLAVE)
    Commodity selectOne(Integer id);
}
