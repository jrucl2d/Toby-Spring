package springbook.user.service.sqlService;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JsonSqlReader implements SqlReader{
    private String sqlmapFile;

    public JsonSqlReader() {
    }
    public JsonSqlReader(String sqlmapFile) {
        this.sqlmapFile = sqlmapFile;
    }

    public void setSqlmapFile(String sqlmapFile) {
        this.sqlmapFile = sqlmapFile;
    }
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
