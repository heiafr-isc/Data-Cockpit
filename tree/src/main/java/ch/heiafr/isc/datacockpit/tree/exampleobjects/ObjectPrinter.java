package ch.heiafr.isc.datacockpit.tree.exampleobjects;

import ch.heiafr.isc.datacockpit.tree.gui.SwingObjectConfigurationAndEnumerator;
import ch.heiafr.isc.datacockpit.tree.object_enum.AbstractEnumerator;

public class ObjectPrinter extends AbstractEnumerator<Object> {

    public static void main(String[] args) {
        SwingObjectConfigurationAndEnumerator<Object> gui = new SwingObjectConfigurationAndEnumerator<>(
                Object.class,
                new ObjectPrinter(),
                new String[]{"ch.heiafr.isc.datacockpit.tree.exampleobjects"}
        );
        // PUT THE ABSOLUTE PATH TO THE FILE HERE TO TEST
     //   gui.show("..Data-Cockpit/tree/src/main/resources/example_tree.conf");
        gui.show();
    }

    private int objectIndex;


    @Override
    public void beforeIteration() {
        objectIndex = 0;
    }

    @Override
    public void iterating(Object object) throws Exception {
        System.out.println("Object " + objectIndex + ": " + object.toString());
        objectIndex++;
    }

    @Override
    public void afterIteration() {

    }

    @Override
    public void clearEnumerationResults() {

    }

    @Override
    public void clearCaches() {

    }

    @Override
    public Object getObjectToWaitFor() {
        return null;
    }
}
