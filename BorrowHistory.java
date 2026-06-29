package com.mycompany.library;

import java.util.Calendar;
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

public class BorrowHistory {
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

    public String borrowBook(LibraryAVLTree library, int isbn, String borrowerName) {
        BookNode book = library.search(isbn);

        if (book == null) {
            return "Transaction Failed: Book with ISBN " + isbn + " does not exist.";
        }

        if (book.copies <= 0) {
            return "OUT_OF_STOCK";
        }

        if (getActiveBorrowCount(borrowerName) >= MAX_BORROW_LIMIT) {
            return "Transaction Failed: " + borrowerName + " has exceeded the maximum borrow limit (3 books).";
        }

        book.copies--;
        book.borrowCount++;

          Calendar cal = Calendar.getInstance();
        Date borrowDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 14);
        Date expectedReturn = cal.getTime();

        addRecordWithDates(isbn, borrowerName, borrowDate, expectedReturn);
        return "Transaction Success: " + borrowerName + " borrowed '" + book.title + "'.\n" +
                "   -> Borrowed On: " + borrowDate + "\n" +
                "   -> Expected Return: " + expectedReturn;
    }

    private void addRecordWithDates(int isbn, String borrowerName, Date borrowDate, Date expectedReturn) {
        BorrowRecord newRecord = new BorrowRecord(isbn, borrowerName, borrowDate, expectedReturn);
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

    public String returnBookDynamically(LibraryAVLTree library, String borrowerName, int isbn) {
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
            return "No active borrow record found for " + borrowerName + " with ISBN " + isbn;
        }

        BookNode book = library.search(isbn);
        if (book != null) {
            QueueNode nextStudent = book.waitingQueue.dequeue();
            if (nextStudent != null) {
                book.borrowCount++;

                Calendar cal = Calendar.getInstance();
                Date borrowDate = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, 14);
                Date expectedReturn = cal.getTime();

                addRecordWithDates(isbn, nextStudent.studentName, borrowDate, expectedReturn);
                return "Book returned. Automatically assigned to waiting student: " + nextStudent.studentName + "\n" +
                        "   -> New Borrow Date: " + borrowDate + "\n" +
                        "   -> New Expected Return: " + expectedReturn;
            } else {
                book.copies++;
                return "Book returned successfully. Stock incremented. Available copies: " + book.copies;
            }
        }
        return "Return processed successfully.";
    }
}