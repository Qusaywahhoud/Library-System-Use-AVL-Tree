package com.mycompany.library;

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

public class WaitingQueue {
    private QueueNode head;

    public boolean isStudentInQueue(String studentName) {
        QueueNode current = head;
        while (current != null) {
            if (current.studentName.equalsIgnoreCase(studentName)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

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
    }//o(n)

    public QueueNode dequeue() {
        if (head == null) {
            return null;
        }
        QueueNode temp = head;
        head = head.next;
        return temp;
    }//o(1)

    public void writeQueueToFile(int isbn, java.io.PrintWriter writer) {
            QueueNode current = head;
        while (current != null) {
            writer.println(isbn + "," + current.studentName + "," + current.isGraduate);
            current = current.next;
        }
    }

    public String getQueueString() {
        if (head == null) return "Empty";
        StringBuilder sb = new StringBuilder();//use StringBuilder because string is immutable
        QueueNode current = head;
        while (current != null) {
            sb.append("[").append(current.studentName)
                    .append(current.isGraduate ? " | Graduate" : " | Undergraduate").append("] -> ");
            current = current.next;
        }
        sb.append("End");
        return sb.toString();
    }
}