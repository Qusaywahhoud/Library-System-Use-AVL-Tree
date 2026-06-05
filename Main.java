package com.mycompany.library;
public class Main {
    public static void main(String[] args) {

        /*LibraryBST library = new LibraryBST();

        System.out.println("(10, 20, 30, 40, 50)");

        library.insert(10, "Book A", 3);
        library.insert(20, "Book B", 5);
        library.insert(30, "Book C", 2);
        library.insert(40, "Book D", 6);
        library.insert(50, "Book E", 1);


        library.printPreOrder();*/


            LibraryBST library = new LibraryBST();



            library.insert(10, "Data Structures", 5);
            library.insert(20, "Intro to Java", 3);
            library.insert(30, "Operating Systems", 2);
            library.insert(40, "Discrete Math", 4);
            library.insert(50, "Database Systems", 6);


            library.printPreOrder();


            int searchIsbn = 30;
            System.out.println("اsearch by ispn " + searchIsbn);
            BookNode foundBook = library.search(searchIsbn);

            if (foundBook != null) {
                System.out.println("found ,title: " + foundBook.title + " |copied : " + foundBook.copies);
            } else {
                System.out.println("sorry ,not found");
            }




            System.out.println("update book 'Intro to Java' (ISBN: 20) from 3>15 copies");
            library.updateCopies(20, 15);


            BookNode updatedBook = library.search(20);
            System.out.println(" (ISBN: 20) " + updatedBook.copies);


            library.delete(50);
            library.printPreOrder();
            library.delete(40);
            library.printPreOrder();
            library.delete(20);
            library.printPreOrder();


        }

    }
