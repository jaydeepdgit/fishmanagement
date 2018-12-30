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
import support.Constants;
import support.Library;

/**
 *
 * @author @JD@
 */
public class SalesBillUpdate {
    Connection dataConnection = DeskFrame.connMpAdmin;
    Library lb = new Library();

    public void addEntry(String refNo) throws SQLException {
        String sql = "SELECT * FROM sale_bill_head WHERE ref_no = '"+ refNo +"'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        if (rsLocal.next()) {
            String ac_cd = rsLocal.getString("fk_account_id");
            String date = rsLocal.getString("voucher_date");
            String remarks = "";
            String doc_cd = "";
            remarks = Constants.SALES_BILL_FORM_NAME1;
            doc_cd = Constants.SALES_BILL_INITIAL;
            double net_amt = rsLocal.getDouble("net_amount");
            sql = "SELECT * FROM sale_bill_detail WHERE ref_no = '"+ refNo +"'";
            pstLocal = dataConnection.prepareStatement(sql);
            rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                sql = "UPDATE stock0_1 SET sal = sal + ? WHERE fk_slab_category_id = ?";
                PreparedStatement psLocal = dataConnection.prepareStatement(sql);
                psLocal.setDouble(1, rsLocal.getDouble("qty"));
                psLocal.setString(2, rsLocal.getString("fk_slab_category_id"));
                psLocal.executeUpdate();

                sql = "INSERT INTO stock0_2(doc_id, fk_slab_category_id, trns_id, sal) VALUES(?, ?, ?, ?)";
                psLocal = dataConnection.prepareStatement(sql);
                psLocal.setString(1, refNo);
                psLocal.setString(2, rsLocal.getString("fk_slab_category_id"));
                psLocal.setInt(3, 4);
                psLocal.setString(4, rsLocal.getString("qty"));
                psLocal.executeUpdate();
            }
            
            String sqlUpdate = "INSERT INTO oldb2_2(doc_ref_no, doc_date, doc_cd, ac_cd, val, drcr, particular) VALUES(?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstUpdate = null;
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setString(1, refNo);
            pstUpdate.setString(2, date);
            pstUpdate.setString(3, doc_cd);
            pstUpdate.setString(4, ac_cd);
            pstUpdate.setDouble(5, net_amt);
            pstUpdate.setString(6, "0");
            pstUpdate.setString(7, remarks);
            pstUpdate.executeUpdate();


            sqlUpdate = "UPDATE oldb2_1 SET dr = dr + ? WHERE ac_cd = ?";
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setDouble(1, net_amt);
            pstUpdate.setString(2, ac_cd);
            pstUpdate.executeUpdate();
        }
    }

    public void deleteEntry(String refNo) throws SQLException {
        String sql = "SELECT * FROM sale_bill_head WHERE ref_no = '"+ refNo +"'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        if (rsLocal.next()) {
            String ac_cd = rsLocal.getString("fk_account_id");
            double net_amt = rsLocal.getDouble("net_amount");
            sql = "SELECT * FROM sale_bill_detail WHERE ref_no = '"+ refNo +"'";
            pstLocal = dataConnection.prepareStatement(sql);
            rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                sql = "UPDATE stock0_1 SET sal = sal - ? WHERE fk_slab_category_id = ?";
                PreparedStatement psLocal = dataConnection.prepareStatement(sql);
                psLocal.setDouble(1, rsLocal.getDouble("qty"));
                psLocal.setString(2, rsLocal.getString("fk_slab_category_id"));
                psLocal.executeUpdate();

                
            }
                
            String sqlUpdate = "";
            PreparedStatement pstUpdate = null;
            
            sql = "DELETE FROM stock0_2 WHERE doc_id = ? AND trns_id = ?";
            pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.setString(1, refNo);
            pstUpdate.setInt(2, 4);
            pstUpdate.executeUpdate();
            
            sqlUpdate = "UPDATE oldb2_1 SET dr = dr - ? WHERE ac_cd = ?";
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setDouble(1, net_amt);
            pstUpdate.setString(2, ac_cd);
            pstUpdate.executeUpdate();

            sqlUpdate = "DELETE FROM oldb2_2 WHERE doc_ref_no = '"+ refNo +"'";
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.executeUpdate();
        }
    }
}