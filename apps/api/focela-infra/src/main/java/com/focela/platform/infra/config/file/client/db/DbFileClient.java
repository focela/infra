package com.focela.platform.infra.config.file.client.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.infra.domain.entity.file.FileContentEntity;
import com.focela.platform.infra.repository.mapper.file.FileContentMapper;
import com.focela.platform.infra.config.file.client.AbstractFileClient;

import java.util.Comparator;
import java.util.List;

/**
 * File client config class based on DB storage
 */
public class DbFileClient extends AbstractFileClient<DbFileClientConfig> {

    private FileContentMapper fileContentMapper;

    public DbFileClient(Long id, DbFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
        fileContentMapper = SpringUtil.getBean(FileContentMapper.class);
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        FileContentEntity fileContent = new FileContentEntity().setConfigId(getId())
                .setPath(path).setContent(content);
        fileContentMapper.insert(fileContent);
        // Build return path
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        fileContentMapper.deleteByConfigIdAndPath(getId(), path);
    }

    @Override
    public byte[] getContent(String path) {
        List<FileContentEntity> list = fileContentMapper.selectListByConfigIdAndPath(getId(), path);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        // After sorting, take the one with the largest id (i.e. the most recently uploaded)
        list.sort(Comparator.comparing(FileContentEntity::getId));
        return CollUtil.getLast(list).getContent();
    }

}
