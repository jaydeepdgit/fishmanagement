/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import dhananistockmanagement.DeskFrame;
import support.Constants;
import support.Library;
import support.PickList;

/**
 *
 * @author @JD@
 */
public class PopUpAccountMaster extends javax.swing.JDialog {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    public static int mode = 0;
    Library lb = new Library();
    public String ac_cd = "";
    private Connection dataConnection = DeskFrame.connMpAdmin;
    boolean form_load = true;
    String Syspath = System.getProperty("user.dir");
    PickList groupPickList = null;
    private PickList cityList, areaList;

    /**
     * Creates new form PopUpTextAreaTaxInvoice
     */
    public PopUpAccountMaster(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setPickListView();
        cancelButton.setIcon(new ImageIcon(Syspath + "close.png"));
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
    }

    public void getData(String textarea, String ac_name, int mode){
        jtxtGroupName.setText(lb.getGroupName(textarea, "N"));
        jtxtAccountName.setText(ac_name);
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    private void setPickListView() {
        groupPickList = new PickList(dataConnection);
        cityList = new PickList(DeskFrame.connMpAdmin);
        areaList = new PickList(DeskFrame.connMpAdmin);

        groupPickList.setLayer(getLayeredPane());
        groupPickList.setPickListComponent(jtxtGroupName);
        groupPickList.setNextComponent(jtxtOPBRs);
        groupPickList.setReturnComponent(new JTextField[]{jtxtGroupName});
        groupPickList.setLocation(jtxtGroupName.getX() + jPanel1.getX(), jPanel1.getY() + jtxtGroupName.getY() + jtxtGroupName.getHeight() + jtxtGroupName.getHeight());

        cityList.setLayer(getLayeredPane());
        cityList.setPickListComponent(jtxtCityID);
        cityList.setNextComponent(jtxtAreaID);
        cityList.setReturnComponent(new JTextField[]{jtxtCityID});
        cityList.setLocation(jPanelContact.getX() + jtxtCityID.getX(), jPanelContact.getY() + jtxtCityID.getY() + jtxtCityID.getHeight()+ jtxtCityID.getHeight());

        areaList.setLayer(getLayeredPane());
        areaList.setPickListComponent(jtxtAreaID);
        areaList.setNextComponent(jtxtEmail1);
        areaList.setReturnComponent(new JTextField[]{jtxtAreaID, jtxtPincode});
        areaList.setLocation(jPanelContact.getX() + jtxtAreaID.getX(), jPanelContact.getY() + jtxtAreaID.getY() + jtxtAreaID.getHeight()+ jtxtAreaID.getHeight());
    }

    private PreparedStatement getPickListStatement(int type) {
        PreparedStatement psLocal = null;
        try {
            switch (type) {
                case 3:
                    psLocal = DeskFrame.connMpAdmin.prepareStatement("SELECT name AS \"City Name\" FROM city_mst WHERE UPPER(name) LIKE '%" + jtxtCityID.getText().toUpperCase() + "%'");
                    break;
                case 4:
                    String city = lb.getField("SELECT ct_cd FROM city_mst WHERE name = '" + jtxtCityID.getText() + "'", DeskFrame.connMpAdmin);
                    if (city != null) {
                        psLocal = DeskFrame.connMpAdmin.prepareStatement("SELECT name AS \"Area Name\", pincode AS \"Pin Code\" FROM area_mst WHERE UPPER(name) LIKE '%" + jtxtAreaID.getText().toUpperCase() + "%' AND ct_cd=" + city);
                    }
                    break;
            }
        } catch (SQLException ex) {
            lb.printToLogFile("Error at getPickListStatement In Account Master", ex);
        }
        return psLocal;
    }

    private int saveVoucher() {
        int i = 0;
        String sql = "";
        try {
            sql = "INSERT INTO acnt_mst(ac_alias, ac_name, grp_cd, ac_eff_rs, opb_rs, lock_date, mo1, "+
                "ph1, fax_no, email1, area_cd, city_cd, add1, add2, contact_prsn, refby, sn, tin_no, pan_no, "+
                "max_rs, min_rs, statecd, transaction_id, check_post, edit_no, user_cd, ac_cd) "+
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, ?)";
            ac_cd = lb.generateKey("acnt_mst", "ac_cd", Constants.ACCOUNT_MASTER_INITIAL, 7);
            dataConnection.setAutoCommit(false);
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            pstLocal.setString(1, jtxtAccountName.getText().toUpperCase()); // ac_alias
            pstLocal.setString(2, jtxtAccountName.getText().toUpperCase()); // ac_name
            pstLocal.setString(3, lb.getGroupName(jtxtGroupName.getText(), "c")); // grp_cd
            pstLocal.setString(4, jcmbRs.getSelectedIndex() + ""); // ac_eff_rs
            pstLocal.setDouble(5, lb.replaceAll(jtxtOPBRs.getText())); // opb_rs
            pstLocal.setString(6, lb.ConvertDateFormetForDB(jtxtLockDate.getText())); // lock_date
            pstLocal.setString(7, jtxtMobile.getText()); // mo1
            pstLocal.setString(8, jtxtPhone.getText()); // ph1
            pstLocal.setString(9, jtxtFaxNo.getText()); // fax_no
            pstLocal.setString(10, jtxtEmail1.getText()); // email1
            if(!lb.getCityCD(jtxtCityID.getText(),"C").equalsIgnoreCase("")){
                pstLocal.setInt(11, (int) lb.isNumber(lb.getAreaCode(jtxtAreaID.getText(),(int) lb.isNumber(lb.getCityCD(jtxtCityID.getText(),"C")),"C"))); // area_cd
            } else {
                pstLocal.setString(11, "0"); // area_cd
            }
            pstLocal.setInt(12, (int) lb.isNumber(lb.getCityCD(jtxtCityID.getText(),"C"))); // city_cd
            pstLocal.setString(13, jtxtAddress1.getText()); // add1
            pstLocal.setString(14, jtxtAddress2.getText()); // add2
            pstLocal.setString(15, jtxtContactName.getText()); // contact_prsn
            pstLocal.setString(16, jtxtReferenceName.getText()); // refby
            pstLocal.setString(17, jtxtShortName.getText()); // sn
            pstLocal.setString(18, jtxtGSTNo.getText()); // tin_no
            pstLocal.setString(19, jtxtPanNo.getText()); // pan_no
            pstLocal.setDouble(20, lb.replaceAll(jtxtMaxRs.getText())); // max_rs
            pstLocal.setDouble(21, lb.replaceAll(jtxtMinRs.getText())); // min_rs
            pstLocal.setString(22, jtxtStateCd.getText()); // statecd
            pstLocal.setString(23, jtxtTransportId.getText()); // transaction_id
            pstLocal.setString(24, jtxtCheckpost.getText()); // check_post
            pstLocal.setInt(25, DeskFrame.user_id); // user_cd
            pstLocal.setString(26, ac_cd); // ac_cd
            i = pstLocal.executeUpdate();
            if (pstLocal != null) {
                pstLocal.close();
            }
            oldbUpdateADD();
            lb.setBalance(ac_cd);
            dataConnection.commit();
            dataConnection.setAutoCommit(true);
        } catch (Exception ex) {
            i = 0;
            lb.printToLogFile("Exception at saveVoucher In Account Master", ex);
            try {
                dataConnection.rollback();
                dataConnection.setAutoCommit(true);
            } catch (Exception ex1) {
                lb.printToLogFile("exception at rollback saveVoucher In Account Master", ex1);
            }
        }
        return i;
    }

    private void oldbUpdateADD() throws SQLException {
        if (lb.getData("ac_cd", "oldb2_1", "ac_cd", ""+ ac_cd +"").equalsIgnoreCase("")) {
            String sql = "INSERT INTO oldb2_1 VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
            pstUpdate.setString(1, ac_cd); // ac_cd
            pstUpdate.setString(2, lb.getGroupName(jtxtGroupName.getText(), "C")); // grp_cd
            if (jcmbRs.getSelectedIndex() == 0) {
                pstUpdate.setDouble(3, lb.replaceAll(jtxtOPBRs.getText())); // opb
                pstUpdate.setString(4, "0"); // dr
                pstUpdate.setString(5, "0"); // cr
                pstUpdate.setDouble(6, lb.replaceAll(jtxtOPBRs.getText())); // bal
            } else if (jcmbRs.getSelectedIndex() == 1) {
                pstUpdate.setString(3, "-" + lb.replaceAll(jtxtOPBRs.getText())); // opb
                pstUpdate.setString(4, "0"); // dr
                pstUpdate.setString(5, "0"); // cr
                pstUpdate.setString(6, "-" + lb.replaceAll(jtxtOPBRs.getText())); // bal
            }
            pstUpdate.executeUpdate();
            lb.closeStatement(pstUpdate);
        } else {
            String sql = "UPDATE oldb2_1 SET opb = ?, grp_cd = ? WHERE ac_cd = ?";
            PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
            if (jcmbRs.getSelectedIndex() == 0) {
                pstUpdate.setDouble(1, lb.replaceAll(jtxtOPBRs.getText())); // opb
            } else if (jcmbRs.getSelectedIndex() == 1) {
                pstUpdate.setString(1, "-" + lb.replaceAll(jtxtOPBRs.getText())); // opb
            }
            pstUpdate.setString(2, lb.getGroupName(jtxtGroupName.getText(), "C")); // grp_cd
            pstUpdate.setString(3, ac_cd); // ac_cd
            pstUpdate.executeUpdate();
            lb.closeStatement(pstUpdate);
        }

        String sql = "DELETE FROM oldb2_2 WHERE doc_ref_no = '"+ Constants.OPB_INITIAL +"' AND ac_cd = ?";
        PreparedStatement pstUpdate = dataConnection.prepareStatement(sql);
        pstUpdate.setString(1, ac_cd); // ac_cd
        pstUpdate.executeUpdate();

        sql = "INSERT INTO oldb2_2 (doc_ref_no, doc_cd, doc_date, ac_cd, drcr, val, particular, fix_time, opp_ac_cd)"
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        pstUpdate = dataConnection.prepareStatement(sql);
        pstUpdate.setString(1, Constants.OPB_INITIAL); // doc_ref_no
        pstUpdate.setString(2, Constants.OPB_INITIAL); // doc_cd
        pstUpdate.setString(3, lb.ConvertDateFormetForDB(DeskFrame.date)); // doc_date
        pstUpdate.setString(4, ac_cd); // ac_cd
        if (jcmbRs.getSelectedIndex() == 0) {
            pstUpdate.setString(5, "0"); // drcr
        } else {
            pstUpdate.setString(5, "1"); // drcr
        }
        pstUpdate.setDouble(6, lb.replaceAll(jtxtOPBRs.getText())); // val
        pstUpdate.setString(7, ""); // particular
        pstUpdate.setString(8, "00:00:00"); // fix_time
        pstUpdate.setString(9, ""); // opp_ac_cd
        pstUpdate.executeUpdate();
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
        jlblMsg = new javax.swing.JLabel();
        jPanelContact = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jtxtContactName = new javax.swing.JTextField();
        jtxtAddress1 = new javax.swing.JTextField();
        jtxtAddress2 = new javax.swing.JTextField();
        jtxtCityID = new javax.swing.JTextField();
        jtxtAreaID = new javax.swing.JTextField();
        jtxtPincode = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jtxtMobile = new javax.swing.JTextField();
        jtxtEmail1 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jtxtPhone = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jtxtFaxNo = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jtxtGSTNo = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jtxtPanNo = new javax.swing.JTextField();
        jtxtTransportId = new javax.swing.JTextField();
        jtxtCheckpost = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jtxtStateCd = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jtxtLockDate = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jtxtAccountCD = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jcmbRs = new javax.swing.JComboBox();
        jtxtAccountName = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jtxtOPBRs = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jtxtGroupName = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jtxtReferenceName = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jtxtShortName = new javax.swing.JTextField();
        jtxtMaxRs = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jtxtMinRs = new javax.swing.JTextField();

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
        okButton.setMnemonic('O');
        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jlblMsg.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jlblMsg.setForeground(new java.awt.Color(255, 0, 0));
        jlblMsg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jPanelContact.setBackground(new java.awt.Color(215, 227, 208));
        jPanelContact.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(53, 154, 141)), "Contact Details", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 16), new java.awt.Color(235, 35, 35))); // NOI18N

        jLabel20.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel20.setText("Pincode");

        jLabel21.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel21.setText("Area");

        jLabel22.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel22.setText("City");

        jLabel23.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel23.setText("Address");

        jLabel24.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel24.setText("Contact Person");

        jtxtContactName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtContactName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtContactName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtContactNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtContactNameFocusLost(evt);
            }
        });
        jtxtContactName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtContactNameKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtContactNameKeyTyped(evt);
            }
        });

        jtxtAddress1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtAddress1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAddress1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAddress1FocusGained(evt);
            }
        });
        jtxtAddress1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAddress1KeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAddress1KeyTyped(evt);
            }
        });

        jtxtAddress2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtAddress2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAddress2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAddress2FocusGained(evt);
            }
        });
        jtxtAddress2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAddress2KeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAddress2KeyTyped(evt);
            }
        });

        jtxtCityID.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtCityID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtCityID.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCityIDFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtCityIDFocusLost(evt);
            }
        });
        jtxtCityID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCityIDKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtCityIDKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtCityIDKeyTyped(evt);
            }
        });

        jtxtAreaID.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtAreaID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAreaID.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAreaIDFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAreaIDFocusLost(evt);
            }
        });
        jtxtAreaID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAreaIDKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtxtAreaIDKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAreaIDKeyTyped(evt);
            }
        });

        jtxtPincode.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtPincode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtPincode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPincodeFocusGained(evt);
            }
        });
        jtxtPincode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPincodeKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtPincodeKeyTyped(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel25.setText("Email (1)");

        jLabel26.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel26.setText("Mobile");

        jtxtMobile.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtMobile.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtMobile.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtMobileFocusGained(evt);
            }
        });
        jtxtMobile.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMobileKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtMobileKeyTyped(evt);
            }
        });

        jtxtEmail1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtEmail1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtEmail1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtEmail1FocusGained(evt);
            }
        });
        jtxtEmail1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtEmail1KeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtEmail1KeyTyped(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel27.setText("Phone");

        jtxtPhone.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtPhone.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtPhone.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPhoneFocusGained(evt);
            }
        });
        jtxtPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPhoneKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtPhoneKeyTyped(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel28.setText("Fax No");

        jtxtFaxNo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtFaxNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtFaxNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtFaxNoFocusGained(evt);
            }
        });
        jtxtFaxNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtFaxNoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtFaxNoKeyTyped(evt);
            }
        });

        jLabel37.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel37.setText("GST No");

        jtxtGSTNo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtGSTNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtGSTNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtGSTNoFocusGained(evt);
            }
        });
        jtxtGSTNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtGSTNoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtGSTNoKeyTyped(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel38.setText("Pan No.");

        jtxtPanNo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtPanNo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtPanNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtPanNoFocusGained(evt);
            }
        });
        jtxtPanNo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtPanNoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtPanNoKeyTyped(evt);
            }
        });

        jtxtTransportId.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtTransportId.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtTransportId.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtTransportIdFocusGained(evt);
            }
        });
        jtxtTransportId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtTransportIdKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtTransportIdKeyTyped(evt);
            }
        });

        jtxtCheckpost.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtCheckpost.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtCheckpost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCheckpostFocusGained(evt);
            }
        });
        jtxtCheckpost.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtCheckpostKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtCheckpostKeyTyped(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel29.setText("Transport Id");

        jLabel32.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel32.setText("Checkpost");

        jLabel33.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel33.setText("State Code");

        jtxtStateCd.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtStateCd.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtStateCd.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtStateCdFocusGained(evt);
            }
        });
        jtxtStateCd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtStateCdKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtStateCdKeyTyped(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 0, 0));
        jLabel18.setText("Lock Date");

        jtxtLockDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jtxtLockDate.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtLockDate.setMinimumSize(new java.awt.Dimension(2, 20));
        jtxtLockDate.setPreferredSize(new java.awt.Dimension(2, 20));
        jtxtLockDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtLockDateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtLockDateFocusLost(evt);
            }
        });
        jtxtLockDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtLockDateKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanelContactLayout = new javax.swing.GroupLayout(jPanelContact);
        jPanelContact.setLayout(jPanelContactLayout);
        jPanelContactLayout.setHorizontalGroup(
            jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContactLayout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtContactName, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAddress1, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAddress2, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtEmail1, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtFaxNo, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtGSTNo, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtTransportId, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtStateCd, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jtxtPanNo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                        .addComponent(jtxtPhone, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jtxtMobile, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jtxtPincode, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jtxtAreaID, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jtxtCityID, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jtxtCheckpost))
                    .addComponent(jtxtLockDate, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jPanelContactLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel23, jLabel24, jLabel25, jLabel28, jLabel29, jLabel33, jLabel37});

        jPanelContactLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel18, jLabel20, jLabel21, jLabel22, jLabel26, jLabel27, jLabel32, jLabel38});

        jPanelContactLayout.setVerticalGroup(
            jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContactLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelContactLayout.createSequentialGroup()
                        .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jtxtCityID)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(jLabel21)
                            .addComponent(jtxtAreaID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jtxtPincode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtMobile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtPanNo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtCheckpost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32)
                            .addComponent(jLabel29))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelContactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jtxtLockDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel33)))
                    .addGroup(jPanelContactLayout.createSequentialGroup()
                        .addComponent(jtxtContactName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAddress1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtAddress2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtEmail1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtFaxNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtGSTNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtTransportId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtStateCd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7))
        );

        jPanelContactLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel22, jLabel24, jtxtCityID, jtxtContactName});

        jPanelContactLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel21, jLabel23, jtxtAddress1, jtxtAreaID});

        jPanelContactLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel20, jtxtAddress2, jtxtPincode});

        jPanelContactLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel25, jLabel26, jtxtEmail1, jtxtMobile});

        jPanelContactLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel27, jLabel28, jtxtFaxNo, jtxtPhone});

        jPanelContactLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel37, jLabel38, jtxtGSTNo, jtxtPanNo});

        jPanelContactLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel29, jLabel32, jtxtCheckpost, jtxtTransportId});

        jPanelContactLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel33, jtxtStateCd});

        jPanel1.setBackground(new java.awt.Color(253, 243, 243));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 3, 3, new java.awt.Color(235, 35, 35)), "Account Information", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 2, 16), new java.awt.Color(0, 0, 255))); // NOI18N

        jtxtAccountCD.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtAccountCD.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtAccountCD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtAccountCDFocusGained(evt);
            }
        });
        jtxtAccountCD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAccountCDKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAccountCDKeyTyped(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel6.setText("A/C Eff Rs");

        jcmbRs.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jcmbRs.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dr", "Cr" }));
        jcmbRs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jcmbRs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jcmbRsKeyPressed(evt);
            }
        });

        jtxtAccountName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtAccountName.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
        jtxtAccountName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtAccountNameFocusLost(evt);
            }
        });
        jtxtAccountName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtAccountNameKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtAccountNameKeyTyped(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel8.setText("Account Code");

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 0, 0));
        jLabel3.setText("Account Name");

        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel4.setText("OPB Rs");

        jtxtOPBRs.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtOPBRs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtOPBRs.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtOPBRsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtOPBRsFocusLost(evt);
            }
        });
        jtxtOPBRs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtOPBRsKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtOPBRsKeyTyped(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("Group Name");

        jtxtGroupName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtGroupName.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));
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

        jLabel30.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel30.setText("Ref. Name");

        jtxtReferenceName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtReferenceName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtReferenceName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtReferenceNameFocusGained(evt);
            }
        });
        jtxtReferenceName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtReferenceNameKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtReferenceNameKeyTyped(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel31.setText("Short Name");

        jtxtShortName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtShortName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtShortName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtShortNameFocusGained(evt);
            }
        });
        jtxtShortName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtShortNameKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtShortNameKeyTyped(evt);
            }
        });

        jtxtMaxRs.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtMaxRs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtMaxRs.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtMaxRsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtMaxRsFocusLost(evt);
            }
        });
        jtxtMaxRs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMaxRsKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtMaxRsKeyTyped(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel5.setText("Max Rs");

        jLabel7.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel7.setText("Min Rs");

        jtxtMinRs.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jtxtMinRs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtMinRs.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtMinRsFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtMinRsFocusLost(evt);
            }
        });
        jtxtMinRs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jtxtMinRsKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jtxtMinRsKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtMaxRs, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtOPBRs, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAccountName, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAccountCD, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtReferenceName, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmbRs, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMinRs, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtShortName, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel3, jLabel30, jLabel31, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jcmbRs, jtxtGroupName, jtxtMinRs, jtxtShortName});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jtxtAccountCD, jtxtAccountName, jtxtMaxRs, jtxtOPBRs, jtxtReferenceName});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAccountCD, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtAccountName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcmbRs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtOPBRs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtMinRs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtMaxRs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtShortName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtReferenceName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel3, jtxtAccountName, jtxtGroupName});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel5, jLabel7, jtxtMaxRs, jtxtMinRs});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel8, jtxtAccountCD});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel4, jLabel6, jcmbRs, jtxtOPBRs});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel30, jLabel31, jtxtReferenceName, jtxtShortName});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlblMsg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 598, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelContact, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelContact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlblMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(okButton)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cancelButton, okButton});

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
        if(saveVoucher() != 0) {
            JOptionPane.showMessageDialog(this, "Account Master is Sucessfully Added", Constants.ACCOUNT_MASTER_FORM_NAME, JOptionPane.INFORMATION_MESSAGE);
        }
        doClose(RET_CANCEL);
    }//GEN-LAST:event_okButtonActionPerformed

    private void jtxtContactNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtContactNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtContactNameFocusGained

    private void jtxtContactNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtContactNameFocusLost
        if (jtxtContactName.getText().trim().isEmpty()) {
            jtxtContactName.setText(jtxtAccountName.getText());
        }
    }//GEN-LAST:event_jtxtContactNameFocusLost

    private void jtxtContactNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtContactNameKeyPressed
        lb.enterFocus(evt, jtxtAddress1);
    }//GEN-LAST:event_jtxtContactNameKeyPressed

    private void jtxtContactNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtContactNameKeyTyped
        lb.fixLength(evt, 100);
    }//GEN-LAST:event_jtxtContactNameKeyTyped

    private void jtxtAddress1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAddress1FocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAddress1FocusGained

    private void jtxtAddress1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAddress1KeyPressed
        lb.enterFocus(evt, jtxtAddress2);
    }//GEN-LAST:event_jtxtAddress1KeyPressed

    private void jtxtAddress1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAddress1KeyTyped
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtAddress1KeyTyped

    private void jtxtAddress2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAddress2FocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAddress2FocusGained

    private void jtxtAddress2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAddress2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jtxtCityID.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtAddress2KeyPressed

    private void jtxtAddress2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAddress2KeyTyped
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtAddress2KeyTyped

    private void jtxtCityIDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCityIDFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCityIDFocusGained

    private void jtxtCityIDFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCityIDFocusLost
        cityList.setVisible(false);
    }//GEN-LAST:event_jtxtCityIDFocusLost

    private void jtxtCityIDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCityIDKeyPressed
        cityList.pickListKeyPress(evt);
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            if (!lb.isBlank(jtxtCityID)) {
                if (!lb.isExist("city_mst", "name", jtxtCityID.getText().toUpperCase())) {
                    lb.confirmDialog("City not exist. Do you want to Add City");
                    if (lb.type) {
                        mode = 3;
                    } else {
                        jtxtCityID.requestFocusInWindow();
                    }
                } else {
                    lb.enterFocus(evt, jtxtAreaID);
                }
            } else {
                lb.enterFocus(evt, jtxtAreaID);
            }
        }
    }//GEN-LAST:event_jtxtCityIDKeyPressed

    private void jtxtCityIDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCityIDKeyReleased
        cityList.setLocation(jPanelContact.getX() + jtxtCityID.getX(), jPanelContact.getY() + jtxtCityID.getY() + jtxtCityID.getHeight());
        cityList.setFirstAssociation(new int[]{0});
        cityList.setSecondAssociation(new int[]{0});
        PreparedStatement psLocal = getPickListStatement(3);
        if (psLocal != null) {
            cityList.setPreparedStatement(psLocal);
            cityList.pickListKeyRelease(evt);
        }
    }//GEN-LAST:event_jtxtCityIDKeyReleased

    private void jtxtCityIDKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCityIDKeyTyped
        lb.fixLength(evt, 50);
    }//GEN-LAST:event_jtxtCityIDKeyTyped

    private void jtxtAreaIDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAreaIDFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAreaIDFocusGained

    private void jtxtAreaIDFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAreaIDFocusLost
        areaList.setVisible(false);
    }//GEN-LAST:event_jtxtAreaIDFocusLost

    private void jtxtAreaIDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAreaIDKeyPressed
        areaList.pickListKeyPress(evt);
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            if (!lb.isBlank(jtxtCityID)) {
                if (!lb.isBlank(jtxtAreaID)) {
                    if (!lb.isExist("area_mst", "name", jtxtAreaID.getText().toUpperCase())) {
                        lb.confirmDialog("Area not exist. Do you want to Add Area");
                        if (lb.type) {
                            mode = 4;
                        } else {
                            jtxtAreaID.requestFocusInWindow();
                        }
                    }
                } else {
                    lb.enterFocus(evt, jtxtEmail1);
                }
            } else {
                lb.enterFocus(evt, jtxtEmail1);
            }
        }
    }//GEN-LAST:event_jtxtAreaIDKeyPressed

    private void jtxtAreaIDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAreaIDKeyReleased
        areaList.setLocation(jPanelContact.getX() + jtxtAreaID.getX(), jPanelContact.getY() + jtxtAreaID.getY() + jtxtAreaID.getHeight());
        areaList.setFirstAssociation(new int[]{0, 1});
        areaList.setSecondAssociation(new int[]{0, 1});
        PreparedStatement psLocal = getPickListStatement(4);
        if (psLocal != null) {
            areaList.setPreparedStatement(psLocal);
            areaList.pickListKeyRelease(evt);
        }
    }//GEN-LAST:event_jtxtAreaIDKeyReleased

    private void jtxtAreaIDKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAreaIDKeyTyped
        lb.fixLength(evt, 50);
    }//GEN-LAST:event_jtxtAreaIDKeyTyped

    private void jtxtPincodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPincodeFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPincodeFocusGained

    private void jtxtPincodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPincodeKeyPressed
        lb.enterFocus(evt, jtxtMobile);
    }//GEN-LAST:event_jtxtPincodeKeyPressed

    private void jtxtPincodeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPincodeKeyTyped
        lb.onlyNumber(evt, 10);
    }//GEN-LAST:event_jtxtPincodeKeyTyped

    private void jtxtMobileFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMobileFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMobileFocusGained

    private void jtxtMobileKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMobileKeyPressed
        lb.enterFocus(evt, jtxtFaxNo);
    }//GEN-LAST:event_jtxtMobileKeyPressed

    private void jtxtMobileKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMobileKeyTyped
        lb.onlyNumber(evt, 17);
    }//GEN-LAST:event_jtxtMobileKeyTyped

    private void jtxtEmail1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtEmail1FocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtEmail1FocusGained

    private void jtxtEmail1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtEmail1KeyPressed
        lb.enterFocus(evt, jtxtMobile);
    }//GEN-LAST:event_jtxtEmail1KeyPressed

    private void jtxtEmail1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtEmail1KeyTyped
        lb.fixLength(evt, 100);
    }//GEN-LAST:event_jtxtEmail1KeyTyped

    private void jtxtPhoneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPhoneFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPhoneFocusGained

    private void jtxtPhoneKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPhoneKeyPressed
        lb.enterFocus(evt, jtxtGSTNo);
    }//GEN-LAST:event_jtxtPhoneKeyPressed

    private void jtxtPhoneKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPhoneKeyTyped
        lb.onlyNumber(evt, 17);
    }//GEN-LAST:event_jtxtPhoneKeyTyped

    private void jtxtFaxNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFaxNoFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtFaxNoFocusGained

    private void jtxtFaxNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFaxNoKeyPressed
        lb.enterFocus(evt, jtxtPhone);
    }//GEN-LAST:event_jtxtFaxNoKeyPressed

    private void jtxtFaxNoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFaxNoKeyTyped
        lb.fixLength(evt, 17);
    }//GEN-LAST:event_jtxtFaxNoKeyTyped

    private void jtxtGSTNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGSTNoFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtGSTNoFocusGained

    private void jtxtGSTNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGSTNoKeyPressed
        lb.enterEvent(evt, jtxtPanNo);
    }//GEN-LAST:event_jtxtGSTNoKeyPressed

    private void jtxtGSTNoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGSTNoKeyTyped
        lb.fixLength(evt, 15);
    }//GEN-LAST:event_jtxtGSTNoKeyTyped

    private void jtxtPanNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPanNoFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtPanNoFocusGained

    private void jtxtPanNoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPanNoKeyPressed
        lb.enterEvent(evt, jtxtStateCd);
    }//GEN-LAST:event_jtxtPanNoKeyPressed

    private void jtxtPanNoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtPanNoKeyTyped
        lb.fixLength(evt, 10);
    }//GEN-LAST:event_jtxtPanNoKeyTyped

    private void jtxtTransportIdFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTransportIdFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtTransportIdFocusGained

    private void jtxtTransportIdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTransportIdKeyPressed
        lb.enterEvent(evt, jtxtCheckpost);
    }//GEN-LAST:event_jtxtTransportIdKeyPressed

    private void jtxtTransportIdKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtTransportIdKeyTyped
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtTransportIdKeyTyped

    private void jtxtCheckpostFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCheckpostFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtCheckpostFocusGained

    private void jtxtCheckpostKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCheckpostKeyPressed
        lb.enterEvent(evt, jtxtStateCd);
    }//GEN-LAST:event_jtxtCheckpostKeyPressed

    private void jtxtCheckpostKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtCheckpostKeyTyped
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtCheckpostKeyTyped

    private void jtxtStateCdFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtStateCdFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtStateCdFocusGained

    private void jtxtStateCdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtStateCdKeyPressed
        lb.enterEvent(evt, jtxtLockDate);
    }//GEN-LAST:event_jtxtStateCdKeyPressed

    private void jtxtStateCdKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtStateCdKeyTyped
        lb.fixLength(evt, 7);
    }//GEN-LAST:event_jtxtStateCdKeyTyped

    private void jtxtAccountCDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAccountCDFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtAccountCDFocusGained

    private void jtxtAccountCDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAccountCDKeyPressed
        
    }//GEN-LAST:event_jtxtAccountCDKeyPressed

    private void jtxtAccountCDKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAccountCDKeyTyped
        lb.fixLength(evt, 7);
    }//GEN-LAST:event_jtxtAccountCDKeyTyped

    private void jcmbRsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jcmbRsKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            evt.consume();
            jtxtMaxRs.requestFocusInWindow();
        }
    }//GEN-LAST:event_jcmbRsKeyPressed

    private void jtxtAccountNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtAccountNameFocusLost
        if (lb.checkAccountName(jtxtAccountName.getText(), "")) {
            jtxtAccountName.setText(jtxtAccountName.getText().toUpperCase());
            jlblMsg.setText("");
        } else {
            jlblMsg.setText("Account Name already exist!");
            jtxtAccountName.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtAccountNameFocusLost

    private void jtxtAccountNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAccountNameKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            evt.consume();
            jtxtGroupName.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtAccountNameKeyPressed

    private void jtxtAccountNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtAccountNameKeyTyped
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtAccountNameKeyTyped

    private void jtxtOPBRsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtOPBRsFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtOPBRsFocusGained

    private void jtxtOPBRsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtOPBRsFocusLost
        if (jtxtOPBRs.getText().trim().equalsIgnoreCase("")) {
            jtxtOPBRs.setText("0");
        }
    }//GEN-LAST:event_jtxtOPBRsFocusLost

    private void jtxtOPBRsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtOPBRsKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            evt.consume();
            jcmbRs.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtOPBRsKeyPressed

    private void jtxtOPBRsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtOPBRsKeyTyped
        lb.onlyNumber(evt, 12);
    }//GEN-LAST:event_jtxtOPBRsKeyTyped

    private void jtxtGroupNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGroupNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtGroupNameFocusGained

    private void jtxtGroupNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtGroupNameFocusLost
        groupPickList.setVisible(false);
    }//GEN-LAST:event_jtxtGroupNameFocusLost

    private void jtxtGroupNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupNameKeyPressed
        groupPickList.setLocation(jtxtGroupName.getX() + jPanel1.getX(), jPanel1.getY() + jtxtGroupName.getY() + jtxtGroupName.getHeight());
        groupPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtGroupNameKeyPressed

    private void jtxtGroupNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupNameKeyReleased
        try {
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT group_name FROM group_mst WHERE group_name LIKE '%" + jtxtGroupName.getText().toUpperCase() + "%'");
            groupPickList.setPreparedStatement(pstLocal);
            groupPickList.setValidation(dataConnection.prepareStatement("SELECT group_name FROM group_mst WHERE group_name = ?"));
            groupPickList.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtGroupNameKeyReleased In Account Master", ex);
        }
    }//GEN-LAST:event_jtxtGroupNameKeyReleased

    private void jtxtGroupNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtGroupNameKeyTyped
        lb.fixLength(evt, 50);
    }//GEN-LAST:event_jtxtGroupNameKeyTyped

    private void jtxtReferenceNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtReferenceNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtReferenceNameFocusGained

    private void jtxtReferenceNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtReferenceNameKeyPressed
        lb.enterFocus(evt, jtxtShortName);
    }//GEN-LAST:event_jtxtReferenceNameKeyPressed

    private void jtxtReferenceNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtReferenceNameKeyTyped
        lb.fixLength(evt, 100);
    }//GEN-LAST:event_jtxtReferenceNameKeyTyped

    private void jtxtShortNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtShortNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtShortNameFocusGained

    private void jtxtShortNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtShortNameKeyPressed
        lb.enterFocus(evt, jtxtContactName);
    }//GEN-LAST:event_jtxtShortNameKeyPressed

    private void jtxtShortNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtShortNameKeyTyped
        lb.fixLength(evt, 5);
    }//GEN-LAST:event_jtxtShortNameKeyTyped

    private void jtxtMaxRsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMaxRsFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMaxRsFocusGained

    private void jtxtMaxRsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMaxRsFocusLost
        if (jtxtMaxRs.getText().trim().equalsIgnoreCase("")) {
            jtxtMaxRs.setText("0");
        }
    }//GEN-LAST:event_jtxtMaxRsFocusLost

    private void jtxtMaxRsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMaxRsKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            evt.consume();
            jtxtMinRs.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtMaxRsKeyPressed

    private void jtxtMaxRsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMaxRsKeyTyped
        lb.onlyNumber(evt, 12);
    }//GEN-LAST:event_jtxtMaxRsKeyTyped

    private void jtxtMinRsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMinRsFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMinRsFocusGained

    private void jtxtMinRsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMinRsFocusLost
        if (jtxtMinRs.getText().trim().equalsIgnoreCase("")) {
            jtxtMinRs.setText("0");
        }
    }//GEN-LAST:event_jtxtMinRsFocusLost

    private void jtxtMinRsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMinRsKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            evt.consume();
            jtxtReferenceName.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtMinRsKeyPressed

    private void jtxtMinRsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMinRsKeyTyped
        lb.onlyNumber(evt, 12);
    }//GEN-LAST:event_jtxtMinRsKeyTyped

    private void jtxtLockDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtLockDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtLockDateFocusGained

    private void jtxtLockDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtLockDateFocusLost
        lb.setDateUsingJTextField(jtxtLockDate);
    }//GEN-LAST:event_jtxtLockDateFocusLost

    private void jtxtLockDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtLockDateKeyPressed
        lb.enterEvent(evt, okButton);
    }//GEN-LAST:event_jtxtLockDateKeyPressed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelContact;
    private javax.swing.JComboBox jcmbRs;
    private javax.swing.JLabel jlblMsg;
    private javax.swing.JTextField jtxtAccountCD;
    public static javax.swing.JTextField jtxtAccountName;
    private javax.swing.JTextField jtxtAddress1;
    private javax.swing.JTextField jtxtAddress2;
    public javax.swing.JTextField jtxtAreaID;
    private javax.swing.JTextField jtxtCheckpost;
    public javax.swing.JTextField jtxtCityID;
    private javax.swing.JTextField jtxtContactName;
    private javax.swing.JTextField jtxtEmail1;
    private javax.swing.JTextField jtxtFaxNo;
    private javax.swing.JTextField jtxtGSTNo;
    private javax.swing.JTextField jtxtGroupName;
    private javax.swing.JTextField jtxtLockDate;
    private javax.swing.JTextField jtxtMaxRs;
    private javax.swing.JTextField jtxtMinRs;
    private javax.swing.JTextField jtxtMobile;
    private javax.swing.JTextField jtxtOPBRs;
    private javax.swing.JTextField jtxtPanNo;
    private javax.swing.JTextField jtxtPhone;
    public javax.swing.JTextField jtxtPincode;
    private javax.swing.JTextField jtxtReferenceName;
    private javax.swing.JTextField jtxtShortName;
    private javax.swing.JTextField jtxtStateCd;
    private javax.swing.JTextField jtxtTransportId;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;
}