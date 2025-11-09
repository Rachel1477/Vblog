package org.example.service;

import org.example.dto.NoteRequest;
import org.example.entity.Note;
import org.example.mapper.NoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 笔记服务层
 */
@Service
public class NoteService {

    @Autowired
    private NoteMapper noteMapper;

    /**
     * 创建笔记
     */
    @Transactional
    public Note createNote(Long userId, NoteRequest request) {
        // 参数校验
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("笔记标题不能为空");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("笔记内容不能为空");
        }

        Note note = new Note();
        note.setUserId(userId);
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setStatus(request.getStatus() != null ? request.getStatus() : 1); // 默认发布状态

        int result = noteMapper.insert(note);
        if (result <= 0) {
            throw new RuntimeException("笔记创建失败");
        }

        return note;
    }

    /**
     * 获取笔记详情
     */
    public Note getNoteById(Long id, Long currentUserId) {
        Note note = noteMapper.findById(id);
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }

        // 如果是私密笔记，只有作者本人可以查看
        if (note.getStatus() == 2 && !note.getUserId().equals(currentUserId)) {
            throw new RuntimeException("无权限查看该笔记");
        }

        // 增加浏览次数
        noteMapper.increaseViewCount(id);

        return note;
    }

    /**
     * 更新笔记
     */
    @Transactional
    public Note updateNote(Long id, Long userId, NoteRequest request) {
        Note note = noteMapper.findById(id);
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }

        // 权限验证：只能修改自己的笔记
        if (!note.getUserId().equals(userId)) {
            throw new RuntimeException("只能修改自己的笔记");
        }

        // 参数校验
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("笔记标题不能为空");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("笔记内容不能为空");
        }

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setStatus(request.getStatus() != null ? request.getStatus() : note.getStatus());

        int result = noteMapper.update(note);
        if (result <= 0) {
            throw new RuntimeException("笔记更新失败");
        }

        return note;
    }

    /**
     * 删除笔记
     */
    @Transactional
    public boolean deleteNote(Long id, Long userId) {
        Note note = noteMapper.findById(id);
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }

        // 权限验证：只能删除自己的笔记
        if (!note.getUserId().equals(userId)) {
            throw new RuntimeException("只能删除自己的笔记");
        }

        int result = noteMapper.deleteById(id);
        return result > 0;
    }

    /**
     * 获取用户的所有笔记
     */
    public List<Note> getUserNotes(Long userId) {
        return noteMapper.findByUserId(userId);
    }

    /**
     * 获取所有公开的笔记
     */
    public List<Note> getPublicNotes() {
        return noteMapper.findPublicNotes();
    }

    /**
     * 统计用户笔记数量
     */
    public int countUserNotes(Long userId) {
        return noteMapper.countByUserId(userId);
    }
}
