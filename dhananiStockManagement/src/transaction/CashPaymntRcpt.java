/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import master.PopUpAccountMaster;
import dhananistockmanagement.DeskFrame;
import oldbupdate.CashBankUpdate;
import dhananistockmanagement.MainClass;
import support.Constants;
import support.EmailSelect;
import support.HeaderIntFrame1;
import support.Library;
import support.NavigationPanel;
import support.OurDateChooser;
import support.PickList;
import support.ReportTable;
import support.VoucherDisplay;

/**
 *
 * @author @JD@
 */
public class CashPaymntRcpt extends javax.swing.JInternalFrame {
    private NavigationPanel navLoad;
    private Library lb = new Library();
    private Connection dataConnection = DeskFrame.connMpAdmin;
    PickList acPickListView = null;
    private String id = "";
    private DefaultTableModel model = null;
    private ReportTable viewTable = null;
    private int mode = 0;
    private String initial = "", form_id = "";
    String Syspath = System.getProperty("user.dir");

    /**
     * Creates new form CashPaymntRcpt
     */
    public CashPaymntRcpt() {
        initComponents();
        initOtherComponents();
        setVoucher("Last");
    }

    public CashPaymntRcpt(int mode) {
        initComponents();
        this.mode = mode;
        initOtherComponents();
        setVoucher("Last");
    }

    public CashPaymntRcpt(int mode, String id) {
        initComponents();
        this.mode = mode;
        jtxtVoucher.setText(id.substring(initial.length()));
        this.id = initial + jtxtVoucher.getText();
        initOtherComponents();
        setVoucher("edit");
    }

    private void initOtherComponents() {
        setInitial();
        jtxtVoucher.requestFocusInWindow();
        acPickListView = new PickList(dataConnection);
        model = (DefaultTableModel) jtableDet.getModel();
        connectToNavigation();
        addValidation();
        setPickListView();
        tableForView();
        setTextFieldsAtBottom();
        lb.selectRowShortcut(this, jtableDet, jtxtDate);
        setIconToPanel();
        jtableDet.setBackground(new Color(253, 243, 243));
        if (mode == 1) {
            setTitle(Constants.CASH_PAYMENT_FORM_NAME);
        } else if (mode == 2) {
            setTitle(Constants.CASH_RECEIPT_FORM_NAME);
        }
        jbtnEmail.setText(Constants.EMAIL_BUTTON);
    }

    private void setInitial() {
        if (mode == 1) {
            initial = Constants.CASH_PAYMENT_INITIAL;
        } else if (mode == 2) {
            initial = Constants.CASH_RECEIPT_INITIAL;
        }
        jlblStart.setText(initial);
    }

    private void setPermission() {
        if (mode == 1) {
            form_id = Constants.CASH_PAYMENT_FORM_ID;
            lb.setPermission(navLoad, form_id);
        } else if (mode == 2) {
            form_id = Constants.CASH_RECEIPT_FORM_ID;
            lb.setPermission(navLoad, form_id);
        }
    }

    private void setPickListView() {
        acPickListView.setLayer(getLayeredPane());
        acPickListView.setPickListComponent(jtxtAcntName);
        acPickListView.setNextComponent(jtxtAmt);
    }

    private void clear() {
        jtxtAcntName.setText("");
        jtxtAmt.setText("0.00");
        jtxtParticular.setText("");
    }

    private boolean reValidate() {
        boolean flag = true;

        if (!lb.isBlank(jtxtAcntName)) {
            if (!lb.isExist("account_master", "name", jtxtAcntName.getText())) {
                navLoad.setMessage(Constants.INVALID_ACCOUNT);
                jtxtAcntName.requestFocusInWindow();
                flag = flag && false;
            }
        } else {
            navLoad.setMessage(Constants.ACCOUNT_NOT_BLANK);
            jtxtAcntName.requestFocusInWindow();
            flag = flag && false;
        }

        if (lb.replaceAll(jtxtAmt.getText()) < 0) {
            navLoad.setMessage(Constants.INVALID_AMOUNT);
            jtxtAmt.requestFocusInWindow();
            flag = flag && false;
        }
        return flag;
    }

    private void setIconToPanel() {
        Syspath += File.separator + "Resources" + File.separator + "Images" + File.separator;
        jbtnAdd.setIcon(new ImageIcon(Syspath +"add.png"));
        jbtnEmail.setIcon(new ImageIcon(Syspath +"E-mail.png"));
        jbtnChiddi.setIcon(new ImageIcon(Syspath +"chiddi.png"));
        jbtnClear.setIcon(new ImageIcon(Syspath +"cancel.png"));
        jbtnDelete.setIcon(new ImageIcon(Syspath +"delete.png"));
    }

    private void tableForView() {
        viewTable = new ReportTable();

        viewTable.AddColumn(0, "Ref No", 100, java.lang.String.class, null, false);
        viewTable.AddColumn(1, "Date", 100, java.lang.String.class, null, false);
        viewTable.AddColumn(2, "Total Amt", 100, java.lang.String.class, null, false);
        viewTable.makeTable();
    }

    public void setID(String id) {
        this.id = id;
        setVoucher("Edit");
    }

    private void onViewVoucher() {
        this.dispose();

        String sql = "SELECT id, voucher_date, total_bal FROM cash_payment_receipt_head WHERE is_del = 0 AND ctype = "+ mode +" ORDER BY id";
        viewTable.setColumnValue(new int[]{1, 2, 3});
        String view_title;
        HeaderIntFrame1 rptDetail;
        if (mode == 1) {
            view_title = Constants.CASH_PAYMENT_FORM_NAME +" VIEW";
        } else {
            view_title = Constants.CASH_RECEIPT_FORM_NAME +" VIEW";
        }
        rptDetail = new HeaderIntFrame1(dataConnection, id, view_title, viewTable, sql, form_id, 1, this, this.getTitle());
        rptDetail.makeView();
        rptDetail.setVisible(true);

        Component c = DeskFrame.tabbedPane.add(view_title, rptDetail);
        c.setName(view_title);
        DeskFrame.tabbedPane.setSelectedComponent(c);
    }

    private void setDay() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEE");
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal = Calendar.getInstance();
            java.util.Date dt = sdfDate.parse(jtxtDate.getText());
            cal.setTime(dt);
            jlblCurDay.setText(sdf.format(cal.getTime()));
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setDay in Cash Payment/Receipt", ex);
        }
    }

    private void addValidation() {
        FieldValidation valid = new FieldValidation();
        jtxtDate.setInputVerifier(valid);
    }

    class FieldValidation extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            boolean val = false;
            ((JTextField) input).setText(((JTextField) input).getText().toUpperCase());

            val = fielddValid(input);

            return val;
        }
    }

    private boolean fielddValid(Component comp) {
        navLoad.setMessage("");
        if (comp == jtxtDate) {
            if (lb.ConvertDateFormetForDB(jtxtDate.getText()).equalsIgnoreCase("")) {
                navLoad.setMessage(Constants.INVALID_DATE);
                comp.requestFocusInWindow();
                return false;
            }
        }
        return true;
    }

    private boolean validateForm() {
        boolean flag = true;
        flag = flag && fielddValid(jtxtDate);
        if (jtableDet.getRowCount() > 0) {
            flag = flag && true;
        } else {
            flag = flag && false;
        }
        return flag;
    }

    private void setTotal() {
        double amt = 0.00;
        for (int i = 0; i < jtableDet.getRowCount(); i++) {
            amt += lb.replaceAll(jtableDet.getValueAt(i, 1).toString());
        }
        jlblAmtTotal.setText(lb.getIndianFormat(amt));
    }

    private void setComponentText() {
        jtxtVoucher.setText("");
        jtxtRemark.setText("");
        jlblEditNo.setText("");
        jlblUserName.setText("");
        jlblLstUpdate.setText("");
        jlblAmtTotal.setText("0.00");
    }

    private void setVoucher(String tag) {
        navLoad.setComponentEnabledDisabled(false);
        String sql = "SELECT * FROM cash_payment_receipt_head";
        if (tag.equalsIgnoreCase("First")) {
            sql += " WHERE id LIKE 'C%' AND ctype = "+ mode +" AND is_del = 0 ORDER BY id";
        } else if (tag.equalsIgnoreCase("Previous")) {
            sql += " WHERE id < '"+ id +"' AND ctype = "+ mode +" AND id LIKE 'C%' AND is_del = 0 ORDER BY id DESC";
        } else if (tag.equalsIgnoreCase("Next")) {
            sql += " WHERE id > '"+ id +"' AND ctype = "+ mode +" AND id LIKE 'C%' AND is_del = 0 ORDER BY id DESC";
        } else if (tag.equalsIgnoreCase("Last")) {
            sql += " WHERE id LIKE 'C%' AND ctype = "+ mode +" AND is_del = 0 ORDER BY id DESC";
        } else if (tag.equalsIgnoreCase("Edit")) {
            sql += " WHERE id = '"+ id +"' AND ctype = "+ mode +"";
        }
        navLoad.viewData = lb.fetchData(sql, dataConnection);
        try {
            if (navLoad.viewData.next()) {
                navLoad.setComponentTextFromResultSet();
            } else {
                if(tag.equalsIgnoreCase("last")) {
                    setComponentText();
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setVoucher In Cash Payment/Receipt", ex);
        }
        setPermission();
    }

    private void cancelOrClose() {
        if (navLoad.getSaveFlag()) {
            this.dispose();
        } else {
            acPickListView.setVisible(false);
            navLoad.setMode("");
            navLoad.setComponentEnabledDisabled(false);
            navLoad.setMessage("");
            navLoad.setSaveFlag(true);
        }
    }

    @Override
    public void dispose() {
        try {
            DeskFrame.removeFromScreen(DeskFrame.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose In Cash Payment/Receipt", ex);
        }
    }

    private void onPrintVoucher(int type) {
        VoucherDisplay vd = new VoucherDisplay(id, "", initial, mode, type);
        if(mode == 1) {
            DeskFrame.addOnScreen(vd, Constants.CASH_PAYMENT_FORM_NAME +" PRINT");
        } else if(mode == 2) {
            DeskFrame.addOnScreen(vd, Constants.CASH_RECEIPT_FORM_NAME +" PRINT");
        }
    }

    private void connectToNavigation() {
        class navPanel extends NavigationPanel {
            @Override
            public void callNew() {
                lb.setDateChooserProperty(jtxtDate);
                jtxtDate.requestFocusInWindow();
                setMode("N");
                setDay();
                setComponentText();
                setComponentEnabledDisabled(true);
                setSaveFlag(false);
                model.setRowCount(0);
            }

            @Override
            public void callEdit() {
                jtxtDate.requestFocusInWindow();
                setMode("E");
                setSaveFlag(false);
                setComponentEnabledDisabled(true);
            }

            @Override
            public void callSave() {
                try {
                    setSaveFlag(false);
                    boolean valid = validateForm();
                    if (valid) {
                        dataConnection.setAutoCommit(false);
                        saveVoucher();
                        dataConnection.commit();
                        dataConnection.setAutoCommit(true);
                        navLoad.setSaveFlag(true);

                        if (navLoad.getMode().equalsIgnoreCase("N")) {
                            setVoucher("Last");
                        } else if (navLoad.getMode().equalsIgnoreCase("E")) {
                            setVoucher("Edit");
                        }
                        navLoad.setMode("");
                    }
                } catch (Exception ex) {
                    try {
                        dataConnection.rollback();
                        dataConnection.setAutoCommit(true);
                        lb.printToLogFile("Error at save In Cash Payment/Receipt", ex);
                    } catch (Exception ex1) {
                        lb.printToLogFile("Error at rollback save In Cash Payment/Receipt", ex1);
                    }
                }
            }

            @Override
            public void callDelete() {
                try {
                    dataConnection.setAutoCommit(false);
                    lb.confirmDialog(Constants.DELETE_THIS + "voucher no. "+ id +" ?");
                    if (lb.type) {
                        CashBankUpdate cu = new CashBankUpdate();
                        cu.deleteEntry(id);

                        PreparedStatement psLocal = dataConnection.prepareStatement("DELETE FROM cash_payment_receipt_head WHERE id = ?");
                        psLocal.setString(1, id);
                        psLocal.executeUpdate();

                        psLocal = dataConnection.prepareStatement("DELETE FROM cash_payment_receipt_details WHERE id = ?");
                        psLocal.setString(1, id);
                        psLocal.executeUpdate();

                        dataConnection.commit();
                        dataConnection.setAutoCommit(true);
                        setVoucher("Last");
                        navLoad.setSaveFocus();
                    } else {
                        navLoad.setSaveFocus();
                    }
                } catch (Exception ex) {
                    try {
                        dataConnection.rollback();
                        dataConnection.setAutoCommit(true);
                        lb.printToLogFile("Error at delete In Cash Payment/Receipt", ex);
                    } catch (Exception ex1) {
                        lb.printToLogFile("Error at rollback delete In Cash Payment/Receipt", ex1);
                    }
                }
            }

            @Override
            public void callView() {
                onViewVoucher();
            }

            @Override
            public void callFirst() {
                setVoucher("First");
            }

            @Override
            public void callPrevious() {
                setVoucher("Previous");
            }

            @Override
            public void callNext() {
                setVoucher("Next");
            }

            @Override
            public void callLast() {
                setVoucher("Last");
            }

            @Override
            public void callClose() {
                cancelOrClose();
                setVoucher("Edit");
            }

            @Override
            public void callPrint() {
                onPrintVoucher(0);
            }

            @Override
            public void setComponentTextFromResultSet() {
                try {
                    id = navLoad.viewData.getString("id");
                    jtxtVoucher.setText(id.substring(initial.length()));
                    jtxtDate.setText(lb.userFormat.format(navLoad.viewData.getDate("voucher_date")));
                    jtxtRemark.setText(navLoad.viewData.getString("remarks"));
                    jlblEditNo.setText(navLoad.viewData.getString("edit_no"));
                    jlblUserName.setText(lb.getUserName(navLoad.viewData.getString("user_cd"), "N"));
                    jlblLstUpdate.setText(lb.timestamp.format(new Date(navLoad.viewData.getTimestamp("time_stamp").getTime())));
                    setDay();

                    navLoad.viewData = lb.fetchData("SELECT * FROM cash_payment_receipt_details WHERE id = '"+ id +"'", dataConnection);
                    model.setRowCount(0);
                    while (navLoad.viewData.next()) {
                        Vector row = new Vector();
                        row.add(lb.getAccountMstName(navLoad.viewData.getString("fk_account_master_id"), "N"));
                        row.add(lb.getIndianFormat(navLoad.viewData.getDouble("amount")));
                        row.add(navLoad.viewData.getString("remark"));
                        model.addRow(row);
                    }
                    setTotal();
                } catch (SQLException ex) {
                    lb.printToLogFile("Error at setComponentTextFromResultSet In Cash Payment/Receipt", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag) {
                jtxtVoucher.setEnabled(!flag);
                jtxtRemark.setEnabled(flag);
                jtxtDate.setEnabled(flag);
                jtxtAcntName.setEnabled(flag);
                jtxtAmt.setEnabled(flag);
                jtxtParticular.setEnabled(flag);
                jbtnAdd.setEnabled(flag);
            }
        }
        navLoad = new navPanel();
        jpanelNavigation.add(navLoad);
        navLoad.setVisible(true);
    }

    private void saveVoucher() throws SQLException, FileNotFoundException, Exception {
        PreparedStatement psLocal = null;
        String sql = null;
        if (navLoad.getMode().equalsIgnoreCase("N")) {
            id = lb.generateKey("cash_payment_receipt_head", "id", 8, initial); // GENERATE REF NO
            sql = "INSERT INTO cash_payment_receipt_head (voucher_date, total_bal, fk_account_master_id, remarks, ctype, is_del, user_cd, fix_time, id) VALUES (?, ?, ?, ?, ?, ?, ?, '"+ new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) +"', ?)";
        } else if (navLoad.getMode().equalsIgnoreCase("E")) {
            CashBankUpdate cu = new CashBankUpdate();
            cu.deleteEntry(id);

            sql = "UPDATE cash_payment_receipt_head SET voucher_date = ?, total_bal = ?, fk_account_master_id = ?, remarks = ?, ctype = ?, is_del = ?, user_cd = ?, edit_no = edit_no + 1, time_stamp = CURRENT_TIMESTAMP WHERE id = ?";
        }
        psLocal = dataConnection.prepareStatement(sql);
        psLocal.setString(1, lb.ConvertDateFormetForDB(jtxtDate.getText())); // V DATE
        psLocal.setDouble(2, lb.replaceAll(jlblAmtTotal.getText())); // TOTAL AMT
        psLocal.setString(3, ""); // AC_CD
        psLocal.setString(4, jtxtRemark.getText()); // REMARKS
        psLocal.setInt(5, mode); // CTYPE
        psLocal.setInt(6, 0); // IS_DEL
        psLocal.setInt(7, DeskFrame.user_id); // USER CD
        psLocal.setString(8, id); // REF NO
        psLocal.executeUpdate();

        if (navLoad.getMode().equalsIgnoreCase("E")) {
            sql = "DELETE FROM cash_payment_receipt_details WHERE id = ?";
            psLocal = dataConnection.prepareStatement(sql);
            psLocal.setString(1, id); // REF NO
            psLocal.executeUpdate();
        }

        sql = "INSERT INTO cash_payment_receipt_details (id, sr_no, fk_account_master_id, amount, remark) VALUES (?, ?, ?, ?, ?)";
        psLocal = dataConnection.prepareStatement(sql);
        for (int i = 0; i < jtableDet.getRowCount(); i++) {
            String table = "", column = "";
            table = "account_master";
            column = "name";
            if (lb.isExist(table, column, jtableDet.getValueAt(i, 0).toString(), dataConnection)) {
                if (lb.replaceAll(jtableDet.getValueAt(i, 1).toString()) > 0) { // Check Amount
                    psLocal.setString(1, id); // Ref No
                    psLocal.setString(2, (i + 1) + ""); // SR No
                    psLocal.setString(3, lb.getAccountMstName(jtableDet.getValueAt(i, 0).toString(), "C")); // Account Name
                    psLocal.setDouble(4, lb.replaceAll(jtableDet.getValueAt(i, 1).toString())); // Bal
                    psLocal.setString(5, jtableDet.getValueAt(i, 2).toString()); // Remark
                    psLocal.executeUpdate();
                }
            }
        }
        CashBankUpdate cu = new CashBankUpdate();
        cu.addEntry(id);

        lb.closeStatement(psLocal);
    }

    private void setTextFieldsAtBottom() {
        JComponent[] header = new JComponent[]{jtxtAcntName, jtxtAmt, jtxtParticular};
        JComponent[] footer = new JComponent[]{null, jlblAmtTotal, null};
        lb.setTable(jPanel1, jtableDet, header, footer);
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
        jScrollPane3 = new javax.swing.JScrollPane();
        jtableDet = new javax.swing.JTable();
        jpanelNavigation = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jtxtRemark = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtxtVoucher = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtxtDate = new javax.swing.JTextField();
        jBillDateBtn = new javax.swing.JButton();
        jlblCurDay = new javax.swing.JLabel();
        jbtnAdd = new javax.swing.JButton();
        jlblStart = new javax.swing.JLabel();
        jbtnEmail = new javax.swing.JButton();
        jtxtAcntName = new javax.swing.JTextField();
        jtxtAmt = new javax.swing.JTextField();
        jtxtParticular = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jlblAmtTotal = new javax.swing.JTextField();
        jbtnClear = new javax.swing.JButton();
        jbtnDelete = new javax.swing.JButton();
        jbtnChiddi = new javax.swing.JButton();

        setBackground(new java.awt.Color(211, 226, 245));
        setPreferredSize(new java.awt.Dimension(795, 513));

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 1, 1, new java.awt.Color(53, 154, 141)));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setBackground(new java.awt.Color(253, 243, 243));
        jScrollPane3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(4, 110, 152), 1, true));

        jtableDet.setBackground(new java.awt.Color(253, 243, 243));
        jtableDet.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtableDet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Account Name", "Amount", "Particular"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtableDet.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jtableDet.setRowHeight(23);
        jtableDet.getTableHeader().setReorderingAllowed(false);
        jtableDet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtableDetMouseClicked(evt);
            }
        });
        jtableDet.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jtableDetComponentResized(evt);
            }
        });
        jtableDet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtableDetKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(jtableDet);
        jtableDet.getColumnModel().getColumn(0).setMinWidth(110);
        jtableDet.getColumnModel().getColumn(0).setPreferredWidth(110);
        jtableDet.getColumnModel().getColumn(1).setMinWidth(50);
        jtableDet.getColumnModel().getColumn(1).setPreferredWidth(50);
        jtableDet.getColumnModel().getColumn(2).setMinWidth(60);
        jtableDet.getColumnModel().getColumn(2).setPreferredWidth(60);

        jPanel1.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jpanelNavigation.setBackground(new java.awt.Color(253, 243, 243));
        jpanelNavigation.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 1, 1, new java.awt.Color(235, 35, 35)));
        jpanelNavigation.setLayout(new java.awt.BorderLayout());

        jLabel9.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel9.setText("User Name : ");

        jlblUserName.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jLabel10.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel10.setText("Edit No");

        jlblEditNo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jLabel12.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel12.setText("Last Change");

        jlblLstUpdate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel6.setText("Naration : ");

        jtxtRemark.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtRemark.setForeground(new java.awt.Color(255, 0, 0));
        jtxtRemark.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtRemark.setMinimumSize(new java.awt.Dimension(6, 25));
        jtxtRemark.setPreferredSize(new java.awt.Dimension(6, 25));
        jtxtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRemarkFocusGained(evt);
            }
        });
        jtxtRemark.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRemarkKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("CTRL + T For Select Row in Table");

        jPanel2.setBackground(new java.awt.Color(253, 243, 243));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)), "Voucher Information", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 16), new java.awt.Color(0, 0, 255))); // NOI18N

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel1.setText("Voucher No :");

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

        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 0, 0));
        jLabel4.setText("Date");

        jtxtDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtDate.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtDateFocusGained(evt);
            }
        });
        jtxtDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtDateKeyPressed(evt);
            }
        });

        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });
        jBillDateBtn.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jBillDateBtnFocusLost(evt);
            }
        });
        jBillDateBtn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jBillDateBtnKeyPressed(evt);
            }
        });

        jlblCurDay.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlblCurDay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblCurDay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jbtnAdd.setBackground(new java.awt.Color(204, 255, 204));
        jbtnAdd.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnAdd.setForeground(new java.awt.Color(235, 35, 35));
        jbtnAdd.setText("ADD");
        jbtnAdd.setMaximumSize(new java.awt.Dimension(51, 25));
        jbtnAdd.setMinimumSize(new java.awt.Dimension(51, 25));
        jbtnAdd.setPreferredSize(new java.awt.Dimension(51, 25));
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

        jlblStart.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlblStart.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblStart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblStart, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblCurDay, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jbtnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblCurDay, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .addComponent(jtxtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .addComponent(jBillDateBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .addComponent(jtxtVoucher, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .addComponent(jlblStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(7, 7, 7))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jLabel1, jLabel4, jbtnAdd, jlblCurDay, jlblStart, jtxtDate, jtxtVoucher});

        jbtnEmail.setBackground(new java.awt.Color(204, 255, 204));
        jbtnEmail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnEmail.setForeground(new java.awt.Color(235, 35, 35));
        jbtnEmail.setText("EMAIL");
        jbtnEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEmailActionPerformed(evt);
            }
        });

        jtxtAcntName.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtAcntName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAcntName.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jtxtAcntNameComponentResized(evt);
            }
        });
        jtxtAcntName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAcntNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAcntNameFocusLost(evt);
            }
        });
        jtxtAcntName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAcntNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtAcntNameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAcntNameKeyTyped(evt);
            }
        });

        jtxtAmt.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtAmt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAmt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAmtFocusGained(evt);
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

        jtxtParticular.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtParticular.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtParticular.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtParticularFocusGained(evt);
            }
        });
        jtxtParticular.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtParticularKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtParticularKeyTyped(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel15.setText("Total Amt");

        jlblAmtTotal.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblAmtTotal.setText("0.00");
        jlblAmtTotal.setEnabled(false);
        jlblAmtTotal.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jlblAmtTotalComponentMoved(evt);
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

        jbtnDelete.setBackground(new java.awt.Color(204, 255, 204));
        jbtnDelete.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnDelete.setForeground(new java.awt.Color(235, 35, 35));
        jbtnDelete.setText("DELETE");
        jbtnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteActionPerformed(evt);
            }
        });

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
                    .addComponent(jpanelNavigation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jtxtAcntName, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAmt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtParticular, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblAmtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblLstUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jtxtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                                .addComponent(jbtnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtParticular, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAcntName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblAmtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtnClear)
                    .addComponent(jbtnChiddi)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpanelNavigation, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel10, jLabel12, jLabel9, jlblEditNo, jlblLstUpdate, jlblUserName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel6, jtxtRemark});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel15, jlblAmtTotal});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jtxtAcntName, jtxtAmt, jtxtParticular});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel2, jbtnClear, jbtnDelete});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtDateFocusGained

    private void jtxtDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtDateKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jtxtAcntName.requestFocusInWindow();
        }
        setDay();
    }//GEN-LAST:event_jtxtDateKeyPressed

    private void jtxtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRemarkFocusGained
        jtxtRemark.selectAll();
    }//GEN-LAST:event_jtxtRemarkFocusGained

    private void jtableDetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtableDetMouseClicked
        if (evt.getClickCount() == 2) {
            evt.consume();
            if (!navLoad.getMode().equalsIgnoreCase("")) {
                int rowSel = jtableDet.getSelectedRow();
                if (rowSel != -1) {
                    jtxtAcntName.setText(jtableDet.getValueAt(rowSel, 0).toString());
                    jtxtAmt.setText(jtableDet.getValueAt(rowSel, 1).toString());
                    jtxtParticular.setText(jtableDet.getValueAt(rowSel, 2).toString());
                    jtxtAcntName.requestFocusInWindow();
                }
            }
        }
    }//GEN-LAST:event_jtableDetMouseClicked

    private void jtableDetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtableDetKeyPressed
        int row1 = jtableDet.getSelectedRow();
        int cnt = jtableDet.getRowCount();
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            evt.consume();
            if (cnt > 1) {
                if (row1 != -1) {
                    if (JOptionPane.showConfirmDialog(this, Constants.DELETE_ROW + jtableDet.getValueAt(row1, 0).toString(), DeskFrame.TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        model.removeRow(row1);
                        setTotal();
                    }
                    jtableDet.clearSelection();
                }
            }
        }
    }//GEN-LAST:event_jtableDetKeyPressed

    private void jtxtRemarkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRemarkKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtRemarkKeyPressed

    private void jtableDetComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtableDetComponentResized
        setTextFieldsAtBottom();
    }//GEN-LAST:event_jtableDetComponentResized

    private void jlblAmtTotalComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblAmtTotalComponentMoved
        setTextFieldsAtBottom();
    }//GEN-LAST:event_jlblAmtTotalComponentMoved

    private void jtxtVoucherKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVoucherKeyPressed
        if(navLoad.getMode().equalsIgnoreCase("")) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                if(lb.isExist("cash_payment_receipt_head", "id", initial + jtxtVoucher.getText(), dataConnection)) {
                    id = initial + jtxtVoucher.getText();
                    navLoad.setMessage("");
                    setVoucher("Edit");
                } else {
                    navLoad.setMessage(Constants.INVALID_VOUCHER_NO);
                }
            }
            jtxtVoucher.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtVoucherKeyPressed

    private void jBillDateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtnActionPerformed
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtDate.getX() - 200, jtxtDate.getY() + 125, jtxtDate.getX() + odc.getWidth(), jtxtDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, Constants.SELECT_DATE);
    }//GEN-LAST:event_jBillDateBtnActionPerformed

    private void jBillDateBtnFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jBillDateBtnFocusLost
        setDay();
    }//GEN-LAST:event_jBillDateBtnFocusLost

    private void jBillDateBtnKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBillDateBtnKeyPressed
        setDay();
    }//GEN-LAST:event_jBillDateBtnKeyPressed

    private void jtxtVoucherKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVoucherKeyTyped
        lb.onlyNumber(evt, (7 - initial.length()));
    }//GEN-LAST:event_jtxtVoucherKeyTyped

    private void jtxtVoucherFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVoucherFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtVoucherFocusGained

    private void jbtnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEmailActionPerformed
        EmailSelect ds = new EmailSelect(MainClass.df, true, id, initial, mode);
    }//GEN-LAST:event_jbtnEmailActionPerformed

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        navLoad.setMessage("");
        boolean flag = reValidate();
        int rowSel = jtableDet.getSelectedRow();
        if (rowSel != -1 && flag) {
            jtableDet.setValueAt(jtxtAcntName.getText(), rowSel, 0);
            jtableDet.setValueAt(lb.getIndianFormat(lb.replaceAll(jtxtAmt.getText())), rowSel, 1);
            jtableDet.setValueAt(jtxtParticular.getText(), rowSel, 2);
            jtableDet.clearSelection();
            setTotal();
        } else {
            if (flag) {
                Vector row = new Vector();
                row.add(jtxtAcntName.getText());
                row.add(lb.getIndianFormat(lb.replaceAll(jtxtAmt.getText())));
                row.add(jtxtParticular.getText());
                model.addRow(row);
            }
        }
        if (flag) {
            setTotal();
            clear();
        } else {
            jtxtAcntName.requestFocusInWindow();
        }
        if (flag) {
            lb.confirmDialog("Do you want to add another payment");
            if (lb.type) {
                jtxtAcntName.requestFocusInWindow();
            } else {
                jtxtRemark.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            jbtnAdd.doClick();
        }
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jtxtAcntNameComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtxtAcntNameComponentResized
        setTextFieldsAtBottom();
    }//GEN-LAST:event_jtxtAcntNameComponentResized

    private void jtxtAcntNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcntNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAcntNameFocusGained

    private void jtxtAcntNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcntNameFocusLost
        acPickListView.setVisible(false);
    }//GEN-LAST:event_jtxtAcntNameFocusLost

    private void jtxtAcntNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcntNameKeyPressed
        acPickListView.pickListKeyPress(evt);
        lb.downFocus(evt, navLoad);
        if(evt.getKeyCode() == KeyEvent.VK_F2) {
            try {
                PopUpAccountMaster glp = new PopUpAccountMaster(null, true);
                if(mode == 1) {
                    glp.getData(Constants.SUNDRY_CREDITORS, jtxtAcntName.getText(), 0);
                } else {
                    glp.getData(Constants.SUNDRY_DEBTORS, jtxtAcntName.getText(), 0);
                }
                glp.jtxtAccountName.requestFocusInWindow();
                glp.setLocation(jtxtAcntName.getX(), jtxtAcntName.getY() + jtxtAcntName.getHeight());

                glp.show();
            } catch(Exception ex) {
                lb.printToLogFile("Exception at openPopUp In Cash Payment/Receipt", ex);
            }
        }
    }//GEN-LAST:event_jtxtAcntNameKeyPressed

    private void jtxtAcntNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcntNameKeyReleased
        try {
            acPickListView.setLocation(jtxtAcntName.getX(), jtxtAcntName.getY() + jtxtAcntName.getHeight());
            acPickListView.setReturnComponent(new JTextField[]{jtxtAcntName});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM account_master WHERE name LIKE '%"+ jtxtAcntName.getText().toUpperCase() +"%'");
            acPickListView.setValidation(dataConnection.prepareStatement("SELECT name FROM account_master WHERE name = ?"));
            acPickListView.setPreparedStatement(pstLocal);
            acPickListView.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtACAliasKeyReleased In Cash Payment/Receipt", ex);
        }
    }//GEN-LAST:event_jtxtAcntNameKeyReleased

    private void jtxtAmtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAmtFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAmtFocusGained

    private void jtxtAmtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmtKeyPressed
        lb.enterFocus(evt, jtxtParticular);
    }//GEN-LAST:event_jtxtAmtKeyPressed

    private void jtxtAmtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAmtKeyTyped
        lb.onlyNumber(evt, 10);
    }//GEN-LAST:event_jtxtAmtKeyTyped

    private void jtxtParticularFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtParticularFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtParticularFocusGained

    private void jtxtParticularKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtParticularKeyPressed
        lb.enterFocus(evt, jbtnAdd);
    }//GEN-LAST:event_jtxtParticularKeyPressed

    private void jtxtParticularKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtParticularKeyTyped
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtParticularKeyTyped

    private void jtxtAcntNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcntNameKeyTyped
        lb.fixLength(evt, 100);
    }//GEN-LAST:event_jtxtAcntNameKeyTyped

    private void jbtnClearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnClearMouseClicked
        jtableDet.clearSelection();
    }//GEN-LAST:event_jbtnClearMouseClicked

    private void jbtnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnClearActionPerformed
        jtableDet.clearSelection();
    }//GEN-LAST:event_jbtnClearActionPerformed

    private void jbtnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteActionPerformed
        int row1 = jtableDet.getSelectedRow();
        int cnt = jtableDet.getRowCount();
        if (cnt > 1) {
            if (row1 != -1) {
                if (JOptionPane.showConfirmDialog(this, Constants.DELETE_ROW + jtableDet.getValueAt(row1, 0).toString(), DeskFrame.TITLE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    model.removeRow(row1);
                    setTotal();
                }
                jtableDet.clearSelection();
            }
        }
    }//GEN-LAST:event_jbtnDeleteActionPerformed

    private void jbtnChiddiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnChiddiMouseClicked
        onPrintVoucher(1);
    }//GEN-LAST:event_jbtnChiddiMouseClicked

    private void jbtnChiddiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnChiddiActionPerformed
        onPrintVoucher(1);
    }//GEN-LAST:event_jbtnChiddiActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JButton jbtnChiddi;
    private javax.swing.JButton jbtnClear;
    private javax.swing.JButton jbtnDelete;
    private javax.swing.JButton jbtnEmail;
    private javax.swing.JTextField jlblAmtTotal;
    private javax.swing.JLabel jlblCurDay;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblStart;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JPanel jpanelNavigation;
    private javax.swing.JTable jtableDet;
    private javax.swing.JTextField jtxtAcntName;
    private javax.swing.JTextField jtxtAmt;
    private javax.swing.JTextField jtxtDate;
    private javax.swing.JTextField jtxtParticular;
    private javax.swing.JTextField jtxtRemark;
    private javax.swing.JTextField jtxtVoucher;
    // End of variables declaration//GEN-END:variables
}