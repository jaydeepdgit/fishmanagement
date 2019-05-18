/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package reports;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;
import javax.sql.rowset.CachedRowSet;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import dhananistockmanagement.DeskFrame;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import dhananistockmanagement.MainClass;
import support.Constants;
import support.Library;
import support.PickList;

/**
 *
 * @author @JD@
 */
public class CollectionReport extends javax.swing.JInternalFrame {
    Library lb = new Library();
    Connection dataConnection = DeskFrame.connMpAdmin;
    private CachedRowSet crsMain = null;
    private PickList groupPickList = null;
    DefaultTableModel dtm = null;
    private ResultSet viewDataRs = null;
    String colname[] = new String[]{"ac_name", "paid_amt", "rec_amt", "ac_cd"};
    int column[] = new int[]{0, 0, 0, 0};
    private TableRowSorter<TableModel> rowSorter;
    String Syspath = System.getProperty("user.dir");

    /**
     * Creates new form CollectionReport
     */
    public CollectionReport() {
        initComponents();
        dtm = (DefaultTableModel) jTable1.getModel();
        groupPickList = new PickList(dataConnection);
        registerShortKeys();
        setPermission();
        setPickListView();
        setTextfieldsAtBottom();
        setIconToPanel();
        setcellRender();
        rowSorter = new TableRowSorter<>(jTable1.getModel());
        jTable1.setRowSorter(rowSorter);
        jTable1.setBackground(new Color(253, 243, 243));
        setTitle(Constants.COLLECTION_REPORT_FORM_NAME);
    }

    private void setcellRender() {
        jTable1.getColumnModel().getColumn(1).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.getColumnModel().getColumn(2).setCellRenderer(new StatusColumnCellRenderer());
    }

    public class StatusColumnCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            // Cells are by default rendered as a JLabel.
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            l.setHorizontalAlignment(SwingConstants.RIGHT);
            // Get the status for the current row.
            if (col == 1) {
                if(!isSelected) {
                    l.setForeground(Color.RED);
                }
                l.setFont(l.getFont().deriveFont(Font.BOLD));
            } else if (col == 2) {
                if(!isSelected) {
                    l.setForeground(Color.BLUE);
                }
                l.setFont(l.getFont().deriveFont(Font.BOLD));
            }
            //Return the JLabel which renders the cell.
            return l;
        }
    }

    private void setIconToPanel() {
        Syspath += File.separator + "Resources" + File.separator + "Images" + File.separator;
        jbtnView.setIcon(new ImageIcon(Syspath + "view.png"));
        jbtnPreview.setIcon(new ImageIcon(Syspath + "preview.png"));
        jbtnClose.setIcon(new ImageIcon(Syspath + "close.png"));
    }

    private void setPermission() {
        lb.setUserRightsToButton(jbtnPreview, Constants.COLLECTION_REPORT_FORM_ID, "PRINT");
        lb.setUserRightsToButton(jbtnView, Constants.COLLECTION_REPORT_FORM_ID, "VIEWS");
    }

    private void setPickListView() {
        groupPickList.setLayer(getLayeredPane());
        groupPickList.setPickListComponent(jtxtGroupName);
        groupPickList.setNextComponent(jbtnView);
    }

    @Override
    public void dispose() {
        try {
            DeskFrame.removeFromScreen(DeskFrame.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose In Collection Report", ex);
        }
    }

    private void registerShortKeys() {
        lb.setViewShortcut(this, jbtnView);
        lb.setPreviewShortcut(this, jbtnPreview);
        lb.setCloseShortcut(this, jbtnClose);
    }

    private boolean validateForm() {
        if(!lb.isBlank(jtxtGroupName)) {
            if(lb.getGroupName(jtxtGroupName.getText(), "C").equalsIgnoreCase("")) {
                JOptionPane.showMessageDialog(this, "Invalid Group Name", DeskFrame.TITLE, JOptionPane.WARNING_MESSAGE);
                jtxtGroupName.requestFocusInWindow();
                return false;
            }
        }
        return true;
    }

    private void jbtnPreviewActionPerformedRoutine() {
        makeQuery();
        try {
            HashMap params = new HashMap();

            if(!lb.isBlank(jtxtGroupName)) {
                params.put("Header", "Group Name");
                params.put("code_name", jtxtGroupName.getText());
            } else {
                params.put("Header", "");
                params.put("code_name", "");
            }
            params.put("rec_amt", recrs.getText());
            params.put("paid_amt", payrs.getText());
            params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
            params.put("cadd1", DeskFrame.clSysEnv.getADD1());
            params.put("cadd2", DeskFrame.clSysEnv.getADD2());
            params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
            params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
            params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());

            crsMain.beforeFirst();
            JRResultSetDataSource dataSource = new JRResultSetDataSource(crsMain);
            JasperPrint print = null;

            print = JasperFillManager.fillReport(System.getProperty("user.dir") + File.separatorChar + "Reports"+ File.separatorChar +"collection.jasper", params, dataSource);
            jPanel1.removeAll();
            JRViewer jrViewer = new JRViewer(print);
            jrViewer.setSize(jPanel1.getWidth(), jPanel1.getHeight());
            jPanel1.add(jrViewer);
            SwingUtilities.updateComponentTreeUI(jPanel1);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jbtnPreviewActionPerformedRoutine In Collection Report", ex);
        }
    }

    private void JbtnViewActionPerformedRoutine() {
        makeQuery();
        jPanel1.removeAll();
        jPanel1.add(jScrollPane1);
        jScrollPane1.setVisible(true);
        try {
            dtm.setRowCount(0);
            crsMain.beforeFirst();
            while(crsMain.next()) {
                Vector row = new Vector();
                row.add(crsMain.getString(1));
                row.add(lb.getIndianFormat(lb.replaceAll(crsMain.getString(2))));
                row.add(lb.getIndianFormat(lb.replaceAll(crsMain.getString(3))));
                row.add(crsMain.getString(4));
                dtm.addRow(row);
            }
            addTotal();
            SwingUtilities.updateComponentTreeUI(jPanel1);
        } catch (Exception ex) {
            lb.printToLogFile("exception at JbtnViewActionPerformedRoutine In Collection Report", ex);
        }
    }

    private void addTotal() {
        double payrs1 = 0.00, recrs1 = 0.00;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            payrs1 += Double.parseDouble(lb.getDeCustomFormat(jTable1.getValueAt(i, 1).toString()));
            recrs1 += Double.parseDouble(lb.getDeCustomFormat(jTable1.getValueAt(i, 2).toString()));
        }
        payrs.setText(lb.getIndianFormat(payrs1));
        recrs.setText(lb.getIndianFormat(recrs1));
        payrs.setForeground(Color.RED);
        recrs.setForeground(Color.BLUE);
    }

    private void makeQuery() {
        try {
            String sql = "";

            sql = "SELECT o.ac_cd, o.grp_cd, o.bal FROM oldb2_1 o, account_master a WHERE a.id = o.ac_cd AND o.bal <> 0 ";
            if(!jtxtGroupName.getText().equalsIgnoreCase("")) {
                sql += "AND a.fk_group_id = '"+ lb.getGroupName(jtxtGroupName.getText(), "C") +"'";
            }
            sql += " ORDER BY a.name";

            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            viewDataRs = pstLocal.executeQuery();

            String ac_cd = "", prev_ac_cd = "";
            String arr[] = new String[4];
            double rs = 0.00;
            crsMain = lb.getBlankCachedRowSet(colname, column);
            while(viewDataRs.next()) {
                ac_cd = viewDataRs.getString("ac_cd");
                if (!prev_ac_cd.equalsIgnoreCase(ac_cd)) {
                    if (rs != 0) {
                        arr[0] = lb.getAcountCode(prev_ac_cd, "N");
                        if (rs < 0) {
                            arr[1] = lb.Convert2DecFmt(Math.abs(rs));
                            arr[2] = "0.00";
                        } else {
                            arr[1] = "0.00";
                            arr[2] = lb.Convert2DecFmt(Math.abs(rs));
                        }
                        arr[3] = prev_ac_cd;

                        lb.appendColumnToCacheRowSet(crsMain, arr, column);
                        rs = 0.00;
                    }
                }
                rs = viewDataRs.getDouble("bal");
                prev_ac_cd = ac_cd;
            }

            if (rs != 0) {
                arr[0] = lb.getAcountCode(prev_ac_cd, "N");
                if (rs < 0) {
                    arr[1] = lb.Convert2DecFmt(Math.abs(rs));
                    arr[2] = "0.00";
                } else {
                    arr[1] = "0.00";
                    arr[2] = lb.Convert2DecFmt(Math.abs(rs));
                }
                arr[3] = prev_ac_cd;
                lb.appendColumnToCacheRowSet(crsMain, arr, column);
                rs = 0.00;
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at makeQuery In Collection Report", ex);
        }
    }

    private void setTextfieldsAtBottom() {
        lb.setTable(jTable1, new JComponent[]{jLabel3, payrs, recrs, null});
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
        jLabel3 = new javax.swing.JLabel();
        payrs = new javax.swing.JLabel();
        recrs = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jbtnClose = new javax.swing.JButton();
        jbtnPreview = new javax.swing.JButton();
        jbtnView = new javax.swing.JButton();
        jtxtGroupName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtxtSearch = new javax.swing.JTextField();

        setBackground(new java.awt.Color(211, 226, 245));
        setClosable(true);
        setIconifiable(true);

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(53, 154, 141)));
        jPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jPanel1ComponentResized(evt);
            }
        });
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setBackground(new java.awt.Color(253, 243, 243));
        jScrollPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(4, 110, 152), 1, true));

        jTable1.setBackground(new java.awt.Color(253, 243, 243));
        jTable1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Party Name", "Payable Amt", "Recievable Amt", "AC_CD"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(0);
        jTable1.getColumnModel().getColumn(3).setMaxWidth(0);

        jPanel1.add(jScrollPane1);

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Balance :");

        payrs.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        payrs.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        payrs.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(235, 35, 35)));

        recrs.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        recrs.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        recrs.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(235, 35, 35)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(payrs, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(recrs, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(recrs, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(payrs, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(0, 0, 0))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, payrs, recrs});

        jPanel3.setBackground(new java.awt.Color(253, 243, 243));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)), "Details", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 16), new java.awt.Color(0, 0, 255))); // NOI18N

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

        jtxtGroupName.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtGroupName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtGroupName.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtGroupName.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtGroupName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtGroupNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtGroupNameFocusLost(evt);
            }
        });
        jtxtGroupName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtGroupNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtGroupNameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtGroupNameKeyTyped(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel4.setText("Group Name");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtGroupName, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnPreview, jbtnView});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jbtnClose, jbtnPreview, jbtnView});

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
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtSearch))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jtxtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel1ComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jPanel1ComponentResized

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if(evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (MainClass.df.hasPermission(Constants.GENERAL_LEDGER_FORM_ID)) {
                lb.openGeneralLedgerPopUp(jTable1.getValueAt(row, 3).toString(), jtxtGroupName);
            } else {
                JOptionPane.showMessageDialog(null, Constants.NO_RIGHTS_TO_VIEW, Constants.GENERAL_LEDGER_FORM_NAME, JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jbtnCloseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnCloseKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            this.dispose();
        }
    }//GEN-LAST:event_jbtnCloseKeyPressed

    private void jbtnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreviewActionPerformed
        if(validateForm()) {
            jbtnPreviewActionPerformedRoutine();
        }
    }//GEN-LAST:event_jbtnPreviewActionPerformed

    private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
        if(validateForm()) {
            JbtnViewActionPerformedRoutine();
        }
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            jbtnView.doClick();
            jbtnPreview.requestFocusInWindow();
        }
    }//GEN-LAST:event_jbtnViewKeyPressed

    private void jtxtGroupNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGroupNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtGroupNameFocusGained

    private void jtxtGroupNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGroupNameFocusLost
        groupPickList.setVisible(false);
    }//GEN-LAST:event_jtxtGroupNameFocusLost

    private void jtxtGroupNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupNameKeyPressed
        groupPickList.setLocation(jPanel3.getX() + jtxtGroupName.getX(), jPanel3.getY() + jtxtGroupName.getY() + jtxtGroupName.getHeight());
        groupPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtGroupNameKeyPressed

    private void jtxtGroupNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupNameKeyReleased
        try {
            groupPickList.setReturnComponent(new JTextField[]{jtxtGroupName});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM group_master WHERE name LIKE '%"+ jtxtGroupName.getText().toUpperCase() +"%'");
            groupPickList.setPreparedStatement(pstLocal);
            groupPickList.setValidation(dataConnection.prepareStatement("SELECT * FROM group_master WHERE name = ?"));
            groupPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtGroupNameKeyReleased In Collection Report", ex);
        }
    }//GEN-LAST:event_jtxtGroupNameKeyReleased

    private void jtxtGroupNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupNameKeyTyped
        lb.fixLength(evt, 50);
    }//GEN-LAST:event_jtxtGroupNameKeyTyped

    private void jtxtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSearchKeyPressed
        lb.searchOnTextFields(jtxtSearch, rowSorter);
    }//GEN-LAST:event_jtxtSearchKeyPressed

    private void jtxtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSearchKeyReleased
        lb.searchOnTextFields(jtxtSearch, rowSorter);
    }//GEN-LAST:event_jtxtSearchKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnPreview;
    private javax.swing.JButton jbtnView;
    private javax.swing.JTextField jtxtGroupName;
    private javax.swing.JTextField jtxtSearch;
    private javax.swing.JLabel payrs;
    private javax.swing.JLabel recrs;
    // End of variables declaration//GEN-END:variables
}