package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.Plan;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 计划Mapper接口
 */
@Mapper
public interface PlanMapper {

    /**
     * 插入计划
     */
    int insert(Plan plan);

    /**
     * 根据ID查询计划
     */
    Plan findById(@Param("id") Long id);

    /**
     * 更新计划
     */
    int update(Plan plan);

    /**
     * 删除计划
     */
    int deleteById(@Param("id") Long id);

    /**
     * 查询用户的所有计划
     */
    List<Plan> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和日期范围查询计划
     */
    List<Plan> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户计划数量
     */
    int countByUserId(@Param("userId") Long userId);
}

