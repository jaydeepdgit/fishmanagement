/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.event.InternalFrameEvent;
import dhananistockmanagement.DeskFrame;
import static dhananistockmanagement.DeskFrame.getConnection;
import javax.swing.JTextField;
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
public class SubCategory extends javax.swing.JInternalFrame {
    private SmallNavigation navLoad = null;
    private Library lb = new Library();
    private String id = "";
    private Connection dataConnection = DeskFrame.connMpAdmin;
    private ReportTable subCategory;
    PickList mainPickList = null;

    /**
     * Creates new form SubCategory
     */
    public SubCategory() {
        initComponents();
        addNavigation();
        addValidation();
        setPickListView();
        setCompEnable(false);
        setVoucher("Last");
        makeChildTable();
        setTitle(Constants.SUB_CATEGORY_FORM_NAME);
    }

    private void setPickListView() {
        mainPickList = new PickList(dataConnection);

        mainPickList.setLayer(getLayeredPane());
        mainPickList.setPickListComponent(jtxtMainCategory);
        mainPickList.setNextComponent(jtxtName);
        mainPickList.setReturnComponent(new JTextField[]{jtxtMainCategory});
    }

    private void makeChildTable() {
        subCategory = new ReportTable();

        subCategory.AddColumn(0, "ID", 100, java.lang.String.class, null, false);
        subCategory.AddColumn(1, "Name", 300, java.lang.String.class, null, false);
        subCategory.AddColumn(2, "Main Category", 300, java.lang.String.class, null, false);
        subCategory.AddColumn(3, "Status", 150, java.lang.String.class, null, false);
        subCategory.makeTable();
    }

    public void setID(String id) {
        this.id = id;
        setVoucher("edit");
    }

    private void onViewVoucher() {
        this.dispose();

        String sql = "SELECT sc.id, sc.name, mc.name AS maincategory, IF(sc.status = 0, '"+ Constants.ACTIVE +"', '"+ Constants.DEACTIVE +"') AS status "+
            "FROM sub_category sc, main_category mc WHERE sc.fk_main_category_id = mc.id";
        subCategory.setColumnValue(new int[]{1, 2, 3, 4});
        String view_title = Constants.SUB_CATEGORY_FORM_NAME +" VIEW";

        HeaderIntFrame1 rptDetail = new HeaderIntFrame1(dataConnection, id+"", view_title, subCategory, sql, Constants.SUB_CATEGORY_FORM_ID, 1, this, this.getTitle());
        rptDetail.makeView();
        rptDetail.setVisible(true);

        Component c = DeskFrame.tabbedPane.add(view_title, rptDetail);
        c.setName(view_title);
        DeskFrame.tabbedPane.setSelectedComponent(c);
    }

    public void setStartupFocus() {
        jtxtMainCategory.requestFocusInWindow();
    }

    private void addValidation() {
        FieldValidation valid = new FieldValidation();
        jtxtID.setInputVerifier(valid);
        jtxtName.setInputVerifier(valid);
        jtxtMainCategory.setInputVerifier(valid);
    }

    class FieldValidation extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            boolean val = false;
            if (input.equals(jtxtID)) {
                val = fielddValid(input);
            } else if (input.equals(jtxtName)) {
                val = fielddValid(input);
            } else if (input.equals(jtxtMainCategory)) {
                val = fielddValid(input);
            }
            return val;
        }
    }

    private boolean fielddValid(Component comp) {
        navLoad.setMessage("");
        if (comp == jtxtMainCategory) {
            if (navLoad.getMode().equalsIgnoreCase("N") || navLoad.getMode().equalsIgnoreCase("E")) {
                if (lb.isBlank(comp)) {
                    navLoad.setMessage("Main Category should not blank");
                    comp.requestFocusInWindow();
                    return false;
                }
                String code = lb.getMainCategory(jtxtMainCategory.getText(), "C");
                if(code.equalsIgnoreCase("0") || code.equalsIgnoreCase("")) {
                    navLoad.setMessage("Enter valid Main Category");
                    jtxtMainCategory.requestFocusInWindow();
                    return false;
                }
            }
        }
        if (comp == jtxtName) {
            if (navLoad.getMode().equalsIgnoreCase("N") || navLoad.getMode().equalsIgnoreCase("E")) {
                if (lb.isBlank(comp)) {
                    navLoad.setMessage("Name should not blank");
                    comp.requestFocusInWindow();
                    return false;
                }
            }
            if (navLoad.getMode().equalsIgnoreCase("N")) {
                if (lb.isExist("sub_category", "name", jtxtName.getText(), dataConnection)) {
                    navLoad.setMessage("Name already exist!");
                    comp.requestFocusInWindow();
                    return false;
                }
            } else if (navLoad.getMode().equalsIgnoreCase("E")) {
                if (lb.isExistForEdit("sub_category", "name", jtxtName.getText(), "id", id, dataConnection)) {
                    navLoad.setMessage("Name already exist!");
                    comp.requestFocusInWindow();
                    return false;
                }
            }
        }
        return true;
    }

    private void setCompEnable(boolean flag) {
        jtxtID.setEnabled(!flag);
        jtxtName.setEnabled(flag);
        jtxtMainCategory.setEnabled(flag);
        jtxtShortName.setEnabled(flag);
        jcmbStatus.setEnabled(flag);
        jtxtMainCategory.requestFocusInWindow();
    }

    private void setCompText(String text) {
        jtxtID.setText(text);
        jtxtName.setText(text);
        jtxtMainCategory.setText(text);
        jtxtShortName.setText(text);
        jcmbStatus.setSelectedIndex(0);
    }

    private boolean validateForm() {
        boolean flag = fielddValid(jtxtName);
        flag = flag && fielddValid(jtxtMainCategory);
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
                    String db_name_list[] = lb.getOtherCompanyConnection().split(",");
                    for(String db_name : db_name_list) {
                        dataConnection = getConnection(db_name);
                        saveVoucher();
                        dataConnection = DeskFrame.connMpAdmin;
                    }
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
                    if(!lb.isExist("slab_category", "fk_sub_category_id", id+"")) {
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
                        lb.printToLogFile("Error at delete In Sub Category", ex);
                    } catch (SQLException ex1) {
                        lb.printToLogFile("Error at rollback delete In Sub Category", ex1);
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
                    VoucherDisplay vd = new VoucherDisplay(id, Constants.SUB_CATEGORY_INITIAL);
                    DeskFrame.addOnScreen(vd, Constants.SUB_CATEGORY_FORM_NAME +" PRINT");
                } catch(Exception ex) {
                    lb.printToLogFile("Exception at callPrint In Sub Category", ex);
                }
            }

            @Override
            public void setComponentTextFromResultSet() {
                try {
                    id = navLoad.viewData.getString("id");
                    jtxtID.setText(id+"");
                    jtxtName.setText(navLoad.viewData.getString("name"));
                    jtxtMainCategory.setText(lb.getMainCategory(navLoad.viewData.getString("fk_main_category_id"), "N"));
                    jtxtShortName.setText(navLoad.viewData.getString("short_name"));
                    jcmbStatus.setSelectedIndex(navLoad.viewData.getInt("status"));
                    jlblUserName.setText(lb.getUserName(navLoad.viewData.getString("user_cd"), "N"));
                    jlblEditNo.setText(navLoad.viewData.getString("edit_no"));
                    jlblLstUpdate.setText(lb.getTimeStamp(viewData.getTimestamp("time_stamp")));
                } catch (SQLException ex) {
                    lb.printToLogFile("Error at setComponentTextFromResultSet In Sub Category", ex);
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

    private void setVoucher(String move) {
        try {
            String sql = "SELECT * FROM sub_category";
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
            lb.setPermission(navLoad, Constants.SUB_CATEGORY_FORM_ID);
        } catch (Exception ex) {
            lb.printToLogFile("Error at setVoucher In Sub Category", ex);
        }
    }

    private void delete() throws SQLException {
        PreparedStatement psLocal = null;

        psLocal = dataConnection.prepareStatement("DELETE FROM sub_category WHERE id = ?");
        psLocal.setString(1, id);
        psLocal.executeUpdate();
    }

    private void saveVoucher() {
        try {
            PreparedStatement psLocal = null;
            dataConnection.setAutoCommit(false);
            if(navLoad.getMode().equalsIgnoreCase("N")) {
                psLocal = dataConnection.prepareStatement("INSERT INTO sub_category(name, fk_main_category_id, short_name, status, user_cd, id) VALUES (?, ?, ?, ?, ?, ?)");
                id = lb.generateKey("sub_category", "id", Constants.SUB_CATEGORY_INITIAL, 8);
            } else if(navLoad.getMode().equalsIgnoreCase("E")) {
                psLocal = dataConnection.prepareStatement("UPDATE sub_category SET name = ?, fk_main_category_id = ?, short_name = ?, status = ?, user_cd = ?, edit_no = edit_no + 1, time_stamp = CURRENT_TIMESTAMP WHERE id = ?");
            }
            psLocal.setString(1, jtxtName.getText()); // name
            psLocal.setString(2, lb.getMainCategory(jtxtMainCategory.getText(), "C")); // name
            psLocal.setString(3, jtxtShortName.getText()); // short name
            psLocal.setInt(4, jcmbStatus.getSelectedIndex()); // status
            psLocal.setInt(5, DeskFrame.user_id); // user_cd
            psLocal.setString(6, id); // id
            psLocal.executeUpdate();
            dataConnection.commit();
            dataConnection.setAutoCommit(true);
        } catch (SQLException ex) {
            try {
                dataConnection.rollback();
                dataConnection.setAutoCommit(true);
                lb.printToLogFile("Error at save In Sub Category", ex);
            } catch (SQLException ex1) {
                lb.printToLogFile("Error at rollback save In Sub Category", ex1);
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
                lb.printToLogFile("Exception at dispose in Sub Category", pve);
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
        jLabel3 = new javax.swing.JLabel();
        jtxtShortName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jcmbStatus = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jtxtMainCategory = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(686, 455));

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 1, 1, new java.awt.Color(235, 35, 35)));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(253, 243, 243));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)), "Sub Category", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 16), new java.awt.Color(0, 0, 255))); // NOI18N

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
        jLabel2.setText("Sub Category");
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

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel3.setText("Short Name");
        jLabel3.setMaximumSize(new java.awt.Dimension(61, 25));
        jLabel3.setMinimumSize(new java.awt.Dimension(61, 25));
        jLabel3.setPreferredSize(new java.awt.Dimension(61, 25));

        jtxtShortName.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtShortName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtShortName.setMinimumSize(new java.awt.Dimension(6, 25));
        jtxtShortName.setPreferredSize(new java.awt.Dimension(6, 25));
        jtxtShortName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtShortNameFocusGained(evt);
            }
        });
        jtxtShortName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtShortNameKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtShortNameKeyTyped(evt);
            }
        });

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
        jLabel5.setForeground(new java.awt.Color(255, 0, 0));
        jLabel5.setText("Main Category");
        jLabel5.setMaximumSize(new java.awt.Dimension(77, 25));
        jLabel5.setMinimumSize(new java.awt.Dimension(77, 25));
        jLabel5.setPreferredSize(new java.awt.Dimension(77, 25));

        jtxtMainCategory.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtMainCategory.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtMainCategory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtMainCategoryFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtMainCategoryFocusLost(evt);
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtName)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jtxtID, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                                    .addComponent(jtxtShortName, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                                    .addComponent(jcmbStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jtxtMainCategory)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblLstUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(183, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel6, jLabel7, jLabel8});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jtxtID, jtxtShortName});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMainCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtShortName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
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
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2, jtxtID, jtxtName});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel6, jLabel7, jLabel8, jlblEditNo, jlblLstUpdate, jlblUserName});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel5, jtxtMainCategory});

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
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtNameKeyPressed
        lb.enterEvent(evt, jtxtShortName);
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
                if(lb.isExist("sub_category", "id", jtxtID.getText(), dataConnection)) {
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

    private void jtxtShortNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtShortNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtShortNameFocusGained

    private void jtxtShortNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtShortNameKeyPressed
        lb.enterEvent(evt, jcmbStatus);
    }//GEN-LAST:event_jtxtShortNameKeyPressed

    private void jtxtShortNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtShortNameKeyTyped
        lb.fixLength(evt, 5);
    }//GEN-LAST:event_jtxtShortNameKeyTyped

    private void jcmbStatusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbStatusKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            navLoad.setSaveFocus();
        }
    }//GEN-LAST:event_jcmbStatusKeyPressed

    private void jtxtMainCategoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMainCategoryFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMainCategoryFocusGained

    private void jtxtMainCategoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMainCategoryFocusLost
        subCategory.setVisible(false);
    }//GEN-LAST:event_jtxtMainCategoryFocusLost

    private void jtxtMainCategoryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainCategoryKeyPressed
        mainPickList.setLocation(jtxtMainCategory.getX() + jPanel2.getX(), jtxtMainCategory.getY() + jtxtMainCategory.getHeight() + jPanel2.getY());
        mainPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtMainCategoryKeyPressed

    private void jtxtMainCategoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainCategoryKeyTyped
        lb.fixLength(evt, 100);
    }//GEN-LAST:event_jtxtMainCategoryKeyTyped

    private void jtxtMainCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainCategoryKeyReleased
        try {
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM main_category WHERE name LIKE '%"+ jtxtMainCategory.getText() +"%'");
            mainPickList.setPreparedStatement(pstLocal);
            mainPickList.setValidation(dataConnection.prepareStatement("SELECT name FROM main_category WHERE name = ?"));
            mainPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtMainCategoryKeyTyped In Sub Category", ex);
        }
    }//GEN-LAST:event_jtxtMainCategoryKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JComboBox jcmbStatus;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblLstUpdate;
    private javax.swing.JLabel jlblUserName;
    private javax.swing.JTextField jtxtID;
    private javax.swing.JTextField jtxtMainCategory;
    private javax.swing.JTextField jtxtName;
    private javax.swing.JTextField jtxtShortName;
    // End of variables declaration//GEN-END:variables
}