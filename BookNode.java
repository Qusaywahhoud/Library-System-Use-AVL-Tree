package com.mycompany.library;

class BookNode {
    int isbn;
    String title;
    String author;
    int copies;
    int borrowCount;
    int height;
    BookNode left;
    BookNode right;
    WaitingQueue waitingQueue;//composition

    public BookNode(int isbn, String title, String author, int copies) {
        if (isbn < 0 || copies < 0) {
            throw new IllegalArgumentException("ISBN and copies cannot be negative values.");
        }
           this.isbn = isbn;
           this.copies = copies;

        this.title = title;
        this.author = author;
        this.borrowCount = 0;
        this.height = 1;
        this.left = null;
        this.right = null;
        this.waitingQueue = new WaitingQueue();
    }
}