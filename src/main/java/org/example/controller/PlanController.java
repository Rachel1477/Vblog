package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.annotation.RequireAuth;
import org.example.common.Result;
import org.example.dto.PlanRequest;
import org.example.entity.Plan;
import org.example.service.PlanService;
import org.example.util.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 计划控制器
 */
@RestController
@RequestMapping("/api/plan")
public class PlanController {

    @Autowired
    private PlanService planService;

    @Autowired
    private TokenHelper tokenHelper;

    /**
     * 创建计划（需要认证）
     */
    @PostMapping("/create")
    @RequireAuth
    public Result<Plan> createPlan(@RequestBody PlanRequest request, 
                                   HttpServletRequest httpRequest) {
        try {
            // 从 Token 获取用户 ID
            Long userId = tokenHelper.getUidFromRequest(httpRequest);
            
            Plan plan = planService.createPlan(userId, request);
            return Result.success("计划创建成功", plan);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取计划详情（需要认证）
     */
    @GetMapping("/{id}")
    @RequireAuth
    public Result<Plan> getPlanById(@PathVariable Long id, 
                                    HttpServletRequest request) {
        try {
            Long currentUserId = tokenHelper.getUidFromRequest(request);
            
            Plan plan = planService.getPlanById(id, currentUserId);
            return Result.success(plan);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新计划（需要认证）
     */
    @PutMapping("/{id}")
    @RequireAuth
    public Result<Plan> updatePlan(@PathVariable Long id, 
                                   @RequestBody PlanRequest request,
                                   HttpServletRequest httpRequest) {
        try {
            Long userId = tokenHelper.getUidFromRequest(httpRequest);
            
            Plan plan = planService.updatePlan(id, userId, request);
            return Result.success("计划更新成功", plan);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除计划（需要认证）
     */
    @DeleteMapping("/{id}")
    @RequireAuth
    public Result<Boolean> deletePlan(@PathVariable Long id, 
                                      HttpServletRequest httpRequest) {
        try {
            Long userId = tokenHelper.getUidFromRequest(httpRequest);
            
            boolean success = planService.deletePlan(id, userId);
            return Result.success("计划删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取我的计划列表（需要认证）
     */
    @GetMapping("/my")
    @RequireAuth
    public Result<List<Plan>> getMyPlans(HttpServletRequest request) {
        try {
            Long userId = tokenHelper.getUidFromRequest(request);
            
            List<Plan> plans = planService.getUserPlans(userId);
            return Result.success("获取成功", plans);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据日期范围获取我的计划列表（需要认证）
     */
    @GetMapping("/my/range")
    @RequireAuth
    public Result<List<Plan>> getMyPlansByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            HttpServletRequest request) {
        try {
            Long userId = tokenHelper.getUidFromRequest(request);
            
            List<Plan> plans = planService.getUserPlansByDateRange(userId, startTime, endTime);
            return Result.success("获取成功", plans);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 统计我的计划数量（需要认证）
     */
    @GetMapping("/my/count")
    @RequireAuth
    public Result<Integer> countMyPlans(HttpServletRequest request) {
        try {
            Long userId = tokenHelper.getUidFromRequest(request);
            
            int count = planService.countUserPlans(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

