package __PKG__.common

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * 未指定排序时补上默认排序（通常是主键或唯一列）。
 *
 * 分页查询必须有稳定排序，否则数据库返回顺序不确定，翻页时可能出现重复或遗漏。
 */
fun Pageable.withDefaultSort(defaultSort: Sort): Pageable {
    if (isUnpaged || sort.isSorted) {
        return this
    }
    return PageRequest.of(pageNumber, pageSize, defaultSort)
}

/**
 * 未指定排序时按 [defaultProperties] 升序排序，属性名使用 pojo 的驼峰形式。
 */
fun Pageable.withDefaultSort(vararg defaultProperties: String): Pageable =
    withDefaultSort(Sort.by(*defaultProperties))

/**
 * 内存列表分页，用于系统配置项这类非数据库来源的数据（内存实现阶段也可用于示例业务）。
 */
fun <T : Any> List<T>.toPage(pageable: Pageable): Page<T> {
    if (pageable.isUnpaged) {
        return PageImpl(this)
    }
    val from = pageable.offset.coerceAtMost(size.toLong()).toInt()
    val to = (from + pageable.pageSize).coerceAtMost(size)
    return PageImpl(subList(from, to), pageable, size.toLong())
}
