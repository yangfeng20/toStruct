package com.maple.plugs.search;

import java.util.List;

/**
 * @author yangfeng
 * @date : 2023/3/2 1:44
 * desc:
 */

public interface ClassSearcher {

    /**
     * 搜索
     *
     * @param name 名字
     * @return {@link List}<{@link Object}>
     */
    List<Object> search(String name);
}
