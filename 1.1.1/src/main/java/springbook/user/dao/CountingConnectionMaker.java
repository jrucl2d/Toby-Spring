package springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class CountingConnectionMaker implements ConnectionMaker{
    private int counter = 0;
    private ConnectionMaker realConnectionMaker;

    public CountingConnectionMaker(ConnectionMaker connectionMaker) {
        this.realConnectionMaker = connectionMaker; // DI
    }
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        this.counter += 1;
        return realConnectionMaker.makeConnection();
    }
    public int getCounter() {
        return this.counter;
    }
}
