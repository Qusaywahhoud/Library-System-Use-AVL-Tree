package com.mycompany.library;

import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class Main {
        public static void main(String[] args) {
                LibraryBST library = new LibraryBST();
                BorrowHistory history = new BorrowHistory();
                WaitingQueue waitingQueue = new WaitingQueue();
                Scanner scanner = new Scanner(System.in);

                // Pre-populating some data for ease of testing
                library.insert(10, "Data Structures", "Dr. Robert", 2);
                library.insert(20, "Intro to Java", "James Gosling", 4);
                library.insert(30, "Operating Systems", "Tanenbaum", 1);

                // Dates Setup for Borrowing Simulation
                Calendar cal = Calendar.getInstance();
                Date borrowDate = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, 14);
                Date expectedReturn = cal.getTime();

                boolean running = true;
                System.out.println("=== Welcome to the Digital Library Management System ===");

                while (running) {

                        System.out.println("Select an option (1-9):");
                        System.out.println("1. Insert a New Book (Req 1 & 2)");
                        System.out.println("2. Search for a Book (Req 1 & 2)");
                        System.out.println("3. Delete a Book (Req 1 & 2)");
                        System.out.println("4. Borrow a Book (Req 3 & 5 - Constraints Check)");
                        System.out.println("5. Return a Book Dynamically (Req 3 & 7 - Queue Auto-Assign)");
                        System.out.println("6. Add Student to Waiting Queue (Req 4 - Priority Queue)");
                        System.out.println("7. Add Additional Copies Dynamically (Req 7)");
                        System.out.println("8. View Analytics & Reports (Req 6)");
                        System.out.println("9. Exit System");
                        System.out.print("Enter choice: ");

                        int choice = scanner.nextInt();
                        scanner.nextLine();

                        switch (choice) {
                                case 1:
                                        System.out.print("Enter ISBN: ");
                                        int isbn = scanner.nextInt();
                                        scanner.nextLine();
                                        System.out.print("Enter Title: ");
                                        String title = scanner.nextLine();
                                        System.out.print("Enter Author: ");
                                        String author = scanner.nextLine();
                                        System.out.print("Enter Number of Copies: ");
                                        int copies = scanner.nextInt();
                                        library.insert(isbn, title, author, copies);
                                        break;

                                case 2:
                                        System.out.print("Enter ISBN to Search: ");
                                        int searchIsbn = scanner.nextInt();
                                        BookNode foundBook = library.search(searchIsbn);
                                        if (foundBook != null) {
                                                System.out.println("Book Found: '" + foundBook.title + "' by " + foundBook.author + " | Available Copies: " + foundBook.copies);
                                        } else {
                                                System.out.println("Book with ISBN " + searchIsbn + " not found.");
                                        }
                                        break;

                                case 3:
                                        System.out.print("Enter ISBN to Delete: ");
                                        int deleteIsbn = scanner.nextInt();
                                        library.delete(deleteIsbn);
                                        break;

                                case 4:
                                        System.out.print("Enter ISBN to Borrow: ");
                                        int borrowIsbn = scanner.nextInt();
                                        scanner.nextLine();
                                        System.out.print("Enter Borrower Name: ");
                                        String bName = scanner.nextLine();

                                        history.borrowBook(library, borrowIsbn, bName, borrowDate, expectedReturn);
                                        break;

                                case 5:
                                        System.out.print("Enter ISBN to Return: ");
                                        int returnIsbn = scanner.nextInt();
                                        scanner.nextLine();
                                        System.out.print("Enter Borrower Name: ");
                                        String rName = scanner.nextLine();

                                        history.returnBookDynamically(library, waitingQueue, rName, returnIsbn, borrowDate, expectedReturn);
                                        break;

                                case 6:
                                        System.out.print("Enter Student Name: ");
                                        String sName = scanner.nextLine();
                                        System.out.print("Is the student a Graduate? (true/false): ");
                                        boolean isGrad = scanner.nextBoolean();
                                        waitingQueue.enqueue(sName, isGrad);
                                        waitingQueue.printQueue();
                                        break;


                                case 7:
                                        System.out.print("Enter ISBN to add copies to: ");
                                        int addCopiesIsbn = scanner.nextInt();
                                        scanner.nextLine();
                                        System.out.print("Enter number of additional copies: ");
                                        int extraCopies = scanner.nextInt();
                                        scanner.nextLine();
                                        library.addCopiesDynamically(addCopiesIsbn, extraCopies);
                                        break;
                                case 8:
                                        System.out.println("\n=== SYSTEM ANALYTICS ===");
                                        System.out.println("1. PreOrder Tree Structure:");
                                        library.printPreOrder();
                                        System.out.println("2. Total Available Copies across Library: " + library.getTotalAvailableCopies());

                                        BookNode popular = library.getMostBorrowedBook();
                                        if (popular != null && popular.borrowCount > 0) {
                                                System.out.println("3. Most Read Book: '" + popular.title + "' by " + popular.author + " (Borrowed " + popular.borrowCount + " times)");
                                        } else {
                                                System.out.println("3. Most Read Book: No borrow interactions recorded yet.");
                                        }
                                        waitingQueue.printQueue();
                                        break;

                                case 9:
                                        System.out.println("Exiting Library Management System. Goodbye!");
                                        running = false;
                                        break;

                                default:
                                        System.out.println("Invalid selection. Please enter a number between 1 and 9.");
                        }
                }
                scanner.close();
        }
}