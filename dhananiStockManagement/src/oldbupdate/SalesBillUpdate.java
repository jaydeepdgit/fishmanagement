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
        String sql = "SELECT * FROM slshd WHERE ref_no = '"+ refNo +"'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        if (rsLocal.next()) {
            String ac_cd = rsLocal.getString("ac_cd");
            String date = rsLocal.getString("vdate");
            String fix_time = rsLocal.getString("fix_time");
            int mode = rsLocal.getInt("pmode");
            int ptype = rsLocal.getInt("ptype");
            String remarks = "";
            String doc_cd = "";
            remarks = Constants.SALES_BILL_FORM_NAME;
            doc_cd = Constants.SALES_BILL_INITIAL;
            double net_amt = rsLocal.getDouble("net_amt");
            sql = "SELECT * FROM slsdt WHERE ref_no = '"+ refNo +"'";
            pstLocal = dataConnection.prepareStatement(sql);
            rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                sql = "UPDATE oldb0_1 SET pcs = pcs - ? WHERE itm_cd = ?";
                PreparedStatement psLocal = dataConnection.prepareStatement(sql);
                psLocal.setDouble(1, rsLocal.getDouble("qty"));
                psLocal.setString(2, rsLocal.getString("itm_cd"));
                psLocal.executeUpdate();

                sql = "INSERT INTO oldb0_2(doc_ref_no, doc_date, doc_nm, itm_cd, ac_cd, pcs, unt_cd, rate, trns_cd) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                psLocal = dataConnection.prepareStatement(sql);
                psLocal.setString(1, refNo);
                psLocal.setString(2, date);
                psLocal.setString(3, doc_cd);
                psLocal.setString(4, rsLocal.getString("itm_cd"));
                psLocal.setString(5, ac_cd);
                psLocal.setString(6, rsLocal.getString("qty"));
                psLocal.setString(7, rsLocal.getString("unt_cd"));
                psLocal.setString(8, rsLocal.getString("rate"));
                psLocal.setString(9, "I");
                psLocal.executeUpdate();
            }
            if(mode == 1) {
                String sqlUpdate = "INSERT INTO oldb2_2(doc_ref_no, doc_date, doc_cd, ac_cd, " +
                    "val, drcr, particular, fix_time, opp_ac_cd) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstUpdate = null;
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setString(1, refNo);
                pstUpdate.setString(2, date);
                pstUpdate.setString(3, doc_cd);
                pstUpdate.setString(4, ac_cd);
                pstUpdate.setDouble(5, net_amt);
                pstUpdate.setString(6, "0");
                pstUpdate.setString(7, remarks);
                pstUpdate.setString(8, fix_time);
                pstUpdate.setString(9, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
                pstUpdate.executeUpdate();

                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setString(1, refNo);
                pstUpdate.setString(2, date);
                pstUpdate.setString(3, doc_cd);
                pstUpdate.setString(4, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
                pstUpdate.setDouble(5, net_amt);
                pstUpdate.setString(6, "1");
                pstUpdate.setString(7, remarks);
                pstUpdate.setString(8, fix_time);
                pstUpdate.setString(9, ac_cd);
                pstUpdate.executeUpdate();

                sqlUpdate = "UPDATE oldb2_1 SET cr = cr + ? WHERE ac_cd = ?";
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setDouble(1, net_amt);
                pstUpdate.setString(2, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
                pstUpdate.executeUpdate();

                sqlUpdate = "UPDATE oldb2_1 SET dr = dr + ? WHERE ac_cd = ?";
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setDouble(1, net_amt);
                pstUpdate.setString(2, ac_cd);
                pstUpdate.executeUpdate();

                sqlUpdate = "INSERT INTO oldb2_2(doc_ref_no, doc_date, doc_cd, ac_cd, " +
                    "val, drcr, particular, fix_time, opp_ac_cd) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setString(1, refNo);
                pstUpdate.setString(2, date);
                pstUpdate.setString(3, doc_cd);
                pstUpdate.setString(4, ac_cd);
                pstUpdate.setDouble(5, net_amt);
                pstUpdate.setString(6, "1");
                pstUpdate.setString(7, Constants.CASH_RECEIPT_FORM_NAME);
                pstUpdate.setString(8, fix_time);
                pstUpdate.setString(9, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
                pstUpdate.executeUpdate();

                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setString(1, refNo);
                pstUpdate.setString(2, date);
                pstUpdate.setString(3, doc_cd);
                pstUpdate.setString(4, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
                pstUpdate.setDouble(5, net_amt);
                pstUpdate.setString(6, "0");
                pstUpdate.setString(7, Constants.CASH_RECEIPT_FORM_NAME);
                pstUpdate.setString(8, fix_time);
                pstUpdate.setString(9, ac_cd);
                pstUpdate.executeUpdate();

                sqlUpdate = "update oldb2_1 set DR = DR + ? where ac_cd = ?";
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setDouble(1, net_amt);
                pstUpdate.setString(2, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
                pstUpdate.executeUpdate();

                sqlUpdate = "UPDATE oldb2_1 SET cr = cr + ? WHERE ac_cd = ?";
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setDouble(1, net_amt);
                pstUpdate.setString(2, ac_cd);
                pstUpdate.executeUpdate();
            } else if(mode == 0 || mode == 2) {
                String sqlUpdate = "INSERT INTO oldb2_2(doc_ref_no, doc_date, doc_cd, ac_cd, " +
                    "val, drcr, particular, fix_time, opp_ac_cd) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstUpdate = null;
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setString(1, refNo);
                pstUpdate.setString(2, date);
                pstUpdate.setString(3, doc_cd);
                pstUpdate.setString(4, ac_cd);
                pstUpdate.setDouble(5, net_amt);
                pstUpdate.setString(6, "0");
                pstUpdate.setString(7, remarks);
                pstUpdate.setString(8, fix_time);
                pstUpdate.setString(9, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
                pstUpdate.executeUpdate();

                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setString(1, refNo);
                pstUpdate.setString(2, date);
                pstUpdate.setString(3, doc_cd);
                pstUpdate.setString(4, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
                pstUpdate.setDouble(5, net_amt);
                pstUpdate.setString(6, "1");
                pstUpdate.setString(7, remarks);
                pstUpdate.setString(8, fix_time);
                pstUpdate.setString(9, ac_cd);
                pstUpdate.executeUpdate();

                sqlUpdate = "UPDATE oldb2_1 SET cr = cr + ? WHERE ac_cd = ?";
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setDouble(1, net_amt);
                pstUpdate.setString(2, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
                pstUpdate.executeUpdate();

                sqlUpdate = "UPDATE oldb2_1 SET dr = dr + ? WHERE ac_cd = ?";
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setDouble(1, net_amt);
                pstUpdate.setString(2, ac_cd);
                pstUpdate.executeUpdate();
            }
            lb.setBalance(ac_cd, dataConnection);
            lb.setBalance(lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()), dataConnection);
        }
    }

    public void deleteEntry(String refNo) throws SQLException {
        String sql = "SELECT * FROM slshd WHERE ref_no = '"+ refNo +"'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        if (rsLocal.next()) {
            String ac_cd = rsLocal.getString("ac_cd");
            int mode = rsLocal.getInt("pmode");
            double net_amt = rsLocal.getDouble("net_amt");
            sql = "SELECT * FROM slsdt WHERE ref_no = '"+ refNo +"'";
            pstLocal = dataConnection.prepareStatement(sql);
            rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                sql = "UPDATE oldb0_1 SET pcs = pcs + ? WHERE itm_cd = ?";
                PreparedStatement psLocal = dataConnection.prepareStatement(sql);
                psLocal.setDouble(1, rsLocal.getDouble("qty"));
                psLocal.setString(2, rsLocal.getString("itm_cd"));
                psLocal.executeUpdate();

                sql = "DELETE FROM oldb0_2 WHERE doc_ref_no = ? AND trns_cd = ?";
                psLocal = dataConnection.prepareStatement(sql);
                psLocal.setString(1, refNo);
                psLocal.setString(2, "I");
                psLocal.executeUpdate();
            }
            String sqlUpdate = "";
            PreparedStatement pstUpdate = null;
            if(mode == 1) {
                sqlUpdate = "UPDATE oldb2_1 SET dr = dr - ? WHERE ac_cd = ?";
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setDouble(1, net_amt);
                pstUpdate.setString(2, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
                pstUpdate.executeUpdate();

                sqlUpdate = "UPDATE oldb2_1 SET cr = cr - ? WHERE ac_cd = ?";
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setDouble(1, net_amt);
                pstUpdate.setString(2, ac_cd);
                pstUpdate.executeUpdate();

                sqlUpdate = "UPDATE oldb2_1 SET cr = cr - ? WHERE ac_cd = ?";
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setDouble(1, net_amt);
                pstUpdate.setString(2, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
                pstUpdate.executeUpdate();

                sqlUpdate = "UPDATE oldb2_1 SET dr = dr - ? WHERE ac_cd = ?";
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setDouble(1, net_amt);
                pstUpdate.setString(2, ac_cd);
                pstUpdate.executeUpdate();

                sqlUpdate = "DELETE FROM oldb2_2 WHERE doc_ref_no = '"+ refNo +"'";
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.executeUpdate();
            } else if(mode == 0 || mode == 2) {
                sqlUpdate = "UPDATE oldb2_1 SET cr = cr - ? WHERE ac_cd = ?";
                pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                pstUpdate.setDouble(1, net_amt);
                pstUpdate.setString(2, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
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
            lb.setBalance(ac_cd, dataConnection);
            lb.setBalance(lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()), dataConnection);
        }
    }
}