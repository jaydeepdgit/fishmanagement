/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import dhananistockmanagement.DeskFrame;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import support.OurDateChooser;
import support.PickList;

/**
 *
 * @author @JD@
 */
public class StockSummary extends javax.swing.JInternalFrame {
    private Library lb = new Library();
    private PickList mainCategoryPickListView, subCategoryPickList = null;
    private Connection dataConnection = DeskFrame.connMpAdmin;
    private DefaultTableModel model = null;
    private ResultSet rsLocal = null;
    private TableRowSorter<TableModel> rowSorter;
    private CachedRowSet crsMain = null;
    String colname[] = new String[]{"main_cat", "sub_cat", "itm_name", "pcs", "slab", "rate_inr", "rate_usd", "total_inr", "total_usd"};
    int column[] = new int[]{0, 0, 0, 0, 1, 2, 2, 2, 2};
    String Syspath = System.getProperty("user.dir");

    /**
     * Creates new form StockSummary
     */
    public StockSummary() {
        initComponents();
        model = (DefaultTableModel) jTable1.getModel();
        mainCategoryPickListView = new PickList(dataConnection);
        subCategoryPickList = new PickList(dataConnection);
        setPickListView();
        registerShortKeys();
        setPermission();
        setIconToPnael();
        setcheckEnableDisable(false);
        rowSorter = new TableRowSorter<>(jTable1.getModel());
        jTable1.setRowSorter(rowSorter);
        jTable1.getColumnModel().getColumn(2).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.getColumnModel().getColumn(3).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.getColumnModel().getColumn(4).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.getColumnModel().getColumn(5).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.getColumnModel().getColumn(6).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.getColumnModel().getColumn(7).setCellRenderer(new StatusColumnCellRenderer());
        jTable1.getColumnModel().getColumn(8).setCellRenderer(new StatusColumnCellRenderer());
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
            if(col == 3 || col == 4 || col == 5 || col == 6 || col == 7 || col == 8) {
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
        mainCategoryPickListView.setNextComponent(jtxtSubCategory);
        mainCategoryPickListView.setDefaultWidth(210);
        mainCategoryPickListView.setAllowBlank(true);
        mainCategoryPickListView.setDefaultColumnWidth(200);
        mainCategoryPickListView.setLocation(122, 63);
        
        subCategoryPickList = new PickList(dataConnection);
        subCategoryPickList.setLayer(getLayeredPane());
        subCategoryPickList.setPickListComponent(jtxtSubCategory);
        subCategoryPickList.setNextComponent(jbtnView);
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
        String fromDate = jtxtFromDate.getText();
        String toDate = jtxtToDate.getText();
        try {
            String sql = "SELECT slabid, sub_category, main_category, slabCategory, SUM(pcs) AS pcs, AVG(rate_inr) AS rate_inr, AVG(rate_usd) AS rate_usd FROM \n" +
                "(SELECT sc.id AS slabid, sbc.name AS sub_category, mc.name AS main_category, sc.name AS slabCategory, ((st.opb + st.pur + st.sal_r + st.qty) - st.sal - st.pur_r) AS pcs,\n" +
                "CASE WHEN gs.rate_inr IS NOT NULL THEN gs.rate_inr ELSE sbd.rate END AS rate_inr, \n" +
                "CASE WHEN gs.rate_usd IS NOT NULL THEN gs.rate_usd ELSE sbd.rate_dollar END AS rate_usd, \n" +
                "st.doc_id FROM stock0_2 st \n" +
                "LEFT JOIN grade_sub gs ON st.doc_id = gs.id AND st.fk_slab_category_id = gs.fk_slab_category_id\n" +
                "LEFT JOIN sale_bill_detail sbd ON st.doc_id = sbd.ref_no AND st.fk_slab_category_id = sbd.fk_slab_category_id\n" +
                "LEFT JOIN slab_category sc ON st.fk_slab_category_id = sc.id \n" +
                "LEFT JOIN sub_category sbc ON sc.fk_sub_category_id = sbc.id \n" +
                "LEFT JOIN main_category mc ON sbc.fk_main_category_id = mc.id \n" +
                "WHERE st.fk_slab_category_id IS NOT NULL";
            
            if(!jtxtMainItemName.getText().equalsIgnoreCase("")) {
              sql += " AND sbc.fk_main_category_id = '"+ lb.getMainCategory(jtxtMainItemName.getText(), "C") +"' ";
            }
            
            if(!jtxtSubCategory.getText().equalsIgnoreCase("")) {
              sql += " AND sc.fk_sub_category_id = '"+ lb.getSubCategory(jtxtSubCategory.getText(), "C") +"' ";
            }  
            
            if(jcmDate.isSelected()) {
                sql += " AND ((st.doc_date >= '"+ lb.ConvertDateFormetForDB(fromDate) +"' AND "
                + " st.doc_date <= '"+ lb.ConvertDateFormetForDB(toDate) +"') OR st.doc_date IS NULL)";
            }
            
            sql += " GROUP BY st.stock2_id) AS stock GROUP BY slabid ORDER BY sub_category,slabid";
            psLocal = dataConnection.prepareStatement(sql);
            rsLocal = psLocal.executeQuery();
            crsMain = lb.getBlankCachedRowSet(colname, column);
            String arr[] = new String[9];
            double pcs = 0.00, slab = 0.00, total_inr = 0.00, total_usd = 0.00;
            double cat_pcs = 0.00, cat_slab = 0.00, cat_total_inr = 0.00, cat_total_usd = 0.00;
            String subCategory = "";
            while (rsLocal.next()) {
                if((!subCategory.equals("")) && (!subCategory.equals(rsLocal.getString("sub_category")))) {
                    arr[0] = "";
                    arr[1] = "";
                    arr[2] = "Total";
                    arr[3] = lb.Convert2DecFmt(cat_slab);
                    arr[4] = lb.Convert2DecFmt(cat_pcs);
                    arr[5] = "";
                    arr[6] = "";
                    arr[7] = lb.Convert2DecFmt(cat_total_inr);
                    arr[8] = lb.Convert2DecFmt(cat_total_usd);
                    lb.appendColumnToCacheRowSet(crsMain, arr, column);
                    
                    cat_pcs = 0.00; 
                    cat_slab = 0.00;
                    cat_total_inr = 0.00;
                    cat_total_usd = 0.00;
                }
                
                arr[0] = rsLocal.getString("main_category");
                arr[1] = rsLocal.getString("sub_category");
                arr[2] = rsLocal.getString("slabCategory");
                arr[3] = lb.Convert2DecFmt(rsLocal.getDouble("pcs") / 10);                
                arr[4] = lb.Convert2DecFmt(rsLocal.getDouble("pcs"));
                arr[5] = lb.getIndianFormat(rsLocal.getDouble("rate_inr"));
                arr[6] = lb.getIndianFormat(rsLocal.getDouble("rate_usd"));                
                arr[7] = lb.getIndianFormat(rsLocal.getDouble("rate_inr") * rsLocal.getDouble("pcs"));
                arr[8] = lb.getIndianFormat(rsLocal.getDouble("rate_usd") * rsLocal.getDouble("pcs"));
                lb.appendColumnToCacheRowSet(crsMain, arr, column);
                cat_pcs += rsLocal.getDouble("pcs");
                cat_slab += rsLocal.getDouble("pcs") / 10;
                cat_total_inr += (rsLocal.getDouble("rate_inr") * rsLocal.getDouble("pcs"));
                cat_total_usd += (rsLocal.getDouble("rate_usd") * rsLocal.getDouble("pcs"));
                
                subCategory = rsLocal.getString("sub_category");
                
                pcs += rsLocal.getDouble("pcs");
                slab += rsLocal.getDouble("pcs") / 10;
                total_inr += (rsLocal.getDouble("rate_inr") * rsLocal.getDouble("pcs"));
                total_usd += (rsLocal.getDouble("rate_usd") * rsLocal.getDouble("pcs"));
            }
            
            arr[0] = "";
            arr[1] = "";
            arr[2] = "Total";
            arr[3] = lb.Convert2DecFmt(cat_slab);
            arr[4] = lb.Convert2DecFmt(cat_pcs);
            arr[5] = "";
            arr[6] = "";
            arr[7] = lb.Convert2DecFmt(cat_total_inr);
            arr[8] = lb.Convert2DecFmt(cat_total_usd);
            lb.appendColumnToCacheRowSet(crsMain, arr, column);
                    
            arr[0] = "";
            arr[1] = "";
            arr[2] = "Grand Total";
            arr[3] = lb.Convert2DecFmt(slab);
            arr[4] = lb.Convert2DecFmt(pcs);
            arr[5] = "";
            arr[6] = "";
            arr[7] = lb.Convert2DecFmt(total_inr);
            arr[8] = lb.Convert2DecFmt(total_usd);
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
        jtxtSubCategory = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jcmDate = new javax.swing.JCheckBox();
        jtxtFromDate = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtxtToDate = new javax.swing.JTextField();
        jBillDateBtn = new javax.swing.JButton();
        jBillDateBtn1 = new javax.swing.JButton();
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
                "SR No", "Sub Category", "Slab Category", "Slab", "Quantity", "Avg_INR", "Avg_USD", "Price_INR", "Price_USD"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, false, false, false, true
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
        jtxtMainItemName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtMainItemNameActionPerformed(evt);
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

        jtxtSubCategory.setFont(new java.awt.Font("Arial Unicode MS", 0, 14)); // NOI18N
        jtxtSubCategory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(4, 110, 152)));
        jtxtSubCategory.setMinimumSize(new java.awt.Dimension(2, 25));
        jtxtSubCategory.setPreferredSize(new java.awt.Dimension(2, 25));
        jtxtSubCategory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtSubCategoryFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtSubCategoryFocusLost(evt);
            }
        });
        jtxtSubCategory.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jtxtSubCategoryComponentResized(evt);
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

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel2.setText("Sub Item Name");

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel3.setText("From");

        jcmDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jcmDate.setText("Check Date");
        jcmDate.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcmDateItemStateChanged(evt);
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

        jBillDateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtnActionPerformed(evt);
            }
        });

        jBillDateBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBillDateBtn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcmDate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBillDateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBillDateBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jtxtMainItemName, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtSubCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnView, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jbtnClose, jbtnPreview});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtMainItemName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnView)
                        .addComponent(jtxtSubCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jbtnPreview)
                    .addComponent(jbtnClose))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtToDate)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jcmDate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtFromDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBillDateBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBillDateBtn1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(6, 6, 6))
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
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtxtSearch))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void jtxtToDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtToDateKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnView.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtToDateKeyPressed

    private void jtxtToDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtToDateFocusLost
        lb.setDateUsingJTextField(jtxtToDate, jbtnView);
    }//GEN-LAST:event_jtxtToDateFocusLost

    private void jtxtToDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtToDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtToDateFocusGained

    private void jtxtFromDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtFromDateKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jtxtToDate.requestFocusInWindow();
        }
    }//GEN-LAST:event_jtxtFromDateKeyPressed

    private void jtxtFromDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDateFocusLost
        lb.setDateUsingJTextField(jtxtFromDate);
    }//GEN-LAST:event_jtxtFromDateFocusLost

    private void jtxtFromDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtFromDateFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtFromDateFocusGained

    private void jcmDateItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcmDateItemStateChanged
        if(jcmDate.isSelected()) {
            setcheckEnableDisable(true);
        } else {
            setcheckEnableDisable(false);
        }
    }//GEN-LAST:event_jcmDateItemStateChanged

    private void setcheckEnableDisable(boolean flag) {
        jLabel3.setEnabled(flag);
        jtxtFromDate.setEnabled(flag);
        jBillDateBtn.setEnabled(flag);
        jLabel4.setEnabled(flag);
        jtxtToDate.setEnabled(flag);
        jBillDateBtn1.setEnabled(flag);
    }
    
    private void jtxtSubCategoryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSubCategoryKeyTyped
        lb.fixLength(evt, 255);
    }//GEN-LAST:event_jtxtSubCategoryKeyTyped

    private void jtxtSubCategoryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSubCategoryKeyReleased
        try {
            String sql = "SELECT name FROM sub_category WHERE fk_main_category_id = '"+ lb.getMainCategory(jtxtMainItemName.getText(), "C")
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

    private void jtxtSubCategoryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtSubCategoryKeyPressed
        subCategoryPickList.setLocation(jtxtSubCategory.getX() + jPanel2.getX(), jtxtSubCategory.getY() + jtxtSubCategory.getHeight() + jPanel2.getY());
        subCategoryPickList.pickListKeyPress(evt);
    }//GEN-LAST:event_jtxtSubCategoryKeyPressed

    private void jtxtSubCategoryComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jtxtSubCategoryComponentResized
        //setTextfieldsAtBottom();
    }//GEN-LAST:event_jtxtSubCategoryComponentResized

    private void jtxtSubCategoryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSubCategoryFocusLost
        subCategoryPickList.setVisible(false);
    }//GEN-LAST:event_jtxtSubCategoryFocusLost

    private void jtxtSubCategoryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSubCategoryFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtSubCategoryFocusGained

    private void jtxtMainItemNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainItemNameKeyTyped
        lb.fixLength(evt, 100);
    }//GEN-LAST:event_jtxtMainItemNameKeyTyped

    private void jtxtMainItemNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainItemNameKeyReleased
        try {
            mainCategoryPickListView.setReturnComponent(new JTextField[]{jtxtMainItemName});
            PreparedStatement pstLocal = dataConnection.prepareStatement("SELECT name FROM main_category WHERE name LIKE '%" + jtxtMainItemName.getText().toUpperCase() + "%'");
            mainCategoryPickListView.setPreparedStatement(pstLocal);
            mainCategoryPickListView.setValidation(dataConnection.prepareStatement("SELECT * FROM main_category WHERE name = ?"));
            mainCategoryPickListView.pickListKeyRelease(evt);
        } catch (Exception ex) {
            lb.printToLogFile("Exception at jtxtMainItemNameKeyReleased In Stock Summary", ex);
        }
    }//GEN-LAST:event_jtxtMainItemNameKeyReleased

    private void jtxtMainItemNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtxtMainItemNameKeyPressed
        mainCategoryPickListView.setLocation(jtxtMainItemName.getX() + jPanel2.getX(), jtxtMainItemName.getY() + jPanel2.getY() + jtxtMainItemName.getHeight());
        mainCategoryPickListView.pickListKeyPress(evt);
        mainCategoryPickListView.setReturnComponent(new JTextField[]{jtxtMainItemName});
    }//GEN-LAST:event_jtxtMainItemNameKeyPressed

    private void jtxtMainItemNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtMainItemNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtMainItemNameActionPerformed

    private void jtxtMainItemNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMainItemNameFocusLost
        jtxtMainItemName.setText(jtxtMainItemName.getText().toUpperCase());
    }//GEN-LAST:event_jtxtMainItemNameFocusLost

    private void jtxtMainItemNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtMainItemNameFocusGained
        lb.selectAll(evt);
    }//GEN-LAST:event_jtxtMainItemNameFocusGained

    private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnView.doClick();
            jbtnPreview.requestFocusInWindow();
        }
    }//GEN-LAST:event_jbtnViewKeyPressed

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
                    row.add(crsMain.getString(6));
                    row.add(crsMain.getString(7));
                    row.add(crsMain.getString(8));
                    row.add(crsMain.getString(9));
                    model.addRow(row);
                    i++;
                }
            }
        } catch (Exception ex) {
            lb.printToLogFile("Error at jbtnViewActionPerformed In Stock Summary", ex);
        }
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jbtnPreviewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPreviewKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnPreview.doClick();
        }
    }//GEN-LAST:event_jbtnPreviewKeyPressed

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
                lb.reportGenerator("StockLedger.jasper", params, crsMain, jPanel1);
            }
        } catch (Exception ex) {
            lb.printToLogFile("Error at jbtnPreviewActionPerformed In Stock Summary", ex);
        }
    }//GEN-LAST:event_jbtnPreviewActionPerformed

    private void jbtnCloseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnCloseKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jbtnClose.doClick();
        }
    }//GEN-LAST:event_jbtnCloseKeyPressed

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        this.dispose();
    }//GEN-LAST:event_jbtnCloseActionPerformed

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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnPreview;
    private javax.swing.JButton jbtnView;
    private javax.swing.JCheckBox jcmDate;
    private javax.swing.JTextField jtxtFromDate;
    private javax.swing.JTextField jtxtMainItemName;
    private javax.swing.JTextField jtxtSearch;
    private javax.swing.JTextField jtxtSubCategory;
    private javax.swing.JTextField jtxtToDate;
    // End of variables declaration//GEN-END:variables
}