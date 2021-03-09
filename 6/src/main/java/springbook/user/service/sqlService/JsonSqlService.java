package springbook.user.service.sqlService;

import com.fasterxml.jackson.databind.ObjectMapper;
import springbook.user.exception.SqlRetrievalFailureException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonSqlService implements SqlService{
    private Map<String, String> sqlMap = new HashMap<>();

    @PostConstruct // 초기화 이후 실행됨
    public void loadSql() {
        String jsonPath = "./src/main/java/springbook/user/service/sqlService/sql.json";
        ObjectMapper mapper = new ObjectMapper();
        try {
            sqlMap = mapper.readValue(new File(jsonPath), Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key);
        if(sql == null) throw new SqlRetrievalFailureException(key + "에 대한 SQL을 찾을 수 없습니다.");
        else return sql;
    }
}
