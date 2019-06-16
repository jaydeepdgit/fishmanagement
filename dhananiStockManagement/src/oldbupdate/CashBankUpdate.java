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
public class CashBankUpdate {
    Connection dataConnection = DeskFrame.connMpAdmin;
    Library lb = new Library();

    public void addEntry(String id) throws SQLException {
        String sql = "SELECT * FROM cash_payment_receipt_head WHERE id = ?";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.setString(1, id);
        ResultSet rsLocal = pstLocal.executeQuery();
        if (rsLocal.next()) {
            int mode = rsLocal.getInt("ctype");
            String bank = rsLocal.getString("fk_account_master_id");
            String fix_time = rsLocal.getString("fix_time");
            String date = rsLocal.getString("voucher_date");

            sql = "SELECT * FROM cash_payment_receipt_details WHERE id = ?";
            pstLocal = dataConnection.prepareStatement(sql);
            pstLocal.setString(1, id);
            rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                String remark = rsLocal.getString("remark");
                if (mode % 2 == 1) {
                    if(remark.equalsIgnoreCase("")) {
                        remark = Constants.CASH_PAYMENT_FORM_NAME;
                    }
                    sql = "UPDATE oldb2_1 SET dr = dr + ? WHERE ac_cd = ?";
                    PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    pstUpdate.setString(2, rsLocal.getString("fk_account_master_id"));
                    pstUpdate.executeUpdate();

                    sql = "INSERT INTO oldb2_2 (doc_ref_no, doc_cd, doc_date, ac_cd, drcr, val, particular, fix_time, opp_ac_cd) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, id);
                    pstLocal.setString(2, Constants.CASH_PAYMENT_INITIAL);
                    pstLocal.setString(3, date);
                    pstLocal.setString(4, rsLocal.getString("fk_account_master_id"));
                    pstLocal.setString(5, "0");
                    pstLocal.setString(6, rsLocal.getString("amount"));
                    pstLocal.setString(7, remark);
                    pstLocal.setString(8, fix_time);
                    pstLocal.setString(9, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
                    pstLocal.executeUpdate();

                    sql = "UPDATE oldb2_1 SET cr = cr + ? WHERE ac_cd = ?";
                    pstUpdate = dataConnection.prepareStatement(sql);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    if (bank.equalsIgnoreCase("")) {
                        pstUpdate.setString(2, lb.getData("SELECT cash_ac_cd FROM cmpny_mst"));
                    } else {
                        pstUpdate.setString(2, bank);
                    }
                    pstUpdate.executeUpdate();

                    sql = "INSERT INTO oldb2_2 (doc_ref_no, doc_cd, doc_date, ac_cd, drcr, val, particular, fix_time, opp_ac_cd) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    pstUpdate = dataConnection.prepareStatement(sql);
                    pstUpdate.setString(1, id);
                    pstUpdate.setString(2, Constants.CASH_PAYMENT_INITIAL);
                    pstUpdate.setString(3, date);
                    if (bank.equalsIgnoreCase("")) {
                        pstUpdate.setString(4, lb.getData("SELECT cash_ac_cd FROM cmpny_mst"));
                    } else {
                        pstUpdate.setString(4, bank);
                    }
                    pstUpdate.setString(5, "1");
                    pstUpdate.setString(6, rsLocal.getString("amount"));
                    pstUpdate.setString(7, remark);
                    pstUpdate.setString(8, fix_time);
                    pstUpdate.setString(9, rsLocal.getString("fk_account_master_id"));
                    pstUpdate.executeUpdate();
                } else {
                    if(remark.equalsIgnoreCase("")) {
                        remark = Constants.CASH_RECEIPT_FORM_NAME;
                    }
                    sql = "UPDATE oldb2_1 SET cr = cr + ? WHERE ac_cd = ?";
                    PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    pstUpdate.setString(2, rsLocal.getString("fk_account_master_id"));
                    pstUpdate.executeUpdate();

                    sql = "INSERT INTO oldb2_2 (doc_ref_no, doc_cd, doc_date, ac_cd, drcr, val, particular, fix_time, opp_ac_cd) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, id);
                    pstLocal.setString(2, Constants.CASH_RECEIPT_INITIAL);
                    pstLocal.setString(3, date);
                    pstLocal.setString(4, rsLocal.getString("fk_account_master_id"));
                    pstLocal.setString(5, "1");
                    pstLocal.setString(6, rsLocal.getString("amount"));
                    pstLocal.setString(7, remark);
                    pstLocal.setString(8, fix_time);
                    pstLocal.setString(9, lb.getDefaultCode("cash_ac_cd", dataConnection, DeskFrame.clSysEnv.getCMPN_NAME()));
                    pstLocal.executeUpdate();

                    sql = "UPDATE oldb2_1 SET dr = dr + ? WHERE ac_cd = ?";
                    pstUpdate = dataConnection.prepareStatement(sql);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    if (bank.equalsIgnoreCase("")) {
                        pstUpdate.setString(2, lb.getData("SELECT cash_ac_cd FROM cmpny_mst"));
                    } else {
                        pstUpdate.setString(2, bank);
                    }
                    pstUpdate.executeUpdate();

                    sql = "INSERT INTO oldb2_2 (doc_ref_no, doc_cd, doc_date, ac_cd, drcr, val, particular, fix_time, opp_ac_cd) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    pstUpdate = dataConnection.prepareStatement(sql);
                    pstUpdate.setString(1, id);
                    pstUpdate.setString(2, Constants.CASH_RECEIPT_INITIAL);
                    pstUpdate.setString(3, date);
                    if (bank.equalsIgnoreCase("")) {
                        pstUpdate.setString(4, lb.getData("SELECT cash_ac_cd FROM cmpny_mst"));
                    } else {
                        pstUpdate.setString(4, bank);
                    }
                    pstUpdate.setString(5, "0");
                    pstUpdate.setString(6, rsLocal.getString("amount"));
                    pstUpdate.setString(7, remark);
                    pstUpdate.setString(8, fix_time);
                    pstUpdate.setString(9, rsLocal.getString("fk_account_master_id"));
                    pstUpdate.executeUpdate();
                }
                lb.setBalance(rsLocal.getString("fk_account_master_id"));
                lb.setBalance(lb.getData("SELECT cash_ac_cd FROM cmpny_mst"));
            }
        }
    }

    public void deleteEntry(String id) throws SQLException {
        String sql = "SELECT * FROM cash_payment_receipt_head WHERE id = ?";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        pstLocal.setString(1, id);
        ResultSet rsLocal = pstLocal.executeQuery();
        if (rsLocal.next()) {
            int mode = rsLocal.getInt("ctype");
            String bank = rsLocal.getString("fk_account_master_id");

            sql = "SELECT * FROM cash_payment_receipt_details WHERE id = ?";
            pstLocal = dataConnection.prepareStatement(sql);
            pstLocal.setString(1, id);
            rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                if (mode % 2 == 1) {
                    sql = "UPDATE oldb2_1 SET dr = dr - ? WHERE ac_cd = ?";
                    PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    pstUpdate.setString(2, rsLocal.getString("fk_account_master_id"));
                    pstUpdate.executeUpdate();

                    sql = "delete from oldb2_2 where doc_ref_no = ?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, id);
                    pstLocal.executeUpdate();

                    sql = "UPDATE oldb2_1 SET cr = cr - ? WHERE ac_cd = ?";
                    pstUpdate = dataConnection.prepareStatement(sql);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    if (bank.equalsIgnoreCase("")) {
                        pstUpdate.setString(2, lb.getData("SELECT cash_ac_cd FROM cmpny_mst"));
                    } else {
                        pstUpdate.setString(2, bank);
                    }
                    pstUpdate.executeUpdate();
                } else {
                    sql = "UPDATE oldb2_1 SET cr = cr - ? WHERE ac_cd = ?";
                    PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    pstUpdate.setString(2, rsLocal.getString("fk_account_master_id"));
                    pstUpdate.executeUpdate();

                    sql = "DELETE FROM oldb2_2 WHERE doc_ref_no = ?";
                    pstLocal = dataConnection.prepareStatement(sql);
                    pstLocal.setString(1, id);
                    pstLocal.executeUpdate();

                    sql = "UPDATE oldb2_1 SET dr = dr - ? WHERE ac_cd = ?";
                    pstUpdate = dataConnection.prepareStatement(sql);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    if (bank.equalsIgnoreCase("")) {
                        pstUpdate.setString(2, lb.getData("SELECT cash_ac_cd FROM cmpny_mst"));
                    } else {
                        pstUpdate.setString(2, bank);
                    }
                    pstUpdate.executeUpdate();
                }
                lb.setBalance(rsLocal.getString("fk_account_master_id"));
                lb.setBalance(lb.getData("SELECT cash_ac_cd FROM cmpny_mst"));
            }
        }
    }
}