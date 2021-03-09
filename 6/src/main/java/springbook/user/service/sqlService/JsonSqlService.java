package springbook.user.service.sqlService;

import com.fasterxml.jackson.databind.ObjectMapper;
import springbook.user.exception.SqlNotFoundException;
import springbook.user.exception.SqlRetrievalFailureException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonSqlService implements SqlService, SqlRegistry, SqlReader {
    private SqlReader sqlReader;
    private SqlRegistry sqlRegistry;
    private Map<String , String > sqlMap;
    private String sqlmapFile;

    public void setSqlmapFile(String sqlmapFile) {
        this.sqlmapFile = sqlmapFile;
    }

    public void setSqlReader(SqlReader sqlReader) {
        this.sqlReader = sqlReader;
    }

    public void setSqlRegistry(SqlRegistry sqlRegistry) {
        this.sqlRegistry = sqlRegistry;
    }
    // SqlService
    @PostConstruct
    public void loadSql() {
        this.sqlReader.read(this.sqlRegistry);
    }
    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        try{
            return this.sqlRegistry.findSql(key);
        } catch (SqlNotFoundException e){
            throw new SqlRetrievalFailureException(e.getMessage(), e);
        }
    }

    // SqlRegistry
    @Override
    public void registerSql(String key, String sql) {
        sqlMap.put(key, sql);
    }

    @Override
    public String findSql(String key) throws SqlNotFoundException {
        String sql = sqlMap.get(key);
        if(sql == null) throw new SqlNotFoundException(key + "에 대한 SQL을 찾을 수 없습니다.");
        else return sql;
    }

    // SqlReader
    @Override
    public void read(SqlRegistry registry) {
        String contextPath = "./src/main/java/springbook/user/service/sqlService/" + sqlmapFile;
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> tmp = mapper.readValue(new File(contextPath), Map.class);
            for(String key : tmp.keySet()){
                registry.registerSql(key, tmp.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
