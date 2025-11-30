package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.Image;

import java.util.List;

/**
 * 文件资源 Mapper（支持图片、视频等）
 */
@Mapper
public interface ImageMapper {

    /**
     * 插入文件记录
     */
    int insert(Image image);

    /**
     * 根据ID查询文件
     */
    Image findById(@Param("id") Long id);

    /**
     * 根据用户ID查询文件列表
     */
    List<Image> findByUserId(@Param("userId") Long userId);

    /**
     * 根据文件类型查询用户文件列表
     */
    List<Image> findByUserIdAndType(@Param("userId") Long userId, @Param("contentType") String contentType);

    /**
     * 删除文件记录
     */
    int deleteById(@Param("id") Long id);

    /**
     * 统计用户文件数量
     */
    int countByUserId(@Param("userId") Long userId);
}


