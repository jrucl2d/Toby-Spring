package springbook.user.service.sqlService;

import springbook.user.exception.SqlRetrievalFailureException;

public interface SqlService {
    String getSql(String key) throws SqlRetrievalFailureException;
}
