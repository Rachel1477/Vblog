package org.example.entity;

import java.time.LocalDateTime;

/**
 * 计划实体类
 */
public class Plan {
    private Long id;
    private Long userId;           // 用户ID
    /**
     * 用户名（从 user 表关联查询时填充）
     */
    private String username;
    private String title;          // 计划标题
    private String content;        // 计划内容
    private LocalDateTime planTime; // 计划时间
    private Integer status;        // 状态：0-未完成，1-已完成，2-已取消
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Plan() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", title='" + title + '\'' +
                ", planTime=" + planTime +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}

