package com.jiangtj.micro.sql.jooq;

import com.jiangtj.micro.sql.jooq.gen.ExtendGenerator;
import com.jiangtj.micro.sql.jooq.gen.GenerateHelper;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.*;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 使用 H2 内存数据库（MySQL 兼容模式）执行 {@code src/test/resources/test.sql}，
 * 再基于该库生成 JOOQ 代码。
 */
public class DBGenerateTest {

    /**
     * H2 内存库，MySQL 兼容模式，标识符保持小写（与 MySQL 表名一致）。
     * SYSTEM_USER 在 H2 中是关键字，需要从关键字列表中移除才能直接使用该表名。
     */
    private static final String URL = "jdbc:h2:mem:jooq_codegen;MODE=MySQL;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=SYSTEM_USER;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";
    /** 测试库 schema，生成后作为默认 schema（不参与包名） */
    private static final String SCHEMA = "system_db";

    @Test
    public void generate() throws Exception {
        initDatabase();

        GenerateHelper.init(URL, USERNAME, PASSWORD);
        GenerationTool.generate(new Configuration()
            .withJdbc(GenerateHelper.getJdbc())
            .withGenerator(new Generator()
                .withName(ExtendGenerator.class.getName())
                .withDatabase(new Database()
                    .withIncludes(".*")
                    .withInputSchema(SCHEMA)
                    .withOutputSchemaToDefault(true)
                    .withOutputCatalogToDefault(true)
                )
                .withTarget(new Target()
                    .withPackageName("com.jiangtj.micro.sql.jooq.jooq")
                    .withDirectory("src/test/java"))
                .withGenerate(new Generate()
                    .withSpatialTypes(false)
                    .withGeneratedSerialVersionUID(GeneratedSerialVersionUID.HASH)
                    .withValidationAnnotations(true)
                    .withFluentSetters(false)
                    .withPojos(true)
                    .withPojosAsJavaRecordClasses(true)
                    .withDaos(true)
                    .withSpringAnnotations(true)
                    .withDefaultSchema(false)
                )
            ));
    }

    /**
     * 初始化测试库：创建 schema 并执行 test.sql。
     */
    private void initDatabase() throws Exception {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS " + SCHEMA);
            statement.execute("SET SCHEMA " + SCHEMA);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("test.sql"));
        }
    }
}
