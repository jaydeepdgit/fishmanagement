/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dhananistockmanagement;

import static dhananistockmanagement.MainClass.df;
import java.awt.Color;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import support.SysEnv;
import utility.ChangePassword;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import login.CompanySelect;
import master.SubCategory;
import master.MainCategory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.JMenuItem;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import master.AccountMaster;
import master.GroupMaster;
import master.SlabCategory;
import reports.CollectionReport;
import reports.DailyActivityReport;
import reports.GeneralLedger;
import reports.GroupSummary;
import reports.ProfitLoss;
import reports.PurchaseAverage;
import reports.StockSummary;
import reports.Workablity;
import support.Constants;
import utility.BackUp;
import support.Library;
import support.UnCaughtException;
import transaction.BankPayment;
import transaction.BreakUp;
import transaction.CashPaymntRcpt;
import transaction.PurchaseBill;
import transaction.SalesBill;
import utility.ChangeThemes;
import utility.CheckPrint;
import utility.CompanySetting;
import utility.DateSetting;
import utility.ManageEmail;
import utility.QuickOpen;
import utility.UserRights;

/**
 *
 * @author @JD@
 */
public class DeskFrame extends javax.swing.JFrame {
    public static JTabbedPane tabbedPane = new JTabbedPane();
    public static String drName = "Dr";
    public static String crName = "Cr";
    public static int cmp_mnth = 0;
    public static int user_id = 1;
    public static int digit = 2;
    public static String SLS_CHR_LBL = "";
    public static String DELETE_PWD = "";
    public static String theme_cd = "";
    public static boolean is_show = true;
    public static boolean is_retail = false;
    public static boolean multiple_company_data = false;
    private SystemTray systemTray = SystemTray.getSystemTray();
    private TrayIcon trayIcon = null;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    public static Connection connMpAdmin = null;
    public static Connection connMpMain = null;
    public static BufferedWriter logFile = null;
    public static String dbConproperty = "dbConnection.properties";
    public static String currentDirectory = System.getProperty("user.dir").toString();
    public static int mode;
    public static String dbName = "";
    public static SysEnv clSysEnv = new SysEnv();
    public static String TITLE = Constants.SOFTWARE_NAME +" "+ Constants.VERSION;
    private HashMap<Integer, JMenuItem> hashMenu = null;
    private ArrayList<String> hasPermission = new ArrayList<String>();
    public static String ip;
    public static String port;
    public static String db_name;
    public static String driver;
    public static String username;
    public static String password;
    public static String backUpSql;
    public static String sqlBinPath;
    public static Library lb = new Library();
    FileOutputStream errorFile = null;
    public static String date = "";
    public static String dbYear = "", month = "", year = "";
    public static String forms;

    /**
     * Creates new form DeskFrame
     */
    public DeskFrame() {
        initComponents();
        tabbedPane.setBounds(0, 0, jDesktopPane1.getWidth(), jDesktopPane1.getHeight());
        jDesktopPane1.add(tabbedPane);
        tabbedPane.setVisible(true);
        setTrayIcon();
        setShortcut();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(0, 0, (int) screenSize.getWidth(), (int) screenSize.getHeight());
        this.setLocationRelativeTo(null);

        setExtendedState(MAXIMIZED_BOTH);
        setMenuText();
    }

    public DeskFrame(String ComName) {
        initComponents();
        setTitle(Constants.SOFTWARE_NAME +" "+ Constants.VERSION +" - "+ ComName);
        tabbedPane.setBounds(0, 0, jDesktopPane1.getWidth(), jDesktopPane1.getHeight());
        jDesktopPane1.add(tabbedPane);
        tabbedPane.setVisible(true);
        setTrayIcon();
        setShortcut();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(0, 0, (int) screenSize.getWidth(), (int) screenSize.getHeight());
        this.setLocationRelativeTo(null);

        setExtendedState(MAXIMIZED_BOTH);

        jmnloginActionPerformedRoutine();
        setMenuText();
    }

    private void setShortcut() {
        JMenuItem newcmpny = new javax.swing.JMenuItem();
        newcmpny.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        newcmpny.setText("Authentication");
        newcmpny.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewCmpny();
            }
        });
        newcmpny.setVisible(true);
        this.add(newcmpny);
    }

    private void createNewCmpny() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter a Password");
        final JPasswordField pass = new JPasswordField(10);

        KeyListener key = new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_ENTER){
                    evt.consume();
                    if(evt.getSource() == pass) {
                        lb.keyPress(KeyEvent.VK_TAB);
                    }
                }
            }
        };

        pass.addKeyListener(key);
        panel.add(label);
        panel.add(pass);
        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, "Authentication",
                JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, pass);
        if (option == 0) { // pressing OK button
            char[] password = pass.getPassword();

            if(new String(password).equals("india123")){
                int index = checkAlradyOpen(Constants.BACK_UP_FORM_NAME);
                if(index == -1){
//                    NewCompany nc = new NewCompany(MainClass.df, true);
//                    nc.show();
                } else {
                    tabbedPane.setSelectedIndex(index);
                }
            }
        }
        pass.requestFocusInWindow();
    }

    private void setMenuText() {
        // LOGIN
        jmnLogin.setText(Constants.LOGIN_FORM_NAME);
        jmnLogout.setText(Constants.LOG_OUT_FORM_NAME);
        jmnExit.setText(Constants.EXIT_FORM_NAME);
        jmnMinimize.setText(Constants.MINIMIZE_FORM_NAME);

        // MASTER
        jmnGroupMst.setText(Constants.GROUP_MASTER_FORM_NAME);
        jmnAccountMst.setText(Constants.ACCOUNT_MASTER_FORM_NAME);
        jmnMainCategory.setText(Constants.MAIN_CATEGORY_FORM_NAME);
        jmnSubCategory.setText(Constants.SUB_CATEGORY_FORM_NAME);
        jmnSlabCategory.setText(Constants.SLAB_CATEGORY_FORM_NAME);

        // TRANSACTION
        jmnPurchaseBill.setText(Constants.PURCHASE_BILL_FORM_NAME);
        jmnBreakUp.setText(Constants.BREAK_UP_FORM_NAME);
        jmnSalesBill.setText(Constants.SALES_BILL_FORM_NAME);
        jmnCashPayment.setText(Constants.CASH_PAYMENT_FORM_NAME);
        jmnCashReceipt.setText(Constants.CASH_RECEIPT_FORM_NAME);
        jmnBankPayment.setText(Constants.BANK_PAYMENT_FORM_NAME);
        jmnBankReceipt.setText(Constants.BANK_RECEIPT_FORM_NAME);

        // Report
        jmnPurchaseAvarage.setText(Constants.PURCHASE_AVERAGE_FORM_NAME);
        jmnWorkablility.setText(Constants.WORKABILITY_FORM_NAME);
        jmnStockSummary.setText(Constants.STOCK_SUMMARY_FORM_NAME);
        jmnCollectionReport.setText(Constants.COLLECTION_REPORT_FORM_NAME);
        jmnGroupSummary.setText(Constants.GROUP_SUMMARY_FORM_NAME);
        jmnGeneralLedger.setText(Constants.GENERAL_LEDGER_FORM_NAME);

        // UTILITY
        jmnCmpnyStting.setText(Constants.COMPANY_SETTING_FORM_NAME);
        jmnManageUser.setText(Constants.MANAGE_USER_FORM_NAME);
        jmnUserRights.setText(Constants.USER_RIGHTS_FORM_NAME);
        jmnManageEmail.setText(Constants.MANAGE_EMAIL_FORM_NAME);
        jmnChangePassword.setText(Constants.CHANGE_PASSWORD_FORM_NAME);
        jmnChangeDate.setText(Constants.CHANGE_DATE_FORM_NAME);
        jmnQuickOpen.setText(Constants.QUICK_OPEN_FORM_NAME);
        jmnBackUp.setText(Constants.BACK_UP_FORM_NAME);
        jmnCheckPrint.setText(Constants.CHECK_PRINT_FORM_NAME);
        jmnChangeThemes.setText(Constants.CHANGE_THEMES_FORM_NAME);
    }

    private void jmnloginActionPerformedRoutine() {
        openLogFile();
        currentDirectory = System.getProperty("user.dir");
        setTrayIcon();
        login.setEnabled(false);
        setEnabledDisabledMenu(false);
        CompanySelect cp = new CompanySelect(this);
        addOnScreen(cp, Constants.COMPANY_SELECT);
        cp.setStartupFocus();
    }

    public void callLogOFF() {
        jmnLogout.doClick();
    }

    private void openLogFile() {
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_hh_mm_ss aaa");
            File folder = new File("LOG");
            if (!folder.exists()) {
                folder.mkdir();
            }
            File localFile = new File(folder, "logFileCatch" + "_" + sdf.format(cal.getTime()) + ".ini");
            FileWriter fw = new FileWriter(localFile);
            logFile = new BufferedWriter(fw);
            File fileName = new File(folder, "logFileUnCaught" + "_" + sdf.format(cal.getTime()) + ".ini");
            errorFile = new FileOutputStream(fileName, true);
            start();
        } catch (Exception ex) {
            lb.printToLogFile("Exception at makeLogFile In DeskFrame...", ex);
        }
    }

    public void start() {
        // Saving the orginal stream
        PrintStream fileStream = new UnCaughtException(errorFile);
        // Redirecting console output to file
        System.setOut(fileStream);
        // Redirecting runtime exceptions to file
        System.setErr(fileStream);
    }

    private void setTrayIcon() {
        String path = System.getProperty("user.dir") + "/Resources/Images/logo.png";
        try {
            if (systemTray.isSupported()) {
                settrayImage(path);
                trayIcon.displayMessage(Constants.SOFTWARE_NAME +" "+ Constants.VERSION, "", TrayIcon.MessageType.INFO);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void getMainConnection() {
        try {
            Properties property = new Properties();
            property.load(new FileReader(new File(dbConproperty)));
            ip = property.getProperty("ip");
            port = property.getProperty("port");
            db_name = property.getProperty("db_name");
            driver = property.getProperty("driverClassName");
            username = property.getProperty("db_username");
            password = property.getProperty("db_password");
            backUpSql = property.getProperty("backUpSql");
            sqlBinPath = property.getProperty("sqlBinPath");
            connMpMain = getConnection(db_name);
            if (DeskFrame.connMpMain != null) {
                MainClass.appConfig.loadDate();

                df = new DeskFrame("");
                java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                df.setBounds(0, 0, (int) screenSize.getWidth(), (int) screenSize.getHeight());
                df.setLocationRelativeTo(null);
                df.setContentPane(df.tabbedPane);
                df.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Unable to connect Server. Please check Connection Setting", "Connection", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Exception at getMainConnection In DeskFrame", ex);
        }
    }

    public static Connection getConnection(String db) {
        Connection tempCon = null;
        try {
            Class.forName(driver);
            // Establish network connection to database
            Connection connection = DriverManager.getConnection("jdbc:mysql://"+ ip +":"+ port +"/"+ db +"?user="+ username +"&password="+ password +"&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8", username, password);
            return(connection);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at getConnection In DeskFrame", ex);
        }
        return tempCon;
    }

    private void settrayImage(String path) {
        try {
            File imageFile = new File(path);
            Image image = toolkit.getImage(imageFile.toURI().toURL());
            setIconImage(image);
            removeTrayIcon();
            trayIcon = null;
            trayIcon = new TrayIcon(image);
            trayIcon.setImageAutoSize(true);
            systemTray.add(trayIcon);

            trayIcon.setToolTip(Constants.SOFTWARE_NAME +" "+ Constants.VERSION +" Running");
        } catch (Exception ex) {
            lb.printToLogFile("Exception at setTrayImage In DeskFrame", ex);
        }
    }

    private void removeTrayIcon() {
        if (trayIcon != null) {
            systemTray.remove(trayIcon);
        }
    }

    private void createMenuList() {
        hashMenu = new HashMap<Integer, JMenuItem>();
        // MASTER
        hashMenu.put(Integer.parseInt(Constants.GROUP_MASTER_FORM_ID), jmnGroupMst);
        hashMenu.put(Integer.parseInt(Constants.ACCOUNT_MASTER_FORM_ID), jmnAccountMst);
        hashMenu.put(Integer.parseInt(Constants.MAIN_CATEGORY_FORM_ID), jmnMainCategory);
        hashMenu.put(Integer.parseInt(Constants.SUB_CATEGORY_FORM_ID), jmnSubCategory);
        hashMenu.put(Integer.parseInt(Constants.SLAB_CATEGORY_FORM_ID), jmnSlabCategory);

        // TRANSACTION
        hashMenu.put(Integer.parseInt(Constants.PURCHASE_BILL_FORM_ID), jmnPurchaseBill);
        hashMenu.put(Integer.parseInt(Constants.BREAK_UP_FORM_ID), jmnBreakUp);
        hashMenu.put(Integer.parseInt(Constants.SALES_BILL_FORM_ID), jmnSalesBill);
        hashMenu.put(Integer.parseInt(Constants.CASH_PAYMENT_FORM_ID), jmnCashPayment);
        hashMenu.put(Integer.parseInt(Constants.CASH_RECEIPT_FORM_ID), jmnCashReceipt);
        hashMenu.put(Integer.parseInt(Constants.BANK_PAYMENT_FORM_ID), jmnBankPayment);
        hashMenu.put(Integer.parseInt(Constants.BANK_RECEIPT_FORM_ID), jmnBankReceipt);

        //REPORT
        hashMenu.put(Integer.parseInt(Constants.PURCHASE_AVERAGE_FORM_ID), jmnPurchaseAvarage);
        hashMenu.put(Integer.parseInt(Constants.WORKABILITY_FORM_ID), jmnWorkablility);
        hashMenu.put(Integer.parseInt(Constants.STOCK_SUMMARY_FORM_ID), jmnStockSummary);
        hashMenu.put(Integer.parseInt(Constants.COLLECTION_REPORT_FORM_ID), jmnCollectionReport);
        hashMenu.put(Integer.parseInt(Constants.GROUP_SUMMARY_FORM_ID), jmnGroupSummary);
        hashMenu.put(Integer.parseInt(Constants.GENERAL_LEDGER_FORM_ID), jmnGeneralLedger);
        hashMenu.put(Integer.parseInt(Constants.DAILY_ACTIVITY_REPORT_FORM_ID), jmnDailyActivity);

        // UTILITY
        hashMenu.put(Integer.parseInt(Constants.COMPANY_SETTING_FORM_ID), jmnCmpnyStting);
        hashMenu.put(Integer.parseInt(Constants.MANAGE_USER_FORM_ID), jmnManageUser);
        hashMenu.put(Integer.parseInt(Constants.USER_RIGHTS_FORM_ID), jmnUserRights);
        hashMenu.put(Integer.parseInt(Constants.MANAGE_EMAIL_FORM_ID), jmnManageEmail);
        hashMenu.put(Integer.parseInt(Constants.CHANGE_PASSWORD_FORM_ID), jmnChangePassword);
        hashMenu.put(Integer.parseInt(Constants.QUICK_OPEN_FORM_ID), jmnQuickOpen);
        hashMenu.put(Integer.parseInt(Constants.BACK_UP_FORM_ID), jmnBackUp);
        hashMenu.put(Integer.parseInt(Constants.CHECK_PRINT_FORM_ID), jmnCheckPrint);
        hashMenu.put(Integer.parseInt(Constants.CHANGE_THEMES_FORM_ID), jmnChangeThemes);
    }

    public boolean hasPermission(String form) {
        return hasPermission.contains(form);
    }

    public void setPermission() {
        createMenuList();
        PreparedStatement psLocal = null;
        ResultSet rsLocal = null;
        try {
            if (hashMenu != null) {
                Set set = hashMenu.entrySet();
                Iterator i = set.iterator();
                while (i.hasNext()) {
                    Map.Entry me = (Map.Entry) i.next();
                    if (me.getValue() != null) {
                        ((JMenuItem) me.getValue()).setVisible(false);
                    }
                }
                forms = "";
                set = hashMenu.entrySet();
                i = set.iterator();
                while (i.hasNext()) {
                    Map.Entry me = (Map.Entry) i.next();
                    forms += (me.getKey().toString() + ",");
                }
                if (!forms.isEmpty()) {
                    forms = forms.substring(0, forms.length() - 1);
                }
                String sql = "SELECT form_cd, views FROM user_rights WHERE user_cd = ? AND views = 1";
                if (!forms.isEmpty()) {
                    sql += " AND form_cd IN (" + forms + ")";
                }
                psLocal = connMpAdmin.prepareStatement(sql);
                psLocal.setInt(1, user_id);
                rsLocal = psLocal.executeQuery();
                hasPermission.clear();

                while(rsLocal.next()) {
                    hasPermission.add(rsLocal.getString("form_cd"));
                    if (hashMenu.get(rsLocal.getInt("form_cd")) != null) {
                        hashMenu.get(rsLocal.getInt("form_cd")).setVisible(true);
                    }
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Error at setPermission In DeskFrame", ex);
        } finally {
            lb.closeResultSet(rsLocal);
            lb.closeStatement(psLocal);
        }
    }

    public void setUserRights(boolean flag) {
        jmnLogout.setEnabled(flag);
        jmnLogin.setEnabled(!flag);
        master.setEnabled(flag);
        transaction.setEnabled(flag);
        utility.setEnabled(flag);

        setPermission();
    }

    public static void removeFromScreen(int index) {
        tabbedPane.removeTabAt(index);
    }

    public static void removeFromScreen(int index, String name) {
        if (!(index == 0 || name.equalsIgnoreCase(Constants.HOME_PAGE))) {
            tabbedPane.removeTabAt(index);
        }
    }

    public void setEnabledDisabledLogin(boolean flag) {
        jmnLogin.setEnabled(flag);
        jmnLogout.setEnabled(!flag);
    }

    public void setEnabledDisabledMenu(boolean flag) {
        master.setEnabled(flag);
        transaction.setEnabled(flag);
        report.setEnabled(flag);
        utility.setEnabled(flag);
    }

    public static int checkAlradyOpen(String Title) {
        double count = tabbedPane.getTabCount();
        for (int i = 0; i < count; i++) {
            if (tabbedPane.getComponentAt(i).getName().equalsIgnoreCase(Title)) {
                System.out.println("Already Open");
                return i;
            }
        }
        return -1;
    }

    public static void addOnScreen(JInternalFrame inFrame, String title) {
        int index = checkAlradyOpen(title);
        if (index == -1) {
            javax.swing.plaf.InternalFrameUI ifu = inFrame.getUI();
            ((javax.swing.plaf.basic.BasicInternalFrameUI) ifu).setNorthPane(null);
            Border b1 = new LineBorder(Color.darkGray, 5) {
            };
            boolean flag = true;
            if (inFrame instanceof ChangePassword || inFrame instanceof CompanySetting || inFrame instanceof AccountMaster || inFrame instanceof MainCategory || inFrame instanceof SubCategory || inFrame instanceof SlabCategory || inFrame instanceof ManageEmail || inFrame instanceof QuickOpen || inFrame instanceof CheckPrint) {
                flag = false;
            }
            if (flag) {
                inFrame.setLocation(0, 0);
                inFrame.setSize(jDesktopPane1.getWidth(), jDesktopPane1.getHeight());
            }
            inFrame.setBorder(b1);
            JPanel jp = new JPanel();
            if (flag) {
                jp.setLayout(new GridLayout());
            }
            jp.add(inFrame);
            jp.setBackground(new Color(201, 212, 216));
            if (flag) {
                jp.setSize(jDesktopPane1.getWidth(), jDesktopPane1.getHeight());
            }
            jp.setName(title);
            tabbedPane.addTab(title, jp);
            tabbedPane.setSelectedComponent(jp);
            inFrame.setVisible(true);
            inFrame.requestFocusInWindow();
            tabbedPane.setVisible(true);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }

    private void startup() {
        CompanySelect compSelect = new CompanySelect(this);
        addOnScreen(compSelect, Constants.COMPANY_SELECT);
        compSelect.setStartupFocus();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDesktopPane1 = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        login = new javax.swing.JMenu();
        jmnLogin = new javax.swing.JMenuItem();
        jmnLogout = new javax.swing.JMenuItem();
        jmnExit = new javax.swing.JMenuItem();
        jmnMinimize = new javax.swing.JMenuItem();
        master = new javax.swing.JMenu();
        jmnGroupMst = new javax.swing.JMenuItem();
        jmnAccountMst = new javax.swing.JMenuItem();
        jmnMainCategory = new javax.swing.JMenuItem();
        jmnSubCategory = new javax.swing.JMenuItem();
        jmnSlabCategory = new javax.swing.JMenuItem();
        transaction = new javax.swing.JMenu();
        jmnPurchaseBill = new javax.swing.JMenuItem();
        jmnBreakUp = new javax.swing.JMenuItem();
        jmnSalesBill = new javax.swing.JMenuItem();
        jmnCashPayment = new javax.swing.JMenuItem();
        jmnCashReceipt = new javax.swing.JMenuItem();
        jmnBankPayment = new javax.swing.JMenuItem();
        jmnBankReceipt = new javax.swing.JMenuItem();
        report = new javax.swing.JMenu();
        jmnPurchaseAvarage = new javax.swing.JMenuItem();
        jmnWorkablility = new javax.swing.JMenuItem();
        jmnStockSummary = new javax.swing.JMenuItem();
        jmnCollectionReport = new javax.swing.JMenuItem();
        jmnGroupSummary = new javax.swing.JMenuItem();
        jmnGeneralLedger = new javax.swing.JMenuItem();
        jmnDailyActivity = new javax.swing.JMenuItem();
        jmnProfitLoss = new javax.swing.JMenuItem();
        utility = new javax.swing.JMenu();
        jmnCmpnyStting = new javax.swing.JMenuItem();
        jmnManageUser = new javax.swing.JMenuItem();
        jmnUserRights = new javax.swing.JMenuItem();
        jmnManageEmail = new javax.swing.JMenuItem();
        jmnChangePassword = new javax.swing.JMenuItem();
        jmnChangeDate = new javax.swing.JMenuItem();
        jmnQuickOpen = new javax.swing.JMenuItem();
        jmnBackUp = new javax.swing.JMenuItem();
        jmnCheckPrint = new javax.swing.JMenuItem();
        jmnChangeThemes = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 491, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 307, Short.MAX_VALUE)
        );

        login.setMnemonic('L');
        login.setText("LOGIN");
        login.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jmnLogin.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jmnLogin.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnLogin.setMnemonic('L');
        jmnLogin.setText("LOGIN");
        jmnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnLoginActionPerformed(evt);
            }
        });
        login.add(jmnLogin);

        jmnLogout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jmnLogout.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnLogout.setMnemonic('O');
        jmnLogout.setText("LOG OUT");
        jmnLogout.setEnabled(false);
        jmnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnLogoutActionPerformed(evt);
            }
        });
        login.add(jmnLogout);

        jmnExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, java.awt.event.InputEvent.SHIFT_MASK));
        jmnExit.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnExit.setMnemonic('E');
        jmnExit.setText("EXIT");
        jmnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnExitActionPerformed(evt);
            }
        });
        login.add(jmnExit);

        jmnMinimize.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnMinimize.setMnemonic('E');
        jmnMinimize.setText("MINIMIZE");
        jmnMinimize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnMinimizeActionPerformed(evt);
            }
        });
        login.add(jmnMinimize);

        jMenuBar1.add(login);

        master.setMnemonic('M');
        master.setText("MASTER");
        master.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jmnGroupMst.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnGroupMst.setMnemonic('A');
        jmnGroupMst.setText("GROUP MASTER");
        jmnGroupMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnGroupMstActionPerformed(evt);
            }
        });
        master.add(jmnGroupMst);

        jmnAccountMst.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnAccountMst.setMnemonic('A');
        jmnAccountMst.setText("ACCOUNT MASTER");
        jmnAccountMst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnAccountMstActionPerformed(evt);
            }
        });
        master.add(jmnAccountMst);

        jmnMainCategory.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnMainCategory.setMnemonic('M');
        jmnMainCategory.setText("MAIN CATEGORY");
        jmnMainCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnMainCategoryActionPerformed(evt);
            }
        });
        master.add(jmnMainCategory);

        jmnSubCategory.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnSubCategory.setMnemonic('S');
        jmnSubCategory.setText("SUB CATEGORY");
        jmnSubCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSubCategoryActionPerformed(evt);
            }
        });
        master.add(jmnSubCategory);

        jmnSlabCategory.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnSlabCategory.setMnemonic('S');
        jmnSlabCategory.setText("SLAB CATEGORY");
        jmnSlabCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSlabCategoryActionPerformed(evt);
            }
        });
        master.add(jmnSlabCategory);

        jMenuBar1.add(master);

        transaction.setMnemonic('T');
        transaction.setText("TRANSACTION");
        transaction.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jmnPurchaseBill.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnPurchaseBill.setMnemonic('P');
        jmnPurchaseBill.setText("PURCHASE BILL");
        jmnPurchaseBill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnPurchaseBillActionPerformed(evt);
            }
        });
        transaction.add(jmnPurchaseBill);

        jmnBreakUp.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnBreakUp.setMnemonic('B');
        jmnBreakUp.setText("BREAK UP");
        jmnBreakUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnBreakUpActionPerformed(evt);
            }
        });
        transaction.add(jmnBreakUp);

        jmnSalesBill.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnSalesBill.setMnemonic('S');
        jmnSalesBill.setText("SALES BILL");
        jmnSalesBill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnSalesBillActionPerformed(evt);
            }
        });
        transaction.add(jmnSalesBill);

        jmnCashPayment.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnCashPayment.setMnemonic('C');
        jmnCashPayment.setText("CASH PAYMENT");
        jmnCashPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnCashPaymentActionPerformed(evt);
            }
        });
        transaction.add(jmnCashPayment);

        jmnCashReceipt.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnCashReceipt.setMnemonic('C');
        jmnCashReceipt.setText("CASH RECEIPT");
        jmnCashReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnCashReceiptActionPerformed(evt);
            }
        });
        transaction.add(jmnCashReceipt);

        jmnBankPayment.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnBankPayment.setMnemonic('B');
        jmnBankPayment.setText("BANK PAYMENT");
        jmnBankPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnBankPaymentActionPerformed(evt);
            }
        });
        transaction.add(jmnBankPayment);

        jmnBankReceipt.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnBankReceipt.setMnemonic('B');
        jmnBankReceipt.setText("BANK RECEIPT");
        jmnBankReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnBankReceiptActionPerformed(evt);
            }
        });
        transaction.add(jmnBankReceipt);

        jMenuBar1.add(transaction);

        report.setMnemonic('R');
        report.setText("REPORT");
        report.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jmnPurchaseAvarage.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnPurchaseAvarage.setMnemonic('P');
        jmnPurchaseAvarage.setText("PURCHASE AVERAGE");
        jmnPurchaseAvarage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnPurchaseAvarageActionPerformed(evt);
            }
        });
        report.add(jmnPurchaseAvarage);

        jmnWorkablility.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnWorkablility.setMnemonic('W');
        jmnWorkablility.setText("WORKABILITY");
        jmnWorkablility.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnWorkablilityActionPerformed(evt);
            }
        });
        report.add(jmnWorkablility);

        jmnStockSummary.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnStockSummary.setMnemonic('S');
        jmnStockSummary.setText("STOCK SUMMARY");
        jmnStockSummary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnStockSummaryActionPerformed(evt);
            }
        });
        report.add(jmnStockSummary);

        jmnCollectionReport.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnCollectionReport.setMnemonic('C');
        jmnCollectionReport.setText("COLLECTION REPORT");
        jmnCollectionReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnCollectionReportActionPerformed(evt);
            }
        });
        report.add(jmnCollectionReport);

        jmnGroupSummary.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnGroupSummary.setMnemonic('G');
        jmnGroupSummary.setText("GROUP SUMMARY");
        jmnGroupSummary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnGroupSummaryActionPerformed(evt);
            }
        });
        report.add(jmnGroupSummary);

        jmnGeneralLedger.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnGeneralLedger.setMnemonic('G');
        jmnGeneralLedger.setText("GENERAL LEDGER");
        jmnGeneralLedger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnGeneralLedgerActionPerformed(evt);
            }
        });
        report.add(jmnGeneralLedger);

        jmnDailyActivity.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnDailyActivity.setMnemonic('D');
        jmnDailyActivity.setText("DAILY ACTIVITY");
        jmnDailyActivity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnDailyActivityActionPerformed(evt);
            }
        });
        report.add(jmnDailyActivity);

        jmnProfitLoss.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnProfitLoss.setMnemonic('S');
        jmnProfitLoss.setText("PROFIT LOSS");
        jmnProfitLoss.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnProfitLossActionPerformed(evt);
            }
        });
        report.add(jmnProfitLoss);

        jMenuBar1.add(report);

        utility.setMnemonic('U');
        utility.setText("UTILITY");
        utility.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jmnCmpnyStting.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnCmpnyStting.setMnemonic('C');
        jmnCmpnyStting.setText("COMPANY SETTING");
        jmnCmpnyStting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnCmpnySttingActionPerformed(evt);
            }
        });
        utility.add(jmnCmpnyStting);

        jmnManageUser.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnManageUser.setMnemonic('M');
        jmnManageUser.setText("MANAGE USER");
        jmnManageUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnManageUserActionPerformed(evt);
            }
        });
        utility.add(jmnManageUser);

        jmnUserRights.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnUserRights.setMnemonic('U');
        jmnUserRights.setText("USER RIGHTS");
        jmnUserRights.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnUserRightsActionPerformed(evt);
            }
        });
        utility.add(jmnUserRights);

        jmnManageEmail.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnManageEmail.setMnemonic('M');
        jmnManageEmail.setText("MANAGE EMAIL");
        jmnManageEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnManageEmailActionPerformed(evt);
            }
        });
        utility.add(jmnManageEmail);

        jmnChangePassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnChangePassword.setMnemonic('C');
        jmnChangePassword.setText("CHANGE PASSWORD");
        jmnChangePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnChangePasswordActionPerformed(evt);
            }
        });
        utility.add(jmnChangePassword);

        jmnChangeDate.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK));
        jmnChangeDate.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnChangeDate.setMnemonic('D');
        jmnChangeDate.setText("CHANGE DATE");
        jmnChangeDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnChangeDateActionPerformed(evt);
            }
        });
        utility.add(jmnChangeDate);

        jmnQuickOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jmnQuickOpen.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnQuickOpen.setMnemonic('Q');
        jmnQuickOpen.setText("QUICK OPEN");
        jmnQuickOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnQuickOpenActionPerformed(evt);
            }
        });
        utility.add(jmnQuickOpen);

        jmnBackUp.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnBackUp.setMnemonic('B');
        jmnBackUp.setText("BACK UP");
        jmnBackUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnBackUpActionPerformed(evt);
            }
        });
        utility.add(jmnBackUp);

        jmnCheckPrint.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnCheckPrint.setMnemonic('C');
        jmnCheckPrint.setText("CHECK PRINT");
        jmnCheckPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnCheckPrintActionPerformed(evt);
            }
        });
        utility.add(jmnCheckPrint);

        jmnChangeThemes.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jmnChangeThemes.setMnemonic('N');
        jmnChangeThemes.setText("CHANGE THEMES");
        jmnChangeThemes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmnChangeThemesActionPerformed(evt);
            }
        });
        utility.add(jmnChangeThemes);

        jMenuBar1.add(utility);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jDesktopPane1)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jmnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnExitActionPerformed
        if (JOptionPane.showConfirmDialog(null, "Do you want to exit from system?", "Exit", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_jmnExitActionPerformed

    private void jmnChangePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnChangePasswordActionPerformed
        int index = checkAlradyOpen(Constants.CHANGE_PASSWORD_FORM_NAME);
        if (index == -1) {
            ChangePassword cp = new ChangePassword();
            addOnScreen(cp, Constants.CHANGE_PASSWORD_FORM_NAME);
            cp.setTitle(Constants.CHANGE_PASSWORD_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnChangePasswordActionPerformed

    private void jmnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnLoginActionPerformed
        startup();
        jmnLogout.setEnabled(true);
        jmnLogin.setEnabled(false);
    }//GEN-LAST:event_jmnLoginActionPerformed

    private void jmnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnLogoutActionPerformed
        tabbedPane.removeAll();
        setEnabledDisabledLogin(true);
        setEnabledDisabledMenu(false);
        setTitle(TITLE);
    }//GEN-LAST:event_jmnLogoutActionPerformed

    private void jmnManageUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnManageUserActionPerformed
        int index = checkAlradyOpen(Constants.MANAGE_USER_FORM_NAME);
        if (index == -1) {
//            ManageUserView mu = new ManageUserView();
//            addOnScreen(mu, Constants.MANAGE_USER_FORM_NAME);
//            mu.setTitle(Constants.MANAGE_USER_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnManageUserActionPerformed

    private void jmnChangeDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnChangeDateActionPerformed
        int index = checkAlradyOpen(Constants.CHANGE_DATE_FORM_NAME);
        if (index == -1) {
            DateSetting ds = new DateSetting();
            addOnScreen(ds, Constants.CHANGE_DATE_FORM_NAME);
            ds.setTitle(Constants.CHANGE_DATE_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnChangeDateActionPerformed

    private void jmnSubCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSubCategoryActionPerformed
        int index = checkAlradyOpen(Constants.SUB_CATEGORY_FORM_NAME);
        if (index == -1) {
            SubCategory sc = new SubCategory();
            addOnScreen(sc, Constants.SUB_CATEGORY_FORM_NAME);
            sc.setTitle(Constants.SUB_CATEGORY_FORM_NAME);
            sc.setStartupFocus();
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnSubCategoryActionPerformed

    private void jmnCmpnySttingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnCmpnySttingActionPerformed
        int index = checkAlradyOpen(Constants.COMPANY_SETTING_FORM_NAME);
        if (index == -1) {
            CompanySetting cs = new CompanySetting();
            addOnScreen(cs, Constants.COMPANY_SETTING_FORM_NAME);
            cs.setTitle(Constants.COMPANY_SETTING_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnCmpnySttingActionPerformed

    private void jmnMainCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnMainCategoryActionPerformed
        int index = checkAlradyOpen(Constants.MAIN_CATEGORY_FORM_NAME);
        if (index == -1) {
            MainCategory mi = new MainCategory();
            addOnScreen(mi, Constants.MAIN_CATEGORY_FORM_NAME);
            mi.setTitle(Constants.MAIN_CATEGORY_FORM_NAME);
            mi.setStartupFocus();
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnMainCategoryActionPerformed

    private void jmnManageEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnManageEmailActionPerformed
        int index = checkAlradyOpen(Constants.MANAGE_EMAIL_FORM_NAME);
        if(index == -1) {
            ManageEmail tm = new ManageEmail();
            addOnScreen(tm, Constants.MANAGE_EMAIL_FORM_NAME);
            tm.setTitle(Constants.MANAGE_EMAIL_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnManageEmailActionPerformed

    private void jmnUserRightsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnUserRightsActionPerformed
        int index = checkAlradyOpen(Constants.USER_RIGHTS_FORM_NAME);
        if (index == -1) {
            UserRights permission = new UserRights();
            addOnScreen(permission, Constants.USER_RIGHTS_FORM_NAME);
            permission.setTitle(Constants.USER_RIGHTS_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnUserRightsActionPerformed

    private void jmnQuickOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnQuickOpenActionPerformed
        int index = checkAlradyOpen(Constants.QUICK_OPEN_FORM_NAME);
        if(index == -1){
            QuickOpen qp = new QuickOpen();
            addOnScreen(qp, Constants.QUICK_OPEN_FORM_NAME);
            qp.setFocus();
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnQuickOpenActionPerformed

    private void jmnBackUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnBackUpActionPerformed
        int index = checkAlradyOpen(Constants.BACK_UP_FORM_NAME);
        if(index == -1) {
            BackUp bu = new BackUp(MainClass.df, true);
            bu.show();
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnBackUpActionPerformed

    private void jmnCheckPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnCheckPrintActionPerformed
        int index = checkAlradyOpen(Constants.CHECK_PRINT_FORM_NAME);
        if (index == -1) {
            CheckPrint cp = new CheckPrint();
            addOnScreen(cp, Constants.CHECK_PRINT_FORM_NAME);
            cp.setTitle(Constants.CHECK_PRINT_FORM_NAME);
            cp.setStartupFocus();
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnCheckPrintActionPerformed

    private void jmnMinimizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnMinimizeActionPerformed
        df.setState(Frame.ICONIFIED);
    }//GEN-LAST:event_jmnMinimizeActionPerformed

    private void jmnChangeThemesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnChangeThemesActionPerformed
        int index = checkAlradyOpen(Constants.CHANGE_THEMES_FORM_NAME);
        if (index == -1) {
            ChangeThemes ct = new ChangeThemes();
            addOnScreen(ct, Constants.CHANGE_THEMES_FORM_NAME);
            ct.setTitle(Constants.CHANGE_THEMES_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnChangeThemesActionPerformed

    private void jmnSlabCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSlabCategoryActionPerformed
        int index = checkAlradyOpen(Constants.SLAB_CATEGORY_FORM_NAME);
        if (index == -1) {
            SlabCategory sc = new SlabCategory();
            addOnScreen(sc, Constants.SLAB_CATEGORY_FORM_NAME);
            sc.setTitle(Constants.SLAB_CATEGORY_FORM_NAME);
            sc.setStartupFocus();
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnSlabCategoryActionPerformed

    private void jmnPurchaseBillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnPurchaseBillActionPerformed
        int index = checkAlradyOpen(Constants.PURCHASE_BILL_FORM_NAME);
        if (index == -1) {
            PurchaseBill cp = new PurchaseBill();
            addOnScreen(cp, Constants.PURCHASE_BILL_FORM_NAME);
            cp.setTitle(Constants.PURCHASE_BILL_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnPurchaseBillActionPerformed

    private void jmnBreakUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnBreakUpActionPerformed
        int index = checkAlradyOpen(Constants.BREAK_UP_FORM_NAME);
        if (index == -1) {
            BreakUp bu = new BreakUp();
            addOnScreen(bu, Constants.BREAK_UP_FORM_NAME);
            bu.setTitle(Constants.BREAK_UP_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnBreakUpActionPerformed

    private void jmnAccountMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnAccountMstActionPerformed
        int index = checkAlradyOpen(Constants.ACCOUNT_MASTER_FORM_NAME);
        if (index == -1) {
            AccountMaster am = new AccountMaster();
            addOnScreen(am, Constants.ACCOUNT_MASTER_FORM_NAME);
            am.setTitle(Constants.ACCOUNT_MASTER_FORM_NAME);
            am.setStartupFocus();
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnAccountMstActionPerformed

    private void jmnPurchaseAvarageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnPurchaseAvarageActionPerformed
        int index = checkAlradyOpen(Constants.PURCHASE_AVERAGE_FORM_NAME);
        if (index == -1) {
            PurchaseAverage pa = new PurchaseAverage();
            addOnScreen(pa, Constants.PURCHASE_AVERAGE_FORM_NAME);
            pa.setTitle(Constants.PURCHASE_AVERAGE_FORM_NAME);
            pa.setStartupFocus();
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnPurchaseAvarageActionPerformed

    private void jmnSalesBillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnSalesBillActionPerformed
        int index = checkAlradyOpen(Constants.SALES_BILL_FORM_NAME);
        if (index == -1) {
            SalesBill sb = new SalesBill(0);
            addOnScreen(sb, Constants.SALES_BILL_FORM_NAME);
            sb.setTitle(Constants.SALES_BILL_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnSalesBillActionPerformed

    private void jmnWorkablilityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnWorkablilityActionPerformed
        int index = checkAlradyOpen(Constants.WORKABILITY_FORM_NAME);
        if (index == -1) {
            Workablity wo = new Workablity();
            addOnScreen(wo, Constants.WORKABILITY_FORM_NAME);
            wo.setTitle(Constants.WORKABILITY_FORM_NAME);
            wo.setStartupFocus();
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnWorkablilityActionPerformed

    private void jmnCashPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnCashPaymentActionPerformed
        int index = checkAlradyOpen(Constants.CASH_PAYMENT_FORM_NAME);
        if (index == -1) {
            CashPaymntRcpt cp = new CashPaymntRcpt(1);
            addOnScreen(cp, Constants.CASH_PAYMENT_FORM_NAME);
            cp.setTitle(Constants.CASH_PAYMENT_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnCashPaymentActionPerformed

    private void jmnCashReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnCashReceiptActionPerformed
        int index = checkAlradyOpen(Constants.CASH_RECEIPT_FORM_NAME);
        if (index == -1) {
            CashPaymntRcpt cp = new CashPaymntRcpt(2);
            addOnScreen(cp, Constants.CASH_RECEIPT_FORM_NAME);
            cp.setTitle(Constants.CASH_RECEIPT_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnCashReceiptActionPerformed

    private void jmnBankPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnBankPaymentActionPerformed
        int index = checkAlradyOpen(Constants.BANK_PAYMENT_FORM_NAME);
        if (index == -1) {
            BankPayment bp = new BankPayment(0);
            addOnScreen(bp, Constants.BANK_PAYMENT_FORM_NAME);
            bp.setTitle(Constants.BANK_PAYMENT_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnBankPaymentActionPerformed

    private void jmnBankReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnBankReceiptActionPerformed
        int index = checkAlradyOpen(Constants.BANK_RECEIPT_FORM_NAME);
        if (index == -1) {
            BankPayment bp = new BankPayment(1);
            addOnScreen(bp, Constants.BANK_RECEIPT_FORM_NAME);
            bp.setTitle(Constants.BANK_RECEIPT_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnBankReceiptActionPerformed

    private void jmnStockSummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnStockSummaryActionPerformed
        int index = checkAlradyOpen(Constants.STOCK_SUMMARY_FORM_NAME);
        if (index == -1) {
            StockSummary bp = new StockSummary();
            addOnScreen(bp, Constants.STOCK_SUMMARY_FORM_NAME);
            bp.setTitle(Constants.STOCK_SUMMARY_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnStockSummaryActionPerformed

    private void jmnGeneralLedgerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnGeneralLedgerActionPerformed
        int index = checkAlradyOpen(Constants.GENERAL_LEDGER_FORM_NAME);
        if (index == -1) {
            GeneralLedger gl = new GeneralLedger();
            addOnScreen(gl, Constants.GENERAL_LEDGER_FORM_NAME);
            gl.setTitle(Constants.GENERAL_LEDGER_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnGeneralLedgerActionPerformed

    private void jmnCollectionReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnCollectionReportActionPerformed
        int index = checkAlradyOpen(Constants.COLLECTION_REPORT_FORM_NAME);
        if (index == -1) {
            CollectionReport cr = new CollectionReport();
            addOnScreen(cr, Constants.COLLECTION_REPORT_FORM_NAME);
            cr.setTitle(Constants.COLLECTION_REPORT_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnCollectionReportActionPerformed

    private void jmnGroupSummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnGroupSummaryActionPerformed
        int index = checkAlradyOpen(Constants.GROUP_SUMMARY_FORM_NAME);
        if (index == -1) {
            GroupSummary cr = new GroupSummary();
            addOnScreen(cr, Constants.GROUP_SUMMARY_FORM_NAME);
            cr.setTitle(Constants.GROUP_SUMMARY_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnGroupSummaryActionPerformed

    private void jmnGroupMstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnGroupMstActionPerformed
        int index = checkAlradyOpen(Constants.GROUP_MASTER_FORM_NAME);
        if (index == -1) {
            GroupMaster gm = new GroupMaster();
            addOnScreen(gm, Constants.GROUP_MASTER_FORM_NAME);
            gm.setTitle(Constants.GROUP_MASTER_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnGroupMstActionPerformed

    private void jmnDailyActivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnDailyActivityActionPerformed
        int index = checkAlradyOpen(Constants.DAILY_ACTIVITY_REPORT_FORM_NAME);
        if (index == -1) {
            DailyActivityReport dar = new DailyActivityReport();
            addOnScreen(dar, Constants.DAILY_ACTIVITY_REPORT_FORM_NAME);
            dar.setTitle(Constants.DAILY_ACTIVITY_REPORT_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnDailyActivityActionPerformed

    private void jmnProfitLossActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmnProfitLossActionPerformed
        int index = checkAlradyOpen(Constants.PROFIT_LOSS_FORM_NAME);
        if (index == -1) {
            ProfitLoss pl = new ProfitLoss();
            addOnScreen(pl, Constants.PROFIT_LOSS_FORM_NAME);
            pl.setTitle(Constants.PROFIT_LOSS_FORM_NAME);
        } else {
            tabbedPane.setSelectedIndex(index);
        }
    }//GEN-LAST:event_jmnProfitLossActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jmnAccountMst;
    private javax.swing.JMenuItem jmnBackUp;
    private javax.swing.JMenuItem jmnBankPayment;
    private javax.swing.JMenuItem jmnBankReceipt;
    private javax.swing.JMenuItem jmnBreakUp;
    private javax.swing.JMenuItem jmnCashPayment;
    private javax.swing.JMenuItem jmnCashReceipt;
    private javax.swing.JMenuItem jmnChangeDate;
    private javax.swing.JMenuItem jmnChangePassword;
    public static javax.swing.JMenuItem jmnChangeThemes;
    public static javax.swing.JMenuItem jmnCheckPrint;
    private javax.swing.JMenuItem jmnCmpnyStting;
    private javax.swing.JMenuItem jmnCollectionReport;
    private javax.swing.JMenuItem jmnDailyActivity;
    private javax.swing.JMenuItem jmnExit;
    private javax.swing.JMenuItem jmnGeneralLedger;
    private javax.swing.JMenuItem jmnGroupMst;
    private javax.swing.JMenuItem jmnGroupSummary;
    private javax.swing.JMenuItem jmnLogin;
    private javax.swing.JMenuItem jmnLogout;
    private javax.swing.JMenuItem jmnMainCategory;
    private javax.swing.JMenuItem jmnManageEmail;
    private javax.swing.JMenuItem jmnManageUser;
    private javax.swing.JMenuItem jmnMinimize;
    private javax.swing.JMenuItem jmnProfitLoss;
    private javax.swing.JMenuItem jmnPurchaseAvarage;
    private javax.swing.JMenuItem jmnPurchaseBill;
    private javax.swing.JMenuItem jmnQuickOpen;
    private javax.swing.JMenuItem jmnSalesBill;
    private javax.swing.JMenuItem jmnSlabCategory;
    private javax.swing.JMenuItem jmnStockSummary;
    private javax.swing.JMenuItem jmnSubCategory;
    private javax.swing.JMenuItem jmnUserRights;
    private javax.swing.JMenuItem jmnWorkablility;
    public javax.swing.JMenu login;
    private javax.swing.JMenu master;
    private javax.swing.JMenu report;
    private javax.swing.JMenu transaction;
    private javax.swing.JMenu utility;
    // End of variables declaration//GEN-END:variables
}