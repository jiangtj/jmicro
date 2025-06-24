package com.jiangtj.micro.sql.jooq.gen;

import com.jiangtj.micro.sql.jooq.PageUtils;
import org.jooq.codegen.JavaWriter;
import org.jooq.codegen.KotlinGenerator;
import org.jooq.meta.TableDefinition;

public class ExtendKtGenerator extends KotlinGenerator {

    @Override
    protected void generateDao(TableDefinition table) {
        super.generateDao(table);
    }

    @Override
    protected void generateDaoClassFooter(TableDefinition table, JavaWriter out) {
        String pageUtils = out.ref(PageUtils.class);
        out.javadoc("Fetch pages with pageable and conditions.");
        out.print("fun fetchPage() = %s.selectFrom(ctx(), table)!!", pageUtils);
        out.println();
    }
}
