import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TreeMain {

    public static void main(String[] args) {
        BinaryTree tree = new BinaryTree();

        tree.addNode(10);
        tree.addNode(51);
        tree.addNode(6);
        tree.addNode(7);
        tree.addNode(3);
        tree.addNode(4);
        tree.addNode(2);
        tree.addNode(50);
        tree.addNode(55);

        TreeThread t1 = new TreeThread(tree);
        t1.setName("TreeThread_1");
        TreeThread t2 = new TreeThread(tree);
        t2.setName("TreeThread_2");
        TreeThread t3 = new TreeThread(tree);
        t2.setName("TreeThread_3");

        t1.start();
        t2.start();
        t3.start();
        System.out.println("=================================\n MULTITHREADING STARTS\n=================================");

    }
}

class TreeThread extends Thread {
    private BinaryTree tree;
    String threadName = Thread.currentThread().getName();

    public TreeThread(BinaryTree tree) {
        this.tree = tree;
    }

    @Override
    public void run() {

        tree.findNode(5000);
        tree.addNode(5000);

        tree.findNode(5000);
        tree.deleteNode(5000);
        tree.findNode(29999);
    }
}

@Aspect("pertarget(execution(BinaryTree.new(..)))")
class AspectLock {
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();

    /**
     * Around advice for addNode().
     * @param joinPoint the jointpoint the advice computes.
     * @param treeStructure the tree strcture we use.
     * @throws Throwable
     */
    @Around("target(treeStructure) && execution(void addNode(int))")
    public void insertPointcut(ProceedingJoinPoint joinPoint, BinaryTree treeStructure) throws Throwable {
        writeLock.lock();
        System.out.println("insertPointcut intercepts addNode()...");
        try {
            Thread.sleep(1000);
            joinPoint.proceed();
        } finally {
            writeLock.unlock();
        }
    }

     /**
     * Around advice for deleteNode().
     * @param joinPoint the jointpoint the advice computes.
     * @param treeStructure the tree strcture we use.
     * @throws Throwable
     */
    @Around("target(treeStructure) && execution(boolean deleteNode(int))")
    public Object deletePointcut(ProceedingJoinPoint joinPoint, BinaryTree treeStructure) throws Throwable {
        writeLock.lock();
        System.out.println("deletePointcut intercepts deleteNode()...");
        try {
            Thread.sleep(1000);
            return joinPoint.proceed();
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Around advice for findNode().
     * @param joinPoint the jointpoint the advice computes.
     * @param treeStructure the tree strcture we use.
     * @throws Throwable
     */
    @Around("target(treeStructure) && execution(void findNode(int))")
    public void findPointCut(ProceedingJoinPoint joinPoint, BinaryTree treeStructure) throws Throwable {
        readLock.lock();
        System.out.println("findPointCut intercepts findNode()...");
        try {
            Thread.sleep(1000);
            joinPoint.proceed();
        } finally {
            readLock.unlock();
        }
    }
}

class BinaryTree {
    public static String NODE_FOUND = "Thread: '%s' exits findNode(). Node with value %s is found";
    public static String NODE_NOT_FOUND = "Thread: '%s' exits findNode(). Node with value %s is NOT found";
    public static String NODE_DELETED = "Thread: '%s' exits deleteNode(). Node with value %s is deleted";
    public static String NODE_NOT_DELETED = "Thread: '%s' exits deleteNode(). Node with value %s is NOT deleted";
    public static String NODE_ADDED = "Thread: '%s' exits addNode(). Node with value %s is added";
    public static String NODE_NOT_ADDED = "Thread: '%s' exits addNode(). Node with value %s is NOT added";
    private Node root;

    public void addNode(int key) {
        String threadName = Thread.currentThread().getName();
        System.out.println(String.format("Thread: '%s' entered addNode() with value: %s", threadName, key));
        Node newNode = new Node(key);

        if (root == null) {
            root = newNode;
        } else {
            Node targetNode = root;
            Node parent;

            while (true) {
                parent = targetNode;
                if (key < targetNode.getValue()) {
                    targetNode = targetNode.getLeftChild();

                    if (targetNode == null) {
                        parent.setLeftChild(newNode);
                        System.out.println(String.format(NODE_ADDED, threadName, key));
                        return;
                    }
                } else if (key > targetNode.getValue()) {
                    targetNode = targetNode.getRightChild();

                    if (targetNode == null) {
                        parent.setRightChild(newNode);
                        System.out.println(String.format(NODE_ADDED, threadName, key));
                        return;
                    }
                } else {
                    System.out.println(String.format(NODE_NOT_ADDED, threadName, key));
                    return;
                }
            }
        }
        System.out.println(String.format(NODE_ADDED, threadName, key));
        return;
    }

    public void findNode(int value) {
        String threadName = Thread.currentThread().getName();
        System.out.println(String.format("Thread: '%s' entered findNode() with value: %s", threadName, value));
        Node targetNode = root;
        if (value == root.getValue()) {
            System.out.println(String.format(NODE_FOUND, threadName, value));
            return;
        }

        while (targetNode.getValue() != value) {
            if (value < targetNode.getValue()) {
                targetNode = targetNode.getLeftChild();
            } else {
                targetNode = targetNode.getRightChild();
            }

            if (targetNode == null) {
                System.out.println(String.format(NODE_NOT_FOUND, threadName, value));
                return;
            }
        }
        System.out.println(String.format(NODE_FOUND, threadName, value));
        return;
    }


    public boolean deleteNode(int value) {
        String threadName = Thread.currentThread().getName();
        System.out.println(String.format("Thread: '%s' entered deleteNode() with value: %s", threadName, value));
        Node targetNode = root;
        Node parent = root;

        boolean isLeftChild = true;

        //this while loop iterates the tree to find the target node we want to delete.
        while (targetNode.getValue() != value) {
            parent = targetNode;

            if (value < targetNode.getValue()) {
                isLeftChild = true;
                targetNode = targetNode.getLeftChild();
            } else {
                isLeftChild = false;
                targetNode = targetNode.getRightChild();
            }

            if (targetNode == null) {
                System.out.println(String.format(NODE_NOT_DELETED, threadName, value));
                return false;
            }
        }

        if (targetNode.getLeftChild() == null && targetNode.getRightChild() == null) {
            if (targetNode == root) {
                root = null;
            } else if (isLeftChild) {
                parent.setLeftChild(null);
            } else {
                parent.setRightChild(null);
            }
            //  no right child
        } else if (targetNode.getRightChild() == null) {

            if (targetNode == root) {
                root = targetNode.getLeftChild();
            } else if (isLeftChild) {
                parent.setLeftChild(targetNode.getLeftChild());
            } else {
                parent.setRightChild(targetNode.getLeftChild());
            }
            // no left child
        } else if (targetNode.getLeftChild() == null) {

            if (targetNode == root) {
                root = targetNode.getRightChild();
            } else if (isLeftChild) {
                parent.setLeftChild(targetNode.getRightChild());
            } else {
                parent.setRightChild(targetNode.getLeftChild());
            }

            //two children are involved
        } else {
            Node replacement = getReplacementNode(targetNode);

            if (targetNode == root) {
                root = replacement;
            } else if (isLeftChild) {
                parent.setLeftChild(replacement);
            } else {
                parent.setRightChild(replacement);
            }
            replacement.setLeftChild(targetNode.getLeftChild());
        }
        System.out.println(String.format(NODE_DELETED, threadName, value));
        return true;

    }

    private Node getReplacementNode(Node replacedNode) {
        Node replacementParent = replacedNode;
        Node replacement = replacedNode;

        Node targetNode = replacedNode.getRightChild();
        while (targetNode != null) {
            replacementParent = replacement;
            replacement = targetNode;
            targetNode = targetNode.getLeftChild();
        }

        if (replacement != replacedNode.getRightChild()) {
            replacementParent.setLeftChild(replacement.getRightChild());
            replacement.setRightChild(replacedNode.getRightChild());
        }

        return replacement;
    }

    public class Node {
        private int value;
        private Node leftChild;
        private Node rightChild;

        public Node(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public Node getLeftChild() {
            return leftChild;
        }

        public void setLeftChild(Node leftChild) {
            this.leftChild = leftChild;
        }

        public Node getRightChild() {
            return rightChild;
        }

        public void setRightChild(Node rightChild) {
            this.rightChild = rightChild;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    '}';
        }
    }
}

