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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import oldbupdate.SalesBillUpdate;
import dhananistockmanagement.MainClass;
import support.EmailSelect;
import support.HeaderIntFrame1;
import support.Library;
import support.NavigationPanel1;
import support.OurDateChooser;
import support.PickList;
import support.ReportTable;
import support.VoucherDisplay;
import static dhananistockmanagement.DeskFrame.SLS_CHR_LBL;
import support.Constants;

/**
 *
 * @author @JD@
 */
public class SalesBill extends javax.swing.JInternalFrame {
    NavigationPanel1 navLoad = null;
    Library lb = new Library();
    DefaultTableModel dtm = null, dtmOld = null;
    private String ref_no = "";
    private ReportTable taxInvoiceView = null;
    Connection dataConnection = DeskFrame.connMpAdmin;
    private int type = 0;
    private String initial = Constants.SALES_BILL_INITIAL;
    private PickList accountPickList = null, itemPickList = null, unitPickList = null, cgstPickList = null, sgstPickList = null;
    String Syspath = System.getProperty("user.dir");
    private String view_title = Constants.SALES_BILL_FORM_NAME;

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
        jcmbType.setSelectedIndex(type);
        setInitial();
        connectToNavigation();
        lb.setDateChooserProperty(jtxtVDate);
        jlblDay.setText(lb.setDay(jtxtVDate));
        dtm = (DefaultTableModel) jTable1.getModel();
        dtmOld = (DefaultTableModel) jTableOld.getModel();
        dataConnection = DeskFrame.connMpAdmin;
        makeChildTableSalesBill();
        setPickListView();
        setIconToPnael();
        setcellRender();
        jTable1.setBackground(new Color(253, 243, 243));
        jTableOld.setBackground(new Color(215, 227, 208));
        checkedVatShow();
        setTitle(Constants.SALES_BILL_FORM_NAME);
        jbtnEmail.setText(Constants.EMAIL_BUTTON);
    }

    private void setcellRender() {
        jTableOld.getColumnModel().getColumn(0).setCellRenderer(lb.new StatusColumnCellRenderer());
        jTableOld.getColumnModel().getColumn(1).setCellRenderer(lb.new StatusColumnCellRenderer());
        jTableOld.getColumnModel().getColumn(2).setCellRenderer(lb.new StatusColumnCellRenderer());
    }

    private void setIconToPnael() {
        Syspath += File.separator + "Resources" + File.separator + "Images" + File.separator;
        jbtnAdd.setIcon(new ImageIcon(Syspath +"Add.png"));
        jbtnEmail.setIcon(new ImageIcon(Syspath +"E-mail.png"));
        jbtnChiddi.setIcon(new ImageIcon(Syspath +"chiddi.png"));
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
        accountPickList.setNextComponent(jcmbPmt);

        itemPickList = new PickList(dataConnection);

        itemPickList.setLayer(getLayeredPane());
        itemPickList.setPickListComponent(jtxtItemName);
        itemPickList.setNextComponent(jtxtHSNCode);
        itemPickList.setReturnComponent(new JTextField[]{jtxtItemName, jtxtHSNCode, jtxtHSNCode, jtxtSGst, jtxtSGstAmt, jtxtCGst, jtxtCGstAmt});

        unitPickList = new PickList(dataConnection);

        unitPickList.setLayer(getLayeredPane());
        unitPickList.setPickListComponent(jtxtUnitName);
        unitPickList.setNextComponent(jtxtRate);

        cgstPickList = new PickList(dataConnection);

        cgstPickList.setLayer(getLayeredPane());
        cgstPickList.setPickListComponent(jtxtCGst);
        cgstPickList.setNextComponent(jtxtAmt);

        sgstPickList = new PickList(dataConnection);

        sgstPickList.setLayer(getLayeredPane());
        sgstPickList.setPickListComponent(jtxtSGst);
        sgstPickList.setNextComponent(jtxtCGst);
    }

    private void makeChildTableSalesBill() {
        taxInvoiceView = new ReportTable();

        taxInvoiceView.AddColumn(0, "Voucher No", 100, java.lang.String.class, null, false);
        taxInvoiceView.AddColumn(1, "Voucher Date", 100, java.lang.String.class, null, false);
        taxInvoiceView.AddColumn(2, "Account Name", 200, java.lang.String.class, null, false);
        taxInvoiceView.AddColumn(3, "Payment Mode", 150, java.lang.String.class, null, false);
        taxInvoiceView.AddColumn(4, "Bill No", 150, java.lang.String.class, null, false);
        taxInvoiceView.AddColumn(5, "Net Amount", 150, java.lang.Double.class, null, false);
        taxInvoiceView.makeTable();
    }

    private void checkedVatShow() {
        jLabel33.setText(SLS_CHR_LBL);
    }

    private void calculation() {
        double qty = lb.replaceAll(jtxtQty.getText());
        double rate = lb.replaceAll(jtxtRate.getText());
        double disc = lb.replaceAll(jtxtDisc.getText());
        double amt = (qty * rate);
        double discRs = amt * (disc / 100);
        double amt_rs = amt - discRs;
        double cgstRs = (amt_rs * (Double.parseDouble(jtxtCGstAmt.getText()) / 100));
        double sgstRs = (amt_rs * (Double.parseDouble(jtxtSGstAmt.getText()) / 100));

        jtxtQty.setText(lb.Convert2DecFmt(qty));
        jtxtRate.setText(lb.getIndianFormat(rate));
        jtxtDisc.setText(lb.replaceAll(jtxtDisc.getText())+"");
        jtxtAmt.setText(lb.getIndianFormat(amt_rs + cgstRs + sgstRs));
    }

    private boolean reValidate(){
        boolean flag = true;

        if(lb.isBlank(jtxtItemName)) {
            navLoad.setMessage(Constants.INVALID_ITEM);
            jtxtItemName.requestFocusInWindow();
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

        if(lb.replaceAll(jtxtDisc.getText()) < 0) {
            navLoad.setMessage(Constants.INVALID_DISCOUNT);
            jtxtDisc.requestFocusInWindow();
            flag = flag && false;
        }

        if(lb.isBlank(jtxtSGst)) {
            navLoad.setMessage(Constants.INVALID_S_GST);
            jtxtSGst.requestFocusInWindow();
            flag = flag && false;
        }

        if(lb.isBlank(jtxtCGst)) {
            navLoad.setMessage(Constants.INVALID_C_GST);
            jtxtCGst.requestFocusInWindow();
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
            cp.deleteEntry(ref_no);

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
                    if(lb.getData("SELECT * FROM slshd WHERE ref_no > '"+ ref_no +"' AND ptype = "+ type +"").equals("")) {
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
                    String sql = "SELECT * FROM slshd";
                    if (tag.equalsIgnoreCase("first")) {
                        sql += " WHERE ref_no = (SELECT MIN(ref_no) FROM slshd WHERE IS_DEL = 0 AND PTYPE = "+ type +")";
                    } else if (tag.equalsIgnoreCase("previous")) {
                        sql += " WHERE ref_no = (SELECT MAX(ref_no) FROM slshd WHERE ref_no < '"+ ref_no +"' AND is_del = 0 AND ptype = "+ type +")";
                    } else if (tag.equalsIgnoreCase("next")) {
                        sql += " WHERE ref_no = (SELECT MIN(ref_no) FROM slshd WHERE ref_no > '"+ ref_no +"' AND is_del = 0 AND ptype = "+ type +")";
                    } else if (tag.equalsIgnoreCase("last")) {
                        sql += " WHERE ref_no = (SELECT MAX(ref_no) FROM slshd WHERE is_del = 0 AND ptype = "+ type +")";
                    } else if (tag.equalsIgnoreCase("edit")) {
                        sql += " WHERE ref_no = '"+ ref_no +"' AND is_del = 0 AND ptype = "+ type;
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
                dtmOld.setRowCount(0);
                jtxtVoucher.setText("");
                jtxtACAlias.setText("");
                jtxtACName1.setText("");
                jtxtInvoiceNo.setText("");
                jtxtLRNo.setText("");
                jtxtTransport.setText("");
                jtxtPlace.setText("");
                jtxtTransactionId.setText("");
                jtxtCheckPost.setText("");
                jtxtRemarks.setText("");
                jtxtDiscPer.setText("0.00");
                jtxtDiscRs.setText("0.00");
                jtxtTotalAmt.setText("0.00");
                jtxtBillAmount.setText("0.00");
                jtxtLoadingCharge.setText("0.00");
                jtxtAdjustAmt.setText("0.00");
                jtxtNetAmt.setText("0.00");
                jlblQty.setText("0.00");
                jlblAmt.setText("0.00");
                jcmbPmt.setSelectedIndex(0);
                jcmbType.setSelectedIndex(type);
                jcmbCheck.setSelected(false);
                jtxtPDate.setText("");
                jtxtNoofParcel.setText("");
                jtxtInvoiceNo.setText(lb.getInvNo("slshd", "", "") + "");
                jtxtLRNo.setText(lb.getInvNo("slshd", "", "") + "");
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
                jtxtdays.setEnabled(bFlag);
                jtxtInvoiceNo.setEnabled(false);
                jtxtLRNo.setEnabled(bFlag);
                jtxtTransport.setEnabled(bFlag);
                jtxtPlace.setEnabled(bFlag);
                jtxtTransactionId.setEnabled(bFlag);
                jtxtCheckPost.setEnabled(bFlag);
                jtxtRemarks.setEnabled(bFlag);
                jtxtNoofParcel.setEnabled(bFlag);
                jcmbPmt.setEnabled(bFlag);
                jcmbType.setEnabled(false);
                jcmbCheck.setEnabled(bFlag);
                jtxtPDate.setEnabled(bFlag);
                jBillDateBtn1.setEnabled(bFlag);
                jTable1.setEnabled(bFlag);
                jtxtACAlias.setEnabled(bFlag);
                jtxtItemName.setEnabled(bFlag);
                jtxtparticulars.setEnabled(false);
                jtxtHSNCode.setEnabled(bFlag);
                jtxtQty.setEnabled(bFlag);
                jtxtUnitName.setEnabled(bFlag);
                jtxtACName1.setEnabled(bFlag);
                jtxtRate.setEnabled(bFlag);
                jtxtDisc.setEnabled(bFlag);
                jtxtSGst.setEnabled(bFlag);
                jtxtSGstAmt.setEnabled(bFlag);
                jtxtCGst.setEnabled(bFlag);
                jtxtCGstAmt.setEnabled(bFlag);
                jtxtAmt.setEnabled(bFlag);
                jtxtDiscPer.setEnabled(bFlag);
                jtxtDiscRs.setEnabled(bFlag);
                jtxtLoadingCharge.setEnabled(bFlag);
                jtxtAdjustAmt.setEnabled(bFlag);
                jTableOld.setEnabled(false);
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
                    jtxtVDate.setText(lb.ConvertDateFormetForDBForConcurrency(viewDataRs.getString("vdate")));
                    jlblDay.setText(lb.setDay(jtxtVDate));
                    jcmbPmt.setSelectedIndex(viewDataRs.getInt("pmode"));
                    jcmbType.setSelectedIndex(viewDataRs.getInt("ptype"));
                    jtxtInvoiceNo.setText(viewDataRs.getString("bill_no"));
                    jtxtACName1.setText(viewDataRs.getString("ac_name1"));
                    jtxtLRNo.setText(viewDataRs.getString("lr_no"));
                    jtxtTransport.setText(viewDataRs.getString("transport"));
                    jtxtACAlias.setText(lb.getAccountMstName(viewDataRs.getString("ac_cd"), "N"));
                    jlblQty.setText(lb.Convert2DecFmt(viewDataRs.getDouble("tot_qty")));
                    jlblAmt.setText(lb.getIndianFormat(viewDataRs.getDouble("tot_amt")));
                    jtxtDiscPer.setText(lb.getIndianFormat(viewDataRs.getDouble("disc_per")));
                    jtxtDiscRs.setText(lb.getIndianFormat(viewDataRs.getDouble("disc_rs")));
                    jtxtTotalAmt.setText(lb.getIndianFormat(viewDataRs.getDouble("amt_tot")));
                    jtxtBillAmount.setText(lb.getIndianFormat(viewDataRs.getDouble("bill_amt")));
                    jtxtLoadingCharge.setText(lb.getIndianFormat(viewDataRs.getDouble("ldng_chrg")));
                    jtxtAdjustAmt.setText(lb.getIndianFormat(viewDataRs.getDouble("adj_amt")));
                    jtxtNetAmt.setText(lb.getIndianFormat(viewDataRs.getDouble("net_amt")));
                    jtxtRemarks.setText(viewDataRs.getString("remark"));
                    jtxtNoofParcel.setText(viewDataRs.getString("no_of_prcl"));
                    jtxtPlace.setText(viewDataRs.getString("place"));
                    jtxtTransactionId.setText(viewDataRs.getString("transaction_id"));
                    jtxtCheckPost.setText(viewDataRs.getString("check_post"));
                    String p_date = viewDataRs.getString("p_date");
                    if(p_date == null) {
                        p_date = "";
                    } else {
                        p_date = lb.ConvertDateFormetForDBForConcurrency(p_date);
                    }
                    jtxtPDate.setText(p_date);
                    if(viewDataRs.getInt("chck") == 0) {
                        jcmbCheck.setSelected(false);
                    } else {
                        jcmbCheck.setSelected(true);
                    }
                    jlblUser.setText(lb.getUserName(viewDataRs.getString("user_cd"), "N"));
                    jlblEditNo.setText(viewDataRs.getString("edit_no"));
                    jlblTimeStamp.setText(lb.ConvertTimeStampFormetForDisplay(viewDataRs.getString("time_stamp")));

                    dtm.setRowCount(0);
                    dtmOld.setRowCount(0);
                    lb.setBal(viewDataRs.getString("ac_cd"), dtmOld);
                    viewDataRs = fetchData("SELECT * FROM slsdt WHERE ref_no = '"+ ref_no +"'");
                    int i = 0;
                    while (viewDataRs.next()) {
                        Vector row = new Vector();
                        row.add(++i);
//                        row.add(lb.getItemCd(viewDataRs.getString("itm_cd"), "N"));
                        row.add(viewDataRs.getString("hsn_code"));
                        row.add(lb.Convert2DecFmt(viewDataRs.getDouble("qty")));
                        row.add(lb.getUnitName(viewDataRs.getString("unt_cd"), "N"));
                        row.add(lb.getIndianFormat(viewDataRs.getDouble("rate")));
                        row.add(lb.getIndianFormat(viewDataRs.getDouble("disc")));
                        row.add(lb.getTaxCode(viewDataRs.getString("vat_cd"), "N"));
                        row.add(lb.getIndianFormat(viewDataRs.getDouble("vat_amt")));
                        row.add(lb.getTaxCode(viewDataRs.getString("add_cd"), "N"));
                        row.add(lb.getIndianFormat(viewDataRs.getDouble("add_amt")));
                        row.add(lb.getIndianFormat(viewDataRs.getDouble("amt")));
                        row.add(viewDataRs.getString("particulars"));
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

        String sql = "SELECT lh.ref_no, lh.vdate, am.ac_name AS ac_name, " +
            "CASE WHEN lh.pmode = 0 THEN 'CASH' ELSE 'CREDIT' END AS pmode, " +
            "lh.bill_no, lh.net_amt FROM slshd lh, acnt_mst am WHERE am.ac_cd = lh.ac_cd AND is_del = 0 AND ptype = "+ type +" ORDER BY ref_no";
        taxInvoiceView.setColumnValue(new int[]{1, 2, 3, 4, 5, 6});
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
            sql = "INSERT INTO slshd(vdate, bill_no, lr_no, transport, podate, ac_cd, ptype, pmode, tot_qty, tot_amt, disc_per, disc_rs, amt_tot, bill_amt, ldng_chrg, adj_amt, net_amt, remark, p_date, chck, ac_name1, fix_time, no_of_prcl, place, transaction_id, check_post, user_cd, ref_no) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '"+ new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) +"', ?, ?, ?, ?, ?, ?)";
            ref_no = lb.generateKey("slshd", "ref_no", 7, initial);
        } else if (navLoad.getMode().equalsIgnoreCase("E")) {
            SalesBillUpdate sb = new SalesBillUpdate();
            sb.deleteEntry(ref_no);

            sql = "DELETE FROM slsdt WHERE ref_no='"+ ref_no +"'";
            psLocal = dataConnection.prepareStatement(sql);
            change += psLocal.executeUpdate();

            sql = "UPDATE slshd SET vdate = ?, bill_no = ?, lr_no = ?, transport = ?, podate = ?, ac_cd = ?, ptype = ?, pmode = ?, tot_qty = ?, tot_amt = ?, disc_per = ?, disc_rs = ?, amt_tot = ?, bill_amt = ?, ldng_chrg = ?, adj_amt = ?, net_amt = ?, remark = ?, p_date = ?, chck = ?, ac_name1 = ?, no_of_prcl = ?, place = ?, transaction_id = ?, check_post = ?, user_cd = ?, edit_no = edit_no + 1, time_stamp = CURRENT_TIMESTAMP WHERE ref_no = ?";
        }
        int check = 0;
        if(jcmbCheck.isSelected()) {
            check = 1;
        } else {
            check = 0;
        }
        String p_date = jtxtPDate.getText();
        if(p_date.equalsIgnoreCase("")) {
            p_date = null;
        } else {
            p_date = lb.tempConvertFormatForDBorConcurrency(p_date);
        }
        psLocal = dataConnection.prepareStatement(sql);
        psLocal.setString(1, lb.tempConvertFormatForDBorConcurrency(jtxtVDate.getText())); // Voucher Date
        psLocal.setString(2, jtxtInvoiceNo.getText()); // Bill No
        psLocal.setString(3, jtxtLRNo.getText()); // DCH_NO
        psLocal.setString(4, jtxtTransport.getText()); // ORDER_PONO
        psLocal.setString(5, null); // PO Date
        psLocal.setString(6, lb.getAccountMstName(jtxtACAlias.getText(), "C")); // AC CD
        psLocal.setInt(7, type); // P Type
        psLocal.setInt(8, jcmbPmt.getSelectedIndex()); // P Mode
        psLocal.setDouble(9, lb.replaceAll(jlblQty.getText())); // Total Qty
        psLocal.setDouble(10, lb.replaceAll(jlblAmt.getText())); // Total Amt
        psLocal.setDouble(11, lb.replaceAll(jtxtDiscPer.getText())); // Disc Per
        psLocal.setDouble(12, lb.replaceAll(jtxtDiscRs.getText())); // Disc Rs
        psLocal.setDouble(13, lb.replaceAll(jtxtTotalAmt.getText())); // Total Amt
        psLocal.setDouble(14, lb.replaceAll(jtxtBillAmount.getText())); // Bill Amt
        psLocal.setDouble(15, lb.replaceAll(jtxtLoadingCharge.getText())); // Loading Charges
        psLocal.setDouble(16, lb.replaceAll(jtxtAdjustAmt.getText())); // Adjustment
        psLocal.setDouble(17, lb.replaceAll(jtxtNetAmt.getText())); // Net Amt
        psLocal.setString(18, jtxtRemarks.getText()); // Remarks
        psLocal.setString(19, p_date); // P_DATE
        psLocal.setInt(20, check); // CHECK
        psLocal.setString(21, jtxtACName1.getText()); // AC Name1
        psLocal.setString(22, jtxtNoofParcel.getText()); // No Of Parcel
        psLocal.setString(23, jtxtPlace.getText()); // Place
        psLocal.setString(24, jtxtTransactionId.getText()); // Transaction Id
        psLocal.setString(25, jtxtCheckPost.getText()); // Check Post
        psLocal.setInt(26, DeskFrame.user_id); // User CD
        psLocal.setString(27, ref_no); // Ref No
        change += psLocal.executeUpdate();

        sql = "INSERT INTO slsdt(sr_no, itm_cd, qty, unt_cd, rate, disc, add_cd, add_amt, vat_cd, vat_amt, igst_cd, igst_amt, amt, particulars, hsn_code, ref_no) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        psLocal = dataConnection.prepareStatement(sql);
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            String itm_cd = ""; //lb.getItemCd(jTable1.getValueAt(i, 1).toString(), "C"); // ITEM CD
            String item_name = jTable1.getValueAt(i, 1).toString(); // ITEM NAME
            String hsn_code = jTable1.getValueAt(i, 2).toString(); // HSN CODE
            double qty = lb.replaceAll(jTable1.getValueAt(i, 3).toString()); // QTY
            String unt_cd = lb.getUnitName(jTable1.getValueAt(i, 4).toString(), "C"); // UNIT CD
            double rate = lb.replaceAll(jTable1.getValueAt(i, 5).toString()); // RATE
            double disc = lb.replaceAll(jTable1.getValueAt(i, 6).toString()); // DISC
            String sgstcd = lb.getTaxCode(jTable1.getValueAt(i, 7).toString(), "C"); // S GST CD
            double sgstamt = lb.replaceAll(jTable1.getValueAt(i, 8).toString()); // S GST AMT
            String cgstcd = lb.getTaxCode(jTable1.getValueAt(i, 9).toString(), "C"); // C GST CD
            double cgstamt = lb.replaceAll(jTable1.getValueAt(i, 10).toString()); // C GST AMT
            double amount = lb.replaceAll(jTable1.getValueAt(i, 11).toString()); // AMOUNT
            String description = jTable1.getValueAt(i, 12).toString(); // DESCRIPTION
//            if(itm_cd.equalsIgnoreCase("")) {
//                itm_cd = lb.generateKey("itm_mst", "itm_cd", Constants.ITEM_MASTER_INITIAL, 7); // itm cd
//                lb.createItemName(item_name, Constants.MAIN_ITEM_FIRST, unt_cd, rate, 0.00, 0.00, 0.00, 0.00, description, hsn_code, sgstcd, cgstcd, Constants.NO_TAX, itm_cd);  // CREATE ITEM MASTER
//            }
            if(!(itm_cd.equalsIgnoreCase("0") || itm_cd.equalsIgnoreCase(""))) {
                if(lb.isExist("SELECT * FROM acntitm_mst WHERE ac_cd = '"+ lb.getAccountMstName(jtxtACAlias.getText(), "C") +"' AND itm_cd = '"+ itm_cd +"'", dataConnection)) {
                    PreparedStatement ps = dataConnection.prepareStatement("UPDATE acntitm_mst SET rate = ?, ref_no = ? WHERE ac_cd = ? AND itm_cd = ?");
                    ps.setDouble(1, rate); // RATE
                    ps.setString(2, ref_no); // REF NO
                    ps.setString(3, lb.getAccountMstName(jtxtACAlias.getText(), "C")); // ACCOUNT CD
                    ps.setString(4, itm_cd); // ITEM CD
                    ps.executeUpdate();
                } else {
                    PreparedStatement ps = dataConnection.prepareStatement("INSERT INTO acntitm_mst(ac_cd, itm_cd, rate, ref_no) VALUES(?, ?, ?, ?)");
                    ps.setString(1, lb.getAccountMstName(jtxtACAlias.getText(), "C")); // ACCOUNT CD
                    ps.setString(2, itm_cd); // ITEM CD
                    ps.setDouble(3, rate); // RATE
                    ps.setString(4, ref_no); // REF NO
                    ps.executeUpdate();
                }
                if (qty > 0) { // CONDITION QTY
                    psLocal.setInt(1, i + 1); // SR NO
                    psLocal.setString(2, itm_cd); // ITEM CD
                    psLocal.setDouble(3, qty); // QTY
                    psLocal.setString(4, unt_cd); // UNT CD
                    psLocal.setDouble(5, rate); // RATE
                    psLocal.setDouble(6, disc); // DISC
                    psLocal.setString(7, cgstcd); // C GST CD
                    psLocal.setDouble(8, cgstamt); // C GST AMT
                    psLocal.setString(9, sgstcd); // S GST CD
                    psLocal.setDouble(10, sgstamt); // S GST AMT
                    psLocal.setString(11, Constants.NO_TAX); // I GST CD
                    psLocal.setDouble(12, 0.00); // I GST AMT
                    psLocal.setDouble(13, amount); // AMT
                    psLocal.setString(14, description); // PARTICULARS
                    psLocal.setString(15, hsn_code); // HSN CODE
                    psLocal.setString(16, ref_no); // REF NO
                    change += psLocal.executeUpdate();
                }
            }
        }
        SalesBillUpdate sb = new SalesBillUpdate();
        sb.addEntry(ref_no);
        return change;
    }

    private void delete() throws SQLException {
        PreparedStatement psLocal = dataConnection.prepareStatement("DELETE FROM slshd WHERE ref_no = '"+ ref_no +"'");
        psLocal.executeUpdate();

        psLocal = dataConnection.prepareStatement("DELETE FROM slsdt WHERE ref_no = '"+ ref_no +"'");
        psLocal.executeUpdate();
        lb.closeStatement(psLocal);
    }

    private void clear() {
        jtxtItemName.setText("");
        jtxtHSNCode.setText("");
        jtxtparticulars.setText("");
        jtxtQty.setText("0.00");
        jtxtUnitName.setText("");
        jtxtRate.setText("0.00");
        jtxtDisc.setText("0.00");
        jtxtSGst.setText("");
        jtxtSGstAmt.setText("0.00");
        jtxtCGst.setText("");
        jtxtCGstAmt.setText("0.00");
        jtxtAmt.setText("0.00");
    }

    private void updateLabel() {
        double qty = 0.00, amt = 0.00;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            jTable1.setValueAt(i+1, i, 0);
            qty += lb.replaceAll(jTable1.getValueAt(i, 3).toString());
            amt += lb.replaceAll(jTable1.getValueAt(i, 11).toString());
        }
        jlblQty.setText(lb.Convert2DecFmt(qty));
        jlblAmt.setText(lb.getIndianFormat(amt));

        double discPer = lb.replaceAll(jtxtDiscPer.getText());
        double discRs = amt * (discPer / 100);
        jtxtDiscRs.setText(lb.getIndianFormat(discRs));
        double total_amt = (amt - discRs);
        jtxtTotalAmt.setText(lb.getIndianFormat(total_amt));
        double bill_amt = total_amt;
        jtxtBillAmount.setText(lb.getIndianFormat(bill_amt));
        double lbr_chrges = lb.replaceAll(jtxtLoadingCharge.getText());
        double adj = lb.replaceAll(jtxtAdjustAmt.getText());
        double net_amt = bill_amt + adj + lbr_chrges;
        jtxtNetAmt.setText(lb.getIndianFormat(net_amt));
    }

    private void setTextfieldsAtBottom() {
        JComponent[] header = new JComponent[]{null, jtxtItemName, jtxtHSNCode, jtxtQty, jtxtUnitName, jtxtRate, jtxtDisc, jtxtSGst, jtxtSGstAmt, jtxtCGst, jtxtCGstAmt, jtxtAmt, jtxtparticulars};
        JComponent[] footer = new JComponent[]{null, null, null, jlblQty, null, null, null, null, null, null, null, jlblAmt, null};
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
        jLabel6 = new javax.swing.JLabel();
        jcmbPmt = new javax.swing.JComboBox();
        jlblDay = new javax.swing.JLabel();
        jBillDateBtn = new javax.swing.JButton();
        jtxtVDate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtACAlias = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jtxtPDate = new javax.swing.JTextField();
        jcmbCheck = new javax.swing.JCheckBox();
        jBillDateBtn1 = new javax.swing.JButton();
        jbtnAdd = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        jtxtLRNo = new javax.swing.JTextField();
        jtxtTransport = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jcmbType = new javax.swing.JComboBox();
        jtxtACName1 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jtxtPlace = new javax.swing.JTextField();
        jtxtdays = new javax.swing.JTextField();
        jlblAmt = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jtxtRemarks = new javax.swing.JTextField();
        jbtnEmail = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableOld = new javax.swing.JTable();
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
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jtxtLoadingCharge = new javax.swing.JTextField();
        jtxtAmt = new javax.swing.JTextField();
        jtxtDisc = new javax.swing.JTextField();
        jtxtRate = new javax.swing.JTextField();
        jtxtUnitName = new javax.swing.JTextField();
        jtxtQty = new javax.swing.JTextField();
        jtxtItemName = new javax.swing.JTextField();
        jtxtparticulars = new javax.swing.JTextArea();
        jtxtNoofParcel = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jtxtSGst = new javax.swing.JTextField();
        jtxtCGst = new javax.swing.JTextField();
        jtxtCGstAmt = new javax.swing.JTextField();
        jtxtSGstAmt = new javax.swing.JTextField();
        jbtnChiddi = new javax.swing.JButton();
        jtxtHSNCode = new javax.swing.JTextField();
        jbtnClear = new javax.swing.JButton();
        jbtnDelete = new javax.swing.JButton();
        jLabel37 = new javax.swing.JLabel();
        jtxtTransactionId = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jtxtCheckPost = new javax.swing.JTextField();

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
                "Sr No.", "Item Name", "HSN Code", "Qty", "Unit Name", "Rate", "Disc(%)", "S GST", "Vat Amt", "C GST", "Add Amt", "Amount", "Desc."
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setMinimumSize(new java.awt.Dimension(75, 0));
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
        jTable1.getColumnModel().getColumn(0).setResizable(false);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(5);
        jTable1.getColumnModel().getColumn(1).setResizable(false);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(170);
        jTable1.getColumnModel().getColumn(2).setResizable(false);
        jTable1.getColumnModel().getColumn(3).setResizable(false);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(4).setResizable(false);
        jTable1.getColumnModel().getColumn(5).setResizable(false);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(6).setResizable(false);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(7).setResizable(false);
        jTable1.getColumnModel().getColumn(8).setMinWidth(0);
        jTable1.getColumnModel().getColumn(8).setPreferredWidth(0);
        jTable1.getColumnModel().getColumn(8).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(9).setResizable(false);
        jTable1.getColumnModel().getColumn(10).setMinWidth(0);
        jTable1.getColumnModel().getColumn(10).setPreferredWidth(0);
        jTable1.getColumnModel().getColumn(10).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(11).setResizable(false);
        jTable1.getColumnModel().getColumn(11).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(12).setMinWidth(0);
        jTable1.getColumnModel().getColumn(12).setPreferredWidth(0);
        jTable1.getColumnModel().getColumn(12).setMaxWidth(0);

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

        jLabel6.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel6.setText("Payment Mode");

        jcmbPmt.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jcmbPmt.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "DEBIT", "CREDIT" }));
        jcmbPmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jcmbPmt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbPmtKeyPressed(evt);
            }
        });

        jlblDay.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlblDay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

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
        jtxtACAlias.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jtxtACAliasComponentResized(evt);
            }
        });
        jtxtACAlias.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtACAliasFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtACAliasFocusLost(evt);
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

        jcmbCheck.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jcmbCheck.setText("Check");
        jcmbCheck.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbCheckKeyPressed(evt);
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

        jLabel28.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel28.setText("LR No.");

        jtxtLRNo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtLRNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtLRNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtLRNoFocusGained(evt);
            }
        });
        jtxtLRNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtLRNoKeyPressed(evt);
            }
        });

        jtxtTransport.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtTransport.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtTransport.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtTransportFocusGained(evt);
            }
        });
        jtxtTransport.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtTransportKeyPressed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel29.setText("Transport");

        jLabel31.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel31.setText("Sales Type");

        jcmbType.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jcmbType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tax", "Retail", "Labour" }));
        jcmbType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbTypeKeyPressed(evt);
            }
        });

        jtxtACName1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtACName1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jLabel35.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel35.setText("Place");

        jtxtPlace.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtPlace.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtPlace.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPlaceFocusGained(evt);
            }
        });
        jtxtPlace.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPlaceKeyPressed(evt);
            }
        });

        jtxtdays.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtdays.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtdays.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtdaysFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtdaysFocusLost(evt);
            }
        });
        jtxtdays.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtdaysKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtdaysKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jtxtInvoiceNo, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtLRNo, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtTransport, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtPlace, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jtxtACAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcmbPmt, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblDay, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jcmbCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtdays, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtPDate, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtACName1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
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
                        .addComponent(jLabel11))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jcmbCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtdays, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jlblDay, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addComponent(jBillDateBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jtxtVDate)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addComponent(jtxtVoucher, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlblStart, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                    .addComponent(jtxtACName1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtACAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jcmbPmt, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jLabel31)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jcmbType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtTransport, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtLRNo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtInvoiceNo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtPlace, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jLabel2, jLabel28, jLabel29, jLabel31, jLabel6, jcmbCheck, jcmbPmt, jcmbType, jlblDay, jtxtLRNo, jtxtTransport, jtxtVDate});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn1, jLabel11, jLabel23, jLabel25, jbtnAdd, jtxtACAlias, jtxtInvoiceNo, jtxtPDate});

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

        jbtnEmail.setBackground(new java.awt.Color(204, 255, 204));
        jbtnEmail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnEmail.setForeground(new java.awt.Color(235, 35, 35));
        jbtnEmail.setText("EMAIL");
        jbtnEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEmailActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(51, 51, 51));
        jLabel10.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("OLD PAYMENT DETAILS");
        jLabel10.setOpaque(true);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 1, 1, new java.awt.Color(53, 154, 141)));

        jTableOld.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jTableOld.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "DR", "CR", "Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableOld.setRowHeight(23);
        jTableOld.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTableOldFocusLost(evt);
            }
        });
        jScrollPane2.setViewportView(jTableOld);

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

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel33.setText("Loading Charge");

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("+");

        jtxtLoadingCharge.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtLoadingCharge.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtLoadingCharge.setText("0.00");
        jtxtLoadingCharge.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtLoadingCharge.setMinimumSize(new java.awt.Dimension(2, 20));
        jtxtLoadingCharge.setPreferredSize(new java.awt.Dimension(27, 20));
        jtxtLoadingCharge.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtLoadingChargeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtLoadingChargeFocusLost(evt);
            }
        });
        jtxtLoadingCharge.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtLoadingChargeKeyPressed(evt);
            }
        });

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
                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtDiscRs, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .addComponent(jtxtTotalAmt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtBillAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtLoadingCharge, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtAdjustAmt, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                    .addComponent(jtxtNetAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jtxtAdjustAmt, jtxtBillAmount, jtxtDiscRs, jtxtLoadingCharge, jtxtNetAmt, jtxtTotalAmt});

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel18, jLabel20, jLabel26, jLabel27, jLabel30, jLabel34});

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel14, jLabel16, jLabel33, jLabel4, jLabel5, jLabel9});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
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
                    .addComponent(jtxtLoadingCharge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34)
                    .addComponent(jLabel33))
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

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel33, jLabel34, jtxtLoadingCharge});

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

        jtxtDisc.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtDisc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtDisc.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtDisc.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtDisc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDiscFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDiscFocusLost(evt);
            }
        });
        jtxtDisc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDiscKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtDiscKeyTyped(evt);
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

        jtxtUnitName.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtUnitName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtUnitName.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtUnitName.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtUnitName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtUnitNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtUnitNameFocusLost(evt);
            }
        });
        jtxtUnitName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtUnitNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtUnitNameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtUnitNameKeyTyped(evt);
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

        jtxtItemName.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtItemName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtItemName.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtItemName.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtItemName.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jtxtItemNameComponentResized(evt);
            }
        });
        jtxtItemName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtItemNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtItemNameFocusLost(evt);
            }
        });
        jtxtItemName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtItemNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtItemNameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtItemNameKeyTyped(evt);
            }
        });

        jtxtparticulars.setColumns(20);
        jtxtparticulars.setRows(5);

        jtxtNoofParcel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtNoofParcel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtNoofParcel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtNoofParcelFocusGained(evt);
            }
        });
        jtxtNoofParcel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtNoofParcelKeyPressed(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel36.setText("No of Parcel");

        jtxtSGst.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtSGst.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtSGst.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtSGst.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtSGst.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtSGstFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtSGstFocusLost(evt);
            }
        });
        jtxtSGst.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtSGstKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtSGstKeyReleased(evt);
            }
        });

        jtxtCGst.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtCGst.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtCGst.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtCGst.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtCGst.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCGstFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtCGstFocusLost(evt);
            }
        });
        jtxtCGst.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCGstKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtCGstKeyReleased(evt);
            }
        });

        jtxtCGstAmt.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtCGstAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtCGstAmt.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtCGstAmt.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtCGstAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCGstAmtFocusGained(evt);
            }
        });

        jtxtSGstAmt.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtSGstAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtSGstAmt.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtSGstAmt.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtSGstAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtSGstAmtFocusGained(evt);
            }
        });

        jbtnChiddi.setBackground(new java.awt.Color(204, 255, 204));
        jbtnChiddi.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnChiddi.setForeground(new java.awt.Color(235, 35, 35));
        jbtnChiddi.setText("CHIDHI");
        jbtnChiddi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnChiddiActionPerformed(evt);
            }
        });

        jtxtHSNCode.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtHSNCode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtHSNCode.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtHSNCode.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtHSNCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtHSNCodeFocusGained(evt);
            }
        });
        jtxtHSNCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtHSNCodeKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtHSNCodeKeyTyped(evt);
            }
        });

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

        jLabel37.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel37.setText("Transaction Id");

        jtxtTransactionId.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtTransactionId.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtTransactionId.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtTransactionIdFocusGained(evt);
            }
        });
        jtxtTransactionId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtTransactionIdKeyPressed(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel38.setText("Check Post");

        jtxtCheckPost.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtCheckPost.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtCheckPost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCheckPostFocusGained(evt);
            }
        });
        jtxtCheckPost.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCheckPostKeyPressed(evt);
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
                        .addComponent(jtxtCGstAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtSGstAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtItemName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtHSNCode, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtQty, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtUnitName, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtRate, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtDisc, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtSGst, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtCGst, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jtxtparticulars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlblQty, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(259, 259, 259)
                        .addComponent(jlblAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtRemarks))
                            .addComponent(jScrollPane2)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtNoofParcel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtTransactionId)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtCheckPost)))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnChiddi, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnChiddi, jbtnEmail});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtCGst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtSGst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtDisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtUnitName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtHSNCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtItemName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtCGstAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtSGstAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jlblQty, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtparticulars, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel36)
                            .addComponent(jtxtNoofParcel)
                            .addComponent(jbtnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jbtnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(3, 3, 3)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtTransactionId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38)
                            .addComponent(jtxtCheckPost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jtxtRemarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblTimeStamp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtnChiddi, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jlblAmt, jlblQty});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jtxtAmt, jtxtDisc, jtxtItemName, jtxtQty, jtxtRate, jtxtUnitName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jbtnChiddi, jbtnEmail});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel36, jbtnClear, jbtnDelete, jtxtNoofParcel});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel37, jLabel38, jtxtCheckPost, jtxtTransactionId});

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
                    jtxtItemName.setText(jTable1.getValueAt(rowSel, 1).toString());
                    jtxtHSNCode.setText(jTable1.getValueAt(rowSel, 2).toString());
                    jtxtQty.setText(jTable1.getValueAt(rowSel, 3).toString());
                    jtxtUnitName.setText(jTable1.getValueAt(rowSel, 4).toString());
                    jtxtRate.setText(jTable1.getValueAt(rowSel, 5).toString());
                    jtxtDisc.setText(jTable1.getValueAt(rowSel, 6).toString());
                    jtxtSGst.setText(jTable1.getValueAt(rowSel, 7).toString());
                    jtxtSGstAmt.setText(jTable1.getValueAt(rowSel, 8).toString());
                    jtxtCGst.setText(jTable1.getValueAt(rowSel, 9).toString());
                    jtxtCGstAmt.setText(jTable1.getValueAt(rowSel, 10).toString());
                    jtxtAmt.setText(jTable1.getValueAt(rowSel, 11).toString());
                    jtxtparticulars.setText(jTable1.getValueAt(rowSel, 12).toString());
                    jtxtItemName.requestFocusInWindow();
                }
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jtxtVDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtVDateFocusGained

    private void jtxtVDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVDateFocusLost
        if(lb.checkDate(jtxtVDate)) {
            jlblDay.setText(lb.setDay(jtxtVDate));
        } else {
            navLoad.setMessage(Constants.CORRECT_DATE);
        }
    }//GEN-LAST:event_jtxtVDateFocusLost

    private void jtxtVDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVDateKeyPressed
        lb.enterFocus(evt, jtxtdays);
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
            jcmbPmt.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtACAliasKeyPressed

    private void jtxtACAliasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtACAliasKeyReleased
        try {
            accountPickList.setReturnComponent(new JTextField[]{jtxtACAlias});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT ac_name FROM acnt_mst WHERE ac_name LIKE '%"+ jtxtACAlias.getText().toUpperCase() +"%'");
            accountPickList.setValidation(dataConnection.prepareStatement("SELECT ac_name FROM acnt_mst WHERE ac_name = ?"));
            accountPickList.setPreparedStatement(pstLocal);
            accountPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtACAliasKeyReleased In Tax Invoice", ex);
        }
    }//GEN-LAST:event_jtxtACAliasKeyReleased

    private void jtxtACAliasFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtACAliasFocusLost
        accountPickList.setVisible(false);
        if(!lb.isExist("acnt_mst", "ac_name", jtxtACAlias.getText())) {
            navLoad.setMessage(Constants.INVALID_ACCOUNT);
            jtxtACAlias.requestFocusInWindow();
        } else {
            navLoad.setMessage("");
            String sql = "SELECT transaction_id, check_post FROM acnt_mst WHERE ac_name LIKE '%"+ jtxtACAlias.getText() +"%'";
            try {
                PreparedStatement psLocal = dataConnection.prepareStatement(sql);
                ResultSet rsLocal = psLocal.executeQuery();
                if(rsLocal.next()) {
                    if(jtxtTransactionId.getText().equals("")) {
                        jtxtTransactionId.setText(rsLocal.getString("transaction_id"));
                    }
                    if(jtxtCheckPost.getText().equals("")) {
                        jtxtCheckPost.setText(rsLocal.getString("check_post"));
                    }
                }
            } catch(Exception ex) {
                lb.printToLogFile("Exception at acalias in Tax Invoice", ex);
            }
        }
        dtmOld.setRowCount(0);
//        lb.setBal(lb.getAccountCode(jtxtACAlias.getText(), "C"), dtmOld);
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
            if(lb.isExist("slshd", "ref_no", initial + jtxtVoucher.getText(), dataConnection)) {
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
        lb.enterFocus(evt, jtxtLRNo);
    }//GEN-LAST:event_jtxtInvoiceNoKeyPressed

    private void jcmbPmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbPmtKeyPressed
        lb.enterFocus(evt, jtxtInvoiceNo);
    }//GEN-LAST:event_jcmbPmtKeyPressed

    private void jtxtRemarksFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRemarksFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRemarksFocusGained

    private void jtxtRemarksKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRemarksKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtRemarksKeyPressed

    private void jbtnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEmailActionPerformed
        EmailSelect ds = new EmailSelect(MainClass.df, true, ref_no, initial, 1);
        ds.show();
    }//GEN-LAST:event_jbtnEmailActionPerformed

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
        double lbr_chrges = lb.replaceAll(jtxtLoadingCharge.getText());
        double adj = lb.replaceAll(jtxtAdjustAmt.getText());
        double net_amt = bill_amt + adj + lbr_chrges;
        jtxtNetAmt.setText(lb.getIndianFormat(net_amt));
    }//GEN-LAST:event_jtxtDiscRsFocusLost

    private void jtxtDiscRsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDiscRsKeyPressed
        lb.enterEvent(evt, jtxtLoadingCharge);
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
        lb.enterEvent(evt, jtxtDiscPer);
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

    private void jcmbCheckKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbCheckKeyPressed
        lb.enterEvent(evt, jtxtPDate);
    }//GEN-LAST:event_jcmbCheckKeyPressed

    private void jtxtAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmtFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAmtFocusGained

    private void jtxtAmtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmtFocusLost
        calculation();
    }//GEN-LAST:event_jtxtAmtFocusLost

    private void jtxtAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmtKeyPressed
        lb.enterFocus(evt, jbtnAdd);
    }//GEN-LAST:event_jtxtAmtKeyPressed

    private void jtxtAmtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmtKeyTyped
        lb.onlyNumber(evt, 15);
    }//GEN-LAST:event_jtxtAmtKeyTyped

    private void jtxtDiscFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDiscFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDiscFocusGained

    private void jtxtDiscFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDiscFocusLost
        calculation();
    }//GEN-LAST:event_jtxtDiscFocusLost

    private void jtxtDiscKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDiscKeyPressed
        lb.enterEvent(evt, jtxtSGst);
    }//GEN-LAST:event_jtxtDiscKeyPressed

    private void jtxtDiscKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDiscKeyTyped
        lb.onlyNumber(evt, 10);
    }//GEN-LAST:event_jtxtDiscKeyTyped

    private void jtxtRateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRateFocusGained

    private void jtxtRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateFocusLost
        calculation();
    }//GEN-LAST:event_jtxtRateFocusLost

    private void jtxtRateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateKeyPressed
        lb.enterFocus(evt, jtxtDisc);
    }//GEN-LAST:event_jtxtRateKeyPressed

    private void jtxtRateKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateKeyTyped
        lb.onlyNumber(evt, 15);
    }//GEN-LAST:event_jtxtRateKeyTyped

    private void jtxtUnitNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtUnitNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtUnitNameFocusGained

    private void jtxtUnitNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtUnitNameKeyPressed
        unitPickList.setLocation(jtxtUnitName.getX(), jtxtUnitName.getY() + jtxtUnitName.getHeight());
        unitPickList.pickListKeyPress(evt);
        lb.enterEvent(evt, jtxtRate);
    }//GEN-LAST:event_jtxtUnitNameKeyPressed

    private void jtxtUnitNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtUnitNameKeyTyped
        lb.onlyAlpha(evt, 30);
    }//GEN-LAST:event_jtxtUnitNameKeyTyped

    private void jtxtQtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtQtyFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtQtyFocusGained

    private void jtxtQtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtQtyFocusLost
        calculation();
    }//GEN-LAST:event_jtxtQtyFocusLost

    private void jtxtQtyKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtQtyKeyPressed
        lb.enterEvent(evt, jtxtUnitName);
    }//GEN-LAST:event_jtxtQtyKeyPressed

    private void jtxtQtyKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtQtyKeyTyped
        lb.onlyNumber(evt, 15);
    }//GEN-LAST:event_jtxtQtyKeyTyped

    private void jtxtItemNameComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtxtItemNameComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jtxtItemNameComponentResized

    private void jtxtItemNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtItemNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtItemNameFocusGained

    private void jtxtItemNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtItemNameFocusLost
        itemPickList.setVisible(false);
        try {
            PreparedStatement psLocal = null;
            ResultSet rsLocal = null;
            String sql = "SELECT im.unt_cd, ai.rate, description FROM itm_mst im LEFT JOIN acntitm_mst ai ON ai.itm_cd = im.itm_cd " +
                "LEFT JOIN slsdt sd ON sd.itm_cd = im.itm_cd LEFT JOIN slshd sh ON sh.ref_no = sd.ref_no AND sh.ac_cd = ai.ac_cd AND sh.ac_cd = ? " +
                "WHERE im.itm_cd = ? AND sh.ref_no LIKE (SELECT MAX(ref_no) FROM slshd WHERE ac_cd = ?)";
            psLocal = dataConnection.prepareStatement(sql);
            psLocal.setString(1, lb.getAccountMstName(jtxtACAlias.getText(), "C"));
//            psLocal.setString(2, lb.getItemCd(jtxtItemName.getText(), "C"));
            psLocal.setString(3, lb.getAccountMstName(jtxtACAlias.getText(), "C"));
            rsLocal = psLocal.executeQuery();
            if(rsLocal.next()) {
                if(lb.replaceAll(jtxtRate.getText()) == 0) {
                    jtxtRate.setText(lb.getIndianFormat(lb.replaceAll(rsLocal.getString("rate"))));
                }
            }
            sql = "SELECT im.unt_cd, im.rate, description FROM itm_mst im WHERE im.itm_name LIKE '%"+ jtxtItemName.getText() +"%'";
            psLocal = dataConnection.prepareStatement(sql);
            rsLocal = psLocal.executeQuery();
            if(rsLocal.next()) {
                jtxtUnitName.setText(lb.getUnitName(rsLocal.getString("unt_cd"), "N"));
                if(jtxtparticulars.getText().equalsIgnoreCase("")) {
                    jtxtparticulars.setText(rsLocal.getString("description"));
                }
                if(lb.replaceAll(jtxtRate.getText()) == 0) {
                    jtxtRate.setText(lb.getIndianFormat(lb.replaceAll(rsLocal.getString("rate"))));
                }
            }
        } catch(Exception ex) {
            lb.printToLogFile("Exception at jtxtItemNameFocusLost In Tax Invoice", ex);
        }
    }//GEN-LAST:event_jtxtItemNameFocusLost

    private void jtxtItemNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtItemNameKeyPressed
        itemPickList.setLocation(jtxtItemName.getX(), jtxtItemName.getY() + jtxtItemName.getHeight());
        itemPickList.pickListKeyPress(evt);
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            if (evt.getModifiers() == KeyEvent.SHIFT_MASK) {
                evt.consume();
                jtxtDiscPer.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jtxtItemNameKeyPressed

    private void jtxtItemNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtItemNameKeyReleased
        try {
            itemPickList.setReturnComponent(new JTextField[]{jtxtItemName, jtxtHSNCode, jtxtHSNCode, jtxtSGst, jtxtSGstAmt, jtxtCGst, jtxtCGstAmt});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT itm_name AS itm_name, SUM(CASE WHEN trns_cd = 'I' THEN -ol.pcs ELSE ol.pcs END) AS pcs, " +
                "im.hsn_code, tms.tax_name AS sgst_name, tms.tax AS sgst_tax, tmc.tax_name AS cgst_name, tmc.tax AS cgst_tax " +
                "FROM itm_mst im LEFT JOIN oldb0_2 ol ON im.itm_cd = ol.itm_cd LEFT JOIN tax_mst tms ON tms.tax_cd = im.sgst_cd " +
                "LEFT JOIN tax_mst tmc ON tmc.tax_cd = im.cgst_cd WHERE im.itm_cd = ol.itm_cd AND im.itm_name LIKE '%" 
                + jtxtItemName.getText() +"%' GROUP BY im.itm_cd ORDER BY im.itm_cd");
            itemPickList.setValidation(dataConnection.prepareStatement("SELECT itm_name FROM itm_mst WHERE itm_name = ?"));
            itemPickList.setFirstAssociation(new int[]{0, 1, 2, 3, 4, 5, 6});
            itemPickList.setSecondAssociation(new int[]{0, 1, 2, 3, 4, 5, 6});
            itemPickList.setPreparedStatement(pstLocal);
            itemPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtItemNameKeyReleased In Tax Invoice", ex);
        }
    }//GEN-LAST:event_jtxtItemNameKeyReleased

    private void jtxtItemNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtItemNameKeyTyped
        lb.fixLength(evt, 200);
    }//GEN-LAST:event_jtxtItemNameKeyTyped

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        int rowSel = jTable1.getSelectedRow();
        if(reValidate()){
            if(rowSel == -1) {
                Vector row = new Vector();
                row.add("");
                row.add(jtxtItemName.getText());
                row.add(jtxtHSNCode.getText());
                row.add(lb.Convert2DecFmt(lb.replaceAll(jtxtQty.getText())));
                row.add(jtxtUnitName.getText());
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtRate.getText())));
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtDisc.getText())));
                row.add(jtxtSGst.getText());
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtSGstAmt.getText())));
                row.add(jtxtCGst.getText());
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtCGstAmt.getText())));
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtAmt.getText())));
                row.add(jtxtparticulars.getText());
                dtm.addRow(row);
            } else {
                jTable1.setValueAt(jtxtItemName.getText(), rowSel, 1);
                jTable1.setValueAt(jtxtHSNCode.getText(), rowSel, 2);
                jTable1.setValueAt(lb.Convert2DecFmt(lb.replaceAll(jtxtQty.getText())), rowSel, 3);
                jTable1.setValueAt(jtxtUnitName.getText(), rowSel, 4);
                jTable1.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtRate.getText())), rowSel, 5);
                jTable1.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtDisc.getText())), rowSel, 6);
                jTable1.setValueAt(jtxtSGst.getText(), rowSel, 7);
                jTable1.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtSGstAmt.getText())), rowSel, 8);
                jTable1.setValueAt(jtxtCGst.getText(), rowSel, 9);
                jTable1.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtCGstAmt.getText())), rowSel, 10);
                jTable1.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtAmt.getText())), rowSel, 11);
                jTable1.setValueAt(jtxtparticulars.getText(), rowSel, 12);
            }
            updateLabel();
            clear();
            jTable1.clearSelection();
            if (JOptionPane.showConfirmDialog(this, Constants.ADD_MORE_ENTRY, DeskFrame.TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                jtxtItemName.requestFocusInWindow();
            } else {
                jtxtNoofParcel.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jtxtLRNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtLRNoFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtLRNoFocusGained

    private void jtxtLRNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtLRNoKeyPressed
        lb.enterEvent(evt, jtxtTransport);
    }//GEN-LAST:event_jtxtLRNoKeyPressed

    private void jtxtTransportFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTransportFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtTransportFocusGained

    private void jtxtTransportKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTransportKeyPressed
        lb.enterEvent(evt, jtxtPlace);
    }//GEN-LAST:event_jtxtTransportKeyPressed

    private void jcmbTypeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbTypeKeyPressed
        lb.enterEvent(evt, jtxtInvoiceNo);
    }//GEN-LAST:event_jcmbTypeKeyPressed

    private void jtxtUnitNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtUnitNameFocusLost
        unitPickList.setVisible(false);
    }//GEN-LAST:event_jtxtUnitNameFocusLost

    private void jtxtUnitNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtUnitNameKeyReleased
        try {
            unitPickList.setReturnComponent(new JTextField[]{jtxtUnitName});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT unt_name FROM unt_mst WHERE unt_name LIKE '%"+ jtxtUnitName.getText() +"%'");
            unitPickList.setValidation(dataConnection.prepareStatement("SELECT unt_name FROM unt_mst WHERE unt_name = ?"));
            unitPickList.setPreparedStatement(pstLocal);
            unitPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setItemPickList In Tax Invoice", ex);
        }
    }//GEN-LAST:event_jtxtUnitNameKeyReleased

    private void jtxtLoadingChargeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtLoadingChargeFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtLoadingChargeFocusGained

    private void jtxtLoadingChargeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtLoadingChargeFocusLost
        updateLabel();
    }//GEN-LAST:event_jtxtLoadingChargeFocusLost

    private void jtxtLoadingChargeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtLoadingChargeKeyPressed
        lb.enterEvent(evt, jtxtAdjustAmt);
    }//GEN-LAST:event_jtxtLoadingChargeKeyPressed

    private void jtxtPlaceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPlaceFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPlaceFocusGained

    private void jtxtPlaceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPlaceKeyPressed
        lb.enterEvent(evt, jtxtItemName);
    }//GEN-LAST:event_jtxtPlaceKeyPressed

    private void jTableOldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTableOldFocusLost
        jTableOld.clearSelection();
    }//GEN-LAST:event_jTableOldFocusLost

    private void jtxtNoofParcelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtNoofParcelFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtNoofParcelFocusGained

    private void jtxtNoofParcelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtNoofParcelKeyPressed
        lb.enterEvent(evt, jtxtTransactionId);
    }//GEN-LAST:event_jtxtNoofParcelKeyPressed

    private void jtxtSGstFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSGstFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtSGstFocusGained

    private void jtxtSGstFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSGstFocusLost
        sgstPickList.setVisible(false);
        try {
            PreparedStatement psLocal = null;
            ResultSet rsLocal = null;
            String sql = "SELECT tax_name, tax FROM tax_mst WHERE tax_cd = ?";
            psLocal = dataConnection.prepareStatement(sql);
            psLocal.setString(1, lb.getTaxCode(jtxtSGst.getText(), "C"));
            rsLocal = psLocal.executeQuery();
            if(rsLocal.next()) {
                jtxtSGstAmt.setText(lb.getIndianFormat(lb.replaceAll(rsLocal.getString("tax"))));
            }
        } catch(Exception ex) {
            lb.printToLogFile("Exception at jtxtAddFocusLost in Tax Invoice", ex);
        }
        calculation();
    }//GEN-LAST:event_jtxtSGstFocusLost

    private void jtxtSGstKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSGstKeyPressed
        sgstPickList.setLocation(jtxtSGst.getX(), jtxtSGst.getY() + jtxtSGst.getHeight());
        sgstPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtSGstKeyPressed

    private void jtxtCGstFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCGstFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCGstFocusGained

    private void jtxtCGstFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCGstFocusLost
        cgstPickList.setVisible(false);
        try {
            PreparedStatement psLocal = null;
            ResultSet rsLocal = null;
            String sql = "SELECT tax_name, tax FROM tax_mst WHERE tax_cd = ?";
            psLocal = dataConnection.prepareStatement(sql);
            psLocal.setString(1, lb.getTaxCode(jtxtCGst.getText(), "C"));
            rsLocal = psLocal.executeQuery();
            if(rsLocal.next()) {
                jtxtCGstAmt.setText(lb.getIndianFormat(lb.replaceAll(rsLocal.getString("tax"))));
            }
        } catch(Exception ex) {
            lb.printToLogFile("Exception at jtxtVatFocusLost in Tax Invoice", ex);
        }
        calculation();
    }//GEN-LAST:event_jtxtCGstFocusLost

    private void jtxtCGstKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCGstKeyPressed
        cgstPickList.setLocation(jtxtCGst.getX(), jtxtCGst.getY() + jtxtCGst.getHeight());
        cgstPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtCGstKeyPressed

    private void jtxtCGstAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCGstAmtFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCGstAmtFocusGained

    private void jtxtSGstAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSGstAmtFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtSGstAmtFocusGained

    private void jtxtSGstKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSGstKeyReleased
        try {
            sgstPickList.setReturnComponent(new JTextField[]{jtxtSGst});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT tax_name AS tax_name FROM tax_mst WHERE tax_name LIKE '%"+ jtxtSGst.getText() +"%'");
            sgstPickList.setValidation(dataConnection.prepareStatement("SELECT tax_name FROM tax_mst WHERE tax_name = ?"));
            sgstPickList.setPreparedStatement(pstLocal);
            sgstPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtAddNameKeyReleased In Tax Invoice", ex);
        }
    }//GEN-LAST:event_jtxtSGstKeyReleased

    private void jtxtCGstKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCGstKeyReleased
        try {
            cgstPickList.setReturnComponent(new JTextField[]{jtxtCGst});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT tax_name AS tax_name FROM tax_mst WHERE tax_name LIKE '%"+ jtxtCGst.getText() +"%'");
            cgstPickList.setValidation(dataConnection.prepareStatement("SELECT tax_name FROM tax_mst WHERE tax_name = ?"));
            cgstPickList.setPreparedStatement(pstLocal);
            cgstPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtVatNameKeyReleased In Tax Invoice", ex);
        }
    }//GEN-LAST:event_jtxtCGstKeyReleased

    private void jtxtdaysFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtdaysFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtdaysFocusGained

    private void jtxtdaysKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtdaysKeyPressed
        lb.enterEvent(evt, jtxtACAlias);
    }//GEN-LAST:event_jtxtdaysKeyPressed

    private void jtxtdaysFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtdaysFocusLost
        try {
            if(!jtxtdays.getText().equalsIgnoreCase("")) {
                Calendar c = Calendar.getInstance();
                c.setTime(lb.tempConvertFormatForDBorConcurrencydATE(jtxtVDate.getText())); 
                c.add(Calendar.DATE, Integer.parseInt(jtxtdays.getText()));
                jtxtPDate.setText(lb.ConvertDateCalendarFormetForDBDiaryString(c.getTime().toString()));
            }
        } catch(Exception ex) {
            ex.getMessage();
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jtxtdaysFocusLost

    private void jtxtdaysKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtdaysKeyTyped
        lb.onlyInteger(evt, 4);
    }//GEN-LAST:event_jtxtdaysKeyTyped

    private void jbtnChiddiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnChiddiActionPerformed
        VoucherDisplay vd = new VoucherDisplay(ref_no, "", initial, 0);
        DeskFrame.addOnScreen(vd, view_title +" PRINT");
    }//GEN-LAST:event_jbtnChiddiActionPerformed

    private void jtxtHSNCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtHSNCodeFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtHSNCodeFocusGained

    private void jtxtHSNCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtHSNCodeKeyPressed
        lb.enterEvent(evt, jtxtQty);
    }//GEN-LAST:event_jtxtHSNCodeKeyPressed

    private void jtxtHSNCodeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtHSNCodeKeyTyped
        lb.fixLength(evt, 20);
    }//GEN-LAST:event_jtxtHSNCodeKeyTyped

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

    private void jtxtTransactionIdFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTransactionIdFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtTransactionIdFocusGained

    private void jtxtTransactionIdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTransactionIdKeyPressed
        lb.enterEvent(evt, jtxtCheckPost);
    }//GEN-LAST:event_jtxtTransactionIdKeyPressed

    private void jtxtCheckPostFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCheckPostFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCheckPostFocusGained

    private void jtxtCheckPostKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCheckPostKeyPressed
        lb.enterEvent(evt, jtxtDiscPer);
    }//GEN-LAST:event_jtxtCheckPostKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jBillDateBtn1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTableOld;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JButton jbtnChiddi;
    private javax.swing.JButton jbtnClear;
    private javax.swing.JButton jbtnDelete;
    private javax.swing.JButton jbtnEmail;
    private javax.swing.JCheckBox jcmbCheck;
    private javax.swing.JComboBox jcmbPmt;
    private javax.swing.JComboBox jcmbType;
    private javax.swing.JLabel jlblAmt;
    private javax.swing.JLabel jlblDay;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblQty;
    private javax.swing.JLabel jlblStart;
    private javax.swing.JLabel jlblTimeStamp;
    private javax.swing.JLabel jlblUser;
    private javax.swing.JTextField jtxtACAlias;
    private javax.swing.JTextField jtxtACName1;
    private javax.swing.JTextField jtxtAdjustAmt;
    private javax.swing.JTextField jtxtAmt;
    private javax.swing.JLabel jtxtBillAmount;
    private javax.swing.JTextField jtxtCGst;
    private javax.swing.JTextField jtxtCGstAmt;
    private javax.swing.JTextField jtxtCheckPost;
    private javax.swing.JTextField jtxtDisc;
    private javax.swing.JTextField jtxtDiscPer;
    private javax.swing.JTextField jtxtDiscRs;
    private javax.swing.JTextField jtxtHSNCode;
    private javax.swing.JTextField jtxtInvoiceNo;
    private javax.swing.JTextField jtxtItemName;
    private javax.swing.JTextField jtxtLRNo;
    private javax.swing.JTextField jtxtLoadingCharge;
    private javax.swing.JLabel jtxtNetAmt;
    private javax.swing.JTextField jtxtNoofParcel;
    private javax.swing.JTextField jtxtPDate;
    private javax.swing.JTextField jtxtPlace;
    private javax.swing.JTextField jtxtQty;
    private javax.swing.JTextField jtxtRate;
    private javax.swing.JTextField jtxtRemarks;
    private javax.swing.JTextField jtxtSGst;
    private javax.swing.JTextField jtxtSGstAmt;
    private javax.swing.JLabel jtxtTotalAmt;
    private javax.swing.JTextField jtxtTransactionId;
    private javax.swing.JTextField jtxtTransport;
    private javax.swing.JTextField jtxtUnitName;
    private javax.swing.JTextField jtxtVDate;
    private javax.swing.JTextField jtxtVoucher;
    private javax.swing.JTextField jtxtdays;
    public static javax.swing.JTextArea jtxtparticulars;
    // End of variables declaration//GEN-END:variables
}