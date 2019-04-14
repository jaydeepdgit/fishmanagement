/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import dhananistockmanagement.DeskFrame;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;
import javax.sql.rowset.CachedRowSet;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import support.Constants;
import support.Library;
import support.PickList;

/**
 *
 * @author @JD@
 */
public class StockSummary extends javax.swing.JInternalFrame {
    private Library lb = new Library();
    private PickList mainCategoryPickListView = null;
    private Connection dataConnection = DeskFrame.connMpAdmin;
    private DefaultTableModel model = null;
    private ResultSet rsLocal = null;
    private TableRowSorter<TableModel> rowSorter;
    private CachedRowSet crsMain = null;
    String colname[] = new String[]{"mnitm_name", "itm_name", "pcs", "unt_name", "rate"};
    int column[] = new int[]{0, 0, 0, 0, 2};
    String Syspath = System.getProperty("user.dir");

    /**
     * Creates new form StockSummary
     */
    public StockSummary() {
        initComponents();
        model = (DefaultTableModel) jTable1.getModel();
        mainCategoryPickListView = new PickList(dataConnection);
        setPickListView();
        registerShortKeys();
        setPermission();
        setIconToPnael();
        rowSorter = new TableRowSorter<>(jTable1.getModel());
        jTable1.setRowSorter(rowSorter);
        jTable1.getColumnModel().getColumn(2).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.setBackground(new Color(253, 243, 243));
        setTitle(Constants.STOCK_SUMMARY_FORM_NAME);
    }

    public class StatusColumnCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            //Cells are by default rendered as a JLabel.
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            l.setHorizontalAlignment(SwingConstants.RIGHT);
            //Get the status for the current row.
            if(col == 2) {
                double val = Double.parseDouble(lb.getDeCustomFormat(jTable1.getValueAt(row, col).toString()));
                if(val >= 0) {
                    if(!isSelected) {
                        l.setForeground(Color.BLUE);
                    }
                    l.setText(lb.getIndianFormat(val));
                } else {
                    if(!isSelected) {
                        l.setForeground(Color.RED);
                    }
                    l.setText(lb.getIndianFormat(val*-1));
                }
                l.setFont(l.getFont().deriveFont(Font.BOLD));
            }
            //Return the JLabel which renders the cell.
            return l;
        }
    }

    private void setIconToPnael() {
        Syspath += File.separator + "Resources" + File.separator + "Images" + File.separator;
        jbtnView.setIcon(new ImageIcon(Syspath + "view.png"));
        jbtnPreview.setIcon(new ImageIcon(Syspath + "preview.png"));
        jbtnClose.setIcon(new ImageIcon(Syspath + "close.png"));
    }

    private void setPermission() {
        lb.setUserRightsToButton(jbtnPreview, Constants.STOCK_SUMMARY_FORM_ID, "PRINT");
        lb.setUserRightsToButton(jbtnView, Constants.STOCK_SUMMARY_FORM_ID, "VIEWS");
    }

    private void registerShortKeys() {
        lb.setViewShortcut(this, jbtnView);
        lb.setPreviewShortcut(this, jbtnPreview);
        lb.setCloseShortcut(this, jbtnClose);
    }

    private void setPickListView() {
        mainCategoryPickListView.setLayer(getLayeredPane());
        mainCategoryPickListView.setPickListComponent(jtxtMainItemName);
        mainCategoryPickListView.setNextComponent(jbtnView);
        mainCategoryPickListView.setDefaultWidth(210);
        mainCategoryPickListView.setAllowBlank(true);
        mainCategoryPickListView.setDefaultColumnWidth(200);
        mainCategoryPickListView.setLocation(122, 63);
    }

    private boolean validateForm() {
        boolean flag = true;
        if (!lb.isBlank(jtxtMainItemName)) {
            flag = flag && lb.isExist("main_category", "name", jtxtMainItemName.getText(), dataConnection);
            if (!flag) {
                JOptionPane.showMessageDialog(this, "Invalid Main Item Name", DeskFrame.TITLE, JOptionPane.WARNING_MESSAGE);
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
            lb.printToLogFile("Exception at dispose In Stock Summary", ex);
        }
    }

    private void makeQuery() {
        PreparedStatement psLocal = null;
        String sql = "";
        try {
            sql = "(SELECT sc.name AS slabCategory, - SUM(st.qty) AS pcs, " +
                "sc.id AS slabid " +
                "FROM slab_category sc, stock0_2 st " +
                "WHERE sc.id = st.fk_slab_category_id AND st.trns_id = '2' ";
//            if(!jtxtMainItemName.getText().equalsIgnoreCase("")) {
//                sql += " AND mm.mnitm_cd = '"+ lb.getMainCategory(jtxtMainItemName.getText(), "C") +"'";
//            }
            sql += "GROUP BY sc.id) UNION "+
                "(SELECT sc.name AS slabCategory, SUM(st.qty) AS pcs, " +
                "sc.id AS slabid " +
                "FROM slab_category sc, stock0_2 st " +
                "WHERE sc.id = st.fk_slab_category_id AND (st.trns_id = '4'  OR st.trns_id = '0') ";
//            if(!jtxtMainItemName.getText().equalsIgnoreCase("")) {
//                sql += " AND mm.mnitm_cd = '"+ lb.getMainCategory(jtxtMainItemName.getText(), "C") +"'";
//            }
            sql += "GROUP BY sc.id) ORDER BY slabid";
            psLocal = dataConnection.prepareStatement(sql);
            rsLocal = psLocal.executeQuery();
            crsMain = lb.getBlankCachedRowSet(colname, column);
            String arr[] = new String[5];
            String new_itm_name = "", old_itm_name = "";
            double rate = 0.00;
            double pcs = 0.00;
            while (rsLocal.next()) {
                new_itm_name = rsLocal.getString("slabCategory");
                if(!new_itm_name.equalsIgnoreCase(old_itm_name) && rsLocal.getRow() > 1) {
                    arr[0] = "";
                    arr[1] = old_itm_name;
                    arr[2] = lb.Convert2DecFmt(pcs);
                    arr[3] = "";
                    arr[4] = lb.getIndianFormat(rate);
                    lb.appendColumnToCacheRowSet(crsMain, arr, column);
                    pcs = 0.00;
                }
                pcs += rsLocal.getDouble("pcs");
//                mnitm_name = rsLocal.getString("mnitm_name");
//                unt_name = rsLocal.getString("unt_name");
//                rate = rsLocal.getDouble("rate");
                old_itm_name = new_itm_name;
            }
            arr[0] = "";
            arr[1] = new_itm_name;
            arr[2] = lb.Convert2DecFmt(pcs);
            arr[3] = "";
            arr[4] = lb.getIndianFormat(rate);
            lb.appendColumnToCacheRowSet(crsMain, arr, column);
        } catch(Exception ex) {
            lb.printToLogFile("Exception at MakeQuery In Stock Summary", ex);
        }
    }

    private void searchOnTextFields() {
        jtxtSearch.setFont(new Font("Cambria", 1, 14));

        jtxtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = jtxtSearch.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = jtxtSearch.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
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
        jbtnClose = new javax.swing.JButton();
        jbtnPreview = new javax.swing.JButton();
        jbtnView = new javax.swing.JButton();
        jtxtMainItemName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jtxtSearch = new javax.swing.JTextField();

        setBackground(new java.awt.Color(211, 226, 245));

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(53, 154, 141)));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setBackground(new java.awt.Color(253, 243, 243));
        jScrollPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(4, 110, 152), 1, true));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(735, 384));

        jTable1.setBackground(new java.awt.Color(253, 243, 243));
        jTable1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SR No", "Item Name", "Stock", "Unit Name", "Rate"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(0).setMinWidth(70);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(70);
        }

        jPanel1.add(jScrollPane1);

        jPanel2.setBackground(new java.awt.Color(253, 243, 243));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)), "Details", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 16), new java.awt.Color(0, 0, 255))); // NOI18N

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

        jtxtMainItemName.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtMainItemName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtMainItemName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtMainItemNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtMainItemNameFocusLost(evt);
            }
        });
        jtxtMainItemName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMainItemNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtMainItemNameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtMainItemNameKeyTyped(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel1.setText("Main Item Name");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtMainItemName, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnPreview});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMainItemName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnView)
                    .addComponent(jbtnPreview)
                    .addComponent(jbtnClose))
                .addGap(7, 7, 7))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jbtnClose, jbtnPreview, jbtnView, jtxtMainItemName});

        jtxtSearch.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtSearchKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtSearchKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 956, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 956, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jtxtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
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
                crsMain.beforeFirst();
                int i = 1;
                while(crsMain.next()) {
                    Vector row = new Vector();
                    row.add(i+"");
                    row.add(crsMain.getString(2));
                    row.add(crsMain.getString(3));
                    row.add(crsMain.getString(4));
                    row.add(crsMain.getString(5));
                    model.addRow(row);
                    i++;
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Error at jbtnViewActionPerformed In Stock Summary", ex);
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
                params.put("digit", lb.getDigit());
                params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
                params.put("cadd1", DeskFrame.clSysEnv.getADD1());
                params.put("cadd2", DeskFrame.clSysEnv.getADD2());
                params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
                params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
                params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
                crsMain.beforeFirst();
                lb.reportGenerator("TotalStock.jasper", params, crsMain, jPanel1);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Error at jbtnPreviewActionPerformed In Stock Summary", ex);
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

    private void jtxtMainItemNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMainItemNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMainItemNameFocusGained

    private void jtxtMainItemNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMainItemNameFocusLost
        jtxtMainItemName.setText(jtxtMainItemName.getText().toUpperCase());
    }//GEN-LAST:event_jtxtMainItemNameFocusLost

    private void jtxtMainItemNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainItemNameKeyPressed
        mainCategoryPickListView.setLocation(jtxtMainItemName.getX() + jPanel2.getX(), jtxtMainItemName.getY() + jPanel2.getY() + jtxtMainItemName.getHeight());
        mainCategoryPickListView.pickListKeyPress(evt);
        mainCategoryPickListView.setReturnComponent(new JTextField[]{jtxtMainItemName});
    }//GEN-LAST:event_jtxtMainItemNameKeyPressed

    private void jtxtMainItemNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainItemNameKeyReleased
        try {
            mainCategoryPickListView.setReturnComponent(new JTextField[]{jtxtMainItemName});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM main_category WHERE name LIKE '%" + jtxtMainItemName.getText().toUpperCase() + "%'");
            mainCategoryPickListView.setPreparedStatement(pstLocal);
            mainCategoryPickListView.setValidation(dataConnection.prepareStatement("SELECT * FROM mnitm_mst WHERE name = ?"));
            mainCategoryPickListView.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtMainItemNameKeyReleased In Stock Summary", ex);
        }
    }//GEN-LAST:event_jtxtMainItemNameKeyReleased

    private void jtxtMainItemNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainItemNameKeyTyped
        lb.fixLength(evt, 100);
    }//GEN-LAST:event_jtxtMainItemNameKeyTyped

    private void jtxtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSearchKeyPressed
        searchOnTextFields();
    }//GEN-LAST:event_jtxtSearchKeyPressed

    private void jtxtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSearchKeyReleased
        searchOnTextFields();
    }//GEN-LAST:event_jtxtSearchKeyReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if(evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
//            if (MainClass.df.hasPermission(Constants.STOCK_LEDGER_FORM_ID)) {
//                int index = checkAlradyOpen(Constants.STOCK_LEDGER_FORM_NAME);
//                if (index == -1) {
//                    StockLedger sl = new StockLedger(jTable1.getValueAt(row, 1).toString());
//                    addOnScreen(sl, Constants.STOCK_LEDGER_FORM_NAME);
//                    sl.setTitle(Constants.STOCK_LEDGER_FORM_NAME);
//                } else {
//                    tabbedPane.setSelectedIndex(index);
//                }
//            } else {
//                JOptionPane.showMessageDialog(null, Constants.NO_RIGHTS_TO_VIEW, Constants.STOCK_LEDGER_FORM_NAME, JOptionPane.WARNING_MESSAGE);
//            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnPreview;
    private javax.swing.JButton jbtnView;
    private javax.swing.JTextField jtxtMainItemName;
    private javax.swing.JTextField jtxtSearch;
    // End of variables declaration//GEN-END:variables
}