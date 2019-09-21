package indi.vicliu.juaner.common.data.mapper;

import tk.mybatis.mapper.additional.aggregation.AggregationMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-10 18:27
 * @Description:
 */
public interface CommonRepository <T> extends Mapper<T>, InsertListMapper<T>, AggregationMapper<T> {
}
