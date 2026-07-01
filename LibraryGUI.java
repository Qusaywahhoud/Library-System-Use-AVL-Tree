package com.mycompany.library;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.*;

public class LibraryGUI extends JFrame {
    private LibraryAVLTree library;
    private BorrowHistory history;

    private CardLayout cardLayout;
    private JPanel contentContainer;


    private AnalyticsPanel analyticsPanel;

    private final String BOOKS_FILE = "books.txt";
    private final String QUEUE_FILE = "waiting_queues.txt";

    private JButton btnInsertMode, btnSearchMode, btnDeleteMode, btnBorrowMode,
            btnReturnMode, btnQueueMode, btnStockMode, btnReports;

    public final Color BG_COLOR = new Color(30, 30, 30);
    public final Color PANEL_COLOR = new Color(45, 45, 45);
    public final Color TEXT_COLOR = new Color(220, 220, 220);
    public final Color ACCENT_COLOR = new Color(0, 122, 204);

    public LibraryGUI() {
        library = new LibraryAVLTree();
        history = new BorrowHistory();

        setupLookAndFeel();
        initializeWindowSettings();
        loadDataFromFiles();

        buildSidebarMenu();
        buildCentralContentArea();
        registerNavigationEvents();

        setLocationRelativeTo(null);
    }

    public static void appendLog(JTextPane console, String text) {
        Color color;
        String upper = text.toUpperCase();


        if (upper.contains("[SUCCESS]") || upper.contains("[FOUND]") || upper.contains("[UPDATED]") || upper.contains("SUCCESS")) {
            color = new Color(86, 211, 100);
        } else if (upper.contains("[ERROR]") || upper.contains("[DENIED]") || upper.contains("[ALERT]") || upper.contains("[VALIDATION ERROR]") || upper.contains("[404]") || upper.contains("NOT FOUND") || upper.contains("FAILED") || upper.contains("INVALID") || upper.contains("EMPTY")) {
            color = new Color(220, 53, 69);
        } else if (upper.contains("[QUEUED]") || upper.contains("[PROCESSING]") || upper.contains("WARNING") || upper.contains("QUEUE")) {
            color = new Color(243, 156, 18);
        } else {
            color = new Color(0, 190, 255);
        }

        StyledDocument doc = console.getStyledDocument();
        Style style = console.addStyle("ColorStyle", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}
    }

    private void initializeWindowSettings() {
        setTitle("Dynamic Library Management System Pro");
        setSize(1150, 780);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(BG_COLOR);
    }

    private void buildSidebarMenu() {
        JPanel sidebarPanel = new JPanel(new GridLayout(8, 1, 10, 10));
        sidebarPanel.setBackground(BG_COLOR);
        sidebarPanel.setBorder(new EmptyBorder(20, 15, 20, 0));

        btnInsertMode = createMenuButton("Insert Book");
        btnSearchMode = createMenuButton("Search Book");
        btnDeleteMode = createMenuButton("Delete Book");
        btnBorrowMode = createMenuButton("Borrow Book");
        btnReturnMode = createMenuButton("Return Book");
        btnQueueMode = createMenuButton("Join Queue");
        btnStockMode = createMenuButton("Adjust Stock");

        btnReports = new JButton("System Analytics");
        btnReports.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReports.setBackground(new Color(23, 162, 184));
        btnReports.setForeground(Color.WHITE);
        btnReports.setFocusPainted(false);
        btnReports.setCursor(new Cursor(Cursor.HAND_CURSOR));

        sidebarPanel.add(btnInsertMode);
        sidebarPanel.add(btnSearchMode);
        sidebarPanel.add(btnDeleteMode);
        sidebarPanel.add(btnBorrowMode);
        sidebarPanel.add(btnReturnMode);
        sidebarPanel.add(btnQueueMode);
        sidebarPanel.add(btnStockMode);
        sidebarPanel.add(btnReports);

        add(sidebarPanel, BorderLayout.WEST);
    }

    private void buildCentralContentArea() {
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentContainer.setBackground(BG_COLOR);

        contentContainer.add(new InsertBookPanel(this), "INSERT");
        contentContainer.add(new SearchBookPanel(this), "SEARCH");
        contentContainer.add(new DeleteBookPanel(this), "DELETE");
        contentContainer.add(new BorrowBookPanel(this), "BORROW");
        contentContainer.add(new ReturnBookPanel(this), "RETURN");
        contentContainer.add(new QueueBookPanel(this), "QUEUE");
        contentContainer.add(new StockBookPanel(this), "STOCK");


        analyticsPanel = new AnalyticsPanel(this);
        contentContainer.add(analyticsPanel, "ANALYTICS");

        add(contentContainer, BorderLayout.CENTER);
    }

    private void registerNavigationEvents() {
        btnInsertMode.addActionListener(e -> cardLayout.show(contentContainer, "INSERT"));
        btnSearchMode.addActionListener(e -> cardLayout.show(contentContainer, "SEARCH"));
        btnDeleteMode.addActionListener(e -> cardLayout.show(contentContainer, "DELETE"));
        btnBorrowMode.addActionListener(e -> cardLayout.show(contentContainer, "BORROW"));
        btnReturnMode.addActionListener(e -> cardLayout.show(contentContainer, "RETURN"));
        btnQueueMode.addActionListener(e -> cardLayout.show(contentContainer, "QUEUE"));
        btnStockMode.addActionListener(e -> cardLayout.show(contentContainer, "STOCK"));


        btnReports.addActionListener(e -> {
            analyticsPanel.refreshAnalytics();
            cardLayout.show(contentContainer, "ANALYTICS");
        });
    }

    public LibraryAVLTree getLibrary() { return library; }
    public BorrowHistory getHistory() { return history; }

    public void saveDataToFiles() {
        try (PrintWriter bookWriter = new PrintWriter(new FileWriter(BOOKS_FILE));
             PrintWriter queueWriter = new PrintWriter(new FileWriter(QUEUE_FILE))) {
            saveBooksRec(library.getRoot(), bookWriter, queueWriter);
        } catch (IOException e) {
            System.err.println("Critical Error: Data could not be saved.");
        }
    }

    private void saveBooksRec(BookNode node, PrintWriter bookWriter, PrintWriter queueWriter) {
        if (node == null) return;
        bookWriter.println(node.isbn + "," + node.title + "," + node.author + "," + node.copies + "," + node.borrowCount);
        node.waitingQueue.writeQueueToFile(node.isbn, queueWriter);
        saveBooksRec(node.left, bookWriter, queueWriter);
        saveBooksRec(node.right, bookWriter, queueWriter);
    }

    private void loadDataFromFiles() {
        File booksFile = new File(BOOKS_FILE);
        if (!booksFile.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(booksFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    int isbn = Integer.parseInt(data[0]);
                    library.insert(isbn, data[1], data[2], Integer.parseInt(data[3]));
                    BookNode ab = library.search(isbn);
                    if (ab != null) ab.borrowCount = Integer.parseInt(data[4]);
                }
            }
        } catch (IOException | NumberFormatException e) {}

        File queueFile = new File(QUEUE_FILE);
        if (!queueFile.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(queueFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    BookNode tb = library.search(Integer.parseInt(data[0]));
                    if (tb != null) tb.waitingQueue.enqueue(data[1], Boolean.parseBoolean(data[2]));
                }
            }
        } catch (IOException | NumberFormatException e) {}
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(PANEL_COLOR);
        btn.setForeground(TEXT_COLOR);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_COLOR);
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(PANEL_COLOR);
                btn.setForeground(TEXT_COLOR);
            }
        });
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }



    // 1. شاشة إضافة كتاب
    private static class InsertBookPanel extends JPanel {
        private LibraryGUI gui;
        private JTextField txtIsbn, txtTitle, txtAuthor, txtCopies;
        private JTextPane txtConsole;

        public InsertBookPanel(LibraryGUI gui) {
            this.gui = gui;
            setLayout(new BorderLayout(20, 20));
            setBackground(gui.PANEL_COLOR);
            setBorder(new EmptyBorder(25, 25, 25, 25));

            JLabel lblHeader = new JLabel(" Book Insertion Command Center");
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblHeader.setForeground(gui.TEXT_COLOR);
            add(lblHeader, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
            formPanel.setBackground(gui.PANEL_COLOR);
            formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(gui.ACCENT_COLOR), " Specifications ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), gui.ACCENT_COLOR));

            formPanel.add(createLabel("ISBN Key:")); txtIsbn = createTextField(); formPanel.add(txtIsbn);
            formPanel.add(createLabel("Book Title:")); txtTitle = createTextField(); formPanel.add(txtTitle);
            formPanel.add(createLabel("Author Intellectual:")); txtAuthor = createTextField(); formPanel.add(txtAuthor);
            formPanel.add(createLabel("Initial Stock Copies:")); txtCopies = createTextField(); formPanel.add(txtCopies);

            JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
            centerContainer.setBackground(gui.PANEL_COLOR);
            centerContainer.add(formPanel, BorderLayout.NORTH);

            txtConsole = new JTextPane();
            txtConsole.setEditable(false);
            txtConsole.setBackground(new Color(20, 20, 20));
            txtConsole.setFont(new Font("Consolas", Font.PLAIN, 14));
            LibraryGUI.appendLog(txtConsole, "System Component Ready. Awaiting data insertion...\n");
            centerContainer.add(new JScrollPane(txtConsole), BorderLayout.CENTER);

            add(centerContainer, BorderLayout.CENTER);

            JButton btnConfirm = new JButton("Execute Insertion & Balance Tree");
            btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnConfirm.setBackground(gui.ACCENT_COLOR);
            btnConfirm.setForeground(Color.WHITE);
            btnConfirm.setPreferredSize(new Dimension(250, 45));
            btnConfirm.addActionListener(e -> executeInsertion());
            add(btnConfirm, BorderLayout.SOUTH);
        }
        private void executeInsertion() {
            try {
                int isbn = Integer.parseInt(txtIsbn.getText().trim());
                String title = txtTitle.getText().trim();
                String author = txtAuthor.getText().trim();
                int copies = Integer.parseInt(txtCopies.getText().trim());

                if (isbn < 0 || copies < 0) {
                    LibraryGUI.appendLog(txtConsole, "[VALIDATION ERROR] ISBN and Copies cannot be negative.\n");
                    return;
                }

                if (gui.getLibrary().insert(isbn, title, author, copies)) {
                    LibraryGUI.appendLog(txtConsole, "[SUCCESS] Inserted '" + title + "' safely into AVL Tree structure.\n");
                    gui.saveDataToFiles();
                    clearFields();
                } else {
                    LibraryGUI.appendLog(txtConsole, "[ERROR] Insertion rejected. Duplicated ISBN or negative stock value.\n");
                }
            } catch (NumberFormatException ex) {
                LibraryGUI.appendLog(txtConsole, "[VALIDATION ERROR] Numeric inputs required for ISBN and Copies.\n");
            } catch (IllegalArgumentException ex) {
                LibraryGUI.appendLog(txtConsole, "[ERROR] " + ex.getMessage() + "\n");
            }
        }
        private JLabel createLabel(String text) {
            JLabel l = new JLabel(text); l.setForeground(gui.TEXT_COLOR); l.setFont(new Font("Segoe UI", Font.BOLD, 14)); return l;
        }
        private JTextField createTextField() {
            JTextField tf = new JTextField(); tf.setBackground(new Color(60, 63, 65)); tf.setForeground(Color.WHITE); tf.setCaretColor(Color.WHITE); return tf;
        }
        private void clearFields() { txtIsbn.setText(""); txtTitle.setText(""); txtAuthor.setText(""); txtCopies.setText(""); }
    }

    // 2. شاشة البحث عن كتاب
    private static class SearchBookPanel extends JPanel {
        private LibraryGUI gui;
        private JTextField txtIsbn;
        private JTextPane txtConsole;

        public SearchBookPanel(LibraryGUI gui) {
            this.gui = gui;
            setLayout(new BorderLayout(20, 20));
            setBackground(gui.PANEL_COLOR);
            setBorder(new EmptyBorder(25, 25, 25, 25));

            JLabel lblHeader = new JLabel("🔍 Search Engine: Find Assets");
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblHeader.setForeground(gui.TEXT_COLOR);
            add(lblHeader, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new GridLayout(1, 2, 15, 15));
            formPanel.setBackground(gui.PANEL_COLOR);
            formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(gui.ACCENT_COLOR), " Lookup Query ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), gui.ACCENT_COLOR));

            JLabel l = new JLabel("Enter Target ISBN:"); l.setForeground(gui.TEXT_COLOR); l.setFont(new Font("Segoe UI", Font.BOLD, 14));
            formPanel.add(l);
            txtIsbn = new JTextField(); txtIsbn.setBackground(new Color(60, 63, 65)); txtIsbn.setForeground(Color.WHITE); txtIsbn.setCaretColor(Color.WHITE);
            formPanel.add(txtIsbn);

            JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
            centerContainer.setBackground(gui.PANEL_COLOR);
            centerContainer.add(formPanel, BorderLayout.NORTH);

            txtConsole = new JTextPane();
            txtConsole.setEditable(false);
            txtConsole.setBackground(new Color(20, 20, 20));
            txtConsole.setFont(new Font("Consolas", Font.PLAIN, 14));
            LibraryGUI.appendLog(txtConsole, "Search Module Ready...\n");
            centerContainer.add(new JScrollPane(txtConsole), BorderLayout.CENTER);

            add(centerContainer, BorderLayout.CENTER);

            JButton btnSearch = new JButton("Execute Search Query");
            btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnSearch.setBackground(gui.ACCENT_COLOR);
            btnSearch.setForeground(Color.WHITE);
            btnSearch.setPreferredSize(new Dimension(250, 45));
            btnSearch.addActionListener(e -> executeSearch());
            add(btnSearch, BorderLayout.SOUTH);
        }

        private void executeSearch() {
            try {
                int searchIsbn = Integer.parseInt(txtIsbn.getText().trim());
                BookNode b = gui.getLibrary().search(searchIsbn);
                if (b != null) {
                    LibraryGUI.appendLog(txtConsole, "\n[FOUND] Target Identified:\n   -> Title: " + b.title + "\n   -> Author: " + b.author + "\n   -> Copies Available: " + b.copies + "\n   -> Waiting Queue: " + b.waitingQueue.getQueueString() + "\n");
                } else {
                    LibraryGUI.appendLog(txtConsole, "[404] Book with ISBN " + searchIsbn + " not found.\n");
                }
            } catch (NumberFormatException ex) {
                LibraryGUI.appendLog(txtConsole, "[ERROR] Invalid ISBN format.\n");
            }
        }
    }

    // 3. شاشة حذف كتاب
    private static class DeleteBookPanel extends JPanel {
        private LibraryGUI gui;
        private JTextField txtIsbn;
        private JTextPane txtConsole;

        public DeleteBookPanel(LibraryGUI gui) {
            this.gui = gui;
            setLayout(new BorderLayout(20, 20));
            setBackground(gui.PANEL_COLOR);
            setBorder(new EmptyBorder(25, 25, 25, 25));

            JLabel lblHeader = new JLabel("🗑️ Asset Deletion & Tree Rebalancing");
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblHeader.setForeground(gui.TEXT_COLOR);
            add(lblHeader, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new GridLayout(1, 2, 15, 15));
            formPanel.setBackground(gui.PANEL_COLOR);
            formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED), " Deletion Target ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), Color.RED));

            JLabel l = new JLabel("Target ISBN to Delete:"); l.setForeground(gui.TEXT_COLOR); l.setFont(new Font("Segoe UI", Font.BOLD, 14));
            formPanel.add(l);
            txtIsbn = new JTextField(); txtIsbn.setBackground(new Color(60, 63, 65)); txtIsbn.setForeground(Color.WHITE); txtIsbn.setCaretColor(Color.WHITE);
            formPanel.add(txtIsbn);

            JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
            centerContainer.setBackground(gui.PANEL_COLOR);
            centerContainer.add(formPanel, BorderLayout.NORTH);

            txtConsole = new JTextPane();
            txtConsole.setEditable(false);
            txtConsole.setBackground(new Color(20, 20, 20));
            txtConsole.setFont(new Font("Consolas", Font.PLAIN, 14));
            LibraryGUI.appendLog(txtConsole, "Warning: Deletion will permanently remove the record and rebalance the AVL tree...\n");
            centerContainer.add(new JScrollPane(txtConsole), BorderLayout.CENTER);

            add(centerContainer, BorderLayout.CENTER);

            JButton btnDelete = new JButton("Confirm Deletion");
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnDelete.setBackground(new Color(220, 53, 69));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setPreferredSize(new Dimension(250, 45));
            btnDelete.addActionListener(e -> executeDelete());
            add(btnDelete, BorderLayout.SOUTH);
        }

        private void executeDelete() {
            try {
                int delIsbn = Integer.parseInt(txtIsbn.getText().trim());
                if (gui.getLibrary().delete(delIsbn)) {
                    LibraryGUI.appendLog(txtConsole, "[SUCCESS] Delete request processed for ISBN: " + delIsbn + ". Tree Rebalanced.\n");
                    gui.saveDataToFiles();
                } else {
                    LibraryGUI.appendLog(txtConsole, "[ERROR] Asset with ISBN " + delIsbn + " not found in the system.\n");
                }
            } catch (NumberFormatException ex) {
                LibraryGUI.appendLog(txtConsole, "[ERROR] Invalid ISBN format.\n");
            }
        }
    }

    // 4. شاشة الاستعارة
    private static class BorrowBookPanel extends JPanel {
        private LibraryGUI gui;
        private JTextField txtIsbn, txtStudentName;
        private JCheckBox chkGraduate;
        private JTextPane txtConsole;

        public BorrowBookPanel(LibraryGUI gui) {
            this.gui = gui;
            setLayout(new BorderLayout(20, 20));
            setBackground(gui.PANEL_COLOR);
            setBorder(new EmptyBorder(25, 25, 25, 25));

            JLabel lblHeader = new JLabel(" Transaction Control Room: Borrow Assets");
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblHeader.setForeground(gui.TEXT_COLOR);
            add(lblHeader, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));
            formPanel.setBackground(gui.PANEL_COLOR);
            formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(gui.ACCENT_COLOR), " Security Clearance & Identity ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), gui.ACCENT_COLOR));

            JLabel l1 = new JLabel("Target Asset ISBN:"); l1.setForeground(gui.TEXT_COLOR); l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JLabel l2 = new JLabel("Borrower Student Name:"); l2.setForeground(gui.TEXT_COLOR); l2.setFont(new Font("Segoe UI", Font.BOLD, 14));

            txtIsbn = new JTextField(); txtIsbn.setBackground(new Color(60, 63, 65)); txtIsbn.setForeground(Color.WHITE);
            txtStudentName = new JTextField(); txtStudentName.setBackground(new Color(60, 63, 65)); txtStudentName.setForeground(Color.WHITE);

            chkGraduate = new JCheckBox("Graduate Rank Privilege");
            chkGraduate.setBackground(gui.PANEL_COLOR); chkGraduate.setForeground(gui.TEXT_COLOR);

            formPanel.add(l1); formPanel.add(txtIsbn);
            formPanel.add(l2); formPanel.add(txtStudentName);
            formPanel.add(new JLabel("")); formPanel.add(chkGraduate);

            JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
            centerContainer.setBackground(gui.PANEL_COLOR);
            centerContainer.add(formPanel, BorderLayout.NORTH);

            txtConsole = new JTextPane();
            txtConsole.setEditable(false);
            txtConsole.setBackground(new Color(20, 20, 20));
            txtConsole.setFont(new Font("Consolas", Font.PLAIN, 14));
            LibraryGUI.appendLog(txtConsole, "Transaction system online. Ready to evaluate rules...\n");
            centerContainer.add(new JScrollPane(txtConsole), BorderLayout.CENTER);

            add(centerContainer, BorderLayout.CENTER);

            JButton btnExecute = new JButton("Authorize Borrow Transaction");
            btnExecute.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnExecute.setBackground(new Color(40, 167, 69));
            btnExecute.setForeground(Color.WHITE);
            btnExecute.setPreferredSize(new Dimension(250, 45));
            btnExecute.addActionListener(e -> executeBorrow());
            add(btnExecute, BorderLayout.SOUTH);
        }

        private void executeBorrow() {
            try {
                int isbn = Integer.parseInt(txtIsbn.getText().trim());
                String name = txtStudentName.getText().trim();

                if (name.isEmpty()) {
                    LibraryGUI.appendLog(txtConsole, "[ALERT] Student validation failed. Name empty.\n");
                    return;
                }

                if (gui.getHistory().getActiveBorrowCount(name) >= 3) {
                    LibraryGUI.appendLog(txtConsole, "[DENIED] Limit reached. " + name + " cannot borrow or enqueue.\n");
                    return;
                }

                BookNode book = gui.getLibrary().search(isbn);

                if (book == null) {
                    int choice = JOptionPane.showConfirmDialog(this,
                            "Asset with ISBN " + isbn + " does not exist in the database.\nWould you like to register a request and join the waiting queue for it?",
                            "Asset Not Found",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (choice == JOptionPane.YES_OPTION) {
                        LibraryGUI.appendLog(txtConsole, "WARNING: Auto-creating placeholder asset for requested ISBN " + isbn + "...\n");

                        gui.getLibrary().insert(isbn, "Pending Title (Auto)", "Pending Author", 0);
                        book = gui.getLibrary().search(isbn);

                        book.waitingQueue.enqueue(name, chkGraduate.isSelected());
                        LibraryGUI.appendLog(txtConsole, "[QUEUED] " + name + " pushed to the priority queue for the newly requested asset.\n");
                        gui.saveDataToFiles();
                    } else {
                        LibraryGUI.appendLog(txtConsole, "[ALERT] Transaction aborted. Asset creation cancelled by user.\n");
                    }
                    return;
                }

                String result = gui.getHistory().borrowBook(gui.getLibrary(), isbn, name);

                if (result.equals("OUT_OF_STOCK")) {
                    book.waitingQueue.enqueue(name, chkGraduate.isSelected());
                    LibraryGUI.appendLog(txtConsole, "[QUEUED] Auto-Assigned! '" + book.title + "' is depleted. " + name + " automatically pushed to priority queue.\n");
                    gui.saveDataToFiles();
                } else {
                    LibraryGUI.appendLog(txtConsole, result + "\n");
                    gui.saveDataToFiles();
                }
            } catch (NumberFormatException ex) {
                LibraryGUI.appendLog(txtConsole, "[VALIDATION ERROR] Invalid ISBN notation.\n");
            }
        }
    }

    // 5. شاشة الإرجاع
    private static class ReturnBookPanel extends JPanel {
        private LibraryGUI gui;
        private JTextField txtIsbn, txtStudentName;
        private JTextPane txtConsole;

        public ReturnBookPanel(LibraryGUI gui) {
            this.gui = gui;
            setLayout(new BorderLayout(20, 20));
            setBackground(gui.PANEL_COLOR);
            setBorder(new EmptyBorder(25, 25, 25, 25));

            JLabel lblHeader = new JLabel("🔄 Return Operations & Queue Auto-Assignment");
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblHeader.setForeground(gui.TEXT_COLOR);
            add(lblHeader, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new GridLayout(2, 2, 15, 15));
            formPanel.setBackground(gui.PANEL_COLOR);
            formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(gui.ACCENT_COLOR), " Return Details ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), gui.ACCENT_COLOR));

            JLabel l1 = new JLabel("Asset ISBN:"); l1.setForeground(gui.TEXT_COLOR); l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JLabel l2 = new JLabel("Borrower Name:"); l2.setForeground(gui.TEXT_COLOR); l2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            txtIsbn = new JTextField(); txtIsbn.setBackground(new Color(60, 63, 65)); txtIsbn.setForeground(Color.WHITE);
            txtStudentName = new JTextField(); txtStudentName.setBackground(new Color(60, 63, 65)); txtStudentName.setForeground(Color.WHITE);

            formPanel.add(l1); formPanel.add(txtIsbn);
            formPanel.add(l2); formPanel.add(txtStudentName);

            JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
            centerContainer.setBackground(gui.PANEL_COLOR);
            centerContainer.add(formPanel, BorderLayout.NORTH);

            txtConsole = new JTextPane();
            txtConsole.setEditable(false);
            txtConsole.setBackground(new Color(20, 20, 20));
            txtConsole.setFont(new Font("Consolas", Font.PLAIN, 14));
            LibraryGUI.appendLog(txtConsole, "Ready to process returns and allocate to queued students...\n");
            centerContainer.add(new JScrollPane(txtConsole), BorderLayout.CENTER);

            add(centerContainer, BorderLayout.CENTER);

            JButton btnReturn = new JButton("Process Return");
            btnReturn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnReturn.setBackground(gui.ACCENT_COLOR);
            btnReturn.setForeground(Color.WHITE);
            btnReturn.setPreferredSize(new Dimension(250, 45));
            btnReturn.addActionListener(e -> executeReturn());
            add(btnReturn, BorderLayout.SOUTH);
        }

        private void executeReturn() {
            try {
                int retIsbn = Integer.parseInt(txtIsbn.getText().trim());
                String retStudent = txtStudentName.getText().trim();
                if(retStudent.isEmpty()) {
                    LibraryGUI.appendLog(txtConsole, "[ERROR] Name required.\n");
                    return;
                }

                String returnRes = gui.getHistory().returnBookDynamically(gui.getLibrary(), retStudent, retIsbn);
                LibraryGUI.appendLog(txtConsole, "\n" + returnRes + "\n");
                gui.saveDataToFiles();
            } catch (NumberFormatException ex) {
                LibraryGUI.appendLog(txtConsole, "[ERROR] Invalid ISBN format.\n");
            }
        }
    }

    // 6. شاشة الطابور
    private static class QueueBookPanel extends JPanel {
        private LibraryGUI gui;
        private JTextField txtIsbn, txtStudentName;
        private JCheckBox chkGraduate;
        private JTextPane txtConsole;

        public QueueBookPanel(LibraryGUI gui) {
            this.gui = gui;
            setLayout(new BorderLayout(20, 20));
            setBackground(gui.PANEL_COLOR);
            setBorder(new EmptyBorder(25, 25, 25, 25));

            JLabel lblHeader = new JLabel("⏳ Priority Waiting Queue Management");
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblHeader.setForeground(gui.TEXT_COLOR);
            add(lblHeader, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));
            formPanel.setBackground(gui.PANEL_COLOR);
            formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(gui.ACCENT_COLOR), " Queue Entry ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), gui.ACCENT_COLOR));

            JLabel l1 = new JLabel("Target Asset ISBN:"); l1.setForeground(gui.TEXT_COLOR); l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JLabel l2 = new JLabel("Student Name:"); l2.setForeground(gui.TEXT_COLOR); l2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            txtIsbn = new JTextField(); txtIsbn.setBackground(new Color(60, 63, 65)); txtIsbn.setForeground(Color.WHITE);
            txtStudentName = new JTextField(); txtStudentName.setBackground(new Color(60, 63, 65)); txtStudentName.setForeground(Color.WHITE);
            chkGraduate = new JCheckBox("Graduate Student (High Priority)"); chkGraduate.setBackground(gui.PANEL_COLOR); chkGraduate.setForeground(gui.TEXT_COLOR);

            formPanel.add(l1); formPanel.add(txtIsbn);
            formPanel.add(l2); formPanel.add(txtStudentName);
            formPanel.add(new JLabel("")); formPanel.add(chkGraduate);

            JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
            centerContainer.setBackground(gui.PANEL_COLOR);
            centerContainer.add(formPanel, BorderLayout.NORTH);

            txtConsole = new JTextPane();
            txtConsole.setEditable(false);
            txtConsole.setBackground(new Color(20, 20, 20));
            txtConsole.setFont(new Font("Consolas", Font.PLAIN, 14));
            LibraryGUI.appendLog(txtConsole, "Queue system active...\n");
            centerContainer.add(new JScrollPane(txtConsole), BorderLayout.CENTER);

            add(centerContainer, BorderLayout.CENTER);

            JButton btnQueue = new JButton("Join Queue");
            btnQueue.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnQueue.setBackground(new Color(243, 156, 18));
            btnQueue.setForeground(Color.WHITE);
            btnQueue.setPreferredSize(new Dimension(250, 45));
            btnQueue.addActionListener(e -> executeQueue());
            add(btnQueue, BorderLayout.SOUTH);
        }

        private void executeQueue() {
            try {
                int qIsbn = Integer.parseInt(txtIsbn.getText().trim());
                String qStudent = txtStudentName.getText().trim();
                BookNode targetB = gui.getLibrary().search(qIsbn);

                if (targetB != null) {
                    if (gui.getHistory().getActiveBorrowCount(qStudent) >= 3) {
                        LibraryGUI.appendLog(txtConsole, "[DENIED] " + qStudent + " has 3 active borrows. Cannot wait for more.\n");
                        return;
                    }
                    if (targetB.waitingQueue.isStudentInQueue(qStudent)) {
                        LibraryGUI.appendLog(txtConsole, "[DENIED] " + qStudent + " is already in the waiting list.\n");
                        return;
                    }
                    targetB.waitingQueue.enqueue(qStudent, chkGraduate.isSelected());
                    LibraryGUI.appendLog(txtConsole, "[SUCCESS] " + qStudent + " enqueued for '" + targetB.title + "'.\n");
                    gui.saveDataToFiles();
                } else {
                    LibraryGUI.appendLog(txtConsole, "[ERROR] Book doesn't exist.\n");
                }
            } catch (NumberFormatException ex) {
                LibraryGUI.appendLog(txtConsole, "[ERROR] Invalid input.\n");
            }
        }
    }

    // 7. شاشة تعديل المخزون
    private static class StockBookPanel extends JPanel {
        private LibraryGUI gui;
        private JTextField txtIsbn, txtCopies;
        private JTextPane txtConsole;

        public StockBookPanel(LibraryGUI gui) {
            this.gui = gui;
            setLayout(new BorderLayout(20, 20));
            setBackground(gui.PANEL_COLOR);
            setBorder(new EmptyBorder(25, 25, 25, 25));

            JLabel lblHeader = new JLabel(" Inventory Control & Stock Adjustment");
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblHeader.setForeground(gui.TEXT_COLOR);
            add(lblHeader, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new GridLayout(2, 2, 15, 15));
            formPanel.setBackground(gui.PANEL_COLOR);
            formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(gui.ACCENT_COLOR), " Adjust Quantities ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), gui.ACCENT_COLOR));

            JLabel l1 = new JLabel("Asset ISBN:"); l1.setForeground(gui.TEXT_COLOR); l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JLabel l2 = new JLabel("Quantity to Add (+ or -):"); l2.setForeground(gui.TEXT_COLOR); l2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            txtIsbn = new JTextField(); txtIsbn.setBackground(new Color(60, 63, 65)); txtIsbn.setForeground(Color.WHITE);
            txtCopies = new JTextField(); txtCopies.setBackground(new Color(60, 63, 65)); txtCopies.setForeground(Color.WHITE);

            formPanel.add(l1); formPanel.add(txtIsbn);
            formPanel.add(l2); formPanel.add(txtCopies);

            JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
            centerContainer.setBackground(gui.PANEL_COLOR);
            centerContainer.add(formPanel, BorderLayout.NORTH);

            txtConsole = new JTextPane();
            txtConsole.setEditable(false);
            txtConsole.setBackground(new Color(20, 20, 20));
            txtConsole.setFont(new Font("Consolas", Font.PLAIN, 14));
            LibraryGUI.appendLog(txtConsole, "Incoming stock will automatically resolve queued students...\n");
            centerContainer.add(new JScrollPane(txtConsole), BorderLayout.CENTER);

            add(centerContainer, BorderLayout.CENTER);

            JButton btnStock = new JButton("Update Inventory");
            btnStock.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnStock.setBackground(gui.ACCENT_COLOR);
            btnStock.setForeground(Color.WHITE);
            btnStock.setPreferredSize(new Dimension(250, 45));
            btnStock.addActionListener(e -> executeStock());
            add(btnStock, BorderLayout.SOUTH);
        }

        private void executeStock() {
            try {
                int stIsbn = Integer.parseInt(txtIsbn.getText().trim());
                int extra = Integer.parseInt(txtCopies.getText().trim());
                BookNode stockBook = gui.getLibrary().search(stIsbn);

                if (stockBook != null) {
                    if (stockBook.copies + extra < 0) {
                        LibraryGUI.appendLog(txtConsole, "[DENIED] Total stock cannot drop below 0.\n");
                        return;
                    }
                    if (extra > 0) {
                        LibraryGUI.appendLog(txtConsole, "\n[PROCESSING] Incoming stock for '" + stockBook.title + "'...\n");
                        while (extra > 0 && !stockBook.waitingQueue.getQueueString().contains("Empty")) {
                            QueueNode nextStudent = stockBook.waitingQueue.dequeue();
                            if (nextStudent != null) {
                                stockBook.borrowCount++;
                                LibraryGUI.appendLog(txtConsole, "   -> Queue cleared dynamically: [" + nextStudent.studentName + "] assigned a copy! [SUCCESS]\n");
                                extra--;
                            }
                        }
                    }
                    stockBook.copies += extra;
                    LibraryGUI.appendLog(txtConsole, "[UPDATED] '" + stockBook.title + "' available stock: " + stockBook.copies + " [SUCCESS]\n");
                    gui.saveDataToFiles();
                } else {
                    LibraryGUI.appendLog(txtConsole, "[ERROR] Book not found.\n");
                }
            } catch (NumberFormatException ex) {
                LibraryGUI.appendLog(txtConsole, "[ERROR] Invalid numeric input.\n");
            }
        }
    }


    private static class AnalyticsPanel extends JPanel {
        private LibraryGUI gui;
        private JLabel lblTotalStock, lblMostBorrowed, lblTopAuthor;

        public AnalyticsPanel(LibraryGUI gui) {
            this.gui = gui;
            setLayout(new BorderLayout(20, 20));
            setBackground(gui.PANEL_COLOR);
            setBorder(new EmptyBorder(25, 25, 25, 25));

            JLabel lblHeader = new JLabel(" Live Data Core Analytics Platform");
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblHeader.setForeground(gui.TEXT_COLOR);
            add(lblHeader, BorderLayout.NORTH);

            JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
            cardsPanel.setBackground(gui.PANEL_COLOR);

            lblTotalStock = new JLabel("0", SwingConstants.CENTER);
            lblMostBorrowed = new JLabel("-", SwingConstants.CENTER);
            lblTopAuthor = new JLabel("-", SwingConstants.CENTER);

            cardsPanel.add(createStatCard("Total System Inventory Asset", lblTotalStock, gui.ACCENT_COLOR));
            cardsPanel.add(createStatCard("Highest Algorithmic Demand", lblMostBorrowed, new Color(40, 167, 69)));
            cardsPanel.add(createStatCard("Dominant Authority Author", lblTopAuthor, new Color(23, 162, 184)));

            add(cardsPanel, BorderLayout.CENTER);
            refreshAnalytics();
        }

        public void refreshAnalytics() {
            if (gui.getLibrary() != null) {

                lblTotalStock.setText("<html><div style='text-align: center; font-family: Segoe UI;'>"
                        + "<span style='font-size: 32px; font-weight: bold; color: #FFFFFF;'>"
                        + gui.getLibrary().getTotalAvailableCopies()
                        + "</span><br><span style='color: #888; font-size: 12px;'>Total Assets</span></div></html>");


                BookNode pop = gui.getLibrary().getMostBorrowedBook();
                if (pop != null && pop.borrowCount > 0) {
                    lblMostBorrowed.setText("<html><div style='text-align: center; font-family: Segoe UI;'>"
                            + "<b style='font-size: 18px; color: #FFFFFF;'>" + pop.title + "</b><br>"
                            + "<span style='color: #007ACC; font-size: 14px; font-weight: bold;'>" + pop.borrowCount + " Transactions</span></div></html>");
                } else {
                    lblMostBorrowed.setText("<html><div style='text-align: center; color: #888;'>No Data</div></html>");
                }


                String authorData = gui.getLibrary().getMostReadAuthor();
                if (authorData != null && !authorData.startsWith("None")) {
                    String[] parts = authorData.split(" \\(Total Borrows: ");
                    String authorName = parts[0];
                    String count = parts.length > 1 ? parts[1].replace(")", "") : "0";

                    lblTopAuthor.setText("<html><div style='text-align: center; font-family: Segoe UI;'>"
                            + "<b style='font-size: 18px; color: #FFFFFF;'>" + authorName + "</b><br>"
                            + "<span style='color: #28A745; font-size: 14px; font-weight: bold;'>Borrows: " + count + "</span></div></html>");
                } else {
                    lblTopAuthor.setText("<html><div style='text-align: center; color: #888;'>No Data</div></html>");
                }
            }
        }

        private JPanel createStatCard(String title, JLabel valueLabel, Color accent) {
            JPanel card = new JPanel(new BorderLayout(5, 5));
            card.setBackground(new Color(35, 35, 35));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(4, 1, 1, 1, accent),
                    new EmptyBorder(15, 10, 15, 10)));

            JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setForeground(Color.LIGHT_GRAY);
            titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            card.add(titleLabel, BorderLayout.NORTH);
            card.add(valueLabel, BorderLayout.CENTER);
            return card;
        }}}