package com.mycompany.library;

public class LibraryBST {
    private BookNode root;

    public LibraryBST() {
        root = null;
    }

    private int height(BookNode node) {
        return (node == null) ? 0 : node.height;
    }

    private int getBalance(BookNode node) {
        return (node == null) ? 0 : height(node.left) - height(node.right);
    }

    private void updateHeight(BookNode node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    private BookNode rotateRight(BookNode y) {
        BookNode x = y.left;
        BookNode T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    private BookNode rotateLeft(BookNode x) {
        BookNode y = x.right;
        BookNode T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }


    public void insert(int isbn, String title, int copies) {
        root = insertRec(root, isbn, title, copies);
    }

    private BookNode insertRec(BookNode node, int isbn, String title, int copies) {

        if (node == null) {
            return new BookNode(isbn, title, copies);
        }

        if (isbn < node.isbn) {
            node.left = insertRec(node.left, isbn, title, copies);
        } else if (isbn > node.isbn) {
            node.right = insertRec(node.right, isbn, title, copies);
        } else {
            System.out.println(" The book with the number" + isbn + "Founded");
            return node;
        }
        updateHeight(node);


        int balance = getBalance(node);


        if (balance > 1 && isbn < node.left.isbn) {
            return rotateRight(node);
        }


        if (balance < -1 && isbn > node.right.isbn) {
            return rotateLeft(node);
        }


        if (balance > 1 && isbn > node.left.isbn) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }


        if (balance < -1 && isbn < node.right.isbn) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    public void delete(int isbn) {
        root = deleteRec(root, isbn);
    }

    private BookNode deleteRec(BookNode node, int isbn) {

        if (node == null) {
            System.out.println("Book not found");
            return node;
        }

        if (isbn < node.isbn) {
            node.left = deleteRec(node.left, isbn);
        } else if (isbn > node.isbn) {
            node.right = deleteRec(node.right, isbn);
        } else {

            if (node.left == null || node.right == null) {
                BookNode temp = (node.left != null) ? node.left : node.right;
                if (temp == null) {
                    node = null;
                } else {
                    node = temp;
                }
            } else {

                BookNode temp = minValueNode(node.right);
                node.isbn = temp.isbn;
                node.title = temp.title;
                node.copies = temp.copies;
                node.right = deleteRec(node.right, temp.isbn);
            }
        }

        if (node == null) return null;


        updateHeight(node);


        int balance = getBalance(node);


        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private BookNode minValueNode(BookNode node) {
        BookNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    public BookNode search(int isbn) {
        BookNode current = root;
        while (current != null) {
            if (isbn == current.isbn) return current;
            current = (isbn < current.isbn) ? current.left : current.right;
        }
        return null;
    }

    public void updateCopies(int isbn, int newCopies) {
        BookNode book = search(isbn);
        if (book != null) {
            book.copies = newCopies;
            System.out.println("The copies has update");
        } else {
            System.out.println("The book not founded");
        }
    }


    public void printPreOrder() {
        printPreOrderRec(root);
        System.out.println();
    }

    private void printPreOrderRec(BookNode node) {
        if (node != null) {
            System.out.print(node.isbn + " (H:" + node.height + ") -> ");
            printPreOrderRec(node.left);
            printPreOrderRec(node.right);
        }


}}
