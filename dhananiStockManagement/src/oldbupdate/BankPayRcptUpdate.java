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
public class BankPayRcptUpdate {
    Connection dataConnection = DeskFrame.connMpAdmin;
    Library lb = new Library();

    public void addEntry(String refNo) throws SQLException {
        String sql = "SELECT * FROM bank_payment_receipt_head WHERE id = '"+ refNo +"'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        if (rsLocal.next()) {
            int mode = rsLocal.getInt("ctype");
            String bank_cd = rsLocal.getString("fk_bank_cd");
            String fix_time = rsLocal.getString("fix_time");
            String date = rsLocal.getString("voucher_date");

            sql = "SELECT * FROM bank_payment_receipt_details WHERE id = '"+ refNo +"'";
            pstLocal = dataConnection.prepareStatement(sql);
            rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                String remark = rsLocal.getString("remark");
                if(mode == 0) {
                    if(remark.equalsIgnoreCase("")) {
                        remark = Constants.BANK_PAYMENT_FORM_NAME;
                    }
                    String sqlUpdate = "INSERT INTO oldb2_2(doc_ref_no, doc_date, doc_cd, ac_cd, " +
                        "val, drcr, particular, fix_time, opp_ac_cd) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstUpdate = null;
                    pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                    pstUpdate.setString(1, refNo);
                    pstUpdate.setString(2, date);
                    pstUpdate.setString(3, Constants.BANK_PAYMENT_INITIAL);
                    pstUpdate.setString(4, rsLocal.getString("fk_account_master_id"));
                    pstUpdate.setString(5, rsLocal.getString("amount"));
                    pstUpdate.setString(6, "0");
                    pstUpdate.setString(7, remark);
                    pstUpdate.setString(8, fix_time);
                    pstUpdate.setString(9, bank_cd);
                    pstUpdate.executeUpdate();

                    pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                    pstUpdate.setString(1, refNo);
                    pstUpdate.setString(2, date);
                    pstUpdate.setString(3, Constants.BANK_PAYMENT_INITIAL);
                    pstUpdate.setString(4, bank_cd);
                    pstUpdate.setString(5, rsLocal.getString("amount"));
                    pstUpdate.setString(6, "1");
                    pstUpdate.setString(7, remark);
                    pstUpdate.setString(8, fix_time);
                    pstUpdate.setString(9, rsLocal.getString("fk_account_master_id"));
                    pstUpdate.executeUpdate();

                    sqlUpdate = "UPDATE oldb2_1 SET cr = cr + ? WHERE ac_cd = ?";
                    pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    pstUpdate.setString(2, bank_cd);
                    pstUpdate.executeUpdate();

                    sqlUpdate = "UPDATE oldb2_1 SET dr = dr + ? WHERE ac_cd = ?";
                    pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    pstUpdate.setString(2, rsLocal.getString("fk_account_master_id"));
                    pstUpdate.executeUpdate();
                } else {
                    if(remark.equalsIgnoreCase("")) {
                        remark = Constants.BANK_RECEIPT_FORM_NAME;
                    }
                    String sqlUpdate = "INSERT INTO oldb2_2(doc_ref_no, doc_date, doc_cd, ac_cd, " +
                        "val, drcr, particular, fix_time, opp_ac_cd) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstUpdate = null;
                    pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                    pstUpdate.setString(1, refNo);
                    pstUpdate.setString(2, date);
                    pstUpdate.setString(3, Constants.BANK_RECEIPT_INITIAL);
                    pstUpdate.setString(4, rsLocal.getString("fk_account_master_id"));
                    pstUpdate.setString(5, rsLocal.getString("amount"));
                    pstUpdate.setString(6, "1");
                    pstUpdate.setString(7, remark);
                    pstUpdate.setString(8, fix_time);
                    pstUpdate.setString(9, bank_cd);
                    pstUpdate.executeUpdate();

                    pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                    pstUpdate.setString(1, refNo);
                    pstUpdate.setString(2, date);
                    pstUpdate.setString(3, Constants.BANK_RECEIPT_INITIAL);
                    pstUpdate.setString(4, bank_cd);
                    pstUpdate.setString(5, rsLocal.getString("amount"));
                    pstUpdate.setString(6, "0");
                    pstUpdate.setString(7, remark);
                    pstUpdate.setString(8, fix_time);
                    pstUpdate.setString(9, rsLocal.getString("fk_account_master_id"));
                    pstUpdate.executeUpdate();

                    sqlUpdate = "UPDATE oldb2_1 SET dr = dr + ? WHERE ac_cd = ?";
                    pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    pstUpdate.setString(2, bank_cd);
                    pstUpdate.executeUpdate();

                    sqlUpdate = "UPDATE oldb2_1 SET cr = cr + ? WHERE ac_cd = ?";
                    pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    pstUpdate.setString(2, rsLocal.getString("fk_account_master_id"));
                    pstUpdate.executeUpdate();
                }
                lb.setBalance(rsLocal.getString("fk_account_master_id"), dataConnection);
            }
            lb.setBalance(bank_cd, dataConnection);
        }
    }

    public void deleteEntry(String refNo) throws SQLException {
        String sql = "SELECT * FROM bank_payment_receipt_head WHERE id = '" + refNo + "'";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();
        if (rsLocal.next()) {
            int mode = rsLocal.getInt("ctype");
            String bank_cd = rsLocal.getString("fk_bank_cd");
            sql = "SELECT * FROM bank_payment_receipt_details WHERE id = '"+ refNo +"'";
            pstLocal = dataConnection.prepareStatement(sql);
            rsLocal = pstLocal.executeQuery();
            while (rsLocal.next()) {
                if(mode == 0) {
                    String sqlUpdate = "DELETE FROM oldb2_2 WHERE doc_ref_no = '"+ refNo +"'";

                    PreparedStatement pstUpdate = null;
                    pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                    pstUpdate.executeUpdate();

                    sqlUpdate = "UPDATE oldb2_1 SET cr = cr - ? WHERE ac_cd = ?";
                    pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    pstUpdate.setString(2, bank_cd);
                    pstUpdate.executeUpdate();

                    sqlUpdate = "UPDATE oldb2_1 SET dr = dr - ? WHERE ac_cd = ?";
                    pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    pstUpdate.setString(2, rsLocal.getString("fk_account_master_id"));
                    pstUpdate.executeUpdate();
                } else {
                    String sqlUpdate = "DELETE FROM oldb2_2 WHERE doc_ref_no = '"+ refNo +"'";

                    PreparedStatement pstUpdate = null;
                    pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                    pstUpdate.executeUpdate();

                    sqlUpdate = "UPDATE oldb2_1 SET dr = dr - ? WHERE ac_cd = ?";
                    pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    pstUpdate.setString(2, bank_cd);
                    pstUpdate.executeUpdate();

                    sqlUpdate = "UPDATE oldb2_1 SET cr = cr - ? WHERE ac_cd = ?";
                    pstUpdate = dataConnection.prepareStatement(sqlUpdate);
                    pstUpdate.setString(1, rsLocal.getString("amount"));
                    pstUpdate.setString(2, rsLocal.getString("fk_account_master_id"));
                    pstUpdate.executeUpdate();
                }
                lb.setBalance(rsLocal.getString("fk_account_master_id"), dataConnection);
            }
            lb.setBalance(bank_cd, dataConnection);
        }
    }
}