/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oldbupdate;

import dhananistockmanagement.DeskFrame;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import support.Library;

/**
 *
 * @author @JD@
 */
public class BreakupBillUpdate {
    Connection dataConnection = null;
    Connection dataConMain = null;
    Library lb = null;
    
    public BreakupBillUpdate(){
        lb = new Library();
        dataConnection = DeskFrame.connMpAdmin;
        dataConMain = DeskFrame.connMpMain;
    }
    
    public void addEntry(String id) throws SQLException {
        String sql = "select * from grade_sub where id='" + id + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        while (rsLocal.next()) {
            String slab_id = rsLocal.getString("fk_slab_category_id");
            double kgs = rsLocal.getDouble("kgs");
            double block = rsLocal.getDouble("block");
            
            String sqlUpdate = "insert into stock0_2(doc_id, fk_slab_category_id, trns_id, pur) values(?, ?, ?, ?)";
            PreparedStatement pstUpdate = null;
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setString(1, id);
            pstUpdate.setString(2, slab_id);
            pstUpdate.setInt(3, 2);
            pstUpdate.setDouble(4, kgs);
            pstUpdate.executeUpdate();

            sqlUpdate = "update stock0_1 set pur = pur + ?, block = ? where fk_slab_category_id = ?";
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setDouble(1, kgs);
            pstUpdate.setDouble(2, block);
            pstUpdate.setString(3, slab_id);
            pstUpdate.executeUpdate();
        }
    }

    public void deleteEntry(String id) throws SQLException {
        String sql = "select * from grade_sub where id='" + id + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        while (rsLocal.next()) {
            String slab_id = rsLocal.getString("fk_slab_category_id");
            double kgs = rsLocal.getDouble("kgs");
            
            String sqlUpdate = "";
            PreparedStatement pstUpdate = null;
            
            sqlUpdate = "update stock0_1 set pur = pur - ? where fk_slab_category_id = ?";
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setDouble(1, kgs);
            pstUpdate.setString(2, slab_id);
            pstUpdate.executeUpdate();

            sqlUpdate = "delete from stock0_2 where fk_slab_category_id = ?";
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setString(1, slab_id);
            pstUpdate.executeUpdate();
            
        }
    }
}