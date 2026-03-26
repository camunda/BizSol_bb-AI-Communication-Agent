package io.camunda.bizsol.bb.comm_agent.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

@Configuration
public class DuckDbConfig {

    @Value("${duckdb.file-path:./data/comm-agent.db}")
    private String filePath;

    /**
     * Single cached DuckDB connection. DuckDB is embedded and file-based; a single shared
     * connection avoids the overhead of opening/closing on every query. Spring calls {@code close()}
     * on shutdown via the declared {@code destroyMethod}.
     */
    @Bean(destroyMethod = "close")
    Connection duckDbConnection() throws SQLException, IOException {
        if (!filePath.isEmpty()) {
            var parent = Paths.get(filePath).getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        }
        return DriverManager.getConnection("jdbc:duckdb:" + filePath);
    }

    @Bean
    DataSource duckDbDataSource(Connection duckDbConnection) {
        // suppressClose=true: close() calls on obtained connections are no-ops so the
        // underlying cached connection stays open until context shutdown.
        return new SingleConnectionDataSource(duckDbConnection, true);
    }

    @Bean
    JdbcTemplate duckDbJdbcTemplate(DataSource duckDbDataSource) {
        JdbcTemplate jt = new JdbcTemplate(duckDbDataSource);
        jt.execute("CREATE TABLE IF NOT EXISTS records (id VARCHAR PRIMARY KEY, payload TEXT)");
        return jt;
    }
}
