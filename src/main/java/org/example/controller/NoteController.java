package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.annotation.GuestAllowed;
import org.example.annotation.RequireAuth;
import org.example.common.Result;
import org.example.dto.NoteRequest;
import org.example.entity.Note;
import org.example.service.NoteService;
import org.example.util.TokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 笔记控制器
 */
@RestController
@RequestMapping("/api/note")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private TokenHelper tokenHelper;

    /**
     * 创建笔记（需要认证）
     */
    @PostMapping("/create")
    @RequireAuth
    public Result<Note> createNote(@RequestBody NoteRequest request, 
                                   HttpServletRequest httpRequest) {
        try {
            // 从 Token 获取用户 ID
            Long userId = tokenHelper.getUidFromRequest(httpRequest);
            
            Note note = noteService.createNote(userId, request);
            return Result.success("笔记创建成功", note);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取笔记详情
     * 公开笔记游客可访问，私密笔记需要认证
     */
    @GetMapping("/{id}")
    @GuestAllowed
    public Result<Note> getNoteById(@PathVariable Long id, 
                                    HttpServletRequest request) {
        try {
            // 尝试获取当前用户ID（可能为null，游客模式）
            Long currentUserId = tokenHelper.getUidFromRequest(request);
            if (currentUserId == null) {
                currentUserId = -1L; // 游客ID
            }
            
            Note note = noteService.getNoteById(id, currentUserId);
            return Result.success(note);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新笔记（需要认证）
     */
    @PutMapping("/{id}")
    @RequireAuth
    public Result<Note> updateNote(@PathVariable Long id, 
                                   @RequestBody NoteRequest request,
                                   HttpServletRequest httpRequest) {
        try {
            Long userId = tokenHelper.getUidFromRequest(httpRequest);
            
            Note note = noteService.updateNote(id, userId, request);
            return Result.success("笔记更新成功", note);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除笔记（需要认证）
     */
    @DeleteMapping("/{id}")
    @RequireAuth
    public Result<Boolean> deleteNote(@PathVariable Long id, 
                                      HttpServletRequest httpRequest) {
        try {
            Long userId = tokenHelper.getUidFromRequest(httpRequest);
            
            boolean success = noteService.deleteNote(id, userId);
            return Result.success("笔记删除成功", success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取我的笔记列表（需要认证）
     */
    @GetMapping("/my")
    @RequireAuth
    public Result<List<Note>> getMyNotes(HttpServletRequest request) {
        try {
            Long userId = tokenHelper.getUidFromRequest(request);
            
            List<Note> notes = noteService.getUserNotes(userId);
            return Result.success("获取成功", notes);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取指定用户的笔记列表（游客可访问）
     */
    @GetMapping("/user/{userId}")
    @GuestAllowed
    public Result<List<Note>> getUserNotes(@PathVariable Long userId) {
        try {
            List<Note> notes = noteService.getUserNotes(userId);
            return Result.success(notes);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有公开笔记（游客可访问）
     */
    @GetMapping("/public")
    @GuestAllowed
    public Result<List<Note>> getPublicNotes() {
        try {
            List<Note> notes = noteService.getPublicNotes();
            return Result.success(notes);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 统计用户笔记数量
     */
    @GetMapping("/count/{userId}")
    @GuestAllowed
    public Result<Integer> countUserNotes(@PathVariable Long userId) {
        try {
            int count = noteService.countUserNotes(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
