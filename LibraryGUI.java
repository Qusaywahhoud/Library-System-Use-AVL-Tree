package com.mycompany.library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

public class LibraryGUI extends JFrame {
    private LibraryBST library;
    private BorrowHistory history;
    private WaitingQueue waitingQueue;


    private JTextField txtIsbn, txtTitle, txtAuthor, txtCopies, txtStudentName;
    private JCheckBox chkIsGraduate;
    private JTextArea txtDisplay;
}