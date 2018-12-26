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
public class PurchaseBillUpdate {
    Connection dataConnection = null;
    Connection dataConMain = null;
    Library lb = null;
    
    public PurchaseBillUpdate(){
        lb = new Library();
        dataConnection = DeskFrame.connMpAdmin;
        dataConMain = DeskFrame.connMpMain;
    }
    
    public void addEntry(String id) throws SQLException {
        String sql = "select * from purchase_bill_head where id='" + id + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        if (rsLocal.next()) {
            String ac_cd = rsLocal.getString("fk_account_master_id");
            String date = rsLocal.getString("v_date");
            double net_amt = rsLocal.getDouble("net_amt");
            
            String sqlUpdate = "insert into oldb2_2(DOC_REF_NO, DOC_DATE, DOC_CD, AC_CD,"
                + "VAL, DRCR, PARTICULAR) values(?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstUpdate = null;
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setString(1, id);
            pstUpdate.setString(2, date);
            pstUpdate.setString(3, "PB");
            pstUpdate.setString(4, ac_cd);
            pstUpdate.setDouble(5, net_amt);
            pstUpdate.setString(6, "1");
            pstUpdate.setString(7, "Purchase Bill");
            pstUpdate.executeUpdate();

            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setString(1, id);
            pstUpdate.setString(2, date);
            pstUpdate.setString(3, "PB");
            pstUpdate.setString(4, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
            pstUpdate.setDouble(5, net_amt);
            pstUpdate.setString(6, "0");
            pstUpdate.setString(7, "Purchase Bill");
            pstUpdate.executeUpdate();

            sqlUpdate = "update oldb2_1 set DR = DR + ? where AC_CD = ?";
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setDouble(1, net_amt);
            pstUpdate.setString(2, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
            pstUpdate.executeUpdate();

            sqlUpdate = "update oldb2_1 set CR = CR + ? where AC_CD = ?";
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setDouble(1, net_amt);
            pstUpdate.setString(2, ac_cd);
            pstUpdate.executeUpdate();

            //lb.setBalance(ac_cd, dataConnection);
            //lb.setBalance(lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()), dataConnection);
        }
    }

    public void deleteEntry(String id) throws SQLException {
        String sql = "select * from purchase_bill_head where id='" + id + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        if (rsLocal.next()) {
            String ac_cd = rsLocal.getString("fk_account_master_id");
            double net_amt = rsLocal.getDouble("net_amt");
            
            String sqlUpdate = "";
            PreparedStatement pstUpdate = null;
            
            sqlUpdate = "update oldb2_1 set DR = DR - ? where AC_CD = ?";
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setDouble(1, net_amt);
            pstUpdate.setString(2, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
            pstUpdate.executeUpdate();

            sqlUpdate = "update oldb2_1 set CR = CR - ? where AC_CD = ?";
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.setDouble(1, net_amt);
            pstUpdate.setString(2, ac_cd);
            pstUpdate.executeUpdate();

            sqlUpdate = "delete from oldb2_2 where doc_ref_no = '"+id+"'";
            pstUpdate = dataConnection.prepareStatement(sqlUpdate);
            pstUpdate.executeUpdate();
            
            //lb.setBalance(ac_cd, dataConnection);
            //lb.setBalance(lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()), dataConnection);
        }
    }
}