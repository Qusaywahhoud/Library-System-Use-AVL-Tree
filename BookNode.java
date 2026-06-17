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

    public BookNode(int isbn, String title, String author, int copies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.copies = copies;
        this.borrowCount = 0;
        this.height = 1;
        this.left = null;
        this.right = null;
    }
}