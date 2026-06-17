package com.mycompany.library;

import java.util.Date;

class BorrowRecord {
    int isbn;
    String borrowerName;
    Date borrowDate;
    Date expectedReturnDate;
    boolean isReturned;
    BorrowRecord next;

    public BorrowRecord(int isbn, String borrowerName, Date borrowDate, Date expectedReturnDate) {
        this.isbn = isbn;
        this.borrowerName = borrowerName;
        this.borrowDate = borrowDate;
        this.expectedReturnDate = expectedReturnDate;
        this.isReturned = false;
        this.next = null;
    }
}

class BorrowHistory {
    private BorrowRecord head;
    private final int MAX_BORROW_LIMIT = 3;

    public int getActiveBorrowCount(String borrowerName) {
        int count = 0;
        BorrowRecord current = head;
        while (current != null) {
            if (current.borrowerName.equalsIgnoreCase(borrowerName) && !current.isReturned) {
                count++;
            }
            current = current.next;
        }
        return count;
    }

    public void borrowBook(LibraryBST library, int isbn, String borrowerName, Date borrowDate, Date expectedReturnDate) {
        BookNode book = library.search(isbn);

        if (book == null) {
            System.out.println("Transaction Failed: Book with ISBN " + isbn + " does not exist.");
            return;
        }

        if (book.copies <= 0) {
            System.out.println("Transaction Failed: '" + book.title + "' is out of stock.");
            return;
        }

        if (getActiveBorrowCount(borrowerName) >= MAX_BORROW_LIMIT) {
            System.out.println("Transaction Failed: " + borrowerName + " exceeded max borrow limit.");
            return;
        }

        book.copies--;
        book.borrowCount++;

        addRecordDirectly(isbn, borrowerName, borrowDate, expectedReturnDate);
        System.out.println("Transaction Success: " + borrowerName + " borrowed '" + book.title + "'.");
    }

    private void addRecordDirectly(int isbn, String borrowerName, Date borrowDate, Date expectedReturnDate) {
        BorrowRecord newRecord = new BorrowRecord(isbn, borrowerName, borrowDate, expectedReturnDate);
        if (head == null) {
            head = newRecord;
        } else {
            BorrowRecord current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newRecord;
        }
    }


    public void returnBookDynamically(LibraryBST library, WaitingQueue waitingQueue, String borrowerName, int isbn, Date borrowDate, Date expectedReturnDate) {
        BorrowRecord current = head;
        boolean found = false;

        while (current != null) {
            if (current.borrowerName.equalsIgnoreCase(borrowerName) && current.isbn == isbn && !current.isReturned) {
                current.isReturned = true;
                found = true;
                break;
            }
            current = current.next;
        }

        if (!found) {
            System.out.println("No active borrow record found for " + borrowerName + " with ISBN " + isbn);
            return;
        }

        System.out.println("Success: " + borrowerName + " returned the book (ISBN: " + isbn + ").");
        BookNode book = library.search(isbn);

        if (book != null) {
            // فحص ما إذا كان هناك شخص ينتظر هذا الكتاب في القائمة
            QueueNode nextStudent = waitingQueue.dequeue();
            if (nextStudent != null) {

                book.borrowCount++;
                addRecordDirectly(isbn, nextStudent.studentName, borrowDate, expectedReturnDate);
                System.out.println("[Dynamic Update] Book automatically assigned to waiting student: " + nextStudent.studentName);
            } else {

                book.copies++;
                System.out.println("[Dynamic Update] Library stock incremented. Total copies available: " + book.copies);
            }
        }
    }
}

class QueueNode {
    String studentName;
    boolean isGraduate;
    QueueNode next;

    public QueueNode(String studentName, boolean isGraduate) {
        this.studentName = studentName;
        this.isGraduate = isGraduate;
        this.next = null;
    }
}

class WaitingQueue {
    private QueueNode head;

    public void enqueue(String studentName, boolean isGraduate) {
        QueueNode newNode = new QueueNode(studentName, isGraduate);

        if (head == null || (newNode.isGraduate && !head.isGraduate)) {
            newNode.next = head;
            head = newNode;
        } else {
            QueueNode current = head;
            while (current.next != null && !(newNode.isGraduate && !current.next.isGraduate)) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        System.out.println("Added " + studentName + (isGraduate ? " (Graduate)" : " (Undergraduate)") + " to the waiting queue.");
    }

    public QueueNode dequeue() {
        if (head == null) {
            return null;
        }
        QueueNode temp = head;
        head = head.next;
        return temp;
    }

    public void printQueue() {
        QueueNode current = head;
        System.out.print("Waiting Queue: ");
        if (current == null) System.out.print("Empty");
        while (current != null) {
            System.out.print("[" + current.studentName + " | Graduate: " + current.isGraduate + "] -> ");
            current = current.next;
        }
        System.out.println("End of Queue");
    }
}