package ch.heiafr.isc.datacockpit.tree.exampleobjects;

import ch.heiafr.isc.datacockpit.general_libraries.results.AbstractResultsDisplayer;
import ch.heiafr.isc.datacockpit.general_libraries.results.AbstractResultsManager;
import ch.heiafr.isc.datacockpit.tree.experiment_aut.WrongExperimentException;

public class TreeTestObject {

    private String desc;
    private TreeTestObject child;
    private TreeTestObject[] children;

    public TreeTestObject(String s) {
        this.desc = "String" + s;
    }

    public TreeTestObject(TreeTestObject tree) {
        this.child = tree;
    }

    public TreeTestObject(int i) {
        this.desc = "Integer" + i;
    }

    public TreeTestObject(double d) {
        this.desc = "Double" + d;
    }

    public TreeTestObject(long l) {
        this.desc = "Long" + l;
    }

    public TreeTestObject(float f) {
        this.desc = "Float" + f;
    }
    public TreeTestObject(boolean b) {
        this.desc = "Boolean" + b;
    }
    public TreeTestObject(char c) {
        this.desc = "Character" + c;
    }

    public TreeTestObject(TreeTestObject[] array) {
        this.children = array;
    }

    public String toString() {
        return toString("").toString();
    }

    public StringBuilder toString(String prefix) {
        StringBuilder sb = new StringBuilder();
        if (desc != null) {
            sb.append(prefix).append(" ").append(desc);
        } else if (child != null) {
            sb.append("Child : \n");
            sb.append(child.toString(prefix + "  "));
        } else if (children != null) {
            sb.append("Children : \n");
            for (TreeTestObject child : children) {
                sb.append(child.toString(prefix + "  "));
            }
        }
        return sb;
    }
}
