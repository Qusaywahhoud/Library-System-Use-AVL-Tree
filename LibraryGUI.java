package com.mycompany.library;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class LibraryGUI extends JFrame {
    private LibraryAVLTree library;
    private BorrowHistory history;

    private JTextField txtIsbn, txtTitle, txtAuthor, txtCopies, txtStudentName;
    private JCheckBox chkIsGraduate;
    private JTextArea txtDisplay;
    private JButton btnConfirmAction;

    private String currentMode = "";

    private final String BOOKS_FILE = "books.txt";
    private final String QUEUE_FILE = "waiting_queues.txt";

    public LibraryGUI() {
        library = new LibraryAVLTree();
        history = new BorrowHistory();

        setTitle("Dynamic Library Management System Pro");
        setSize(950, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        loadDataFromFiles();

        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Dynamic Form - Inputs automatically unlock per operation"));

        inputPanel.add(new JLabel(" ISBN:"));
        txtIsbn = new JTextField();
        inputPanel.add(txtIsbn);

        inputPanel.add(new JLabel(" Title:"));
        txtTitle = new JTextField();
        inputPanel.add(txtTitle);

        inputPanel.add(new JLabel(" Author:"));
        txtAuthor = new JTextField();
        inputPanel.add(txtAuthor);

        inputPanel.add(new JLabel(" Copies / Adj:"));
        txtCopies = new JTextField();
        inputPanel.add(txtCopies);

        inputPanel.add(new JLabel(" Student Name:"));
        txtStudentName = new JTextField();
        inputPanel.add(txtStudentName);

        chkIsGraduate = new JCheckBox("Is Graduate Student");
        inputPanel.add(chkIsGraduate);

        btnConfirmAction = new JButton("Select an Operation First");
        btnConfirmAction.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnConfirmAction.setBackground(new Color(40, 167, 69));
        btnConfirmAction.setForeground(Color.WHITE);
        btnConfirmAction.setEnabled(false);
        inputPanel.add(btnConfirmAction);

        add(inputPanel, BorderLayout.NORTH);

        txtDisplay = new JTextArea("System Initialized Pro. Please select an operation from the panel to start.\n");
        txtDisplay.setEditable(false);
        txtDisplay.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(txtDisplay);
        add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new GridLayout(8, 1, 8, 8));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));

        JButton btnInsertMode = new JButton("Go to: Insert Book");
        JButton btnSearchMode = new JButton("Go to: Search Book");
        JButton btnDeleteMode = new JButton("Go to: Delete Book");
        JButton btnBorrowMode = new JButton("Go to: Borrow Book");
        JButton btnReturnMode = new JButton("Go to: Return Book");
        JButton btnQueueMode = new JButton("Go to: Join Queue");
        JButton btnStockMode = new JButton("Go to: Adjust Stock");
        JButton btnReports = new JButton(" System Analytics");

        Color modeColor = new Color(240, 244, 248);
        for (JButton btn : new JButton[]{btnInsertMode, btnSearchMode, btnDeleteMode, btnBorrowMode, btnReturnMode, btnQueueMode, btnStockMode}) {
            btn.setBackground(modeColor);
        }
        btnReports.setBackground(new Color(23, 162, 184));
        btnReports.setForeground(Color.WHITE);

        actionPanel.add(btnInsertMode);
        actionPanel.add(btnSearchMode);
        actionPanel.add(btnDeleteMode);
        actionPanel.add(btnBorrowMode);
        actionPanel.add(btnReturnMode);
        actionPanel.add(btnQueueMode);
        actionPanel.add(btnStockMode);
        actionPanel.add(btnReports);

        add(actionPanel, BorderLayout.LINE_END);

        disableAllFields();

        btnInsertMode.addActionListener(e -> {
            currentMode = "INSERT";
            enableFields(true, true, true, true, false, false);
            btnConfirmAction.setText(" Confirm: Insert New Book");
            btnConfirmAction.setEnabled(true);
            txtDisplay.setText("[Mode Window] Fields unlocked for: BOOK INSERTION. Please type (ISBN, Title, Author, Copies).\n");
            txtIsbn.requestFocus();
        });

        btnSearchMode.addActionListener(e -> {
            currentMode = "SEARCH";
            enableFields(true, false, false, false, false, false);
            btnConfirmAction.setText(" Confirm: Search via ISBN");
            btnConfirmAction.setEnabled(true);
            txtDisplay.setText("[Mode Window] Fields unlocked for: SEARCH. Please type the [ISBN] only.\n");
            txtIsbn.requestFocus();
        });

        btnDeleteMode.addActionListener(e -> {
            currentMode = "DELETE";
            enableFields(true, false, false, false, false, false);
            btnConfirmAction.setText(" Confirm: Delete via ISBN");
            btnConfirmAction.setEnabled(true);
            txtDisplay.setText("[Mode Window] Fields unlocked for: DELETION. Please type the target [ISBN].\n");
            txtIsbn.requestFocus();
        });

        btnBorrowMode.addActionListener(e -> {
            currentMode = "BORROW";
            enableFields(true, false, false, false, true, true);
            btnConfirmAction.setText(" Confirm: Process Borrow");
            btnConfirmAction.setEnabled(true);
            txtDisplay.setText("[Mode Window] Fields unlocked for: BORROWING. Please type [ISBN] and [Student Name].\n");
            txtIsbn.requestFocus();
        });

        btnReturnMode.addActionListener(e -> {
            currentMode = "RETURN";
            enableFields(true, false, false, false, true, false);
            btnConfirmAction.setText(" Confirm: Process Return");
            btnConfirmAction.setEnabled(true);
            txtDisplay.setText("[Mode Window] Fields unlocked for: RETURNS. Please type [ISBN] and [Student Name].\n");
            txtIsbn.requestFocus();
        });

        btnQueueMode.addActionListener(e -> {
            currentMode = "QUEUE";
            enableFields(true, false, false, false, true, true);
            btnConfirmAction.setText(" Confirm: Join Waiting Queue");
            btnConfirmAction.setEnabled(true);
            txtDisplay.setText("[Mode Window] Fields unlocked for: QUEUE ENTRY. Please type [ISBN] and [Student Name].\n");
            txtIsbn.requestFocus();
        });

        btnStockMode.addActionListener(e -> {
            currentMode = "STOCK";
            enableFields(true, false, false, true, false, false);
            btnConfirmAction.setText(" Confirm: Adjust Stock Inventory");
            btnConfirmAction.setEnabled(true);
            txtDisplay.setText("[Mode Window] Fields unlocked for: STOCK ADJUSTMENT. Please type [ISBN] and [Quantity adjustment (+/-)].\n");
            txtIsbn.requestFocus();
        });

        btnReports.addActionListener(e -> {
            disableAllFields();
            txtDisplay.setText("==================== LIVE SYSTEM ANALYTICS ====================\n");
            txtDisplay.append(" Total Available Stock across library: " + library.getTotalAvailableCopies() + " copies.\n");
            BookNode pop = library.getMostBorrowedBook();
            txtDisplay.append(" Most Borrowed Book: " + (pop != null && pop.borrowCount > 0 ? "'" + pop.title + "' (Borrowed " + pop.borrowCount + " times)." : "No records yet.") + "\n");
            txtDisplay.append(" Most Read Author: " + library.getMostReadAuthor() + "\n");
            txtDisplay.append("==================================================================\n");
        });

        btnConfirmAction.addActionListener(e -> {
            try {
                switch (currentMode) {
                    case "INSERT":
                        if (!validateInputs(true, true, false)) return;
                        int isbn = Integer.parseInt(txtIsbn.getText().trim());
                        String title = txtTitle.getText().trim();
                        String author = txtAuthor.getText().trim();
                        int copies = Integer.parseInt(txtCopies.getText().trim());

                   
                        boolean isSuccess = library.insert(isbn, title, author, copies);
                        if (isSuccess) {
                            txtDisplay.append(" Successfully inserted '" + title + "'. Tree balanced.\n");
                            saveDataToFiles();
                        } else {
                            txtDisplay.append(" Error: A book with ISBN " + isbn + " already exists in the system!\n");
                            txtDisplay.append(" Tip: Use 'Adjust Stock' mode to add copies to an existing book.\n");
                        }
                        break;

                    case "SEARCH":
                        if (!validateInputs(true, false, false)) return;
                        int searchIsbn = Integer.parseInt(txtIsbn.getText().trim());
                        BookNode b = library.search(searchIsbn);
                        if (b != null) {
                            txtDisplay.append(" Found -> Title: " + b.title + " | Author: " + b.author + " | Available: " + b.copies + " | Queue: " + b.waitingQueue.getQueueString() + "\n");
                            txtTitle.setText(b.title);
                            txtAuthor.setText(b.author);
                            txtCopies.setText(String.valueOf(b.copies));
                        } else {
                            txtDisplay.append(" Book with ISBN " + searchIsbn + " not found.\n");
                        }
                        break;

                    case "DELETE":
                        if (!validateInputs(true, false, false)) return;
                        int delIsbn = Integer.parseInt(txtIsbn.getText().trim());
                        library.delete(delIsbn);
                        txtDisplay.append(" Delete request processed for ISBN: " + delIsbn + ".\n");
                        saveDataToFiles();
                        break;

                    case "BORROW":
                        if (!validateInputs(true, false, true)) return;
                        int borIsbn = Integer.parseInt(txtIsbn.getText().trim());
                        String studentName = txtStudentName.getText().trim();
                        String res = history.borrowBook(library, borIsbn, studentName);

                        if (res.equals("OUT_OF_STOCK")) {
                            BookNode book = library.search(borIsbn);
                            if (history.getActiveBorrowCount(studentName) >= 3) {
                                txtDisplay.append(" Transaction Failed: " + studentName + " has reached the limit (3 books) and cannot join the queue.\n");
                                return;
                            }
                            if (book.waitingQueue.isStudentInQueue(studentName)) {
                                txtDisplay.append(" Transaction Failed: " + studentName + " is already waiting for this book.\n");
                                return;
                            }
                            int choice = JOptionPane.showConfirmDialog(this, "'" + book.title + "' is out of stock. Join queue?", "Stock Alert", JOptionPane.YES_NO_OPTION);
                            if (choice == JOptionPane.YES_OPTION) {
                                book.waitingQueue.enqueue(studentName, chkIsGraduate.isSelected());
                                txtDisplay.append(" Enqueued: " + studentName + " added to waiting list.\n");
                                saveDataToFiles();
                            }
                        } else {
                            txtDisplay.append(res + "\n");
                            saveDataToFiles();
                        }
                        break;

                    case "RETURN":
                        if (!validateInputs(true, false, true)) return;
                        int retIsbn = Integer.parseInt(txtIsbn.getText().trim());
                        String retStudent = txtStudentName.getText().trim();
                        String returnRes = history.returnBookDynamically(library, retStudent, retIsbn);
                        txtDisplay.append(" " + returnRes + "\n");
                        saveDataToFiles();
                        break;

                    case "QUEUE":
                        if (!validateInputs(true, false, true)) return;
                        int qIsbn = Integer.parseInt(txtIsbn.getText().trim());
                        String qStudent = txtStudentName.getText().trim();
                        BookNode targetB = library.search(qIsbn);
                        if (targetB != null) {
                            if (history.getActiveBorrowCount(qStudent) >= 3) {
                                txtDisplay.append(" Queue Failed: " + qStudent + " has 3 active borrows. Cannot wait for more.\n");
                                return;
                            }
                            if (targetB.waitingQueue.isStudentInQueue(qStudent)) {
                                txtDisplay.append(" Queue Failed: " + qStudent + " is already in the waiting list for this book.\n");
                                return;
                            }
                            targetB.waitingQueue.enqueue(qStudent, chkIsGraduate.isSelected());
                            txtDisplay.append(" Queue Update: " + qStudent + " added to waiting list for '" + targetB.title + "'.\n");
                            saveDataToFiles();
                        } else {
                            txtDisplay.append(" Transaction Failed: Book doesn't exist.\n");
                        }
                        break;

                    case "STOCK":
                        if (!validateInputs(true, false, false) || txtCopies.getText().trim().isEmpty()) {
                            txtDisplay.append(" Error: Missing stock adjustment amount.\n");
                            return;
                        }
                        int stIsbn = Integer.parseInt(txtIsbn.getText().trim());
                        int extra = Integer.parseInt(txtCopies.getText().trim());
                        BookNode stockBook = library.search(stIsbn);

                        if (stockBook != null) {
                            if (stockBook.copies + extra < 0) {
                                txtDisplay.append(" Update Failed: Total stock cannot drop below 0.\n");
                                return;
                            }
                            if (extra > 0) {
                                txtDisplay.append(" Processing incoming stock for '" + stockBook.title + "'...\n");
                                while (extra > 0 && !stockBook.waitingQueue.getQueueString().contains("Empty")) {
                                    QueueNode nextStudent = stockBook.waitingQueue.dequeue();
                                    if (nextStudent != null) {
                                        stockBook.borrowCount++;
                                        txtDisplay.append("    Queue cleared dynamically: [" + nextStudent.studentName + "] has been automatically assigned a copy from the new stock!\n");
                                        extra--;
                                    }
                                }
                            }
                            stockBook.copies += extra;
                            txtDisplay.append(" Inventory Adjusted: '" + stockBook.title + "' remaining available stock on shelf: " + stockBook.copies + "\n");
                            saveDataToFiles();
                        } else {
                            txtDisplay.append(" Update Failed: Book not found.\n");
                        }
                        break;
                }
                clearFields();
                disableAllFields();
            } catch (NumberFormatException ex) {
                txtDisplay.append(" Input Format Error: Please ensure numeric values are valid.\n");
            }
        });

        setLocationRelativeTo(null);
    }

    private boolean validateInputs(boolean checkIsbn, boolean checkDetails, boolean checkStudent) {
        if (checkIsbn && txtIsbn.getText().trim().isEmpty()) {
            txtDisplay.append(" Input Error: ISBN field cannot be empty.\n");
            return false;
        }
        if (checkDetails && (txtTitle.getText().trim().isEmpty() || txtAuthor.getText().trim().isEmpty())) {
            txtDisplay.append(" Input Error: Book Title and Author fields are required.\n");
            return false;
        }
        if (checkStudent && txtStudentName.getText().trim().isEmpty()) {
            txtDisplay.append(" Input Error: Student Name field is required.\n");
            return false;
        }
        return true;
    }

    private void disableAllFields() {
        enableFields(false, false, false, false, false, false);
        btnConfirmAction.setText("Select an Operation First");
        btnConfirmAction.setEnabled(false);
        currentMode = "";
    }

    private void enableFields(boolean isbn, boolean title, boolean author, boolean copies, boolean student, boolean graduate) {
        txtIsbn.setEnabled(isbn);
        txtIsbn.setBackground(isbn ? Color.WHITE : new Color(235, 235, 235));
        txtTitle.setEnabled(title);
        txtTitle.setBackground(title ? Color.WHITE : new Color(235, 235, 235));
        txtAuthor.setEnabled(author);
        txtAuthor.setBackground(author ? Color.WHITE : new Color(235, 235, 235));
        txtCopies.setEnabled(copies);
        txtCopies.setBackground(copies ? Color.WHITE : new Color(235, 235, 235));
        txtStudentName.setEnabled(student);
        txtStudentName.setBackground(student ? Color.WHITE : new Color(235, 235, 235));
        chkIsGraduate.setEnabled(graduate);
    }

    private void saveDataToFiles() {
        try (PrintWriter bookWriter = new PrintWriter(new FileWriter(BOOKS_FILE));
             PrintWriter queueWriter = new PrintWriter(new FileWriter(QUEUE_FILE))) {
            saveBooksRec(library.getRoot(), bookWriter, queueWriter);
        } catch (IOException e) {
            txtDisplay.append(" Critical Error: Data could not be saved.\n");
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
        } catch (IOException | NumberFormatException e) {
            System.out.println("No records parsed.");
        }

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
        } catch (IOException | NumberFormatException e) {
            System.out.println("No queues restored.");
        }
    }

    private void clearFields() {
        txtIsbn.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtCopies.setText("");
        txtStudentName.setText("");
        chkIsGraduate.setSelected(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryGUI frame = new LibraryGUI();
            frame.setVisible(true);
        });
    }
}