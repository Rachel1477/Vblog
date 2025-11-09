package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.Note;

import java.util.List;

/**
 * 笔记Mapper接口
 */
@Mapper
public interface NoteMapper {

    /**
     * 插入笔记
     */
    int insert(Note note);

    /**
     * 根据ID查询笔记
     */
    Note findById(@Param("id") Long id);

    /**
     * 更新笔记
     */
    int update(Note note);

    /**
     * 删除笔记
     */
    int deleteById(@Param("id") Long id);

    /**
     * 查询用户的所有笔记
     */
    List<Note> findByUserId(@Param("userId") Long userId);

    /**
     * 查询所有公开的笔记（状态为1）
     */
    List<Note> findPublicNotes();

    /**
     * 统计用户笔记数量
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 增加浏览次数
     */
    int increaseViewCount(@Param("id") Long id);
}
