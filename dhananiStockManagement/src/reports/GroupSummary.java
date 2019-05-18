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
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import dhananistockmanagement.DeskFrame;
import dhananistockmanagement.MainClass;
import support.Constants;
import support.Library;
import support.PickList;

/**
 *
 * @author @JD@
 */
public class GroupSummary extends javax.swing.JInternalFrame {
    Library lb = new Library();
    private DefaultTableModel dtm = null;
    Connection dataConnection = DeskFrame.connMpAdmin;
    PickList grpPickListView = null;
    ResultSet viewDataRS = null;
    private TableRowSorter<TableModel> rowSorter;
    String Syspath = System.getProperty("user.dir");

    /**
     * Creates new form GroupSummary
     */
    public GroupSummary() {
        initComponents();
        grpPickListView = new PickList(dataConnection);
        dtm = (DefaultTableModel) jTable1.getModel();
        setPickListView();
        registerShortKeys();
        setIconToPnael();

        jTable1.getColumnModel().getColumn(1).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.getColumnModel().getColumn(2).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.getColumnModel().getColumn(3).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.getColumnModel().getColumn(4).setCellRenderer(new StatusColumnCellRenderer());
        rowSorter = new TableRowSorter<>(jTable1.getModel());
        jTable1.setRowSorter(rowSorter);
        jTable1.setBackground(new Color(253, 243, 243));
        setPermission();
        setTitle(Constants.GROUP_SUMMARY_FORM_NAME);
    }

    public class StatusColumnCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            //Cells are by default rendered as a JLabel.
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            l.setHorizontalAlignment(SwingConstants.RIGHT);
            // Get the status for the current row.
            if(col == 1 || col == 4) {
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
            } else if (col == 2) {
                if(!isSelected) {
                    l.setForeground(Color.RED);
                }
                l.setFont(l.getFont().deriveFont(Font.BOLD));
            } else if (col == 3) {
                if(!isSelected) {
                    l.setForeground(Color.BLUE);
                }
                l.setFont(l.getFont().deriveFont(Font.BOLD));
            }
            //Return the JLabel which renders the cell.
            return l;
        }
    }

    private void setPermission() {
        lb.setUserRightsToButton(jbtnPreview, Constants.GROUP_SUMMARY_FORM_ID, "PRINT");
        lb.setUserRightsToButton(jbtnView, Constants.GROUP_SUMMARY_FORM_ID, "VIEWS");
    }

    private void setIconToPnael() {
        Syspath += File.separator + "Resources" + File.separator + "Images" + File.separator;
        jbtnView.setIcon(new ImageIcon(Syspath + "view.png"));
        jbtnPreview.setIcon(new ImageIcon(Syspath + "preview.png"));
        jbtnClose.setIcon(new ImageIcon(Syspath + "close.png"));
    }

    private void setPickListView() {
        grpPickListView.setLayer(getLayeredPane());
        grpPickListView.setPickListComponent(jtxtGroupName);
        grpPickListView.setNextComponent(jbtnView);
    }

    private void registerShortKeys() {
        lb.setViewShortcut(this, jbtnView);
        lb.setPreviewShortcut(this, jbtnPreview);
        lb.setCloseShortcut(this, jbtnClose);
    }

    @Override
    public void dispose() {
        try {
            DeskFrame.removeFromScreen(DeskFrame.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose In Group Summary", ex);
        }
    }

    private void makeQuery() {
        try {
            String sql = null;
            PreparedStatement pstLocal = null;
            sql = "SELECT a.name as ac_name, opb, cr, dr, bal, o.ac_cd FROM oldb2_1 o, account_master a WHERE a.grp_cd = o.grp_cd ";
            if (!jtxtGroupName.getText().equalsIgnoreCase("")) {
                sql += "AND (o.grp_cd = '"+ lb.getGroupName(jtxtGroupName.getText(), "C") +"' OR o.grp_cd "
                +"IN(SELECT id FROM group_master WHERE head_grp = '"+ lb.getGroupName(jtxtGroupName.getText(), "C") +"')) ";
            }
            sql += "AND a.id = o.ac_cd AND bal <> 0 ORDER BY a.name";
            pstLocal = dataConnection.prepareStatement(sql);
            viewDataRS = pstLocal.executeQuery();
        } catch(Exception ex) {
            lb.printToLogFile("Exception at makeQuery at Group Summary", ex);
        }
    }

    private void jbtnViewActionPerformedRoutine() {
        makeQuery();
        try {
            double opb = 0.00, dr = 0.00, cr = 0.00, bal = 0.00;
            jPanel1.removeAll();
            jPanel1.add(jScrollPane1);
            dtm.setRowCount(0);
            while(viewDataRS.next()) {
                opb += viewDataRS.getDouble(2);
                dr += viewDataRS.getDouble(3);
                cr += viewDataRS.getDouble(4);
                bal += viewDataRS.getDouble(5);
                Vector row = new Vector();
                row.add(viewDataRS.getString(1));
                row.add(lb.getIndianFormat(viewDataRS.getDouble(2)));
                row.add(lb.getIndianFormat(viewDataRS.getDouble(3)));
                row.add(lb.getIndianFormat(viewDataRS.getDouble(4)));
                row.add(lb.getIndianFormat(viewDataRS.getDouble(5)));
                row.add(viewDataRS.getString(6));
                dtm.addRow(row);
            }
            jlblTotal.setText("TOTAL : ");
            jlblOPB.setText(lb.getIndianFormat(opb));
            if(opb >= 0) {
                jlblOPB.setForeground(Color.BLUE);
            } else {
                jlblOPB.setForeground(Color.RED);
            }
            jlblDR.setText(lb.getIndianFormat(dr));
            jlblDR.setForeground(Color.RED);
            jlblCR.setText(lb.getIndianFormat(cr));
            jlblCR.setForeground(Color.BLUE);
            jlblBal.setText(lb.getIndianFormat(bal));
            if(bal >= 0) {
                jlblBal.setForeground(Color.BLUE);
            } else {
                jlblBal.setForeground(Color.RED);
            }
        } catch(Exception ex) {
            lb.printToLogFile("Exception at jbtnViewActionPerformedRoutine in Group Summary", ex);
        }
    }

    private void jbtnPreviewActionPerformedRoutine() {
        try {
            makeQuery();
            HashMap params = new HashMap();
            params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
            params.put("cadd1", DeskFrame.clSysEnv.getADD1());
            params.put("cadd2", DeskFrame.clSysEnv.getADD2());
            params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
            params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
            params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
            params.put("fromDate", "");
            params.put("toDate", "");
            params.put("dir", System.getProperty("user.dir"));
            params.put("digit", lb.getDigit());
            params.put("ptype", Constants.GROUP_SUMMARY_FORM_NAME);
            JRResultSetDataSource dataSource = new JRResultSetDataSource(viewDataRS);
            JasperPrint print = JasperFillManager.fillReport(System.getProperty("user.dir") + File.separatorChar + "Reports"+ File.separatorChar +"GroupSummary.jasper", params, dataSource);
            jPanel1.removeAll();
            JRViewer jrViewer = new JRViewer(print);
            jrViewer.setZoomRatio(0.9113f);
            jPanel1.add(jrViewer);
            SwingUtilities.updateComponentTreeUI(jPanel1);
        } catch(Exception ex) {
            lb.printToLogFile("Exception at jbtnPreviewActionPerformedRoutine In Group Summary", ex);
        }
    }

    private void setTextfieldsAtBottom() {
        JComponent[] footer = new JComponent[]{jlblTotal, jlblOPB, jlblDR, jlblCR, jlblBal, null};
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtxtGroupName = new javax.swing.JTextField();
        jbtnView = new javax.swing.JButton();
        jbtnPreview = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();
        jlblTotal = new javax.swing.JLabel();
        jlblOPB = new javax.swing.JLabel();
        jlblCR = new javax.swing.JLabel();
        jlblBal = new javax.swing.JLabel();
        jlblDR = new javax.swing.JLabel();
        jtxtSearch = new javax.swing.JTextField();

        setBackground(new java.awt.Color(211, 226, 245));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(53, 154, 141)));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jTable1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Account", "Opening", "CR", "DR", "BAL", "AC_CD"
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
            jTable1.getColumnModel().getColumn(5).setMinWidth(0);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(5).setMaxWidth(0);
        }

        jPanel1.add(jScrollPane1);

        jPanel2.setBackground(new java.awt.Color(253, 243, 243));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)), "Details", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 16), new java.awt.Color(0, 0, 255))); // NOI18N

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel1.setText("Group Name");

        jtxtGroupName.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtGroupName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtGroupName.setPreferredSize(new java.awt.Dimension(221, 20));
        jtxtGroupName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtGroupNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtGroupNameKeyReleased(evt);
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtGroupName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnPreview, jbtnView});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtnPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtGroupName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                .addGap(7, 7, 7))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jbtnClose, jbtnPreview, jbtnView, jtxtGroupName});

        jlblTotal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblTotal.setText("TOTAL : ");
        jlblTotal.setMaximumSize(new java.awt.Dimension(34, 25));
        jlblTotal.setMinimumSize(new java.awt.Dimension(34, 25));
        jlblTotal.setPreferredSize(new java.awt.Dimension(34, 25));

        jlblOPB.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlblOPB.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblOPB.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(235, 35, 35)));
        jlblOPB.setMaximumSize(new java.awt.Dimension(34, 25));
        jlblOPB.setMinimumSize(new java.awt.Dimension(34, 25));
        jlblOPB.setPreferredSize(new java.awt.Dimension(34, 25));

        jlblCR.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlblCR.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblCR.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(235, 35, 35)));
        jlblCR.setMaximumSize(new java.awt.Dimension(34, 25));
        jlblCR.setMinimumSize(new java.awt.Dimension(34, 25));
        jlblCR.setPreferredSize(new java.awt.Dimension(34, 25));

        jlblBal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlblBal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblBal.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(235, 35, 35)));
        jlblBal.setMaximumSize(new java.awt.Dimension(34, 25));
        jlblBal.setMinimumSize(new java.awt.Dimension(34, 25));
        jlblBal.setPreferredSize(new java.awt.Dimension(34, 25));

        jlblDR.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlblDR.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblDR.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(235, 35, 35)));
        jlblDR.setMaximumSize(new java.awt.Dimension(34, 25));
        jlblDR.setMinimumSize(new java.awt.Dimension(34, 25));
        jlblDR.setPreferredSize(new java.awt.Dimension(34, 25));

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
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jlblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblOPB, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblDR, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblCR, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblBal, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jtxtSearch))
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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblBal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblCR, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblDR, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblOPB, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jlblBal, jlblCR, jlblDR, jlblOPB, jlblTotal});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtGroupNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupNameKeyPressed
        grpPickListView.setLocation(jtxtGroupName.getX() + jPanel2.getX(), jtxtGroupName.getY() + jtxtGroupName.getHeight() + jPanel2.getY());
        grpPickListView.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtGroupNameKeyPressed

    private void jtxtGroupNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupNameKeyReleased
        try {
            grpPickListView.setReturnComponent(new JTextField[]{jtxtGroupName});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM group_master WHERE name LIKE '%"+ jtxtGroupName.getText().toUpperCase() +"%'");
            grpPickListView.setPreparedStatement(pstLocal);
            grpPickListView.setValidation(dataConnection.prepareStatement("SELECT * FROM group_master WHERE name = ?"));
            grpPickListView.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtGroupNameKeyReleased In Group Summary", ex);
        }
    }//GEN-LAST:event_jtxtGroupNameKeyReleased

    private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
        jbtnViewActionPerformedRoutine();
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            jbtnView.doClick();
            jbtnPreview.requestFocusInWindow();
        }
    }//GEN-LAST:event_jbtnViewKeyPressed

    private void jbtnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreviewActionPerformed
        jbtnPreviewActionPerformedRoutine();
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

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_formComponentResized

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if(evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            if (MainClass.df.hasPermission(Constants.GENERAL_LEDGER_FORM_ID)) {
                lb.openGeneralLedgerPopUp(jTable1.getValueAt(row, 5).toString(), jtxtGroupName);
            } else {
                JOptionPane.showMessageDialog(null, Constants.NO_RIGHTS_TO_VIEW, Constants.GENERAL_LEDGER_FORM_NAME, JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jtxtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSearchKeyPressed
        lb.searchOnTextFields(jtxtSearch, rowSorter);
    }//GEN-LAST:event_jtxtSearchKeyPressed

    private void jtxtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSearchKeyReleased
        lb.searchOnTextFields(jtxtSearch, rowSorter);
    }//GEN-LAST:event_jtxtSearchKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnPreview;
    private javax.swing.JButton jbtnView;
    private javax.swing.JLabel jlblBal;
    private javax.swing.JLabel jlblCR;
    private javax.swing.JLabel jlblDR;
    private javax.swing.JLabel jlblOPB;
    private javax.swing.JLabel jlblTotal;
    private javax.swing.JTextField jtxtGroupName;
    private javax.swing.JTextField jtxtSearch;
    // End of variables declaration//GEN-END:variables
}