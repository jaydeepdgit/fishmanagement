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
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import dhananistockmanagement.DeskFrame;
import dhananistockmanagement.HomePage;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import support.Constants;
import support.Library;
import support.OurDateChooser;
import support.PickList;

/**
 *
 * @author @JD@
 */
public class GeneralLedger extends javax.swing.JInternalFrame {
    Library lb = new Library();
    private DefaultTableModel dtm = null;
    Connection dataConnection = DeskFrame.connMpAdmin;
    PickList acPickListView = null;
    private CachedRowSet crsMain = null;
    private double opbRs = 0.00;
    String[] columnName = {"ref_no", "doc_date", "doc_type", "amt", "amt_drcr", "amt_crdr", "amtBal", "remark", "rate", "ac_name", "bill_no"};
    int[] datatype = new int[]{0, 0, 0, 2, 2, 2, 2, 0, 2, 0, 0};
    private Date fromDT;
    private String ac_cd = "";
    String Syspath = System.getProperty("user.dir");

    /**
     * Creates new form GeneralLedger
     */
    public GeneralLedger() {
        initComponents();
        lb.setDateChooserProperty(jtxtToDate);
        initOtherComponents();
    }

    public GeneralLedger(String ac_name) {
        initComponents();
        jtxtAcName.setText(ac_name);
        initOtherComponents();
        jbtnView.doClick();
    }

    public GeneralLedger(String ac_name, String mnth) {
        initComponents();
        jtxtAcName.setText(ac_name);
        jtxtFromDate.setText("01/"+ mnth +"/"+ Calendar.getInstance().get(Calendar.YEAR));
        jtxtToDate.setText("31/"+ mnth +"/"+ Calendar.getInstance().get(Calendar.YEAR));
        initOtherComponents();
        jbtnView.doClick();
        setTitle(Constants.GENERAL_LEDGER_FORM_NAME);
    }

    private void initOtherComponents() {
        acPickListView = new PickList(dataConnection);
        setPickListView();
        dtm = (DefaultTableModel) jTable1.getModel();
        registerShortKeys();
        setPermission();
        setIconToPanel();
        setcellRender();
        jTable1.setBackground(new Color(253, 243, 243));
    }

    private void setcellRender() {
        jTable1.getColumnModel().getColumn(4).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.getColumnModel().getColumn(5).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.getColumnModel().getColumn(6).setCellRenderer(new StatusColumnCellRenderer());
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
                if(!isSelected) {
                    l.setForeground(Color.BLUE);
                }
                l.setFont(l.getFont().deriveFont(Font.BOLD));
            } else if (col == 4) {
                if(!isSelected) {
                    l.setForeground(Color.RED);
                }
                l.setFont(l.getFont().deriveFont(Font.BOLD));
            } else if(col == 5) {
                double val=Double.parseDouble(lb.getDeCustomFormat(tableModel.getValueAt(row,col).toString()));
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

    public void setStartupFocus() {
        jtxtAcName.requestFocusInWindow();
    }

    private void setPermission() {
        lb.setUserRightsToButton(jbtnPreview, Constants.GENERAL_LEDGER_FORM_ID, "PRINT");
        lb.setUserRightsToButton(jbtnView, Constants.GENERAL_LEDGER_FORM_ID, "VIEWS");
    }

    private void setIconToPanel() {
        Syspath += File.separator + "Resources" + File.separator + "Images" + File.separator;
        jbtnView.setIcon(new ImageIcon(Syspath + "view.png"));
        jbtnPreview.setIcon(new ImageIcon(Syspath + "preview.png"));
        jbtnClose.setIcon(new ImageIcon(Syspath + "close.png"));
    }

    private void setPickListView() {
        acPickListView.setLayer(getLayeredPane());
        acPickListView.setPickListComponent(jtxtAcName);
        acPickListView.setNextComponent(jtxtFromDate);
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
            lb.printToLogFile("Exception at dispose In General Ledger", ex);
        }
    }

    private void makePrewviousBal() {
        String fromDate = jtxtFromDate.getText();
        String sql = "";
        try {
            opbRs = 0.00;

            sql = "SELECT drcr, val FROM oldb2_2 WHERE (doc_date < '"+ lb.ConvertDateFormetForDB(fromDate) +"' OR doc_date IS NULL)";
            if (!jtxtAcName.getText().equalsIgnoreCase("All Accounts")) {
                sql += " AND ac_cd = '"+ ac_cd +"'";
            }
            sql += " AND (1 = 1 ";
            if(jcmbCash.isSelected()) {
                sql += " AND doc_ref_no like 'CP%' OR doc_ref_no like 'CR%' OR doc_ref_no like 'VCP%' OR doc_ref_no like 'VCR%'";
            }
            if(jcmbBank.isSelected()) {
                sql += " OR doc_ref_no like 'BP%' OR doc_ref_no like 'BR%' OR doc_ref_no like 'VBP%' OR doc_ref_no like 'VBR%'";
            }
            sql += ")";
            PreparedStatement pstlocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = pstlocal.executeQuery();
            while (rsLocal.next()) {
                if (rsLocal.getString("drcr").equalsIgnoreCase("0")) {
                    opbRs += rsLocal.getDouble("val");
                } else {
                    opbRs -= rsLocal.getDouble("val");
                }
            }
        } catch (SQLException ex) {
            lb.printToLogFile("Exception at makePrewviousBal In General Ledger", ex);
        }
    }

    private void makeQuery() throws SQLException, Exception {
        String fromDate = jtxtFromDate.getText();
        String toDate = jtxtToDate.getText();
        String sql = "SELECT doc_ref_no, doc_cd, doc_date, drcr, val AS val, particular, opp_ac_cd "
            + " FROM oldb2_2 WHERE ((doc_date >= '"+ lb.ConvertDateFormetForDB(fromDate) +"' AND "
            + " doc_date <= '"+ lb.ConvertDateFormetForDB(toDate) +"') OR doc_date IS NULL) AND val <> 0";
        if (!jtxtAcName.getText().equalsIgnoreCase("All Accounts")) {
            sql += " AND ac_cd='"+ ac_cd +"' AND doc_date IS NOT NULL";
        }
        sql += " AND (1 = 1 ";
        if(jcmbCash.isSelected()) {
            sql += " AND doc_ref_no like 'CP%' OR doc_ref_no like 'CR%' OR doc_ref_no like 'VCP%' OR doc_ref_no like 'VCR%'";
        }
        if(jcmbBank.isSelected()) {
            sql += " OR doc_ref_no like 'BP%' OR doc_ref_no like 'BR%' OR doc_ref_no like 'VBP%' OR doc_ref_no like 'VBR%'";
        }
        sql += ") ORDER BY doc_date, fix_time";
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
        data[10] = "";
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
                    data[10] = "";
                    if(oldRefNO.startsWith(Constants.SALES_BILL_INITIAL)) {
                        data[10] = lb.getField("SELECT bill_no FROM sale_bill_head WHERE ref_no='"+ oldRefNO +"'", dataConnection);
                    }
                    if(oldRefNO.startsWith(Constants.PURCHASE_BILL_INITIAL)) {
                        data[10] = lb.getField("SELECT bill_no FROM purchase_bill_head WHERE ref_no='"+ oldRefNO +"'", dataConnection);
                    }
                    if(data[10].equalsIgnoreCase("")) {
                        data[10] = oldRefNO;
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
                data[10] = "";
                if(newrefNO.startsWith(Constants.SALES_BILL_INITIAL)) {
                    data[10] = lb.getField("SELECT bill_no FROM sale_bill_head WHERE ref_no='" + newrefNO + "'", dataConnection);
                }
                if(newrefNO.startsWith(Constants.PURCHASE_BILL_INITIAL)) {
                    data[10] = lb.getField("SELECT bill_no FROM purchase_bill_head WHERE ref_no='" + newrefNO + "'", dataConnection);
                }
                if(data[10].equalsIgnoreCase("")) {
                    data[10] = newrefNO;
                }
            }
            oldRefNO = newrefNO;
            oldac_name = newac_name;
            oldparticular = newparticular;
        }
        if (flag) {
            opbRs += rs;
            lb.appendColumnToCacheRowSet(crsMain, data, datatype);
        }
    }

    private void jbtnViewActionPerformedRoutine() {
        opbRs = 0.00;
        if(jcmbOPB.isSelected()) {
            makePrewviousBal();
        }
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
                row.add(crsMain.getString(11)); // Bill No
                row.add(crsMain.getString(2)); // doc_date
                row.add(crsMain.getString(3)); // doc_type
                row.add(lb.getIndianFormat(crsMain.getDouble(5))); // amt_drcr
                row.add(lb.getIndianFormat(Math.abs(crsMain.getDouble(6)))); // amt_crdr
                row.add(lb.getIndianFormat(crsMain.getDouble(7))); // amtBal
                row.add(crsMain.getString(8)); // remark
                row.add(crsMain.getString(9)); // rate
                dtm.addRow(row);
                closingBal = crsMain.getDouble(7);
                jlblClosingBal.setText(lb.getIndianFormat(closingBal));
            }
            if(closingBal >= 0) {
                jlblClosingBal.setForeground(Color.BLUE);
            } else {
                jlblClosingBal.setForeground(Color.RED);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jbtnViewActionPerformedRoutine In General Ledger", ex);
        }
    }

    private void tableViewOnPanel1MouseClickedRoutine(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();
            lb.quickOpen(jTable1.getValueAt(row, 0).toString());
        }
    }

    private void jbtnPreviewActionPerformedRoutine() {
        try {
            opbRs = 0.00;
            if(jcmbOPB.isSelected()) {
                makePrewviousBal();
            }
            makeQuery();
            crsMain.beforeFirst();
            HashMap params = new HashMap();
            params.put("acName", jtxtAcName.getText());
            params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
            params.put("cadd1", DeskFrame.clSysEnv.getADD1());
            params.put("cadd2", DeskFrame.clSysEnv.getADD2());
            params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
            params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
            params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
            params.put("fromDate", jtxtFromDate.getText());
            params.put("toDate", jtxtToDate.getText());
            params.put("dir", System.getProperty("user.dir"));
            params.put("Cash_Bal", lb.getIndianFormat(opbRs));
            params.put("DR/CR", DeskFrame.drName + "\\" + DeskFrame.crName);
            params.put("digit", lb.getDigit());
            JRResultSetDataSource dataSource = new JRResultSetDataSource(crsMain);
            JasperPrint print = null;
            if(lb.isExist("SELECT fk_group_id FROM account_master WHERE name = '"+ jtxtAcName.getText() +"' AND fk_group_id IN (SELECT id FROM group_master WHERE head = 0 AND (id = 'G000012' OR id = 'G000013'))", dataConnection)) {
                print = JasperFillManager.fillReport(System.getProperty("user.dir") + File.separatorChar + "Reports"+ File.separatorChar +"GeneralLedgerCash.jasper", params, dataSource);
            } else {
                print = JasperFillManager.fillReport(System.getProperty("user.dir") + File.separatorChar + "Reports"+ File.separatorChar +"GeneralLedger.jasper", params, dataSource);
            }
            jPanel1.removeAll();
            JRViewer jrViewer = new JRViewer(print);
            jrViewer.setZoomRatio(0.9113f);
            jPanel1.add(jrViewer);
            SwingUtilities.updateComponentTreeUI(jPanel1);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jbtnPreviewActionPerformedRoutine In General Ledger", ex);
        }
    }

    private boolean validateData() {
        ac_cd = lb.getAcountCode(jtxtAcName.getText(), "C");
        if(ac_cd.equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, Constants.INVALID_ACCOUNT, DeskFrame.TITLE, JOptionPane.WARNING_MESSAGE);
            jtxtAcName.requestFocusInWindow();
            return false;
        }
        try {
            fromDT = lb.userFormat.parse(jtxtFromDate.getText());
            lb.userFormat.parse(jtxtToDate.getText());
            lb.dbFormat.parse(lb.getAcountCode(ac_cd, "DT"));

            jtxtFromDate.setText(lb.userFormat.format(fromDT));
        } catch(Exception ex){
            lb.printToLogFile("Error at vaidateData In General Ledger", ex);
            return false;
        }
        return true;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jtxtFromDate = new javax.swing.JTextField();
        jBillDateBtn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jtxtToDate = new javax.swing.JTextField();
        jBillDateBtn1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtAcName = new javax.swing.JTextField();
        jbtnView = new javax.swing.JButton();
        jbtnPreview = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();
        jcmbOPB = new javax.swing.JCheckBox();
        jcmbCash = new javax.swing.JCheckBox();
        jcmbBank = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jlblClosingBal = new javax.swing.JLabel();
        jlblTotal = new javax.swing.JLabel();

        setBackground(new java.awt.Color(211, 226, 245));

        jPanel2.setBackground(new java.awt.Color(253, 243, 243));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)), "Details", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 16), new java.awt.Color(0, 0, 255))); // NOI18N

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel3.setText("From");

        jtxtFromDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtFromDate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtFromDate.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtFromDate.setPreferredSize(new java.awt.Dimension(2, 25));
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

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("Ac Name");

        jtxtAcName.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtAcName.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtAcName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAcNameFocusGained(evt);
            }
        });
        jtxtAcName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAcNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtAcNameKeyReleased(evt);
            }
        });

        jbtnView.setBackground(new java.awt.Color(204, 255, 204));
        jbtnView.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnView.setForeground(new java.awt.Color(235, 35, 35));
        jbtnView.setMnemonic('V');
        jbtnView.setText("VIEW RESULT");
        jbtnView.setMaximumSize(new java.awt.Dimension(87, 25));
        jbtnView.setMinimumSize(new java.awt.Dimension(87, 25));
        jbtnView.setPreferredSize(new java.awt.Dimension(87, 25));
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

        jcmbOPB.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jcmbOPB.setSelected(true);
        jcmbOPB.setText("OPB");

        jcmbCash.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jcmbCash.setText("Cash");

        jcmbBank.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jcmbBank.setText("Bank");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jtxtAcName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jcmbCash)
                        .addGap(18, 18, 18)
                        .addComponent(jcmbBank)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(205, 205, 205)
                        .addComponent(jcmbOPB)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnPreview, jbtnView});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel3});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addComponent(jtxtAcName)
                    .addComponent(jbtnView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtnPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcmbOPB)
                    .addComponent(jcmbBank, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbCash)
                    .addComponent(jBillDateBtn1, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jtxtToDate)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBillDateBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jbtnClose, jbtnPreview, jbtnView, jtxtAcName});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jBillDateBtn1, jLabel3, jLabel4, jcmbBank, jcmbCash, jcmbOPB, jtxtFromDate, jtxtToDate});

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(53, 154, 141)));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setBackground(new java.awt.Color(253, 243, 243));
        jScrollPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(4, 110, 152), 1, true));

        jTable1.setBackground(new java.awt.Color(253, 243, 243));
        jTable1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Doc No", "Doc No", "DOC Date", "Doc Type", "R DR", "R CR", "Bal RS", "Remark", "Rate"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(23);
        jTable1.getTableHeader().setReorderingAllowed(false);
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
            jTable1.getColumnModel().getColumn(7).setMinWidth(0);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(7).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(8).setMinWidth(0);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(8).setMaxWidth(0);
        }

        jPanel1.add(jScrollPane1);

        jlblClosingBal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlblClosingBal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblClosingBal.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(235, 35, 35)));

        jlblTotal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblTotal.setText("CLOSING BAL : ");
        jlblTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jlblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblClosingBal, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlblClosingBal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jtxtAcNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcNameKeyPressed
        acPickListView.setLocation(jtxtAcName.getX() + jPanel2.getX(), jPanel2.getY() + jtxtAcName.getY() + jtxtAcName.getHeight());
        acPickListView.pickListKeyPress(evt);
        acPickListView.setReturnComponent(new JTextField[]{jtxtAcName});
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                evt.consume();
                jtxtFromDate.setText((lb.getAcountCode(lb.getAcountCode(jtxtAcName.getText(), "C"), "DT")==null)? HomePage.jlblDate.getText():lb.ConvertDateFormetForDBForConcurrency(lb.getAcountCode(lb.getAcountCode(jtxtAcName.getText(), "C"), "DT")));
            } catch (Exception ex) {
                Logger.getLogger(GeneralLedger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jtxtAcNameKeyPressed

    private void jtxtAcNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAcNameKeyReleased
        try {
            acPickListView.setReturnComponent(new JTextField[]{jtxtAcName});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM account_master WHERE name LIKE '%" + jtxtAcName.getText().toUpperCase() + "%'");
            acPickListView.setPreparedStatement(pstLocal);
            acPickListView.setValidation(dataConnection.prepareStatement("SELECT * FROM account_master WHERE name = ?"));
            acPickListView.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtAcNameKeyReleased In General Ledger", ex);
        }
    }//GEN-LAST:event_jtxtAcNameKeyReleased

    private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
        if(validateData()) {
            jbtnViewActionPerformedRoutine();
        }
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnView.doClick();
            jbtnPreview.requestFocusInWindow();
        }
    }//GEN-LAST:event_jbtnViewKeyPressed

    private void jbtnPreviewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPreviewKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnPreview.doClick();
        }
    }//GEN-LAST:event_jbtnPreviewKeyPressed

    private void jbtnCloseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnCloseKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnClose.doClick();
        }
    }//GEN-LAST:event_jbtnCloseKeyPressed

    private void jbtnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreviewActionPerformed
        if(validateData()) {
            jbtnPreviewActionPerformedRoutine();
        }
    }//GEN-LAST:event_jbtnPreviewActionPerformed

    private void jtxtAcNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAcNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAcNameFocusGained

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
        lb.setDateUsingJTextField(jtxtFromDate, jbtnView);
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

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        tableViewOnPanel1MouseClickedRoutine(evt);
    }//GEN-LAST:event_jTable1MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JButton jBillDateBtn1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnPreview;
    private javax.swing.JButton jbtnView;
    private javax.swing.JCheckBox jcmbBank;
    private javax.swing.JCheckBox jcmbCash;
    private javax.swing.JCheckBox jcmbOPB;
    private javax.swing.JLabel jlblClosingBal;
    private javax.swing.JLabel jlblTotal;
    private javax.swing.JTextField jtxtAcName;
    private javax.swing.JTextField jtxtFromDate;
    private javax.swing.JTextField jtxtToDate;
    // End of variables declaration//GEN-END:variables
}