package springbook.user.service.sqlService;

public class DefaultSqlService extends BaseSqlService{
    public DefaultSqlService() {
        setSqlReader(new JsonSqlReader());
        setSqlRegistry(new HashMapSqlRegistry());
    }
}
