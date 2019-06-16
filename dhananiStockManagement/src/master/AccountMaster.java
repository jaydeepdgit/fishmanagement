/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import dhananistockmanagement.DeskFrame;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.InternalFrameEvent;
import support.Constants;
import support.HeaderIntFrame1;
import support.Library;
import support.PickList;
import support.ReportTable;
import support.SmallNavigation;
import support.VoucherDisplay;

/**
 *
 * @author @JD@
 */
public class AccountMaster extends javax.swing.JInternalFrame {
    private SmallNavigation navLoad = null;
    private Library lb = new Library();
    private String id = "";
    private Connection dataConnection = DeskFrame.connMpAdmin;
    private PickList groupPickList = null;
    private ReportTable accountMaster;

    /**
     * Creates new form AccountMaster
     */
    public AccountMaster() {
        initComponents();
        addNavigation();
        addValidation();
        setPickListView();
        setCompEnable(false);
        setVoucher("Last");
        makeChildTable();
        setEnableLockDate(true);
        setLockShortcut();
        setTitle(Constants.ACCOUNT_MASTER_FORM_NAME);
    }

    private void setLockShortcut() {
        KeyStroke lockKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_K, KeyEvent.CTRL_MASK, false);
        Action lockKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getLockDate();
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(lockKeyStroke, "Lock");
        this.getActionMap().put("Lock", lockKeyAction);
    }

    private void getLockDate() {
        if(!navLoad.getMode().equalsIgnoreCase("")) {
            String lock = JOptionPane.showInputDialog(this, "Enter Lock Date (DD/MM/YYYY) format", jtxtLockDate.getText());
            String current = jtxtLockDate.getText();
            jtxtLockDate.setText(lock);
            if(!lb.checkDate2(jtxtLockDate)) {
                jtxtLockDate.setText(current);
            }
        }
    }

    private void makeChildTable() {
        accountMaster = new ReportTable();

        accountMaster.AddColumn(0, "id", 100, java.lang.String.class, null, false);
        accountMaster.AddColumn(1, "Name", 300, java.lang.String.class, null, false);
        accountMaster.AddColumn(2, "Status", 150, java.lang.String.class, null, false);
        accountMaster.AddColumn(3, "OPB", 150, java.lang.String.class, null, false);
        accountMaster.AddColumn(4, "Account Type", 150, java.lang.String.class, null, false);
        accountMaster.makeTable();
    }

    public void setID(String id) {
        this.id = id;
        setVoucher("edit");
    }

    private void setEnableLockDate(boolean flag) {
        jtxtLockDate.setVisible(flag);
        jLabel18.setVisible(flag);
    }

    private void setPickListView() {
        groupPickList = new PickList(dataConnection);

        groupPickList.setLayer(getLayeredPane());
        groupPickList.setPickListComponent(jtxtGroup);
        groupPickList.setNextComponent(jtxtExpense);

    }

    private void onViewVoucher() {
        this.dispose();

        String sql = "SELECT id, name, IF(status = 0, '"+ Constants.ACTIVE +"', '"+ Constants.DEACTIVE +"') AS status, opb, IF(amount_type = 0, '"+ Constants.USD +"', '"+ Constants.RS +"') AS status FROM account_master";
        accountMaster.setColumnValue(new int[]{1, 2, 3, 4, 5});
        String view_title = Constants.ACCOUNT_MASTER_FORM_NAME +" VIEW";

        HeaderIntFrame1 rptDetail = new HeaderIntFrame1(dataConnection, id+"", view_title, accountMaster, sql, Constants.ACCOUNT_MASTER_FORM_ID, 1, this, this.getTitle());
        rptDetail.makeView();
        rptDetail.setVisible(true);

        Component c = DeskFrame.tabbedPane.add(view_title, rptDetail);
        c.setName(view_title);
        DeskFrame.tabbedPane.setSelectedComponent(c);
    }

    public void setStartupFocus() {
        jtxtName.requestFocusInWindow();
    }

    private void addValidation() {
        FieldValidation valid = new FieldValidation();
        jtxtID.setInputVerifier(valid);
        jtxtName.setInputVerifier(valid);
        jtxtGroup.setInputVerifier(valid);
    }

    class FieldValidation extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            boolean val = false;
            if (input.equals(jtxtID)) {
                val = fieldValid(input);
            } else if (input.equals(jtxtName)) {
                val = fieldValid(input);
            } else if (input.equals(jtxtGroup)) {
                val = fieldValid(input);
            } else if (input.equals(jtxtOPB)) {
                val = fieldValid(input);
            } 
            return val;
        }
    }

    private boolean fieldValid(Component comp) {
        navLoad.setMessage("");
        if (comp == jtxtName) {
            if (navLoad.getMode().equalsIgnoreCase("N") || navLoad.getMode().equalsIgnoreCase("E")) {
                if (lb.isBlank(comp)) {
                    navLoad.setMessage("Name should not blank");
                    comp.requestFocusInWindow();
                    return false;
                }
            }
            if (navLoad.getMode().equalsIgnoreCase("N")) {
                if (lb.isExist("account_master", "name", jtxtName.getText(), dataConnection)) {
                    navLoad.setMessage("Name already exist!");
                    comp.requestFocusInWindow();
                    return false;
                }
            } else if (navLoad.getMode().equalsIgnoreCase("E")) {
                if (lb.isExistForEdit("account_master", "name", jtxtName.getText(), "id", id, dataConnection)) {
                    navLoad.setMessage("Name already exist!");
                    comp.requestFocusInWindow();
                    return false;
                }
            }
        }
        if (comp == jtxtGroup) {
            if (navLoad.getMode().equalsIgnoreCase("N") || navLoad.getMode().equalsIgnoreCase("E")) {
                if (lb.isBlank(comp)) {
                    navLoad.setMessage("Please select group");
                    comp.requestFocusInWindow();
                    return false;
                }
            }
        }
        if (comp == jtxtOPB) {
            if (navLoad.getMode().equalsIgnoreCase("N") || navLoad.getMode().equalsIgnoreCase("E")) {
                if (lb.isBlank(comp)) {
                    jtxtOPB.setText("0.00");
                }
            }
        }
        if (comp == jtxtExpense) {
            if (navLoad.getMode().equalsIgnoreCase("N") || navLoad.getMode().equalsIgnoreCase("E")) {
                if (lb.isBlank(comp)) {
                    jtxtExpense.setText("0.00");
                }
            }
        }
        return true;
    }

    private void setCompEnable(boolean flag) {
        jtxtID.setEnabled(!flag);
        jtxtName.setEnabled(flag);
        jtxtGroup.setEnabled(flag);
        jtxtExpense.setEnabled(flag);
        jcmbStatus.setEnabled(flag);
        jtxtOPB.setEnabled(flag);
        jcmbAmountType.setEnabled(flag);
        jtxtLockDate.setEnabled(flag);
        jtxtName.requestFocusInWindow();
    }

    private void setCompText(String text) {
        jtxtID.setText(text);
        jtxtName.setText(text);
        jtxtGroup.setText(text);
        jtxtExpense.setText(text);
        jcmbStatus.setSelectedIndex(0);
        jtxtOPB.setText(text);
        jtxtLockDate.setText(text);
        jcmbAmountType.setSelectedIndex(0);
    }

    private boolean validateForm() {
        boolean flag = true;
        flag = flag && fieldValid(jtxtName);
        flag = flag && fieldValid(jtxtGroup);
        flag = flag && fieldValid(jtxtExpense);
        flag = flag && fieldValid(jtxtOPB);
        return flag;
    }

    private void addNavigation() {
        class Navigation extends SmallNavigation {
            @Override
            public void callNew() {
                setMode("N");
                setSaveFlag(false);
                setCompText("");
                setCompEnable(true);
                lb.setDateChooserProperty(jtxtLockDate);
            }

            @Override
            public void callEdit() {
                setMode("E");
                setSaveFlag(false);
                setCompEnable(true);
            }

            @Override
            public void callSave() {
                setSaveFlag(false);
                boolean valid = validateForm();
                if(valid) {
                    saveVoucher();
                    navLoad.setSaveFlag(true);
                    if(navLoad.getMode().equalsIgnoreCase("N")) {
                        setVoucher("Last");
                    } else if(navLoad.getMode().equalsIgnoreCase("E")) {
                        setVoucher("Edit");
                    }
                    navLoad.setMode("");
                    navLoad.setFirstFocus();
                }
            }

            @Override
            public void callDelete() {
                try {
                    if(!lb.isExist("purchase_bill_head", "fk_account_master_id", id+"") && !lb.isExist("sale_bill_head", "fk_account_master_id", id+"")) {
                        lb.confirmDialog(Constants.DELETE_THIS + " "+ jtxtName.getText() +" ?");
                        if(lb.type) {
                            dataConnection.setAutoCommit(false);
                            delete();
                            dataConnection.commit();
                            dataConnection.setAutoCommit(true);
                            setVoucher("Last");
                        }
                    } else {
                        navLoad.setMessage("Name is in use");
                    }
                    navLoad.setFirstFocus();
                } catch (SQLException ex) {
                    try {
                        dataConnection.rollback();
                        dataConnection.setAutoCommit(true);
                        lb.printToLogFile("Error at delete In Account Master", ex);
                    } catch (SQLException ex1) {
                        lb.printToLogFile("Error at rollback delete In Account Master", ex1);
                    }
                }
                setSaveFlag(true);
                navLoad.setFirstFocus();
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
            }

            @Override
            public void callPrint() {
                try {
                    VoucherDisplay vd = new VoucherDisplay(id, Constants.ACCOUNT_MASTER_INITIAL);
                    DeskFrame.addOnScreen(vd, Constants.ACCOUNT_MASTER_FORM_NAME +" PRINT");
                } catch(Exception ex) {
                    lb.printToLogFile("Exception at callPrint In Account Master", ex);
                }
            }

            @Override
            public void setComponentTextFromResultSet() {
                try {
                    id = navLoad.viewData.getString("id");
                    jtxtID.setText(id+"");
                    jtxtName.setText(navLoad.viewData.getString("name"));
                    jtxtGroup.setText(lb.getGroupName(navLoad.viewData.getString("fk_group_id"), "N"));
                    jtxtExpense.setText(navLoad.viewData.getString("expense"));
                    jcmbStatus.setSelectedIndex(navLoad.viewData.getInt("status"));
                    jtxtOPB.setText(navLoad.viewData.getString("opb"));
                    jcmbAmountType.setSelectedIndex(navLoad.viewData.getInt("amount_type"));
                    jlblUserName.setText(lb.getUserName(navLoad.viewData.getString("user_cd"), "N"));
                    if (viewData.getString("lock_date") != null) {
                        jtxtLockDate.setText(lb.ConvertDateFormetForDBForConcurrency(navLoad.viewData.getString("lock_date")));
                    } else {
                        lb.setDateChooserProperty(jtxtLockDate);
                    }
                    jlblEditNo.setText(navLoad.viewData.getString("edit_no"));
                    jlblLstUpdate.setText(lb.getTimeStamp(viewData.getTimestamp("time_stamp")));
                } catch (Exception ex) {
                    lb.printToLogFile("Error at setComponentTextFromResultSet In Account Master", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag) {
                jtxtID.setEnabled(!flag);
                jtxtName.setEnabled(flag);
                jtxtName.requestFocusInWindow();
            }
        }
        navLoad = new Navigation();
        navLoad.setVisible(true);
        jPanel1.add(navLoad);
        jPanel1.setVisible(true);
    }
    
    private void oldbUpdateADD() throws SQLException {
        if (lb.getData("AC_CD", "oldb2_1", "AC_CD", ""+ id +"").equalsIgnoreCase("")) {
            String sql = "insert into oldb2_1(ac_cd, grp_cd, opb, dr, cr, bal, amount_type) values(?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.setString(1, id);
            pstUpdate.setString(2, lb.getGroupName(jtxtGroup.getText(), "C")); // grp_cd
            pstUpdate.setDouble(3, Double.parseDouble(jtxtOPB.getText()));
            pstUpdate.setString(4, "0");
            pstUpdate.setString(5, "0");
            pstUpdate.setDouble(6, 0);
            pstUpdate.setInt(7, jcmbAmountType.getSelectedIndex());
            
            pstUpdate.executeUpdate();
            lb.closeStatement(pstUpdate);
        } else {
            String sql = "UPDATE oldb2_1 SET opb = ?, amount_type = ?, grp_cd = ? WHERE AC_CD = ?";
            PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.setDouble(1, Double.parseDouble(jtxtOPB.getText()));
            pstUpdate.setInt(2, jcmbAmountType.getSelectedIndex());
            pstUpdate.setString(3, lb.getGroupName(jtxtGroup.getText(), "C")); // grp_cd
            pstUpdate.setString(4, id);
            pstUpdate.executeUpdate();
            lb.closeStatement(pstUpdate);
        }

        String sql = "DELETE FROM oldb2_2 WHERE DOC_REF_NO = 'OPB' AND AC_CD = ?";
        PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
        pstUpdate.setString(1, id);
        pstUpdate.executeUpdate();

        sql = "insert into oldb2_2 (DOC_REF_NO, DOC_CD, DOC_DATE, AC_CD, DRCR, VAL, PARTICULAR, opp_ac_cd)"
                + "values(?, ?, ?, ?, ?, ?, ?, ?)";
        pstUpdate = dataConnection.prepareStatement(sql);
        pstUpdate.setString(1, "OPB");
        pstUpdate.setString(2, "OPB");
        pstUpdate.setString(3, lb.ConvertDateFormetForDB(jtxtLockDate.getText())); // doc_date
        pstUpdate.setString(4, id);
        pstUpdate.setString(5, "0");
        pstUpdate.setDouble(6, Double.parseDouble(jtxtOPB.getText()));
        pstUpdate.setString(7, "");
        pstUpdate.setString(8, "");
        pstUpdate.executeUpdate();
    }
    
    private void oldbUpdateEdit() throws SQLException {
        String sql = "UPDATE oldb2_1 SET OPB = ?, amount_type = ?, grp_cd = ? WHERE AC_CD = ?";
        PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
        pstUpdate.setDouble(1, Double.parseDouble(jtxtOPB.getText()));
        pstUpdate.setInt(2, jcmbAmountType.getSelectedIndex());
        pstUpdate.setString(3, lb.getGroupName(jtxtGroup.getText(), "C")); // grp_cd
        pstUpdate.setString(4, id);
        pstUpdate.executeUpdate();
        lb.closeStatement(pstUpdate);
             
        sql = "UPDATE oldb2_2 SET val = ? WHERE DOC_REF_NO = 'OPB' AND AC_CD = ?";
        pstUpdate = dataConnection.prepareStatement(sql);
        pstUpdate.setDouble(1, Double.parseDouble(jtxtOPB.getText()));
        pstUpdate.setString(2, id);
        pstUpdate.executeUpdate();
    }

    private void setVoucher(String move) {
        try {
            String sql = "SELECT * FROM account_master";
            if (move.equalsIgnoreCase("first")) {
                sql += " ORDER BY id";
            } else if (move.equalsIgnoreCase("previous")) {
                sql += " WHERE id < '"+ id +"' ORDER BY id DESC";
            } else if (move.equalsIgnoreCase("next")) {
                sql += " WHERE id > '"+ id +"'";
            } else if (move.equalsIgnoreCase("last")) {
                sql += " ORDER BY id DESC";
            } else if (move.equalsIgnoreCase("edit")) {
                sql += " WHERE id = '"+ id +"'";
            }
            navLoad.viewData = navLoad.fetchData(sql);
            if (navLoad.viewData.next()) {
                navLoad.setComponentTextFromResultSet();
            } else {
                if(move.equalsIgnoreCase("last")) {
                    setCompText("");
                }
            }
            setCompEnable(false);
            navLoad.setFirstFocus();
            lb.setPermission(navLoad, Constants.ACCOUNT_MASTER_FORM_ID);
        } catch (Exception ex) {
            lb.printToLogFile("Error at setVoucher In Account Master", ex);
        }
    }

    private void delete() throws SQLException {
        PreparedStatement psLocal = null;

        psLocal = dataConnection.prepareStatement("DELETE FROM account_master WHERE id = ?");
        psLocal.setString(1, id);
        psLocal.executeUpdate();
    }

    private void saveVoucher() {
        try {
            PreparedStatement psLocal = null;
            dataConnection.setAutoCommit(false);
            if(navLoad.getMode().equalsIgnoreCase("N")) {
                psLocal = dataConnection.prepareStatement("INSERT INTO account_master(name, fk_group_id, expense, status, opb, amount_type, lock_date, user_cd, id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                id = lb.generateKey("account_master", "id", Constants.ACCOUNT_MASTER_INITIAL, 8);
                psLocal.setString(1, jtxtName.getText()); // name
                psLocal.setString(2, lb.getGroupName(jtxtGroup.getText(), "C")); // group
                psLocal.setString(3, jtxtExpense.getText()); // expense
                psLocal.setInt(4, jcmbStatus.getSelectedIndex()); // status
                psLocal.setString(5, jtxtOPB.getText()); // opb
                psLocal.setInt(6, jcmbAmountType.getSelectedIndex()); // amount type
                psLocal.setString(7, lb.ConvertDateFormetForDB(jtxtLockDate.getText())); // lock_date
                psLocal.setInt(8, DeskFrame.user_id); // user_cd
                psLocal.setString(9, id); // id
                psLocal.executeUpdate();
                
                oldbUpdateADD();
            } else if(navLoad.getMode().equalsIgnoreCase("E")) {
                psLocal = dataConnection.prepareStatement("UPDATE account_master SET name = ?, fk_group_id = ?, expense = ?, status = ?, opb = ?, amount_type = ?, lock_date = ?, user_cd = ?, edit_no = edit_no + 1, time_stamp = CURRENT_TIMESTAMP WHERE id = ?");
                psLocal.setString(1, jtxtName.getText()); // name
                psLocal.setString(2, lb.getGroupName(jtxtGroup.getText(), "C")); // group
                psLocal.setString(3, jtxtExpense.getText()); // expense
                psLocal.setInt(4, jcmbStatus.getSelectedIndex()); // status
                psLocal.setString(5, jtxtOPB.getText()); // opb
                psLocal.setInt(6, jcmbAmountType.getSelectedIndex()); // amount type
                psLocal.setString(7, lb.ConvertDateFormetForDB(jtxtLockDate.getText())); // lock_date
                psLocal.setInt(8, DeskFrame.user_id); // user_cd
                psLocal.setString(9, id); // id
                psLocal.executeUpdate();
                
                oldbUpdateEdit();
            }
            lb.setBalance(id);
            dataConnection.commit();
            dataConnection.setAutoCommit(true);
        } catch (SQLException ex) {
            try {
                dataConnection.rollback();
                dataConnection.setAutoCommit(true);
                lb.printToLogFile("Error at save In Account Master", ex);
            } catch (SQLException ex1) {
                lb.printToLogFile("Error at rollback save In Account Master", ex1);
            }
        }
    }

    private void cancelOrClose() {
        if(navLoad.getSaveFlag()) {
            this.dispose();
        } else {
            navLoad.setMode("");
            setCompEnable(false);
            setVoucher("Edit");
            navLoad.setMessage("");
            navLoad.setSaveFlag(true);
        }
    }

    @Override
    public void dispose() {
        if (isVisible()) {
            setVisible(false);
        }
        if (isSelected()) {
            try {
                setSelected(false);
            } catch (PropertyVetoException pve) {
                lb.printToLogFile("Exception at dispose in Account Master", pve);
            }
        }
        if (!isClosed) {
            firePropertyChange(IS_CLOSED_PROPERTY, Boolean.FALSE, Boolean.TRUE);
            isClosed = true;
        }
        fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSED);
        DeskFrame.removeFromScreen(DeskFrame.tabbedPane.getSelectedIndex());
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtxtID = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jcmbStatus = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jtxtExpense = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jtxtOPB = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jcmbAmountType = new javax.swing.JComboBox();
        jtxtGroup = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jtxtLockDate = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(686, 535));

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 1, 1, new java.awt.Color(235, 35, 35)));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(253, 243, 243));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)), "Account Master", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 16), new java.awt.Color(0, 0, 255))); // NOI18N

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel1.setText("ID");
        jLabel1.setMaximumSize(new java.awt.Dimension(61, 25));
        jLabel1.setMinimumSize(new java.awt.Dimension(61, 25));
        jLabel1.setPreferredSize(new java.awt.Dimension(61, 25));

        jtxtID.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtID.setMinimumSize(new java.awt.Dimension(6, 25));
        jtxtID.setPreferredSize(new java.awt.Dimension(6, 25));
        jtxtID.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtIDFocusGained(evt);
            }
        });
        jtxtID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtIDKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtIDKeyTyped(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("Name");
        jLabel2.setMaximumSize(new java.awt.Dimension(77, 25));
        jLabel2.setMinimumSize(new java.awt.Dimension(77, 25));
        jLabel2.setPreferredSize(new java.awt.Dimension(77, 25));

        jtxtName.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtName.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtNameFocusLost(evt);
            }
        });
        jtxtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtNameKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtNameKeyTyped(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel6.setText("User Name:");

        jLabel7.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel7.setText("Edit No:");

        jLabel8.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel8.setText("Last Updated:");

        jlblLstUpdate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jlblEditNo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jlblUserName.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel4.setText("Status");
        jLabel4.setMaximumSize(new java.awt.Dimension(61, 25));
        jLabel4.setMinimumSize(new java.awt.Dimension(61, 25));
        jLabel4.setPreferredSize(new java.awt.Dimension(61, 25));

        jcmbStatus.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jcmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Active", "Deactive" }));
        jcmbStatus.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jcmbStatus.setMinimumSize(new java.awt.Dimension(39, 25));
        jcmbStatus.setPreferredSize(new java.awt.Dimension(39, 25));
        jcmbStatus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbStatusKeyPressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel5.setText("Expense");
        jLabel5.setMaximumSize(new java.awt.Dimension(61, 25));
        jLabel5.setMinimumSize(new java.awt.Dimension(61, 25));
        jLabel5.setPreferredSize(new java.awt.Dimension(61, 25));

        jtxtExpense.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtExpense.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtExpense.setMinimumSize(new java.awt.Dimension(6, 25));
        jtxtExpense.setPreferredSize(new java.awt.Dimension(6, 25));
        jtxtExpense.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtExpenseFocusGained(evt);
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

        jLabel9.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel9.setText("OPB");
        jLabel9.setMaximumSize(new java.awt.Dimension(61, 25));
        jLabel9.setMinimumSize(new java.awt.Dimension(61, 25));
        jLabel9.setPreferredSize(new java.awt.Dimension(61, 25));

        jtxtOPB.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtOPB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtOPB.setMinimumSize(new java.awt.Dimension(6, 25));
        jtxtOPB.setPreferredSize(new java.awt.Dimension(6, 25));
        jtxtOPB.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtOPBFocusGained(evt);
            }
        });
        jtxtOPB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtOPBKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtOPBKeyTyped(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel10.setText("Amount Type");
        jLabel10.setMaximumSize(new java.awt.Dimension(61, 25));
        jLabel10.setMinimumSize(new java.awt.Dimension(61, 25));
        jLabel10.setPreferredSize(new java.awt.Dimension(61, 25));

        jcmbAmountType.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jcmbAmountType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "USD", "RS" }));
        jcmbAmountType.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jcmbAmountType.setMinimumSize(new java.awt.Dimension(39, 25));
        jcmbAmountType.setPreferredSize(new java.awt.Dimension(39, 25));
        jcmbAmountType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbAmountTypeKeyPressed(evt);
            }
        });

        jtxtGroup.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtGroup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtGroup.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtGroup.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtGroup.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtGroupFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtGroupFocusLost(evt);
            }
        });
        jtxtGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtGroupKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtGroupKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtGroupKeyTyped(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 0, 0));
        jLabel3.setText("Group");
        jLabel3.setMaximumSize(new java.awt.Dimension(77, 25));
        jLabel3.setMinimumSize(new java.awt.Dimension(77, 25));
        jLabel3.setPreferredSize(new java.awt.Dimension(77, 25));

        jLabel18.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 0, 0));
        jLabel18.setText("Lock Date");

        jtxtLockDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtLockDate.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtLockDate.setMinimumSize(new java.awt.Dimension(2, 20));
        jtxtLockDate.setPreferredSize(new java.awt.Dimension(2, 20));
        jtxtLockDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtLockDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtLockDateFocusLost(evt);
            }
        });
        jtxtLockDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtLockDateKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtOPB, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtExpense, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jtxtLockDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jcmbAmountType, javax.swing.GroupLayout.Alignment.LEADING, 0, 103, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtID, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jtxtName, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jtxtGroup, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)))
                        .addGap(0, 121, Short.MAX_VALUE)))
                .addContainerGap(76, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel6, jLabel7, jLabel8});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtOPB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcmbAmountType, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtLockDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2, jtxtID, jtxtName});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel6, jLabel7, jLabel8, jlblEditNo, jlblLstUpdate, jlblUserName});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtNameKeyPressed
        lb.enterEvent(evt, jtxtGroup);
    }//GEN-LAST:event_jtxtNameKeyPressed

    private void jtxtNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtNameKeyTyped
        lb.fixLength(evt, 100);
    }//GEN-LAST:event_jtxtNameKeyTyped

    private void jtxtNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtNameFocusGained

    private void jtxtNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtNameFocusLost
        lb.toUpper(evt);
    }//GEN-LAST:event_jtxtNameFocusLost

    private void jtxtIDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtIDFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtIDFocusGained

    private void jtxtIDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtIDKeyPressed
        if(navLoad.getMode().equalsIgnoreCase("")) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                if(lb.isExist("account_master", "id", jtxtID.getText(), dataConnection)) {
                    id = jtxtID.getText();
                    navLoad.setMessage("");
                    setVoucher("edit");
                } else {
                    navLoad.setMessage("ID is invalid");
                }
            }
            jtxtID.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtIDKeyPressed

    private void jtxtIDKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtIDKeyTyped
        lb.fixLength(evt, 7);
    }//GEN-LAST:event_jtxtIDKeyTyped

    private void jcmbStatusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbStatusKeyPressed
        lb.enterEvent(evt, jtxtOPB);
    }//GEN-LAST:event_jcmbStatusKeyPressed

    private void jtxtExpenseFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtExpenseFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtExpenseFocusGained

    private void jtxtExpenseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtExpenseKeyPressed
        lb.enterEvent(evt, jcmbStatus);
    }//GEN-LAST:event_jtxtExpenseKeyPressed

    private void jtxtExpenseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtExpenseKeyTyped
        lb.onlyNumber(evt, 10);
    }//GEN-LAST:event_jtxtExpenseKeyTyped

    private void jtxtOPBFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtOPBFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtOPBFocusGained

    private void jtxtOPBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtOPBKeyPressed
        lb.enterEvent(evt, jcmbAmountType);
    }//GEN-LAST:event_jtxtOPBKeyPressed

    private void jtxtOPBKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtOPBKeyTyped
        lb.onlyNumber(evt, 10);
    }//GEN-LAST:event_jtxtOPBKeyTyped

    private void jcmbAmountTypeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbAmountTypeKeyPressed
        lb.enterEvent(evt, jtxtLockDate);
    }//GEN-LAST:event_jcmbAmountTypeKeyPressed

    private void jtxtGroupFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGroupFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtGroupFocusGained

    private void jtxtGroupFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGroupFocusLost
        groupPickList.setVisible(false);
    }//GEN-LAST:event_jtxtGroupFocusLost

    private void jtxtGroupKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupKeyPressed
        groupPickList.setLocation(jtxtGroup.getX(), jtxtGroup.getY() + jtxtGroup.getHeight());
        groupPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtGroupKeyPressed

    private void jtxtGroupKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupKeyReleased
        try {
            groupPickList.setReturnComponent(new JTextField[]{jtxtGroup});
            String sql = "SELECT name FROM group_master WHERE name LIKE '%"+ jtxtGroup.getText() +"%'";
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            groupPickList.setValidation(dataConnection.prepareStatement("SELECT name FROM group_master WHERE name = ?"));
            groupPickList.setFirstAssociation(new int[]{0});
            groupPickList.setSecondAssociation(new int[]{0});
            groupPickList.setPreparedStatement(pstLocal);
            groupPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtGroupKeyReleased In Account Master", ex);
        }
    }//GEN-LAST:event_jtxtGroupKeyReleased

    private void jtxtGroupKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupKeyTyped
        lb.onlyAlpha(evt, 255);
    }//GEN-LAST:event_jtxtGroupKeyTyped

    private void jtxtLockDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtLockDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtLockDateFocusGained

    private void jtxtLockDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtLockDateFocusLost
        lb.setDateUsingJTextField(jtxtLockDate);
    }//GEN-LAST:event_jtxtLockDateFocusLost

    private void jtxtLockDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtLockDateKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jtxtLockDateKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JComboBox jcmbAmountType;
    private javax.swing.JComboBox jcmbStatus;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JTextField jtxtExpense;
    private javax.swing.JTextField jtxtGroup;
    private javax.swing.JTextField jtxtID;
    private javax.swing.JTextField jtxtLockDate;
    private javax.swing.JTextField jtxtName;
    private javax.swing.JTextField jtxtOPB;
    // End of variables declaration//GEN-END:variables
}