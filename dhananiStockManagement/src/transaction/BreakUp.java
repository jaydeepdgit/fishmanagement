/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package transaction;

import dhananistockmanagement.DeskFrame;
import dhananistockmanagement.MainClass;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import oldbupdate.BreakupBillUpdate;
import support.Constants;
import support.EmailSelect;
import support.HeaderIntFrame1;
import support.Library;
import support.NavigationPanel1;
import support.OurDateChooser;
import support.PickList;
import support.ReportTable;
import support.VoucherDisplay;

/**
 *
 * @author @JD@
 */
public class BreakUp extends javax.swing.JInternalFrame {
    NavigationPanel1 navLoad = null;
    Library lb = new Library();
    DefaultTableModel dtm = null;
    private String id = "";
    double tot_amt = 0.00;
    private ReportTable breakUpView = null;
    Connection dataConnection = DeskFrame.connMpAdmin;
    private String initial = Constants.BREAK_UP_INITIAL;
    private PickList mainCategoryPickList = null, subCategoryPickList = null, accountMasterPickList = null;
    String Syspath = System.getProperty("user.dir");

    /**
     * Creates new form BreakUp
     */
    public BreakUp() {
        initComponents();
        initOtherComponents();
        navLoad.setVoucher("last");
    }

    public BreakUp(String id) {
        initComponents();
        jtxtVoucher.setText(id.substring(initial.length()));
        this.id = initial + jtxtVoucher.getText();
        initOtherComponents();
        navLoad.setVoucher("edit");
    }

    private void initOtherComponents() {
        setInitial();
        jtxtVoucher.requestFocusInWindow();
        connectToNavigation();
        lb.setDateChooserProperty(jtxtVDate);
        jlblDay.setText(lb.setDay(jtxtVDate));
        dtm = (DefaultTableModel) jTable1.getModel();
        makeChildTableBreakUp();
        setPickListView();
        setIconToPnael();
        jTable1.setBackground(new Color(253, 243, 243));
        setTitle(Constants.BREAK_UP_FORM_NAME);
        jbtnEmail.setText(Constants.EMAIL_BUTTON);
    }

    private void setIconToPnael() {
        Syspath += File.separator + "Resources" + File.separator + "Images" + File.separator;
        jbtnAdd.setIcon(new ImageIcon(Syspath +"Add.png"));
        jbtnEmail.setIcon(new ImageIcon(Syspath +"E-mail.png"));
    }

    private void setInitial(){
        jlblStart.setText(initial);
    }

    private void setPickListView() {
        mainCategoryPickList = new PickList(dataConnection);

        mainCategoryPickList.setLayer(getLayeredPane());
        mainCategoryPickList.setPickListComponent(jtxtMainCategory);
        mainCategoryPickList.setNextComponent(jtxtSubCategory);

        subCategoryPickList = new PickList(dataConnection);

        subCategoryPickList.setLayer(getLayeredPane());
        subCategoryPickList.setPickListComponent(jtxtSubCategory);
        subCategoryPickList.setNextComponent(jtxtPackagingWeight);

        accountMasterPickList = new PickList(dataConnection);

        accountMasterPickList.setLayer(getLayeredPane());
        accountMasterPickList.setPickListComponent(jtxtAccountName);
        accountMasterPickList.setNextComponent(jtxtMainCategory);
    }

    private void makeChildTableBreakUp() {
        breakUpView = new ReportTable();

        breakUpView.AddColumn(0, "Voucher No", 100, java.lang.String.class, null, false);
        breakUpView.AddColumn(1, "Account Name", 100, java.lang.String.class, null, false);
        breakUpView.AddColumn(2, "Date", 100, java.lang.String.class, null, false);
        breakUpView.AddColumn(3, "Net Amount", 150, java.lang.Double.class, null, false);
        breakUpView.makeTable();
    }

    private boolean validateData() {
        boolean flag = true;
        if(!lb.checkDate(jtxtVDate)) {
            navLoad.setMessage(Constants.INVALID_VOUCHER_DATE);
            jtxtVDate.requestFocusInWindow();
            flag = false;
        }
        return flag;
    }

    private void close() {
        this.dispose();
    }

    private void connectToNavigation() {
        class navPanel extends NavigationPanel1 {
            @Override
            public void callSave() throws Exception {
                setJtableTotal();
                valueUpdateToDatabase(false);
            }

            @Override
            public void callDelete() throws Exception {
                navLoad.setMessage("");
                setComponentEditable(true);
                int ans = JOptionPane.showConfirmDialog(null, Constants.DELETE_THIS + "voucher no. "+ id, "Confirm Delete", JOptionPane.OK_CANCEL_OPTION);
                if (ans == JOptionPane.OK_OPTION) {
                    try {
                        dataConnection.setAutoCommit(false);

                        delete();
                        dataConnection.commit();
                        setVoucher("last");
                        dataConnection.setAutoCommit(true);
                    } catch (Exception ex) {
                        try {
                            lb.printToLogFile("Error at delete In Break Up", ex);
                            dataConnection.rollback();
                            dataConnection.setAutoCommit(true);
                        } catch (SQLException ex1) {
                            lb.printToLogFile("Error at rollback delete In Break Up", ex1);
                        }
                    }
                } else {
                    setComponentEditable(false);
                }
            }

            @Override
            public void callView() {
                onviewVoucher();
            }

            @Override
            public void callPrint() {
                try {
                    VoucherDisplay vd = new VoucherDisplay(id, Constants.BREAK_UP_INITIAL);
                    DeskFrame.addOnScreen(vd, Constants.BREAK_UP_FORM_NAME +" PRINT");
                } catch(Exception ex) {
                    lb.printToLogFile("Exception at callPrint In Breakup Master", ex);
                }
            }

            @Override
            public void callClose() {
                close();
            }

            @Override
            public void setVoucher(String tag) {
                try {
                    navLoad.setComponentEditable(false);
                    String sql = "SELECT * FROM grade_main";
                    if (tag.equalsIgnoreCase("first")) {
                        sql += " WHERE id = (SELECT MIN(id) FROM grade_main)";
                    } else if (tag.equalsIgnoreCase("previous")) {
                        sql += " WHERE id = (SELECT MAX(id) FROM grade_main WHERE id < '"+ id +"')";
                    } else if (tag.equalsIgnoreCase("next")) {
                        sql += " WHERE id = (SELECT MIN(id) FROM grade_main WHERE id > '"+ id +"')";
                    } else if (tag.equalsIgnoreCase("last")) {
                        sql += " WHERE id = (SELECT MAX(id) FROM grade_main)";
                    } else if (tag.equalsIgnoreCase("edit")) {
                        sql += " WHERE id = '"+ id +"'";
                    }
                    navLoad.viewDataRs = navLoad.fetchData(sql);
                    if (navLoad.viewDataRs.next()) {
                        navLoad.setComponentTextFromRs();
                    } else {
                        if(tag.equalsIgnoreCase("last")) {
                            navLoad.setComponentText();
                        }
                    }
                    navLoad.setComponentEditable(false);
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setVoucher In Break Up", ex);
                }
                lb.setPermission(navLoad, Constants.BREAK_UP_FORM_ID);
                setTextfieldsAtBottom();
            }

            @Override
            public void setComponentText() {
                dtm.setRowCount(0);
                jtxtVoucher.setText("");
                jlblKgs.setText("0.00");
                jtxtAccountName.setText("");
                jtxtMainCategory.setText("");
                jtxtSubCategory.setText("");
                jtxtPackagingWeight.setText("0.000");
                jtxtRateDollarRs.setText("0.000");
                jlblINR.setText("0.000");
                jlblUSD.setText("0.000");
                jlblKgs.setText("0.000");
                jlblSlabQty.setText("0.000");
                lb.setDateChooserProperty(jtxtVDate);
                jtxtVDate.requestFocusInWindow();
                jtxtVDate.selectAll();
            }

            @Override
            public void updateOperationToDatabase(boolean bPrepareStatement) {
            }

            @Override
            public boolean validateForm() {
                return validateData();
            }

            @Override
            public void setComponentEditable(boolean bFlag) {
                jtxtVDate.setEnabled(bFlag);
                jtxtPackagingWeight.setEnabled(bFlag);
                jtxtRateDollarRs.setEnabled(bFlag);
                jtxtVoucher.setEnabled(!bFlag);
                jTable1.setEnabled(bFlag);
                jtxtMainCategory.setEnabled(bFlag);
                jtxtSubCategory.setEnabled(bFlag);
                jtxtAccountName.setEnabled(bFlag);
                jbtnAdd.setEnabled(bFlag);
                jtxtVDate.requestFocusInWindow();
                jtxtVDate.selectAll();
            }

            public int valueUpdateToDatabase(boolean bPrepareStatement) {
                try {
                    dataConnection.setAutoCommit(false);
                    saveVoucher();
                    dataConnection.commit();
                    dataConnection.setAutoCommit(true);
                    if (navLoad.getMode().equalsIgnoreCase("N")) {
                        setVoucher("last");
                    } else if (navLoad.getMode().equalsIgnoreCase("E")) {
                        setVoucher("Edit");
                    }
                } catch (Exception ex) {
                    try {
                        dataConnection.rollback();
                        dataConnection.setAutoCommit(true);
                        lb.printToLogFile("Exception at saveVoucher In Break Up", ex);
                    } catch (SQLException ex1) {
                        lb.printToLogFile("Exception at saveVoucher rollback In Break Up", ex1);
                    }
                }
                return 1;
            }

            @Override
            public void setComponentTextFromRs() {
                try {
                    id = viewDataRs.getString("id");
                    String sub_category_id = viewDataRs.getString("fk_sub_category_id");
                    jtxtVoucher.setText(viewDataRs.getString("id").substring(initial.length()));
                    jtxtVDate.setText(lb.ConvertDateFormetForDBForConcurrency(viewDataRs.getString("v_date")));
                    jtxtAccountName.setText(lb.getAccountMstName(viewDataRs.getString("fk_account_master_id"), "N"));
                    jtxtMainCategory.setText(lb.getMainCategory(viewDataRs.getString("fk_main_category_id"), "N"));
                    jtxtSubCategory.setText(lb.getSubCategory(sub_category_id, "N"));
                    jlblDay.setText(lb.setDay(jtxtVDate));
                    jtxtPackagingWeight.setText(viewDataRs.getString("packaging_weight"));
                    jtxtRateDollarRs.setText(viewDataRs.getString("rate_dollar_rs"));
                    jlblSlabQty.setText(lb.getIndianFormat(viewDataRs.getDouble("total_slabqty")));
                    jlblKgs.setText(lb.getIndianFormat(viewDataRs.getDouble("total_kgs")));
                    jlblBlockUsed.setText(lb.getIndianFormat(viewDataRs.getDouble("total_blockused")));
                    jlblUSD.setText(lb.getIndianFormat(viewDataRs.getDouble("total_usd")));
                    jlblINR.setText(lb.getIndianFormat(viewDataRs.getDouble("total_inr")));
                    jlblBlock.setText(lb.getIndianFormat(viewDataRs.getDouble("total_block")));
                    jlblUser.setText(lb.getUserName(viewDataRs.getString("user_cd"), "N"));
                    jlblEditNo.setText(viewDataRs.getString("edit_no"));
                    jlblTimeStamp.setText(lb.ConvertTimeStampFormetForDisplay(viewDataRs.getString("time_stamp")));

                    dtm.setRowCount(0);
                    viewDataRs = fetchData("SELECT gs.*, st.block as stBlock FROM grade_sub gs LEFT JOIN stock0_1 st ON gs.fk_slab_category_id = st.fk_slab_category_id WHERE gs.id = '"+ id +"'");
                    int i = 0;
                    while (viewDataRs.next()) {
                        Vector row = new Vector();
                        row.add(++i);
                        row.add(lb.getSlabCategory(viewDataRs.getString("fk_slab_category_id"), "N", sub_category_id));
                        row.add(lb.Convert2DecFmt(viewDataRs.getDouble("grad_qty")));
                        row.add(lb.Convert2DecFmt(viewDataRs.getDouble("kgs")));
                        row.add(lb.Convert2DecFmt(viewDataRs.getDouble("block_used")));
                        row.add(lb.Convert2DecFmt(viewDataRs.getDouble("rate_usd")));
                        row.add(lb.Convert2DecFmt(viewDataRs.getDouble("total_usd")));
                        row.add(lb.Convert2DecFmt(viewDataRs.getDouble("rate_inr")));
                        row.add(lb.Convert2DecFmt(viewDataRs.getDouble("total_inr")));
                        row.add(lb.Convert2DecFmt(viewDataRs.getDouble("stBlock")));
                        row.add(lb.Convert2DecFmt(viewDataRs.getDouble("stBlock")));
                        row.add(lb.Convert2DecFmt(viewDataRs.getDouble("block_used")));
                        dtm.addRow(row);
                    }
                } catch (Exception ex) {
                    lb.printToLogFile("Exception at setComponentTextFromRs In Break Up", ex);
                }
            }
        }
        navLoad = new navPanel();
        jPanel2.add(navLoad);
        navLoad.setVisible(true);
    }

    public void setID(String id) {
        this.id = id;
        navLoad.setVoucher("edit");
    }

    private void onviewVoucher() {
        this.dispose();

         String sql = "SELECT gm.id, am.name, gm.v_date, gm.total_kgs FROM grade_main gm, account_master am "
            +"WHERE gm.fk_account_master_id = am.id ORDER BY gm.id";
        breakUpView.setColumnValue(new int[]{1, 2, 3, 4});
        String view_title = Constants.BREAK_UP_FORM_NAME +" VIEW";

        HeaderIntFrame1 rptDetail = new HeaderIntFrame1(dataConnection, id, view_title, breakUpView, sql, Constants.BREAK_UP_FORM_ID, 1, this, this.getTitle());
        rptDetail.makeView();
        rptDetail.setVisible(true);

        Component c = DeskFrame.tabbedPane.add(view_title, rptDetail);
        c.setName(view_title);
        DeskFrame.tabbedPane.setSelectedComponent(c);
    }

    @Override
    public void dispose() {
        try {
            DeskFrame.removeFromScreen(DeskFrame.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose In Break Up", ex);
        }
    }

    private int saveVoucher() throws SQLException, ParseException, FileNotFoundException, IOException {
        String sql = null;
        
        BreakupBillUpdate breakupBillUpdate = new BreakupBillUpdate();
        PreparedStatement psLocal = null;
        int change = 0;
        if (navLoad.getMode().equalsIgnoreCase("N")) {
            sql = "INSERT INTO grade_main (fk_account_master_id, v_date, fk_main_category_id, fk_sub_category_id, packaging_weight, rate_dollar_rs, total_slabqty, total_kgs, total_blockused, total_usd, total_inr, total_block, fix_time, user_cd, id) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '"+ new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) +"', ?, ?)";
            id = lb.generateKey("grade_main", "id", 8, initial); // GENERATE REF NO
        } else if (navLoad.getMode().equalsIgnoreCase("E")) {
            breakupBillUpdate.deleteEntry(id);
            
            sql = "DELETE FROM grade_sub WHERE id='"+ id +"'";
            psLocal = dataConnection.prepareStatement(sql);
            change += psLocal.executeUpdate();

            sql = "UPDATE grade_main SET fk_account_master_id = ?, v_date = ?, fk_main_category_id = ?, fk_sub_category_id = ?, packaging_weight = ?, rate_dollar_rs = ?, total_slabqty = ?, total_kgs = ?, total_blockused = ?, total_usd = ?, total_inr = ?, total_block = ?, fix_time = '"+ new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) 
                +"', user_cd = ?, edit_no = edit_no + 1, time_stamp = CURRENT_TIMESTAMP WHERE id = ?";
        }
        
        String sub_category_id = lb.getSubCategory(jtxtSubCategory.getText(), "C");
        psLocal = dataConnection.prepareStatement(sql);
        psLocal.setString(1, lb.getAccountMstName(jtxtAccountName.getText(), "C")); // fk_account_master_id
        psLocal.setString(2, lb.tempConvertFormatForDBorConcurrency(jtxtVDate.getText())); // v_date
        psLocal.setString(3, lb.getMainCategory(jtxtMainCategory.getText(), "C")); // Main Category
        psLocal.setString(4, sub_category_id); // Sub Category
        psLocal.setDouble(5, lb.replaceAll(jtxtPackagingWeight.getText())); // packaging weight
        psLocal.setDouble(6, lb.replaceAll(jtxtRateDollarRs.getText())); // rate dollar rs
        psLocal.setDouble(7, lb.replaceAll(jlblSlabQty.getText())); // total_slabqty
        psLocal.setDouble(8, lb.replaceAll(jlblKgs.getText())); // total_kgs
        psLocal.setDouble(9, lb.replaceAll(jlblBlockUsed.getText())); // total_blockused
        psLocal.setDouble(10, lb.replaceAll(jlblUSD.getText())); // total_USD
        psLocal.setDouble(11, lb.replaceAll(jlblINR.getText())); // total_INT
        psLocal.setDouble(12, lb.replaceAll(jlblBlock.getText())); // total_Block
        psLocal.setInt(13, DeskFrame.user_id); // user_cd
        psLocal.setString(14, id); // id
        change += psLocal.executeUpdate();

        sql = "INSERT INTO grade_sub (sr_no, fk_slab_category_id, grad_qty, kgs, block_used, rate_usd, total_usd, rate_inr, total_inr, block, id) VALUES (?, ?, ?, ?, ?, ? ,? ,?, ?, ?, ?)";
        psLocal = dataConnection.prepareStatement(sql);
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            String fkSlabCategoryName = jTable1.getValueAt(i, 1).toString(); // fkSlabCategoryName
            double grad_qty = lb.replaceAll(jTable1.getValueAt(i, 2).toString()); // grad_qty
            double kgs = lb.replaceAll(jTable1.getValueAt(i, 3).toString()); // kgs
            double block_used = lb.replaceAll(jTable1.getValueAt(i, 4).toString()); // block_used
            double rateUSD = lb.replaceAll(jTable1.getValueAt(i, 5).toString()); // rateUSD
            double totalUSD = lb.replaceAll(jTable1.getValueAt(i, 6).toString()); // totalUSD
            double rateINR = lb.replaceAll(jTable1.getValueAt(i, 7).toString()); // rateINR
            double totalINR = lb.replaceAll(jTable1.getValueAt(i, 8).toString()); // totalINR
            double block = lb.replaceAll(jTable1.getValueAt(i, 9).toString()); // block
            String fkSlabCategoryId = lb.getSlabCategory(fkSlabCategoryName, "C", sub_category_id); // slab category od
            if(!(fkSlabCategoryId.equalsIgnoreCase("0") || fkSlabCategoryId.equalsIgnoreCase(""))) {
                psLocal.setInt(1, i + 1); // sr_no
                psLocal.setString(2, fkSlabCategoryId); // sub category id
                psLocal.setDouble(3, grad_qty); // grad_qty
                psLocal.setDouble(4, kgs); // kgs
                psLocal.setDouble(5, block_used); // block_used
                psLocal.setDouble(6, rateUSD); // rateUSD
                psLocal.setDouble(7, totalUSD); // totalUSD
                psLocal.setDouble(8, rateINR); // rateINR
                psLocal.setDouble(9, totalINR); // totalINR
                psLocal.setDouble(10, block); // block
                psLocal.setString(11, id); // id
                change += psLocal.executeUpdate();
            }
        }
        
        breakupBillUpdate.addEntry(id, lb.tempConvertFormatForDBorConcurrency(jtxtVDate.getText()));
        return change;
    }

    private void delete() throws SQLException {
        BreakupBillUpdate breakupBillUpdate = new BreakupBillUpdate();
        breakupBillUpdate.deleteEntry(id);
        
        PreparedStatement psLocal = dataConnection.prepareStatement("DELETE FROM grade_main WHERE id='"+ id +"'");
        psLocal.executeUpdate();

        psLocal = dataConnection.prepareStatement("DELETE FROM grade_sub WHERE id='"+ id +"'");
        psLocal.executeUpdate();
        lb.closeStatement(psLocal);
    }

    private void setTextfieldsAtBottom() {
        JComponent[] footer = new JComponent[]{null, null, jlblSlabQty, jlblKgs, jlblBlockUsed, null, jlblUSD, null, jlblINR, jlblBlock, null, null};
        lb.setTable(jPanel1, jTable1, null, footer);
    }

    private void setJtableTotal() {
        double tUSD = 0.000, tINR = 0.000, tSlabQty = 0.000, tKgs = 0.000, tBlockUsed = 0.000, tBlock = 0.000;
        
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            double slabQty = 0.000, kgs = 0.00, blockUsed = 0.000, editBlockUsed = 0.000, block = 0.00;
            
            slabQty = lb.replaceAll(jTable1.getValueAt(i, 2).toString());
            tSlabQty += slabQty;

            kgs = lb.replaceAll(jtxtPackagingWeight.getText());
            jTable1.setValueAt(lb.Convert2DecFmt(kgs * slabQty), i, 3);
            tKgs += (kgs * slabQty);

            blockUsed = lb.replaceAll(jTable1.getValueAt(i, 4).toString());
            tBlockUsed += blockUsed;
            
            block = lb.replaceAll(jTable1.getValueAt(i, 10).toString());
            editBlockUsed = lb.replaceAll(jTable1.getValueAt(i, 11).toString());
            jTable1.setValueAt(((block + editBlockUsed) - blockUsed), i, 9);
            tBlock += block;
        }
        
        
        double rateDollarRs = lb.replaceAll(jtxtRateDollarRs.getText());
        if(rateDollarRs > 0) {
            for (int i = 0; i < jTable1.getRowCount(); i++) {
                double totalUSD = 0.00, totalINR = 0.00;
                jTable1.setValueAt(i+1, i, 0);
                double rowWeight = lb.replaceAll(jTable1.getValueAt(i, 3).toString());
                double rowDollarRate = lb.replaceAll(jTable1.getValueAt(i, 5).toString());
                if(rowDollarRate > 0) {
                    double rowRate = rowDollarRate * rateDollarRs;
                    jTable1.setValueAt(lb.Convert2DecFmt(rowWeight * rateDollarRs), i, 6);
                    jTable1.setValueAt(lb.Convert2DecFmt(rowRate), i, 7);
                    jTable1.setValueAt(lb.Convert2DecFmt(rowWeight * rowRate), i, 8);
                }
                
                double rowRate = lb.replaceAll(jTable1.getValueAt(i, 7).toString());
                if(rowRate > 0) {
                    rowDollarRate = rowRate / rateDollarRs;
                    jTable1.setValueAt(lb.Convert2DecFmt(rowDollarRate), i, 5);
                    jTable1.setValueAt(lb.Convert2DecFmt(rowWeight * rowDollarRate), i, 6);
                    jTable1.setValueAt(lb.Convert2DecFmt(rowWeight * rowRate), i, 8);
                }
                
                totalUSD = lb.replaceAll(jTable1.getValueAt(i, 6).toString());
                tUSD += totalUSD;
            
                totalINR = lb.replaceAll(jTable1.getValueAt(i, 8).toString());
                tINR += totalINR;
            }
        }
       
        
        jlblSlabQty.setText(tSlabQty+"");
        jlblKgs.setText(tKgs+"");
        jlblBlockUsed.setText(tBlockUsed+"");
        jlblUSD.setText(tUSD+"");
        jlblINR.setText(tINR+"");
        jlblBlock.setText(tBlock+"");
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
        jLabel12 = new javax.swing.JLabel();
        jlblUser = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jlblTimeStamp = new javax.swing.JLabel();
        jlblEditNo = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jlblStart = new javax.swing.JLabel();
        jtxtVoucher = new javax.swing.JTextField();
        jlblDay = new javax.swing.JLabel();
        jBillDateBtn = new javax.swing.JButton();
        jtxtVDate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jbtnAdd = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        jtxtMainCategory = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jtxtSubCategory = new javax.swing.JTextField();
        jtxtAccountName = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jtxtPackagingWeight = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jtxtRateDollarRs = new javax.swing.JTextField();
        jlblKgs = new javax.swing.JLabel();
        jbtnEmail = new javax.swing.JButton();
        jlblUSD = new javax.swing.JLabel();
        jlblINR = new javax.swing.JLabel();
        jlblBlock = new javax.swing.JLabel();
        jlblSlabQty = new javax.swing.JLabel();
        jlblBlockUsed = new javax.swing.JLabel();

        setBackground(new java.awt.Color(211, 226, 245));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(53, 154, 141)));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(new java.awt.Color(253, 243, 243));
        jScrollPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(4, 110, 152), 1, true));
        jScrollPane1.setOpaque(false);

        jTable1.setBackground(new java.awt.Color(253, 243, 243));
        jTable1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sr No.", "Grade Name", "Slab Qty", "Total KGS", "Block Used", "Rate (USD)", "Total (USD)", "Rate (INR)", "Total (INR)", "Block", "Block Hidden", "Block Used Hidden"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, false, true, true, false, true, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRowHeight(23);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTable1KeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(5);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(170);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(4).setMinWidth(0);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(4).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
            jTable1.getColumnModel().getColumn(9).setMinWidth(0);
            jTable1.getColumnModel().getColumn(9).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(9).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(10).setMinWidth(0);
            jTable1.getColumnModel().getColumn(10).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(10).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(11).setMinWidth(0);
            jTable1.getColumnModel().getColumn(11).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(11).setMaxWidth(0);
        }

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setBackground(new java.awt.Color(253, 243, 243));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 1, 1, new java.awt.Color(235, 35, 35)));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel12.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel12.setText("User Name : ");

        jlblUser.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jLabel13.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel13.setText("Edit No");

        jLabel15.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel15.setText("Last Updated");

        jlblTimeStamp.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jlblEditNo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jPanel3.setBackground(new java.awt.Color(253, 243, 243));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)), "Voucher Information", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 16), new java.awt.Color(0, 0, 255))); // NOI18N

        jLabel22.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel22.setText("Voucher No.:");

        jlblStart.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlblStart.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblStart.setText("BK");
        jlblStart.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jtxtVoucher.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtVoucher.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtVoucher.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtVoucherFocusGained(evt);
            }
        });
        jtxtVoucher.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtVoucherKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtVoucherKeyTyped(evt);
            }
        });

        jlblDay.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlblDay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));

        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });

        jtxtVDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtVDate.setAutoscrolls(false);
        jtxtVDate.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtVDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtVDateFocusLost(evt);
            }
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtVDateFocusGained(evt);
            }
        });
        jtxtVDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtVDateKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("Voucher Date");

        jbtnAdd.setBackground(new java.awt.Color(204, 255, 204));
        jbtnAdd.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnAdd.setForeground(new java.awt.Color(235, 35, 35));
        jbtnAdd.setText("ADD");
        jbtnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddActionPerformed(evt);
            }
        });
        jbtnAdd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnAddKeyPressed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel23.setText("Main Category: ");

        jtxtMainCategory.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtMainCategory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtMainCategory.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtMainCategory.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtMainCategory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtMainCategoryFocusLost(evt);
            }
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtMainCategoryFocusGained(evt);
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

        jLabel24.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel24.setText("Sub Category: ");

        jtxtSubCategory.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtSubCategory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtSubCategory.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtSubCategory.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtSubCategory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtSubCategoryFocusLost(evt);
            }
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtSubCategoryFocusGained(evt);
            }
        });
        jtxtSubCategory.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtSubCategoryKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtSubCategoryKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtSubCategoryKeyTyped(evt);
            }
        });

        jtxtAccountName.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtAccountName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAccountName.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtAccountName.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtAccountName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAccountNameFocusLost(evt);
            }
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAccountNameFocusGained(evt);
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

        jLabel25.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel25.setText("Account Name:");

        jLabel26.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel26.setText("Packaging Weight:");

        jtxtPackagingWeight.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtPackagingWeight.setAutoscrolls(false);
        jtxtPackagingWeight.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtPackagingWeight.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPackagingWeightFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtPackagingWeightFocusLost(evt);
            }
        });
        jtxtPackagingWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtPackagingWeightActionPerformed(evt);
            }
        });
        jtxtPackagingWeight.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPackagingWeightKeyPressed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel27.setText("Rate Dollar:");

        jtxtRateDollarRs.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtRateDollarRs.setAutoscrolls(false);
        jtxtRateDollarRs.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtRateDollarRs.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtRateDollarRsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtRateDollarRsFocusLost(evt);
            }
        });
        jtxtRateDollarRs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtRateDollarRsActionPerformed(evt);
            }
        });
        jtxtRateDollarRs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtRateDollarRsKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtMainCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtSubCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtPackagingWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtRateDollarRs, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
                        .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblStart, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtVDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblDay, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAccountName, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(jlblStart)
                    .addComponent(jtxtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtVDate, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jBillDateBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblDay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtAccountName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jtxtRateDollarRs, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxtMainCategory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jtxtSubCategory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jtxtPackagingWeight, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(7, 7, 7))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel23, jLabel24, jtxtMainCategory, jtxtSubCategory});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBillDateBtn, jLabel2, jLabel22, jbtnAdd, jlblDay, jlblStart, jtxtVDate, jtxtVoucher});

        jlblKgs.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblKgs.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblKgs.setText("0.00");
        jlblKgs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jlblKgs.setMaximumSize(new java.awt.Dimension(24, 20));
        jlblKgs.setMinimumSize(new java.awt.Dimension(24, 20));
        jlblKgs.setPreferredSize(new java.awt.Dimension(24, 20));
        jlblKgs.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jlblKgsComponentResized(evt);
            }
        });

        jbtnEmail.setBackground(new java.awt.Color(204, 255, 204));
        jbtnEmail.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnEmail.setForeground(new java.awt.Color(235, 35, 35));
        jbtnEmail.setText("EMAIL");
        jbtnEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEmailActionPerformed(evt);
            }
        });

        jlblUSD.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblUSD.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblUSD.setText("0.00");
        jlblUSD.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jlblUSD.setMaximumSize(new java.awt.Dimension(24, 20));
        jlblUSD.setMinimumSize(new java.awt.Dimension(24, 20));
        jlblUSD.setPreferredSize(new java.awt.Dimension(24, 20));
        jlblUSD.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jlblUSDComponentResized(evt);
            }
        });

        jlblINR.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblINR.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblINR.setText("0.00");
        jlblINR.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jlblINR.setMaximumSize(new java.awt.Dimension(24, 20));
        jlblINR.setMinimumSize(new java.awt.Dimension(24, 20));
        jlblINR.setPreferredSize(new java.awt.Dimension(24, 20));
        jlblINR.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jlblINRComponentResized(evt);
            }
        });

        jlblBlock.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblBlock.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblBlock.setText("0.00");
        jlblBlock.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jlblBlock.setMaximumSize(new java.awt.Dimension(24, 20));
        jlblBlock.setMinimumSize(new java.awt.Dimension(24, 20));
        jlblBlock.setPreferredSize(new java.awt.Dimension(24, 20));
        jlblBlock.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jlblBlockComponentResized(evt);
            }
        });

        jlblSlabQty.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblSlabQty.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblSlabQty.setText("0.00");
        jlblSlabQty.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jlblSlabQty.setMaximumSize(new java.awt.Dimension(24, 20));
        jlblSlabQty.setMinimumSize(new java.awt.Dimension(24, 20));
        jlblSlabQty.setPreferredSize(new java.awt.Dimension(24, 20));
        jlblSlabQty.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jlblSlabQtyComponentResized(evt);
            }
        });

        jlblBlockUsed.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jlblBlockUsed.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblBlockUsed.setText("0.00");
        jlblBlockUsed.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jlblBlockUsed.setMaximumSize(new java.awt.Dimension(24, 20));
        jlblBlockUsed.setMinimumSize(new java.awt.Dimension(24, 20));
        jlblBlockUsed.setPreferredSize(new java.awt.Dimension(24, 20));
        jlblBlockUsed.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jlblBlockUsedComponentResized(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblUser, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblEditNo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblTimeStamp, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jlblSlabQty, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblKgs, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblBlockUsed, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblUSD, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblINR, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlblBlock, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlblKgs, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblUSD, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblINR, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblBlock, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblSlabQty, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlblBlockUsed, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnEmail)
                    .addComponent(jlblTimeStamp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblEditNo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlblUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel12, jLabel13, jLabel15, jbtnEmail, jlblEditNo, jlblTimeStamp, jlblUser});

        pack();
    }// </editor-fold>//GEN-END:initComponents
   
    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        setTextfieldsAtBottom();
    }//GEN-LAST:event_formMouseMoved

    private void jtxtVDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtVDateFocusGained

    private void jtxtVDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVDateFocusLost
        if(lb.checkDate(jtxtVDate)){
            jlblDay.setText(lb.setDay(jtxtVDate));
        } else {
            navLoad.setMessage(Constants.CORRECT_DATE);
        }
    }//GEN-LAST:event_jtxtVDateFocusLost

    private void jtxtVDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVDateKeyPressed
        lb.enterFocus(evt, jtxtAccountName);
    }//GEN-LAST:event_jtxtVDateKeyPressed

    private void jBillDateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBillDateBtnActionPerformed
        OurDateChooser odc = new OurDateChooser();
        odc.setnextFocus(jtxtVDate);
        odc.setFormat("dd/MM/yyyy");
        JPanel jp = new JPanel();
        this.add(jp);
        jp.setBounds(jtxtVDate.getX(), jtxtVDate.getY() + 145, jtxtVDate.getX() + odc.getWidth(), jtxtVDate.getY() + odc.getHeight());
        odc.setLocation(0, 0);
        odc.showDialog(jp, Constants.SELECT_DATE);
    }//GEN-LAST:event_jBillDateBtnActionPerformed

    private void jtxtVoucherFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtVoucherFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtVoucherFocusGained

    private void jtxtVoucherKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVoucherKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if(lb.isExist("grade_main", "id", initial + jtxtVoucher.getText(), dataConnection)) {
                id = initial + jtxtVoucher.getText();
                navLoad.setMessage("");
                navLoad.setVoucher("Edit");
            } else {
                navLoad.setMessage(Constants.INVALID_VOUCHER_NO);
            }
            jtxtVoucher.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtVoucherKeyPressed

    private void jtxtVoucherKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtVoucherKeyTyped
        lb.onlyNumber(evt, (7 - initial.length()));
    }//GEN-LAST:event_jtxtVoucherKeyTyped

    private void jbtnEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEmailActionPerformed
        EmailSelect ds = new EmailSelect(MainClass.df, true, id, initial, 1);
        ds.show();
    }//GEN-LAST:event_jbtnEmailActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_formComponentResized

    private void jlblKgsComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblKgsComponentResized
        setTextfieldsAtBottom();
    }//GEN-LAST:event_jlblKgsComponentResized

    private void jtxtMainCategoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMainCategoryFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMainCategoryFocusGained

    private void jtxtMainCategoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMainCategoryFocusLost
        mainCategoryPickList.setVisible(false);
    }//GEN-LAST:event_jtxtMainCategoryFocusLost

    private void jtxtMainCategoryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainCategoryKeyPressed
        mainCategoryPickList.setLocation(jtxtMainCategory.getX() + jPanel3.getX(), jtxtMainCategory.getY() + jtxtMainCategory.getHeight() + jPanel3.getY());
        mainCategoryPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtMainCategoryKeyPressed

    private void jtxtMainCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainCategoryKeyReleased
        try {
            mainCategoryPickList.setReturnComponent(new JTextField[]{jtxtMainCategory});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM main_category WHERE status = 0 AND name LIKE '%"+ jtxtMainCategory.getText() +"%'");
            mainCategoryPickList.setValidation(dataConnection.prepareStatement("SELECT name FROM main_category WHERE status = 0 AND name = ?"));
            mainCategoryPickList.setFirstAssociation(new int[]{0});
            mainCategoryPickList.setSecondAssociation(new int[]{0});
            mainCategoryPickList.setPreparedStatement(pstLocal);
            mainCategoryPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtMainCategoryKeyReleased In Break Up", ex);
        }
    }//GEN-LAST:event_jtxtMainCategoryKeyReleased

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        try {
            String sql = "SELECT sc.name, (st.block-st.block_used) as block FROM slab_category sc LEFT JOIN stock0_1 st ON sc.id = st.fk_slab_category_id WHERE sc.status = 0 AND sc.fk_sub_category_id = '"+ lb.getSubCategory(jtxtSubCategory.getText(), "C") +"'";
            PreparedStatement psLocal = dataConnection.prepareStatement(sql);
            ResultSet rsLocal = psLocal.executeQuery();
            int i = 0;
            for (int i1=(jTable1.getRowCount()-1); i1 >= 0; i1--) {
                dtm.removeRow(i1);
            }
            while(rsLocal.next()) {
                Vector row = new Vector();
                row.add(++i);
                row.add(rsLocal.getString("name"));
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add(rsLocal.getString("block"));
                row.add(rsLocal.getString("block"));
                row.add("");
                dtm.addRow(row);
            }
        } catch(Exception ex) {
            lb.printToLogFile("Exception at acalias in Tax Invoice", ex);
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jbtnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnAddKeyPressed
        lb.enterClick(evt);
    }//GEN-LAST:event_jbtnAddKeyPressed

    private void jtxtSubCategoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSubCategoryFocusLost
        subCategoryPickList.setVisible(false);
    }//GEN-LAST:event_jtxtSubCategoryFocusLost

    private void jtxtSubCategoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSubCategoryFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtSubCategoryFocusGained

    private void jtxtSubCategoryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSubCategoryKeyPressed
        subCategoryPickList.setLocation(jtxtSubCategory.getX() + jPanel3.getX(), jtxtSubCategory.getY() + jtxtSubCategory.getHeight() + jPanel3.getY());
        subCategoryPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtSubCategoryKeyPressed

    private void jtxtSubCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSubCategoryKeyReleased
        try {
            String sql = "SELECT name FROM sub_category WHERE fk_main_category_id = '"+ lb.getMainCategory(jtxtMainCategory.getText(), "C") 
                +"' AND status = 0 AND name LIKE '%"+ jtxtSubCategory.getText() +"%'";
            subCategoryPickList.setReturnComponent(new JTextField[]{jtxtSubCategory});
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            subCategoryPickList.setValidation(dataConnection.prepareStatement("SELECT name FROM sub_category WHERE status = 0 AND name = ?"));
            subCategoryPickList.setFirstAssociation(new int[]{0});
            subCategoryPickList.setSecondAssociation(new int[]{0});
            subCategoryPickList.setPreparedStatement(pstLocal);
            subCategoryPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtMainCategoryKeyReleased In Break Up", ex);
        }
    }//GEN-LAST:event_jtxtSubCategoryKeyReleased

    private void jtxtMainCategoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainCategoryKeyTyped
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtMainCategoryKeyTyped

    private void jtxtSubCategoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSubCategoryKeyTyped
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtSubCategoryKeyTyped

    private void jtxtAccountNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAccountNameFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtAccountNameFocusLost

    private void jtxtAccountNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAccountNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAccountNameFocusGained

    private void jtxtAccountNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAccountNameKeyPressed
        accountMasterPickList.setLocation(jtxtAccountName.getX() + jPanel3.getX(), jtxtAccountName.getY() + jtxtAccountName.getHeight() + jPanel3.getY());
        accountMasterPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtAccountNameKeyPressed

    private void jtxtAccountNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAccountNameKeyReleased
        try {
            accountMasterPickList.setReturnComponent(new JTextField[]{jtxtAccountName});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM account_master WHERE status = 0 AND name LIKE '%"+ jtxtAccountName.getText() +"%'");
            accountMasterPickList.setValidation(dataConnection.prepareStatement("SELECT name FROM account_master WHERE status = 0 AND name = ?"));
            accountMasterPickList.setFirstAssociation(new int[]{0});
            accountMasterPickList.setSecondAssociation(new int[]{0});
            accountMasterPickList.setPreparedStatement(pstLocal);
            accountMasterPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtAccountNameKeyReleased In Purchase Bill", ex);
        }
    }//GEN-LAST:event_jtxtAccountNameKeyReleased

    private void jtxtAccountNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAccountNameKeyTyped
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtAccountNameKeyTyped

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
        setJtableTotal();
    }//GEN-LAST:event_jTable1MousePressed

    private void jlblUSDComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblUSDComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_jlblUSDComponentResized

    private void jlblINRComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblINRComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_jlblINRComponentResized

    private void jTable1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyTyped
        setJtableTotal();
    }//GEN-LAST:event_jTable1KeyTyped

    private void jlblBlockComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblBlockComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_jlblBlockComponentResized

    private void jlblSlabQtyComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblSlabQtyComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_jlblSlabQtyComponentResized

    private void jlblBlockUsedComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jlblBlockUsedComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_jlblBlockUsedComponentResized

    private void jtxtPackagingWeightFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPackagingWeightFocusLost
        setJtableTotal();
    }//GEN-LAST:event_jtxtPackagingWeightFocusLost

    private void jtxtPackagingWeightFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPackagingWeightFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPackagingWeightFocusGained

    private void jtxtPackagingWeightKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPackagingWeightKeyPressed
        lb.enterEvent(evt, jtxtRateDollarRs);
    }//GEN-LAST:event_jtxtPackagingWeightKeyPressed

    private void jtxtPackagingWeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtPackagingWeightActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtPackagingWeightActionPerformed

    private void jtxtRateDollarRsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateDollarRsFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtRateDollarRsFocusGained

    private void jtxtRateDollarRsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtRateDollarRsFocusLost
        setJtableTotal();
    }//GEN-LAST:event_jtxtRateDollarRsFocusLost

    private void jtxtRateDollarRsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtRateDollarRsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtRateDollarRsActionPerformed

    private void jtxtRateDollarRsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtRateDollarRsKeyPressed
        lb.enterEvent(evt, jbtnAdd);
    }//GEN-LAST:event_jtxtRateDollarRsKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBillDateBtn;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JButton jbtnEmail;
    private javax.swing.JLabel jlblBlock;
    private javax.swing.JLabel jlblBlockUsed;
    private javax.swing.JLabel jlblDay;
    private javax.swing.JLabel jlblEditNo;
    private javax.swing.JLabel jlblINR;
    private javax.swing.JLabel jlblKgs;
    private javax.swing.JLabel jlblSlabQty;
    private javax.swing.JLabel jlblStart;
    private javax.swing.JLabel jlblTimeStamp;
    private javax.swing.JLabel jlblUSD;
    private javax.swing.JLabel jlblUser;
    private javax.swing.JTextField jtxtAccountName;
    private javax.swing.JTextField jtxtMainCategory;
    private javax.swing.JTextField jtxtPackagingWeight;
    private javax.swing.JTextField jtxtRateDollarRs;
    private javax.swing.JTextField jtxtSubCategory;
    private javax.swing.JTextField jtxtVDate;
    private javax.swing.JTextField jtxtVoucher;
    // End of variables declaration//GEN-END:variables
}