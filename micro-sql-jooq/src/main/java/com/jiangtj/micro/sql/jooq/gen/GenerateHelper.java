package com.jiangtj.micro.sql.jooq.gen;

import lombok.Getter;
import lombok.Setter;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;

/**
 * JOOQ 代码生成器的辅助工具类。
 */
public class GenerateHelper {

    /** 数据库连接 URL */
    static String url = null;
    /** 数据库用户名 */
    @Setter
    static String username = null;
    /** 数据库密码 */
    @Setter
    static String password = null;
    /** 数据库驱动类名 */
    @Setter
    static String driver = null;
    /** 是否生成 POJO 类型引用方法，默认为 false */
    @Setter
    @Getter
    static boolean isGeneratePojoTypeRef = false;
    /** 是否生成分页查询方法，默认为 true */
    @Setter
    @Getter
    static boolean isGeneratePageFetch = true;
    @Setter
    @Getter
    static boolean isGeneratePojoWithLombok = true;
    @Setter
    @Getter
    static boolean isGeneratePojoWithLombokBuilder = false;

    /**
     * 使用 Spring 的数据源配置初始化生成器配置。
     *
     * @param properties Spring 数据源配置对象
     */
    public static void init(DataSourceProperties properties) {
        init(properties.getUrl(), properties.getUsername(), properties.getPassword());
        driver = properties.getDriverClassName();
    }

    /**
     * 使用数据库连接信息初始化生成器配置。
     *
     * @param url      数据库连接 URL
     * @param username 数据库用户名
     * @param password 数据库密码
     */
    public static void init(String url, String username, String password) {
        GenerateHelper.url = url;
        GenerateHelper.username = username;
        GenerateHelper.password = password;
        GenerateHelper.driver = DatabaseDriver.fromJdbcUrl(url).getDriverClassName();
    }

    /**
     * 从数据库 URL 中提取 schema 名称。
     *
     * @param url 数据库连接 URL
     * @return schema 名称
     */
    public static String getSchemaFromUrl(String url) {
        return url.split("//")[1].split("/")[1].split("\\?")[0];
    }

    /**
     * 获取 JDBC 配置对象。
     *
     * @return JOOQ 的 JDBC 配置对象
     */
    public static Jdbc getJdbc() {
        return new Jdbc().withDriver(driver).withUrl(url).withUser(username).withPassword(password);
    }

    /**
     * 获取数据库配置对象。
     *
     * @param pattern 表名匹配模式
     * @return JOOQ 的数据库配置对象
     */
    public static Database getDatabase(String pattern) {
        return new Database().withIncludes(pattern).withInputSchema(getSchemaFromUrl(url));
    }

    /**
     * 获取代码生成目标配置对象。
     *
     * @param packageName 生成的代码包名
     * @return JOOQ 的代码生成目标配置对象
     */
    public static Target getTarget(String packageName) {
        return new Target().withPackageName(packageName).withDirectory("src/main/java");
    }
}
