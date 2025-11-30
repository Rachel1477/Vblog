package org.example.dto;

import java.time.LocalDateTime;

/**
 * 计划请求DTO
 */
public class PlanRequest {
    private String title;
    private String content;
    private LocalDateTime planTime; // 计划时间
    private Integer status;  // 0-未完成，1-已完成，2-已取消

    public PlanRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getPlanTime() {
        return planTime;
    }

    public void setPlanTime(LocalDateTime planTime) {
        this.planTime = planTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PlanRequest{" +
                "title='" + title + '\'' +
                ", planTime=" + planTime +
                ", status=" + status +
                '}';
    }
}

