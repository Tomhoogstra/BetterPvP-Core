package net.betterpvp.core.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Query {


    private String stmt;

    public Query(String stmt) {
        this.stmt = stmt;

    }

    public String getStatment() {
        return stmt;
    }

    public void setStatment(String stmt) {
        this.stmt = stmt;
    }


    public void execute() {
        try {

            PreparedStatement preparedStatement = Connect.getConnection().prepareStatement(getStatment());
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException ex) {
            System.out.println(stmt);
            ex.printStackTrace();

        }
    }

}
