package io.dynamic.threadpool.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.dynamic.threadpool.server.model.ConfigAllInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Config Info Mapper.
 *
 * @author chen.ma
 * @date 2021/6/29 22:44
 */
@Mapper
public interface ConfigInfoMapper extends BaseMapper<ConfigAllInfo> {
}
