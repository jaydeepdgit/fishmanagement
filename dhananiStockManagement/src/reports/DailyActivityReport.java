/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package reports;

import java.awt.Component;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.sql.rowset.CachedRowSet;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.swing.JRViewer;
import dhananistockmanagement.DeskFrame;
import support.CachedRowSetAdapter;
import support.Constants;
import support.Library;
import support.OurDateChooser;

/**
 *
 * @author @JD@
 */
public class DailyActivityReport extends javax.swing.JInternalFrame {
    Connection dataConnection = DeskFrame.connMpAdmin;
    Library lb = new Library();
    private ResultSet viewDataRs = null;
    private CachedRowSet crsMain = null, crsMain2 = null;
    private CachedRowSetAdapter crsa = new CachedRowSetAdapter();
    private String[] colName = {"doc_date", "doc_ref_no", "doc_nm", "itm_name", "ac_cd", "unt_name", "issue", "reciept", "bal"};
    private int[] colType = {0, 0, 0, 0, 0, 0, 2, 2, 2};
    String[] columnName = {"ref_no", "doc_date", "doc_type", "amt", "amt_drcr", "amt_crdr", "amtbal", "remark", "rate", "ac_cd", "ac_nm"};
    int[] datatype = new int[]{0, 0, 0, 2, 2, 2, 2, 0, 2, 0, 0};
    JasperReport report = null;
    String Syspath = System.getProperty("user.dir");

    /**
     * Creates new form DailyActivityReport
     */
    public DailyActivityReport() {
        initComponents();
        lb.setDateChooserProperty(jtxtToDate);
        lb.setDateChooserProperty(jtxtFromDate);
        registerShortKeys();
        setIconToPnael();
        setPermission();
        setCompVisible(false);
        addValidation();
        setTitle(Constants.DAILY_ACTIVITY_REPORT_FORM_NAME);
    }

    private void addValidation() {
        FieldValidation valid = new FieldValidation();
        jtxtFromDate.setInputVerifier(valid);
        jtxtToDate.setInputVerifier(valid);
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
        if (comp == jtxtFromDate) {
            if (!lb.checkDate(jtxtFromDate)) {
                lb.setDateChooserProperty(jtxtFromDate);
            }
            if (!lb.checkDate2(jtxtFromDate)) {
                lb.setDateChooserProperty(jtxtFromDate);
            }
        }
        if (comp == jtxtToDate) {
            if (!lb.checkDate(jtxtToDate)) {
                lb.setDateChooserProperty(jtxtToDate);
            }
            if (!lb.checkDate2(jtxtToDate)) {
                lb.setDateChooserProperty(jtxtToDate);
            }
        }
        return true;
    }

    private void setCompVisible(boolean flag) {
        jlblOpb.setVisible(flag);
        jLabel5.setVisible(flag);
        jlbClosingBal.setVisible(flag);
        jLabel1.setVisible(flag);
    }

    public void setStartupFocus() {
        jtxtFromDate.requestFocusInWindow();
    }

    private void setPermission() {
        lb.setUserRightsToButton(jbtnPreview, Constants.DAILY_ACTIVITY_REPORT_FORM_ID, "PRINT");
    }

    private void setIconToPnael() {
        Syspath += File.separator + "Resources" + File.separator + "Images" + File.separator;
        jbtnPreview.setIcon(new ImageIcon(Syspath + "preview.png"));
        jbtnClose.setIcon(new ImageIcon(Syspath + "close.png"));
    }

    @Override
    public void dispose() {
        try {
            DeskFrame.removeFromScreen(DeskFrame.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose In Daily Activity Report", ex);
        }
    }

    private void registerShortKeys() {
        lb.setPreviewShortcut(this, jbtnPreview);
        lb.setCloseShortcut(this, jbtnClose);
    }

    private void jbtnPrintPreviewActionPerformed() {
        HashMap params = new HashMap();
        try {
            params.put("ItemName", "");
            params.put("FromDate", jtxtFromDate.getText());
            params.put("ToDate", jtxtToDate.getText());
            params.put("OPB", jlblOpb.getText());
            params.put("CLB", jlbClosingBal.getText());
            params.put("dir", System.getProperty("user.dir"));
            params.put("digit", lb.getDigit());
            params.put("ac_type", "ACCOUNT ");
            params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
            params.put("cadd1", DeskFrame.clSysEnv.getADD1());
            params.put("cadd2", DeskFrame.clSysEnv.getADD2());
            params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
            params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
            params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
            JRResultSetDataSource dataSource1 = new JRResultSetDataSource(getItemDetails());
            JRResultSetDataSource dataSource2 = new JRResultSetDataSource(getCashDetails("G000012"));
            JRResultSetDataSource dataSource3 = new JRResultSetDataSource(getCashDetails("G000013"));
            JRResultSetDataSource dataSource = null;
            params.put("itmrst", dataSource1);
            params.put("cashrst", dataSource2);
            params.put("bankrst", dataSource3);
            params.put("cash", "CASH ACCOUNT");
            params.put("bank", "BANK ACCOUNT");
            JasperPrint print = JasperFillManager.fillReport(System.getProperty("user.dir") + File.separatorChar + "Reports"+ File.separatorChar +"DailyActivity.jasper", params, dataSource);
            jPanel1.removeAll();
            JRViewer jrViewer = new JRViewer(print);
            jrViewer.setZoomRatio(0.842f);
            jPanel1.add(jrViewer);

            SwingUtilities.updateComponentTreeUI(jPanel1);
        } catch(Exception ex) {
            lb.printToLogFile("Exception at jbtnPrintPreviewActionPerformed In Daily Activity Report", ex);
        }
    }

    private CachedRowSet getItemDetails() {
        String sql = "";
        sql = "SELECT doc_date, doc_ref_no, doc_nm, i.itm_name, u.unt_name, "+
            "(CASE WHEN o.ac_cd='' THEN '' ELSE (SELECT ac_name FROM acnt_mst WHERE ac_cd = o.ac_cd) END) AS ac_cd, "+
            "(CASE WHEN(o.trns_cd='I') THEN (o.pcs) ELSE(0) END) AS issue, "+
            "(CASE WHEN(o.trns_cd='R' OR o.trns_cd='o') THEN (o.pcs) ELSE(0) END) AS receipt, 0.00 AS bal "+
            "FROM oldb0_2 o, itm_mst i, unt_mst u "+
            "WHERE o.itm_cd = i.itm_cd AND u.unt_cd = o.unt_cd AND o.pcs <> 0 ";
        String strCriteria = " AND o.doc_date >= '"+ lb.ConvertDateFormetForDB(jtxtFromDate.getText()) +"'";
        strCriteria += " AND o.doc_date <= '"+ lb.ConvertDateFormetForDB(jtxtToDate.getText()) +"' ";
        sql += strCriteria + " ORDER BY itm_name, doc_date, trns_cd DESC";
        try {
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            viewDataRs = pstLocal.executeQuery();
            crsMain = crsa.getCachedResultSet(viewDataRs);

            String prd = "";

            crsMain2 = lb.getBlankCachedRowSet(colName, colType);
            String data[] = new String[colName.length];
            data[0] = jtxtFromDate.getText();
            data[1] = Constants.OPB_INITIAL;
            data[2] = "Opening";
            data[3] = "";
            data[4] = "";
            data[5] = "";
            data[6] = "0.00";
            data[7] = "0.00";
            double opb = lb.getOpeningStock("", "pcs", jtxtFromDate.getText());
            if(opb < 0) {
                data[6] = Math.abs(opb) + "";
            } else {
                data[7] = opb+"";
            }
            data[8] = "0";
            lb.appendColumnToCacheRowSet(crsMain2, data, colType);
            while(crsMain.next()) {
                for(int i = 0; i < data.length; i++) {
                    data[i]="";
                }
                if(!prd.equalsIgnoreCase(crsMain.getString("itm_name"))) {
                    prd = crsMain.getString("itm_name");
                }
                data[0] = lb.userFormat.format(crsMain.getDate("doc_date"));
                data[1] = crsMain.getString("doc_ref_no");
                data[2] = lb.getBookName(crsMain.getString("doc_nm"));
                data[3] = crsMain.getString("itm_name");
                data[4] = crsMain.getString("ac_cd");
                data[5] = crsMain.getString("unt_name");
                data[6] = crsMain.getString("issue");
                data[7] = crsMain.getString("receipt");
                data[8] = "0";
                lb.appendColumnToCacheRowSet(crsMain2, data, colType);
            }
            crsMain2.beforeFirst();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at getItemDetails In Daily Activity Report", ex);
        }
        return crsMain2;
    }

    private CachedRowSet getCashDetails(String group) throws SQLException, Exception {
        String fromDate = jtxtFromDate.getText();
        String toDate = jtxtToDate.getText();
        String sql = "SELECT doc_ref_no, doc_cd, doc_date, drcr, val AS val, particular, a.ac_name, o.ac_cd " +
            "FROM oldb2_2 o, acnt_mst a WHERE o.ac_cd = a.ac_cd AND ((doc_date >= '"+ lb.ConvertDateFormetForDB(fromDate) +"' AND "+
            "doc_date <= '"+ lb.ConvertDateFormetForDB(toDate) +"') OR doc_date is null) AND val <> 0 "+
            "AND o.ac_cd IN (SELECT ac_cd FROM acnt_mst WHERE grp_cd IN (SELECT grp_cd FROM group_mst " +
            "WHERE grp_cd = '"+ group +"' OR head_grp = '"+ group +"'))";
        sql += " ORDER BY ac_name, doc_date, doc_ref_no";
        PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
        ResultSet rsLocal = pstLocal.executeQuery();

        crsMain = lb.getBlankCachedRowSet(columnName, datatype);
        String data[] = new String[columnName.length];

        data[0] = Constants.OPB_INITIAL;
        data[1] = jtxtFromDate.getText();
        data[2] = Constants.OPB_INITIAL;
        data[3] = "0.00";
        double opb = getCashOpening(lb.getAccountMstName(group, Constants.GROUP_MASTER_INITIAL));
        if(opb >= 0) {
            data[4] = opb + "";
            data[5] = "0.00";
        } else {
            data[4] = "0.00";
            data[5] = Math.abs(opb) + "";
        }
        data[6] = opb + "";
        data[7] = "";
        data[8] = "0.00";
        data[9] = lb.getAccountMstName(group, "GN");
        data[10] = "";
        lb.appendColumnToCacheRowSet(crsMain, data, datatype);
        while (rsLocal.next()) {
            data[0] = rsLocal.getString("doc_ref_no");
            data[1] = lb.userFormat.format(rsLocal.getDate("doc_date"));
            data[2] = rsLocal.getString("doc_cd");
            data[3] = "0.00";
            if (rsLocal.getInt("drcr") == 0) {
                data[4] = rsLocal.getString("val");
                data[5] = "0.00";
            } else {
                data[4] = "0.00";
                data[5] = rsLocal.getString("val");
            }
            data[6] = rsLocal.getString("val");
            data[7] = rsLocal.getString("particular");
            data[8] = "0.00";
            data[9] = rsLocal.getString("ac_name");
            data[10] = lb.getAccountName(rsLocal.getDouble("val"), "ac_name", rsLocal.getString("doc_cd"), rsLocal.getString("doc_ref_no"));
            lb.appendColumnToCacheRowSet(crsMain, data, datatype);
        }

        crsMain.beforeFirst();
        while(crsMain.next()) {
            if ((crsMain.getDouble("amt_crdr") + crsMain.getDouble("amt_drcr")) == 0) {
                crsMain.deleteRow();
            }
        }
        crsMain.beforeFirst();
        return crsMain;
    }

    private double getCashOpening(String ac_cd) {
        String fromDate = jtxtFromDate.getText();
        String sql = "SELECT drcr, val FROM oldb2_2 WHERE (doc_date < '"+ lb.ConvertDateFormetForDB(fromDate) +"' OR doc_date IS NULL) AND ac_cd = '"+ ac_cd +"'";
        double opbRs = 0.00;
        try {
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
            lb.printToLogFile("Exception at getCashOpening In Daily Activity Report", ex);
        }
        return opbRs;
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
        jLabel1 = new javax.swing.JLabel();
        jlbClosingBal = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jlblOpb = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jbtnClose = new javax.swing.JButton();
        jbtnPreview = new javax.swing.JButton();
        jBillDateBtn1 = new javax.swing.JButton();
        jtxtToDate = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jBillDateBtn = new javax.swing.JButton();
        jtxtFromDate = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)));
        jPanel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("CLOSING STOCK");

        jlbClosingBal.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlbClosingBal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlbClosingBal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jLabel5.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("OPB");

        jlblOpb.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlblOpb.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlblOpb.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

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

        jBillDateBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn1ActionPerformed(evt);
            }
        });

        jtxtToDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtToDate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtToDate.setMinimumSize(new java.awt.Dimension(6, 25));
        jtxtToDate.setPreferredSize(new java.awt.Dimension(6, 25));
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

        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel4.setText("To");

        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });

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

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel3.setText("From");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnPreview});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtnPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBillDateBtn1, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jtxtToDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBillDateBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                    .addComponent(jtxtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jBillDateBtn1, jLabel3, jLabel4, jbtnClose, jbtnPreview, jtxtFromDate, jtxtToDate});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblOpb, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlbClosingBal, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblOpb, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbClosingBal, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
    this.dispose();
}//GEN-LAST:event_jbtnCloseActionPerformed

private void jbtnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreviewActionPerformed
    fielddValid(jtxtFromDate);
    fielddValid(jtxtToDate);
    jLabel1.setVisible(false);
    jlbClosingBal.setVisible(false);
    jLabel5.setVisible(false);
    jlblOpb.setVisible(false);
    jbtnPreview.setEnabled(false);
    jbtnClose.setEnabled(false);
    jbtnPrintPreviewActionPerformed();
    jbtnPreview.setEnabled(true);
    jbtnClose.setEnabled(true);
}//GEN-LAST:event_jbtnPreviewActionPerformed

private void jbtnPreviewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPreviewKeyPressed
    lb.enterClick(evt);
}//GEN-LAST:event_jbtnPreviewKeyPressed

    private void jtxtFromDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtFromDateFocusGained

    private void jtxtFromDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDateFocusLost
        if (!lb.checkDate(jtxtFromDate)) {
            lb.setDateChooserProperty(jtxtFromDate);
        }
        if(!lb.checkDate2(jtxtFromDate)) {
            lb.setDateChooserProperty(jtxtFromDate);
        }
    }//GEN-LAST:event_jtxtFromDateFocusLost

    private void jtxtFromDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFromDateKeyPressed
        lb.enterFocus(evt, jtxtToDate);
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
        if (!lb.checkDate(jtxtToDate)) {
            lb.setDateChooserProperty(jtxtToDate);
        }
        if(!lb.checkDate2(jtxtToDate)) {
            lb.setDateChooserProperty(jtxtToDate);
        }
    }//GEN-LAST:event_jtxtToDateFocusLost

    private void jtxtToDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtToDateKeyPressed
        lb.enterFocus(evt, jbtnPreview);
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnPreview;
    private javax.swing.JLabel jlbClosingBal;
    private javax.swing.JLabel jlblOpb;
    private javax.swing.JTextField jtxtFromDate;
    private javax.swing.JTextField jtxtToDate;
    // End of variables declaration//GEN-END:variables
}