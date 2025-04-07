package com.collectioniq.ciqschema;

import model.Column;
import model.Constraint;
import model.Schema;
import model.Table;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(classes = CiqSchemaApplication.class)
@Rollback
@FlywayTest
class CiqSchemaApplicationTests {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String SCHEMA_NAME = "ciq";
	private static final String SCHEMA_MIGRATOR_TABLE_NAME = "flyway_schema_history";
	private static Schema expectedSchema;
	// comma-separated list of tables to exclude from the evaluation
	private static final String excludeTables = "'" + SCHEMA_MIGRATOR_TABLE_NAME + "'" ;

	@BeforeAll
    public static void setup(){
		expectedSchema = buildExpectedSchema();
	}

	@Test
	public void verifySchema() {
		Schema actualSchema = getActualSchema();

		//confirm the schema is present, we've looked it up by name
		assertThat(actualSchema, notNullValue(Schema.class));

		//a specific set of tables, with specific columns and constraints should exist (we exclude a defined set)
		assertThat(actualSchema.getTables().size(), is(expectedSchema.getTables().size()));
		assertThat(actualSchema.getTables(), hasItems(expectedSchema.getTables().toArray(new Table[0])));
	}

	@Test
	void contextLoads() {
	}

	private Schema getActualSchema() {

		String schemaCriteriaSchema = " schema_name = '" + SCHEMA_NAME + "'";
		String schemaCriteria = " table_schema = '" + SCHEMA_NAME + "'";
		String tableNameCriteria = " table_name NOT IN (" + excludeTables + ")";

		String schemaSql = "select schema_name from information_schema.schemata " +
				"WHERE " + schemaCriteriaSchema + ";";

		String tablesSql = "select table_name from information_schema.tables " +
				"WHERE " + schemaCriteria +
				"AND " + tableNameCriteria + ";";

		String columnsSql = "SELECT table_name, column_name, column_default, is_nullable, " +
				"data_type, character_maximum_length " +
				"FROM information_schema.columns " +
				"WHERE " + schemaCriteria +
				"AND " + tableNameCriteria + ";";

		String constraintsSql = "SELECT constraint_name, table_name, constraint_type, enforced " +
				"FROM information_schema.table_constraints " +
				"WHERE " + schemaCriteria +
				"AND " + tableNameCriteria + ";";


		Schema schema = jdbcTemplate.queryForObject(schemaSql, new BeanPropertyRowMapper<>(Schema.class));

		//get all tables
		List<Table> tables = jdbcTemplate.query(tablesSql, new BeanPropertyRowMapper<>(Table.class));

		//get all columns
		List<Column> columns = jdbcTemplate.query(columnsSql, new BeanPropertyRowMapper<>(Column.class));

		//get all constraints
		List<Constraint> constraints = jdbcTemplate.query(constraintsSql,
				new BeanPropertyRowMapper<>(Constraint.class));

		//map columns to tables
		Map<String, List<Column>> columnsByTable = columns.stream().collect(Collectors.groupingBy(Column::getTable_name));
		tables.forEach(table -> table.setColumns(columnsByTable.getOrDefault(table.getTable_name(), new ArrayList<>())));

		//map constraints to tables
		Map<String, List<Constraint>> constraintsByTable = constraints.stream().collect(Collectors.groupingBy(Constraint::getTable_name));
		tables.forEach(table -> table.setConstraints(constraintsByTable.getOrDefault(table.getTable_name(), new ArrayList<>())));

        assert schema != null;
        schema.setTables(tables);

		return schema;
	}

	private static Schema buildExpectedSchema() {
		final String IS_NULLABLE_NO = "NO";
		final String IS_NULLABLE_YES = "YES";
		final String DATA_TYPE_VARCHAR = "varchar";
		final String DATA_TYPE_INT = "int";
		final String DATA_TYPE_TINYINT = "tinyint";
		final String DATA_TYPE_DATETIME = "datetime";
		final String DATA_TYPE_BIGINT = "bigint";
		final String DEFAULT_CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";
		final String CONSTRAINT_TYPE_UNIQUE = "UNIQUE";
		final String CONSTRAINT_TYPE_PK = "PRIMARY KEY";
		final String CONSTRAINT_ENFORCED = "YES";

		//==============================brand table ====================================================================
		final String BRAND_TABLE = "brand";

		//===== brand table columns
		Column pid = Column.builder().table_name(BRAND_TABLE).column_name("pid").column_default(null)
				.is_nullable(IS_NULLABLE_NO).data_type(DATA_TYPE_INT).character_maximum_length(null).build();

		Column brandName = Column.builder().table_name(BRAND_TABLE).column_name("name").column_default(null)
				.is_nullable(IS_NULLABLE_NO).data_type(DATA_TYPE_VARCHAR).character_maximum_length(50).build();

		Column enabled = Column.builder().table_name(BRAND_TABLE).column_name("enabled").column_default("1")
				.is_nullable(IS_NULLABLE_NO).data_type(DATA_TYPE_TINYINT).character_maximum_length(null).build();

		Column createDateTime = Column.builder().table_name(BRAND_TABLE).column_name("create_datetm")
				.column_default(DEFAULT_CURRENT_TIMESTAMP).is_nullable(IS_NULLABLE_NO).data_type(DATA_TYPE_DATETIME)
				.character_maximum_length(null).build();

		Column lastUpdateDateTime = Column.builder().table_name(BRAND_TABLE).column_name("last_update_datetm")
				.column_default(null).is_nullable(IS_NULLABLE_YES).data_type(DATA_TYPE_DATETIME)
				.character_maximum_length(null).build();

		Column deletedInd = Column.builder().table_name(BRAND_TABLE).column_name("deleted_ind").column_default("0")
				.is_nullable(IS_NULLABLE_NO).data_type(DATA_TYPE_BIGINT).character_maximum_length(null).build();

		Column createUser = Column.builder().table_name(BRAND_TABLE).column_name("create_user").column_default(null)
				.is_nullable(IS_NULLABLE_NO).data_type(DATA_TYPE_VARCHAR).character_maximum_length(50).build();

		Column lastUpdateUser = Column.builder().table_name(BRAND_TABLE).column_name("last_update_user")
				.column_default(null).is_nullable(IS_NULLABLE_YES).data_type(DATA_TYPE_VARCHAR)
				.character_maximum_length(50).build();

		Column deleteUser = Column.builder().table_name(BRAND_TABLE).column_name("delete_user").column_default(null)
				.is_nullable(IS_NULLABLE_YES).data_type(DATA_TYPE_VARCHAR).character_maximum_length(50).build();

		List<Column> brandTableColumns = List.of(pid, brandName, enabled, createDateTime, lastUpdateDateTime,
				deletedInd, createUser, lastUpdateUser, deleteUser);

		//===== brand table constraints
		Constraint brandAltKey = Constraint.builder().constraint_name("IDX_UNQ_BRAND_NAME_DEL").table_name(BRAND_TABLE)
				.constraint_type(CONSTRAINT_TYPE_UNIQUE).enforced(CONSTRAINT_ENFORCED).build();

		Constraint brandPrimaryKey = Constraint.builder().constraint_name("PRIMARY").table_name(BRAND_TABLE)
				.constraint_type(CONSTRAINT_TYPE_PK).enforced(CONSTRAINT_ENFORCED).build();

		List<Constraint> brandTableConstraints = List.of(brandAltKey, brandPrimaryKey);

		//===== build the expected brand table
		Table brandTable = Table.builder().table_name(BRAND_TABLE).columns(brandTableColumns)
				.constraints(brandTableConstraints).build();

		//===== set tables on the schema
		List<Table> tables = List.of(brandTable);
		expectedSchema = Schema.builder().schema_name(SCHEMA_NAME).tables(tables).build();

		return expectedSchema;
	}

}
