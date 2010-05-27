package com.jme.scene;

import static org.junit.Assert.*;

public class TestNode {
    public TestNode() {}

    private Node root;

    @org.junit.Before
    public void setUp() throws Exception {
        root = new Node("root");
    }

    @org.junit.Test
    public void testInit() {
        assertEquals(0, root.getQuantity());
        assertEquals(0, root.getVertexCount());
        assertEquals(0, root.getTriangleCount());
        assertNull(root.getParent());
        assertNull(root.getChild(5));
        assertNull(root.getChild("test"));
        assertEquals("root", root.getName());
    }

    @org.junit.Test
    public void testHierarchy() {
        Node child1 = new Node("child1");
        Node child2 = new Node("child2");
        Node child3 = new Node("child3");

        root.attachChild(child1);
        root.attachChild(child2);
        root.attachChild(child3);
        assertEquals(3, root.getQuantity());

        assertEquals(child2, root.getChild(1));
        assertEquals(child2, root.getChild("child2"));
        assertEquals(root, child1.getParent());

        Spatial s = root.detachChildAt(2);
        assertEquals(child3, s);
        assertEquals(2, root.getQuantity());
        int index = root.detachChild(child2);
        assertEquals(1, index);
        assertEquals(1, root.getQuantity());
        index = root.detachChildNamed("not a child");
        assertEquals(-1, index);
        assertEquals(1, root.getQuantity());
        index = root.detachChildNamed("child1");
        assertEquals(0, index);
        assertEquals(0, root.getQuantity());

        root.attachChild(child1);
        root.attachChild(child2);
        root.attachChildAt(child3, 1);
        assertEquals(3, root.getQuantity());
        assertEquals(child3, root.getChild(1));
        assertEquals(2, root.getChildIndex(child2));

        root.detachAllChildren();
        assertEquals(0, root.getQuantity());
    }

    @org.junit.Test
    public void matches() {
        Node n1 = new Node("n1");
        Node n2 = new Node("n2");
        Node n11 = new Node("n11");
        Node n12 = new Node("n12");
        Node n21 = new Node("n21");
        Node n111 = new Node("n111");
        TriMesh t11 = new TriMesh("t11");
        TriMesh t111 = new TriMesh("t111");
        TriMesh t211 = new TriMesh("t211");
        root.attachChild(n1);
        root.attachChild(n2);
        n1.attachChild(n11);
        n1.attachChild(n12);
        n2.attachChild(n21);
        n1.attachChild(t11);
        n11.attachChild(n111);
        n11.attachChild(t111);
        n21.attachChild(t211);

        // Negative match tests
        assertEquals(0, root.descendantMatches("nosuchName").size());
        assertEquals(0, root.descendantMatches(Circle.class).size());
        assertEquals(0, n11.descendantMatches("nosuchName").size());
        assertEquals(0, n11.descendantMatches(Circle.class).size());
        assertEquals(0, n2.descendantMatches(".+111").size());

        assertEquals(3, root.descendantMatches(TriMesh.class).size());
        assertEquals(2, n1.descendantMatches(TriMesh.class).size());
        assertEquals(4, root.descendantMatches(".*2.*").size());
        assertEquals(1, n1.descendantMatches(".*2.*").size());
        assertEquals(1, root.descendantMatches(TriMesh.class, ".+111").size());
        assertEquals(2, root.descendantMatches(".+111").size());
        assertEquals(1, n1.descendantMatches(TriMesh.class, ".+111").size());
        assertEquals(2, n1.descendantMatches(".+111").size());

        // Test that "descendants" does not include self:
        assertEquals(9, root.descendantMatches((String) null).size());
        assertEquals(2, n2.descendantMatches((String) null).size());
        assertEquals(1, n2.descendantMatches(Node.class).size());
        assertEquals(0, n21.descendantMatches(Node.class).size());
    }
}
