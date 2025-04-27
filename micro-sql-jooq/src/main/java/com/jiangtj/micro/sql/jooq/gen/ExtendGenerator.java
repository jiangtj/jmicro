package com.jiangtj.micro.sql.jooq.gen;

import com.jiangtj.micro.sql.jooq.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.codegen.GeneratorStrategy;
import org.jooq.codegen.JavaGenerator;
import org.jooq.codegen.JavaWriter;
import org.jooq.meta.TableDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * JOOQ 代码生成器的扩展类，用于增强生成的代码功能。
 */
@Slf4j
public class ExtendGenerator extends JavaGenerator {

    @Override
    protected void generateTableClassFooter(TableDefinition table, JavaWriter out) {
        super.generateTableClassFooter(table, out);
        if (GenerateHelper.isGeneratePojoTypeRef())
            generatePojoTypeForTableClass(table, out);
    }

    @Override
    protected void generateDaoClassFooter(TableDefinition table, JavaWriter out) {
        super.generateDaoClassFooter(table, out);
        if (GenerateHelper.isGeneratePageFetch())
            generatePageQueryForDaoClass(table, out);
    }

    /**
     * 为表类生成获取 POJO 类型的方法。
     * <p>
     * 该方法会在表类中生成一个 getPojoType() 方法，用于获取对应的 POJO 类的 Class 对象。 这个功能需要通过
     * GenerateHelper.isGeneratePojoTypeRef() 来启用。
     * </p>
     *
     * @param table 表定义对象
     * @param out   Java 代码输出器
     */
    public void generatePojoTypeForTableClass(TableDefinition table, JavaWriter out) {
        String className = out.ref(getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO));
        out.javadoc("The class holding pojos for this type");
        printNonnullAnnotation(out);
        out.println("public %s<%s> getPojoType() {", Class.class, className);
        out.println("return %s.class;", className);
        out.println("}");
    }

    /**
     * 为 DAO 类生成分页查询方法。
     * <p>
     * 该方法会在 DAO 类中生成一个 fetchPage 方法，用于支持分页查询功能。 这个功能需要通过
     * GenerateHelper.isGeneratePageFetch() 来启用。
     * </p>
     * 
     * 生成的方法示例如下：
     * 
     * <pre>
     * public Page fetchPage(Pageable pageable, Condition... conditions) {
     *     return PageUtils.selectFrom(ctx(), getTable()).conditions(conditions).pageable(pageable)
     *             .fetchPage(getType());
     * }
     * </pre>
     *
     * @param table 表定义对象
     * @param out   Java 代码输出器
     */
    public void generatePageQueryForDaoClass(TableDefinition table, JavaWriter out) {
        String pojo = out.ref(getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO));
        String condition = out.ref(Condition.class);
        String pageable = out.ref(Pageable.class);
        String pageUtils = out.ref(PageUtils.class);
        out.javadoc("Fetch pages with pageable and conditions.");
        printNonnullAnnotation(out);
        out.println("public %s<%s> fetchPage(%s pageable, %s... conditions) {", Page.class, pojo, pageable, condition);
        out.println("return %s.selectFrom(ctx(), getTable())", pageUtils);
        out.println(".conditions(conditions)");
        out.println(".pageable(pageable)");
        out.println(".fetchPage(getType());");
        out.println("}");
    }
}
