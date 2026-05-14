package com.focela.platform.framework.common.utils.object;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ArrayUtil;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.SortablePageParam;
import com.focela.platform.framework.common.model.SortingField;
import org.springframework.util.Assert;

import static java.util.Collections.singletonList;

/**
 * Utility class for {@link com.focela.platform.framework.common.model.PageParam}
 */
public class PageUtils {

    private static final Object[] ORDER_TYPES = new String[]{SortingField.ORDER_ASC, SortingField.ORDER_DESC};

    public static int getStart(PageParam pageParam) {
        return (pageParam.getPageNo() - 1) * pageParam.getPageSize();
    }

    /**
     * Build a sorting field (descending by default)
     *
     * @param func Lambda expression of the sort field
     * @param <T>  type that owns the sort field
     * @return sorting field
     */
    public static <T> SortingField buildSortingField(Func1<T, ?> func) {
        return buildSortingField(func, SortingField.ORDER_DESC);
    }

    /**
     * Build a sorting field
     *
     * @param func  Lambda expression of the sort field
     * @param order sort order, {@link SortingField#ORDER_ASC} or {@link SortingField#ORDER_DESC}
     * @param <T>   type that owns the sort field
     * @return sorting field
     */
    public static <T> SortingField buildSortingField(Func1<T, ?> func, String order) {
        Assert.isTrue(ArrayUtil.contains(ORDER_TYPES, order), String.format("Field sort type must be %s or %s", ORDER_TYPES));

        String fieldName = LambdaUtil.getFieldName(func);
        return new SortingField(fieldName, order);
    }

    /**
     * Build a default sorting field.
     * If no sorting field is set, apply the default; otherwise leave it alone.
     *
     * @param sortablePageParam sortable pagination query parameter
     * @param func              Lambda expression of the sort field
     * @param <T>               type that owns the sort field
     */
    public static <T> void buildDefaultSortingField(SortablePageParam sortablePageParam, Func1<T, ?> func) {
        if (sortablePageParam != null && CollUtil.isEmpty(sortablePageParam.getSortingFields())) {
            sortablePageParam.setSortingFields(singletonList(buildSortingField(func)));
        }
    }

}
