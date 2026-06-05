package com.mycompany.library;

class BookNode {
    int isbn;
    String title;
    int copies;
    int height;
    BookNode left;
    BookNode right;

    public BookNode(int isbn, String title, int copies) {
        this.isbn = isbn;
        this.title = title;
        this.copies = copies;
        this.height = 1;
        this.left = null;
        this.right = null;
    }
}
