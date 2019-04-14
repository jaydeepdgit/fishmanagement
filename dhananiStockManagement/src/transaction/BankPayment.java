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
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import master.PopUpAccountMaster;
import oldbupdate.BankPayRcptUpdate;
import dhananistockmanagement.MainClass;
import support.Constants;
import support.EmailSelect;
import support.HeaderIntFrame1;
import support.Library;
import support.NavigationPanel1;
import support.OurDateChooser;
import support.PickList;
import support.ReportTable;
import support.VoucherDisplay;

/**
 *
 * @author @JD@
 */
public class BankPayment extends javax.swing.JInternalFrame {
    NavigationPanel1 navLoad = null;
    Library lb = new Library();
    DefaultTableModel dtm = null;
    private String id = "";
    private ReportTable bankView = null;
    Connection dataConnection = DeskFrame.connMpAdmin;
    private int type = 0;
    private String initial = "", bank_cd = "";
    private OurDateChooser odc = null;
    private PickList accountPickList = null, bankPickList = null;
    String Syspath = System.getProperty("user.dir");
    String form_id = "";

    /**
     * Creates new form BankPayment
     */
    public BankPayment(int type) {
        initComponents();
        this.type = type;
        initOtherComponents();
        navLoad.setVoucher("last");
    }

    public BankPayment(int type, String id) {
        initComponents();
        this.type = type;
        jtxtVoucher.setText(id.substring(initial.length()));
        this.id = initial + jtxtVoucher.getText();
        initOtherComponents();
        navLoad.setVoucher("edit");
    }

    private void initOtherComponents() {
        setInitial();
        jtxtVoucher.requestFocusInWindow();
        connectToNavigation();
        lb.setDateChooserProperty(jtxtVDate);
        jlblDay.setText(lb.setDay(jtxtVDate));
        dtm = (DefaultTableModel) jTableDetail.getModel();
        setCashBalance(null);
        addValidation();
        makeChildTableCashPayment();
        setPickListView();
        setIconToPnael();
        jTableDetail.setBackground(new Color(253, 243, 243));
        if (type == 0) {
            setTitle(Constants.BANK_PAYMENT_FORM_NAME);
        } else if (type == 1) {
            setTitle(Constants.BANK_RECEIPT_FORM_NAME);
        }
        jbtnEmail.setText(Constants.EMAIL_BUTTON);
    }

    private void setIconToPnael() {
        Syspath += File.separator + "Resources" + File.separator + "Images" + File.separator;
        jbtnAdd.setIcon(new ImageIcon(Syspath +"Add.png"));
        jbtnEmail.setIcon(new ImageIcon(Syspath +"E-mail.png"));
        jbtnChiddi.setIcon(new ImageIcon(Syspath +"chiddi.png"));
        jbtnClear.setIcon(new ImageIcon(Syspath +"cancel.png"));
        jbtnDelete.setIcon(new ImageIcon(Syspath +"delete.png"));
    }

    private void setInitial() {
        if (type == 0) {
            initial = Constants.BANK_PAYMENT_INITIAL;
        } else if (type == 1) {
            initial = Constants.BANK_RECEIPT_INITIAL;
        }
        jlblStart.setText(initial);
    }

    private void setPermission() {
        if (type == 0) {
            form_id = Constants.BANK_PAYMENT_FORM_ID;
            lb.setPermission(navLoad, form_id);
        } else if (type == 1) {
            form_id = Constants.BANK_RECEIPT_FORM_ID;
            lb.setPermission(navLoad, form_id);
        }
    }

    private void setPickListView() {
        accountPickList = new PickList(dataConnection);
        bankPickList = new PickList(dataConnection);

        accountPickList.setLayer(getLayeredPane());
        accountPickList.setPickListComponent(jtxtACAlias);
        accountPickList.setNextComponent(jtxtAmount);

        bankPickList.setLayer(getLayeredPane());
        bankPickList.setPickListComponent(jtxtBankName);
        bankPickList.setNextComponent(jtxtACAlias);
        bankPickList.setAllowBlank(true);
    }

    private void clear() {
        jtxtACAlias.setText("");
        jtxtAmount.setText("0.00");
        jtxtCheque.setText("");
        jtxtRemark.setText("");
    }

    private boolean reValidate() {
        boolean flag = true;

        if (!lb.isBlank(jtxtACAlias)) {
            if (!lb.isExist("account_master", "name", jtxtACAlias.getText())) {
                navLoad.setMessage(Constants.INVALID_ACCOUNT);
                jtxtACAlias.requestFocusInWindow();
                flag = flag && false;
            }
        } else {
            navLoad.setMessage(Constants.ACCOUNT_NOT_BLANK);
            jtxtACAlias.requestFocusInWindow();
            flag = flag && false;
        }

        if (lb.replaceAll(jtxtAmount.getText()) < 0) {
            navLoad.setMessage(Constants.INVALID_AMOUNT);
            jtxtAmount.requestFocusInWindow();
            flag = flag && false;
        }

        if (!lb.checkFinancialDate(jtxtDate)) {
            navLoad.setMessage(Constants.INVALID_CHEQUE_DATE);
            jtxtDate.requestFocusInWindow();
            flag = flag && false;
        }
        return flag;
    }

    private void addValidation() {

    }

    class fieldvalidation extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            boolean val = false;
            if (input.equals(jtxtBankName)) {
                val = fldValid(input);
            }
            return val;
        }
    }

    private boolean fldValid(Component comp) {
        navLoad.setMessage("");
        if (comp == jtxtBankName) {
            if (lb.isBlank(comp)) {
                navLoad.setMessage("Bank "+ Constants.ACCOUNT_NOT_BLANK);
                jtxtBankName.requestFocusInWindow();
                return false;
            }
            bank_cd = lb.getAccountMstName(jtxtBankName.getText(), "C");
            if(bank_cd.equalsIgnoreCase("")) {
                navLoad.setMessage("Bank "+ Constants.ACCOUNT_NOT_EXIST);
                jtxtBankName.requestFocusInWindow();
                return false;
            }
        }
        if (comp == jtxtVDate) {
            if(!lb.checkFinancialDate(jtxtVDate)){
                navLoad.setMessage(Constants.INVALID_VOUCHER_DATE);
                jtxtVDate.requestFocusInWindow();
                return false;
            }
        }
        return true;
    }

    private boolean validateData() {
        boolean flag = true;
        flag = flag && fldValid(jtxtBankName);
        flag = flag && fldValid(jtxtVDate);
        return flag;
    }

    private void makeChildTableCashPayment() {
        bankView = new ReportTable();

        bankView.AddColumn(0, "Voucher No", 100, java.lang.String.class, null, false);
        bankView.AddColumn(1, "Voucher Date", 100, java.lang.String.class, null, false);
        bankView.AddColumn(2, "Bank A/c", 200, java.lang.String.class, null, false);
        bankView.AddColumn(3, "Total Amount", 150, java.lang.Double.class, null, false);
        bankView.makeTable();
    }

    private void setCashBalance(String banckAC) {
        try {
            String bal = lb.getBalance("0", banckAC);
            if (lb.isNegative(bal)) {
                jlblDC.setText("CR");
            } else {
                jlblDC.setText("DR");
            }
            jlblBalAmt.setText(lb.getIndianFormat(Math.abs(lb.replaceAll(bal))));
        } catch (SQLException ex) {
            lb.printToLogFile("Error at setCashBalance In Bank Payment", ex);
        }
    }

    private void close() {
        this.dispose();
    }

    private void connectToNavigation() {
        class navPanel extends NavigationPanel1 {
            @Override
            public void callSave() throws Exception {
                try {
                    dataConnection.setAutoCommit(false);
                    saveVoucher();
                    navLoad.setMessage(Constants.SAVE_SUCCESS);
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
                        lb.printToLogFile("Exception at saveVoucher In Bank Payment", ex);
                    } catch (SQLException ex1) {
                        lb.printToLogFile("Exception at rollback saveVoucher In Bank Payment ", ex1);
                    }
                }
            }

            @Override
            public void callDelete() throws Exception {
                navLoad.setMessage("");
                int ans = JOptionPane.showConfirmDialog(null, Constants.DELETE_THIS + "voucher no. "+ id, "Confirm Delete", JOptionPane.OK_CANCEL_OPTION);
                if (ans == JOptionPane.OK_OPTION) {
                    try {
                        dataConnection.setAutoCommit(false);
                        BankPayRcptUpdate bp = new BankPayRcptUpdate();
                        bp.deleteEntry(id);

                        delete();
                        dataConnection.commit();
                        dataConnection.setAutoCommit(true);
                        setVoucher("last");
                    } catch (Exception ex) {
                        try {
                            lb.printToLogFile("error at delete In Bank Payment", ex);
                            dataConnection.rollback();
                            dataConnection.setAutoCommit(true);
                        } catch (SQLException ex1) {
                            lb.printToLogFile("error at rollback delete In Bank Payment", ex1);
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
                onPrintVoucher(0);
            }

            @Override
            public void callClose() {
                close();
            }

            @Override
            public void setVoucher(String tag) {
                try {
                    navLoad.setComponentEditable(false);
                    String sql = "SELECT * FROM bank_payment_receipt_head";
                    if (tag.equalsIgnoreCase("first")) {
                        sql += " WHERE id = (SELECT MIN(id) FROM bank_payment_receipt_head WHERE is_del = 0 AND ctype = "+ type +")";
                    } else if (tag.equalsIgnoreCase("previous")) {
                        sql += " WHERE id = (SELECT MAX(id) FROM bank_payment_receipt_head WHERE id < '"+ id +"' AND is_del = 0 AND ctype = "+ type +")";
                    } else if (tag.equalsIgnoreCase("next")) {
                        sql += " WHERE id = (SELECT MIN(id) FROM bank_payment_receipt_head WHERE id > '"+ id +"' AND is_del = 0 AND ctype = "+ type +")";
                    } else if (tag.equalsIgnoreCase("last")) {
                        sql += " WHERE id = (SELECT MAX(id) FROM bank_payment_receipt_head WHERE is_del = 0 AND ctype = "+ type +")";
                    } else if (tag.equalsIgnoreCase("edit")) {
                        sql += " WHERE id = '"+ id +"' AND is_del = 0 AND ctype = "+ type;
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
                    lb.printToLogFile("Exception at setVoucher In Bank Payment", ex);
                }
                setPermission();
                setTextfieldsAtBottom();
            }

            @Override
            public void setComponentText() {
                dtm.setRowCount(0);
                jtxtVoucher.setText("");
                setCashBalance(null);
                jlblTotalAmt.setText("0.00");
                jlblUser.setText("");
                jlblEditNo.setText("");
                jlblTimeStamp.setText("");
                lb.setDateChooserProperty(jtxtVDate);
                lb.setDateChooserProperty(jtxtDate);
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
                jBillDateBtn.setEnabled(bFlag);
                jtxtBankName.setEnabled(bFlag);
                jtxtVoucher.setEnabled(!bFlag);
                jTableDetail.setEnabled(bFlag);
                jtxtACAlias.setEnabled(bFlag);
                jtxtAmount.setEnabled(bFlag);
                jtxtCheque.setEnabled(bFlag);
                jtxtDate.setEnabled(bFlag);
                jBillDateBtn1.setEnabled(bFlag);
                jtxtRemark.setEnabled(bFlag);
                jbtnAdd.setEnabled(bFlag);
                jtxtVDate.requestFocusInWindow();
            }

            @Override
            public void setComponentTextFromRs() {
                try {
                    id = viewDataRs.getString("id");
                    jtxtVoucher.setText(id.substring(initial.length()));
                    jtxtVDate.setText(lb.ConvertDateFormetForDBForConcurrency(viewDataRs.getString("voucher_date")));
                    jtxtBankName.setText(lb.getAccountMstName(viewDataRs.getString("fk_bank_cd"), "N"));
                    jtxtAcNo.setText(lb.getAccountMstName(viewDataRs.getString("fk_bank_cd"), "CP"));
                    setCashBalance(viewDataRs.getString("fk_bank_cd"));
                    jlblTotalAmt.setText(lb.getIndianFormat(viewDataRs.getDouble("total_bal")));
                    jlblDay.setText(lb.setDay(jtxtVDate));
                    jlblUser.setText(lb.getUserName(viewDataRs.getString("user_cd"), "N"));
                    jlblEditNo.setText(viewDataRs.getString("edit_no"));
                    jlblTimeStamp.setText(lb.ConvertTimeStampFormetForDisplay(viewDataRs.getString("time_stamp")));

                    viewDataRs = fetchData("SELECT * FROM bank_payment_receipt_details WHERE id = '"+ id +"'");
                    dtm.setRowCount(0);
                    int i = 0;

                    while (viewDataRs.next()) {
                        String chq_dt = viewDataRs.getString("chq_dt");
                        if(chq_dt == null) {
                            chq_dt = "";
                        } else {
                            chq_dt = lb.ConvertDateFormetForDBForConcurrency(viewDataRs.getString("chq_dt"));
                        }
                        Vector row = new Vector();
                        row.add(++i);
                        row.add(lb.getAccountMstName(viewDataRs.getString("fk_account_master_id"), "N"));
                        row.add(lb.getIndianFormat(viewDataRs.getDouble("amount")));
                        row.add(viewDataRs.getString("chq_no"));
                        row.add(chq_dt);
                        row.add(viewDataRs.getString("remark"));
                        dtm.addRow(row);
                    }
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setComponentTextFromRs In Bank Payment", ex);
                }
            }
        }

        navLoad = new navPanel();
        jPanel2.add(navLoad);
        navLoad.setVisible(true);
    }

    public void setID(String id) {
        this.id = id;
        navLoad.setVoucher("edit");
    }

    private void onPrintVoucher(int vtype) {
        VoucherDisplay vd = new VoucherDisplay(id, "", initial, type, vtype);
        if(type == 0) {
            DeskFrame.addOnScreen(vd, Constants.BANK_PAYMENT_FORM_NAME +" PRINT");
        } else if(type == 1) {
            DeskFrame.addOnScreen(vd, Constants.BANK_RECEIPT_FORM_NAME +" PRINT");
        }
    }

    private void onviewVoucher() {
        this.dispose();

        String sql = "SELECT bh.id, bh.voucher_date, am.name AS ac_name, bh.total_bal FROM bank_payment_receipt_head bh, account_master am " +
            "WHERE bh.is_del = 0 AND bh.fk_bank_cd = am.id AND bh.ctype = "+ type +" ORDER BY bh.id";
        bankView.setColumnValue(new int[]{1, 2, 3, 4});
        String view_title;

        HeaderIntFrame1 rptDetail;
        if (type == 0) {
            view_title = Constants.BANK_PAYMENT_FORM_NAME +" VIEW";
        } else {
            view_title = Constants.BANK_RECEIPT_FORM_NAME +" VIEW";
        }
        rptDetail = new HeaderIntFrame1(dataConnection, id, view_title, bankView, sql, form_id, 1, this, this.getTitle());
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
            lb.printToLogFile("Exception at dispose In Bank Payment", ex);
        }
    }

    private int saveVoucher() throws SQLException, ParseException, FileNotFoundException, IOException {
        String sql = null;

        PreparedStatement psLocal = null;
        int change = 0;

        if (navLoad.getMode().equalsIgnoreCase("N")) {
            sql = "INSERT INTO bank_payment_receipt_head (voucher_date, total_bal, user_cd, ctype, fk_bank_cd, fix_time, id) VALUES (?, ?, ?, ?, ?, '"+ new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) +"', ?)";
            id = lb.generateKey("bank_payment_receipt_head", "id", 7, initial); // GENERAE REF NO
        } else if (navLoad.getMode().equalsIgnoreCase("E")) {
            BankPayRcptUpdate bp = new BankPayRcptUpdate();
            bp.deleteEntry(id);

            sql = "DELETE FROM bank_payment_receipt_details WHERE id = '"+ id +"'";
            psLocal = dataConnection.prepareStatement(sql);
            change += psLocal.executeUpdate();

            sql = "UPDATE bank_payment_receipt_head SET voucher_date = ?, total_bal = ?, user_cd = ?, ctype = ?, fk_bank_cd = ?, edit_no = edit_no + 1, time_stamp = CURRENT_TIMESTAMP WHERE id = ?";
        }
        psLocal = dataConnection.prepareStatement(sql);
        psLocal.setString(1, lb.tempConvertFormatForDBorConcurrency(jtxtVDate.getText())); // V DATE
        psLocal.setDouble(2, lb.replaceAll(jlblTotalAmt.getText())); // TOTAL BAL
        psLocal.setInt(3, DeskFrame.user_id); // USER CD
        psLocal.setInt(4, type); // C TYPE
        psLocal.setString(5, bank_cd); // BANK CD
        psLocal.setString(6, id); // REF NO
        change += psLocal.executeUpdate();

        sql = "INSERT INTO bank_payment_receipt_details(sr_no, fk_account_master_id, amount, chq_no, chq_dt, remark, id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        psLocal = dataConnection.prepareStatement(sql);
        for (int i = 0; i < jTableDetail.getRowCount(); i++) {
            String accd = lb.getAccountMstName(jTableDetail.getValueAt(i, 1).toString(), "C"); // ACCOUNT CD
            String chq_dt = jTableDetail.getValueAt(i, 4).toString(); // CHEQUE DATE
            if (!(accd.equalsIgnoreCase("0") || accd.equalsIgnoreCase("") || lb.replaceAll(jTableDetail.getValueAt(i, 2).toString()) < 0)) {
                if (lb.replaceAll(jTableDetail.getValueAt(i, 2).toString()) > 0) { // Check Amount
                    if(chq_dt.equalsIgnoreCase("")) {
                        chq_dt = null;
                    } else {
                        chq_dt = lb.ConvertDateFormetForDB(chq_dt);
                    }
                    psLocal.setInt(1, i + 1); // SR No
                    psLocal.setString(2, accd); // AC CD
                    psLocal.setDouble(3, lb.replaceAll(jTableDetail.getValueAt(i, 2).toString())); // BAL
                    psLocal.setString(4, jTableDetail.getValueAt(i, 3).toString()); // CHQ NO
                    psLocal.setString(5, chq_dt); // CHQ DATE
                    psLocal.setString(6, jTableDetail.getValueAt(i, 5).toString()); // REMARK
                    psLocal.setString(7, id); // REF NO
                    change += psLocal.executeUpdate();
                }
            }
        }
        BankPayRcptUpdate bp = new BankPayRcptUpdate();
        bp.addEntry(id);
        return change;
    }

    private void delete() throws SQLException {
        PreparedStatement psLocal = dataConnection.prepareStatement("DELETE FROM bank_payment_receipt_head WHERE id = '"+ id +"'");
        psLocal.executeUpdate();
        psLocal = dataConnection.prepareStatement("DELETE FROM bank_payment_receipt_details WHERE id = '"+ id +"'");
        psLocal.executeUpdate();
        lb.closeStatement(psLocal);
    }

    private void deleteItem() {
        int row = jTableDetail.getSelectedRow();
        if (row != -1) {
            if (JOptionPane.showConfirmDialog(this, Constants.DELETE_THIS + jTableDetail.getValueAt(row, 1).toString() +" account entry?", DeskFrame.TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dtm.removeRow(row);
                updateTotal();
                jTableDetail.clearSelection();
            }
        }
    }

    private void updateTotal() {
        double amt = 0.00;
        for (int i = 0; i < jTableDetail.getRowCount(); i++) {
            jTableDetail.setValueAt(i + 1, i, 0);
            amt += lb.replaceAll(jTableDetail.getValueAt(i, 2).toString());
        }
        jlblTotalAmt.setText(lb.getIndianFormat(amt) + "");
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void setTextfieldsAtBottom() {
        JComponent[] header = new JComponent[]{null, jtxtACAlias, jtxtAmount, jtxtCheque, jPanelDate, jtxtRemark};
        JComponent[] footer = new JComponent[]{null, null, jlblTotalAmt, null, null, null};
        lb.setTable(jPanel1, jTableDetail, header, footer);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableDetail = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jlblUser = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jlblTimeStamp = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jlblStart = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jtxtVoucher = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtVDate = new javax.swing.JTextField();
        jBillDateBtn = new javax.swing.JButton();
        jlblDay = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jtxtBankName = new javax.swing.JTextField();
        jlblBalAmt = new javax.swing.JLabel();
        jlblDC = new javax.swing.JLabel();
        jtxtAcNo = new javax.swing.JTextField();
        jbtnAdd = new javax.swing.JButton();
        jbtnEmail = new javax.swing.JButton();
        jlblTotalAmt = new javax.swing.JLabel();
        jtxtACAlias = new javax.swing.JTextField();
        jtxtAmount = new javax.swing.JTextField();
        jtxtCheque = new javax.swing.JTextField();
        jtxtRemark = new javax.swing.JTextField();
        jPanelDate = new javax.swing.JPanel();
        jBillDateBtn1 = new javax.swing.JButton();
        jtxtDate = new javax.swing.JTextField();
        jbtnDelete = new javax.swing.JButton();
        jbtnClear = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jbtnChiddi = new javax.swing.JButton();

        setBackground(new java.awt.Color(211, 226, 245));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 1, 1, new java.awt.Color(4, 110, 152)));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(new java.awt.Color(253, 243, 243));
        jScrollPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(4, 110, 152), 1, true));

        jTableDetail.setBackground(new java.awt.Color(253, 243, 243));
        jTableDetail.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jTableDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Account Name", "Amount", "Chq No", "Chq Date", "Remark"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableDetail.setRowHeight(23);
        jTableDetail.getTableHeader().setReorderingAllowed(false);
        jTableDetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableDetailMouseClicked(evt);
            }
        });
        jTableDetail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableDetailKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTableDetail);
        jTableDetail.getColumnModel().getColumn(0).setResizable(false);
        jTableDetail.getColumnModel().getColumn(0).setPreferredWidth(30);
        jTableDetail.getColumnModel().getColumn(1).setPreferredWidth(200);
        jTableDetail.getColumnModel().getColumn(2).setResizable(false);
        jTableDetail.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTableDetail.getColumnModel().getColumn(3).setResizable(false);
        jTableDetail.getColumnModel().getColumn(3).setPreferredWidth(50);
        jTableDetail.getColumnModel().getColumn(4).setResizable(false);
        jTableDetail.getColumnModel().getColumn(4).setPreferredWidth(80);
        jTableDetail.getColumnModel().getColumn(5).setResizable(false);
        jTableDetail.getColumnModel().getColumn(5).setPreferredWidth(300);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setBackground(new java.awt.Color(253, 243, 243));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 1, 1, new java.awt.Color(235, 35, 35)));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel12.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel12.setText("User Name : ");

        jlblUser.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jLabel13.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel13.setText("Edit No :");

        jLabel15.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel15.setText("Last Updated : ");

        jlblTimeStamp.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jlblEditNo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jPanel3.setBackground(new java.awt.Color(253, 243, 243));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)), "Voucher Information", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 16), new java.awt.Color(0, 0, 255))); // NOI18N

        jlblStart.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlblStart.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblStart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel1.setText("Voucher No");

        jtxtVoucher.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtVoucher.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtVoucher.setNextFocusableComponent(jtxtVDate);
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

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("Voucher Date");

        jtxtVDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtVDate.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtVDate.setNextFocusableComponent(jtxtBankName);
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

        jBillDateBtn.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });
        jBillDateBtn.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jBillDateBtnFocusGained(evt);
            }
        });

        jlblDay.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlblDay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblDay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 0, 0));
        jLabel4.setText("Bank A/c");

        jtxtBankName.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtBankName.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtBankName.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jtxtBankNameComponentResized(evt);
            }
        });
        jtxtBankName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtBankNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtBankNameFocusLost(evt);
            }
        });
        jtxtBankName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtBankNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtBankNameKeyReleased(evt);
            }
        });

        jlblBalAmt.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblBalAmt.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblBalAmt.setText("0.00");
        jlblBalAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jlblDC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlblDC.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblDC.setText("CR");
        jlblDC.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0)));

        jtxtAcNo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtAcNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAcNo.setEnabled(false);

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jtxtBankName, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblBalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblDC, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAcNo, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                        .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jlblStart, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtVDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblDay, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel4});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblDay, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtVDate)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblStart, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnAdd)
                    .addComponent(jtxtAcNo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblDC, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblBalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtBankName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jlblStart, jtxtVDate, jtxtVoucher});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jLabel4, jbtnAdd, jlblBalAmt, jlblDC, jtxtAcNo, jtxtBankName});

        jbtnEmail.setBackground(new java.awt.Color(204, 255, 204));
        jbtnEmail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnEmail.setForeground(new java.awt.Color(235, 35, 35));
        jbtnEmail.setText("EMAIL");
        jbtnEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEmailActionPerformed(evt);
            }
        });

        jlblTotalAmt.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblTotalAmt.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblTotalAmt.setText("0.00");
        jlblTotalAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jlblTotalAmt.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jlblTotalAmtComponentMoved(evt);
            }
        });

        jtxtACAlias.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtACAlias.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
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

        jtxtAmount.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtAmount.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAmount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAmountFocusGained(evt);
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

        jtxtCheque.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtCheque.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtCheque.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtChequeFocusGained(evt);
            }
        });
        jtxtCheque.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtChequeKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtChequeKeyTyped(evt);
            }
        });

        jtxtRemark.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtRemark.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRemarkFocusGained(evt);
            }
        });
        jtxtRemark.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRemarkKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtRemarkKeyTyped(evt);
            }
        });

        jBillDateBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn1ActionPerformed(evt);
            }
        });

        jtxtDate.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtDate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDateFocusLost(evt);
            }
        });
        jtxtDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDateKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanelDateLayout = new javax.swing.GroupLayout(jPanelDate);
        jPanelDate.setLayout(jPanelDateLayout);
        jPanelDateLayout.setHorizontalGroup(
            jPanelDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDateLayout.createSequentialGroup()
                .addComponent(jtxtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanelDateLayout.setVerticalGroup(
            jPanelDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDateLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanelDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

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

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("CTRL + T For Select Row in Table");

        jbtnChiddi.setBackground(new java.awt.Color(204, 255, 204));
        jbtnChiddi.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnChiddi.setForeground(new java.awt.Color(235, 35, 35));
        jbtnChiddi.setText("CHIDDI");
        jbtnChiddi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbtnChiddiMouseClicked(evt);
            }
        });
        jbtnChiddi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnChiddiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jlblTotalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel15)
                                .addGap(5, 5, 5)
                                .addComponent(jlblTimeStamp, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jbtnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jtxtACAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtCheque, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jtxtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnChiddi, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtACAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblTotalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnChiddi)
                    .addComponent(jbtnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblTimeStamp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanelDate, jtxtACAlias, jtxtAmount, jtxtCheque, jtxtRemark});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, jbtnChiddi, jbtnClear, jbtnDelete, jbtnEmail, jlblTimeStamp});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtVoucherFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVoucherFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtVoucherFocusGained

    private void jTableDetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableDetailMouseClicked
        if (evt.getClickCount() == 2) {
            evt.consume();
            if (!navLoad.getMode().equalsIgnoreCase("")) {
                int rowSel = jTableDetail.getSelectedRow();
                if (rowSel != -1) {
                    jtxtACAlias.setText(jTableDetail.getValueAt(rowSel, 1).toString());
                    jtxtAmount.setText(jTableDetail.getValueAt(rowSel, 2).toString());
                    jtxtCheque.setText(jTableDetail.getValueAt(rowSel, 3).toString());
                    jtxtDate.setText(jTableDetail.getValueAt(rowSel, 4).toString());
                    jtxtRemark.setText(jTableDetail.getValueAt(rowSel, 5).toString());
                    jtxtACAlias.requestFocusInWindow();
                }
            }
        }
    }//GEN-LAST:event_jTableDetailMouseClicked

    private void jtxtBankNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBankNameFocusGained
        lb.selectAll(evt);
        lb.sendKeys(KeyEvent.VK_RIGHT);
    }//GEN-LAST:event_jtxtBankNameFocusGained

    private void jtxtBankNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBankNameKeyPressed
        bankPickList.setLocation(jtxtBankName.getX() + jPanel3.getX(), jPanel3.getY() + jtxtBankName.getY() + jtxtBankName.getHeight());
        bankPickList.pickListKeyPress(evt);
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jtxtACAlias.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtBankNameKeyPressed

    private void jtxtBankNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtBankNameKeyReleased
        try {
            bankPickList.setReturnComponent(new JTextField[]{jtxtBankName, jtxtAcNo});
            bankPickList.setFirstAssociation(new int[]{0, 1});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name AS bank, id AS ac_no FROM account_master WHERE name LIKE '%"+ jtxtBankName.getText().toUpperCase() +"%'");
            bankPickList.setValidation(dataConnection.prepareStatement("SELECT name FROM account_master WHERE name = ?"));
            bankPickList.setPreparedStatement(pstLocal);
            bankPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtBankNameKeyReleased In Bank Payment", ex);
        }
    }//GEN-LAST:event_jtxtBankNameKeyReleased

    private void jlblTotalAmtComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblTotalAmtComponentMoved
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jlblTotalAmtComponentMoved

    private void jtxtVDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtVDateFocusGained

    private void jtxtVDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVDateFocusLost
        lb.setDateUsingJTextField(jtxtVDate, navLoad);
    }//GEN-LAST:event_jtxtVDateFocusLost

    private void jtxtVDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVDateKeyPressed
        lb.enterFocus(evt, jtxtBankName);
    }//GEN-LAST:event_jtxtVDateKeyPressed

    private void jBillDateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtnActionPerformed
        odc = new OurDateChooser();
        odc.setnextFocus(jtxtVDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtVDate.getX(), jtxtVDate.getY() + 145, jtxtVDate.getX() + odc.getWidth(), jtxtVDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, Constants.SELECT_DATE);
    }//GEN-LAST:event_jBillDateBtnActionPerformed

    private void jBillDateBtnFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jBillDateBtnFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jBillDateBtnFocusGained

    private void jTableDetailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableDetailKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            evt.consume();
            if (!navLoad.getMode().equalsIgnoreCase("")) {
                deleteItem();
            }
        }
    }//GEN-LAST:event_jTableDetailKeyPressed

    private void jtxtBankNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBankNameFocusLost
        bankPickList.setVisible(false);
        setCashBalance(lb.getAccountMstName(jtxtBankName.getText(), "C"));
    }//GEN-LAST:event_jtxtBankNameFocusLost

    private void jtxtVoucherKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVoucherKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if(lb.isExist("bank_payment_receipt_head", "id", initial + jtxtVoucher.getText(), dataConnection)) {
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
        lb.onlyNumber(evt, (7 - initial.length()));
    }//GEN-LAST:event_jtxtVoucherKeyTyped

    private void jbtnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEmailActionPerformed
        EmailSelect ds = new EmailSelect(MainClass.df, true, id, initial, type);
        ds.show();
    }//GEN-LAST:event_jbtnEmailActionPerformed

    private void jtxtBankNameComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtxtBankNameComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jtxtBankNameComponentResized

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_formComponentResized

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        int rowSel = jTableDetail.getSelectedRow();
        if (reValidate()) {
            navLoad.setMessage("");
            if (rowSel == -1) {
                Vector row = new Vector();
                row.add("");
                row.add(jtxtACAlias.getText());
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtAmount.getText())));
                row.add(jtxtCheque.getText());
                row.add(jtxtDate.getText());
                row.add(jtxtRemark.getText());
                dtm.addRow(row);
            } else {
                jTableDetail.setValueAt(jtxtACAlias.getText(), rowSel, 1);
                jTableDetail.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtAmount.getText())), rowSel, 2);
                jTableDetail.setValueAt(jtxtCheque.getText(), rowSel, 3);
                jTableDetail.setValueAt(jtxtDate.getText(), rowSel, 4);
                jTableDetail.setValueAt(jtxtRemark.getText(), rowSel, 5);
            }
            updateTotal();
            clear();
            jTableDetail.clearSelection();
            if (JOptionPane.showConfirmDialog(this, Constants.ADD_MORE_ENTRY, DeskFrame.TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                jtxtACAlias.requestFocusInWindow();
            } else {
                navLoad.setSaveFocus();
            }
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jtxtACAliasComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtxtACAliasComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jtxtACAliasComponentResized

    private void jtxtACAliasFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtACAliasFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtACAliasFocusGained

    private void jtxtACAliasFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtACAliasFocusLost
        accountPickList.setVisible(false);
    }//GEN-LAST:event_jtxtACAliasFocusLost

    private void jtxtACAliasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtACAliasKeyPressed
        accountPickList.pickListKeyPress(evt);
        lb.downFocus(evt, navLoad);
        if(evt.getKeyCode() == KeyEvent.VK_F2) {
            try {
                PopUpAccountMaster glp = new PopUpAccountMaster(null, true);
                if(type == 0) {
                    glp.getData(Constants.SUNDRY_CREDITORS, jtxtACAlias.getText(), 0);
                } else {
                    glp.getData(Constants.SUNDRY_DEBTORS, jtxtACAlias.getText(), 0);
                }
                glp.jtxtAccountName.requestFocusInWindow();
                glp.setLocation(jtxtACAlias.getX(), jtxtACAlias.getY() + jtxtACAlias.getHeight());

                glp.show();
            } catch(Exception ex) {
                lb.printToLogFile("Exception at openPopUp In Bank Payment", ex);
            }
        }
    }//GEN-LAST:event_jtxtACAliasKeyPressed

    private void jtxtACAliasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtACAliasKeyReleased
        try {
            accountPickList.setLocation(jtxtACAlias.getX(), jtxtACAlias.getY() + jtxtACAlias.getHeight());
            accountPickList.setReturnComponent(new JTextField[]{jtxtACAlias});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM account_master WHERE name LIKE '%"+ jtxtACAlias.getText().toUpperCase() +"%'");
            accountPickList.setValidation(dataConnection.prepareStatement("SELECT name FROM account_master WHERE name = ?"));
            accountPickList.setPreparedStatement(pstLocal);
            accountPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtACAliasKeyReleased In Bank Payment", ex);
        }
    }//GEN-LAST:event_jtxtACAliasKeyReleased

    private void jtxtAmountFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmountFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAmountFocusGained

    private void jtxtAmountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmountKeyPressed
        lb.enterFocus(evt, jtxtCheque);
    }//GEN-LAST:event_jtxtAmountKeyPressed

    private void jtxtAmountKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmountKeyTyped
        lb.onlyNumber(evt, jtxtAmount.getText().length() + 1);
    }//GEN-LAST:event_jtxtAmountKeyTyped

    private void jtxtChequeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtChequeFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtChequeFocusGained

    private void jtxtChequeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtChequeKeyPressed
        lb.enterFocus(evt, jtxtDate);
    }//GEN-LAST:event_jtxtChequeKeyPressed

    private void jtxtChequeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtChequeKeyTyped
        lb.onlyNumber(evt, jtxtCheque.getText().length() + 1);
    }//GEN-LAST:event_jtxtChequeKeyTyped

    private void jtxtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRemarkFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRemarkFocusGained

    private void jtxtRemarkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRemarkKeyPressed
        lb.enterFocus(evt, jbtnAdd);
    }//GEN-LAST:event_jtxtRemarkKeyPressed

    private void jtxtRemarkKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRemarkKeyTyped
        lb.fixLength(evt, 200);
    }//GEN-LAST:event_jtxtRemarkKeyTyped

    private void jBillDateBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtn1ActionPerformed
        odc = new OurDateChooser();
        odc.setnextFocus(jtxtDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jPanelDate.getX() + jtxtDate.getX(), jPanelDate.getY() + jtxtDate.getY() + 145, jtxtDate.getX() + odc.getWidth(), jtxtDate.getY() + odc.getHeight());
        odc.showDialog(jp, Constants.SELECT_DATE);
    }//GEN-LAST:event_jBillDateBtn1ActionPerformed

    private void jtxtDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDateFocusGained

    private void jtxtDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDateFocusLost
        lb.setDateUsingJTextField(jtxtDate, navLoad);
    }//GEN-LAST:event_jtxtDateFocusLost

    private void jtxtDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDateKeyPressed
        lb.enterFocus(evt, jtxtRemark);
    }//GEN-LAST:event_jtxtDateKeyPressed

    private void jbtnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteActionPerformed
        int row = jTableDetail.getSelectedRow();
        int cnt = jTableDetail.getRowCount();
        if (cnt > 1) {
            if (row != -1) {
                if (JOptionPane.showConfirmDialog(this, Constants.DELETE_ROW + jTableDetail.getValueAt(row, 0).toString(), DeskFrame.TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    dtm.removeRow(row);
                    updateTotal();
                }
                jTableDetail.clearSelection();
            }
        }
    }//GEN-LAST:event_jbtnDeleteActionPerformed

    private void jbtnClearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnClearMouseClicked
        jTableDetail.clearSelection();
    }//GEN-LAST:event_jbtnClearMouseClicked

    private void jbtnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnClearActionPerformed
        jTableDetail.clearSelection();
    }//GEN-LAST:event_jbtnClearActionPerformed

    private void jbtnChiddiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnChiddiMouseClicked
        onPrintVoucher(1);
    }//GEN-LAST:event_jbtnChiddiMouseClicked

    private void jbtnChiddiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnChiddiActionPerformed
        onPrintVoucher(1);
    }//GEN-LAST:event_jbtnChiddiActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jBillDateBtn1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelDate;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableDetail;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JButton jbtnChiddi;
    private javax.swing.JButton jbtnClear;
    private javax.swing.JButton jbtnDelete;
    private javax.swing.JButton jbtnEmail;
    private javax.swing.JLabel jlblBalAmt;
    private javax.swing.JLabel jlblDC;
    private javax.swing.JLabel jlblDay;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblStart;
    private javax.swing.JLabel jlblTimeStamp;
    private javax.swing.JLabel jlblTotalAmt;
    private javax.swing.JLabel jlblUser;
    private javax.swing.JTextField jtxtACAlias;
    private javax.swing.JTextField jtxtAcNo;
    private javax.swing.JTextField jtxtAmount;
    private javax.swing.JTextField jtxtBankName;
    private javax.swing.JTextField jtxtCheque;
    private javax.swing.JTextField jtxtDate;
    private javax.swing.JTextField jtxtRemark;
    private javax.swing.JTextField jtxtVDate;
    private javax.swing.JTextField jtxtVoucher;
    // End of variables declaration//GEN-END:variables
}