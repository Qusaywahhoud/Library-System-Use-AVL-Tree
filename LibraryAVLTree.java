package com.mycompany.library;
import java.util.HashMap;
import java.util.Map;

public class LibraryAVLTree {
    private BookNode root;

    public BookNode getRoot() {
        return root;
    }

    public BookNode search(int isbn) {
        return searchRec(root, isbn);
    }

    private BookNode searchRec(BookNode node, int isbn) {
        if (node == null || node.isbn == isbn) return node;
        if (isbn < node.isbn) return searchRec(node.left, isbn);
        return searchRec(node.right, isbn);
    }


    public boolean insert(int isbn, String title, String author, int copies) {
        if (search(isbn) != null) {
            return false;
        }
        root = insertRec(root, isbn, title, author, copies);
        return true;
    }

    private BookNode insertRec(BookNode node, int isbn, String title, String author, int copies) {
        if (node == null) return new BookNode(isbn, title, author, copies);

        if (isbn < node.isbn) {
            node.left = insertRec(node.left, isbn, title, author, copies);
        } else if (isbn > node.isbn) {
            node.right = insertRec(node.right, isbn, title, author, copies);
        } else {
            return node;
        }

        updateHeight(node);
        int balance = getBalance(node);

        if (balance > 1 && isbn < node.left.isbn) return rotateRight(node);
        if (balance < -1 && isbn > node.right.isbn) return rotateLeft(node);
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
        if (node == null) return node;

        if (isbn < node.isbn) {
            node.left = deleteRec(node.left, isbn);
        } else if (isbn > node.isbn) {
            node.right = deleteRec(node.right, isbn);
        } else {
            if (node.left == null || node.right == null) {
                node = (node.left != null) ? node.left : node.right;
            } else {
                BookNode temp = minValueNode(node.right);
                node.isbn = temp.isbn;
                node.title = temp.title;
                node.author = temp.author;
                node.copies = temp.copies;
                node.borrowCount = temp.borrowCount;
                node.waitingQueue = temp.waitingQueue;
                node.right = deleteRec(node.right, temp.isbn);
            }
        }

        if (node == null) return null;

        updateHeight(node);
        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0) return rotateRight(node);
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1 && getBalance(node.right) <= 0) return rotateLeft(node);
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    private int height(BookNode node) {
        return node == null ? 0 : node.height;
    }

    private void updateHeight(BookNode node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    private int getBalance(BookNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
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

    private BookNode minValueNode(BookNode node) {
        BookNode current = node;
        while (current.left != null) current = current.left;
        return current;
    }

    public int getTotalAvailableCopies() {
        return countTotalCopiesRec(root);
    }

    private int countTotalCopiesRec(BookNode node) {
        if (node == null) return 0;
        return node.copies + countTotalCopiesRec(node.left) + countTotalCopiesRec(node.right);
    }

    public BookNode getMostBorrowedBook() {
        return findMostBorrowedRec(root, null);
    }

    private BookNode findMostBorrowedRec(BookNode node, BookNode currentMax) {
        if (node == null) return currentMax;
        if (currentMax == null || node.borrowCount > currentMax.borrowCount) {
            currentMax = node;
        }
        currentMax = findMostBorrowedRec(node.left, currentMax);
        return findMostBorrowedRec(node.right, currentMax);
    }

    public String getMostReadAuthor() {
        Map<String, Integer> authorCounts = new HashMap<>();
        populateAuthorCounts(root, authorCounts);
        String topAuthor = "No Data Available";
        int maxRead = 0;
        for (Map.Entry<String, Integer> entry : authorCounts.entrySet()) {
            if (entry.getValue() > maxRead) {
                maxRead = entry.getValue();
                topAuthor = entry.getKey();
            }
        }
        return topAuthor + " (Total Borrows: " + maxRead + ")";
    }

    private void populateAuthorCounts(BookNode node, Map<String, Integer> map) {
        if (node == null) return;
        map.put(node.author, map.getOrDefault(node.author, 0) + node.borrowCount);
        populateAuthorCounts(node.left, map);
        populateAuthorCounts(node.right, map);
    }
}