package com.jiangtj.micro.sql.jooq.gen;

import org.jooq.codegen.GeneratorStrategy;
import org.jooq.codegen.JavaWriter;
import org.jooq.meta.TableDefinition;
import org.jooq.meta.TypedElementDefinition;

import java.util.List;

public class LombokGenerator extends ExtendGenerator {

    @Override
    protected void generatePojo(TableDefinition table, JavaWriter out) {
        if (!GenerateHelper.isGeneratePojoWithLombok()) {
            super.generatePojo(table, out);
            return;
        }
        final String className = getStrategy().getJavaClassName(table, GeneratorStrategy.Mode.POJO);
        final List<String> interfaces = out.ref(getStrategy().getJavaClassImplements(table, GeneratorStrategy.Mode.POJO));
        printPackage(out, table, GeneratorStrategy.Mode.POJO);

        generatePojoClassJavadoc(table, out);
        printTableJPAAnnotation(out, table);
        out.println("@%s", out.ref("lombok.Data"));
        if (GenerateHelper.isGeneratePojoWithLombokBuilder()) {
            out.println("@%s", out.ref("lombok.Builder"));
            out.println("@%s", out.ref("lombok.NoArgsConstructor"));
            out.println("@%s", out.ref("lombok.AllArgsConstructor"));
        }
        out.println("public class %s[[before= implements ][%s]] {", className, interfaces);

        if (generateSerializablePojos() || generateSerializableInterfaces())
            out.printSerial();

        for (TypedElementDefinition<?> column : table.getColumns()) {
            out.println();
            String comment = column.getComment();
            printClassJavadoc(out, comment);
            out.println("private %s %s;",
                out.ref(getJavaType(column.getType(resolver(out, GeneratorStrategy.Mode.POJO)), out, GeneratorStrategy.Mode.POJO)),
                getStrategy().getJavaMemberName(column, GeneratorStrategy.Mode.POJO));
        }
        out.println("}");
        closeJavaWriter(out);
    }

    @Override
    protected void generateRecord(TableDefinition table, JavaWriter out) {
        super.generateRecord(table, out);
    }
}
