package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.entity.Image;

/**
 * 图片资源 Mapper
 */
@Mapper
public interface ImageMapper {

    /**
     * 插入图片记录
     */
    int insert(Image image);
}


