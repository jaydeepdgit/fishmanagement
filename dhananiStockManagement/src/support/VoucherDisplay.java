/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import java.io.File;
import dhananistockmanagement.DeskFrame;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperPrint;

/**
 *
 * @author @JD@
 */
public class VoucherDisplay extends javax.swing.JInternalFrame {
    Library lb = new Library();
    String ref_no = "";
    String tag = "";
    String from = "";
    int mode = 0, type = 0;
    Connection dataConnecrtion = DeskFrame.connMpAdmin;
    SysEnv sysEnv = DeskFrame.clSysEnv;
    JasperPrint print = null;
    String Syspath = System.getProperty("user.dir");
    String email = "";
    String bill_type = "(Original/Duplicate/Triplicate)";

    /**
     * Creates new form VoucherDisplay
     */
    public VoucherDisplay() {
        initComponents();
        registerShortKeys();
    }

    public VoucherDisplay(String ref_no, String tag) {
        initComponents();
        registerShortKeys();
        this.ref_no = ref_no;
        this.tag = tag;
        getVoucher(tag, ref_no, true);
    }

    public VoucherDisplay(String ref_no, String email, String tag, int mode) {
        initComponents();
        registerShortKeys();
        this.ref_no = ref_no;
        this.tag = tag;
        this.mode = mode;
        this.email = email;
        getVoucher(tag, ref_no, mode, true);
    }

    public VoucherDisplay(String ref_no, String email, String tag, int mode, String bill_type) {
        initComponents();
        registerShortKeys();
        this.ref_no = ref_no;
        this.tag = tag;
        this.mode = mode;
        this.email = email;
        this.bill_type = bill_type;
        getVoucher(tag, ref_no, mode, true);
    }

    public VoucherDisplay(String ref_no, String email, String tag, int mode, int type) {
        initComponents();
        registerShortKeys();
        this.ref_no = ref_no;
        this.tag = tag;
        this.mode = mode;
        this.email = email;
        this.type = type;
        getVoucher(tag, ref_no, mode, type, true);
    }

    private void getVoucher(String tag, String ref_no, int mode, int type, boolean flag) {
        if (tag.equalsIgnoreCase(Constants.CASH_PAYMENT_INITIAL) || tag.equalsIgnoreCase(Constants.CASH_RECEIPT_INITIAL)) {
            displayCashReceipt(ref_no, mode, type);
        } else if (tag.equalsIgnoreCase(Constants.BANK_PAYMENT_INITIAL) || tag.equalsIgnoreCase(Constants.BANK_RECEIPT_INITIAL)) {
            displayBankReceipt(ref_no, mode, type);
        }
    }

    private void getVoucher(String tag, String ref_no, int mode, boolean flag) {
        if (tag.equalsIgnoreCase(Constants.CASH_PAYMENT_INITIAL) || tag.equalsIgnoreCase(Constants.CASH_RECEIPT_INITIAL)) {
            displayCashReceipt(ref_no, mode, 0);
        } else if (tag.equalsIgnoreCase(Constants.BANK_PAYMENT_INITIAL) || tag.equalsIgnoreCase(Constants.BANK_RECEIPT_INITIAL)) {
            displayBankReceipt(ref_no, mode, 0);
        }
    }

    private void getVoucher(String tag, String ref_no, boolean flag) {
        if(tag.equalsIgnoreCase(Constants.GROUP_MASTER_INITIAL)) {
            groupMasterReport(ref_no);
        } else if(tag.equalsIgnoreCase(Constants.ACCOUNT_MASTER_INITIAL)) {
            accountMasterReport(ref_no);
        } else if(tag.equalsIgnoreCase(Constants.COMPANY_SETTING_INITIAL)) {
            companyMasterReport(ref_no);
        } else if(tag.equalsIgnoreCase("UM")) {
            unitMasterReport(ref_no);
        } else if(tag.equalsIgnoreCase(Constants.MAIN_CATEGORY_INITIAL)) {
            mainItemMasterReport(ref_no);
        } else if(tag.equalsIgnoreCase(""+ Constants.SUB_CATEGORY_INITIAL +"M")) {
            itemMasterReport(ref_no);
        } else if(tag.equalsIgnoreCase("CNM")) {
            countryMasterReport(ref_no);
        } else if(tag.equalsIgnoreCase("SNM")) {
            stateMasterReport(ref_no);
        } else if(tag.equalsIgnoreCase("CTM")) {
            cityMasterReport(ref_no);
        } else if(tag.equalsIgnoreCase("ARM")) {
            areaMasterReport(ref_no);
        } else if(tag.equalsIgnoreCase("MU")) {
            manageUserReport(ref_no);
        } else if(tag.equalsIgnoreCase(Constants.CHECK_PRINT_INITIAL)) {
            checkPrintReport(ref_no);
        } else if(tag.equalsIgnoreCase("UR")) {
            userRightsReport(ref_no);
        } else if(tag.equalsIgnoreCase(Constants.PURCHASE_BILL_INITIAL)) {
            purchaseBillReport(ref_no);
        } else if(tag.equalsIgnoreCase(Constants.SALES_BILL_INITIAL)) {
            saleBillReport(ref_no);
        } else if(tag.equalsIgnoreCase(Constants.BREAK_UP_INITIAL)) {
            breakUpReport(ref_no);
        }
    }
    
    private void breakUpReport(String ref_no) {
        String sql = "SELECT gm.rate_dollar_rs, gs.grad_qty, sc.name AS itm_name, am.name AS ac_name, gm.v_date, \n" +
                "gs.kgs AS qty, gs.rate_inr, gs.rate_usd, subc.name as sub_cat_name, mc.name as main_cat_name \n" +
                "FROM `grade_main` gm \n" +
                "LEFT JOIN `grade_sub` gs ON gm.id = gs.id\n" +
                "LEFT JOIN `slab_category` sc ON sc.id = gs.fk_slab_category_id\n" +
                "LEFT JOIN `sub_category` subc ON subc.id = sc.fk_sub_category_id\n" +
                "LEFT JOIN `main_category` mc ON mc.id = subc.fk_main_category_id\n" +
                "LEFT JOIN `account_master` am ON gm.fk_account_master_id = am.id\n" +
                "WHERE gm.id = '"+ ref_no +"'";
        
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        params.put("bill_no", ref_no);
            
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("Breakup.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at Breakup Report In Voucher Display", ex);
        }
    }
    
    private void saleBillReport(String ref_no) {
        String sql = "SELECT sh.rate_dollar_rs, sd.slab, mc.hs_code as bsn_code, sc.name as itm_name, am.name as ac_name, sh.voucher_date as v_date, "
                + "sd.qty, sd.rate AS rate_inr, sd.rate_dollar AS rate_usd \n" +
                "FROM `sale_bill_head` sh \n" +
                "LEFT JOIN `sale_bill_detail` sd ON sh.ref_no = sd.ref_no\n" +
                "LEFT JOIN `slab_category` sc ON sc.id = sd.fk_slab_category_id\n" +
                "LEFT JOIN `main_category` mc ON mc.id = sd.fk_main_category_id\n" +
                "LEFT JOIN `account_master` am ON sh.fk_account_id = am.id\n" +
                "WHERE sh.ref_no = '"+ ref_no +"'";
        
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        params.put("bill_no", ref_no);
            
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("SaleBill.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at Sale bill Report In Voucher Display", ex);
        }
    }
    
    private void purchaseBillReport(String ref_no) {
        String sql = "SELECT ph.rate_dollar_rs, mc.hs_code as bsn_code, sc.name as itm_name, am.name as ac_name, ph.v_date, pd.weight AS qty, pd.rate AS rate_inr, pd.rate_dollar AS rate_usd \n" +
                "FROM `purchase_bill_head` ph \n" +
                "LEFT JOIN `purchase_bill_details` pd ON ph.id = pd.id\n" +
                "LEFT JOIN `sub_category` sc ON sc.id = pd.fk_sub_category_id\n" +
                "LEFT JOIN `main_category` mc ON mc.id = sc.fk_main_category_id\n" +
                "LEFT JOIN `account_master` am ON ph.fk_account_master_id = am.id\n" +
                "WHERE ph.id = '"+ ref_no +"'";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        params.put("bill_no", ref_no);
            
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("PurchaseBill.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at Purchase bill Report In Voucher Display", ex);
        }
    }

    private void groupMasterReport(String ref_no) {
        String sql = "SELECT g.id, g.name, (SELECT g1.name FROM group_master g1 "
                +"WHERE g.head_grp = g1.id) AS headg_name FROM group_master g ORDER BY g.name";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("GroupMaster.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at groupMasterReport In Voucher Display", ex);
        }
    }

    private void accountMasterReport(String ref_no) {
        String sql = "SELECT a.ac_cd, a.ac_name, g.group_name, a.opb_rs, a.ac_eff_rs, a.mo1, a.ph1, a.fax_no, " +
            "a.email1, a.add1, a.add2, a.contact_prsn, a.refby, a.sn, " +
            "am.name AS area_name, am.pincode, c.name AS city_name " +
            "FROM acnt_mst a LEFT JOIN group_mst g ON a.grp_cd = g.grp_cd " +
            "LEFT JOIN city_mst c ON c.ct_cd = a.city_cd " +
            "LEFT JOIN area_mst am ON am.ar_cd = a.area_cd WHERE a.grp_cd = g.grp_cd ORDER BY g.group_name, a.ac_name";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("AccountMaster.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at accountMasterReport In Voucher Display", ex);
        }
    }

    private void companyMasterReport(String ref_no) {
        String sql = "SELECT c.cmpn_name, c.add1, c.add2, cm.name AS city_name, am.name AS area_name, am.pincode, " +
            "c.mob_no, c.phone_no, c.fax_no, c.email, c.cst_no, c.pan_no, c.tin_no, c.sh_name " +
            "FROM cmpny_mst c LEFT JOIN city_mst cm ON cm.ct_cd = c.city_cd " +
            "LEFT JOIN area_mst am ON am.ar_cd = c.area_cd ORDER BY c.cmpn_name";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("CompanyMaster.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at companyMasterReport In Voucher Display", ex);
        }
    }

    private void unitMasterReport(String ref_no) {
        String sql = "SELECT unt_cd, unt_name, unt_symbol FROM unt_mst ORDER BY unt_name";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("UnitMaster.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at unitMasterReport In Voucher Display", ex);
        }
    }

    private void mainItemMasterReport(String ref_no) {
        String sql = "SELECT mnitm_cd, mnitm_name FROM mnitm_mst ORDER BY mnitm_name";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("MainItemMaster.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at mainItemMasterReport In Voucher Display", ex);
        }
    }

    private void itemMasterReport(String ref_no) {
        String sql = "SELECT im.itm_cd, im.itm_name, mb.mnitm_name, im.rate, um.unt_name "
            +"FROM itm_mst im, mnitm_mst mb, unt_mst um WHERE im.mnitm_cd = mb.mnitm_cd AND "
            +"um.unt_cd = im.unt_cd ORDER BY mb.mnitm_name, im.itm_name";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("ItemMaster.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at itemMasterReport In Voucher Display", ex);
        }
    }

    private void taxMasterReport(String ref_no) {
        String sql = "SELECT tax_cd, tax_name, tax FROM tax_mst ORDER BY tax_name";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("TaxMaster.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at taxMasterReport In Voucher Display", ex);
        }
    }

    private void bankMasterReport(String ref_no) {
        String sql = "SELECT bank_cd, bank_name FROM bank_mst ORDER BY bank_name";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("BankMaster.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at bankMasterReport In Voucher Display", ex);
        }
    }

    private void countryMasterReport(String ref_no) {
        String sql = "SELECT cnt_cd AS cntry_id, name AS cntry_name FROM country_mst ORDER BY name";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("CountryMaster.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at countryMasterReport In Voucher Display", ex);
        }
    }

    private void stateMasterReport(String ref_no) {
        String sql = "SELECT s.st_cd AS cntry_id, c.name AS cntry_name, s.name AS state_name FROM state_mst s, country_mst c "
            +"WHERE s.cnt_cd = c.cnt_cd ORDER BY c.name, s.name";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("StateMaster.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at stateMasterReport In Voucher Display", ex);
        }
    }

    private void cityMasterReport(String ref_no) {
        String sql = "SELECT c.ct_cd AS city_id, c.name AS city_name, s.name AS state_name FROM state_mst s, city_mst c "
            +"WHERE c.st_cd = s.st_cd ORDER BY s.name, c.name";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("CityMaster.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at cityMasterReport In Voucher Display", ex);
        }
    }

    private void areaMasterReport(String ref_no) {
        String sql = "SELECT a.ar_cd AS area_id, a.name AS area_name, a.pincode, c.name AS city_name "
            +"FROM area_mst a, city_mst c WHERE a.ct_cd = c.ct_cd ORDER BY c.name, a.name";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("AreaMaster.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at areaMasterReport In Voucher Display", ex);
        }
    }

    private void manageUserReport(String ref_no) {
        String sql = "SELECT user_cd AS userid, username FROM user_mst ORDER BY username";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("ManageUserRpt.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at manageUserReport In Voucher Display", ex);
        }
    }

    private void checkPrintReport(String ref_no) {
        String sql = "SELECT cp.check_cd, cp.party_name, cp.c_date, cp.amount, cp.chq_no, bm.bank_name, "
                +"cp.ac_pay FROM check_print cp LEFT JOIN bank_mst bm ON bm.bank_cd = cp.bank_cd "
                +"WHERE cp.check_cd = '"+ ref_no +"' ORDER BY cp.c_date";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        AmountInWords amt = new AmountInWords();
        try {
            String sql1 = "SELECT * FROM check_print WHERE check_cd = '"+ ref_no +"'";
            PreparedStatement ps = dataConnecrtion.prepareStatement(sql1);
            ResultSet rs = ps.executeQuery();
            String bnk_name = "";
            if (rs.next()) {
                bnk_name = rs.getString("bank_cd");
                params.put("word", amt.convertToWords(rs.getInt("amount")));
            }
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator(""+ bnk_name +".jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at checkPrintReport In Voucher Display", ex);
        }
    }

    private void userRightsReport(String ref_no) {
        String sql = "SELECT ur.user_cd, um.username, mm.menu_name, fm.form_name, ur.views, ur.edit, " +
            "ur.adds, ur.deletes, ur.print, ur.navigate_view  FROM menu_mst mm, form_mst fm, user_rights ur, " +
            "user_mst um WHERE mm.menu_cd = fm.menu_cd AND fm.form_cd = ur.form_cd AND ur.user_cd = um.user_cd " +
            "ORDER BY ur.user_cd, mm.menu_cd, fm.form_cd";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        try {
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("UserRights.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at userRightsReport In Voucher Display", ex);
        }
    }

    private void coverPrint(String ref_no, int mode) {
        String sql = "";
        HashMap params = new HashMap();
        params.put("dir", System.getProperty("user.dir"));
        params.put("digit", lb.getDigit());
        params.put("v_type", Constants.COVER_PRINT);
        params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
        params.put("cadd1", DeskFrame.clSysEnv.getADD1());
        params.put("cadd2", DeskFrame.clSysEnv.getADD2());
        params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
        params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
        params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
        params.put("cphno", DeskFrame.clSysEnv.getPHONE_NO());
        params.put("cemail", DeskFrame.clSysEnv.getEMAIL());
        params.put("cvatno", DeskFrame.clSysEnv.getTIN_NO());
        params.put("ccstno", DeskFrame.clSysEnv.getCST_NO());
        params.put("ctaxno", DeskFrame.clSysEnv.getTAX_NO());
        params.put("cpanno", DeskFrame.clSysEnv.getPAN_NO());
        try {
            if(mode == 0) {
                sql = "SELECT am.ac_name, am.add1, am.add2, am.contact_prsn, am.mo1, am.ph1, cm.name AS city_name " +
                    "FROM slshd sh LEFT JOIN acnt_mst am ON sh.ac_cd = am.ac_cd " +
                    "LEFT JOIN city_mst cm ON cm.ct_cd = am.city_cd WHERE sh.ref_no = '"+ ref_no +"'";
            } else if(mode == 1) {
                sql = "SELECT am.ac_name, am.add1, am.add2, am.contact_prsn, am.mo1, am.ph1, cm.name AS city_name "+
                      "FROM acnt_mst am LEFT JOIN city_mst cm ON cm.ct_cd = am.city_cd WHERE am.ac_cd = '"+ ref_no +"'";
            }
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            print = lb.reportGenerator("CoverPrint.jasper", params, rsLocal, jPanel1);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at coverPrint In Voucher Display", ex);
        }
    }

    private void displayCashReceipt(String ref_no, int mode, int type) {
        HashMap params = new HashMap();
        try {
            String sql = "SELECT ref_no, vdate FROM cprhd WHERE ref_no = '"+ ref_no +"'";
            dataConnecrtion.setAutoCommit(false);
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                params.put("date", lb.ConvertDateFormetForDBForConcurrency(rsLocal.getString("vdate")));
                params.put("voucher", (int)lb.replaceAll(rsLocal.getString("ref_no").substring(tag.length()))+"");
                params.put("dir", System.getProperty("user.dir"));
                params.put("ac_tin_no", lb.getData("SELECT tin_no FROM cprdt c, acnt_mst a WHERE a.ac_cd = c.ac_cd AND ref_no = '"+ ref_no +"'"));
            }
            String view_title = "";
            String email_title = "";
            if(mode == 1) {
                if(type == 0) {
                    view_title = "PAYMENT VOUCHER";
                } else {
                    view_title = Constants.CASH_PAYMENT_FORM_NAME;
                }
                email_title = "CashPayment";
                params.put("v_type", view_title);
            } else if(mode == 2) {
                if(type == 0) {
                    view_title = "RECEIPT VOUCHER";
                } else {
                    view_title = Constants.CASH_RECEIPT_FORM_NAME;
                }
                email_title = "CashReceipt";
                params.put("v_type", view_title);
            }
            params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
            params.put("cadd1", DeskFrame.clSysEnv.getADD1());
            params.put("cadd2", DeskFrame.clSysEnv.getADD2());
            params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
            params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
            params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
            params.put("cphno", DeskFrame.clSysEnv.getPHONE_NO());
            params.put("cemail", DeskFrame.clSysEnv.getEMAIL());
            params.put("cwebsite", DeskFrame.clSysEnv.getWEBSITE());
            params.put("cvatno", DeskFrame.clSysEnv.getTIN_NO());
            params.put("ccstno", DeskFrame.clSysEnv.getCST_NO());
            params.put("ctaxno", DeskFrame.clSysEnv.getTAX_NO());
            params.put("cpanno", DeskFrame.clSysEnv.getPAN_NO());
            params.put("bank_name", DeskFrame.clSysEnv.getBANK_NAME());
            params.put("branch_name", DeskFrame.clSysEnv.getBRANCH_NAME());
            params.put("ifsc_code", DeskFrame.clSysEnv.getIFSC_CODE());
            params.put("ac_no", DeskFrame.clSysEnv.getAC_NO());
            dataConnecrtion.setAutoCommit(true);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
            sql = "SELECT ch.ref_no, ch.vdate, a.ac_name, a.add1, a.add2, a.mo1, cd.bal AS amt, cd.remark " +
                "FROM cprhd ch, cprdt cd, acnt_mst a " +
                "WHERE a.ac_cd = cd.ac_cd AND ch.ref_no = cd.ref_no AND ch.ref_no = '"+ ref_no +"'";
            pstLocal = dataConnecrtion.prepareStatement(sql);
            rsLocal = pstLocal.executeQuery();
            String report = "cashReport.jasper";
            if(type == 0) {
                report = "cashReportWithGST.jasper";
            }
            if(email.equalsIgnoreCase("")) {
                params.put("isshow", DeskFrame.is_show);
                print = lb.reportGenerator(report, params, rsLocal, jPanel1);
            } else {
                params.put("isshow", true);
                if(lb.reportGeneratorInPDF(report, email_title, email, params, rsLocal, jPanel1)) {
                    JOptionPane.showMessageDialog(null, "Successfully Send E-mail", view_title, JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Problem In Send E-mail", view_title, JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at displayCashReceipt In Voucher Display", ex);
        }
    }

    private void displayBankReceipt(String ref_no, int mode, int type) {
        HashMap params = new HashMap();
        try {
            String sql = "SELECT ref_no, vdate FROM bprhd WHERE ref_no='"+ ref_no +"'";
            dataConnecrtion.setAutoCommit(false);
            PreparedStatement pstLocal = dataConnecrtion.prepareStatement(sql);
            ResultSet rsLocal = pstLocal.executeQuery();
            if (rsLocal.next()) {
                params.put("date", lb.ConvertDateFormetForDBForConcurrency(rsLocal.getString("vdate")));
                params.put("voucher", (int)lb.replaceAll(rsLocal.getString("ref_no").substring(tag.length())) +"");
                params.put("dir", System.getProperty("user.dir"));
                params.put("ac_tin_no", lb.getData("SELECT tin_no FROM bprdt c, acnt_mst a WHERE a.ac_cd = c.ac_cd AND ref_no = '"+ ref_no +"'"));
            }
            String view_title = "";
            String email_title = "";
            if(mode == 0) {
                if(type == 0) {
                    view_title = "PAYMENT VOUCHER";
                } else {
                    view_title = Constants.BANK_PAYMENT_FORM_NAME;
                }
                email_title = "BankPayment";
                params.put("v_type", view_title);
            } else if(mode == 1) {
                if(type == 0) {
                    view_title = "RECEIPT VOUCHER";
                } else {
                    view_title = Constants.BANK_RECEIPT_FORM_NAME;
                }
                email_title = "BankReceipt";
                params.put("v_type", view_title);
            }
            params.put("cname", DeskFrame.clSysEnv.getCMPN_NAME());
            params.put("cadd1", DeskFrame.clSysEnv.getADD1());
            params.put("cadd2", DeskFrame.clSysEnv.getADD2());
            params.put("ccorradd1", DeskFrame.clSysEnv.getCORRADD1());
            params.put("ccorradd2", DeskFrame.clSysEnv.getCORRADD2());
            params.put("cmobno", DeskFrame.clSysEnv.getMOB_NO());
            params.put("cphno", DeskFrame.clSysEnv.getPHONE_NO());
            params.put("cemail", DeskFrame.clSysEnv.getEMAIL());
            params.put("cwebsite", DeskFrame.clSysEnv.getWEBSITE());
            params.put("cvatno", DeskFrame.clSysEnv.getTIN_NO());
            params.put("ccstno", DeskFrame.clSysEnv.getCST_NO());
            params.put("ctaxno", DeskFrame.clSysEnv.getTAX_NO());
            params.put("cpanno", DeskFrame.clSysEnv.getPAN_NO());
            params.put("bank_name", DeskFrame.clSysEnv.getBANK_NAME());
            params.put("branch_name", DeskFrame.clSysEnv.getBRANCH_NAME());
            params.put("ifsc_code", DeskFrame.clSysEnv.getIFSC_CODE());
            params.put("ac_no", DeskFrame.clSysEnv.getAC_NO());
            dataConnecrtion.setAutoCommit(true);
            lb.closeResultSet(rsLocal);
            lb.closeStatement(pstLocal);
            sql = "SELECT bh.ref_no, bh.vdate, bh.ctype, am.ac_name AS ac_name, am.add1, am.add2, am.mo1, bd.bal AS amt, bd.chq_dt, " +
                "bd.chq_no, bd.remark AS remark FROM bprhd bh, bprdt bd, acnt_mst am " +
                "WHERE bh.ref_no = bd.ref_no AND am.ac_cd = bd.ac_cd AND bh.ref_no = '"+ ref_no +"' ORDER BY bh.ref_no";
            pstLocal = dataConnecrtion.prepareStatement(sql);
            rsLocal = pstLocal.executeQuery();
            String report = "BankPayRcptReport.jasper";
            if(type == 0) {
                report = "cashReportWithGST.jasper";
            }
            if(email.equalsIgnoreCase("")) {
                params.put("isshow", DeskFrame.is_show);
                print = lb.reportGenerator(report, params, rsLocal, jPanel1);
            } else {
                params.put("isshow", true);
                if(lb.reportGeneratorInPDF(report, email_title, email, params, rsLocal, jPanel1)) {
                    JOptionPane.showMessageDialog(null, "Successfully Send E-mail", view_title, JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Problem In Send E-mail", view_title, JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at displayBankReceipt In Voucher Display", ex);
        }
    }

    private void setIconToPnael() {
        Syspath += File.separator + "Resources" + File.separator + "Images" + File.separator;
        jbtnPrint.setIcon(new ImageIcon(Syspath+"print.png"));
        jbtnClose.setIcon(new ImageIcon(Syspath+"close.png"));
    }

    private void registerShortKeys() {
        lb.setCloseShortcut(this, jbtnClose);
        setIconToPnael();
    }

    @Override
    public void dispose() {
        try {
            DeskFrame.removeFromScreen(DeskFrame.tabbedPane.getSelectedIndex());
            super.dispose();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at dispose In Voucher Display", ex);
        }
    }

    private void printVoucher() {
        try {
            getVoucher(tag, ref_no,false);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at printVoucher In Voucher Display", ex);
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

        jPanel1 = new javax.swing.JPanel();
        jbtnPrint = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();

        setTitle("VOUCHER DISPLAY");

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)));
        jPanel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jbtnPrint.setBackground(new java.awt.Color(204, 255, 204));
        jbtnPrint.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnPrint.setMnemonic('P');
        jbtnPrint.setText("PRINT");
        jbtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPrintActionPerformed(evt);
            }
        });

        jbtnClose.setBackground(new java.awt.Color(204, 255, 204));
        jbtnClose.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jbtnClose.setMnemonic('C');
        jbtnClose.setText("CLOSE");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbtnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnPrint});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbtnPrint, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jbtnClose, jbtnPrint});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPrintActionPerformed
        printVoucher();
        this.dispose();
    }//GEN-LAST:event_jbtnPrintActionPerformed

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnPrint;
    // End of variables declaration//GEN-END:variables
}