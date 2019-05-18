/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.sql.rowset.CachedRowSet;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import dhananistockmanagement.DeskFrame;
import support.Constants;
import support.Library;

/**
 *
 * @author @JD@
 */
public class GeneralLedgerPopUp extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    Library lb = new Library();
    Connection dataConnection = DeskFrame.connMpAdmin;
    DefaultTableModel dtm = null;
    private CachedRowSet crsMain = null;
    private double opbRs = 0.00;
    String[] columnName = {"ref_no", "doc_date", "doc_type", "amt", "amt_drcr", "amt_crdr", "amtBal", "remark", "rate", "ac_name"};
    int[] datatype = new int[]{0, 0, 0, 2, 2, 2, 2, 0, 2, 0};
    String ac_cd = "";
    String Syspath = System.getProperty("user.dir");

    /**
     * Creates new form GeneralLedgerPopUp
     */
    public GeneralLedgerPopUp(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // Close the dialog when Esc is pressed
        String cancelName = "cancel";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                doClose(RET_CANCEL);
            }
        });
        dtm = (DefaultTableModel) jTable1.getModel();
        jTable1.setBackground(new Color(253, 243, 243));
        Syspath += File.separator + "Resources" + File.separator + "Images" + File.separator;
        okButton.setIcon(new ImageIcon(Syspath + "view.png"));
        cancelButton.setIcon(new ImageIcon(Syspath + "close.png"));
        setcellRender();
    }

    private void setcellRender() {
        jTable1.getColumnModel().getColumn(3).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.getColumnModel().getColumn(4).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.getColumnModel().getColumn(5).setCellRenderer(new StatusColumnCellRenderer());
    }

    public class StatusColumnCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            //Cells are by default rendered as a JLabel.
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            l.setHorizontalAlignment(SwingConstants.RIGHT);
            //Get the status for the current row.
            TableModel tableModel = (TableModel) table.getModel();
            if (col == 3) {
                if(!isSelected){
                    l.setForeground(Color.BLUE);
                }
                l.setFont(l.getFont().deriveFont(Font.BOLD));
            } else if (col == 4) {
                if(!isSelected){
                    l.setForeground(Color.RED);
                }
                l.setFont(l.getFont().deriveFont(Font.BOLD));
            } else if(col == 5) {
                double val=Double.parseDouble(lb.getDeCustomFormat(tableModel.getValueAt(row,col).toString()));
                if(val >= 0) {
                    if(!isSelected){
                        l.setForeground(Color.BLUE);
                    }
                    l.setText(lb.getIndianFormat(val));
                } else {
                    if(!isSelected){
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

    private void makeQuery() throws SQLException, Exception {
        String sql = "SELECT doc_ref_no, doc_cd, doc_date, drcr, val AS val, particular, opp_ac_cd "
            +"FROM oldb2_2 WHERE val <> 0 AND ac_cd = '"+ ac_cd +"' AND doc_date IS NOT NULL "
            +"ORDER BY doc_date, fix_time";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();

        String dropb = "0.00", crOpb = "0.00";
        crsMain = lb.getBlankCachedRowSet(columnName, datatype);
        if(opbRs > 0) {
            dropb = lb.Convert2DecFmt(opbRs);
        } else {
            crOpb = lb.Convert2DecFmt(opbRs);
        }
        String data[] = new String[columnName.length];
        data[0] = "OPB/Prev";
        data[1] = Constants.OPB_INITIAL;
        data[2] = Constants.OPB_INITIAL;
        data[3] = "0.00";
        data[4] = dropb;
        data[5] = crOpb;
        data[6] = lb.Convert2DecFmt(opbRs);
        data[7] = "";
        data[8] = "0.00";
        data[9] = "";
        lb.appendColumnToCacheRowSet(crsMain, data, datatype);
        String oldRefNO = "", newrefNO = "", doc_cd = "", doc_date = "", oldac_name = "", newac_name = "";
        String oldparticular = "", newparticular = "";
        double rs = 0.00;
        boolean flag = false;
        double drRs = 0, crRs = 0;
        while (rsLocal.next()) {
            flag = true;
            newrefNO = rsLocal.getString("doc_ref_no");
            newac_name = rsLocal.getString("opp_ac_cd");
            newparticular = rsLocal.getString("particular");
            if ((!oldRefNO.equalsIgnoreCase(newrefNO)) && rsLocal.getRow() > 1 || !oldac_name.equalsIgnoreCase(newac_name)) {
                if(!oldRefNO.equalsIgnoreCase("")) {
                    data[0] = oldRefNO;
                    data[1] = doc_date;
                    data[2] = doc_cd;
                    data[3] = rs + "";
                    data[4] = drRs + "";
                    data[5] = crRs + "";
                    data[6] = opbRs + "";
                    data[7] = oldparticular;
                    data[8] = "";
                    data[9] = lb.getAccountMstName(oldac_name, "N");
                    if(data[7].equalsIgnoreCase("")) {
                        data[7] = oldparticular;
                    }
                    lb.appendColumnToCacheRowSet(crsMain, data, datatype);
                }
                rs = 0.00;
                drRs = 0;
                crRs = 0;
            }
            if (rsLocal.getString("drcr").equalsIgnoreCase("0")) {
                drRs += rsLocal.getDouble("val");
                rs += rsLocal.getDouble("val");
                opbRs += rsLocal.getDouble("val");
            } else {
                crRs -= rsLocal.getDouble("val");
                rs -= rsLocal.getDouble("val");
                opbRs -= rsLocal.getDouble("val");
            }
            doc_date = lb.ConvertDateFormetForDBForConcurrency(rsLocal.getString("doc_date"));
            doc_cd = rsLocal.getString("doc_cd");
            if(!newrefNO.equalsIgnoreCase("")) {
                data[0] = newrefNO;
                data[1] = doc_date;
                data[2] = doc_cd;
                data[3] = rs +"";
                data[4] = drRs +"";
                data[5] = crRs +"";
                data[6] = opbRs +"";
                data[7] = newparticular;
                data[8] = "0.00";
                data[9] = lb.getAccountMstName(newac_name, "N");
                if(data[7].equalsIgnoreCase("")) {
                    data[7] = newparticular;
                }
            }
            oldRefNO = newrefNO;
            oldac_name = newac_name;
            oldparticular = newparticular;
        }
        if(flag) {
            opbRs += rs;
            lb.appendColumnToCacheRowSet(crsMain, data, datatype);
        }
    }

    private void viewData() {
        jlblACCD.setText(ac_cd);
        jlblACName.setText(lb.getAccountMstName(ac_cd, "N"));
        try {
            makeQuery();
            double closingBal = 0.00;
            jPanel1.removeAll();
            jPanel1.add(jScrollPane1);
            crsMain.beforeFirst();
            dtm.setRowCount(0);
            while (crsMain.next()) {
                Vector row = new Vector();
                row.add(crsMain.getString(1)); // ref_no
                row.add(crsMain.getString(2)); // doc_date
                row.add(crsMain.getString(3)); // doc_type
                row.add(lb.getIndianFormat(crsMain.getDouble(5))); // amt_drcr
                row.add(lb.getIndianFormat(Math.abs(crsMain.getDouble(6)))); // amt_crdr
                row.add(lb.getIndianFormat(crsMain.getDouble(7))); // amtBal
                row.add(crsMain.getString(8)); // remark
                row.add(crsMain.getString(9)); // rate
                dtm.addRow(row);
                closingBal = crsMain.getDouble(7); // amtBal
                jlblClosingBal.setText(lb.getIndianFormat(closingBal));
            }
            if(closingBal >= 0) {
                jlblClosingBal.setForeground(Color.BLUE);
            } else {
                jlblClosingBal.setForeground(Color.RED);
            }
        } catch(Exception ex) {
            lb.printToLogFile("Exception at getData In General Ledger Pop Up.", ex);
        }
        EditCell(0, 1, true, false);
    }

    public void getData(String ac_cd) {
        this.ac_cd = ac_cd;
        viewData();
    }

    private void tableViewOnPanel1MouseClickedRoutine(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            cancelButton.doClick();
            lb.quickOpen(jTable1.getValueAt(row, 0).toString());
        }
    }

    private JTextField EditCell(int iRow, int iColumn, boolean bSelect, boolean bColor) {
        JTextField jTextFldEdit = null;
        try {
            if(iRow != -1 && iColumn != -1 && jTable1.isCellEditable(iRow, iColumn)) {
                jTable1.changeSelection(iRow, iColumn, false, false);
                jTable1.editCellAt(iRow, iColumn);
                //tableViewOnPanel.setValueAt(tableViewOnPanel.getValueAt(iRow, iColumn).toString().trim(), iRow, iColumn);
                DefaultCellEditor df = (DefaultCellEditor) jTable1.getCellEditor();

                JTextField jc = (JTextField) df.getComponent();
                jTextFldEdit = (JTextField) df.getTableCellEditorComponent(jTable1, jc.getText(), true, iRow, iColumn);

                jTextFldEdit.setText(jTextFldEdit.getText().trim());

                {
                    Color bg = Color.white;
                    jTextFldEdit.setBackground(bg);
                }
                jTextFldEdit.getCaret().setVisible(true);

                Highlighter hc = jTextFldEdit.getHighlighter();
                DefaultHighlighter.DefaultHighlightPainter high_painter = new DefaultHighlighter.DefaultHighlightPainter(new Color(198, 198, 250));
                hc.addHighlight(0, jTextFldEdit.getText().length(), high_painter);
                if (bSelect) {
                    jTextFldEdit.select(jTextFldEdit.getText().length(), jTextFldEdit.getText().length() - 1);
                    jTextFldEdit.selectAll();
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception in EditCell at Column:"+ iColumn +" and Row:"+ iRow +" ", ex);
        }
        return jTextFldEdit;
    }

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        jlblACName = new javax.swing.JLabel();
        jlblACCD = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jlblClosingBal = new javax.swing.JLabel();
        jlblTotal = new javax.swing.JLabel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        cancelButton.setBackground(new java.awt.Color(204, 255, 204));
        cancelButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cancelButton.setMnemonic('C');
        cancelButton.setText("CLOSE");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setBackground(new java.awt.Color(204, 255, 204));
        okButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        okButton.setText("VIEW");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jlblACName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jlblACName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jlblACCD.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jlblACCD.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setText("A/C CD : ");
        jLabel11.setMaximumSize(new java.awt.Dimension(59, 25));
        jLabel11.setMinimumSize(new java.awt.Dimension(59, 25));
        jLabel11.setPreferredSize(new java.awt.Dimension(59, 25));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("A/C NAME : ");
        jLabel4.setMaximumSize(new java.awt.Dimension(76, 25));
        jLabel4.setMinimumSize(new java.awt.Dimension(76, 25));
        jLabel4.setPreferredSize(new java.awt.Dimension(76, 25));

        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setFont(new java.awt.Font("Arial Unicode MS", 0, 12)); // NOI18N

        jTable1.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Doc No", "Doc Date", "Doc Type", "R DR", "R CR", "Bal Rs", "Remarks", "Rate"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
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
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(0);
        jTable1.getColumnModel().getColumn(6).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(0);
        jTable1.getColumnModel().getColumn(7).setMaxWidth(0);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jlblClosingBal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlblClosingBal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblClosingBal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jlblTotal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblTotal.setText("CLOSING BAL : ");
        jlblTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblACCD, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 302, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblACName, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblClosingBal, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblACCD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblACName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlblClosingBal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(okButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel11, jLabel4, jlblACCD, jlblACName});

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            doClose(RET_CANCEL);
            lb.quickOpen(jTable1.getValueAt(row, 0).toString());
        } else {
            JOptionPane.showMessageDialog(null, "Please Select row from Table to Modify");
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        tableViewOnPanel1MouseClickedRoutine(evt);
    }//GEN-LAST:event_jTable1MouseClicked

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTable1;
    private javax.swing.JLabel jlblACCD;
    private javax.swing.JLabel jlblACName;
    private javax.swing.JLabel jlblClosingBal;
    private javax.swing.JLabel jlblTotal;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;
}