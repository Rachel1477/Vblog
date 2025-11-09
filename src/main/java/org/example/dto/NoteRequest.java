package org.example.dto;

/**
 * 笔记请求DTO
 */
public class NoteRequest {
    private String title;
    private String content;
    private Integer status;  // 0-草稿，1-发布，2-私密

    public NoteRequest() {
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "NoteRequest{" +
                "title='" + title + '\'' +
                ", status=" + status +
                '}';
    }
}
