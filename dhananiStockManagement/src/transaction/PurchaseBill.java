/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import dhananistockmanagement.DeskFrame;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import dhananistockmanagement.MainClass;
import java.sql.ResultSet;
import oldbupdate.PurchaseBillUpdate;
import support.Constants;
import support.HeaderIntFrame1;
import support.Library;
import support.NavigationPanel1;
import support.OurDateChooser;
import support.PickList;
import support.ReportTable;

/**
 *
 * @author @JD@
 */
public class PurchaseBill extends javax.swing.JInternalFrame {
    NavigationPanel1 navLoad = null;
    Library lb = new Library();
    DefaultTableModel dtm = null;
    private String id = "";
    private ReportTable purchaseBillView = null;
    Connection dataConnection = DeskFrame.connMpAdmin;
    private String initial = Constants.PURCHASE_BILL_INITIAL;
    private PickList mainCategoryPickList = null, subCategoryPickList = null, accountCategoryPickList = null;
    String Syspath = System.getProperty("user.dir");
    double expense = 0.00;
    double rateDollarRs = 0.00;

    /**
     * Creates new form PurchaseBill
     */
    public PurchaseBill() {
        initComponents();
        initOtherComponents();
        navLoad.setVoucher("last");
        setVisible();
    }

    public PurchaseBill(String id) {
        initComponents();
        jtxtVoucher.setText(id.substring(initial.length()));
        this.id = initial + jtxtVoucher.getText();
        initOtherComponents();
        navLoad.setVoucher("edit");
        setVisible();
    }

    private void setVisible() {
        jlblVoucherNo.setVisible(false);
        jlblStart.setVisible(false);
        jtxtVoucher.setVisible(false);
    }

    private void initOtherComponents() {
        setInitial();
        jtxtVoucher.requestFocusInWindow();
        connectToNavigation();
        lb.setDateChooserProperty(jtxtVDate);
        jlblDay.setText(lb.setDay(jtxtVDate));
        dtm = (DefaultTableModel) jTable1.getModel();
        makeChildTablePurchaseBill();
        setPickListView();
        setIconToPnael();
        jTable1.setBackground(new Color(253, 243, 243));
        setTitle(Constants.PURCHASE_BILL_FORM_NAME);
        navLoad.jbtnPrint.setVisible(false);
    }

    private void setIconToPnael() {
        Syspath += File.separator + "Resources" + File.separator + "Images" + File.separator;
        jbtnAdd.setIcon(new ImageIcon(Syspath +"Add.png"));
        jbtnClear.setIcon(new ImageIcon(Syspath +"cancel.png"));
        jbtnDelete.setIcon(new ImageIcon(Syspath +"delete.png"));
    }

    private void setInitial(){
        jlblStart.setText(initial);
    }

    private void setPickListView() {
        mainCategoryPickList = new PickList(dataConnection);

        mainCategoryPickList.setLayer(getLayeredPane());
        mainCategoryPickList.setPickListComponent(jtxtMainCategory);
        mainCategoryPickList.setNextComponent(jtxtWeight);

        subCategoryPickList = new PickList(dataConnection);

        subCategoryPickList.setLayer(getLayeredPane());
        subCategoryPickList.setPickListComponent(jtxtSubCategory);
        subCategoryPickList.setNextComponent(jtxtWeight);

        accountCategoryPickList = new PickList(dataConnection);

        accountCategoryPickList.setLayer(getLayeredPane());
        accountCategoryPickList.setPickListComponent(jtxtAccountName);
        accountCategoryPickList.setNextComponent(jtxtTalliNo);
    }

    private void makeChildTablePurchaseBill() {
        purchaseBillView = new ReportTable();

        purchaseBillView.AddColumn(0, "Voucher No", 100, java.lang.String.class, null, false);
        purchaseBillView.AddColumn(1, "Account Name", 100, java.lang.String.class, null, false);
        purchaseBillView.AddColumn(2, "Date", 100, java.lang.String.class, null, false);
        purchaseBillView.AddColumn(3, "Talli No", 100, java.lang.String.class, null, false);
        purchaseBillView.AddColumn(4, "Net Amount", 150, java.lang.Double.class, null, false);
        purchaseBillView.makeTable();
    }

    private void calculation() {
        double rate = lb.replaceAll(jtxtRate.getText());
        double rateDollar = lb.replaceAll(jtxtRateDollar.getText());
        double rateDollarRs = lb.replaceAll(jtxtRateDollarRs.getText());
        double qty = lb.replaceAll(jtxtWeight.getText());
        double amt = 0.00;
        double amtDollar = 0.00;
        if(rateDollarRs > 0) {
            if(rate > 0) {
                rateDollar = rate / rateDollarRs;
            } else if(rateDollar > 0) {
                rate = rateDollar * rateDollarRs;
            }    
        }
        
        amtDollar = (qty * rateDollar);
        amt = (qty * rate);
        
        jtxtWeight.setText(lb.Convert2DecFmt(qty));
        jtxtRateDollarRs.setText(lb.getIndianFormat(rateDollarRs));
        
        jtxtRate.setText(lb.getIndianFormat(rate));
        jtxtAmount.setText(lb.getIndianFormat(amt));
        
        jtxtRateDollar.setText(lb.getIndianFormat(rateDollar));
        jtxtAmountDollar.setText(lb.getIndianFormat(amtDollar));
    }

    private boolean reValidate(){
        boolean flag = true;

        if(lb.isBlank(jtxtMainCategory)) {
            navLoad.setMessage(Constants.INVALID_MAIN_CATEGORY);
            jtxtMainCategory.requestFocusInWindow();
            flag = flag && false;
        }

        if(lb.replaceAll(jtxtWeight.getText()) < 0) {
            navLoad.setMessage(Constants.INVALID_QTY);
            jtxtWeight.requestFocusInWindow();
            flag = flag && false;
        }

        if(lb.replaceAll(jtxtRate.getText()) < 0) {
            navLoad.setMessage(Constants.INVALID_RATE);
            jtxtRate.requestFocusInWindow();
            flag = flag && false;
        }
        
        if(lb.replaceAll(jtxtRateDollar.getText()) < 0) {
            navLoad.setMessage(Constants.INVALID_RATE);
            jtxtRateDollar.requestFocusInWindow();
            flag = flag && false;
        }

        if (!lb.checkDate(jtxtVDate)) {
            flag = flag && false;
            navLoad.setMessage(Constants.INVALID_DATE);
            jtxtVDate.requestFocusInWindow();
        }
        return flag;
    }

    private boolean validateData() {
        boolean flag = true;
        if(!lb.checkDate(jtxtVDate)) {
            navLoad.setMessage(Constants.INVALID_VOUCHER_DATE);
            jtxtVDate.requestFocusInWindow();
            flag = false;
        }
        return flag;
    }

    private void close() {
        this.dispose();
    }

    private void connectToNavigation() {
        class navPanel extends NavigationPanel1 {
            @Override
            public void callSave() throws Exception {
                valueUpdateToDatabase(false);
            }

            @Override
            public void callDelete() throws Exception {
                navLoad.setMessage("");
                setComponentEditable(true);
                int ans = JOptionPane.showConfirmDialog(null, Constants.DELETE_THIS + "voucher no. "+ id, "Confirm Delete", JOptionPane.OK_CANCEL_OPTION);
                if (ans == JOptionPane.OK_OPTION) {
                    try {
                        dataConnection.setAutoCommit(false);
                        PurchaseBillUpdate pb = new PurchaseBillUpdate();
                        pb.deleteEntry(id);
                        
                        delete();
                        dataConnection.commit();
                        setVoucher("last");
                        dataConnection.setAutoCommit(true);
                    } catch (Exception ex) {
                        try {
                            lb.printToLogFile("Error at delete In Purchase Bill", ex);
                            dataConnection.rollback();
                            dataConnection.setAutoCommit(true);
                        } catch (SQLException ex1) {
                            lb.printToLogFile("Error at rollback delete In Purchase Bill", ex1);
                        }
                    }
                } else {
                    setComponentEditable(false);
                }
            }

            @Override
            public void callView() {
                onviewVoucher();
            }

            @Override
            public void callPrint() {
                onPrintVoucher();
            }

            @Override
            public void callClose() {
                close();
            }

            @Override
            public void setVoucher(String tag) {
                try {
                    navLoad.setComponentEditable(false);
                    String sql = "SELECT * FROM purchase_bill_head";
                    if (tag.equalsIgnoreCase("first")) {
                        sql += " WHERE id = (SELECT MIN(id) FROM purchase_bill_head)";
                    } else if (tag.equalsIgnoreCase("previous")) {
                        sql += " WHERE id = (SELECT MAX(id) FROM purchase_bill_head WHERE id < '"+ id +"')";
                    } else if (tag.equalsIgnoreCase("next")) {
                        sql += " WHERE id = (SELECT MIN(id) FROM purchase_bill_head WHERE id > '"+ id +"')";
                    } else if (tag.equalsIgnoreCase("last")) {
                        sql += " WHERE id = (SELECT MAX(id) FROM purchase_bill_head)";
                    } else if (tag.equalsIgnoreCase("edit")) {
                        sql += " WHERE id = '"+ id +"'";
                    }
                    navLoad.viewDataRs = navLoad.fetchData(sql);
                    if (navLoad.viewDataRs.next()) {
                        navLoad.setComponentTextFromRs();
                    } else {
                        if(tag.equalsIgnoreCase("last")) {
                            navLoad.setComponentText();
                        }
                    }
                    navLoad.setComponentEditable(false);
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setVoucher In Purchase Bill", ex);
                }
                lb.setPermission(navLoad, Constants.PURCHASE_BILL_FORM_ID);
                setTextfieldsAtBottom();
            }

            @Override
            public void setComponentText() {
                dtm.setRowCount(0);
                jtxtVoucher.setText("");
                jtxtAccountName.setText("");
                jtxtTalliNo.setText("");
                jtxtRateDollarRs.setText("0.00");
                jtxtAcExpense.setText("0.00");
                jtxtDescription.setText("");
                jtxtExpense.setText("0.00");
                jtxtOtherExpense.setText("0.00");
                jtxtNetAmt.setText("0.00");
                jlblWeight.setText("0.00");
                jlblTotalAmt.setText("0.00");
                jlblTotalAmtDollar.setText("0.00");
                lb.setDateChooserProperty(jtxtVDate);
                jtxtVDate.requestFocusInWindow();
                jtxtVDate.selectAll();
            }

            @Override
            public void updateOperationToDatabase(boolean bPrepareStatement) {
            }

            @Override
            public boolean validateForm() {
                return validateData();
            }

            @Override
            public void setComponentEditable(boolean bFlag) {
                jtxtVDate.setEnabled(bFlag);
                jtxtVoucher.setEnabled(!bFlag);
                jtxtAccountName.setEnabled(bFlag);
                jtxtTalliNo.setEnabled(bFlag);
                jtxtRateDollarRs.setEnabled(bFlag);
                jtxtAcExpense.setEnabled(bFlag);
                jtxtDescription.setEnabled(bFlag);
                jTable1.setEnabled(bFlag);
                jtxtMainCategory.setEnabled(bFlag);
                jtxtSubCategory.setEnabled(bFlag);
                jtxtWeight.setEnabled(bFlag);
                jtxtRate.setEnabled(bFlag);
                jtxtAmount.setEnabled(false);
                jtxtRateDollar.setEnabled(bFlag);
                jtxtAmountDollar.setEnabled(false);
                jtxtExpense.setEnabled(bFlag);
                jtxtOtherExpense.setEnabled(bFlag);
                jtxtNetAmt.setEnabled(false);
                jbtnAdd.setEnabled(bFlag);
                jtxtVDate.requestFocusInWindow();
                jtxtVDate.selectAll();
            }

            public int valueUpdateToDatabase(boolean bPrepareStatement) {
                try {
                    dataConnection.setAutoCommit(false);
                    updateLabel();
                    saveVoucher();
                    dataConnection.commit();
                    dataConnection.setAutoCommit(true);
                    if (navLoad.getMode().equalsIgnoreCase("N")) {
                        setVoucher("last");
                    } else if (navLoad.getMode().equalsIgnoreCase("E")) {
                        setVoucher("Edit");
                    }
                } catch (Exception ex) {
                    try {
                        dataConnection.rollback();
                        dataConnection.setAutoCommit(true);
                        lb.printToLogFile("Exception at saveVoucher In Purchase Bill", ex);
                    } catch (SQLException ex1) {
                        lb.printToLogFile("Exception at saveVoucher rollback In Purchase Bill", ex1);
                    }
                }
                return 1;
            }

            @Override
            public void setComponentTextFromRs() {
                try {
                    id = viewDataRs.getString("id");
                    jtxtVoucher.setText(id.substring(initial.length()));
                    jtxtVDate.setText(lb.ConvertDateFormetForDBForConcurrency(viewDataRs.getString("v_date")));
                    jlblDay.setText(lb.setDay(jtxtVDate));
                    jlblWeight.setText(lb.Convert2DecFmt(viewDataRs.getDouble("total_weight")));
                    jlblTotalAmt.setText(lb.getIndianFormat(viewDataRs.getDouble("total_amount")));
                    jlblTotalAmtDollar.setText(lb.getIndianFormat(viewDataRs.getDouble("total_amount_dollar")));
                    jtxtExpense.setText(lb.getIndianFormat(viewDataRs.getDouble("total_expense")));
                    expense = viewDataRs.getDouble("expense");
                    jtxtOtherExpense.setText(lb.getIndianFormat(viewDataRs.getDouble("other_expense")));
                    jtxtNetAmt.setText(lb.getIndianFormat(viewDataRs.getDouble("net_amt")));
                    jtxtDescription.setText(viewDataRs.getString("description"));
                    jtxtTalliNo.setText(viewDataRs.getString("talli_no"));
                    jtxtAcExpense.setText(lb.getIndianFormat(expense));
                    jtxtRateDollarRs.setText(lb.getIndianFormat(viewDataRs.getDouble("rate_dollar_rs")));
                    jtxtAccountName.setText(lb.getAccountMstName(viewDataRs.getString("fk_account_master_id"), "N"));
                    jlblUser.setText(lb.getUserName(viewDataRs.getString("user_cd"), "N"));
                    jlblEditNo.setText(viewDataRs.getString("edit_no"));
                    jlblTimeStamp.setText(lb.ConvertTimeStampFormetForDisplay(viewDataRs.getString("time_stamp")));

                    dtm.setRowCount(0);
                    viewDataRs = fetchData("SELECT * FROM purchase_bill_details WHERE id = '"+ id +"'");
                    int i = 0;
                    while (viewDataRs.next()) {
                        Vector row = new Vector();
                        row.add(++i);
                        row.add(lb.getMainCategory(viewDataRs.getString("fk_main_category_id"), "N"));
                        row.add(lb.getSubCategory(viewDataRs.getString("fk_sub_category_id"), "N"));
                        row.add(lb.Convert2DecFmt(viewDataRs.getDouble("weight")));
                        row.add(lb.getIndianFormat(viewDataRs.getDouble("rate")));
                        row.add(lb.getIndianFormat(viewDataRs.getDouble("amount")));
                        row.add(lb.getIndianFormat(viewDataRs.getDouble("rate_dollar")));
                        row.add(lb.getIndianFormat(viewDataRs.getDouble("amount_dollar")));
                        dtm.addRow(row);
                    }
                    updateLabel();
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setComponentTextFromRs In Purchase Bill", ex);
                }
            }
        }
        navLoad = new navPanel();
        jPanel2.add(navLoad);
        navLoad.setVisible(true);
    }

    private void onPrintVoucher() {
        PopUpPrintType ds = new PopUpPrintType(MainClass.df, true, id, initial, Constants.PURCHASE_BILL_FORM_NAME, 0);
        ds.show();
    }

    public void setID(String id) {
        this.id = id;
        navLoad.setVoucher("edit");
    }

    private void onviewVoucher() {
        this.dispose();

        String sql = "SELECT pb.id, am.name, pb.v_date, pb.talli_no, pb.net_amt FROM purchase_bill_head pb, account_master am "
            +"WHERE pb.fk_account_master_id = am.id ORDER BY pb.id";
        purchaseBillView.setColumnValue(new int[]{1, 2, 3, 4, 5});
        String view_title = Constants.PURCHASE_BILL_FORM_NAME +" VIEW";

        HeaderIntFrame1 rptDetail = new HeaderIntFrame1(dataConnection, id, view_title, purchaseBillView, sql, Constants.PURCHASE_BILL_FORM_ID, 1, this, this.getTitle());
        rptDetail.makeView();
        rptDetail.setVisible(true);

        Component c = DeskFrame.tabbedPane.add(view_title, rptDetail);
        c.setName(view_title);
        DeskFrame.tabbedPane.setSelectedComponent(c);
    }

    @Override
    public void dispose() {
        try {
            DeskFrame.removeFromScreen(DeskFrame.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose In Purchase Bill", ex);
        }
    }

    private int saveVoucher() throws SQLException, ParseException, FileNotFoundException, IOException {
        String sql = null;

        PreparedStatement psLocal = null;
        int change = 0;
        if (navLoad.getMode().equalsIgnoreCase("N")) {
            sql = "INSERT INTO purchase_bill_head (fk_account_master_id, v_date, expense, total_expense, other_expense, net_amt, total_weight, total_amount, total_amount_dollar, description, fix_time, user_cd, talli_no, rate_dollar_rs, id) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '"+ new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) +"', ?, ?, ?, ?)";
            id = lb.generateKey("purchase_bill_head", "id", 8, initial); // GENERATE REF NO
        } else if (navLoad.getMode().equalsIgnoreCase("E")) {
            PurchaseBillUpdate pb = new PurchaseBillUpdate();
            pb.deleteEntry(id);
            
            sql = "DELETE FROM purchase_bill_details WHERE id='"+ id +"'";
            psLocal = dataConnection.prepareStatement(sql);
            change += psLocal.executeUpdate();

            sql = "UPDATE purchase_bill_head SET fk_account_master_id = ?, v_date = ?, expense = ?, total_expense = ?, other_expense = ?, net_amt = ?, total_weight = ?, total_amount = ?, total_amount_dollar = ?, description = ?, fix_time = '"+ new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) 
                +"', user_cd = ?, talli_no=?, rate_dollar_rs = ?, edit_no = edit_no + 1, time_stamp = CURRENT_TIMESTAMP WHERE id = ?";
        }
        psLocal = dataConnection.prepareStatement(sql);
        psLocal.setString(1, lb.getAccountMstName(jtxtAccountName.getText(), "C")); // fk_account_master_id
        psLocal.setString(2, lb.tempConvertFormatForDBorConcurrency(jtxtVDate.getText())); // v_date
        psLocal.setDouble(3, expense); // expense
        psLocal.setDouble(4, lb.replaceAll(jtxtExpense.getText())); // total_expense
        psLocal.setDouble(5, lb.replaceAll(jtxtOtherExpense.getText())); // other_expense
        psLocal.setDouble(6, lb.replaceAll(jtxtNetAmt.getText())); // net_amt
        psLocal.setDouble(7, lb.replaceAll(jlblWeight.getText())); // total_weight
        psLocal.setDouble(8, lb.replaceAll(jlblTotalAmt.getText())); // total_amount
        psLocal.setDouble(9, lb.replaceAll(jlblTotalAmtDollar.getText())); // total_amount
        psLocal.setString(10, jtxtDescription.getText()); // description
        psLocal.setInt(11, DeskFrame.user_id); // user_cd
        psLocal.setString(12, jtxtTalliNo.getText()); // taali_no
        psLocal.setDouble(13, lb.replaceAll(jtxtRateDollarRs.getText())); // dolare rate
        psLocal.setString(14, id); // REF NO
        change += psLocal.executeUpdate();

        sql = "INSERT INTO purchase_bill_details (sr_no, fk_main_category_id, fk_sub_category_id, weight, rate, amount, rate_dollar, amount_dollar, id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        psLocal = dataConnection.prepareStatement(sql);
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            String fkMainCategoryName = jTable1.getValueAt(i, 1).toString(); // fk_main_category_id
            String fkSubCategoryName = jTable1.getValueAt(i, 2).toString(); // fk_sub_category_id
            double weight = lb.replaceAll(jTable1.getValueAt(i, 3).toString()); // weight
            double rate = lb.replaceAll(jTable1.getValueAt(i, 4).toString()); // rate
            double amount = lb.replaceAll(jTable1.getValueAt(i, 5).toString()); // amount
            double rateDollar = lb.replaceAll(jTable1.getValueAt(i, 6).toString()); // rate dollar
            double amountDollar = lb.replaceAll(jTable1.getValueAt(i, 7).toString()); // amount dollar
            String fkMainCategoryId = lb.getMainCategory(fkMainCategoryName, "C");
            String fkSubCategoryId = lb.getSubCategory(fkSubCategoryName, "C");
            if(!(fkMainCategoryId.equalsIgnoreCase("0") || fkMainCategoryId.equalsIgnoreCase(""))) {
                psLocal.setInt(1, i + 1); // sr_no
                psLocal.setString(2, fkMainCategoryId); // fk_main_category_id
                psLocal.setString(3, fkSubCategoryId); // fk_sub_category_id
                psLocal.setDouble(4, weight); // weight
                psLocal.setDouble(5, rate); // rate
                psLocal.setDouble(6, amount); // amount
                psLocal.setDouble(7, rateDollar); // rate dollar
                psLocal.setDouble(8, amountDollar); // amount dollar
                psLocal.setString(9, id); // id
                change += psLocal.executeUpdate();
            }
        }
        PurchaseBillUpdate pb = new PurchaseBillUpdate();
        pb.addEntry(id);
        return change;
    }

    private void delete() throws SQLException {
        PreparedStatement psLocal = dataConnection.prepareStatement("DELETE FROM purchase_bill_head WHERE id='"+ id +"'");
        psLocal.executeUpdate();

        psLocal = dataConnection.prepareStatement("DELETE FROM purchase_bill_details WHERE id='"+ id +"'");
        psLocal.executeUpdate();
        lb.closeStatement(psLocal);
    }

    private void clear() {
        jtxtMainCategory.setText("");
        jtxtSubCategory.setText("");
        jtxtWeight.setText("0.00");
        jtxtRate.setText("0.00");
        jtxtAmount.setText("0.00");
        jtxtRateDollar.setText("0.00");
        jtxtAmountDollar.setText("0.00");
    }

    private void updateLabel() {
        double weight = 0.00, amount = 0.00, amountDollar = 0.00;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            jTable1.setValueAt(i+1, i, 0);
            weight += lb.replaceAll(jTable1.getValueAt(i, 3).toString());
            amount += lb.replaceAll(jTable1.getValueAt(i, 5).toString());
            amountDollar += lb.replaceAll(jTable1.getValueAt(i, 7).toString());
        }
        jlblWeight.setText(lb.Convert2DecFmt(weight));
        jlblTotalAmt.setText(lb.getIndianFormat(amount));
        jlblTotalAmtDollar.setText(lb.getIndianFormat(amountDollar));
        
        double expenseText = expense * weight;
        jtxtExpense.setText(expenseText +"");
        double otherExpense = lb.replaceAll(jtxtOtherExpense.getText());
        double net_amt = amount + expenseText + otherExpense;
        jtxtNetAmt.setText(lb.getIndianFormat(net_amt));
    }

    private void setTextfieldsAtBottom() {
        JComponent[] header = new JComponent[]{null, jtxtMainCategory, jtxtSubCategory, jtxtWeight, jtxtRate, jtxtAmount, jtxtRateDollar, jtxtAmountDollar};
        JComponent[] footer = new JComponent[]{null, null, null, jlblWeight, null, jlblTotalAmt, null, jlblTotalAmtDollar};
        lb.setTable(jPanel1, jTable1, header, footer);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jlblWeight = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jlblUser = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jlblTimeStamp = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jlblVoucherNo = new javax.swing.JLabel();
        jlblStart = new javax.swing.JLabel();
        jtxtVoucher = new javax.swing.JTextField();
        jlblDay = new javax.swing.JLabel();
        jBillDateBtn = new javax.swing.JButton();
        jtxtVDate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jbtnAdd = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        jtxtAccountName = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jtxtTalliNo = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jtxtAcExpense = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jtxtRateDollarRs = new javax.swing.JTextField();
        jlblTotalAmt = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jtxtDescription = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jtxtExpense = new javax.swing.JTextField();
        jtxtOtherExpense = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jtxtNetAmt = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jtxtMainCategory = new javax.swing.JTextField();
        jtxtWeight = new javax.swing.JTextField();
        jtxtRate = new javax.swing.JTextField();
        jtxtSubCategory = new javax.swing.JTextField();
        jtxtAmount = new javax.swing.JTextField();
        jbtnDelete = new javax.swing.JButton();
        jbtnClear = new javax.swing.JButton();
        jtxtRateDollar = new javax.swing.JTextField();
        jtxtAmountDollar = new javax.swing.JTextField();
        jlblTotalAmtDollar = new javax.swing.JLabel();

        setBackground(new java.awt.Color(211, 226, 245));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(new java.awt.Color(253, 243, 243));
        jScrollPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(4, 110, 152), 1, true));
        jScrollPane1.setOpaque(false);

        jTable1.setBackground(new java.awt.Color(253, 243, 243));
        jTable1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sr No.", "Main Category", "Sub Category", "Weight", "Rate", "Amount", "Rate (Dollar)", "Amount (Dollar)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(23);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(15);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(170);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(170);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(80);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setBackground(new java.awt.Color(253, 243, 243));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 1, 1, new java.awt.Color(235, 35, 35)));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jlblWeight.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblWeight.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblWeight.setText("0.00");
        jlblWeight.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jlblWeight.setMaximumSize(new java.awt.Dimension(24, 20));
        jlblWeight.setMinimumSize(new java.awt.Dimension(24, 20));
        jlblWeight.setPreferredSize(new java.awt.Dimension(24, 20));
        jlblWeight.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jlblWeightComponentResized(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel12.setText("User Name : ");

        jlblUser.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jLabel13.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel13.setText("Edit No");

        jLabel15.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel15.setText("Last Updated");

        jlblTimeStamp.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jlblEditNo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jPanel3.setBackground(new java.awt.Color(253, 243, 243));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)), "Voucher Information", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 16), new java.awt.Color(0, 0, 255))); // NOI18N

        jlblVoucherNo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlblVoucherNo.setText("Voucher No.:");

        jlblStart.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlblStart.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblStart.setText("PB");
        jlblStart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jtxtVoucher.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtVoucher.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtVoucher.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtVoucherFocusGained(evt);
            }
        });
        jtxtVoucher.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtVoucherKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtVoucherKeyTyped(evt);
            }
        });

        jlblDay.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlblDay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });

        jtxtVDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtVDate.setAutoscrolls(false);
        jtxtVDate.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtVDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtVDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtVDateFocusLost(evt);
            }
        });
        jtxtVDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtVDateKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("Voucher Date");

        jbtnAdd.setBackground(new java.awt.Color(204, 255, 204));
        jbtnAdd.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnAdd.setForeground(new java.awt.Color(235, 35, 35));
        jbtnAdd.setText("ADD");
        jbtnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddActionPerformed(evt);
            }
        });
        jbtnAdd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnAddKeyPressed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel23.setText("Account Name:");

        jtxtAccountName.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtAccountName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAccountName.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtAccountName.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtAccountName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAccountNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAccountNameFocusLost(evt);
            }
        });
        jtxtAccountName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAccountNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtAccountNameKeyReleased(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel24.setText("Talli No:");

        jtxtTalliNo.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtTalliNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtTalliNo.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtTalliNo.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtTalliNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtTalliNoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtTalliNoFocusLost(evt);
            }
        });
        jtxtTalliNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtTalliNoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtTalliNoKeyReleased(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel25.setText("Expense:");

        jtxtAcExpense.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtAcExpense.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAcExpense.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtAcExpense.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtAcExpense.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAcExpenseFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAcExpenseFocusLost(evt);
            }
        });
        jtxtAcExpense.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAcExpenseKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtAcExpenseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAcExpenseKeyTyped(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel28.setText("Rate Dollar:");

        jtxtRateDollarRs.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtRateDollarRs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtRateDollarRs.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtRateDollarRs.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtRateDollarRs.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtRateDollarRsFocusLost(evt);
            }
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRateDollarRsFocusGained(evt);
            }
        });
        jtxtRateDollarRs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRateDollarRsKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtRateDollarRsKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtRateDollarRsKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlblVoucherNo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblStart, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtVDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblDay, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtAccountName, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtTalliNo, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtAcExpense, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtRateDollarRs, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtRateDollarRs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtAccountName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtTalliNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtAcExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jlblDay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBillDateBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtVDate, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jtxtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblStart)
                    .addComponent(jlblVoucherNo))
                .addGap(11, 11, 11))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jLabel2, jLabel23, jbtnAdd, jlblDay, jlblStart, jlblVoucherNo, jtxtAccountName, jtxtVDate, jtxtVoucher});

        jlblTotalAmt.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblTotalAmt.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblTotalAmt.setText("0.00");
        jlblTotalAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jlblTotalAmt.setMaximumSize(new java.awt.Dimension(24, 20));
        jlblTotalAmt.setMinimumSize(new java.awt.Dimension(24, 20));
        jlblTotalAmt.setPreferredSize(new java.awt.Dimension(24, 20));
        jlblTotalAmt.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jlblTotalAmtComponentResized(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel1.setText("Description: ");

        jtxtDescription.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtDescription.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtDescription.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtDescription.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtDescription.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDescriptionFocusGained(evt);
            }
        });
        jtxtDescription.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDescriptionKeyPressed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(215, 227, 208));
        jPanel4.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(53, 154, 141)));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Expense");

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("+");

        jtxtExpense.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtExpense.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtExpense.setText("0.00");
        jtxtExpense.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtExpense.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtExpenseFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtExpenseFocusLost(evt);
            }
        });
        jtxtExpense.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtExpenseKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtExpenseKeyTyped(evt);
            }
        });

        jtxtOtherExpense.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtOtherExpense.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtOtherExpense.setText("0.00");
        jtxtOtherExpense.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtOtherExpense.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtOtherExpenseFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtOtherExpenseFocusLost(evt);
            }
        });
        jtxtOtherExpense.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtOtherExpenseKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtOtherExpenseKeyTyped(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("+");

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel16.setText("Other Expense");

        jtxtNetAmt.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtNetAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtNetAmt.setText("0.00");
        jtxtNetAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("=");
        jLabel27.setMaximumSize(new java.awt.Dimension(6, 20));
        jLabel27.setMinimumSize(new java.awt.Dimension(6, 20));
        jLabel27.setPreferredSize(new java.awt.Dimension(6, 20));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setText("Net Amount");
        jLabel14.setMaximumSize(new java.awt.Dimension(57, 20));
        jLabel14.setMinimumSize(new java.awt.Dimension(57, 20));
        jLabel14.setPreferredSize(new java.awt.Dimension(57, 20));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(169, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtOtherExpense, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtExpense, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtNetAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel20, jLabel26, jLabel27});

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel14, jLabel16, jLabel9});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtExpense, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtOtherExpense, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtNetAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel14, jLabel16, jLabel20, jLabel26, jLabel27, jLabel9, jtxtExpense, jtxtNetAmt, jtxtOtherExpense});

        jtxtMainCategory.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtMainCategory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtMainCategory.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtMainCategory.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtMainCategory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtMainCategoryFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtMainCategoryFocusLost(evt);
            }
        });
        jtxtMainCategory.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jtxtMainCategoryComponentResized(evt);
            }
        });
        jtxtMainCategory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMainCategoryKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtMainCategoryKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtMainCategoryKeyTyped(evt);
            }
        });

        jtxtWeight.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtWeight.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtWeight.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtWeight.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtWeight.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtWeightFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtWeightFocusLost(evt);
            }
        });
        jtxtWeight.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtWeightKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtWeightKeyTyped(evt);
            }
        });

        jtxtRate.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtRate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtRate.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtRate.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtRate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtRateFocusLost(evt);
            }
        });
        jtxtRate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRateKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtRateKeyTyped(evt);
            }
        });

        jtxtSubCategory.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtSubCategory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtSubCategory.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtSubCategory.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtSubCategory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtSubCategoryFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtSubCategoryFocusLost(evt);
            }
        });
        jtxtSubCategory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtSubCategoryKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtSubCategoryKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtSubCategoryKeyTyped(evt);
            }
        });

        jtxtAmount.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtAmount.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAmount.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtAmount.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtAmount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAmountFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAmountFocusLost(evt);
            }
        });
        jtxtAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAmountKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAmountKeyTyped(evt);
            }
        });

        jbtnDelete.setBackground(new java.awt.Color(204, 255, 204));
        jbtnDelete.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnDelete.setForeground(new java.awt.Color(235, 35, 35));
        jbtnDelete.setText("DELETE");
        jbtnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteActionPerformed(evt);
            }
        });

        jbtnClear.setBackground(new java.awt.Color(204, 255, 204));
        jbtnClear.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnClear.setForeground(new java.awt.Color(235, 35, 35));
        jbtnClear.setText("CLEAR");
        jbtnClear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbtnClearMouseClicked(evt);
            }
        });
        jbtnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnClearActionPerformed(evt);
            }
        });

        jtxtRateDollar.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtRateDollar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtRateDollar.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtRateDollar.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtRateDollar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRateDollarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtRateDollarFocusLost(evt);
            }
        });
        jtxtRateDollar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRateDollarKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtRateDollarKeyTyped(evt);
            }
        });

        jtxtAmountDollar.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtAmountDollar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAmountDollar.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtAmountDollar.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtAmountDollar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAmountDollarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAmountDollarFocusLost(evt);
            }
        });
        jtxtAmountDollar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAmountDollarKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAmountDollarKeyTyped(evt);
            }
        });

        jlblTotalAmtDollar.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblTotalAmtDollar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblTotalAmtDollar.setText("0.00");
        jlblTotalAmtDollar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jlblTotalAmtDollar.setMaximumSize(new java.awt.Dimension(24, 20));
        jlblTotalAmtDollar.setMinimumSize(new java.awt.Dimension(24, 20));
        jlblTotalAmtDollar.setPreferredSize(new java.awt.Dimension(24, 20));
        jlblTotalAmtDollar.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jlblTotalAmtDollarComponentResized(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblTimeStamp, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jbtnClear, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jbtnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(jtxtDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jlblWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jlblTotalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jlblTotalAmtDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(16, 16, 16))))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(171, 171, 171)
                        .addComponent(jtxtMainCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtSubCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtRate, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtRateDollar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAmountDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtSubCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMainCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtRateDollar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAmountDollar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblTotalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblTotalAmtDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbtnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtnClear))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblTimeStamp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jlblTotalAmt, jlblWeight});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jtxtAmount, jtxtMainCategory, jtxtRate, jtxtSubCategory, jtxtWeight});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jbtnClear, jbtnDelete});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel12, jLabel13, jLabel15, jlblEditNo, jlblTimeStamp, jlblUser});

        pack();
    }// </editor-fold>//GEN-END:initComponents
   
    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if(evt.getClickCount() == 2) {
            evt.consume();
            if(!navLoad.getMode().equalsIgnoreCase("")) {
                int rowSel = jTable1.getSelectedRow();
                if(rowSel != -1) {
                    jtxtMainCategory.setText(jTable1.getValueAt(rowSel, 1).toString());
                    jtxtSubCategory.setText(jTable1.getValueAt(rowSel, 2).toString());
                    jtxtWeight.setText(jTable1.getValueAt(rowSel, 3).toString());
                    jtxtRate.setText(jTable1.getValueAt(rowSel, 4).toString());
                    jtxtAmount.setText(jTable1.getValueAt(rowSel, 5).toString());
                    jtxtRateDollar.setText(jTable1.getValueAt(rowSel, 6).toString());
                    jtxtAmountDollar.setText(jTable1.getValueAt(rowSel, 7).toString());
                    jtxtWeight.requestFocusInWindow();
                }
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        setTextfieldsAtBottom();
    }//GEN-LAST:event_formMouseMoved

    private void jtxtVDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtVDateFocusGained

    private void jtxtVDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVDateFocusLost
        if(lb.checkDate(jtxtVDate)){
            jlblDay.setText(lb.setDay(jtxtVDate));
        } else {
            navLoad.setMessage(Constants.CORRECT_DATE);
        }
    }//GEN-LAST:event_jtxtVDateFocusLost

    private void jtxtVDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVDateKeyPressed
        lb.enterFocus(evt, jtxtAccountName);
    }//GEN-LAST:event_jtxtVDateKeyPressed

    private void jBillDateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtnActionPerformed
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtVDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtVDate.getX(), jtxtVDate.getY() + 145, jtxtVDate.getX() + odc.getWidth(), jtxtVDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, Constants.SELECT_DATE);
    }//GEN-LAST:event_jBillDateBtnActionPerformed

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        int row1 = jTable1.getSelectedRow();
        int cnt = jTable1.getRowCount();
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            evt.consume();
            if (cnt > 1) {
                if (row1 != -1) {
                    if (JOptionPane.showConfirmDialog(this, Constants.DELETE_ROW + jTable1.getValueAt(row1, 0).toString(), DeskFrame.TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        dtm.removeRow(row1);
                        updateLabel();
                    }
                    jTable1.clearSelection();
                }
            }
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jtxtVoucherFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVoucherFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtVoucherFocusGained

    private void jtxtVoucherKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVoucherKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if(lb.isExist("purchase_bill_head", "id", initial + jtxtVoucher.getText(), dataConnection)) {
                id = initial + jtxtVoucher.getText();
                navLoad.setMessage("");
                navLoad.setVoucher("Edit");
            } else {
                navLoad.setMessage(Constants.INVALID_VOUCHER_NO);
            }
            jtxtVoucher.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtVoucherKeyPressed

    private void jtxtVoucherKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVoucherKeyTyped
        lb.onlyNumber(evt, (8 - initial.length()));
    }//GEN-LAST:event_jtxtVoucherKeyTyped

    private void jtxtDescriptionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDescriptionFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDescriptionFocusGained

    private void jtxtDescriptionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDescriptionKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtDescriptionKeyPressed

    private void jtxtOtherExpenseFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtOtherExpenseFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtOtherExpenseFocusGained

    private void jtxtOtherExpenseFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtOtherExpenseFocusLost
        updateLabel();
    }//GEN-LAST:event_jtxtOtherExpenseFocusLost

    private void jtxtOtherExpenseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtOtherExpenseKeyPressed
        lb.enterEvent(evt, jtxtDescription);
    }//GEN-LAST:event_jtxtOtherExpenseKeyPressed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_formComponentResized

    private void jlblWeightComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblWeightComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jlblWeightComponentResized

    private void jlblTotalAmtComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblTotalAmtComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jlblTotalAmtComponentResized

    private void jtxtMainCategoryComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtxtMainCategoryComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jtxtMainCategoryComponentResized

    private void jtxtMainCategoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMainCategoryFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMainCategoryFocusGained

    private void jtxtMainCategoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMainCategoryFocusLost
        mainCategoryPickList.setVisible(false);
        if(!lb.isExist("main_category", "name", jtxtMainCategory.getText())) {
            navLoad.setMessage(Constants.INVALID_MAIN_CATEGORY);
            jtxtMainCategory.requestFocusInWindow();
        } else {
            navLoad.setMessage("");
            String sql = "SELECT name FROM sub_category WHERE fk_main_category_id LIKE '%"+ lb.getMainCategory(jtxtMainCategory.getText(), "C") +"%'";
            try {
                PreparedStatement psLocal = dataConnection.prepareStatement(sql);
                ResultSet rsLocal = psLocal.executeQuery();
                if(rsLocal.next()) {
                    jtxtSubCategory.setText("");
                    jtxtSubCategory.setEnabled(true);
                    jtxtSubCategory.setEditable(true);
                    jtxtSubCategory.requestFocusInWindow();
                } else {
                    jtxtSubCategory.setText("");
                    jtxtSubCategory.setEnabled(false);
                    jtxtSubCategory.setEditable(false);
                    jtxtWeight.requestFocusInWindow();
                }
            } catch(Exception ex) {
                lb.printToLogFile("Exception at acalias in Tax Invoice", ex);
            }
        }
    }//GEN-LAST:event_jtxtMainCategoryFocusLost

    private void jtxtMainCategoryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainCategoryKeyPressed
        mainCategoryPickList.setLocation(jtxtMainCategory.getX(), jtxtMainCategory.getY() + jtxtMainCategory.getHeight());
        mainCategoryPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtMainCategoryKeyPressed

    private void jtxtMainCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainCategoryKeyReleased
        try {
            mainCategoryPickList.setReturnComponent(new JTextField[]{jtxtMainCategory});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM main_category WHERE status = 0 AND name LIKE '%"+ jtxtMainCategory.getText() +"%'");
            mainCategoryPickList.setValidation(dataConnection.prepareStatement("SELECT name FROM main_category WHERE status = 0 AND name = ?"));
            mainCategoryPickList.setFirstAssociation(new int[]{0});
            mainCategoryPickList.setSecondAssociation(new int[]{0});
            mainCategoryPickList.setPreparedStatement(pstLocal);
            mainCategoryPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtMainCategoryKeyReleased In Purchase Bill", ex);
        }
    }//GEN-LAST:event_jtxtMainCategoryKeyReleased

    private void jtxtWeightFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtWeightFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtWeightFocusGained

    private void jtxtWeightFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtWeightFocusLost
        calculation();
    }//GEN-LAST:event_jtxtWeightFocusLost

    private void jtxtWeightKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtWeightKeyPressed
        lb.enterEvent(evt, jtxtRate);
    }//GEN-LAST:event_jtxtWeightKeyPressed

    private void jtxtWeightKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtWeightKeyTyped
        lb.onlyNumber(evt, 15);
    }//GEN-LAST:event_jtxtWeightKeyTyped

    private void jtxtRateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRateFocusGained

    private void jtxtRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateFocusLost
        calculation();
    }//GEN-LAST:event_jtxtRateFocusLost

    private void jtxtRateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateKeyPressed
        lb.enterFocus(evt, jtxtRateDollar);
    }//GEN-LAST:event_jtxtRateKeyPressed

    private void jtxtRateKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateKeyTyped
        lb.onlyNumber(evt, 15);
    }//GEN-LAST:event_jtxtRateKeyTyped

    private void jtxtSubCategoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSubCategoryFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtSubCategoryFocusGained

    private void jtxtSubCategoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSubCategoryFocusLost
        subCategoryPickList.setVisible(false);
    }//GEN-LAST:event_jtxtSubCategoryFocusLost

    private void jtxtSubCategoryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSubCategoryKeyPressed
        subCategoryPickList.setLocation(jtxtSubCategory.getX(), jtxtSubCategory.getY() + jtxtSubCategory.getHeight());
        subCategoryPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtSubCategoryKeyPressed

    private void jtxtSubCategoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSubCategoryKeyTyped
        lb.onlyAlpha(evt, 255);
    }//GEN-LAST:event_jtxtSubCategoryKeyTyped

    private void jtxtAmountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmountFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAmountFocusGained

    private void jtxtAmountFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmountFocusLost
        calculation();
    }//GEN-LAST:event_jtxtAmountFocusLost

    private void jtxtAmountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmountKeyPressed
        lb.enterFocus(evt, jtxtRateDollar);
    }//GEN-LAST:event_jtxtAmountKeyPressed

    private void jtxtAmountKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmountKeyTyped
        lb.onlyNumber(evt, 15);
    }//GEN-LAST:event_jtxtAmountKeyTyped

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        int rowSel = jTable1.getSelectedRow();
        if(reValidate()){
            if(rowSel == -1) {
                Vector row = new Vector();
                row.add("");
                row.add(jtxtMainCategory.getText());
                row.add(jtxtSubCategory.getText());
                row.add(lb.Convert2DecFmt(lb.replaceAll(jtxtWeight.getText())));
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtRate.getText())));
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtAmount.getText())));
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtRateDollar.getText())));
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtAmountDollar.getText())));
                dtm.addRow(row);
            } else {
                jTable1.setValueAt(jtxtMainCategory.getText(), rowSel, 1);
                jTable1.setValueAt(jtxtSubCategory.getText(), rowSel, 2);
                jTable1.setValueAt(lb.Convert2DecFmt(lb.replaceAll(jtxtWeight.getText())), rowSel, 3);
                jTable1.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtRate.getText())), rowSel, 4);
                jTable1.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtAmount.getText())), rowSel, 5);
                jTable1.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtRateDollar.getText())), rowSel, 6);
                jTable1.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtAmountDollar.getText())), rowSel, 7);
            }
            updateLabel();
            clear();
            jTable1.clearSelection();
            if (JOptionPane.showConfirmDialog(this, Constants.ADD_MORE_ENTRY, DeskFrame.TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                jtxtMainCategory.requestFocusInWindow();
            } else {
                jtxtExpense.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jbtnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteActionPerformed
        int row1 = jTable1.getSelectedRow();
        int cnt = jTable1.getRowCount();
        if (cnt > 1) {
            if (row1 != -1) {
                if (JOptionPane.showConfirmDialog(this, Constants.DELETE_ROW + jTable1.getValueAt(row1, 0).toString(), DeskFrame.TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    dtm.removeRow(row1);
                    updateLabel();
                }
                jTable1.clearSelection();
            }
        }
    }//GEN-LAST:event_jbtnDeleteActionPerformed

    private void jbtnClearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnClearMouseClicked
        jTable1.clearSelection();
    }//GEN-LAST:event_jbtnClearMouseClicked

    private void jbtnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnClearActionPerformed
        jTable1.clearSelection();
    }//GEN-LAST:event_jbtnClearActionPerformed

    private void jtxtExpenseFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtExpenseFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtExpenseFocusGained

    private void jtxtExpenseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtExpenseKeyPressed
        lb.enterEvent(evt, jtxtOtherExpense);
    }//GEN-LAST:event_jtxtExpenseKeyPressed

    private void jtxtExpenseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtExpenseKeyTyped
        lb.onlyNumber(evt, 10);
    }//GEN-LAST:event_jtxtExpenseKeyTyped

    private void jtxtExpenseFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtExpenseFocusLost
        updateLabel();
    }//GEN-LAST:event_jtxtExpenseFocusLost

    private void jtxtOtherExpenseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtOtherExpenseKeyTyped
        lb.onlyNumber(evt, 10);
    }//GEN-LAST:event_jtxtOtherExpenseKeyTyped

    private void jtxtAccountNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAccountNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAccountNameFocusGained

    private void jtxtAccountNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAccountNameFocusLost
        accountCategoryPickList.setVisible(false);
        if(!lb.isExist("account_master", "name", jtxtAccountName.getText())) {
            navLoad.setMessage(Constants.INVALID_ACCOUNT);
            jtxtAccountName.requestFocusInWindow();
        } else {
            navLoad.setMessage("");
            String sql = "SELECT expense FROM account_master WHERE name LIKE '%"+ jtxtAccountName.getText() +"%'";
            try {
                PreparedStatement psLocal = dataConnection.prepareStatement(sql);
                ResultSet rsLocal = psLocal.executeQuery();
                if(rsLocal.next()) {
                    expense = rsLocal.getDouble("expense");
                    jtxtAcExpense.setText(expense +"");
                }
            } catch(Exception ex) {
                lb.printToLogFile("Exception at acalias in Tax Invoice", ex);
            }
        }
    }//GEN-LAST:event_jtxtAccountNameFocusLost

    private void jtxtAccountNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAccountNameKeyPressed
        accountCategoryPickList.setLocation(jtxtAccountName.getX() + jPanel3.getX(), jtxtAccountName.getY() + jtxtAccountName.getHeight() + jPanel3.getY());
        accountCategoryPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtAccountNameKeyPressed

    private void jtxtAccountNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAccountNameKeyReleased
        try {
            accountCategoryPickList.setReturnComponent(new JTextField[]{jtxtAccountName});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM account_master WHERE status = 0 AND name LIKE '%"+ jtxtAccountName.getText() +"%'");
            accountCategoryPickList.setValidation(dataConnection.prepareStatement("SELECT name FROM account_master WHERE status = 0 AND name = ?"));
            accountCategoryPickList.setFirstAssociation(new int[]{0});
            accountCategoryPickList.setSecondAssociation(new int[]{0});
            accountCategoryPickList.setPreparedStatement(pstLocal);
            accountCategoryPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtAccountNameKeyReleased In Purchase Bill", ex);
        }
    }//GEN-LAST:event_jtxtAccountNameKeyReleased

    private void jtxtSubCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSubCategoryKeyReleased
        try {
            subCategoryPickList.setReturnComponent(new JTextField[]{jtxtSubCategory});
            String sql = "SELECT name FROM sub_category WHERE status = 0 AND fk_main_category_id = '"+ lb.getMainCategory(jtxtMainCategory.getText(), "C") 
                    +"' AND name LIKE '%"+ jtxtSubCategory.getText() +"%'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            subCategoryPickList.setValidation(dataConnection.prepareStatement("SELECT name FROM sub_category WHERE status = 0 AND name = ?"));
            subCategoryPickList.setFirstAssociation(new int[]{0});
            subCategoryPickList.setSecondAssociation(new int[]{0});
            subCategoryPickList.setPreparedStatement(pstLocal);
            subCategoryPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtSubCategoryKeyReleased In Purchase Bill", ex);
        }
    }//GEN-LAST:event_jtxtSubCategoryKeyReleased

    private void jtxtMainCategoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainCategoryKeyTyped
        lb.onlyAlpha(evt, 255);
    }//GEN-LAST:event_jtxtMainCategoryKeyTyped

    private void jtxtTalliNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTalliNoFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtTalliNoFocusGained

    private void jtxtTalliNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTalliNoFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtTalliNoFocusLost

    private void jtxtTalliNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTalliNoKeyPressed
        lb.enterEvent(evt, jtxtAcExpense);
    }//GEN-LAST:event_jtxtTalliNoKeyPressed

    private void jtxtTalliNoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTalliNoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtTalliNoKeyReleased

    private void jtxtAcExpenseFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcExpenseFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAcExpenseFocusGained

    private void jtxtAcExpenseFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcExpenseFocusLost
        expense = Double.valueOf(jtxtAcExpense.getText());
    }//GEN-LAST:event_jtxtAcExpenseFocusLost

    private void jtxtAcExpenseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcExpenseKeyPressed
        lb.enterEvent(evt, jtxtRateDollarRs);
    }//GEN-LAST:event_jtxtAcExpenseKeyPressed

    private void jtxtAcExpenseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcExpenseKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtAcExpenseKeyReleased

    private void jtxtAcExpenseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcExpenseKeyTyped
        lb.onlyNumber(evt, 15);
    }//GEN-LAST:event_jtxtAcExpenseKeyTyped

    private void jtxtRateDollarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateDollarFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRateDollarFocusGained

    private void jtxtRateDollarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateDollarFocusLost
       calculation();
    }//GEN-LAST:event_jtxtRateDollarFocusLost

    private void jtxtRateDollarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateDollarKeyPressed
       lb.enterFocus(evt, jbtnAdd);
    }//GEN-LAST:event_jtxtRateDollarKeyPressed

    private void jtxtRateDollarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateDollarKeyTyped
       lb.onlyNumber(evt, 15);
    }//GEN-LAST:event_jtxtRateDollarKeyTyped

    private void jtxtAmountDollarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmountDollarFocusGained
       lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAmountDollarFocusGained

    private void jtxtAmountDollarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmountDollarFocusLost
       calculation();
    }//GEN-LAST:event_jtxtAmountDollarFocusLost

    private void jtxtAmountDollarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmountDollarKeyPressed
       lb.enterFocus(evt, jbtnAdd);
    }//GEN-LAST:event_jtxtAmountDollarKeyPressed

    private void jtxtAmountDollarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmountDollarKeyTyped
       lb.onlyNumber(evt, 15);
    }//GEN-LAST:event_jtxtAmountDollarKeyTyped

    private void jlblTotalAmtDollarComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblTotalAmtDollarComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_jlblTotalAmtDollarComponentResized

    private void jtxtRateDollarRsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateDollarRsFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRateDollarRsFocusGained

    private void jtxtRateDollarRsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateDollarRsFocusLost
        rateDollarRs = Double.valueOf(jtxtRateDollarRs.getText());
    }//GEN-LAST:event_jtxtRateDollarRsFocusLost

    private void jtxtRateDollarRsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateDollarRsKeyPressed
        lb.enterEvent(evt, jtxtMainCategory);
    }//GEN-LAST:event_jtxtRateDollarRsKeyPressed

    private void jtxtRateDollarRsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateDollarRsKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtRateDollarRsKeyReleased

    private void jtxtRateDollarRsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateDollarRsKeyTyped
        lb.onlyNumber(evt, 15);
    }//GEN-LAST:event_jtxtRateDollarRsKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JButton jbtnClear;
    private javax.swing.JButton jbtnDelete;
    private javax.swing.JLabel jlblDay;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblStart;
    private javax.swing.JLabel jlblTimeStamp;
    private javax.swing.JLabel jlblTotalAmt;
    private javax.swing.JLabel jlblTotalAmtDollar;
    private javax.swing.JLabel jlblUser;
    private javax.swing.JLabel jlblVoucherNo;
    private javax.swing.JLabel jlblWeight;
    private javax.swing.JTextField jtxtAcExpense;
    private javax.swing.JTextField jtxtAccountName;
    private javax.swing.JTextField jtxtAmount;
    private javax.swing.JTextField jtxtAmountDollar;
    private javax.swing.JTextField jtxtDescription;
    private javax.swing.JTextField jtxtExpense;
    private javax.swing.JTextField jtxtMainCategory;
    private javax.swing.JTextField jtxtNetAmt;
    private javax.swing.JTextField jtxtOtherExpense;
    private javax.swing.JTextField jtxtRate;
    private javax.swing.JTextField jtxtRateDollar;
    private javax.swing.JTextField jtxtRateDollarRs;
    private javax.swing.JTextField jtxtSubCategory;
    private javax.swing.JTextField jtxtTalliNo;
    private javax.swing.JTextField jtxtVDate;
    private javax.swing.JTextField jtxtVoucher;
    private javax.swing.JTextField jtxtWeight;
    // End of variables declaration//GEN-END:variables
}