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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import oldbupdate.SalesBillUpdate;
import dhananistockmanagement.MainClass;
import support.HeaderIntFrame1;
import support.Library;
import support.NavigationPanel1;
import support.OurDateChooser;
import support.PickList;
import support.ReportTable;
import support.Constants;

/**
 *
 * @author @JD@
 */
public class SalesBill extends javax.swing.JInternalFrame {
    NavigationPanel1 navLoad = null;
    Library lb = new Library();
    DefaultTableModel dtm = null;
    private String ref_no = "";
    private ReportTable taxInvoiceView = null;
    Connection dataConnection = DeskFrame.connMpAdmin;
    private int type = 0;
    private String initial = Constants.SALES_BILL_INITIAL;
    private PickList accountPickList = null, itemPickList = null, mainCategoryPickList = null, subCategoryPickList = null, gradePickList = null;
    String Syspath = System.getProperty("user.dir");
    private String view_title = Constants.SALES_BILL_FORM_NAME;
    private Double rateDollarRs;

    /**
     * Creates new form TaxInvoice
     */
    public SalesBill(int type) {
        initComponents();
        this.type = type;
        initOtherComponents();
        navLoad.setVoucher("last");
    }

    public SalesBill(int type, String ref_no) {
        initComponents();
        this.type = type;
        jtxtVoucher.setText(ref_no.substring(initial.length()));
        this.ref_no = initial + jtxtVoucher.getText();
        initOtherComponents();
        navLoad.setVoucher("edit");
    }

    private void initOtherComponents() {
        jtxtVoucher.requestFocusInWindow();
        setInitial();
        connectToNavigation();
        lb.setDateChooserProperty(jtxtVDate);
        dtm = (DefaultTableModel) jTable1.getModel();
        dataConnection = DeskFrame.connMpAdmin;
        makeChildTableSalesBill();
        setPickListView();
        setIconToPnael();
        jTable1.setBackground(new Color(253, 243, 243));
        setTitle(Constants.SALES_BILL_FORM_NAME);
    }

    private void setIconToPnael() {
        Syspath += File.separator + "Resources" + File.separator + "Images" + File.separator;
        jbtnAdd.setIcon(new ImageIcon(Syspath +"Add.png"));
        jbtnClear.setIcon(new ImageIcon(Syspath +"cancel.png"));
        jbtnDelete.setIcon(new ImageIcon(Syspath +"delete.png"));
    }

    private void setInitial(){
        jlblStart.setText(initial);
        jtxtparticulars.setVisible(false);
    }

    private void setPickListView() {
        accountPickList = new PickList(dataConnection);
        accountPickList.setLayer(getLayeredPane());
        accountPickList.setPickListComponent(jtxtACAlias);
        accountPickList.setNextComponent(jtxtGradeCategory);

        itemPickList = new PickList(dataConnection);
        itemPickList.setLayer(getLayeredPane());
        itemPickList.setPickListComponent(jtxtGradeCategory);
        itemPickList.setNextComponent(jtxtSlab);
        itemPickList.setReturnComponent(new JTextField[]{jtxtGradeCategory});

        mainCategoryPickList = new PickList(dataConnection);

        mainCategoryPickList.setLayer(getLayeredPane());
        mainCategoryPickList.setPickListComponent(jtxtMainCategory);
        mainCategoryPickList.setNextComponent(jtxtSubCategory);

        subCategoryPickList = new PickList(dataConnection);

        subCategoryPickList.setLayer(getLayeredPane());
        subCategoryPickList.setPickListComponent(jtxtSubCategory);
        subCategoryPickList.setNextComponent(jtxtGradeCategory);
    }

    private void makeChildTableSalesBill() {
        taxInvoiceView = new ReportTable();

        taxInvoiceView.AddColumn(0, "Voucher No", 100, java.lang.String.class, null, false);
        taxInvoiceView.AddColumn(1, "Voucher Date", 100, java.lang.String.class, null, false);
        taxInvoiceView.AddColumn(2, "Account Name", 200, java.lang.String.class, null, false);
        taxInvoiceView.AddColumn(3, "Bill No", 150, java.lang.String.class, null, false);
        taxInvoiceView.AddColumn(4, "Net Amount", 150, java.lang.Double.class, null, false);
        taxInvoiceView.makeTable();
    }

    private void calculation() {
        int slab = Integer.parseInt(jtxtSlab.getText());
        double qty = slab * 10;
        double rate = lb.replaceAll(jtxtRate.getText());
        double rateDollar = lb.replaceAll(jtxtRateDollar.getText());
        double rateDollarRs = lb.replaceAll(jtxtRateDollarRs.getText());
        double amt = 0.00;
        double amtDollar = 0.00;
        if(rateDollarRs > 0) {
            if(rateDollar > 0) {
                rate = rateDollar * rateDollarRs;
            } else if(rate > 0) {
                rateDollar = rate / rateDollarRs;
            }
        }
        
        amt = (qty * rate);
        amtDollar = (qty * rateDollar);
        
        jtxtQty.setText(lb.Convert2DecFmt(qty));
        jtxtRate.setText(lb.getIndianFormat(rate));
        jtxtRateDollar.setText(lb.getIndianFormat(rateDollar));
        jtxtAmt.setText(lb.getIndianFormat(amt));
        jtxtAmtDollar.setText(lb.getIndianFormat(amtDollar));
    }

    private boolean reValidate(){
        boolean flag = true;

        if(lb.isBlank(jtxtGradeCategory)) {
            navLoad.setMessage(Constants.INVALID_ITEM);
            jtxtGradeCategory.requestFocusInWindow();
            flag = flag && false;
        }
        
        if(lb.replaceAll(jtxtSlab.getText()) < 0) {
            navLoad.setMessage(Constants.INVALID_SLAB);
            jtxtSlab.requestFocusInWindow();
            flag = flag && false;
        }

        if(lb.replaceAll(jtxtQty.getText()) < 0) {
            navLoad.setMessage(Constants.INVALID_QTY);
            jtxtQty.requestFocusInWindow();
            flag = flag && false;
        }

        if(lb.replaceAll(jtxtRate.getText()) < 0) {
            navLoad.setMessage(Constants.INVALID_RATE);
            jtxtRate.requestFocusInWindow();
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

    private void deleteData() {
        try {
            dataConnection.setAutoCommit(false);
            SalesBillUpdate cp = new SalesBillUpdate();
            //cp.deleteEntry(ref_no);

            delete();
            dataConnection.commit();
            navLoad.setVoucher("last");
            dataConnection.setAutoCommit(true);
        } catch (Exception ex) {
            try {
                lb.printToLogFile("Error at delete In Tax Invoice", ex);
                dataConnection.rollback();
                dataConnection.setAutoCommit(true);
            } catch (SQLException ex1) {
                lb.printToLogFile("Error at rollback delete In Tax Invoice", ex1);
            }
        }
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
                int ans = JOptionPane.showConfirmDialog(null, Constants.DELETE_THIS + "voucher no. "+ ref_no, "Confirm Delete", JOptionPane.OK_CANCEL_OPTION);
                if (ans == JOptionPane.OK_OPTION) {
                    if(lb.getData("SELECT * FROM sale_bill_head WHERE ref_no > '"+ ref_no +"'").equals("")) {
                        deleteData();
                    } else {
                        if(lb.createPassword()) {
                            deleteData();
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
                    String sql = "SELECT * FROM sale_bill_head";
                    if (tag.equalsIgnoreCase("first")) {
                        sql += " WHERE ref_no = (SELECT MIN(ref_no) FROM sale_bill_head)";
                    } else if (tag.equalsIgnoreCase("previous")) {
                        sql += " WHERE ref_no = (SELECT MAX(ref_no) FROM sale_bill_head WHERE ref_no < '"+ ref_no +"')";
                    } else if (tag.equalsIgnoreCase("next")) {
                        sql += " WHERE ref_no = (SELECT MIN(ref_no) FROM sale_bill_head WHERE ref_no > '"+ ref_no +"')";
                    } else if (tag.equalsIgnoreCase("last")) {
                        sql += " WHERE ref_no = (SELECT MAX(ref_no) FROM sale_bill_head)";
                    } else if (tag.equalsIgnoreCase("edit")) {
                        sql += " WHERE ref_no = '"+ ref_no +"' AND is_del = 0";
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
                    lb.printToLogFile("Exception at setVoucher In Tax Invoice", ex);
                }
                lb.setPermission(navLoad, Constants.SALES_BILL_FORM_ID);
                setTextfieldsAtBottom();
            }

            @Override
            public void setComponentText() {
                dtm.setRowCount(0);
                jtxtVoucher.setText("");
                jtxtACAlias.setText("");
                jtxtInvoiceNo.setText("");
                jtxtRemarks.setText("");
                jtxtDiscPer.setText("0.00");
                jtxtDiscRs.setText("0.00");
                jtxtTotalAmt.setText("0.00");
                jtxtBillAmount.setText("0.00");
                jtxtAdjustAmt.setText("0.00");
                jtxtNetAmt.setText("0.00");
                jlblQty.setText("0.00");
                jlblAmt.setText("0.00");
                jtxtPDate.setText("");
                jtxtInvoiceNo.setText(lb.getInvNo("sale_bill_head", "", "") + "");
                lb.setDateChooserProperty(jtxtVDate);
                jtxtVDate.requestFocusInWindow();
                jtxtVDate.selectAll();
                jtxtRateDollarRs.setText("0.00");
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
                jtxtInvoiceNo.setEnabled(false);
                jtxtRemarks.setEnabled(bFlag);
                jtxtRateDollarRs.setEnabled(bFlag);
                jtxtPDate.setEnabled(bFlag);
                jBillDateBtn1.setEnabled(bFlag);
                jTable1.setEnabled(bFlag);
                jtxtACAlias.setEnabled(bFlag);
                jtxtMainCategory.setEnabled(bFlag);
                jtxtSubCategory.setEnabled(bFlag);
                jtxtGradeCategory.setEnabled(bFlag);
                jtxtparticulars.setEnabled(false);
                jtxtSlab.setEnabled(bFlag);
                jtxtQty.setEnabled(false);
                jtxtRate.setEnabled(bFlag);
                jtxtAmt.setEnabled(false);
                jtxtRateDollar.setEnabled(bFlag);
                jtxtAmtDollar.setEnabled(false);
                jtxtDiscPer.setEnabled(bFlag);
                jtxtDiscRs.setEnabled(bFlag);
                jtxtAdjustAmt.setEnabled(bFlag);
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
                        lb.printToLogFile("Exception at saveVoucher In Tax Invoice", ex);
                    } catch (SQLException ex1) {
                        lb.printToLogFile("Exception at saveVoucher rollback In Tax Invoice", ex1);
                    }
                }
                return 1;
            }

            @Override
            public void setComponentTextFromRs() {
                try {
                    ref_no = viewDataRs.getString("ref_no");
                    jtxtVoucher.setText(viewDataRs.getString("ref_no").substring(initial.length()));
                    jtxtVDate.setText(lb.ConvertDateFormetForDBForConcurrency(viewDataRs.getString("voucher_date")));
                    jtxtInvoiceNo.setText(viewDataRs.getString("bill_no"));
                    jtxtRateDollarRs.setText(lb.Convert2DecFmt(viewDataRs.getDouble("rate_dollar_rs")));
                    jtxtACAlias.setText(lb.getAccountMstName(viewDataRs.getString("fk_account_id"), "N"));
                    jlblQty.setText(lb.Convert2DecFmt(viewDataRs.getDouble("total_qty")));
                    jlblAmt.setText(lb.getIndianFormat(viewDataRs.getDouble("total_amt")));
                    jtxtDiscPer.setText(lb.getIndianFormat(viewDataRs.getDouble("disc_per")));
                    jtxtDiscRs.setText(lb.getIndianFormat(viewDataRs.getDouble("disc_rs")));
                    jtxtTotalAmt.setText(lb.getIndianFormat(viewDataRs.getDouble("amount_total")));
                    jtxtBillAmount.setText(lb.getIndianFormat(viewDataRs.getDouble("bill_amount")));
                    jtxtAdjustAmt.setText(lb.getIndianFormat(viewDataRs.getDouble("adj_amount")));
                    jtxtNetAmt.setText(lb.getIndianFormat(viewDataRs.getDouble("net_amount")));
                    jtxtRemarks.setText(viewDataRs.getString("remark"));
                    String p_date = viewDataRs.getString("p_date");
                    if(p_date == null) {
                        p_date = "";
                    } else {
                        p_date = lb.ConvertDateFormetForDBForConcurrency(p_date);
                    }
                    jtxtPDate.setText(p_date);
                    jlblUser.setText(lb.getUserName(viewDataRs.getString("user_cd"), "N"));
                    jlblEditNo.setText(viewDataRs.getString("edit_no"));
                    jlblTimeStamp.setText(lb.ConvertTimeStampFormetForDisplay(viewDataRs.getString("time_stamp")));

                    dtm.setRowCount(0);
                    viewDataRs = fetchData("SELECT * FROM sale_bill_detail WHERE ref_no = '"+ ref_no +"'");
                    int i = 0;
                    while (viewDataRs.next()) {
                        Vector row = new Vector();
                        row.add(++i);
                        row.add(lb.getMainCategory(viewDataRs.getString("fk_main_category_id"), "N"));
                        row.add(lb.getSubCategory(viewDataRs.getString("fk_sub_category_id"), "N"));
                        row.add(lb.getSlabCategory(viewDataRs.getString("fk_slab_category_id"), "N", viewDataRs.getString("fk_sub_category_id")));
                        row.add(String.valueOf(viewDataRs.getInt("slab")));
                        row.add(lb.Convert2DecFmt(viewDataRs.getDouble("qty")));
                        row.add(lb.getIndianFormat(viewDataRs.getDouble("rate")));
                        row.add(lb.getIndianFormat(viewDataRs.getDouble("amt")));
                        row.add(lb.getIndianFormat(viewDataRs.getDouble("rate_dollar")));
                        row.add(lb.getIndianFormat(viewDataRs.getDouble("amt_dollar")));
                        dtm.addRow(row);
                    }
                    updateLabel();
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setComponentTextFromRs In Tax Invoice", ex);
                }
            }
        }
        navLoad = new navPanel();
        jPanel2.add(navLoad);
        navLoad.setVisible(true);
    }

    private void onPrintVoucher() {
        PopUpPrintType ds = new PopUpPrintType(MainClass.df, true, ref_no, initial, view_title, 1);
        ds.show();
    }

    public void setID(String ref_no) {
        this.ref_no = ref_no;
        navLoad.setVoucher("edit");
    }

    private void onviewVoucher() {
        this.dispose();

        String sql = "SELECT sbh.ref_no, sbh.voucher_date, am.name AS ac_name, " +
            "sbh.bill_no, sbh.net_amount FROM sale_bill_head sbh, account_master am WHERE am.ac_cd = sbh.fk_account_id AND is_del = 0 ORDER BY ref_no";
        taxInvoiceView.setColumnValue(new int[]{1, 2, 3, 4, 5});
        String view_header = view_title+" VIEW";

        HeaderIntFrame1 rptDetail = new HeaderIntFrame1(dataConnection, ref_no, view_header, taxInvoiceView, sql, Constants.SALES_BILL_FORM_ID, 1, this, this.getTitle());
        rptDetail.makeView();
        rptDetail.setVisible(true);

        Component c = DeskFrame.tabbedPane.add(view_header, rptDetail);
        c.setName(view_header);
        DeskFrame.tabbedPane.setSelectedComponent(c);
    }

    @Override
    public void dispose() {
        try {
            DeskFrame.removeFromScreen(DeskFrame.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose In Tax Invoice", ex);
        }
    }

    private int saveVoucher() throws SQLException, ParseException, FileNotFoundException, IOException {
        String sql = null;

        PreparedStatement psLocal = null;
        int change = 0;
        if (navLoad.getMode().equalsIgnoreCase("N")) {
            sql = "INSERT INTO sale_bill_head(voucher_date, bill_no, rate_dollar_rs, fk_account_id, total_qty, total_amt, disc_per, disc_rs, amount_total, bill_amount, adj_amount, net_amount, remark, p_date, chck, user_cd, ref_no) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ref_no = lb.generateKey("sale_bill_head", "ref_no", 7, initial);
        } else if (navLoad.getMode().equalsIgnoreCase("E")) {
            SalesBillUpdate sb = new SalesBillUpdate();
            sb.deleteEntry(ref_no);

            sql = "DELETE FROM sale_bill_detail WHERE ref_no='"+ ref_no +"'";
            psLocal = dataConnection.prepareStatement(sql);
            change += psLocal.executeUpdate();

            sql = "UPDATE sale_bill_head SET voucher_date = ?, bill_no = ?, rate_dollar_rs = ?, fk_account_id = ?, total_qty = ?, total_amt = ?, disc_per = ?, disc_rs = ?, amount_total = ?, bill_amount = ?, adj_amount = ?, net_amount = ?, remark = ?, p_date = ?, chck = ?, user_cd = ?, edit_no = edit_no + 1, time_stamp = CURRENT_TIMESTAMP WHERE ref_no = ?";
        }
        int check = 0;
        String p_date = jtxtPDate.getText();
        if(p_date.equalsIgnoreCase("")) {
            p_date = null;
        } else {
            p_date = lb.tempConvertFormatForDBorConcurrency(p_date);
        }
        psLocal = dataConnection.prepareStatement(sql);
        psLocal.setString(1, lb.tempConvertFormatForDBorConcurrency(jtxtVDate.getText())); // Voucher Date
        psLocal.setString(2, jtxtInvoiceNo.getText()); // Bill No
        psLocal.setDouble(3, lb.replaceAll(jtxtRateDollarRs.getText())); // amount type
        psLocal.setString(4, lb.getAccountMstName(jtxtACAlias.getText(), "C")); // AC CD
        psLocal.setDouble(5, lb.replaceAll(jlblQty.getText())); // Total Qty
        psLocal.setDouble(6, lb.replaceAll(jlblAmt.getText())); // Total Amt
        psLocal.setDouble(7, lb.replaceAll(jtxtDiscPer.getText())); // Disc Per
        psLocal.setDouble(8, lb.replaceAll(jtxtDiscRs.getText())); // Disc Rs
        psLocal.setDouble(9, lb.replaceAll(jtxtTotalAmt.getText())); // Total Amt
        psLocal.setDouble(10, lb.replaceAll(jtxtBillAmount.getText())); // Bill Amt
        psLocal.setDouble(11, lb.replaceAll(jtxtAdjustAmt.getText())); // Adjustment
        psLocal.setDouble(12, lb.replaceAll(jtxtNetAmt.getText())); // Net Amt
        psLocal.setString(13, jtxtRemarks.getText()); // Remarks
        psLocal.setString(14, p_date); // P_DATE
        psLocal.setInt(15, check); // CHECK
        psLocal.setInt(16, DeskFrame.user_id); // User CD
        psLocal.setString(17, ref_no); // Ref No
        change += psLocal.executeUpdate();

        sql = "INSERT INTO sale_bill_detail(sr_no, fk_main_category_id, fk_sub_category_id, fk_slab_category_id, slab, qty, rate, amt, rate_dollar, amt_dollar, ref_no) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        psLocal = dataConnection.prepareStatement(sql);
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            String main_category_cd = lb.getMainCategory(jTable1.getValueAt(i, 1).toString(), "C"); // ITEM CD
            String sub_category_cd = lb.getSubCategory(jTable1.getValueAt(i, 2).toString(), "C"); // ITEM CD
            String slab_category_cd = lb.getSlabCategory(jTable1.getValueAt(i, 3).toString(), "C", sub_category_cd); // ITEM CD
            int slab = Integer.parseInt(jTable1.getValueAt(i, 4).toString()); // SLAB
            double qty = lb.replaceAll(jTable1.getValueAt(i, 5).toString()); // QTY
            double rate = lb.replaceAll(jTable1.getValueAt(i, 6).toString()); // RATE
            double amount = lb.replaceAll(jTable1.getValueAt(i, 7).toString()); // AMOUNT
            double rateDollar = lb.replaceAll(jTable1.getValueAt(i, 8).toString()); // RATE DOLLAR
            double amountDollar = lb.replaceAll(jTable1.getValueAt(i, 9).toString()); // AMOUNT DOLLAR
            if(!(slab_category_cd.equalsIgnoreCase("0") || slab_category_cd.equalsIgnoreCase(""))) {
                if (qty > 0) { // CONDITION QTY
                    psLocal.setInt(1, i + 1); // SR NO
                    psLocal.setString(2, main_category_cd); // Main Category CD
                    psLocal.setString(3, sub_category_cd); // Sub Category CD
                    psLocal.setString(4, slab_category_cd); // Slab Category CD
                    psLocal.setInt(5, slab); // QTY
                    psLocal.setDouble(6, qty); // QTY
                    psLocal.setDouble(7, rate); // RATE
                    psLocal.setDouble(8, amount); // AMT
                    psLocal.setDouble(9, rateDollar); // RATE
                    psLocal.setDouble(10, amountDollar); // AMT
                    psLocal.setString(11, ref_no); // REF NO
                    change += psLocal.executeUpdate();
                }
            }
        }
        SalesBillUpdate sb = new SalesBillUpdate();
        sb.addEntry(ref_no, lb.tempConvertFormatForDBorConcurrency(jtxtVDate.getText()));
        return change;
    }

    private void delete() throws SQLException {
        SalesBillUpdate sb = new SalesBillUpdate();
        sb.deleteEntry(ref_no);
        
        PreparedStatement psLocal = dataConnection.prepareStatement("DELETE FROM sale_bill_head WHERE ref_no = '"+ ref_no +"'");
        psLocal.executeUpdate();

        psLocal = dataConnection.prepareStatement("DELETE FROM sale_bill_detail WHERE ref_no = '"+ ref_no +"'");
        psLocal.executeUpdate();
        lb.closeStatement(psLocal);
    }

    private void clear() {
        jtxtMainCategory.setText("");
        jtxtSubCategory.setText("");
        jtxtGradeCategory.setText("");
        jtxtparticulars.setText("");
        jtxtSlab.setText("0");
        jtxtQty.setText("0.00");
        jtxtRate.setText("0.00");
        jtxtAmt.setText("0.00");
        jtxtRateDollar.setText("0.00");
        jtxtAmtDollar.setText("0.00");
    }

    private void updateLabel() {
        int totalSlab = 0;
        double qty = 0.00, amt = 0.00, amtDollar = 0.00;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            int slab = 0;
            jTable1.setValueAt(i+1, i, 0);
            slab = Integer.parseInt(jTable1.getValueAt(i, 4).toString());
            qty += (slab * 10);
            jTable1.setValueAt((slab * 10), i, 5);
            amt += lb.replaceAll(jTable1.getValueAt(i, 7).toString());
            amtDollar += lb.replaceAll(jTable1.getValueAt(i, 9).toString());
            totalSlab += slab;
        }
        jlblSlab.setText(String.valueOf(totalSlab));
        jlblQty.setText(lb.Convert2DecFmt(qty));
        jlblAmt.setText(lb.getIndianFormat(amt));
        jlblAmtDollar.setText(lb.getIndianFormat(amtDollar));

        double discPer = lb.replaceAll(jtxtDiscPer.getText());
        double discRs = amt * (discPer / 100);
        jtxtDiscRs.setText(lb.getIndianFormat(discRs));
        double total_amt = (amt - discRs);
        jtxtTotalAmt.setText(lb.getIndianFormat(total_amt));
        double bill_amt = total_amt;
        jtxtBillAmount.setText(lb.getIndianFormat(bill_amt));
        double adj = lb.replaceAll(jtxtAdjustAmt.getText());
        double net_amt = bill_amt + adj;
        jtxtNetAmt.setText(lb.getIndianFormat(net_amt));
    }

    private void setTextfieldsAtBottom() {
        JComponent[] header = new JComponent[]{null, jtxtMainCategory, jtxtSubCategory, jtxtGradeCategory, jtxtSlab, jtxtQty, jtxtRate, jtxtAmt, jtxtRateDollar, jtxtAmtDollar};
        JComponent[] footer = new JComponent[]{null, null, null, null, jlblSlab, jlblQty, null, jlblAmt, null, jlblAmtDollar};
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
        jlblQty = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jlblUser = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jlblTimeStamp = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jlblStart = new javax.swing.JLabel();
        jtxtVoucher = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jtxtInvoiceNo = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jBillDateBtn = new javax.swing.JButton();
        jtxtVDate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtACAlias = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jtxtPDate = new javax.swing.JTextField();
        jBillDateBtn1 = new javax.swing.JButton();
        jbtnAdd = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jtxtRateDollarRs = new javax.swing.JTextField();
        jlblAmt = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jtxtRemarks = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jtxtDiscPer = new javax.swing.JTextField();
        jtxtDiscRs = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jtxtAdjustAmt = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jtxtTotalAmt = new javax.swing.JLabel();
        jtxtBillAmount = new javax.swing.JLabel();
        jtxtNetAmt = new javax.swing.JLabel();
        jtxtAmt = new javax.swing.JTextField();
        jtxtRate = new javax.swing.JTextField();
        jtxtQty = new javax.swing.JTextField();
        jtxtGradeCategory = new javax.swing.JTextField();
        jtxtparticulars = new javax.swing.JTextArea();
        jbtnClear = new javax.swing.JButton();
        jbtnDelete = new javax.swing.JButton();
        jtxtSubCategory = new javax.swing.JTextField();
        jtxtMainCategory = new javax.swing.JTextField();
        jtxtSlab = new javax.swing.JTextField();
        jlblSlab = new javax.swing.JLabel();
        jtxtRateDollar = new javax.swing.JTextField();
        jtxtAmtDollar = new javax.swing.JTextField();
        jlblAmtDollar = new javax.swing.JLabel();

        setBackground(new java.awt.Color(211, 226, 245));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(53, 154, 141)));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(new java.awt.Color(253, 243, 243));
        jScrollPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(4, 110, 152), 1, true));
        jScrollPane1.setOpaque(false);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(452, 388));

        jTable1.setBackground(new java.awt.Color(253, 243, 243));
        jTable1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sr No.", "Main Category", "Sub Category", "Grade Category", "Slab", "Qty", "Rate", "Amount", "Rate (Dollar)", "Amount (Dollar)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(5);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(170);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(170);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(170);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(9).setResizable(false);
            jTable1.getColumnModel().getColumn(9).setPreferredWidth(100);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setBackground(new java.awt.Color(253, 243, 243));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 1, 1, new java.awt.Color(235, 35, 35)));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jlblQty.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblQty.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblQty.setText("0.00");
        jlblQty.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jlblQty.setMaximumSize(new java.awt.Dimension(24, 20));
        jlblQty.setMinimumSize(new java.awt.Dimension(24, 20));
        jlblQty.setPreferredSize(new java.awt.Dimension(24, 20));
        jlblQty.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jlblQtyComponentMoved(evt);
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

        jLabel22.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel22.setText("Voucher No.:");

        jlblStart.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlblStart.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblStart.setText("TI");
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

        jLabel25.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel25.setText("Invoice No.");

        jtxtInvoiceNo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtInvoiceNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtInvoiceNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtInvoiceNoFocusGained(evt);
            }
        });
        jtxtInvoiceNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtInvoiceNoKeyPressed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 0, 0));
        jLabel23.setText("A/C Name :");

        jBillDateBtn.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });

        jtxtVDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtVDate.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtVDate.setNextFocusableComponent(jtxtACAlias);
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

        jtxtACAlias.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtACAlias.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtACAlias.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtACAliasFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtACAliasFocusLost(evt);
            }
        });
        jtxtACAlias.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jtxtACAliasComponentResized(evt);
            }
        });
        jtxtACAlias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtACAliasKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtACAliasKeyReleased(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel11.setText("P Date");

        jtxtPDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtPDate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtPDate.setNextFocusableComponent(jtxtACAlias);
        jtxtPDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtPDateFocusLost(evt);
            }
        });
        jtxtPDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPDateKeyPressed(evt);
            }
        });

        jBillDateBtn1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jBillDateBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn1ActionPerformed(evt);
            }
        });

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

        jLabel6.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jLabel6.setText("Dollar Rate : ");

        jtxtRateDollarRs.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtRateDollarRs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
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
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jlblStart, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtVDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtInvoiceNo, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtPDate, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 135, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jtxtACAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtRateDollarRs, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel22, jLabel23, jLabel25});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtPDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtInvoiceNo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jBillDateBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jtxtVDate)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addComponent(jtxtVoucher, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlblStart, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtRateDollarRs, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jtxtACAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jLabel2, jtxtVDate});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn1, jLabel11, jLabel23, jLabel25, jLabel6, jbtnAdd, jtxtACAlias, jtxtInvoiceNo, jtxtPDate});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel22, jlblStart, jtxtVoucher});

        jlblAmt.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblAmt.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblAmt.setText("0.00");
        jlblAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jlblAmt.setMaximumSize(new java.awt.Dimension(24, 20));
        jlblAmt.setMinimumSize(new java.awt.Dimension(24, 20));
        jlblAmt.setPreferredSize(new java.awt.Dimension(24, 20));
        jlblAmt.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jlblAmtComponentMoved(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel1.setText("Naration : ");

        jtxtRemarks.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtRemarks.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtRemarks.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRemarksFocusGained(evt);
            }
        });
        jtxtRemarks.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRemarksKeyPressed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(215, 227, 208));
        jPanel4.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(53, 154, 141)));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("=");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Total Amount");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Discount(Rs.)");

        jtxtDiscPer.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtDiscPer.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtDiscPer.setText("0.00");
        jtxtDiscPer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtDiscPer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDiscPerFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDiscPerFocusLost(evt);
            }
        });
        jtxtDiscPer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDiscPerKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtDiscPerKeyTyped(evt);
            }
        });

        jtxtDiscRs.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtDiscRs.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtDiscRs.setText("0.00");
        jtxtDiscRs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtDiscRs.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDiscRsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDiscRsFocusLost(evt);
            }
        });
        jtxtDiscRs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDiscRsKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtDiscRsKeyTyped(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("-");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setText("Bill Amount");

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("=");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Disc(%)");

        jtxtAdjustAmt.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtAdjustAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtAdjustAmt.setText("0.00");
        jtxtAdjustAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAdjustAmt.setMinimumSize(new java.awt.Dimension(2, 20));
        jtxtAdjustAmt.setPreferredSize(new java.awt.Dimension(27, 20));
        jtxtAdjustAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAdjustAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAdjustAmtFocusLost(evt);
            }
        });
        jtxtAdjustAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAdjustAmtKeyPressed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("+/-");

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel16.setText("AdjustMent");

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

        jtxtTotalAmt.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtTotalAmt.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jtxtTotalAmt.setText("0.00");
        jtxtTotalAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jtxtBillAmount.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtBillAmount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jtxtBillAmount.setText("0.00");
        jtxtBillAmount.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jtxtNetAmt.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jtxtNetAmt.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jtxtNetAmt.setText("0.00");
        jtxtNetAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtDiscPer, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtDiscRs, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .addComponent(jtxtTotalAmt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtBillAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtAdjustAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .addComponent(jtxtNetAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jtxtAdjustAmt, jtxtBillAmount, jtxtDiscRs, jtxtNetAmt, jtxtTotalAmt});

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel18, jLabel20, jLabel26, jLabel27, jLabel30});

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel14, jLabel16, jLabel4, jLabel5, jLabel9});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtDiscPer)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtDiscRs))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtTotalAmt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtBillAmount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtAdjustAmt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtNetAmt)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel14, jLabel16, jLabel18, jLabel20, jLabel26, jLabel27, jLabel3, jLabel30, jLabel4, jLabel5, jLabel9, jtxtAdjustAmt, jtxtBillAmount, jtxtDiscPer, jtxtDiscRs, jtxtNetAmt, jtxtTotalAmt});

        jtxtAmt.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAmt.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtAmt.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAmtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAmtFocusLost(evt);
            }
        });
        jtxtAmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAmtKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAmtKeyTyped(evt);
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

        jtxtQty.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtQty.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtQty.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtQty.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtQty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtQtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtQtyFocusLost(evt);
            }
        });
        jtxtQty.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtQtyKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtQtyKeyTyped(evt);
            }
        });

        jtxtGradeCategory.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtGradeCategory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtGradeCategory.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtGradeCategory.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtGradeCategory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtGradeCategoryFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtGradeCategoryFocusLost(evt);
            }
        });
        jtxtGradeCategory.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jtxtGradeCategoryComponentResized(evt);
            }
        });
        jtxtGradeCategory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtGradeCategoryKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtGradeCategoryKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtGradeCategoryKeyTyped(evt);
            }
        });

        jtxtparticulars.setColumns(20);
        jtxtparticulars.setRows(5);

        jbtnClear.setBackground(new java.awt.Color(204, 255, 204));
        jbtnClear.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnClear.setForeground(new java.awt.Color(235, 35, 35));
        jbtnClear.setText("CLEAR");
        jbtnClear.setDisplayedMnemonicIndex(0);
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

        jbtnDelete.setBackground(new java.awt.Color(204, 255, 204));
        jbtnDelete.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnDelete.setForeground(new java.awt.Color(235, 35, 35));
        jbtnDelete.setText("DELETE");
        jbtnDelete.setDisplayedMnemonicIndex(0);
        jbtnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteActionPerformed(evt);
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
        jtxtSubCategory.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jtxtSubCategoryComponentResized(evt);
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

        jtxtSlab.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtSlab.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtSlab.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtSlab.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtSlab.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtSlabFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtSlabFocusLost(evt);
            }
        });
        jtxtSlab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtSlabKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtSlabKeyTyped(evt);
            }
        });

        jlblSlab.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblSlab.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblSlab.setText("0.00");
        jlblSlab.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jlblSlab.setMaximumSize(new java.awt.Dimension(24, 20));
        jlblSlab.setMinimumSize(new java.awt.Dimension(24, 20));
        jlblSlab.setPreferredSize(new java.awt.Dimension(24, 20));
        jlblSlab.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jlblSlabComponentMoved(evt);
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

        jtxtAmtDollar.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtAmtDollar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAmtDollar.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtAmtDollar.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtAmtDollar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAmtDollarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAmtDollarFocusLost(evt);
            }
        });
        jtxtAmtDollar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAmtDollarKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAmtDollarKeyTyped(evt);
            }
        });

        jlblAmtDollar.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblAmtDollar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblAmtDollar.setText("0.00");
        jlblAmtDollar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jlblAmtDollar.setMaximumSize(new java.awt.Dimension(24, 20));
        jlblAmtDollar.setMinimumSize(new java.awt.Dimension(24, 20));
        jlblAmtDollar.setPreferredSize(new java.awt.Dimension(24, 20));
        jlblAmtDollar.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jlblAmtDollarComponentMoved(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jtxtMainCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtSubCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtGradeCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtSlab, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtQty, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtRate, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtRateDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAmtDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jtxtparticulars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlblSlab, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblQty, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67)
                        .addComponent(jlblAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(91, 91, 91)
                        .addComponent(jlblAmtDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtRemarks))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jbtnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
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
                        .addComponent(jlblTimeStamp, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtRateDollar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtAmtDollar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtMainCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtSubCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtGradeCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtSlab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblQty, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtparticulars, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblSlab, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblAmtDollar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jbtnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jtxtRemarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblTimeStamp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jlblAmt, jlblQty});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jtxtAmt, jtxtGradeCategory, jtxtMainCategory, jtxtQty, jtxtRate, jtxtSubCategory});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jbtnClear, jbtnDelete});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jtxtRemarks});

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
                    jtxtGradeCategory.setText(jTable1.getValueAt(rowSel, 3).toString());
                    jtxtSlab.setText(jTable1.getValueAt(rowSel, 4).toString());
                    jtxtQty.setText(jTable1.getValueAt(rowSel, 5).toString());
                    jtxtRate.setText(jTable1.getValueAt(rowSel, 6).toString());
                    jtxtAmt.setText(jTable1.getValueAt(rowSel, 7).toString());
                    jtxtRateDollar.setText(jTable1.getValueAt(rowSel, 8).toString());
                    jtxtAmtDollar.setText(jTable1.getValueAt(rowSel, 9).toString());
                    jtxtMainCategory.requestFocusInWindow();
                }
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jtxtVDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtVDateFocusGained

    private void jtxtVDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVDateFocusLost
        if(lb.checkDate(jtxtVDate)) {
            //jlblDay.setText(lb.setDay(jtxtVDate));
        } else {
            navLoad.setMessage(Constants.CORRECT_DATE);
        }
    }//GEN-LAST:event_jtxtVDateFocusLost

    private void jtxtVDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVDateKeyPressed
        lb.enterFocus(evt, jtxtPDate);
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

    private void jtxtACAliasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtACAliasFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtACAliasFocusGained

    private void jtxtACAliasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtACAliasKeyPressed
        accountPickList.setLocation(jtxtACAlias.getX() + jPanel3.getX(), jtxtACAlias.getY() + jtxtACAlias.getHeight() + jPanel3.getY());
        accountPickList.pickListKeyPress(evt);
        lb.downFocus(evt, navLoad);
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jtxtRateDollarRs.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtACAliasKeyPressed

    private void jtxtACAliasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtACAliasKeyReleased
        try {
            accountPickList.setReturnComponent(new JTextField[]{jtxtACAlias});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM account_master WHERE name LIKE '%"+ jtxtACAlias.getText().toUpperCase() +"%'");
            accountPickList.setValidation(dataConnection.prepareStatement("SELECT name FROM account_master WHERE name = ?"));
            accountPickList.setPreparedStatement(pstLocal);
            accountPickList.setNextComponent(jtxtRateDollarRs);
            accountPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtACAliasKeyReleased In Tax Invoice", ex);
        }
    }//GEN-LAST:event_jtxtACAliasKeyReleased

    private void jtxtACAliasFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtACAliasFocusLost
        accountPickList.setVisible(false);
        if(!lb.isExist("account_master", "name", jtxtACAlias.getText())) {
            navLoad.setMessage(Constants.INVALID_ACCOUNT);
            jtxtACAlias.requestFocusInWindow();
        } else {
            navLoad.setMessage("");
            String sql = "SELECT transaction_id, check_post FROM account_master WHERE ac_name LIKE '%"+ jtxtACAlias.getText() +"%'";
            try {
                PreparedStatement psLocal = dataConnection.prepareStatement(sql);
                ResultSet rsLocal = psLocal.executeQuery();
                if(rsLocal.next()) {
                    
                }
            } catch(Exception ex) {
                lb.printToLogFile("Exception at acalias in Tax Invoice", ex);
            }
        }
        
        jtxtQty.setText("");
    }//GEN-LAST:event_jtxtACAliasFocusLost

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
            if(lb.isExist("sale_bill_head", "ref_no", initial + jtxtVoucher.getText(), dataConnection)) {
                ref_no = initial + jtxtVoucher.getText();
                navLoad.setMessage("");
                navLoad.setVoucher("Edit");
            } else {
                navLoad.setMessage(Constants.INVALID_VOUCHER_NO);
            }
            jtxtVoucher.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtVoucherKeyPressed

    private void jtxtVoucherKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVoucherKeyTyped
        lb.onlyNumber(evt, (7 - initial.length()));
    }//GEN-LAST:event_jtxtVoucherKeyTyped

    private void jtxtInvoiceNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtInvoiceNoFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtInvoiceNoFocusGained

    private void jtxtInvoiceNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtInvoiceNoKeyPressed
        //lb.enterFocus(evt, jtxtLRNo);
    }//GEN-LAST:event_jtxtInvoiceNoKeyPressed

    private void jtxtRemarksFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRemarksFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRemarksFocusGained

    private void jtxtRemarksKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRemarksKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtRemarksKeyPressed

    private void jtxtDiscPerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDiscPerFocusGained
        jtxtDiscPer.selectAll();
    }//GEN-LAST:event_jtxtDiscPerFocusGained

    private void jtxtDiscPerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDiscPerKeyPressed
        lb.enterEvent(evt, jtxtDiscRs);
    }//GEN-LAST:event_jtxtDiscPerKeyPressed

    private void jtxtAdjustAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAdjustAmtFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAdjustAmtFocusGained

    private void jtxtAdjustAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAdjustAmtFocusLost
        updateLabel();
    }//GEN-LAST:event_jtxtAdjustAmtFocusLost

    private void jtxtAdjustAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAdjustAmtKeyPressed
        lb.enterEvent(evt, jtxtRemarks);
    }//GEN-LAST:event_jtxtAdjustAmtKeyPressed

    private void jtxtDiscPerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDiscPerFocusLost
        updateLabel();
    }//GEN-LAST:event_jtxtDiscPerFocusLost

    private void jlblAmtComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblAmtComponentMoved
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jlblAmtComponentMoved

    private void jlblQtyComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblQtyComponentMoved
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jlblQtyComponentMoved

    private void jtxtACAliasComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtxtACAliasComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jtxtACAliasComponentResized

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_formComponentResized

    private void jtxtDiscRsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDiscRsFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDiscRsFocusGained

    private void jtxtDiscRsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDiscRsFocusLost
        double amt = lb.replaceAll(jlblAmt.getText());
        double discRs = lb.replaceAll(jtxtDiscRs.getText());
        double discPer = (discRs * 100) / amt;
        jtxtDiscRs.setText(lb.getIndianFormat(discRs));
        jtxtDiscPer.setText(lb.getIndianFormat(discPer));
        double total_amt = (amt - discRs);
        jtxtTotalAmt.setText(lb.getIndianFormat(total_amt));
        double bill_amt = total_amt;
        jtxtBillAmount.setText(lb.getIndianFormat(bill_amt));
        double adj = lb.replaceAll(jtxtAdjustAmt.getText());
        double net_amt = bill_amt + adj;
        jtxtNetAmt.setText(lb.getIndianFormat(net_amt));
    }//GEN-LAST:event_jtxtDiscRsFocusLost

    private void jtxtDiscRsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDiscRsKeyPressed
        lb.enterEvent(evt, jtxtAdjustAmt);
    }//GEN-LAST:event_jtxtDiscRsKeyPressed

    private void jtxtDiscPerKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDiscPerKeyTyped
        lb.onlyNumber(evt, 10);
    }//GEN-LAST:event_jtxtDiscPerKeyTyped

    private void jtxtDiscRsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDiscRsKeyTyped
        lb.onlyNumber(evt, 10);
    }//GEN-LAST:event_jtxtDiscRsKeyTyped

    private void jtxtPDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPDateFocusLost
        if(lb.checkDate(jtxtPDate)){
//            jlblDay.setText(lb.setDay(jtxtPDate));
        } else {
            navLoad.setMessage(Constants.CORRECT_DATE);
        }
    }//GEN-LAST:event_jtxtPDateFocusLost

    private void jtxtPDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPDateFocusGained

    private void jtxtPDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPDateKeyPressed
        lb.enterEvent(evt, jtxtACAlias);
    }//GEN-LAST:event_jtxtPDateKeyPressed

    private void jBillDateBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtn1ActionPerformed
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtPDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtPDate.getX(), jtxtPDate.getY() + 145, jtxtPDate.getX() + odc.getWidth(), jtxtPDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, Constants.SELECT_DATE);
    }//GEN-LAST:event_jBillDateBtn1ActionPerformed

    private void jtxtAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmtFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAmtFocusGained

    private void jtxtAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmtFocusLost
        calculation();
    }//GEN-LAST:event_jtxtAmtFocusLost

    private void jtxtAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmtKeyPressed

    }//GEN-LAST:event_jtxtAmtKeyPressed

    private void jtxtAmtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmtKeyTyped
        lb.onlyNumber(evt, 15);
    }//GEN-LAST:event_jtxtAmtKeyTyped

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

    private void jtxtQtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtQtyFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtQtyFocusGained

    private void jtxtQtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtQtyFocusLost
        calculation();
    }//GEN-LAST:event_jtxtQtyFocusLost

    private void jtxtQtyKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtQtyKeyPressed
        lb.enterEvent(evt, jtxtRate);
    }//GEN-LAST:event_jtxtQtyKeyPressed

    private void jtxtQtyKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtQtyKeyTyped
        lb.onlyNumber(evt, 15);
    }//GEN-LAST:event_jtxtQtyKeyTyped

    private void jtxtGradeCategoryComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtxtGradeCategoryComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jtxtGradeCategoryComponentResized

    private void jtxtGradeCategoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGradeCategoryFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtGradeCategoryFocusGained

    private void jtxtGradeCategoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGradeCategoryFocusLost
        itemPickList.setVisible(false);
        try {
            PreparedStatement psLocal = null;
            ResultSet rsLocal = null;
            String sql = "SELECT rate FROM slab_category WHERE name LIKE '%"+ jtxtGradeCategory.getText() +"%'";
            psLocal = dataConnection.prepareStatement(sql);
            rsLocal = psLocal.executeQuery();
            if(rsLocal.next()) {
                jtxtRate.setText(lb.getIndianFormat(lb.replaceAll(rsLocal.getString("rate"))));
            }
        } catch(Exception ex) {
            lb.printToLogFile("Exception at jtxtItemNameFocusLost In Tax Invoice", ex);
        }
    }//GEN-LAST:event_jtxtGradeCategoryFocusLost

    private void jtxtGradeCategoryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGradeCategoryKeyPressed
        itemPickList.setLocation(jtxtGradeCategory.getX(), jtxtGradeCategory.getY() + jtxtGradeCategory.getHeight());
        itemPickList.pickListKeyPress(evt);
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            if (evt.getModifiers() == KeyEvent.SHIFT_MASK) {
                evt.consume();
                jtxtDiscPer.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jtxtGradeCategoryKeyPressed

    private void jtxtGradeCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGradeCategoryKeyReleased
        try {
            itemPickList.setReturnComponent(new JTextField[]{jtxtGradeCategory});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM slab_category "
                    + " WHERE fk_sub_category_id = '"+ lb.getSubCategory(jtxtSubCategory.getText(), "C") +"' AND name LIKE '%" + jtxtGradeCategory.getText() +"%' GROUP BY id ORDER BY id");
            itemPickList.setValidation(dataConnection.prepareStatement("SELECT name FROM slab_category WHERE name = ?"));
            itemPickList.setFirstAssociation(new int[]{0});
            itemPickList.setSecondAssociation(new int[]{0});
            itemPickList.setPreparedStatement(pstLocal);
            itemPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtItemNameKeyReleased In Tax Invoice", ex);
        }
    }//GEN-LAST:event_jtxtGradeCategoryKeyReleased

    private void jtxtGradeCategoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGradeCategoryKeyTyped
        lb.fixLength(evt, 200);
    }//GEN-LAST:event_jtxtGradeCategoryKeyTyped

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        int rowSel = jTable1.getSelectedRow();
        if(reValidate()){
            if(rowSel == -1) {
                Vector row = new Vector();
                row.add("");
                row.add(jtxtMainCategory.getText());
                row.add(jtxtSubCategory.getText());
                row.add(jtxtGradeCategory.getText());
                row.add(Integer.parseInt(jtxtSlab.getText()));
                row.add(lb.Convert2DecFmt(lb.replaceAll(jtxtQty.getText())));
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtRate.getText())));
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtAmt.getText())));
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtRateDollar.getText())));
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtAmtDollar.getText())));
                dtm.addRow(row);
            } else {
                jTable1.setValueAt(jtxtMainCategory.getText(), rowSel, 1);
                jTable1.setValueAt(jtxtSubCategory.getText(), rowSel, 2);
                jTable1.setValueAt(jtxtGradeCategory.getText(), rowSel, 3);
                jTable1.setValueAt(jtxtSlab.getText(), rowSel, 4);
                jTable1.setValueAt(lb.Convert2DecFmt(lb.replaceAll(jtxtQty.getText())), rowSel, 5);
                jTable1.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtRate.getText())), rowSel, 6);
                jTable1.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtAmt.getText())), rowSel, 7);
                jTable1.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtRateDollar.getText())), rowSel, 8);
                jTable1.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtAmtDollar.getText())), rowSel, 9);
            }
            updateLabel();
            clear();
            jTable1.clearSelection();
            if (JOptionPane.showConfirmDialog(this, Constants.ADD_MORE_ENTRY, DeskFrame.TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                jtxtMainCategory.requestFocusInWindow();
            } else {
                jtxtDiscPer.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jbtnClearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnClearMouseClicked
        jTable1.clearSelection();
    }//GEN-LAST:event_jbtnClearMouseClicked

    private void jbtnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnClearActionPerformed
        jTable1.clearSelection();
    }//GEN-LAST:event_jbtnClearActionPerformed

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

    private void jtxtSubCategoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSubCategoryFocusLost
        subCategoryPickList.setVisible(false);
    }//GEN-LAST:event_jtxtSubCategoryFocusLost

    private void jtxtSubCategoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSubCategoryFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtSubCategoryFocusGained

    private void jtxtSubCategoryComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtxtSubCategoryComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jtxtSubCategoryComponentResized

    private void jtxtSubCategoryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSubCategoryKeyPressed
        subCategoryPickList.setLocation(jtxtSubCategory.getX() + jPanel3.getX(), jtxtSubCategory.getY() + jtxtSubCategory.getHeight() + jPanel3.getY());
        subCategoryPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtSubCategoryKeyPressed

    private void jtxtSubCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSubCategoryKeyReleased
        try {
            String sql = "SELECT name FROM sub_category WHERE fk_main_category_id = '"+ lb.getMainCategory(jtxtMainCategory.getText(), "C") 
                +"' AND status = 0 AND name LIKE '%"+ jtxtSubCategory.getText() +"%'";
            subCategoryPickList.setReturnComponent(new JTextField[]{jtxtSubCategory});
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            subCategoryPickList.setValidation(dataConnection.prepareStatement("SELECT name FROM sub_category WHERE status = 0 AND name = ?"));
            subCategoryPickList.setFirstAssociation(new int[]{0});
            subCategoryPickList.setSecondAssociation(new int[]{0});
            subCategoryPickList.setPreparedStatement(pstLocal);
            subCategoryPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtMainCategoryKeyReleased In Break Up", ex);
        }
    }//GEN-LAST:event_jtxtSubCategoryKeyReleased

    private void jtxtSubCategoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSubCategoryKeyTyped
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtSubCategoryKeyTyped

    private void jtxtMainCategoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMainCategoryFocusLost
        mainCategoryPickList.setVisible(false);
    }//GEN-LAST:event_jtxtMainCategoryFocusLost

    private void jtxtMainCategoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMainCategoryFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMainCategoryFocusGained

    private void jtxtMainCategoryComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtxtMainCategoryComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jtxtMainCategoryComponentResized

    private void jtxtMainCategoryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainCategoryKeyPressed
        mainCategoryPickList.setLocation(jtxtMainCategory.getX() + jPanel3.getX(), jtxtMainCategory.getY() + jtxtMainCategory.getHeight() + jPanel3.getY());
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
            lb.printToLogFile("Exception at jtxtMainCategoryKeyReleased In Break Up", ex);
        }
    }//GEN-LAST:event_jtxtMainCategoryKeyReleased

    private void jtxtMainCategoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainCategoryKeyTyped
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtMainCategoryKeyTyped

    private void jtxtSlabFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSlabFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtSlabFocusGained

    private void jtxtSlabFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSlabFocusLost
        calculation();
    }//GEN-LAST:event_jtxtSlabFocusLost

    private void jtxtSlabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSlabKeyPressed
        lb.enterEvent(evt, jtxtRate);
    }//GEN-LAST:event_jtxtSlabKeyPressed

    private void jtxtSlabKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSlabKeyTyped
        lb.onlyInteger(evt, 10);
    }//GEN-LAST:event_jtxtSlabKeyTyped

    private void jlblSlabComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblSlabComponentMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_jlblSlabComponentMoved

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

    private void jtxtAmtDollarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmtDollarFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAmtDollarFocusGained

    private void jtxtAmtDollarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmtDollarFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtAmtDollarFocusLost

    private void jtxtAmtDollarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmtDollarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtAmtDollarKeyPressed

    private void jtxtAmtDollarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmtDollarKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtAmtDollarKeyTyped

    private void jlblAmtDollarComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblAmtDollarComponentMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_jlblAmtDollarComponentMoved

    private void jtxtRateDollarRsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateDollarRsFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRateDollarRsFocusGained

    private void jtxtRateDollarRsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateDollarRsKeyPressed
        lb.enterFocus(evt, jtxtMainCategory);
    }//GEN-LAST:event_jtxtRateDollarRsKeyPressed

    private void jtxtRateDollarRsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateDollarRsFocusLost
        rateDollarRs = Double.valueOf(jtxtRateDollarRs.getText());
    }//GEN-LAST:event_jtxtRateDollarRsFocusLost

    private void jtxtRateDollarRsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateDollarRsKeyTyped
        lb.onlyNumber(evt, 15);
    }//GEN-LAST:event_jtxtRateDollarRsKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jBillDateBtn1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
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
    private javax.swing.JLabel jlblAmt;
    private javax.swing.JLabel jlblAmtDollar;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblQty;
    private javax.swing.JLabel jlblSlab;
    private javax.swing.JLabel jlblStart;
    private javax.swing.JLabel jlblTimeStamp;
    private javax.swing.JLabel jlblUser;
    private javax.swing.JTextField jtxtACAlias;
    private javax.swing.JTextField jtxtAdjustAmt;
    private javax.swing.JTextField jtxtAmt;
    private javax.swing.JTextField jtxtAmtDollar;
    private javax.swing.JLabel jtxtBillAmount;
    private javax.swing.JTextField jtxtDiscPer;
    private javax.swing.JTextField jtxtDiscRs;
    private javax.swing.JTextField jtxtGradeCategory;
    private javax.swing.JTextField jtxtInvoiceNo;
    private javax.swing.JTextField jtxtMainCategory;
    private javax.swing.JLabel jtxtNetAmt;
    private javax.swing.JTextField jtxtPDate;
    private javax.swing.JTextField jtxtQty;
    private javax.swing.JTextField jtxtRate;
    private javax.swing.JTextField jtxtRateDollar;
    private javax.swing.JTextField jtxtRateDollarRs;
    private javax.swing.JTextField jtxtRemarks;
    private javax.swing.JTextField jtxtSlab;
    private javax.swing.JTextField jtxtSubCategory;
    private javax.swing.JLabel jtxtTotalAmt;
    private javax.swing.JTextField jtxtVDate;
    private javax.swing.JTextField jtxtVoucher;
    public static javax.swing.JTextArea jtxtparticulars;
    // End of variables declaration//GEN-END:variables
}