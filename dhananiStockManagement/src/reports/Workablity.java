/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import java.awt.Color;
import dhananistockmanagement.DeskFrame;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import support.Constants;
import support.Library;
import support.OurDateChooser;
import support.PickList;

/**
 *
 * @author @JD@
 */
public class Workablity extends javax.swing.JInternalFrame {
    private Library lb = new Library();
    private PickList acntPickListView = null, mainCategoryPickListView = null;
    private Connection dataConnection = DeskFrame.connMpAdmin;
    private DefaultTableModel model = null;
    private String form_id = Constants.PURCHASE_AVERAGE_FORM_ID;
    private double tot_netamt = 0.00;
    private ResultSet rsLocal = null;
    String Syspath = System.getProperty("user.dir");

    /**
     * Creates new form PurchaseAverage
     */
    public Workablity() {
        initComponents();
        model = (DefaultTableModel) jTable1.getModel();
        acntPickListView = new PickList(dataConnection);
        mainCategoryPickListView = new PickList(dataConnection);
        setPickListView();
        registerShortKeys();
        setPermission();
        setIconToPnael();
        setcheckEnableDisable(false);
        setTextfieldsAtBottom();
        jTable1.setBackground(new Color(255, 255, 224));
        setTitle(Constants.PURCHASE_AVERAGE_FORM_NAME);
    }

    private void setIconToPnael() {
        Syspath += File.separator + "Resources" + File.separator + "Images" + File.separator;
        jbtnView.setIcon(new ImageIcon(Syspath + "view.png"));
        jbtnPreview.setIcon(new ImageIcon(Syspath + "preview.png"));
        jbtnClose.setIcon(new ImageIcon(Syspath + "close.png"));
    }

    public void setStartupFocus() {
        jtxtMainCategory.requestFocusInWindow();
    }

    private void registerShortKeys() {
        lb.setViewShortcut(this, jbtnView);
        lb.setPreviewShortcut(this, jbtnPreview);
        lb.setCloseShortcut(this, jbtnClose);
    }

    private void setPermission() {
        lb.setUserRightsToButton(jbtnPreview, form_id, "PRINT");
        lb.setUserRightsToButton(jbtnView, form_id, "VIEWS");
    }

    private void setPickListView() {
        acntPickListView.setLayer(getLayeredPane());
        acntPickListView.setPickListComponent(jtxtAccountName);
        acntPickListView.setNextComponent(jtxtMainCategory);
        acntPickListView.setDefaultWidth(210);
        acntPickListView.setAllowBlank(true);
        acntPickListView.setDefaultColumnWidth(200);
        acntPickListView.setLocation(122, 63);

        mainCategoryPickListView.setLayer(getLayeredPane());
        mainCategoryPickListView.setPickListComponent(jtxtMainCategory);
        mainCategoryPickListView.setNextComponent(jbtnView);
    }

    private boolean validateForm() {
        boolean flag = true;
        if (!lb.isBlank(jtxtAccountName)) {
            if(!lb.isExist("SELECT id FROM account_master WHERE name = '"+ jtxtAccountName.getText() +"'", dataConnection)) {
                JOptionPane.showMessageDialog(this, Constants.INVALID_ACCOUNT, DeskFrame.TITLE, JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        if (!lb.isBlank(jtxtMainCategory)) {
            String itm_cd = lb.getMainCategory(jtxtMainCategory.getText(), "C");
            if(itm_cd.equalsIgnoreCase("")) {
                JOptionPane.showMessageDialog(this, Constants.INVALID_MAIN_CATEGORY, DeskFrame.TITLE, JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return flag;
    }

    @Override
    public void dispose() {
        try {
            DeskFrame.removeFromScreen(DeskFrame.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose In Purchase Average", ex);
        }
    }

    private void makeQuery() {
        PreparedStatement psLocal = null;
        String sql = "";
        String fromDate = jtxtFromDate.getText();
        String toDate = jtxtToDate.getText();
        try {
            sql = "SELECT pbh.id, pbh.v_date, am.name AS account_name, mc.name AS main_category, " +
                "pbd.weight, pbd.rate, pbd.amount, pbh.total_expense " +
                "FROM purchase_bill_head pbh, purchase_bill_details pbd, account_master am, main_category mc " +
                "WHERE pbh.id = pbd.id AND am.id = pbh.fk_account_master_id AND mc.id = pbd.fk_main_category_id"; 
            if(!jtxtAccountName.getText().equalsIgnoreCase("")) {
                sql +=" AND am.name = \""+ jtxtAccountName.getText() +"\"";
            }
            if(!jtxtMainCategory.getText().equalsIgnoreCase("")) {
                sql +=" AND mc.name = \""+ jtxtMainCategory.getText() +"\"";
            }
            if(jcmDate.isSelected()) {
                sql += " AND ((pbh.v_date >= '"+ lb.ConvertDateFormetForDB(fromDate) +"' AND "
                + " pbh.v_date <= '"+ lb.ConvertDateFormetForDB(toDate) +"') OR pbh.v_date IS NULL)";
            }
            sql += " ORDER BY am.name, mc.name, pbh.v_date";
            psLocal = dataConnection.prepareStatement(sql);
            rsLocal = psLocal.executeQuery();
        } catch(Exception ex) {
            lb.printToLogFile("Exception at MakeQuery In Purchase Average", ex);
        }
    }

    private void setcheckEnableDisable(boolean flag) {
        jLabel3.setEnabled(flag);
        jtxtFromDate.setEnabled(flag);
        jBillDateBtn.setEnabled(flag);
        jLabel4.setEnabled(flag);
        jtxtToDate.setEnabled(flag);
        jBillDateBtn1.setEnabled(flag);
    }

    private void setTextfieldsAtBottom() {
        JComponent[] footer = new JComponent[]{null, null, null, null, null, jlblTotalAmt};
        lb.setTable(jPanel1, jTable1, null, footer);
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
        jlblTotalAmt = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtxtAccountName = new javax.swing.JTextField();
        jbtnView = new javax.swing.JButton();
        jbtnPreview = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jtxtMainCategory = new javax.swing.JTextField();
        jcmDate = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jtxtFromDate = new javax.swing.JTextField();
        jBillDateBtn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jtxtToDate = new javax.swing.JTextField();
        jBillDateBtn1 = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(53, 154, 141)));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setBackground(new java.awt.Color(253, 243, 243));
        jScrollPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(4, 110, 152), 1, true));
        jScrollPane1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(735, 384));

        jTable1.setBackground(new java.awt.Color(253, 243, 243));
        jTable1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ref No", "SR No", "Product Name", "Weight", "Rate", "Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(23);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(0);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(1).setMinWidth(70);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(1).setMaxWidth(70);
            jTable1.getColumnModel().getColumn(3).setMinWidth(120);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(120);
            jTable1.getColumnModel().getColumn(3).setMaxWidth(120);
            jTable1.getColumnModel().getColumn(4).setMinWidth(120);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(120);
            jTable1.getColumnModel().getColumn(4).setMaxWidth(120);
            jTable1.getColumnModel().getColumn(5).setMinWidth(150);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(5).setMaxWidth(150);
        }

        jPanel1.add(jScrollPane1);

        jlblTotalAmt.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblTotalAmt.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblTotalAmt.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(235, 35, 35)));

        jPanel2.setBackground(new java.awt.Color(253, 243, 243));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)), "Details", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 16), new java.awt.Color(0, 0, 255))); // NOI18N

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel1.setText("Account Name:");

        jtxtAccountName.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtAccountName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
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
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAccountNameKeyTyped(evt);
            }
        });

        jbtnView.setBackground(new java.awt.Color(204, 255, 204));
        jbtnView.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnView.setForeground(new java.awt.Color(235, 35, 35));
        jbtnView.setMnemonic('V');
        jbtnView.setText("VIEW RESULT");
        jbtnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnViewActionPerformed(evt);
            }
        });
        jbtnView.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnViewKeyPressed(evt);
            }
        });

        jbtnPreview.setBackground(new java.awt.Color(204, 255, 204));
        jbtnPreview.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnPreview.setForeground(new java.awt.Color(235, 35, 35));
        jbtnPreview.setMnemonic('P');
        jbtnPreview.setText("PREVIEW");
        jbtnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPreviewActionPerformed(evt);
            }
        });
        jbtnPreview.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnPreviewKeyPressed(evt);
            }
        });

        jbtnClose.setBackground(new java.awt.Color(204, 255, 204));
        jbtnClose.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnClose.setForeground(new java.awt.Color(235, 35, 35));
        jbtnClose.setMnemonic('C');
        jbtnClose.setText("CLOSE");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });
        jbtnClose.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnCloseKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel2.setText("Main Category:");

        jtxtMainCategory.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtMainCategory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
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

        jcmDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jcmDate.setText("Check Date");
        jcmDate.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcmDateItemStateChanged(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel3.setText("From");

        jtxtFromDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtFromDate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtFromDate.setMinimumSize(new java.awt.Dimension(6, 25));
        jtxtFromDate.setPreferredSize(new java.awt.Dimension(6, 25));
        jtxtFromDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtFromDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtFromDateFocusLost(evt);
            }
        });
        jtxtFromDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtFromDateKeyPressed(evt);
            }
        });

        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel4.setText("To");

        jtxtToDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtToDate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtToDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtToDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtToDateFocusLost(evt);
            }
        });
        jtxtToDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtToDateKeyPressed(evt);
            }
        });

        jBillDateBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jtxtAccountName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jtxtMainCategory)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jcmDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnPreview, jbtnView});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnClose)
                        .addComponent(jbtnPreview)
                        .addComponent(jbtnView))
                    .addComponent(jtxtAccountName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMainCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jbtnClose, jbtnPreview, jbtnView, jtxtAccountName});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jBillDateBtn1, jLabel2, jLabel3, jLabel4, jcmDate, jtxtFromDate, jtxtMainCategory, jtxtToDate});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jlblTotalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(90, 90, 90)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblTotalAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
        try {
            model.setRowCount(0);
            if (validateForm()) {
                makeQuery();
                jPanel1.removeAll();
                jPanel1.add(jScrollPane1);
                int i = 1;
                tot_netamt = 0.00;
                while (rsLocal.next()) {
                    Vector row = new Vector();
                    row.add(rsLocal.getString("id"));
                    row.add(i+"");
                    row.add(rsLocal.getString("main_category"));
                    row.add(lb.Convert2DecFmt(rsLocal.getDouble("weight")));
                    row.add(lb.getIndianFormat(rsLocal.getDouble("rate")));
                    row.add(lb.getIndianFormat(rsLocal.getDouble("amount")));
                    tot_netamt += rsLocal.getDouble("amount");
                    model.addRow(row);
                    i++;
                }
                jlblTotalAmt.setText(lb.getIndianFormat(tot_netamt));
            }
            setTextfieldsAtBottom();
        } catch (Exception ex) {
            lb.printToLogFile("Error at jbtnViewActionPerformed In Purchase Average", ex);
        }
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnView.doClick();
            jbtnPreview.requestFocusInWindow();
        }
    }//GEN-LAST:event_jbtnViewKeyPressed

    private void jbtnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreviewActionPerformed
        try {
            if (validateForm()) {
                makeQuery();
                jPanel1.removeAll();
                jPanel1.add(jScrollPane1);
                HashMap params = new HashMap();
                params.put("dir", System.getProperty("user.dir"));
                params.put("tot_netamt", tot_netamt);
                params.put("digit", lb.getDigit());
                params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
                params.put("cadd1", DeskFrame.clSysEnv.getADD1());
                params.put("cadd2", DeskFrame.clSysEnv.getADD2());
                params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
                params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
                params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
                params.put("P_TYPE", Constants.PURCHASE_AVERAGE_FORM_NAME);
                lb.reportGenerator("PurchaseAverage.jasper", params, rsLocal, jPanel1);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Error at jbtnPreviewActionPerformed In Purchase Average", ex);
        }
    }//GEN-LAST:event_jbtnPreviewActionPerformed

    private void jbtnPreviewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPreviewKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnPreview.doClick();
        }
    }//GEN-LAST:event_jbtnPreviewKeyPressed

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jbtnCloseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnCloseKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnClose.doClick();
        }
    }//GEN-LAST:event_jbtnCloseKeyPressed

    private void jtxtAccountNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAccountNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAccountNameFocusGained

    private void jtxtAccountNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAccountNameFocusLost
        jtxtAccountName.setText(jtxtAccountName.getText().toUpperCase());
    }//GEN-LAST:event_jtxtAccountNameFocusLost

    private void jtxtAccountNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAccountNameKeyPressed
        acntPickListView.setLocation(jtxtAccountName.getX() + jPanel2.getX(), jtxtAccountName.getY()  + jPanel2.getY() + jtxtAccountName.getHeight());
        acntPickListView.pickListKeyPress(evt);
        acntPickListView.setReturnComponent(new JTextField[]{jtxtAccountName});
    }//GEN-LAST:event_jtxtAccountNameKeyPressed

    private void jtxtAccountNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAccountNameKeyReleased
        try {
            acntPickListView.setReturnComponent(new JTextField[]{jtxtAccountName});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM account_master WHERE name LIKE '%"+ jtxtAccountName.getText().toUpperCase() +"%'");
            acntPickListView.setPreparedStatement(pstLocal);
            acntPickListView.setValidation(dataConnection.prepareStatement("SELECT * FROM account_master WHERE name = ?"));
            acntPickListView.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtAccountNameKeyReleased In Purchase Average", ex);
        }
    }//GEN-LAST:event_jtxtAccountNameKeyReleased

    private void jtxtAccountNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAccountNameKeyTyped
        lb.fixLength(evt, 100);
    }//GEN-LAST:event_jtxtAccountNameKeyTyped

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            lb.quickOpen(jTable1.getValueAt(row, 0).toString());
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jtxtMainCategoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMainCategoryFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMainCategoryFocusGained

    private void jtxtMainCategoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMainCategoryFocusLost
        mainCategoryPickListView.setVisible(false);
    }//GEN-LAST:event_jtxtMainCategoryFocusLost

    private void jtxtMainCategoryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainCategoryKeyPressed
        mainCategoryPickListView.setReturnComponent(new JTextField[]{jtxtMainCategory});
        mainCategoryPickListView.setLocation(jtxtMainCategory.getX() + jPanel2.getX(), jtxtMainCategory.getY() + jtxtMainCategory.getHeight()+ jPanel2.getY());
        mainCategoryPickListView.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtMainCategoryKeyPressed

    private void jtxtMainCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainCategoryKeyReleased
        try {
            mainCategoryPickListView.setReturnComponent(new JTextField[]{jtxtMainCategory});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM main_category WHERE name LIKE '%"+ jtxtMainCategory.getText().toUpperCase() +"%'");
            mainCategoryPickListView.setValidation(dataConnection.prepareStatement("SELECT * FROM main_category WHERE name = ?"));
            mainCategoryPickListView.setPreparedStatement(pstLocal);
            mainCategoryPickListView.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtMainCategoryKeyReleased In Purchase Average", ex);
        }
    }//GEN-LAST:event_jtxtMainCategoryKeyReleased

    private void jtxtMainCategoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainCategoryKeyTyped
        lb.fixLength(evt, 200);
    }//GEN-LAST:event_jtxtMainCategoryKeyTyped

    private void jcmDateItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcmDateItemStateChanged
        if(jcmDate.isSelected()) {
            setcheckEnableDisable(true);
        } else {
            setcheckEnableDisable(false);
        }
    }//GEN-LAST:event_jcmDateItemStateChanged

    private void jtxtFromDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtFromDateFocusGained

    private void jtxtFromDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDateFocusLost
        lb.setDateUsingJTextField(jtxtFromDate);
    }//GEN-LAST:event_jtxtFromDateFocusLost

    private void jtxtFromDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFromDateKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jtxtToDate.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtFromDateKeyPressed

    private void jBillDateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtnActionPerformed
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtFromDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtFromDate.getX() - 200, jtxtFromDate.getY() + 125, jtxtFromDate.getX() + odc.getWidth(), jtxtFromDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, Constants.SELECT_DATE);
    }//GEN-LAST:event_jBillDateBtnActionPerformed

    private void jtxtToDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtToDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtToDateFocusGained

    private void jtxtToDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtToDateFocusLost
        lb.setDateUsingJTextField(jtxtToDate, jbtnView);
    }//GEN-LAST:event_jtxtToDateFocusLost

    private void jtxtToDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtToDateKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnView.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtToDateKeyPressed

    private void jBillDateBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtn1ActionPerformed
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtToDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtToDate.getX() - 200, jtxtToDate.getY() + 125, jtxtToDate.getX() + odc.getWidth(), jtxtToDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, Constants.SELECT_DATE);
    }//GEN-LAST:event_jBillDateBtn1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jBillDateBtn1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnPreview;
    private javax.swing.JButton jbtnView;
    private javax.swing.JCheckBox jcmDate;
    private javax.swing.JLabel jlblTotalAmt;
    private javax.swing.JTextField jtxtAccountName;
    private javax.swing.JTextField jtxtFromDate;
    private javax.swing.JTextField jtxtMainCategory;
    private javax.swing.JTextField jtxtToDate;
    // End of variables declaration//GEN-END:variables
}