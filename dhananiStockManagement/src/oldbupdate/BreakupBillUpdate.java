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
    
    public void addEntry(String id, String doc_date) throws SQLException {
        String sql = "SELECT * FROM grade_sub WHERE id='" + id + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        while (rsLocal.next()) {
            String slab_id = rsLocal.getString("fk_slab_category_id");
            double kgs = rsLocal.getDouble("kgs");
            double block = rsLocal.getDouble("block");
            double block_used = rsLocal.getDouble("block_used");
            
            String sqlUpdate = "INSERT INTO stock0_2(doc_id, doc_date, fk_slab_category_id, trns_id, qty, block, block_used) VALUES(?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstUpdate = null;
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setString(1, id);
            pstUpdate.setString(2, doc_date);
            pstUpdate.setString(3, slab_id);
            pstUpdate.setInt(4, 4);
            pstUpdate.setDouble(5, kgs);
            pstUpdate.setDouble(6, 0.00);
            pstUpdate.setDouble(7, block_used);
            pstUpdate.executeUpdate();

            sqlUpdate = "UPDATE stock0_1 SET qty = qty + ?, block = ?, block_used = ? WHERE fk_slab_category_id = ?";
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setDouble(1, kgs);
            pstUpdate.setDouble(2, block);
            pstUpdate.setDouble(3, 0.00);
            pstUpdate.setString(4, slab_id);
            pstUpdate.executeUpdate();
        }
    }

    public void deleteEntry(String id) throws SQLException {
        String sql = "SELECT * FROM grade_sub WHERE id='" + id + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        while (rsLocal.next()) {
            String slab_id = rsLocal.getString("fk_slab_category_id");
            double kgs = rsLocal.getDouble("kgs");
            double block = rsLocal.getDouble("block");

            String sqlUpdate = "";
            PreparedStatement pstUpdate = null;

            sqlUpdate = "UPDATE stock0_1 SET qty = qty - ?, block = block - ?, block_used = ? WHERE fk_slab_category_id = ?";
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setDouble(1, kgs);
            pstUpdate.setDouble(2, block);
            pstUpdate.setDouble(3, 0.000);
            pstUpdate.setString(4, slab_id);
            pstUpdate.executeUpdate();

            sqlUpdate = "DELETE FROM stock0_2 WHERE fk_slab_category_id = ?";
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setString(1, slab_id);
            pstUpdate.executeUpdate();
        }
    }
}