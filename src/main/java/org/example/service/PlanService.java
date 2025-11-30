package org.example.service;

import org.example.dto.PlanRequest;
import org.example.entity.Plan;
import org.example.mapper.PlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 计划服务层
 */
@Service
public class PlanService {

    @Autowired
    private PlanMapper planMapper;

    /**
     * 创建计划
     */
    @Transactional
    public Plan createPlan(Long userId, PlanRequest request) {
        // 参数校验
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("计划标题不能为空");
        }
        if (request.getPlanTime() == null) {
            throw new RuntimeException("计划时间不能为空");
        }

        Plan plan = new Plan();
        plan.setUserId(userId);
        plan.setTitle(request.getTitle());
        plan.setContent(request.getContent());
        plan.setPlanTime(request.getPlanTime());
        plan.setStatus(request.getStatus() != null ? request.getStatus() : 0); // 默认未完成状态

        int result = planMapper.insert(plan);
        if (result <= 0) {
            throw new RuntimeException("计划创建失败");
        }

        return plan;
    }

    /**
     * 获取计划详情
     */
    public Plan getPlanById(Long id, Long currentUserId) {
        Plan plan = planMapper.findById(id);
        if (plan == null) {
            throw new RuntimeException("计划不存在");
        }

        // 只能查看自己的计划
        if (!plan.getUserId().equals(currentUserId)) {
            throw new RuntimeException("无权限查看该计划");
        }

        return plan;
    }

    /**
     * 更新计划
     */
    @Transactional
    public Plan updatePlan(Long id, Long userId, PlanRequest request) {
        Plan plan = planMapper.findById(id);
        if (plan == null) {
            throw new RuntimeException("计划不存在");
        }

        // 权限验证：只能修改自己的计划
        if (!plan.getUserId().equals(userId)) {
            throw new RuntimeException("只能修改自己的计划");
        }

        // 参数校验
        if (request.getTitle() != null && request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("计划标题不能为空");
        }
        if (request.getPlanTime() == null) {
            throw new RuntimeException("计划时间不能为空");
        }

        // 更新字段
        if (request.getTitle() != null) {
            plan.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            plan.setContent(request.getContent());
        }
        if (request.getPlanTime() != null) {
            plan.setPlanTime(request.getPlanTime());
        }
        if (request.getStatus() != null) {
            plan.setStatus(request.getStatus());
        }

        int result = planMapper.update(plan);
        if (result <= 0) {
            throw new RuntimeException("计划更新失败");
        }

        return plan;
    }

    /**
     * 删除计划
     */
    @Transactional
    public boolean deletePlan(Long id, Long userId) {
        Plan plan = planMapper.findById(id);
        if (plan == null) {
            throw new RuntimeException("计划不存在");
        }

        // 权限验证：只能删除自己的计划
        if (!plan.getUserId().equals(userId)) {
            throw new RuntimeException("只能删除自己的计划");
        }

        int result = planMapper.deleteById(id);
        return result > 0;
    }

    /**
     * 获取用户的所有计划
     */
    public List<Plan> getUserPlans(Long userId) {
        return planMapper.findByUserId(userId);
    }

    /**
     * 根据日期范围获取用户的计划
     */
    public List<Plan> getUserPlansByDateRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return planMapper.findByUserIdAndDateRange(userId, startTime, endTime);
    }

    /**
     * 统计用户计划数量
     */
    public int countUserPlans(Long userId) {
        return planMapper.countByUserId(userId);
    }
}

