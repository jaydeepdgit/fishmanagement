/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import dhananistockmanagement.DeskFrame;
import static dhananistockmanagement.DeskFrame.getConnection;
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
public class GroupMaster extends javax.swing.JInternalFrame {
    private Connection dataConnection = DeskFrame.connMpAdmin;
    private SmallNavigation navLoad;
    private Library lb = new Library();
    private String groupCd = "";
    private ReportTable viewTable = null;
    PickList groupPickList = null;

    /**
     * Creates new form GroupMaster
     */
    public GroupMaster() {
        initComponents();
        connectNavigation();
        navLoad.setComponentEnabledDisabled(false);
        setVoucher("last");
        groupPickList = new PickList(dataConnection);
        setPickListView();
        tableForView();
        addValidation();
        setTitle(Constants.GROUP_MASTER_FORM_NAME);
    }

    private void tableForView() {
        viewTable = new ReportTable();

        viewTable.AddColumn(0, "Group Name", 600, java.lang.String.class, null, false);
        viewTable.makeTable();
    }

    public void setID(String code) {
        groupCd = lb.getGroupName(code, "C");
        setVoucher("Edit");
    }

    private void onViewVoucher() {
        this.dispose();

        String sql = "SELECT name FROM group_master";
        viewTable.setColumnValue(new int[]{1});
        String view_title = Constants.GROUP_MASTER_FORM_NAME +" VIEW";

        HeaderIntFrame1 rptDetail = new HeaderIntFrame1(dataConnection, lb.getGroupName(groupCd, "N"), view_title, viewTable, sql, Constants.GROUP_MASTER_FORM_ID, 1, this,this.getTitle());
        rptDetail.makeView();
        rptDetail.setVisible(true);

        Component c = DeskFrame.tabbedPane.add(view_title, rptDetail);
        c.setName(view_title);
        DeskFrame.tabbedPane.setSelectedComponent(c);
    }

    private void setPickListView() {
        groupPickList.setLayer(getLayeredPane());
        groupPickList.setPickListComponent(jtxtHeadGroup);
        groupPickList.setNextComponent(jcmbEffectTo);
        groupPickList.setReturnComponent(new JTextField[]{jtxtHeadGroup});
    }

    @Override
    public void dispose() {
        try {
            DeskFrame.removeFromScreen(DeskFrame.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose In Group Master", ex);
        }
    }

    private void addValidation() {
        fieldvalidation fldvalidation = new fieldvalidation();
        jtxtGroupName.setInputVerifier(fldvalidation);
        jtxtHeadGroup.setInputVerifier(fldvalidation);
    }

    class fieldvalidation extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            boolean val = false;
            ((JTextField) input).setText(((JTextField) input).getText().toUpperCase());
            val = fldValid(input);
            return val;
        }
    }

    private boolean fldValid(Component comp) {
        navLoad.setMessage("");
        if (!navLoad.getMode().equalsIgnoreCase("")) {
            if (comp == jtxtGroupName) {
                if (navLoad.getMode().equalsIgnoreCase("N") || navLoad.getMode().equalsIgnoreCase("E")) {
                    if (lb.isBlank(comp)) {
                        navLoad.setMessage("Group Name should not be blank");
                        comp.requestFocusInWindow();
                        return false;
                    }
                }
                if (navLoad.getMode().equalsIgnoreCase("N")) {
                    if (lb.isExist("group_master", "name", jtxtGroupName.getText(), dataConnection)) {
                        navLoad.setMessage("Group Name is already exist!");
                        comp.requestFocusInWindow();
                        return false;
                    }
                } else if (navLoad.getMode().equalsIgnoreCase("E")) {
                    if (lb.isExistForEdit("group_master", "name", jtxtGroupName.getText(), "id", String.valueOf(groupCd), dataConnection)) {
                        navLoad.setMessage("Group Name is already exist!");
                        comp.requestFocusInWindow();
                        return false;
                    }
                }
            }
            if(comp == jtxtHeadGroup) {
                jtxtHeadGroup.setText(jtxtHeadGroup.getText().toUpperCase());
                if (lb.isBlank(comp)) {
                    navLoad.setMessage("Head Group name should not be blank");
                    return false;
                }
                String code = lb.getData("name", "group_master", "head_grp='' AND name", jtxtHeadGroup.getText());
                if (code.equalsIgnoreCase("")) {
                    navLoad.setMessage("Head Group is invalid");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validateForm() {
        boolean flag = true;
        flag = flag && fldValid(jtxtGroupName);
        flag = flag && fldValid(jtxtHeadGroup);
        return flag;
    }

    public void setGroupid(String groupCd) {
        this.groupCd = groupCd;
        setVoucher("edit");
    }

    private void setVoucher(String tag) {
        try {
            navLoad.setComponentEnabledDisabled(false);
            String sql = "SELECT * FROM group_master";
            if (tag.equalsIgnoreCase("first")) {
                sql += " ORDER BY id";
            } else if (tag.equalsIgnoreCase("previous")) {
                sql += " WHERE id < '"+ groupCd +"' ORDER BY id DESC";
            } else if (tag.equalsIgnoreCase("next")) {
                sql += " WHERE id > '"+ groupCd +"'";
            } else if (tag.equalsIgnoreCase("last")) {
                sql += " ORDER BY id DESC";
            } else if (tag.equalsIgnoreCase("edit")) {
                sql += " WHERE id = '"+ groupCd +"'";
            }
            navLoad.viewData = lb.fetchData(sql, dataConnection);
            if (navLoad.viewData.next()) {
                navLoad.setComponentTextFromResultSet();
            } else {
                if(tag.equalsIgnoreCase("last")) {
                    setComponenttextToBlank();
                }
            }
            navLoad.setComponentEnabledDisabled(false);
            navLoad.setLastFocus();
            lb.setPermission(navLoad, Constants.GROUP_MASTER_FORM_ID);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setVoucher In Group Master", ex);
        }
    }

    private void onPrintVoucher() {
        try {
            VoucherDisplay vd = new VoucherDisplay(groupCd, Constants.GROUP_MASTER_INITIAL +"M");
            DeskFrame.addOnScreen(vd, Constants.GROUP_MASTER_FORM_NAME +" PRINT");
        } catch(Exception ex) {
            lb.printToLogFile("Exception at onPrintVoucher In Group Master", ex);
        }
    }

    private void cancelOrClose() {
        if (navLoad.getSaveFlag()) {
            this.dispose();
        } else {
            groupPickList.setVisible(false);
            navLoad.setMode("");
            navLoad.setComponentEnabledDisabled(false);
            navLoad.setMessage("");
            navLoad.setSaveFlag(true);
        }
    }

    private int saveVoucher() {
        PreparedStatement pstLocal = null;
        String sql = "";
        int data = 0;
        try {
            dataConnection.setAutoCommit(false);
            if (navLoad.getMode().equalsIgnoreCase("N")) {
                groupCd = lb.generateKey("group_master", "id", Constants.GROUP_MASTER_INITIAL, 8);
                sql = "INSERT INTO group_master(name, user_cd, head, head_grp, acc_eff, id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, jtxtGroupName.getText().trim().toUpperCase()); // name
                pstLocal.setInt(2, DeskFrame.user_id); // user_cd
                pstLocal.setInt(3, Integer.parseInt("1")); // head
                pstLocal.setString(4, lb.getGroupName(jtxtHeadGroup.getText(), "C")); // head_grp
                pstLocal.setInt(5, jcmbEffectTo.getSelectedIndex()); // acc_eff
                pstLocal.setString(6, groupCd); // id
                data = pstLocal.executeUpdate();
            } else if (navLoad.getMode().equalsIgnoreCase("E")) {
                sql = "UPDATE group_master SET name = ?, user_cd = ?, head_grp = ?, edit_no = edit_no + 1,"
                        + "time_stamp = CURRENT_TIMESTAMP WHERE id='"+ groupCd +"'";
                pstLocal = dataConnection.prepareStatement(sql);
                pstLocal.setString(1, jtxtGroupName.getText().trim().toUpperCase()); // name
                pstLocal.setInt(2, DeskFrame.user_id); // user_cd
                pstLocal.setString(3, lb.getGroupName(jtxtHeadGroup.getText(), "C")); // head_grp
                data = pstLocal.executeUpdate();
            }
            dataConnection.commit();
            dataConnection.setAutoCommit(true);
        } catch (SQLException ex) {
            try {
                dataConnection.rollback();
                dataConnection.setAutoCommit(true);
                lb.printToLogFile("Error at save In Group Master", ex);
            } catch (SQLException ex1) {
                lb.printToLogFile("Error at rollback save In Group Master", ex1);
            }
        }
        return data;
    }

    private void setComponenttextToBlank() {
        jtxtGroupCD.setText("");
        jtxtGroupName.setText("");
        jtxtHeadGroup.setText("");
    }

    private void connectNavigation() {
        class smallNavigation extends SmallNavigation {
            @Override
            public void callNew() {
                setComponenttextToBlank();
                setComponentEnabledDisabled(true);
                jtxtGroupName.requestFocusInWindow();
                navLoad.setSaveFlag(false);
                navLoad.setMode("N");
            }

            @Override
            public void callEdit() {
                if (lb.checkGroup(groupCd)) {
                    setComponentEnabledDisabled(true);
                    jtxtGroupName.requestFocusInWindow();
                    navLoad.setSaveFlag(false);
                    navLoad.setMode("E");
                } else {
                    navLoad.setMessage("You can not edit default group.");
                }
            }

            @Override
            public void callSave() {
                if(validateForm()) {
                    try {
                        saveVoucher();
                        String db_name_list[] = lb.getOtherCompanyConnection().split(",");
                        for(String db_name : db_name_list) {
                            dataConnection = getConnection(db_name);
                            saveVoucher();
                            dataConnection = DeskFrame.connMpAdmin;
                        }
                        setSaveFlag(true);
                        if(navLoad.getMode().equalsIgnoreCase("N")) {
                            setVoucher("Last");
                        } else {
                            setVoucher("Edit");
                        }
                    } catch(Exception ex) {
                        lb.printToLogFile("Error at save in Group Master", ex);
                        try {
                            dataConnection.rollback();
                            dataConnection.setAutoCommit(true);
                        } catch(Exception ex1){
                            lb.printToLogFile("Error at rollback save in Group Master", ex1);
                        }
                    }
                }
            }

            @Override
            public void callDelete() {
                if (lb.checkGroup(groupCd)) {
                    if (lb.getData("id", "account_master", "fk_group_id", groupCd).equalsIgnoreCase("")) {
                        lb.confirmDialog(Constants.DELETE_RECORD);
                        if (lb.type) {
                            try {
                                dataConnection.setAutoCommit(false);
                                String sql = "DELETE FROM group_master WHERE id = ?";
                                PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
                                pstLocal.setString(1, groupCd);
                                pstLocal.executeUpdate();
                                setVoucher("Previous");
                                dataConnection.commit();
                                dataConnection.setAutoCommit(true);
                            } catch (Exception ex) {
                                lb.printToLogFile("Exception at callDelete In Group Master", ex);
                                try {
                                    dataConnection.rollback();
                                    dataConnection.setAutoCommit(true);
                                } catch (Exception ex1) {
                                    lb.printToLogFile("Exception at rollback callDelete In Group Master", ex1);
                                }
                            }
                        } else {
                            navLoad.setSaveFocus();
                        }
                    } else {
                        navLoad.setMessage("Group is used in other forms.You can not delete this group");
                    }
                } else {
                    navLoad.setMessage("You can not delete default group.");
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
                onPrintVoucher();
            }

            @Override
            public void setComponentTextFromResultSet() {
                try {
                    groupCd = viewData.getString("id");
                    jtxtGroupCD.setText(groupCd);
                    jtxtGroupName.setText(viewData.getString("name"));
                    jtxtHeadGroup.setText(lb.getGroupName(viewData.getString("head_grp"), "N"));
                    jcmbEffectTo.setSelectedIndex(lb.getGroupEffect(groupCd));
                    jlblUserName.setText(lb.getUserName(viewData.getString("user_cd"), "N"));
                    jlblEditNo.setText(viewData.getString("edit_no"));
                    jlblLstUpdate.setText(lb.getTimeStamp(viewData.getTimestamp("time_stamp")));
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setComponentTextFromResultSet In Group Master", ex);
                }
            }

            @Override
            public void setComponentEnabledDisabled(boolean flag) {
                jtxtGroupCD.setEnabled(!flag);
                jtxtGroupName.setEnabled(flag);
                jtxtHeadGroup.setEnabled(flag);
                jcmbEffectTo.setEnabled(flag);
            }
        }
        navLoad = new smallNavigation();
        jpanelNavigation.add(navLoad);
        navLoad.setVisible(true);
    }

    private void setComboIndex() {
        String sql = "SELECT acc_eff FROM group_master WHERE name = '"+ jtxtHeadGroup.getText() +"'";
        try {
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                jcmbEffectTo.setSelectedIndex(rsLocal.getInt(1));
            }
            if (rsLocal != null) {
                rsLocal.close();
            }
            if (pstLocal != null) {
                pstLocal.close();
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setComboIndex In Group Master", ex);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpanelNavigation = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jtxtGroupName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jtxtHeadGroup = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jcmbEffectTo = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jlblLstUpdate = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jlblUserName = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jtxtGroupCD = new javax.swing.JTextField();

        setClosable(true);

        jpanelNavigation.setBackground(new java.awt.Color(253, 243, 243));
        jpanelNavigation.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 1, 1, new java.awt.Color(235, 35, 35)));
        jpanelNavigation.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)), "Group Information", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 16), new java.awt.Color(0, 0, 255))); // NOI18N

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("Group Name");

        jtxtGroupName.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtGroupName.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtGroupName.setMinimumSize(new java.awt.Dimension(6, 25));
        jtxtGroupName.setName(""); // NOI18N
        jtxtGroupName.setPreferredSize(new java.awt.Dimension(6, 25));
        jtxtGroupName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtGroupNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtGroupNameFocusLost(evt);
            }
        });
        jtxtGroupName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtGroupNameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtGroupNameKeyTyped(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("Head Group");

        jtxtHeadGroup.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtHeadGroup.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtHeadGroup.setMinimumSize(new java.awt.Dimension(6, 25));
        jtxtHeadGroup.setPreferredSize(new java.awt.Dimension(6, 25));
        jtxtHeadGroup.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtHeadGroupFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtHeadGroupFocusLost(evt);
            }
        });
        jtxtHeadGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtHeadGroupKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtHeadGroupKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtHeadGroupKeyTyped(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel3.setText("Effect To");

        jcmbEffectTo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jcmbEffectTo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Trading A/c", "Profit & Loss A/C", "Ballance Sheet" }));
        jcmbEffectTo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jcmbEffectTo.setEnabled(false);
        jcmbEffectTo.setMinimumSize(new java.awt.Dimension(106, 25));
        jcmbEffectTo.setPreferredSize(new java.awt.Dimension(106, 25));
        jcmbEffectTo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jcmbEffectToFocusGained(evt);
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
        jLabel4.setText("Group CD");

        jtxtGroupCD.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtGroupCD.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtGroupCD.setMinimumSize(new java.awt.Dimension(6, 25));
        jtxtGroupCD.setPreferredSize(new java.awt.Dimension(6, 25));
        jtxtGroupCD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtGroupCDFocusGained(evt);
            }
        });
        jtxtGroupCD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtGroupCDKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtGroupCDKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtGroupCD, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jlblLstUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jlblEditNo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jlblUserName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jtxtHeadGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jtxtGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jcmbEffectTo, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(153, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtGroupCD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtHeadGroup, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbEffectTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel6, jlblUserName});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel7, jlblEditNo});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel8, jlblLstUpdate});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2, jtxtGroupName, jtxtHeadGroup});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, jcmbEffectTo});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel4, jtxtGroupCD});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpanelNavigation, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jpanelNavigation, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtGroupNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGroupNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtGroupNameFocusGained

    private void jtxtHeadGroupFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtHeadGroupFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtHeadGroupFocusGained

    private void jtxtGroupNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGroupNameFocusLost
        jtxtGroupName.setText(jtxtGroupName.getText().toUpperCase());
    }//GEN-LAST:event_jtxtGroupNameFocusLost

    private void jtxtHeadGroupFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtHeadGroupFocusLost
        
    }//GEN-LAST:event_jtxtHeadGroupFocusLost

    private void jtxtGroupNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupNameKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jtxtHeadGroup.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtGroupNameKeyReleased

    private void jcmbEffectToFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jcmbEffectToFocusGained
        setComboIndex();
        navLoad.setSaveFocus();
    }//GEN-LAST:event_jcmbEffectToFocusGained

    private void jtxtGroupCDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGroupCDFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtGroupCDFocusGained

    private void jtxtGroupCDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupCDKeyPressed
        if(navLoad.getMode().equalsIgnoreCase("")) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                if(lb.isExist("group_master", "id", jtxtGroupCD.getText(), dataConnection)) {
                    groupCd = jtxtGroupCD.getText();
                    navLoad.setMessage("");
                    setVoucher("edit");
                } else {
                    navLoad.setMessage("Group CD is invalid");
                }
            }
            jtxtGroupCD.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtGroupCDKeyPressed

    private void jtxtGroupCDKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupCDKeyTyped
        lb.fixLength(evt, 7);
    }//GEN-LAST:event_jtxtGroupCDKeyTyped

    private void jtxtGroupNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupNameKeyTyped
        lb.fixLength(evt, 50);
    }//GEN-LAST:event_jtxtGroupNameKeyTyped

    private void jtxtHeadGroupKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtHeadGroupKeyTyped
        lb.fixLength(evt, 50);
    }//GEN-LAST:event_jtxtHeadGroupKeyTyped

    private void jtxtHeadGroupKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtHeadGroupKeyReleased
        try {
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM group_master WHERE head = 0 AND name LIKE '%"+ jtxtHeadGroup.getText().toUpperCase() +"%'");
            groupPickList.setPreparedStatement(pstLocal);
            groupPickList.setValidation(dataConnection.prepareStatement("SELECT name FROM group_master WHERE head = 0 AND name = ?"));
            groupPickList.pickListKeyRelease(evt);
        } catch(Exception ex) {
            lb.printToLogFile("Exception at jtxtHeadGroupKeyReleased In Group Master", ex);
        }
    }//GEN-LAST:event_jtxtHeadGroupKeyReleased

    private void jtxtHeadGroupKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtHeadGroupKeyPressed
        groupPickList.setLocation(jtxtHeadGroup.getX() + jPanel1.getX(), jPanel1.getY() + jtxtHeadGroup.getY() + jtxtHeadGroup.getHeight());
        groupPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtHeadGroupKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox jcmbEffectTo;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JPanel jpanelNavigation;
    private javax.swing.JTextField jtxtGroupCD;
    private javax.swing.JTextField jtxtGroupName;
    private javax.swing.JTextField jtxtHeadGroup;
    // End of variables declaration//GEN-END:variables
}